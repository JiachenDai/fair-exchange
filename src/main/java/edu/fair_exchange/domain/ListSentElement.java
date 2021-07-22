package edu.fair_exchange.domain;

public class ListSentElement {
    private String fileName;

    private String recipient;

    private String status;

    private String fileSequenceNumber;

    public ListSentElement() {
    }


    public ListSentElement(String fileName, String recipient, String status) {
        this.fileName = fileName;
        this.recipient = recipient;
        this.status = status;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
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
