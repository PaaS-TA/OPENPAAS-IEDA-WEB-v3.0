<%
/* =================================================================
 * 작성일 : 2017.05.08
 * 작성자 : 이정윤
 * 상세설명 : 계정 관리 화면(전체 인프라 계정 조회)
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<script type="text/javascript">
var search_grid_fail_msg = '<spring:message code="common.grid.select.fail"/>';//목록 조회 중 오류가 발생했습니다.
$(function() {
    // 전체 클라우드 인프라 계정 정보 조회
    $('#iaas_allAccountGrid').w2grid({
        name: 'iaas_allAccountGrid',
        style   : 'text-align:center',
        method  : 'GET',
        multiSelect: false,
        show: {
                selectColumn: false,
                footer: true},
        columns : [
             {field: 'id',  caption: 'id', hidden:true}
           , {field: 'iaasType', caption: '인프라 유형', size: '5%', style: 'text-align:center'}
           , {field: 'status', caption: '환경 설정 정보 사용 여부', size: '7%', style: 'text-align:center'}
           , {field: 'accountName', caption: '계정 별칭', size: '30%', style: 'text-align:left'}
           , {field: 'createDate', caption: '계정 생성 일자', size: '5%', style: 'text-align:center'}
           , {field: 'updateDate', caption: '계정 수정 일자', size: '5%', style: 'text-align:center'}
          
         ],onError: function(event) {
        	 w2alert(search_grid_fail_msg, "전체 인프라 계정 목록");
        },onLoad : function(event){
        }
    });
    doSearch(); 
});
    

/****************************************************
 * 기능 : doSearch
 * 설명 : 전체 인프라 계정 목록 조회
*****************************************************/
function doSearch() {
    // 목록
    w2ui['iaas_allAccountGrid'].load("<c:url value='/iaasMgnt/account/all/list'/>","",function(event){}); 
    setIaasAccountCountInfo();
}

/****************************************************
 * 기능 : setIaasAccountCountInfo
 * 설명 : 전체 인프라 별 계정 개수 조회
*****************************************************/
function setIaasAccountCountInfo(){
	 if( $("#infra_account_mgnt_wrap").find("ul").length == 0 ){
		 $("#infra_account_mgnt_nowrap").show();
	 }
     $.ajax({
        type : "GET",
        url : "/iaasMgnt/account/all/cnt",
        contentType : "application/json",
        success : function(data, status) {
            $("#infra_account_mgnt_wrap .aws-cnt").append(data.aws_cnt + "(개)");
            $("#infra_account_mgnt_wrap .openstack-cnt").append(data.openstack_cnt + "(개)");
            $("#infra_account_mgnt_wrap .google-cnt").append(data.google_cnt + "(개)");
            $("#infra_account_mgnt_wrap .vsphere-cnt").append(data.vsphere_cnt + "(개)");
            $("#infra_account_mgnt_wrap .azure-cnt").append(data.azure_cnt + "(개)");
        },
        error : function(request, status, error) {
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "인프라 별 계정 개수 정보");
        }
    });
}

/****************************************************
 * 기능 : hover
 * 설명 : 인프라 계정 관리 hover evet
*****************************************************/
function hover(event){
	$($(event).find(".aws-go")).css("color","#3e80b5")
}

/****************************************************
 * 기능 : unhover
 * 설명 : 인프라 계정 관리 unhover evet
*****************************************************/
function unhover(event){
    $($(event).find(".aws-go")).css("color","gray")
}

/****************************************************
 * 기능 : clearMainPage
 * 설명 : 다른페이지 이동시 호출
*****************************************************/
function clearMainPage() {
    $().w2destroy('iaas_allAccountGrid');
}

/****************************************************
 * 기능 : resize
 * 설명 : 화면 리사이즈시 호출
*****************************************************/
$( window ).resize(function() {
  setLayoutContainerHeight();
});
</script>
<div id="main">
    <div class="page_site">계정 관리 > <strong>계정 관리 메인 </strong></div>
    <div class="pdt20">
        <div class="title fl">전체 인프라 계정 목록</div>
        <div id="iaas_allAccountGrid" style="width: 100%; height: 400px;"></div>
    </div>
    
    <div class="pdt20">
        <div class="title fl">인프라 계정 관리</div>
        <div id="infra_account_mgnt_wrap">
            <ul>
                <sec:authorize access="hasAuthority('IAAS_ACCOUNT_AWS_MENU')">
                <li>
                    <ul class="aws-box col-md-3 well" onmouseover="hover(this);" onmouseout="unhover(this);" onclick="javascript:goPage('<c:url value="/iaasMgnt/account/aws"/>', 'AWS 조회');">
	                    <li class="aws-go">| AWS 계정 관리 화면 이동</li>
	                    <li><img src='<c:url value="images/iaasMgnt/aws-icon.png"/>' class="aws-icon" alt="AWS 조회"></li>
	                    <li class="aws-cnt"><span style="color:#000;">계정: </span></li>
	                    
                    </ul>
                </li>
                </sec:authorize>
                <sec:authorize access="hasAuthority('IAAS_ACCOUNT_OPENSTACK_MENU')">
                <li>
                    <ul class="col-md-3 well" onmouseover="hover(this);" onmouseout="unhover(this);" onclick="javascript:goPage('<c:url value="/iaasMgnt/account/openstack"/>', 'Openstack 조회');">
	                   <li class="aws-go">| Openstack 계정 관리 화면 이동</li>
	                   <li><img src='<c:url value="images/iaasMgnt/openstack-icon.png" />' class="openstack-icon" alt="Openstack 조회"></li>
	                   <li class="openstack-cnt"><span style="color:#000;">계정: </span></li>
                    </ul>
                </li>
                </sec:authorize>
                <sec:authorize access="hasAuthority('IAAS_ACCOUNT_GOOGLE_MENU')">
                <li>
	                <ul class="col-md-3 well" onmouseover="hover(this);" onmouseout="unhover(this);" onclick="javascript:goPage('<c:url value="/iaasMgnt/account/google"/>', 'Google 조회');">
	                   <li class="aws-go">| Google 계정 관리 화면 이동</li>
	                   <li><img src='<c:url value="images/iaasMgnt/google-icon.png"/>' class="google-icon" alt="Google 조회"> </li>
	                   <li class="google-cnt"><span style="color:#000;">계정: </span></li>
	                </ul>
	            </li>
                </sec:authorize>
                <sec:authorize access="hasAuthority('IAAS_ACCOUNT_VSPHERE_MENU')">
                <li>
	                <ul class="vSphere-box col-md-3 well" onmouseover="hover(this);" onmouseout="unhover(this);" onclick="javascript:goPage('<c:url value="/iaasMgnt/account/vSphere"/>', 'vSphere 조회');">
	                   <li class="aws-go">| vSphere 계정 관리 화면 이동</li>
	                   <li><img src='<c:url value="images/iaasMgnt/vSphere-icon.png"/>' class="vSphere-icon" alt="vSphere 조회"></li>
	                   <li class="vsphere-cnt"><span style="color:#000;">계정: </span></li>
	                </ul>
	            </li>
            </sec:authorize>
            </ul>
            <ul>
                <sec:authorize access="hasAuthority('IAAS_ACCOUNT_VSPHERE_MENU')">
                <li>
                    <ul class="vSphere-box col-md-3 well" onmouseover="hover(this);" onmouseout="unhover(this);" onclick="javascript:goPage('<c:url value="/iaasMgnt/account/azure"/>', 'Azure 조회');">
                       <li class="aws-go">| Azure 계정 관리 화면 이동</li>
                       <li><img src='<c:url value="images/iaasMgnt/azure-icon.png"/>' class="google-icon" alt="Azure 조회"></li>
                       <li class="azure-cnt"><span style="color:#000;">계정: </span></li>
                    </ul>
                </li>
            </sec:authorize>
            </ul>
        </div>
        <div id="infra_account_mgnt_nowrap" class="panel panel-danger" style="display:none;">
          <div class="panel-heading">인프라 계정 관리</div>
          <div class="panel-body">
                <p>인프라 계정 관리 권한을 가지고 있지 않습니다.</p>
                <p><b>권한을 확인해주세요.</b></p> 
          </div>
        </div>
    </div>
</div>

