package Ir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoadPointer implements Ir {
    private String ident;
    private String offset;
    private String pointer;
    private LoadPointType type;

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
    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset, RegMemAllocator
                         allocator) {
        String baseReg = "";
        if (type == LoadPointType.Offset) {
            allocator.loadGetBase(ident,mips);
            baseReg = "$t0";
        } else {
            int baseNum = allocator.findRegister(ident, mips);
            baseReg = "$" + baseNum;
        } // baseReg 寄存器存着地址

        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        Matcher matcher = pattern.matcher(offset);


        if (matcher.find()) {
            int value = Integer.parseInt(offset) * 4;
            mips.add("li $t1, " + value +  "    #计算数组元素离首地址的偏移");
        } else {
            allocator.loadGetOffset(offset, mips);
        }

        int resNum = allocator.getAssign(pointer, mips);
        String resReg = "$" + resNum;
        mips.add("addu " + resReg + ", " + baseReg + ", " + "$t1");


    } // finish
    // TODO 注意再给point的时候， offset 要乘4
    // TODO offset 虽然是字符串形式，但有可能是数字
    // TODO ident有三种形式 全局数组， 局部数组， 数组传参的地址变量
}
