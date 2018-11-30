package org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao;

import java.util.Date;

import javax.validation.constraints.NotNull;

public class HbBootstrapCpiConfigVO {
    
    private Integer recid;
    private Integer cpiInfoId;
    private String iaasType;
    @NotNull
    private Integer iaasConfigId;
    private String cpiName;
    private String commonAccessUser;
    private String commonTenant;
    private String commonProject;
    private String openstackVersion;
    private String commonSecurityGroup; //보안 그룹 이름
    private String commonKeypairName; //Key Pair 이름
    private String commonKeypairPath; // Key Pair 경로
    private String commonAvailabilityZone; //공통 영역
    private String iaasConfigAlias; //환경 설정 별칭
    private String createUserId;//등록자 아이디
    private String updateUserId;//수정자 아이디
    private Date createDate;//등록일
    private Date updateDate;//수정일
    
    
    public Integer getRecid() {
        return recid;
    }
    public void setRecid(Integer recid) {
        this.recid = recid;
    }
    public Integer getCpiInfoId() {
        return cpiInfoId;
    }
    public void setCpiInfoId(Integer cpiInfoId) {
        this.cpiInfoId = cpiInfoId;
    }
    public String getIaasType() {
        return iaasType;
    }
    public void setIaasType(String iaasType) {
        this.iaasType = iaasType;
    }
    public Integer getIaasConfigId() {
        return iaasConfigId;
    }
    public void setIaasConfigId(Integer iaasConfigId) {
        this.iaasConfigId = iaasConfigId;
    }
    public String getCommonTenant() {
        return commonTenant;
    }
    public void setCommonTenant(String commonTenant) {
        this.commonTenant = commonTenant;
    }
    public String getCommonProject() {
        return commonProject;
    }
    public void setCommonProject(String commonProject) {
        this.commonProject = commonProject;
    }
    public String getOpenstackVersion() {
        return openstackVersion;
    }
    public void setOpenstackVersion(String openstackVersion) {
        this.openstackVersion = openstackVersion;
    }
    public String getCpiName() {
        return cpiName;
    }
    public void setCpiName(String cpiName) {
        this.cpiName = cpiName;
    }
    public String getCommonAccessUser() {
        return commonAccessUser;
    }
    public void setCommonAccessUser(String commonAccessUser) {
        this.commonAccessUser = commonAccessUser;
    }
    public String getCommonSecurityGroup() {
        return commonSecurityGroup;
    }
    public void setCommonSecurityGroup(String commonSecurityGroup) {
        this.commonSecurityGroup = commonSecurityGroup;
    }
    public String getCommonKeypairName() {
        return commonKeypairName;
    }
    public void setCommonKeypairName(String commonKeypairName) {
        this.commonKeypairName = commonKeypairName;
    }
    public String getCommonKeypairPath() {
        return commonKeypairPath;
    }
    public void setCommonKeypairPath(String commonKeypairPath) {
        this.commonKeypairPath = commonKeypairPath;
    }
    public String getCommonAvailabilityZone() {
        return commonAvailabilityZone;
    }
    public void setCommonAvailabilityZone(String commonAvailabilityZone) {
        this.commonAvailabilityZone = commonAvailabilityZone;
    }
    public String getIaasConfigAlias() {
        return iaasConfigAlias;
    }
    public void setIaasConfigAlias(String iaasConfigAlias) {
        this.iaasConfigAlias = iaasConfigAlias;
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
