package org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto;

import javax.validation.constraints.NotNull;

public class HbBootstrapCpiConfigDTO {
    
    private String cpiInfoId;
    private String iaasType;
    @NotNull
    private String iaasConfigId;
    private String cpiName;
    public String getCpiInfoId() {
        return cpiInfoId;
    }
    public void setCpiInfoId(String cpiInfoId) {
        this.cpiInfoId = cpiInfoId;
    }
    public String getIaasType() {
        return iaasType;
    }
    public String getIaasConfigId() {
        return iaasConfigId;
    }
    public void setIaasType(String iaasType) {
        this.iaasType = iaasType;
    }
    public void setIaasConfigId(String iaasConfigId) {
        this.iaasConfigId = iaasConfigId;
    }
    public String getCpiName() {
        return cpiName;
    }
    public void setCpiName(String cpiName) {
        this.cpiName = cpiName;
    }
}
