<%
/* =================================================================
 * 작성일 : 2017.06.26
 * 작성자 : 지향은
 * 상세설명 : AWS 관리 화면
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
    if( $("#awsMgntWrap").find("ul").length == 0 ){
           $("#infra_aws_mgnt_nowrap").show();
    }
    // AWS 클라우드 인프라 계정 정보 조회 메소드 호출
    bDefaultAccount = setDefaultIaasAccountList("aws");
});

function doSearch(){ }

/********************************************************
 * 기능 : hover
 * 설명 : mouse hover event
 *********************************************************/
function hover(event, val){
    var src = "<c:url value='images/awsMgnt/"+val+".png'/>";
    $($(event).find("img")).attr("src",src);
}

/********************************************************
 * 기능 : unhover
 * 설명 : mouse unhover event
 *********************************************************/
function unhover(event, val){
    var src = "<c:url value='images/awsMgnt/"+val+".png'/>";
    $($(event).find("img")).attr("src",src);
}
</script>
<div id="awsMain" >
    <div class="page_site">인프라 관리 > <strong>AWS 관리 </strong></div>
    <div class="pdt20" style="float:left">
        <div class="fl" style="width:100%;padding-bottom: 20px;border-bottom: 1px solid gray;">
             <label  style="font-size:14px">AWS 계정 명</label>
             &nbsp;&nbsp;&nbsp;
             <select name="accountId" id="setAccountList" class="select" style="width: 300px; font-size: 15px; height: 32px;" onchange="setAccountInfo(this.value, 'aws');">
             </select>
             &nbsp;&nbsp;&nbsp;
             <span id="doSearch" onclick="setDefaultIaasAccount('noPopup','aws');" class="btn btn-info" style="width:80px" >선택</span>
        </div> 
        <div class="pdt20" style="width:100%; float:left;">
            <div class="title fl">AWS 관리 메뉴</div>
        </div>
        <div id="awsMgntWrap" >
            <ul>
              <sec:authorize access="hasAuthority('AWS_VPC_MENU')">
                    <li class="awsMgntDiv" onmouseover="hover(this,'vpc_b2');" onmouseout="unhover(this,'vpc_b1');" onclick="javascript:goPage('<c:url value="/awsMgnt/vpc"/>', 'VPC');">
                        <ul>
                            <li class="aws-li"><span class="aws-li-span1">VPC</span></li>
                            <li>
                                <ul style="margin-top:59px">
                                    <li style="margin-bottom:22px;"><img src='<c:url value="images/awsMgnt/vpc_b1.png"/>' class="aws-icon" alt="AWS"><span></span></li>
                                </ul>
                            </li>
                        </ul>
                    </li>
                </sec:authorize>
                 <sec:authorize access="hasAuthority('AWS_SUBNET_MENU')">
                <li class="awsMgntDiv"  onmouseover="hover(this,'subnet_a2');" onmouseout="unhover(this,'subnet_a1');" onclick="javascript:goPage('<c:url value="/awsMgnt/subnet"/>', 'Subnet');">
                    <ul>
                         <li class="aws-li"><span class="aws-li-span1">Subnet</span></li>
                         <li>
                             <ul style="margin-top:59px">
                                 <li style="margin-bottom:22px;">
                                     <img src='<c:url value="images/awsMgnt/subnet_a1.png"/>'  class="aws-icon" alt="AWS"><span></span>
                                 </li>
                             </ul>
                         </li>
                     </ul>
                </li>
                </sec:authorize>
                <sec:authorize access="hasAuthority('AWS_INTERNET_GATEWAY_MENU')">
                    <li class="awsMgntDiv" onmouseover="hover(this,'gateway_b2');" onmouseout="unhover(this,'gateway_b1');" onclick="javascript:goPage('<c:url value="/awsMgnt/internetGateway"/>', 'Internet Gateway');">
                        <ul>
                            <li class="aws-li"><span class="aws-li-span1">Internet Gateway</span></li>
                            <li>
                                <ul style="margin-top:59px">
                                    <li style="margin-bottom:22px;"><img src='<c:url value="images/awsMgnt/gateway_b1.png"/>' class="aws-icon" alt="AWS"><span></span></li>
                                </ul>
                            </li>
                        </ul>
                    </li>
                </sec:authorize>
                <sec:authorize access="hasAuthority('AWS_SECURITY_GROUP_MENU')">
                    <li class="awsMgntDiv securityGroup" onmouseover="hover(this,'securityGroup_a2');" onmouseout="unhover(this,'securityGroup_a1');"  onclick="javascript:goPage('<c:url value="/awsMgnt/securityGroup"/>', 'Security Group');">
                        <ul >
                             <li class="aws-li"><span class="aws-li-span1">Security Group</span></li>
                             <li>
                                 <ul style="margin-top:59px">
                                     <li style="margin-bottom:22px;">
                                         <img src='<c:url value="images/awsMgnt/securityGroup_a1.png"/>' class="aws-icon" alt="AWS"><span></span>
                                     </li>
                                 </ul>
                             </li>
                         </ul>
                    </li>
                </sec:authorize>
                 <sec:authorize access="hasAuthority('AWS_ELASTIC_IP_MENU')">
                    <li class="awsMgntDiv" onmouseover="hover(this,'elasticIP_c2');" onmouseout="unhover(this,'elasticIP_c1');" onclick="javascript:goPage('<c:url value="/awsMgnt/elasticIp"/>', 'Elastic IPs');">
                        <ul>
                            <li class="aws-li"><span class="aws-li-span1">Elastic IPs</span></li>
                            <li>
                                <ul style="margin-top:59px">
                                    <li style="margin-bottom:22px;"><img src='<c:url value="images/awsMgnt/elasticIP_c1.png"/>'  class="aws-icon" alt="AWS"><span></span></li>
                                </ul>
                            </li>
                        </ul>
                    </li>
                </sec:authorize>
                <sec:authorize access="hasAuthority('AWS_KEYPAIR_MENU')">
                    <li class="awsMgntDiv" onmouseover="hover(this,'keypair_c2');" onmouseout="unhover(this,'keypair_c1');" onclick="javascript:goPage('<c:url value="/awsMgnt/keypair"/>', 'Key Pair');">
                        <ul>
                             <li class="aws-li"><span class="aws-li-span1">Key Pair</span></li>
                             <li>
                                 <ul style="margin-top:59px">
                                     <li style="margin-bottom:22px;">
                                         <img src='<c:url value="images/awsMgnt/keypair_c1.png"/>' class="aws-icon" alt="AWS"><span></span>
                                     </li>
                                 </ul>
                             </li>
                         </ul>
                    </li>
                </sec:authorize>
                <sec:authorize access="hasAuthority('AWS_NAT_GATEWAY_MENU')">
                    <li class="awsMgntDiv" onmouseover="hover(this,'aws_nat_2');" onmouseout="unhover(this,'aws_nat_1');" onclick="javascript:goPage('<c:url value="/awsMgnt/natGateway"/>', 'NAT Gateway');">
                        <ul>
                            <li class="aws-li"><span class="aws-li-span1">NAT Gateway</span></li>
                            <li>
                                <ul style="margin-top:59px">
                                    <li style="margin-bottom:22px;"><img src='<c:url value="images/awsMgnt/aws_nat_1.png"/>'  class="aws-icon" alt="AWS"><span></span></li>
                                </ul>
                            </li>
                        </ul>
                    </li>
                </sec:authorize>
                 <sec:authorize access="hasAuthority('AWS_ROUTE_TABLE_MENU')">
                    <li class="awsMgntDiv" onmouseover="hover(this,'aws_rTable_2');" onmouseout="unhover(this,'aws_rTable_1');" onclick="javascript:goPage('<c:url value="/awsMgnt/routeTable"/>', 'Route Tables');">
                        <ul>
                            <li class="aws-li"><span class="aws-li-span1">Route Tables</span></li>
                            <li>
                                <ul style="margin-top:59px">
                                    <li style="margin-bottom:22px;"><img src='<c:url value="images/awsMgnt/aws_rTable_1.png"/>'  class="aws-icon" alt="AWS"><span></span></li>
                                </ul>
                            </li>
                        </ul>
                    </li>
                </sec:authorize>
            </ul>
        </div>
    </div>
</div>

<div id="infra_aws_mgnt_nowrap" class="panel panel-danger" style="display:none;">
  <div class="panel-heading">AWS 관리</div>
  <div class="panel-body">
       <p>AWS 관리 권한을 가지고 있지 않습니다.</p>
       <p><b>권한을 확인해주세요.</b></p> 
  </div>
</div>

<div id="registAccountPopupDiv"  hidden="true">
    <input name="codeIdx" type="hidden"/>
    <div class="panel panel-info" style="margin-top:5px;" >    
        <div class="panel-heading"><b>AWS 계정 별칭 목록</b></div>
        <div class="panel-body" style="padding:5px 5% 10px 5%;height:65px;">
            <div class="w2ui-field">
                <label style="width:30%;text-align: left;padding-left: 20px; margin-top: 20px;">AWS 계정 별칭</label>
                <div style="width: 70%;" class="accountList"></div>
            </div>
        </div>
    </div>
</div>

<div id="registAccountPopupBtnDiv" hidden="true">
    <button class="btn" id="registBtn" onclick="setDefaultIaasAccount('popup','aws');">확인</button>
    <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
</div>
