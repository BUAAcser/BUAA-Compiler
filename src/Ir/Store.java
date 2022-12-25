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
    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        Matcher matcher = pattern.matcher(valueTemp);


        mips.add("###    start   " + this.toString());

        String m1;
        // 用t1寄存器存储待存的值
        if (matcher.find()) {
            int value = Integer.parseInt(valueTemp);
            m1 = "li $t1, " + value;
        } else {
            if (varOffset.containsKey(valueTemp)) {
                int offset = varOffset.get(valueTemp);
                m1  = "lw $t1, " + offset + "($fp)";
            } else {
                m1  = "lw $t1, " + valueTemp + "($0)";
            }
        }
        mips.add(m1);

        int off1 = varOffset.get(addressTemp);
        String calcAddress = "lw $t2, " + off1 + "($fp)"; //  t2寄存器存储着地址
        mips.add(calcAddress);

        String store = "sw $t1, 0($t2)";
        mips.add(store);
        mips.add("###   end    " + this.toString());

    } // finish
}
