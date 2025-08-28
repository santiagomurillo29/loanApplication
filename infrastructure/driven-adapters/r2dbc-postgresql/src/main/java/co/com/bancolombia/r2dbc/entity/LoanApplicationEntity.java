package co.com.bancolombia.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("loan_application")
public class LoanApplicationEntity {

    @Id
    @Column("id_loan_application")
    private Long idLoanApplication;

    @Column("amount")
    private BigDecimal amount;

    @Column("term")
    private Integer term;

    @Column("email")
    private String email;

    @Column("id_state")
    private Long idState;

    @Column("id_loan_type")
    private Long idLoanType;
}
