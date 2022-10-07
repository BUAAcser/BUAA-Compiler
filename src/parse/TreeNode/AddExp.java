package parse.TreeNode;

import lex.Token;

import java.util.ArrayList;

public class AddExp {
    private MulExp firstMul;
    private ArrayList<Token> operators;
    private ArrayList<MulExp> mulExps;

    public AddExp(MulExp firstMul, ArrayList<Token> ops, ArrayList<MulExp> mulExps) {
        this.firstMul = firstMul;
        this.operators = ops;
        this.mulExps = mulExps;
    }

}
