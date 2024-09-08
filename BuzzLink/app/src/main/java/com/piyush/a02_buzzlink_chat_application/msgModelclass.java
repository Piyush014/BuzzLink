package com.piyush.a02_buzzlink_chat_application;

public class msgModelclass {
    private String senderid;
    private String message;
    private long timeStamp;

    public msgModelclass() {
        // Default constructor required for calls to DataSnapshot.getValue(msgModelclass.class)
    }

    public msgModelclass(String senderid, String message, long timeStamp) {
        this.senderid = senderid;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
