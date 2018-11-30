package org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Repository;
@Repository
public class HbCfDeploymentDefaultConfigVO {
    private Integer id;
    private Integer recid;
    @NotNull
    private String defaultConfigName; // 리소스 별칭
    @NotNull
    private String iaasType; // 클라우드 인프라 환경 타입
    private String deploymentName;
    private String cfDeploymentVersion;
    private String domain;
    private String domainOrganization;
    private String cfDbType;
    private String inceptionOsUserName; // inception user name
    private String cfAdminPassword; // cf admin password
    private String portalDomain; // portal Domain
    private String metricUrl;
    private String syslogAddress;
    private String syslogPort;
    private String syslogCustomRule;
    private String syslogFallbackServers;
    private String paastaMonitoringUse;
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

    public String getDefaultConfigName() {
        return defaultConfigName;
    }
    public void setDefaultConfigName(String defaultConfigName) {
        this.defaultConfigName = defaultConfigName;
    }
    public String getIaasType() {
        return iaasType;
    }
    public void setIaasType(String iaasType) {
        this.iaasType = iaasType;
    }
    public String getDeploymentName() {
        return deploymentName;
    }
    public void setDeploymentName(String deploymentName) {
        this.deploymentName = deploymentName;
    }
    public String getCfDeploymentVersion() {
        return cfDeploymentVersion;
    }
    public void setCfDeploymentVersion(String cfDeploymentVersion) {
        this.cfDeploymentVersion = cfDeploymentVersion;
    }
    public String getDomain() {
        return domain;
    }
    public void setDomain(String domain) {
        this.domain = domain;
    }
    public String getDomainOrganization() {
        return domainOrganization;
    }
    public void setDomainOrganization(String domainOrganization) {
        this.domainOrganization = domainOrganization;
    }
    public String getCfDbType() {
        return cfDbType;
    }
    public void setCfDbType(String cfDbType) {
        this.cfDbType = cfDbType;
    }
    public String getInceptionOsUserName() {
        return inceptionOsUserName;
    }
    public void setInceptionOsUserName(String inceptionOsUserName) {
        this.inceptionOsUserName = inceptionOsUserName;
    }
    public String getCfAdminPassword() {
        return cfAdminPassword;
    }
    public void setCfAdminPassword(String cfAdminPassword) {
        this.cfAdminPassword = cfAdminPassword;
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
    public String getPortalDomain() {
        return portalDomain;
    }
    public void setPortalDomain(String portalDomain) {
        this.portalDomain = portalDomain;
    }
    public String getMetricUrl() {
        return metricUrl;
    }
    public void setMetricUrl(String metricUrl) {
        this.metricUrl = metricUrl;
    }
    public String getSyslogAddress() {
        return syslogAddress;
    }
    public void setSyslogAddress(String syslogAddress) {
        this.syslogAddress = syslogAddress;
    }
    public String getSyslogPort() {
        return syslogPort;
    }
    public void setSyslogPort(String syslogPort) {
        this.syslogPort = syslogPort;
    }
    public String getSyslogCustomRule() {
        return syslogCustomRule;
    }
    public void setSyslogCustomRule(String syslogCustomRule) {
        this.syslogCustomRule = syslogCustomRule;
    }
    public String getSyslogFallbackServers() {
        return syslogFallbackServers;
    }
    public void setSyslogFallbackServers(String syslogFallbackServers) {
        this.syslogFallbackServers = syslogFallbackServers;
    }
    public String getPaastaMonitoringUse() {
        return paastaMonitoringUse;
    }
    public void setPaastaMonitoringUse(String paastaMonitoringUse) {
        this.paastaMonitoringUse = paastaMonitoringUse;
    }
}
