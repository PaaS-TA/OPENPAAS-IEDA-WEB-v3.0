package org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

public class HbCfDeploymentNetworkConfigDTO {
    private Integer recid;
    @NotNull
    private Integer id;
    private String iaasType;
    private String networkName;
    private String createUserId;//등록자 아이디
    private String updateUserId;//수정자 아이디
    private List<?> networkInfoList;
    private Date createDate;//등록일
    private Date updateDate;//수정일
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
    public String getIaasType() {
        return iaasType;
    }
    public void setIaasType(String iaasType) {
        this.iaasType = iaasType;
    }
    public String getNetworkName() {
        return networkName;
    }
    public void setNetworkName(String networkName) {
        this.networkName = networkName;
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
	public List<?> getNetworkInfoList() {
		return networkInfoList;
	}
	public void setNetworkInfoList(List<?> networkInfoList) {
		this.networkInfoList = networkInfoList;
	}


   
}
