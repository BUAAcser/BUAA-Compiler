package error;

public class Error implements Comparable<Error> {
    private int lineNum;
    private String type;

    public Error(int lineNum, String type) {
        this.lineNum = lineNum;
        this.type = type;
    }

    public int getLineNum() {
        return this.lineNum;
    }

    @Override
    public int compareTo(Error o) {
        return Integer.compare(this.getLineNum(), o.getLineNum());
    }
}
