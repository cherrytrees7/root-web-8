package meeting.validation;




import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, PasswordHolder> {
    @Override
    public void initialize(final PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(final PasswordHolder obj, final ConstraintValidatorContext context) {
        return obj.getPassword().equals(obj.getPasswordConfirm());
    }
}

