package org.openpaas.ieda.awsMgnt.web.routeTable.dto;

import org.springframework.stereotype.Repository;

@Repository
public class AwsRouteTableMgntDTO {
	private Integer accountId; // 계정 아이디
    private Integer recid;
    private String nameTag;
    private String region;
    private String routeTableId; //route Table ID
    private String destinationIpv4CidrBlock; //destination CIDR Block
    private String targetId;
    private String privateGatewayId; //
    private String subnetId; //
    private String vpcId;
    private String associationId;
    private String propagationId;
    private boolean mainYN;
    private boolean propagatedYN;
    
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
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getRouteTableId() {
		return routeTableId;
	}
	public void setRouteTableId(String routeTableId) {
		this.routeTableId = routeTableId;
	}
	public String getDestinationIpv4CidrBlock() {
		return destinationIpv4CidrBlock;
	}
	public void setDestinationIpv4CidrBlock(String destinationIpv4CidrBlock) {
		this.destinationIpv4CidrBlock = destinationIpv4CidrBlock;
	}
	public String getTargetId() {
		return targetId;
	}
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	public String getPrivateGatewayId() {
		return privateGatewayId;
	}
	public void setPrivateGatewayId(String privateGatewayId) {
		this.privateGatewayId = privateGatewayId;
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
	public String getAssociationId() {
		return associationId;
	}
	public void setAssociationId(String associationId) {
		this.associationId = associationId;
	}
	public String getPropagationId() {
		return propagationId;
	}
	public void setPropagationId(String propagationId) {
		this.propagationId = propagationId;
	}
	public boolean isMainYN() {
		return mainYN;
	}
	public void setMainYN(boolean mainYN) {
		this.mainYN = mainYN;
	}
	public boolean isPropagatedYN() {
		return propagatedYN;
	}
	public void setPropagatedYN(boolean propagatedYN) {
		this.propagatedYN = propagatedYN;
	}
}
