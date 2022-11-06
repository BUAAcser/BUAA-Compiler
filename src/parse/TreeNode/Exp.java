package parse.TreeNode;

import symbol.SymbolTable;

public class Exp {
    private boolean isConstant;
    private AddExp addExp;

    public Exp(boolean isConstant, AddExp addExp) {
        this.isConstant = isConstant;
        this.addExp = addExp;
    }

    public int calcConst(SymbolTable symbolTable) {
        return addExp.calcConst(symbolTable);
    } // 用于在全局中计算Exp

    public AddExp getAddExp() {
        return addExp;
    }
//    public int calcDims(SymbolTable symbolTable) {
//        return addExp.calc
//    }
}
