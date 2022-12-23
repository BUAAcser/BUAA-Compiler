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
    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset, RegMemAllocator
                         allocator) {

    }

    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset, int allOff,
                         RegMemAllocator allocator) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        Matcher matcher = pattern.matcher(paramName);

        String m1;
        String valueReg;
        if (matcher.find()) {
            int value = Integer.parseInt(paramName);
            m1 = "li $t1, " + value;
            mips.add(m1);
            valueReg = "$t1";
        } else {
            int value = allocator.findRegister(paramName, mips);
            valueReg = "$" + value;
        } // 将参数的值赋给t1寄存器

        mips.add("sw " +  valueReg +  ", " + allOff + "($fp)");
    } // finish

}
