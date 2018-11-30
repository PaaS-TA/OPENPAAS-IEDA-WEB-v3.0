<%
/* =================================================================
 * 작성일 : 2017.06.05
 * 작성자 : 이정윤
 * 상세설명 :  IaaS Config Info 관리 화면 (전체 IaaS Config Info조회)
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<script type="text/javascript">
var search_grid_fail_msg = '<spring:message code="common.grid.select.fail"/>'; //error message

$(function() {
    $('#iaas_allConfigGrid').w2grid({
        name : 'iaas_allConfigGrid',
        style : 'text-align:center',
        method : 'GET',
        multiSelect: false,
        show:{
            selectColum: false,
            footer: true},
        columns : [
             {field: 'id', caption: 'id', hidden:true}
            ,{field: 'iaasType', caption:'인프라 유형', size: '10%', style:'text-align: center'}
            ,{field: 'iaasConfigAlias', caption:'인프라 환경 설정 별칭', size:'20%', style:'text-align: left'}
            ,{field: 'deployStatus', caption: '플랫폼 배포 사용 여부', size: '130px', style: 'text-align:center'}
            ,{field:'createDate', caption:'생성 일자', size:'20%', style:'text-align:center'}
            ,{field:'updateDate', caption:'수정 일자', size:'20%', style:'text-align:center'}
        ],onError: function(event){
            w2alert(search_grid_fail_msg,"전체 인프라 환경 설정 정보 목록");
        },onLoad : function(event){
        }
    });

    doSearch();
});

/****************************************************
 * 기능 : doSearch
 * 설명 : 전체 인프라 환경 설정 정보 목록 조회
*****************************************************/
function doSearch(){
    w2ui['iaas_allConfigGrid'].load("<c:url value='info/hbIaasConfig/all/list' />","",function(event){});
    setIaasConfigCountInfo();
}

/****************************************************
 * 기능 : setIaasConfigCountInfo
 * 설명 : 전체 인프라 별 환경 설정 정보 개수 조회
*****************************************************/
function setIaasConfigCountInfo(){
    if($("#infra_config_mgnt_wrap").find("ul").length == 0){
        $("#infra_config_mgnt_nowrap").show();
    }
    $.ajax({
        type : "GET",
        url : "/info/hbIaasConfig/all/cnt",
        contentType : "application/json",
        success : function(data, status){
            $("#iaas_config_mgnt_wrap .aws-config-cnt").append(data.aws_config_cnt + " (개)");
            $("#iaas_config_mgnt_wrap .openstack-config-cnt").append(data.openstack_config_cnt + " (개)");
            $("#iaas_config_mgnt_wrap .google-config-cnt").append(data.google_config_cnt + " (개)");
            $("#iaas_config_mgnt_wrap .vSphere-config-cnt").append(data.vsphere_config_cnt + " (개)");
            $("#iaas_config_mgnt_wrap .azure-config-cnt").append(data.azure_config_cnt + " (개)");

        },
        error : function(request, status, error){
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "인프라 별 환경 설정 정보 개수 정보");
        }

    });

}
/****************************************************
 * 기능 : hover
 * 설명 : 인프라 환경 설정 정보 관리 hover evet
*****************************************************/
function hover(event){
    $($(event).find(".config-go-style")).css("color","#297cbe")
}

/****************************************************
 * 기능 : unhover
 * 설명 : 인프라 환경 설정 정보 관리 unhover evet
*****************************************************/
function unhover(event){
    $($(event).find(".config-go-style")).css("color","gray")
}

 /****************************************************
 * 기능 : clearMainPage
 * 설명 : 다른페이지 이동시 호출
*****************************************************/
function clearMainPage(){
    $().w2destroy('iaas_allConfigGrid');    
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
    <div class="page_site">정보조회 > <strong>인프라 환경 설정 관리</strong></div>
    <div class="pdt20">
        <div class="title">전체 인프라 환경 설정 목록</div>
        <div id="iaas_allConfigGrid" style="width: 100%; height: 400px;"></div>
    </div>
    <div class="pdt20">
        <div class="title fl">인프라 환경 설정 관리</div>
        <div id="iaas_config_mgnt_wrap">
             <sec:authorize access="hasAuthority('INFO_HBIAASCONFIG_AWS_MENU')">
                <ul class="col-md-3 well" onmouseover="hover(this);" onmouseout="unhover(this);" onclick="javascript:goPage('<c:url value="/info/hbIaasConfig/aws"/>','AWS 조회');">
                    <li class="config-go-style">| AWS 환경 설정 관리 화면 이동</li>
                    <li><img src='images/iaasMgnt/aws-icon.png' class="aws-icon" alt="AWS 조회"></li>
                    <li class="aws-config-cnt"><span style="font-size: 14px;color:#000;">설정 정보 : </span></li>
                </ul>
            </sec:authorize>
            <sec:authorize access="hasAuthority('INFO_HBIAASCONFIG_OPENSTACK_MENU')">
                <ul class="col-md-3 well" onmouseover="hover(this);" onmouseout="unhover(this);" onclick="javascript:goPage('<c:url value="/info/hbIaasConfig/openstack"/>','Openstack 조회');">
                    <li class="config-go-style">| 오픈스택 환경 설정 관리 화면 이동</li>
                    <li><img src='images/iaasMgnt/openstack-icon.png' class="openstack-icon" alt="Openstack 조회"></li>
                    <li class="openstack-config-cnt"><span style="font-size: 14px;color:#000;">설정 정보 : </span></li>
                </ul>
            </sec:authorize>
        </div>
        <div id="iaas_config_mgnt_nowrap" class="panel panel-danger" style="display:none">
            <div class="panel-heading"></div>
            <div class="panel-body">
                <p>권한을 가지고 있지 않습니다.</p>
                <p><b>권한을 확인해주세요.</b></p> 
            </div> 
        </div>
    </div>
</div>