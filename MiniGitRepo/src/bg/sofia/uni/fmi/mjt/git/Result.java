package bg.sofia.uni.fmi.mjt.git;

public class Result {

    private boolean successful;
    private String message;

    public Result(String message, boolean successful) {
        this.message = message;
        this.successful = successful;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccessful() {
        return successful;
    }
}
