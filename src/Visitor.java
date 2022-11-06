import Ir.*;
import count.*;
import lex.Pair;
import lex.Token;
import lex.TokenType;
import parse.TreeNode.Number;
import parse.TreeNode.StmtEle.AssignStmt;
import parse.TreeNode.StmtEle.ExqStmt;
import parse.TreeNode.StmtEle.PrintfStmt;
import parse.TreeNode.StmtEle.ReturnStmt;
import symbol.*;
import parse.TreeNode.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Visitor {
    private CompUnit compUnit;
    private SymbolTable globalSymbolTable;
    private ArrayList<Function> functions;
    private Function mainFunc;

    private AddressCounter addressCounter;
    private TableCounter tableCounter;
    private LabelCounter labelCounter;
    private TempCounter tempCounter;
    private StringCounter strCounter;
    private ArrayList<String> strs;

    private Function curFunction;
    private SymbolTable curTable;
    private BasicBlock curBlock;
    private BufferedWriter bw;

    public Visitor(CompUnit compUnit, BufferedWriter bw) {
        this.compUnit = compUnit;
        this.tableCounter = new TableCounter();
        this.globalSymbolTable = new SymbolTable(SymbolTableType.Global, tableCounter.allocate(),
                null);
        this.functions = new ArrayList<>();

        this.addressCounter = new AddressCounter();
        this.labelCounter = new LabelCounter();
        this.tempCounter = new TempCounter();
        this.strCounter = new StringCounter();
        this.strs = new ArrayList<>();
        strs.add("\\n");

        this.curFunction = null;
        this.curTable = null;
        this.curBlock = null;
        this.bw = bw;
        // visit();
    }

    public SymbolTable getGlobal() {
        return globalSymbolTable;
    }

    public ArrayList<Function> getFunctions() {
        return functions;
    }

    public ArrayList<String> getStrs() {
        return strs;
    }

    public Function getMainFunc() {
        return mainFunc;
    }

    public void printIr() {
        try {
            bw.write("Main:\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BasicBlock mainblock = mainFunc.getBlock();
        mainblock.printIrs(bw);
        for (Function function : functions) {
            String funcName = function.getFunctionName();
            try {
                bw.write(funcName + ":\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            BasicBlock block = function.getBlock();
            block.printIrs(bw);
        }
        int testBreakPoint = 0;
        int testBreakPoint1 = 1;
    }

    public void visit() {
        visitCompUnit(compUnit); // TODO MORE
    }

    public void allocateBasicBlock() {
        int num = labelCounter.allocate();
        BasicBlock block = new BasicBlock(num);
        curBlock = block; // 再Allocate时已经将curBlock切换了
    }

    public String allocateTemp() {
        String tempName = tempCounter.allocateTemp();
        int addressOffset = addressCounter.allocateTemp();
        curFunction.addSymbolAndOffset(tempName, addressOffset);
        return tempName;
    } // 封装此方法用于申请临时变量，并在内存中记录

    public void addGlobalSymbol(Symbol var) {
        String name = var.getName();
        String irName;
        if (var.getType() == SymbolType.Var || var.getType() == SymbolType.Array) {
            if (!((VarSymbol)var).getIsConstant() || var.getType() == SymbolType.Array) {
                irName = "Global_" + name;
            } else {
                irName = name; // 只有常量的变量不用分配存储空间
            }
        } else {
            irName = "Func_" + name;
        }
        var.setIrName(irName);
        globalSymbolTable.addSymbol(name, var);
    }

    // TODO 这里还是有点问题，临时变量这里不需要存入符号表了
    public void addLocalSymbol(SymbolTable symbolTable, VarSymbol var) {
        boolean isConstant = var.getIsConstant();
        SymbolType varType = var.getType();
        if (!isConstant || varType == SymbolType.Array) {
            int num = symbolTable.getNum();
            String irName;
            irName = "Var_" + var.getName() +  "_" + Integer.toString(num);
            var.setIrName(irName);
            int addressOffset = addressCounter.allocate(var);
            var.setOffset(addressOffset);
            symbolTable.addSymbol(var.getName(), var);
            // TODO
            curFunction.addSymbolAndOffset(irName, addressOffset);
            // 这一if分支处理普通常量的情况
        } else {
            String irName = var.getName();
            var.setIrName(irName);
            symbolTable.addSymbol(var.getName(), var);
            // 这一else分支处理常量的情况, 常量不需要中间代码名字以及不需要地址偏移来存储
        }

    } //  这个用于给局部符号表加入符号的同时计算符号的内存中的偏移, 并给出中间代码的变量名称

    public void visitCompUnit(CompUnit compUnit) {
        ArrayList<Decl> decls = compUnit.getDecls();
        for (Decl decl : decls) {
            visitGlobalDecl(decl);
        } // 全局变量处理完毕
        // TODO 下一步开始处理函数和main函数
        ArrayList<FuncDef> funcDefs = compUnit.getFuncDefs();
        for (FuncDef funcDef : funcDefs) {
            visitFuncDef(funcDef);
        }
        MainFuncDef main = compUnit.getMain();

        visitMainFunc(main);
    }

    // 用于visit全局变量、常量的定义
    public void visitGlobalDecl(Decl decl) {
        ArrayList<Def> defs = decl.getDefs();
        for (Def def : defs) {
            Symbol symbol;
            ArrayList<Integer> dims = new ArrayList<>();   // 用于存储各个维度Exp的值
            ArrayList<Integer> values = new ArrayList<>();  // 用于存储全局变量初值
            boolean isConstant = def.getIsConstant();
            String name = def.getName();
            int dimension = def.getDimension();
            def.calcDims(dims, globalSymbolTable); // 计算得到全局变量各个维度
            def.getConstDefValue(values, globalSymbolTable, dims);
            if (dimension == 0) {
                int initValue = values.get(0);
                symbol = new VarSymbol(name, isConstant, SymbolType.Var, initValue);
            } else {
                symbol = new VarSymbol(name, isConstant, SymbolType.Array, dimension, dims, values);
            } //
            addGlobalSymbol(symbol); // 全局变量是在这里被加入全局变量表的
        }
    }

    public void visitFuncDef(FuncDef funcDef) {
        String name = funcDef.getName();
        Function function = new Function(name);
        this.curFunction = function;
        addressCounter.clear();  // 地址偏移清0
        curTable = new SymbolTable(SymbolTableType.Local, tableCounter.allocate(),globalSymbolTable);
        curBlock = new BasicBlock(labelCounter.allocate());
        /// 预处理工作

        FuncType funcType = funcDef.getType();
        FuncSymbol funcSymbol = new FuncSymbol(name, SymbolType.Func, funcType);
        addGlobalSymbol(funcSymbol);


        // TODO 将参数加入局部变量表
        ArrayList<FuncFParam> funcParams = funcDef.getFuncParams();
        for (FuncFParam funcParam : funcParams) {
            VarSymbol symbol;
            String paramName = funcParam.getName();
            FuncParaSym param;
            int dimension = funcParam.getDimension();
            if (dimension == 0) {
                symbol = new VarSymbol(paramName, false, SymbolType.Var, 0);
                param = new FuncParaSym(paramName, 0);
            } else if (dimension == 1) {
                symbol = new VarSymbol(paramName, false,  SymbolType.Pointer, 1, null, null);
                param = new FuncParaSym(paramName, 1);
            } else {
                Exp constExp = funcParam.getConstExp();
                ArrayList<Integer> dims = new ArrayList<>();
                dims.add(0);
                int dim2 = constExp.calcConst(globalSymbolTable);
                dims.add(dim2);
                symbol = new VarSymbol(paramName, false,  SymbolType.Pointer, 2, dims, null);
                param = new FuncParaSym(paramName, 2, dim2);
            }
            addLocalSymbol(curTable, symbol);
            funcSymbol.addParam(param);
        }

        Block block = funcDef.getBlock();
        visitBlock(block);

        function.addBasicBlock(curBlock);
        functions.add(function);
    }

    public void visitMainFunc(MainFuncDef mainFuncDef) {
        Function main = new Function("Main");
        this.curFunction = main;
        addressCounter.clear();  // 地址偏移清0
        curTable = new SymbolTable(SymbolTableType.Local, tableCounter.allocate(),globalSymbolTable);
        curBlock = new BasicBlock(labelCounter.allocate());

        Block block = mainFuncDef.getBlock();
        visitBlock(block);
        curFunction.addBasicBlock(curBlock);
        mainFunc = main;
    }

    public void visitBlock(Block block) {
        // 进入每一个新的block前, 需要定义一个新的符号表SymbolTable
        // SymbolTable fatherTable = curTable;
        //        SymbolTable blockTable = new SymbolTable(SymbolTableType.Global, tableCounter.allocate(), fatherTable);
        //        curTable = blockTable;
        // 此处改为在调用visitBlock前调用

        ArrayList<BlockItem> items = block.getItems();
        for (BlockItem blockItem : items) {
            if (blockItem instanceof Decl) {
                visitDecl((Decl)blockItem);
            } else if (blockItem instanceof Stmt) {
                visitStmt((Stmt)blockItem);
            }
        }
        //        curTable = fatherTable;
    }

    public void visitDecl(Decl decl) {
        // 不用传symbolTable
        boolean isConstant = (decl.getDeclType() == DeclType.CONSTANT);
        ArrayList<Def> defs = decl.getDefs();
        Symbol symbol;
        ArrayList<Integer> dims = new ArrayList<>();
        ArrayList<Integer> values = new ArrayList<>();
        for (Def def : defs) {
            if (isConstant) {
                visitConstDef(def);
            } else {
                visitVarDef(def);
            }
        }
    }

    public void visitConstDef(Def def) {
        VarSymbol symbol;
        ArrayList<Integer> dims = new ArrayList<>();   // 用于存储各个维度constExp的值
        ArrayList<Integer> values = new ArrayList<>();  // 用于存储常量的初值
        String name = def.getName();
        int dimension = def.getDimension();

        def.calcDims(dims, curTable); //   计算得到常量的各个维度
        def.getConstDefValue(values, curTable, dims);   // 用于计算得到常量的初值

        if (dimension == 0) {
            int initValue = values.get(0);
            symbol = new VarSymbol(name, true, SymbolType.Var, initValue);
        } else {
            symbol = new VarSymbol(name, true, SymbolType.Array, dimension, dims, values);
        }
        addLocalSymbol(curTable, symbol);
    }

    public void visitVarDef(Def def) {
        VarSymbol symbol;

        int dimension = def.getDimension(); // 变量的维数
        ArrayList<Integer> dims = new ArrayList<>();   // 用于存储各个维度constExp的值
        def.calcDims(dims, curTable);
        String name = def.getName();
        InitVal val = def.getVal();

        if (dimension == 0) {
            symbol = new VarSymbol(name, false, SymbolType.Var, 0);
        } else {
            symbol = new VarSymbol(name, false, SymbolType.Array, dimension, dims, new ArrayList<>());
        }
        addLocalSymbol(curTable, symbol);
        if (val != null) {
            String irName = curTable.searchVar(name);
            ArrayList<String> initValues = new ArrayList<>();
            visitInitVal(val, initValues);
            if (dimension == 0) {
                Assign assign = new Assign(irName, initValues.get(0));
                curBlock.insertIr(assign);
            } else if (dimension == 1) {
                int dim1 = dims.get(0);
                for (int i = 0; i < dim1; i++) {
                    String t1 = allocateTemp();
                    LoadPointer loadPointer = new LoadPointer(t1, irName, String.valueOf(i), LoadPointType.Offset);
                    curBlock.insertIr(loadPointer);
                    Store store = new Store(initValues.get(i), t1);
                    curBlock.insertIr(store);
                }
            } else {
                int dim1 = dims.get(0);
                int dim2 = dims.get(1);
                for (int i = 0; i < dim1; i++) {
                    for (int j = 0; j < dim2; j++) {
                        String t1 = allocateTemp();
                        LoadPointer loadPointer = new LoadPointer(t1, irName, String.valueOf(i * dim2 + j),LoadPointType.Offset);
                        curBlock.insertIr(loadPointer);
                        Store store = new Store(initValues.get(i * dim2 + j), t1);
                        curBlock.insertIr(store);
                    }
                }
            }
        }

    }

    public void visitInitVal(InitVal val, ArrayList<String> initValues) {

        if (val.getType() == InitValType.SIMPLE) {
            Exp exp = val.getExp();
            initValues.add(visitExp(exp));
        } else {
            ArrayList<InitVal> vals = val.getInitVals();
            for (InitVal perVal : vals) {
                visitInitVal(perVal, initValues);
            }
        }
    }

    public String visitExp(Exp exp) {
        if (exp != null) {
            AddExp addExp = exp.getAddExp();
            return visitAddExp(addExp);
        } else {
            return "0";
        }
    }

    public String visitAddExp(AddExp addExp) {
        String nowResult;
        MulExp firstMul = addExp.getFirstMul();
        ArrayList<Token> operators = addExp.getOperators();
        ArrayList<MulExp> mulExps = addExp.getMulExps();
        String firstResult = visitMulExp(firstMul);
        if (operators.size() == 0) {
            return firstResult;
        } else {
            nowResult = firstResult;
            for (int i = 0; i < operators.size(); i++) {
                Token op = operators.get(i);
                MulExp mulExp = mulExps.get(i);
                String temp = allocateTemp(); // 调用函数分配临时寄存器
                String operand2 = visitMulExp(mulExp);
                if (op.getType() == TokenType.PLUS) {
                    Addition addition = new Addition(temp, nowResult, operand2);
                    curBlock.insertIr(addition);
                } else {
                    Subtraction subtraction = new Subtraction(temp, nowResult, operand2);
                    curBlock.insertIr(subtraction);
                }
                nowResult = temp;
            }
            return nowResult;
        }
    }

    public String visitMulExp(MulExp mulExp) {
        String nowResult;
        UnaryExp firstUnary = mulExp.getFirstUnary();
        ArrayList<Token> operators = mulExp.getOperators();
        ArrayList<UnaryExp> unaryExps = mulExp.getUnaryExps();
        String firstResult = visitUnaryExp(firstUnary);
        if (operators.size() == 0) {
            return firstResult;
        } else {
            nowResult = firstResult;
            for (int i = 0; i < operators.size(); i++) {
                Token op = operators.get(i);
                UnaryExp perUnary = unaryExps.get(i);
                String temp = allocateTemp();
                String operand2 = visitUnaryExp(perUnary);
                if (op.getType() == TokenType.MULT) {
                    Multiply multiply = new Multiply(temp, nowResult, operand2);
                    curBlock.insertIr(multiply);
                } else if (op.getType() == TokenType.DIV) {
                    Divide divide = new Divide(temp, nowResult, operand2);
                    curBlock.insertIr(divide);
                } else {
                    Mod mod = new Mod(temp, nowResult, operand2);
                    curBlock.insertIr(mod);
                }
                nowResult = temp;
            }
            return nowResult;
        }
    }
    // TODO 目前所有AddExp, MulExp均不考虑立即数直接求解，先直接将立即数加入中间代码，后续再处理

    public String visitUnaryExp(UnaryExp unaryExp) {
        PrimaryExp primaryExp = unaryExp.getPrimaryExp();
        Token func = unaryExp.getFunc();
        String result;
        if (primaryExp != null) {
            result = visitPrimaryExp(primaryExp);
        } else {
            String funcName = func.getContent();
            FuncRParams funcRParams = unaryExp.getFuncRparams();

            if (funcRParams != null) {
                ArrayList<String> canShus = new ArrayList<>();
                ArrayList<Exp> realParamsExps = funcRParams.getExps();
                ArrayList<FuncParaSym> formalParams = globalSymbolTable.getFormalParams(funcName);
                for (int i = 0; i < formalParams.size(); i++) {
                    if (formalParams.get(i).getDimension() == 0) {
                        String canShu = visitExp(realParamsExps.get(i));
                        canShus.add(canShu);
                    } else {
                        String canShu = visitArrayParam(realParamsExps.get(i), formalParams.get(i));
                        canShus.add(canShu);
                    }
                    curBlock.insertIr(new PreToCall());
                    for (String canShu : canShus) {
                        Push push = new Push(canShu);
                        curBlock.insertIr(push);
                    }
                }
            }
            int offset = addressCounter.getNow();
            Call call = new Call("Func_" + funcName, offset);
            curBlock.insertIr(call);
            FuncType funcType = globalSymbolTable.searchFuncType(funcName);
            if (funcType == FuncType.INT) {
                String resultReg = allocateTemp();
                Ret ret = new Ret(resultReg);
                curBlock.insertIr(ret);
                result = resultReg;
            } else {
                result = "0"; //TODO  对应void类函数，此处还可以继续整改
            }
        }

        ArrayList<Token> operators = unaryExp.getOperators();
        for (Token op : operators) {
            if (op.getType() == TokenType.MINU) {
                String temp = allocateTemp();
                Negative negative = new Negative(temp, result);
                curBlock.insertIr(negative);
                result = temp;
            } else if (op.getType() == TokenType.NOT) {
                String temp = allocateTemp();
                NotLogical not = new NotLogical(temp, result);
                curBlock.insertIr(not);
                result = temp;
            }
        }
        return result;
    }

    public String visitArrayParam(Exp exp, FuncParaSym paraSym) {
        AddExp addExp = exp.getAddExp();
        MulExp mulExp = addExp.getFirstMul();
        UnaryExp unaryExp  = mulExp.getFirstUnary();
        PrimaryExp primaryExp = unaryExp.getPrimaryExp();
        Lval lval = primaryExp.getLval();
        ArrayList<Exp> indexes = lval.getIndexes();
        String arrayName = lval.getLvalName();
        int dimension = paraSym.getDimension();
        String str;
        if (dimension == 2) {
            String irName = curTable.searchVar(arrayName);
            String reg = allocateTemp();
            SymbolType arrayType = curTable.searchSymbolType(arrayName);
            LoadPointer loadPointer;
            if (arrayType == SymbolType.Array) {
                loadPointer = new LoadPointer(reg, irName, "0", LoadPointType.Offset);
            } else {
                loadPointer = new LoadPointer(reg, irName, "0", LoadPointType.Pointer);
            }
            curBlock.insertIr(loadPointer);
            str = reg;
        } else {
            String irName = curTable.searchVar(arrayName);
            String reg = allocateTemp();
            if (indexes.size() == 0) {
                SymbolType arrayType = curTable.searchSymbolType(arrayName);
                LoadPointer loadPointer;
                if (arrayType == SymbolType.Array) {
                    loadPointer = new LoadPointer(reg, irName, "0", LoadPointType.Offset);
                } else {
                    loadPointer = new LoadPointer(reg, irName, "0", LoadPointType.Pointer);
                }
                curBlock.insertIr(loadPointer);
            } else {
                String index = visitExp(indexes.get(0));
                int dim2 = paraSym.getDimension();
                String t1 = allocateTemp();
                Multiply multiply = new Multiply(t1, index, String.valueOf(dim2));
                curBlock.insertIr(multiply);
                SymbolType arrayType = curTable.searchSymbolType(arrayName);
                LoadPointer loadPointer;
                if (arrayType == SymbolType.Array) {
                    loadPointer = new LoadPointer(reg, irName, t1, LoadPointType.Offset);
                } else {
                    loadPointer = new LoadPointer(reg, irName, t1, LoadPointType.Pointer);
                }
                curBlock.insertIr(loadPointer);
            }
            str = reg;
        }
        return str;
    }

    public String visitPrimaryExp(PrimaryExp primaryExp) {
        String result;
        int type = primaryExp.getPrimaryExpType();
        if (type == 1) {
            Exp exp = primaryExp.getExp();
            result = visitExp(exp);
        } else if (type == 2) {
            Lval lval = primaryExp.getLval();
            Pair<String, Integer> per = visitLval(lval, true);
            result = per.getLeft();
        } else {
            Number number = primaryExp.getNumber();
            result = number.getNumberStr();
        }
        return result;
    }

    public Pair<String, Integer> visitLval(Lval lval, boolean load) {
        // true 代表着是对lval是读取
        String result;
        int value = 0;
        String name = lval.getLvalName();
        ArrayList<Exp> indexes = lval.getIndexes();
        if (indexes.size() == 0) {
            result = curTable.searchVar(name);  // 搜索得到的是IrName
        } else if (indexes.size() == 1) {
            Exp exp1 = indexes.get(0);

            // 算地址
            String arrayIrName = curTable.searchVar(name); // 搜索得到的是IrName

            String temp = allocateTemp(); // 申请临时寄存器给pointer地址
            String offset = visitExp(exp1);

            SymbolType arrayType = curTable.searchSymbolType(name);
            LoadPointer loadPointer;
            if (arrayType == SymbolType.Array) {
                loadPointer = new LoadPointer(temp, arrayIrName, offset, LoadPointType.Offset);
            } else {
                loadPointer = new LoadPointer(temp, arrayIrName, offset, LoadPointType.Pointer);
            }

            curBlock.insertIr(loadPointer);
            result = temp; // 如果是存入(store)的话返回元素的地址即可

            if (load) {
                String resultTemp = allocateTemp(); // 再分配一个寄存器用于
                Load loadNum = new Load(resultTemp, temp);
                curBlock.insertIr(loadNum);
                result = resultTemp;
            }
            value = 1;
        } else {
            String arrayIrName = curTable.searchVar(name); // 搜索得到的是IrName
            int dimension2 = curTable.searchArrayDimension(name);

            Exp exp1 = indexes.get(0);
            Exp exp2 = indexes.get(1);

            String offset1 = visitExp(exp1);


            String offset2 = visitExp(exp2);

            String temp3 = allocateTemp();
            Multiply multiply = new Multiply(temp3, offset1, String.valueOf(dimension2));
            curBlock.insertIr(multiply);

            String temp4 = allocateTemp(); // 这个才是真正的二维数组元素距离首地址的偏移
            Addition addition = new Addition(temp4, temp3, offset2);
            curBlock.insertIr(addition);

            SymbolType arrayType = curTable.searchSymbolType(name);
            LoadPointer loadPointer;
            String temp5 = allocateTemp(); // 这才是总地址偏移

            if (arrayType == SymbolType.Array) {
                loadPointer = new LoadPointer(temp5, arrayIrName, temp4, LoadPointType.Offset);
            } else {
                loadPointer = new LoadPointer(temp5, arrayIrName, temp4, LoadPointType.Pointer);
            }

            curBlock.insertIr(loadPointer);
            result = temp5;

            if (load) {
                String resultTemp = allocateTemp(); // 再分配一个临时变量用于Load值
                Load loadNum = new Load(resultTemp, temp5);
                curBlock.insertIr(loadNum);
                result = resultTemp;
            }
            value = 2;
        }
        return new Pair<>(result, value);
    }

    public void visitStmt(Stmt stmt) {
        if (stmt instanceof AssignStmt) {
            visitAssignStmt((AssignStmt)stmt);
        } else if (stmt instanceof ExqStmt) {
            visitExqStmt((ExqStmt)stmt);
        } else if (stmt instanceof Block) {
            SymbolTable fatherTable = curTable;
            SymbolTable blockTable = new SymbolTable(SymbolTableType.Local, tableCounter.allocate(), fatherTable);
            curTable = blockTable;
            visitBlock((Block) stmt);
            curTable = fatherTable;
        } else if (stmt instanceof ReturnStmt) {
            visitReturnStmt((ReturnStmt) stmt);
        } else if (stmt instanceof PrintfStmt) {
            visitPrintfStmt((PrintfStmt)stmt);
        }
    }

    public void visitAssignStmt(AssignStmt assignStmt) {
        Lval lval = assignStmt.getLval();
        Exp exp = assignStmt.getExp();
        if (exp != null) {
            String value = visitExp(exp);
            Pair<String, Integer> pair = visitLval(lval, false);
            String var = pair.getLeft();
            int type = pair.getRight();
            if (type == 0) {
                Assign assign = new Assign(var, value);
                curBlock.insertIr(assign);
            } else {
                Store store = new Store(value, var);
                curBlock.insertIr(store);
            }
        } else {
            Pair<String, Integer> pair = visitLval(lval, false);
            String var = pair.getLeft();
            int type = pair.getRight();
            GetInt getInt;
            if (type == 0) {
                getInt = new GetInt(var, null);
            } else {
                getInt = new GetInt(null, var);
            }
            curBlock.insertIr(getInt);
        }
    }

    public void visitExqStmt(ExqStmt exqStmt) {
        visitExp(exqStmt.getExp());
    }

    public void visitReturnStmt(ReturnStmt returnStmt) {
        Exp exp = returnStmt.getExp();
        String retValue = "";
        Return ret;
        if (exp != null) {
            String t1 = visitExp(exp);
            ret = new Return(t1);
        } else {
            ret =  new Return(null);
        }
        curBlock.insertIr(ret);
    }

    public void visitPrintfStmt(PrintfStmt printfStmt) {
        int expNum = 0;
        String str = printfStmt.getContent().replace("\"", "");
        ArrayList<Exp> exps = printfStmt.getExps();
        int index = 0;
        StringBuilder output = new StringBuilder();
        while (index < str.length()) {
            if ((int) str.charAt(index) == 32 || (int) str.charAt(index) == 33 ||
                    ((int) str.charAt(index) >= 40 && (int) str.charAt(index) <= 126 &&
                            (int) str.charAt(index) != 92)) {
                output.append(str.charAt(index));
                index++;
            } else if (str.charAt(index) == '\\') {
                if (!output.toString().isEmpty()) {
                    int strNo = strCounter.allocate();
                    PrintStr printStr = new PrintStr(strNo, null);
                    curBlock.insertIr(printStr);
                    strs.add(output.toString());
                    output = new StringBuilder();
                }
                index += 2;
                PrintStr printStr1 = new PrintStr(0, null);
                curBlock.insertIr(printStr1);
            } else if (str.charAt(index) == '%') {
                if (!output.toString().isEmpty()) {
                    int strNo = strCounter.allocate();
                    PrintStr printStr = new PrintStr(strNo, null);
                    curBlock.insertIr(printStr);
                    strs.add(output.toString());
                    output = new StringBuilder();
                }
                Exp exp = exps.get(expNum);
                String temp = visitExp(exp);
                expNum++;
                PrintStr printStr = new PrintStr(0, temp);
                curBlock.insertIr(printStr);
                index += 2;
            }
        }
        if (!output.toString().isEmpty()) {
            int strNo = strCounter.allocate();
            PrintStr printStr = new PrintStr(strNo, null);
            curBlock.insertIr(printStr);
            strs.add(output.toString());
        }
    }     // 备注： str_0 是 '\n';

    public boolean judgeIsNum(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
