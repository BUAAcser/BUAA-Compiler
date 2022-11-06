package Ir;

import java.util.ArrayList;

public class IrCode {
    private ArrayList<Ir> irs;

    public IrCode() {
        this.irs = new ArrayList<>();
    }

    public void addIr(Ir ir) {
        this.irs.add(ir);
    }
}
