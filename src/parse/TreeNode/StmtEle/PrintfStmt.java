package parse.TreeNode.StmtEle;

import lex.Token;
import parse.TreeNode.Exp;
import parse.TreeNode.Stmt;

import java.util.ArrayList;

public class PrintfStmt extends Stmt {
    private Token str; // Token.content是输出字符串的内容
    private ArrayList<Exp> exps;

    public PrintfStmt(Token str, ArrayList<Exp> exps) {
        this.str = str;
        this.exps = exps;
    }

    public String getContent() {
        return str.getContent();
    }

    public ArrayList<Exp> getExps() {
        return exps;
    }

}
