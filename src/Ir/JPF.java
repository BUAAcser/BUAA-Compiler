package Ir;

import java.util.ArrayList;
import java.util.HashMap;


public class JPF implements Ir {
    private String varName;
    private String label;

    public JPF(String varName, String label) {
        this.varName = varName;
        this.label = label;
    }

    @Override
    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset) {

    }
}
