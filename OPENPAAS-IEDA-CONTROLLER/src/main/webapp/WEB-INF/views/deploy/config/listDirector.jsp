<%
/* =================================================================
 * 상세설명 : 디렉터 조회 및 설정
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2016-08       지향은      디렉터 설정 화면 개선
 * 2018-04       배병욱      디렉터 BOSH_CLI_V2 적용 및 버그 수정
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix = "spring" uri = "http://www.springframework.org/tags" %>
<script type="text/javascript">
var credsKeyPathFileList = "";
var text_required_msg='<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
var text_injection_msg='<spring:message code="common.text.validate.sqlInjection.message"/>';//입력하신 값은 입력하실 수 없습니다.
var save_lock_msg='<spring:message code="common.save.data.lock"/>';//등록 중 입니다.
var update_lock_msg='<spring:message code="common.update.data.lock"/>';//등록 중 입니다.
var text_ip_msg = '<spring:message code="common.text.validate.ip.message"/>';
var fadeOutTime = 3000;
$(function() {
/********************************************************
 * 설명 :  디렉터 목록 조회
 *********************************************************/
  $('#config_directorGrid').w2grid({
    name: 'config_directorGrid',
    header: '<b>디렉터 목록</b>',
     method: 'GET',
     multiSelect: false,
    show: {    
            selectColumn: true,
            footer: true},
    style: 'text-align: center',
    columns:[
             {field: 'recid', caption: 'recid', hidden: true},
             {field: 'iedaDirectorConfigSeq', caption: '레코드키', hidden: true},
             {field: 'defaultYn',     caption: '기본 디렉터', size: '10%',
                   render: function(record) {
                       if ( record.defaultYn == 'Y' )
                           return '<span class="btn btn-primary" style="width:70px">기본</span>';
                       else
                           return '';
                   }
                 },
             {field: 'directorCpi' ,caption: 'CPI'      , size: '10%'},
             {field: 'directorName',caption: '디렉터 이름', size: '10%'},
             {field: 'userId'      ,caption: '계정'      , size: '10%'},
             {field: 'directorUrl' ,caption: 'URL'      , size: '30%',
                 render: function(record) {
                     return 'https://' + record.directorUrl + ':' + record.directorPort;
                     } 
             },
             {field: 'directorUuid', caption: '디렉터 UUID', size: '30%'}
             ],
    onSelect : function(event) {
        var grid = this;
        event.onComplete = function() {
            var sel = grid.getSelection();
            var record = grid.get(sel);
            if ( record.defaultYn == 'Y' ) {
                $('#setDefaultDirector').attr('disabled', true);
            }
            else {
                $('#setDefaultDirector').attr('disabled', false);
            }
            
            $('#updateSetting').attr('disabled', false);
            $('#deleteSetting').attr('disabled', false);
        }
    },
    onUnselect : function(event) {
        event.onComplete = function() {
            $('#setDefaultDirector').attr('disabled', true);
            $('#deleteSetting').attr('disabled', true);
            $('#updateSetting').attr('disabled', true);
        }
    }, 
    onLoad:function(event){
        if(event.xhr.status == 403){
            location.href = "/abuse";
            event.preventDefault();
        }
        getCredsKeyPathFileList();
    },
    onError: function(event) {
        
    }
});
      
 initView();
 /********************************************************
  * 설명 :  기본 디렉터 설정
  *********************************************************/
 $("#setDefaultDirector").click(function(){
     if($("#setDefaultDirector").attr('disabled') == "disabled") return;
     
     var selected = w2ui['config_directorGrid'].getSelection();
     if( selected.length == 0 ){
         w2alert("선택된 정보가 없습니다.", "기본 디렉터 설정");
         return;
     }
     else  if ( selected.length > 1 ){
         w2alert("기본 디렉터 설정은 하나만 선택 가능합니다.", "기본 디렉터 설정");
         return;
     }
     else{
         var record = w2ui['config_directorGrid'].get(selected);
         if( record.defaultYn == "Y" ){
             //클릭시 버튼  Disable 다른 페이지 호출
             w2alert("선택한 디렉터는 이미 기본 디렉터로 설정되어 있습니다.","기본 디렉터 설정");
             return;
         }
         else{
             w2confirm({
                 title        : "<b>기본관리자 설정</b>",
                 msg          : record.directorName + "를 " + "기본 디렉터로 설정하시겠습니까?",
                 yes_text     : "확인",
                 no_text      : "취소",
                 yes_callBack : function(envent){
                     w2ui['config_directorGrid'].lock("기본 디렉터 설정 중입니다.", {
                         spinner: true, opacity : 1
                     });
                     registDefault(record.iedaDirectorConfigSeq, record.directorName);
                     w2ui['config_directorGrid'].reset();
                 },
                 no_callBack  : function(envent){
                     w2ui['config_directorGrid'].reset();
                     doSearch();
                 }
             });
         }
     }
});
     
 /********************************************************
  * 설명 :  디렉터 추가 버튼
  *********************************************************/
$("#addSetting").click(function(){
    w2popup.open({
        title   : "<b>디렉터 추가</b>",
        width   : 600,
        height  : 380,
        modal   : true,
        body    : $("#regPopupDiv").html(),
        buttons : $("#regPopupBtnDiv").html(),
        onOpen : function(event){
            getCredsKeyPathFileList();
        },
        onClose:function(event){
            doSearch();
        }
    });
});
     
/********************************************************
 * 설명 :  설정 관리자 수정 버튼
 *********************************************************/
$("#updateSetting").click(function(){
    if($("#updateSetting").attr('disabled') == "disabled") return;
    
    var selected = w2ui['config_directorGrid'].getSelection();
    
    if( selected.length == 0 ){
        w2alert("선택된 정보가 없습니다.", "디렉터 정보 수정");
        return;
    }
    updateDirectorConfigPopup(w2ui['config_directorGrid'].get(selected));
});
    
/********************************************************
 * 설명 :  디렉터 삭제 버튼
 *********************************************************/
$("#deleteSetting").click(function(){
    if($("#deleteSetting").attr('disabled') == "disabled") return;
    var selected = w2ui['config_directorGrid'].getSelection();
    
    if( selected.length == 0 ){
        w2alert("선택된 정보가 없습니다.", "디렉터 삭제");
        return;
    }
    else {
        var record = w2ui['config_directorGrid'].get(selected);
        w2confirm({
            title       : "<b>디렉터 삭제</b>",
            msg         : "디렉터(" + record.directorName + ")를 삭제하시겠습니까?",
            yes_text    : "확인",
            no_text     : "취소",
            yes_callBack: function(event){
                // 디렉터 삭제
                deleteDirector(record.iedaDirectorConfigSeq);
                // 기본 관리자일 경우 
                if ( record.defaultYn == "Y" ) {
                    // 기본 디렉터 정보 조회
                    $('.defaultDirector').text('');
                }
                w2ui['config_directorGrid'].clear();
                
                initView();
            },
            no_callBack    : function(){
                w2ui['config_directorGrid'].reset();
                doSearch();
            }
        });
    }
});// 디렉터 삭제 버튼 END
});
/********************************************************
 * 설명 : 목록 재조회
 * 기능 : initView
 *********************************************************/
function initView() {
    // 기본 디렉터 정보 조회
     getDefaultDirector("<c:url value='/common/use/director'/>");
    // 디렉터 목록조회
    doSearch();
}
/********************************************************
 * 설명 : 목록 조회
 * 기능 : doSearch
 *********************************************************/
function doSearch() {
    w2ui['config_directorGrid'].load("<c:url value='/config/director/list'/>", doButtonStyle);
}
/********************************************************
 * 설명 : 버튼 스타일 초기화
 * 기능 : doButtonStyle
 *********************************************************/
function doButtonStyle(){
    var girdTotal = w2ui['config_directorGrid'].records.length;
    $('#setDefaultDirector').attr('disabled', true);
    $('#updateSetting').attr('disabled', true);
    $('#deleteSetting').attr('disabled', true);
}
/********************************************************
 * 설명 : 기본 디렉터 설정
 * 기능 : registDefault
 *********************************************************/
function registDefault(seq, target){
    $.ajax({
        type : "PUT",
        url : "/config/director/setDefault/"+seq,
        contentType : "application/json",
        success : function(data, status) {
            w2alert("기본 디렉터를 \n" + target +"로 설정하였습니다.",  "기본 디렉터 설정", doSearch);
            getDefaultDirector("<c:url value='/common/use/director'/>");
        },
        error : function(request, status, error) {
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "기본 디렉터 설정");
            doSearch();
        }
    });
}
/********************************************************
 * 설명 : 디렉터 등록
 * 기능 : registDirectorConfig
 *********************************************************/
function registDirectorConfig(){
     w2popup.lock(save_lock_msg, true);
    $.ajax({
        type : "POST",
        url : "/config/director/add",
        contentType : "application/json",
        //dataType: "json",
        async : true,
        data : JSON.stringify({
            directorUrl : $(".w2ui-msg-body input[name='ip']").val(),
            directorPort : parseInt($(".w2ui-msg-body input[name='port']").val()),
            userId : $(".w2ui-msg-body input[name='user']").val(),
            userPassword : $(".w2ui-msg-body input[name='pwd']").val(),
            credentialFile : $(".w2ui-msg-body input[name=credsKeyPath]").val()
        }),
        success : function(data, status) {
            
            w2popup.unlock();
            w2popup.close();
            doSearch();
            
            // 기본 디렉터 정보 조회
             getDefaultDirector("<c:url value='/common/use/director'/>");
        },
        error : function(request, status, error) {
            // ajax가 실패할때 처리...
            w2popup.unlock();
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message);
            doSearch();
        }
    });
}
/********************************************************
 * 설명 : 디렉터 수정 팝업
 * 기능 : updateDirectorConfigPopup
 *********************************************************/
function updateDirectorConfigPopup(record) {
             w2popup.open({
                title     : "<b>디렉터 정보수정</b>",
                width     : 600,
                height    : 350,
                modal     : true,
                body      : $("#regPopupDiv").html(),
                buttons   : $("#updatePopupBtnDiv").html(),
                onOpen    : function(event){
                    event.onComplete = function(){
                        $(".w2ui-msg-body input[name='seq']").val(record.iedaDirectorConfigSeq);
                        $(".w2ui-msg-body input[name='ip']").val(record.directorUrl);
                        $(".w2ui-msg-body input[name='ip']").attr("disabled", true);
                        $(".w2ui-msg-body input[name='port']").val(record.directorPort);
                        $(".w2ui-msg-body input[name='port']").attr("disabled", true);
                        $(".w2ui-msg-body input[name='user']").val(record.userId);
                        $(".w2ui-msg-body input[name='pwd']").val("");
                        $(".w2ui-msg-body input[name='ip']").val();
                    }
                },onClose : function(event){
                    w2ui['config_directorGrid'].reset();
                    doSearch();
                }
            });
}
/********************************************************
 * 설명 : 디렉터 수정 
 * 기능 : updateDirectorConfig
 *********************************************************/
function updateDirectorConfig() {
     w2popup.lock(update_lock_msg, true);
    $.ajax({
        type : "PUT",
        url : "/config/director/update",
        contentType : "application/json",
        async : true,
        data : JSON.stringify({
            iedaDirectorConfigSeq : parseInt($(".w2ui-msg-body input[name='seq']").val()),
            userId : $(".w2ui-msg-body input[name='user']").val(),
            userPassword : $(".w2ui-msg-body input[name='pwd']").val()
        }),
        success : function(data, status) {
            // ajax가 성공할때 처리...
            w2popup.unlock();
            w2popup.close();
            w2ui['config_directorGrid'].reset();
            doSearch();
            
            // 기본 디렉터 정보 조회
             getDefaultDirector("<c:url value='/common/use/director'/>");
        },
        error : function(request, status, error) {
            // ajax가 실패할때 처리...
            w2popup.unlock();
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message);
            doSearch();
        }
    });
}
/********************************************************
 * 설명 : 디렉터 삭제 
 * 기능 : deleteDirector
 *********************************************************/
function deleteDirector(seq){
    $.ajax({
        type : "DELETE",
        url : "/config/director/delete/"+ seq,
        contentType : "application/json",
        success : function(data, status) {
            // ajax가 성공할때 처리...
            doSearch();
            w2popup.unlock();
            w2popup.close();
        },
        error : function(request, status, error) {
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "디렉터 삭제");
            doSearch();
        }
    });
}
function closew2ui(){
    w2popup.close();
    doSearch();
}
/********************************************************
 * 설명 : 다른 페이지 호출
 * 기능 : clearMainPage
 *********************************************************/
function clearMainPage() {
    $().w2destroy('config_directorGrid');
}

/******************************************************************
 * 기능 : setCredentialKeyPath
 * 설명: 공통 File upload Input
 ***************************************************************** */
function setCredentialKeyPath(fileInput){
    var file = fileInput.files;
    $(".w2ui-msg-body #credsKeyFileName").val(file[0].name);
    $(".w2ui-msg-body input[name=credsKeyPath]").val(file[0].name);
}

function setCredentialKeyPathList(fileList){
    $(".w2ui-msg-body input[name=credsKeyPath]").val(fileList.value);
}
/****************************************************
 * 기능 : getCredsKeyPathFileList
 * 설명 : credential 키 파일 정보 조회
 *****************************************************/
function getCredsKeyPathFileList(){
    $.ajax({
        type : "GET",
        url : "/common/deploy/creds/list",
        contentType : "application/json",
        async : true,
        success : function(data, status) {
            credsKeyPathFileList = data;
            $('.w2ui-msg-body input:radio[name=credskeyPathType]:input[value=list]').attr("checked", true);  
            credsChangeKeyPathType("list");
        },
        error : function( e, status ) {
            w2alert("Credential KeyPath File 목록을 가져오는데 실패하였습니다.", "디렉터 등록");
        }
    });
}

/****************************************************
 * 기능 : uploadCredentialKey
 * 설명 : CredentialKey 업로드
*****************************************************/
function uploadCredentialKey(){
    var form = $(".w2ui-msg-body #settingForm")[0];
    var formData = new FormData(form);
            
    var files = document.getElementsByName('keyPathFile')[0].files;
    console.log("1:"+files[0]);
    formData.append("file", files[0]);
            
    $.ajax({
        type : "POST",        
        url : "/config/director/credskey/upload",
        enctype : 'multipart/formdata',
        dataType: "text",
        async : true,
        processData: false,
        contentType:false,
        data : formData,
        success : function(data, status) {
            registDirectorConfig();
        },
        error : function( e, status ) {
            w2alert( "Credential Key 업로드에 실패 하였습니다.", "디렉터 설정");
        }
    });
}

/******************************************************************
 * 기능 : credsChangeKeyPathType
 * 설명 : Credential key file 선택
 ***************************************************************** */
function credsChangeKeyPathType(type){
     $(".w2ui-msg-body input[name=credsKeyPath]").val("");
     $(".w2ui-msg-body input[name=credsKeyFileName]").val("");
     //목록에서 선택
     if(type == "list") {
         $('.w2ui-msg-body select[name=credsKeyPathList]').css("borderColor", "#bbb")
         credsChangeKeyPathStyle("#credsKeyPathListDiv", "#credsKeyPathFileDiv");
         
         var options = "<option value=''>Credential 파일을 선택하세요.</option>";
         for( var i=0; i<credsKeyPathFileList.length; i++ ){
             options += "<option value='"+credsKeyPathFileList[i]+"'>"+credsKeyPathFileList[i]+"</option>";
         }
         $('.w2ui-msg-body select[name=credsKeyPathList]').html(options);
     }else{
         //파일업로드
         $('.w2ui-msg-body input[name=keyPathFileName]').css("borderColor", "#bbb")
         credsChangeKeyPathStyle("#credsKeyPathFileDiv", "#credsKeyPathListDiv");
     }
}
/********************************************************
 * 기능 : credsChangeKeyPathStyle
 * 설명 : Credential File 입력 스타일 변경
 *********************************************************/
function credsChangeKeyPathStyle( showDiv, hideDiv ){
     $(".w2ui-msg-body "+ hideDiv).hide();
     $(".w2ui-msg-body "+ hideDiv +" p").remove();
     $(".w2ui-msg-body "+ showDiv).show();
}
/******************************************************************
 * Function : openBrowse
 * 설명 : 공통 File upload Browse Button
 ***************************************************************** */
function openBrowse(){
    $(".w2ui-msg-body input[name='keyPathFile']").click();
}
</script>

<div id="main">
    <div class="page_site">환경설정 및 관리 > <strong>디렉터 설정</strong></div>
    
    <!-- 디렉터 정보 -->
    <div id="isDefaultDirector"></div>
    
    <!-- 디렉터 목록-->
    <div class="pdt20">
        <div class="title fl">디렉터 목록</div>
        <div class="fr"> 
        <!-- Btn -->
        <sec:authorize access="hasAuthority('CONFIG_DIRECTOR_SET')">
        <span id="setDefaultDirector" class="btn btn-primary" style="width:180px" >기본 디렉터로 설정</span>
        </sec:authorize>
        <sec:authorize access="hasAuthority('CONFIG_DIRECTOR_ADD')">
        <span id="addSetting" class="btn btn-primary" style="width:130px" >설정 추가</span>
        </sec:authorize>
        <sec:authorize access="hasAuthority('CONFIG_DIRECTOR_UPDATE')">
        <!-- <span id="updateSetting" class="btn btn-info" style="width:130px" >설정 수정</span> -->
        </sec:authorize>
        <sec:authorize access="hasAuthority('CONFIG_DIRECTOR_DELETE')">
        <span id="deleteSetting" class="btn btn-danger" style="width:130px" >설정 삭제</span>
        </sec:authorize>
        <!-- //Btn -->
        </div>
    </div>
    
    <!-- 디렉터 목록 조회-->
    <div id="config_directorGrid" style="width:100%; height:610px"></div>    
</div>
<!-- 디렉터 정보추가/수정 팝업 -->
<div id="regPopupDiv" hidden="true">
    <form id="settingForm" action="POST">
        <input name="seq" type="hidden"/>
        <div class="w2ui-page page-0">
            <div class="panel panel-info" style="margin-top:5px;">
                <div class="panel-heading"><b>디렉터 정보</b></div>
                <div class="panel-body" style="overflow-y:auto;height:240px;">
                    <div class="w2ui-field">
                        <label style="width:30%;text-align: left;padding-left: 20px;">디렉터 IP</label>
                        <div style="width: 70%;">
                            <input name="ip" type="text" maxlength="100" style="width: 250px" placeholder="xxx.xx.xx.xxx" />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="width:30%;text-align: left;padding-left: 20px;">포트번호</label>
                        <div style="width: 70%;">
                            <input name="port" type="number" maxlength="100" style="width: 250px" placeholder="25555" />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="width:30%;text-align: left;padding-left: 20px;">계정</label>
                        <div style="width: 70%">
                            <input name="user" type="text" maxlength="100" style="width: 250px"  placeholder="admin" />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="width:30%;text-align: left;padding-left: 20px;">비밀번호</label>
                        <div style="width: 70%;">
                            <input name="pwd" type="password" maxlength="100" style="width: 250px"   placeholder="admin" />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="width:30%;text-align: left; padding-left: 20px;">Credential File 정보</label>
                        <div>
                            <span onclick="credsChangeKeyPathType('file');" style="width:30%;"><label><input type="radio" name="credskeyPathType" value="file" />&nbsp;파일업로드</label></span>
                            &nbsp;&nbsp;
                            <span onclick="credsChangeKeyPathType('list');" style="width:30%;"><label><input type="radio" name="credskeyPathType" value="list" />&nbsp;목록에서 선택</label></span>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <!-- <label style="width:36%; text-align: left; padding-left: 20px;" class="control-label"></label> -->
                        <div id="credsKeyPathDiv" style="padding-left: 30%">
                            <div id="credsKeyPathFileDiv" hidden="true">
                                <input type="text" id="credsKeyFileName" name="credsKeyFileName" style="width: 250px;" readonly onclick="openBrowse();" placeholder="Credential File을 선택하세요"/>
                                <input type="file" name="keyPathFile" onchange="setCredentialKeyPath(this);" style="display:none"/>
                                <a href="#" id="browse" onclick="openBrowse();"><span id="BrowseBtn">Browse</span></a>
                            </div>
                            <div id="credsKeyPathListDiv">
                                <select name="credsKeyPathList" id="credsKeyPathList" style="width: 250px;" onchange="setCredentialKeyPathList(this)"></select>
                            </div>
                        </div>
                        <input name="credsKeyPath" type="hidden" />
                    </div>
                </div>
            </div>
        </div>
    </form>    
</div>

<div id="regPopupBtnDiv" hidden="true">
    <button class="btn" id="registBtn" onclick="$('#settingForm').submit();">확인</button>
    <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
</div>

<div id="updatePopupBtnDiv" hidden="true">
    <button class="btn" id="updateBtn" onclick="$('#settingForm').submit();">확인</button>
    <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
</div>

<script>
$(function() {
    $.validator.addMethod("sqlInjection", function(value, element, params) {
        return checkInjectionBlacklist(params);
      },text_injection_msg);
    
    $.validator.addMethod( "ipv4", function( value, element, params ) {
        return /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(params);
    }, "Please enter a valid IP v4 address." );
    
    $("#settingForm").validate({
        ignore : "",
        onfocusout: true,
        rules: {
            ip : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='ip']").val() );
                }, sqlInjection : function(){
                    return $(".w2ui-msg-body input[name='ip']").val();
                }, ipv4 : function(){
                    return $(".w2ui-msg-body input[name='ip']").val();
                }
            },  user: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='user']").val() );
                }, sqlInjection :   function(){
                    return $(".w2ui-msg-body input[name='user']").val();
                }
            }, port: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='port']").val() );
                }, sqlInjection :   function(){
                    return $(".w2ui-msg-body input[name='port']").val();
                }
            },  pwd: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='pwd']").val() );
                }, sqlInjection :   function(){
                    return $(".w2ui-msg-body input[name='pwd']").val();
                }
            },  credsKeyFileName: {
                required: function(){
                    if( $(".w2ui-msg-body input:radio[name='credskeyPathType']:checked").val() == 'file' ){
                        return checkEmpty( $(".w2ui-msg-body input[name='credsKeyFileName']").val() );
                    }else{
                        return false;
                    }
                }, sqlInjection :   function(){
                    return $(".w2ui-msg-body input[name='credsKeyFileName']").val();
                }
            },  credsKeyPathList: {
                required: function(){
                    if( $(".w2ui-msg-body input:radio[name='credskeyPathType']:checked").val() == 'list' ){
                        return checkEmpty( $(".w2ui-msg-body #credsKeyPathList").val() );
                    }else{
                        return false;
                    }
                }, sqlInjection :   function(){
                    return $(".w2ui-msg-body select[name='credsKeyPathList']").val();
                }
            }
        }, messages: {
            ip: { 
                required:  "디렉터 IP" + text_required_msg
                , sqlInjection : text_injection_msg
                , ipv4: text_ip_msg
            }, port: { 
                required:  "포트 번호"+text_required_msg
                ,sqlInjection : text_injection_msg
            }, user: { 
                required:  "계정"+text_required_msg
                ,sqlInjection : text_injection_msg
            }, pwd: { 
                required:  "비밀번호"+text_required_msg
                ,sqlInjection : text_injection_msg
            }, credsKeyFileName: {
                required:  "Key 값"+text_required_msg
                ,sqlInjection : text_injection_msg
            }, credsKeyPathList: {
                required:  "Key 값"+text_required_msg
                ,sqlInjection : text_injection_msg
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
            if(checkEmpty( $(".w2ui-msg-body input[name='keyPathFile']").val() )){
                registDirectorConfig();
            }else{
                uploadCredentialKey();
            }
            
        }
    });
});    
</script>