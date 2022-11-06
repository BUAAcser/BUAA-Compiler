package Ir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoadPointer implements Ir {
    private LoadPointType type;
    private String ident;
    private String offset;
    private String pointer;

    public LoadPointer(String pointer, String ident, String offset, LoadPointType type) {
        this.type = type;
        this.pointer = pointer;
        this.ident = ident;
        this.offset = offset;
    }

    public String toString() {
        String str;
        if (type == LoadPointType.Offset) {
            str = "Load " + ident + " offset " + offset  + " 's pointer to " + pointer +
                    "Type: offset.";
        } else {
            str = "Load " + ident + " offset " + offset  + " 's pointer to " + pointer +
                    "Type: pointer.";
        }
        return str;
    }

    @Override
    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset) {
        if (type == LoadPointType.Offset) {
            if (varOffset.containsKey(ident)) {
                int offset = varOffset.get(ident);
                mips.add("li $t0, " + offset + "     #数组首地址相对于fp的偏移");
                mips.add("addu $t1, $t0, $fp" + "    #当前数组在内存中的首地址");
            } else {
                mips.add("la $t1,  " + ident); // 全局数组
            }
        } else {
            int offset = varOffset.get(ident);
            mips.add("lw $t1, " + offset + "($fp)");
        }

        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        Matcher matcher = pattern.matcher(offset);


        if (matcher.find()) {
            int value = Integer.parseInt(offset) * 4;
            mips.add("li $t2, " + value +  "    #计算数组元素离首地址的偏移");
        } else {
            if (varOffset.containsKey(offset)) {
                int offsetValue = varOffset.get(offset);
                mips.add("lw $t3, " + offsetValue + "($fp)");
                mips.add("li $t4, 4");
                mips.add("mul $t2, $t3, $t4" + "    #计算数组元素离首地址的偏移");
            } else {
                System.out.println("Wrong! In Pointer offset finding" + offset);
            }
        }

        mips.add("addu $t3, $t1, $t2");

        int pointerOff = varOffset.get(pointer);
        mips.add("sw $t3, " + pointerOff + "($fp)" + "    #将计算出来的地址存入变量");
        // Pointer 一定能在 varOffset中找到
    }
    // TODO 注意再给point的时候， offset 要乘4
    // TODO offset 虽然是字符串形式，但有可能是数字
    // TODO ident有三种形式 全局数组， 局部数组， 数组传参的地址变量
}
