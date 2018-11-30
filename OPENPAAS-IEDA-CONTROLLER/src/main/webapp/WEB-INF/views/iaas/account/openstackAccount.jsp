<%
/* =================================================================
 * 작성일 : 2017.05.08
 * 작성자 : Ji,Hyangeun
 * 상세설명 : Openstack 계정 관리 화면(Openstack 계정 정보 조회/등록/수정/삭제 등)
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix = "spring" uri = "http://www.springframework.org/tags" %>
<script type="text/javascript">
var popup_height= 0;
var body_height= 0;
var accountInfo="";
var search_grid_fail_msg = '<spring:message code="common.grid.select.fail"/>';//목록 조회 중 오류가 발생했습니다.
var search_lock_msg = '<spring:message code="common.search.data.lock"/>';//데이터 조회 중 입니다.
var save_lock_msg='<spring:message code="common.save.data.lock"/>';//등록 중 입니다.
var delete_lock_msg='<spring:message code="common.delete.data.lock"/>';//삭제 중 입니다.
var select_fail_msg='<spring:message code="common.grid.selected.fail"/>'//선택된 레코드가 존재하지 않습니다.
var popup_delete_msg='<spring:message code="common.popup.delete.message"/>';//삭제 하시겠습니까?
var text_required_msg='<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
var text_injection_msg='<spring:message code="common.text.validate.sqlInjection.message"/>';//입력하신 값은 입력하실 수 없습니다.

$(function() {
    $('#openstack_accountGrid').w2grid({
        name: 'openstack_accountGrid',
        style   : 'text-align:center',
        method  : 'GET',
        multiSelect: false,
        show: { 
            selectColumn: true,
            footer: true},
        columns : [
                     {field: 'id', caption: 'id', hidden: true}
                     , {field: 'status', caption: '환경 설정 정보 사용 여부', size: '150px', style: 'text-align:center'}
                     , {field: 'openstackKeystoneVersion', caption: 'Keystone Version', size: '120px', style: 'text-align:center'}
                     , {field: 'accountName', caption: '계정 별칭', size: '200px', style: 'text-align:left'}
                     , {field: 'commonAccessEndpoint', caption: 'Identify API Token URL', size: '250px', style: 'text-align:left'}
                     , {field: 'commonAccessUser', caption: 'user name', size: '200px', style: 'text-align:left'}
                     , {field: 'commonTenant', caption: 'Tenant', size: '200px', style: 'text-align:left'}
                     , {field: 'commonProject', caption: 'Project', size: '200px', style: 'text-align:left'}
                     , {field: 'openstackDomain', caption: 'Domain', size: '200px', style: 'text-align:left'}
                     , {field: 'createDate', caption: '생성 일자', size: '120px', style: 'text-align:center'}
                     , {field: 'updateDate', caption: '수정 일자', size: '120px', style: 'text-align:center'}
        ],onError: function(event) {
             w2alert(search_grid_fail_msg, "Openstack 계정 목록");
        },onLoad : function(event){
            $("#rsaPublicKeyModulus").val(  JSON.parse(event.xhr.responseText).publicKeyModulus );
            $("#rsaPublicKeyExponent").val(  JSON.parse(event.xhr.responseText).publicKeyExponent );
        }, onSelect : function(event) {
            event.onComplete = function(){
                $("#updateAccountBtn").attr('disabled', false);
                $("#deleteAccountBtn").attr('disabled', false);
            }
        }, onUnselect : function(event) {
            event.onComplete = function(){
                $("#updateAccountBtn").attr('disabled', true);
                $("#deleteAccountBtn").attr('disabled', true);
            }
        }
    });
    
    doSearch(); 
    
    /*************************** *****************************
    * 설명 :  Openstack 등록 팝업 화면
    *********************************************************/
   $("#registAccountBtn").click(function(){
       w2popup.open({
           title   : "<b>Openstack 계정 등록</b>",
           width   : 650,
           height  : 411,
           modal   : true,
           body    : $("#registPopupDiv").html(),
           buttons : $("#registPopupBtnDiv").html(),
           onOpen : function(event){
               event.onComplete = function(){
                 //popup size 값 설정
                   popup_height  = Number( $(".w2ui-popup").css("height").substring(-1, 3));
                   body_height = Number( $(".panel-body").css("height").substring(-1, 3)) ;
               }                   
           },onClose:function(event){
               w2ui['openstack_accountGrid'].reset();
               initsetting();
               doSearch();
           }
       });
   });
    
   /*************************** *****************************
    * 설명 :  openstack 수정 팝업 화면
    *********************************************************/
   $("#updateAccountBtn").click(function(){
       if( $("#updateAccountBtn").attr("disabled") == "disabled" ) return;
       w2popup.open({
           title   : "<b>Openstack 계정 수정</b>",
           width   : 650,
           height  : 411,
           modal   : true,
           body    : $("#registPopupDiv").html(),
           buttons : $("#registPopupBtnDiv").html(),
           onOpen : function(event){
               event.onComplete = function(){
                   popup_height  = Number( $(".w2ui-popup").css("height").substring(-1, 3));
                   body_height = Number( $(".panel-body").css("height").substring(-1, 3)) ;
                   
                   //var version =  $(".w2ui-msg-body selected[name='openstackKeystoneVersion']").val();
                   
                   //input readonly 설정
                   $(".w2ui-msg-body input[name='accountName']").attr("readonly", true);
                   $(".w2ui-msg-body input[name='commonAccessEndpoint']").attr("readonly", true);
                   $(".w2ui-msg-body input[name='commonAccessUser']").attr("readonly", true);
                   $(".w2ui-msg-body select[name='openstackKeystoneVersion']").attr("disabled", true);
                   
                   //grid record
                   var selected = w2ui['openstack_accountGrid'].getSelection();
                   if( selected.length == 0 ){
                       w2alert(select_fail_msg, "openstack 계정 수정");
                       return;
                   }
                   var record = w2ui['openstack_accountGrid'].get(selected);
                   setOpenstackAccountInfo(record.id);
                   var version = record.openstackKeystoneVersion;
                   if(version == "v2"){
                       $(".w2ui-msg-body .commonTenantDiv").show();
                       $(".w2ui-msg-body .commonProjectDiv").hide();
                       $(".w2ui-msg-body .openstackDomainDiv").hide();
                   }else if((version == "v3")){
                       $(".w2ui-msg-body .commonTenantDiv").hide();
                       $(".w2ui-msg-body .commonProjectDiv").show();
                       $(".w2ui-msg-body .openstackDomainDiv").show();
                   }
                   
               }                   
           },onClose:function(event){
               w2ui['openstack_accountGrid'].reset();
               initsetting();
               doSearch();
           }
       });
   });
    
   $("#deleteAccountBtn").click(function(){
       if( $("#deleteAccountBtn").attr("disabled") == "disabled" ) return;
     //grid record
       var selected = w2ui['openstack_accountGrid'].getSelection();
       if( selected.length == 0 ){
           w2alert(select_fail_msg);
           w2ui['openstack_accountGrid'].unlock();
           w2ui['openstack_accountGrid'].reset();
           doSearch();
       }
       var record = w2ui['openstack_accountGrid'].get(selected);
       var msg = "계정(" + record.accountName + ")"+ popup_delete_msg;
       if( record.status == '사용중' ){
           msg = "<span style='color:red'>현재 Openstack 환경 설정 정보 화면에서 <br/>해당 계정("+record.accountName+")을 사용하고 있습니다. </span><br/><span style='color:red; font-weight:bolder'>그래도 삭제 하시겠습니까?</span>";
       }
       
       w2confirm({
           title        : "<b>Openstack 계정 정보 삭제</b>",
           msg          : msg,
           yes_text     : "확인",
           no_text      : "취소",
           yes_callBack : function(event){
               w2ui['openstack_accountGrid'].lock(delete_lock_msg, {
                   spinner: true, opacity : 1
               });
               //delete function 호출
               deleteOpenstackAccountInfo(record);
               w2ui['openstack_accountGrid'].reset();
           },
           no_callBack: function(event){
               w2ui['openstack_accountGrid'].unlock();
               w2ui['openstack_accountGrid'].reset();
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
     w2ui['openstack_accountGrid'].load("<c:url value='/iaasMgnt/account/openstack/list'/>","",function(event){}); 
}
   
/********************************************************
 * 기능 : setAccountInputByKeystoneVersion
 * 설명 : Openstack 키스톤 버전에 따른 Input 설정
 *********************************************************/
function setAccountInputByKeystoneVersion(event){
     if( event.value == 'v2' ){
         $(".w2ui-msg-body .commonTenantDiv").show();
         $(".w2ui-msg-body .commonProjectDiv").hide();
         $(".w2ui-msg-body .openstackDomainDiv").hide();
     }else if( event.value == 'v3' ){
         $(".w2ui-msg-body .commonTenantDiv").hide();
         $(".w2ui-msg-body .commonProjectDiv").show();
         $(".w2ui-msg-body .openstackDomainDiv").show();
     }else{
         w2alert('<spring:message code="common.validation.fail"/>', "Openstack 키스톤 버전");
     }
}

/********************************************************
 * 기능 : setOpenstackAccountInfo
 * 설명 : Openstack계정 상세 정보 조회 후 데이터 설정
 *********************************************************/
function setOpenstackAccountInfo( id ){
     w2popup.lock( search_lock_msg, true);
    $.ajax({
        type : "GET",
        url : "/iaasMgnt/account/openstack/save/detail/"+id,
        contentType : "application/json",
        dataType : "json",
        success : function(data, status) {
        	
        	var ver = data.openstackKeystoneVersion;
        	$(".w2ui-msg-body input[name='accountId']").val(data.id);
        	$(".w2ui-msg-body input[name='accountName']").val(data.accountName);
            $(".w2ui-msg-body select[name='openstackKeystoneVersion']").val(ver);
            $(".w2ui-msg-body input[name='commonAccessEndpoint']").val(data.commonAccessEndpoint);
            $(".w2ui-msg-body input[name='commonAccessUser']").val(data.commonAccessUser);
            $(".w2ui-msg-body input[name='commonAccessSecret']").val(data.commonAccessSecret);
            $(".w2ui-msg-body input[name='commonTenant']").val(data.commonTenant);
            $(".w2ui-msg-body input[name='openstackDomain']").val(data.openstackDomain);
            $(".w2ui-msg-body input[name='commonProject']").val(data.commonProject);
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
 * 기능 : deleteOpenstackAccountInfo
 * 설명 : Openstack 계정 정보 삭제
 *********************************************************/
function deleteOpenstackAccountInfo(record){
    accountInfo= {
           id :          record.id,
           iaasType :    record.iaasType,
           accountName : record.accountName
    }
    w2popup.lock(delete_lock_msg, true);
     $.ajax({
            type : "DELETE",
            url : "/iaasMgnt/account/openstack/delete",
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
     $(".w2ui-popup").css( "height" ,"411");
     $(".panel-body").css( "height" ,"276");
    accountInfo="";
}

/********************************************************
 * 기능 : clearMainPage
 * 설명 : 다른페이지 이동시 호출
 *********************************************************/
function clearMainPage() {
    $().w2destroy('openstack_accountGrid');
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
    <div class="page_site">계정 관리 > <strong>Openstack 계정 관리 </strong></div>
    <div class="pdt20">
        <div class="fl" style="width:100%">
            <div class="dropdown" >
                <a href="#" class="dropdown-toggle iaas-dropdown" data-toggle="dropdown" aria-expanded="false">
                    <i class="fa fa-cloud"></i>&nbsp;Openstack<b class="caret"></b>
                </a>
                <ul class="dropdown-menu alert-dropdown">
                    <sec:authorize access="hasAuthority('IAAS_ACCOUNT_AWS_MENU')">
                        <li><a href="javascript:goPage('<c:url value="/iaasMgnt/account/aws"/>', 'AWS 관리');">AWS</a></li>
                    </sec:authorize>
                    <sec:authorize access="hasAuthority('IAAS_ACCOUNT_GOOGLE_MENU')">
                        <li><a href="javascript:goPage('<c:url value="/iaasMgnt/account/google"/>', 'Google 관리');">Google</a></li>
                    </sec:authorize>
                    <sec:authorize access="hasAuthority('IAAS_ACCOUNT_VSPHERE_MENU')">
                        <li><a href="javascript:goPage('<c:url value="/iaasMgnt/account/vSphere"/>', 'vSphere 관리');">vSphere</a></li>
                    </sec:authorize>
                    <sec:authorize access="hasAuthority('IAAS_ACCOUNT_AZURE_MENU')">
                        <li><a href="javascript:goPage('<c:url value="/iaasMgnt/account/azure"/>', 'vSphere 관리');">Azure</a></li>
                    </sec:authorize>
                </ul>
            </div>
        </div> 
    </div>
    <div class="pdt20">
        <div class="title fl">Openstack 계정 목록</div>
        <div class="fr"> 
            <!-- Button -->
            <sec:authorize access="hasAuthority('IAAS_ACCOUNT_OPENSTACK_CREATE')">
               <span id="registAccountBtn" class="btn btn-primary" style="width:100px" >등록</span>
            </sec:authorize>
            <sec:authorize access="hasAuthority('IAAS_ACCOUNT_OPENSTACK_UPDATE')">
               <span id="updateAccountBtn" class="btn btn-info" style="width:100px" >수정</span>
            </sec:authorize>
            <sec:authorize access="hasAuthority('IAAS_ACCOUNT_OPENSTACK_DELETE')">
               <span id="deleteAccountBtn" class="btn btn-danger" style="width:100px" >삭제</span>
            </sec:authorize>
            <!-- //Btn -->
        </div>
    </div>
    <div id="openstack_accountGrid" style="width:100%; height:610px">
    </div>
 </div>
<!-- 오픈스택 등록 팝업 Div-->
<div id="registPopupDiv" hidden="true">
     <form id="openstackAccountForm" style="padding:5px 0 5px 0;margin:0;">
       <div class="panel panel-info" > 
            <div class="panel-heading"><b>Openstack 계정 정보</b></div>
            <div class="panel-body" style="padding:20px 10px 10px 10px; height:276px; overflow-y:auto;">
                <input type="hidden"  name="accountId"/>
                <input type="hidden" id="rsaPublicKeyModulus" />
                <input type="hidden" id="rsaPublicKeyExponent" />
                <div class="w2ui-field">
                    <label for="accountName"  style="width:35%; text-align: left; padding-left: 20px;">계정 별칭</label>
                    <div>
                        <input type="text"  name="accountName"  maxlength="100" style="width: 300px" placeholder="계정 별칭을 입력하세요." />
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:35%;text-align: left; padding-left: 20px;">Keystone Version</label>
                    <div>
                        <select name="openstackKeystoneVersion" onchange="setAccountInputByKeystoneVersion(this);" style="width: 300px">
                            <option value="v2" selected>v2</option>
                            <option value="v3">v3</option> 
                        </select>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label for="commonAccessEndpoint" style="width:35%;text-align: left; padding-left: 20px;">Identify API Tokens URL</label>
                    <div>
                        <input type="text" name="commonAccessEndpoint"  maxlength="255"  style="width: 300px" placeholder="Identify API Tokens URL을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label for="commonAccessUser" style="width:35%;text-align: left; padding-left: 20px;">User Name</label>
                    <div>
                        <input name="commonAccessUser"  type="text"  maxlength="255"  style="width: 300px" placeholder="사용자 명을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label for="commonAccessSecret"  style="width:35%;text-align: left; padding-left: 20px;">User Password</label>
                    <div>
                        <input name="commonAccessSecret" type="password"  maxlength="255" maxlength="100"  style="width: 300px" placeholder="사용자 비밀번호를 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field commonTenantDiv">
                    <label style="width:35%;text-align: left; padding-left: 20px;" for="commonTenant">Tenant</label>
                    <div>
                        <input name="commonTenant" type="text"  maxlength="255" style="width: 300px"  placeholder="테넌트를 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field commonProjectDiv" hidden="true">
                    <label style="width:35%;text-align: left; padding-left: 20px;" for="commonProject">Project</label>
                    <div>
                        <input name="commonProject" type="text"  maxlength="255" style="width: 300px" placeholder="프로젝트를 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field openstackDomainDiv" hidden="true">
                    <label style="width:35%;text-align: left; padding-left: 20px;" for="openstackDomain">Domain</label>
                    <div>
                        <input name="openstackDomain" type="text"  maxlength="255"  style="width: 300px" placeholder="도메인을 입력하세요."/>
                    </div>
                </div>
            </div>
        </div>
     </form> 
</div>
<div id="registPopupBtnDiv" hidden="true">
    <button type="button" class="btn" id="registBtn" onclick="$('#openstackAccountForm').submit();">확인</button>
    <button type="button" class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
</div>
<script>
$(function() {
    $.validator.addMethod("sqlInjection", function(value, element, params) {
        return checkInjectionBlacklist(params);
      },text_injection_msg);
    
    $("#openstackAccountForm").validate({
        ignore : "",
        success: "valid",
        onfocusout: true,
        rules: {
            accountName : { 
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='accountName']").val().trim() );
                }, sqlInjection : function(){
                    return $(".w2ui-msg-body input[name='accountName']").val().trim();
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
            }, commonTenant: { 
                required: function(){
                    if( $(".w2ui-msg-body .commonTenantDiv").css("display") == "none"){
                        return false;
                    }else{
                        return checkEmpty($(".w2ui-msg-body input[name='commonTenant']").val().trim());
                    }
                } , sqlInjection : function(){
                    return $(".w2ui-msg-body input[name='commonTenant']").val().trim();
                }
            }, commonProject: {
                required: function(){
                    if( $(".w2ui-msg-body .commonProjectDiv").css("display") == "none"){
                        return false;
                    }else{
                        return checkEmpty($(".w2ui-msg-body input[name='commonProject']").val().trim());
                    }
                }, sqlInjection : function(){
                    return $(".w2ui-msg-body input[name='commonProject']").val().trim();
                }
            }
        },messages: {
            accountName: { 
                 required:  "계정 별칭"+ text_required_msg
            } , commonAccessEndpoint: { 
                required:  "Identify API Tokens URL"+ text_required_msg
            } , commonAccessUser: { 
                required: "User Name"+ text_required_msg
            }, commonAccessSecret: { 
                required: "User Password"+ text_required_msg
            }, commonTenant: { 
                required:  "Tenant"+ text_required_msg
            }, commonProject: {
                required: "Project"+ text_required_msg
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
        }, submitHandler: function (form) {
            w2popup.lock(save_lock_msg, true);
            
            try {
                var rsa = new RSAKey();
                rsa.setPublic($("#rsaPublicKeyModulus").val(), $("#rsaPublicKeyExponent").val() );
            } catch(err) {
                w2alert(err);
            }
            accountInfo = {
                    iaasType : "Openstack"
                    , id : $(".w2ui-msg-body input[name='accountId']").val()
                    , accountName : $(".w2ui-msg-body input[name='accountName']").val()
                    , openstackKeystoneVersion : $(".w2ui-msg-body select[name='openstackKeystoneVersion']").val()
                    , commonAccessEndpoint : $(".w2ui-msg-body input[name='commonAccessEndpoint']").val()
                    , commonAccessUser : rsa.encrypt( $(".w2ui-msg-body input[name='commonAccessUser']").val() )
                    , commonAccessSecret : rsa.encrypt( $(".w2ui-msg-body input[name='commonAccessSecret']").val() )
                    , commonTenant : $(".w2ui-msg-body input[name='commonTenant']").val()
                    , commonProject : $(".w2ui-msg-body input[name='commonProject']").val()
                    , openstackDomain : $(".w2ui-msg-body input[name='openstackDomain']").val()
            }
            $.ajax({
                type : "PUT",
                url : "/iaasMgnt/account/openstack/save",
                contentType : "application/json",
                async : true,
                data : JSON.stringify(accountInfo),
                success : function(status) {
                    w2popup.unlock();
                    w2popup.close();    
                    initsetting();
                    w2ui['openstack_accountGrid'].reset();
                }, error : function(request, status, error) {
                    w2popup.unlock();
                    var errorResult = JSON.parse(request.responseText);
                    w2alert(errorResult.message);
                }
            });
        }
    });
});
</script>
