package parse.TreeNode;

import lex.Token;

import java.util.ArrayList;

public class RelExp {
    private AddExp first;
    private ArrayList<Token> operators;
    private ArrayList<AddExp> addExps;

    public RelExp(AddExp first, ArrayList<Token> ops, ArrayList<AddExp> addExps) {
        this.first = first;
        this.operators = ops;
        this.addExps = addExps;
    }
}
