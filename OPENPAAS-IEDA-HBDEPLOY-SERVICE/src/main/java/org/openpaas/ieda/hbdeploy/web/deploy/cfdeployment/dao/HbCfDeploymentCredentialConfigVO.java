package org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao;

import java.util.Date;

import javax.validation.constraints.NotNull;

public class HbCfDeploymentCredentialConfigVO {
    private Integer id;
    private Integer recid;
    @NotNull
    private String credentialConfigName;
    private String credentialConfigKeyFileName;
    private String releaseName;
    private String releaseVersion;
    private String iaasType; // 클라우드 인프라 환경 타입
    private String domain;
    private String countryCode;
    private String city;
    private String company;
    private String jobTitle;
    private String emailAddress;
    private String createUserId;
    private String updateUserId;
    private Date createDate;
    private Date updateDate;
    
    public String getJobTitle() {
        return jobTitle;
    }
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
    public Integer getRecid() {
        return recid;
    }
    public void setRecid(Integer recid) {
        this.recid = recid;
    }
    public String getEmailAddress() {
        return emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    public String getCredentialConfigName() {
        return credentialConfigName;
    }
    public void setCredentialConfigName(String credentialConfigName) {
        this.credentialConfigName = credentialConfigName;
    }
    public String getCredentialConfigKeyFileName() {
        return credentialConfigKeyFileName;
    }
    public void setCredentialConfigKeyFileName(String credentialConfigKeyFileName) {
        this.credentialConfigKeyFileName = credentialConfigKeyFileName;
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
    public String getIaasType() {
        return iaasType;
    }
    public void setIaasType(String iaasType) {
        this.iaasType = iaasType;
    }
    private String email;
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getDomain() {
        return domain;
    }
    public void setDomain(String domain) {
        this.domain = domain;
    }
    public String getCountryCode() {
        return countryCode;
    }
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getCompany() {
        return company;
    }
    public void setCompany(String company) {
        this.company = company;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
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
}
