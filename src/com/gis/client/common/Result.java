package com.gis.client.common;

/**
 * @author wpt
 * @param <T>
 */
public class Result<T> {
    //正常
    public static final int OK = 0;
    
    //客户端错误
    public static final int CLIENT_ERROR_NETWORK = 1;
    public static final int CLIENT_ERROR_NO_RESULT = 2;
    public static final int CLIENT_ERROR_JSON_ERROR = 3;
    
    
    private int total;
    private String errorCode;
    private T result;

    public Result(T result) {
        this.result = result;
    }

    public Result(String errorCode, T result) {
        this.errorCode = errorCode;
        this.result = result;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public boolean isSucceeded() {
        return errorCode.equals(OK);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

}
