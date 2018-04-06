package org.openpaas.ieda.azureMgnt.web.network.dao;

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
    private String azureSubscriptionId;//구독id
    private String subscriptionName;//그룹 명
    private String location;//그룹 명
    private String resourceGroupId;//리소스 그룹 id
    private String resourceGroupName;//리소스 그룹 명
    private String networkAddressRangeCidr; //network address range
    private String subnetName;//Subnet 명
    private String subnetAddressRangeCidr; //Subnet address range
    private String dnsServer; //dns server
    private String deviceName; 
    private String deviceType;
    private String deviceIpAddress;
    private String deviceSubnet;
    
    
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
	public void setResourceGroupName(String resourceGroupName) {
		this.resourceGroupName = resourceGroupName;
	}
	public String getNetworkAddressRangeCidr() {
		return networkAddressRangeCidr;
	}
	public void setNetworkAddressRangeCidr(String networkAddressRangeCidr) {
		this.networkAddressRangeCidr = networkAddressRangeCidr;
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
	public String getDnsServer() {
		return dnsServer;
	}
	public void setDnsServer(String dnsServer) {
		this.dnsServer = dnsServer;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getDeviceIpAddress() {
		return deviceIpAddress;
	}
	public void setDeviceIpAddress(String deviceIpAddress) {
		this.deviceIpAddress = deviceIpAddress;
	}
	public String getDeviceSubnet() {
		return deviceSubnet;
	}
	public void setDeviceSubnet(String deviceSubnet) {
		this.deviceSubnet = deviceSubnet;
	}
}
