package org.openpaas.ieda.iaasDashboard.azureMgnt.web.storageAccount.dto;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Repository;
@Repository
public class AzureStorageAccountMgntDTO {
	
	@NotNull
    private Integer accountId; // 계정 아이디
    @NotNull
    private String storageAccountName;// 스토리지 계정 명
    private String storageAccountId;// 스토리지 계정 아이디
    private String blobName;
    private String blobId;
    private String publicAccessType;
    private String subscriptionName;//구독 명
    private String azureSubscriptionId;//구독id
    private String resourceGroupName;//리소스 그룹 id
    private String resourceGroupId;//리소스 그룹 id
    private String location;//리전 명
    private String tableName;
	
	public Integer getAccountId() {
		return accountId;
	}
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	public String getStorageAccountName() {
		return storageAccountName;
	}
	public void setStorageAccountName(String storageAccountName) {
		this.storageAccountName = storageAccountName;
	}
	public String getStorageAccountId() {
		return storageAccountId;
	}
	public void setStorageAccountId(String storageAccountId) {
		this.storageAccountId = storageAccountId;
	}
	public String getBlobName() {
		return blobName;
	}
	public void setBlobName(String blobName) {
		this.blobName = blobName;
	}
	public String getBlobId() {
		return blobId;
	}
	public void setBlobId(String blobId) {
		this.blobId = blobId;
	}
	public String getPublicAccessType() {
		return publicAccessType;
	}
	public void setPublicAccessType(String publicAccessType) {
		this.publicAccessType = publicAccessType;
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
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
}
