package org.openpaas.ieda.deploy.web.deploy.cf.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class CfParamDTO {
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Default{
        private String id; //id
        @NotNull
        private String iaas; //IaaS
        // 1.1 Deployment 정보
        @NotNull
        private String deploymentName; //배포명
        @NotNull
        private String directorUuid; //설치관리자 UUID
        @NotNull
        private String releaseName; //릴리즈명
        @NotNull
        private String releaseVersion; //릴리즈 버전
        
        private String cfDbType;
        
        // 1.2 기본정보
        @NotNull
        private String domain; //도메인
        @NotNull
        private String domainOrganization; //도메인 그룹
        private String paastaMonitoringUse;//PaaS-TA 모니터링 사용 유무
        private String userAddSsh;//os-conf ssh public-key
        private String osConfReleaseName;//os-conf Release Name
        private String osConfReleaseVersion;//os-conf Release Version
        private String inceptionOsUserName; // Inception User Name
        private String cfAdminPassword; // cf admin password
        private String portalDomain; // paasta portal url
        
        private String metricUrl;
        private String syslogAddress;
        private String syslogPort;
        private String syslogCustomRule;
        private String syslogFallbackServers;
        
        
        
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getIaas() {
            return iaas;
        }
        public void setIaas(String iaas) {
            this.iaas = iaas;
        }
        public String getDeploymentName() {
            return deploymentName;
        }
        public void setDeploymentName(String deploymentName) {
            this.deploymentName = deploymentName;
        }
        public String getDirectorUuid() {
            return directorUuid;
        }
        public void setDirectorUuid(String directorUuid) {
            this.directorUuid = directorUuid;
        }
        public String getReleaseName() {
            return releaseName;
        }
        public void setReleaseName(String releaseName) {
            this.releaseName = releaseName;
        }
        public String getReleaseVersion() {
            return releaseVersion;
        }
        public void setReleaseVersion(String releaseVersion) {
            this.releaseVersion = releaseVersion;
        }
        public String getCfDbType() {
            return cfDbType;
        }
        public void setCfDbType(String cfDbType) {
            this.cfDbType = cfDbType;
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
        public String getPaastaMonitoringUse() {
            return paastaMonitoringUse;
        }
        public void setPaastaMonitoringUse(String paastaMonitoringUse) {
            this.paastaMonitoringUse = paastaMonitoringUse;
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
        public String getUserAddSsh() {
            return userAddSsh;
        }
        public void setUserAddSsh(String userAddSsh) {
            this.userAddSsh = userAddSsh;
        }
        public String getOsConfReleaseName() {
            return osConfReleaseName;
        }
        public void setOsConfReleaseName(String osConfReleaseName) {
            this.osConfReleaseName = osConfReleaseName;
        }
        public String getOsConfReleaseVersion() {
            return osConfReleaseVersion;
        }
        public void setOsConfReleaseVersion(String osConfReleaseVersion) {
            this.osConfReleaseVersion = osConfReleaseVersion;
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
        public String getPortalDomain() {
            return portalDomain;
        }
        public void setPortalDomain(String portalDomain) {
            this.portalDomain = portalDomain;
        }

    }
    
    public static class Delete{
        @NotNull
        private String iaas; //IaaS
        @NotNull
        private String id; //id
        @NotNull
        private String platform;//플랫폼 유형
        
        public String getIaas() {
            return iaas;
        }
        public void setIaas(String iaas) {
            this.iaas = iaas;
        }
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getPlatform() {
            return platform;
        }
        public void setPlatform(String platform) {
            this.platform = platform;
        }
    }
    
    public static class Install{
        @NotNull
        private String iaas;//IaaS
        @NotNull
        private String id; //id
        @NotNull
        private String platform;//플랫폼 유형

        public String getIaas() {
            return iaas;
        }
        public void setIaas(String iaas) {
            this.iaas = iaas;
        }
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getPlatform() {
            return platform;
        }
        public void setPlatform(String platform) {
            this.platform = platform;
        }
        
    }
    
}