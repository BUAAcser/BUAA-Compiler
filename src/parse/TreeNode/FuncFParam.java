package parse.TreeNode;

import lex.Token;

public class FuncFParam {
    private Token ident;
    private int dimension;
    private Exp constExp;

    public FuncFParam(Token ident, int dimension, Exp constExp) {
        this.ident = ident;
        this.dimension = dimension;
        this.constExp = constExp;
    }

    public int getDimension() {
        return dimension;
    }

    public Exp getConstExp() {
        return constExp;
    }

    public String getName() {
        return ident.getContent();
    }
}
