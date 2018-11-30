package org.openpaas.ieda.deploy.web.deploy.bootstrap.dto;

import java.util.Date;


public class BootstrapListDTO {

    private Integer recid; //recid
    private Integer id; //id
    private String iaasConfigAlias;
    private String deployStatus; //배포상태
    private String deploymentName; //배포명
    private String directorName;//디렉터명
    private String iaas; //IaaS
    private String boshRelease; //BOSH 릴리즈
    private String boshCpiRelease; //BOSH CPI 릴리즈
    private String boshBpmRelease; //BOSH BPM 릴리즈
    private String subnetId; //서브넷 ID(NET ID)
    private String subnetRange; //서브넷 범위
    private String publicStaticIp; //디렉터 공인 IP
    private String privateStaticIp; //디렉터 내부 IP
    private String subnetGateway; //게이트웨이
    private String subnetDns; //DNS
    private String ntp; //NTP
    private String stemcell; //스템셀
    private String instanceType; //인스턴스 유형
    private String boshPassword; //VM 비밀번호
    private String deploymentFile; //배포 파일명
    private String deployLog; //배포로그
    private Date createDate; // 생성일자
    private Date updateDate; // 수정일자
    
    
    public Integer getRecid() {
        return recid;
    }

    public Integer getId() {
        return id;
    }

    public String getIaasConfigAlias() {
        return iaasConfigAlias;
    }

    public String getDeployStatus() {
        return deployStatus;
    }

    public String getDeploymentName() {
        return deploymentName;
    }

    public String getDirectorName() {
        return directorName;
    }

    public String getIaas() {
        return iaas;
    }

    public String getBoshRelease() {
        return boshRelease;
    }

    public String getBoshCpiRelease() {
        return boshCpiRelease;
    }

    public String getSubnetId() {
        return subnetId;
    }

    public String getSubnetRange() {
        return subnetRange;
    }

    public String getPublicStaticIp() {
        return publicStaticIp;
    }

    public String getPrivateStaticIp() {
        return privateStaticIp;
    }

    public String getSubnetGateway() {
        return subnetGateway;
    }

    public String getSubnetDns() {
        return subnetDns;
    }

    public String getNtp() {
        return ntp;
    }

    public String getStemcell() {
        return stemcell;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public String getBoshPassword() {
        return boshPassword;
    }

    public String getDeploymentFile() {
        return deploymentFile;
    }

    public String getDeployLog() {
        return deployLog;
    }

    public void setRecid(Integer recid) {
        this.recid = recid;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setIaasConfigAlias(String iaasConfigAlias) {
        this.iaasConfigAlias = iaasConfigAlias;
    }

    public void setDeployStatus(String deployStatus) {
        this.deployStatus = deployStatus;
    }

    public void setDeploymentName(String deploymentName) {
        this.deploymentName = deploymentName;
    }

    public void setDirectorName(String directorName) {
        this.directorName = directorName;
    }

    public void setIaas(String iaas) {
        this.iaas = iaas;
    }

    public void setBoshRelease(String boshRelease) {
        this.boshRelease = boshRelease;
    }

    public void setBoshCpiRelease(String boshCpiRelease) {
        this.boshCpiRelease = boshCpiRelease;
    }

    public void setSubnetId(String subnetId) {
        this.subnetId = subnetId;
    }

    public void setSubnetRange(String subnetRange) {
        this.subnetRange = subnetRange;
    }

    public void setPublicStaticIp(String publicStaticIp) {
        this.publicStaticIp = publicStaticIp;
    }

    public void setPrivateStaticIp(String privateStaticIp) {
        this.privateStaticIp = privateStaticIp;
    }

    public void setSubnetGateway(String subnetGateway) {
        this.subnetGateway = subnetGateway;
    }

    public void setSubnetDns(String subnetDns) {
        this.subnetDns = subnetDns;
    }

    public void setNtp(String ntp) {
        this.ntp = ntp;
    }

    public void setStemcell(String stemcell) {
        this.stemcell = stemcell;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public void setBoshPassword(String boshPassword) {
        this.boshPassword = boshPassword;
    }

    public void setDeploymentFile(String deploymentFile) {
        this.deploymentFile = deploymentFile;
    }

    public void setDeployLog(String deployLog) {
        this.deployLog = deployLog;
    }

    public void setUpdateDate(Date updateDate) {
        if(updateDate == null) {
            this.updateDate = null;
        } else {
            this.updateDate = new Date(updateDate.getTime());
        }
    }

    public Date getUpdateDate() {
        if(updateDate == null) {
            return null;
        } else {
            return new Date(updateDate.getTime());
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

    public String getBoshBpmRelease() {
        return boshBpmRelease;
    }

    public void setBoshBpmRelease(String boshBpmRelease) {
        this.boshBpmRelease = boshBpmRelease;
    }
    
    
    
}