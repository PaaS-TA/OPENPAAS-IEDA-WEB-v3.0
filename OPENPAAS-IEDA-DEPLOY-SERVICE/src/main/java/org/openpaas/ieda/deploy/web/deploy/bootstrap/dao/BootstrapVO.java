package org.openpaas.ieda.deploy.web.deploy.bootstrap.dao;

import java.util.Date;
import java.util.HashMap;

import org.openpaas.ieda.deploy.web.information.iassConfig.dao.IaasConfigMgntVO;

public class BootstrapVO {

    private Integer id; // id
    private String iaasType; // iaas 유형
    private Integer iaasConfigId;//인프라 환경 설정 아이디
    private String createUserId; // 설치 사용자
    private String updateUserId; // 설치 수정 사용자
    private Date createDate; // 생성일자
    private Date updateDate; // 수정일자
    private String testFlag; //통합 테스트 사용
    
    /** Iaas Config Info **/
    private IaasConfigMgntVO iaasConfig;
    private HashMap<String, Object> iaasAccount;
    
    /** Default Info **/
    private String deploymentName; // 배포명
    private String directorName; // 디렉터명
    private String credentialKeyName;
    private String boshRelease; // BOSH 릴리즈
    private String boshCpiRelease; // BOSH API 릴리즈
    private String boshBpmRelease; // BOSH Bpm 릴리즈
    private String boshCredhubRelease; // BOSH Credhub 릴리즈
    private String boshUaaRelease; //BOSH uaa 릴리즈
    private String snapshotSchedule;//스냅샷 스케줄
    private String enableSnapshots;//스냅샷 사용 유무
    private String ntp; // NTP
    private String osConfRelease;
    private String paastaMonitoringUse; //PaaS-TA 모니터링 사용 유무
    private String paastaMonitoringAgentRelease; //PaaS-TA 모니터링 사용시 Agent 릴리즈
    private String paastaMonitoringSyslogRelease; //PaaS-TA 모니터링 사용시 Syslog 릴리즈
    private String metricUrl; //PaaS-TA 모니터링
    private String syslogAddress; //PaaS-TA 모니터링
    private String syslogPort; //PaaS-TA 모니터링
    private String syslogTransport; //PaaS-TA 모니터링
    /** Network Info **/
    private String subnetId; // 네트워크id
    private String privateStaticIp; // 디렉터 내부 ip
    private String subnetRange; // 서브넷 범위
    private String subnetGateway; // 게이트웨이
    private String subnetDns; // DNS
    
    private String publicStaticIp; //디렉터 공인 ip
    private String publicSubnetId; //public 네트워크id
    private String publicSubnetRange; //public 서브넷 범위 
    private String publicSubnetGateway; //public 게이트웨이
    private String publicSubnetDns; //public DNS
    private String networkName;//네트워크 명

    /** Resource Info **/
    private String stemcell; // 스템셀
    private String cloudInstanceType; // 인스턴스 유형
    private String boshPassword; //VM 비밀번호
    private String resourcePoolCpu;//리소스 풀 CPU
    private String resourcePoolRam;//리소스 풀 RAM
    private String resourcePoolDisk;//리소스 풀 DISK

    private String deploymentFile; // 배포파일
    private String deployStatus; // 배포상태
    private String deployLog; // 배포로그
    
    public BootstrapVO(){
        iaasConfig = new IaasConfigMgntVO();
        iaasAccount = new HashMap<String, Object>();
    }
    
    public Integer getId() {
        return id;
    }
    public String getIaasType() {
        return iaasType;
    }
    public Integer getIaasConfigId() {
        return iaasConfigId;
    }
    public String getCreateUserId() {
        return createUserId;
    }
    public String getUpdateUserId() {
        return updateUserId;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public void setIaasType(String iaasType) {
        this.iaasType = iaasType;
    }
    public void setIaasConfigId(Integer iaasConfigId) {
        this.iaasConfigId = iaasConfigId;
    }
    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }
    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }
    public void setUpdateDate(Date updateDate) {
        if(updateDate == null) {
            this.updateDate = null;
        } else {
            this.updateDate = new Date(updateDate.getTime());
        }
    }
    public Date getUpdateDate() {
        if(updateDate == null) {
            return null;
        } else {
            return new Date(updateDate.getTime());
        }
    }
    public Date getCreateDate() {
        if(createDate == null) {
            return null;
        } else {
            return new Date(createDate.getTime());
        }
    }
    
    public void setCreateDate(Date createDate) {
        if(createDate == null) {
            this.createDate = null;
        } else {
            this.createDate = new Date(createDate.getTime());
        }
    }
    public String getOsConfRelease() {
        return osConfRelease;
    }
    public void setOsConfRelease(String osConfRelease) {
        this.osConfRelease = osConfRelease;
    }
    public String getDeploymentName() {
        return deploymentName;
    }
    public void setDeploymentName(String deploymentName) {
        this.deploymentName = deploymentName;
    }
    public String getDirectorName() {
        return directorName;
    }
    public void setDirectorName(String directorName) {
        this.directorName = directorName;
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
    public void setBoshRelease(String boshRelease) {
        this.boshRelease = boshRelease;
    }
    public String getBoshCpiRelease() {
        return boshCpiRelease;
    }
    public void setBoshCpiRelease(String boshCpiRelease) {
        this.boshCpiRelease = boshCpiRelease;
    }
    public String getSnapshotSchedule() {
        return snapshotSchedule;
    }
    public void setSnapshotSchedule(String snapshotSchedule) {
        this.snapshotSchedule = snapshotSchedule;
    }
    public String getEnableSnapshots() {
        return enableSnapshots;
    }
    public void setEnableSnapshots(String enableSnapshots) {
        this.enableSnapshots = enableSnapshots;
    }
    public String getSubnetId() {
        return subnetId;
    }
    public void setSubnetId(String subnetId) {
        this.subnetId = subnetId;
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
    public String getNtp() {
        return ntp;
    }
    public void setNtp(String ntp) {
        this.ntp = ntp;
    }
    public String getPublicStaticIp() {
        return publicStaticIp;
    }
    public void setPublicStaticIp(String publicStaticIp) {
        this.publicStaticIp = publicStaticIp;
    }
    public String getPublicSubnetId() {
        return publicSubnetId;
    }
    public void setPublicSubnetId(String publicSubnetId) {
        this.publicSubnetId = publicSubnetId;
    }
    public String getPublicSubnetRange() {
        return publicSubnetRange;
    }
    public void setPublicSubnetRange(String publicSubnetRange) {
        this.publicSubnetRange = publicSubnetRange;
    }
    public String getPublicSubnetGateway() {
        return publicSubnetGateway;
    }
    public void setPublicSubnetGateway(String publicSubnetGateway) {
        this.publicSubnetGateway = publicSubnetGateway;
    }
    public String getPublicSubnetDns() {
        return publicSubnetDns;
    }
    public void setPublicSubnetDns(String publicSubnetDns) {
        this.publicSubnetDns = publicSubnetDns;
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
    public String getNetworkName() {
        return networkName;
    }
    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }
    public IaasConfigMgntVO getIaasConfig() {
        return iaasConfig;
    }
    public HashMap<String, Object> getIaasAccount() {
        return iaasAccount;
    }
    public void setIaasConfig(IaasConfigMgntVO iaasConfig) {
        this.iaasConfig = iaasConfig;
    }
    public void setIaasAccount(HashMap<String, Object> iaasAccount) {
        this.iaasAccount = iaasAccount;
    }

    public String getPrivateStaticIp() {
        return privateStaticIp;
    }

    public void setPrivateStaticIp(String privateStaticIp) {
        this.privateStaticIp = privateStaticIp;
    }

    public String getTestFlag() {
        return testFlag;
    }

    public void setTestFlag(String testFlag) {
        this.testFlag = testFlag;
    }

    public String getPaastaMonitoringUse() {
        return paastaMonitoringUse;
    }

    public void setPaastaMonitoringUse(String paastaMonitoringUse) {
        this.paastaMonitoringUse = paastaMonitoringUse;
    }

    public String getBoshBpmRelease() {
        return boshBpmRelease;
    }

    public void setBoshBpmRelease(String boshBpmRelease) {
        this.boshBpmRelease = boshBpmRelease;
    }

    public String getBoshCredhubRelease() {
        return boshCredhubRelease;
    }

    public void setBoshCredhubRelease(String boshCredhubRelease) {
        this.boshCredhubRelease = boshCredhubRelease;
    }

    public String getBoshUaaRelease() {
        return boshUaaRelease;
    }

    public void setBoshUaaRelease(String boshUaaRelease) {
        this.boshUaaRelease = boshUaaRelease;
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

}