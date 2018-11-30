package org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao;

import java.util.Date;

public class HbCfDeploymentResourceConfigVO {
    private Integer id;
    private Integer recid;
    private String resourceConfigName; // 리소스 별칭
    private String iaasType; // 클라우드 인프라 환경 타입
    private String stemcellName;
    private String stemcellVersion;
    private String instanceTypeS;
    private String instanceTypeM;
    private String instanceTypeL;
    private String directorInfo;
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
    public String getResourceConfigName() {
        return resourceConfigName;
    }
    public void setResourceConfigName(String resourceConfigName) {
        this.resourceConfigName = resourceConfigName;
    }
    public String getIaasType() {
        return iaasType;
    }
    public void setIaasType(String iaasType) {
        this.iaasType = iaasType;
    }
    public String getStemcellName() {
        return stemcellName;
    }
    public void setStemcellName(String stemcellName) {
        this.stemcellName = stemcellName;
    }
    public String getStemcellVersion() {
        return stemcellVersion;
    }
    public void setStemcellVersion(String stemcellVersion) {
        this.stemcellVersion = stemcellVersion;
    }
    public String getInstanceTypeS() {
        return instanceTypeS;
    }
    public void setInstanceTypeS(String instanceTypeS) {
        this.instanceTypeS = instanceTypeS;
    }
    public String getInstanceTypeM() {
        return instanceTypeM;
    }
    public void setInstanceTypeM(String instanceTypeM) {
        this.instanceTypeM = instanceTypeM;
    }
    public String getInstanceTypeL() {
        return instanceTypeL;
    }
    public void setInstanceTypeL(String instanceTypeL) {
        this.instanceTypeL = instanceTypeL;
    }
    public String getDirectorInfo() {
        return directorInfo;
    }
    public void setDirectorInfo(String directorInfo) {
        this.directorInfo = directorInfo;
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
