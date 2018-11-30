package org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao;

import java.util.Date;

public class HbBootstrapCredentialConfigVO {
             
    private Integer recid; //recid
    private Integer id;//id
    private String credentialConfigName;
    private String networkConfigName;
    private String iaasType;
    private String credentialKeyName;
    private String directorPublicIp;
    private String directorPrivateIp;
    private String createUserId;
    private String updateUserId;
    private Date updateDate;
    private Date createDate; //생성 날짜
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
    public String getCredentialConfigName() {
        return credentialConfigName;
    }
    public void setCredentialConfigName(String credentialConfigName) {
        this.credentialConfigName = credentialConfigName;
    }
    public String getIaasType() {
        return iaasType;
    }
    public void setIaasType(String iaasType) {
        this.iaasType = iaasType;
    }
    public String getCredentialKeyName() {
        return credentialKeyName;
    }
    public void setCredentialKeyName(String credentialKeyName) {
        this.credentialKeyName = credentialKeyName;
    }
    public String getDirectorPublicIp() {
        return directorPublicIp;
    }
    public void setDirectorPublicIp(String directorPublicIp) {
        this.directorPublicIp = directorPublicIp;
    }
    public String getDirectorPrivateIp() {
        return directorPrivateIp;
    }
    public void setDirectorPrivateIp(String directorPrivateIp) {
        this.directorPrivateIp = directorPrivateIp;
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
    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    public String getNetworkConfigName() {
        return networkConfigName;
    }
    public void setNetworkConfigName(String networkConfigName) {
        this.networkConfigName = networkConfigName;
    }
}
