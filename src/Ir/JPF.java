package Ir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public void generate(ArrayList<String> mips,  HashMap<String, Integer> varOffset) {
        mips.add("###   start    " + this.toString());
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        Matcher matcher = pattern.matcher(varName);
        if (matcher.find()) {
            int value = Integer.parseInt(varName);
            mips.add("li $t0, " + value);
        } else {
            if (varOffset.containsKey(varName)) {
                int offset = varOffset.get(varName);
                mips.add("lw $t0, " + offset + "($fp)");
            } else {
                mips.add("lw $t0, " + varName + "($0)");
            }
        }
        String jpf = "beqz $t0, "  + label;
        mips.add(jpf);
        mips.add("###   end    " + this.toString());
    } // finish
}
