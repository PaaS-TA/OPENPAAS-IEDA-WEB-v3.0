package org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto;

import javax.validation.constraints.NotNull;

public class HbBootstrapResourceConfigDTO {
    private Integer id;
    @NotNull
    private String resourceConfigName; // 리소스 별칭
    @NotNull
    private String iaasType; // 클라우드 인프라 환경 타입
    private String stemcellName;
    private String instanceType;
    private String vmPassword;
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getResourceConfigName() {
        return resourceConfigName;
    }
    public void setResourceConfigName(String resourceConfigName) {
        this.resourceConfigName = resourceConfigName;
    }
    public String getIaasType() {
        return iaasType;
    }
    public void setIaasType(String iaasType) {
        this.iaasType = iaasType;
    }
    public String getStemcellName() {
        return stemcellName;
    }
    public void setStemcellName(String stemcellName) {
        this.stemcellName = stemcellName;
    }

	public String getInstanceType() {
		return instanceType;
	}
	public void setInstanceType(String instanceType) {
		this.instanceType = instanceType;
	}
	public String getVmPassword() {
        return vmPassword;
    }
    public void setVmPassword(String vmPassword) {
        this.vmPassword = vmPassword;
    }
}
