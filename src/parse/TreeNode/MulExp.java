package parse.TreeNode;

import lex.Token;
import lex.TokenType;
import symbol.SymbolTable;

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

    public UnaryExp getFirstUnary() {
        return firstUnary;
    }

    public ArrayList<Token> getOperators() {
        return operators;
    }

    public ArrayList<UnaryExp> getUnaryExps() {
        return unaryExps;
    }

    public int calcConst(SymbolTable symbolTable) {
        int result = firstUnary.calcConst(symbolTable);
        for (int i = 0; i < operators.size(); i++) {
            if (operators.get(i).getType() == TokenType.MULT) {
                result = result * unaryExps.get(i).calcConst(symbolTable);
            } else if (operators.get(i).getType() == TokenType.DIV) {
                result = result / unaryExps.get(i).calcConst(symbolTable);
            } else {
                result = result % unaryExps.get(i).calcConst(symbolTable);
            }
        }
        return result;
    }

}
