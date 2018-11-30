package org.openpaas.ieda.iaasDashboard.azureMgnt.web.routeTable.dao;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Repository;
@Repository
public class AzureRouteTableMgntVO {
	@NotNull
	private Integer recid;
    private Integer accountId; // 계정 아이디
    private Integer associations; // 계정 아이디
    @NotNull
    private String routeTableName;//routeTable 명
    private String routeTableId;
    private String location;
    private String resourceGroupName;//리소스 그룹 명 
    private String resourceGroupId;//리소스 그룹 id
    private String subscriptionName;//그룹 명
    private String azureSubscriptionId;//구독id
    private String subnetName;
    private String subnetAddressRange;
    private String networkName;
    private String securityGroupName;
    
	public Integer getRecid() {
		return recid;
	}
	public void setRecid(Integer recid) {
		this.recid = recid;
	}
	public Integer getAccountId() {
		return accountId;
	}
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	public Integer getAssociations() {
		return associations;
	}
	public void setAssociations(Integer associations) {
		this.associations = associations;
	}
	public String getRouteTableName() {
		return routeTableName;
	}
	public void setRouteTableName(String routeTableName) {
		this.routeTableName = routeTableName;
	}
	public String getRouteTableId() {
		return routeTableId;
	}
	public void setRouteTableId(String routeTableId) {
		this.routeTableId = routeTableId;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getResourceGroupName() {
		return resourceGroupName;
	}
	public void setResourceGroupName(String resourceGroupName) {
		this.resourceGroupName = resourceGroupName;
	}
	public String getResourceGroupId() {
		return resourceGroupId;
	}
	public void setResourceGroupId(String resourceGroupId) {
		this.resourceGroupId = resourceGroupId;
	}
	public String getSubscriptionName() {
		return subscriptionName;
	}
	public void setSubscriptionName(String subscriptionName) {
		this.subscriptionName = subscriptionName;
	}
	public String getAzureSubscriptionId() {
		return azureSubscriptionId;
	}
	public void setAzureSubscriptionId(String azureSubscriptionId) {
		this.azureSubscriptionId = azureSubscriptionId;
	}
	public String getSubnetName() {
		return subnetName;
	}
	public void setSubnetName(String subnetName) {
		this.subnetName = subnetName;
	}
	public String getSubnetAddressRange() {
		return subnetAddressRange;
	}
	public void setSubnetAddressRange(String subnetAddressRange) {
		this.subnetAddressRange = subnetAddressRange;
	}
	public String getNetworkName() {
		return networkName;
	}
	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}
	public String getSecurityGroupName() {
		return securityGroupName;
	}
	public void setSecurityGroupName(String securityGroupName) {
		this.securityGroupName = securityGroupName;
	}
}
