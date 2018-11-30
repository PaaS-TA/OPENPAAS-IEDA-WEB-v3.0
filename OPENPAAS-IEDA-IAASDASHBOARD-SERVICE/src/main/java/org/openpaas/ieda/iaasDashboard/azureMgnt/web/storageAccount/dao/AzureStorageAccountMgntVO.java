package org.openpaas.ieda.iaasDashboard.azureMgnt.web.storageAccount.dao;

import javax.validation.constraints.NotNull;

public class AzureStorageAccountMgntVO {
	@NotNull
	private Integer recid;
	private Integer accountId; // 계정 아이디
    @NotNull
    private String storageAccountName;// 스토리지 계정 명
    private String storageAccountId;// 스토리지 계정 아이디
    private String blobName;
    private String blobId;
    private String subscriptionName;//구독 명
    private String azureSubscriptionId;//구독id
    private String resourceGroupName;//리소스 그룹 id
    private String resourceGroupId;//리소스 그룹 id
    private String location;//리전 명
    private String accountType;
    private String blobServiceEndpoint1;
    private String blobServiceEndpoint2;
    private String status;
    private String lastSync;
    private String publicAccessLevel;
    private String leaseState;
    private String etag;
    private String lastModified;
    private String tableName;
    private String tableUrl;
    
    
    
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
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getBlobServiceEndpoint1() {
		return blobServiceEndpoint1;
	}
	public void setBlobServiceEndpoint1(String blobServiceEndpoint1) {
		this.blobServiceEndpoint1 = blobServiceEndpoint1;
	}
	public String getBlobServiceEndpoint2() {
		return blobServiceEndpoint2;
	}
	public void setBlobServiceEndpoint2(String blobServiceEndpoint2) {
		this.blobServiceEndpoint2 = blobServiceEndpoint2;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getLastSync() {
		return lastSync;
	}
	public void setLastSync(String lastSync) {
		this.lastSync = lastSync;
	}
	public String getPublicAccessLevel() {
		return publicAccessLevel;
	}
	public void setPublicAccessLevel(String publicAccessLevel) {
		this.publicAccessLevel = publicAccessLevel;
	}
	public String getLeaseState() {
		return leaseState;
	}
	public void setLeaseState(String leaseState) {
		this.leaseState = leaseState;
	}
	public String getEtag() {
		return etag;
	}
	public void setEtag(String etag) {
		this.etag = etag;
	}
	public String getLastModified() {
		return lastModified;
	}
	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getTableUrl() {
		return tableUrl;
	}
	public void setTableUrl(String tableUrl) {
		this.tableUrl = tableUrl;
	}
	
}
