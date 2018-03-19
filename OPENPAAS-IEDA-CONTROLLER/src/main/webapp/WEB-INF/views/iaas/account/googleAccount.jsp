<%
/* =================================================================
 * 작성일 : 2017.05.10
 * 작성자 : 이정윤
 * 상세설명 : 계정 관리 화면( google 인프라 계정 조회)
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix = "spring" uri = "http://www.springframework.org/tags" %>

<script type="text/javascript">
var accountInfo ="";
var keyPathFileList="";
var search_grid_fail_msg = '<spring:message code="common.grid.select.fail"/>';//목록 조회 중 오류가 발생했습니다.
var search_lock_msg = '<spring:message code="common.search.data.lock"/>';//데이터 조회 중 입니다.
var save_lock_msg='<spring:message code="common.save.data.lock"/>';//등록 중 입니다.
var delete_lock_msg='<spring:message code="common.delete.data.lock"/>';//삭제 중 입니다.
var select_fail_msg='<spring:message code="common.grid.selected.fail"/>';//선택된 레코드가 존재하지 않습니다.
var popup_delete_msg='<spring:message code="common.popup.delete.message"/>';//삭제 하시겠습니까?
var text_required_msg='<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
var select_required_msg='<spring:message code="common.select.vaildate.required.message"/>';//을(를) 선택하세요.
var text_injection_msg='<spring:message code="common.text.validate.sqlInjection.message"/>';//입력하신 값은 입력하실 수 없습니다.

$(function() {
    $('#google_accountGrid').w2grid({
        name: 'google_accountGrid',
        style   : 'text-align:center',
        method  : 'GET',
        multiSelect: false,
        show: { 
                selectColumn: true,
                footer: true},
        columns : [
                   {field: 'id', caption: 'id', hidden: true}
                   , {field: 'status', caption: '환경 설정 정보 사용 여부', size: '12%', style: 'text-align:center'}
                   , {field: 'accountName', caption: '계정 별칭', size: '20%', style: 'text-align:left'}
                   , {field: 'commonProject', caption: 'project Id', size: '15%', style: 'text-align:left'}
                   , {field: 'googleJsonKeyPath', caption: 'Json Key', size: '20%', style: 'text-align:left'}
                   , {field: 'createDate', caption: '계정 생성 일자', size: '10%', style: 'text-align:center'}
                   , {field: 'updateDate', caption: '계정 수정 일자', size: '10%', style: 'text-align:center'}
        ],onError: function(event) {
        	w2alert(search_grid_fail_msg, "Google 계정 목록");
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
 * 설명 :  Google 등록 팝업 화면
 *********************************************************/
$("#registAccountBtn").click(function(){
    w2popup.open({
        title   : "<b>Google 계정 등록</b>",
        width   : 600,
        height  : 355,
        modal   : true,
        body    : $("#registPopupDiv").html(),
        buttons : $("#registPopupBtnDiv").html(),
        onOpen : function(event){
        	event.onComplete = function(){
        		//키파일정보 리스트
                getKeyPathFileList();
        	}                   
        },onClose:function(event){
        	w2ui['google_accountGrid'].reset();
        	initsetting();
            doSearch();
        }
    });
});

/*************************** *****************************
 * 설명 :  Google 수정 팝업 화면
 *********************************************************/
$("#updateAccountBtn").click(function(){
	if( $("#updateAccountBtn").attr("disabled") == "disabled" ) return;
    w2popup.open({
        title   : "<b>Google 계정 수정</b>",
        width   : 600,
        height  : 355,
        modal   : true,
        body    : $("#registPopupDiv").html(),
        buttons : $("#registPopupBtnDiv").html(),
        onOpen : function(event){
        	event.onComplete = function(){
        		//input readonly 설정
                $(".w2ui-msg-body input[name='accountName']").attr("readonly", true);
                $(".w2ui-msg-body input[name='commonProject']").attr("readonly", true);
                $(".w2ui-msg-body input[name='googleJsonKeyPath']").attr("readonly", true);
                
                //grid record
                var selected = w2ui['google_accountGrid'].getSelection();
                if( selected.length == 0 ){
                    w2alert(select_fail_msg, "Google 계정 수정");
                    return;
                }
                var record = w2ui['google_accountGrid'].get(selected);
                setGoogleAccountInfo(record.id);
            }                    
        },onClose:function(event){
        	w2ui['google_accountGrid'].reset();
        	initsetting();
        	doSearch();
        }
    });
});

/*************************** *****************************
 * 설명 :  google 삭제 팝업 화면
 *********************************************************/
$("#deleteAccountBtn").click(function(){
	if( $("#deleteAccountBtn").attr("disabled") == "disabled" ) return;
	//grid record
    var selected = w2ui['google_accountGrid'].getSelection();
    if( selected.length == 0 ){
        w2alert(select_fail_msg, "Google 계정 삭제");
        return;
    }
    var record = w2ui['google_accountGrid'].get(selected);
    var msg = "계정(" + record.accountName + ")"+ popup_delete_msg;
    if( record.status == '사용중' ){
        msg = "<span style='color:red'>현재 Google 환경 설정 정보 화면에서 <br/>해당 계정("+record.accountName+")을 사용하고 있습니다. </span><br/><span style='color:red; font-weight:bolder'>그래도 삭제 하시겠습니까?</span>";
    }
    w2confirm({
        title        : "<b>Google 계정 정보 삭제</b>",
        msg          : msg,
        yes_text     : "확인",
        no_text      : "취소",
        yes_callBack : function(event){
            //delete function 호출
            deleteGoogleAccountInfo(record);
            w2ui['google_accountGrid'].reset();
        }, no_callBack     : function(event){
            w2ui['google_accountGrid'].reset();
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
    w2ui['google_accountGrid'].load("<c:url value='/iaasMgnt/account/google/list'/>","",function(event){}); 
}

/****************************************************
 * 기능 : getKeyPathFileList
 * 설명 : 키 파일 정보 조회
 *****************************************************/
function getKeyPathFileList(){
    $.ajax({
        type : "GET",
        url : "/iaasMgnt/account/key/list",
        contentType : "application/json",
        async : true,
        success : function(data, status) {
            keyPathFileList = data;
            $('.w2ui-msg-body input:radio[name=keyPathType]:input[value=list]').attr("checked", true);  
            changeKeyPathType("list");
        },
        error : function( e, status ) {
            w2alert("KeyPath File 목록을 가져오는데 실패하였습니다.", "Google 계정 등록");
        }
    });
}

/******************************************************************
 * 기능 : changeKeyPathType
 * 설명 : Private key file 선택
 ***************************************************************** */
function changeKeyPathType(type){
        $(".w2ui-msg-body input[name=keyPathFileName]").val("");
        //목록에서 선택
        if(type == "list") {
        	$('.w2ui-msg-body select[name=keyPathList]').css("borderColor", "#bbb")
            changeKeyPathStyle("#keyPathListDiv", "#keyPathFileDiv");
            
        	var options = "<option value=''>키 파일을 선택하세요.</option>";
        	for( var i=0; i<keyPathFileList.length; i++ ){
        		if( accountInfo.googleJsonKeyPath == keyPathFileList[i] ){
        			options += "<option value='"+keyPathFileList[i]+"' selected='selected'>"+keyPathFileList[i]+"</option>";
        			$(".w2ui-msg-body input[name=googleJsonKeyPath]").val(keyPathFileList[i]);
                }else{
                	options += "<option value='"+keyPathFileList[i]+"'>"+keyPathFileList[i]+"</option>";
                }
        	}
            $('.w2ui-msg-body select[name=keyPathList]').html(options);
            
        }else{
            //파일업로드
            $('.w2ui-msg-body input[name=keyPathFileName]').css("borderColor", "#bbb")
            changeKeyPathStyle("#keyPathFileDiv", "#keyPathListDiv");
        }
}

/********************************************************
 * 기능 : changeKeyPathStyle
 * 설명 : Json 키 파일 스타일 변경
 *********************************************************/
function changeKeyPathStyle( showDiv, hideDiv ){
	 $(".w2ui-msg-body "+ hideDiv).hide();
     $(".w2ui-msg-body "+ hideDiv +" p").remove();
     $(".w2ui-msg-body "+ showDiv).show();
}

/******************************************************************
 * 기능 : setPrivateKeyPath
 * 설명 : 공통 Private key File List
 ***************************************************************** */
function setJsonKeyPath(event){
    $(".w2ui-msg-body input[name='googleJsonKeyPath']").val(event.value);
    
}

/******************************************************************
 * 기능 : openBrowse
 * 설명 : 공통 File upload Browse Button
 ***************************************************************** */
function openBrowse(){
    $(".w2ui-msg-body input[name='keyPathFile']").click();
}

/******************************************************************
 * 기능 : setPrivateKeyPathFileName
 * 설명: 공통 File upload Input
 ***************************************************************** */
function setPrivateKeyPathFileName(fileInput){
    var file = fileInput.files;
    $(".w2ui-msg-body input[name=googleJsonKeyPath]").val(file[0].name);
    $(".w2ui-msg-body #keyPathFileName").val(file[0].name);
    
}

/********************************************************
 * 기능 : setGoogleAccountInfo
 * 설명 : google 계정 상세 정보 조회 후 데이터 설정
 *********************************************************/
function setGoogleAccountInfo( id ){
     w2popup.lock( search_lock_msg, true);
     $.ajax({
        type : "GET",
        url : "/iaasMgnt/account/google/save/detail/"+id,
        contentType : "application/json",
        dataType : "json",
        async : true,
        success : function(data, status) {
        	accountInfo = {
                    iaasType : data.iaasType
                   ,id : data.id
                   ,accountName : data.accountName
                   ,commonProject : data.commonProject
                   ,googleJsonKeyPath : data.googleJsonKeyPath
        	}
        	
        	$(".w2ui-msg-body input[name='accountId']").val(data.id);
            $(".w2ui-msg-body input[name='accountName']").val(data.accountName);
            $(".w2ui-msg-body input[name='commonProject']").val(data.commonProject);
            $(".w2ui-msg-body input[name='googleJsonKeyPath']").val(data.googleJsonKeyPath);
            w2popup.unlock();
            getKeyPathFileList();
        },
        error : function(request, status, error) {
            w2popup.unlock();
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message);
        }
    });
}

/********************************************************
 * 기능 : keyPathFileUpload
 * 설명 : google json key 파일 업로드
 *********************************************************/
function keyPathFileUpload(){
    var form = $(".w2ui-msg-body #googleAccountForm")[0];
    var formData = new FormData(form);
    
    var files = document.getElementsByName('keyPathFile')[0].files;
    formData.append("file", files[0]);
    
    $.ajax({
        type : "POST",
        url : "/iaasMgnt/account/key/upload",
        enctype : 'multipart/form-data',
        dataType: "text",
        async : true,
        processData: false, 
        contentType:false,
        data : formData,  
        success : function(data, status) {
        	saveGoogleAccountInfo();
        },
        error : function( e, status ) {
            w2alert( "Json Key 업로드에 실패 하였습니다.", "Google 계정 등록");
        }
    });
}

/********************************************************
 * 기능 : saveGoogleAccountInfo
 * 설명 : google 계정 정보 저장
 *********************************************************/
function saveGoogleAccountInfo(){
	accountInfo = {
             iaasType : "Google"
            ,id : $(".w2ui-msg-body input[name='accountId']").val()
            ,accountName : $(".w2ui-msg-body input[name='accountName']").val()
            ,commonProject : $(".w2ui-msg-body input[name='commonProject']").val()
            ,googleJsonKeyPath : $(".w2ui-msg-body input[name='googleJsonKeyPath']").val()
    }
	$.ajax({
        type : "PUT",
        url : "/iaasMgnt/account/google/save",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(accountInfo),
        success : function(status) {
            w2popup.unlock();
            w2popup.close();  
            initsetting();
            w2ui['google_accountGrid'].reset();
        },
        error : function(request, status, error) {
            w2popup.unlock();
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message);
        }
    });
}

/********************************************************
 * 기능 : deleteGoogleAccountInfo
 * 설명 : google 계정 정보 삭제
 *********************************************************/
function deleteGoogleAccountInfo(record){ 
     if( $("#deleteAccountBtn").attr("disabled") == "disabled" ) return;
     accountInfo ={
            id : record.id,
            iaasType : record.iaasType,
            accountName : record.accountName
     }
     w2popup.lock(delete_lock_msg, true);
     
      $.ajax({
             type : "DELETE",
             url : "/iaasMgnt/account/google/delete",
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
    keyPathFileList="";
}

/********************************************************
 * 기능 : clearMainPage
 * 설명 : 다른페이지 이동시 호출
 *********************************************************/
function clearMainPage() {
    $().w2destroy('google_accountGrid');
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
    <div class="page_site">계정 관리 > <strong>Google 계정 관리 </strong></div>
     <div class="pdt20">
        <div class="fl" style="width:100%">
            <div class="dropdown" >
                <a href="#" class="dropdown-toggle iaas-dropdown" data-toggle="dropdown" aria-expanded="false">
                    <i class="fa fa-cloud"></i>&nbsp;Google<b class="caret"></b>
                </a>
                <ul class="dropdown-menu alert-dropdown">
                    <sec:authorize access="hasAuthority('IAAS_ACCOUNT_AWS_MENU')">
                        <li><a href="javascript:goPage('<c:url value="/iaasMgnt/account/aws"/>', 'AWS 관리');">AWS</a></li>
                    </sec:authorize>
                    <sec:authorize access="hasAuthority('IAAS_ACCOUNT_OPENSTACK_MENU')">
                        <li><a href="javascript:goPage('<c:url value="/iaasMgnt/account/openstack"/>', 'OPENSTACK 관리');">Openstack</a></li>
                    </sec:authorize>
                    <sec:authorize access="hasAuthority('IAAS_ACCOUNT_VSPHERE_MENU')">
                        <li><a href="javascript:goPage('<c:url value="/iaasMgnt/account/vSphere"/>', 'vSphere 관리');">vSphere</a></li>
                    </sec:authorize>
                </ul>
            </div>
        </div> 
    </div>
    <div class="pdt20">
        <div class="title fl">Google 계정 목록</div>
       <div class="fr"> 
            <!-- Button -->
            <sec:authorize access="hasAuthority('IAAS_ACCOUNT_GOOGLE_CREATE')">
               <span id="registAccountBtn" class="btn btn-primary" style="width:100px" >등록</span>
            </sec:authorize>
             <sec:authorize access="hasAuthority('IAAS_ACCOUNT_GOOGLE_UPDATE')">
               <span id="updateAccountBtn" class="btn btn-info" style="width:100px" >수정</span>
            </sec:authorize>
            <sec:authorize access="hasAuthority('IAAS_ACCOUNT_GOOGLE_DELETE')">
               <span id="deleteAccountBtn" class="btn btn-danger" style="width:100px" >삭제</span>
            </sec:authorize>
            <!-- //Btn -->
        </div>
        <div id="google_accountGrid" style="width:100%; height:610px"></div>
    </div>
</div>

<!-- Google 등록 및 수정 팝업 Div-->
<div id="registPopupDiv" hidden="true">
    <form id="googleAccountForm" action="PUT" style="padding:5px 0 5px 0;margin:0;">
        <div class="panel panel-info" > 
            <div class="panel-heading"><b>Google 계정 정보</b></div>
            <div class="panel-body" style="padding:20px 5% 10px 5%; height:219px; overflow-y:auto;">
             <input type="hidden"  name="accountId"/>
             <input type="hidden" id="rsaPublicKeyModulus" />
             <input type="hidden" id="rsaPublicKeyExponent" />
                <div class="w2ui-field">
                    <label style="width:35%;text-align: left; padding-left: 20px;">계정 별칭</label>
                    <div style="width: 60%;">
                        <input name="accountName" type="text"  maxlength="100" style="width: 300px"  placeholder="계정 별칭을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:35%;text-align: left; padding-left: 20px;">Project Id</label>
                    <div>
                        <input name="commonProject" type="text"  maxlength="100" style="width: 300px" placeholder="프로젝트 Id를 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:35%;text-align: left; padding-left: 20px;">Json Key File</label>
                    <div>
                        <span onclick="changeKeyPathType('file');" style="width:30%;"><label><input type="radio" name="keyPathType" value="file" />&nbsp;파일업로드</label></span>
                        &nbsp;&nbsp;
                        <span onclick="changeKeyPathType('list');" style="width:30%;"><label><input type="radio" name="keyPathType" value="list" />&nbsp;목록에서 선택</label></span>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:35%;font-size:11px;" class="control-label"></label>
                    <div style="position: relative;white-space: nowrap;width: 53%;min-width: 45%;" >
                        <div id="keyPathFileDiv" hidden="true">
                            <input type="text" id="keyPathFileName" name="keyPathFileName" style="width:80%;" readonly  onClick="openBrowse();" placeholder="업로드할 Key 파일을 선택하세요."/>
	                        <a href="#" id="browse" onClick="openBrowse();"><span id="BrowseBtn">Browse</span></a>
	                        <input type="file" name="keyPathFile" onchange="setPrivateKeyPathFileName(this);" style="display:none;"/>
                        </div>
                        <div id="keyPathListDiv">
                            <select name="keyPathList" onchange="setJsonKeyPath(this)" class="form-control" style="height:30px; font-size:12px;"></select>
                        </div>
                    </div>
                    <input name="googleJsonKeyPath" type="hidden" />
                </div>
            </div>
        </div>
    </form> 
</div>
<div id="registPopupBtnDiv" hidden="true">
     <button class="btn" id="registBtn" onclick="$('#googleAccountForm').submit();">확인</button>
     <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
</div>

<script>
$(function() {
	$.validator.addMethod("sqlInjection", function(value, element, params) {
        return checkInjectionBlacklist(params);
      },text_injection_msg);
    
	
    $("#googleAccountForm").validate({
        ignore : "",
        onfocusout: true,
        rules: {
            accountName : { 
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='accountName']").val().trim() );
                }
            }, commonProject: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='commonProject']").val().trim() );
                }
            }, keyPathList: { 
                required: function(){
                	if( $(".w2ui-msg-body input:radio[name='keyPathType']:checked").val() == 'list' ){
                        return checkEmpty(  $(".w2ui-msg-body select[name='keyPathList']").val().trim() );
                    }else{
                    	 return false;
                    }
                }
            }, keyPathFileName: { 
                required: function(){
                    if( $(".w2ui-msg-body input:radio[name='keyPathType']:checked").val() == 'file' ){
                        return checkEmpty(  $(".w2ui-msg-body input[name='keyPathFileName']").val().trim() );
                    }else{
                         return false;
                    }
                }
            }
        }, messages: {
            accountName: { 
                 required:  "계정 별칭"+ text_required_msg
            } , commonProject: { 
                required:  "프로젝트 Id"+ text_required_msg
            }, keyPathList: { 
                required:  "Json 키 파일"+ select_required_msg
            }, keyPathFileName: { 
                required:  "Json 키 파일"+ text_required_msg
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
        	w2popup.lock( save_lock_msg, true);
        	try {
                var rsa = new RSAKey();
                rsa.setPublic($("#rsaPublicKeyModulus").val(), $("#rsaPublicKeyExponent").val() );
            } catch(err) {
                w2alert(err);
            }
            if(  $(".w2ui-msg-body input:radio[name='keyPathType']:checked").val() == 'file' ){
            	keyPathFileUpload();
            }else{
            	saveGoogleAccountInfo();
            }
            
        }
    });
});
</script>