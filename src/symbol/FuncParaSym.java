package symbol;

public class FuncParaSym {
    private String name;
    private int dimension;
    private int dim2Num;

    public FuncParaSym(String name, int dimension) {
        this.name = name;
        this.dimension = dimension;
    }

    public FuncParaSym(String name, int dimension, int num) {
        this.name = name;
        this.dimension = dimension;
        this.dim2Num = num;
    }

    public int getDimension() {
        return dimension;
    }

    public int getDim2Num() {
        return dim2Num;
    }
}
