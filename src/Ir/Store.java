package Ir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Store implements Ir {
    private String valueTemp;
    private String addressTemp;

    public Store(String valueTemp, String addressTemp) {
        this.valueTemp = valueTemp;
        this.addressTemp = addressTemp;
    }

    public String toString() {
        String str = "Store Value " + valueTemp + " to " + addressTemp;
        return str;
    }

    @Override
    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset, RegMemAllocator
                         allocator ) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        Matcher matcher = pattern.matcher(valueTemp);
        String m1;

        String valueReg = "";
        if (matcher.find()) {
            int value = Integer.parseInt(valueTemp);
            valueReg = "$t1";
            m1 = "li $t1, " + value;
            mips.add(m1);
        } else {
            int regNum = allocator.findRegister(valueTemp, mips);
            valueReg = "$" + regNum;
        }

        int addressNum = allocator.findRegister(addressTemp, mips);
        String addressReg = "$" + addressNum;

        String store = "sw " + valueReg + ", 0(" + addressReg + ")      #" + this.toString();
        mips.add(store);
    } // finish
}
