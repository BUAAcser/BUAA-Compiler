package Ir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrintStr implements Ir {
    private int strNo;
    private String numVar;

    public PrintStr(int strNo, String numVar) {
        this.strNo = strNo;
        this.numVar = numVar;
    }

    public String toString() {
        String str;
        if (numVar != null) {
            str = "Print " + numVar;
        } else {
            str = "Print  str_" + strNo;
        }
        return str;
    }

    @Override
    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset,
                         RegMemAllocator allocator) {
        if (numVar != null) {
            Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
            Matcher matcher = pattern.matcher(numVar);
            String m1;
            if (matcher.find()) {
                int value = Integer.parseInt(numVar);
                m1 = "li $a0, " + value;
                mips.add(m1);
            } else {
                allocator.preparePrintNum(numVar, mips);
            } // 将要输出的值赋值到a0寄存器

            mips.add("li $v0, 1");
            mips.add("syscall");
            // 要输出数字
        } else {
            mips.add("li $v0, 4");
            mips.add("la $a0, str_" + strNo);
            mips.add("syscall");
        }
    } // finish
    // TODO 生成后端代码时，先判断numvar字符串是否为空
}
