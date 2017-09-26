package org.openpaas.ieda.deploy.web.config.stemcell.dao;

import java.util.Date;

public class StemcellManagementVO {
    
    private Integer recid; //recid
    private Integer id;//id
    private String stemcellName;
    private String stemcellUrl; // 스템셀 url
    private String size; //size
    private String os; //운영체제
    private String osVersion; //버전
    private String iaas; //IaaS
    private String stemcellFileName; //파일명
    private String stemcellVersion; //스템셀 버전
    private String isExisted; //다운로드 여부
    private String isDose; //다운로드 진행 여부
    private String downloadStatus; //다운로드 상태
    private String downloadLink;
    private String createUserId; //생성한 사용자
    private Date createDate; //생성 날짜
    private String updateUserId; //수정한 사용자
    private Date updateDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRecid() {
        return recid;
    }
    
    public void setRecid(Integer recid) {
        this.recid = recid;
    }
    
    public String getStemcellName() {
        return stemcellName;
    }

    public void setStemcellName(String stemcellName) {
        this.stemcellName = stemcellName;
    }

    public String getStemcellUrl() {
        return stemcellUrl;
    }

    public void setStemcellUrl(String stemcellUrl) {
        this.stemcellUrl = stemcellUrl;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getIaas() {
        return iaas;
    }

    public void setIaas(String iaas) {
        this.iaas = iaas;
    }

    public String getStemcellFileName() {
        return stemcellFileName;
    }

    public void setStemcellFileName(String stemcellFileName) {
        this.stemcellFileName = stemcellFileName;
    }

    public String getStemcellVersion() {
        return stemcellVersion;
    }

    public void setStemcellVersion(String stemcellVersion) {
        this.stemcellVersion = stemcellVersion;
    }

    public String getIsExisted() {
        return isExisted;
    }

    public void setIsExisted(String isExisted) {
        this.isExisted = isExisted;
    }

    public String getIsDose() {
        return isDose;
    }

    public void setIsDose(String isDose) {
        this.isDose = isDose;
    }

    public String getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(String downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public Date getCreateDate() {
        if(createDate == null) {
            return null;
        } else {
            return new Date(createDate.getTime());
        }
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public Date getUpdateDate() {
        if(updateDate == null) {
            return null;
        } else {
            return new Date(updateDate.getTime());
        }
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public void setCreateDate(Date createDate) {
        if(createDate == null) {
            this.createDate = null;
        } else {
            this.createDate = new Date(createDate.getTime());
        }
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public void setUpdateDate(Date updateDate) {
        if(updateDate == null) {
            this.updateDate = null;
        } else {
            this.updateDate = new Date(updateDate.getTime());
        }
    }
    
    
}
