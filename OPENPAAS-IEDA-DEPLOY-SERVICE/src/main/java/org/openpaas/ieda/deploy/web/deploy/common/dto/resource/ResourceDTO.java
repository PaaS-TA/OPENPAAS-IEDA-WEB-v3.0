package org.openpaas.ieda.deploy.web.deploy.common.dto.resource;

import javax.validation.constraints.NotNull;

public class ResourceDTO {
    private String id; //cfId
    private String cfId;//diegoId
    @NotNull
    private String iaas; //IaaS
    @NotNull
    private String platform; //플랫폼 구분
    // 5. 리소스 정보
    private String stemcellName; //스템셀명
    private String stemcellVersion; //스템셀버전
    private String boshPassword; //VM 비밀번호
    private String smallFlavor;//small 인스턴스 유형
    private String mediumFlavor;//medium 인스턴스 유형
    private String largeFlavor;//large 인스턴스 유형
    private String runnerFlavor;//runner 인스턴스 유형
    private String smallCpu; //small 인스턴스 유형 Cpu
    private String smallRam;//small 인스턴스 유형 Ram
    private String smallDisk;//small 인스턴스 유형 Disk
    private String mediumCpu;//medium 인스턴스 유형 Cpu
    private String mediumRam;//medium 인스턴스 유형 Ram
    private String mediumDisk;//medium 인스턴스 유형 Disk
    private String largeCpu;//large 인스턴스 유형 Cpu
    private String largeRam;//large 인스턴스 유형 Ram
    private String largeDisk;//large 인스턴스 유형 Disk
    
    private String keyFile;//key파일명
    private String enableWindowsStemcell; //windows stemcell use
    private String windowsStemcellName; // Azure IaaS Windows 스템셀 명
    private String windowsStemcellVersion; // Azure IaaS windows 스템셀 버전
    private String windowsCellInstance; //windows Cell Instance 수
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getCfId() {
        return cfId;
    }
    public void setCfId(String cfId) {
        this.cfId = cfId;
    }
    public String getIaas() {
        return iaas;
    }
    public void setIaas(String iaas) {
        this.iaas = iaas;
    }
    public String getPlatform() {
        return platform;
    }
    public void setPlatform(String platform) {
        this.platform = platform;
    }
    public String getStemcellName() {
        return stemcellName;
    }
    public void setStemcellName(String stemcellName) {
        this.stemcellName = stemcellName;
    }
    public String getStemcellVersion() {
        return stemcellVersion;
    }
    public void setStemcellVersion(String stemcellVersion) {
        this.stemcellVersion = stemcellVersion;
    }
    public String getBoshPassword() {
        return boshPassword;
    }
    public void setBoshPassword(String boshPassword) {
        this.boshPassword = boshPassword;
    }
    public String getSmallFlavor() {
        return smallFlavor;
    }
    public void setSmallFlavor(String smallFlavor) {
        this.smallFlavor = smallFlavor;
    }
    public String getMediumFlavor() {
        return mediumFlavor;
    }
    public void setMediumFlavor(String mediumFlavor) {
        this.mediumFlavor = mediumFlavor;
    }
    public String getLargeFlavor() {
        return largeFlavor;
    }
    public void setLargeFlavor(String largeFlavor) {
        this.largeFlavor = largeFlavor;
    }
    public String getRunnerFlavor() {
        return runnerFlavor;
    }
    public void setRunnerFlavor(String runnerFlavor) {
        this.runnerFlavor = runnerFlavor;
    }
    public String getSmallCpu() {
        return smallCpu;
    }
    public void setSmallCpu(String smallCpu) {
        this.smallCpu = smallCpu;
    }
    public String getSmallRam() {
        return smallRam;
    }
    public void setSmallRam(String smallRam) {
        this.smallRam = smallRam;
    }
    public String getSmallDisk() {
        return smallDisk;
    }
    public void setSmallDisk(String smallDisk) {
        this.smallDisk = smallDisk;
    }
    public String getMediumCpu() {
        return mediumCpu;
    }
    public void setMediumCpu(String mediumCpu) {
        this.mediumCpu = mediumCpu;
    }
    public String getMediumRam() {
        return mediumRam;
    }
    public void setMediumRam(String mediumRam) {
        this.mediumRam = mediumRam;
    }
    public String getMediumDisk() {
        return mediumDisk;
    }
    public void setMediumDisk(String mediumDisk) {
        this.mediumDisk = mediumDisk;
    }
    public String getLargeCpu() {
        return largeCpu;
    }
    public void setLargeCpu(String largeCpu) {
        this.largeCpu = largeCpu;
    }
    public String getLargeRam() {
        return largeRam;
    }
    public void setLargeRam(String largeRam) {
        this.largeRam = largeRam;
    }
    public String getLargeDisk() {
        return largeDisk;
    }
    public void setLargeDisk(String largeDisk) {
        this.largeDisk = largeDisk;
    }
    public String getKeyFile() {
        return keyFile;
    }
    public void setKeyFile(String keyFile) {
        this.keyFile = keyFile;
    }
    
    public String getEnableWindowsStemcell() {
        return enableWindowsStemcell;
    }
    public void setEnableWindowsStemcell(String enableWindowsStemcell) {
        this.enableWindowsStemcell = enableWindowsStemcell;
    }
    public String getWindowsStemcellName() {
        return windowsStemcellName;
    }
    public void setWindowsStemcellName(String windowsStemcellName) {
        this.windowsStemcellName = windowsStemcellName;
    }
    public String getWindowsStemcellVersion() {
        return windowsStemcellVersion;
    }
    public void setWindowsStemcellVersion(String windowsStemcellVersion) {
        this.windowsStemcellVersion = windowsStemcellVersion;
    }
    public String getWindowsCellInstance() {
        return windowsCellInstance;
    }
    public void setWindowsCellInstance(String windowsCellInstance) {
        this.windowsCellInstance = windowsCellInstance;
    }
    
}
