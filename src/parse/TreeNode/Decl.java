package parse.TreeNode;

import java.util.ArrayList;

public class Decl implements BlockItem {
    private DeclType type;
    private ArrayList<Def> defs;

    public Decl(DeclType type, ArrayList<Def> defs) {
        this.type = type;
        this.defs = defs;
    }

    public ArrayList<Def> getDefs() {
        return defs;
    }

    public DeclType getDeclType() {
        return type;
    }

}
