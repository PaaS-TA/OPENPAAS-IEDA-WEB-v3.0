<%
/* =================================================================
 * 작성일 : 2017.05.10
 * 작성자 : 이정윤
 * 상세설명 : 계정 관리 화면( vSphere 인프라 계정 조회)
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix = "spring" uri = "http://www.springframework.org/tags" %>

<script type="text/javascript">
var accountInfo = "";
var search_grid_fail_msg = '<spring:message code="common.grid.select.fail"/>';//목록 조회 중 오류가 발생했습니다.
var search_lock_msg = '<spring:message code="common.search.data.lock"/>'; //데이터 조회 중 입니다.
var save_lock_msg='<spring:message code="common.save.data.lock"/>'; //등록 중 입니다.
var delete_lock_msg='<spring:message code="common.delete.data.lock"/>'; //삭제 중 입니다.
var select_fail_msg='<spring:message code="common.grid.selected.fail"/>';//선택된 레코드가 존재하지 않습니다.
var popup_delete_msg='<spring:message code="common.popup.delete.message"/>';//삭제 하시겠습니까?
var text_required_msg='<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
var text_injection_msg='<spring:message code="common.text.validate.sqlInjection.message"/>';//입력하신 값은 입력하실 수 없습니다.

$(function() {
    $('#vSphere_accountGrid').w2grid({
        name: 'vSphere_accountGrid',
        style   : 'text-align:center',
        method  : 'GET',
        multiSelect: false,
        show: {
            selectColumn: true,
            footer: true},
        columns : [
                   {field: 'id', caption: 'recid', hidden: true}
                   , {field: 'status', caption: '환경 설정 정보 사용 여부', size: '10%', style: 'text-align:center'}
                   , {field: 'accountName', caption: '계정 별칭', size: '20%', style: 'text-align:left'}
                   , {field: 'commonAccessEndpoint', caption: 'vCenter Address', size: '20%', style: 'text-align:left'}
                   , {field: 'commonAccessUser', caption: 'vCenter User Id', size: '20%', style: 'text-align:left'}
                   , {field: 'createDate', caption: '계정 생성 일자', size: '10%', style: 'text-align:center'}
                   , {field: 'updateDate', caption: '계정 수정 일자', size: '10%', style: 'text-align:center'}
        ],onError: function(event) {
            w2alert(search_grid_fail_msg, "vSPhere 계정 목록");
        },onLoad : function(event){
            $("#rsaPublicKeyModulus").val(  JSON.parse(event.xhr.responseText).publicKeyModulus );
            $("#rsaPublicKeyExponent").val(  JSON.parse(event.xhr.responseText).publicKeyExponent );
        },onSelect : function(event) {
            event.onComplete = function(){
                $("#updateAccountBtn").attr('disabled', false);
                $("#deleteAccountBtn").attr('disabled', false);
            }
        },onUnselect : function(event) {
            event.onComplete = function(){
                $("#updateAccountBtn").attr('disabled', true);
                $("#deleteAccountBtn").attr('disabled', true);
            }
        }
    });
    
    doSearch(); 

/*************************** *****************************
 * 설명 :  vSphere 등록 팝업 화면
 *********************************************************/
$("#registAccountBtn").click(function(){
    w2popup.open({
        title   : "<b>vSphere 계정 등록</b>",
        width   : 650,
        height  : 315,
        modal   : true,
        body    : $("#registPopupDiv").html(),
        buttons : $("#registPopupBtnDiv").html(),
        onOpen  : function(event){
            event.onComplete = function(){
                //popup size 값 설정
            }
        },onClose:function(event){
            w2ui['vSphere_accountGrid'].reset();
            initsetting();
            doSearch();
        }
    });
});

/*************************** *****************************
 * 설명 :  vSphere 수정 팝업 화면
 *********************************************************/
$("#updateAccountBtn").click(function(){
    if( $("#updateAccountBtn").attr("disabled") == "disabled" ) return;
    w2popup.open({
        title   : "<b>vSphere 계정 수정</b>",
        width   : 650,
        height  : 315,
        modal   : true,
        body    : $("#registPopupDiv").html(),
        buttons : $("#registPopupBtnDiv").html(),
        onOpen  : function(event){
            event.onComplete = function(){
                //input readonly 설정
                $(".w2ui-msg-body input[name='accountName']").attr("readonly", true);
                $(".w2ui-msg-body input[name='commonAccessEndpoint']").attr("readonly", true);
                $(".w2ui-msg-body input[name='commonAccessUser']").attr("readonly", true);
                
                //grid record
                var selected = w2ui['vSphere_accountGrid'].getSelection();
                if( selected.length == 0 ){
                    w2alert(select_fail_msg, "vSphere 계정 수정");
                    return;
                }
                var record = w2ui['vSphere_accountGrid'].get(selected);
                setVsphereAccountInfo(record.id);
            }
        },onClose:function(event){
            w2ui['vSphere_accountGrid'].reset();
            initsetting();
            doSearch();
        }
    });
});

/*************************** *****************************
 * 설명 : vSphere 삭제 팝업 화면
 *********************************************************/
$("#deleteAccountBtn").click(function(){
    if( $("#deleteAccountBtn").attr("disabled") == "disabled" ) return;
     //grid record
    var selected = w2ui['vSphere_accountGrid'].getSelection();
    if( selected.length == 0 ){
        w2alert(select_fail_msg, "MS Azure 계정 삭제");
        return;
    }
    var record = w2ui['vSphere_accountGrid'].get(selected);
    var msg = "계정(" + record.accountName + ")"+ popup_delete_msg;
    if( record.status == '사용중' ){
        msg = "<span style='color:red'>현재 vSphere 환경 설정 정보 화면에서 <br/>해당 계정("+record.accountName+")을 사용하고 있습니다. </span><br/><span style='color:red; font-weight:bolder'>그래도 삭제 하시겠습니까?</span>";
    }
    
    w2confirm({
        title    : "<b>vSphere 계정 정보 삭제</b>",
        msg      : msg,
        yes_text : "확인",
        no_text  : "취소",
        yes_callBack: function(event){
            //delete function 호출
            deleteVsphereAccountInfo(record);
            w2ui['vSphere_accountGrid'].reset();
        },
        no_callBack: function(event){
            initsetting();
            w2ui['vSphere_accountGrid'].reset();
            doSearch();
        }
    });
  });
});

/********************************************************
 * 기능 : doSearch
 * 설명 : 조회기능
 *********************************************************/
function doSearch() {
    $("#updateAccountBtn").attr('disabled', true);
    $("#deleteAccountBtn").attr('disabled', true);
    w2ui['vSphere_accountGrid'].load("<c:url value='/iaasMgnt/account/vSphere/list'/>","",function(event){}); 
}

/********************************************************
 * 기능 : setVsphereAccountInfo
 * 설명 : vSphere계정 상세 정보 조회 후 데이터 설정
 *********************************************************/
function setVsphereAccountInfo( id ){
     w2popup.lock( search_lock_msg, true);
     $.ajax({
         type : "GET",
         url : "/iaasMgnt/account/vSphere/save/detail/"+id,
         contentType : "application/json",
         dataType : "json",
         async : true,
         success : function(data, status) {
             $(".w2ui-msg-body input[name='accountId']").val(data.id);
             $(".w2ui-msg-body input[name='accountName']").val(data.accountName);
             $(".w2ui-msg-body input[name='commonAccessEndpoint']").val(data.commonAccessEndpoint);
             $(".w2ui-msg-body input[name='commonAccessUser']").val(data.commonAccessUser);
             $(".w2ui-msg-body input[name='commonAccessSecret']").val(data.commonAccessSecret);
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
 * 기능 : deleteVsphereAccountInfo
 * 설명 : vSphere 계정 정보 삭제
 *********************************************************/
function deleteVsphereAccountInfo(record){ 
    if( $("#deleteAccountBtn").attr("disabled") == "disabled" ) return;
    accountInfo ={
           id : record.id,
           iaasType : record.iaasType,
           accountName : record.accountName
    }
    
    w2popup.lock( delete_lock_msg, true);
    
     $.ajax({
         type : "DELETE",
         url : "/iaasMgnt/account/vSphere/delete",
         contentType : "application/json",
         dataType: "json",
         async : true,
         data : JSON.stringify(accountInfo),
         success : function(status) {
             w2popup.unlock();
             w2popup.close();
             initsetting();
             doSearch(); 
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
    accountInfo="";
}

/********************************************************
 * 기능 : clearMainPage
 * 설명 : 다른페이지 이동시 호출
 *********************************************************/
function clearMainPage() {
    $().w2destroy('vSphere_accountGrid');
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
    <div class="page_site">계정 관리 > <strong>vSphere 계정 관리 </strong></div>
     <div class="pdt20">
        <div class="fl" style="width:100%">
            <div class="dropdown">
                <a href="#" class="dropdown-toggle iaas-dropdown" data-toggle="dropdown" aria-expanded="false">
                    <i class="fa fa-cloud"></i>&nbsp;vSphere<b class="caret"></b>
                </a>
                <ul class="dropdown-menu alert-dropdown">
                    <sec:authorize access="hasAuthority('IAAS_ACCOUNT_AWS_MENU')">
                         <li><a href="javascript:goPage('<c:url value="/iaasMgnt/account/aws"/>', 'AWS 관리');">AWS</a></li>
                     </sec:authorize>
                     <sec:authorize access="hasAuthority('IAAS_ACCOUNT_OPENSTACK_MENU')">
                        <li><a href="javascript:goPage('<c:url value="/iaasMgnt/account/openstack"/>', 'Openstack 관리');">Openstack</a></li>
                     </sec:authorize>
                     <sec:authorize access="hasAuthority('IAAS_ACCOUNT_GOOGLE_MENU')">
                        <li><a href="javascript:goPage('<c:url value="/iaasMgnt/account/google"/>', 'Google 관리');">Google</a></li>
                     </sec:authorize>
                    <sec:authorize access="hasAuthority('IAAS_ACCOUNT_AZURE_MENU')">
                        <li><a href="javascript:goPage('<c:url value="/iaasMgnt/account/azure"/>', 'vSphere 관리');">Azure</a></li>
                    </sec:authorize>
                </ul>
            </div>
        </div> 
    </div>
    <div class="pdt20">
        <div class="title fl">vSphere 계정 목록</div>
        
        <div class="fr"> 
            <!-- Button -->
            <sec:authorize access="hasAuthority('IAAS_ACCOUNT_VSPHERE_CREATE')">
               <span id="registAccountBtn" class="btn btn-primary" style="width:100px" >등록</span>
            </sec:authorize>
            <sec:authorize access="hasAuthority('IAAS_ACCOUNT_VSPHERE_UPDATE')">
               <span id="updateAccountBtn" class="btn btn-info" style="width:100px" >수정</span>
            </sec:authorize>
            <sec:authorize access="hasAuthority('IAAS_ACCOUNT_VSPHERE_DELETE')">
               <span id="deleteAccountBtn" class="btn btn-danger" style="width:100px" >삭제</span>
            </sec:authorize>
            <!-- //Btn -->
        </div>
        <div id="vSphere_accountGrid" style="width:100%; height:610px"></div>
    </div>
</div>
<!-- vSphere 등록 및 수정 팝업 Div-->
<div id="registPopupDiv" hidden="true">
    <form id="vSphereAccountForm" action="PUT" style="padding:5px 0 5px 0;margin:0;">
        <div class="panel panel-info" > 
            <div class="panel-heading"><b>vSphere 계정 정보</b></div>
            <div class="panel-body" style="padding:20px 5% 10px 5%; height:180px; overflow-y:auto;">
                <input type="hidden" name="accountId" />
                <input type="hidden" id="rsaPublicKeyModulus" />
                <input type="hidden" id="rsaPublicKeyExponent" />
                <div class="w2ui-field">
                    <label style="width:35%;text-align: left; padding-left: 20px;">계정 별칭</label>
                    <div>
                        <input name="accountName" type="text"  maxlength="100" style="width: 300px" placeholder="계정 별칭을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:35%;text-align: left; padding-left: 20px;">vCenter Address</label>
                    <div>
                        <input name="commonAccessEndpoint" type="text"  maxlength="100" style="width: 300px" placeholder="vCenter IP 또는 주소를 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:35%;text-align: left; padding-left: 20px;">vCenter User Id</label>
                    <div>
                        <input name="commonAccessUser" type="text"  maxlength="100" style="width: 300px" placeholder="vCenter 로그인 아이디 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:35%;text-align: left; padding-left: 20px;">vCenter Password</label>
                    <div>
                        <input name="commonAccessSecret" type="password"  maxlength="100" style="width: 300px" placeholder="vCenter 로그인 비밀번호를 입력하세요."/>
                    </div>
                </div>
            </div>
        </div>
    </form> 
</div>
<div id="registPopupBtnDiv" hidden="true">
     <button class="btn" id="registBtn" onclick="$('#vSphereAccountForm').submit();">확인</button>
     <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
</div>

<script>
$(function() {
    $.validator.addMethod("sqlInjection", function(value, element, params) {
        return checkInjectionBlacklist(params);
      },text_injection_msg);
    
    $("#vSphereAccountForm").validate({
        ignore : "",
        success: "valid",
        onfocusout: true,
        rules: {
            accountName : { 
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='accountName']").val().trim() );
                }, sqlInjection : function(){
                    return $(".w2ui-msg-body input[name='accountName']").val();
                }
            }, commonAccessEndpoint: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='commonAccessEndpoint']").val().trim() );
                }, sqlInjection : function(){
                    return $(".w2ui-msg-body input[name='commonAccessEndpoint']").val().trim();
                }
            }, commonAccessUser: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='commonAccessUser']").val().trim() );
                }, sqlInjection : function(){
                    return $(".w2ui-msg-body input[name='commonAccessUser']").val().trim();
                }
            }, commonAccessSecret: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='commonAccessSecret']").val().trim() );
                }
            }
        }, messages: {
            accountName: { 
                 required:  "계정 별칭"+ text_required_msg
            } , commonAccessEndpoint: { 
                required:  "vCenter Address"+ text_required_msg
            } , commonAccessUser: { 
                required:  "vCenter User Id"+ text_required_msg
            }, commonAccessSecret: { 
                required: "vCenter Password"+ text_required_msg
            }
        }, unhighlight: function(element) {
            setSuccessStyle(element);
        },errorPlacement: function(error, element) {
            //do nothing
        },invalidHandler : function(event, validator) {
            var errors = validator.numberOfInvalids();
            if (errors) {
                setInvalidHandlerStyle(errors, validator);
            }
        },submitHandler: function (form) {
            w2popup.lock( save_lock_msg, true);
            try {
                var rsa = new RSAKey();
                rsa.setPublic($("#rsaPublicKeyModulus").val(), $("#rsaPublicKeyExponent").val() );
            } catch(err) {
                w2alert(err);
            }
            //vSphere계정 정보 등록
            accountInfo = {
                    iaasType : "vSphere"
                    ,id : $(".w2ui-msg-body input[name='accountId']").val()
                    ,accountName : $(".w2ui-msg-body input[name='accountName']").val()
                    ,commonAccessEndpoint : $(".w2ui-msg-body input[name='commonAccessEndpoint']").val()
                    ,commonAccessUser : rsa.encrypt( $(".w2ui-msg-body input[name='commonAccessUser']").val() )
                    ,commonAccessSecret : rsa.encrypt( $(".w2ui-msg-body input[name='commonAccessSecret']").val() )
            }
            
            w2popup.lock( save_lock_msg, true);
            $.ajax({
                type : "PUT",
                url : "/iaasMgnt/account/vSphere/save",
                contentType : "application/json",
                async : true,
                data : JSON.stringify(accountInfo),
                success : function(status) {
                    w2popup.unlock();
                    w2popup.close();    
                    initsetting();
                    w2ui['vsphere_accountGrid'].reset();
                },
                error : function(request, status, error) {
                    w2popup.unlock();
                    var errorResult = JSON.parse(request.responseText);
                    w2alert(errorResult.message);
                }
            });
        }
    });
});
</script>