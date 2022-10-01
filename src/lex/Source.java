package lex;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Source {
    private int line;
    private int index;
    private final int lineNum;
    private int curLength;
    private ArrayList<String> sources;
    private HashMap<Integer,Integer> realNumber;

    public Source() {
        sources = new ArrayList<>();
        realNumber = new HashMap<>();
        this.line = 0;
        this.index = 0;
        readSource();
        this.lineNum = sources.size();
        this.curLength = sources.get(0).length();
    }

    private void readSource() {
        try {
            FileReader fileReader = new FileReader("testfile.txt");
            BufferedReader br = new BufferedReader(fileReader);
            String line;
            int realNum = 0;
            int num = 0;
            while ((line = br.readLine()) != null) {
                realNum++;
                String str = line.trim();
                if (str.length() > 0) {
                    sources.add(str);
                    realNumber.put(num, realNum);
                    num++;
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Resource file can not be found.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void next(boolean adjust) {
        index++;
        if (adjust) {
            if (index >= curLength) {
                line++;
                index = 0;
                curLength = 0;
                if (line < lineNum) {
                    curLength = sources.get(line).length();
                }
            }

        }
    }

    public void skipBlank() {
        while (inputNotEnd() && Character.isWhitespace(visitCurChar())) {
            next(false);
        }    // 怀疑这里的inputNotEnd可以去掉
    }

    public boolean isNotLineEnd() {
        return (index < curLength);
    }

    public boolean inputNotEnd() {
        return  ((line < lineNum - 1) || (line == lineNum - 1 && index < curLength));
    }

    public char visitCurChar() {
        return sources.get(line).charAt(index);
    }

    public Pair<Integer,Character> getCurChar() {
        char s;
        s = sources.get(line).charAt(index);
        int realNum = realNumber.get(line);
        return new Pair<>(realNum,s);
    }

    public boolean meetCharacter(char s) {
        return isNotLineEnd() && visitCurChar() == s;
    }

    public void skipLine() {
        line++;
        index = 0;
        if (line < lineNum) {
            curLength = sources.get(line).length();
        }
    }

    public void dealMultiAnnotation() {
        boolean annotationEnd = false;
        boolean meetStar = false;
        next(true);
        while (inputNotEnd() && !annotationEnd) {
            if (visitCurChar() == '/') {
                if (meetStar) {
                    annotationEnd = true;
                }
            } else {
                meetStar = (visitCurChar() == '*');
                if (index == curLength - 1 && meetStar) {
                    meetStar = false;
                }
            }
            next(true);
        }
        index--;
    }

    public String getNumber() {
        int start = index;
        while (isNotLineEnd() && Character.isDigit(visitCurChar())) {
            index++;
        }
        String number = sources.get(line).substring(start, index);
        index--;
        return  number;
    }

    public String getString() {
        int start = index;
        while (isNotLineEnd() && visitCurChar() != '"') {
            index++;
        }
        String str = sources.get(line).substring(start, index);
        return str;
    }

    public String getIdentify() {
        int start = index;
        while (isNotLineEnd() && judgeIdentContinue(visitCurChar())) {
            index++;
        }
        String str = sources.get(line).substring(start, index);
        index--;
        return str;
    }

    public boolean judgeIdentContinue(char s) {
        return Character.isDigit(s) || Character.isLetter(s) || (s == '_');
    }

    public void back() {
        index--;
    }

}
