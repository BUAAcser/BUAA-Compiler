package Ir;

import java.util.ArrayList;
import java.util.HashMap;

public class Ret implements Ir {
    private String left;

    public Ret(String left) {
        this.left = left;
    }

    public String toString() {
        String str = left + " = ret";
        return str;
    }

    @Override
    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset,
                         RegMemAllocator allocator) {

        int num = allocator.getAssign(left, mips);
        String resReg = "$" + num;
        String move = "move " + resReg + ", $v0" + "   # " + this.toString();
        mips.add(move);

        /* int offset = varOffset.get(left);
        String sto = "sw $t1, " + offset + "($fp)";
        mips.add(sto);*/
    } // finish
}
