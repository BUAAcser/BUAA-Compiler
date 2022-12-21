package parse.TreeNode;

public class Cond {
    private LorExp lorExp;

    public Cond(LorExp lorExp) {
        this.lorExp = lorExp;
    }

    public LorExp getLorExp() {
        return lorExp;
    }
}
