package org.openpaas.ieda.openstackMgnt.web.network.dto;

public class OpenstackNetworkMgntDTO {
   private Integer accountId;
   private String networkName;
   private String networkId;
   private String subnetId;
   private String networkAddress;
   private String subnetName;
   private String gatewayIp;
   private String dnsNameServers;
   private boolean enableDHCP;
   private String ipVersion;
   private boolean adminState;
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
    public String getNetworkAddress() {
        return networkAddress;
    }
    public void setNetworkAddress(String networkAddress) {
        this.networkAddress = networkAddress;
    }
    public String getGatewayIp() {
        return gatewayIp;
    }
    public void setGatewayIp(String gatewayIp) {
        this.gatewayIp = gatewayIp;
    }
    public String getDnsNameServers() {
        return dnsNameServers;
    }
    public void setDnsNameServers(String dnsNameServers) {
        this.dnsNameServers = dnsNameServers;
    }
    public String getSubnetId() {
        return subnetId;
    }
    public void setSubnetId(String subnetId) {
        this.subnetId = subnetId;
    }
    public boolean isEnableDHCP() {
        return enableDHCP;
    }
    public void setEnableDHCP(boolean enableDHCP) {
        this.enableDHCP = enableDHCP;
    }
    public String getIpVersion() {
        return ipVersion;
    }
    public void setIpVersion(String ipVersion) {
        this.ipVersion = ipVersion;
    }
    public String getSubnetName() {
        return subnetName;
    }
    public void setSubnetName(String subnetName) {
        this.subnetName = subnetName;
    }
    public boolean isAdminState() {
        return adminState;
    }
    public void setAdminState(boolean adminState) {
        this.adminState = adminState;
    }
    public String getNetworkId() {
        return networkId;
    }
    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }
}
