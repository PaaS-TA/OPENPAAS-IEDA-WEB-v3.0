<%
/* =================================================================
 * 작성일 : 2017.08.28
 * 작성자 : 이동현
 * 상세설명 : OPENSTACK NETWORK 관리 화면
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
var securityGroupCount = 0;
var save_lock_msg = '<spring:message code="common.save.data.lock"/>';//등록 중 입니다.
var search_lock_msg = '<spring:message code="common.search.data.lock"/>';//조회 중 입니다.
var text_required_msg='<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
var select_required_msg='<spring:message code="common.select.vaildate.required.message"/>';//을(를) 선택하세요.
var delete_lock_msg='<spring:message code="common.delete.data.lock"/>';//삭제 중 입니다.
var text_cidr_msg='<spring:message code="common.text.validate.cidr.message"/>';//CIDR 대역을 확인 하세요.
$(function() {
    
    bDefaultAccount = setDefaultIaasAccountList("openstack");
    
    $('#openstack_securityGroupGrid').w2grid({
        name: 'openstack_securityGroupGrid',
        method: 'GET',
        msgAJAXerror : 'OPENSTACK 계정을 확인해주세요.',
        header: '<b>OPENSTACK Security Group 목록</b>',
        multiSelect: false,
        show: {    
                selectColumn: true,
                footer: true},
        style: 'text-align: center',
        columns    : [
                     {field: 'recid',     caption: 'recid', hidden: true}
                   , {field: 'accountId',     caption: 'accountId', hidden: true}
                   , {field: 'securityGroupId', caption: 'SecurityGroup Id', size: '50%', style: 'text-align:center', hidden: true}
                   , {field: 'securityGroupName', caption: 'SecurityGroup Name', size: '50%', style: 'text-align:center'}
                   , {field: 'description', caption: 'Description', size: '50%', style: 'text-align:center'}
                   ],
        onSelect: function(event) {
            event.onComplete = function() {
                $('#deleteBtn').attr('disabled', false);
                var accountId =  w2ui.openstack_securityGroupGrid.get(event.recid).accountId;
                var securityGroupId = w2ui.openstack_securityGroupGrid.get(event.recid).securityGroupId;
                doSearchSecurityGroupDetailInfo(accountId, securityGroupId);
            }
        },
        onUnselect: function(event) {
            event.onComplete = function() {
                $("#ingressRulesTable .ingressRulesData").html("");
                $('#deleteBtn').attr('disabled', true);
            }
        },
        onLoad:function(event){
            var jsondata = JSON.parse(event.xhr.responseText);
            if(jsondata.records != null)
            securityGroupCount = jsondata.records.length;
            if(event.xhr.status == 403){
                location.href = "/abuse";
                event.preventDefault();
            }
        }, onError:function(evnet){
        }
    });
    /*************************** *****************************
     * 설명 :  OPENSTACK Security Group 생성 팝업 화면
     *********************************************************/
    $("#addBtn").click(function(){
        if($("#addBtn").attr('disabled') == "disabled") return;
        w2popup.open({
            title   : "<b>OPENSTACK Security Group 생성</b>",
            width   : 650,
            height  : 285,
            modal   : true,
            body    : $("#SecurityGroupRegistPopupDiv").html(),
            buttons : $("#SecurityGroupRegistPopupBtnDiv").html(),
            onClose:function(event){
                accountId = $("select[name='accountId']").val();
                w2ui['openstack_securityGroupGrid'].clear();
                $("#ingressRulesTable .ingressRulesData").html("");
                doSearch();
            }
        });
    });
    
    
    /********************************************************
     * 설명 : OPENSTACK Security Group 삭제 버튼 클릭
    *********************************************************/
   $("#deleteBtn").click(function(){
       if($("#deleteBtn").attr('disabled') == "disabled") return;
       var selected = w2ui['openstack_securityGroupGrid'].getSelection();
       if( selected.length == 0 ){
           w2alert("선택된 정보가 없습니다.", "Security Group 삭제");
           return;
       }
       else {
           var record = w2ui['openstack_securityGroupGrid'].get(selected);
           w2confirm({
               title : "<b>Security Group 삭제</b>",
               msg : "Security Group </br> (보안 그룹 Id :" + record.securityGroupId + ") </br> 을 삭제하시겠습니까?",
               yes_text : "확인",
               no_text : "취소",
               yes_callBack: function(event){
                   w2popup.lock($("#layout_layout_panel_main"),delete_lock_msg, true);
                   deleteOpenstackSecurityGroupInfo(record);
               },
               no_callBack    : function(){
                   $("#ingressRulesTable .ingressRulesData").html("");
                   w2ui['openstack_securityGroupGrid'].clear();
                   accountId = record.accountId;
                   doSearch();
               }
           });
       }
   });
});

/********************************************************
 * 설명 : OPENSTACK SecurityGroup 생성
 * 기능 : saveOpenstackSecurityGroupInfo
 *********************************************************/ 
function saveOpenstackSecurityGroupInfo(){
     w2popup.lock(save_lock_msg, true);
     var ingressRules = setIngressRulesInfo($(".w2ui-msg-body input:radio[name='ingressRuleType']:checked").val());
     var groupInfo ={
             accountId : $("select[name='accountId']").val()
            ,securityGroupName: $(".w2ui-msg-body input[name='securityGroupName']").val()
            ,description : $(".w2ui-msg-body input[name='description']").val()
            ,ingressRuleType: $(".w2ui-msg-body input:radio[name='ingressRuleType']:checked").val()
            ,ingressRules : ingressRules
     }
     $.ajax({
         type: "POST",
         url : "/openstackMgnt/securityGroup/save",
         contentType: "application/json",
         async : true,
         data: JSON.stringify(groupInfo),
         success : function(status){
             w2popup.unlock();
             w2popup.close();
             accountId = groupInfo.accountId;
             w2ui['openstack_securityGroupGrid'].clear();
             $("#ingressRulesTable .ingressRulesData").html("");
             doSearch();
         }, error : function(request, status, error){
             w2popup.unlock();
             doSearch();
             w2ui['openstack_securityGroupGrid'].clear();
             $("#ingressRulesTable .ingressRulesData").html("");
             var errorResult = JSON.parse(request.responseText);
             w2alert(errorResult.message);
         }
     });
 }

/********************************************************
 * 설명 : Security Group Inbound Rule 유형에 따른 정보 설정 
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
 * 설명 : 보안 그룹 삭제
 * 기능 : deleteOpenstackSecurityGroupInfo
*********************************************************/
function deleteOpenstackSecurityGroupInfo(record){
     var groupInfo = {
             accountId : record.accountId,
             securityGroupId : record.securityGroupId,
     }
     $.ajax({
         type : "DELETE",
         url : "/openstackMgnt/securityGroup/delete",
         contentType : "application/json",
         async : true,
         data : JSON.stringify(groupInfo),
         success : function(status) {
             w2popup.unlock();
             w2popup.close();
             accountId = groupInfo.accountId;
             w2ui['openstack_securityGroupGrid'].clear();
             $("#ingressRulesTable .ingressRulesData").html("");
             doSearch();
         },error : function(request, status, error) {
             w2popup.unlock();
             w2ui['openstack_securityGroupGrid'].clear();
             $("#ingressRulesTable .ingressRulesData").html("");
             doSearch();
             var errorResult = JSON.parse(request.responseText);
             w2alert(errorResult.message);
         }
     });
 };

/********************************************************
 * 설명 : Openstack 네트워크 목록 조회 
 * 기능 : doSearch
 *********************************************************/
function doSearch() {
    w2ui['openstack_securityGroupGrid'].load('/openstackMgnt/securityGroup/list/'+accountId+'');
    doButtonStyle();
    accountId = "";
    securityGroupCount = 0;
}

/********************************************************
 * 설명 : 초기 버튼 스타일
 * 기능 : doButtonStyle
 *********************************************************/
function doButtonStyle() {
    $('#deleteBtn').attr('disabled', true);
}

/********************************************************
 * 설명 : OPENSTACK 보안 그룹 상세 조회
 * 기능 : doSearchSecurityGroupDetailInfo
 *********************************************************/
function doSearchSecurityGroupDetailInfo(accountId, groupId){
    w2utils.lock($("#layout_layout_panel_main"), search_lock_msg, true);
    $.ajax({
        type : "GET",
        url : "/openstackMgnt/securityGroup/ingress/list/"+accountId+"/"+groupId+"",
        contentType : "application/json",
        success : function(data, status) {
            w2utils.unlock($("#layout_layout_panel_main"));
            if( !checkEmpty(data) ){
                for( var i=0; i<data.length; i++ ){
                   var html = "";
                   var etherType = checkEmpty(data[i].etherType) ? "-" : data[i].etherType;
                   var IpProtocol = checkEmpty(data[i].IpProtocol) ? "-" : data[i].IpProtocol;
                   var portRange = checkEmpty(data[i].portRange) ? "-" : data[i].portRange;
                   var remote = checkEmpty(data[i].remote) ? "-" : data[i].remote;
                   var direction = checkEmpty(data[i].direction) ? "-" : data[i].direction;
                   html +="<tr class='ingressRulesData'>";
                   html +="<td class='rules'>"+direction     +"</td>";
                   html +="<td class='rules'>"+etherType+"</td>";
                   html +="<td class='rules'>"+IpProtocol   +"</td>";
                   html +="<td class='rules'>"+portRange  +"</td>";
                   html +="<td class='rules'>"+remote     +"</td>";
                   html +="</tr>";
                   if( i == 0 ){
                       $("#ingressRulesTable .ingressRulesData").remove()
                   }
                   $("#ingressRulesTable").append(html);
                }
            }else{
                var html = "";
                var style="style='text-align:left'";
                html +="<tr class='ingressRulesData'>";
                html +="<td "+style+">-</td><td "+style+">-</td><td "+style+">-</td><td "+style+">-</td><td "+style+">-</td>";
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
 * 기능 : clearMainPage
 * 설명 : 다른페이지 이동시 호출
*****************************************************/
function clearMainPage() {
    $().w2destroy('openstack_securityGroupGrid');
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
    <div class="page_site pdt20">인프라 관리 > OPENSTACK 관리 > <strong>OPENSTACK Security Group 관리 </strong></div>
    <div id="openstackMgnt" class="pdt20">
        <ul>
            <li>
                <label style="font-size: 14px">OPENSTACK 관리 화면</label> &nbsp;&nbsp;&nbsp; 
                <div class="dropdown" style="display:inline-block;">
                    <a href="#" class="dropdown-toggle iaas-dropdown" data-toggle="dropdown" aria-expanded="false">
                        &nbsp;&nbsp;SecurityGroup 관리<b class="caret"></b>
                    </a>
                    <ul class="dropdown-menu alert-dropdown">
                        <sec:authorize access="hasAuthority('OPENSTACK_NETWORK_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/openstackMgnt/network"/>', 'Openstack Network');">Network 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('OPENSTACK_ROUTER_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/openstackMgnt/router"/>', 'OPENSTACK ROUTER');">Router 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('OPENSTACK_KEYPAIRS_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/openstackMgnt/keypairs"/>', 'OPENSTACK KEYPAIR');">KeyPair 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('OPENSTACK_INTERFACE_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/openstackMgnt/interface"/>', 'OPENSTACK INTERFACE');">인터페이스 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('OPENSTACK_FLOATING_IP_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/openstackMgnt/floatingIp"/>', 'OPENSTACK FLOATING IP');">Floating IP 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('OPENSTACK_SECURITY_GROUP_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/openstackMgnt/securityGroup"/>', 'OPENSTACK Security Group');">Security Group 관리</a></li>
                        </sec:authorize>
                    </ul>
                </div>
            </li>
            <li>
                <label  style="font-size:14px">OPENSTACK 계정 명</label>
                &nbsp;&nbsp;&nbsp;
                <select name="accountId" id="setAccountList" class="select" style="width: 300px; font-size: 15px; height: 32px;" onchange="setAccountInfo(this.value, 'openstack')">
                </select>
                <span id="doSearch" onclick="setDefaultIaasAccount('noPopup','openstack');" class="btn btn-info" style="width:80px" >선택</span>
            </li>
        </ul>
    </div>
    
     <div class="pdt20">
        <div class="title fl">OPENSTACK Security Group 목록</div>
        <div class="fr"> 
            <sec:authorize access="hasAuthority('OPENSTACK_SECURITY_GROUP_CREATE')">
            <span id="addBtn" class="btn btn-primary" style="width:120px">생성</span>
            </sec:authorize>
            <sec:authorize access="hasAuthority('OPENSTACK_SECURITY_GROUP_DELETE')">
            <span id="deleteBtn" class="btn btn-danger" style="width:120px">삭제</span>
            </sec:authorize>
        </div>
    </div>
    <div id="openstack_securityGroupGrid" style="width:100%; height:475px"></div>
    
    <div class="pdt20">
        <div class="title fl">OPENSTACK 인바운드 규칙</div>
    </div>
    <!-- OPENSTACK Security Group Inbound Rules 목록 -->
    <div id="IngressRulesDiv" style="width: 100%;height: 200px;background-color: whitesmoke; overflow-y: auto;">
        <table id="ingressRulesTable" class="table table-condensed table-hover" style="table-layout: fixed;  border-collapse: collapse;">
            <thead style="position:relatvie;">
	            <tr style="">
	                <th style="width: 20%;"  class="trTitle"><span>Direction</span></th>
	                <th style="width: 20%;" class="trTitle">Ether Type</th>
	                <th style="width: 20%" class="trTitle">IP Protocol</th>
	                <th style="width: 20%" class="trTitle">Port Range</th>
	                <th style="width: 20%" class="trTitle">Remote</th>
	            </tr>
            </thead>
            <tbody style="overflow: auto; width: 100%;height:200px">
	            <tr class="ingressRulesData" style="position: relative;">
	               <td style="text-align:center" colspan="4">보안 그룹을 선택하세요.</td>
	            </tr>
            </tbody>
        </table>
    </div>
    
<!-- OPENSTACK Security Group 등록 팝업 Div-->
<div id="SecurityGroupRegistPopupDiv" hidden="true">
    <form id="openstackSecurityGroupForm" action="POST" style="padding:5px 0 5px 0;margin:0;">
        <div class="panel panel-info"> 
            <div class="panel-heading"><b>Security Group 정보</b></div>
            <div class="panel-body" style="padding:20px 10px; height:150px; overflow-y:auto;">
                <div class="w2ui-field">
                    <label style="width:36%;text-align: left; padding-left: 20px;">Security Group Name</label>
                    <div>
                        <input name="securityGroupName" type="text"  maxlength="100" style="width: 300px" placeholder="보안 그룹 명"/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:36%;text-align: left; padding-left: 20px;">Description</label>
                    <div>
                        <input name="description" type="text"  maxlength="100" style="width: 300px" placeholder="설명"/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width: 36%; text-align: left; padding-left: 20px;">Ingress Rule</label>
                    <div>
                        <label><input type="radio" name="ingressRuleType" id="none" value="none"  checked="checked" />없음</label>&nbsp;&nbsp;&nbsp;
                        <label><input type="radio" name="ingressRuleType" id="boshSecurity" value="boshSecurity" />bosh-security</label>&nbsp;&nbsp;&nbsp;
                        <label><input type="radio" name="ingressRuleType" id="cfSecurity" value="cfSecurity" />cf-security</label>
                    </div>
                    <div  style="display:none">
                        <input type="text" name="ssh" value="22" class="bosh_security_rules">
                        <input type="text" name="boshAgent" value="6868" class="bosh_security_rules">
                        <input type="text" name="boshDirector" value="25555" class="bosh_security_rules">
                        <input type="text" name="allTcp" value="1-65535" class="bosh_security_rules">
                        <input type="text" name="allUdp" value="1-65535" class="bosh_security_rules">
                    </div>
                    <div  style="display:none">
                        <input type="text" name="http" value="80" class="cf_security_rules">
                        <input type="text" name="https" value="443" class="cf_security_rules">
                        <input type="text" name="cfLogs" value="4443" class="cf_security_rules">
                        <input type="text" name="allTcp" value="1-65535" class="cf_security_rules">
                        <input type="text" name="allUdp" value="1-65535" class="cf_security_rules">
                    </div>
                </div>
            </div>
        </div>
    </form> 
</div>
<div id="SecurityGroupRegistPopupBtnDiv" hidden="true">
     <button class="btn" id="registBtn" onclick="$('#openstackSecurityGroupForm').submit();">확인</button>
     <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
</div>
</div>

<script>
$(function() {
    $("#openstackSecurityGroupForm").validate({
        ignore : "",
        onfocusout: true,
        rules: {
            securityGroupName: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='securityGroupName']").val().trim() );
                }
            },
            description: {
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='description']").val().trim() );
                }
            }
        }, messages: {
            securityGroupName: { 
                required:  "Security Group Name"+text_required_msg
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
            //w2popup.lock(save_lock_msg, true);
            if(securityGroupCount > 9){
            	w2alert("OPENSTACK Security Group 최대 할당량은 10개 입니다.", "OPENSTACK Security Group 생성");
            	return;
            }
            saveOpenstackSecurityGroupInfo();
        }
    });
});
</script>


