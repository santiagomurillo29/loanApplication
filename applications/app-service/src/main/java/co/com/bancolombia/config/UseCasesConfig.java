package co.com.bancolombia.config;

import co.com.bancolombia.model.loanapplication.gateways.AuthenticationClientPort;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationPersistencePort;
import co.com.bancolombia.model.loanapplication.gateways.LoanTypePersistencePort;
import co.com.bancolombia.model.loanapplication.gateways.SQSSenderPort;
import co.com.bancolombia.model.loanapplication.gateways.StatePersistencePort;
import co.com.bancolombia.model.loanapplication.gateways.TokenAuthSecurityPort;
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
        private final AuthenticationClientPort authenticationClientPort;
        private final SQSSenderPort sqsSenderPort;


        @Bean
        public LoanApplicationServicePort loanApplicationServicePort() {
                return new LoanApplicationUseCase(
                        tokenAuthSecurityPort,
                        loanApplicationPersistencePort,
                        statePersistencePort,
                        loanTypePersistencePort,
                        authenticationClientPort,
                        sqsSenderPort
                );
        }


}
