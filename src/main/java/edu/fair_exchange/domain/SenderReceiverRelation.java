package edu.fair_exchange.domain;

public class SenderReceiverRelation {
    private Integer id;

    private String senderId;

    private String receiverId;

    private String fileId;

    //0--unaccepted, 1--accepted, 2--abort
    private Integer status;

    public SenderReceiverRelation(Integer id, String senderId, String receiverId, String fileId, Integer status) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.fileId = fileId;
        this.status = status;
    }

    public SenderReceiverRelation() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String  getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SenderReceiverRelation{" +
                "id=" + id +
                ", senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", fileId='" + fileId + '\'' +
                '}';
    }
}
