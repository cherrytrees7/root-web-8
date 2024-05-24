package meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import meeting.Bean.UserUpload;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserUploadService  extends IService<UserUpload> {

    void saveUploadInfo(UserUpload userUpload);

    List<UserUpload> findAllByUserName(String userName);

}