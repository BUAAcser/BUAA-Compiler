package parse.TreeNode;

import Ir.Addition;
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

    public AddExp getFirst() {
        return first;
    }

    public ArrayList<Token> getOperators() {
        return operators;
    }
    public ArrayList<AddExp> getAddExps() {
        return addExps;
    }
}
