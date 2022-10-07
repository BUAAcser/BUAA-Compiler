package parse.TreeNode.StmtEle;

import parse.TreeNode.Exp;
import parse.TreeNode.Stmt;

public class ExqStmt extends Stmt {
    private Exp exp;

    public ExqStmt(Exp exp) {
        this.exp = exp;
    }
}
