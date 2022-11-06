package Ir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotLogical implements Ir {
    private String left;
    private String right;

    public NotLogical(String left, String right) {
        this.left = left;
        this.right = right;
    }

    public String toString() {
        String str =  left + " = - " + right;
        return str;
    }

    @Override
    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        Matcher matcher = pattern.matcher(right);
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
        } // 将right赋值到t1寄存器
        mips.add(m1);
        String result = "seq $t3, $t1, $0";

        if (varOffset.containsKey(left)) {
            int offset = varOffset.get(left);
            String sto  = "sw $t3, " + offset + "($fp)" + "     #" + this.toString();
            mips.add(sto);
        } else {
            String sto  = "sw $t3, " + left + "($0)" + "     #" + this.toString();
            mips.add(sto);
        }
    }
}