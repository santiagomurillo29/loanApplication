package co.com.bancolombia.usecase.loanapplication.usecase;

import co.com.bancolombia.model.loanapplication.gateways.AuthenticationClientPersistencePort;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationPersistencePort;
import co.com.bancolombia.model.loanapplication.gateways.LoanTypePersistencePort;
import co.com.bancolombia.model.loanapplication.gateways.SQSSenderPersistencePort;
import co.com.bancolombia.model.loanapplication.gateways.StatePersistencePort;
import co.com.bancolombia.model.loanapplication.gateways.TokenAuthSecurityPort;
import co.com.bancolombia.model.loanapplication.globalmessage.GlobalMessage;
import co.com.bancolombia.model.loanapplication.model.LoanApplicationModel;
import co.com.bancolombia.model.loanapplication.model.LoanApplicationPendingModel;
import co.com.bancolombia.model.loanapplication.model.page.PageLoanApplicationModel;
import co.com.bancolombia.usecase.loanapplication.exception.BusinessException;
import co.com.bancolombia.usecase.loanapplication.usecase.api.LoanApplicationServicePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RequiredArgsConstructor
public class LoanApplicationUseCase implements LoanApplicationServicePort {

    private final TokenAuthSecurityPort tokenAuthSecurityPort;
    private final LoanApplicationPersistencePort loanApplicationPersistencePort;
    private final StatePersistencePort statePersistencePort;
    private final LoanTypePersistencePort loanTypePersistencePort;
    private final AuthenticationClientPersistencePort authenticationClientPersistencePort;
    private final SQSSenderPersistencePort sqsSenderPersistencePort;

    @Override
    public Mono<LoanApplicationModel> createLoanApplication(LoanApplicationModel loanApplicationModel, String token) {
        return tokenAuthSecurityPort.getSubject(token)
                .flatMap(userEmailFromToken -> {
                    if (!loanApplicationModel.getEmail().equalsIgnoreCase(userEmailFromToken)) {
                        return Mono.error(new BusinessException(GlobalMessage.EMAIL_NOT_MATCH));
                    }
                    return authenticationClientPersistencePort.validateEmailExists(loanApplicationModel.getEmail(), token)
                            .flatMap(exists -> {
                                if (!exists) {
                                    return Mono.error(new BusinessException(GlobalMessage.NOT_FOUND_EMAIL));
                                }
                                return loanTypePersistencePort.findLoanTypeById(
                                        loanApplicationModel.getLoanType().getIdLoanType()
                                );
                            })
                            .flatMap(loanTypeModel -> {
                                loanApplicationModel.setLoanType(loanTypeModel);
                                return statePersistencePort.findStateByName("PENDING")
                                        .switchIfEmpty(Mono.error(new BusinessException(GlobalMessage.NOT_FOUND_STATE)));
                            })
                            .flatMap(state -> {
                                loanApplicationModel.setState(state);
                                return loanApplicationPersistencePort.saveLoanApplication(loanApplicationModel);
                            });
                });
    }

    @Override
    public Mono<LoanApplicationModel> updateLoanApplication(Long idLoanApplication, String stateName, String token) {
        return tokenAuthSecurityPort.getSubject(token)
                .flatMap(subject ->
                        statePersistencePort.findStateByName(stateName)
                                .switchIfEmpty(Mono.error(new BusinessException(GlobalMessage.NOT_FOUND_STATE)))
                                .flatMap(newState ->
                                    loanApplicationPersistencePort.findLoanApplicationById(idLoanApplication)
                                            .switchIfEmpty(Mono.error(new BusinessException(GlobalMessage.NOT_FOUND_LOAN_APPLICATION)))
                                            .flatMap(loanApplication -> {
                                                loanApplication.setState(newState);
                                                return loanApplicationPersistencePort.saveLoanApplication(loanApplication)
                                                        .flatMap(saved -> {
                                                            String message = String.format(
                                                                    "{\"id\":%d,\"estado\":\"%s\",\"email\":\"%s\"}",
                                                                    saved.getIdLoanApplication(),
                                                                    saved.getState().getName(),
                                                                    saved.getEmail()
                                                            );
                                                            return sqsSenderPersistencePort.send(message)
                                                                    .thenReturn(saved);
                                                        });
                                            })
                                ));
    }

    @Override
    public Mono<PageLoanApplicationModel<LoanApplicationPendingModel>> findLoanApplicationPending(int page, int size, List<String> states, String token) {
        return tokenAuthSecurityPort.getSubject(token)
                .flatMap(subject ->
                        loanApplicationPersistencePort.findLoanApplicationsByStates(page, size, states)
                                .flatMap(pageModel -> {
                                    List<LoanApplicationModel> content = pageModel.getContent();
                                    return Flux.fromIterable(content)
                                            .flatMap(loanApp ->
                                                    authenticationClientPersistencePort.getUserByEmail(loanApp.getEmail(), token)
                                                            .map(user -> {
                                                                BigDecimal monthly = loanApp.getAmount().divide(BigDecimal.valueOf(loanApp.getTerm()), RoundingMode.HALF_UP);
                                                                return new LoanApplicationPendingModel(loanApp, user.getNameUser(), user.getBaseSalaryUser(), monthly);
                                                            })
                                            )
                                            .collectList()
                                            .map(pendingList -> new PageLoanApplicationModel<>(pendingList, pageModel.getCurrentPage(), pageModel.getPageSize(), pageModel.getTotalPages(), pageModel.getTotalElements()));
                                })
                );
    }
}
