<%
/* =================================================================
 * 작성일 : 2018.07
 * 작성자 : 이정윤
 * 상세설명 : 리소스 정보 관리 화면
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri = "http://www.springframework.org/tags" %>

<script type="text/javascript">
var text_required_msg = '<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
var select_required_msg='<spring:message code="common.select.vaildate.required.message"/>';//을(를) 선택하세요.
var search_data_fail_msg ='클라우드 인프라 환경을 선택하세요.';
var resourceConfigInfo = [];//리소스 정보
var iaas = "";
var resourceLayout = {
        layout2: {
            name: 'layout2',
            padding: 4,
            panels: [
                { type: 'left', size: '65%', resizable: true, minSize: 300 },
                { type: 'main', minSize: 300 }
            ]
        },
        /********************************************************
         *  설명 : 리소스 정보 목록 Grid
        *********************************************************/
        grid: {
            name: 'resource_GroupGrid',
            header: '<b>Resource 정보</b>',
            method: 'GET',
                multiSelect: false,
            show: {
                    selectColumn: true,
                    footer: true},
            style: 'text-align: center',
            columns:[
                   { field: 'recid', hidden: true },
                   { field: 'id', hidden: true },
                   { field: 'resourceConfigName', caption: '리소스 정보 별칭', size:'50%', style:'text-align:center;' },
                   { field: 'iaasType', caption: '인프라 환경 타입', size:'50%', style:'text-align:center;' ,render: function(record){ 
                       if(record.iaasType.toLowerCase() == "aws"){
                           return "<img src='images/iaasMgnt/aws-icon.png' width='80' height='30' />";
                       }else if (record.iaasType.toLowerCase() == "openstack"){
                           return "<img src='images/iaasMgnt/openstack-icon.png' width='90' height='35' />";
                       }
                   }},
                   { field: 'stemcellName', caption: '스템셀 명', size:'50%', style:'text-align:center;'},
                   { field: 'instanceType', caption: '인스턴스 유형', size:'60%', style:'text-align:center;'},
                   { field: 'vmPassword', caption: 'VM 비밀번호', size:'50%', style:'text-align:center;', hidden: true}
                  ],
            onSelect : function(event) {
                event.onComplete = function() {
                    $('#deleteBtn').attr('disabled', false);
                    settingResourceInfo();
                    return;
                }
            },
            onUnselect : function(event) {
                event.onComplete = function() {
                    resetForm();
                    $('#deleteBtn').attr('disabled', true);
                    return;
                }
            },onLoad:function(event){
                if(event.xhr.status == 403){
                    location.href = "/abuse";
                    event.preventDefault();
                }
            },onError : function(event) {
            },
        form: { 
            header: 'Edit Record',
            name: 'regPopupDiv',
            fields: [
                { name: 'recid', type: 'text', html: { caption: 'ID', attr: 'size="10" readonly' } },
                { name: 'fname', type: 'text', required: true, html: { caption: 'First Name', attr: 'size="40" maxlength="40"' } },
                { name: 'lname', type: 'text', required: true, html: { caption: 'Last Name', attr: 'size="40" maxlength="40"' } },
                { name: 'email', type: 'email', html: { caption: 'Email', attr: 'size="30"' } },
                { name: 'sdate', type: 'date', html: { caption: 'Date', attr: 'size="10"' } }
            ],
            actions: {
                Reset: function () {
                    this.clear();
                },
                Save: function () {
                    var errors = this.validate();
                    if (errors.length > 0) return;
                    if (this.recid == 0) {
                        w2ui.grid.add($.extend(true, { recid: w2ui.grid.records.length + 1 }, this.record));
                        w2ui.grid.selectNone();
                        this.clear();
                    } else {
                        w2ui.grid.set(this.recid, this.record);
                        w2ui.grid.selectNone();
                        this.clear();
                    }
                }
            }
        }
    }
}

$(function(){
    $('#resource_GroupGrid').w2layout(resourceLayout.layout2);
    w2ui.layout2.content('left', $().w2grid(resourceLayout.grid));
    w2ui['layout2'].content('main', $('#regPopupDiv').html());
    doSearch();
    
    $("#deleteBtn").click(function(){
        if($("#deleteBtn").attr('disabled') == "disabled") return;
        var selected = w2ui['resource_GroupGrid'].getSelection();
        if( selected.length == 0 ){
            w2alert("선택된 정보가 없습니다.", "리소스 삭제");
            return;
        }
        else {
            var record = w2ui['resource_GroupGrid'].get(selected);
            w2confirm({
                title        : "리소스 정보",
                msg            : "리소스 정보 ("+record.resourceConfigName + ")을 삭제하시겠습니까?",
                yes_text    : "확인",
                no_text        : "취소",
                yes_callBack: function(event){
                    deleteBootstrapResourceConfigInfo(record.recid, record.resourceConfigName);
                },
                no_callBack    : function(){
                    w2ui['resource_GroupGrid'].clear();
                    doSearch();
                }
            });
        }
    });
});

/********************************************************
 * 설명 : 리소스 수정 정보 설정
 * 기능 : settingResourceInfo
 *********************************************************/
function settingResourceInfo(){
    var selected = w2ui['resource_GroupGrid'].getSelection();
    var record = w2ui['resource_GroupGrid'].get(selected);
    if(record == null) {
        w2alert("리소스 정보 설정 중 에러가 발생 했습니다.");
        return;
    }
    iaas = record.iaasType;
    $("input[name=resourceInfoId]").val(record.recid);
    $("input[name=resourceConfigName]").val(record.resourceConfigName);
    $("select[name=iaasType]").val(record.iaasType);
    //$("select[name=iaasConfigId]").val(record.iaasConfigAlias);
    getStemcellList(iaas);
    $("input[name=instanceType]").val(record.instanceType);
    $("input[name=vmPassword]").val(record.vmPassword);
}

/********************************************************
 * 설명 : 리소스 정보 목록 조회
 * 기능 : doSearch
 *********************************************************/
function doSearch() {
    resourceConfigInfo = [];//리소스 정보
    iaas = "";
    resetForm();
    
    w2ui['resource_GroupGrid'].clear();
    w2ui['resource_GroupGrid'].load('/deploy/hbBootstrap/resourceConfig/list');
    doButtonStyle(); 
}

/********************************************************
 * 설명 : 초기 버튼 스타일
 * 기능 : doButtonStyle
 *********************************************************/
function doButtonStyle() {
    $('#deleteBtn').attr('disabled', true);
}

/********************************************************
 * 설명 : 리소스 정보 등록
 * 기능 : registBootstrapResourceConfigInfo
 *********************************************************/
function registBootstrapResourceConfigInfo(){
    w2popup.lock("등록 중입니다.", true);
    resourceConfigInfo = {
            id                     : $("input[name=resourceInfoId]").val(),
            iaasType               : $("select[name=iaasType]").val(),
            resourceConfigName     : $("input[name=resourceConfigName]").val(),
            stemcellName           : $("select[name=stemcellName]").val(),
            instanceType           : $("input[name=instanceType]").val(),
            vmPassword             : $("input[name=vmPassword]").val()
            
    }
    $.ajax({
        type : "PUT",
        url : "/deploy/hbBootstrap/resourceConfig/save",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(resourceConfigInfo),
        success : function(data, status) {
            doSearch();
        },
        error : function( e, status ) {
            w2popup.unlock();
            var errorResult = JSON.parse(e.responseText);
            w2alert(errorResult.message, "리소스 정보 저장");
        }
    });
}

/********************************************************
 * 설명 : 리소스 정보 삭제
 * 기능 : deleteBootstrapResourceConfigInfo
 *********************************************************/
function deleteBootstrapResourceConfigInfo(id, resourceConfigName){
    w2popup.lock("삭제 중입니다.", true);
    resourceInfo = {
        id : id,
        resourceConfigName : resourceConfigName
    }
    $.ajax({
        type : "DELETE",
        url : "/deploy/hbBootstrap/resourceConfig/delete",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(resourceInfo),
        success : function(status) {
            w2popup.unlock();
            w2popup.close();
            doSearch();
        }, error : function(request, status, error) {
            w2popup.unlock();
            w2popup.close();
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message);
        }
    });
}

/******************************************************************
 * 기능 : getStemcellList
 * 설명 : 스템셀 목록 조회
 ***************************************************************** */
function getStemcellList(iaas){
    
    var url = "/common/deploy/stemcell/list/bootstrap/" + iaas;
    $.ajax({
        type : "GET",
        url : url,
        contentType : "application/json",
        async : true,
        success : function(data, status) {
            console.log(iaas);
            stemcells = new Array();
            if(data.records.length != 0){
                var option = "";
                data.records.map(function (obj){
                    option += "<option "+ obj.stemcellFileName +">"+obj.stemcellFileName+"</option>";
                });
            }else if (data.records.length == 0){
                if (iaas == 'nothing'){
                    option = "<option value=''>인프라 환경을 먼저 선택하세요.</option>";
                }else{
                    option = "<option value=''>스템셀이 없습니다.</option>";
                }
            }
            $("#stemcellName").html(option);
        },
        error : function( e, status ) {
            w2alert(search_data_fail_msg, "BOOTSTRAP 설치");
            resetForm();
        }
    });
}

/********************************************************
 * 설명 : 화면 리사이즈시 호출
 *********************************************************/
$( window ).resize(function() {
    setLayoutContainerHeight();
});

/********************************************************
 * 설명 : Lock 팝업 메세지 Function
 * 기능 : lock
 *********************************************************/
function lock (msg) {
    w2popup.lock(msg, true);
}
/********************************************************
 * 설명 : 다른 페이지 이동 시 호출 Function
 * 기능 : clearMainPage
 *********************************************************/
function clearMainPage() {
    $().w2destroy('layout2');
    $().w2destroy('resource_GroupGrid');
}
/********************************************************
 * 설명 : 리소스 정보 리셋
 * 기능 : resetForm
 *********************************************************/
function resetForm(status){
    $(".panel-body").find("p").remove();
    $(".panel-body").children().children().children().css("borderColor", "#bbb");
    $("input[name=resourceConfigName]").val("");
    $("select[name=iaasType]").val("");
    $("select[name=stemcellName]").val("");
    $("select[name=stemcellName]").html("<option value=''>인프라 환경을 먼저 선택하세요.</option>");
    $("input[name=instanceType]").val("");
    $("input[name=vmPassword]").val("");
    $("input[name=resourceInfoId]").val("");
    resourceConfigInfo = [];
    if(status=="reset"){
        w2ui['resource_GroupGrid'].clear();
        $("select[name=stemcellName]").html("<option value=''>인프라 환경을 먼저 선택하세요.</option>");
        doSearch();
    }
    document.getElementById("settingForm").reset();
}

</script>
<div id="main">
    <div class="page_site">이종 BOOTSTRAP 설치 > <strong>Resource 정보 관리</strong></div>
    <!-- 사용자 목록-->
    <div class="pdt20">
        <div class="title fl"> 리소스 정보 목록</div>
    </div>
    <div id="resource_GroupGrid" style="width:100%;  height:700px;"></div>
</div>


<div id="regPopupDiv" hidden="true" >
    <form id="settingForm" action="POST" >
    <input type="hidden" name="resourceInfoId" />
        <div class="w2ui-page page-0" style="">
           <div class="panel panel-default">
               <div class="panel-heading"><b>리소스 정보</b></div>
               <div class="panel-body" style="height:615px; overflow-y:auto;">
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">리소스 정보 별칭</label>
                       <div>
                           <input class="form-control" name = "resourceConfigName" type="text"  maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="리소스 별칭을 입력 하세요."/>
                       </div>
                   </div>
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">클라우드 인프라 환경</label>
                       <div>
                           <select class="form-control" onchange="getStemcellList(this.value);" name="iaasType" style="width: 320px; margin-left: 20px;">
                               <option value="nothing">인프라 환경을 선택하세요.</option>
                               <option value="aws">AWS</option>
                               <option value="openstack">Openstack</option>
                           </select>
                       </div>
                   </div>
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">스템셀 명</label>
                       <div>
                           <select class="form-control" name="stemcellName" id="stemcellName" style="width: 320px; margin-left: 20px;">
                               <option value="">스템셀을 선택하세요.</option>
                           </select>
                       </div>
                   </div>
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">인스턴스 유형</label>
                       <div>
                           <input class="form-control"  name="instanceType" type="text" maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="인스턴스 유형 입력하세요."/>
                       </div>
                   </div>
               </div>
           </div>
        </div>
    </form>
    <div id="regPopupBtnDiv" style="text-align: center; margin-top: 5px;">
        <sec:authorize access="hasAuthority('DEPLOY_HBBOOTSTRAP_RESOURCE_ADD')">
            <span id="installBtn" onclick="$('#settingForm').submit();" class="btn btn-primary">등록</span>
        </sec:authorize>
        <span id="resetBtn" onclick="resetForm('reset');" class="btn btn-info">취소</span>
        <sec:authorize access="hasAuthority('DEPLOY_HBBOOTSTRAP_RESOURCE_DELETE')">
            <span id="deleteBtn" class="btn btn-danger">삭제</span>
        </sec:authorize>
    </div>
</div>
<script>
$(function() {
    $("#settingForm").validate({
        ignore : [],
        //onfocusout: function(element) {$(element).valid()},
        rules: {
            resourceConfigName: { 
                required: function(){
                    return checkEmpty( $("input[name='resourceConfigName']").val() );
                }
            }, iaasType: { 
                required: function(){
                    return checkEmpty( $("select[name='iaasType']").val() );
                }
            }, stemcellName: { 
                required: function(){
                    return checkEmpty( $("select[name='stemcellName']").val() );
                }
            }, instanceType: { 
                required: function(){
                    return checkEmpty( $("input[name='instanceType']").val() );
                }
            }
        }, messages: {
            resourceConfigName: { 
                required:  "리소스 별칭"+text_required_msg
            }, iaasType: { 
                required:  "클라우드 인프라 환경 타입"+select_required_msg,
            }, stemcellName: { 
                required:  "Stemcell Name"+select_required_msg,
            }, instanceType: { 
                required:  "Instance Type"+text_required_msg,
            }
        }, unhighlight: function(element) {
            setHybridSuccessStyle(element);
        },errorPlacement: function(error, element) {
            //do nothing
        }, invalidHandler: function(event, validator) {
            var errors = validator.numberOfInvalids();
            if (errors) {
                setHybridInvalidHandlerStyle(errors, validator);
            }
        }, submitHandler: function (form) {
            registBootstrapResourceConfigInfo();
        }
    });
});


</script>