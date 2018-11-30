<%
/* =================================================================
 * 작성일 : 2017.07.07
 * 작성자 : 이동현
 * 상세설명 : AWS 인터넷 게이트웨이 관리 화면
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
var save_lock_msg = '<spring:message code="common.save.data.lock"/>';//등록 중 입니다.
var delete_lock_msg='<spring:message code="common.delete.data.lock"/>';//삭제 중 입니다.
var attach_lock_msg='<spring:message code="common.attach.data.lock"/>';//연결 중 입니다.
var detach_lock_msg='<spring:message code="common.detach.data.lock"/>';//연결 해제 중입니다. 
var text_required_msg='<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
var delete_confirm_msg='<spring:message code="common.popup.delete.message"/>';//을(를) 삭제 하시겠습니까?
var delete_confirm_msg='<spring:message code="common.popup.detach.message"/>';//을(를) 해제하시겠습니까?

$(function() {
    bDefaultAccount = setDefaultIaasAccountList("aws");
    
    $('#aws_internetGateWayGrid').w2grid({
        name: 'aws_internetGateWayGrid',
        method: 'GET',
        msgAJAXerror : 'AWS 계정을 확인해주세요.',
        header: '<b>InternatGateway 목록</b>',
        multiSelect: false,
        show: {    
                selectColumn: true,
                footer: true},
        style: 'text-align: center',
        columns    : [
                     {field: 'recid',     caption: 'recid', hidden: true}
                   , {field: 'accountId',     caption: 'accountId', hidden: true}
                   , {field: 'internetGatewayName', caption: 'InternetGateWay Name', size: '50%', style: 'text-align:center', render : function(record){
                       if(record.internetGatewayName == null || record.internetGatewayName == ""){
                           return "-"
                       }else{
                           return record.internetGatewayName;
                       }}
                   }
                   , {field: 'internetGatewayId', size: '50%', caption: 'InternetGateWay Id'}
                   , {field: 'status', size: '50%', caption: 'InternetGateWay Status', render : function(record){
                       if(record.status == null || record.status == ""){
                           return "detached"
                       }else{
                           return "attached"
                       }}
                   }
                   , {field: 'vpcId', size: '50%', caption: 'VPC Id', render : function(record){
                       if(record.vpcId == null || record.vpcId == ""){
                           return "-"
                       }else{
                           return record.vpcId
                       }}
                   }
                   ],
        onSelect: function(event) {
            event.onComplete = function() {
                var selected = w2ui['aws_internetGateWayGrid'].getSelection();
                var record = w2ui['aws_internetGateWayGrid'].get(selected);
                if(record.vpcId == null){
                    $('#attachBtn').attr('disabled', false);
                }else{
                    $('#detachBtn').attr('disabled', false);
                }
                $('#deleteBtn').attr('disabled', false);
            }
        },
        onUnselect: function(event) {
            event.onComplete = function() {
                $('#attachBtn').attr('disabled', true);
                $('#detachBtn').attr('disabled', true);
                $('#deleteBtn').attr('disabled', true);
            }
        },
           onLoad:function(event){
            if(event.xhr.status == 403){
                location.href = "/abuse";
                event.preventDefault();
            }
        }, onError:function(event){
            
        }
    });
    
    /********************************************************
     * 설명 : AWS 인터넷 게이트웨이 생성 버튼 클릭
    *********************************************************/
    $("#addBtn").click(function(){
       if($("#addBtn").attr('disabled') == "disabled") return;
       w2popup.open({
           title   : "<b>AWS 게이트웨이 생성</b>",
           width   : 710,
           height  : 223,
           modal   : true,
           body    : $("#registPopupDiv").html(),
           buttons : $("#registPopupBtnDiv").html(),
           onClose : function(event){
              accountId = $("select[name='accountId']").val();
              w2ui['aws_internetGateWayGrid'].clear();
              doSearch();
           }
       });
    });
    
    /********************************************************
     * 설명 : AWS 인터넷 게이트웨이 삭제 버튼 클릭
    *********************************************************/
   $("#deleteBtn").click(function(){
       if($("#deleteBtn").attr('disabled') == "disabled") return;
       var selected = w2ui['aws_internetGateWayGrid'].getSelection();
       if( selected.length == 0 ){
           w2alert("선택된 정보가 없습니다.", "인터넷 게이트웨이 삭제");
           return;
       }
       else {
           var record = w2ui['aws_internetGateWayGrid'].get(selected);
           w2confirm({
               title    : "<b>인터넷 게이트웨이 삭제</b>",
               msg      : "InternetGateway (" + record.internetGatewayId + ")"+delete_confirm_msg,
               yes_text : "확인",
               no_text  : "취소",
               yes_callBack: function(event){
                   deleteAwsinternetGatewayInfo(record);
               },
               no_callBack    : function(){
                   w2ui['aws_internetGateWayGrid'].clear();
                   accountId = record.accountId;
                   doSearch();
               }
           });
       }
   });
   
   /********************************************************
    * 설명 : AWS VPC 연결 버튼 클릭
   *********************************************************/
   $("#attachBtn").click(function(){
       if($("#attachBtn").attr('disabled') == "disabled") return;
       w2popup.open({
           title   : "<b>AWS VPC 연결</b>",
           width   : 540,
           height  : 230,
           modal   : true,
           body    : $("#attachPopupDiv").html(),
           buttons : $("#attachPopupBtnDiv").html(),
           onOpen : function(event){
               event.onComplete = function(){
                   getAwsVpcListInfo();
               }
           },
           onClose : function(event){
              accountId = $("select[name='accountId']").val();
              w2ui['aws_internetGateWayGrid'].clear();
              doSearch();
           }
       });
   });
    
   /********************************************************
    * 설명 : AWS VPC 연결 해제 버튼 클릭
   *********************************************************/
  $("#detachBtn").click(function(){
      if($("#detachBtn").attr('disabled') == "disabled") return;
      var selected = w2ui['aws_internetGateWayGrid'].getSelection();
      if( selected.length == 0 ){
          w2alert("선택된 정보가 없습니다.", "VPC 연결 해제");
          return;
      }
      else {
          var record = w2ui['aws_internetGateWayGrid'].get(selected);
          w2confirm({
              title    : "<b>VPC 연결 해제</b>",
              msg      : "InternetGateway (" + record.internetGatewayId + ")와 VPC ("+record.vpcId+") </br>" +delete_confirm_msg,
              yes_text : "확인",
              no_text  : "취소",
              yes_callBack: function(event){
                  detachToVpc(record);
              },
              no_callBack    : function(){
                  w2ui['aws_internetGateWayGrid'].clear();
                  accountId = record.accountId;
                  doSearch();
              }
          });
      }
  });
});

/********************************************************
 * 설명 : AWS 인터넷 게이트웨이 생성 
 * 기능 : saveAwsInternetGatewayInfo
 *********************************************************/
function saveAwsInternetGatewayInfo(){
     w2popup.lock(save_lock_msg, true);
     var internetGatewayInfo = {
         accountId : $("select[name='accountId']").val(),
         region : $("select[name='region']").val(),
         internetGatewayName : $(".w2ui-msg-body input[name='internetGatewayName']").val()
     }
     $.ajax({
         type : "POST",
            url : "/awsMgnt/internetGateWay/save",
            contentType : "application/json",
            async : true,
            data : JSON.stringify(internetGatewayInfo),
            success : function(status) {
                w2popup.unlock();
                w2popup.close();
                accountId = internetGatewayInfo.accountId;
                doSearch();
            }, error : function(request, status, error) {
                w2popup.unlock();
                var errorResult = JSON.parse(request.responseText);
                w2alert(errorResult.message);
            }
     });
}
/********************************************************
 * 설명 : AWS 인터넷 게이트웨이 삭제 Function 
 * 기능 : deleteAwsinternetGatewayInfo
 *******************************************************/
function deleteAwsinternetGatewayInfo(record){
     w2popup.lock( delete_lock_msg, true );
     var internetGatewayInfo = {
             accountId : record.accountId,
             region : $("select[name='region']").val(),
             internetGatewayId : record.internetGatewayId
     }
     $.ajax({
         type : "DELETE",
         url : "/awsMgnt/internetGateWay/delete",
         contentType : "application/json",
         async : true,
         data : JSON.stringify(internetGatewayInfo),
         success : function(status) {
             w2popup.unlock();
             w2popup.close();
             accountId = internetGatewayInfo.accountId;
             doSearch();
         }, error : function(request, status, error) {
             w2popup.unlock();
             var errorResult = JSON.parse(request.responseText);
             w2alert(errorResult.message);
         }
    });
}

/********************************************************
 * 설명 : AWS 인터넷 게이트웨이에 연결한 VPC 정보 조회
 * 기능 : getAwsVpcListInfo
 *******************************************************/
function getAwsVpcListInfo(){
     w2popup.lock(search_lock_msg, true);
     var selected = w2ui['aws_internetGateWayGrid'].getSelection();
     var record = w2ui['aws_internetGateWayGrid'].get(selected);
     var region = $("select[name='region']").val();
     $.ajax({
        type : "GET",
        async : true,
        url : "/awsMgnt/internetGateWay/attach/vpc/"+record.accountId+"/"+region+"",
        contentType : "application/json",
        success : function(data, status) {
            var result = "";
            if(data.length != 0){
                for(var i=0;i<data.length;i++){
                    result+="<option value="+data[i].vpcId+">"+data[i].vpcId;
                    console.log(JSON.stringify(data[i])+"TEST NAME TAG");
                    if(data[i].vpcName != null && data[i].vpcName != ""){
                      result+=" | "+data[i].vpcName;
                    }
                    result+="</option>";
            }
            }else{
                result+="<option value='noAttachVpc'>연결 할 VPC가 존재 하지 않습니다.</option>"
            }
            $(".w2ui-msg-body select[name='vpcToAttachId']").html(result);
            w2popup.unlock();
        },
        error : function(request, status, error) {
            w2ui['aws_internetGateWayGrid'].clear();
            var errorResult = JSON.parse(request.responseText);
            w2popup.unlock();
        }
    });
}
/********************************************************
 * 설명 : AWS VPC 연결
 * 기능 : attachToVpc
 *******************************************************/
function attachToVpc(){
    if($(".w2ui-msg-body select[name='vpcToAttachId']").val() == "noAttachVpc"){
        w2alert("연결 할 VPC가 존재 하지 않습니다.");
        return;
    }
    w2popup.lock(attach_lock_msg, true);
    var selected = w2ui['aws_internetGateWayGrid'].getSelection();
    var record = w2ui['aws_internetGateWayGrid'].get(selected);
    var internetGatewayInfo = {
        accountId : $("select[name='accountId']").val(),
        internetGatewayId : record.internetGatewayId,
        region : $("select[name='region']").val(),
        vpcId : $(".w2ui-msg-body select[name='vpcToAttachId']").val()
    }
    $.ajax({
        type : "PUT",
        url : "/awsMgnt/internetGateWay/attach",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(internetGatewayInfo),
        success : function(status) {
            w2popup.unlock();
            w2popup.close();
            w2ui['aws_internetGateWayGrid'].clear();
            accountId = internetGatewayInfo.accountId;
            doSearch();
        }, error : function(request, status, error) {
            w2popup.unlock();
            w2popup.close();
            w2ui['aws_internetGateWayGrid'].clear();
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message);
        }
    });
}

/********************************************************
 * 설명 : AWS VPC 연결 해제
 * 기능 : detachToVpc
 *******************************************************/
function detachToVpc(record){
     w2popup.lock(detach_lock_msg, true);
     var internetGatewayInfo = {
            region : $("select[name='region']").val(),
            accountId : record.accountId,
            internetGatewayId : record.internetGatewayId,
            vpcId : record.vpcId
     }
    $.ajax({
        type : "PUT",
        url : "/awsMgnt/internetGateWay/detach",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(internetGatewayInfo),
        success : function(status) {
            w2popup.unlock();
            w2popup.close();
            w2ui['aws_internetGateWayGrid'].clear();
            accountId = internetGatewayInfo.accountId;
            doSearch();
        }, error : function(request, status, error) {
            w2popup.unlock();
            w2popup.close();
            w2ui['aws_internetGateWayGrid'].clear();
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message);
        }
    });
}
    
/********************************************************
 * 설명 : AWS 인터넷 게이트웨이 목록 조회 Function 
 * 기능 : doSearch
 *********************************************************/
function doSearch() {
    region = $("select[name='region']").val();
    if(region == null) region = "us-west-2";
    w2ui['aws_internetGateWayGrid'].load('/awsMgnt/internetGateWay/list/'+accountId+'/'+region+'');
    doButtonStyle();
    accountId = "";
}

/********************************************************
 * 설명 : 초기 버튼 스타일
 * 기능 : doButtonStyle
 *********************************************************/
function doButtonStyle() {
    $('#deleteBtn').attr('disabled', true);
}

/****************************************************
 * 기능 : resize
 * 설명 : 화면 리사이즈시 호출
*****************************************************/
$( window ).resize(function() {
  setLayoutContainerHeight();
});

/********************************************************
 * 설명 : 초기 버튼 스타일
 * 기능 : doButtonStyle
 *********************************************************/
function doButtonStyle() {
    $('#attachBtn').attr('disabled', true);
    $('#detachBtn').attr('disabled', true);
    $('#deleteBtn').attr('disabled', true);
}

/****************************************************
 * 기능 : clearMainPage
 * 설명 : 다른페이지 이동시 호출
*****************************************************/
function clearMainPage() {
    $().w2destroy('aws_internetGateWayGrid');
}
    
</script>
<div id="main">
     <div class="page_site pdt20">인프라 관리 > AWS 관리 > <strong>AWS Internal Gateway 관리 </strong></div>
     <div id="awsMgnt" class="pdt20">
        <ul>
            <li>
                <label style="font-size: 14px">AWS 관리 화면</label> &nbsp;&nbsp;&nbsp; 
                <div class="dropdown" style="display:inline-block;">
                    <a href="#" class="dropdown-toggle iaas-dropdown" data-toggle="dropdown" aria-expanded="false">
                        &nbsp;&nbsp;Internet Gateway 관리<b class="caret"></b>
                    </a>
                    <ul class="dropdown-menu alert-dropdown">
                        <sec:authorize access="hasAuthority('AWS_VPC_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/awsMgnt/vpc"/>', 'AWS VPC');">VPC 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('AWS_SUBNET_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/awsMgnt/subnet"/>', 'AWS SUBNET');">Subnet 관리</a></li>
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
        <div class="title fl">AWS Internat Gateway 목록</div>
        <div class="fr"> 
            <sec:authorize access="hasAuthority('AWS_INTERNET_GATEWAY_CREATE')">
            <span id="addBtn" class="btn btn-primary" style="width:120px">생성</span>
            </sec:authorize>
            <sec:authorize access="hasAuthority('AWS_INTERNET_GATEWAY_ATTACH')">
            <span id="attachBtn" class="btn btn-info" style="width:120px">VPC 연결</span>
            </sec:authorize>
            <sec:authorize access="hasAuthority('AWS_INTERNET_GATEWAY_DETACH')">
            <span id="detachBtn" class="btn btn-warning" style="width:120px">VPC 연결 해제</span>
            </sec:authorize>
            <sec:authorize access="hasAuthority('AWS_INTERNET_GATEWAY_DELETE')">
            <span id="deleteBtn" class="btn btn-danger" style="width:120px">삭제</span>
            </sec:authorize>
        </div>
    </div>
    <div id="aws_internetGateWayGrid" style="width:100%; height:475px"></div>
    
    <!-- 인터넷 게이트웨이 생성 팝업 -->
<div id="registPopupDiv" hidden="true">
    <form id="awsInternetGatewayForm" action="POST">
        <div class="panel panel-info" style="height: 120px; margin-top: 7px;"> 
            <div class="panel-heading"><b>AWS InternetGateway 정보</b></div>
            <div class="panel-body">
                <input type="hidden" name="accountId"/>
                <div class="w2ui-field">
                    <label style="width:36%;text-align: left; padding-left: 20px;">Internet Gateway Name tag</label>
                    <div>
                        <input name="internetGatewayName" type="text"   maxlength="100" style="width: 300px; margin-top: 1px;" placeholder="인터넷 게이트웨이 태그 명을 입력하세요."/>
                    </div>
                </div>
            </div>
        </div>
    </form> 
</div>
<div id="registPopupBtnDiv" hidden="true">
     <button class="btn" id="registBtn" onclick="$('#awsInternetGatewayForm').submit();">확인</button>
     <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
</div>

<div id="attachPopupDiv" hidden="true">
    <form id="awsAttachVpcForm" action="POST" style="padding:5px 0 5px 0;margin:0;">
        <div class="panel panel-info" style="height: 110px; margin-top: 7px;"> 
            <div class="panel-heading"><b>AWS 연결 할 VPC 정보</b></div>
            <div class="panel-body" style="padding:20px 10px; height:90px; overflow-y:auto;">
                <input type="hidden" name="accountId"/>
                <div class="w2ui-field">
                    <label style="width:36%;text-align: left; padding-left: 100px;">VPC</label>
                        <select id = "vpcToAttachId" name = "vpcToAttachId" style="width: 220px;"></select>
                </div>
            </div>
        </div>
    </form> 
</div>
<div id="attachPopupBtnDiv" hidden="true">
     <button class="btn" id="registBtn" onclick="attachToVpc();">확인</button>
     <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
</div>
</div>

<div id="registAccountPopupDiv"  hidden="true">
    <input name="codeIdx" type="hidden"/>
    <div class="panel panel-info" style="margin-top:5px;" >    
        <div class="panel-heading"><b>AWS 계정 별칭 목록</b></div>
        <div class="panel-body">
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
    $("#awsInternetGatewayForm").validate({
        ignore : "",
        onfocusout: true,
        rules: {
        }, messages: {
            internetGatewayName: { 
                 required:  "InternetGateway Name Tag" + text_required_msg
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
            saveAwsInternetGatewayInfo();
        }
    });
});

</script>
