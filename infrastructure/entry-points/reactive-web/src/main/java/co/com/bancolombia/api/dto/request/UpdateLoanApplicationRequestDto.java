package co.com.bancolombia.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "UpdateLoanApplicationRequest", description = "Model represent a loan application on database")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLoanApplicationRequestDto {
    @Schema(description = "State of loan application", example = "APPROVED or REJECTED")
    @NotBlank(message = "The STATE must not be empty or null")
    private String state;
}
