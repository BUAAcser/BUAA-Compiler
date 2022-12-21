package Ir;

import java.util.ArrayList;
import java.util.HashMap;

public class Equal implements  Ir {
    private String left;
    private String operand1;
    private String operand2;

    public Equal(String left, String operand1, String operand2) {
        this.left = left;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    public String toString() {
        String result;
        result = left + " =  (" + operand1 + " == " + operand2 + ")";
        return result;
    }

    @Override
    public void generate(ArrayList<String> mips, HashMap<String, Integer> varOffset) {

    }
}
