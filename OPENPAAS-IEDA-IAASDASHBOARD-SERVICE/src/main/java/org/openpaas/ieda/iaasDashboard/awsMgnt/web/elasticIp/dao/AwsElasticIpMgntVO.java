package org.openpaas.ieda.iaasDashboard.awsMgnt.web.elasticIp.dao;

public class AwsElasticIpMgntVO {
    
    private Integer accountId; // 계정 아이디
    private Integer recid;
    private String publicIp; //Elastic IP Address
    private String allocationId;//VPC인스턴스 와 사용되는 Elastic Ip의 할당 아이디
    private String domain; //Elastic IP 주소의 use with instances in EC2-classic(standard) or VPC (vpc) 여부 
    private String requestId; 
    private String associationId;
    private String networkInterfaceId;
    private String networkInterfaceOwner;
    private String instanceId;
    private String publicDns;
    private String privateIpAddress;
    
    
    public Integer getAccountId() {
        return accountId;
    }
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
    public Integer getRecid() {
        return recid;
    }
    public void setRecid(Integer recid) {
        this.recid = recid;
    }
    public String getPublicIp() {
        return publicIp;
    }
    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }
    public String getAllocationId() {
        return allocationId;
    }
    public void setAllocationId(String allocationId) {
        this.allocationId = allocationId;
    }
    public String getDomain() {
        return domain;
    }
    public void setDomain(String domain) {
        this.domain = domain;
    }
    public String getRequestId() {
        return requestId;
    }
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    public String getAssociationId() {
        return associationId;
    }
    public void setAssociationId(String associationId) {
        this.associationId = associationId;
    }
    public String getNetworkInterfaceId() {
        return networkInterfaceId;
    }
    public void setNetworkInterfaceId(String networkInterfaceId) {
        this.networkInterfaceId = networkInterfaceId;
    }
    public String getNetworkInterfaceOwner() {
        return networkInterfaceOwner;
    }
    public void setNetworkInterfaceOwner(String networkInterfaceOwner) {
        this.networkInterfaceOwner = networkInterfaceOwner;
    }
    public String getInstanceId() {
        return instanceId;
    }
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
    public String getPublicDns() {
        return publicDns;
    }
    public void setPublicDns(String publicDns) {
        this.publicDns = publicDns;
    }
    public String getPrivateIpAddress() {
        return privateIpAddress;
    }
    public void setPrivateIpAddress(String privateIpAddress) {
        this.privateIpAddress = privateIpAddress;
    }
}