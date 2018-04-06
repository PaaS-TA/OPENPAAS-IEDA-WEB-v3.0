package org.openpaas.ieda.deploy.web.information.iassConfig.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class IaasConfigMgntVO{
    private Integer id; //id
    private Integer recid;
    private Integer accountId;
    private String accountName;
    private String deployStatus;
    private String iaasType; //iaasType
    private String iaasConfigAlias; //환경 설정 별칭
    private String commonSecurityGroup; //보안 그룹 이름
    private String commonRegion; //Region
    private String commonKeypairName; //Key Pair 이름
    private String commonKeypairPath; // Key Pair 경로
    private String commonAvailabilityZone; //공통 영역
    private String openstackDomain; //도메인
    private String commonProject; //프로젝트
    private String openstackKeystoneVersion; //오픈스택 키스톤 버전
    private String vsphereVcentDataCenterName;
    private String vsphereVcenterVmFolder; //vCenter
    private String vsphereVcenterTemplateFolder; //vCenter
    private String vsphereVcenterDatastore; //vCenter 데이터 스토어 
    private String vsphereVcenterPersistentDatastore; // vCenter 영구 데이터 스토어을 
    private String vsphereVcenterDiskPath; //vCenter 디스크 경로
    private String vsphereVcenterCluster; //vCenter 클러스터
    private String azureResourceGroup;
    private String azureStorageAccountName;
    private String azureSshPublicKey;
    private String azurePrivateKey;
    private String googlePublicKey; //구글 Public key
    private String createUserId;//등록자 아이디
    private String updateUserId;//수정자 아이디
    private Date createDate;//등록일
    private Date updateDate;//수정일
    private String testFlag;//unit test 여부
    
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
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
    public Integer getAccountId() {
        return accountId;
    }
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
    public String getAccountName() {
        return accountName;
    }
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
    public String getDeployStatus() {
        return deployStatus;
    }
    public void setDeployStatus(String deployStatus) {
        this.deployStatus = deployStatus;
    }
    public String getIaasType() {
        return iaasType;
    }
    public void setIaasType(String iaasType) {
        this.iaasType = iaasType;
    }
    
    public String getIaasConfigAlias() {
        return iaasConfigAlias;
    }
    public void setIaasConfigAlias(String iaasConfigAlias) {
        this.iaasConfigAlias = iaasConfigAlias;
    }
    public String getCommonSecurityGroup() {
        return commonSecurityGroup;
    }
    public void setCommonSecurityGroup(String commonSecurityGroup) {
        this.commonSecurityGroup = commonSecurityGroup;
    }
    public String getCommonRegion() {
        return commonRegion;
    }
    public void setCommonRegion(String commonRegion) {
        this.commonRegion = commonRegion;
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
    public String getVsphereVcenterVmFolder() {
        return vsphereVcenterVmFolder;
    }
    public void setVsphereVcenterVmFolder(String vsphereVcenterVmFolder) {
        this.vsphereVcenterVmFolder = vsphereVcenterVmFolder;
    }
    public String getVsphereVcenterTemplateFolder() {
        return vsphereVcenterTemplateFolder;
    }
    public void setVsphereVcenterTemplateFolder(String vsphereVcenterTemplateFolder) {
        this.vsphereVcenterTemplateFolder = vsphereVcenterTemplateFolder;
    }
    public String getVsphereVcenterDatastore() {
        return vsphereVcenterDatastore;
    }
    public String getVsphereVcentDataCenterName() {
        return vsphereVcentDataCenterName;
    }
    public void setVsphereVcentDataCenterName(String vsphereVcentDataCenterName) {
        this.vsphereVcentDataCenterName = vsphereVcentDataCenterName;
    }
    public void setVsphereVcenterDatastore(String vsphereVcenterDatastore) {
        this.vsphereVcenterDatastore = vsphereVcenterDatastore;
    }
    public String getVsphereVcenterPersistentDatastore() {
        return vsphereVcenterPersistentDatastore;
    }
    public void setVsphereVcenterPersistentDatastore(String vsphereVcenterPersistentDatastore) {
        this.vsphereVcenterPersistentDatastore = vsphereVcenterPersistentDatastore;
    }
    public String getVsphereVcenterDiskPath() {
        return vsphereVcenterDiskPath;
    }
    public void setVsphereVcenterDiskPath(String vsphereVcenterDiskPath) {
        this.vsphereVcenterDiskPath = vsphereVcenterDiskPath;
    }
    public String getVsphereVcenterCluster() {
        return vsphereVcenterCluster;
    }
    public void setVsphereVcenterCluster(String vsphereVcenterCluster) {
        this.vsphereVcenterCluster = vsphereVcenterCluster;
    }
    public String getAzureResourceGroup() {
        return azureResourceGroup;
    }
    public void setAzureResourceGroup(String azureResourceGroup) {
        this.azureResourceGroup = azureResourceGroup;
    }
    public String getAzureStorageAccountName() {
        return azureStorageAccountName;
    }
    public void setAzureStorageAccountName(String azureStorageAccountName) {
        this.azureStorageAccountName = azureStorageAccountName;
    }
    public String getAzureSshPublicKey() {
        return azureSshPublicKey;
    }
    public void setAzureSshPublicKey(String azureSshPublicKey) {
        this.azureSshPublicKey = azureSshPublicKey;
    }
    public String getAzurePrivateKey() {
        return azurePrivateKey;
    }
    public void setAzurePrivateKey(String azurePrivateKey) {
        this.azurePrivateKey = azurePrivateKey;
    }
    public String getGooglePublicKey() {
        return googlePublicKey;
    }
    public void setGooglePublicKey(String googlePublicKey) {
        this.googlePublicKey = googlePublicKey;
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
    public String getCreateDate() {
        if(createDate == null) {
            return null;
        } else {
            String date1 = format.format(createDate);
            return date1;
        }
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    public String getUpdateDate() {
        if(updateDate == null) {
            return null;
        } else {
            String date1 = format.format(updateDate);
            return date1;
        }
    }
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
    public String getTestFlag() {
        return testFlag;
    }
    public void setTestFlag(String testFlag) {
        this.testFlag = testFlag;
    }
    public String getCommonAvailabilityZone() {
        return commonAvailabilityZone;
    }
    public void setCommonAvailabilityZone(String commonAvailabilityZone) {
        this.commonAvailabilityZone = commonAvailabilityZone;
    }
    public SimpleDateFormat getFormat() {
        return format;
    }
    public void setFormat(SimpleDateFormat format) {
        this.format = format;
    }
    public String getCommonProject() {
        return commonProject;
    }
    public void setCommonProject(String commonProject) {
        this.commonProject = commonProject;
    }
    public String getOpenstackKeystoneVersion() {
        return openstackKeystoneVersion;
    }
    public void setOpenstackKeystoneVersion(String openstackKeystoneVersion) {
        this.openstackKeystoneVersion = openstackKeystoneVersion;
    }
    public String getOpenstackDomain() {
        return openstackDomain;
    }
    public void setOpenstackDomain(String openstackDomain) {
        this.openstackDomain = openstackDomain;
    }
    

}