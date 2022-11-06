import Ir.Function;
import Ir.*;
import symbol.Symbol;
import symbol.SymbolTable;
import symbol.SymbolType;
import symbol.VarSymbol;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Generator {
    private ArrayList<Function> functions;
    private Function main;
    private SymbolTable globalTable;
    private ArrayList<String> strs;
    private ArrayList<String> mips;
    private BufferedWriter bw2;

    public Generator(ArrayList<Function> functions, Function main, SymbolTable globalTable,
                     ArrayList<String> strs, BufferedWriter bw2) {
        this.functions = functions;
        this.main = main;
        this.globalTable = globalTable;
        this.strs = strs;
        this.mips = new ArrayList<>();
        this.bw2 = bw2;
    }

    public void generateMips() {
        generateData();
        generateString();
        generateMain();
        generateFunctions();
    }

    public void printMips() {
        for (String str : mips) {
            try {
                bw2.write(str + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void generateData() {
        mips.add(".data");
        HashMap<String, Symbol> globals = globalTable.getSymbols();
        for (String name : globals.keySet()) {
            Symbol  symbol = globals.get(name);
            if (symbol.getType() != SymbolType.Func) {
                if (!((VarSymbol)symbol).getIsConstant() || symbol.getType() == SymbolType.Array) {
                    String irName = symbol.getIrName();
                    if (symbol.getType() == SymbolType.Var) {
                        int init = ((VarSymbol) symbol).getInitValue();
                        mips.add(irName + ": .word " + init);
                    } else {
                        String output = irName + ": .word ";
                        ArrayList<Integer> dims = ((VarSymbol) symbol).getDims();
                        ArrayList<Integer> initVals = ((VarSymbol) symbol).getInitValues();
                        int dim1 = dims.get(0);
                        if (dims.size() == 1) {
                            for (int i = 0; i < dim1; i++) {
                                if (i != dim1 - 1) {
                                    output = output + initVals.get(i) + ", ";
                                } else {
                                    output = output + initVals.get(i);
                                }
                            }
                        } else {
                            int dim2 = dims.get(1);
                            for (int i = 0; i < dim1; i++) {
                                for (int j = 0; j < dim2; j++) {
                                    int value = initVals.get((i * dim2 + j));
                                    if (i == dim1 -1 && j == dim2 - 1) {
                                        output = output + value;
                                    } else {
                                        output = output + value + ", ";
                                    }
                                }
                            }

                        }
                        mips.add(output);
                    }
                }
            }
        }
        mips.add("\n");
    }

    public void generateMain() {
        mips.add(".text");
        mips.add(" li $fp, 0x10040000");
        generateFunc(main, "main");
        mips.add("\n");
    }

    public void generateString() {
        for (int i = 0; i < strs.size(); i++) {
            String output;
            output = "str_" + i + ":  .ascii  \"" + strs.get(i) + "\"";
            mips.add(output);
            mips.add(".space 4");
        }
        mips.add("\n");
    }

    public void generateFunctions() {
        for (Function function : functions) {
            String name = function.getFunctionName();
            name = "Func_" + name;
            mips.add(name + ":");
            generateFunc(function, "Normal");
            mips.add("\n");
        }
    }

    public void generateFunc(Function function, String type) {
        boolean hasReturn = false;
        ArrayList<Ir> irs =  function.getIrs();
        HashMap<String, Integer> varOffset = function.getVarOffset();
        ArrayList<Ir> callFuncList = new ArrayList<>();
        for (Ir ir : irs) {
            if (ir instanceof PreToCall) {
                callFuncList = new ArrayList<>();
                callFuncList.add(ir);
            } else if (ir instanceof Push) {
                callFuncList.add(ir);
            }  else if (ir instanceof Call) {
                callFuncList.add(ir);
                generatorCall(callFuncList, varOffset);
            } else if (ir instanceof Return) {
                hasReturn = true;
                ((Return) ir).generate(mips,varOffset,type);
            } else {
                ir.generate(mips, varOffset);
            }
        }
        if (!hasReturn) {
            mips.add("jr $ra");
        }
    }  // TODO ReTurn的generate比较独特

    public void generatorCall(ArrayList<Ir> irs, HashMap<String, Integer> varOffset) {
        int paramNum = 0;
        int fp = 0;
        for (Ir pir : irs) {
            if (pir instanceof Call) {
                fp = ((Call) pir).getOffset();
            }
        }
        for (Ir ir : irs) {
            if (ir instanceof Push) {
                ((Push) ir).generate(mips, varOffset, fp + (paramNum * 4));
                paramNum++;
            } else if (ir instanceof Call) {
                ((Call) ir).generate(mips,varOffset,  fp);
            }
        }
    }
}
