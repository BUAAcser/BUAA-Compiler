package parse.TreeNode.StmtEle;

import parse.TreeNode.Cond;
import parse.TreeNode.Stmt;

public class WhileStmt extends Stmt {
    private Cond cond;
    private Stmt stmt;

    public WhileStmt(Cond cond, Stmt stmt) {
        this.cond = cond;
        this.stmt = stmt;
    }

    public Cond getCond() {
        return cond;
    }

    public Stmt getStmt() {
        return stmt;
    }

}
