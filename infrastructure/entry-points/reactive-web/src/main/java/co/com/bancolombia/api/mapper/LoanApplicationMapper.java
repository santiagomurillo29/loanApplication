package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.request.LoanApplicationRequestDto;
import co.com.bancolombia.api.dto.response.LoanApplicationResponseDto;
import co.com.bancolombia.model.loanapplication.model.LoanApplicationModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanApplicationMapper {

    @Mapping(target = "idLoanApplication", ignore = true)
    @Mapping(target = "state.idState", ignore = true)
    @Mapping(target = "loanType.idLoanType", source = "idLoanType")
    @Mapping(target = "amount", source = "amount")
    LoanApplicationModel toModelLoanApplication(LoanApplicationRequestDto loanApplicationRequestDto);

    @Mapping(target = "idState", source = "state.idState")
    @Mapping(target = "idLoanType", source = "loanType.idLoanType")
    @Mapping(target = "amount", source = "amount")
    LoanApplicationResponseDto toDtoLoanApplication(LoanApplicationModel loanApplicationModel);
}
