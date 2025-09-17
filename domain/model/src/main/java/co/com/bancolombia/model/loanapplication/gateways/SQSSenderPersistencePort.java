package co.com.bancolombia.model.loanapplication.gateways;

import co.com.bancolombia.model.loanapplication.model.sqs.LoanApplicationState;
import co.com.bancolombia.model.loanapplication.model.sqs.LoanApplicationPayment;
import reactor.core.publisher.Mono;

public interface SQSSenderPersistencePort {
    Mono<String> sendState(LoanApplicationState state);
    Mono<String> sendPayment(LoanApplicationPayment payment);
}
