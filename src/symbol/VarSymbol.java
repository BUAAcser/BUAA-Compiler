package symbol;

import java.util.ArrayList;

public class VarSymbol extends Symbol {
    private boolean isConstant;
    private int dimension;
    private ArrayList<Integer> dims = null;
    private int initValue;
    private ArrayList<Integer> arrayInitValue = null;
    private int offset = -1;

    public VarSymbol(String name, boolean isConstant, SymbolType type, int initValue) {
        super(name, type);
        this.isConstant = isConstant;
        this.dimension = 0;
        this.dims = null;
        this.initValue = initValue;
        this.arrayInitValue = null;
    } // 普通变量或常量的创建方法

    public VarSymbol(String name, boolean isConstant, SymbolType type, int dimension,
                  ArrayList<Integer> dims, ArrayList<Integer> arrayInitValue) {
        super(name,type);
        this.isConstant = isConstant;
        this.dimension = dimension;
        this.dims = dims;
        this.initValue = 0;
        this.arrayInitValue = arrayInitValue;
    }  // 普通数组或常量数组的创建方法

    public int getValue(ArrayList<Integer> indexes) {
        int dimen = indexes.size();
        if (dimen == 0) {
            return initValue;
        } else if (dimen == 1) {
            return arrayInitValue.get(indexes.get(0));
        } else {
            int index = indexes.get(0) * dims.get(1) + indexes.get(1);
            return arrayInitValue.get(index);
        }
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public ArrayList<Integer> getDims() {
        return dims;
    }

    public boolean getIsConstant() {
        return isConstant;
    }

    public int getInitValue() {
        return initValue;
    } // 和getValue方法有点重合，但是不管了，以后再说吧

    public int getDimension2() {
        return dims.get(1);
    }

    public ArrayList<Integer> getInitValues() {
        return arrayInitValue;
    }
}
