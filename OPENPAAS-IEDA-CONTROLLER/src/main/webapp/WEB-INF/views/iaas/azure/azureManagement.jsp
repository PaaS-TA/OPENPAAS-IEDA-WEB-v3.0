<%
/* =================================================================
 * 작성일 : 2018.03.00
 * 작성자 : 이정윤
 * 상세설명 : Azure 관리 화면
 * =================================================================
 */ 
%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix = "spring" uri = "http://www.springframework.org/tags" %>

<script>
var bDefaultAccount = "";
var flag = true;
$(function() {
    if( $("#azureMgntWrap").find("ul").length == 0 ){
           $("#infra_azure_mgnt_nowrap").show();
    }
    // azure 클라우드 인프라 계정 정보 조회 메소드 호출
    bDefaultAccount = setDefaultIaasAccountList("azure");
});

function doSearch(){ }

/********************************************************
 * 기능 : hover
 * 설명 : mouse hover event
 *********************************************************/
function hover(event, val){
    var src = "<c:url value='images/azureMgnt/"+val+".png'/>";
    $($(event).find("img")).attr("src",src);
}

/********************************************************
 * 기능 : unhover
 * 설명 : mouse unhover event
 *********************************************************/
function unhover(event, val){
    var src = "<c:url value='images/azureMgnt/"+val+".png'/>";
    $($(event).find("img")).attr("src",src);
}
</script>
<div id="azureMain" >
    <div class="page_site">인프라 관리 > <strong>azure 관리 </strong></div>
    <div class="pdt20" style="float:left">
        <div class="fl" style="width:100%;padding-bottom: 20px;border-bottom: 1px solid gray;">
             <label  style="font-size:14px">azure 계정 명</label>
             &nbsp;&nbsp;&nbsp;
             <select name="accountId" id="setAccountList" class="select" style="width: 300px; font-size: 15px; height: 32px;" onchange="setAccountInfo(this.value, 'azure');">
             </select>
             &nbsp;&nbsp;&nbsp;
             <span id="doSearch" onclick="setDefaultIaasAccount('noPopup','azure');" class="btn btn-info" style="width:80px" >선택</span>
        </div> 
        <div class="pdt20" style="width:100%; float:left;">
            <div class="title fl">azure 관리 메뉴</div>
        </div>
        <div id="azureMgntWrap" >
            <ul>
                <sec:authorize access="hasAuthority('AZURE_RESOURCE_GROUP_MENU')"> 
                    <li class="azureMgntDiv" onmouseover="hover(this,'az_resourceGroup_2');" onmouseout="unhover(this,'az_resourceGroup_1');" onclick="javascript:goPage('<c:url value="/azureMgnt/resourceGroup"/>', 'Resource Group');">
                        <ul>
                             <li class="azure-li"><span class="azure-li-span1">Resource Group</span></li>
                             <li>
                                 <ul style="margin-top:59px">
                                     <li style="margin-bottom:22px;">
                                         <img src='<c:url value="images/azureMgnt/az_resourceGroup_1.png"/>' class="azure-icon" alt="azure"><span></span>
                                     </li>
                                 </ul>
                             </li>
                         </ul>
                    </li>
                 </sec:authorize> 
                 <sec:authorize access="hasAuthority('AZURE_NETWORK_MENU')"> 
                    <li class="azureMgntDiv" onmouseover="hover(this,'az_network_2');" onmouseout="unhover(this,'az_network_1');" onclick="javascript:goPage('<c:url value="/azureMgnt/network"/>', 'Virtual Network');">
                        <ul>
                             <li class="azure-li"><span class="azure-li-span1">Virtual Network & Subnets </span></li>
                             <li>
                                 <ul style="margin-top:59px">
                                     <li style="margin-bottom:22px;">
                                         <img src='<c:url value="images/azureMgnt/az_network_1.png"/>' class="azure-icon" alt="azure"><span></span>
                                     </li>
                                 </ul>
                             </li>
                         </ul>
                    </li>
                 </sec:authorize> 
                <%-- <sec:authorize access="hasAuthority('AZURE_SUBNET_MENU')">
                <li class="azureMgntDiv"  onmouseover="hover(this,'subnet_a2');" onmouseout="unhover(this,'subnet_a1');" onclick="javascript:goPage('<c:url value="/azureMgnt/subnet"/>', 'Subnet');">
                    <ul>
                         <li class="azure-li"><span class="azure-li-span1">Subnet</span></li>
                         <li>
                             <ul style="margin-top:59px">
                                 <li style="margin-bottom:22px;">
                                     <img src='<c:url value="images/azureMgnt/subnet_a1.png"/>'  class="azure-icon" alt="azure"><span></span>
                                 </li>
                             </ul>
                         </li>
                     </ul>
                </li>
                </sec:authorize> --%>
                <sec:authorize access="hasAuthority('AZURE_STORAGE_ACCOUNT_MENU')">
                    <li class="azureMgntDiv" onmouseover="hover(this,'az_storageAccount_2');" onmouseout="unhover(this,'az_storageAccount_1');" onclick="javascript:goPage('<c:url value="/azureMgnt/storageAccount"/>', 'Storage Account');">
                        <ul>
                            <li class="azure-li"><span class="azure-li-span1">Storage Account</span></li>
                            <li>
                                <ul style="margin-top:59px">
                                    <li style="margin-bottom:22px;"><img src='<c:url value="images/azureMgnt/az_storageAccount_1.png"/>'  class="azure-icon" alt="azure"><span></span></li>
                                </ul>
                            </li>
                        </ul>
                    </li>
                </sec:authorize>
                <sec:authorize access="hasAuthority('AZURE_PUBLIC_IP_MENU')">
                    <li class="azureMgntDiv" onmouseover="hover(this,'az_pIp_2');" onmouseout="unhover(this,'az_pIp_1');" onclick="javascript:goPage('<c:url value="/azureMgnt/publicIp"/>', 'Public IP');">
                        <ul>
                            <li class="azure-li"><span class="azure-li-span1">Public IP</span></li>
                            <li>
                                <ul style="margin-top:59px">
                                    <li style="margin-bottom:22px;"><img src='<c:url value="images/azureMgnt/az_pIp_1.png"/>' class="azure-icon" alt="azure"><span></span></li>
                                </ul>
                            </li>
                        </ul>
                    </li>
                </sec:authorize>
                <sec:authorize access="hasAuthority('AZURE_STORAGE_ACCESS_KEY_MENU')">
                    <li class="azureMgntDiv" onmouseover="hover(this,'az_keypair_2');" onmouseout="unhover(this,'az_keypair_1');" onclick="javascript:goPage('<c:url value="/azureMgnt/storageAccessKey"/>', 'Key Pair');">
                        <ul>
                            <li class="azure-li"><span class="azure-li-span1">Key Pair </span></li>
                            <li>
                                <ul style="margin-top:59px">
                                    <li style="margin-bottom:22px;"><img src='<c:url value="images/azureMgnt/az_keypair_1.png"/>'  class="azure-icon" alt="azure"><span></span></li>
                                </ul>
                            </li>
                        </ul>
                    </li>
                </sec:authorize>
                <sec:authorize access="hasAuthority('AZURE_SECURITY_GROUP_MENU')">
                    <li class="azureMgntDiv" onmouseover="hover(this,'az_securityGroup_2');" onmouseout="unhover(this,'az_securityGroup_1');" onclick="javascript:goPage('<c:url value="/azureMgnt/securityGroup"/>', 'Security Group');">
                        <ul>
                            <li class="azure-li"><span class="azure-li-span1">Security Group</span></li>
                            <li>
                                <ul style="margin-top:59px">
                                    <li style="margin-bottom:22px;"><img src='<c:url value="images/azureMgnt/az_securityGroup_1.png"/>' class="azure-icon" alt="azure"><span></span></li>
                                </ul>
                            </li>
                        </ul>
                    </li>
                </sec:authorize>
                <sec:authorize access="hasAuthority('AZURE_ROUTE_TABLE_MENU')">
                    <li class="azureMgntDiv" onmouseover="hover(this,'az_routeTable_2');" onmouseout="unhover(this,'az_routeTable_1');" onclick="javascript:goPage('<c:url value="/azureMgnt/routeTable"/>', 'Route Table');">
                        <ul>
                            <li class="azure-li"><span class="azure-li-span1">Route Table</span></li>
                            <li>
                                <ul style="margin-top:59px">
                                    <li style="margin-bottom:22px;"><img src='<c:url value="images/azureMgnt/az_routeTable_1.png"/>'  class="azure-icon" alt="azure"><span></span></li>
                                </ul>
                            </li>
                        </ul>
                    </li>
                </sec:authorize>
             </ul>
        </div>
    </div>
</div>

<div id="infra_azure_mgnt_nowrap" class="panel panel-danger" style="display:none;">
  <div class="panel-heading">azure 관리</div>
  <div class="panel-body">
       <p>azure 관리 권한을 가지고 있지 않습니다.</p>
       <p><b>권한을 확인해주세요.</b></p> 
  </div>
</div>

<div id="registAccountPopupDiv"  hidden="true">
    <input name="codeIdx" type="hidden"/>
    <div class="panel panel-info" style="margin-top:5px;" >    
        <div class="panel-heading"><b>Azure 계정 별칭 목록</b></div>
        <div class="panel-body" style="padding:5px 5% 10px 5%;height:65px;">
            <div class="w2ui-field">
                <label style="width:30%;text-align: left;padding-left: 20px; margin-top: 20px;">azure 계정 별칭</label>
                <div style="width: 70%;" class="accountList"></div>
            </div>
        </div>
    </div>
</div>

<div id="registAccountPopupBtnDiv" hidden="true">
    <button class="btn" id="registBtn" onclick="setDefaultIaasAccount('popup','azure');">확인</button>
    <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
</div>
