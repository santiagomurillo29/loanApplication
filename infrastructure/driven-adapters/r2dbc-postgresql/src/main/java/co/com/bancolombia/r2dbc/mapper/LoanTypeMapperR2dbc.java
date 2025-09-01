package co.com.bancolombia.r2dbc.mapper;

import co.com.bancolombia.model.loanapplication.model.LoanTypeModel;
import co.com.bancolombia.r2dbc.entity.LoanTypeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoanTypeMapperR2dbc {
    LoanTypeEntity toEntityLoanType(LoanTypeModel loanTypeModel);
    LoanTypeModel toModelLoanType(LoanTypeEntity loanTypeEntity);
}
