package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.request.LoanApplicationRequestDto;
import co.com.bancolombia.api.dto.request.validation.RequestValidator;
import co.com.bancolombia.api.dto.response.LoanApplicationResponseDto;
import co.com.bancolombia.api.handler.HandlerLoanApplication;
import co.com.bancolombia.api.mapper.LoanApplicationMapper;
import co.com.bancolombia.api.router.RouterRestLoanApplication;
import co.com.bancolombia.model.loanapplication.model.LoanApplicationModel;
import co.com.bancolombia.usecase.loanapplication.usecase.api.LoanApplicationServicePort;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ContextConfiguration(classes = {RouterRestLoanApplication.class, HandlerLoanApplication.class})
@WebFluxTest
class RouterRestLoanApplicationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private LoanApplicationServicePort servicePort;

    @MockitoBean
    private RequestValidator validator;

    @MockitoBean
    private LoanApplicationMapper mapper;

    @Test
    void createLoanApplication_endpoint() {
        LoanApplicationRequestDto request = new LoanApplicationRequestDto(BigDecimal.valueOf(30000.00), 24, "user@gmail.com", 1L);
        LoanApplicationModel model = new LoanApplicationModel(1L, BigDecimal.valueOf(30000.00), 24, "user@gmail.com", null, null);
        LoanApplicationResponseDto response = new LoanApplicationResponseDto(1L, BigDecimal.valueOf(30000.00), 24, "user@gmail.com", 1L, "Personal Loan", 1L, "PENDING");

        given(validator.validate(any())).willReturn(Mono.just(request));
        given(mapper.toModelLoanApplication(any())).willReturn(model);
        given(servicePort.createLoanApplication(any())).willReturn(Mono.just(model));
        given(mapper.toDtoLoanApplication(any())).willReturn(response);

        webTestClient.post().uri("/api/v1/solicitudes")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LoanApplicationResponseDto.class)
                .isEqualTo(response);
    }
}
