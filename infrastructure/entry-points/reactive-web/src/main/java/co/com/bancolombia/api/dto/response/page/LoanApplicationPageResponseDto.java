package co.com.bancolombia.api.dto.response.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanApplicationPageResponseDto {
    private List<LoanApplicationWithPaginationResponseDto> content;
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private Long totalElements;
}
