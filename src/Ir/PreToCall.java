package Ir;

import java.util.ArrayList;
import java.util.HashMap;

public class PreToCall implements Ir {

    public PreToCall() {}

    public String toString() {
        String res = "PreTOCall";
        return  res;
    }

    @Override
    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset) {

    }
}
