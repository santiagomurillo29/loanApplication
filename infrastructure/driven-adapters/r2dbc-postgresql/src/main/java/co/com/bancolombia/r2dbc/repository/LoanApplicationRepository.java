package co.com.bancolombia.r2dbc.repository;

import co.com.bancolombia.r2dbc.entity.LoanApplicationEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface LoanApplicationRepository extends ReactiveCrudRepository<LoanApplicationEntity, Long> {
}
