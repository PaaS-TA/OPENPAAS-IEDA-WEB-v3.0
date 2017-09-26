package org.openpaas.ieda.iaasDashboard.web.resourceUsage.dao;

public class IaasResourceUsageVO {
    
    private String accountName; //계정 별칭
    private String iaasType; //클라우드 인프라 유형
    private long instance; //인스턴스
    private long network; //네트워크
    private long volume; //볼륨
    private Double billing; //과금
    public String getAccountName() {
        return accountName;
    }
    public String getIaasType() {
        return iaasType;
    }
    public long getInstance() {
        return instance;
    }
    public long getNetwork() {
        return network;
    }
    public long getVolume() {
        return volume;
    }
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
    public void setIaasType(String iaasType) {
        this.iaasType = iaasType;
    }
    public void setInstance(long instance) {
        this.instance = instance;
    }
    public void setNetwork(long network) {
        this.network = network;
    }
    public void setVolume(long volume) {
        this.volume = volume;
    }
    public Double getBilling() {
        return billing;
    }
    public void setBilling(Double billing) {
        this.billing = billing;
    }
    
}
