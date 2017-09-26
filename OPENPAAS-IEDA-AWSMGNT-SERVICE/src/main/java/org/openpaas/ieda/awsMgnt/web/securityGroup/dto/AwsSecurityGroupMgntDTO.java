package org.openpaas.ieda.awsMgnt.web.securityGroup.dto;

import java.util.HashMap;
import java.util.List;

import javax.validation.constraints.NotNull;

public class AwsSecurityGroupMgntDTO {
    @NotNull
    private Integer accountId; // 계정 아이디
    private String groupId;//그룹 id
    @NotNull
    private String groupName;//그룹 명
    @NotNull
    private String description;//설명
    @NotNull
    private String ingressRuleType;//ingressRule 유형
    private String vpcId;
    private String nameTag;
    private String region;
    private List<HashMap<String, String>> ingressRules;
    
    
    public Integer getAccountId() {
        return accountId;
    }
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
    public String getGroupId() {
        return groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
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
    public String getRegion() {
        return region;
    }
    public void setRegion(String region) {
        this.region = region;
    }
    
    
    
}