package org.openpaas.ieda.iaasDashboard.awsMgnt.web.internetGateway.dto;

public class AwsInternetGatewayMgntDTO {
    private Integer accountId;
    private String internetGatewayName;
    private String internetGatewayId;
    private String vpcId;
    private String region;
    
    public Integer getAccountId() {
        return accountId;
    }
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
    public String getInternetGatewayName() {
        return internetGatewayName;
    }
    public void setInternetGatewayName(String internetGatewayName) {
        this.internetGatewayName = internetGatewayName;
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
    public String getRegion() {
        return region;
    }
    public void setRegion(String region) {
        this.region = region;
    }
}
