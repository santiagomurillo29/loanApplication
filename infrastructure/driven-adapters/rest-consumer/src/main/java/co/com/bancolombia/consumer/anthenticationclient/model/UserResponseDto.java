package co.com.bancolombia.consumer.anthenticationclient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
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
