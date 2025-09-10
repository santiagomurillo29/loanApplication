package co.com.bancolombia.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoanApplicationWithPaginationResponseDto {
    // microservicio authentication
    @Schema(description = "User's name", example = "string@gmail.com")
    private String name;

    @Schema(description = "User's email", example = "string@gmail.com")
    private String email;

    @Schema(description = "User's base salary", example = "3500000.00")
    private BigDecimal baseSalaryUser;

    // this microservice - table loan_application
    @Schema(description = "LoanApplication's amount", example = "500000")
    private BigDecimal amount;

    @Schema(description = "LoanApplication's term", example = "24")
    private Integer term;

    @Schema(description = "Number of loan type", example = "1")
    private Long idLoanType;

    @Schema(description = "Name of loan type", example = "string")
    private String nameLoanType;

    @Schema(description = "Interest rate", example = "12.50")
    private BigDecimal interestRate;

    @Schema(description = "Number of state", example = "1")
    private Long idState;

    @Schema(description = "Name of state", example = "string")
    private String nameState;

    // calcular this result
    @Schema(description = "LoanApplication's amountMonthly", example = "500000")
    private BigDecimal amountMonthly;

    // extras
    private int currentPage;

    private int pageSize;

    private int totalPages;

    private Long totalElements;
}
