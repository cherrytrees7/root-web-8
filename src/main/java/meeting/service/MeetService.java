package meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import meeting.Bean.Meet;
import meeting.domain.AdminUserDetails;
import org.springframework.http.ResponseEntity;

import java.util.Map;


public interface MeetService extends IService<Meet> {

    public String login(String username, String password );

    public Meet check(String mail, String telePhone);


    AdminUserDetails getAdminByUsername(String username);


    String register(String username, String password,String email,String telePhone);
}
