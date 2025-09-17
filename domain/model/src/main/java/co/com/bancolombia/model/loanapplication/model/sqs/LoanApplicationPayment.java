package co.com.bancolombia.model.loanapplication.model.sqs;

import java.math.BigDecimal;

public class LoanApplicationPayment {
    private Long id;
    private String state;
    private String email;
    private BigDecimal amount;
    private BigDecimal annualRate;
    private Integer term;
    private String type;

    public LoanApplicationPayment(Long id, String state, String email, BigDecimal amount, BigDecimal annualRate, Integer term, String type) {
        this.id = id;
        this.state = state;
        this.email = email;
        this.amount = amount;
        this.annualRate = annualRate;
        this.term = term;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAnnualRate() {
        return annualRate;
    }

    public void setAnnualRate(BigDecimal annualRate) {
        this.annualRate = annualRate;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
