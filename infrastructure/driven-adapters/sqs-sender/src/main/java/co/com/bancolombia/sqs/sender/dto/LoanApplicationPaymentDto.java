package co.com.bancolombia.sqs.sender.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationPaymentDto {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("state")
    private String state;

    @JsonProperty("email")
    private String email;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("annualRate")
    private BigDecimal annualRate;

    @JsonProperty("term")
    private Integer term;

    @JsonProperty("type")
    private String type;
}
