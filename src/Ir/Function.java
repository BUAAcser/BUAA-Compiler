package Ir;

import java.util.ArrayList;
import java.util.HashMap;

public class Function {
    //  private SymbolTable localVars;
    //  private SymbolTable tempVars;
    private String FunctionName;
    private ArrayList<BasicBlock> basicBlocks;
    private HashMap<String, Integer> varOffset;

    public Function(String functionName) {
        this.FunctionName = functionName;
        this.basicBlocks = new ArrayList<>();
        this.varOffset = new HashMap<>();
    }

    public void addSymbolAndOffset(String symbolName, int offset) {
        varOffset.put(symbolName, offset);
    }

    public void addBasicBlock(BasicBlock basicBlock) {
        basicBlocks.add(basicBlock);
    }

    public BasicBlock getBlock() {
        return basicBlocks.get(0);
    }

    public String getFunctionName() {
        return FunctionName;
    }

    public HashMap<String, Integer> getVarOffset() {
        return varOffset;
    }

    public ArrayList<Ir> getIrs() {
        return  basicBlocks.get(0).getIrs();
    }
}