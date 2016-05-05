package com.zyyoona7.androidserialport.bean;

import java.io.Serializable;

/**
 * Created by User on 2016/5/4.
 */
public class MsgBean implements Serializable {

    private boolean isFrom;
    private String msg;
    private int imgId;

    public final static int RECIEVE_MSG = 0;
    public final static int SEND_MSG = 1;

    public boolean isFrom() {
        return isFrom;
    }

    public void setFrom(boolean from) {
        isFrom = from;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }
}
