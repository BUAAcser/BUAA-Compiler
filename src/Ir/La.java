package Ir;

import java.util.ArrayList;
import java.util.HashMap;

public class La implements Ir {
    private String reg;
    private String globalIrName;

    public La(String reg, String globalIrName) {
        this.reg = reg;
        this.globalIrName = globalIrName;
    }

    public String toString() {
        String res = "La " + globalIrName + " to  " + reg;
        return res;
    }
    @Override
    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset, RegMemAllocator allocator) {
        String tem = "$" + allocator.getAssign(reg,mips);
        mips.add("la " + tem + ", " + globalIrName + "     #" + this.toString());
    }
}
