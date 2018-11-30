package org.openpaas.ieda.deploy.web.deploy.bootstrap.dto;

import javax.validation.constraints.NotNull;

public class BootStrapDeployDTO{
    
    public static class IaasConfig{
        private String id;
        @NotNull
        private String iaasType;
        @NotNull
        private String iaasConfigId;
        private String testFlag;
        
        public String getId() {
            return id;
        }
        public String getIaasType() {
            return iaasType;
        }
        public String getIaasConfigId() {
            return iaasConfigId;
        }
        public void setId(String id) {
            this.id = id;
        }
        public void setIaasType(String iaasType) {
            this.iaasType = iaasType;
        }
        public void setIaasConfigId(String iaasConfigId) {
            this.iaasConfigId = iaasConfigId;
        }
        public String getTestFlag() {
            return testFlag;
        }
        public void setTestFlag(String testFlag) {
            this.testFlag = testFlag;
        }
    }
    
    public static class Default{
        @NotNull
        private String id; //id
        @NotNull
        private String iaasConfigId;
        @NotNull
        private String deploymentName; //배포명
        @NotNull
        private String directorName; //디렉터 명
        @NotNull
        private String credentialKeyName;
        @NotNull
        private String boshRelease; //bosh 릴리즈
        @NotNull
        private String ntp; //내부 NTP
        @NotNull
        private String boshCpiRelease; //bosh cpi 릴리즈
        @NotNull
        private String enableSnapshots;//스냅샷 사용 유무
        @NotNull
        private String snapshotSchedule;//스냅샷 스케줄
        private String paastaMonitoringUse;// PaaS-TA 모니터링 사용 유무
        private String osConfRelease;
        private String boshBpmRelease; //BOSH BPM 릴리즈
        private String boshUaaRelease; //BOSH uaa 릴리즈
        private String boshCredhubRelease; //BOSH Credhub 릴리즈
        private String paastaMonitoringAgentRelease; //PaaS-TA 모니터링 사용시 Agent 릴리즈
        private String paastaMonitoringSyslogRelease; //PaaS-TA 모니터링 사용시 Syslog 릴리즈
        private String metricUrl; //PaaS-TA 모니터링
        private String syslogAddress; //PaaS-TA 모니터링
        private String syslogPort; //PaaS-TA 모니터링
        private String syslogTransport; //PaaS-TA 모니터링
        
        public String getId() {
            return id;
        }
        public String getIaasConfigId() {
            return iaasConfigId;
        }
        public String getDeploymentName() {
            return deploymentName;
        }
        public String getDirectorName() {
            return directorName;
        }
        public String getCredentialKeyName() {
            return credentialKeyName;
        }
        public void setCredentialKeyName(String credentialKeyName) {
            this.credentialKeyName = credentialKeyName;
        }
        public String getBoshRelease() {
            return boshRelease;
        }
        public String getNtp() {
            return ntp;
        }
        public String getBoshCpiRelease() {
            return boshCpiRelease;
        }
        public String getEnableSnapshots() {
            return enableSnapshots;
        }
        public String getSnapshotSchedule() {
            return snapshotSchedule;
        }
        public void setId(String id) {
            this.id = id;
        }
        public void setIaasConfigId(String iaasConfigId) {
            this.iaasConfigId = iaasConfigId;
        }
        public void setDeploymentName(String deploymentName) {
            this.deploymentName = deploymentName;
        }
        public void setDirectorName(String directorName) {
            this.directorName = directorName;
        }
        public void setBoshRelease(String boshRelease) {
            this.boshRelease = boshRelease;
        }
        public void setNtp(String ntp) {
            this.ntp = ntp;
        }
        public void setBoshCpiRelease(String boshCpiRelease) {
            this.boshCpiRelease = boshCpiRelease;
        }
        public void setEnableSnapshots(String enableSnapshots) {
            this.enableSnapshots = enableSnapshots;
        }
        public void setSnapshotSchedule(String snapshotSchedule) {
            this.snapshotSchedule = snapshotSchedule;
        }
        public String getPaastaMonitoringUse() {
            return paastaMonitoringUse;
        }
        public void setPaastaMonitoringUse(String paastaMonitoringUse) {
            this.paastaMonitoringUse = paastaMonitoringUse;
        }
        public String getOsConfRelease() {
            return osConfRelease;
        }
        public void setOsConfRelease(String osConfRelease) {
            this.osConfRelease = osConfRelease;
        }
        public String getBoshBpmRelease() {
            return boshBpmRelease;
        }
        public void setBoshBpmRelease(String boshBpmRelease) {
            this.boshBpmRelease = boshBpmRelease;
        }
        public String getBoshUaaRelease() {
            return boshUaaRelease;
        }
        public void setBoshUaaRelease(String boshUaaRelease) {
            this.boshUaaRelease = boshUaaRelease;
        }
        public String getBoshCredhubRelease() {
            return boshCredhubRelease;
        }
        public void setBoshCredhubRelease(String boshCredhubRelease) {
            this.boshCredhubRelease = boshCredhubRelease;
        }
        public String getPaastaMonitoringAgentRelease() {
            return paastaMonitoringAgentRelease;
        }
        public void setPaastaMonitoringAgentRelease(String paastaMonitoringAgentRelease) {
            this.paastaMonitoringAgentRelease = paastaMonitoringAgentRelease;
        }
        public String getPaastaMonitoringSyslogRelease() {
            return paastaMonitoringSyslogRelease;
        }
        public void setPaastaMonitoringSyslogRelease(String paastaMonitoringSyslogRelease) {
            this.paastaMonitoringSyslogRelease = paastaMonitoringSyslogRelease;
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
        public String getSyslogTransport() {
            return syslogTransport;
        }
        public void setSyslogTransport(String syslogTransport) {
            this.syslogTransport = syslogTransport;
        }
        
    }
    
    public static class Network{
        @NotNull
        private String id; //id
        @NotNull
        private String privateStaticIp; //디렉터 내부 ip
        @NotNull
        private String subnetId; //내부 네트워크id
        private String networkName;//네트워크 명
        @NotNull
        private String subnetRange; //내부 서브넷 범위 
        @NotNull
        private String subnetGateway; //내부 게이트웨이
        @NotNull
        private String subnetDns; //내부 DNS
        private String publicStaticIp; //디렉터 공인 ip
        private String publicSubnetId; //public 네트워크id
        private String publicSubnetRange; //public 서브넷 범위 
        private String publicSubnetGateway; //public 게이트웨이
        private String publicSubnetDns; //public DNS
        public String getId() {
            return id;
        }
        public String getPrivateStaticIp() {
            return privateStaticIp;
        }
        public String getSubnetId() {
            return subnetId;
        }
        public String getSubnetRange() {
            return subnetRange;
        }
        public String getSubnetGateway() {
            return subnetGateway;
        }
        public String getSubnetDns() {
            return subnetDns;
        }
        public String getPublicStaticIp() {
            return publicStaticIp;
        }
        public String getPublicSubnetId() {
            return publicSubnetId;
        }
        public String getPublicSubnetRange() {
            return publicSubnetRange;
        }
        public String getPublicSubnetGateway() {
            return publicSubnetGateway;
        }
        public String getPublicSubnetDns() {
            return publicSubnetDns;
        }
        public void setId(String id) {
            this.id = id;
        }
        public void setPrivateStaticIp(String privateStaticIp) {
            this.privateStaticIp = privateStaticIp;
        }
        public void setSubnetId(String subnetId) {
            this.subnetId = subnetId;
        }
        public void setSubnetRange(String subnetRange) {
            this.subnetRange = subnetRange;
        }
        public void setSubnetGateway(String subnetGateway) {
            this.subnetGateway = subnetGateway;
        }
        public void setSubnetDns(String subnetDns) {
            this.subnetDns = subnetDns;
        }
        public void setPublicStaticIp(String publicStaticIp) {
            this.publicStaticIp = publicStaticIp;
        }
        public void setPublicSubnetId(String publicSubnetId) {
            this.publicSubnetId = publicSubnetId;
        }
        public void setPublicSubnetRange(String publicSubnetRange) {
            this.publicSubnetRange = publicSubnetRange;
        }
        public void setPublicSubnetGateway(String publicSubnetGateway) {
            this.publicSubnetGateway = publicSubnetGateway;
        }
        public void setPublicSubnetDns(String publicSubnetDns) {
            this.publicSubnetDns = publicSubnetDns;
        }
        public String getNetworkName() {
            return networkName;
        }
        public void setNetworkName(String networkName) {
            this.networkName = networkName;
        }
        
        
    }
    
    public static class Resource{
        @NotNull
        private String id; //id
        @NotNull
        private String stemcell; //스템셀
        private String cloudInstanceType; //인스턴스유형
        private String boshPassword; //VM 비밀번호
        private String resourcePoolCpu;//리소스 풀 CPU
        private String resourcePoolRam;//리소스 풀 RAM
        private String resourcePoolDisk;//리소스 풀 DISK
        
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getStemcell() {
            return stemcell;
        }
        public void setStemcell(String stemcell) {
            this.stemcell = stemcell;
        }
        public String getCloudInstanceType() {
            return cloudInstanceType;
        }
        public void setCloudInstanceType(String cloudInstanceType) {
            this.cloudInstanceType = cloudInstanceType;
        }
        public String getBoshPassword() {
            return boshPassword;
        }
        public void setBoshPassword(String boshPassword) {
            this.boshPassword = boshPassword;
        }
        public String getResourcePoolCpu() {
            return resourcePoolCpu;
        }
        public void setResourcePoolCpu(String resourcePoolCpu) {
            this.resourcePoolCpu = resourcePoolCpu;
        }
        public String getResourcePoolRam() {
            return resourcePoolRam;
        }
        public void setResourcePoolRam(String resourcePoolRam) {
            this.resourcePoolRam = resourcePoolRam;
        }
        public String getResourcePoolDisk() {
            return resourcePoolDisk;
        }
        public void setResourcePoolDisk(String resourcePoolDisk) {
            this.resourcePoolDisk = resourcePoolDisk;
        }
    }
    
    public static class Install{
        @NotNull
        private String iaasType; //Iaas
        @NotNull
        private String id; //id
        
        public String getIaasType() {
            return iaasType;
        }
        public void setIaasType(String iaasType) {
            this.iaasType = iaasType;
        }
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
    }
    
    public static class Delete{
        @NotNull
        private String iaasType; //Iaas
        @NotNull
        private String id; //id
        
        public String getIaasType() {
            return iaasType;
        }
        public void setIaasType(String iaasType) {
            this.iaasType = iaasType;
        }
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
    }
    
    public static class Deployment{
        @NotNull
        private String deploymentFile; //배포파일

        public String getDeploymentFile() {
            return deploymentFile;
        }

        public void setDeploymentFile(String deploymentFile) {
            this.deploymentFile = deploymentFile;
        }
    }
}