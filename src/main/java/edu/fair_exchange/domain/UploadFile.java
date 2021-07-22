package edu.fair_exchange.domain;

public class UploadFile {
    private Integer id;

    private String filename;

    private String signature;

    private String fileKey;

    private String bucketName;

    private String receipt;

    private String fileSequenceNumber;

    private String abortSignature;

    public UploadFile() {
    }

    public UploadFile(String filename, String signature, String fileKey, String bucketName) {
        this.filename = filename;
        this.signature = signature;
        this.fileKey = fileKey;
        this.bucketName = bucketName;
    }

    public String getAbortSignature() {
        return abortSignature;
    }

    public void setAbortSignature(String abortSignature) {
        this.abortSignature = abortSignature;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getFileSequenceNumber() {
        return fileSequenceNumber;
    }

    public void setFileSequenceNumber(String fileSequenceNumber) {
        this.fileSequenceNumber = fileSequenceNumber;
    }
}
