package Ir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Equal implements  Ir {
    private String left;
    private String operand1;
    private String operand2;

    public Equal(String left, String operand1, String operand2) {
        this.left = left;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    public String toString() {
        String result;
        result = left + " =  (" + operand1 + " == " + operand2 + ")";
        return result;
    }

    @Override
    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset,
                         RegMemAllocator allocator) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        Matcher matcher1 = pattern.matcher(operand1);
        Matcher matcher2 = pattern.matcher(operand2);

        String leftReg;
        if (matcher1.find()) {
            int value = Integer.parseInt(operand1);
            leftReg = "$t0";
            String m1 = "li $t0, " + value;
            mips.add(m1);
        } else {
            leftReg = "$" + allocator.findRegister(operand1, mips);
        }



        String rightReg;
        if (matcher2.find()) {
            int value = Integer.parseInt(operand2);
            rightReg = "$t1";
            String m2 = "li $t1, " + value;
            mips.add(m2);
        } else {
            rightReg = "$" + allocator.findRegister(operand2, mips);
        }

        String resReg =  "$" + allocator.getAssign(left, mips);
        String result = "seq " + resReg + ", " + leftReg + ", "  + rightReg + "   #"  + this.toString();
        mips.add(result);
    }
}
