package Ir;

public class RegMap {
    private int regNum;
    private String varName;

    public RegMap(int regNum, String varName) {
        this.regNum = regNum;
        this.varName = varName;
    }

    public int getRegNum() {
        return  regNum;
    }

    public String getVarName() {
        return varName;
    }

}

