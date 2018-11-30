<%
/* =================================================================
 * 작성일 : 2018.03.15
 * 작성자 : 이정윤
 * 상세설명 : AWS 관리 화면
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix = "spring" uri = "http://www.springframework.org/tags" %>
 
<script>
var text_required_msg='<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
var detail_lock_msg='<spring:message code="common.search.detaildata.lock"/>';//상세 조회 중 입니다.
var accountId ="";
var bDefaultAccount = "";
var setAwsRegion = "";
var region = "";
var search_lock_msg = '<spring:message code="common.update.data.lock"/>';//등록 중 입니다.
$(function() {
    
    bDefaultAccount = setDefaultIaasAccountList("aws");
    
    $('#aws_natGatewayGrid').w2grid({
        name: 'aws_natGatewayGrid',
        method: 'GET',
        msgAJAXerror : 'AWS NAT Gateway 목록 조회 실패',
        header: '<b>Property 목록</b>',
        multiSelect: false,
        
        show: {    
                footer: true},
        style: 'text-align: center',
        columns    : [
                    {field: 'recid',     caption: 'recid', hidden: true}
                   ,{field: 'natGatewayId', caption: 'NAT GW ID', size: '30%', style: 'text-align:center'}
                   ,{field: 'state', caption: 'Status', size: '20%', style: 'text-align:center'}
                   ,{field: 'publicIp', caption: 'Elastic IP', size: '30%', style: 'text-align:center'} 
                   ,{field: 'privateIp', caption: 'Private IP', size: '30%', style: 'text-align:center'}
                   ,{field: 'networkInterfaceId', caption: 'NIC ID', size: '20%', style: 'text-align:center'}
                   ,{field: 'vpcId', caption: 'VPC', size: '20%', style: 'text-align:center'}
                   ,{field: 'subnetId', caption: 'Subnet ID', size: '20%', style: 'text-align:center'}
                   ,{field: 'allocationId', caption: 'Allocation ID', size: '20%', style: 'text-align:center'}
                   ,{field: 'createdTime', caption: 'Created Date', size: '30%', style: 'text-align:center'}
                   ],
        onSelect: function(event) {
            event.onComplete = function() {
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
     * 설명 :  AWS NAT Gateway Create 팝업 화면
     *********************************************************/
    $("#addBtn").click(function(){
        if($("#addBtn").attr('disabled') == "disabled") return;
        w2popup.open({
            title   : "<b>AWS NAT Gateway 생성 </b>",
            width   : 500,
            height  : 400,
            modal   : true,
            body    : $("#registPopupDiv").html(),
            buttons : $("#registPopupBtnDiv").html(),
            onOpen : function(event){
                event.onComplete = function(){
                    setAwsSubnetIdList();
                    setAwsEipAllocationIdList();
                }                   
            },onClose:function(event){
                initsetting();
                doSearch();
            }
        });
    }); 
    
    var iTime = 15;
    var h;
    var m; 
    setInterval(function() {
        if(iTime == 0){
            iTime = 15; 
            refresh();
        }
        iTime--;
        h = parseInt(iTime/60);
        m = iTime%60;
        
        if(m < 10){
            m = "0"+m;
        }
        $("#iTime").text(h+":"+m);
    },1000);
    
});

function refresh(){
    region = $("select[name='region']").val();
    if(region == null) region = "us-west-2";
	if(w2ui['aws_natGatewayGrid'] != undefined)
    w2ui['aws_natGatewayGrid'].load("<c:url value='/awsMgnt/natGateway/list/"+accountId+"/"+region+"'/>","",function(event){});
}

/********************************************************
 * 설명 : NAT Gateway 목록 조회 Function
 * 기능 : doSearch
 *********************************************************/
function doSearch() {
    region = $("select[name='region']").val();
    if(region == null) region = "us-west-2";
    w2ui['aws_natGatewayGrid'].load("<c:url value='/awsMgnt/natGateway/list/"+accountId+"/"+region+"'/>","",function(event){});
}

/********************************************************
 * 설명 : NAT Gateway 생성
 * 기능 : awsNatGatewayCreate
 *********************************************************/
function awsNatGatewayCreate(){
    w2popup.lock( "생성중", true);
    var natGwInfo = {
            accountId : $("select[name='accountId']").val(),
            region :  $("select[name='region']").val(),  
            subnetId : $(".w2ui-msg-body select[name='subnetId']").val(),
            allocationId : $(".w2ui-msg-body select[name='allocationId']").val()
        }
    
$.ajax({
    type : "POST",
    url : "/awsMgnt/natGateway/save",
    contentType : "application/json",
    async : true,
    data : JSON.stringify(natGwInfo),
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
 * 설명 :NEW Elastic IP 할당 
 * 기능 : 
 *********************************************************/

function awsElasticIpAllocate(){
    w2utils.lock($("#layout_layout_panel_main"), "", true);
     w2popup.lock( "할당 중 입니다.", true);
    awsInfo = { 
            accountId : $("select[name='accountId']").val(),
            region :  $("select[name='region']").val()  
            }
$.ajax({
    type : "POST",
    url : "/awsMgnt/natGateway/list/elasictIp/save",
    contentType : "application/json",
    async : true,
    data : JSON.stringify(awsInfo),
    success : function(status) {
        setAwsEipAllocationIdList();
        w2utils.unlock($("#layout_layout_panel_main"));
        w2popup.unlock();
    },
    error : function(request, status, error) {
        w2utils.unlock($("#layout_layout_panel_main"));
        w2popup.unlock();
        var errorResult = JSON.parse(request.responseText);
        w2alert(errorResult.message, "");
    }
  });
}

/********************************************************
 * 기능 : setAwsSubnetIdList
 * 설명 : 기본  Azure Subnet 목록 조회 기능
 *********************************************************/
function setAwsSubnetIdList(){
     w2popup.lock(detail_lock_msg, true);
    var accountId = $("select[name='accountId']").val();
    var region = $("select[name='region']").val();
    $.ajax({
           type : "GET",
           url : '/awsMgnt/natGateway/list/subnetIdList/'+accountId+'/'+region,
           contentType : "application/json",
           dataType : "json",
           success : function(data, status) {
               var result = "";
               if(data != null){
                   for(var i=0; i<data.length; i++){
                           result += "<option value='" + data[i].subnetId + "' >";
                           result += data[i].subnetId;
                           result += "  |  ";
                           result += data[i].vpcId;
                           if(data[i].nameTag!=null){
                           result += "  |  ";
                           result += data[i].nameTag;}
                           result += "</option>"; 
                   }
               }
               
               $('#subnetInfoDiv #subnetInfo').html(result);
               w2popup.unlock();
           },
           error : function(request, status, error) {
               w2popup.unlock();
               var errorResult = JSON.parse(request.responseText);
               w2alert(errorResult.message);
           }
       });
}

/********************************************************
 * 기능 : setAwsEipAllocationIdList
 * 설명 : 기본  Azure Subnet 목록 조회 기능
 *********************************************************/
function setAwsEipAllocationIdList(){
     w2popup.lock(detail_lock_msg, true);
    var accountId = $("select[name='accountId']").val();
    var region = $("select[name='region']").val();
    $.ajax({
           type : "GET",
           url : '/awsMgnt/natGateway/list/eipAllocationIdList/'+accountId+'/'+region,
           contentType : "application/json",
           dataType : "json",
           success : function(data, status) {
               var result = "";
               if(data.length == 0){
                   result = "<option value=''>Allocation ID를 가진 EIP가 없습니다. 새로 EIP를 할당 받으세요.</option>";
               }
               else if(data != null){
                   for(var i=0; i<data.length; i++){
                       result += "<option value='" + data[i].allocationId + "' >";
                       if(data[i].allocationId !=null){
                       result += data[i].allocationId;
                       }
                       if(data[i].publicIp !=null){
                       result += "  |  ";
                       result += data[i].publicIp;
                       }
                       result += "</option>"; 
                   }
               }
               
               $('#eipInfoDiv #eipInfo').html(result);
               w2popup.unlock();
           },
           error : function(request, status, error) {
               w2popup.unlock();
               var errorResult = JSON.parse(request.responseText);
               w2alert(errorResult.message);
           }
       });
}


/********************************************************
 * 기능 : initsetting
 * 설명 : 기본 설정값 초기화
 *********************************************************/
function initsetting(){
     bDefaultAccount="";
     w2ui['aws_natGatewayGrid'].clear();
     doSearch();
}

/********************************************************
 * 기능 : clearMainPage
 * 설명 : 다른페이지 이동시 호출
 *********************************************************/
function clearMainPage() {
    $().w2destroy('aws_natGatewayGrid');
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
     <div class="page_site pdt20">인프라 관리 > AWS 관리 > <strong>AWS NAT Gateways 관리 </strong></div>
     <div id="awsMgnt" class="pdt20">
        <ul>
            <li>
                <label style="font-size: 14px">AWS 관리 화면</label> &nbsp;&nbsp;&nbsp; 
                <div class="dropdown" style="display:inline-block;">
                    <a href="#" class="dropdown-toggle iaas-dropdown" data-toggle="dropdown" aria-expanded="false">
                        &nbsp;&nbsp;NAT Gateway 관리<b class="caret"></b>
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
                        <sec:authorize access="hasAuthority('AWS_KEYPAIR_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/awsMgnt/keypair"/>', 'AWS KEYPAIR');">KeyPair 관리</a></li>
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
        <div class="title fl">AWS NAT Gateway 목록</div>
        <div class="fr"> 
            <span id="addBtn" class="btn btn-primary" style="width:120px">할당</span>
            
        </div>
    </div>
    <div id="aws_natGatewayGrid" style="width:100%; height:705px"></div>

</div>

 

<!-- AWS NAT Gateway Allocate 팝업 Div-->
<div id="registPopupDiv" hidden="true">
<form id="awsNatGatewayForm" action="POST" style="padding:5px 0 5px 0;margin:0;">
    <div id="awsNatGatewayCreate" >
         <div class="panel panel-info" style="height: 250px; margin-top: 7px;"> 
            <div class="panel-heading"><b>AWS NAT Gateway 생성</b></div>
            <div class="w2ui-field" style="margin-top:20px; margin-bottom:20px;">
               <label style="width:100%; text-align: left; padding-left: 20px;">NAT Gateway생성 후 Elastic IP 주소를 연결하세요. </label>
           </div>
           <div class="w2ui-field">
               <label style="width:100%; margin-top:20px; text-align: left; padding-left: 20px;"> Subnet</label>
           </div>
            <div class="w2ui-field">
               <div id="subnetInfoDiv" style="width:420px;  padding-left: 20px;">
                   <select id="subnetInfo" style="width:400px;" name="subnetId"><option></option></select>
               </div>
           </div>
           <div class="w2ui-field">
               <label style="width:100%; margin-top:20px; text-align: left; padding-left: 20px;"> Elastic IP Allocation ID</label>
           </div>
           <div class="w2ui-field">    
               <div id="eipInfoDiv" style="width:420px;  padding-left: 20px;">
                   <select id="eipInfo" style="width:400px;" name="allocationId"><option></option></select>
               </div>
           </div>
           
          
           
           
        </div>
    </div> 
</form>

 <div class="w2ui-field">  
               <div style="width:420px; padding-left: 20px;">
                <button style="border:1px solid black;" class="btn" id="" onclick="awsElasticIpAllocate();" style="left: 20px;" > NEW EIP할당</button>
               </div>          
           </div>
</div>
<div id="registPopupBtnDiv" hidden="true">
     <button class="btn btn-primary" id="registBtn" onclick="$('#awsNatGatewayForm').submit();">확인</button>
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
    $("#awsNatGatewayForm").validate({
        ignore : "",
        onfocusout: true,
        rules: {
            subnetId : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body select[name='subnetId']").val() );
                },
            }, allocationId: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body select[name='allocationId']").val() );
                }
            }
        }, messages: {
            subnetId: { 
                 required:  "Subnet ID" + text_required_msg
            }, allocationId: { 
                required:  "Allocation ID"+text_required_msg
            }
        }, unhighlight: function(element) {
            setSuccessStyle(element);
        },errorPlacement: function(error, element) {
            //do nothing
        }, invalidHandler: function(event, validator) {
            var errors = validator.numberOfInvalids();
            if (errors) {
                setInvalidHandlerStyle(errors, validator);
            }
        }, submitHandler: function (form) {
            awsNatGatewayCreate();
        }
    });
});

</script>
