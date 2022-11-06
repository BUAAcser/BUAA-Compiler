package Ir;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

public class BasicBlock {
    private int num;
    private ArrayList<Ir> irs;

    public BasicBlock(int num) {
        this.num = num;
        this.irs = new ArrayList<>();
    }

    public void insertIr(Ir ir) {
        irs.add(ir);
    }

    public void printIrs(BufferedWriter bw) {
        for (Ir ir : irs) {
            String put = ir.toString() + "\n";
            try {
                bw.write(put);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public ArrayList<Ir> getIrs() {
        return irs;
    }
}
