package parse.TreeNode;

import lex.Token;

import java.util.ArrayList;

public class FuncDef {
    private FuncType type;
    private Token ident;
    private FuncFParams funcParams;
    private Block block;

    public FuncDef(FuncType type, Token ident, FuncFParams funcParams, Block block) {
        this.type = type;
        this.ident = ident;
        this.funcParams = funcParams;
        this.block = block;
    }
}
