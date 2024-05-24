package meeting.VO;


import lombok.Data;
import meeting.validation.PasswordHolder;
import meeting.validation.PasswordMatches;

@Data
@PasswordMatches
public class PasswordChangeRequest implements PasswordHolder {

    private String currentPassword;
    private String password;
    private String passwordConfirm;
}
