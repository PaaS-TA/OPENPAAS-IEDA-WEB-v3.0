package org.openpaas.ieda.iaasDashboard.web.common.dao;

public class CommonCodeVO {
    
    private String subGroupCode; //서브 그룹(상)
    private String parentCode; // 상위 codeIdx\
    private String codeDescription;//코드 설명
    private String codeName;//코드명
    private String codeNameKr;//코드명 한글
    private String codeValue;//코드값
    private String uSubGroupCode;//서브 그룹(하)
    
    public String getSubGroupCode() {
        return subGroupCode;
    }
    public void setSubGroupCode(String subGroupCode) {
        this.subGroupCode = subGroupCode;
    }
    public String getParentCode() {
        return parentCode;
    }
    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }
    public String getCodeDescription() {
        return codeDescription;
    }
    public void setCodeDescription(String codeDescription) {
        this.codeDescription = codeDescription;
    }
    public String getCodeName() {
        return codeName;
    }
    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }
    public String getCodeNameKr() {
        return codeNameKr;
    }
    public void setCodeNameKr(String codeNameKr) {
        this.codeNameKr = codeNameKr;
    }
    public String getCodeValue() {
        return codeValue;
    }
    public void setCodeValue(String codeValue) {
        this.codeValue = codeValue;
    }
    public String getuSubGroupCode() {
        return uSubGroupCode;
    }
    public void setuSubGroupCode(String uSubGroupCode) {
        this.uSubGroupCode = uSubGroupCode;
    }
   
}
