package org.openpaas.ieda.iaasDashboard.awsMgnt.web.keypair.dao;

public class AwsKeypairMgntVO {
    private Integer accountId; // 계정 아이디
    private String keyPairName; // Key Pair 이름
    private String fingerPrint; // fingerPrint
    private String privateKey;  // 개인 키
    private String status; // 현재상태
    private String region; // 지역이름
    
    public Integer getAccountId() {
        return accountId;
    }
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
    public String getKeyPairName() {
        return keyPairName;
    }
    public void setKeyPairName(String keyPairName) {
        this.keyPairName = keyPairName;
    }
    public String getFingerPrint() {
        return fingerPrint;
    }
    public void setFingerPrint(String fingerPrint) {
        this.fingerPrint = fingerPrint;
    }
    public String getPrivateKey() {
        return privateKey;
    }
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getRegion() {
        return region;
    }
    public void setRegion(String region) {
        this.region = region;
    }


    
}
