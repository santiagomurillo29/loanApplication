package co.com.bancolombia.config;

import co.com.bancolombia.model.loanapplication.gateways.AuthenticationClientPersistencePort;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationPersistencePort;
import co.com.bancolombia.r2dbc.adapter.LoanApplicationAdapterR2dbc;
import co.com.bancolombia.r2dbc.health.R2dbcSafeExecutor;
import co.com.bancolombia.r2dbc.mapper.LoanApplicationMapperR2dbc;
import co.com.bancolombia.r2dbc.repository.LoanApplicationRepository;
import co.com.bancolombia.r2dbc.repository.LoanTypeRepository;
import co.com.bancolombia.r2dbc.repository.StateRepository;
import co.com.bancolombia.usecase.loanapplication.usecase.LoanApplicationUseCase;
import co.com.bancolombia.usecase.loanapplication.usecase.api.LoanApplicationServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UseCasesConfig {

        private final R2dbcSafeExecutor r2dbcSafeExecutor;
        private final LoanApplicationRepository loanApplicationRepository;
        private final StateRepository stateRepository;
        private final LoanTypeRepository loanTypeRepository;
        private final LoanApplicationMapperR2dbc loanApplicationMapperR2dbc;

        @Bean
        public LoanApplicationPersistencePort loanApplicationPersistencePort(){
                return new LoanApplicationAdapterR2dbc(
                        loanApplicationRepository,
                        loanTypeRepository,
                        stateRepository,
                        loanApplicationMapperR2dbc,
                        r2dbcSafeExecutor
                );
        }

        @Bean
        public LoanApplicationServicePort loanApplicationServicePort(
                AuthenticationClientPersistencePort authenticationClientPersistencePort
        ) {
                return new LoanApplicationUseCase(
                        loanApplicationPersistencePort(),
                        authenticationClientPersistencePort
                        );
        }
}
