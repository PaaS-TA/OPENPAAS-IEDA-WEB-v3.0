package org.openpaas.ieda.openstackMgnt.web.securityGroup.dao;

public class OpenstackSecurityGroupMgntVO {
    private Integer recid;
    private String securityGroupId;
    private String securityGroupName;
    private String description;
    private Integer accountId;
    
    public Integer getRecid() {
        return recid;
    }
    public void setRecid(Integer recid) {
        this.recid = recid;
    }
    public String getSecurityGroupId() {
        return securityGroupId;
    }
    public void setSecurityGroupId(String securityGroupId) {
        this.securityGroupId = securityGroupId;
    }
    public String getSecurityGroupName() {
        return securityGroupName;
    }
    public void setSecurityGroupName(String securityGroupName) {
        this.securityGroupName = securityGroupName;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Integer getAccountId() {
        return accountId;
    }
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
}
