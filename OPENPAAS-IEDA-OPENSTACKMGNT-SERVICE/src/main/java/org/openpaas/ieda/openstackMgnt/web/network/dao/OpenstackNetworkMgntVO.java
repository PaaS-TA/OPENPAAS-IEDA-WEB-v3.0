package org.openpaas.ieda.openstackMgnt.web.network.dao;

public class OpenstackNetworkMgntVO {
    
    private String networkId;
    private Integer recid;
    private String networkName;
    private String status;
    private boolean routerExternal;
    private boolean adminStateUp;
    private boolean shared;
    private String cidrIpv4;
    private String subnetName;
    private String subnetId;
    private String tenantId;
    private Integer accountId;
    private String providerNetwork;
    private String segId;
    private String networkType;
    private String allocationPools;
    private String gatewayIp;
    private String ipVersion;
    private boolean dhcpEnabled;
    private String dnsName;
    private String routeDestination;

    public String getNetworkId() {
        return networkId;
    }
    public void setNetworkId(String networkId) {
        this.networkId = networkId;
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
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public boolean isRouterExternal() {
        return routerExternal;
    }
    public void setRouterExternal(boolean routerExternal) {
        this.routerExternal = routerExternal;
    }
    public boolean isAdminStateUp() {
        return adminStateUp;
    }
    public void setAdminStateUp(boolean adminStateUp) {
        this.adminStateUp = adminStateUp;
    }
    public boolean isShared() {
        return shared;
    }
    public void setShared(boolean shared) {
        this.shared = shared;
    }
    public String getCidrIpv4() {
        return cidrIpv4;
    }
    public void setCidrIpv4(String cidrIpv4) {
        this.cidrIpv4 = cidrIpv4;
    }
    public String getSubnetName() {
        return subnetName;
    }
    public void setSubnetName(String subnetName) {
        this.subnetName = subnetName;
    }
    public String getTenantId() {
        return tenantId;
    }
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    public Integer getAccountId() {
        return accountId;
    }
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
    public String getProviderNetwork() {
        return providerNetwork;
    }
    public void setProviderNetwork(String providerNetwork) {
        this.providerNetwork = providerNetwork;
    }
    public String getSegId() {
        return segId;
    }
    public void setSegId(String segId) {
        this.segId = segId;
    }
    public String getNetworkType() {
        return networkType;
    }
    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }
    public String getAllocationPools() {
        return allocationPools;
    }
    public void setAllocationPools(String allocationPools) {
        this.allocationPools = allocationPools;
    }
    public String getGatewayIp() {
        return gatewayIp;
    }
    public void setGatewayIp(String gatewayIp) {
        this.gatewayIp = gatewayIp;
    }
    public String getIpVersion() {
        return ipVersion;
    }
    public void setIpVersion(String ipVersion) {
        this.ipVersion = ipVersion;
    }
    public boolean isDhcpEnabled() {
        return dhcpEnabled;
    }
    public void setDhcpEnabled(boolean dhcpEnabled) {
        this.dhcpEnabled = dhcpEnabled;
    }
    public String getDnsName() {
        return dnsName;
    }
    public void setDnsName(String dnsName) {
        this.dnsName = dnsName;
    }
    public String getRouteDestination() {
        return routeDestination;
    }
    public void setRouteDestination(String routeDestination) {
        this.routeDestination = routeDestination;
    }
    public String getSubnetId() {
        return subnetId;
    }
    public void setSubnetId(String subnetId) {
        this.subnetId = subnetId;
    }
}
