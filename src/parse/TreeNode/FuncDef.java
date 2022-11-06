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

    public String getName() {
        return ident.getContent();
    } // 得到函数名称

    public FuncType getType() {
        return type;
    }

    public ArrayList<FuncFParam> getFuncParams() {
        if (funcParams != null) {
            return funcParams.getFParams();
        } else {
            return  new ArrayList<FuncFParam>();
        }

    }

    public Block getBlock() {
        return block;
    }
}
