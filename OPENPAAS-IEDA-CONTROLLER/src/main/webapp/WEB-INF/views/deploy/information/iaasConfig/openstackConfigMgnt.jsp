<%
/* =================================================================
 * 작성일 : 2017.06.05
 * 작성자 : 이정윤
 * 상세설명 : 환경 설정 관리 화면( Openstack 인프라 환경 설정 조회)
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec"
    uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<style>
.error {
    display: block;
    color: red;
    font-weight: 100;
}
</style>

<script type="text/javascript">
var configInfo = "";
var keyPathFileList = "";
var search_grid_fail_msg = '<spring:message code="common.grid.select.fail"/>';//목록 조회 중 오류가 발생했습니다.
var search_lock_msg = '<spring:message code="common.search.data.lock"/>';//데이터 조회 중 입니다.
var save_lock_msg = '<spring:message code="common.save.data.lock"/>';//등록 중 입니다.
var delete_lock_msg = '<spring:message code="common.delete.data.lock"/>';//삭제 중 입니다.
var select_fail_msg = '<spring:message code="common.grid.selected.fail"/>'//선택된 레코드가 존재하지 않습니다.
var popup_delete_msg = '<spring:message code="common.popup.delete.message"/>';//삭제 하시겠습니까?
var text_required_msg = '<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
var text_injection_msg = '<spring:message code="common.text.validate.sqlInjection.message"/>';//입력하신 값은 입력하실 수 없습니다.
var select_required_msg = '<spring:message code="common.select.vaildate.required.message"/>';
$(function() {
    // Openstack 클라우드 인프라 환경 설정 정보 조회
    $('#openstack_configGrid').w2grid({
        name : 'openstack_configGrid',
        style : 'text-align:center',
        method : 'GET',
        multiSelect : false,
        show : {
            selectColumn : true,
            footer : true
        },columns : [ 
              {field : 'id', caption : 'id', hidden : true }
            , {field : 'iaasConfigAlias', caption : '환경 설정 별칭', size : '10%', style : 'text-align:left' }
            , {field : 'deployStatus', caption : '플랫폼 배포 사용 여부', size : '15%', style : 'text-align:center'}
            , {field : 'accountName', caption : '인프라 계정 별칭', size : '15%', style : 'text-align:left'}
            , {field : 'openstackKeystoneVersion', caption : 'Keystone Version', size : '10%', style : 'text-align:left' }
            , {field : 'commonSecurityGroup', caption : 'Security Group', size : '15%', style : 'text-align:left'}
            , {field : 'commonRegion', caption : 'Region', size : '15%', style : 'text-align:left'}
            , {field : 'commonKeypairName', caption : 'Keypair name', size : '10%', style : 'text-align:left' }
            , {field : 'createUserId', caption : '생성자', hidden : true}
            , {field : 'createDate', caption : '생성 일자', size : '10%', style : 'text-align:center'}
            , {field : 'updateDate', caption : '수정 일자', size : '10%', style : 'text-align:center'} 
        ], onError : function(event) {
            w2alert(search_grid_fail_msg, "Openstack 환경 설정 목록");
        }, onLoad : function(event) {

        }, onSelect : function(event) {
            event.onComplete = function() {
                $("#deleteConfigBtn").attr('disabled', false);
                $("#updateConfigBtn").attr('disabled', false);
            }
        }, onUnselect : function(event) {
            event.onComplete = function() {
                $("#deleteConfigBtn").attr('disabled', true);
                $("#updateConfigBtn").attr('disabled', true);
            }
        }
    });
    
    doSearch();

/*************************** *****************************
 * 설명 :  Openstack 등록 팝업 화면
 *********************************************************/
$("#registConfigBtn").click(function(){
    w2popup.open({
        title : "<b>Openstack 환경 설정 등록</b>",
        width : 680,
        height : 470,
        modal : true,
        body : $("#registPopupDiv").html(),
        buttons : $("#registPopupBtnDiv").html(),
        onOpen : function(event) {
            event.onComplete = function() {
                getIaasAccountName();
                getKeyPathFileList();
            }
        },
        onClose : function(event) {
            w2ui['openstack_configGrid'].reset();
            initsetting();
            doSearch();
        }
    });
});
        
/*************************** *****************************
* 설명 :  openstack 수정 팝업 화면
*********************************************************/
$("#updateConfigBtn").click(function(){
    if( $("#updateConfigBtn").attr("disabled") == "disabled" ) return;
    w2popup.open({
        title   : "<b>Openstack 환경 설정 수정</b>",
        width : 680,
        height : 470,
        modal   : true,
        body    : $("#registPopupDiv").html(),
        buttons : $("#registPopupBtnDiv").html(),
        onOpen : function(event){
            event.onComplete = function(){
                //grid record
                var selected = w2ui['openstack_configGrid'].getSelection();
                if( selected.length == 0 ){
                    w2alert('<spring:message code="common.grid.selected.fail"/>', "Openstack 환경 설정 수정");
                    return;
                }
                var record = w2ui['openstack_configGrid'].get(selected);
                setOpenstackConfigInfo(record.id);
            }                   
        },onClose:function(event){
            w2ui['openstack_configGrid'].reset();
            initsetting();
            doSearch();
        }
    });
});
    
/*************************** *****************************
 * 설명 :  Openstack 삭제 팝업 화면
 *********************************************************/
$("#deleteConfigBtn").click(function(){
    if( $("#deleteConfigBtn").attr("disabled") == "disabled" ) return;
    
    //grid record
    var selected = w2ui['openstack_configGrid'].getSelection();
    if( selected.length == 0 ){
        w2alert(select_fail_msg);
        w2ui['openstack_configGrid'].reset();
        doSearch();
        return;
    }
    var record = w2ui['openstack_configGrid'].get(selected);
    var msg = "환경 설정 정보(" + record.iaasConfigAlias + ")"+ popup_delete_msg;
    if( record.deployStatus == '사용중' ){
        msg = "<span style='color:red'>현재 Openstack 플랫폼 설치에서 <br/>해당 환경 설정 정보("+record.iaasConfigAlias+")를 사용하고 있습니다. </span><br/><span style='color:red; font-weight:bolder'>그래도 삭제 하시겠습니까?</span>";
    }
    w2confirm({
        title        : "<b>Openstack 환경 설정 정보 삭제</b>",
        msg          : msg,
        yes_text     : "확인",
        no_text      : "취소",
        yes_callBack : function(event){
            //delete function 호출
            deleteOpenstackConfigInfo(record);
            w2ui['openstack_configGrid'].reset();
        },no_callBack  : function(event){
            w2ui['openstack_configGrid'].reset();
            doSearch();
        }
    });
});     
});
    
/****************************************************
 * 기능 : getKeyPathFileList
 * 설명 : 키 파일 정보 조회
 *****************************************************/
function getKeyPathFileList(){
    $.ajax({
        type : "GET",
        url : "/common/deploy/key/list/openstack",
        contentType : "application/json",
        async : true,
        success : function(data, status) {
            keyPathFileList = data;
            $('.w2ui-msg-body input:radio[name=keyPathType]:input[value=list]').attr("checked", true);  
            changeKeyPathType("list");
        },
        error : function( e, status ) {
            w2alert("KeyPath File 목록을 가져오는데 실패하였습니다.", "BOOTSTRAP 설치");
        }
    });
}

/******************************************************************
 * 기능 : changeKeyPathType
 * 설명 : Private key file 선택
 ***************************************************************** */
function changeKeyPathType(type){
     $(".w2ui-msg-body input[name=commonKeypairPath]").val("");
     $(".w2ui-msg-body input[name=keyPathFileName]").val("");
     //목록에서 선택
     if(type == "list") {
         $('.w2ui-msg-body select[name=keyPathList]').css("borderColor", "#bbb")
         changeKeyPathStyle("#keyPathListDiv", "#keyPathFileDiv");
         
         var options = "<option value=''>키 파일을 선택하세요.</option>";
         for( var i=0; i<keyPathFileList.length; i++ ){
             if( configInfo.commonKeypairPath == keyPathFileList[i] ){
                 options += "<option value='"+keyPathFileList[i]+"' selected='selected'>"+keyPathFileList[i]+"</option>";
                 $(".w2ui-msg-body input[name=commonKeypairPath]").val(keyPathFileList[i]);
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

/****************************************************
 * 기능 : getIaasAccountName
 * 설명 : 해당 유저의 인프라에 대한 계정 별칭 목록 조회 요청
 *****************************************************/
function getIaasAccountName() {
    $.ajax({
        type : "GET",
        url : "/common/deploy/accountList/openstack",
        contentType : "application/json",
        success : function(data, status) {
            setupIaasAccountName(data);
            w2popup.unlock();
        },
        error : function(request, status, error) {
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "해당 계정 별칭 목록 조회");
        }
    });
}

/****************************************************
 * 기능 : setupIaasAccountName
 * 설명 : 해당 유저의 인프라에 대한 계정 별칭 목록 설정
*****************************************************/
var openstackVersionArray = [];
function setupIaasAccountName(data){
     openstackVersionArray = data;
     var iaasAccountName = "<select class='form-control select-control' style='width: 300px' onchange = 'setupOnChangeOpenstackVersion(this.value)' name='accountName' id='accountName'>";
     var openstackVersion = "";
     if( data.length ==0 ){
         iaasAccountName +="<option value=''>계정을 등록하세요.</option>";
     }else{
         for (var i=0; i<data.length; i++){
             if( configInfo.accountId == data[i].id ){
                 iaasAccountName +="<option value='"+data[i].id+"' selected>" + data[i].accountName + "</option>";
             }else{
                 iaasAccountName +="<option value='"+data[i].id+"'>" + data[i].accountName + "</option>";
             }
         }
     }
     iaasAccountName+="</select>";
     $(".w2ui-msg-body .accountNameDiv").append(iaasAccountName);
     setupOpenstackVersion(data);
}

/****************************************************
 * 기능 : setupOnChangeOpenstackVersion
 * 설명 : 해당 인프라 계정의 OnChange Openstack Version 설정
*****************************************************/
function setupOnChangeOpenstackVersion(value){
    openstackVersion = "";
    if(openstackVersionArray == 0){
        openstackVersion +="<input style = 'width:300px;' type= 'text' readonly value = '존재하지 않습니다.'/>";
    } else {
        for (var i=0; i<openstackVersionArray.length; i++){
            if(value == openstackVersionArray[i].id){
                if(openstackVersionArray[i].openstackKeystoneVersion=="v3"){
                    openstackVersion +="<input style = 'width:300px;' type= 'text' readonly value = 'Openstack V3 버전 입니다.'/>";
                    $('.w2ui-msg-body #region').css('display','block');
                }else {
                    openstackVersion +="<input style = 'width:300px;' type= 'text' readonly value = 'Openstack V2 버전 입니다.'/>";
                    $('.w2ui-msg-body #region').css('display','none');
                }
            }
        }
    }
    $(".w2ui-msg-body .openstackVersion").html(openstackVersion);
}

/****************************************************
 * 기능 : setupIaasAccountName
 * 설명 : 해당 인프라 계정의 Openstack Version 설정
*****************************************************/
function setupOpenstackVersion(data){
     openstackVersion = "";
     if(data.length == 0){
         openstackVersion +="<input style = 'width:300px;' type= 'text' readonly value = '존재하지 않습니다.'/>";
     } else {
         for (var i=0; i<data.length; i++){
             if(  $(".w2ui-msg-body .accountNameDiv #accountName option:selected").text() == data[i].accountName ){
                 if(data[i].openstackKeystoneVersion=="v3"){
                     openstackVersion +="<input style = 'width:300px;' type= 'text' name = 'openstackVersion' readonly value = 'V3'/>";
                     $('.w2ui-msg-body #region').css('display','block');
                 }else {
                     openstackVersion +="<input style = 'width:300px;' type= 'text' name = 'openstackVersion' readonly value = 'V2'/>";
                     $('.w2ui-msg-body #region').css('display','none');
                 }
             }
         }
     }
     $(".w2ui-msg-body .openstackVersion").html(openstackVersion);
}

/********************************************************
 * 기능 : doSearch
 * 설명 : 조회기능
 *********************************************************/
function doSearch() {
    $("#deleteConfigBtn").attr('disabled', true);
    $("#updateConfigBtn").attr('disabled', true);
    w2ui['openstack_configGrid'].load( "<c:url value='/info/iaasConfig/openstack/list'/>", "", function(event) {});
}

/********************************************************
 * 기능 : setOpenstackConfigInfo
 * 설명 : Openstack 계정 상세 정보 조회 후 데이터 설정
 *********************************************************/
function setOpenstackConfigInfo( id ){
     w2popup.lock( search_lock_msg, true);
    $.ajax({
        type : "GET",
        url : "/info/iaasConfig/openstack/save/detail/"+id,
        contentType : "application/json",
        dataType : "json",
        success : function(data, status) {
            $(".w2ui-msg-body input[name='configId']").val(data.id);
            $(".w2ui-msg-body input[name='iaasConfigAlias']").val(data.iaasConfigAlias);
            $(".w2ui-msg-body input[name='commonSecurityGroup']").val(data.commonSecurityGroup);
            $(".w2ui-msg-body input[name='commonRegion']").val(data.commonRegion);
            $(".w2ui-msg-body input[name='commonKeypairName']").val(data.commonKeypairName);
            configInfo = {
                    accountId : data.accountId,
                    commonKeypairPath : data.commonKeypairPath
            }
            w2popup.unlock();
            getKeyPathFileList();
            getIaasAccountName();
        },
        error : function(request, status, error) {
            w2popup.unlock();
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message);
        }
    });
}

/****************************************************
 * 기능 : uploadPrivateKey
 * 설명 : Openstack Private key 업로드
*****************************************************/
function uploadPrivateKey(){
    var form = $(".w2ui-msg-body #openstackConfigForm")[0];
    var formData = new FormData(form);
    
    var files = document.getElementsByName('keyPathFile')[0].files;
    formData.append("file", files[0]);
    $.ajax({
        type : "POST",
        url : "/common/deploy/key/upload",
        enctype : 'multipart/form-data',
        dataType: "text",
        async : true,
        processData: false, 
        contentType:false,
        data : formData,  
        success : function(data, status) {
            saveOpenstackConfigInfo();
        },
        error : function( e, status ) {
            w2alert( "Private Key 업로드에 실패 하였습니다.", "Openstack 환경 설정 등록");
        }
    });
    
}

/********************************************************
 * 기능 : saveOpenstackConfigInfo
 * 설명 : Openstack 환경 설정 정보 등록
 *********************************************************/
function saveOpenstackConfigInfo(){
    w2popup.lock(save_lock_msg, true);
    //Openstack 환경 설정 정보 등록
    configInfo = {
            iaasType : "OPENSTACK",
            id : $(".w2ui-msg-body input[name='configId']").val(),
            iaasConfigAlias : $(".w2ui-msg-body input[name='iaasConfigAlias']").val(),
            accountId : $(".w2ui-msg-body select[name='accountName']").val(),
            commonSecurityGroup : $(".w2ui-msg-body input[name='commonSecurityGroup']").val(),
            commonRegion : $(".w2ui-msg-body input[name='commonRegion']").val(),
            commonKeypairName : $(".w2ui-msg-body input[name='commonKeypairName']").val(),
            commonKeypairPath : $(".w2ui-msg-body input[name='commonKeypairPath']").val()
    }
    $.ajax({
        type : "PUT",
        url : "/info/iaasConfig/openstack/save",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(configInfo),
        success : function(status) {
            w2popup.unlock();
            w2popup.close();
            initsetting();
            w2ui['openstack_configGrid'].reset();
        },
        error : function(request, status, error) {
            w2popup.unlock();
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message);
        }
    });
}
/********************************************************
 * 기능 : deleteOpenstackConfigInfo
 * 설명 : AWS 환경 설정 정보 삭제
 *********************************************************/
function deleteOpenstackConfigInfo(record){
     if( $("#deleteConfigBtn").attr("disabled") == "disabled" ) return;
     configInfo ={
            id :  record.id,
            iaasType :  record.iaasType,
            iaasConfigAlias : record.iaasConfigAlias
     }
     
     w2popup.lock(delete_lock_msg, true);
     $.ajax({
             type : "DELETE",
             url : "/info/iaasConfig/openstack/delete",
             contentType : "application/json",
             dataType: "json",
             async : true,
             data : JSON.stringify(configInfo),
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
function initsetting() {
    configInfo = "";
}

/********************************************************
 * 기능 : clearMainPage
 * 설명 : 다른페이지 이동시 호출
 *********************************************************/
function clearMainPage() {
    $().w2destroy('openstack_configGrid');
}
/******************************************************************
 * Function : openBrowse
 * 설명 : 공통 File upload Browse Button
 ***************************************************************** */
function openBrowse(){
    $(".w2ui-msg-body input[name='keyPathFile']").click();
}
/********************************************************
 * 기능 : resize
 * 설명 : 화면 리사이즈시 호출
 *********************************************************/
$(window).resize(function() {
    setLayoutContainerHeight();
});
</script>


<div id="main">
    <div class="page_site">정보조회 > 인프라 환경 설정 관리 > <strong>Openstack 환경 설정 관리 </strong></div>
    <div class="pdt20">
        <div class="fl" style="width: 100%">
            <div class="dropdown">
                <a href="#" class="dropdown-toggle iaas-dropdown"
                    data-toggle="dropdown" aria-expanded="false"> <i
                    class="fa fa-cloud"></i>&nbsp;Openstack<b class="caret"></b>
                </a>
                <ul class="dropdown-menu alert-dropdown">
                    <sec:authorize access="hasAuthority('INFO_IAASCONFIG_AWS_LIST')">
                        <li><a href="javascript:goPage('<c:url value="/info/iaasConfig/aws"/>', 'AWS 관리');">AWS</a></li>
                    </sec:authorize>
                    <sec:authorize access="hasAuthority('INFO_IAASCONFIG_GOOGLE_LIST')">
                        <li><a href="javascript:goPage('<c:url value="/info/iaasConfig/google"/>', 'Google 관리');">Google</a></li>
                    </sec:authorize>
                    <sec:authorize
                        access="hasAuthority('INFO_IAASCONFIG_VSPHERE_LIST')">
                        <li><a href="javascript:goPage('<c:url value="/info/iaasConfig/vSphere"/>', 'vSphere 관리');">vSphere</a></li>
                    </sec:authorize>
                    <sec:authorize access="hasAuthority('INFO_IAASCONFIG_AZURE_LIST')">
                        <li><a href="javascript:goPage('<c:url value="/info/iaasConfig/azure"/>', 'vSphere 관리');">Azure</a></li>
                    </sec:authorize>
                    
                </ul>
            </div>
        </div>
    </div>
    <div class="pdt20">
        <div class="title fl">Openstack 환경 설정 목록</div>
        <div class="fr">
            <!-- Button -->
            <sec:authorize access="hasAuthority('INFO_IAASCONFIG_OPENSTACK_CREATE')">
                <span id="registConfigBtn" class="btn btn-primary"  style="width: 100px">등록</span>
            </sec:authorize>
            <sec:authorize access="hasAuthority('INFO_IAASCONFIG_OPENSTACK_UPDATE')">
                <span id="updateConfigBtn" class="btn btn-info" style="width: 100px">수정</span>
            </sec:authorize>
            <sec:authorize access="hasAuthority('INFO_IAASCONFIG_OPENSTACK_DELETE')">
                <span id="deleteConfigBtn" class="btn btn-danger" style="width: 100px">삭제</span>
            </sec:authorize>
            <!-- //Btn -->
        </div>
        <div id="openstack_configGrid" style="width: 100%; height: 610px;"></div>
    </div>
</div>


<!-- OPenstack 등록 및 수정 팝업 Div-->
<div id="registPopupDiv" hidden="true">
    <form id="openstackConfigForm" action="POST" style="padding: 5px 0 5px 0; margin: 0;">
        <div class="panel panel-info">
          <div class="panel-heading">
              <b>Openstack 환경 설정 정보</b>
          </div>
          <div class="panel-body" style="padding: 20px 10px; height: 335px; overflow-y: auto;">
              <input type="hidden" name="configId" />
              <div class="w2ui-field">
                  <label style="width: 36%; text-align: left; padding-left: 20px;">Openstack 환경 설정 별칭</label>
                  <div>
                      <input name="iaasConfigAlias" type="text"  maxlength="100" style="width: 300px" placeholder="환경 설정 별칭을 입력하세요." />
                  </div>
              </div>
              <div class="w2ui-field">
                <label style="width: 36%; text-align: left; padding-left: 20px;">Openstack 계정 별칭</label>
                <div class="accountNameDiv"></div>
              </div>
              
              <div class="w2ui-field">
                <label style="width: 36%; text-align: left; padding-left: 20px;">Keystone Version</label>
                <div class="openstackVersion"></div>
              </div>
              
              <div class="w2ui-field" id= "region" style = "display:none;">
                <label style="width: 36%; text-align: left; padding-left: 20px;">Region</label>
                <div>
                    <input name="commonRegion" type="text"   onkeydown="javascript:textSpaceValid(this);" maxlength="100" style="width: 300px" placeholder="Region을 입력하세요." />
                </div>
              </div>
              
              <div class="w2ui-field">
                <label style="width: 36%; text-align: left; padding-left: 20px;">Security Group</label>
                <div>
                    <input name="commonSecurityGroup" type="text"   onkeydown="javascript:textSpaceValid(this);" maxlength="100" style="width: 300px" placeholder="보안 그룹을 입력하세요." />
                </div>
              </div>

              <div class="w2ui-field">
                <label style="width: 36%; text-align: left; padding-left: 20px;">Keypair Name</label>
                <div>
                    <input name="commonKeypairName" type="text"   onkeydown="javascript:textSpaceValid(this);" maxlength="100" style="width: 300px" placeholder="Keypair Name 를 입력하세요." />
                </div>
              </div>
              <div class="w2ui-field">
                  <label style="width:36%;text-align: left; padding-left: 20px;">Private Key File</label>
                  <div>
                      <span onclick="changeKeyPathType('file');" style="width:30%;"><label><input type="radio" name="keyPathType" value="file" />&nbsp;파일업로드</label></span>
                      &nbsp;&nbsp;
                      <span onclick="changeKeyPathType('list');" style="width:30%;"><label><input type="radio" name="keyPathType" value="list" />&nbsp;목록에서 선택</label></span>
                  </div>
              </div>
              <div class="w2ui-field">
                  <label style="text-align: left;font-size:11px;" class="control-label"></label>
                  <div id="keyPathDiv" style="position:relative; width: 65%; left:230px;">
                      <div id="keyPathFileDiv" hidden="true">
                          <span>
                          <input type="text" id="keyPathFileName" name="keyPathFileName" style="width:55%;" readonly  onClick="openBrowse();" placeholder="업로드할 Key 파일을 선택하세요."/>
                          <input type="file" name="keyPathFile" onchange="setPrivateKeyPathFileName(this);" style="display:none;"/>
                          <span id="BrowseBtn"><a href="#" id="browse" onClick="openBrowse();">Browse</a></span>
                          </span>
                      </div>
                      <div id="keyPathListDiv">
                          <select name="keyPathList"  id="commonKeypairPathList" onchange="setPrivateKeyPath(this.value);" class="form-control select-control" style="width:55%"></select>
                      </div>
                  </div>
                  <input name="commonKeypairPath" type="hidden" />
              </div>
          </div>
        </div>
    </form>
</div>

<div id="registPopupBtnDiv" hidden="true">
    <button class="btn" id="registBtn"  onclick="$('#openstackConfigForm').submit();">확인</button>
    <button class="btn" id="popClose" onclick="w2popup.close();">취소</button>
</div>

<script>
$(function() {
    $.validator.addMethod("sqlInjection", function(value, element, params) {
        return checkInjectionBlacklist(params);
    }, text_injection_msg);

    $("#openstackConfigForm").validate({
        ignore : "",
        onfocusout : true,
        rules : {
            iaasConfigAlias : {
                required : function() {
                    return checkEmpty($(".w2ui-msg-body input[name='iaasConfigAlias']").val());
                }, sqlInjection : function() {
                    return $(".w2ui-msg-body input[name='iaasConfigAlias']").val();
                }
            },
            accountName : {
                required : function() {
                    return checkEmpty($(".w2ui-msg-body select[name='accountName']").val());
                }, sqlInjection : function() {
                    return $(".w2ui-msg-body select[name='accountName']").val();
                }
            },
            commonSecurityGroup : {
                required : function() {
                    return checkEmpty($(".w2ui-msg-body input[name='commonSecurityGroup']").val());
                }, sqlInjection : function() {
                    return $(".w2ui-msg-body input[name='commonSecurityGroup']").val();
                }
            },
            commonRegion : {
                required : function() {
                    if($(".w2ui-msg-body input[name='openstackVersion']").val() == "V3"){
                        return checkEmpty($(".w2ui-msg-body input[name='commonRegion']").val());
                    }else{
                        return false;
                    }
                }, sqlInjection : function() {
                    if($(".w2ui-msg-body input[name='openstackVersion']").val() == "V3"){
                        return $(".w2ui-msg-body input[name='commonRegion']").val();
                    }
                }
            }, commonKeypairName : {
                required : function() {
                    return checkEmpty($(".w2ui-msg-body input[name='commonKeypairName']").val());
                }, sqlInjection : function() {
                    return $(".w2ui-msg-body input[name='commonKeypairName']").val();
                }
            }, keyPathList: { 
                required: function(){
                    if( $(".w2ui-msg-body input:radio[name='keyPathType']:checked").val() == 'list' ){
                        return checkEmpty(  $(".w2ui-msg-body select[name='keyPathList']").val() );
                    }else{
                         return false;
                    }
                }
            }, keyPathFileName: { 
                required: function(){
                    if( $(".w2ui-msg-body input:radio[name='keyPathType']:checked").val() == 'file' ){
                        return checkEmpty(  $(".w2ui-msg-body input[name='keyPathFileName']").val() );
                    }else{
                         return false;
                    }
                }
            }
        },
        messages : {
            iaasConfigAlias : {
                required : "환경 설정 별칭" + text_required_msg,
                sqlInjection : text_injection_msg
            },accountName : {
                required : "인프라 계정 별칭" + text_required_msg,
                sqlInjection : text_injection_msg
            },commonSecurityGroup : {
                required : "보안 그룹" + text_required_msg
            },commonRegion : {
                required : "지역" + text_required_msg
            },commonKeypairName : {
                required : "Keypair 이름" + text_required_msg
            }, keyPathList: { 
                required:  "Key Pair 파일"+ select_required_msg
            }, keyPathFileName: { 
                required:  "Key Pair 파일"+ text_required_msg
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
        },submitHandler : function(form) {
            if(  $(".w2ui-msg-body input:radio[name='keyPathType']:checked").val() == 'file' ){
                uploadPrivateKey();
            }else{
                saveOpenstackConfigInfo();
            }
        }
    });
});
</script>

