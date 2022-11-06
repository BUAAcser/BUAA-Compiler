package count;

public class LabelCounter {
    private int number;

    public LabelCounter() {
        number = 0;
    }

    public int allocate() {
        number++;
        return number;
    }
}
