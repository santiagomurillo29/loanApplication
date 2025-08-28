package co.com.bancolombia.r2dbc.exception;

import co.com.bancolombia.model.loanapplication.globalmessage.GlobalMessage;
import co.com.bancolombia.usecase.loanapplication.exception.CoreException;

public class DataBaseException extends CoreException {
    public DataBaseException(GlobalMessage error){
        super(error);
    }
}



