package org.openpaas.ieda.awsMgnt.web.keypair.dto;

public class AwsKeypairMgntDTO {
    private String accountId; // 계정 아이디
    private String keyPairName; // Key Pair 이름
    
    public String getAccountId() {
        return accountId;
    }
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    public String getKeyPairName() {
        return keyPairName;
    }
    public void setKeyPairName(String keyPairName) {
        this.keyPairName = keyPairName;
    }
}
