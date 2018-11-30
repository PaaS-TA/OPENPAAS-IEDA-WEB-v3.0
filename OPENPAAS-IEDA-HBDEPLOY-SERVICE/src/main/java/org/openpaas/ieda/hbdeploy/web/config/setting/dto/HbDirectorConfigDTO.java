package org.openpaas.ieda.hbdeploy.web.config.setting.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
public class HbDirectorConfigDTO {
    
    Integer iedaDirectorConfigSeq;
    @NotBlank
    @Size(min=4)
    private String  userId; //계정
    @NotBlank
    @Size(min=4)
    private String  userPassword; //비밀번호
    @NotBlank
    private String  directorUrl; //url
    @NotNull
    private Integer directorPort; //포트번호
    private String directorType;
    private String credentialFile; //Credential File 이름
    
    public Integer getIedaDirectorConfigSeq() {
        return iedaDirectorConfigSeq;
    }
    public void setIedaDirectorConfigSeq(Integer iedaDirectorConfigSeq) {
        this.iedaDirectorConfigSeq = iedaDirectorConfigSeq;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserPassword() {
        return userPassword;
    }
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
    public String getDirectorUrl() {
        return directorUrl;
    }
    public void setDirectorUrl(String directorUrl) {
        this.directorUrl = directorUrl;
    }
    public Integer getDirectorPort() {
        return directorPort;
    }
    public void setDirectorPort(Integer directorPort) {
        this.directorPort = directorPort;
    }
    public String getCredentialFile() {
        return credentialFile;
    }
    public void setCredentialFile(String credentialFile) {
        this.credentialFile = credentialFile;
    }
    public String getDirectorType() {
        return directorType;
    }
    public void setDirectorType(String directorType) {
        this.directorType = directorType;
    }

}
