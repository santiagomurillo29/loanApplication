package co.com.bancolombia.sqs.sender.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationStateDto {
    private Long id;

    @JsonProperty("state")
    private String state;

    @JsonProperty("email")
    private String email;

    @JsonProperty("type")
    private String type;
}
