package dgu.aecofarm.entity;

public enum ResponseType {
    SUCCESS(200, "SUCCESS"),
    FAILURE(400, "FAILURE"),
    UNAUTHORIZED(401, "Unauthorized access."),
    NOT_FOUND(404, "Resource not found.");

    private final int code;
    private final String message;

    ResponseType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
