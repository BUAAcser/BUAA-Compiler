package symbol;

import java.util.ArrayList;

public class Symbol {
    private String name;
    private SymbolType type;
    private String irName = null;

    public Symbol(String name, SymbolType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getIrName() {
        return irName;
    }

    public void setIrName(String irName) {
        this.irName = irName;
    }

    public SymbolType getType() {
        return type;
    }
}
