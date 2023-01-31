package parse;

import lex.Lexer;
import lex.Token;
import lex.TokenType;
import parse.TreeNode.*;
import parse.TreeNode.Number;
import parse.TreeNode.StmtEle.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {
    private Lexer lexer;
    private CompUnit compUnit;
    private BufferedWriter bf;

    public Parser(Lexer lexer, BufferedWriter bf) {
        this.lexer = lexer;
        this.bf = bf;
        compUnit = parseCompUnit();
    }

    public CompUnit getCompUnit() {
        return this.compUnit;
    }

    public void printStream(String str) {
        try {
            bf.write(str);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Token getNextToken() {
        Token token = lexer.getNextToken();
        String str = token.getType() + " " + token.getContent() + "\n";
        try {
            bf.write(str);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return token;
    }

    public CompUnit parseCompUnit() {
        ArrayList<Decl> decls = new ArrayList<>();
        ArrayList<FuncDef> functions = new ArrayList<>();
        MainFuncDef main = null;

        while (lexer.hasNextToken()) {
            Token firToken = lexer.visitNextToken();
            Token secToken = lexer.visitNext2Token();
            if (firToken.getType() == TokenType.CONSTTK) {
                decls.add(parseDecl());
            } else if (firToken.getType() == TokenType.INTTK && secToken.getType() == TokenType.IDENFR) {
                Token thTok = lexer.visitNext3Token();
                if (!(thTok != null && thTok.getType() == TokenType.LPARENT)) {
                    decls.add(parseDecl());
                } else {
                    break;
                }
            } else {
                break;
            }
        }

        while (lexer.hasNextToken()) {
            Token firToken = lexer.visitNextToken();
            Token secToken = lexer.visitNext2Token();
            if (firToken.getType() == TokenType.VOIDTK) {
                functions.add(parseFuncDecl());
            } else if (firToken.getType() == TokenType.INTTK && secToken.getType() == TokenType.IDENFR) {
                functions.add(parseFuncDecl());
            } else {
                break;
            }
        }

        main = parseMain();

        CompUnit compUnit = new CompUnit(decls, functions, main);
        printStream("<CompUnit>\n");
        return compUnit;
    }

    public Decl parseDecl() {
        ArrayList<Def> defs = new ArrayList<>();
        boolean isConstant = false;
        Token symbol = getNextToken();
        if (symbol.getType() == TokenType.CONSTTK) {
            getNextToken(); // 把下一个int吃掉
            isConstant = true;
        }
        /* 如果之后要扩展成分正确性判断的话，const 和 int 开头必正确， const 后面不能保证一定会出现int， 正确性判断从此处开始，
        但是无所谓了，课设输入保证大多数输入不残缺, 乏了, 已经没那个精力分析正确性了，就只能在这水水注释  */
        Def def = parseDef(isConstant);
        defs.add(def);

        symbol = getNextToken();

        // TODO 之后错误处理设计需要考虑分号不存在的情况，改写此处即可， 把getnextToken换成 visitNext， 现在改不动了
        while (symbol.getType() == TokenType.COMMA) {
            def = parseDef(isConstant);
            defs.add(def);
            symbol = getNextToken();
        }

        // 此时symbol得到的Token表示 ；SEMICN
        DeclType type;
        if (isConstant) {
            printStream("<ConstDecl>\n");
            type = DeclType.CONSTANT;
        } else {
            printStream("<VarDecl>\n");
            type = DeclType.VAR;
        }

        return new Decl(type, defs);
    }  // 已完成

    public Def parseDef(boolean isConstant) {
        Def def = null;
        Token ident;
        Token symbol;
        int dimension = 0;
        ArrayList<Exp> dimSizes = new ArrayList<>();
        InitVal initval = null;
        if (isConstant) {
            ident = getNextToken();
            symbol = getNextToken();
            while (symbol.getType() == TokenType.LBRACK) {
                dimension++;
                Exp constantExp = parseExp(true);
                dimSizes.add(constantExp);
                symbol = getNextToken();
                if (symbol.getType() == TokenType.RBRACK) {
                    symbol = getNextToken();
                } /* else {
                    throw new exception
                } */
            }
            // 退出循环 即此时symbol的TokenType为 TokenType.ASSIGN,即为 =
            initval = parseInitVal(true);
            def = new Def(true, ident, dimension, dimSizes, initval, false);
            printStream("<ConstDef>\n");
        } else {
            ident = getNextToken();
            symbol = lexer.visitNextToken();
            while (symbol.getType() == TokenType.LBRACK) {
                dimension++;
                getNextToken(); // get [
                Exp constantExp = parseExp(true);
                dimSizes.add(constantExp);
                symbol = getNextToken(); // get ]
                if (symbol.getType() == TokenType.RBRACK) {
                    symbol = lexer.visitNextToken();
                } /* else {
                    throw new exception
                } */
            }  // visit 发现下一token 不是 [ 则退出循环
            // 判断下一Token是不是 =
            if (symbol.getType() == TokenType.ASSIGN) {
                getNextToken(); // get 消化 =
                Token nextFinal = lexer.visitNextToken();
                if (nextFinal.getType() == TokenType.GETINTTK) {
                    getNextToken(); // getInt
                    getNextToken(); // get(
                    getNextToken(); // get)
                    def = new Def(false, ident, dimension, dimSizes, null, true);
                } else {
                    initval = parseInitVal(false);
                    def = new Def(false, ident, dimension, dimSizes, initval, false);
                }
            } else {
                def = new Def(false, ident, dimension, dimSizes, null, false);
            }
            printStream("<VarDef>\n");
        }
        return def;
    } // 已完成

    public Exp parseExp(boolean isConstant) {
        Exp exp = new Exp(isConstant, parseAddExp());
        if (isConstant) {
            printStream("<ConstExp>\n");
        } else {
            printStream("<Exp>\n");
        }
        return exp;
    }  // 已完成

    public InitVal parseInitVal(boolean isConstant) {
        Token token = lexer.visitNextToken();
        InitVal returnVal = null;
        if (token.getType() != TokenType.LBRACE) {
            Exp exp = parseExp(isConstant);
            returnVal = new InitVal(isConstant, InitValType.SIMPLE, exp);
        } else {
            getNextToken(); // get得到的就是 { LBRACE
            ArrayList<InitVal> vals = new ArrayList<>();
            Token sym = lexer.visitNextToken();
            if (sym.getType() == TokenType.RBRACE) {
                getNextToken();
                if (isConstant) {
                    printStream("<ConstInitVal>\n");
                } else {
                    printStream("<InitVal>\n");
                }
                return new InitVal(isConstant, InitValType.ARRAY, vals);
            }
            InitVal val = parseInitVal(isConstant);
            vals.add(val);
            sym = getNextToken();
            while (sym.getType() == TokenType.COMMA) {
                val = parseInitVal(isConstant);
                vals.add(val);
                sym = getNextToken();
            }
            // 退出循环，此时sym得到的Token为 }  RBRACE
            returnVal = new InitVal(isConstant, InitValType.ARRAY, vals);
        }
        if (isConstant) {
            printStream("<ConstInitVal>\n");
        } else {
            printStream("<InitVal>\n");
        }
        return returnVal;
    }

    public FuncDef parseFuncDecl() {
        FuncDef def = null;
        FuncType type = parseFuncType();
        Token ident = getNextToken();
        FuncFParams params = null;
        Block block = null;
        getNextToken(); // 跳过 (
        Token sym = lexer.visitNextToken();
        if (sym.getType() == TokenType.INTTK) {
            params = parseFuncFParams();
        }
        // 此时token的位置可能为) , 需要getNext吃掉； 也可能缺少),需要报错。
        sym = getNextToken();
        if (sym.getType() == TokenType.RPARENT) {
            block = parseBlock();
        } /* else {
            // Todo 缺少右括号错误处理
        } */
        printStream("<FuncDef>\n");
        return new FuncDef(type,ident, params, block);
    }

    public FuncType parseFuncType() {
        Token type = getNextToken();
        printStream("<FuncType>\n");
        if (type.getType() == TokenType.INTTK) {
            return FuncType.INT;
        } else {   // 此处由于错误处理不含void和int错误情况，直接else，也无需判断
            return FuncType.VOID;
        }
    } // 已完成

    public FuncFParams parseFuncFParams() {
        ArrayList<FuncFParam> funcFParams = new ArrayList<>();
        FuncFParam first  = parseFuncFParam();
        funcFParams.add(first);
        Token sym = lexer.visitNextToken();
        while (sym.getType() == TokenType.COMMA) {
            getNextToken(); //  吃掉 ,逗号
            funcFParams.add(parseFuncFParam());
            sym = lexer.visitNextToken();
        }
        printStream("<FuncFParams>\n");
        return new FuncFParams(funcFParams);
    }

    public FuncFParam parseFuncFParam() {
        int dimension = 0;
        Exp exp = null;
        getNextToken(); // 得到Token int
        Token ident = getNextToken();  // 得到ident
        Token sym = lexer.visitNextToken();
        while (sym.getType() == TokenType.LBRACK) {
            dimension++;
            if (dimension == 1) {
                getNextToken(); // 吃掉 [
                sym = lexer.visitNextToken();
                if (sym.getType() == TokenType.RBRACK) {
                    getNextToken(); // 吃掉 ]
                    sym = lexer.visitNextToken();
                } /* else {

                } */
            } else {
                getNextToken(); //  吃掉 [
                exp = parseExp(true);
                sym = lexer.visitNextToken();
                if (sym.getType() == TokenType.RBRACK) {
                    getNextToken(); // 吃掉 ]
                    sym = lexer.visitNextToken();
                }/* else {

                } */
            }
        }
        printStream("<FuncFParam>\n");
        return new FuncFParam(ident, dimension, exp);
    }

    public Block parseBlock() {
        ArrayList<BlockItem> blockItems = new ArrayList<>();
        getNextToken(); // 吃掉 左花括号{
        Token next = lexer.visitNextToken();
        while (next.getType() != TokenType.RBRACE) {
            if (next.getType() == TokenType.CONSTTK || next.getType() == TokenType.INTTK) {
                Decl decl = parseDecl();
                blockItems.add(decl);
            } else {
                Stmt stmt = parseStmt();
                blockItems.add(stmt);
            }
            next = lexer.visitNextToken();
        }
        getNextToken(); // 吃掉 右花括号}
        printStream("<Block>\n");
        return new Block(blockItems);
    }

    public Stmt parseStmt() {
        Stmt stmt = null;
        Token next = lexer.visitNextToken();
        if (next.getType() == TokenType.PRINTFTK) {
            stmt = parsePrintFStmt();
        } else if (next.getType() == TokenType.RETURNTK) {
            stmt = parseReturnStmt();
        } else if (next.getType() == TokenType.BREAKTK) {
            getNextToken(); // 吃掉Break;
            getNextToken(); // 吃掉分号;
            stmt = new BreakStmt(); // TODO 处理缺少分号
        } else if (next.getType() == TokenType.CONTINUETK) {
            getNextToken(); // 吃掉Continue;
            getNextToken(); // 吃掉分号;
            stmt = new ContinueStmt();
        } else if (next.getType() == TokenType.WHILETK) {
            stmt = parseWhileStmt();
        } else if (next.getType() == TokenType.IFTK) {
            stmt = parseIfStmt();
        } else if (next.getType() == TokenType.LBRACE) {
            stmt = parseBlock();
        } else if (next.getType() == TokenType.IDENFR) {
            stmt = parseAssignOrExqStmt();
        } else if (nextExp()) {
            stmt = parseExqStmt();
        } else if (next.getType() == TokenType.SEMICN) {
            getNextToken(); // 吃掉分号;
            stmt = new ExqStmt(null);
        } /* else {
            抛出异常
        } */
        printStream("<Stmt>\n");
        return stmt;
    }

    public PrintfStmt parsePrintFStmt() {
        ArrayList<Exp> exps = new ArrayList<>();
        getNextToken(); // 得到token为printf， 目前token下一个移到了(
        getNextToken(); // 得到token为(, 目前token下一个移到了 forMatString
        Token str = getNextToken(); // 目前token为str, 下一可能为, 或者), ；或者缺少成分
        Token sym = lexer.visitNextToken();
        while (sym.getType() == TokenType.COMMA) {
            getNextToken(); // 吃掉逗号，
            Exp exp = parseExp(false);
            exps.add(exp);
            sym = lexer.visitNextToken();
        }
        if (sym.getType() == TokenType.RPARENT) {
            getNextToken(); // 吃掉右括号)
            sym = lexer.visitNextToken();
        }  /* else {
            处理缺少右括号的错误
        } */
        if (sym.getType() == TokenType.SEMICN) {
            getNextToken(); // 吃掉分号; Token移到分号;后一个
        }  /* else {
            处理缺少分号的错误
        } */
        return new PrintfStmt(str, exps);
    }

    public ReturnStmt parseReturnStmt() {
        ReturnStmt returnStmt = null;
        getNextToken(); // 吃掉return
        if (nextExp()) {
            Exp exp = parseExp(false);
            returnStmt = new ReturnStmt(exp);
        } else {
            returnStmt = new ReturnStmt();
        }
        Token sym = lexer.visitNextToken();
        if (sym.getType() == TokenType.SEMICN) {
            getNextToken(); // 吃掉分号;
        } /*else {
            异常处理缺少分号的情况
        } */
        return returnStmt;
    }

    public WhileStmt parseWhileStmt() {
        getNextToken(); // 吃掉while
        getNextToken(); // 吃掉左括号(
        Cond cond = parseCond();
        Token next = lexer.visitNextToken();
        if (next.getType() == TokenType.RPARENT) {
            getNextToken(); // 吃掉右括号)
        } /* else {
            异常处理没有右括号的错误
        } */
        Stmt stmt = parseStmt();
        return new WhileStmt(cond, stmt);
    }

    public IfStmt parseIfStmt() {
        Stmt elseStmt = null;
        getNextToken(); // 吃掉IF
        getNextToken(); // 吃掉(
        Cond cond = parseCond();
        Token next = lexer.visitNextToken();
        if (next.getType() == TokenType.RPARENT) {
            getNextToken(); // 吃掉右括号)
        } /* else {
            异常处理没有右括号的错误
        } */
        Stmt ifStmt = parseStmt();
        Token sym = lexer.visitNextToken();
        if (sym.getType() == TokenType.ELSETK) {
            getNextToken(); // 吃掉 else
            elseStmt = parseStmt();
        }
        return new IfStmt(cond,ifStmt, elseStmt);
    }

    public AssignStmt parseAssignStmt() {
        Lval lval = parseLval();
        Exp exp = null;
        getNextToken(); // 吃掉等号
        if (nextExp()) {
            exp = parseExp(false);
            Token sym = lexer.visitNextToken();
            if (sym.getType() == TokenType.SEMICN) {
                getNextToken(); // 吃掉 分号;
            } /* else {

            } */
        } else if (lexer.visitNextToken().getType() == TokenType.GETINTTK) {
            getNextToken(); // 吃掉 getInt
            getNextToken(); // 吃掉 (
            getNextToken(); // 吃掉 )
            Token sym = lexer.visitNextToken();
            if (sym.getType() == TokenType.SEMICN) {
                getNextToken(); // 吃掉 分号;
            }  /* else {

            } */
        }
        return new AssignStmt(lval, exp);
    }

    public ExqStmt parseExqStmt() {
        Exp exp  = parseExp(false);
        Token sym = lexer.visitNextToken();
        if (sym.getType() == TokenType.SEMICN) {
            getNextToken(); // 吃掉 分号;
        }  /* else {

            } */
        return new ExqStmt(exp);
    }

    public Stmt parseAssignOrExqStmt() {
        Stmt stmt;
        boolean isExq = false;
        boolean isAssign = false;
        Token ident = lexer.visitNextToken();
        Token sym = lexer.visitNext2Token();
        if (sym.getType() == TokenType.LPARENT) {
            isExq = true;
        } else {
            if (lexer.meetAssignFirst()) {
                isAssign = true;
            } else {
                isExq = true;
            }
        }
        if (isAssign) {
            stmt = parseAssignStmt();
        } else {
            stmt = parseExqStmt();
        }
        return stmt;
    }

    public Cond parseCond() {
        LorExp lorExp  = parseLorExp();
        Cond cond = new Cond(lorExp);
        printStream("<Cond>\n");
        return cond;
    } // 已完成

    public LorExp parseLorExp() {
        LandExp first = parseLandExp();
        ArrayList<LandExp> landExps = new ArrayList<>();
        landExps.add(first);
        Token sym = lexer.visitNextToken();
        while (sym.getType() == TokenType.OR) {
            printStream("<LOrExp>\n");
            getNextToken(); // 吃掉 || 符号
            landExps.add(parseLandExp());
            sym = lexer.visitNextToken();
        }
        printStream("<LOrExp>\n");
        return new LorExp(landExps);
    } // 已完成

    public LandExp parseLandExp() {
        EqExp first = parseEqExp();
        ArrayList<EqExp> eqExps = new ArrayList<>();
        eqExps.add(first);
        Token sym = lexer.visitNextToken();
        while (sym.getType() == TokenType.AND) {
            printStream("<LAndExp>\n");
            getNextToken(); // 吃掉 && 符号
            eqExps.add(parseEqExp());
            sym = lexer.visitNextToken();
        }
        printStream("<LAndExp>\n");
        return new LandExp(eqExps);
    } // 已完成

    public EqExp parseEqExp() {
        RelExp first = parseRelExp();
        ArrayList<Token> operators = new ArrayList<>();
        ArrayList<RelExp> relExps = new ArrayList<>();

        Token sym = lexer.visitNextToken();
        while (sym.getType() == TokenType.EQL || sym.getType() == TokenType.NEQ) {
            printStream("<EqExp>\n");
            sym = getNextToken(); //吃掉 == 或 !=
            operators.add(sym);
            relExps.add(parseRelExp());
            sym = lexer.visitNextToken();
        }
        printStream("<EqExp>\n");
        return new EqExp(first, operators, relExps);
    } // 已完成

    public RelExp parseRelExp() {
        AddExp first = parseAddExp();
        ArrayList<Token> operators = new ArrayList<>();
        ArrayList<AddExp> addExps = new ArrayList<>();
        Token sym = lexer.visitNextToken();
        while (sym.getType() == TokenType.LSS || sym.getType() == TokenType.LEQ ||
        sym.getType() == TokenType.GRE || sym.getType() == TokenType.GEQ) {
            printStream("<RelExp>\n");
            sym = getNextToken(); // 吃掉 < > <= >=
            operators.add(sym);
            addExps.add(parseAddExp());
            sym = lexer.visitNextToken();
        }
        printStream("<RelExp>\n");
        return new RelExp(first, operators, addExps);
    } // 已完成

    public AddExp parseAddExp() {
        MulExp mulexp = parseMulExp();
        ArrayList<Token> operators = new ArrayList<>();
        ArrayList<MulExp> mulExps = new ArrayList<>();

        Token sym = lexer.visitNextToken();
        while (sym.getType() == TokenType.PLUS || sym.getType() == TokenType.MINU) {
            printStream("<AddExp>\n");
            sym = getNextToken();   //   吃掉+号或者-号
            operators.add(sym);
            MulExp followMul = parseMulExp();
            mulExps.add(followMul);
            sym = lexer.visitNextToken();
        }
        printStream("<AddExp>\n");
        return new AddExp(mulexp, operators, mulExps);
    } // 已完成

    public MulExp parseMulExp() {
        UnaryExp firstUnary = parseUnaryExp();
        ArrayList<Token> operators = new ArrayList<>();
        ArrayList<UnaryExp> unaryExps = new ArrayList<>();

        Token sym = lexer.visitNextToken();
        while (sym.getType() == TokenType.MULT || sym.getType() == TokenType.DIV || sym.getType() == TokenType.MOD
            || sym.getType() == TokenType.BitTand) {
            printStream("<MulExp>\n");
            sym = getNextToken();   //   吃掉* 或/ 或%
            operators.add(sym);
            UnaryExp followUnary = parseUnaryExp();
            unaryExps.add(followUnary);
            sym = lexer.visitNextToken();
        }
        printStream("<MulExp>\n");
        return new MulExp(firstUnary, operators, unaryExps);
    } //已完成

    public UnaryExp parseUnaryExp() {
        UnaryExp unaryExp = null;
        ArrayList<Token> operators = new ArrayList<>();
        int opNum = 0;
        Token sym = lexer.visitNextToken();
        while (sym.getType() == TokenType.PLUS || sym.getType() == TokenType.MINU || sym.getType() == TokenType.NOT) {
            operators.add(sym); // 将 PrimaryExp和函数 前的所有符号存进符号ArrayList
            opNum++; // 符号数+1
            getNextToken(); // 吃掉这个符号
            printStream("<UnaryOp>\n");
            sym = lexer.visitNextToken();
        }

        if (sym.getType() == TokenType.LPARENT || sym.getType() == TokenType.INTCON) {
            PrimaryExp primaryExp = parsePrimaryExp();
            unaryExp = new UnaryExp(operators, primaryExp);
        } else if (sym.getType() == TokenType.IDENFR) {
            Token next = lexer.visitNext2Token();
            if (next != null && next.getType() == TokenType.LPARENT) {
                Token ident = getNextToken(); // 吃掉函数标识符
                getNextToken();  // 吃掉函数左括号
                if (nextExp()) {
                    FuncRParams rParams = parseFuncRParams();
                    Token rRarent = lexer.visitNextToken();
                    if (rRarent.getType() == TokenType.RPARENT) {
                        getNextToken(); //  吃掉)括号
                        unaryExp = new UnaryExp(operators, ident, rParams);
                    } /* else {
                        缺少右括号，抛出异常
                    } */
                } else {  //else 情况对应为函数无参数
                    Token rRarent = lexer.visitNextToken();
                    if (rRarent.getType() == TokenType.RPARENT) {
                        getNextToken(); //  吃掉)括号
                        unaryExp = new UnaryExp(operators, ident);
                    } /* else {
                        缺少右括号，抛出异常
                    } */
                }
            } else { // else 为 primaryExp
                PrimaryExp primaryExp = parsePrimaryExp();
                unaryExp = new UnaryExp(operators, primaryExp);
            }

        }
        printStream("<UnaryExp>\n");
        for (int i = 0; i < opNum; i++) {
            printStream("<UnaryExp>\n");
        }
        return unaryExp;
    } // 已完成

    public PrimaryExp parsePrimaryExp() {
        PrimaryExp primaryExp = null;
        Token sym = lexer.visitNextToken();
        if (sym.getType() == TokenType.LPARENT) {
            getNextToken(); // 吃掉(左括号
            Exp exp = parseExp(false);
            Token next = lexer.visitNextToken();
            if (next.getType() == TokenType.RPARENT) {
                getNextToken(); // 吃掉)右括号
                primaryExp = new PrimaryExp(exp);
            } /* else {
                处理缺少右括号的异常
            } */
        } else if (sym.getType() == TokenType.IDENFR) {
            Lval lval = parseLval();
            primaryExp = new PrimaryExp(lval);
        } else {
            Number number = parseNumber();
            primaryExp = new PrimaryExp(number);
        }
        printStream("<PrimaryExp>\n");
        return primaryExp;
    } // 已完成

    public Lval parseLval() {
        ArrayList<Exp> indexes = new ArrayList<>();
        Token ident = getNextToken();
        Token sym = lexer.visitNextToken();
        while (sym.getType() == TokenType.LBRACK) {
            getNextToken(); // 吃掉[
            indexes.add(parseExp(false));
            sym = lexer.visitNextToken();
            if (sym.getType() == TokenType.RBRACK) {
                getNextToken(); // 吃掉]
                sym = lexer.visitNextToken();
            } /* else {
                异常处理缺少]
            } */
        }
        printStream("<LVal>\n");
        return new Lval(ident, indexes);
    } // 已完成

    public Number parseNumber() {
        Token intConst = getNextToken();
        printStream("<Number>\n");
        return new Number(intConst);
    }

    public FuncRParams parseFuncRParams() {
        ArrayList<Exp> exps = new ArrayList<>();
        Exp first = parseExp(false);
        exps.add(first);
        Token sym = lexer.visitNextToken();
        while (sym.getType() == TokenType.COMMA) {
            getNextToken(); // 吃掉逗号//
            exps.add(parseExp(false));
            sym = lexer.visitNextToken();
        }
        printStream("<FuncRParams>\n");
        return new FuncRParams(exps);
    } // 已完成

    public MainFuncDef parseMain() {
        getNextToken(); // get int
        getNextToken(); // get main
        getNextToken(); // get 左括号(
        getNextToken(); // get 右括号)
        Block block = parseBlock();
        printStream("<MainFuncDef>\n");
        return new MainFuncDef(block);
    }

    ///// 用于辅助判断接下去是否为Exp
    public boolean nextExp() {
        Token next = lexer.visitNextToken();
        if (next.getType() == TokenType.LPARENT || next.getType() == TokenType.INTCON ||
                next.getType() == TokenType.IDENFR || next.getType() == TokenType.PLUS ||
                next.getType() == TokenType.MINU || next.getType() == TokenType.NOT) {
            return true;
        } else {
            return false;
        }
    }
}


