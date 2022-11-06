package parse.TreeNode;

import lex.Token;
import symbol.SymbolTable;

import java.util.ArrayList;

public class Def {
    private boolean isConstant;
    private Token ident;
    private int dimension;
    private ArrayList<Exp> dimSizes;
    private InitVal val;

    public Def(boolean constant, Token ident, int dimension, ArrayList<Exp> dims, InitVal val) {
        this.isConstant = constant;
        this.ident = ident;
        this.dimension = dimension;
        this.dimSizes = dims;
        this.val = val;
    }
    // 对dimSizes数组放心用吧

    public Def(boolean constant, Token ident, int dimension, InitVal val) {
        this.isConstant = constant;
        this.ident = ident;
        this.dimension = dimension;
        this.dimSizes = null;
        this.val = val;
    }

    public Def(boolean constant, Token ident, int dimension) {
        this.isConstant = constant;
        this.ident = ident;
        this.dimension = dimension;
        this.dimSizes = null;
        this.val = null;
    }

    public Def(boolean constant, Token ident, int dimension, ArrayList<Exp> dims) {
        this.isConstant = constant;
        this.ident = ident;
        this.dimension = dimension;
        this.dimSizes = dims;
        this.val = null;
    }

    public boolean getIsConstant() {
        return isConstant;
    }

    public String getName() {
        return ident.getContent();
    }

    public int getDimension() {
        return dimension;
    }

    public void calcDims(ArrayList<Integer> dims, SymbolTable symbolTable) {
        for (int i = 0; i < dimension; i++) {
            dims.add(dimSizes.get(i).calcConst(symbolTable));
        }
    } // 用于计算全局变量中的计算第一维度、第二维度

    public void getConstDefValue(ArrayList<Integer> values, SymbolTable symbolTable, ArrayList<Integer> dims) {
        if (val != null) {
            val.getConstDefValue(values, symbolTable);
        } else {
            // val为空 全局变量需要全部赋值为0
            if (dimension == 0) {
                values.add(0);
            } else if (dimension == 1) {
                for (int i = 0; i < dims.get(0); i++) {
                    values.add(0);
                }
            } else {
                for (int i = 0; i < (dims.get(0) * dims.get(1)); i++) {
                    values.add(0);
                }
            }
        }
    }

    public InitVal getVal() {
        return val;
    }

}
