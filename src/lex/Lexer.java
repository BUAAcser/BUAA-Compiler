package lex;

import java.util.ArrayList;
import java.util.HashMap;

public class Lexer {
    private Source source;
    private HashMap<String, TokenType> types;

    public Lexer(Source source) {
        this.source = source;
        initTypes();
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
}
