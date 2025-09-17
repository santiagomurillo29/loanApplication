package co.com.bancolombia.api.router;

import co.com.bancolombia.api.dto.request.LoanApplicationRequestDto;
import co.com.bancolombia.api.dto.request.UpdateLoanApplicationRequestDto;
import co.com.bancolombia.api.dto.response.LoanApplicationResponseDto;
import co.com.bancolombia.api.handler.HandlerLoanApplication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRestLoanApplication {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    method = RequestMethod.GET,
                    beanClass = HandlerLoanApplication.class,
                    beanMethod = "GetLoanApplication",
                    operation = @Operation(
                            operationId = "getLoanApplications",
                            summary = "Get all loan applications",
                            parameters = {
                                    @Parameter(
                                            name = "page",
                                            description = "Page number",
                                            in = ParameterIn.QUERY,
                                            required = false,
                                            schema = @Schema(type = "integer", defaultValue = "0")
                                    ),
                                    @Parameter(
                                            name = "size",
                                            description = "Page size",
                                            in = ParameterIn.QUERY,
                                            required = false,
                                            schema = @Schema(type = "integer", defaultValue = "10")
                                    ),
                                    @Parameter(
                                            name = "state",
                                            description = "Loan state(s)",
                                            in = ParameterIn.QUERY,
                                            required = false,
                                            schema = @Schema(type = "string", example = "PENDING")
                                    )
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "OK")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/solicitudes",
                    method = RequestMethod.POST,
                    beanClass = HandlerLoanApplication.class,
                    beanMethod = "CreateLoanApplication",
                    consumes = "application/json",
                    produces = "application/json",
                    operation = @Operation(
                            operationId = "createLoanApplication",
                            summary = "Create a new loan application",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    description = "Loan application to create",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = LoanApplicationRequestDto.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Loan application created",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = LoanApplicationResponseDto.class))
                                    ),
                                    @ApiResponse(responseCode = "409", description = "Loan application already exists")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/solicitud/{idLoanApplication}",
                    method = RequestMethod.PUT,
                    beanClass = HandlerLoanApplication.class,
                    beanMethod = "UpdateLoanApplication",
                    consumes = "application/json",
                    produces = "application/json",
                    operation = @Operation(
                            operationId = "updateLoanApplication",
                            summary = "Update an existing loan application",
                            parameters = {
                                    @Parameter(
                                            name = "idLoanApplication",
                                            description = "Loan application ID",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            schema = @Schema(type = "integer", example = "1")
                                    )
                            },
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    description = "Updated loan application data",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = UpdateLoanApplicationRequestDto.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Loan application updated",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = LoanApplicationResponseDto.class)
                                            )
                                    ),
                                    @ApiResponse(responseCode = "404", description = "Loan application not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/calcular-capacidad/{idLoanApplication}",
                    method = RequestMethod.POST,
                    beanClass = HandlerLoanApplication.class,
                    beanMethod = "CalculateCapacityLoanApplication",
                    consumes = "application/json",
                    produces = "application/json",
                    operation = @Operation(
                            operationId = "calculateCapacityLoanApplication",
                            summary = "calculate capacity an loan application",
                            parameters = {
                                    @Parameter(
                                            name = "idLoanApplication",
                                            description = "Loan application ID",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            schema = @Schema(type = "integer", example = "1")
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Loan application calculate capacity",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = LoanApplicationResponseDto.class)
                                            )
                                    ),
                                    @ApiResponse(responseCode = "404", description = "Loan application not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/health",
                    method = RequestMethod.GET,
                    beanClass = HandlerLoanApplication.class,
                    beanMethod = "GetHealth",
                    produces = "application/json",
                    operation = @Operation(
                            operationId = "getHealth",
                            summary = "Check database health",
                            description = "Returns UP when the database is reachable, otherwise DOWN.",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Database is UP",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(example = "{\"status\":\"UP\",\"database\":\"UP\"}")
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "503",
                                            description = "Database is DOWN",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(example = "{\"status\":\"DOWN\",\"database\":\"DOWN\"}")
                                            )
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/liveness",
                    method = RequestMethod.GET,
                    beanClass = HandlerLoanApplication.class,
                    beanMethod = "GetLiveness",
                    produces = "text/plain",
                    operation = @Operation(
                            operationId = "getLiveness",
                            summary = "Check liveness",
                            description = "Always returns OK if the service is alive.",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Service is alive",
                                            content = @Content(
                                                    mediaType = "text/plain",
                                                    schema = @Schema(example = "OK")
                                            )
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(HandlerLoanApplication handlerLoanApplication) {
        return route(GET("/api/v1/solicitud"), handlerLoanApplication::GetLoanApplication)
                .andRoute(GET("/health"), handlerLoanApplication::GetHealth)
                .andRoute(GET("/liveness"), handlerLoanApplication::GetLiveness)
                .andRoute(POST("/api/v1/solicitudes"), handlerLoanApplication::CreateLoanApplication)
                .andRoute(POST("/api/v1/calcular-capacidad/{idLoanApplication}"), handlerLoanApplication::CalculateCapacityLoanApplication)
                .andRoute(PUT("/api/v1/solicitud/{idLoanApplication}"), handlerLoanApplication::UpdateLoanApplication);
    }
}
