package co.com.bancolombia.consumer.anthenticationclient.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UserResponse {
    private Long idUser;
    private String nameUser;
    private String lastNameUser;
    private String documentUser;
    private LocalDate birthdayUser;
    private String addressUser;
    private String phoneUser;
    private String emailUser;
    private BigDecimal baseSalaryUser;
}
