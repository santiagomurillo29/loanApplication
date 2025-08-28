package co.com.bancolombia.usecase.loanapplication.exception;

import co.com.bancolombia.model.loanapplication.globalmessage.GlobalMessage;

public class BusinessException extends CoreException{
    public BusinessException(GlobalMessage error) {
        super(error);
    }
}
