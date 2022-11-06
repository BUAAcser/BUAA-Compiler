package parse.TreeNode;

import symbol.SymbolTable;

import java.util.ArrayList;

public class InitVal {
    private boolean isConstant;
    private InitValType type;
    private Exp exp;
    private ArrayList<InitVal> vals;

    public InitVal(boolean constant, InitValType type, Exp exp) {
        this.isConstant = constant;
        this.type = type;
        this.exp = exp;
        this.vals = null;
    }

    public InitVal(boolean constant, InitValType type, ArrayList<InitVal> vals) {
        this.isConstant = constant;
        this.type = type;
        this.exp = null;
        this.vals = vals;
    }

    public void getConstDefValue(ArrayList<Integer> values, SymbolTable symbolTable) {
        if (type == InitValType.SIMPLE) {
            values.add(exp.calcConst(symbolTable));
        } else {
            for (InitVal initVal : vals) {
                initVal.getConstDefValue(values, symbolTable);
            }
        }
    }

    public InitValType getType() {
        return type;
    }

    public Exp getExp() {
        return exp;
    }

    public ArrayList<InitVal> getInitVals() {
        return vals;
    }

}
