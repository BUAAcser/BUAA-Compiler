package parse.TreeNode;

import lex.Token;

import java.util.ArrayList;

public class EqExp {
    private RelExp first;
    private ArrayList<Token> operators;
    private ArrayList<RelExp> relExps;

    public EqExp(RelExp first, ArrayList<Token> ops, ArrayList<RelExp> relExps) {
        this.first = first;
        this.operators = ops;
        this.relExps = relExps;
    }

    public RelExp getFirst() {
        return first;
    }

    public ArrayList<Token> getOperators() {
        return operators;
    }

    public ArrayList<RelExp> getRelExps() {
        return relExps;
    }

}
