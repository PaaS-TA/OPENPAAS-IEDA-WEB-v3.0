<%
/* =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2017.05       이동현        화면 개선 및 코드 버그 수정
 * 2017.08       지향은        인프라 환경 추가(Google)
 * 2018.02       배병욱        인프라 환경 추가(Azure)
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix ="spring" uri = "http://www.springframework.org/tags" %>
<script type="text/javascript">
//private common variable
var downloadClient = "";
var downloadStatus = "";
var osVersionArray = [];
var stemcellArray = [];
var completeButton = '<div><div class="btn btn-success btn-xs" style="width:100px; padding:3px;">Downloaded</div></div>';
var downloadingButton = '<div class="btn btn-info btn-xs" style="width:100px;">Downloading</div>';
var progressBarDiv = '<div class="progress">';
    progressBarDiv += '<div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" >';
    progressBarDiv += '</div></div>';
    
/*message.properties*/
var delete_confirm_msg = '<spring:message code="common.popup.delete.message"/>';//을(를) 삭제하시겠습니까?
var os_type_code = '<spring:message code="common.code.osType.code.parent"/>';//200
var iaas_type_code = '<spring:message code="common.code.iaasType.code.parent"/>';//100
var os_version_code = '<spring:message code="common.code.osVersion.code.subGroup"/>';//201
var text_required_msg='<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
var text_injection_msg='<spring:message code="common.text.validate.sqlInjection.message"/>';//입력하신 값은 입력하실 수 없습니다.

$(function() {
/********************************************************
 * 설명 :  스템셀 목록 조회 Grid 생성
 *********************************************************/
 $('#config_opStemcellsGrid').w2grid({
    name: 'config_opStemcellsGrid',
    show: {selectColumn: true, footer: true},
    multiSelect: false,
    msgAJAXerror : '스템셀 조회 실패',
    method: 'GET',
    style: 'text-align:center',
    columns:[
         {field: 'recid', caption: 'recid', hidden: true}
        ,{field: 'id', caption: '아이디', hidden: true} 
        ,{field: 'downloadStatus', caption: '다운로드 여부', hidden:true}
        ,{field: 'stemcellName', caption: '스템셀 명', size: '15%'}
        ,{field: 'os', caption: 'Os 유형', size: '10%'}            
        ,{field: 'osVersion', caption: 'Os 버전', size: '10%'}
        ,{field: 'iaas', caption: 'IaaS', size: '10%', sortable: true}
        ,{field: 'stemcellFileName', caption: '스템셀 파일명', size: '45%', style: 'text-align:left'}
        ,{field: 'stemcellVersion', caption: '스템셀 버전', size: '10%'}
        ,{field: 'size', caption: '파일 크기', size: '10%'}
        ,{field: 'isExisted', caption: '다운로드 여부', size: '20%',
            render: function(record) {
                if ( record.downloadStatus == 'DOWNLOADED'  ){
                    return '<div class="btn btn-success btn-xs" id= "downloaded_'+record.id+'" style="width:100px;">Downloaded</div>';
                }else if(record.downloadStatus == 'DOWNLOADING'){ //다른 사용자가다운로드 중일 경우
                    return '<div class="btn btn-info btn-xs" id= "downloading_'+record.id+'" style="width:100px;">Downloading</div>';
                } else{
                    return '<div class="btn" id="isExisted_'+record.id+'" style="position: relative;width:100px;"></div>';
                }
            }
        }
    ],
    onSelect: function(event) {
        event.onComplete = function() {
                $('#doregist').attr('disabled', false);
                $('#doDelete').attr('disabled', false);
        }
    },
    onUnselect: function(event) {
        var grid = this;
        event.onComplete = function() {
            $('#doRegist').attr('disabled', false);
            $('#doDelete').attr('disabled', true);
        }
    }, onLoad:function(event){
        if(event.xhr.status == 403){
            location.href = "/abuse";
            event.preventDefault();
        }
        
    }, onError:function(evnet){

    }
});
    
/**************************************************************
 * 설명 : 스템셀 등록 버튼 클릭
 **************************************************************/
$("#doRegist").click(function(){
    w2popup.open({
        title   : "<b>스템셀 등록</b>",
        width   : 675,
        height  : 548,
        modal   : true,
        body    : $("#regPopupDiv").html(),
        buttons : $("#regPopupBtnDiv").html(),
        onClose : function(event){
            w2ui['config_opStemcellsGrid'].clear();
            initView();
        }
    });
    setCommonCode('<c:url value="/common/deploy/codes/parent/"/>'+ os_type_code, 'os');
    setCommonCode('<c:url value="/common/deploy/codes/parent/"/>' + iaas_type_code, 'iaas');
    setCommonCode('<c:url value="/common/deploy/codes/parent/' + os_type_code + '/subcode/' + os_version_code+'"/>', 'osVersion');
    $('.w2ui-msg-body input:radio[name=fileType]:input[value=version]').attr("checked", true);
    $('.w2ui-msg-body input:text[name=stemcellPathVersion]').attr("readonly", false);
    $('[data-toggle="popover"]').popover();
     //스템셀 버전 정보
     $(".w2ui-msg-body .stemcell-info").attr('data-content', "http://bosh.cloudfoundry.org/stemcells/");
     
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
     
 /**************************************************************z
  * 설명 : 스템셀 삭제 버튼 클릭
  **************************************************************/
 $('#doDelete').click(function(){
     if($("#doDelete").attr('disabled') == "disabled") return;
     var selected = w2ui['config_opStemcellsGrid'].getSelection();
     var record = w2ui['config_opStemcellsGrid'].get(selected);
     var message = "";
    
     if ( record.stemcellFileName  && record.downloadStatus != "DOWNLOADING" ){
         message = "스템셀 (파일명 : " + record.stemcellFileName + ") " + delete_confirm_msg;
     }else{
         message = "현재 다운로드 중입니다. <br/> 스템셀 (파일명 : " + record.stemcellFileName + ")" + delete_confirm_msg;
     }
     w2confirm({
         title        : "<b>스템셀 삭제</b>",
         msg          : message,
         yes_text     : "확인",
         yes_callBack : function(event){
             deletePop(record);
         },
         no_text : "취소",
         no_callBack    : function(){
             w2ui['config_opStemcellsGrid'].clear();
             initView();
         }
     });
 });
     
//  화면 초기화에 필요한 데이터 요청
initView();
});

/**************************************************************
 * 설명 : 공통코드 설정
 * 기능 : setCommonCode
 **************************************************************/
function setCommonCode(url, id) {
    $.ajax({
        type : "GET",
        url : url,
        success : function(data) {
            stemcellArray = new Array();
            if(data[0].parentCode == 200 && checkEmpty(data[0].subGroupCode)){
                var osList = "";
                var iaasListSelect = "";
                var osVersionList = "";
                data.map(function(obj) {
                    stemcellArray.push(obj.codeName);
                    osVersionArray.push(obj);
                });
                osList="<select style='width:60%' onchange='setOsVersion(this.value);' name ='osList'>";
                 for(var i=0; i<stemcellArray.length; i++){
                    osList+="<option value="+stemcellArray[i]+">"+stemcellArray[i]+"</option>";
                 }
                 osList+="</select>";
                 $(".w2ui-msg-body #osListDiv").html(osList);
            }else if(data[0].parentCode == 100){
                data.map(function(obj) {
                    stemcellArray.push(obj.codeName);
                });
                iaasListSelect="<select style='width:60%' name ='iaasList' onchange='setLightCheckbox(this.value);'>";
                 for(var i=0; i<stemcellArray.length; i++){
                     iaasListSelect+="<option value="+stemcellArray[i]+">"+stemcellArray[i]+"</option>";
                 }
                 iaasListSelect+="</select'>";
                 $(".w2ui-msg-body #iaasListDiv").html(iaasListSelect);
            }else if(data[0].parentCode == 200 && !checkEmpty(data[0].subGroupCode)){
                data.map(function(obj) {
                    stemcellArray.push(obj.codeName);
                });
                osVersionList="<select style='width:60%' name='osVersionList'>";
                for(var i=0; i<stemcellArray.length; i++){
                    osVersionList+="<option value="+stemcellArray[i]+">"+stemcellArray[i]+"</option>";
                 }
                 osVersionList+="</select>";
                 $(".w2ui-msg-body #osVersionDiv").html(osVersionList);
            }
        },error : function(xhr, status) {
            if(xhr.status==403){
                location.href = "/abuse";
            }else{
                var errorResult = JSON.parse(request.responseText);
                w2alert(errorResult, "스템셀 조회");
            }
        }
    });
}
/**************************************************************
 * 설명 : IaaS 유형 값 change 이벤트
 * 기능 : setLightCheckbox
 **************************************************************/
function setLightCheckbox(iaasValue){
    if(iaasValue == "AWS" || iaasValue == "GOOGLE" || iaasValue == "AZURE"){
        if($('.w2ui-msg-body input:radio[name=fileType]:input[value=version]').is(':checked')==true){
            $('.w2ui-msg-body input:checkbox[name=light]').attr("disabled", false);
        }
    }else{
        $('.w2ui-msg-body input:checkbox[name=light]').attr("disabled", true);
        $('.w2ui-msg-body input:checkbox[name=light]').attr('checked',false);
    }
    
}


/**************************************************************
 * 설명 : Os 유형 change 이벤트
 * 기능 : setOsVersion
 **************************************************************/
function setOsVersion(value){
    var subCodeValue = 0;
     for(var i=0;i<osVersionArray.length;i++){
        if(value == osVersionArray[i].codeName){
            subCodeValue = osVersionArray[i].codeValue;
            if(subCodeValue == 203){
                $('.w2ui-msg-body #lightVerChk').fadeOut(100);
                //$(".w2ui-msg-body :checkbox[name='light']").attr("disabled", true);
                $(".w2ui-msg-body :checkbox[name='light']").prop("checked", true);
            }else{
                $('.w2ui-msg-body #lightVerChk').fadeIn(100);
                //$(".w2ui-msg-body :checkbox[name='light']").attr("disabled", false);
                $(".w2ui-msg-body :checkbox[name='light']").prop("checked", false);
            }
        }
    }
    setCommonCode('<c:url value="/common/deploy/codes/parent/"/>' + os_type_code + '<c:url value="/subcode/"/>' + subCodeValue, 'osVersion');
}
/**************************************************************
 * 설명 : 스템셀 다운 유형 change 이벤트
 * 기능 : setRegistType
 **************************************************************/
function setRegistType(value){
     $(".w2ui-msg-body :radio[name='fileType'][value='"+value+"']").prop("checked", true);

    if(value == "file"){
        $('.w2ui-msg-body #browser').attr("disabled", false);
        $('.w2ui-msg-body input:text[name=stemcellPathUrl]').attr("readonly", true);
        $('.w2ui-msg-body input:text[name=stemcellPathVersion]').attr("readonly", true);
        $('.w2ui-msg-body input:checkbox[name=light]').attr("disabled", true);
        $('.w2ui-msg-body input:text[name=stemcellPathUrl]').val("");
        $('.w2ui-msg-body input:text[name=stemcellPathVersion]').val("");
        $('.w2ui-msg-body input:checkbox[name=light]').attr('checked',false)
        $(".w2ui-msg-body input[name=stemcellPathUrl]").css("borderColor","#777");
        $(".w2ui-msg-body input[name=stemcellPathUrl]").parent().find("p").remove();
        $(".w2ui-msg-body input[name=stemcellPathVersion]").parent().find("p").remove();
        $(".w2ui-msg-body input[name=stemcellPathVersion]").css("borderColor","#777");
    }else if(value == "url"){
        $('.w2ui-msg-body input:text[name=stemcellPathUrl]').attr("readonly", false);
        $('.w2ui-msg-body #browser').attr("disabled", true);
        $('.w2ui-msg-body input:text[name=stemcellPathVersion]').attr("readonly", true);
        $('.w2ui-msg-body input:checkbox[name=light]').attr("disabled", true);
        $('.w2ui-msg-body input:text[name=stemcellPathVersion]').val("");
        $('.w2ui-msg-body input:checkbox[name=light]').attr('checked',false)
        $('.w2ui-msg-body input:text[name=stemcellPathFileName]').val("");
        $(".w2ui-msg-body input[name=stemcellPathFileName]").parent().parent().find("p").remove();
        $(".w2ui-msg-body input[name=stemcellPathFileName]").css("borderColor","#777");
        $(".w2ui-msg-body input[name=stemcellPathVersion]").parent().find("p").remove();
        $(".w2ui-msg-body input[name=stemcellPathVersion]").css("borderColor","#777");
    }else if(value == "version"){
            $('.w2ui-msg-body input:text[name=stemcellPathVersion]').attr("readonly", false);
            $('.w2ui-msg-body #browser').attr("disabled", true);
            $('.w2ui-msg-body input:text[name=stemcellPathUrl]').attr("readonly", true);
            $('.w2ui-msg-body input:checkbox[name=light]').attr("disabled", true);
            $('.w2ui-msg-body input:text[name=stemcellPathUrl]').val("");
            $('.w2ui-msg-body input:text[name=stemcellPathFileName]').val("");
            if($(".w2ui-msg-body select[name='iaasList']").val()=="AWS" || 
               $(".w2ui-msg-body select[name='iaasList']").val()=="GOOGLE" || 
               $(".w2ui-msg-body select[name='iaasList']").val()=="AZURE"){
                    $('.w2ui-msg-body input:checkbox[name=light]').attr("disabled", false);
            }
            $(".w2ui-msg-body input[name=stemcellPathFileName]").parent().parent().find("p").remove();
            $(".w2ui-msg-body input[name=stemcellPathFileName]").css("borderColor","#777");
            $(".w2ui-msg-body input[name=stemcellPathUrl]").css("borderColor","#777");
            $(".w2ui-msg-body input[name=stemcellPathUrl]").parent().find("p").remove();
    }else{
        w2alert(errorResult, "잘못 된 요청 입니다.");
    }
}
/**************************************************************
 * 설명 : 초기 스템셀 조회
 * 기능 : doSearch
 **************************************************************/
function doSearch(){
    w2ui['config_opStemcellsGrid'].load('/config/stemcell/list');
}
/********************************************************
 * 설명 : 스템셀 파일 정보
 * 기능 : setstemcellFilePath
 *********************************************************/
function setstemcellFilePath(fileInput){
    var file = fileInput.files;
    var files = $('.w2ui-msg-body #stemcellPathFile')[0].files;
    $(".w2ui-msg-body input[name='stemcellSize']").val(files[0].size);
    $(".w2ui-msg-body input[name=stemcellPath]").val(files[0].name);
    $(".w2ui-msg-body #stemcellPathFileName").val(files[0].name);
    
}
/********************************************************
 * 설명 : 스템셀 브라우저 선택
 * 기능 : openBrowse
 *********************************************************/
function openBrowse(){
    if($('.w2ui-msg-body #browser').attr('disabled') == "disabled") return;    
    $(".w2ui-msg-body input[name='stemcellPathFile[]']").click();
}

/**************************************************************
 * 설명 : 스템셀 정보 저장 Array 설정
 * 기능 : stemcellRegist
 **************************************************************/
function stemcellRegist(){
    var stemcellInfo = {
            id               : $(".w2ui-msg-body input[name='id']").val(),
            stemcellName     : $(".w2ui-msg-body input[name='stemcellName']").val(),
            stemcellFileName : $(".w2ui-msg-body input[name=stemcellPathFileName]").val(),
            stemcellUrl      : $(".w2ui-msg-body input[name='stemcellPathUrl']").val(),
            stemcellVersion  : $(".w2ui-msg-body input[name='stemcellPathVersion']").val(),
            osName           : $(".w2ui-msg-body select[name='osList']").val(),
            osVersion        : $(".w2ui-msg-body select[name='osVersionList']").val(),
            iaasType         : $(".w2ui-msg-body select[name='iaasList']").val(),
            fileType         : $(".w2ui-msg-body :radio[name='fileType']:checked").val(), 
            overlayCheck     : $(".w2ui-msg-body y:checkbox[name='overlay']").is(':checked'),
            stemcellSize     : $(".w2ui-msg-body input[name='stemcellSize']").val(),
            light            : $(".w2ui-msg-body :checkbox[name='light']").is(':checked'),
            downloadStatus   : ""
    }
    
    if(stemcellInfo.fileType == "file"){
        if($(".w2ui-msg-body input[name='stemcellSize']").val() == 0){
             w2alert("스템셀 파일을 찾을 수 없습니다. 확인해주세요.", "스템셀 파일 업로드");
             return false;
         }
    }
    stemcellInfoSave(stemcellInfo);
}
/**************************************************************
 * 설명 : 스템셀 정보 저장
 * 기능 : stemcellInfoSave
 **************************************************************/
function stemcellInfoSave(stemcellInfo){
    lock( '등록 중입니다.', true);
    $.ajax({
        type : "POST",
        url : "/config/stemcell/regist/savestemcell/N",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(stemcellInfo),
        success : function(data, status) {
            w2popup.close();
            stemcellInfo.id = data.id;
            stemcellInfo.downloadStatus = data.downloadStatus;
            stemcellInfo.stemcellFileName = data.stemcellFileName;
            initView();//재조회
            if(stemcellInfo.fileType == "file"){
                stemcellFileUpload(stemcellInfo);        
            }else if(stemcellInfo.fileType == "url"){
                stemcellFileDownload(stemcellInfo);
            }else if (stemcellInfo.fileType == "version"){
                stemcellFileDownload(stemcellInfo);
            }else{
                w2alert("잘못된 스템셀 등록 방식 입니다.");
            }
        }, error : function(request, status, error) {
            w2popup.unlock();
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message);
        }
    });
}
/**************************************************************
 * 설명 : 로컬에 있는 스템셀 업로드
 * 기능 : stemcellFileUpload
 **************************************************************/
function stemcellFileUpload(stemcellInfo){
    var form = $(".w2ui-msg-body #settingForm");
    var formData = new FormData(form);
    var files = $('.w2ui-msg-body #stemcellPathFile')[0].files;
    formData.append("file", files[0]);
    formData.append("overlay", stemcellInfo.overlayCheck);
    formData.append("id", stemcellInfo.id);
    formData.append("fileSize", files[0].size);
    
    if(stemcellInfo.id == 'undefined' || stemcellInfo.id==null || stemcellInfo.id=="" ){
        return;
    }
    
    if(files[0].size == 0){
         w2alert("스템셀 파일을 찾을 수 없습니다. 확인해주세요.", "스템셀 파일 업로드");
         return false;
     }
    
    $.ajax({
        type     :'POST',
        url      : '/config/stemcell/regist/upload',
        enctype  : 'multipart/form-data',
        dataType : "text",
        async    : true,
        data     :formData,
        xhr      : function() {
            var myXhr = $.ajaxSettings.xhr();
            myXhr.onreadystatechange = function () {}
            myXhr.upload.onprogress = function(e) {
                if (e.lengthComputable) {
                    var max = e.total;
                    var current = e.loaded;
                    var Percentage = parseInt((current * 100) / max);
                    if (Percentage == 1) {
                        if(stemcellInfo.downloadStatus == "DOWNLOADED"  ){
                            $("#downloaded_"+ stemcellInfo.id).wrap('<div class="btn" id="isExisted_'+stemcellInfo.id+'" style="position: relative;width:100px;"></div>');
                            $("div").remove(stemcellInfo.id);
                        } else if(  stemcellInfo.downloadStatus == 'DOWNLOADING'  ){
                            $("#downloading_"+ stemcellInfo.id).wrap('<div class="btn" id="isExisted_'+stemcellInfo.id+'" style="position: relative;width:100px;"></div>');
                            $("div").remove(stemcellInfo.id);
                        }
                        $("#isExisted_" + stemcellInfo.id).html(progressBarDiv);
                    } else if (Percentage == 100){ 
                        Percentage = 99;
                    };
                    $("#isExisted_"+ stemcellInfo.id + " .progress .progress-bar")
                    .css({ "width" : Percentage + "%", "padding-top" : "5px", "text-align" : "center"}).text(Percentage + "%");
                }
            }
            return myXhr;
        },
        cache : false,
        contentType : false,
        processData : false,
        success : function(data) {
            $("#isExisted_" + stemcellInfo.id + " .progress .progress-bar")
            .css({ "width" : "100%", "padding-top" : "5px", "text-align" : "center" }).text("100%");
            doSearch();
        },
        error : function(data) {
        }
    });
}
/**************************************************************
 * 설명 : 원격지에 있는 스템셀 다운로드
 * 기능 : stemcellFileDownload
 **************************************************************/
var fail_count = 0;
function stemcellFileDownload(stemcellInfo){
    lock( '다운로드 중입니다.', true);
    var socket = new SockJS("<c:url value='/config/stemcell/regist/stemcellDownloading'/>");
    downloadClient = Stomp.over(socket); 
    var status = 0;
    
    var downloadPercentage = 0;
    downloadClient.heartbeat.outgoing = 50000;
    downloadClient.heartbeat.incoming = 0;
    downloadClient.connect({}, function(frame) {
        downloadClient.subscribe('/user/config/stemcell/regist/download/logs', function(data){
            w2popup.unlock();
            status = data.body.split('/')[1]; //recid/percent 중 percent
            id = data.body.split('/')[0]; //recid/percent 중 recid
            if(  stemcellInfo.downloadStatus == 'DOWNLOADING' &&  downloadStatus == ""){
                downloadStatus ="PROCESSING";
                $("#downloading_"+id).wrap('<div class="btn" id="isExisted_'+ id+'" style="position: relative;width:100px;"></div>');
                $("#downloading_"+id).remove();
                $("#isExisted_" + id).html(progressBarDiv);
            }
            
            console.log("### Download Status ::: " + status.split("%")[0]);
            
            if ( Number(status.split("%")[0]) < 100 ) {
                $("#isExisted_" + id+ " .progress .progress-bar")
                    .css({"width": status , "padding-top": "5px" , "text-align": "center"})
                    .text( status );
            }else if( status == "done") {
                downloadStatus = '';
                $("#isExisted_" + id).parent().html(completeButton);
                
                var flag = true;
                w2ui['config_opStemcellsGrid'].records.map(function(obj) {
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
        downloadClient.send("<c:url value='/send/config/stemcell/regist/stemcellDownloading'/>", {}, JSON.stringify(stemcellInfo));
    }, function(frame){
        fail_count ++;
        downloadClient.disconnect();
        if( fail_count < 10 ){
            socketDwonload(stemcellInfo);    
        }else{
            w2alert("스템셀 다운로드에 실패하였습니다. ", "스템셀 다운로드")
            fail_count = 0;
            var requestParameter = {
                    id : stemcellInfo.id,
                    stemcellFileName : stemcellInfo.stemcelleFileName
                };
        }
    });
}
/**************************************************************
 * 설명 : 스템셀 삭제 버튼 클릭 후 확인 팝업 화면
 * 기능 : deletePop
 **************************************************************/
function deletePop(record){
    var requestParameter = {
            id : record.id,
            stemcellFileName : record.stemcellFileName
    };
    
    $.ajax({
        type : "DELETE",
        url : "/config/stemcell/delete",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(requestParameter),
        success : function(data, status) {
            if( downloadClient != ""){
            	console.log(downloadClient);
                downloadClient.disconnect();
                downloadClient = "";
            }
            w2ui['config_opStemcellsGrid'].clear();
            initView();
        }, error : function(request, status, error) {
            w2popup.unlock();
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message);
            w2ui['config_opStemcellsGrid'].clear();
            doSearch();
        }
    });
}
/********************************************************
 * 설명 : 그리드 재조회
 * 기능 : gridReload
 *********************************************************/
function gridReload() {
    w2ui['config_opStemcellsGrid'].reset();
    doSearch();
}
/********************************************************
 * 설명 : 다른 페이지 이동 시 호출
 * 기능 : clearMainPage
 *********************************************************/
function clearMainPage() {
    $().w2destroy('config_opStemcellsGrid');
}
 /********************************************************
 * 설명 : Lock 실행
 * 기능 : clearMainPage
 *********************************************************/
function lock(msg) {
    w2popup.lock(msg, true);
}
/********************************************************
 * 설명 : 화면 변환 시
 *********************************************************/
$(window).resize(function() {
    setLayoutContainerHeight();
});
/**************************************************************
 * 설명 : 정보 조회
 * 기능 : initView
 **************************************************************/
function initView() {
    $('#doDelete').attr('disabled', true);
    doSearch();
}
</script>
<style type="text/css">
#stemcellPathFile { display:none; } 
</style>
<div id="main">
    <div class="page_site">환경설정 및 관리 > <strong>스템셀 관리</strong></div>
    <!-- OpenPaaS 스템셀 목록-->
    <div class="pdt20">
        <div class="title fl">스템셀 목록</div>
        <div class="fr"> 
            <!-- Btn -->
            <sec:authorize access="hasAuthority('CONFIG_STEMCELL_REGIST')">
            <span id="doRegist" class="btn btn-primary" style="width:120px" >등록</span>
            </sec:authorize>
            <sec:authorize access="hasAuthority('CONFIG_STEMCELL_DELETE')">
            <span id="doDelete" class="btn btn-danger" style="width:120px" >삭제</span>
            </sec:authorize>
            <!-- //Btn -->
        </div>
    </div>
    <!-- 그리드 영역 -->
    <div id="config_opStemcellsGrid" style="width:100%; height:718px"></div>
</div>
<!-- 스템셀 등록 팝업 -->
<div id="regPopupDiv" hidden="true">
    <input name="stemcellSize" type="hidden" />
    <input name="id" type="hidden" />
    <form id="settinfForm">
        <div class="w2ui-page page-0">
            <div class="panel panel-info" style="margin-top:5px;"> 
                <div class="panel-heading"><b>스템셀 정보</b></div>
                <div class="panel-body" style="">
                   <div class="w2ui-field">
                       <label style="width:30%;text-align: left;padding-left: 20px;">스템셀 명</label>
                       <div style="width: 70%;">
                           <input type="text" name="stemcellName" maxlength="100" style="width: 60%" placeholder="스템셀 명을 입력 하세요."  />
                       </div>
                   </div>
                   <div class="w2ui-field" >
                       <label style="width:30%;text-align: left; padding-left: 20px;">IaaS 유형</label>
                       <div style="width: 70%" id="iaasListDiv">
                       </div>
                   </div>
                   <div class="w2ui-field">
                       <label style="width:30%;text-align: left; padding-left: 20px;">OS 유형</label>
                       <div style="width: 70%" id="osListDiv">
                       </div>
                   </div>
                   <div class="w2ui-field">
                       <label style="width:30%;text-align:left;padding-left: 20px;">OS 버전</label>
                       <div style="width: 70%" id="osVersionDiv">
                       </div>
                   </div>
                </div>
            </div>
        </div>
        <div class="w2ui-page page-0">
            <div class="panel panel-info" style='margin: 15px 0;' > 
               <div class="panel-heading"><b>스템셀 다운 유형</b></div>
               <div class="panel-body" style="">
                    <div class="w2ui-field" style="margin: 8px 0px 0px 0px;" >
                        <input type="radio" id="fileTypLocal" name="fileType" value="file" style="float:left; margin-left:15px;" onchange='setRegistType(this.value);'/>
                        <label for="fileTypLocal" style="width:25%;text-align: left;" >&nbsp;&nbsp;로컬에서 선택</label>
                        <div style="width:70%;" >
                            <span>
                            <input type="file" name="stemcellPathFile[]"  id="stemcellPathFile" onchange="setstemcellFilePath(this);" hidden="true"/>
                            <input type="text" style="width:60%;" id="stemcellPathFileName"  name="stemcellPathFileName" readonly  onClick="openBrowse();" placeholder="업로드할 stemcell 파일을 선택하세요."/>
                            <span class="btn btn-primary" id = "browser" onClick="openBrowse();" disabled style="height: 25px; padding: 1px 7px 7px 6px;">Browse </span>&nbsp;&nbsp;&nbsp;
                            </span>
                        </div>
                    </div>
                    <div class="w2ui-field" style="margin: 8px 0px 0px 0px;">
                         <input type="radio" name="fileType" id="fileTypeUrl" style="float:left; margin-left:15px;" value="url" onchange="setRegistType(this.value);"/>
                         <label for="fileTypeUrl" style="width:25%;text-align: left;" >
                            &nbsp;&nbsp;스템셀 Url
                            <span class="glyphicon glyphicon glyphicon-question-sign stemcell-info" style="cursor:pointer;font-size: 14px;color: #157ad0;" data-toggle="popover"  data-trigger="click" data-html="true" title="<b>공개 스템셀 참조 사이트</b>"></span>
                         </label>
                         <div style="width:70%;">
                             <input type="text" style="width:60%;" id="stemcellPathUrl" name="stemcellPathUrl" readonly placeholder="스템셀 다운로드 Url을 입력 하세요."/>
                         </div>
                    </div>
                    <div class="w2ui-field" style="margin: 8px 0px 0px 0px;">
                        <input type="radio" name="fileType" id="fileTypeVersion" value="version" style="float:left; margin-left:15px;" onchange='setRegistType(this.value);' />
                        <label for="fileTypeVersion" style="width:25%; text-align: left;">&nbsp;&nbsp;스템셀 Version</label>
                        <div style="width:70%;">
                            <input type="text" id="stemcellPathVersion"   name="stemcellPathVersion" style="width:60%;" readonly placeholder="스템셀 다운로드 버전을 입력 하세요."/>
                            <span id="lightVerChk">
                                <label style="position: absolute; margin-left: 10px;" >
                                   <input name="light" type="checkbox" value="true" disabled />&nbsp;Light 유형
                                   <span style="display: inline-block;color:gray;font-size:12px;width: 100%;">(AWS/GCP/AZURE)</span>
                                </label>
                            </span>
                        </div>
                    </div>
                    <div class="w2ui-field" style="margin: 8px 0px 0px 0px; color:#666">
                        <label style="width:30%;text-align: left;padding-left: 15px;color:#3f51b5">
                           <input name="overlay" type="checkbox" value="overlay" checked/>&nbsp;&nbsp;파일 덮어 쓰기
                        </label>
                    </div>
                </div>
            </div>
        </div>
        <div id="regPopupBtnDiv" hidden="true">
           <button class="btn" id="registBtn" onclick="$('#settinfForm').submit();">확인</button>
           <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
        </div>
    </form>
 </div>
<!-- //스템셀 등록 팝업 -->

<script>
$(function() {
    $.validator.addMethod("sqlInjection", function(value, element, params) {
        return checkInjectionBlacklist(params);
      },text_injection_msg);
    
    $("#settinfForm").validate({
        ignore : "",
        onfocusout: true,
        rules: {
            stemcellName : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='stemcellName']").val() );
                }, sqlInjection : function(){
                    return $(".w2ui-msg-body input[name='stemcellName']").val();
                }
            }, stemcellPathFileName : {
                required : function(){
                    if( $(".w2ui-msg-body input:radio[name='fileType']:checked").val() == "file" ){
                        return checkEmpty( $(".w2ui-msg-body input[name='stemcellPathFileName']").val() );
                    }else return false;
                }
            }, stemcellPathUrl : {
                required : function(){
                    if( $(".w2ui-msg-body input:radio[name='fileType']:checked").val() == "url" ){
                        return checkEmpty( $(".w2ui-msg-body input[name='stemcellPathUrl']").val() );
                    }else return false;
                }, sqlInjection : function(){
                    return $(".w2ui-msg-body input[name='stemcellPathUrl']").val();
                }
            }, stemcellPathVersion : {
                required : function(){
                    if( $(".w2ui-msg-body input:radio[name='fileType']:checked").val() == "version" ){
                        return checkEmpty( $(".w2ui-msg-body input[name='stemcellPathVersion']").val() );
                    }else return false;
                }, sqlInjection : function(){
                    return $(".w2ui-msg-body input[name='stemcellPathVersion']").val();
                }
            }
        }, messages: {
            stemcellName: { 
               required:  "스템셀 명" + text_required_msg
               ,sqlInjection : text_injection_msg
           }, stemcellPathFileName: { 
               required:  "스템셀 파일" + text_required_msg
           }, stemcellPathUrl: { 
               required:  "스템셀 URL" + text_required_msg
           }, stemcellPathVersion: { 
               required:  "스템셀 버전" + text_required_msg
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
            var stemcellInfo = {
                        id               : $(".w2ui-msg-body input[name='id']").val(),
                        stemcellName     : $(".w2ui-msg-body input[name='stemcellName']").val(),
                        stemcellFileName : $(".w2ui-msg-body input[name=stemcellPathFileName]").val(),
                        stemcellUrl      : $(".w2ui-msg-body input[name='stemcellPathUrl']").val(),
                        stemcellVersion  : $(".w2ui-msg-body input[name='stemcellPathVersion']").val(),
                        osName           : $(".w2ui-msg-body select[name='osList']").val(),
                        osVersion        : $(".w2ui-msg-body select[name='osVersionList']").val(),
                        iaasType         : $(".w2ui-msg-body select[name='iaasList']").val(),
                        fileType         : $(".w2ui-msg-body :radio[name='fileType']:checked").val(), 
                        overlayCheck     : $(".w2ui-msg-body :checkbox[name='overlay']").is(':checked'),
                        stemcellSize     : $(".w2ui-msg-body input[name='stemcellSize']").val(),
                        light            : $(".w2ui-msg-body :checkbox[name='light']").is(':checked'),
                        downloadStatus   : ""
            }
            if(stemcellInfo.fileType == "file"){
                if($(".w2ui-msg-body input[name='stemcellSize']").val() == 0){
                     w2alert("스템셀 파일을 찾을 수 없습니다. 확인해주세요.", "스템셀 파일 업로드");
                     return false;
                 }
            }
            lock( '등록 중입니다.', true);
            
            $.ajax({
                type : "POST",
                url : "/config/stemcell/regist/info/N",
                contentType : "application/json",
                async : true,
                data : JSON.stringify(stemcellInfo),
                success : function(data, status) {
                    w2popup.close();
                    stemcellInfo.id = data.id;
                    stemcellInfo.downloadStatus = data.downloadStatus;
                    stemcellInfo.stemcellFileName = data.stemcellFileName;
                    initView();//재조회
                    if(stemcellInfo.fileType == "file"){
                        stemcellFileUpload(stemcellInfo);        
                    }else if(stemcellInfo.fileType == "url"){
                        stemcellFileDownload(stemcellInfo);
                    }else if (stemcellInfo.fileType == "version"){
                        stemcellFileDownload(stemcellInfo);
                    }else{
                        w2alert("잘못된 스템셀 등록 방식 입니다.");
                    }
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