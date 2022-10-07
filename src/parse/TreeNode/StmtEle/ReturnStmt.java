package parse.TreeNode.StmtEle;

import parse.TreeNode.Exp;
import parse.TreeNode.Stmt;

public class ReturnStmt extends Stmt {
    private Exp exp;

    public ReturnStmt(Exp exp) {
        this.exp = exp;
    }

    public ReturnStmt() {
        this.exp = null;
    }

}
