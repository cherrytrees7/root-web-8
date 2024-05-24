package meeting.convert;


import meeting.Bean.User;
import meeting.VO.UserVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapStructMapper {

    UserMapStructMapper INSTANCE = Mappers.getMapper(UserMapStructMapper.class);

    User voToEntity(UserVo userVo);

    UserVo entityToVo(User user);
}
