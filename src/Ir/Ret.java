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
    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset) {
        String move = "move $t1, $v0";
        int offset = varOffset.get(left);
        String sto = "sw $t1, " + offset + "($fp)";
        mips.add(move);
        mips.add(sto);
    }
}
