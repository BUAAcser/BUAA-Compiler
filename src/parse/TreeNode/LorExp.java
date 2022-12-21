package parse.TreeNode;

import java.util.ArrayList;

public class LorExp {
    private ArrayList<LandExp> landExps;

    public LorExp(ArrayList<LandExp> landExps) {
        this.landExps = landExps;
    }

    public ArrayList<LandExp> getLandExps() {
        return landExps;
    }
}
