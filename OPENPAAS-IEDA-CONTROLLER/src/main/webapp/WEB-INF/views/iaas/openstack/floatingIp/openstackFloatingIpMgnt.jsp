<%
/* =================================================================
 * 작성일 : 2017.08.28
 * 작성자 : 이정윤
 * 상세설명 : OPENSTACK ELASTIC IP 관리 화면
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
var save_lock_msg = '<spring:message code="common.save.data.lock"/>';//등록 중 입니다.
var text_required_msg='<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
$(function(){
    bDefaultAccount = setDefaultIaasAccountList("openstack");
    $('#openstack_floatingIpGrid').w2grid({
        name: 'openstack_floatingIpGrid',
        method: 'GET',
        msgAJAXerror : 'OPENSTACK 계정을 확인해주세요.',
        header: '<b>Network 목록</b>',
        multiSelect: false,
        show: {    
                selectColumn: false,
                footer: true},
        style: 'text-align: center',
        columns    : [
                     {field: 'ipAddress', caption: 'IP Address', size: '30%', style: 'text-align:center'}
                   , {field: 'instanceName', caption: 'Instance', size: '30%', style: 'text-align:left'}
                   , {field: 'pool', caption: 'Floating IP Pool', size: '30%', style: 'text-align:left'}
                   ],
           onLoad:function(event){
            if(event.xhr.status == 403){
                location.href = "/abuse";
                event.preventDefault();
            }
        }, onError:function(event){
        }
    });
    
    /********************************************************
     * 설명 : Floating IP 할당 버튼 클릭 > 할당 화면 팝업
    *********************************************************/
    $("#allocateBtn").click(function(){
        
       w2popup.open({
           title   : "<b>OPENSTACK Floating IP 할당 </b>",
           width   : 700,
           height  : 220,
           modal   : true,
           body    : $("#allocatePopupDiv").html(),
           buttons : $("#allocatePopupBtnDiv").html(),
           onOpen  : function(event){
               event.onComplete = function(){
                   getPools();
               }
           },
           onClose : function(event){
               event.onComplete = function(){
                   initsetting();//기본 설정값 초기화
            }
           }
       });
    });
});

/********************************************************
 * 설명 : Openstack Floating IP 할 버튼 클릭
 * 기능 : saveFloatingIpInfo
 *********************************************************/ 
function saveFloatingIpInfo(){
     w2utils.lock($("#layout_layout_panel_main"),save_lock_msg, true);
     var floatingIpInfo ={
             accountId : $("select[name='accountId']").val()
            ,pool: $(".w2ui-msg-body select[name='pools']").val()
     }
     $.ajax({
         type: "POST",
         url : "/openstackMgnt/floatingIp/save",
         contentType: "application/json",
         async : true,
         data: JSON.stringify(floatingIpInfo),
         success : function(status){
             w2popup.unlock();
             w2utils.unlock($("#layout_layout_panel_main"));
             w2popup.close();
             //accountId = floatingIpInfo.accountId;
             doSearch();
         }, error : function(request, status, error){
             w2popup.unlock();
             w2utils.unlock($("#layout_layout_panel_main"));
             var errorResult = JSON.parse(request.responseText).message;
             var idx = errorResult.indexOf("(");
             var message = errorResult.substring(0, idx);
             w2alert(message);
         }
     });
 }
/****************************************************
 * 기능 : getPools
 * 설명 : Floating IP 할당시 Pools 조회 요청
 *****************************************************/
function getPools() {
    w2popup.lock("Pool "+search_lock_msg, true);
    var accountId = $("select[name=accountId]").val();
    $.ajax({
        type : "GET",
        url : "/openstackMgnt/floatingIp/save/pool/list/"+accountId,
        contentType : "application/json",
        success : function(data, status) {
            setupPools(data);
        },
        error : function(request, status, error) {
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "Pool 목록 조회");
        }
    });
}

/****************************************************
 * 기능 : setupPools
 * 설명 : Floating IP에 대한 Pool설정 
*****************************************************/
function setupPools(data){
     var options= "";
     if( data.length == 0 ){
         options +="<option value=''>존재하지 않습니다.</option>";
     }else{
         for (var i=0; i<data.length; i++){
             options +="<option value='"+data[i]+"'>" + data[i] + "</option>"; 
         } 
     }
     poolInfo=data;
     w2popup.unlock();
     $(".w2ui-msg-body select[name='pools']").html(options);
}

/********************************************************
 * 설명 : Openstack Floating IP 목록 조회 Function 
 * 기능 : doSearch
 *********************************************************/
function doSearch() {
    w2ui['openstack_floatingIpGrid'].load('/openstackMgnt/floatingIp/list/'+accountId+'');
}

/********************************************************
 * 기능 : initsetting
 * 설명 : 기본 설정값 초기화
 *********************************************************/
function initsetting(){
    bDefaultAccount="";
    poolInfo="";
    w2ui['openstack_floatingIpGrid'].clear();
    doSearch();
 }

/****************************************************
 * 기능 : clearMainPage
 * 설명 : 다른페이지 이동시 호출
*****************************************************/
function clearMainPage() {
    $().w2destroy('openstack_floatingIpGrid');
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
    <div class="page_site pdt20">인프라 관리 > OPENSTACK 관리 > <strong>OPENSTACK Floating IP 관리 </strong></div>
    <div id="openstackMgnt" class="pdt20">
        <ul>
            <li>
                <label style="font-size: 14px">OPENSTACK 관리 화면</label> &nbsp;&nbsp;&nbsp; 
                <div class="dropdown" style="display:inline-block;">
                    <a href="#" class="dropdown-toggle iaas-dropdown" data-toggle="dropdown" aria-expanded="false">
                        &nbsp;&nbsp;Floating IP 관리<b class="caret"></b>
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
                <select name="accountId" id="setAccountList" class="select" style="width:300px; font-size:15px; height: 32px;" onchange="setAccountInfo(this.value, 'openstack')">
                </select>
                <span id="doSearch" onclick="setDefaultIaasAccount('noPopup','openstack');" class="btn btn-info" style="width:80px" >선택</span>
            </li>
        </ul>
    </div>
    
    <div class="pdt20">
        <div class="title fl">Openstack Floating IP 목록</div>
        <div class="fr"> 
            <sec:authorize access="hasAuthority('OPENSTACK_FLOATING_IP_ALLOCATE')">
            <span id="allocateBtn" class="btn btn-primary" style="width:120px">할당</span>
            </sec:authorize>
        </div>
    </div>
    <div id="openstack_floatingIpGrid" style="width:100%; height:475px"></div>

    
    <!-- Floating IP 할당 팝업 -->
    <div id="allocatePopupDiv" hidden="true">
        <form id="openstackFloatingIpForm" action="POST">
            <div class="panel panel-info" style="height: 120px; margin-top: 7px;"> 
                <div class="panel-heading"><b>Floating IP 할당 정보</b></div>
                <div class="panel-body" style="padding:20px 10px; overflow-y:auto;">
                    <input type="hidden" name="accountId"/>
                    <div class="w2ui-field">
                        <label style="width:36%;text-align: left; padding-left: 20px;">Pool</label>
                        <div>
                            <select style="width: 320px;" name="pools">
                                <option value=""></option>
                            </select>
                        </div>
                    </div>
                    
                </div>
            </div>
        </form> 
    </div>
    <div id="allocatePopupBtnDiv" hidden="true">
         <button class="btn" id="registBtn" onclick="$('#openstackFloatingIpForm').submit();">확인</button>
         <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
    </div>
</div>

<script>
$(function() {
    $("#openstackFloatingIpForm").validate({
        ignore : "",
        onfocusout: true,
        rules: {
            pool: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body select[name='pools']").val() );
                }
            }
        }, messages: {
             pool: { 
                required:  "Pools"+text_required_msg
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
            w2popup.lock(save_lock_msg, true);
            saveFloatingIpInfo();
        }
    });
});

</script>