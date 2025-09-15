package co.com.bancolombia.config;

import co.com.bancolombia.model.loanapplication.gateways.AuthenticationClientPersistencePort;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationPersistencePort;
import co.com.bancolombia.model.loanapplication.gateways.LoanTypePersistencePort;
import co.com.bancolombia.model.loanapplication.gateways.SQSSenderPersistencePort;
import co.com.bancolombia.model.loanapplication.gateways.StatePersistencePort;
import co.com.bancolombia.model.loanapplication.gateways.TokenAuthSecurityPort;
import co.com.bancolombia.r2dbc.adapter.LoanApplicationAdapterR2dbc;
import co.com.bancolombia.r2dbc.health.R2dbcSafeExecutor;
import co.com.bancolombia.r2dbc.mapper.LoanApplicationMapperR2dbc;
import co.com.bancolombia.r2dbc.mapper.LoanTypeMapperR2dbc;
import co.com.bancolombia.r2dbc.mapper.StateMapperR2dbc;
import co.com.bancolombia.r2dbc.repository.LoanApplicationRepository;
import co.com.bancolombia.r2dbc.repository.LoanApplicationRepositoryCustom;
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

        private final TokenAuthSecurityPort tokenAuthSecurityPort;
        private final LoanApplicationPersistencePort loanApplicationPersistencePort;
        private final StatePersistencePort statePersistencePort;
        private final LoanTypePersistencePort loanTypePersistencePort;
        private final AuthenticationClientPersistencePort authenticationClientPersistencePort;
        private final SQSSenderPersistencePort sqsSenderPersistencePort;


        @Bean
        public LoanApplicationServicePort loanApplicationServicePort() {
                return new LoanApplicationUseCase(
                        tokenAuthSecurityPort,
                        loanApplicationPersistencePort,
                        statePersistencePort,
                        loanTypePersistencePort,
                        authenticationClientPersistencePort,
                        sqsSenderPersistencePort
                );
        }


}
