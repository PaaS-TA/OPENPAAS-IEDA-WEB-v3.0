<%
/* =================================================================
 * 작성일 : 2018.06
 * 작성자 : 이동현
 * 상세설명 : CPI 인증서 관리 화면
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri = "http://www.springframework.org/tags" %>

<script type="text/javascript">
var text_required_msg = '<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
var text_injection_msg='<spring:message code="common.text.validate.sqlInjection.message"/>';//입력하신 값은 입력하실 수 없습니다.
var select_required_msg='<spring:message code="common.select.vaildate.required.message"/>';//을(를) 선택하세요.
var iaasConfigInfo = "";//인프라 환경 설정 정ㅈ보
var iaas = "";
var cpiLayout = {
        layout2: {
            name: 'layout2',
            padding: 4,
            panels: [
                { type: 'left', size: '65%', resizable: true, minSize: 300 },
                { type: 'main', minSize: 300 }
            ]
        },
        /********************************************************
         *  설명 : 디렉터 인증서 Grid
        *********************************************************/
        grid: {
            name: 'cpi_GroupGrid',
            header: '<b>CPI 정보</b>',
            method: 'GET',
                multiSelect: false,
            show: {
                    selectColumn: true,
                    footer: true},
            style: 'text-align: center',
            columns:[
                   { field: 'recid', hidden: true },
                   { field: 'cpiName', caption: 'CPI 정보 별칭', size:'50%', style:'text-align:center;' },
                   { field: 'iaasType', caption: '인프라 환경 타입', size:'50%', style:'text-align:center;' ,render: function(record){ 
                       if(record.iaasType.toLowerCase() == "aws"){
                           return "<img src='images/iaasMgnt/aws-icon.png' width='80' height='30' />";
                       }else if (record.iaasType.toLowerCase() == "openstack"){
                           return "<img src='images/iaasMgnt/openstack-icon.png' width='90' height='35' />";
                       }
                   }},
                   { field: 'iaasConfigAlias', caption: '인프라 환경 설정 별칭', size:'50%', style:'text-align:center;'},
                   { field: 'commonAccessUser', caption: '인프라 사용자 명', size:'60%', style:'text-align:center;'},
                   { field: 'commonSecurityGroup', caption: '보안 그룹', size:'50%', style:'text-align:center;'},
                   { field: 'commonAvailabilityZone', caption: '가용 영역', size:'50%', style:'text-align:center;',render: function(record){ 
                       if(record.iaasType.toLowerCase() == "aws"){
                           return record.commonAvailabilityZone;
                       }else if (record.iaasType.toLowerCase() == "openstack"){
                           if(record.openstackVersion == "v2"){
                               return record.commonTenant;
                           } else if (record.openstackVersion == "v3") {
                               return record.commonProject;
                           }
                       }
                   }},
                   { field: 'commonKeypairName', caption: 'Key Pair 명', size:'50%', style:'text-align:center;'},
                   { field: 'commonKeypairPath', caption: 'Key Pair Path', size:'50%', style:'text-align:center;'}
                  ],
            onSelect : function(event) {
                event.onComplete = function() {
                    $('#deleteBtn').attr('disabled', false);
                    settingCpiInfo();
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
    $('#cpi_GroupGrid').w2layout(cpiLayout.layout2);
    w2ui.layout2.content('left', $().w2grid(cpiLayout.grid));
    w2ui['layout2'].content('main', $('#regPopupDiv').html());
    doSearch();
    
    $("#deleteBtn").click(function(){
        if($("#deleteBtn").attr('disabled') == "disabled") return;
        var selected = w2ui['cpi_GroupGrid'].getSelection();
        if( selected.length == 0 ){
            w2alert("선택된 정보가 없습니다.", "디렉터 인증서 삭제");
            return;
        }
        else {
            var record = w2ui['cpi_GroupGrid'].get(selected);
            w2confirm({
                title        : "CPI 정보",
                msg            : "CPI ("+record.cpiName + ")을 삭제하시겠습니까?",
                yes_text    : "확인",
                no_text        : "취소",
                yes_callBack: function(event){
                    deleteBootstrapCpiConfigInfo(record.recid, record.cpiName);
                },
                no_callBack    : function(){
                    w2ui['cpi_GroupGrid'].clear();
                    doSearch();
                }
            });
        }
    });
});


/********************************************************
 * 설명 : 인프라 환경 설정 별칭 목록 조회
 * 기능 : getIaasConfigAliasList
 *********************************************************/
function getIaasConfigAliasList(iaasType){
    $("input[name=commonAccessUser]").val("");
    $("input[name=commonSecurityGroup]").val("");
    $("input[name=commonAvailabilityZone]").val("");
    $("input[name=commonKeypairName]").val("");
    $("input[name=commonKeypairPath]").val("");
    if(iaasType==""){
        w2alert("클라우드 인프라 환경을 선택하세요.");
        $("select[name=iaasConfigId]").html("<option value='' >인프라 환경 별칭을 선택하세요.</option>");
        $("select[name=iaasConfigId]").attr("disabled", "disabled");
        return;
    }
    iaas = iaasType;
    $.ajax({
        type :"GET",
        url :"/common/deploy/list/iaasConfig/"+iaas, 
        contentType :"application/json",
        success :function(data, status) {
            $("select[name=iaasConfigId]").html("<option value='' >인프라 환경 별칭을 선택하세요.</option>");
            if($("select[name=iaasConfigId]").attr("disabled") == "disabled"){
                $("select[name=iaasConfigId]").removeAttr("disabled");
            }
            if( !checkEmpty(data) ){
                var options= "";
                for( var i=0; i<data.length; i++ ){
                    if( data[i].id == iaasConfigInfo.iaasConfigId ){
                        options+= "<option value='"+data[i].id+"' selected>"+data[i].iaasConfigAlias+"</option>";
                        settingIaasConfigInfo(data[i].id);
                    }else{
                        options+= "<option value='"+data[i].id+"'>"+data[i].iaasConfigAlias+"</option>";
                    }
                }
                $("select[name=iaasConfigId]").append(options);
                
            }
        },
        error :function(request, status, error) {
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "인프라 환경설정 정보 목록 조회");
        }
    });
}


/********************************************************
 * 설명 : 인프라 환경 설정 별칭 선택 시 정보 설정
 * 기능 : settingIaasConfigInfo
 *********************************************************/
function settingIaasConfigInfo(val){
    if( !checkEmpty(val) ){
         $.ajax({
            type :"GET",
            url :"/common/deploy/list/iaasConfig/"+iaas+"/"+val, 
            contentType :"application/json",
            success :function(data, status) {
                if( !checkEmpty(data) ){
                    $("input[name=commonAccessUser]").val(data.commonAccessUser);
                    $("input[name=commonSecurityGroup]").val(data.commonSecurityGroup);
                    $("input[name=commonKeypairName]").val(data.commonKeypairName);
                    $("input[name=commonKeypairPath]").val(data.commonKeypairPath);
                    if( data.openstackKeystoneVersion == "v2" ){
                        $("input[name=commonAvailabilityZone]").val(data.commonTenant);
                    }else if(data.openstackKeystoneVersion == "v3" ){
                        $("input[name=commonAvailabilityZone]").val(data.commonProject);
                    } else {
                        $("input[name=commonAvailabilityZone]").val(data.commonAvailabilityZone);
                    }
                }
            },
            error :function(request, status, error) {
                var errorResult = JSON.parse(request.responseText);
                w2alert(errorResult.message, "인프라 환경설정 정보 목록 조회");
            }
        });
    }else{
        var div = iaas+"InfoDiv";
        var elements = $("div#"+div).find("input");
        for( var i=0; i<elements.length; i++ ){
            $(".w2ui-msg-body input[name='"+elements[i].name+"']" ).val("");
        }
        var textarea = $("div#"+div).find("textarea"); 
        if( textarea.length >0 ) $(".w2ui-msg-body textarea[name='"+textarea[0].name+"']" ).val("");
    }
}

/********************************************************
 * 설명 : CPI 수정 정보 설정
 * 기능 : settingCpiInfo
 *********************************************************/
function settingCpiInfo(){
    var selected = w2ui['cpi_GroupGrid'].getSelection();
    var record = w2ui['cpi_GroupGrid'].get(selected);
    if(record == null) {
        w2alert("CPI 정보 설정 중 에러가 발생 했습니다.");
        return;
    }
    iaas = record.iaasType;
    $("select[name=iaasConfigId]").removeAttr("disabled");
    getIaasConfigAliasList(iaas);
    iaasConfigInfo = {
        iaasConfigId : record.iaasConfigId
    }
    $("input[name=cpiInfoId]").val(record.cpiInfoId);
    $("input[name=cpiConfigName]").val(record.cpiName);
    $("select[name=iaasType]").val(record.iaasType);
    $("select[name=iaasConfigId]").val(record.iaasConfigAlias);
    settingIaasConfigInfo(record.iaasConfigId);
}

/********************************************************
 * 설명 : 인증서 목록 조회
 * 기능 : doSearch
 *********************************************************/
function doSearch() {
    iaasConfigInfo="";//인프라 환경 설정 정ㅈ보
    iaas = "";
    resetForm();
    
    w2ui['cpi_GroupGrid'].clear();
    //w2ui['regPopupDiv'].clear();
    w2ui['cpi_GroupGrid'].load('/deploy/hbBootstrap/cpi/list');
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
 * 설명 : CPI 정보 등록
 * 기능 : registBootstrapCpiConfigInfo
 *********************************************************/
function registBootstrapCpiConfigInfo(){
    w2popup.lock("등록 중입니다.", true);
    iaasConfigInfo = {
            cpiInfoId:    $("input[name=cpiInfoId]").val(),
            iaasType     : iaas,
            iaasConfigId : $("select[name=iaasConfigId]").val(),
            cpiName      : $("input[name=cpiConfigName]").val()
    }
    $.ajax({
        type : "PUT",
        url : "/deploy/hbBootstrap/cpi/save",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(iaasConfigInfo),
        success : function(data, status) {
            doSearch();
        },
        error : function( e, status ) {
            w2popup.unlock();
            var errorResult = JSON.parse(e.responseText);
            w2alert(errorResult.message, "CPI 정보 저장");
        }
    });
}

/********************************************************
 * 설명 : CPI 정보 삭제
 * 기능 : deleteBootstrapCpiConfigInfo
 *********************************************************/
function deleteBootstrapCpiConfigInfo(id, cpiName){
    w2popup.lock("삭제 중입니다.", true);
    iaasConfigInfo = {
        cpiInfoId : id,
        cpiName : cpiName
    }
    $.ajax({
        type : "DELETE",
        url : "/deploy/hbBootstrap/cpi/delete",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(iaasConfigInfo),
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
    $().w2destroy('cpi_GroupGrid');
}
/********************************************************
 * 설명 : CPI 정보 리셋
 * 기능 : resetForm
 *********************************************************/
function resetForm(status){
    $(".panel-body").find("p").remove();
    $(".panel-body").children().children().children().css("borderColor", "#bbb");
    $("input[name=commonAccessUser]").val("");
    $("input[name=commonSecurityGroup]").val("");
    $("input[name=commonAvailabilityZone]").val("");
    $("input[name=commonKeypairName]").val("");
    $("input[name=commonKeypairPath]").val("");
    $("input[name=cpiConfigName]").val("");
    $("select[name=iaasType]").val("");
    $("select[name=iaasConfigId]").html("<option value='' >인프라 환경 별칭을 선택하세요.</option>");
    $("input[name=cpiInfoId]").val("");
    $("select[name=iaasConfigId]").attr("disabled", "disabled");
    if(status=="reset"){
        w2ui['cpi_GroupGrid'].clear();
        doSearch();
    }
    document.getElementById("settingForm").reset();
}

</script>
<div id="main">
    <div class="page_site">이종 BOOTSTRAP 설치 > <strong>CPI 정보 관리</strong></div>
    <!-- 사용자 목록-->
    <div class="pdt20">
        <div class="title fl"> CPI 정보 목록</div>
    </div>
    <div id="cpi_GroupGrid" style="width:100%;  height:700px;"></div>

</div>


<div id="regPopupDiv" hidden="true" >
    <form id="settingForm" action="POST" >
    <input type="hidden" name="cpiInfoId" />
        <div class="w2ui-page page-0" style="">
           <div class="panel panel-default">
               <div class="panel-heading"><b>CPI 정보</b></div>
               <div class="panel-body" style="height:615px; overflow-y:auto;">
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">CPI 정보 별칭</label>
                       <div>
                           <input class="form-control" name = "cpiConfigName" type="text"  maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="CPI 별칭을 입력 하세요."/>
                       </div>
                   </div>
                   
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">클라우드 인프라 환경</label>
                       <div>
                           <select class="form-control" onchange="getIaasConfigAliasList(this.value);" name="iaasType" style="width: 320px; margin-left: 20px;">
                               <option value="">인프라 환경을 선택하세요.</option>
                               <option value="aws">AWS</option>
                               <option value="openstack">Openstack</option>
                           </select>
                       </div>
                   </div>
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">인프라 환경 별칭</label>
                       <div>
                           <select class="form-control" disabled="disabled"  name="iaasConfigId"  onchange="settingIaasConfigInfo(this.value);" style="width: 320px; margin-left: 20px;">
                               <option value="" >인프라 환경 별칭을 선택하세요.</option>
                           </select>
                       </div>
                   </div>
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">인프라 사용자</label>
                       <div>
                           <input class="form-control" readonly   name="commonAccessUser" type="text"  maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="인프라 환경 아이디를 입력하세요."/>
                       </div>
                   </div>
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">보안 그룹</label>
                       <div>
                           <input class="form-control"  name="commonSecurityGroup" type="text" readonly maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="보안 그룹을 입력하세요."/>
                       </div>
                   </div>
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">가용 영역</label>
                       <div>
                           <input class="form-control"  name="commonAvailabilityZone" type="text" readonly maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="가용 영역을 입력하세요."/>
                       </div>
                   </div>
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">Private Key Name</label>
                       <div>
                           <input class="form-control" name="commonKeypairName" type="text" readonly maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="Key Pair 명을 입력하세요."/>
                       </div>
                   </div>
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">Private Key Path</label>
                       <div>
                           <input class="form-control" name="commonKeypairPath" type="text" readonly maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="Key path를 입력하세요."/>
                       </div>
                   </div>
               </div>
           </div>
        </div>
    </form>
    <div id="regPopupBtnDiv" style="text-align: center; margin-top: 5px;">
        <sec:authorize access="hasAuthority('DEPLOY_HBBOOTSTRAP_CPI_ADD')">
            <span id="installBtn" onclick="$('#settingForm').submit();" class="btn btn-primary">등록</span>
        </sec:authorize>
        <span id="resetBtn" onclick="resetForm('reset');" class="btn btn-info">취소</span>
        <sec:authorize access="hasAuthority('DEPLOY_HBBOOTSTRAP_CPI_DELETE')">
        	<span id="deleteBtn" class="btn btn-danger">삭제</span>
        </sec:authorize>
    </div>
</div>
<script>
$(function() {
    $.validator.addMethod("sqlInjection", function(value, element, params) {
        return checkInjectionBlacklist(params);
    },text_injection_msg);
    
    $("#settingForm").validate({
        ignore : [],
        //onfocusout: function(element) {$(element).valid()},
        rules: {
            cpiConfigName : {
                required : function(){
                    return checkEmpty( $("input[name='cpiConfigName']").val() );
                }, sqlInjection : function(){
                    return $("input[name='cpiConfigName']").val();
                }
            },
            iaasType : {
                required : function(){
                    return checkEmpty( $("select[name='iaasType']").val() );
                }
            },
            iaasConfigId : {
                required : function(){
                    return checkEmpty( $("select[name='iaasConfigId']").val() );
                }
            }
        }, messages: {
            cpiConfigName: { required:  "CPI 정보 별칭" + text_required_msg },
            iaasType: {  required:  "클라우드 인프라 환경" + select_required_msg},
            iaasConfigId: {  required:  "인프라 환경 별칭" + select_required_msg}
        }, unhighlight: function(element) {
            setHybridSuccessStyle(element);
        },errorPlacement: function(error, element) {
            //do nothingalert("1");
        }, invalidHandler: function(event, validator) {
            var errors = validator.numberOfInvalids();
            if (errors) {
                setHybridInvalidHandlerStyle(errors, validator);
            }
        }, submitHandler: function (form) {
            registBootstrapCpiConfigInfo();
        }
    });
});
</script>