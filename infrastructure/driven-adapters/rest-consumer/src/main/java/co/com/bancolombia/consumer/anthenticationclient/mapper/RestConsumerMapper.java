package co.com.bancolombia.consumer.anthenticationclient.mapper;

import co.com.bancolombia.consumer.anthenticationclient.model.UserResponseDto;
import co.com.bancolombia.model.loanapplication.model.restconsumer.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RestConsumerMapper {
    UserResponse toDomain(UserResponseDto dto);
}
