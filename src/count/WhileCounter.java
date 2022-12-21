package count;

public class WhileCounter {

    private int number;

    public WhileCounter() {
        number = 0;
    }

    public int allocate() {
        number++;
        return number;
    }

}
