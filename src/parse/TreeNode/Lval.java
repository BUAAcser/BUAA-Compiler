package parse.TreeNode;

import lex.Token;
import symbol.SymbolTable;

import java.util.ArrayList;

public class Lval {
    private Token ident;
    private ArrayList<Exp> indexes;

    public Lval(Token ident, ArrayList<Exp> indexes) {
        this.ident = ident;
        this.indexes = indexes;
    }

    public String getLvalName() {
        return ident.getContent();
    }

    public ArrayList<Exp> getIndexes() {
        return indexes;
    }

    public int calcConst(SymbolTable symbolTable) {
        String name = ident.getContent();
        int dimension = indexes.size();
        int res;
        ArrayList<Integer> indexResult = new ArrayList<>();
        if (dimension == 1) {
            indexResult.add(indexes.get(0).calcConst(symbolTable));
        } else if (dimension == 2) {
            indexResult.add(indexes.get(0).calcConst(symbolTable));
            indexResult.add(indexes.get(1).calcConst(symbolTable));
        }
        return symbolTable.searchConstValue(name, indexResult);
    }

}
