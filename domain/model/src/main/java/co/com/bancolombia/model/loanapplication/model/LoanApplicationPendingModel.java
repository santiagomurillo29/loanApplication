package co.com.bancolombia.model.loanapplication.model;

import java.math.BigDecimal;

public class LoanApplicationPendingModel {
    private LoanApplicationModel loanApplication;
    private String userName;
    private BigDecimal baseSalaryUser;
    private BigDecimal monthlyAmount;

    public LoanApplicationPendingModel(LoanApplicationModel loanApplication, String userName, BigDecimal baseSalaryUser, BigDecimal monthlyAmount) {
        this.loanApplication = loanApplication;
        this.userName = userName;
        this.baseSalaryUser = baseSalaryUser;
        this.monthlyAmount = monthlyAmount;
    }

    public LoanApplicationModel getLoanApplication() {
        return loanApplication;
    }

    public void setLoanApplication(LoanApplicationModel loanApplication) {
        this.loanApplication = loanApplication;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public BigDecimal getBaseSalaryUser() {
        return baseSalaryUser;
    }

    public void setBaseSalaryUser(BigDecimal baseSalaryUser) {
        this.baseSalaryUser = baseSalaryUser;
    }

    public BigDecimal getMonthlyAmount() {
        return monthlyAmount;
    }

    public void setMonthlyAmount(BigDecimal monthlyAmount) {
        this.monthlyAmount = monthlyAmount;
    }
}
