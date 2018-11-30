package org.openpaas.ieda.iaasDashboard.awsMgnt.web.vpc.dao;

public class AwsVpcMgntVO {
    
    private Integer accountId; // 계정 아이디
    private Integer recid;
    private String vpcId; // VPC 아이디
    private String nameTag; // VPC 태그 명
    private String ipv4CidrBlock; // CIDR 블록 형태의 VPC IPv4 주소 범위
    private String ipv6CidrBlock; // IPv6 CIDR 블록 사용 여
    private String tenancy; //공유 또는 전용 인스턴스 사용 여부
    private String status;
    private String dhcpOptionSet;
    private boolean dnsHostNames;
    private boolean dnsResolution;
    private boolean classicLinkDns;
    private String routeTable;
    private String networkAcle;
    private boolean defaultVpc;
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
    public String getVpcId() {
        return vpcId;
    }
    public void setVpcId(String vpcId) {
        this.vpcId = vpcId;
    }
    public String getNameTag() {
        return nameTag;
    }
    public void setNameTag(String nameTag) {
        this.nameTag = nameTag;
    }
    public String getIpv4CidrBlock() {
        return ipv4CidrBlock;
    }
    public void setIpv4CidrBlock(String ipv4CidrBlock) {
        this.ipv4CidrBlock = ipv4CidrBlock;
    }
    public String getIpv6CidrBlock() {
        return ipv6CidrBlock;
    }
    public void setIpv6CidrBlock(String ipv6CidrBlock) {
        this.ipv6CidrBlock = ipv6CidrBlock;
    }
    public String getTenancy() {
        return tenancy;
    }
    public void setTenancy(String tenancy) {
        this.tenancy = tenancy;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getDhcpOptionSet() {
        return dhcpOptionSet;
    }
    public void setDhcpOptionSet(String dhcpOptionSet) {
        this.dhcpOptionSet = dhcpOptionSet;
    }
    public String getRouteTable() {
        return routeTable;
    }
    public void setRouteTable(String routeTable) {
        this.routeTable = routeTable;
    }
    public String getNetworkAcle() {
        return networkAcle;
    }
    public void setNetworkAcle(String networkAcle) {
        this.networkAcle = networkAcle;
    }
    public boolean isDefaultVpc() {
        return defaultVpc;
    }
    public void setDefaultVpc(boolean defaultVpc) {
        this.defaultVpc = defaultVpc;
    }
    public boolean isDnsHostNames() {
        return dnsHostNames;
    }
    public void setDnsHostNames(boolean dnsHostNames) {
        this.dnsHostNames = dnsHostNames;
    }
    public boolean isDnsResolution() {
        return dnsResolution;
    }
    public void setDnsResolution(boolean dnsResolution) {
        this.dnsResolution = dnsResolution;
    }
    public boolean isClassicLinkDns() {
        return classicLinkDns;
    }
    public void setClassicLinkDns(boolean classicLinkDns) {
        this.classicLinkDns = classicLinkDns;
    }
    
}
