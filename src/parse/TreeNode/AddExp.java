package parse.TreeNode;

import lex.Token;
import lex.TokenType;
import symbol.SymbolTable;

import java.util.ArrayList;
import java.util.Optional;

public class AddExp {
    private MulExp firstMul;
    private ArrayList<Token> operators;
    private ArrayList<MulExp> mulExps;

    public AddExp(MulExp firstMul, ArrayList<Token> ops, ArrayList<MulExp> mulExps) {
        this.firstMul = firstMul;
        this.operators = ops;
        this.mulExps = mulExps;
    }

    public int calcConst(SymbolTable symbolTable) {
        int result = firstMul.calcConst(symbolTable);
        for (int i = 0; i < operators.size(); i++) {
            if (operators.get(i).getType() == TokenType.PLUS) {
                result = result + mulExps.get(i).calcConst(symbolTable);
            } else {
                result = result - mulExps.get(i).calcConst(symbolTable);
            }
        }
        return result;
    } // 用于在全局中计算Exp

    public MulExp getFirstMul() {
        return firstMul;
    }

    public ArrayList<Token> getOperators() {
        return operators;
    }

    public ArrayList<MulExp> getMulExps() {
        return mulExps;
    }


}
