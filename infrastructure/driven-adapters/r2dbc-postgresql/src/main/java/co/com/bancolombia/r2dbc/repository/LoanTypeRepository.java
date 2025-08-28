package co.com.bancolombia.r2dbc.repository;

import co.com.bancolombia.r2dbc.entity.LoanTypeEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface LoanTypeRepository extends ReactiveCrudRepository<LoanTypeEntity, Long> {
}
