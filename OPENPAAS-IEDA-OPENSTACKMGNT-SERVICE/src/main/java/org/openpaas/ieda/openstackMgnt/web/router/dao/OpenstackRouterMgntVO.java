package org.openpaas.ieda.openstackMgnt.web.router.dao;
 
import org.openstack4j.model.network.State;
 
public class OpenstackRouterMgntVO {
 
    private Integer recid;
    private Integer accountId;
    private String routerName;
    private String routeId;
    private State status;
    private String externalNetwork;
    /************sub net**********************/
    private String networkId;
    private String subnetId;
    private String subnetName;
    private String subnetFixedIps;
    private State subnetStatus;
    private String subnetType;
    private boolean subnetAdminStateUp;
     
     
    public Integer getAccountId() {
        return accountId;
    }
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
    public String getRouterName() {
        return routerName;
    }
    public void setRouterName(String routerName) {
        this.routerName = routerName;
    }
    public String getRouteId() {
        return routeId;
    }
    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }
    public State getStatus() {
        return status;
    }
    public void setStatus(State status) {
        this.status = status;
    }
    public String getExternalNetwork() {
        return externalNetwork;
    }
    public void setExternalNetwork(String externalNetwork) {
        this.externalNetwork = externalNetwork;
    }
    public Integer getRecid() {
        return recid;
    }
    public void setRecid(Integer recid) {
        this.recid = recid;
    }
    /*********************************************/
    public String getNetworkId() {
        return networkId;
    }
    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }
    public String getSubnetId() {
        return subnetId;
    }
    public void setSubnetId(String subnetId) {
        this.subnetId = subnetId;
    }
    public String getSubnetName() {
        return subnetName;
    }
    public void setSubnetName(String subnetName) {
        this.subnetName = subnetName;
    }
    public String getSubnetFixedIps() {
        return subnetFixedIps;
    }
    public void setSubnetFixedIps(String subnetFixedIps) {
        this.subnetFixedIps = subnetFixedIps;
    }
    public State getSubnetStatus() {
        return subnetStatus;
    }
    public void setSubnetStatus(State subnetStatus) {
        this.subnetStatus = subnetStatus;
    }
    public String getSubnetType() {
        return subnetType;
    }
    public void setSubnetType(String subnetType) {
        this.subnetType = subnetType;
    }
    public boolean getSubnetAdminStateUp() {
        return subnetAdminStateUp;
    }
    public void setSubnetAdminStateUp(boolean subnetAdminStateUp) {
        this.subnetAdminStateUp = subnetAdminStateUp;
    }
     
}