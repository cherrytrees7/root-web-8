package meeting.exception;

import meeting.common.api.ResultCode;



public class BusinessException  extends RuntimeException{

    private long errorCode;
    private String errorMessage;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.errorCode = resultCode.getCode();
        this.errorMessage = resultCode.getMessage();
    }

    public long getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
