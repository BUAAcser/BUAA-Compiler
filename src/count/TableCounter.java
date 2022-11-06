package count;

public class TableCounter {
    private int number;

    public TableCounter() {
        number = 0;
    }

    public int allocate() {
        number++;
        return number;
    }
}
