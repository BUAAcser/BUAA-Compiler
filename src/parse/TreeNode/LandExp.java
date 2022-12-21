package parse.TreeNode;

import java.util.ArrayList;

public class LandExp {
    private ArrayList<EqExp> eqExps;

    public LandExp(ArrayList<EqExp> eqExps) {
        this.eqExps = eqExps;
    }

    public ArrayList<EqExp> getEqExps() {
        return eqExps;
    }
}
