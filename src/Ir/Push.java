package Ir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Push implements Ir {
    private String paramName;

    public Push(String paramName) {
        this.paramName = paramName;
    }

    public String toString() {
        String str = "Push " + paramName;
        return str;
    }

    @Override
    public void generate(ArrayList<String> mips, HashMap<String, Integer> offSet) {

    }

    public void generate(ArrayList<String> mips,  int allOff, HashMap<String, Integer> varOffset) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        Matcher matcher = pattern.matcher(paramName);

        mips.add("###    start   " + this.toString());

        String m1;
        if (matcher.find()) {
            int value = Integer.parseInt(paramName);
            m1 = "li $t1, " + value;
        } else {
            if (varOffset.containsKey(paramName)) {
                int offset = varOffset.get(paramName);
                m1  = "lw $t1, " + offset + "($fp)";
            } else {
                m1  = "lw $t1, " + paramName + "($0)";
            }
        } // 将参数的值赋给t1寄存器
        mips.add(m1);
        mips.add("sw $t1, " + allOff + "($fp)");

        mips.add("###    end   " + this.toString());
    } // finish

}
