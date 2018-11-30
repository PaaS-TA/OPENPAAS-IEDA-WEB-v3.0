package org.openpaas.ieda.iaasDashboard.azureMgnt.web.network.dao;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Repository;
@Repository
public class AzureNetworkMgntVO {
	@NotNull
    private Integer accountId; // 계정 아이디
	@NotNull
	private Integer recid;
    @NotNull
    private String networkName;//network 명
    private String networkId; //network id
    private String azureSubscriptionId;//구독id
    private String subscriptionName;//그룹 명
    private String location;//리전
    private String resourceGroupId;//리소스 그룹 id
    private String resourceGroupName;//리소스 그룹 명
    private String resourceType;
    private String networkAddressSpaceCidr; //network address range
    private String subnetName;//Subnet 명
    private String subnetAddressRangeCidr; //Subnet address range
    private Integer subnetAddressesCnt;
    private String securityGroupName; 
    
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
	public String getNetworkName() {
		return networkName;
	}
	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}
	public String getNetworkId() {
		return networkId;
	}
	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}
	public String getAzureSubscriptionId() {
		return azureSubscriptionId;
	}
	public void setAzureSubscriptionId(String azureSubscriptionId) {
		this.azureSubscriptionId = azureSubscriptionId;
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
	public String getResourceGroupId() {
		return resourceGroupId;
	}
	public void setResourceGroupId(String resourceGroupId) {
		this.resourceGroupId = resourceGroupId;
	}
	public String getResourceGroupName() {
		return resourceGroupName;
	}
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	public void setResourceGroupName(String resourceGroupName) {
		this.resourceGroupName = resourceGroupName;
	}
	public String getNetworkAddressSpaceCidr() {
		return networkAddressSpaceCidr;
	}
	public void setNetworkAddressSpaceCidr(String networkAddressSpaceCidr) {
		this.networkAddressSpaceCidr = networkAddressSpaceCidr;
	}
	public Integer getSubnetAddressesCnt() {
		return subnetAddressesCnt;
	}
	public void setSubnetAddressesCnt(Integer subnetAddressesCnt) {
		this.subnetAddressesCnt = subnetAddressesCnt;
	}
	public String getSecurityGroupName() {
		return securityGroupName;
	}
	public void setSecurityGroupName(String securityGroupName) {
		this.securityGroupName = securityGroupName;
	}
	public String getSubnetName() {
		return subnetName;
	}
	public void setSubnetName(String subnetName) {
		this.subnetName = subnetName;
	}
	public String getSubnetAddressRangeCidr() {
		return subnetAddressRangeCidr;
	}
	public void setSubnetAddressRangeCidr(String subnetAddressRangeCidr) {
		this.subnetAddressRangeCidr = subnetAddressRangeCidr;
	}
}
