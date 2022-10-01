package lex;

public class Token {
    private TokenType type;
    private int lineNum;
    private String content;

    public Token(TokenType type, int lineNum, String content) {
        this.type = type;
        this.lineNum = lineNum;
        this.content = content;
    }

    public TokenType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }
}
