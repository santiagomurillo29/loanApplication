package co.com.bancolombia.sqs.sender.mapper;

import co.com.bancolombia.model.loanapplication.model.sqs.LoanApplicationPayment;
import co.com.bancolombia.model.loanapplication.model.sqs.LoanApplicationState;
import co.com.bancolombia.sqs.sender.dto.LoanApplicationPaymentDto;
import co.com.bancolombia.sqs.sender.dto.LoanApplicationStateDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoanApplicationEventMapper {
    LoanApplicationPaymentDto toDtoPayment(LoanApplicationPayment loanApplicationPayment);
    LoanApplicationStateDto toDtoState(LoanApplicationState loanApplicationState);

}
