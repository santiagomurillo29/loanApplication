package co.com.bancolombia.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("state")
public class StateEntity {
    @Id
    @Column("id_state")
    private Long idState;

    @Column("name")
    private String name;

    @Column("description")
    private String description;
}
