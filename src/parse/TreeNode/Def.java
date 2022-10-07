package parse.TreeNode;

import lex.Token;

import java.util.ArrayList;

public class Def {
    private boolean isConstant;
    private Token ident;
    private int dimension;
    private ArrayList<Exp> dimSizes;
    private InitVal val;

    public Def(boolean constant, Token ident, int dimension, ArrayList<Exp> dims, InitVal val) {
        this.isConstant = constant;
        this.ident = ident;
        this.dimension = dimension;
        this.dimSizes = dims;
        this.val = val;
    }

    public Def(boolean constant, Token ident, int dimension, InitVal val) {
        this.isConstant = constant;
        this.ident = ident;
        this.dimension = dimension;
        this.dimSizes = null;
        this.val = val;
    }

    public Def(boolean constant, Token ident, int dimension) {
        this.isConstant = constant;
        this.ident = ident;
        this.dimension = dimension;
        this.dimSizes = null;
        this.val = null;
    }

    public Def(boolean constant, Token ident, int dimension, ArrayList<Exp> dims) {
        this.isConstant = constant;
        this.ident = ident;
        this.dimension = dimension;
        this.dimSizes = dims;
        this.val = null;
    }

}
