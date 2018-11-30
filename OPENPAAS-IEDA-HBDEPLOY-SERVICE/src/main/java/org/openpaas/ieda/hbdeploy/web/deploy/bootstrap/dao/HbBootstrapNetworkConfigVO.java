package org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao;

import java.util.Date;

public class HbBootstrapNetworkConfigVO {
    
    private Integer id;
    private Integer recid;
    private String networkConfigName; // 네트워크 별칭
    private String iaasType; // 클라우드 인프라 환경 타입
    private String subnetId; // 네트워크id
    private String privateStaticIp; // 디렉터 내부 ip
    private String subnetRange; // 서브넷 범위
    private String subnetGateway; // 게이트웨이
    private String subnetDns; // DNS 서버 주소
    private String publicStaticIp; //디렉터 공인 ip
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
    public String getNetworkConfigName() {
        return networkConfigName;
    }
    public void setNetworkConfigName(String networkConfigName) {
        this.networkConfigName = networkConfigName;
    }
    public String getIaasType() {
        return iaasType;
    }
    public void setIaasType(String iaasType) {
        this.iaasType = iaasType;
    }
    public String getSubnetId() {
        return subnetId;
    }
    public void setSubnetId(String subnetId) {
        this.subnetId = subnetId;
    }
    public String getPrivateStaticIp() {
        return privateStaticIp;
    }
    public void setPrivateStaticIp(String privateStaticIp) {
        this.privateStaticIp = privateStaticIp;
    }
    public String getSubnetRange() {
        return subnetRange;
    }
    public void setSubnetRange(String subnetRange) {
        this.subnetRange = subnetRange;
    }
    public String getSubnetGateway() {
        return subnetGateway;
    }
    public void setSubnetGateway(String subnetGateway) {
        this.subnetGateway = subnetGateway;
    }
    public String getSubnetDns() {
        return subnetDns;
    }
    public void setSubnetDns(String subnetDns) {
        this.subnetDns = subnetDns;
    }
    public String getPublicStaticIp() {
        return publicStaticIp;
    }
    public void setPublicStaticIp(String publicStaticIp) {
        this.publicStaticIp = publicStaticIp;
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
    
}
