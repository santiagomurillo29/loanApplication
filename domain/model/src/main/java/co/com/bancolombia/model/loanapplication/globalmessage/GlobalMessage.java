package co.com.bancolombia.model.loanapplication.globalmessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GlobalMessage {

    NOT_FOUND_EMAIL(GlobalMessage.STATUS_CODE_404, "Email not found"),
    NOT_FOUND_LOAN_TYPE(GlobalMessage.STATUS_CODE_404, "Loan type not found"),
    NOT_FOUND_STATE(GlobalMessage.STATUS_CODE_404, "State not found"),
    MICROSERVICE_DOWN(GlobalMessage.STATUS_CODE_500, "Microservice is down"),
    DATABASE_ERROR(GlobalMessage.STATUS_CODE_500, "Database is down"),
    INTERNAL_ERROR(GlobalMessage.STATUS_CODE_500, "Internal server error");

    public static final String STATUS_CODE_400 = "400";
    public static final String STATUS_CODE_404 = "404";
    public static final String STATUS_CODE_500 = "500";

    private final String statusCode;
    private final String message;
}
