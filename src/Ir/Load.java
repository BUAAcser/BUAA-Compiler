package Ir;

import java.util.ArrayList;
import java.util.HashMap;

public class Load implements Ir {
    private String resultTemp;
    private String addressTemp;

    public Load(String resultTemp, String addressTemp) {
        this.resultTemp = resultTemp;
        this.addressTemp = addressTemp;
    }

    public String toString() {
        String str = "Load Value from " + addressTemp + " to " + resultTemp;
        return str;
    }

    @Override
    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset) {
        mips.add("###    start   " + this.toString());

        int off1 = varOffset.get(addressTemp);
        String addr = "lw $t1, " + off1 + "($fp)"; //  t1寄存器存储着地址
        String value = "lw $t2, 0($t1)"; // t2寄存器存着从t1寄存器中的地址加载来的值

        mips.add(addr);
        mips.add(value);
        String m1;
        if (varOffset.containsKey(resultTemp)) {
            int offset = varOffset.get(resultTemp);
            m1  = "sw $t2, " + offset + "($fp)";
        } else {
            m1  = "sw $t2, " + resultTemp + "($0)";
        }
        mips.add(m1);
        mips.add("###    end   " + this.toString());
    } // finish
}