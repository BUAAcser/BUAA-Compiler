package count;

public class TempCounter {
    private int number;

    public TempCounter() {
        number = 0;
    }

    public String allocateTemp() {
        number++;
        String tempName = "Temp_" + String.valueOf(number);
        return tempName;
    }

}
