package parse.TreeNode;

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

}
