package parse.TreeNode;

import java.util.ArrayList;

public class Block extends Stmt {
    private ArrayList<BlockItem> items;

    public Block(ArrayList<BlockItem> items) {
        this.items = items;
    }

}
