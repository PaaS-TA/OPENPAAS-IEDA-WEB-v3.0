package org.openpaas.ieda.deploy.web.config.credential.dao;

import java.util.Date;

public class CredentialManagementVO {
    private Integer recid; //recid
    private Integer id;//id
    private String credentialName;
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
    public String getCredentialName() {
        return credentialName;
    }
    public void setCredentialName(String credentialName) {
        this.credentialName = credentialName;
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
        if(updateDate == null) {
            return null;
        } else {
            return new Date(updateDate.getTime());
        }
    }
    public void setUpdateDate(Date updateDate) {
        if(updateDate == null) {
            this.updateDate = null;
        } else {
            this.updateDate = new Date(updateDate.getTime());
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
    
    
}
