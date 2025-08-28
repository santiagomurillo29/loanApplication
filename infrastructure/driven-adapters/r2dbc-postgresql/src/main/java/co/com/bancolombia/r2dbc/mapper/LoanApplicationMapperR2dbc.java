package co.com.bancolombia.r2dbc.mapper;

import co.com.bancolombia.model.loanapplication.model.LoanApplicationModel;
import co.com.bancolombia.model.loanapplication.model.LoanTypeModel;
import co.com.bancolombia.model.loanapplication.model.StateModel;
import co.com.bancolombia.r2dbc.entity.LoanApplicationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanApplicationMapperR2dbc {

    @Mapping(target = "idState", source = "state.idState")
    @Mapping(target = "idLoanType", source = "loanType.idLoanType")
    LoanApplicationEntity toEntityLoanApplication(LoanApplicationModel loanApplicationModel);

    @Mapping(target = "state", source = "idState")
    @Mapping(target = "loanType", source = "idLoanType")
    LoanApplicationModel toModelLoanApplication(LoanApplicationEntity loanApplicationEntity);

    default StateModel mapToState(Long id) { return id == null ? null : new StateModel(id); }
    default LoanTypeModel mapToLoanType(Long id) { return id == null ? null : new LoanTypeModel(id); }
    default Long mapStateToId(StateModel state) { return state == null ? null : state.getIdState(); }
    default Long mapLoanTypeToId(LoanTypeModel loanType) { return loanType == null ? null : loanType.getIdLoanType(); }
}
