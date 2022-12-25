package Ir;

import java.util.ArrayList;
import java.util.HashMap;

public class RegMemAllocator {
    private int offset;
    private HashMap<String, Integer> localIrs;
    private HashMap<String, Integer> varOffset;
    private HashMap<Integer, String> allocatedRegs;
    private HashMap<String, Integer> irNamesToRegs;
    ArrayList<RegMap> regMaps;

    public RegMemAllocator(int offset,HashMap<String, Integer> localIrs, HashMap<String, Integer> varOffset) {
        this.offset = offset;
        this.localIrs = localIrs;
        this.varOffset = varOffset;
        this.allocatedRegs = new HashMap<>();
        this.irNamesToRegs = new HashMap<>();
        this.regMaps = new ArrayList<>();
    }

    public int getOffset() {
        return offset;
    }

    public HashMap<Integer, String> getAllocatedRegs() {
        return allocatedRegs;
    }

    public int findRegister(String varName, ArrayList<String> mips) {
        int regNum = 0;
        if (irNamesToRegs.containsKey(varName)) {
            regNum = irNamesToRegs.get(varName);
        } else {
            // 变量没有被分配寄存器,在内存中
            if (allocatedRegs.size() == 19) {
                regNum = clearAReg(mips);
            } else {
                for (int i = 5; i <= 25; i++) {
                    if (i != 8 && i != 9 && !allocatedRegs.containsKey(i)) {
                        regNum = i;
                        break;
                    }
                }
            }
            String load;
            if (localIrs.containsKey(varName)) {
                int offset = varOffset.get(varName);
                load  = "lw $" + regNum + ", " + offset + "($fp)"  + " #"  + varName + ":  " + regNum;
            } else {
                load  = "lw $" + regNum + ", " + varName + "($0)" +  " #"  + varName + ":  " + regNum;
            }
            mips.add(load + "  #  " + varName + regNum);
            allocatedRegs.put(regNum,varName);
            irNamesToRegs.put(varName, regNum);
            regMaps.add(new RegMap(regNum, varName));
        }
        return regNum;
    }

    public int clearAReg(ArrayList<String> mips) {
        RegMap first = regMaps.get(0);
        int freeRegNum = first.getRegNum();
        String rePlacedVarIr = first.getVarName();
        if (localIrs.containsKey(rePlacedVarIr)) {
            // 不是全局变量
            if (varOffset.containsKey(rePlacedVarIr)) {
                int off = varOffset.get(rePlacedVarIr);
                String restore = "sw $" + freeRegNum + ", " + off + "($fp)    # restore" + rePlacedVarIr;
                mips.add(restore);
            } else {
                int addr = this.offset;
                this.offset += 4;
                varOffset.put(rePlacedVarIr, addr);
                String restore = "sw $" + freeRegNum + ", " + addr + "($fp)    # restore" + rePlacedVarIr;
                mips.add(restore);
            }
        } else {
            // 全局变量
            String restore = "sw $" + freeRegNum + ", " + rePlacedVarIr + "($0)    # restore" + rePlacedVarIr;
            mips.add(restore);
        }
        allocatedRegs.remove(freeRegNum);
        irNamesToRegs.remove(rePlacedVarIr);
        regMaps.remove(0);
        return freeRegNum;
    }

    public int getAssign(String varIrName, ArrayList<String> mips) {
        int regNum = 0;
        if (irNamesToRegs.containsKey(varIrName)) {
            regNum = irNamesToRegs.get(varIrName);
            // 变量已经有对应好的寄存器
        } else {
            // 变量没有对应的寄存器
            if (allocatedRegs.size() == 19) {
                regNum = clearAReg(mips);
            } else {
                for (int i = 5; i <= 25; i++) {
                    if (i != 8 && i != 9 && !allocatedRegs.containsKey(i)) {
                        regNum = i;
                        break;
                    }
                }
            }
            allocatedRegs.put(regNum,varIrName);
            irNamesToRegs.put(varIrName, regNum);
            regMaps.add(new RegMap(regNum, varIrName));
        }
        return regNum;
    }

    // TODO 分配5-7   10-25 (19个)寄存器
    public void preparePrintNum(String varIr, ArrayList<String> mips) {
        int regNum = 0;
        if (irNamesToRegs.containsKey(varIr)) {
            regNum = irNamesToRegs.get(varIr);
            String move =  "move $a0, " + "$" + regNum;
            mips.add(move);
        } else {
            // 变量没有被分配寄存器,在内存中
            String load;
            if (localIrs.containsKey(varIr)) {
                int offset = varOffset.get(varIr);
                load  = "lw $a0" + ", " + offset + "($fp)";
            } else {
                load  = "lw $a0" + ", " + varIr + "($0)";
            }
            mips.add(load);
        }
    }

    public void loadGetBase(String varIr, ArrayList<String> mips) {
        if (localIrs.containsKey(varIr)) {
            int offset = varOffset.get(varIr);
            mips.add("li $t0, " + offset + "     #" + varIr + "数组首地址相对于fp的偏移");
            mips.add("addu $t0, $t0, $fp" + "    #当前数组在内存中的首地址");
        } else {
            mips.add("la $t0,  " + varIr); // 全局数组
        }
    }

    public void loadGetOffset(String varIr, ArrayList<String> mips) {
        if (irNamesToRegs.containsKey(varIr)) {
            String offReg = "$" + irNamesToRegs.get(varIr);
            mips.add("li $3, 4");
            mips.add("mul $t1, " + offReg + ", $3" + "    #计算数组元素离首地址的偏移");
        } else {
            if (localIrs.containsKey(varIr)) {
                int offsetValue = varOffset.get(varIr);
                mips.add("lw $t1, " + offsetValue + "($fp)");
            } else {
                mips.add("lw $t1, " + varIr + "($0)");
                //System.out.println("Wrong! In Pointer offset finding" + offset);
            }
            mips.add("li $3, 4");
            mips.add("mul $t1, $t1, $3" + "    #计算数组元素离首地址的偏移");
        }

    }

    public void getInt(String varIr, ArrayList<String> mips) {

    }

}