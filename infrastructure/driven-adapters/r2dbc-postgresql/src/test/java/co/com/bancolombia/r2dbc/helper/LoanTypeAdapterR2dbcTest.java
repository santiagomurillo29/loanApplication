package co.com.bancolombia.r2dbc.helper;

import co.com.bancolombia.model.loanapplication.model.LoanTypeModel;
import co.com.bancolombia.r2dbc.adapter.LoanTypeAdapterR2dbc;
import co.com.bancolombia.r2dbc.entity.LoanTypeEntity;
import co.com.bancolombia.r2dbc.health.R2dbcSafeExecutor;
import co.com.bancolombia.r2dbc.mapper.LoanTypeMapperR2dbc;
import co.com.bancolombia.r2dbc.repository.LoanTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static reactor.test.StepVerifier.create;

@ExtendWith(MockitoExtension.class)
class LoanTypeAdapterR2dbcTest {
    @Mock private LoanTypeRepository loanTypeRepository;
    @Mock private LoanTypeMapperR2dbc loanTypeMapperR2dbc;
    @Mock private R2dbcSafeExecutor safeExecutor;
    private LoanTypeAdapterR2dbc adapter;

    @BeforeEach
    void setUp() {
        adapter = new LoanTypeAdapterR2dbc(
                loanTypeRepository,
                loanTypeMapperR2dbc,
                safeExecutor
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void findLoanTypeById_returnsMappedLoanType() {
        Long loanTypeId = 1L;
        LoanTypeEntity loanTypeEntity = new LoanTypeEntity(1L, "Personal Loan", BigDecimal.valueOf(5000.00), BigDecimal.valueOf(500000.00), BigDecimal.valueOf(12.50), true);
        LoanTypeModel loanTypeModel = new LoanTypeModel(1L, "Personal Loan", BigDecimal.valueOf(5000.00), BigDecimal.valueOf(500000.00), BigDecimal.valueOf(12.50), true);

        when(loanTypeRepository.findById(loanTypeId))
                .thenReturn(Mono.just(loanTypeEntity));
        when(loanTypeMapperR2dbc.toModelLoanType(loanTypeEntity)).thenReturn(loanTypeModel);

        when(safeExecutor.executeMono(any()))
                .thenAnswer(invocation -> ((Supplier<Mono<LoanTypeModel>>) invocation.getArgument(0)).get());

        create(adapter.findLoanTypeById(loanTypeId))
                .expectNext(loanTypeModel)
                .verifyComplete();
    }
}
