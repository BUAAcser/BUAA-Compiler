package parse.TreeNode;

import lex.Token;

public class Number {
    private Token intConst;

    public Number(Token intConst) {
        this.intConst = intConst;
    }

    public String getNumberStr() {
        return intConst.getContent();
    }

    public int toInt() {
        return Integer.parseInt(intConst.getContent());
    }
}
