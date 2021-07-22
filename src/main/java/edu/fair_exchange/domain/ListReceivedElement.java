package edu.fair_exchange.domain;

public class ListReceivedElement {

    private String fileName;

    private String sender;

    private String status;

    private String fileSequenceNumber;

    public ListReceivedElement(String fileName, String sender) {
        this.fileName = fileName;
        this.sender = sender;
    }
    public ListReceivedElement() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFileSequenceNumber() {
        return fileSequenceNumber;
    }

    public void setFileSequenceNumber(String fileSequenceNumber) {
        this.fileSequenceNumber = fileSequenceNumber;
    }
}
