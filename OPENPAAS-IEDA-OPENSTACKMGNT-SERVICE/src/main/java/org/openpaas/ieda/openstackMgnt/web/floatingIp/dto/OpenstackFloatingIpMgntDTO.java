package org.openpaas.ieda.openstackMgnt.web.floatingIp.dto;

public class OpenstackFloatingIpMgntDTO {
    private Integer accountId;
    private String pool;
    public Integer getAccountId() {
        return accountId;
    }
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
    public String getPool() {
        return pool;
    }
    public void setPool(String pool) {
        this.pool = pool;
    }
}
