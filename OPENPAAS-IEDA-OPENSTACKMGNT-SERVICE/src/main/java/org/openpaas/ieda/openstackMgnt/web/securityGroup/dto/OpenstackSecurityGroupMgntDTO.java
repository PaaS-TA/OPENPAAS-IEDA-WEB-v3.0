package org.openpaas.ieda.openstackMgnt.web.securityGroup.dto;

import java.util.HashMap;
import java.util.List;

import javax.validation.constraints.NotNull;

public class OpenstackSecurityGroupMgntDTO {
    private String securityGroupId;
    private String securityGroupName;
    private Integer accountId;
    private String description;//설명
    @NotNull
    private String ingressRuleType;//ingressRule 유형
    private List<HashMap<String, String>> ingressRules;
    
    public String getSecurityGroupId() {
        return securityGroupId;
    }
    public void setSecurityGroupId(String securityGroupId) {
        this.securityGroupId = securityGroupId;
    }
    public String getSecurityGroupName() {
        return securityGroupName;
    }
    public void setSecurityGroupName(String securityGroupName) {
        this.securityGroupName = securityGroupName;
    }
    public Integer getAccountId() {
        return accountId;
    }
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getIngressRuleType() {
        return ingressRuleType;
    }
    public void setIngressRuleType(String ingressRuleType) {
        this.ingressRuleType = ingressRuleType;
    }
    public List<HashMap<String, String>> getIngressRules() {
        return ingressRules;
    }
    public void setIngressRules(List<HashMap<String, String>> ingressRules) {
        this.ingressRules = ingressRules;
    }
}
