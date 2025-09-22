package co.com.bancolombia.config;

import co.com.bancolombia.r2dbc.health.R2dbcSafeExecutor;
import co.com.bancolombia.r2dbc.mapper.LoanApplicationMapperR2dbc;
import co.com.bancolombia.r2dbc.repository.LoanApplicationRepository;
import co.com.bancolombia.r2dbc.repository.LoanTypeRepository;
import co.com.bancolombia.r2dbc.repository.StateRepository;
import co.com.bancolombia.usecase.loanapplication.usecase.api.LoanApplicationServicePort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UseCasesConfigTest {

    @MockitoBean
    private R2dbcSafeExecutor r2dbcSafeExecutor;
    @MockitoBean
    private LoanApplicationRepository loanApplicationRepository;
    @MockitoBean
    private StateRepository stateRepository;
    @MockitoBean
    private LoanTypeRepository loanTypeRepository;
    @MockitoBean
    private LoanApplicationMapperR2dbc loanApplicationMapperR2dbc;
    @MockitoBean
    private LoanApplicationMapperR2dbc loanTypeMapperR2dbc;
    @MockitoBean
    private LoanApplicationMapperR2dbc stateMapperR2dbc;
    @Autowired
    private LoanApplicationServicePort loanApplicationServicePort;

    @Test
    void shouldLoadLoanApplicationServicePortBean() {
        assertThat(loanApplicationServicePort).isNotNull();
    }
}