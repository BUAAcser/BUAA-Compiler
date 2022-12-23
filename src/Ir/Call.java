package Ir;

import java.util.ArrayList;
import java.util.HashMap;

public class Call implements Ir {
    private String funcName;
    private int offset;

    public Call(String funcName) {
        this.funcName = funcName;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    public String toString() {
        String res = "Call " + funcName + ",  fp move " + offset + " higher ";
        return res;
    }

    @Override
    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset, RegMemAllocator allocator) {

    }

    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset, int fp,
                        RegMemAllocator allocator) {
        HashMap<Integer, String> allocateRegs = allocator.getAllocatedRegs();
        ArrayList<Integer> regs = new ArrayList<>();
        for (int i = 5; i <= 25; i++) {
            if (i != 8 && i != 9 && allocateRegs.containsKey(i)) {
                regs.add(i);
            }
        }
        int size = allocateRegs.size();
        int all = size * 4 + 4;

        mips.add("addi $sp, $sp, -" + all);
        mips.add("sw $ra, 0($sp)");
        for (int i = 0; i < size; i++) {
            int offset = (i + 1) * 4;
            mips.add("sw $" + regs.get(i) + ", " + offset + "($sp)");
        }

        mips.add("addi $fp, $fp " + fp);
        mips.add("jal " + funcName);
        mips.add("addi $fp, $fp " + "-" + fp);


        mips.add("lw $ra, 0($sp)");
        for (int i = 0; i < size; i++) {
            int offset = (i + 1) * 4;
            mips.add("lw $" + regs.get(i) + ", " + offset + "($sp)");
        }
        mips.add("addi $sp, $sp, " + all); // TODO
    } // finish
}
