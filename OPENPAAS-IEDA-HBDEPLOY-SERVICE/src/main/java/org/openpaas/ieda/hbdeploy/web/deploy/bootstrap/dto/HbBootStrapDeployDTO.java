package org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto;

public class HbBootStrapDeployDTO{

    private String id;
    private String bootstrapConfigName;
    private String iaasType;
    private String networkConfigInfo;
    private String cpiConfigInfo;
    private String defaultConfigInfo;
    private String resourceConfigInfo;
    private String deploymentFile; // 배포파일
    private String deployStatus; // 배포상태
    private String deployLog; // 배포로그
    public String getId() {
        return id;
    }
    public void setId(String id) {
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
}