package org.openpaas.ieda.iaasDashboard.azureMgnt.web.publicIp.dto;

import javax.validation.constraints.NotNull;

public class AzurePublicIpMgntDTO {
	@NotNull
	private Integer recid;
	private Integer accountId; // 계정 아이디
	@NotNull
    private String publicIpName; // public IP 명
    private String publicIpAddress;
    private String subscriptionName;//구독 명
    private String azureSubscriptionId;//구독 아디이
    private String resourceGroupName;//리소스 그룹 
    private String resourceGroupId;//리소스 그룹 아디이
    private String location; //리전
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
	public String getPublicIpName() {
		return publicIpName;
	}
	public void setPublicIpName(String publicIpName) {
		this.publicIpName = publicIpName;
	}
	public String getPublicIpAddress() {
		return publicIpAddress;
	}
	public void setPublicIpAddress(String publicIpAddress) {
		this.publicIpAddress = publicIpAddress;
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
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
}
