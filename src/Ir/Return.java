package Ir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Return implements Ir {
    private String returnValue;

    public Return(String returnValue) {
        this.returnValue = returnValue;
    }

    public String toString() {
        if (returnValue == null) {
            return "Return";
        } else {
            return  "Return " + returnValue;
        }

    }

    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset, String type,
                         RegMemAllocator allocator) {
        if (returnValue != null) {
            Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
            Matcher matcher = pattern.matcher(returnValue);
            String m1;
            if (matcher.find()) {
                int value = Integer.parseInt(returnValue);
                m1 = "li $v0, " + value;
                mips.add(m1);
            } else {
                int valNum = allocator.findRegister(returnValue, mips);
                String valReg = "$" + valNum;
                String move = "move $v0, " +  valReg + " # return " + returnValue;
                mips.add(move);
                /* if (varOffset.containsKey(returnValue)) {
                    int offset = varOffset.get(returnValue);
                    m1  = "lw $t1, " + offset + "($fp)";
                } else {
                    m1  = "lw $t1, " + returnValue + "($0)";
                }
                mips.add(m1);
                String move = "move $v0, $t1";
                mips.add(move); */
            }
        }
        if (type.equals("Normal")) {
            String nor = "jr $ra";
            mips.add(nor);
        } else if (type.equals("main")) {
            String main1 = "li $v0, 10";
            String main2 = "syscall";
            mips.add(main1);
            mips.add(main2);
        }
    } //finish

    // Return 代表的机器码，首先判断是否有返回值，如果有返回值，将范围值移入v0寄存器；在根据函数类型进行转跳，
    // 如果是Main函数直接syscall, 如果是普通函数就Jr
    @Override
    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset, RegMemAllocator
                         allocator) {

    }
}
