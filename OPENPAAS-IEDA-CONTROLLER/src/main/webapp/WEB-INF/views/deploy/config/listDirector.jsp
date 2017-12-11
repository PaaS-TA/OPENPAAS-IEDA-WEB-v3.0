<%
/* =================================================================
 * 상세설명 : 설치관리자 조회 및 설정
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2016-08       지향은      설치관리자 설정 화면 개선
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix = "spring" uri = "http://www.springframework.org/tags" %>
<script type="text/javascript">
var text_required_msg='<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
var text_injection_msg='<spring:message code="common.text.validate.sqlInjection.message"/>';//입력하신 값은 입력하실 수 없습니다.
var save_lock_msg='<spring:message code="common.save.data.lock"/>';//등록 중 입니다.
var update_lock_msg='<spring:message code="common.update.data.lock"/>';//등록 중 입니다.
var text_ip_msg = '<spring:message code="common.text.validate.ip.message"/>';
var fadeOutTime = 3000;
$(function() {
/********************************************************
 * 설명 :  설치관리자 목록 조회
 *********************************************************/
  $('#config_directorGrid').w2grid({
    name: 'config_directorGrid',
    header: '<b>설치관리자 목록</b>',
     method: 'GET',
     multiSelect: false,
    show: {    
            selectColumn: true,
            footer: true},
    style: 'text-align: center',
    columns:[
             {field: 'recid', caption: 'recid', hidden: true},
             {field: 'iedaDirectorConfigSeq', caption: '레코드키', hidden: true},
             {field: 'defaultYn',     caption: '기본 관리자', size: '10%',
                   render: function(record) {
                       if ( record.defaultYn == 'Y' )
                           return '<span class="btn btn-primary" style="width:70px">기본</span>';
                       else
                           return '';
                   }
                 },
             {field: 'directorCpi' ,caption: 'CPI'      , size: '10%'},
             {field: 'directorName',caption: '관리자 이름', size: '10%'},
             {field: 'userId'      ,caption: '계정'      , size: '10%'},
             {field: 'directorUrl' ,caption: 'URL'      , size: '30%',
                 render: function(record) {
                     return 'https://' + record.directorUrl + ':' + record.directorPort;
                     } 
             },
             {field: 'directorUuid', caption: '관리자 UUID', size: '30%'}
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
    },
    onError: function(event) {
        
    }
});
      
 initView();
 /********************************************************
  * 설명 :  기본 설치 관리자 설정
  *********************************************************/
 $("#setDefaultDirector").click(function(){
     if($("#setDefaultDirector").attr('disabled') == "disabled") return;
     
     var selected = w2ui['config_directorGrid'].getSelection();
     if( selected.length == 0 ){
         w2alert("선택된 정보가 없습니다.", "기본 설치 관리자 설정");
         return;
     }
     else  if ( selected.length > 1 ){
         w2alert("기본 설치 관리자 설정은 하나만 선택 가능합니다.", "기본 설치 관리자 설정");
         return;
     }
     else{
         var record = w2ui['config_directorGrid'].get(selected);
         if( record.defaultYn == "Y" ){
             //클릭시 버튼  Disable 다른 페이지 호출
             w2alert("선택한 설치 관리자는 이미 기본 설치 관리자로 설정되어 있습니다.","기본 설치 관리자 설정");
             return;
         }
         else{
             w2confirm({
                 title        : "<b>기본관리자 설정</b>",
                 msg          : record.directorName + "를 " + "기본관리자로 설정하시겠습니까?",
                 yes_text     : "확인",
                 no_text      : "취소",
                 yes_callBack : function(envent){
                	 w2ui['config_directorGrid'].lock("기본관리자 설정 중입니다.", {
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
  * 설명 :  설정 관리자 추가 버튼
  *********************************************************/
$("#addSetting").click(function(){
    w2popup.open({
        title   : "<b>설치관리자 설정추가</b>",
        width   : 550,
        height  : 322,
        modal   : true,
        body    : $("#regPopupDiv").html(),
        buttons : $("#regPopupBtnDiv").html(),
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
        w2alert("선택된 정보가 없습니다.", "설치 관리자 정보 수정");
        return;
    }
    updateDirectorConfigPopup(w2ui['config_directorGrid'].get(selected));
});
    
/********************************************************
 * 설명 :  설정관리자 삭제 버튼
 *********************************************************/
$("#deleteSetting").click(function(){
    if($("#deleteSetting").attr('disabled') == "disabled") return;
    var selected = w2ui['config_directorGrid'].getSelection();
    
    if( selected.length == 0 ){
        w2alert("선택된 정보가 없습니다.", "설치 관리자 삭제");
        return;
    }
    else {
        var record = w2ui['config_directorGrid'].get(selected);
        w2confirm({
            title       : "<b>설치 관리자 삭제</b>",
            msg         : "설치 관리자(" + record.directorName + ")를 삭제하시겠습니까?",
            yes_text    : "확인",
            no_text     : "취소",
            yes_callBack: function(event){
                // 디렉터 삭제
                deleteDirector(record.iedaDirectorConfigSeq);
                // 기본 관리자일 경우 
                if ( record.defaultYn == "Y" ) {
                    // 기본 설치 관리자 정보 조회
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
});// 설정관리자 삭제 버튼 END
});
/********************************************************
 * 설명 : 목록 재조회
 * 기능 : initView
 *********************************************************/
function initView() {
    // 기본 설치 관리자 정보 조회
     getDefaultDirector("<c:url value='/common/use/director'/>");
    // 설치관리자 목록조회
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
 * 설명 : 기본 설치 관리자 설정
 * 기능 : registDefault
 *********************************************************/
function registDefault(seq, target){
    $.ajax({
        type : "PUT",
        url : "/config/director/setDefault/"+seq,
        contentType : "application/json",
        success : function(data, status) {
            w2alert("기본 설치 관리자를 \n" + target +"로 설정하였습니다.",  "기본 설치 관리자 설정", doSearch);
            getDefaultDirector("<c:url value='/common/use/director'/>");
        },
        error : function(request, status, error) {
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "기본 설치 관리자 설정");
            doSearch();
        }
    });
}
/********************************************************
 * 설명 : 설정관리자 등록
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
        }),
        success : function(data, status) {
            
            w2popup.unlock();
            w2popup.close();
            doSearch();
            
            // 기본 설치 관리자 정보 조회
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
 * 설명 : 설정관리자 수정 팝업
 * 기능 : updateDirectorConfigPopup
 *********************************************************/
function updateDirectorConfigPopup(record) {
             w2popup.open({
                title     : "<b>설치관리자 정보수정</b>",
                width     : 550,
                height    : 340,
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
                    }
                },onClose : function(event){
                    w2ui['config_directorGrid'].reset();
                    doSearch();
                }
            });
}
/********************************************************
 * 설명 : 설정관리자 수정 
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
            
            // 기본 설치 관리자 정보 조회
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
 * 설명 : 설정관리자 삭제 
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
            w2alert(errorResult.message, "설치 관리자 삭제");
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

</script>

<div id="main">
    <div class="page_site">환경설정 및 관리 > <strong>설치관리자 설정</strong></div>
    
    <!-- 설치 관리자 정보 -->
    <div id="isDefaultDirector"></div>
    
    <!-- 설치관리자 목록-->
    <div class="pdt20">
        <div class="title fl">설치관리자 목록</div>
        <div class="fr"> 
        <!-- Btn -->
        <sec:authorize access="hasAuthority('CONFIG_DIRECTOR_SET')">
        <span id="setDefaultDirector" class="btn btn-primary" style="width:180px" >기본 설치 관리자로 설정</span>
        </sec:authorize>
        <sec:authorize access="hasAuthority('CONFIG_DIRECTOR_ADD')">
        <span id="addSetting" class="btn btn-primary" style="width:130px" >설정 추가</span>
        </sec:authorize>
        <sec:authorize access="hasAuthority('CONFIG_DIRECTOR_UPDATE')">
        <span id="updateSetting" class="btn btn-info" style="width:130px" >설정 수정</span>
        </sec:authorize>
        <sec:authorize access="hasAuthority('CONFIG_DIRECTOR_DELETE')">
        <span id="deleteSetting" class="btn btn-danger" style="width:130px" >설정 삭제</span>
        </sec:authorize>
        <!-- //Btn -->
        </div>
    </div>
    
    <!-- 설치관리자 목록 조회-->
    <div id="config_directorGrid" style="width:100%; height:610px"></div>    
</div>
<!-- 설치관리자 정보추가/수정 팝업 -->
<div id="regPopupDiv" hidden="true">
    <form id="settingForm">
        <input name="seq" type="hidden"/>
        <div class="w2ui-page page-0">
	        <div class="panel panel-info" style="margin-top:5px;">
	            <div class="panel-heading"><b>설치관리자 정보</b></div>
	            <div class="panel-body" style="overflow-y:auto;height:185px;">
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
            if(checkEmpty( $(".w2ui-msg-body input[name='seq']").val() )){
                registDirectorConfig();
            }else{
                updateDirectorConfig();
            }
            
        }
    });
});    
</script>