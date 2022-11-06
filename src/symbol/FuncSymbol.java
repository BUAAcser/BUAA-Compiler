package symbol;

import Ir.Addition;
import parse.TreeNode.FuncType;

import java.util.ArrayList;

public class FuncSymbol extends Symbol {
    private FuncType funcType;
    private ArrayList<FuncParaSym> paraSymbols;

    public FuncSymbol(String name, SymbolType type, FuncType funcType) {
        super(name, type);
        this.funcType = funcType;
        this.paraSymbols = new ArrayList<>();
    }

    public void addParam(FuncParaSym funcParaSym) {
        paraSymbols.add(funcParaSym);
    }

    public ArrayList<FuncParaSym> getParaSymbols() {
        return paraSymbols;
    }

    public FuncType getFuncType() {
        return funcType;
    }
}
