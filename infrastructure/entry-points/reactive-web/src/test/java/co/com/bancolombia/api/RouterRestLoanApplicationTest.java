package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.request.LoanApplicationRequestDto;
import co.com.bancolombia.api.dto.request.UpdateLoanApplicationRequestDto;
import co.com.bancolombia.api.dto.request.validation.RequestValidator;
import co.com.bancolombia.api.dto.response.LoanApplicationResponseDto;
import co.com.bancolombia.api.dto.response.page.LoanApplicationPageResponseDto;
import co.com.bancolombia.api.dto.response.page.LoanApplicationWithPaginationResponseDto;
import co.com.bancolombia.api.handler.HandlerLoanApplication;
import co.com.bancolombia.api.mapper.LoanApplicationMapper;
import co.com.bancolombia.api.router.RouterRestLoanApplication;
import co.com.bancolombia.model.loanapplication.model.LoanApplicationModel;
import co.com.bancolombia.model.loanapplication.model.LoanApplicationPendingModel;
import co.com.bancolombia.model.loanapplication.model.page.PageLoanApplicationModel;
import co.com.bancolombia.usecase.loanapplication.usecase.api.LoanApplicationServicePort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ContextConfiguration(classes = {RouterRestLoanApplication.class, HandlerLoanApplication.class})
@WebFluxTest
@ImportAutoConfiguration(exclude = { org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration.class })
@Import(HandlerLoanApplication.class)
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
        given(servicePort.createLoanApplication(any(LoanApplicationModel.class), any()))
                .willReturn(Mono.just(model));

        given(mapper.toDtoLoanApplication(any())).willReturn(response);

        webTestClient.post().uri("/api/v1/solicitudes")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LoanApplicationResponseDto.class)
                .isEqualTo(response);
    }

    @Test
    void getLoanApplication_endpoint() {
        LoanApplicationModel loanModel = new LoanApplicationModel(1L, BigDecimal.valueOf(500000), 24, "user@gmail.com", null, null);
        LoanApplicationPendingModel pendingModel = new LoanApplicationPendingModel(loanModel, "Santiago", BigDecimal.valueOf(3500000), BigDecimal.valueOf(500000));
        PageLoanApplicationModel<LoanApplicationPendingModel> pageModel = new PageLoanApplicationModel<>(List.of(pendingModel), 0, 10, 1, 1);

        LoanApplicationWithPaginationResponseDto dtoItem = new LoanApplicationWithPaginationResponseDto(null, "Santiago", "user@gmail.com", BigDecimal.valueOf(3500000), BigDecimal.valueOf(500000), 24, 1L, "Personal Loan", BigDecimal.valueOf(12.5), 1L, "PENDING", BigDecimal.valueOf(500000));
        LoanApplicationPageResponseDto responseDto = new LoanApplicationPageResponseDto(List.of(dtoItem), 0, 10, 1, 1L);

        int pages = 0;
        int size = 10;
        List<String> states = List.of("PENDING", "REJECTED");

        given(servicePort.findLoanApplicationPending(eq(pages), eq(size), eq(states), any()))
                .willReturn(Mono.just(pageModel));

        given(mapper.toPageDto(any())).willReturn(responseDto);

        webTestClient.get()
                .uri("/api/v1/solicitud")
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoanApplicationPageResponseDto.class)
                .isEqualTo(responseDto);
    }

    @Test
    void updateLoanApplication_endpoint() {
        Long loanId = 1L;
        String newState = "APPROVED";

        UpdateLoanApplicationRequestDto requestDto = new UpdateLoanApplicationRequestDto(newState);
        LoanApplicationModel model = new LoanApplicationModel(loanId, BigDecimal.valueOf(30000.00), 24, "user@gmail.com", null, null);
        LoanApplicationResponseDto responseDto = new LoanApplicationResponseDto(loanId, BigDecimal.valueOf(30000.00), 24, "user@gmail.com", 1L, "Personal Loan", 1L, newState);

        given(validator.validate(any())).willReturn(Mono.just(requestDto));
        given(servicePort.updateLoanApplication(eq(loanId), eq(newState), any()))
                .willReturn(Mono.just(model));
        given(mapper.toDtoLoanApplication(any())).willReturn(responseDto);

        webTestClient.put()
                .uri("/api/v1/solicitud/{idLoanApplication}", loanId)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LoanApplicationResponseDto.class)
                .isEqualTo(responseDto);
    }

}
