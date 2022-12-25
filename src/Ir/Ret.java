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
        mips.add("###    start   " + this.toString());

        String move = "move $t1, $v0";
        String sto;

        if (varOffset.containsKey(left)) {
            int offset = varOffset.get(left);
            sto = "sw $t1, " + offset + "($fp)";
        } else {
            sto = "sw $t1, " + left + "($0)";
        }

        mips.add(move);
        mips.add(sto);

        mips.add("###    end   " + this.toString());
    } // finish
}
