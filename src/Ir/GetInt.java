package Ir;

import java.util.ArrayList;
import java.util.HashMap;

public class GetInt implements Ir {
    private String varName;
    private String arrayAddVar;

    public GetInt(String varName, String arrayAddVar) {
        this.varName = varName;
        this.arrayAddVar = arrayAddVar;
    }

    public String toString() {
        String str;
        if (varName != null) {
            str = "GetInt  to " + varName;
        } else {
            str = "GetInt  to  Address" + arrayAddVar;
        }
        return str;
    }

    @Override
    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset) {
        mips.add("li $v0, 5");
        mips.add("syscall");

        if (varName != null) {
            if (varOffset.containsKey(varName)) {
                int offset = varOffset.get(varName);
                mips.add("sw $v0, " + offset + "($fp)");
            } else {
                mips.add("sw $v0, " + varName + "($0)");
            }
        } else {
            int offset = varOffset.get(arrayAddVar);
            mips.add("lw $t1, " + offset + "($fp)");
            mips.add("sw $v0, 0($t1)");  // t1寄存器 代表着数组元素地址
        }
    }
}
