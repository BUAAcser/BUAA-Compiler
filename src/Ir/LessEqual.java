package Ir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LessEqual implements Ir {
    private String left;
    private String operand1;
    private String operand2;

    public LessEqual(String left, String operand1, String operand2) {
        this.left = left;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    public String toString() {
        String result;
        result = left + " = (" + operand1 + " <= " + operand2 + ")";
        return result;
    }
    // sle
    @Override
    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        Matcher matcher1 = pattern.matcher(operand1);
        Matcher matcher2 = pattern.matcher(operand2);

        mips.add("###    start   " + this.toString());

        String m1;
        if (matcher1.find()) {
            int value = Integer.parseInt(operand1);
            m1 = "li $t1, " + value;
        } else {
            if (varOffset.containsKey(operand1)) {
                int offset = varOffset.get(operand1);
                m1  = "lw $t1, " + offset + "($fp)";
            } else {
                m1  = "lw $t1, " + operand1 + "($0)";
            }
        } // 将左值赋值到t1寄存器
        mips.add(m1);

        String m2;
        if (matcher2.find()) {
            int value = Integer.parseInt(operand2);
            m2 = "li $t2, " + value;
        } else {
            if (varOffset.containsKey(operand2)) {
                int offset = varOffset.get(operand2);
                m2  = "lw $t2, " + offset + "($fp)";
            } else {
                m2  = "lw $t2, " + operand2 + "($0)";
            }
        } // 将右值赋值到t2寄存器
        mips.add(m2);

        String result = "sle $t3, $t1, $t2";
        mips.add(result);

        if (varOffset.containsKey(left)) {
            int offset = varOffset.get(left);
            String sto  = "sw $t3, " + offset + "($fp)";
            mips.add(sto);
        } else {
            String sto  = "sw $t3, " + left + "($0)";
            mips.add(sto);
        }

        mips.add("###   end    " + this.toString());
    } // finish
}
