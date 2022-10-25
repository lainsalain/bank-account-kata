package exceptions;


public class NotEnoughMoneyException extends Exception {
    private static final String ERROR_MESSAGE = "The withdrawal cannot be greater than the balance.";

    public NotEnoughMoneyException(){
        super(ERROR_MESSAGE);
    }
}
