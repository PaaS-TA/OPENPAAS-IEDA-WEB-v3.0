<%
   /* =================================================================
 * 작성일 : 2017.7.27 
 * 작성자 : 이정윤
 * 상세설명 : AWS Security Group 관리 화면
 * =================================================================
 */
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<script>
var save_lock_msg = '<spring:message code="common.save.data.lock"/>';//등록 중 입니다.
var search_lock_msg = '<spring:message code="common.search.data.lock"/>';//조회 중 입니다.
var text_required_msg='<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
var select_required_msg='<spring:message code="common.select.vaildate.required.message"/>';//을(를) 선택하세요.
var delete_lock_msg='<spring:message code="common.delete.data.lock"/>';//삭제 중 입니다.
var text_cidr_msg='<spring:message code="common.text.validate.cidr.message"/>';//CIDR 대역을 확인 하세요.
var delete_confirm_msg='<spring:message code="common.popup.delete.message"/>';//을(를) 삭제 하시겠습니까?

var ingressRules="";//ingress rules array
var groupInfo=""; //group info
var bDefaultAccount = ""; //기본 aws 계정
var accountId = "";
var setAwsRegion = "";//aws 지역 설정
var region = "" //aws 지역 변수
$(function() {

    bDefaultAccount = setDefaultIaasAccountList("aws");
    
    $('#aws_securityGroupGrid').w2grid({
        name : 'aws_securityGroupGrid',
        method : 'GET',
        msgAJAXerror : 'AWS Security Group 목록 조회 실패',
        header : '<b>Property 목록</b>',
        multiSelect : false,
        show : {
            selectColumn : true,
            footer : true
        },
        style : 'text-align: center',
        columns : [ {field : 'recid', caption : 'recid', hidden : true}
                  , {field : 'groupId', caption : 'Group ID', size : '20%', style : 'text-align:center'}
                  , {field : 'groupName', caption : 'Group Name', size : '20%', style : 'text-align:center'}
                  , {field : 'vpcId',caption : 'VPC',size : '20%',style : 'text-align:center'}
                  , {field : 'description',caption : 'Description',size : '20%',style : 'text-align:left'}
                  ],
        onSelect : function(event) {
            event.onComplete = function() {
                $('#deleteBtn').attr('disabled', false);
                
                var accountId =  w2ui.aws_securityGroupGrid.get(event.recid).accountId;
                var groupId = w2ui.aws_securityGroupGrid.get(event.recid).groupId;
                doSearchGroupInboudRules(accountId, groupId);
            }
        },
        onUnselect : function(event) {
            event.onComplete = function() {
                $('#deleteBtn').attr('disabled', true);
                
                var td="<td style='text-align:center' colspan='4'>보안 그룹을 선택하세요.</td>";
                var html ="<tr class='ingressRulesData'>" + td+ "</tr>";
                $("#ingressRulesTable .ingressRulesData").remove()
                $("#ingressRulesTable").append(html);
            }
        },
        onLoad : function(event) {
            if (event.xhr.status == 403) {
                location.href = "/abuse";
                event.preventDefault();
            }
        },
        onError : function(evnet) {
        }
    });
    
    doSearch();
    
    /*************************** *****************************
     * 설명 :  AWS Security Group 생성 팝업 화면
     *********************************************************/
    $("#addBtn").click(function(){
        if($("#addBtn").attr('disabled') == "disabled") return;
        w2popup.open({
            title   : "<b>AWS Security Group 생성</b>",
            width   : 650,
            height  : 350,
            modal   : true,
            body    : $("#SecurityGroupRegistPopupDiv").html(),
            buttons : $("#SecurityGroupRegistPopupBtnDiv").html(),
            onOpen : function(event){
                event.onComplete = function(){
                    getVpcIds();//vpc id 목록조회
                }
            },onClose:function(event){
                $("#ingressRulesTable .ingressRulesData").html("");
                initsetting();
                doSearch();
            }
        });
    });
    
    /********************************************************
     * 설명 : AWS Security Group 삭제 버튼 클릭
    *********************************************************/
    $("#deleteBtn").click(function(){
        if($("#deleteBtn").attr('disabled') == "disabled") return;
        var selected = w2ui['aws_securityGroupGrid'].getSelection();        
        if( selected.length == 0 ){
            w2alert("선택된 정보가 없습니다.", "Security Group 삭제");
            return;
        }
        else {
            var record = w2ui['aws_securityGroupGrid'].get(selected);
            w2confirm({
                title    : "<b>Security Group 삭제</b>",
                msg      : "Security Group (" + record.groupId + ")"+ delete_confirm_msg,
                yes_text : "확인",
                no_text  : "취소",
                height   : 200,
                yes_callBack: function(event){
                    deleteAwsSecurityGroupInfo(record);
                },
                no_callBack    : function(){
                    $("#ingressRulesTable .ingressRulesData").html("");
                    w2ui['aws_securityGroupGrid'].clear();
                    accountId = record.accountId;
                    initsetting();
                }
            });
        }
    });
    
});

/********************************************************
 * 설명 : AWS Security Group 목록 조회 Function 
 * 기능 : doSearch
 *********************************************************/
function doSearch() {
    region = $("select[name='region']").val();
    if(region == null) region = "us-west-2";
    if(accountId != "")
    w2ui['aws_securityGroupGrid'].load("<c:url value='/awsMgnt/securityGroup/list/"+accountId+"/"+ region +"'/>","",function(event){});
    doButtonStyle();
}

/********************************************************
 * 설명 : AWS SecurityGroup 생성 버튼 클릭
 * 기능 : saveAwsSecurityGroupInfo
 *********************************************************/ 
function saveAwsSecurityGroupInfo(){
     w2popup.lock(save_lock_msg, true);
     var ingressRules = setIngressRulesInfo($(".w2ui-msg-body input:radio[name='ingressRuleType']:checked").val());
     var vpcidss = $(".w2ui-msg-body select[name='vpcId']").val();
     var groupInfo ={
             accountId : $("select[name='accountId']").val()
            ,nameTag: $(".w2ui-msg-body input[name='nameTag']").val()
            ,groupName: $(".w2ui-msg-body input[name='groupName']").val()
            ,description : $(".w2ui-msg-body input[name='description']").val()
            ,vpcId: $(".w2ui-msg-body select[name='vpcId']").val()
            ,region : $("select[name='region']").val()
            ,ingressRuleType: $(".w2ui-msg-body input:radio[name='ingressRuleType']:checked").val()
            ,ingressRules : ingressRules
     }
     $.ajax({
         type: "POST",
         url : "/awsMgnt/securityGroup/save",
         contentType: "application/json",
         async : true,
         data: JSON.stringify(groupInfo),
         success : function(status){
             w2popup.unlock();
             w2popup.close();
             accountId = groupInfo.accountId;
             doSearch();
         }, error : function(request, status, error){
             w2popup.unlock();
             var errorResult = JSON.parse(request.responseText).message;
             var idx = errorResult.indexOf("(");
             var message = errorResult.substring(0, idx);
             w2alert(message);
         }
     });
 }
 
/********************************************************
 * 설명 : AWS Security Group Inbound Rule 유형에 따른 정보 설정 
 * 기능 : getIngressRulesInfo
 *********************************************************/
 function setIngressRulesInfo(type){
      var list = new Array();
      if( type == "boshSecurity" ){
          $(".w2ui-msg-body .bosh_security_rules").each(function(index){
              var protocol = "tcp";
              if( ($(this).attr("name")).indexOf("Udp") > -1 ){
                  protocol = "udp";
              }
              if( ($(this).attr("name")).indexOf("Icmp") > -1 ){
                  protocol = "icmp";
              }
              var ingressRule = {
                     protocol: protocol
                    ,portRange : $(this).val()
              }
              list.push(ingressRule);
          });
          return list;
      }else if( type == "cfSecurity" ){
          $(".w2ui-msg-body .cf_security_rules").each(function(index){
              var protocol = "tcp";
              if( ($(this).attr("name")).indexOf("Udp") > -1 ){
                  protocol = "udp";
              }
              if( ($(this).attr("name")).indexOf("Icmp") > -1 ){
                  protocol = "icmp";
              }
              var ingressRule = {
                     protocol: protocol
                    ,portRange : $(this).val()
              }
              list.push(ingressRule);
          });
          return list;
      }else{
          return;
      }
 }

    
/********************************************************
 * 설명 : AWS Security Group Inbound Rule 정보 조회 
 * 기능 : doSearchGroupInboudRules
 *********************************************************/
function doSearchGroupInboudRules(accountId, groupId){
     w2utils.lock($("#layout_layout_panel_main"), search_lock_msg, true);
     var region = $("select[name='region']").val();
     $.ajax({
         type : "GET",
         url : "/awsMgnt/securityGroup/ingress/list/"+accountId+"/"+groupId+"/"+region,
         contentType : "application/json",
         success : function(data, status) {
             w2utils.unlock($("#layout_layout_panel_main"));
             if( !checkEmpty(data) ){
                 for( var i=0; i<data.length; i++ ){
                    var html = "";
                    var trafficType = checkEmpty(data[i].trafficType) ? "-" : data[i].trafficType;
                    var protocol = checkEmpty(data[i].protocol) ? "-" : data[i].protocol;
                    var source = checkEmpty(data[i].source) ? "-" : data[i].source;
                    var portRange = checkEmpty(data[i].portRange) ? "-" : data[i].portRange;
                    html +="<tr class='ingressRulesData'>";
                    html +="<td class='rules'>"+trafficType+"</td>";
                    html +="<td class='rules'>"+protocol   +"</td>";
                    html +="<td class='rules'>"+portRange  +"</td>";
                    html +="<td class='rules'>"+source     +"</td>";
                    html +="</tr>";
                    if( i == 0 ){
                        $("#ingressRulesTable .ingressRulesData").remove()
                    }
                    $("#ingressRulesTable").append(html);
                 }
             }else{
                 var html = "";
                 var style="style='text-align:center'";
                 html +="<tr class='ingressRulesData'>";
                 html +="<td "+style+">-</td><td "+style+">-</td><td "+style+">-</td><td "+style+">-</td>";
                 html +="</tr>";
                 $("#ingressRulesTable .ingressRulesData").remove()
                 $("#ingressRulesTable").append(html);
             }
             return;
         },
         error : function(request, status, error) {
             w2utils.unlock($("#layout_layout_panel_main"));
             var errorResult = JSON.parse(request.responseText);
             w2alert(errorResult.message, "Security Group Inbound Rules정보");
         } 
     });
 }
    
/****************************************************
 * 기능 : getVpcIds
 * 설명 : Security Group 생성시 VpcIds 조회 요청
 *****************************************************/
function getVpcIds() {
    w2popup.lock("VPC "+search_lock_msg, true);
    var accountId = $("select[name=accountId]").val();
    var region = $("select[name=region]").val();
    $.ajax({
        type : "GET",
        url : "/awsMgnt/securityGroup/save/vpcs/"+accountId+"/"+region,
        contentType : "application/json",
        success : function(data, status) {
            setupVpcIds(data);
        },
        error : function(request, status, error) {
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "VPC 목록 조회");
        }
    });
}
/****************************************************
 * 기능 : setupVpcIds
 * 설명 : Security Group에 대한 Vpc Id설정 
*****************************************************/
function setupVpcIds(data){
     var options= "";
     if( data.length == 0 ){
         options +="<option value=''>존재하지 않습니다.</option>";
     }else{
         for (var i=0; i<data.length; i++){
             options +="<option value='"+data[i].vpcId+"'>" + data[i].vpcId + "</option>"; 
         } 
     }
     vpcsInfo=data;
     w2popup.unlock();
     $(".w2ui-msg-body select[name='vpcId']").html(options);
}
    
    
/****************************************************
 * 기능 : setStyleBySourceType
 * 설명 : Ingress Rule Souce 유형에 따른 스타일 설정 
*****************************************************/
function setStyleBySourceType(event){
    var index = $(event).parent().parent().attr("class").split("_")[1];
    if( event.value == "cidr" ){
        $(".w2ui-msg-body select[name='sourceGroupId_"+index+"']").css("display", "none");
        $(".w2ui-msg-body select[name='sourceGroupId_"+index+"']").attr("disabled", true);
        $(".w2ui-msg-body select[name='sourceGroupId_"+index+"']").val("");
        $(".w2ui-msg-body input[name='sourceIp_"+index+"']").val("");
        $(".w2ui-msg-body input[name='sourceIp_"+index+"']").attr("disabled", false);
        $(".w2ui-msg-body input[name='sourceIp_"+index+"']").css("display","inline-block").css("borderColor", "#bababa");
    }else if( event.value == "securityId" ){
        $(".w2ui-msg-body input[name='sourceIp_"+index+"']").attr("disabled", true);
        $(".w2ui-msg-body input[name='sourceIp_"+index+"']").css("display","none")
        $(".w2ui-msg-body input[name='sourceIp_"+index+"']").val("");
        $(".w2ui-msg-body select[name='sourceGroupId_"+index+"']").attr("disabled", false);
        $(".w2ui-msg-body select[name='sourceGroupId_"+index+"']").css("display", "inline-block").css("borderColor", "#bababa");
    }else if( event.value == "anywhere" ){
        $(".w2ui-msg-body select[name='sourceGroupId_"+index+"']").val("");
        $(".w2ui-msg-body select[name='sourceGroupId_"+index+"']").attr("disabled", true);
        $(".w2ui-msg-body select[name='sourceGroupId_"+index+"']").css("display", "none");
        $(".w2ui-msg-body input[name='sourceIp_"+index+"']").val("0.0.0.0/0, ::/0");
        $(".w2ui-msg-body input[name='sourceIp_"+index+"']").attr("disabled", false);
        $(".w2ui-msg-body input[name='sourceIp_"+index+"']").css("display","inline-block").css("borderColor", "#bababa");
    }
    if( $(".w2ui-msg-body input[name='sourceIp_"+index+"']").next().is("p") || $(".w2ui-msg-body input[name='sourceGroupId_"+index+"']").next().is("p") ){
        $(".w2ui-msg-body input[name='sourceIp_"+index+"']").next().remove();
        $(".w2ui-msg-body input[name='sourceGroupId_"+index+"']").next().remove();
    }
}

 
 
 /****************************************************
  * 기능 : addIngressRulesValidation
  * 설명 : 인바운드 규칙 유효성 검사 추가
 *****************************************************/
 function addIngressRulesValidation(index){
      $("[name*='trafficType_"+index+"']").rules("add", {
             required: function(){
                 return checkEmpty($(".w2ui-msg-body select[name='trafficType_"+index+"']").val());
             }, messages: {required: "type"+select_required_msg}
      });
      $("[name*='portRange_"+index+"']").rules("add", {
          required: function(){
              return checkEmpty($(".w2ui-msg-body input[name='portRange_"+index+"']").val());
          }, messages: {required: "portRange"+text_required_msg}
      });
     $("[name*='sourceIp_"+index+"']").rules("add", {
         required: function(){
             if( $(".w2ui-msg-body select[name='sourceType_"+index+"']").val()  == "cidr"){
                 return checkEmpty($(".w2ui-msg-body input[name='sourceIp_"+index+"']").val());
             }else{
                 return false;
             }
         },ipv4Range : function(){
             if( $(".w2ui-msg-body select[name='sourceType_"+index+"']").val()  == "cidr"){
                 return $(".w2ui-msg-body input[name='sourceIp_"+index+"']").val();     
             }else{
                 return "0.0.0.0/0";
             }
         }, messages: {required: "source"+text_required_msg}
     });
     $("[name*='sourceGroupId_"+index+"']").rules("add", {
         required: function(){
             if( $(".w2ui-msg-body select[name='sourceType_"+index+"']").val()  == "securityId"){
                 return checkEmpty($(".w2ui-msg-body select[name='sourceGroupId_"+index+"']").val());
             }else{
                 return false;
             }
         }, messages: {required: "source"+select_required_msg}
     });
 }
 
 /****************************************************
  * 기능 : removeIngressRules
  * 설명 : ingress 규칙 삭제
 *****************************************************/
 function removeIngressRules(event){
      $(event).parent().parent().remove();
 }

/********************************************************
 * 설명 : AWS SecurityGroup 삭제
 * 기능 : deleteAwsSecurityGroupInfo
*********************************************************/
function deleteAwsSecurityGroupInfo(record){
     w2popup.lock(delete_lock_msg, true);
     var groupInfo = {
             accountId : record.accountId,
             groupId : record.groupId,
             region :  $("select[name='region']").val()
     }
     $.ajax({
         type  : "DELETE",
         url   : "/awsMgnt/securityGroup/delete",
         async : true,
         data  : JSON.stringify(groupInfo),
         contentType : "application/json",
         success : function(status) {
             w2popup.unlock();
             w2popup.close();
             accountId = groupInfo.accountId;
             doSearch();
         },error : function(request, status, error) {
             w2popup.unlock();
             initsetting();
             var errorResult = JSON.parse(request.responseText);
             w2alert(errorResult.message);
         }
     });
 };
   
 
/********************************************************
 * 기능 : initsetting
 * 설명 : 기본 설정값 초기화
 *********************************************************/
function initsetting(){
     bDefaultAccount="";
     groupInfo="";
     ingressRules="";
     $("#ingressRulesEditTable > tbody > tr:gt(1)").remove();
     w2ui['aws_securityGroupGrid'].clear();
     doSearch();
}

/********************************************************
 * 설명 : 초기 버튼 스타일
 * 기능 : doButtonStyle
 *********************************************************/
function doButtonStyle() {
    $('#deleteBtn').attr('disabled', true);
}

/********************************************************
 * 기능 : clearMainPage
 * 설명 : 다른페이지 이동시 호출
 *********************************************************/
function clearMainPage() {
    $().w2destroy('aws_securityGroupGrid');
}

/********************************************************
 * 기능 : resize
 * 설명 : 화면 리사이즈시 호출
 *********************************************************/
$( window ).resize(function() {
  setLayoutContainerHeight();
});

</script>

<div id="main">
    <div class="page_site pdt20">인프라 관리 > AWS 관리 > <strong>AWS Security Group 관리 </strong></div>
    <div id="awsMgnt" class="pdt20">
        <ul>
            <li>
                <label style="font-size: 14px">AWS 관리 화면</label> &nbsp;&nbsp;&nbsp; 
                <div class="dropdown" style="display:inline-block;">
                    <a href="#" class="dropdown-toggle iaas-dropdown" data-toggle="dropdown" aria-expanded="false">
                        &nbsp;&nbsp;Security Group 관리<b class="caret"></b>
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
                <label  style="font-size:14px">AWS Region</label>&nbsp;&nbsp;&nbsp;
                <select name="region" onchange="awsRegionOnchange();" id="regionList" class="select" style="width:300px; font-size:15px; height: 32px;"></select>&nbsp;&nbsp;&nbsp;
            </li>
            <li>
                <label style="font-size: 14px">AWS 계정 명</label> &nbsp;&nbsp;&nbsp; 
                <select name="accountId" id="setAccountList" class="select" style="width: 300px; font-size: 15px; height: 32px;" onchange="setAccountInfo(this.value, 'aws')">
                </select>
                <span id="doSearch" onclick="setDefaultIaasAccount('noPopup','aws');" class="btn btn-info" style="width:80px" >선택</span>
            </li>
        </ul>
    </div>
    <div class="pdt20">
        <div class="title fl">AWS Security Group 목록</div>
        <div class="fr">
            <span id="addBtn" class="btn btn-primary" style="width: 120px">생성</span>
            <span id="deleteBtn" class="btn btn-danger" style="width: 120px">삭제</span>
        </div>
    </div>
    <!-- AWS Security Group 목록 Div -->    
    <div id="aws_securityGroupGrid" style="width: 100%; height: 405px"></div>

    <!-- AWS Security Group Inbound Rule Div -->
    <div class="pdt20">
        <div class="title fl">AWS 인바운드 규칙</div>
    </div>
    
    <!-- AWS Security Group Inbound Rules 목록 -->
    <div id="IngressRulesDiv" style="width: 100%;">
        <table id="ingressRulesTable" class="table table-condensed table-hover">
            <tr>
                <th style="width: 25%;" class="trTitle">Type</th>
                <th style="width: 25%" class="trTitle">Protocol</th>
                <th style="width: 25%" class="trTitle">Port Range</th>
                <th style="width: 25%" class="trTitle">Source</th>
            </tr>
            <tr class="ingressRulesData" >
               <td style="text-align:center" colspan="4">보안 그룹을 선택하세요.</td>
            </tr>
        </table>
    </div>
</div>

<!-- AWS Security Group 등록 팝업 Div-->
<div id="SecurityGroupRegistPopupDiv" hidden="true">
    <form id="awsSecurityGroupForm" action="POST" style="padding:5px 0 5px 0;margin:0;">
        <div class="panel panel-info"> 
            <div class="panel-heading"><b>AWS Security Group 정보</b></div>
            <div class="panel-body" style="padding:20px 10px; height:215px; overflow-y:auto;">
                <input type="hidden" name="groupId" />
                <div class="w2ui-field">
                    <label style="width:36%;text-align: left; padding-left: 20px;">Security Group Tag</label>
                    <div>
                        <input name="nameTag" type="text"  maxlength="100" style="width: 300px" placeholder="보안 그룹 태그"/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:36%;text-align: left; padding-left: 20px;">Group Name</label>
                    <div>
                        <input name="groupName" type="text"  maxlength="100" style="width: 300px" placeholder="그룹 명"/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:36%;text-align: left; padding-left: 20px;">Description</label>
                    <div>
                        <input name="description" type="text"  maxlength="100" style="width: 300px" placeholder="설명"/>
                    </div>
                </div>
                
                <div class="w2ui-field">
                    <label style="width: 36%; text-align: left; padding-left: 20px;">VPC</label>
                    <div>
                       <select name="vpcId" style="width:300px">
                           <option value="">VPC를 선택하세요.</option>
                       </select>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width: 36%; text-align: left; padding-left: 20px;">Ingress Rule</label>
                    <div>
                        <label><input  type="radio" name="ingressRuleType" id="none" value="none"  checked="checked" />없음</label>&nbsp;&nbsp;&nbsp;
                        <label><input type="radio" name="ingressRuleType" id="boshSecurity" value="boshSecurity" />bosh-security</label>&nbsp;&nbsp;&nbsp;
                        <label><input type="radio" name="ingressRuleType" id="cfSecurity" value="cfSecurity" />cf-security</label>
                    </div>
                    <div  style="display:none">
                        <input type="text" name="ssh" value="22" class="bosh_security_rules">
                        <input type="text" name="boshAgent" value="6868" class="bosh_security_rules">
                        <input type="text" name="boshDirector" value="25555" class="bosh_security_rules">
                        <input type="text" name="allTcp" value="0-65535" class="bosh_security_rules">
                        <input type="text" name="allUdp" value="0-65535" class="bosh_security_rules">
                        <input type="text" name="allIcmp" value="0-65535" class="bosh_security_rules">
                    </div>
                    <div  style="display:none">
                        <input type="text" name="http" value="80" class="cf_security_rules">
                        <input type="text" name="https" value="443" class="cf_security_rules">
                        <input type="text" name="cfLogs" value="4443" class="cf_security_rules">
                        <input type="text" name="allTcp" value="0-65535" class="cf_security_rules">
                        <input type="text" name="allUdp" value="0-65535" class="cf_security_rules">
                        <input type="text" name="allIcmp" value="0-65535" class="cf_security_rules">
                    </div>
                </div>
            </div>
        </div>
    </form> 
</div>
<div id="SecurityGroupRegistPopupBtnDiv" hidden="true">
     <button class="btn" id="registBtn" onclick="$('#awsSecurityGroupForm').submit();">확인</button>
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
    $.validator.addMethod( "ipv4Range", function( value, element, params ) {
        return /^((\b|\.)(0|1|2(?!5(?=6|7|8|9)|6|7|8|9))?\d{1,2}){4}(-((\b|\.)(0|1|2(?!5(?=6|7|8|9)|6|7|8|9))?\d{1,2}){4}|\/((0|1|2|3(?=1|2))\d|\d))\b$/.test(params);
    }, text_cidr_msg );
    $("#awsSecurityGroupForm").validate({
        ignore : "",
        onfocusout: true,
        rules: {
            groupName: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='groupName']").val() );
                }
            },
            description: {
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='description']").val() );
                }
            }
        }, messages: {
            groupName: { 
                required:  "Group Name"+text_required_msg
            },
            description: { 
                required:  "Description"+text_required_msg
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
            saveAwsSecurityGroupInfo();
        }
    });
});
</script>

