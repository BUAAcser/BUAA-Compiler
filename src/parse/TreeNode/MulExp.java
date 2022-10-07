package parse.TreeNode;

import lex.Token;

import java.util.ArrayList;

public class MulExp {
    private UnaryExp firstUnary;
    private ArrayList<Token> operators;
    private ArrayList<UnaryExp> unaryExps;

    public MulExp(UnaryExp firstUnary, ArrayList<Token> ops, ArrayList<UnaryExp> unaryExps) {
        this.firstUnary = firstUnary;
        this.operators = ops;
        this.unaryExps = unaryExps;
    }

}
