package Ir;

import java.util.ArrayList;
import java.util.HashMap;

public class Call implements Ir {
    private String funcName;
    private int offset;

    public Call(String funcName, int offset) {
        this.funcName = funcName;
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
    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset) {

    }

    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset, int fp) {
        mips.add("addi $sp, $sp, -4");
        mips.add("sw $ra, 0($sp)");
        mips.add("addi $fp, $fp " + fp);
        mips.add("jal " + funcName);
        mips.add("addi $fp, $fp " + "-" + fp);
        mips.add("lw $ra, 0($sp)");
        mips.add("addi $sp, $sp, 4");
    }
}
