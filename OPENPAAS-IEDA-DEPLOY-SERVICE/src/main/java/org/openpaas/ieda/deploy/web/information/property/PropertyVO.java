package org.openpaas.ieda.deploy.web.information.property;

public class PropertyVO {

    private int recid;  //Property 번호
    private String name; //Property 명
    private String value; //Property 값
    private String deploymentName; //배포 명
    
    public int getRecid() {
        return recid;
    }
    public void setRecid(int recid) {
        this.recid = recid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getDeploymentName() {
        return deploymentName;
    }
    public void setDeploymentName(String deploymentName) {
        this.deploymentName = deploymentName;
    }
}
