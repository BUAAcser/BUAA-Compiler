package Ir;

import java.util.ArrayList;
import java.util.HashMap;

public class Load implements Ir {
    private String resultTemp;
    private String addressTemp;

    public Load(String resultTemp, String addressTemp) {
        this.resultTemp = resultTemp;
        this.addressTemp = addressTemp;
    }

    public String toString() {
        String str = "Load Value from " + addressTemp + " to " + resultTemp;
        return str;
    }

    @Override
    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset,RegMemAllocator
                         allocator) {

        int addressNum = allocator.findRegister(addressTemp, mips);
        String addressReg = "$" + addressNum;
        int resNum = allocator.getAssign(resultTemp, mips);
        String resReg = "$" + resNum;
        String load = "lw " + resReg + ", 0(" + addressReg + ")" + "   #" + this.toString();
        mips.add(load);

        //        int off1 = varOffset.get(addressTemp);
        //          String addr = "lw $t1, " + off1 + "($fp)"; //  t1寄存器存储着地址//        String value = "lw $t2, 0($t1)"; // t2寄存器存着从t1寄存器中的地址加载来的值
        //        int off2 = varOffset.get(resultTemp);
        //        String sto = "sw $t2, " +  off2 + "($fp)" + "         #" + this.toString(); // 将值存入resultTemp内存

    } // finish
}