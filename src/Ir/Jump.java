package Ir;

import java.util.ArrayList;
import java.util.HashMap;

public class Jump implements Ir {
    private String label;

    public Jump(String label) {
        this.label = label;
    }

    public String toString() {
        String res = "jump  " + label;
        return res;
    }

    @Override
    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset, RegMemAllocator
                         allocator) {
        mips.add("j  " + label);
    } // finish
}
