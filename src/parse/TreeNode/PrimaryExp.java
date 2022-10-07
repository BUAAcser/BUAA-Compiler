package parse.TreeNode;

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
}
