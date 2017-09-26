package org.openpaas.ieda.awsMgnt.web.internetGateway.dao;

public class AwsInternetGatewayMgntVO {
    private String internetGatewayName;
    private Integer recid;
    private String status;
    private String internetGatewayId;
    private String vpcId;
    private Integer accountId;
    
    
    public String getInternetGatewayName() {
        return internetGatewayName;
    }
    public void setInternetGatewayName(String internetGatewayName) {
        this.internetGatewayName = internetGatewayName;
    }
    public Integer getRecid() {
        return recid;
    }
    public void setRecid(Integer recid) {
        this.recid = recid;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getInternetGatewayId() {
        return internetGatewayId;
    }
    public void setInternetGatewayId(String internetGatewayId) {
        this.internetGatewayId = internetGatewayId;
    }
    public String getVpcId() {
        return vpcId;
    }
    public void setVpcId(String vpcId) {
        this.vpcId = vpcId;
    }
    public Integer getAccountId() {
        return accountId;
    }
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
}
