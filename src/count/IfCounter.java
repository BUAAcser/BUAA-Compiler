package count;

public class IfCounter {
    private int number;

    public IfCounter() {
        number = 0;
    }

    public int allocate() {
        number++;
        return number;
    }
}
