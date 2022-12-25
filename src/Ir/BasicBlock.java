package Ir;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

public class BasicBlock {
    private String label;
    private ArrayList<Ir> irs;

    public BasicBlock(String label) {
        this.label = label;
        this.irs = new ArrayList<>();
    }

    public void insertIr(Ir ir) {
        irs.add(ir);
    }

    public void printIrs(BufferedWriter bw) {
        try {
            bw.write(label + ":\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (Ir ir : irs) {
            if (!ir.toString().isEmpty()) {
                String put = ir.toString() + "\n";
                try {
                    bw.write(put);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public ArrayList<Ir> getIrs() {
        return irs;
    }

    public boolean judgeIsEmpty() {
        return irs.isEmpty();
    }

    public String getLabelName() {
        return label;
    }
}
