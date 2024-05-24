package meeting.VO;


import lombok.Data;
import meeting.validation.PasswordHolder;
import meeting.validation.PasswordMatches;


import javax.validation.constraints.NotBlank;




@PasswordMatches
@Data
public class UserVo  implements PasswordHolder {


    @NotBlank(message = "用户名不能为空")
    private String userName;

    private String password;

    private String passwordConfirm;
}
