package co.com.bancolombia.usecase.loanapplication.usecase.businessoperation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class BusinessOperation {
    public static BigDecimal calculateMonthlyInstallment(BigDecimal amount, BigDecimal annualRate, int termInMonths) {
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return amount.divide(BigDecimal.valueOf(termInMonths), 2, RoundingMode.HALF_UP);
        }

        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal power = onePlusRate.pow(termInMonths);

        BigDecimal numerator = amount.multiply(monthlyRate).multiply(power);
        BigDecimal denominator = power.subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateMaximumCapacity(BigDecimal income) {
        return income.multiply(BigDecimal.valueOf(0.35));
    }

    public static BigDecimal calculateCurrentDebt(List<BigDecimal> installments) {
        return installments.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal calculateAvailableCapacity(BigDecimal maximumCapacity, BigDecimal currentDebt) {
        return maximumCapacity.subtract(currentDebt);
    }

    public static String decideLoan(BigDecimal newFee, BigDecimal availableCapacity, BigDecimal loanAmount, BigDecimal income) {
        if (newFee.compareTo(availableCapacity) <= 0) {
            if (loanAmount.compareTo(income.multiply(BigDecimal.valueOf(5))) > 0) {
                return "MANUAL_REVIEW";
            }
            return "APPROVED";
        } else {
            return "REJECTED";
        }
    }
}
