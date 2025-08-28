package co.com.bancolombia.model.loanapplication.model;

import java.math.BigDecimal;

public class LoanApplicationModel {
    private Long idLoanApplication;
    private BigDecimal amount;
    private Integer term;
    private String email;
    private StateModel state;
    private LoanTypeModel loanType;

    public LoanApplicationModel(StateModel state, String email, LoanTypeModel loanType, Integer term, BigDecimal amount, Long idLoanApplication) {
        this.state = state;
        this.email = email;
        this.loanType = loanType;
        this.term = term;
        this.idLoanApplication = idLoanApplication;
    }

    public Long getIdLoanApplication() {
        return idLoanApplication;
    }

    public void setIdLoanApplication(Long idLoanApplication) {
        this.idLoanApplication = idLoanApplication;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public StateModel getState() {
        return state;
    }

    public void setState(StateModel state) {
        this.state = state;
    }

    public LoanTypeModel getLoanType() {
        return loanType;
    }

    public void setLoanType(LoanTypeModel loanType) {
        this.loanType = loanType;
    }
}
