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
var accountId ="";
var bDefaultAccount = "";
var setAwsRegion = "";
var region = "";
var search_lock_msg = '<spring:message code="common.search.data.lock"/>';//조회 중 입니다.
$(function() {
    
    bDefaultAccount = setDefaultIaasAccountList("aws");
    
    $('#aws_elasticIpGrid').w2grid({
        name: 'aws_elasticIpGrid',
        method: 'GET',
        msgAJAXerror : 'AWS Elastic IP 목록 조회 실패',
        header: '<b>Property 목록</b>',
        multiSelect: false,
        
        show: {    
                footer: true},
        style: 'text-align: center',
        columns    : [
                     {field: 'recid',     caption: 'recid', hidden: true}
                   ,{field: 'publicIp', caption: 'Elastic IP', size: '30%', style: 'text-align:center'}
                   ,{field: 'allocationId', caption: 'Allocation ID', size: '30%', style: 'text-align:center'}
                   ,{field: 'domain', caption: 'Scope', size: '20%', style: 'text-align:center'}
                   ],
        onSelect: function(event) {
            event.onComplete = function() {
                var accountId =  w2ui.aws_elasticIpGrid.get(event.recid).accountId;
                var publicIp = w2ui.aws_elasticIpGrid.get(event.recid).publicIp;
                doSearchElasticIpDetail(accountId, publicIp);
            }
        },
        onUnselect: function(event) {
            event.onComplete = function() {
            }
        },
           onLoad:function(event){
            if(event.xhr.status == 403){
                location.href = "/abuse";
                event.preventDefault();
            }
        }, onError:function(evnet){
        }
    });
     
    
    /*************************** *****************************
     * 설명 :  AWS Elastic IP Allocate 팝업 화면
     *********************************************************/
    $("#addBtn").click(function(){
        if($("#addBtn").attr('disabled') == "disabled") return;
        w2popup.open({
            title   : "<b>AWS Elastic IP 할당 </b>",
            width   : 500,
            height  : 210,
            modal   : true,
            body    : $("#registPopupDiv").html(),
            buttons : $("#registPopupBtnDiv").html(),
            onOpen : function(event){
                event.onComplete = function(){
                }                   
            },onClose:function(event){
                initsetting();
                doSearch();
            }
        });
    });  
    
});


/********************************************************
 * 설명 : Elastic IP 목록 조회 Function
 * 기능 : doSearch
 *********************************************************/
function doSearch() {
    region = $("select[name='region']").val();
    if(region == null) region = "us-west-2";
    w2ui['aws_elasticIpGrid'].load("<c:url value='/awsMgnt/elasticIp/list/"+accountId+"/"+region+"'/>","",function(event){});
}

/********************************************************
 * 설명 : Elastic IP 상세 조회
 * 기능 : doSearchElasticIpDetail
 *********************************************************/
function doSearchElasticIpDetail(accountId, publicIp){
    var accountId =  $("select[name='accountId']").val();
    var region = $("select[name='region']").val();
    w2utils.lock($("#layout_layout_panel_main"), search_lock_msg, true);
    
    $.ajax({
        type : "GET",
        url : "/awsMgnt/elasticIp/save/detail/"+accountId+"/"+publicIp+"/"+region+"",
        contentType : "application/json",
        success : function(data, status) {
            w2utils.unlock($("#layout_layout_panel_main"));
            if(data != null){
                $(".elasticIp").html(data.publicIp+"");
                $(".allocationId").html(data.allocationId+"");
                $(".domain").html(data.domain+"");
                
                $(".instanceId").html(data.instanceId+"");
                if(data.instanceId != null){
                    $(".instanceId").html(data.instanceId);
                }else{
                    $(".instanceId").html("-");
                }
                
                $(".privateIpAddress").html(data.privateIpAddress+"");
                if(data.privateIpAddress != null){
                    $(".privateIpAddress").html(data.privateIpAddress);
                }else{
                    $(".privateIpAddress").html("-");
                }
                
                $(".associationId").html(data.associationId+"");
                if(data.associationId != null){
                    $(".associationId").html(data.associationId);
                }else{
                    $(".associationId").html("-");
                }
                
                $(".publicDns").html(data.publicDns);
                if(data.publicDns != null){
                    $(".publicDns").html(data.publicDns);
                }else{
                    $(".publicDns").html("-");
                }
                
                $(".networkInterfaceId").html(data.networkInterfaceId+"");
                if(data.networkInterfaceId != null){
                    $(".networkInterfaceId").html(data.networkInterfaceId);
                }else{
                    $(".networkInterfaceId").html("-");
                }
                
                $(".networkInterfaceOwner").html(data.networkInterfaceOwner+"");
                if(data.networkInterfaceOwner != null){
                    $(".networkInterfaceOwner").html(data.networkInterfaceOwner);
                }else{
                    $(".networkInterfaceOwner").html("-");
                }
            }
            return;
        },
        error : function(request, status, error) {
            w2utils.unlock($("#layout_layout_panel_main"));
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "AWS Elastic IP 상세 정보");
        }
    });
}


function elasticIpAllocate(){
    w2popup.lock( "할당중", true);
    var accountId =  $("select[name='accountId']").val();
    var region = $("select[name='region']").val();
$.ajax({
    type : "POST",
    url : "/awsMgnt/elasticIp/save/"+accountId+"/"+region,
    contentType : "application/json",
    async : true,
    success : function(status) {
        w2popup.unlock();
        w2popup.close();    
        initsetting();
    },
    error : function(request, status, error) {
        w2popup.unlock();
        initsetting();
        var errorResult = JSON.parse(request.responseText);
        w2alert(errorResult.message, "");
    }
});

}


/********************************************************
 * 기능 : initsetting
 * 설명 : 기본 설정값 초기화
 *********************************************************/
function initsetting(){
     bDefaultAccount="";
     w2ui['aws_elasticIpGrid'].clear();
     doSearch();
}

/********************************************************
 * 기능 : clearMainPage
 * 설명 : 다른페이지 이동시 호출
 *********************************************************/
function clearMainPage() {
    $().w2destroy('aws_elasticIpGrid');
}

/********************************************************
 * 기능 : resize
 * 설명 : 화면 리사이즈시 호출
 *********************************************************/
$( window ).resize(function() {
  setLayoutContainerHeight();
});
</script>

<style>
.trTitle {
     background-color: #f3f6fa;
     width: 180px;
 }
td {
    width: 280px;
}
 
</style>

<div id="main">
     <div class="page_site pdt20">인프라 관리 > AWS 관리 > <strong>AWS Elastic IPs 관리 </strong></div>
     <div id="awsMgnt" class="pdt20">
        <ul>
            <li>
                <label style="font-size: 14px">AWS 관리 화면</label> &nbsp;&nbsp;&nbsp; 
                <div class="dropdown" style="display:inline-block;">
                    <a href="#" class="dropdown-toggle iaas-dropdown" data-toggle="dropdown" aria-expanded="false">
                        &nbsp;&nbsp;Elastic IP 관리<b class="caret"></b>
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
                        <sec:authorize access="hasAuthority('AWS_KEYPAIR_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/awsMgnt/keypair"/>', 'AWS KEYPAIR');">KeyPair 관리</a></li>
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
        <div class="title fl">AWS Elastic Ip 목록</div>
        <div class="fr"> 
            <span id="addBtn" class="btn btn-primary" style="width:120px">할당</span>
            
        </div>
    </div>
    <div id="aws_elasticIpGrid" style="width:100%; height:425px"></div>
    <div class="title fl">AWS Elastic IP 상세 정보</div>
    <div id="aws_elasticIpDetailGrid" style="width:100%; height:128px; margin-top:50px; border-top: 2px solid #c5c5c5; ">
        <table id= "elasticIpDetailTable" class="table table-condensed table-hover">
              <tr>
                  <th class= "trTitle">Elastic IP</th>
                  <td class= "elasticIp"></td>
                  <th class= "trTitle">Allocation ID</th>
                  <td class="allocationId"></td>
                  <th class= "trTitle">Scope</th>
                  <td class= "domain"></td>
              </tr>
              <tr style = "border-bottom: 1px solid #ddd;">
                  <th class= "trTitle">Instance</th>
                  <td class= "instanceId"></td>
                  <th class= "trTitle">Private IP Address</th>
                  <td class= "privateIpAddress"></td>
                  <th class= "trTitle">Association ID</th>
                  <td class= "associationId"></td>
              </tr>
              <tr>
                  <th class= "trTitle">Public DNS</th>
                  <td class= "publicDns"></td>
                  <th class= "trTitle">Network Interface ID</th>
                  <td class= "networkInterfaceId"></td>
                  <th class= "trTitle">Network Interface Owner</tH>
                  <td class= "networkInterfaceOwner"></td>
              </tr>
        </table>
    </div>
</div>

<!-- AWS Elastic IP Allocate 팝업 Div-->
<div id="registPopupDiv" hidden="true">
    <div id="awsElasticIpAllocate" >
         <div class="panel panel-info" style="height: 110px; margin-top: 7px;"> 
            <div class="panel-heading"><b>AWS Elastic IP</b></div>
            <div class="panel-body">
                <div class="w2ui-field">
                    <label style="width:70%;text-align: left; padding-left: 20px;">새로운 Elastic IP 주소 할당</label>
                </div>
            </div>
        </div>
    </div> 
</div>
<div id="registPopupBtnDiv" hidden="true">
     <button class="btn btn-primary" id="registBtn" onclick="elasticIpAllocate();">확인</button>
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
