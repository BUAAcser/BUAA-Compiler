package symbol;

import parse.TreeNode.FuncType;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    private SymbolTableType tableType;
    private int num;
    private HashMap<String, Symbol> symbols;
    private SymbolTable fatherTable;

    public SymbolTable(SymbolTableType type,  int num, SymbolTable fatherTable) {
        this.tableType = type;
        this.num = num;
        this.symbols = new HashMap<>();
        this.fatherTable = fatherTable;
    }

    //    public boolean judgeVarExist(String name) {
    //        return true;
    //    }
    // 可以先不写

    public int searchConstValue(String name, ArrayList<Integer> indexes) {
        if (this.symbols.containsKey(name)) {
            return ((VarSymbol)this.symbols.get(name)).getValue(indexes);
        } else {
            if (this.fatherTable != null) {
                return this.fatherTable.searchConstValue(name, indexes);
            } else {
                return 0; // 这个分支就是说找遍了所有的符号表都找不到这个常量，估计是出问题了，暂且返回个0
            }
        }
    }

    /*    public int searchValue(String name, ArrayList<Integer> indexes) {
    //        return ((VarSymbol)symbols.get(name)).getValue(indexes);
           }
        // 只要保证全局表中不会出现函数、变量同名即可 */

    public String searchVar(String name) {
        String result;
        if (this.symbols.containsKey(name)) {
            Symbol symbol = this.symbols.get(name);
            if (((VarSymbol)symbol).getIsConstant() && symbol.getType() == SymbolType.Var) {
                int value = ((VarSymbol) symbol).getInitValue();
                result = String.valueOf(value);
                // 变量如果是常量直接返回initValue
            } else {
                result = symbol.getIrName();
                // 变量或数组  返回irName
            }
        } else {
            if (this.fatherTable != null) {
                result = this.fatherTable.searchVar(name);
            } else {
                result = "There is someThing wrong in my testFile or program " + name;
            }
        }
        return result;
    }

    public int searchArrayDimension(String name) {
        int dimension;
        if (this.symbols.containsKey(name)) {
            Symbol symbol = this.symbols.get(name);
            dimension = ((VarSymbol)symbol).getDimension2();
        } else {
            if (this.fatherTable != null) {
                dimension = fatherTable.searchArrayDimension(name);
            } else {
                System.out.println("There is someThing wrong in my testFile or program " +  name);
                dimension = 0;
            }
        }
        return dimension;
    }

    public FuncType searchFuncType(String name) {
        if (this.symbols.containsKey(name)) {
            return ((FuncSymbol)this.symbols.get(name)).getFuncType();
        }  else {
            System.out.println("There is something wrong in my testFile or program " + name);
            return FuncType.INT;
        } // 只在globalTable中查询
    }

    public SymbolType searchSymbolType(String name) {
        if (this.symbols.containsKey(name)) {
            return (this.symbols.get(name)).getType();
        }  else {
            if (this.fatherTable != null) {
                return this.fatherTable.searchSymbolType(name);
            } else {
                System.out.println("There is something wrong in my testFile or program with name " + name);
                System.out.println("searchSymbolType\n");
                return SymbolType.Array;
            }
        }
    }

    public ArrayList<FuncParaSym> getFormalParams(String name) {
        if (this.symbols.containsKey(name)) {
            return ((FuncSymbol)this.symbols.get(name)).getParaSymbols();
        }  else {
            System.out.println("There is something wrong in my testFile or program " + name);
            System.out.println("getFormalParams\n");
            return new ArrayList<FuncParaSym>();
        }
    }

    public void addSymbol(String name, Symbol symbol) {
        symbols.put(name, symbol);
    }

    public int getNum() {
        return num;
    }

    public HashMap<String, Symbol> getSymbols() {
        return symbols;
    }

    public boolean isGlobal(String name) {
        if (this.symbols.containsKey(name)) {
            return (this.tableType == SymbolTableType.Global);
        }  else {
            if (this.fatherTable != null) {
                return this.fatherTable.isGlobal(name);
            } else {
                System.out.println("There is something wrong in my testFile or program with name " + name);
                System.out.println("judge is Global\n");
                return false;
            }
        }
    }
}
