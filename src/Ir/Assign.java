package Ir;

import count.AddressCounter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Assign implements Ir {
    private String left;
    private String right;

    public Assign(String left, String right) {
        this.left = left;
        this.right = right;
    }

    public String toString() {
        String res = left + " = " + right;
        return res;
    }


    @Override
    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        Matcher matcher = pattern.matcher(right);

        mips.add("###    start   " + this.toString());

        String m1;
        if (matcher.find()) {
            int value = Integer.parseInt(right);
            m1 = "li $t1, " + value;
        } else {
            if (varOffset.containsKey(right)) {
                int offset = varOffset.get(right);
                m1  = "lw $t1, " + offset + "($fp)";
            } else {
                m1  = "lw $t1, " + right + "($0)";
            }
        }
        mips.add(m1);

        if (varOffset.containsKey(left)) {
            int offset = varOffset.get(left);
            String sto  = "sw $t1, " + offset + "($fp)";
            mips.add(sto);
        } else {
            String sto  = "sw $t1, " + left + "($0)";
            mips.add(sto);   //  左端是全局变量
        }

        mips.add("###   end    " + this.toString());
    } // finish
}
