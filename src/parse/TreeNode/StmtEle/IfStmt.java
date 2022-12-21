package parse.TreeNode.StmtEle;

import parse.TreeNode.Cond;
import parse.TreeNode.Stmt;

public class IfStmt extends Stmt {
    private Cond cond;
    private Stmt ifStmt;
    private Stmt elseStmt;

    public IfStmt(Cond cond, Stmt ifStmt, Stmt elseStmt) {
        this.cond = cond;
        this.ifStmt = ifStmt;
        this.elseStmt = elseStmt;
    }

    public Cond getCond() {
        return cond;
    }

    public Stmt getIfStmt() {
        return ifStmt;
    }

    public Stmt getElseStmt() {
        return elseStmt;
    }
}
