<%
/* =================================================================
 * 작성일 : 2016-09
 * 작성자 : 지향은
 * 상세설명 : Manifest 관리
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2016.12       지향은        화면 개선 및 코드 버그 수정
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix = "spring" uri = "http://www.springframework.org/tags" %>
<script type="text/javascript">
var search_msg='<spring:message code="common.search.data.lock"/>';//조회 중 입니다.
var update_lock_msg='<spring:message code="common.update.data.lock"/>';//수정 중 입니다.
var save_lock_msg='<spring:message code="common.save.data.lock"/>';//등록 중 입니다.
var delete_lock_msg='<spring:message code="common.delete.data.lock"/>';//삭제 중 입니다.
var IAAS_TYPE_CODE='<spring:message code="common.code.iaasType.code.parent"/>';//100
var delete_confirm_msg='<spring:message code="common.popup.delete.message"/>';//을(를) 삭제 하시겠습니까?
var text_required_msg='<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
var upload_lock_msg = '<spring:message code="common.upload.file.lock"/>';//파일 업로드 중입니다.
var file_requred_msg = '<spring:message code="common.file.validate.required.message"/>';//업로드 할 파일을 선택하세요.
var file_notfound_msg = '<spring:message code="common.file.validate.notfound.message"/>';//파일을 찾을 수 없습니다.
$(function() {
   /************************************************
    * 설명: Manifest 조회
   ************************************************/
   $('#us_manifestGrid').w2grid({
      name: 'us_manifestGrid',
      header: '<b>Manifest 목록</b>',
      style    : 'text-align:center',
      msgAJAXerror : 'Manifset 조회 실패',
      method    : 'GET',
      multiSelect: false,
      show: {    
          selectColumn: true,
          footer: true},
      style: 'text-align: center',
      columns    : [
                   {field: 'recid',     caption: 'recid', hidden: true}
                 , {field: 'id',     caption: 'id', hidden: true}
                 , {field: 'fileName', caption: 'Manifest 파일명', size: '20%', style: 'text-align:center'}
                 , {field: 'iaas', caption: 'iaas', size: '20%', style: 'text-align:center'}
                 , {field: 'deploymentName', caption: '배포명', size: '20%', style: 'text-align:center'}
                    , {field: 'description', caption: '설명', size: '70%', style: 'text-align:center'}
                    , {field: 'deployStatus', caption: '배포 상태', size: '20%', style: 'text-align:center',
                        render: function(record) {
                            if( record.deployStatus == "DEPLOY_STATUS_PROCESSING" ){
                                 return '<span class="btn btn-primary" style="width:60px">배포 중</span>';
                            }else if( record.deployStatus == "DEPLOY_STATUS_DONE" ){
                                 return '<span class="btn btn-primary" style="width:60px">배포 완료</span>';
                            }else if( record.deployStatus == "DEPLOY_STATUS_CANCELLED" ){
                                 return '<span class="btn btn-primary" style="width:60px">배포 취소</span>';
                            }else if( record.deployStatus == "DEPLOY_STATUS_FAILED" ){
                                 return '<span class="btn btn-primary" style="width:60px">배포 실패</span>';
                            }
                        }
                    }
                 ],
         onSelect: function(event) {
          var grid = this;
          event.onComplete = function() {
              $('#manifestDownload').attr('disabled', false);
              $('#manifestUpdate').attr('disabled', false);
              $('#manifestDelete').attr('disabled', false);
          }
      },
      onUnselect: function(event) {
          event.onComplete = function() {
              $('#manifestDownload').attr('disabled', true);
              $('#manifestUpdate').attr('disabled', true);
              $('#manifestDelete').attr('disabled', true);
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
    
   /************************************************
    * 설명: Manifest 업로드
   ************************************************/
   $("#manifestUpload").click(function(){
       w2popup.open({
           title   : "<b>Manifest 업로드</b>",
           width   : 550,
           height  : 300,
           modal   : true,
           body    : $("#uploadPopupDiv").html(),
           buttons : $("#uploadPopupBtnDiv").html(),
           onClose : function(event){
               event.onComplete = function(){
                   initView();
               }
           },onOpen  : function(event){
               event.onComplete = function(){
                   getIaaSListInfo();
               }
           }
       });
   });
     
   /************************************************
    * 설명: Manifest 삭제 확인
   ************************************************/
   $("#manifestDelete").click(function(){
       if($("#manifestDelete").attr('disabled') == "disabled") return;
       //select grid row info
       var selected = w2ui['us_manifestGrid'].getSelection();
       if ( selected == "" || selected == null) return;
       var record = w2ui['us_manifestGrid'].get(selected);
       if ( record == "" || record == null) return;
      
       var msg = "";
       if( record.deployStatus != "" && record.deployStatus != null  ){
           msg = "현재 사용 중인 "+record.fileName + "파일입니다. <br/> 그래도 지우시겠습니까?";
       } else{
           msg = record.fileName +"파일"+delete_confirm_msg;
       }
       w2confirm({
           title        : "<b>Manifest 삭제</b>",
           msg          : msg,
           yes_text     : "확인",
           yes_callBack : function(envent){
               deleteManifest(record.id, record.fileName);
               w2popup.close();
           },
           no_text     : "취소",
           no_callBack : function(event){
               initView();
           }
       });
   });
   
   initView();
});

/********************************************************
 * 설명 : 조회 기능
 * 기능 : doSearch
 *********************************************************/
function doSearch() {
    w2ui['us_manifestGrid'].load("<c:url value='/info/manifest/list'/>");
}

/********************************************************
 * 설명 : 인프라 환경 정보 조회
 * 기능 : getIaaSListInfo
 *********************************************************/
function getIaaSListInfo(){
    var releaseTypeList = "";
    $.ajax({
        type : "GET",
        url : "/common/deploy/codes/parent/"+IAAS_TYPE_CODE+"",
        contentType : "application/json",
        async : true,
        success : function(data, status) {
            var options= "";
            if( !checkEmpty(data) ){
                for( var i=0; i<data.length; i++ ){
                    options += "<option value='"+ data[i].codeName +"'>"+data[i].codeName+"</option>";
                }
            }else{
                options = "<option value=''>인프라 환경을 선택하세요.</option>";
            }
            $(".w2ui-msg-body select[name='iaasList']").html(options);
        },
        error : function(e, status) {
            w2popup.unlock();
            w2alert("Release 유형을 가져오는데 실패하였습니다.", "릴리즈 유형");
        }
    });
}

 /********************************************************
  * 설명 : iaas 정보 설정
  * 기능 : setIaasInfo
  *********************************************************/
function setIaasInfo(value){
    $(".w2ui-msg-body #iaasInput").val(value);
}

 /********************************************************
  * 설명 : Manifest 브라우저 찾기
  * 기능 : openBrowse
  *********************************************************/
function openBrowse(){
    $(".w2ui-msg-body input[name='uploadPathFile']").click();
}

 /********************************************************
  * 설명 : Manifest 파일 경로
  * 기능 : setUploadFilePath
  *********************************************************/
function setUploadFilePath(fileInput){
      var file = fileInput.files;
      var files = $('#uploadPathFile')[0].files;
      $(".w2ui-msg-body input[name=uploadPathFileName]").val(file[0].name);
}

 /********************************************************
  * 설명 : Manifest validation
  * 기능 : uploadManifestValidate
  *********************************************************/
function uploadManifestValidate(){
    w2popup.lock(upload_lock_msg,true);
    var form = $(".w2ui-msg-body #settingForm");
    var formData = new FormData(form);

    var files = $(".w2ui-msg-body input[name='uploadPathFile']")[0].files;
    formData.append("file", files[0]);
    formData.append("description", $(".w2ui-msg-body input[name='description']").val());
    formData.append("iaas", $(".w2ui-msg-body select[name='iaasList']").val());
    
    if(files[0] == null || files[0] == undefined ){
        w2popup.unlock();
        w2alert(file_requred_msg, "Manifest 파일 업로드");
        return false;
    }
    
    if(files[0].size == 0){
        w2popup.unlock();
        w2alert(file_notfound_msg, "Manifest 파일 업로드");
        return false;
     }
    if( $(".w2ui-msg-body #uploadPathFileName").val().indexOf(".yml") < 0 ){
        w2popup.unlock();
        w2alert(".yml 확장자를 가진 파일만 가능합니다. 확인해주세요.", "Manifest 파일 업로드");
        return false;
    }
    
    uploadManifest(formData, name);
}

 /********************************************************
  * 설명 : Manifest 업로드 요청
  * 기능 : uploadManifest
  *********************************************************/
function uploadManifest(formData, name){
     $.ajax({
        type     : "POST",
        url      : "/info/manifest/upload",
        enctype  : 'multipart/form-data',
        dataType : "text",
        async    : true,
        processData : false, 
        contentType :false,
        data : formData,  
        success : function(data, status) {
            w2popup.close();
            w2popup.unlock();
            doSearch();
        },
        error : function(e, status) {
            w2popup.unlock();
            w2ui['us_manifestGrid'].reset();
            if((JSON.parse(e.responseText).code).indexOf("yaml") > -1){
                var errorResult = JSON.parse(e.responseText).message;
                errorResult =  "<span style='font-weight:bold'>YAML 형식 오류:</span>";
                errorResult += "<br/><br/><div style='text-align:left'>" + errorResult+"</div>";
                w2alert(errorResult, "Manifest 업로드");
            }else{
                w2alert(JSON.parse(e.responseText).message, "Manifest 업로드");
            }
        }
    });
}


 /********************************************************
 * 설명 : Manifest 다운로드
 * 기능 : downloadManifest
 *********************************************************/
function downloadManifest( ){
     if($("#manifestDownload").attr('disabled') == "disabled") return;
    //select grid row info
    var selected = w2ui['us_manifestGrid'].getSelection();
    if ( selected == "" || selected == null) return;
    var record = w2ui['us_manifestGrid'].get(selected);
    if ( record == "" || record == null) return;
    
    var logDownloadUrl = "/info/manifest/download/"+ record.id; 
    
    window.open(logDownloadUrl, '', ''); 
    initView();
    return false;
}

 /********************************************************
 * 설명 : Manifest 수정
 * 기능 : updateManifest
 *********************************************************/
function updateManifest(){
    if($("#manifestUpdate").attr('disabled') == "disabled") return;
    //select grid row info
    var selected = w2ui['us_manifestGrid'].getSelection();
    if ( selected == "" || selected == null) return;
    var record = w2ui['us_manifestGrid'].get(selected);
    if ( record == "" || record == null) return;

    w2popup.open({
        title   : "<b>Manifest 수정</b>",
        width    : 850,
        height   : 650,
        body     : $("#updatePopupDiv").html(),
        buttons  : $("#updatePopupButtons").html(),
        modal    : true,
        showMax  : true,
        onClose  : doSearch,
        onOpen   : function(event) {
            event.onComplete = function() {
                getManifestInfo(record.id);
            }
        },onClose : function(event){
            initView();
        }
    });
}

/********************************************************
 * 설명 : Manifest 내용 조회
 * 기능 : getManifestInfo
 *********************************************************/
function getManifestInfo(id){
    $.ajax({
        type : "GET",
        url : "/info/manifest/update/"+id,
        contentType : "application/json",
        async : true,
        success : function(data, status) {
            $(".w2ui-msg-body #manifestContent").text(data);
        },
        error : function(e, status) {
            w2alert("Manifest 내용을 가져오는 중 오류가 발생하였습니다. ", "Manifest 조회");
        }
    });
}

/********************************************************
 * 설명 : 내용 수정 요청
 * 기능 : updateManifestContnet
 *********************************************************/
function updateManifestContnet(){
    w2popup.lock(update_lock_msg,true);
    var selected = w2ui['us_manifestGrid'].getSelection();
    if ( selected == "" || selected == null) return;
    var record = w2ui['us_manifestGrid'].get(selected);
    if ( record == "" || record == null) return;

    //1.2
    var manifest = {
            id       : record.id,
            content  : $(".w2ui-msg-body #manifestContent").val(),
            fileName : record.fileName
    }
    
    if( manifest.content != "" ) {
        $.ajax({
            type : "PUT",
            url : "/info/manifest/update",
            contentType : "application/json",
            data : JSON.stringify(manifest),
            success : function(data, status) {
                w2popup.unlock();
                w2popup.close();
                doSearch();
            },
            error : function(e, status) {
                w2popup.unlock();
                if((JSON.parse(e.responseText).code).indexOf("yaml") > -1){
                    var errorResult = JSON.parse(e.responseText).message;
                    errorResult =  "<span style='font-weight:bold'>YAML 형식 오류:</span>" +
                        "<br/><br/><div style='text-align:left'>" + errorResult+"</div>";
                    w2alert(errorResult, "YAML 형식 오류");
                }else{
                    w2alert( JSON.parse(e.responseText).message, "Manifest 수정")
                }
            }
        });
    }
}

/********************************************************
 * 설명 : Manifest 삭제
 * 기능 : deleteManifest
 *********************************************************/
function deleteManifest( id, manifestFile ){
     w2popup.lock(delete_lock_msg, true);
     $.ajax({
         type : "DELETE",
         url : "/info/manifest/delete/"+ id,
         success : function(data, status) {
             w2popup.unlock();
            initView();
         }, error : function(e, status) {
             w2ui['us_manifestGrid'].reset();
             var errorResult = JSON.parse(e.responseText);
             w2alert(errorResult.message, "Manifest 삭제");
         }
     });
 }

 /********************************************************
  * 설명 : 팝업 종료시 이벤트
  * 기능 : popupComplete
  *********************************************************/
function popupComplete(){
    var msg = $(".w2ui-msg-title b").text() + " 화면을 닫으시겠습니까?";
    w2confirm({
        title        : "<b>"+$(".w2ui-msg-title b").text()+"</b>",
        msg          : msg,
        yes_text     : "확인",
        yes_callBack : function(envent){
            w2popup.close();
            doSearch();
        },
        no_text : "취소"
    });
}

/********************************************************
 * 설명 : 초기 설정
 * 기능 : initView
 *********************************************************/
function initView(bDefaultDirector) {
    w2ui['us_manifestGrid'].clear();
    doSearch();
    $('#manifestDownload').attr('disabled', true);
    $('#manifestUpdate').attr('disabled', true);
    $('#manifestDelete').attr('disabled', true);
}

/********************************************************
 * 설명 : 다른페이지 이동시 호출
 * 기능 : clearMainPage
 *********************************************************/
function clearMainPage() {
    $().w2destroy('us_manifestGrid');
}

/********************************************************
 * 설명 : 화면 리사이즈시 호출
 * 기능 : resize
 *********************************************************/
$( window ).resize(function() {
    setLayoutContainerHeight();
});
</script>

<div id="main">
    <div class="page_site">정보조회 > <strong>Manifest 관리</strong></div>
    <div class="pdt20"> 
        <div class="title fl">Manifest 목록</div>
        <div class="fr"> 
            <!-- Btn -->
            <sec:authorize access="hasAuthority('INFO_MANIFEST_UPLOAD')">
                <span class="btn btn-warning" style="width:120px" id="manifestUpload">업로드</span>
            </sec:authorize>
            <sec:authorize access="hasAuthority('INFO_MANIFEST_DOWNLOAD')">
                <span class="btn btn-primary" style="width:120px" id="manifestDownload" onclick="downloadManifest();">다운로드</span>
            </sec:authorize>
            <sec:authorize access="hasAuthority('INFO_MANIFEST_UPDATE')">
                <span class="btn btn-info" style="width:120px" id="manifestUpdate" onclick="updateManifest();">수정</span>
            </sec:authorize>
            <sec:authorize access="hasAuthority('INFO_MANIFEST_DELETE')">
                <span class="btn btn-danger" style="width:120px" id="manifestDelete">삭제</span>
            </sec:authorize>
            <!-- //Btn -->
        </div>
    </div>
    <!-- Manifest Grid -->
    <div id="us_manifestGrid" style="width:100%; height:718px"></div>

    <!-- Manifest 업로드  -->
    
    <div id="uploadPopupDiv" hidden="true">
        <form id="settingForm">
            <div class="w2ui-page page-0">
                <div class="panel panel-info" style="margin-top:5px;">
                    <div class="panel-heading"><b>Manifest 업로드</b></div>
                    <div class="panel-body" style="height:165px;">
                        <input name="iaasInput" id="iaasInput" type="text" style="width:200px;" hidden="true" />
                        <div class="w2ui-field">
                            <label style="width:30%;text-align: left;padding-left: 20px;">인프라 환경</label>
                            <div>
	                            <select name="iaasList" style="width:217px;">
	                                <option value="">인프라 환경을 선택하세요.</option>
	                            </select>
                            </div>
                        </div>
                        <div class="w2ui-field">
                            <label style="width:30%;text-align: left;padding-left: 20px;">설명</label>
                            <div>
                                <input name="description" id="description" type="text" style="width:217px;"/>
                            </div>
                        </div>
                        <div class="w2ui-field">
                            <label style="width:30%;text-align: left;padding-left: 20px;">파일</label>
                            <div>
                                <input type="text" id="uploadPathFileName" name="uploadPathFileName" style="width:218px" readonly  onClick="openBrowse();" placeholder="업로드할 Manifest 파일을 선택하세요."/>
                                <a href="#" id="browse" onClick="openBrowse();"><span id="BrowseBtn">Browse</span></a>
                                <input type="file" name="uploadPathFile" id="uploadPathFile" onchange="setUploadFilePath(this);" style="display:none;"/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>
    <div id="uploadPopupBtnDiv" hidden="true">
        <button class="btn" id="registBtn" onclick="$('#settingForm').submit();">업로드</button>
        <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
    </div>
    
    <!--  Manifest 수정  -->
    <div id="updatePopupDiv" hidden="true">
        <div class="w2ui-page page-0" >
            <div class="panel panel-info" style="margin-top:5px;" >
                    <div style="height:300px;">
                        <textarea id="manifestContent" style="width:100%;height:550px; overflow-y:visible;resize:none;background-color: #FFF;"></textarea>
                    </div>
            </div>
        </div>
        <div class="w2ui-buttons" id="updatePopupButtons" hidden="true">
            <button class="btn" id="updateManifestContnet" onclick="updateManifestContnet()">저장</button>
            <button class="btn" onclick="popupComplete();">닫기</button>
        </div>
    </div>
</div>
<script type="text/javascript">
$(function() {
    $("#settingForm").validate({
        ignore : "",
        onfocusout: true,
        rules: {
            iaasList: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body select[name='iaasList']").val() ); }
            }, description: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='description']").val() ); }
            }, uploadPathFileName: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='uploadPathFileName']").val() ); }
            }
        }, messages: {
            iaasList           : { required:  "인프라 환경"+text_required_msg }
           ,description        : {  required: "설명"+text_required_msg }
           ,uploadPathFileName : { required:  "파일"+text_required_msg }
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
            uploadManifestValidate();
        }
    });
});
</script>