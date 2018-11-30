package org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao;

import java.util.Date;

import javax.validation.constraints.NotNull;

public class HbCfDeploymentNetworkConfigVO {
    @NotNull
    private Integer id;
    @NotNull
    private Integer recid;
    private Integer seq;
    private String iaasType;
    private String networkName;
    private String publicStaticIp;
    private String subnetReservedIp;
    private String subnetStaticIp;
    private String availabilityZone;
    private String securityGroup;
    private String subnetRange;
    private String subnetDns;
    private String subnetId;
    private String subnetGateway;
    
    private String subnetId1;
    private String securityGroup1;
    private String subnetRange1;
    private String subnetGateway1;
    private String subnetDns1;
    private String subnetReservedFrom1;
    private String subnetReservedTo1;
    private String subnetStaticFrom1;
    private String subnetStaticTo1;
    private String availabilityZone1;
    private String subnetId2;
    private String securityGroup2;
    private String subnetRange2;
    private String subnetGateway2;
    private String subnetDns2;
    private String subnetReservedFrom2;
    private String subnetReservedTo2;
    private String subnetStaticFrom2;
    private String subnetStaticTo2;
    private String availabilityZone2;
    private String createUserId;//등록자 아이디
    private String updateUserId;//수정자 아이디
    private Date createDate;//등록일
    private Date updateDate;//수정일
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getRecid() {
        return recid;
    }
    public void setRecid(Integer recid) {
        this.recid = recid;
    }
    public String getIaasType() {
        return iaasType;
    }
    public void setIaasType(String iaasType) {
        this.iaasType = iaasType;
    }
    public String getNetworkName() {
        return networkName;
    }
    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }
    public String getPublicStaticIp() {
        return publicStaticIp;
    }
    public void setPublicStaticIp(String publicStaticIp) {
        this.publicStaticIp = publicStaticIp;
    }
    public String getSubnetReservedIp() {
        return subnetReservedIp;
    }
    public void setSubnetReservedIp(String subnetReservedIp) {
        this.subnetReservedIp = subnetReservedIp;
    }
    public String getSubnetStaticIp() {
        return subnetStaticIp;
    }
    public void setSubnetStaticIp(String subnetStaticIp) {
        this.subnetStaticIp = subnetStaticIp;
    }
    public String getAvailabilityZone() {
        return availabilityZone;
    }
    public void setAvailabilityZone(String availabilityZone) {
        this.availabilityZone = availabilityZone;
    }
    public String getSubnetId1() {
        return subnetId1;
    }
    public void setSubnetId1(String subnetId1) {
        this.subnetId1 = subnetId1;
    }
    public String getSecurityGroup1() {
        return securityGroup1;
    }
    public void setSecurityGroup1(String securityGroup1) {
        this.securityGroup1 = securityGroup1;
    }
    public String getSubnetRange1() {
        return subnetRange1;
    }
    public void setSubnetRange1(String subnetRange1) {
        this.subnetRange1 = subnetRange1;
    }
    public String getSubnetGateway1() {
        return subnetGateway1;
    }
    public void setSubnetGateway1(String subnetGateway1) {
        this.subnetGateway1 = subnetGateway1;
    }
    public String getSubnetDns1() {
        return subnetDns1;
    }
    public void setSubnetDns1(String subnetDns1) {
        this.subnetDns1 = subnetDns1;
    }
    public String getSubnetReservedFrom1() {
        return subnetReservedFrom1;
    }
    public void setSubnetReservedFrom1(String subnetReservedFrom1) {
        this.subnetReservedFrom1 = subnetReservedFrom1;
    }
    public String getSubnetReservedTo1() {
        return subnetReservedTo1;
    }
    public void setSubnetReservedTo1(String subnetReservedTo1) {
        this.subnetReservedTo1 = subnetReservedTo1;
    }
    public String getSubnetStaticFrom1() {
        return subnetStaticFrom1;
    }
    public void setSubnetStaticFrom1(String subnetStaticFrom1) {
        this.subnetStaticFrom1 = subnetStaticFrom1;
    }
    public String getSubnetStaticTo1() {
        return subnetStaticTo1;
    }
    public void setSubnetStaticTo1(String subnetStaticTo1) {
        this.subnetStaticTo1 = subnetStaticTo1;
    }
    public String getAvailabilityZone1() {
        return availabilityZone1;
    }
    public void setAvailabilityZone1(String availabilityZone1) {
        this.availabilityZone1 = availabilityZone1;
    }
    public String getSubnetId2() {
        return subnetId2;
    }
    public void setSubnetId2(String subnetId2) {
        this.subnetId2 = subnetId2;
    }
    public String getSecurityGroup2() {
        return securityGroup2;
    }
    public void setSecurityGroup2(String securityGroup2) {
        this.securityGroup2 = securityGroup2;
    }
    public String getSubnetRange2() {
        return subnetRange2;
    }
    public void setSubnetRange2(String subnetRange2) {
        this.subnetRange2 = subnetRange2;
    }
    public String getSubnetGateway2() {
        return subnetGateway2;
    }
    public void setSubnetGateway2(String subnetGateway2) {
        this.subnetGateway2 = subnetGateway2;
    }
    public String getSubnetDns2() {
        return subnetDns2;
    }
    public void setSubnetDns2(String subnetDns2) {
        this.subnetDns2 = subnetDns2;
    }
    public String getSubnetReservedFrom2() {
        return subnetReservedFrom2;
    }
    public void setSubnetReservedFrom2(String subnetReservedFrom2) {
        this.subnetReservedFrom2 = subnetReservedFrom2;
    }
    public String getSubnetReservedTo2() {
        return subnetReservedTo2;
    }
    public void setSubnetReservedTo2(String subnetReservedTo2) {
        this.subnetReservedTo2 = subnetReservedTo2;
    }
    public String getSubnetStaticFrom2() {
        return subnetStaticFrom2;
    }
    public void setSubnetStaticFrom2(String subnetStaticFrom2) {
        this.subnetStaticFrom2 = subnetStaticFrom2;
    }
    public String getSubnetStaticTo2() {
        return subnetStaticTo2;
    }
    public void setSubnetStaticTo2(String subnetStaticTo2) {
        this.subnetStaticTo2 = subnetStaticTo2;
    }
    public String getAvailabilityZone2() {
        return availabilityZone2;
    }
    public void setAvailabilityZone2(String availabilityZone2) {
        this.availabilityZone2 = availabilityZone2;
    }
    public Integer getSeq() {
        return seq;
    }
    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String getCreateUserId() {
        return createUserId;
    }
    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }
    public String getUpdateUserId() {
        return updateUserId;
    }
    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }
    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    public Date getUpdateDate() {
        return updateDate;
    }
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
    public String getSecurityGroup() {
        return securityGroup;
    }
    public void setSecurityGroup(String securityGroup) {
        this.securityGroup = securityGroup;
    }
    public String getSubnetRange() {
        return subnetRange;
    }
    public void setSubnetRange(String subnetRange) {
        this.subnetRange = subnetRange;
    }
    public String getSubnetDns() {
        return subnetDns;
    }
    public void setSubnetDns(String subnetDns) {
        this.subnetDns = subnetDns;
    }
    public String getSubnetId() {
        return subnetId;
    }
    public void setSubnetId(String subnetId) {
        this.subnetId = subnetId;
    }
    public String getSubnetGateway() {
        return subnetGateway;
    }
    public void setSubnetGateway(String subnetGateway) {
        this.subnetGateway = subnetGateway;
    }
}
