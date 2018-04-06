<%
/* =================================================================
 * 작성일 : 2016-09
 * 작성자 : 지향은
 * 상세설명 : 릴리즈 관리
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2016.12       지향은        화면 개선 및 코드 버그 수정
 * 2017.05       이동현        화면 개선 및 코드 버그 수정
 * 2017.08       지향은        Google 클라우드 추가
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix = "spring" uri = "http://www.springframework.org/tags" %>
<script type="text/javascript">
//private common variable
var downloadClient = "";
var downloadStatus = "";
var releaseTyps = "";
var iaasTypes = "";
var completeButton = '<div><div class="btn btn-success btn-xs" style="width:100px; padding:3px;">Downloaded</div></div>';
var downloadingButton = '<div class="btn btn-info btn-xs" style="width:100px;">Downloading</div>';
var progressBarDiv = '<div class="progress">';
    progressBarDiv += '<div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" >';
    progressBarDiv += '</div></div>';

var IAAS_TYPE_CODE='<spring:message code="common.code.iaasType.code.parent"/>';//100
var text_required_msg='<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
var text_injection_msg='<spring:message code="common.text.validate.sqlInjection.message"/>';//입력하신 값은 입력하실 수 없습니다.
$(function() {
    /********************************************************
     * 설명 :  릴리즈 목록 조회 Grid 생성
     *********************************************************/
    $('#config_releaseGrid').w2grid({
        name: 'config_releaseGrid',
        header: '<b>릴리즈 등록</b>',
        method: 'GET',
        multiSelect: false,
        show: {    selectColumn: true, footer: true},
        style: 'text-align:center',
        columns:[
             {field: 'recid', caption: 'recid', hidden: true}
            ,{field: 'id', caption: 'id', hidden: true}
            ,{field: 'releaseName', caption: '릴리즈 명', size: '15%', style:'text-align:left; padding-left:10px' }            
            ,{field: 'releaseType', caption: '릴리즈 유형', size: '10%'}
            ,{field: 'releaseFileName', caption: '릴리즈 파일명', size: '20%', style:'text-align:left;  padding-left:10px'}
            ,{field: 'releaseSize', caption: '릴리즈 파일 크기', size: '7%'}
            ,{field: 'downloadStatus', caption: '다운로드 여부', size: '10%',
                render: function(record) {
                    if ( record.downloadStatus == 'DOWNLOADED'  ){
                        return '<div class="btn btn-success btn-xs" id= "downloaded_'+record.id+'" style="width:100px;">Downloaded</div>';
                    }else if(record.downloadStatus == 'DOWNLOADING'){
                        return '<div class="btn btn-info btn-xs" id= "downloading_'+record.id+'" style="width:100px;">Downloading</div>';
                    } else{
                        return '<div class="btn" id="isExisted_'+record.id+'" style="position: relative;width:100px;"></div>';
                    }
                }
            }
        ],
        onSelect : function(event) {
            event.onComplete = function() {
                $('#doDelete').attr('disabled', false);
                return;
            }
        },
        onUnselect: function(event) {
            event.onComplete = function() {
                $('#doDelete').attr('disabled', true);
            }
        },onLoad:function(event){
            if(event.xhr.status == 403){
                location.href = "/abuse";
                event.preventDefault();
            }
        },
        onError : function(event) {
        }
    });
    
    /********************************************************
     * 설명 :  릴리즈 등록 팝업
     *********************************************************/
     $("#doRegist").click(function(){
         w2popup.open({
             title   : "<b>릴리즈 등록</b>",
             width   : 635,
             height  : 505,
             modal   : true,
             body    : $("#regPopupDiv").html(),
             buttons : $("#regPopupBtnDiv").html(),
             onClose : function(event){
                 doSearch();
             }
         });
         $('.w2ui-msg-body input:radio[name=fileType]:input[value=version]').attr("checked", true);    
         $('.w2ui-msg-body input:text[name=releasePathVersion]').attr("readonly", false);
         //릴리즈 유형 조회
         $('[data-toggle="popover"]').popover();
          //스템셀 버전 정보
          $(".release-info").attr('data-content', "http://bosh.io/releases");
          getReleaseTypes();
          //다른 곳 클릭 시 popover hide 이벤트
          $('.w2ui-popup').on('click', function (e) {
              $('[data-toggle="popover"]').each(function () {
                  //the 'is' for buttons that trigger popups
                  //the 'has' for icons within a button that triggers a popup
                  if (!$(this).is(e.target) && $(this).has(e.target).length === 0 && $('.popover').has(e.target).length === 0) {
                      $(this).popover('hide');
                  }
              });
          });
     });
    
    /********************************************************
     * 설명 :  릴리즈 삭제 팝업
     *********************************************************/
    $("#doDelete").click(function(){
        if($("#deleteBtn").attr('disabled') == "disabled"){
            return;
        }
        var selected = w2ui['config_releaseGrid'].getSelection();
        var record = w2ui['config_releaseGrid'].get(selected);
        var message = "";
        
        if ( record.releaseFileName )
            message = "릴리즈 (파일명 : " + record.releaseFileName + ")를 삭제하시겠습니까?";
        else
            message = "선택된 릴리즈를 삭제하시겠습니까?";
        
        w2confirm({
            title     : "릴리즈 삭제",
            msg        : message,
            yes_text: "확인",
            yes_callBack : function(event){
                    deletePop(record);
            },
            no_text : "취소",
            no_callBack    : function(){
                initView();
            }
        });
    });
    
     initView();
    
});


/******************************************************************
 * 기능 : getReleaseTypes
 * 설명 : 릴리즈 유형 조회
 ***************************************************************** */
function getReleaseTypes() {
    var releaseTypeList = "";
    $.ajax({
        type : "GET",
        url : "/config/systemRelease/list/releaseType",
        contentType : "application/json",
        async : true,
        success : function(data, status) {
            releaseTyps = new Array();
            if( data != null){
                data.map(function(obj) {
                    releaseTyps.push(obj);
                });
            }
            releaseTypeList="<select style='width:60%' onchange='setiaasType(this.value);' name = 'releaseType'>";
            for(var i = 0; i<releaseTyps.length; i++){
                releaseTypeList+="<option value="+releaseTyps[i]+">"+releaseTyps[i]+"</option>";
            }
            releaseTypeList+="</select>";
            $(".w2ui-msg-body #releaseTypeDiv").html(releaseTypeList);
        },
        error : function(e, status) {
            w2popup.unlock();
            w2alert("Release 유형을 가져오는데 실패하였습니다.", "릴리즈 유형");
        }
    });
}

/**************************************************************
 * 설명 인프라 환경 유형 설정
 * 기능 : setiaasType
 **************************************************************/
function setiaasType(releaseType){
    var iaasTypeList = "";
    if(releaseType.toUpperCase()=="BOSH_CPI"){
    	$(".w2ui-msg-body #releasePathVersion").attr("disabled", false);
        $(".w2ui-msg-body #releasePathVersion").val("");
        $(".w2ui-msg-body select[name='releaseIaasType']").attr("readonly", false);
        $.ajax({
            type : "GET",
            url : "/common/deploy/codes/parent/"+IAAS_TYPE_CODE+"",
            contentType : "application/json",
            async : true,
            success : function(data, status) {
                iaasTypes = new Array();
                if( data != null){
                    data.map(function(obj) {
                        iaasTypes.push(obj.codeName);
                    });
                }
                iaasTypeList="<select style='width:60%'  name = 'releaseIaasType'>";
                for(var i=0; i<iaasTypes.length; i++){
                    iaasTypeList+="<option value="+iaasTypes[i]+">"+iaasTypes[i]+"</option>";
                }
                iaasTypeList+="</select>";
                $(".w2ui-msg-body #iaasTypeListDiv").html(iaasTypeList);
            },
            error : function(e, status) {
                w2popup.unlock();
                w2alert("IaaS 유형을 가져오는데 실패하였습니다.", "릴리즈 유형");
            }
        });
    }else if( releaseType.toUpperCase()=="ETC" ){
    	$(".w2ui-msg-body #releasePathVersion").attr("disabled", true);
    	$(".w2ui-msg-body #releasePathVersion").val("버전을 통한 릴리즈 다운로드는 이용하실 수 없습니다.");
    }else{
    	$(".w2ui-msg-body #releasePathVersion").attr("disabled", false);
        $(".w2ui-msg-body #releasePathVersion").val("");
        $(".w2ui-msg-body #iaasTypeListDiv").html("<select disabled='disabled' style = 'width:60%'><option selected='selected' disabled='disabled' value='' style='color:red'>BOSH_CPI 일 경우 선택 가능 합니다.</option></select>");
    }
}

/**************************************************************
 * 설명 릴리즈 다운 유형 change Event
 * 기능 : setRegistType
 **************************************************************/
function setRegistType(type){
    $(".w2ui-msg-body :radio[name='fileType'][value='"+type+"']").prop("checked", true);
    $('.w2ui-msg-body input:text[name=releasePathUrl]').val("");
    $('.w2ui-msg-body input:text[name=releasePathVersion]').val("");
    $('.w2ui-msg-body input:text[name=releaseFileName]').val("");
    if(type.toUpperCase()=="URL"){
        $('.w2ui-msg-body #browser').attr("disabled", true);
        $('.w2ui-msg-body input:text[name=releasePathVersion]').attr("readonly", true);
        $('.w2ui-msg-body input:text[name=releasePathUrl]').attr("readonly", false);
        $(".w2ui-msg-body input[name=releasePathVersion]").parent().find("p").remove();
        $(".w2ui-msg-body input[name=releasePathVersion]").css("borderColor","#777");
        $(".w2ui-msg-body input[name=releaseFileName]").parent().parent().find("p").remove();
        $(".w2ui-msg-body input[name=releaseFileName]").css("borderColor","#777");
    }else if(type.toUpperCase()=="FILE"){
        $('.w2ui-msg-body #browser').attr("disabled", false);
        $('.w2ui-msg-body input:text[name=releasePathUrl]').attr("readonly", true);
        $('.w2ui-msg-body input:text[name=releasePathVersion]').attr("readonly", true);
        $(".w2ui-msg-body input[name=releasePathVersion]").parent().find("p").remove();
        $(".w2ui-msg-body input[name=releasePathVersion]").css("borderColor","#777");
        $(".w2ui-msg-body input[name=releasePathUrl]").parent().find("p").remove();
        $(".w2ui-msg-body input[name=releasePathUrl]").css("borderColor","#777");
    }else if(type.toUpperCase()=="VERSION"){
        $('.w2ui-msg-body #browser').attr("disabled", true);
        $('.w2ui-msg-body input:text[name=releasePathUrl]').attr("readonly", true);
        $('.w2ui-msg-body input:text[name=releasePathVersion]').attr("readonly", false);
        $(".w2ui-msg-body input[name=releaseFileName]").parent().parent().find("p").remove();
        $(".w2ui-msg-body input[name=releaseFileName]").css("borderColor","#777");
        $(".w2ui-msg-body input[name=releasePathUrl]").parent().find("p").remove();
        $(".w2ui-msg-body input[name=releasePathUrl]").css("borderColor","#777");
    }else{
        alert("잘못 된 요청입니다.");
    }
    if( type.toUpperCase()=="VERSION" && $(".w2ui-msg-body select[name='releaseType']").val() =="ETC" ){
        $(".w2ui-msg-body input[name='releasePathVersion']").val("버전을 통한 릴리즈 다운로드는 이용하실 수 없습니다.");
        $(".w2ui-msg-body #releasePathVersion").attr("disabled", true);
        
    }else{
        $(".w2ui-msg-body #releasePathVersion").attr("disabled", false);
        $(".w2ui-msg-body #releasePathVersion").val("");
    }
}


/********************************************************
 * 설명 : 화면 초기화
 * 기능 : initView
 *********************************************************/
function initView() {
    downloadStatus = "";
    doSearch();
    w2ui['config_releaseGrid'].selectNone();
    $('#doDelete').attr('disabled', true);
    
}

/********************************************************
 * 설명 : 릴리즈 목록 조회
 * 기능 : doSearch
 *********************************************************/
function doSearch() {
    w2ui['config_releaseGrid'].load('/config/systemRelease/list');
    $("#iaasTypeListDiv").html("<select disabled='disabled' style = 'width:60%'><option selected='selected' disabled='disabled' value='' style='color:red'>BOSH_CPI 일 경우 선택 가능 합니다.</option></select>");
}

/********************************************************
 * 설명 : Release 브라우저 선택
 * 기능 : openBrowse
 *********************************************************/
function openBrowse(){
    if($('.w2ui-msg-body #browser').attr('disabled') == "disabled") return;    
    $(".w2ui-msg-body input[name='releasePathFile[]']").click();
}

/********************************************************
 * 설명 : 릴리즈 파일 정보
 * 기능 : setReleaseFilePath
 *********************************************************/
function setReleaseFilePath(fileInput){
    var file = fileInput.files;
    var files = $('.w2ui-msg-body #releasePathFile')[0].files;
    $(".w2ui-msg-body input[name='releaseSize']").val(files[0].size);
    $(".w2ui-msg-body input[name=releasePath]").val(file[0].name);
    $(".w2ui-msg-body input[name=releaseFileName]").val(file[0].name);
    
}

/********************************************************
 * 설명 : 릴리즈 등록 확인 버튼
 * 기능 : releaseRegist
 *********************************************************/
function releaseRegist(){
    var releaseInfo = {
            id                  : $(".w2ui-msg-body input[name='id']").val(),
            releaseName         : $(".w2ui-msg-body input[name='releaseName']").val(),
            releaseType         : $(".w2ui-msg-body select[name='releaseType']").val(),
            iaasType            : $(".w2ui-msg-body select[name='releaseIaasType']").val(),
            releaseFileName     : $(".w2ui-msg-body input[name='releaseFileName']").val(),
            releasePathUrl      : $(".w2ui-msg-body input[name='releasePathUrl']").val(),
            releasePathVersion  : $(".w2ui-msg-body input[name='releasePathVersion']").val(),
            fileType            : $(".w2ui-msg-body :radio[name='fileType']:checked").val(),
            overlayCheck        : $(".w2ui-msg-body :checkbox[name='overlay']").is(':checked'),
            releaseSize         : $(".w2ui-msg-body input[name='releaseSize']").val(),
            downloadStatus      : ""
    }
    if(releaseInfo.fileType == "file"){
        var files = $('.w2ui-msg-body #releasePathFile')[0].files;
        if(files[0].size == 0){
             w2alert("릴리즈 파일을 찾을 수 없습니다. 확인해주세요.", "릴리즈 파일 업로드");
             return false;
         }
        //file upload 하기 전 lock 파일 검사
        if(!lockFileSet(files[0].name)){
            return;
        }
    }
    releaseInfoSave(releaseInfo);
}

/********************************************************
 * 설명 : lock 검사
 * 기능 : lockFileSet
 *********************************************************/
var lockFile = false;
function lockFileSet(releaseFile){
    var fileName  ="";
    if( releaseFile.indexOf(".") > -1 ){
        var pathHeader = releaseFile;
         var pathMiddle = releaseFile.lastIndexOf('.');
        var pathEnd = releaseFile.length;
        fileName = releaseFile.substring(pathHeader,pathMiddle)+"-download";
    }
    var message = "현재 다른 플랫폼 설치 관리자가 동일한 릴리즈를 등록 중 입니다."
    lockFile = commonLockFile("<c:url value='/common/deploy/lockFile/"+fileName+"'/>", message);
    return lockFile;
}


/********************************************************
 * 설명 : 공통 릴리즈 정보 저장
 * 기능 : releaseInfoSave
 *********************************************************/
function releaseInfoSave(releaseInfo){
    lock( '등록 중입니다.', true);
    $.ajax({
        type : "POST",
        url : "/config/systemRelease/regist",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(releaseInfo),
        success : function(data, status) {
            w2popup.close();
            releaseInfo.id = data.id; 
            releaseInfo.downloadStatus = data.downloadStatus;
            initView();//재조회
            if(releaseInfo.fileType == 'file'){
                releaseFileUpload(releaseInfo);
            }else{
                socketDwonload(releaseInfo);
            }
        }, error : function(request, status, error) {
            w2popup.unlock();
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message);
        }
    });
}


/********************************************************
 * 설명 : 릴리즈 다운로드
 * 기능 : socketDwonload
 *********************************************************/
var fail_count = 0;
function socketDwonload(releaseInfo){
    lock( '다운로드 중입니다.', true);
    
    var socket = new SockJS("<c:url value='/config/systemRelease/regist/download/releaseDownloading'/>");
    downloadClient = Stomp.over(socket); 
    var status = 0;
    
    var downloadPercentage = 0;
    downloadClient.heartbeat.outgoing = 50000;
    downloadClient.heartbeat.incoming = 0;
    downloadClient.connect({}, function(frame) {
        downloadClient.subscribe('/user/config/systemRelease/regist/download/logs', function(data){
            w2popup.unlock();
             
            status = data.body.split('/')[1]; //recid/percent 중 percent
            id = data.body.split('/')[0]; //recid/percent 중 recid
            
            if( releaseInfo.downloadStatus == 'DOWNLOADING' &&  downloadStatus == ""){
                downloadStatus ="PROCESSING";
                $("#downloading_"+ id).wrap('<div class="btn" id="isExisted_'+ id+'" style="position: relative;width:100px;"></div>');
                $("#downloading_"+id).remove();
                $("#isExisted_" + id).html(progressBarDiv);
            }
            
            console.log("### Download Status ::: " + status.split("%")[0]);
            
            if ( Number(status.split("%")[0]) < 100 ) {
                $("#isExisted_" + id+ " .progress .progress-bar").css({"width": status, "padding-top": "5px", "text-align": "center"}).text( status );
            }else if( status == "done") {
                downloadStatus = '';
                $("#isExisted_" + id).parent().html(completeButton);
                var flag = true;
                w2ui['config_releaseGrid'].records.map(function(obj) {
                    if( id != obj.id && obj.downloadStatus.toUpperCase() == "DOWNLOADING" ){
                        flag= false;
                    }
                });
                if(downloadClient != "" && flag ){
                    downloadClient.disconnect();
                    downloadClient = "";
                }
                doSearch();
            }
        });
        downloadClient.send("<c:url value='/send/config/systemRelease/regist/download/releaseDownloading'/>", {}, JSON.stringify(releaseInfo));
    }, function(frame){
        fail_count ++;
        console.log("request reConnecting.... fail_count: " + fail_count);
        downloadClient.disconnect();
        if( fail_count < 10 ){
            socketDwonload(releaseInfo);    
        }else{
            w2alert("시스템 릴리즈 다운로드에 실패하였습니다. ", "시스템 릴리즈 다운로드")
            fail_count = 0;
            var requestParameter = {
                    id : releaseInfo.id,
                    releaseFileName : releaseInfo.releaseFileName
            };
            deleteRelease(requestParameter);
        }
    });
}

/********************************************************
 * 설명 : 릴리즈 파일 업로드
 * 기능 : releaseFileUpload
 *********************************************************/
function releaseFileUpload(releaseInfo){
    var form = $(".w2ui-msg-body #settingForm");
    var formData = new FormData(form);

    var files = $('.w2ui-msg-body #releasePathFile')[0].files;
    formData.append("file", files[0]);
    formData.append("overlay", releaseInfo.overlayCheck);
    formData.append("id", releaseInfo.id);
    formData.append("fileSize", files[0].size);
    
    if(files[0].size == 0){
         w2alert("릴리즈 파일을 찾을 수 없습니다. 확인해주세요.", "릴리즈 파일 업로드");
         return false;
     }
    
    $.ajax({
        type:'POST',
        url: '/config/systemRelease/regist/upload',
        enctype : 'multipart/form-data',
        dataType: "text",
        async : true,
        data:formData,
        xhr: function() {
            var myXhr = $.ajaxSettings.xhr();
            myXhr.onreadystatechange = function () {}
            myXhr.upload.onprogress = function(e) {
                if (e.lengthComputable) {
                    var max = e.total;
                    var current = e.loaded;
                    var Percentage = parseInt((current * 100) / max);
                    if (Percentage == 1) {
                        if(releaseInfo.downloadStatus == "DOWNLOADED"  ){
                            $("#downloaded_"+ releaseInfo.id).wrap('<div class="btn" id="isExisted_'+releaseInfo.id+'" style="position: relative;width:100px;"></div>');
                            $("div").remove(releaseInfo.id);
                        } else if(  releaseInfo.downloadStatus == 'DOWNLOADING'  ){
                            $("#downloading_"+ releaseInfo.id).wrap('<div class="btn" id="isExisted_'+releaseInfo.id+'" style="position: relative;width:100px;"></div>');
                            $("div").remove(releaseInfo.id);
                        }
                        $("#isExisted_" + releaseInfo.id).html(progressBarDiv);
                    } else if (Percentage == 100){ 
                        Percentage = 99;
                    };
                    $("#isExisted_"+ releaseInfo.id + " .progress .progress-bar").css(
                            { "width" : Percentage + "%", "padding-top" : "5px", "text-align" : "center"}).text(Percentage + "%");
                }
            }
            return myXhr;
        },
        cache : false,
        contentType : false,
        processData : false,
        success : function(data) {
            $("#isExisted_" + releaseInfo.id + " .progress .progress-bar").css({ "width" : "100%", "padding-top" : "5px", "text-align" : "center" }).text("100%");
            doSearch();
        },
        error : function(data) {}
    });
 }

/********************************************************
 * 설명 : 릴리즈 삭제 데이터 셋팅
 * 기능 : deletePop
 *********************************************************/
function deletePop(record) {
    var requestParameter = {
        id : record.id,
        releaseFileName : record.releaseFileName
    };
    deleteRelease(requestParameter);
}
    
/********************************************************
 * 설명 : 릴리즈 삭제 요청
 * 기능 : deleteRelease
*********************************************************/
function deleteRelease(requestParameter){
    $.ajax({
        type : "DELETE",
        url : "/config/systemRelease/delete",
        data : JSON.stringify(requestParameter),
        contentType : "application/json",
        success : function(data, status) {
            if( downloadClient != "") {
                downloadClient.disconnect();
                downloadClient ="";
            }
            initView();
            w2ui['config_releaseGrid'].reset();
        },
        error : function(request, status, error) {
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "릴리즈 삭제");
            doSearch();
        }
    });
}

/********************************************************
 * 설명 : 그리드 재조회
 * 기능 : gridReload
 *********************************************************/
function gridReload() {
    w2ui['config_releaseGrid'].reset();
    doSearch();
}

/********************************************************
 * 설명 : 팝업 창을 닫을 경우
 * 기능 : initSetting
 *********************************************************/
function initSetting() {
     if(  downloadClient != "") {
            downloadClient.disconnect();
            downloadClient ="";
    }
     doSearch(); 
}

/********************************************************
 * 설명 : 다른 페이지 이동 시 호출
 * 기능 : clearMainPage
 *********************************************************/
function clearMainPage() {
    $().w2destroy('config_releaseGrid');
}
    
/********************************************************
 * 설명 : Lock 실행
 * 기능 : clearMainPage
 *********************************************************/
function lock(msg) {
    w2popup.lock(msg, true);
}

/********************************************************
 * 설명 :  화면 변환 시
*********************************************************/
$(window).resize(function() {
    setLayoutContainerHeight();
});
</script>
<style type="text/css">
#releasePathFile { display:none; } 
</style>
<div id="main">
    <div class="page_site">환경설정 및 관리 > <strong>릴리즈 관리</strong></div>
    
    <!-- OpenPaaS 릴리즈 목록-->
    <div class="pdt20">
        <div class="title fl">릴리즈 목록</div>
        <div class="fr"> 
            <!-- Btn -->
            <sec:authorize access="hasAuthority('CONFIG_RELEASE_REGIST')">
            <span id="doRegist" class="btn btn-primary" style="width:120px" >등록</span>
            </sec:authorize>
            <sec:authorize access="hasAuthority('CONFIG_RELEASE_DELETE')">
            <span id="doDelete" class="btn btn-danger" style="width:120px" >삭제</span>
            </sec:authorize>
            <!-- //Btn -->
        </div>
    </div>
    <!-- 릴리즈 grid -->
    <div id="config_releaseGrid" style="width:100%; height:718px"></div>    
        
    <!-- 릴리즈 등록 팝업 -->
    <div id="regPopupDiv" hidden="true">
        <input name="releaseSize" type="hidden" />
        <input name="id" type="hidden" />
        <form id="settingForm" action="POST">
            <div class="w2ui-page page-0">
	            <div class="panel panel-info" style="margin-top:5px;" >
	                <div class="panel-heading"><b>릴리즈 기본 정보</b></div>
	                <div class="panel-body">
	                    <div class="w2ui-field">
	                        <label style="width:30%;text-align: left;padding-left: 20px;">릴리즈 명</label>
	                        <div style="width: 70%;">
	                            <input name="releaseName" type="text" maxlength="100" style="width: 60%" required="required" placeholder="릴리즈 명을 입력하세요." />
	                        </div>
	                    </div>
	                    <div class="w2ui-field">
	                        <label style="width:30%;text-align: left;padding-left: 20px;">릴리즈 유형</label>
	                        <div style="width: 70%" id="releaseTypeDiv"></div>
	                    </div>
	                    <div class="w2ui-field">
	                        <label style="width:30%;text-align: left;padding-left: 20px;">IaaS 유형</label>
	                        <div style="width: 70%" id="iaasTypeListDiv"></div>
	                    </div>
	                </div>
	            </div>
	        </div>
	        <div class="w2ui-page page-0">
	            <div class="panel panel-info" style='margin: 10px 0;'>
	                <div class="panel-heading"><b>릴리즈 다운 유형</b></div>
	                <div class="panel-body">
	                    <div class="w2ui-field">
	                        <input type="radio" name="fileType" id="fileTypLocal" value="file" style="float:left; margin-left:15px;" onchange='setRegistType(this.value);'/>
	                        <label for="fileTypLocal" style="width:25%;text-align:left;">&nbsp;&nbsp;로컬에서 선택</label>
	                        <div style="width: 70%">
	                            <span>
	                                <input type="file" name="releasePathFile[]" id="releasePathFile" onchange="setReleaseFilePath(this);" hidden="true"/>
	                                <input style="width: 60%" type="text" id="releaseFileName" name="releaseFileName" style="width:53%;" readonly  onClick="openBrowse();" placeholder="업로드할 릴리즈 파일을 선택하세요."/>
	                            <span class="btn btn-primary" id = "browser" onClick="openBrowse();" disabled style="height: 25px; padding: 1px 7px 7px 6px;">Browse </span>&nbsp;&nbsp;&nbsp;
	                           </span>
	                        </div>
	                    </div>
	                    <div class="w2ui-field" style="margin: 8px 0px 0px 0px;">
	                        <input type="radio" name="fileType" id="fileTypeUrl" style="float:left; margin-left:15px;" value="url" onchange='setRegistType(this.value);'/>
	                        <label for="fileTypeUrl" for="fileTypeUrl" style="width:25%;text-align: left;">
	                            &nbsp;&nbsp;릴리즈 Url
	                            <span class="glyphicon glyphicon glyphicon-question-sign release-info" style="cursor:pointer;font-size: 14px;color: #157ad0;" data-toggle="popover"  data-trigger="click" data-html="true" title="<b>공개 릴리즈 참조 사이트</b>"></span>
	                        </label>
	                        <div style="width: 70%">
	                            <input style="width: 60%" type="text" id="releasePathUrl" name="releasePathUrl" style="width:53%;" readonly   placeholder="릴리즈 다운로드 Url을 입력 하세요."/>
	                        </div>
	                    </div>
	                    <div class="w2ui-field" style="margin: 8px 0px 0px 0px;">
	                        <input type="radio" name="fileType" id="fileTypeVersion" value="version"  style="float:left; margin-left:15px;" onchange='setRegistType(this.value);' />
	                        <label for="fileTypeVersion" style="width:25%;text-align: left;">&nbsp;&nbsp;릴리즈 Version</label>
	                        <div style="width: 70%">
	                            <input style="width: 60%"type="text" id="releasePathVersion" name="releasePathVersion" style="width:53%;" readonly placeholder="릴리즈 다운로드 버전을 입력 하세요."/>
	                        </div>
	                    </div>
	                    <div class="w2ui-field" style="margin: 8px 0px 0px 0px; color:#666">
	                        <label style="width:30%;text-align: left;padding-left: 15px;color:#3f51b5">&nbsp;&nbsp;파일 덮어 쓰기
	                            <input name="overlay" type="checkbox" value="overlay" checked/>&nbsp;
	                        </label>
	                    </div>
	                </div>
	            </div>
            </div>
        </form>
        <div id="regPopupBtnDiv" hidden="true">
            <button class="btn" id="registBtn" onclick="$('#settingForm').submit();">등록</button>
            <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
        </div>
    </div>
    <!-- //릴리즈 등록 팝업 -->
</div>
<script>
$(function() {
    $.validator.addMethod("sqlInjection", function(value, element, params) {
        return checkInjectionBlacklist(params);
      },text_injection_msg);
    
    $("#settingForm").validate({
        ignore : "",
        onfocusout: true,
        rules: {
            releaseName : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='releaseName']").val() );
                }, sqlInjection : function(){
                    return $(".w2ui-msg-body input[name='releaseName']").val();
                }
            }, releaseFileName: { 
                required: function(){
                	if( $(".w2ui-msg-body input:radio[name='fileType']:checked").val() == "file" ){
                		return checkEmpty( $(".w2ui-msg-body input[name='releaseFileName']").val());
                	}else return false;
                }
            }, releasePathUrl: { 
                required: function(){
                	if( $(".w2ui-msg-body input:radio[name='fileType']:checked").val() == "url" ){
                        return checkEmpty( $(".w2ui-msg-body input[name='releasePathUrl']").val());
                    }else return false;
                }, sqlInjection : function(){
                    return $(".w2ui-msg-body input[name='releasePathUrl']").val();
                }
            }, releasePathVersion: { 
                required: function(){
                	if( $(".w2ui-msg-body input:radio[name='fileType']:checked").val() == "version" ){
                        return checkEmpty( $(".w2ui-msg-body input[name='releasePathVersion']").val());
                    }else return false;
                }, sqlInjection : function(){
                    return $(".w2ui-msg-body input[name='releasePathVersion']").val();
                }
            }
        }, messages: {
            releaseName: { 
                 required:  "릴리즈 명" + text_required_msg
                , sqlInjection : text_injection_msg
            }, releaseFileName: { 
                required:  "릴리즈 파일"+text_required_msg
            }, releasePathUrl: { 
                required:  "릴리즈 URL"+text_required_msg
                ,sqlInjection : text_injection_msg
            }, releasePathVersion: { 
                required:  "릴리즈 버전"+text_required_msg
                ,sqlInjection : text_injection_msg
            }
        },unhighlight: function(element) {
            setSuccessStyle(element);
        },errorPlacement: function(error, element) {
            //do nothing
        }, invalidHandler: function(event, validator) {
            var errors = validator.numberOfInvalids();
            if (errors) {
                setInvalidHandlerStyle(errors, validator);
            }
        }, submitHandler: function (form) {
            releaseRegist();
        }
    });
});
</script>