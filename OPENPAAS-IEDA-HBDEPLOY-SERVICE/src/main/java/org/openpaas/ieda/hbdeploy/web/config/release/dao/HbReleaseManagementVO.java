package org.openpaas.ieda.hbdeploy.web.config.release.dao;

import org.openpaas.ieda.deploy.web.config.systemRelease.dao.ReleaseManagementVO;

public class HbReleaseManagementVO extends ReleaseManagementVO{

    private String iaasType;

    public String getIaasType() {
        return iaasType;
    }

    public void setIaasType(String iaasType) {
        this.iaasType = iaasType;
    }
    
    
}
