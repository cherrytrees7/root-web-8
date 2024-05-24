package meeting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import meeting.Bean.Meet;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MeetMapper extends BaseMapper<Meet> {
}
