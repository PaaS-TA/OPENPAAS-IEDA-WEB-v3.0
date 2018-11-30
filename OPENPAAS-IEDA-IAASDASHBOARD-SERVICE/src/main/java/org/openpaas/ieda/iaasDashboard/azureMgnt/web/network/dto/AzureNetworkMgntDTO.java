package org.openpaas.ieda.iaasDashboard.azureMgnt.web.network.dto;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Repository;

@Repository
public class AzureNetworkMgntDTO {
	@NotNull
    private Integer accountId; // 계정 아이디
    @NotNull
    private String networkName;//network 명
    private String networkId;
    private String networkAddressSpaceCidr; //network address space
    private String subnetName;//Subnet 명
    private String subnetAddressRangeCidr; //Subnet address range
    private String resourceGroupId;
    private String resourceGroupName;
    private String location;
    
    public Integer getAccountId() {
		return accountId;
	}
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
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
	public String getNetworkAddressSpaceCidr() {
		return networkAddressSpaceCidr;
	}
	public void setNetworkAddressSpaceCidr(String networkAddressSpaceCidr) {
		this.networkAddressSpaceCidr = networkAddressSpaceCidr;
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
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
}
