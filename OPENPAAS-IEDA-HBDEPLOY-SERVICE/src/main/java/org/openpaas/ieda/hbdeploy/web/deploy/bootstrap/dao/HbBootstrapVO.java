package org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao;

import java.util.Date;
import java.util.HashMap;

import org.openpaas.ieda.deploy.web.information.iassConfig.dao.IaasConfigMgntVO;

public class HbBootstrapVO {
    private Integer recid;
    private Integer id;
    private String bootstrapConfigName;
    private String iaasType;
    private String networkConfigInfo;
    private String cpiConfigInfo;
    private String defaultConfigInfo;
    private String resourceConfigInfo;
    private String deploymentFile; // 배포파일
    private String deployStatus; // 배포상태
    private String deployLog; // 배포로그
    private HbBootstrapCpiConfigVO cpiConfigVo;
    private HbBootstrapNetworkConfigVO networkConfigVo;
    private HbBootstrapResourceConfigVO resourceConfigVo;
    private HbBootstrapDefaultConfigVO defaultConfigVo;
    private IaasConfigMgntVO iaasConfig;
    private HashMap<String, Object> iaasAccount;
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
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getBootstrapConfigName() {
        return bootstrapConfigName;
    }
    public void setBootstrapConfigName(String bootstrapConfigName) {
        this.bootstrapConfigName = bootstrapConfigName;
    }
    public String getIaasType() {
        return iaasType;
    }
    public void setIaasType(String iaasType) {
        this.iaasType = iaasType;
    }
    public String getNetworkConfigInfo() {
        return networkConfigInfo;
    }
    public void setNetworkConfigInfo(String networkConfigInfo) {
        this.networkConfigInfo = networkConfigInfo;
    }
    public String getCpiConfigInfo() {
        return cpiConfigInfo;
    }
    public void setCpiConfigInfo(String cpiConfigInfo) {
        this.cpiConfigInfo = cpiConfigInfo;
    }
    public String getDefaultConfigInfo() {
        return defaultConfigInfo;
    }
    public void setDefaultConfigInfo(String defaultConfigInfo) {
        this.defaultConfigInfo = defaultConfigInfo;
    }
    public String getResourceConfigInfo() {
        return resourceConfigInfo;
    }
    public void setResourceConfigInfo(String resourceConfigInfo) {
        this.resourceConfigInfo = resourceConfigInfo;
    }
    public String getDeploymentFile() {
        return deploymentFile;
    }
    public void setDeploymentFile(String deploymentFile) {
        this.deploymentFile = deploymentFile;
    }
    public String getDeployStatus() {
        return deployStatus;
    }
    public void setDeployStatus(String deployStatus) {
        this.deployStatus = deployStatus;
    }
    public String getDeployLog() {
        return deployLog;
    }
    public void setDeployLog(String deployLog) {
        this.deployLog = deployLog;
    }
    public HbBootstrapCpiConfigVO getCpiConfigVo() {
        return cpiConfigVo;
    }
    public void setCpiConfigVo(HbBootstrapCpiConfigVO cpiConfigVo) {
        this.cpiConfigVo = cpiConfigVo;
    }
    public HbBootstrapNetworkConfigVO getNetworkConfigVo() {
        return networkConfigVo;
    }
    public void setNetworkConfigVo(HbBootstrapNetworkConfigVO networkConfigVo) {
        this.networkConfigVo = networkConfigVo;
    }
    public HbBootstrapResourceConfigVO getResourceConfigVo() {
        return resourceConfigVo;
    }
    public void setResourceConfigVo(HbBootstrapResourceConfigVO resourceConfigVo) {
        this.resourceConfigVo = resourceConfigVo;
    }
    public HbBootstrapDefaultConfigVO getDefaultConfigVo() {
        return defaultConfigVo;
    }
    public void setDefaultConfigVo(HbBootstrapDefaultConfigVO defaultConfigVo) {
        this.defaultConfigVo = defaultConfigVo;
    }
    public IaasConfigMgntVO getIaasConfig() {
        return iaasConfig;
    }
    public void setIaasConfig(IaasConfigMgntVO iaasConfig) {
        this.iaasConfig = iaasConfig;
    }
    public HashMap<String, Object> getIaasAccount() {
        return iaasAccount;
    }
    public void setIaasAccount(HashMap<String, Object> iaasAccount) {
        this.iaasAccount = iaasAccount;
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