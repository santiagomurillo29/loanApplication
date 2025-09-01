package co.com.bancolombia.model.loanapplication.model;

import java.math.BigDecimal;

public class LoanApplicationModel {
    private Long idLoanApplication;
    private BigDecimal amount;
    private Integer term;
    private String email;
    private StateModel state;
    private LoanTypeModel loanType;

    public LoanApplicationModel(Long idLoanApplication, BigDecimal amount, Integer term, String email, StateModel state, LoanTypeModel loanType) {
        this.idLoanApplication = idLoanApplication;
        this.amount = amount;
        this.term = term;
        this.email = email;
        this.state = state;
        this.loanType = loanType;
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
