package com.small.vo;

/**
 * 富文本VO
 * Created by 85073 on 2018/5/11.
 */
public class RichTextVo {

    private boolean success;

    private String msg;

    private String file_path;

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }
}
