package helmet.bikelab.apiserver.objects.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class BusinessExceptionWithMessage extends RuntimeException implements AutoCloseable {

    private String messageCode;

    private HttpStatus errHttpStatus = HttpStatus.BAD_REQUEST;

    BusinessExceptionWithMessage(){}

    public BusinessExceptionWithMessage(String messageCode) {
        super(messageCode);
        this.messageCode = messageCode;
    }

    public BusinessExceptionWithMessage(String messageCode, HttpStatus errHttpStatus) {
        super(messageCode);
        this.messageCode = messageCode;
        this.errHttpStatus = errHttpStatus;
    }

    public BusinessExceptionWithMessage(Throwable cause, String messageCode) {
        super(cause);
        this.messageCode = messageCode;
    }

    public BusinessExceptionWithMessage(Throwable cause, String messageCode, HttpStatus errHttpStatus) {
        super(cause);
        this.messageCode = messageCode;
        this.errHttpStatus = errHttpStatus;
    }

    @Override
    public void close() throws Exception {

    }
}