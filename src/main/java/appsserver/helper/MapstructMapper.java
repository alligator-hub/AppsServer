package appsserver.helper;


import appsserver.entity.App;
import appsserver.model.respDto.AppDto;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.mapstruct.ReportingPolicy.IGNORE;


@Mapper(unmappedTargetPolicy = IGNORE, componentModel = "spring")
@Component
public interface MapstructMapper {

    AppDto toAppDto(App app);

    List<AppDto> toAppDto(List<App> apps);

}