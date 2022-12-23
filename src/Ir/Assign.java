package Ir;

import count.AddressCounter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Assign implements Ir {
    private String left;
    private String right;

    public Assign(String left, String right) {
        this.left = left;
        this.right = right;
    }

    public String toString() {
        String res = left + " = " + right;
        return res;
    }


    @Override
    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset,
                         RegMemAllocator allocator) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        Matcher matcher = pattern.matcher(right);

        String m1;
        String lef =  "$" + allocator.getAssign(left, mips);
        if (matcher.find()) {
            int value = Integer.parseInt(right);
            m1 = "li " + lef +  ", " + value;
        } else {
            String rig = "$" + allocator.getAssign(right, mips);
            m1 = "move " + lef + ", " + rig;
        }
        mips.add(m1);

    } // finish
}
