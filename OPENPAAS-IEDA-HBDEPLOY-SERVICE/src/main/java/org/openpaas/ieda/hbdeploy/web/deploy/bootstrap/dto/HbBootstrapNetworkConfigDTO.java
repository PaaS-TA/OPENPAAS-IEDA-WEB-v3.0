package org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto;

import javax.validation.constraints.NotNull;

public class HbBootstrapNetworkConfigDTO {
    private String id;
    @NotNull
    private String networkConfigName; // 네트워크 별칭
    @NotNull
    private String iaasType; // 클라우드 인프라 환경 타입
    @NotNull
    private String subnetId; // 네트워크id
    @NotNull
    private String privateStaticIp; // 디렉터 내부 ip
    @NotNull
    private String subnetRange; // 서브넷 범위
    @NotNull
    private String subnetGateway; // 게이트웨이
    @NotNull
    private String subnetDns; // DNS 서버 주소
    @NotNull
    private String publicStaticIp; //디렉터 공인 ip
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getNetworkConfigName() {
        return networkConfigName;
    }
    public void setNetworkConfigName(String networkConfigName) {
        this.networkConfigName = networkConfigName;
    }
    public String getIaasType() {
        return iaasType;
    }
    public void setIaasType(String iaasType) {
        this.iaasType = iaasType;
    }
    public String getSubnetId() {
        return subnetId;
    }
    public void setSubnetId(String subnetId) {
        this.subnetId = subnetId;
    }
    public String getPrivateStaticIp() {
        return privateStaticIp;
    }
    public void setPrivateStaticIp(String privateStaticIp) {
        this.privateStaticIp = privateStaticIp;
    }
    public String getSubnetRange() {
        return subnetRange;
    }
    public void setSubnetRange(String subnetRange) {
        this.subnetRange = subnetRange;
    }
    public String getSubnetGateway() {
        return subnetGateway;
    }
    public void setSubnetGateway(String subnetGateway) {
        this.subnetGateway = subnetGateway;
    }
    public String getSubnetDns() {
        return subnetDns;
    }
    public void setSubnetDns(String subnetDns) {
        this.subnetDns = subnetDns;
    }
    public String getPublicStaticIp() {
        return publicStaticIp;
    }
    public void setPublicStaticIp(String publicStaticIp) {
        this.publicStaticIp = publicStaticIp;
    }
    
    
}
