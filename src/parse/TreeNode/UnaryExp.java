package parse.TreeNode;

import lex.Token;
import lex.TokenType;
import symbol.SymbolTable;

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

    //// 以下两种创建创建方式对应的是函数有参和无参
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

    public int calcConst(SymbolTable symbolTable) {
        int coe = 1;
        for (Token operator : operators) {
            if (operator.getType() == TokenType.MINU) {
                coe = coe * (-1);
            }
        }
        int result = coe * primaryExp.calcConst(symbolTable);
        return result;
    } // 求解global变量中不会出现函数

    public PrimaryExp getPrimaryExp() {
        return primaryExp;
    }

    public Token getFunc() {
        return func;
    }

    public FuncRParams getFuncRparams() {
        return funcRparams;
    }

    public ArrayList<Token> getOperators() {
        return operators;
    }
}
