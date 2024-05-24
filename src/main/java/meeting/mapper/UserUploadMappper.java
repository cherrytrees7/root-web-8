package meeting.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import meeting.Bean.UserUpload;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserUploadMappper extends BaseMapper<UserUpload> {

}
