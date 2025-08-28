package co.com.bancolombia.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(name = "LoanApplicationResponse", description = "Model represent a loan application on database")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoanApplicationResponseDto {
    @Schema(description = "Unique identifier of the user")
    private Long idLoanApplication;

    @Schema(description = "LoanApplication's amount", example = "500000")
    private BigDecimal amount;

    @Schema(description = "LoanApplication's term", example = "24")
    private Integer term;

    @Schema(description = "User's email", example = "string@gmail.com")
    private String email;

    @Schema(description = "Number of state", example = "1")
    private Long idState;

    @Schema(description = "Number of loan type", example = "1")
    private Long idLoanType;
}
