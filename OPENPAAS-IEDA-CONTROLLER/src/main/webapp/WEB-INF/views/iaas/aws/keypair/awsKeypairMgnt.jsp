<%
/* =================================================================
 * 작성일 : 2017.07.27
 * 작성자 : 배병욱
 * 상세설명 : AWS Key pair 관리 화면
 * =================================================================
 */ 
%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix = "spring" uri = "http://www.springframework.org/tags" %>
<script>

var save_lock_msg = '<spring:message code="common.save.data.lock"/>';//등록 중 입니다.
var text_required_msg='<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.

var accountId = "";
var bDefaultAccount = "";
var setAwsRegion = "";
var region = "";
$(function() {

    bDefaultAccount = setDefaultIaasAccountList("aws");
    
    $('#aws_KeyPairGrid').w2grid({
        name: 'aws_KeyPairGrid',
        method: 'GET',
        msgAJAXerror: 'AWS 계정을 확인해주세요.',
        header: '<b>Key Pair 목록</b>',
        multiSelect: false,
        show: {
                selectColumn: false,
                footer: true},
        style: 'text-align: center',
        columns     : [
                        {field: 'accountId', caption: 'accountId', hidden: true}
                    ,    {field: 'status', caption: 'status', hidden: true}
                    ,    {field: 'keyPairName', caption: 'keyPairName', size: '50%', style: 'text-align:center', render: function(record){
                            if(record.keyName == null || record.keyName == ""){
                                return "-"
                            }else{
                                return record.keyName;
                            }}
                        }
                    ,    {filed: 'fingerPrint', caption: 'fingerPrint', size: '50%', style: 'text-align:center', render: function(record){
                            if(record.keyFingerprint == null || record.keyFingerprint == ""){
                                return "-"
                            }else{
                                return record.keyFingerprint;
                            }}
                        }
                    ,    {field: 'privateKey', caption: 'privateKey', hidden: true}
        ],
        onLoad:function(event){
            if(event.xhr.status == 403){
                location.href = "/abuse";
                event.preventDefault();
            }
        },
        onError:function(event){
        }
    });
    
    /*************************** *****************************
     * 설명 :  AWS Key pair 생성 팝업
     *********************************************************/
    $('#addBtn').click(function(){
        if($("#addBtn").attr('disabled') == "disabled") return;
        w2popup.open({
            title   : "<b> AWS Keypair 생성 </b>",
            width   : 500,
            height  : 230,
            modal   : true,
            body    : $("#registPopupDiv").html(),
            buttons : $("#registPopupBtnDiv").html(),
            onClose:function(event){
                accountId = $("select[name='accountId']").val();
                initsetting();
                doSearch();
            }
        });
    });
});

/*********************************************************
    * 설명 :  AWS Key pair 생성
 *********************************************************/
function createKeyPair(){
        var accountId = $("select[name='accountId']").val();
        var keyPairName = $(".w2ui-msg-body input[name='keyPairName']").val();
        var region = $("select[name='region']").val();
        var debugLogdownUrl = "/awsMgnt/keypair/create/"+ accountId+"/"+keyPairName+"/"+region;
	    window.open(debugLogdownUrl, '', '');
	    w2popup.unlock();
	    w2popup.close();
	    doSearch();
}
 
/********************************************************
 * 설명 : keyPair 목록 조회 Function 
 * 기능 : doSearch
 *********************************************************/
function doSearch() {
     region = $("select[name='region']").val();
     if(region == null) region = "us-west-2";
     w2ui['aws_KeyPairGrid'].load('/awsMgnt/keypair/list/'+accountId+'/'+region+'');
     doButtonStyle();
}
/********************************************************
 * 기능 : initsetting
 * 설명 : 기본 설정값 초기화
 *********************************************************/
function initsetting(){
     bDefaultAccount="";
     w2ui['aws_KeyPairGrid'].clear();
}

/********************************************************
 * 설명 : 초기 버튼 스타일
 * 기능 : doButtonStyle
 *********************************************************/
function doButtonStyle() {

}

/********************************************************
 * 기능 : clearMainPage
 * 설명 : 다른페이지 이동시 호출
 *********************************************************/
function clearMainPage() {
    $().w2destroy('aws_KeyPairGrid');
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
    <div class="page_site pdt20">인프라 관리 > AWS 관리 > <strong>AWS KeyPair 관리 </strong></div>
    <div id="awsMgnt" class="pdt20">
        <ul>
            <li>
                <label style="font-size: 14px">AWS 관리 화면</label> &nbsp;&nbsp;&nbsp; 
                <div class="dropdown" style="display:inline-block;">
                    <a href="#" class="dropdown-toggle iaas-dropdown" data-toggle="dropdown" aria-expanded="false">
                        &nbsp;&nbsp;KeyPair 관리<b class="caret"></b>
                    </a>
                    <ul class="dropdown-menu alert-dropdown">
                        <sec:authorize access="hasAuthority('AWS_VPC_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/awsMgnt/vpc"/>', 'AWS VPC');">VPC 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('AWS_SUBNET_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/awsMgnt/subnet"/>', 'AWS SUBNET');">Subnet 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('AWS_INTERNET_GATEWAY_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/awsMgnt/internetGateway"/>', 'AWS Internet GateWay');">Internet Gateway 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('AWS_SECURITY_GROUP_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/awsMgnt/securityGroup"/>', 'AWS SECURITY GROUP');">Security Group 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('AWS_ELASTIC_IP_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/awsMgnt/elasticIp"/>', 'AWS Elastic Ip');">Elastic Ip 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('AWS_NAT_GATEWAY_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/awsMgnt/natGateway"/>', 'AWS NAT GateWay');">NAT Gateway 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('AWS_ROUTE_TABLE_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/awsMgnt/routeTable"/>', 'AWS Route Table');">Route Table 관리</a></li>
                        </sec:authorize>
                    </ul>
                </div>
            </li>
            <li>
                <label style="font-size: 14px">AWS Region</label>
                &nbsp;&nbsp;&nbsp;
                <select name="region" onchange="awsRegionOnchange();" id="regionList" class="select" style="width:300px; font-size: 15px; height: 32px;"></select>
            </li>
            <li>
                <label style="font-size: 14px">AWS 계정 명</label>
		        &nbsp;&nbsp;&nbsp;
		        <select name="accountId" id="setAccountList" class="select" style="width: 300px; font-size: 15px; height: 32px;" onchange="setAccountInfo(this.value, 'aws')">
		        </select>
		        <span id="doSearch" onclick="setDefaultIaasAccount('noPopup','aws');" class="btn btn-info" style="width:80px" >선택</span>
            </li>
        </ul>
    </div> 
    <div class="pdt20">
        <div class="title fl">AWS Key pair 목록</div>
        <div class="fr">
            <sec:authorize access="hasAuthority('AWS_KEYPAIR_CREATE')">
            <span id="addBtn" class="btn btn-primary" style="width:120px">생성</span>
            </sec:authorize>
        </div>
    </div>
    <div id="aws_KeyPairGrid" style="width:100%; height:475px"></div>
</div>


<!-- AWS Key Pair 팝업 Div-->
<div id="registPopupDiv" hidden="true">
<form id="awsKeyPairForm" action="POST">
    <div id="awsKeypairAdd" style="padding:5px 0 5px 0;margin:0;">
        <div class="panel panel-info" style="height: 110px; margin-top: 7px;"> 
            <div class="panel-heading"><b>AWS Key pair</b></div>
            <div class="panel-body" style="padding:20px 10px; height:90px; overflow-y:auto;">
                <input type="hidden" name="accountId"/>
                <div class="w2ui-field">
                    <label style="width:36%;text-align: left; padding-left: 20px;">key pair name:&nbsp;&nbsp;</label>
                        <div>
                            <input name="keyPairName" type="text" maxlength="100" style="width: 200px; margin-top: 1px;" placeholder="Key Pair 이름을 입력하세요.">
                        </div>
                </div>
            </div>
        </div>
    </div>
    </form> 
</div>
<div id="registPopupBtnDiv" hidden="true">
     <button class="btn btn-primary" id="registBtn" onclick="$('#awsKeyPairForm').submit();">생성</button>
     <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
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

<script>
$(function() {
    $("#awsKeyPairForm").validate({
        ignore : "",
        onfocusout: true,
        rules: {
            keyPairName: {
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='keyPairName']").val());
                }
            }
        }, messages: {
            keyPairName: {
                required: "KeyPair" + text_required_msg
            }
        }, unhighlight: function(element) {
            setSuccessStyle(element);
        }, errorPlacement: function(error, element){
            // do nothing
        }, invalidHandler: function(event, validator){
            var errors = validator.numberOfInvalids();
            if (errors) {
                setInvalidHandlerStyle(errors, validator);
            }
        }, submitHandler: function(form) {
            w2popup.lock(save_lock_msg, true);
            createKeyPair();
        }
    });    
});
</script>
