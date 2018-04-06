package org.openpaas.ieda.iaasDashboard.web.account.dto;

import javax.validation.constraints.NotNull;

public class IaasAccountMgntDTO {
    private String id; //id
    @NotNull
    private String iaasType; //iaasType
    @NotNull
    private String accountName;//계정 별칭
    private String commonAccessEndpoint;//공통 인프라 접근 로그인 주소
    private String commonAccessUser;//공통 인프라 접근 아이디
    private String commonAccessSecret;//공통 인프라 접근 비밀번호
    private String openstackKeystoneVersion;//오픈스택 키스톤 버전
    private String commonTenant;//공통 테넌트
    private String commonProject;//공통 프로젝트
    private String openstackDomain;//오픈스택 도메인
    private String googleJsonKeyPath;//google json 키 파일
    private String azureSubscriptionId; //azure 구독 아이디
    private String defaultYn;
    private String testFlag;
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
    public String getAccountName() {
        return accountName;
    }
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
    public String getCommonAccessEndpoint() {
        return commonAccessEndpoint;
    }
    public void setCommonAccessEndpoint(String commonAccessEndpoint) {
        this.commonAccessEndpoint = commonAccessEndpoint;
    }
    public String getCommonAccessUser() {
        return commonAccessUser;
    }
    public void setCommonAccessUser(String commonAccessUser) {
        this.commonAccessUser = commonAccessUser;
    }
    public String getCommonAccessSecret() {
        return commonAccessSecret;
    }
    public void setCommonAccessSecret(String commonAccessSecret) {
        this.commonAccessSecret = commonAccessSecret;
    }
    public String getOpenstackKeystoneVersion() {
        return openstackKeystoneVersion;
    }
    public void setOpenstackKeystoneVersion(String openstackKeystoneVersion) {
        this.openstackKeystoneVersion = openstackKeystoneVersion;
    }
    public String getCommonTenant() {
        return commonTenant;
    }
    public void setCommonTenant(String commonTenant) {
        this.commonTenant = commonTenant;
    }
    public String getOpenstackDomain() {
        return openstackDomain;
    }
    public void setOpenstackDomain(String openstackDomain) {
        this.openstackDomain = openstackDomain;
    }
    public String getDefaultYn() {
        return defaultYn;
    }
    public void setDefaultYn(String defaultYn) {
        this.defaultYn = defaultYn;
    }
    public String getTestFlag() {
        return testFlag;
    }
    public void setTestFlag(String testFlag) {
        this.testFlag = testFlag;
    }
    public String getGoogleJsonKeyPath() {
        return googleJsonKeyPath;
    }
    public String getCommonProject() {
        return commonProject;
    }
    public void setCommonProject(String commonProject) {
        this.commonProject = commonProject;
    }
    public void setGoogleJsonKeyPath(String googleJsonKeyPath) {
        this.googleJsonKeyPath = googleJsonKeyPath;
    }
	public String getAzureSubscriptionId() {
		return azureSubscriptionId;
	}
	public void setAzureSubscriptionId(String azureSubscriptionId) {
		this.azureSubscriptionId = azureSubscriptionId;
	}
    
}
