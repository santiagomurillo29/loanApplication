package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.request.LoanApplicationRequestDto;
import co.com.bancolombia.api.dto.response.LoanApplicationResponseDto;
import co.com.bancolombia.api.dto.response.page.LoanApplicationPageResponseDto;
import co.com.bancolombia.api.dto.response.page.LoanApplicationWithPaginationResponseDto;
import co.com.bancolombia.model.loanapplication.model.LoanApplicationModel;
import co.com.bancolombia.model.loanapplication.model.LoanApplicationPendingModel;
import co.com.bancolombia.model.loanapplication.model.page.PageLoanApplicationModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface LoanApplicationMapper {

    @Mapping(target = "idLoanApplication", ignore = true)
    @Mapping(target = "state.idState", ignore = true)
    @Mapping(target = "loanType.idLoanType", source = "idLoanType")
    LoanApplicationModel toModelLoanApplication(LoanApplicationRequestDto loanApplicationRequestDto);

    @Mapping(target = "idState", source = "state.idState")
    @Mapping(target = "nameState", source = "state.name")
    @Mapping(target = "idLoanType", source = "loanType.idLoanType")
    @Mapping(target = "nameLoanType", source = "loanType.name")
    LoanApplicationResponseDto toDtoLoanApplication(LoanApplicationModel loanApplicationModel);

    @Mapping(target = "idLoanApplication", source = "loanApplication.idLoanApplication")
    @Mapping(target = "name", source = "userName")
    @Mapping(target = "email", source = "loanApplication.email")
    @Mapping(target = "baseSalaryUser", source = "baseSalaryUser")
    @Mapping(target = "amount", source = "loanApplication.amount")
    @Mapping(target = "term", source = "loanApplication.term")
    @Mapping(target = "idLoanType", source = "loanApplication.loanType.idLoanType")
    @Mapping(target = "nameLoanType", source = "loanApplication.loanType.name")
    @Mapping(target = "interestRate", source = "loanApplication.loanType.interestRate")
    @Mapping(target = "idState", source = "loanApplication.state.idState")
    @Mapping(target = "nameState", source = "loanApplication.state.name")
    @Mapping(target = "amountMonthly", source = "monthlyAmount")
    LoanApplicationWithPaginationResponseDto toItemDto(LoanApplicationPendingModel pending);

    default LoanApplicationPageResponseDto toPageDto(PageLoanApplicationModel<LoanApplicationPendingModel> page) {
        List<LoanApplicationWithPaginationResponseDto> content =
                page.getContent().stream().map(this::toItemDto).collect(Collectors.toList());
        return new LoanApplicationPageResponseDto(content, page.getCurrentPage(),
                page.getPageSize(), page.getTotalPages(), page.getTotalElements());
    }
}
