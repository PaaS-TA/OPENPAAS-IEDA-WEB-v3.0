<%
/* =================================================================
 * 작성일 : 2017.07.29
 * 작성자 : 이동현
 * 상세설명 : OPENSTACK 관리 화면
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
    if( $("#openstackMgntWrap").find("ul").length == 0 ){
          $("#infra_openstack_mgnt_nowrap").show();
   }
    // OPENSTACK 클라우드 인프라 계정 정보 조회 메소드 호출
    bDefaultAccount = setDefaultIaasAccountList("openstack");
});

function doSearch(){ 
}

/********************************************************
 * 기능 : hover
 * 설명 : mouse hover event
 *********************************************************/
function hover(event, val){
    var src = "<c:url value='images/openstackMgnt/"+val+".png'/>";
    $($(event).find("img")).attr("src",src);
}

/********************************************************
 * 기능 : unhover
 * 설명 : mouse unhover event
 *********************************************************/
function unhover(event, val){
    var src = "<c:url value='images/openstackMgnt/"+val+".png'/>";
    $($(event).find("img")).attr("src",src);
}

</script>
<div id="openstackMain" >
    <div class="page_site">인프라 관리 > <strong>OPENSTACK 관리 </strong></div>
    <div class="pdt20" style="float:left">
         <div class="fl" style="width:100%;padding-bottom: 20px;border-bottom: 1px solid gray;">
            <label  style="font-size:14px">OPENSTACK 계정 명</label>
            &nbsp;&nbsp;&nbsp;
            <select name="accountId" id="setAccountList" class="select" style="width: 300px; font-size: 15px; height: 32px;" onchange="setAccountInfo(this.value, 'openstack')">
            </select>
            &nbsp;&nbsp;&nbsp;
            <span id="doSearch" onclick="setDefaultIaasAccount('noPopup','openstack');" class="btn btn-info" style="width:80px" >선택</span>
        </div> 
        <div class="pdt20" style="width:100%; float:left;">
            <div class="title fl">Openstack 관리 메뉴</div>
        </div>
        <div id="openstackMgntWrap" >
            <ul>
                <sec:authorize access="hasAuthority('OPENSTACK_NETWORK_MENU')">
                    <li class="openstackMgntDiv" onmouseover="hover(this,'op_network_a2');" onmouseout="unhover(this,'op_network_a1');" onclick="javascript:goPage('<c:url value="/openstackMgnt/network"/>', 'Network');">
                       <ul>
                          <li class="openstack-li"><span class="openstack-li-span1">Network</span></li>
                          <li>
                              <ul style="margin-top:59px">
                                  <li style="margin-bottom:22px;"><img src='<c:url value="images/openstackMgnt/op_network_a1.png"/>' class="aws-icon" alt="AWS"><span></span></li>
                              </ul>
                          </li>
                        </ul>
                    </li>
                </sec:authorize>
                <sec:authorize access="hasAuthority('OPENSTACK_ROUTER_MENU')">
                    <li class="openstackMgntDiv" onmouseover="hover(this,'op_router_a2');" onmouseout="unhover(this,'op_router_a1');" onclick="javascript:goPage('<c:url value="/openstackMgnt/router"/>', 'Router');">
                       <ul>
                           <li class="openstack-li"><span class="openstack-li-span1">Router</span></li>
                           <li>
                              <ul style="margin-top:59px">
                                  <li style="margin-bottom:22px;"><img src='<c:url value="images/openstackMgnt/op_router_a1.png"/>'  class="aws-icon" alt="AWS"><span></span></li>
                              </ul>
                           </li>
                        </ul>
                    </li>
                </sec:authorize>
                <sec:authorize access="hasAuthority('OPENSTACK_KEYPAIRS_MENU')">
                    <li class="openstackMgntDiv" onmouseover="hover(this,'op_keypair_a2');" onmouseout="unhover(this,'op_keypair_a1');" onclick="javascript:goPage('<c:url value="/openstackMgnt/keypairs"/>', 'KeyPair');">
                        <ul>
                            <li class="openstack-li"><span class="openstack-li-span1">Key Pair</span></li>
                            <li>
                                <ul style="margin-top:59px">
                                    <li style="margin-bottom:22px;"><img src='<c:url value="images/openstackMgnt/op_keypair_a1.png"/>'  class="aws-icon" alt="AWS"><span></span></li>
                                </ul>
                            </li>
                        </ul>
                    </li>
                </sec:authorize>
                <sec:authorize access="hasAuthority('OPENSTACK_INTERFACE_MENU')">
                    <li class="openstackMgntDiv" onmouseover="hover(this,'op_interface_a2');" onmouseout="unhover(this,'op_interface_a1');" onclick="javascript:goPage('<c:url value="/openstackMgnt/interface"/>', 'Interface');">
                        <ul>
                            <li class="openstack-li"><span class="openstack-li-span1">Interface</span></li>
                            <li>
                                <ul style="margin-top:59px">
                                    <li style="margin-bottom:22px;"><img src='<c:url value="images/openstackMgnt/op_interface_a1.png"/>' class="aws-icon" alt="AWS"><span></span></li>
                                </ul>
                            </li>
                        </ul>
                    </li>
                </sec:authorize>
                <sec:authorize access="hasAuthority('OPENSTACK_FLOATING_IP_MENU')">
                    <li class="openstackMgntDiv" onmouseover="hover(this,'op_elasticIp_a2');" onmouseout="unhover(this,'op_elasticIp_a1');" onclick="javascript:goPage('<c:url value="/openstackMgnt/floatingIp"/>', 'Floating IP');">
                        <ul>
                            <li class="openstack-li"><span class="openstack-li-span1">Floating IPs</span></li>
                            <li>
                                <ul style="margin-top:59px">
                                    <li style="margin-bottom:22px;"><img src='<c:url value="images/openstackMgnt/op_elasticIp_a1.png"/>' class="aws-icon" alt="AWS"><span></span></li>
                                </ul>
                            </li>
                        </ul>
                    </li>
                </sec:authorize>
                <sec:authorize access="hasAuthority('OPENSTACK_SECURITY_GROUP_MENU')">
                    <li class="openstackMgntDiv" onmouseover="hover(this,'op_sgroup_a2');" onmouseout="unhover(this,'op_sgroup_a1');" onclick="javascript:goPage('<c:url value="/openstackMgnt/securityGroup"/>', 'Security Group');">
                        <ul>
                            <li class="openstack-li"><span class="openstack-li-span1">Security Group</span></li>
                            <li>
                                <ul style="margin-top:59px">
                                    <li style="margin-bottom:22px;"><img src='<c:url value="images/openstackMgnt/op_sgroup_a1.png"/>'  class="aws-icon" alt="AWS"><span></span></li>
                                </ul>
                            </li>
                        </ul>
                    </li>
                </sec:authorize>
            </ul>
        </div>
    </div>
    <div id="infra_openstack_mgnt_nowrap" class="panel panel-danger" style="display:none;">
        <div class="panel-heading">OPENSTACK 관리</div>
        <div class="panel-body">
           <p>OPENSTACK 관리 권한을 가지고 있지 않습니다.</p>
           <p><b>권한을 확인해주세요.</b></p> 
        </div>
    </div>
</div>

<div id="registAccountPopupDiv"  hidden="true">
    <input name="codeIdx" type="hidden"/>
    <div class="panel panel-info" style="margin-top:5px;" >    
        <div class="panel-heading"><b>openstack 계정 별칭 목록</b></div>
        <div class="panel-body" style="padding:5px 5% 10px 5%;height:65px;">
            <div class="w2ui-field">
                <label style="width:30%;text-align: left;padding-left: 20px; margin-top: 20px;">계정 별칭</label>
                <div style="width: 70%;" class="accountList"></div>
            </div>
        </div>
    </div>
</div>

<div id="registAccountPopupBtnDiv" hidden="true">
    <button class="btn" id="registBtn" onclick="setDefaultIaasAccount('popup','openstack');">확인</button>
    <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
</div>