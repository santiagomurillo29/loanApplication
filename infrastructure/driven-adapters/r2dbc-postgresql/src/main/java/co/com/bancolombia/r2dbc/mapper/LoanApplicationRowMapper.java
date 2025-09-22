package co.com.bancolombia.r2dbc.mapper;

import co.com.bancolombia.model.loanapplication.model.LoanApplicationModel;
import co.com.bancolombia.model.loanapplication.model.LoanTypeModel;
import co.com.bancolombia.model.loanapplication.model.StateModel;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class LoanApplicationRowMapper {
    public LoanApplicationModel map(Row row, RowMetadata ignored) {
        StateModel state = new StateModel(
                row.get("state_id", Long.class),
                row.get("state_name", String.class)
        );

        LoanTypeModel loanType = new LoanTypeModel(
                row.get("loan_type_id", Long.class),
                row.get("loan_type_name", String.class),
                row.get("interest_rate", BigDecimal.class)
        );

        return new LoanApplicationModel(
                row.get("id", Long.class),
                row.get("amount", BigDecimal.class),
                row.get("term", Integer.class),
                row.get("email", String.class),
                state,
                loanType
        );
    }
}
