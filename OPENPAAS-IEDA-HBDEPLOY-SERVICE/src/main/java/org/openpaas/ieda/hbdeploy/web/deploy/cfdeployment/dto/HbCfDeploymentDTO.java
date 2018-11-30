package org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto;

import java.util.Date;

public class HbCfDeploymentDTO {
    
    private Integer id;
    private String cfDeploymentConfigName;
    private String iaasType;
    private String networkConfigInfo;
    private String credentialConfigInfo;
    private String defaultConfigInfo;
    private String resourceConfigInfo;
    private String instanceConfigInfo;
    private String cloudConfigFile;
    private String deployStatus; // 배포상태
    private String taskId;
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
    public String getCfDeploymentConfigName() {
        return cfDeploymentConfigName;
    }
    public void setCfDeploymentConfigName(String cfDeploymentConfigName) {
        this.cfDeploymentConfigName = cfDeploymentConfigName;
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
    public String getCredentialConfigInfo() {
        return credentialConfigInfo;
    }
    public void setCredentialConfigInfo(String credentialConfigInfo) {
        this.credentialConfigInfo = credentialConfigInfo;
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
    public String getCloudConfigFile() {
        return cloudConfigFile;
    }
    public void setCloudConfigFile(String cloudConfigFile) {
        this.cloudConfigFile = cloudConfigFile;
    }
    public String getDeployStatus() {
        return deployStatus;
    }
    public void setDeployStatus(String deployStatus) {
        this.deployStatus = deployStatus;
    }
    public String getTaskId() {
        return taskId;
    }
    public void setTaskId(String taskId) {
        this.taskId = taskId;
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
    public String getInstanceConfigInfo() {
        return instanceConfigInfo;
    }
    public void setInstanceConfigInfo(String instanceConfigInfo) {
        this.instanceConfigInfo = instanceConfigInfo;
    }
    
}
