package org.openpaas.ieda.iaasDashboard.azureMgnt.web.routeTable.dto;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Repository;
@Repository
public class AzureRouteTableMgntDTO {
    private Integer accountId; // 계정 아이디
    @NotNull
    private String routeTableName;
    private String routeTableId;
    private String networkName;
    private String subnetName;
    private String location;
    private String resourceGroupName;//리소스 그룹 명 
    private String subscriptionName;//그룹 명
    private String azureSubscriptionId;//구독id
    
	public Integer getAccountId() {
		return accountId;
	}
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
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
	public String getNetworkName() {
		return networkName;
	}
	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}
	public String getSubnetName() {
		return subnetName;
	}
	public void setSubnetName(String subnetName) {
		this.subnetName = subnetName;
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
}
