package co.com.bancolombia.r2dbc.mapper;

import co.com.bancolombia.model.loanapplication.model.StateModel;
import co.com.bancolombia.r2dbc.entity.StateEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StateMapperR2dbc {
    StateEntity toEntityState(StateModel stateModel);
    StateModel toModelState(StateEntity stateEntity);
}
