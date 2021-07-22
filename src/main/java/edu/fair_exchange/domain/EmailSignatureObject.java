package edu.fair_exchange.domain;

public class EmailSignatureObject {

    private Integer id;

    private String emailSignature;

    private String responsePlusEmailSignature;

    private String fileSequenceNumber;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmailSignature() {
        return emailSignature;
    }

    public void setEmailSignature(String emailSignature) {
        this.emailSignature = emailSignature;
    }

    public String getResponsePlusEmailSignature() {
        return responsePlusEmailSignature;
    }

    public void setResponsePlusEmailSignature(String responsePlusEmailSignature) {
        this.responsePlusEmailSignature = responsePlusEmailSignature;
    }

    public String getFileSequenceNumber() {
        return fileSequenceNumber;
    }

    public void setFileSequenceNumber(String fileSequenceNumber) {
        this.fileSequenceNumber = fileSequenceNumber;
    }
}
