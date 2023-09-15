package com.pmk.notif.response;

public class ResponseMsg<T> {
    
    private String rc;
    private String rm;
    private String statusId;
    private String message;
    private String rCode;
    private T data;

    public ResponseMsg() {
    }

    public String getRc() {
        return rc;
    }

    public void setRc(String rc) {
        this.rc = rc;
    }

    public String getRm() {
        return rm;
    }

    public void setRm(String rm) {
        this.rm = rm;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getrCode() {
        return rCode;
    }

    public void setrCode(String rCode) {
        this.rCode = rCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseMsg{" +
                "rc='" + rc + '\'' +
                ", rm='" + rm + '\'' +
                ", statusId='" + statusId + '\'' +
                ", message='" + message + '\'' +
                ", rCode='" + rCode + '\'' +
                ", data=" + data +
                '}';
    }

}
