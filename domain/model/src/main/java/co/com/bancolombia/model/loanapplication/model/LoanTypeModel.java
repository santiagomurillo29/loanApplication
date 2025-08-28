package co.com.bancolombia.model.loanapplication.model;

import java.math.BigDecimal;

public class LoanTypeModel {
    private Long idLoanType;
    private String name;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal interestRate;
    private Boolean autoValidation;

    public LoanTypeModel() {
    }

    public LoanTypeModel(Long idLoanType) {
        this.idLoanType = idLoanType;
    }

    public LoanTypeModel(Long idLoanType, String name, BigDecimal minAmount, BigDecimal maxAmount, BigDecimal interestRate, Boolean autoValidation) {
        this.idLoanType = idLoanType;
        this.name = name;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.interestRate = interestRate;
        this.autoValidation = autoValidation;
    }

    public Long getIdLoanType() {
        return idLoanType;
    }

    public void setIdLoanType(Long idLoanType) {
        this.idLoanType = idLoanType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Boolean getAutoValidation() {
        return autoValidation;
    }

    public void setAutoValidation(Boolean autoValidation) {
        this.autoValidation = autoValidation;
    }


}
