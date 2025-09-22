package co.com.bancolombia.r2dbc.repository;

import co.com.bancolombia.model.loanapplication.model.LoanApplicationModel;
import co.com.bancolombia.r2dbc.mapper.LoanApplicationRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LoanApplicationRepositoryCustom {

    private final DatabaseClient client;
    private final LoanApplicationRowMapper rowMapper;

    public Flux<LoanApplicationModel> findByStatesPaged(List<String> states, int page, int size) {
        String sql =
                "SELECT s.id_loan_application AS id, s.amount, s.term, s.email, " +
                        "tp.id_loan_type AS loan_type_id, tp.name AS loan_type_name, tp.interest_rate, " +
                        "st.id_state AS state_id, st.name AS state_name " +
                        "FROM loan_application s " +
                        "JOIN loan_type tp ON s.id_loan_type = tp.id_loan_type " +
                        "JOIN state st ON s.id_state = st.id_state " +
                        "WHERE st.name IN (:states) " +
                        "LIMIT :limit OFFSET :offset";

        return client.sql(sql)
                .bind("states", states)
                .bind("limit", size)
                .bind("offset", page * size)
                .map(rowMapper::map)
                .all();
    }

    public Mono<Long> countByStates(List<String> states) {
        String sql = "SELECT COUNT(*) AS cnt " +
                "FROM loan_application s " +
                "JOIN state st ON s.id_state = st.id_state " +
                "WHERE st.name IN (:states)";

        return client.sql(sql)
                .bind("states", states)
                .map((row, meta) -> row.get("cnt", Long.class))
                .one();
    }
}
