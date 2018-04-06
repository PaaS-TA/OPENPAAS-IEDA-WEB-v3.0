package org.openpaas.ieda.iaasDashboard.web.account.dao;

import java.util.Date;

public class IaasAccountMgntVO {
    private Integer recid;
    private Integer id; //id
    private String iaasType;//클라우드 인프라 유형
    private String status;//활성화 상태
    private String accountName;//계정 별칭
    private String commonAccessEndpoint;//공통 인프라 접근 로그인 주소
    private String commonAccessUser;//공통 인프라 접근 아이디
    private String commonAccessSecret;//공통 인프라 접근 비밀번호
    private String openstackKeystoneVersion;//오픈스택 키스톤 버전
    private String commonTenant;//공통 테넌트
    private String commonProject;//공통 프로젝트 아이디
    private String openstackDomain;//오픈스택 도메인
    private String googleJsonKeyPath;//google json 키 파일
    private String azureSubscriptionId; //azure 구독 아이디
    private String defaultYn;
    private String createUserId;//등록자 아이디
    private String updateUserId;//수정자 아이디
    private Date createDate;//등록일
    private Date updateDate;//수정일
    private String testFlag;
    
    public Integer getRecid() {
        return recid;
    }
    public Integer getId() {
        return id;
    }
    public String getIaasType() {
        return iaasType;
    }
    public String getStatus() {
        return status;
    }
    public String getAccountName() {
        return accountName;
    }
    public String getCommonAccessEndpoint() {
        return commonAccessEndpoint;
    }
    public String getCommonAccessUser() {
        return commonAccessUser;
    }
    public String getCommonAccessSecret() {
        return commonAccessSecret;
    }
    public String getOpenstackKeystoneVersion() {
        return openstackKeystoneVersion;
    }
    public String getCommonTenant() {
        return commonTenant;
    }
    public String getOpenstackDomain() {
        return openstackDomain;
    }
    public String getDefaultYn() {
        return defaultYn;
    }
    public void setDefaultYn(String defaultYn) {
        this.defaultYn = defaultYn;
    }
    public String getCreateUserId() {
        return createUserId;
    }
    public String getUpdateUserId() {
        return updateUserId;
    }
    public Date getCreateDate() {
        if(createDate == null) {
            return null;
        } else {
            return new Date(createDate.getTime());
        }
    }
    public Date getUpdateDate() {
        if(updateDate == null) {
            return null;
        } else {
            return new Date(updateDate.getTime());
        }
    }
    public void setRecid(Integer recid) {
        this.recid = recid;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public void setIaasType(String iaasType) {
        this.iaasType = iaasType;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
    public void setCommonAccessEndpoint(String commonAccessEndpoint) {
        this.commonAccessEndpoint = commonAccessEndpoint;
    }
    public void setCommonAccessUser(String commonAccessUser) {
        this.commonAccessUser = commonAccessUser;
    }
    public void setCommonAccessSecret(String commonAccessSecret) {
        this.commonAccessSecret = commonAccessSecret;
    }
    public void setOpenstackKeystoneVersion(String openstackKeystoneVersion) {
        this.openstackKeystoneVersion = openstackKeystoneVersion;
    }
    public void setCommonTenant(String commonTenant) {
        this.commonTenant = commonTenant;
    }
    public void setOpenstackDomain(String openstackDomain) {
        this.openstackDomain = openstackDomain;
    }
    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }
    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }
    public void setCreateDate(Date createDate) {
        if(createDate == null) {
            this.createDate = null;
        } else {
            this.createDate = new Date(createDate.getTime());
        }
    }
    public void setUpdateDate(Date updateDate) {
        if(updateDate == null) {
            this.updateDate = null;
        } else {
            this.updateDate = new Date(updateDate.getTime());
        }
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
    public void setGoogleJsonKeyPath(String googleJsonKeyPath) {
        this.googleJsonKeyPath = googleJsonKeyPath;
    }
    public String getAzureSubscriptionId() {
		return azureSubscriptionId;
	}
	public void setAzureSubscriptionId(String azureSubscriptionId) {
		this.azureSubscriptionId = azureSubscriptionId;
	}
	public String getCommonProject() {
        return commonProject;
    }
    public void setCommonProject(String commonProject) {
        this.commonProject = commonProject;
    }
    
}
