package co.com.bancolombia.r2dbc.helper;

import co.com.bancolombia.model.loanapplication.model.StateModel;
import co.com.bancolombia.r2dbc.adapter.StateAdapterR2dbc;
import co.com.bancolombia.r2dbc.entity.StateEntity;
import co.com.bancolombia.r2dbc.health.R2dbcSafeExecutor;
import co.com.bancolombia.r2dbc.mapper.StateMapperR2dbc;
import co.com.bancolombia.r2dbc.repository.StateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static reactor.test.StepVerifier.create;

@ExtendWith(MockitoExtension.class)
class StateAdapterR2dbcTest {
    @Mock private StateRepository stateRepository;
    @Mock private StateMapperR2dbc stateMapperR2dbc;
    @Mock private R2dbcSafeExecutor safeExecutor;
    private StateAdapterR2dbc adapter;

    @BeforeEach
    void setUp() {
        adapter = new StateAdapterR2dbc(
                stateRepository,
                stateMapperR2dbc,
                safeExecutor
        );
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
    void findStateById_persistsAndMaps() {
        StateModel stateModel = new StateModel("Description", "PENDING", 1L);
        StateEntity stateEntity = new StateEntity(1L, "PENDING", "Description");

        when(stateRepository.findById(stateEntity.getIdState())).thenReturn(Mono.just(stateEntity));
        when(stateMapperR2dbc.toModelState(stateEntity)).thenReturn(stateModel);

        when(safeExecutor.executeMono(any()))
                .thenAnswer(invocation -> ((Supplier<Mono<StateModel>>) invocation.getArgument(0)).get());

        create(adapter.findStateById(stateModel.getIdState()))
                .expectNextMatches(result -> result.getIdState().equals(1L))
                .verifyComplete();
    }
}
