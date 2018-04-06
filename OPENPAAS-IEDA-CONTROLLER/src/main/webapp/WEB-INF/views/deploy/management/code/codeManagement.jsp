<%
/* =================================================================
 * 작성일 : 2016.07
 * 작성자 : 황보유정
 * 상세설명 : 코드 관리 화면
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2016.12      이동현         코드 관리 화면 버그 수정
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "spring" uri = "http://www.springframework.org/tags" %>
<script type="text/javascript">
var code = "";
var codeIdx = "";
var subCodeIdx = "";
var save_lock_msg='<spring:message code="common.save.data.lock"/>';//등록 중 입니다.
var modify_lock_msg='<spring:message code="common.update.data.lock"/>';//수정 중 입니다.
var search_msg='<spring:message code="common.search.data.lock"/>';//조회 중 입니다.
var text_required_msg='<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
var select_required_msg='<spring:message code="common.select.vaildate.required.message"/>';//을(를) 선택하세요.
$(function(){
    /********************************************************
     * 설명 : 코드 그룹 목록 grid 설정
     *********************************************************/
    $('#us_codeGroupGrid').w2grid({
        name: 'us_codeGroupGrid',
        header: '<b>코드 그룹 목록</b>',
        method: 'GET',
            multiSelect: false,
        show: {    
                selectColumn: true,
                footer: true},
        style: 'text-align: center',
        columns:[
              { field: 'recid', hidden: true },
              { field: 'codeIdx', hidden: true },
              { field: 'codeName', caption: '코드그룹명', size:'25%', style:'text-align:center;' },
              { field: 'codeValue', caption: '코드 그룹값', size:'25%', style:'text-align:center;' },
              { field: 'codeDescription', caption: '설명', size:'35%', style:'text-align:center;'}
              ],
              onSelect : function(event) {
                event.onComplete = function() {
                    $('#modifyBtn').attr('disabled', false);
                    $('#deleteBtn').attr('disabled', false);
                    $('#addCodeBtn').attr('disabled', false);
                    var name =  w2ui.us_codeGroupGrid.get(event.recid).codeName;
                    var codeValue = w2ui.us_codeGroupGrid.get(event.recid).codeValue;
                    doSearchByIdx(codeValue);
                }
            },
            onUnselect : function(event) {
                event.onComplete = function() {
                    w2ui['us_codeGrid'].clear();
                    $('#addCodeBtn').attr('disabled', true);
                    $('#modifyBtn').attr('disabled', true);
                    $('#deleteBtn').attr('disabled', true);
                    $('#modifyCodeBtn').attr('disabled', true);
                    $('#deleteCodeBtn').attr('disabled', true);
                }
            },onLoad:function(event){
                if(event.xhr.status == 403){
                    location.href = "/abuse";
                    event.preventDefault();
                }
            },onError : function(event) {
            }
     });
    
    /********************************************************
     * 설명 : 코드 목록 Grid 설정
     *********************************************************/
    $('#us_codeGrid').w2grid({
        name    : 'us_codeGrid',
        show    : {    
                    selectColumn: true,
                    footer: true
                    },
        msgAJAXerror : '해당 코드가 존재 하지 않습니다.',
        multiSelect: false,
        method     : "GET",
        style    : 'text-align:center',
        columns    :[
                  { field: 'recid', hidden: true },
                  { field: 'codeIdx', hidden: true },
                  { field: 'parentCode', hidden: true },
                  { field: 'codeNameKR', caption: '코드명', size:'25%', style:'text-align:left;' },
                  { field: 'codeValue', caption: '코드값', size:'25%', style:'text-align:center;' },
                  { field: 'codeDescription', caption: '설명', size:'35%', style:'text-align:left;'},
                  { field: 'subGroupCode', caption: '서브 그룹', size:'25%', style:'text-align:center;' }
                  ],
                  onSelect : function(event) {
                    event.onComplete = function() {
                        $("#addCodeBtn").attr('disabled', false);
                        $('#modifyCodeBtn').attr('disabled', false);
                        $('#deleteCodeBtn').attr('disabled', false);
                        return;
                    }
                },
                onUnselect : function(event) {
                    event.onComplete = function() {
                        $('#modifyCodeBtn').attr('disabled', true);
                        $('#deleteCodeBtn').attr('disabled', true);
                        return;
                }
            }
    });
    
    /********************************************************
     * 설명 : 코드 그룹 등록 버튼
     *********************************************************/
    $("#addBtn").click(function(){
        w2popup.open({
            title     : "<b>코드 그룹 등록</b>",
            width     : 550,
            height    : 322,
            modal     : true,
            body      : $("#regPopupDiv").html(),
            buttons   : $("#regPopupBtnDiv").html()
            ,onClose:function(event){
                w2ui['us_codeGroupGrid'].clear();
                w2ui['us_codeGrid'].clear();
                doSearch();
            }
        });
    });
    
    /********************************************************
     * 설명 : 코드 그룹 삭제 버튼
     *********************************************************/
    $("#deleteBtn").click(function(){
        if($("#deleteBtn").attr('disabled') == "disabled") return;

        var selected = w2ui['us_codeGroupGrid'].getSelection();
        if( selected.length == 0 ){
            w2alert("선택된 정보가 없습니다.", "코드 그룹 삭제");
            return;
        }else {
            var record = w2ui['us_codeGroupGrid'].get(selected);
            w2confirm({
                title        : "<b>코드 그룹 삭제</b>",
                msg          : "코드 그룹(" + record.codeName + ")을 삭제하시겠습니까?",
                yes_text     : "확인",
                no_text      : "취소",
                yes_callBack : function(event){
                    deleteCodeGroupInfo(record.codeIdx);// 코드 삭제
                },
                no_callBack  : function(){
                    w2ui['us_codeGroupGrid'].clear();
                    w2ui['us_codeGrid'].clear();
                    doSearch();
                }
            });
        }
    });
    
    /********************************************************
     * 설명 : 코드 등록 버튼
    *********************************************************/
    $("#addCodeBtn").click(function(){
        if($("#addCodeBtn").attr('disabled') == "disabled") return;
        var selected = w2ui['us_codeGroupGrid'].getSelection();
        var record = w2ui['us_codeGroupGrid'].get(selected);
        
        w2popup.open({
            title     : "<b>코드 등록</b>",
            width     : 550,
            height    : 420,
            modal    : true,
            body    : $("#regCodePopupDiv").html(),
            buttons : $("#regCodePopupBtnDiv").html(),
            onOpen : function(event){
                event.onComplete = function(){
                    codeIdx = record.codeIdx;
                    $(".w2ui-msg-body input[name='parentCodeName']").val(record.codeName);
                    $(".w2ui-msg-body input[name='parentCode']").val(record.codeValue);
                    getSubList(record.codeValue);
                }
            },onClose:function(event){
                w2ui['us_codeGrid'].clear();
                $('#modifyCodeBtn').attr('disabled', true);
                $('#deleteCodeBtn').attr('disabled', true);
                doSearchByIdx(record.codeValue);
                initSetting();
            }
        });
    });
    
    /********************************************************
     * 설명 : 코드 삭제 버튼
     *********************************************************/
    $("#deleteCodeBtn").click(function(){
        if($("#deleteCodeBtn").attr('disabled') == "disabled") return;
        var selected = w2ui['us_codeGrid'].getSelection();
        
        if( selected.length == 0 ){
            w2alert("선택된 정보가 없습니다.", "코드 삭제");
            return;
        }else {
            var record = w2ui['us_codeGrid'].get(selected);
             w2confirm({
                title        : "<b>코드 삭제</b>",
                msg          : "<span style='color:red'>삭제하실 경우 관련된 하위 코드도 전체 삭제됩니다.</span> <br>코드 (" + record.codeName + ")를 삭제하시겠습니까?",
                yes_text     : "확인",
                no_text      : "취소",
                yes_callBack : function(event){
                    deleteCodeInfo(record.codeIdx);// 코드 삭제
                },
                no_callBack    : function(){
                    w2ui['us_codeGrid'].clear();
                    $('#modifyCodeBtn').attr('disabled', true);
                    $('#deleteCodeBtn').attr('disabled', true);
                    doSearchByIdx(record.parentCode)
                }
            });
        }
    });
    
    doSearch();
});

/********************************************************
 * 설명 : 코드 그룹 목록 조회
 * 기능 : doSearch
 *********************************************************/
function doSearch() {
    w2ui['us_codeGroupGrid'].load('/admin/code/groupList',
    function() {
        doButtonStyle();
    });
}

/********************************************************
 * 설명 : 코드 목록 조회
 * 기능 : doSearchByIdx
 *********************************************************/
function doSearchByIdx(parentVal) {
    if(parentVal == undefined || parentVal == 'undefined' || parentVal == -1) {
        return;
    }
    w2ui['us_codeGrid'].load('/admin/code/codeList/' + parentVal); //코드 목록 조회
}


/********************************************************
 * 설명 : 코드 그룹 수정팝업
 * 기능 : updatePopup
 *********************************************************/
function updatePopup(record) {
    if($("#modifyBtn").attr('disabled') == "disabled") return;
    var selected = w2ui['us_codeGroupGrid'].getSelection();
    if( selected.length == 0 ){
        w2alert("선택된 정보가 없습니다.", "코드 그룹 수정");
        return;
    }else {
        var record = w2ui['us_codeGroupGrid'].get(selected);
        codeIdx = record.codeIdx;
    }
    //코드그 룹 수정 팝업 활성화
     w2popup.open({
         title   : "<b>코드 그룹 수정</b>",
         width   : 550,
         height  : 322,
         modal   : true,
         body      : $("#regPopupDiv").html(),
         buttons   : $("#regPopupBtnDiv").html(),
         onOpen  : function(event){
             event.onComplete = function(){
                 //코드 그룹 상세 조회
                 $(".w2ui-msg-body input[name='codeValue']").attr("readonly", true);
                 w2popup.lock(search_msg, true);
                 getCodeGroupInfo();
             }
         },onClose:function(event){
                w2ui['us_codeGroupGrid'].clear();
                w2ui['us_codeGrid'].clear();
                doSearch();
         }
     });
}

/********************************************************
 * 설명 : 코드 그룹 정보 상세 조회
 * 기능 : getCodeGroupInfo
 *********************************************************/
function getCodeGroupInfo(){
    $.ajax({
        type : "GET",
        url : "/admin/code/list/" + codeIdx,
        contentType : "application/json",
        dataType: "json",
        async : true,
        success : function(data, status) {
            w2popup.unlock();
            $(".w2ui-msg-body input[name='codeValue']").val(data.codeValue);
            $(".w2ui-msg-body input[name='codeName']").val(data.codeName);
            $(".w2ui-msg-body textarea[name='codeDescription']").val(data.codeDescription);
        },
        error : function(request, status, error) {
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message);
        }
    });
}

/********************************************************
 * 설명 : 코드 그룹 등록
 * 기능 : registCodeGroupInfo
 *********************************************************/
function registCodeGroupInfo(){
     w2popup.lock(save_lock_msg, true);
    var code = {
            codeIdx         : checkEmpty(codeIdx) ? "" : codeIdx,
            codeName        : $(".w2ui-msg-body input[name='codeName']").val(),
            codeValue       : $(".w2ui-msg-body input[name='codeValue']").val(),
            codeDescription : $(".w2ui-msg-body textarea[name='codeDescription']").val()
    }
    $.ajax({
        type : "POST",
        url : "/admin/code/codeGroup/add",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(code),
        success : function(status) {
            w2popup.unlock();
            w2popup.close();
            doSearch();
            initSetting();
        },
        error : function(request, status, error) {
            w2popup.unlock();
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message);
        }
    });
}

/********************************************************
 * 설명 : 코드 그룹 수정
 * 기능 : update
 *********************************************************/
function update(record, codeType) {
    lock( modify_lock_msg, true);
    $.ajax({
        type : "PUT",
        url : "/admin/code/update/" + record.codeIdx,
        contentType : "application/json",
        async : true,
        data : JSON.stringify(record),
        success : function(data, status) {
            // ajax가 성공할때 처리...
            w2popup.unlock();
            w2popup.close();
            if(codeType == 'codeGroup') { 
                doSearch(); 
            }else{ 
                doSearchByIdx(record.parentCode); 
            }
        },
        error : function(request, status, error) {
            // ajax가 실패할때 처리...
            w2popup.unlock();
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message);
        }
    });
}

/********************************************************
 * 설명 : 코드 그룹 삭제
 * 기능 : deleteCodeGroupInfo
 *********************************************************/
function deleteCodeGroupInfo(codeIdx){
    $.ajax({
        type : "DELETE",
        url : "/admin/code/codeGroup/delete/"+ codeIdx,
        contentType : "application/json",
        success : function(data, status) {
            w2popup.unlock();
            w2popup.close();
            gridReload();
        },
        error : function(request, status, error) {
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "코드 그룹 삭제");
        }
    });
}

/********************************************************
 * 설명 : 하위 그룹 목록 조회
 * 기능 : getSubList
 *********************************************************/
function getSubList(parentCode) {
    w2popup.lock(search_msg, true);
    $.ajax({
        type : "GET",
        url : "/common/deploy/codes/parent/" + parentCode,
        contentType : "application/json",
        dataType: "json",
        async : true,
        success : function(data, status) {
            w2popup.unlock();
            var options = "<option value=''>선택안함</option>";
            if( data.length > 0 ){
	            for(var i=0; i < data.length; i++) {
	                if( $(".w2ui-msg-body input[name='subGroupCode']").val() == data[i].codeValue ){
	                    options += "<option value='"+data[i].codeValue+"' selected>"+data[i].codeName+"</option>";
	                }else{
		                options += "<option value='"+data[i].codeValue+"'>"+data[i].codeName+"</option>";
	                }
	            }
            }
            $(".w2ui-msg-body select[name='subGroupList']").html(options);
        }, error : function(request, status, error) {
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message);
        }
    });
}

/********************************************************
 * 설명 : 하위 그룹 목록 선택 값
 * 기능 : setSubGroupCode
 *********************************************************/
function setSubGroupCode(value){
    $(".w2ui-msg-body input[name=subGroupCode]").val(value);
}

/********************************************************
 * 설명 : 코드 등록
 * 기능 : saveCodeInfo
 *********************************************************/
function saveCodeInfo(){
    w2popup.lock(save_lock_msg, true);
    var subCode = {
            codeIdx         : checkEmpty(subCodeIdx) ? "" : subCodeIdx,
            parentCode      : $(".w2ui-msg-body input[name='parentCode']").val(),
            subGroupCode    : $(".w2ui-msg-body select[name='subGroupList']").val(),
            codeName        : $(".w2ui-msg-body input[name='subCodeName']").val(),
            codeNameKR      : $(".w2ui-msg-body input[name='subCodeNameKR']").val(),
            codeValue       : $(".w2ui-msg-body input[name='subCodeValue']").val(),
            codeDescription : $(".w2ui-msg-body textarea[name='subCodeDescription']").val()
    }
    $.ajax({
        type : "POST",
        url : "/admin/code/add",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(subCode),
        success : function(status) {
            w2popup.unlock();
            w2popup.close();
            doSearchByIdx(code.parentCode);
            initSetting();
        }, error : function(request, status, error) {
            w2popup.unlock();
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message);
        }
    });
}

/********************************************************
 * 설명 : 코드 수정팝업
 * 기능 : updateCodePopup
 *********************************************************/
 function updateCodePopup(record) {
     if($("#modifyCodeBtn").attr('disabled') == "disabled") return;
     var selected = w2ui['us_codeGrid'].getSelection();
     if( selected.length == 0 ){
         w2alert("선택된 정보가 없습니다.", "코드 수정");
         return;
     }
     var record = w2ui['us_codeGrid'].get(selected);
     subCodeIdx = record.codeIdx;
     //코드그 룹 수정 팝업 활성화
      w2popup.open({
          title   : "<b>코드 수정</b>",
          width   : 550,
          height  : 420,
          modal   : true,
          body    : $("#regCodePopupDiv").html(),
          buttons : $("#regCodePopupBtnDiv").html(),
          onOpen  : function(event){
              event.onComplete = function(){
                  w2popup.lock(search_msg, true);
                  $(".w2ui-msg-body input[name='parentCodeName']").val(record.codeName);
                  $(".w2ui-msg-body input[name='subCodeValue']").attr("readonly", true);
                  getCodeInfo(subCodeIdx);//코드 상세 조회
              }
          },onClose:function(event){
              w2ui['us_codeGrid'].clear();
              $('#modifyCodeBtn').attr('disabled', true);
              $('#deleteCodeBtn').attr('disabled', true);
              doSearchByIdx(record.parentCode);
          }
      });
 }
 
 /********************************************************
  * 설명 : 코드 정보 조회
  * 기능 : getCodeInfo
 *********************************************************/
 function getCodeInfo(subCodeIdx){
     $.ajax({
         type : "GET",
         url : "/admin/code/list/" + subCodeIdx,
         contentType : "application/json",
         dataType: "json",
         async : true,
         success : function(data, status) {
             w2popup.unlock();
             subCodeIdx = data.codeIdx;
             $(".w2ui-msg-body input[name='parentCode']").val(data.parentCode);
             $(".w2ui-msg-body input[name='subCodeValue']").val(data.codeValue);
             $(".w2ui-msg-body input[name='subCodeName']").val(data.codeName);
             $(".w2ui-msg-body input[name='subCodeNameKR']").val(data.codeNameKR);
             $(".w2ui-msg-body input[name='subGroupCode']").val(data.subGroupCode);
             $(".w2ui-msg-body textarea[name='subCodeDescription']").val(data.codeDescription);
             
             //코드 그룹명 설정
             var selected = w2ui['us_codeGroupGrid'].getSelection();
             var record = w2ui['us_codeGroupGrid'].get(selected);
             getSubList(record.codeValue);//하위 코드 목록 조회
         },
         error : function(request, status, error) {
             var errorResult = JSON.parse(request.responseText);
             w2alert(errorResult.message);
         }
     });
 }
 
 /********************************************************
  * 설명 : 코드 정보 삭제
  * 기능 : deleteCodeInfo
  *********************************************************/
 function deleteCodeInfo(codeIdx){
     $.ajax({
         type : "DELETE",
         url : "/admin/code/delete/"+ codeIdx,
         contentType : "application/json",
         success : function(data, status) {
             w2popup.unlock();
             w2popup.close();
             w2ui['us_codeGrid'].clear();
             var selected = w2ui['us_codeGroupGrid'].getSelection();
             var record = w2ui['us_codeGroupGrid'].get(selected);
             doSearchByIdx(record.codeValue);
             $('#modifyCodeBtn').attr('disabled', true);
             $('#deleteCodeBtn').attr('disabled', true);
         },
         error : function(request, status, error) {
             var errorResult = JSON.parse(request.responseText);
             w2alert(errorResult.message, "코드 삭제");
         }
     });
 }

 /********************************************************
  * 설명 : 초기 셋팅
  * 기능 : initSetting
  *********************************************************/
 function initSetting(){
     codeIdx ="";
     subCodeIdx="";
     code="";
 }

 /********************************************************
  * 설명 : 그리드 재조회
  * 기능 : gridReload
  *********************************************************/
 function gridReload() {
     w2ui['us_codeGroupGrid'].clear();
     w2ui['us_codeGrid'].clear();
     doSearch();
 }
 
/********************************************************
 * 설명 : 버튼 스타일 변경
 * 기능 : doButtonStyle
 *********************************************************/
function doButtonStyle() {
    //코드 그룹 목록
    $('#modifyBtn').attr('disabled', true);
    $('#deleteBtn').attr('disabled', true);
    
    //코드 목록 버튼
    $('#addCodeBtn').attr('disabled', true);
    $('#modifyCodeBtn').attr('disabled', true);
    $('#deleteCodeBtn').attr('disabled', true);
    
}

/********************************************************
 * 설명 : 다른페이지 이동시 호출
 * 기능 : clearMainPage
 *********************************************************/
function clearMainPage() {
    $().w2destroy('us_codeGroupGrid');
    $().w2destroy('us_codeGrid');
}

/********************************************************
 * 설명 : Lock 실행
 * 기능 : lock
 *********************************************************/
function lock (msg) {
    w2popup.lock(msg, true);
}


</script>
<div id="main">
    <div class="page_site">플랫폼 관리자 관리 > <strong>코드 관리</strong></div>
    
    <!-- 코드 그룹 목록-->
    <div class="pdt20">
        <div class="title fl">코드 그룹 목록</div>
        <div class="fr"> 
            <span id="addBtn" class="btn btn-primary" style="width:120px">등록</span>
            <span id="modifyBtn" onclick="updatePopup();" class="btn btn-info" style="width:120px">수정</span>
            <span id="deleteBtn" class="btn btn-danger" style="width:120px">삭제</span>
        </div>
    </div>
    <div id="us_codeGroupGrid" style="width:100%; height:230px"></div>
    
    <!-- 코드 목록-->
    <div class="pdt20">
        <div class="title fl">코드 목록</div>
        <div class="fr"> 
            <span id="addCodeBtn" class="btn btn-primary" style="width:120px">등록</span>
            <span id="modifyCodeBtn" onclick="updateCodePopup();" class="btn btn-info" style="width:120px">수정</span>
            <span id="deleteCodeBtn" class="btn btn-danger" style="width:120px">삭제</span>
        </div>
    </div>    
    <div id="us_codeGrid" style="width:100%; height:430px"></div>
    
    <!-- 코드 그룹 추가/수정 팝업 -->
    <div id="regPopupDiv" hidden="true">
        <form id="codeGroupForm" action="POST">
            <div class="panel panel-info" style="margin-top: 5px;">
                <div class="panel-heading"><b>코드 그룹 정보</b></div>
                <div class="panel-body" style="height:185px;">
                    <div class="w2ui-field">
                        <label style="width:30%;text-align: left;padding-left: 20px;">코드 그룹명</label>
                        <div style="width: 70%;">
                            <input name="codeName" type="text" placeholder="예) 권한 코드" maxlength="100" style="width: 250px" />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="width:30%;text-align: left;padding-left: 20px;">코드 그룹값</label>
                        <div style="width: 70%">
                            <input name="codeValue" type="text" maxlength="100" style="width: 250px" placeholder="예) 10000" />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="width:30%;text-align: left;padding-left: 20px;">설명</label>
                        <div style="width: 70%;">
                            <textarea name="codeDescription" placeholder="설명을 입력하세요." style="display:inline-block; width: 250px; height: 60px; overflow-y: auto; resize: none;"  ></textarea>
                        </div>
                    </div>
                </div>
            </div>
        </form>    
    </div>
    <div id="regPopupBtnDiv" hidden="true">
        <button class="btn" id="registBtn" onclick="$('#codeGroupForm').submit();">확인</button>
        <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
    </div>
    
    <!-- 코드 추가/수정 팝업 -->
    <div id="regCodePopupDiv"  hidden="true">
        <form id="codeForm" action="POST">
            <div class="panel panel-info" style="margin-top: 5px;">
                <div class="panel-heading"><b>코드 정보</b></div>
                <div class="panel-body" style="height:285px;overflow-y: auto;">
                    <div class="w2ui-field">
                        <label style="width:30%;text-align: left;padding-left: 20px;">코드 그룹명</label>
                        <div>
                            <input name="parentCodeName" type="text" maxlength="100" style="width: 250px" readonly />
                            <input name="parentCode" type="hidden" />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="width:30%;text-align: left;padding-left: 20px;">하위 그룹</label>
                        <div>
	                        <select name="subGroupList" id="subGroupList"  onchange="setSubGroupCode(this.value);"  style="display:inline-block; width:250px;"></select>
                            <input name="subGroupCode" type="hidden" />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="width:30%;text-align: left;padding-left: 20px;">코드명(영문)</label>
                        <div>
                            <input name="subCodeName" type="text" maxlength="100" style="width: 250px" placeholder="예) sub_code" />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="width:30%;text-align: left;padding-left: 20px;">코드명(한글)</label>
                        <div>
                            <input name="subCodeNameKR" type="text" maxlength="100" style="width: 250px" placeholder="예) 하위 코드" />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="width:30%;text-align: left;padding-left: 20px;">코드값</label>
                        <div>
                            <input name="subCodeValue" type="text" maxlength="100" placeholder="예)11000" style="width: 250px" onkeypress="Keycode(event);"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="width:30%;text-align: left;padding-left: 20px;">설명</label>
                        <div>
                            <textarea name="subCodeDescription" placeholder="예)sub code"  style="display:inline-block; width: 250px; height: 70px; overflow-y: visible; resize:none; " ></textarea>
                        </div>
                    </div>
                </div>
            </div>
        </form>    
    </div>
    <div id="regCodePopupBtnDiv" hidden="true">
        <button class="btn" id="registCodeBtn" onclick="$('#codeForm').submit();">확인</button>
        <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
    </div>
</div>
<script type="text/javascript" src="<c:url value='/js/rules/code/code_subGroup_rule.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/rules/code/code_rule.js'/>"></script>
