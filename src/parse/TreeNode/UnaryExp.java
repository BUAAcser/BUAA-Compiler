package parse.TreeNode;

import lex.Token;

import java.util.ArrayList;

public class UnaryExp {
    private ArrayList<Token> operators;
    private PrimaryExp primaryExp;
    private Token func;
    private FuncRParams funcRparams;

    public UnaryExp(ArrayList<Token> operators, PrimaryExp primaryExp) {
        this.operators = operators;
        this.primaryExp = primaryExp;
        this.func = null;
        this.funcRparams = null;
    }

    public UnaryExp(ArrayList<Token> operators, Token func, FuncRParams funcRparams) {
        this.operators = operators;
        this.primaryExp = null;
        this.func = func;
        this.funcRparams = funcRparams;
    }

    public UnaryExp(ArrayList<Token> operators, Token func) {
        this.operators = operators;
        this.primaryExp = null;
        this.func = func;
        this.funcRparams = null;
    }

}
