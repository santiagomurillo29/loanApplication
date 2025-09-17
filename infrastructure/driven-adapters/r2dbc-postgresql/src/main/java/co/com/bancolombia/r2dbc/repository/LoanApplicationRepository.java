package co.com.bancolombia.r2dbc.repository;

import co.com.bancolombia.r2dbc.entity.LoanApplicationEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface LoanApplicationRepository extends ReactiveCrudRepository<LoanApplicationEntity, Long> {

    @Query("SELECT * FROM loan_application la " +
            "INNER JOIN state s ON la.id_state = s.id_state " +
            "WHERE la.email = :email AND s.name = 'APPROVED'")
    Flux<LoanApplicationEntity> findApprovedLoansByEmail(String email);
}
