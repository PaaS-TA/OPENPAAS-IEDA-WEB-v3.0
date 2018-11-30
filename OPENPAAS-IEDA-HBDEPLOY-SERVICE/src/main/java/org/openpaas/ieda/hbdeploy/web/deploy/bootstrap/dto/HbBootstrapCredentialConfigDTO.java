package org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto;

import javax.validation.constraints.NotNull;

public class HbBootstrapCredentialConfigDTO {
    
    private String id;
    @NotNull
    private String credentialConfigName;
    @NotNull
    private String iaasType;
    private String credentialKeyName;
    private String createUserId;
    private String updateUserId;
    @NotNull
    private String networkConfigName;
    private String directorPublicIp;
    private String directorPrivateIp;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
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
    public String getNetworkConfigName() {
        return networkConfigName;
    }
    public void setNetworkConfigName(String networkConfigName) {
        this.networkConfigName = networkConfigName;
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

}
