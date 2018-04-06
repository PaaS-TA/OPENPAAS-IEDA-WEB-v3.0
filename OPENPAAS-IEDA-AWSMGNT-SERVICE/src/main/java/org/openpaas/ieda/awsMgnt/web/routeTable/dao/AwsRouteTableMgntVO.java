package org.openpaas.ieda.awsMgnt.web.routeTable.dao;

import org.springframework.stereotype.Repository;

@Repository
public class AwsRouteTableMgntVO {
	private Integer accountId; // 계정 아이디
    private Integer recid;
    private Integer associationCnt;
    private String nameTag;
    private String routeTableId; //route Table ID
    private String destinationIpv4CidrBlock; //destination CIDR Block
    private String ipv6CidrBlock; //IPv6 CIDR Block 
    private String targetId;
    private String privateGatewayId; //
    private String subnetId; //
    private String vpcId;
    private String associationId;
    private String requestId;
    private String propagationId;
    private boolean check;
    private boolean mainYN;
    private boolean propagatedYN;
    private String status;
    
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
	public Integer getAssociationCnt() {
		return associationCnt;
	}
	public void setAssociationCnt(Integer associationCnt) {
		this.associationCnt = associationCnt;
	}
	public String getNameTag() {
		return nameTag;
	}
	public void setNameTag(String nameTag) {
		this.nameTag = nameTag;
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
	public String getIpv6CidrBlock() {
		return ipv6CidrBlock;
	}
	public void setIpv6CidrBlock(String ipv6CidrBlock) {
		this.ipv6CidrBlock = ipv6CidrBlock;
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
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getPropagationId() {
		return propagationId;
	}
	public void setPropagationId(String propagationId) {
		this.propagationId = propagationId;
	}
	public boolean isCheck() {
		return check;
	}
	public void setCheck(boolean check) {
		this.check = check;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
