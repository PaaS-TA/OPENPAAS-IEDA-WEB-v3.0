package org.openpaas.ieda.iaasDashboard.awsMgnt.web.subnet.dao;

public class AwsSubnetMgntVO{
    private Integer accountId;
    private Integer recid;
    private String subnetId;
    private String nameTag;
    private String vpcId;
    private String availabilityZone;
    private String cidrBlock;
    private String state;
    private Integer availableIpAddressCount;
    private boolean mapPublicIpOnLaunch;
    private boolean defaultForAz; 
    private boolean assignIpv6AddressOnCreation;
    private String associationId;
    private String ipv6CidrBlock;
    
    
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
    public String getSubnetId() {
        return subnetId;
    }
    public void setSubnetId(String subnetId) {
        this.subnetId = subnetId;
    }
    public String getNameTag() {
        return nameTag;
    }
    public void setNameTag(String nameTag) {
        this.nameTag = nameTag;
    }
    public String getVpcId() {
        return vpcId;
    }
    public void setVpcId(String vpcId) {
        this.vpcId = vpcId;
    }
    public String getAvailabilityZone() {
        return availabilityZone;
    }
    public void setAvailabilityZone(String availabilityZone) {
        this.availabilityZone = availabilityZone;
    }
    public String getCidrBlock() {
        return cidrBlock;
    }
    public void setCidrBlock(String cidrBlock) {
        this.cidrBlock = cidrBlock;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public Integer getAvailableIpAddressCount() {
        return availableIpAddressCount;
    }
    public void setAvailableIpAddressCount(Integer availableIpAddressCount) {
        this.availableIpAddressCount = availableIpAddressCount;
    }
    public boolean isMapPublicIpOnLaunch() {
        return mapPublicIpOnLaunch;
    }
    public void setMapPublicIpOnLaunch(boolean mapPublicIpOnLaunch) {
        this.mapPublicIpOnLaunch = mapPublicIpOnLaunch;
    }
    public boolean isDefaultForAz() {
        return defaultForAz;
    }
    public void setDefaultForAz(boolean defaultForAz) {
        this.defaultForAz = defaultForAz;
    }
    public boolean isAssignIpv6AddressOnCreation() {
        return assignIpv6AddressOnCreation;
    }
    public void setAssignIpv6AddressOnCreation(boolean assignIpv6AddressOnCreation) {
        this.assignIpv6AddressOnCreation = assignIpv6AddressOnCreation;
    }
    public String getAssociationId() {
        return associationId;
    }
    public void setAssociationId(String associationId) {
        this.associationId = associationId;
    }
    public String getIpv6CidrBlock() {
        return ipv6CidrBlock;
    }
    public void setIpv6CidrBlock(String ipv6CidrBlock) {
        this.ipv6CidrBlock = ipv6CidrBlock;
    }
    
    
    
}