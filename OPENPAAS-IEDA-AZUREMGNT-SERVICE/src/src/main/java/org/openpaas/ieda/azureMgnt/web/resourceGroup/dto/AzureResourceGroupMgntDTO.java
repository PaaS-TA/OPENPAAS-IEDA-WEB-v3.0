package org.openpaas.ieda.azureMgnt.web.resourceGroup.dto;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Repository;
@Repository
public class AzureResourceGroupMgntDTO {
	@NotNull
    private Integer accountId; // 계정 아이디
    @NotNull
    private String name;//그룹 명
    private String subscriptionName;//구독 명
    private String location;//리전 명
    private String rglocation;
    private String resourceGroupId;//리소스 그룹 id
    private String status;//상태
    private String azureSubscriptionId;//구독id
    private String type;
    private String depolyments;
	public Integer getAccountId() {
		return accountId;
	}
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSubscriptionName() {
		return subscriptionName;
	}
	public void setSubscriptionName(String subscriptionName) {
		this.subscriptionName = subscriptionName;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getRglocation() {
		return rglocation;
	}
	public void setRglocation(String rglocation) {
		this.rglocation = rglocation;
	}
	public String getResourceGroupId() {
		return resourceGroupId;
	}
	public void setResourceGroupId(String resourceGroupId) {
		this.resourceGroupId = resourceGroupId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAzureSubscriptionId() {
		return azureSubscriptionId;
	}
	public void setAzureSubscriptionId(String azureSubscriptionId) {
		this.azureSubscriptionId = azureSubscriptionId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDepolyments() {
		return depolyments;
	}
	public void setDepolyments(String depolyments) {
		this.depolyments = depolyments;
	}
}
