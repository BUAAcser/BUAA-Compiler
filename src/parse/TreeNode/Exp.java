package parse.TreeNode;

public class Exp {
    private boolean isConstant;
    private AddExp addExp;

    public Exp(boolean isConstant, AddExp addExp) {
        this.isConstant = isConstant;
        this.addExp = addExp;
    }
}
