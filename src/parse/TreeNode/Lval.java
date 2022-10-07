package parse.TreeNode;

import lex.Token;

import java.util.ArrayList;

public class Lval {
    private Token ident;
    private ArrayList<Exp> indexes;

    public Lval(Token ident, ArrayList<Exp> indexes) {
        this.ident = ident;
        this.indexes = indexes;
    }
}
