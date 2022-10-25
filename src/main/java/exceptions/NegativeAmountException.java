package exceptions;

public class NegativeAmountException extends Exception{
    private static final String ERROR_MESSAGE = "The amount value cannot be negative.";

    public NegativeAmountException(){
        super(ERROR_MESSAGE);
    }
}
