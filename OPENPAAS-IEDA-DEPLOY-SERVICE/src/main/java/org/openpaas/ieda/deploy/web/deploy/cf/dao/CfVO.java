package org.openpaas.ieda.deploy.web.deploy.cf.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkVO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.resource.ResourceVO;

public class CfVO {
    
    private Integer id; //id
    private String iaasType; //iaas 유형
    
    private Date createDate; //생성알자
    private String createUserId;//생성자
    private String updateUserId;//수정자
    private Date updateDate; //수정일자
    
    // 1.1 Deployment 정보
    private String deploymentName;//배포명
    private String directorUuid;//설치관리자 UUID
    private String releaseName;//릴리즈명
    private String releaseVersion; //릴리즈 버전
    private String cfDbType;
    
    // 1.2 기본정보
    private String domain;//도메인
    private String description;//도메인 설명
    private String domainOrganization;//도메인 그룹
    private String loginSecret; //로그인 비밀번호
    private String userAddSsh;  //os-conf ssh public-key
    private String inceptionOsUserName; //inception user name
    private String cfAdminPassword; // cf 관리자 비밀번호
    private String portalDomain; // paasta portal url

    
    
    //1.3 PaaS-TA 모니터링
    private String paastaMonitoringUse;//PaaS-TA 모니터링 사용 유무
    private String metricUrl;
    private String syslogAddress;
    private String syslogPort;
    private String syslogCustomRule;
    private String syslogFallbackServers;
    //2. 네트워크 목록 정보
    private List<NetworkVO> networks;
    //2.1 네트워크 정보
    private NetworkVO network;
    
    //3. key 생성 정보
    private String countryCode;//국가
    private String stateName;//시/도
    private String localityName;//시/구/군
    private String organizationName;//회사명
    private String unitName;//부서명
    private String email;//이메일
    private String keyFile;//키 파일명
    
    List<HashMap<String, Object>> jobs;
    
    // 4. 리소스 정보
    ResourceVO resource;
    
    // 5. Deploy 정보
    private String deploymentFile;//배포파일명
    private String deployStatus;//배포상태
    private int taskId;//TASK ID
    
    public CfVO(){
        network = new NetworkVO();
        networks = new ArrayList<NetworkVO>();
        resource = new ResourceVO();
    }
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getIaasType() {
        return iaasType;
    }
    public void setIaasType(String iaasType) {
        this.iaasType = iaasType;
    }
    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
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
    public Date getUpdateDate() {
        return updateDate;
    }
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
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
    public String getDomain() {
        return domain;
    }
    public void setDomain(String domain) {
        this.domain = domain;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDomainOrganization() {
        return domainOrganization;
    }
    public void setDomainOrganization(String domainOrganization) {
        this.domainOrganization = domainOrganization;
    }
    public List<NetworkVO> getNetworks() {
        return networks;
    }
    public void setNetworks(List<NetworkVO> networks) {
        this.networks = networks;
    }
    public NetworkVO getNetwork() {
        return network;
    }
    public void setNetwork(NetworkVO network) {
        this.network = network;
    }
    public String getCountryCode() {
        return countryCode;
    }
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    public String getStateName() {
        return stateName;
    }
    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
    public String getLocalityName() {
        return localityName;
    }
    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }
    public String getOrganizationName() {
        return organizationName;
    }
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
    public String getUnitName() {
        return unitName;
    }
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
    public String getCfDbType() {
        return cfDbType;
    }
    public void setCfDbtype(String cfDbType) {
        this.cfDbType = cfDbType;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public ResourceVO getResource() {
        return resource;
    }
    public void setResource(ResourceVO resource) {
        this.resource = resource;
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
    public int getTaskId() {
        return taskId;
    }
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
    public String getKeyFile() {
        return keyFile;
    }
    public void setKeyFile(String keyFile) {
        this.keyFile = keyFile;
    }
    public String getLoginSecret() {
        return loginSecret;
    }
    public void setLoginSecret(String loginSecret) {
        this.loginSecret = loginSecret;
    }
    public String getPaastaMonitoringUse() {
        return paastaMonitoringUse;
    }
    public void setPaastaMonitoringUse(String paastaMonitoringUse) {
        this.paastaMonitoringUse = paastaMonitoringUse;
    }
    public List<HashMap<String, Object>> getJobs() {
        return jobs;
    }

    public void setJobs(List<HashMap<String, Object>> jobs) {
        this.jobs = jobs;
    }

    public String getUserAddSsh() {
        return userAddSsh;
    }

    public void setUserAddSsh(String userAddSsh) {
        this.userAddSsh = userAddSsh;
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

    public void setCfDbType(String cfDbType) {
        this.cfDbType = cfDbType;
    }

    
}
