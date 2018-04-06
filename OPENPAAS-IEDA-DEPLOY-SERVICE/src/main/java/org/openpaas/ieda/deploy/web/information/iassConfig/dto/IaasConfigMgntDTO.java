package org.openpaas.ieda.deploy.web.information.iassConfig.dto;

import javax.validation.constraints.NotNull;

public class IaasConfigMgntDTO {
   
    
    private String id; //id
    @NotNull
    private String iaasType; //iaasType
    private String accountId; //계정 별칭
    @NotNull
    private String iaasConfigAlias; //환경 설정 별칭
    private String commonSecurityGroup;//공통 시큐리티 그룹 명
    private String commonRegion; //region
    private String commonKeypairName; //Key Pair 이름
    private String commonKeypairPath; // Key Pair 경로
    private String commonAvailabilityZone; //공통 영역
    private String vsphereVcenterDataCenterName;    
    private String vsphereVcenterVmFolder; //vCenter
    private String vsphereVcenterTemplateFolder; //vCenter
    private String vsphereVcenterDatastore; //vCenter 데이터 스토어 
    private String vsphereVcenterPersistentDatastore; // vCenter 영구 데이터 스토어 
    private String vsphereVcenterDiskPath; //vCenter 디스크 경로
    private String vsphereVcenterCluster; //vCenter 클러스터
    private String azureResourceGroup;
    private String azureStorageAccountName;
    private String azureSshPublicKey;
    private String azurePrivateKey;
    private String testFlag;
    private String googlePublicKey; //구글 Public Key
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getIaasType() {
        return iaasType;
    }
    public void setIaasType(String iaasType) {
        this.iaasType = iaasType;
    }
    public String getAccountId() {
        return accountId;
    }
    public void setAccountId(String accountId) {
        this.accountId = accountId;
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
    public String getVsphereVcenterDataCenterName() {
        return vsphereVcenterDataCenterName;
    }
    public void setVsphereVcenterDataCenterName(String vsphereVcenterDataCenterName) {
        this.vsphereVcenterDataCenterName = vsphereVcenterDataCenterName;
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
    public String getGooglePublicKey() {
        return googlePublicKey;
    }
    public void setGooglePublicKey(String googlePublicKey) {
        this.googlePublicKey = googlePublicKey;
    }

}