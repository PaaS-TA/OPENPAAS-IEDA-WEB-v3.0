package org.openpaas.ieda.awsMgnt.web.natGateway.dao;

import org.springframework.stereotype.Repository;

@Repository
public class AwsNatGatewayMgntVO {
	private Integer accountId; // 계정 아이디
    private Integer recid;
    private String nameTag;
    private String natGatewayId; //NAT Gateway ID
    private String natGatewayAddress; //NAT Gateway ID
    private String subnetId; //
    private String vpcId;
    private String state; //status
    private String publicIp; //Elastic IP Address
    private String privateIp; //Private IP Address
    private String networkInterfaceId;
    private String allocationId;//NAT Gateway와 사용되는 Elastic Ip의 할당 아이디
    private String createdTime;
    
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
	public String getNameTag() {
		return nameTag;
	}
	public void setNameTag(String nameTag) {
		this.nameTag = nameTag;
	}
	public String getNatGatewayId() {
		return natGatewayId;
	}
	public void setNatGatewayId(String natGatewayId) {
		this.natGatewayId = natGatewayId;
	}
	public String getNatGatewayAddress() {
		return natGatewayAddress;
	}
	public void setNatGatewayAddress(String natGatewayAddress) {
		this.natGatewayAddress = natGatewayAddress;
	}
	public String getSubnetId() {
		return subnetId;
	}
	public void setSubnetId(String subnetId) {
		this.subnetId = subnetId;
	}
	public String getVpcId() {
		return vpcId;
	}
	public void setVpcId(String vpcId) {
		this.vpcId = vpcId;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPublicIp() {
		return publicIp;
	}
	public void setPublicIp(String publicIp) {
		this.publicIp = publicIp;
	}
	public String getPrivateIp() {
		return privateIp;
	}
	public void setPrivateIp(String privateIp) {
		this.privateIp = privateIp;
	}
	public String getNetworkInterfaceId() {
		return networkInterfaceId;
	}
	public void setNetworkInterfaceId(String networkInterfaceId) {
		this.networkInterfaceId = networkInterfaceId;
	}
	public String getAllocationId() {
		return allocationId;
	}
	public void setAllocationId(String allocationId) {
		this.allocationId = allocationId;
	}
	public String getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
}
