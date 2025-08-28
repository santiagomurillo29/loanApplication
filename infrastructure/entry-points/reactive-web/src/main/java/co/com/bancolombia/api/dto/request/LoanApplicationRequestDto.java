package co.com.bancolombia.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(name = "LoanApplicationRequest", description = "Model represent a loan application on database")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationRequestDto {

    @Schema(description = "LoanApplication's amount", example = "500000")
    //@NotNull(message = "The amount must not be null")
    @DecimalMin(value = "1000.0", inclusive = true, message = "The amount must be at least 1000")
    private BigDecimal amount;

    @Schema(description = "LoanApplication's term", example = "24")
    @NotNull(message = "The term must not be null")
    @Min(value = 1, message = "The term must be at least 1 month")
    @Max(value = 360, message = "The term must not exceed 360 months")
    private Integer term;

    @Schema(description = "User's email", example = "string@gmail.com")
    @NotBlank(message = "The email must not be empty or null")
    @Email(message = "The email must be a valid email address")
    private String email;

    @Schema(description = "Number of loan type", example = "1")
    @NotNull(message = "The loan type ID must not be null")
    @Positive(message = "The loan type ID must be a positive number")
    private Long idLoanType;
}
