package org.openpaas.ieda.deploy.web.config.credential.dto;


public class CredentialManagementDTO {
    
    private String id;
    private String credentialName;
    private String credentialKeyName;
    private String directorPublicIp;
    private String directorPrivateIp;
    private String createUserId;
    private String updateUserId;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
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
    
}
