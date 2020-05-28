package com.mee.manage.exception;

import com.mee.manage.util.StatusCode;

/**
 * NonBizException
 */
public class MeeException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 6796227785803927652L;
    /** 错误码 */
    protected final StatusCode errorCode;

    public MeeException() {
        super(StatusCode.FAIL.getCodeMsg());
        this.errorCode = StatusCode.FAIL;
    }

    /**
     * 指定错误码构造通用异常
     * @param errorCode 错误码
     */
    public MeeException(final StatusCode errorCode) {
        super(errorCode.getCodeMsg());
        this.errorCode = errorCode;
    }
 
    /**
     * 指定详细描述构造通用异常
     * @param detailedMessage 详细描述
     */
    public MeeException(final String detailedMessage) {
        super(detailedMessage);
        this.errorCode = StatusCode.SYS_ERROR;
    }
 
    /**
     * 指定导火索构造通用异常
     * @param t 导火索
     */
    public MeeException(final Throwable t) {
        super(t);
        this.errorCode = StatusCode.SYS_ERROR;
    }
 
    /**
     * 构造通用异常
     * @param errorCode 错误码
     * @param detailedMessage 详细描述
     */
    public MeeException(final StatusCode errorCode, final String detailedMessage) {
        super(detailedMessage);
        this.errorCode = errorCode;
    }
 
    /**
     * 构造通用异常
     * @param errorCode 错误码
     * @param t 导火索
     */
    public MeeException(final StatusCode errorCode, final Throwable t) {
        super(errorCode.getCodeMsg(), t);
        this.errorCode = errorCode;
    }
 
    /**
     * 构造通用异常
     * @param detailedMessage 详细描述
     * @param t 导火索
     */
    public MeeException(final String detailedMessage, final Throwable t) {
        super(detailedMessage, t);
        this.errorCode = StatusCode.SYS_ERROR;
    }
 
    /**
     * 构造通用异常
     * @param errorCode 错误码
     * @param detailedMessage 详细描述
     * @param t 导火索
     */
    public MeeException(final StatusCode errorCode, final String detailedMessage,
                        final Throwable t) {
        super(detailedMessage, t);
        this.errorCode = errorCode;
    }

    public StatusCode getStatusCode() {
        return this.errorCode;
    }
    
}