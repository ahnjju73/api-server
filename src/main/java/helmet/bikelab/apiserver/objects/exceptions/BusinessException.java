package helmet.bikelab.apiserver.objects.exceptions;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException implements AutoCloseable {

    private String msg;

    private String err_code;

    private HttpStatus errHttpStatus = HttpStatus.BAD_REQUEST;

    public BusinessException(){}

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String msg, HttpStatus httpStatus, String err_code){
        this.msg = msg;
        this.errHttpStatus = httpStatus;
        this.err_code = err_code;
    }

    public HttpStatus getErrHttpStatus() {
        return errHttpStatus;
    }

    public void setErrHttpStatus(HttpStatus errHttpStatus) {
        this.errHttpStatus = errHttpStatus;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getErr_code() {
        return err_code;
    }

    public void setErr_code(String err_code) {
        this.err_code = err_code;
    }

    @Override
    public void close() throws Exception {

    }
}