package naatsms.orchestra.exception;

public class PasswordMismatchException extends RuntimeException {

    public PasswordMismatchException() {
        super("Password confirmation failed");
    }
}
