package utils;

public class FindPercentOperation implements AdditionalMathOperation {
    @Override
    public float calculate(float number) {
        return number/100;
    }
}
