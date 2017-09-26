package org.openpaas.ieda.openstackMgnt.web.keypairs.dto;

public class OpenstackKeypairsMgntDTO {
    private Integer accountId;
    private String keypairsName;
    public Integer getAccountId() {
        return accountId;
    }
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
    public String getKeypairsName() {
        return keypairsName;
    }
    public void setKeypairsName(String keypairsName) {
        this.keypairsName = keypairsName;
    }
}
