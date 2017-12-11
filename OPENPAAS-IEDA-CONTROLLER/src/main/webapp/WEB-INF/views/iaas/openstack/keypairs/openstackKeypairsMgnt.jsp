<%
/* =================================================================
 * 작성일 : 2017.09.01
 * 작성자 : 이정윤
 * 상세설명 : OPENSTACK KEYPAIRS 관리 화면
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
var input_duplication_msg='<spring:message code="common.data.duplication.fail.message"/>';//중복된 데이터 입니다.


$(function(){
    bDefaultAccount = setDefaultIaasAccountList("openstack");
    $('#openstack_keypairsGrid').w2grid({
        name: 'openstack_keypairsGrid',
        method: 'GET',
        msgAJAXerror : 'OPENSTACK 계정을 확인해주세요.',
        header: '<b>Keypairs 목록</b>',
        multiSelect: false,
        show: {    
                selectColumn: false,
                footer: true},
        style: 'text-align: center',
        columns    : [
                     {field: 'keypairsName', caption: 'Keypairs Name', size: '40%', style: 'text-align:center'}
                   , {field: 'fingerprint', caption: 'Fingerprint', size: '40%', style: 'text-align:left'}
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
     * 설명 : Keypair 생성 클릭 > 생성 화면 팝업
    *********************************************************/
    $("#createBtn").click(function(){
       w2popup.open({
           title   : "<b>OPENSTACK Keypairs 생성 </b>",
           width   : 700,
           height  : 220,
           modal   : true,
           body    : $("#createPopupDiv").html(),
           buttons : $("#createPopupBtnDiv").html(),
           onOpen : function(event){
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
 * 설명 : Openstack Keypairs 생성 버튼 클릭
 * 기능 : saveKeypairsInfo
 *********************************************************/ 
function saveKeypairsInfo(){
     var accountId = $("select[name='accountId']").val()
     var keypairsName = $(".w2ui-msg-body input[name='keypairsName']").val()
     var records = w2ui['openstack_keypairsGrid'].records;
     for( var i=0; i<records.length; i++ ){
         if( keypairsName == records[i].keypairsName ){
             w2alert("Key Pair 명 (" + keypairsName +")은 " + input_duplication_msg);
             return;
         }
     }
     var debugLogdownUrl = "/openstackMgnt/keypairs/save/"+ accountId +"/"+keypairsName+"";
     window.open(debugLogdownUrl, '', ''); 
     w2popup.close();
 }


/********************************************************
 * 설명 : Openstack Keypairs 목록 조회 Function 
 * 기능 : doSearch
 *********************************************************/
function doSearch() {
    w2ui['openstack_keypairsGrid'].load('/openstackMgnt/keypairs/list/'+accountId+'');
}

/********************************************************
 * 기능 : initsetting
 * 설명 : 기본 설정값 초기화
 *********************************************************/
function initsetting(){
    bDefaultAccount="";
    w2ui['openstack_keypairsGrid'].clear();
    doSearch();
 }


/****************************************************
 * 기능 : clearMainPage
 * 설명 : 다른페이지 이동시 호출
*****************************************************/
function clearMainPage() {
    $().w2destroy('openstack_keypairsGrid');
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
    <div class="page_site pdt20">인프라 관리 > OPENSTACK 관리 > <strong>OPENSTACK Key Pair 관리 </strong></div>
    <div id="openstackMgnt" class="pdt20">
        <ul>
            <li>
                <label style="font-size: 14px">OPENSTACK 관리 화면</label> &nbsp;&nbsp;&nbsp; 
                <div class="dropdown" style="display:inline-block;">
                    <a href="#" class="dropdown-toggle iaas-dropdown" data-toggle="dropdown" aria-expanded="false">
                        &nbsp;&nbsp;Keypairs 관리<b class="caret"></b>
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
        <div class="title fl">Openstack KeyPair 목록</div>
        <div class="fr"> 
            <sec:authorize access="hasAuthority('OPENSTACK_KEYPAIRS_CREATE')">
            <span id="createBtn" class="btn btn-primary" style="width:120px">생성</span>
            </sec:authorize>
        </div>
    </div>
    <div id="openstack_keypairsGrid" style="width:100%; height:475px"></div>

    <!-- Keypairs 생성 팝업 -->
    <div id="createPopupDiv" hidden="true">
        <form id="openstackKeypairsForm" action="POST">
            <div class="panel panel-info" style="height: 120px; margin-top: 7px;"> 
                <div class="panel-heading"><b>Openstack Keypiar 생성</b></div>
                <div class="panel-body" style="padding:20px 10px; overflow-y:auto;">
                    <input type="hidden" name="accountId"/>
                    <div class="w2ui-field">
                        <label style="width:22%;text-align: left; padding-left: 20px;">Key Pair Name</label>
                        <div>
                            <input style="width: 320px;" name="keypairsName" placeholder="예) bosh-key">
                        </div>
                    </div>
                    
                </div>
            </div>
        </form> 
    </div>
    <div id="createPopupBtnDiv" hidden="true">
         <button class="btn" id="registBtn" onclick="$('#openstackKeypairsForm').submit();">확인</button>
         <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
    </div>

</div>

<script>
$(function() {
    $("#openstackKeypairsForm").validate({
        ignore : "",
        onfocusout: true,
        rules: {
            keypairsName: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='keypairsName']").val() );
                }
            }
        }, messages: {
            keypairsName: { 
                required:  "Keypair 이름"+text_required_msg
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
            saveKeypairsInfo();
        }
    });
});

</script>