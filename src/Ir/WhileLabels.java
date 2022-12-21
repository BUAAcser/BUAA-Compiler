package Ir;

public class WhileLabels {
    private String startLabel;
    private String endLabel;

    public WhileLabels(String startLabel, String endLabel) {
        this.startLabel = startLabel;
        this.endLabel = endLabel;
    }

    public String getStartLabel() {
        return startLabel;
    }

    public String getEndLabel() {
        return endLabel;
    }
}
