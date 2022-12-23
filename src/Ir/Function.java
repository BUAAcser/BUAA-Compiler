package Ir;

import java.util.ArrayList;
import java.util.HashMap;

public class Function {
    //  private SymbolTable localVars;
    //  private SymbolTable tempVars;
    private String FunctionName;
    private ArrayList<BasicBlock> basicBlocks;
    private HashMap<String, Integer> varOffset;
    private HashMap<String, Integer> localIrVars;
    private int offset;

    public Function(String functionName) {
        this.FunctionName = functionName;
        this.basicBlocks = new ArrayList<>();
        this.varOffset = new HashMap<>();
        this.localIrVars = new HashMap<>();
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void addSymbolAndOffset(String symbolName, int offset) {
        varOffset.put(symbolName, offset);
    }

    public void addIrLocal(String varIrName) {
        localIrVars.put(varIrName, 1);
    }

    public void addBasicBlock(BasicBlock basicBlock) {
        basicBlocks.add(basicBlock);
    }

    public ArrayList<BasicBlock> getBlock() {
        return basicBlocks;
    }

    public String getFunctionName() {
        return FunctionName;
    }

    public HashMap<String, Integer> getVarOffset() {
        return varOffset;
    }

//    public ArrayList<Ir> getIrs() {
//        return  basicBlocks.get(0).getIrs();
//    } // TODO 有问题

}