package co.com.bancolombia.consumer.exception;

import co.com.bancolombia.model.loanapplication.globalmessage.GlobalMessage;
import co.com.bancolombia.usecase.loanapplication.exception.CoreException;

public class WebClientException extends CoreException {
  public WebClientException(GlobalMessage error) {
    super(error);
  }
}
