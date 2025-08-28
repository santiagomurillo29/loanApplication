package co.com.bancolombia.api.dto.response.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponseBodyDto {
    private String message;
    private String code;
    private String timestamp;
    private String path;
}
