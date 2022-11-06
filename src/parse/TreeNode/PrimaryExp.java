package parse.TreeNode;

import symbol.SymbolTable;

public class PrimaryExp {
    private Exp exp;
    private Lval lval;
    private Number number;

    public PrimaryExp(Exp exp) {
        this.exp = exp;
        this.lval = null;
        this.number = null;
    }

    public PrimaryExp(Lval lval) {
        this.exp = null;
        this.lval = lval;
        this.number = null;
    }

    public PrimaryExp(Number number) {
        this.exp = null;
        this.lval = null;
        this.number = number;
    }

    public int calcConst(SymbolTable symbolTable) {
        int res;
        if (exp != null) {
            res = exp.calcConst(symbolTable);
        } else if (lval != null) {
            res = lval.calcConst(symbolTable);
        } else {
            res = number.toInt();
        }
        return res;
    }

    public int getPrimaryExpType() {
        if (exp != null) {
            return 1;
        } else if (lval != null) {
            return 2;
        } else {
            return 3;
        }
    }

    public Exp getExp() {
        return this.exp;
    }

    public Lval getLval() {
        return this.lval;
    }

    public Number getNumber() {
        return this.number;
    }
}
