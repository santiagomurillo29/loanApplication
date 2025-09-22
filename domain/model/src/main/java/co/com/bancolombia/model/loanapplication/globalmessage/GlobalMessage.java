package co.com.bancolombia.model.loanapplication.globalmessage;

public enum GlobalMessage {

    NOT_FOUND_EMAIL(GlobalMessage.STATUS_CODE_404, "Email not found"),
    NOT_FOUND_LOAN_APPLICATION(GlobalMessage.STATUS_CODE_404, "Loan application not found"),
    NOT_FOUND_LOAN_TYPE(GlobalMessage.STATUS_CODE_404, "Loan type not found"),
    NOT_FOUND_STATE(GlobalMessage.STATUS_CODE_404, "State not found"),
    MICROSERVICE_DOWN(GlobalMessage.STATUS_CODE_500, "Microservice is down"),
    DATABASE_ERROR(GlobalMessage.STATUS_CODE_500, "Database error"),
    EMAIL_NOT_MATCH(GlobalMessage.STATUS_CODE_500, "The email is not the same"),
    INTERNAL_ERROR(GlobalMessage.STATUS_CODE_500, "Internal server error"),
    AUTO_VALIDATION_NOT_ALLOWED(GlobalMessage.STATUS_CODE_400, "Auto validation es false");

    public static final String STATUS_CODE_400 = "400";
    public static final String STATUS_CODE_404 = "404";
    public static final String STATUS_CODE_500 = "500";

    private final String statusCode;
    private final String message;

    GlobalMessage(String statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getStatusCode() {
        return statusCode;
    }
}
