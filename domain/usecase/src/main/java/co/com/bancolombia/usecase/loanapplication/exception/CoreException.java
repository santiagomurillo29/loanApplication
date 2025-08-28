package co.com.bancolombia.usecase.loanapplication.exception;

import co.com.bancolombia.model.loanapplication.globalmessage.GlobalMessage;
import lombok.Getter;

@Getter
public class CoreException extends RuntimeException {
    private final GlobalMessage error;

    protected CoreException(GlobalMessage error) {
        super(error.getMessage());
        this.error = error;
    }
}
