package org.openpaas.ieda.iaasDashboard.awsMgnt.web.vpc.dto;

import javax.validation.constraints.NotNull;

public class AwsVpcMgntDTO {
    @NotNull
    private Integer accountId; // 계정 아이디
    private String vpcId; // VPC 아이디
    private String nameTag; // VPC 태그 명
    private String region;
    @NotNull
    private String ipv4CirdBlock; // CIDR 블록 형태의 VPC IPv4 주소 범위
    private boolean ipv6CirdBlock; // IPv6 CIDR 블록 사용 여
    private String tenancy; //공유 또는 전용 인스턴스 사용 여부
    
    public Integer getAccountId() {
        return accountId;
    }
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
    public String getVpcId() {
        return vpcId;
    }
    public void setVpcId(String vpcId) {
        this.vpcId = vpcId;
    }
    public String getNameTag() {
        return nameTag;
    }
    public void setNameTag(String nameTag) {
        this.nameTag = nameTag;
    }
    public String getIpv4CirdBlock() {
        return ipv4CirdBlock;
    }
    public void setIpv4CirdBlock(String ipv4CirdBlock) {
        this.ipv4CirdBlock = ipv4CirdBlock;
    }
    public boolean isIpv6CirdBlock() {
        return ipv6CirdBlock;
    }
    public void setIpv6CirdBlock(boolean ipv6CirdBlock) {
        this.ipv6CirdBlock = ipv6CirdBlock;
    }
    public String getTenancy() {
        return tenancy;
    }
    public void setTenancy(String tenancy) {
        this.tenancy = tenancy;
    }
    public String getRegion() {
        return region;
    }
    public void setRegion(String region) {
        this.region = region;
    }
}
