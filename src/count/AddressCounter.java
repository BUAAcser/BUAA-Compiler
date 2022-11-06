package count;

import symbol.SymbolType;
import symbol.VarSymbol;

import java.util.ArrayList;

public class AddressCounter {
    private int offset = 0;

    public void clear() {
        this.offset = 0;
    }

    public int allocate(VarSymbol symbol) {
        int address = this.offset;
        if (symbol.getType() == SymbolType.Pointer || symbol.getType() == SymbolType.Var) {
            offset = offset + 4;
        } else if (symbol.getType() == SymbolType.Array) {
            ArrayList<Integer> dims = symbol.getDims();
            if (dims.size() == 1) {
                offset = offset + dims.get(0) * 4;
            } else if (dims.size() == 2) {
                offset = offset + dims.get(0) * dims.get(1) * 4;
            }
        }  // allocate不用关注是否是const, 在visitor中已经做出了判断
        return address;
    }

    public int allocateTemp() {
        int address = this.offset;
        offset += 4;
        return address;
    }

    public int getNow() {
        return offset;
    }
}
