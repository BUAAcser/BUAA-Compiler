package Ir;

import java.util.ArrayList;
import java.util.HashMap;
public class JPF implements Ir {
    private String varName;
    private String label;

    public JPF(String varName, String label) {
        this.varName = varName;
        this.label = label;
    }

    public String toString() {
        String res = "Jump false to " + label;
        return res;
    }

    @Override
    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset, RegMemAllocator
                         allocator) {
        int regNum = allocator.findRegister(varName, mips);
        String reg = "$" + regNum;
        String jpf = "beqz " + reg + ",  " + label;
        mips.add(jpf);
    } // finish
}
