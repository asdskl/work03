package management;

public class OrderExistException extends RuntimeException {
    public OrderExistException (String message) {
        super(message);
    }
}
