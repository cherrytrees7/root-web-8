package meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import meeting.Bean.User;
import meeting.VO.UserVo;
import meeting.domain.AdminUserDetails;

public interface UserService extends IService<User> {

    public String register(UserVo userVo);

    public String login(UserVo userVo);

    public AdminUserDetails getAdminByUsername(String username);

    public String updateUsername(String currentUsername, String newUsername);

    String changePassword(String currentUsername, String currentPassword, String newPassword);
}
