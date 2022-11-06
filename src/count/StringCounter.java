package count;

public class StringCounter {
    private int num;

    public StringCounter() {
        this.num = 0;
    }

    public int allocate() {
        num++;
        return num;
    }
}
