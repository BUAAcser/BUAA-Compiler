package Ir;

import java.util.ArrayList;
import java.util.HashMap;

public interface Ir {

    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset);
}
