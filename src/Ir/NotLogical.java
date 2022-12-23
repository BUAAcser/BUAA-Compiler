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
        String str =  left + " = ! " + right;
        return str;
    }

    @Override
    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset,
                         RegMemAllocator allocator) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        Matcher matcher1 = pattern.matcher(right);

        String rightReg;
        if (matcher1.find()) {
            int value = Integer.parseInt(right);
            rightReg = "$t0";
            String m1 = "li $t0, " + value;
            mips.add(m1);
        } else {
            rightReg = "$" + allocator.findRegister(right, mips);
        }

        String resReg =  "$" + allocator.getAssign(left, mips);
        String result = "seq " + resReg + ", $0, "  + rightReg + "   #"  + this.toString();
        mips.add(result);

    } // finsih
}
