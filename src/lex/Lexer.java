package lex;

import java.util.ArrayList;
import java.util.HashMap;

public class Lexer {
    private Source source;
    private HashMap<String, TokenType> types;
    private ArrayList<Token> tokens;
    private int index;
    private int length;

    public Lexer(Source source) {
        this.source = source;
        initTypes();
        this.tokens = lexical();
        this.index = 0;
        this.length = tokens.size();
    }

    private void initTypes() {
        types = new HashMap<>();
        types.put("main", TokenType.MAINTK);
        types.put("const", TokenType.CONSTTK);
        types.put("int",TokenType.INTTK);
        types.put("break", TokenType.BREAKTK);
        types.put("continue", TokenType.CONTINUETK);
        types.put("if", TokenType.IFTK);
        types.put("else", TokenType.ELSETK);
        types.put("while", TokenType.WHILETK);
        types.put("getint", TokenType.GETINTTK);
        types.put("printf", TokenType.PRINTFTK);
        types.put("return", TokenType.RETURNTK);
        types.put("void", TokenType.VOIDTK);
        types.put("bitand", TokenType.BitTand);

    }

    public ArrayList<Token> lexical() {
        ArrayList<Token> tokens = new ArrayList<>();
        while (source.inputNotEnd()) {
            source.skipBlank();
            Pair<Integer,Character> pair = source.getCurChar();
            char s = pair.getRight();
            int lineNo = pair.getLeft();
            switch (s) {
                case '/':
                    source.next(false);
                    if (source.meetCharacter('/')) {
                        source.skipLine();
                        source.back();
                    } else if (source.meetCharacter('*')) {
                        source.dealMultiAnnotation();
                    } else {
                        source.back();
                        tokens.add(new Token(TokenType.DIV, lineNo, "/"));
                    }
                    break;
                case ',':
                    tokens.add(new Token(TokenType.COMMA, lineNo, ","));
                    break;
                case ';':
                    tokens.add(new Token(TokenType.SEMICN, lineNo, ";"));
                    break;
                case '(':
                    tokens.add(new Token(TokenType.LPARENT, lineNo, "("));
                    break;
                case ')':
                    tokens.add(new Token(TokenType.RPARENT, lineNo, ")"));
                    break;
                case '[':
                    tokens.add(new Token(TokenType.LBRACK, lineNo, "["));
                    break;
                case ']':
                    tokens.add(new Token(TokenType.RBRACK, lineNo, "]"));
                    break;
                case '{':
                    tokens.add(new Token(TokenType.LBRACE, lineNo, "{"));
                    break;
                case '}':
                    tokens.add(new Token(TokenType.RBRACE, lineNo, "}"));
                    break;
                case '+':
                    tokens.add(new Token(TokenType.PLUS, lineNo, "+"));
                    break;
                case '-':
                    tokens.add(new Token(TokenType.MINU, lineNo, "-"));
                    break;
                case '*':
                    tokens.add((new Token(TokenType.MULT, lineNo, "*")));
                    break;
                case '%':
                    tokens.add((new Token(TokenType.MOD, lineNo, "%")));
                    break;
                case '!':
                    source.next(false);
                    if (source.meetCharacter('=')) {
                        tokens.add(new Token(TokenType.NEQ, lineNo, "!="));
                    } else {
                        source.back();
                        tokens.add(new Token(TokenType.NOT, lineNo, "!"));
                    }
                    break;
                case '=':
                    source.next(false);
                    if (source.meetCharacter('=')) {
                        tokens.add(new Token(TokenType.EQL, lineNo, "=="));
                    } else {
                        source.back();
                        tokens.add(new Token(TokenType.ASSIGN, lineNo, "="));
                    }
                    break;
                case '<':
                    source.next(false);
                    if (source.meetCharacter('=')) {
                        tokens.add(new Token(TokenType.LEQ, lineNo, "<="));
                    } else {
                        source.back();
                        tokens.add(new Token(TokenType.LSS, lineNo, "<"));
                    }
                    break;
                case '>':
                    source.next(false);
                    if (source.meetCharacter('=')) {
                        tokens.add(new Token(TokenType.GEQ, lineNo, ">="));
                    } else {
                        source.back();
                        tokens.add(new Token(TokenType.GRE, lineNo, ">"));
                    }
                    break;
                case '&':
                    source.next(false);
                    if (source.meetCharacter('&')) {
                        tokens.add(new Token(TokenType.AND, lineNo, "&&"));
                    } else {
                        source.back();
                    }
                    break;
                case '|':
                    source.next(false);
                    if (source.meetCharacter('|')) {
                        tokens.add(new Token(TokenType.OR, lineNo, "||"));
                    } else {
                        source.back();
                    }
                    break;
                case '"':
                    source.next(false);
                    String str = source.getString();
                    tokens.add(new Token(TokenType.STRCON, lineNo, "\"" + str + "\"" ));
                    break;
                default:
                    if (Character.isDigit(s)) {
                        String number = source.getNumber();
                        tokens.add(new Token(TokenType.INTCON, lineNo, number));
                    } else if (judgeIdentStart(s)) {
                        String ident = source.getIdentify();
                        tokens.add(new Token(types.getOrDefault(ident, TokenType.IDENFR), lineNo, ident));
                    }
            }
            source.next(true);
        }
        return tokens;
    }

    public boolean judgeIdentStart(char s) {
        return Character.isLetter(s) || s == '_';
    }

    public boolean hasNextToken() {
        return (index < length);
    }

    public Token getNextToken() {
        Token token = tokens.get(index);
        index++;
        return token;
    }

    public Token visitNextToken() {
        return tokens.get(index);
    }

    public Token visitNext2Token() {
        Token token = null;
        if (index + 1 < length) {
            token = tokens.get(index + 1);
        }
        return token;
    }

    public Token visitNext3Token() {
        Token token = null;
        if (index + 2 < length) {
            token = tokens.get(index + 2);
        }
        return token;
    }

    public int getNowIndex() {
        return index;
    }

    public void backIndex(int index) {
        this.index = index;
    }

    public boolean judgeHasSemi() {
        boolean res = false;
        for (int i = index; i < length; i++) {
            if (tokens.get(i).getType() == TokenType.SEMICN) {
                res = true;
                break;
            }
        }
        return res;
    }

    public boolean meetAssignFirst() {
        boolean res = false;
        boolean meetSemi = false;
        for (int i = index; i < length && !meetSemi; i++) {
            if (tokens.get(i).getType() == TokenType.ASSIGN) {
                res = true;
                break;
            } else if (tokens.get(i).getType() == TokenType.SEMICN) {
                meetSemi = true;
            }
        }
        return res;
    }
}
