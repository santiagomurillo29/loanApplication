package co.com.bancolombia.r2dbc.helper;

import co.com.bancolombia.model.loanapplication.model.LoanApplicationModel;
import co.com.bancolombia.model.loanapplication.model.LoanTypeModel;
import co.com.bancolombia.model.loanapplication.model.StateModel;
import co.com.bancolombia.r2dbc.adapter.LoanApplicationAdapterR2dbc;
import co.com.bancolombia.r2dbc.entity.LoanApplicationEntity;
import co.com.bancolombia.r2dbc.entity.LoanTypeEntity;
import co.com.bancolombia.r2dbc.entity.StateEntity;
import co.com.bancolombia.r2dbc.health.R2dbcSafeExecutor;
import co.com.bancolombia.r2dbc.mapper.LoanApplicationMapperR2dbc;
import co.com.bancolombia.r2dbc.mapper.LoanTypeMapperR2dbc;
import co.com.bancolombia.r2dbc.mapper.StateMapperR2dbc;
import co.com.bancolombia.r2dbc.repository.LoanApplicationRepository;
import co.com.bancolombia.r2dbc.repository.LoanApplicationRepositoryCustom;
import co.com.bancolombia.r2dbc.repository.LoanTypeRepository;
import co.com.bancolombia.r2dbc.repository.StateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Supplier;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static reactor.test.StepVerifier.create;

@ExtendWith(MockitoExtension.class)
public class LoanApplicationAdapterTest {

    @Mock private LoanApplicationRepository loanApplicationRepository;
    @Mock private LoanApplicationRepositoryCustom loanApplicationRepositoryCustom;
    @Mock private LoanTypeRepository loanTypeRepository;
    @Mock private StateRepository stateRepository;
    @Mock private LoanApplicationMapperR2dbc loanApplicationMapperR2dbc;
    @Mock private LoanTypeMapperR2dbc loanTypeMapperR2dbc;
    @Mock private StateMapperR2dbc stateMapperR2dbc;
    @Mock private R2dbcSafeExecutor safeExecutor;
    private LoanApplicationAdapterR2dbc adapter;

    @BeforeEach
    void setUp() {
        adapter = new LoanApplicationAdapterR2dbc(
                loanApplicationRepository,
                loanApplicationRepositoryCustom,
                loanTypeRepository,
                stateRepository,
                loanApplicationMapperR2dbc,
                loanTypeMapperR2dbc,
                stateMapperR2dbc,
                safeExecutor
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void saveLoanApplication_persistsAndMaps() {
        StateModel stateModel = new StateModel("Description", "PENDING", 1L);
        LoanTypeModel loanTypeModel = new LoanTypeModel(1L, "Personal Loan", BigDecimal.valueOf(5000.00), BigDecimal.valueOf(500000.00), BigDecimal.valueOf(12.50), true);

        LoanApplicationModel inputModel = new LoanApplicationModel(1L, BigDecimal.valueOf(30000.00), 24, "user@gmail.com", stateModel, loanTypeModel);
        LoanApplicationEntity entity = new LoanApplicationEntity(1L, BigDecimal.valueOf(30000.00), 24, "user@gmail.com", stateModel.getIdState(), loanTypeModel.getIdLoanType());

        when(loanApplicationMapperR2dbc.toEntityLoanApplication(inputModel)).thenReturn(entity);
        when(loanApplicationRepository.save(entity)).thenReturn(Mono.just(entity));

        LoanApplicationModel mappedAfterSave = new LoanApplicationModel(1L, BigDecimal.valueOf(30000.00), 24, "user@gmail.com", stateModel, loanTypeModel);
        when(loanApplicationMapperR2dbc.toModelLoanApplication(entity)).thenReturn(mappedAfterSave);

        when(loanTypeRepository.findById(1L)).thenReturn(Mono.just(new LoanTypeEntity(1L, "Personal Loan", BigDecimal.valueOf(5000.00), BigDecimal.valueOf(500000.00), BigDecimal.valueOf(12.50), true)));
        when(loanTypeMapperR2dbc.toModelLoanType(any())).thenReturn(loanTypeModel);

        when(stateRepository.findByName("PENDING")).thenReturn(Mono.just(new StateEntity(1L, "PENDING", "description")));
        when(stateMapperR2dbc.toModelState(any())).thenReturn(stateModel);

        when(safeExecutor.executeMono(any()))
                .thenAnswer(invocation -> ((Supplier<Mono<LoanApplicationModel>>) invocation.getArgument(0)).get());

        StepVerifier.create(adapter.saveLoanApplication(inputModel))
                .expectNextMatches(result ->
                        result.getIdLoanApplication().equals(1L) &&
                                result.getLoanType().getIdLoanType().equals(1L) &&
                                result.getLoanType().getName().equals("Personal Loan") &&
                                result.getState().getName().equals("PENDING")
                )
                .verifyComplete();
    }

    @Test
    @SuppressWarnings("unchecked")
    void findStateByName_persistsAndMaps() {
        StateModel stateModel = new StateModel("Description", "PENDING", 1L);
        StateEntity stateEntity = new StateEntity(1L, "PENDING", "Description");

        when(stateRepository.findByName("PENDING")).thenReturn(Mono.just(stateEntity));
        when(stateMapperR2dbc.toModelState(stateEntity)).thenReturn(stateModel);

        when(safeExecutor.executeMono(any()))
                .thenAnswer(invocation -> ((Supplier<Mono<StateModel>>) invocation.getArgument(0)).get());

        create(adapter.findStateByName(stateModel.getName()))
                .expectNextMatches(result -> result.getIdState().equals(1L))
                .verifyComplete();
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

    @Test
    void findLoanApplicationsByStates_returnsPagedResult() {
        StateModel stateModel = new StateModel("Description", "PENDING", 1L);
        LoanTypeModel loanTypeModel = new LoanTypeModel(1L, "Personal Loan",
                BigDecimal.valueOf(5000.00),
                BigDecimal.valueOf(500000.00),
                BigDecimal.valueOf(12.50),
                true);

        LoanApplicationModel loan1 = new LoanApplicationModel(
                1L, BigDecimal.valueOf(30000.00), 24, "user1@gmail.com", stateModel, loanTypeModel);
        LoanApplicationModel loan2 = new LoanApplicationModel(
                2L, BigDecimal.valueOf(40000.00), 12, "user2@gmail.com", stateModel, loanTypeModel);

        when(loanApplicationRepositoryCustom.findByStatesPaged(any(), any(Integer.class), any(Integer.class)))
                .thenReturn(Flux.just(loan1, loan2));
        when(loanApplicationRepositoryCustom.countByStates(any()))
                .thenReturn(Mono.just(2L));

        StepVerifier.create(adapter.findLoanApplicationsByStates(0, 10, List.of("PENDING")))
                .expectNextMatches(page ->
                        page.getContent().size() == 2 &&
                                page.getContent().get(0).getIdLoanApplication().equals(1L) &&
                                page.getContent().get(1).getIdLoanApplication().equals(2L) &&
                                page.getCurrentPage() == 0 &&
                                page.getPageSize() == 10 &&
                                page.getTotalPages() == 1 &&
                                page.getTotalElements() == 2
                )
                .verifyComplete();

    }
}
