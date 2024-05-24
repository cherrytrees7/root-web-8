package meeting.VO;


import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdateUsernameRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;
}
