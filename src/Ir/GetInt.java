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
    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset, RegMemAllocator
                         allocator) {
        mips.add("li $v0, 5");
        mips.add("syscall");

        if (varName != null) {
            String res = "$" + allocator.getAssign(varName,mips);
            mips.add("move " + res + ", " + "$v0" +  "    #" + this.toString());
        } else {
            String arrayAdd = "$" + allocator.findRegister(arrayAddVar, mips);
            mips.add("sw $v0, 0(" + arrayAdd + ")" + "     #" + this.toString());
        }
    } // finish
}
