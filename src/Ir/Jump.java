package Ir;

import java.util.ArrayList;
import java.util.HashMap;

public class Jump implements Ir {
    private String label;

    public Jump(String label) {
        this.label = label;
    }


    @Override
    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset) {

    }
}
