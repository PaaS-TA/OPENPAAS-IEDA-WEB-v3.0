package org.openpaas.ieda.azureMgnt.web.network.dto;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Repository;

@Repository
public class AzureNetworkMgntDTO {
	@NotNull
    private Integer accountId; // 계정 아이디
    @NotNull
    private String networkName;//network 명
    private String networkAddressRangeCidr; //network address range
    private String subnetName;//Subnet 명
    private String subnetAddressRangeCidr; //Subnet address range
	
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
}
