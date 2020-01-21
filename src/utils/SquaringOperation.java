package utils;

public class SquaringOperation implements AdditionalMathOperation {

    @Override
    public float calculate(float number) {
        return (float) Math.pow(number, 2);
    }
}
