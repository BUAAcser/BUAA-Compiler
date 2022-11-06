package parse.TreeNode.StmtEle;

import parse.TreeNode.Exp;
import parse.TreeNode.Lval;
import parse.TreeNode.Stmt;

public class AssignStmt extends Stmt {
    private Lval lval;
    private Exp exp;

    public AssignStmt(Lval lval, Exp exp) {
        this.lval = lval;
        this.exp = exp;
    }

    public Lval getLval() {
        return lval;
    }

    public Exp getExp() {
        return exp;
    }
}
