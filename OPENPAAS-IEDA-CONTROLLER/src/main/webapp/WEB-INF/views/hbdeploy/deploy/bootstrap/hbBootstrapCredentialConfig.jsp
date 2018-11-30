<%
/* =================================================================
 * 작성일 : 2018.07
 * 작성자 : 이동현
 * 상세설명 :  디렉터 인증서 관리 화면
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri = "http://www.springframework.org/tags" %>

<script type="text/javascript">
var save_lock_msg = '<spring:message code="common.save.data.lock"/>';//등록 중 입니다.
var text_required_msg = '<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
var text_injection_msg='<spring:message code="common.text.validate.sqlInjection.message"/>';//입력하신 값은 입력하실 수 없습니다.
var select_required_msg='<spring:message code="common.select.vaildate.required.message"/>';//을(를) 선택하세요.
var text_ip_msg = '<spring:message code="common.text.validate.ip.message"/>';//IP을(를) 확인 하세요.
var credentialConfigInfo = "";//Credentail 정보
var iaas = "";
var credentialLayout = {
        layout2: {
            name: 'layout2',
            padding: 4,
            panels: [
                { type: 'left', size: '65%', resizable: true, minSize: 300 },
                { type: 'main', minSize: 300 }
            ]
        },
        /********************************************************
         *  설명 : 네트워크 정보 목록 Grid
        *********************************************************/
        grid: {
            name: 'credential_GroupGrid',
            header: '<b>디렉터 인증서 정보</b>',
            method: 'GET',
                multiSelect: false,
            show: {
                    selectColumn: true,
                    footer: true},
            style: 'text-align: center',
            columns:[
                   { field: 'recid', hidden: true },
                   { field: 'credentialConfigName', caption: '디렉터 인증서 별칭', size:'50%', style:'text-align:center;' },
                   { field: 'iaasType', caption: '인프라 환경 타입', size:'50%', style:'text-align:center;' ,render: function(record){ 
                       if(record.iaasType.toLowerCase() == "aws"){
                           return "<img src='images/iaasMgnt/aws-icon.png' width='80' height='30' />";
                       }else if (record.iaasType.toLowerCase() == "openstack"){
                           return "<img src='images/iaasMgnt/openstack-icon.png' width='90' height='35' />";
                       }
                   }},
                   { field: 'credentialKeyName', caption: '디렉터 인증서 파일 명', size:'60%', style:'text-align:center;'},
                   { field: 'directorPublicIp', caption: '디렉터 Public IP', size:'50%', style:'text-align:center;'},
                   { field: 'directorPrivateIp', caption: '디렉터 Private IP', size:'50%', style:'text-align:center;'}
                  ],
            onSelect : function(event) {
                event.onComplete = function() {
                    $('#deleteBtn').attr('disabled', false);
                    settingCredentialInfo();
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
    
    $('#credential_GroupGrid').w2layout(credentialLayout.layout2);
    w2ui.layout2.content('left', $().w2grid(credentialLayout.grid));
    w2ui['layout2'].content('main', $('#regPopupDiv').html());
    doSearch();
    
    $("#deleteBtn").click(function(){
        if($("#deleteBtn").attr('disabled') == "disabled") return;
        var selected = w2ui['credential_GroupGrid'].getSelection();
        if( selected.length == 0 ){
            w2alert("선택된 정보가 없습니다.", "디렉터 인증서 삭제");
            return;
        }
        else {
            var record = w2ui['credential_GroupGrid'].get(selected);
            w2confirm({
                title        : "디렉터 인증서 정보",
                msg            : "디렉터 인증서 정보 ("+record.credentialConfigName + ")을 삭제하시겠습니까?",
                yes_text    : "확인",
                no_text        : "취소",
                yes_callBack: function(event){
                    deleteBootstrapcredentialConfigInfo(record.recid, record.credentialConfigName);
                },
                no_callBack    : function(){
                    w2ui['credential_GroupGrid'].clear();
                    doSearch();
                }
            });
        }
    });
});

/********************************************************
 * 설명 : 네트워크 목록 조회
 * 기능 : settingNetworkConfigInfo
 *********************************************************/
function settingNetworkConfigInfo(iaasType){
    $("input[name=directorPublicIp]").val("");
    $("input[name=directorPrivateIp]").val("");
    $("select[name=networkConfigInfo]").removeAttr("disabled");
    $("select[name=networkConfigInfo]").html("<option value='' >네트워크 별칭을 선택하세요.</option>");
    if(iaasType==""){
        $("select[name=iaasConfigId]").html("<option value='' >인프라 환경 별칭을 선택하세요.</option>");
        $("select[name=networkConfigInfo]").attr("disabled", "disabled");
        return;
    }
    $.ajax({
        type :"GET",
        url :"/deploy/hbBootstrap/credential/networkList/"+iaasType+"", 
        contentType :"application/json",
        success :function(data, status) {
            if( !checkEmpty(data) ){
                var options = "";
                for(var i=0; i<data.length; i++) {
                    if(data[i].id == credentialConfigInfo.networkConfigId) {
                        options+= "<option value='"+data[i].id+"' selected>"+data[i].networkConfigName+"</option>";
                        getNetworkConfigInfo(data[i].id, iaas);
                    } else {
                        options+= "<option value='"+data[i].id+"'>"+data[i].networkConfigName+"</option>";
                    }
                }
                $("select[name=networkConfigInfo]").append(options);
            }
        },
        error :function(request, status, error) {
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "네트워크 별칭 정보 목록 조회");
        }
    });
}

/********************************************************
 * 설명 : 네트워크 정보 디렉터 IP 정보 설정
 * 기능 : getNetworkConfigInfo
 *********************************************************/
function getNetworkConfigInfo(val){
    if(val != ""){
        iaas =  $("select[name=iaasType]").val();
        $.ajax({
            type :"GET",
            url :"/deploy/hbBootstrap/credential/networkInfo/"+val+"/"+iaas+"", 
            contentType :"application/json",
            success :function(data, status) {
                if( !checkEmpty(data) ){
                    $("input[name=directorPublicIp]").val(data.publicStaticIp);
                    $("input[name=directorPrivateIp]").val(data.privateStaticIp);
                }
            },
            error :function(request, status, error) {
                var errorResult = JSON.parse(request.responseText);
                w2alert(errorResult.message, "네트워크 별칭 정보 목록 조회");
            }
        });
    } else {
        $("input[name=networkConfigInfo]").val("");
        $("input[name=directorPublicIp]").val("");
        $("input[name=directorPrivateIp]").val("");
    }
}

/********************************************************
 * 설명 : 인증서 수정 정보 설정
 * 기능 : settingCredentialInfo
 *********************************************************/
function settingCredentialInfo(){
    var selected = w2ui['credential_GroupGrid'].getSelection();
    var record = w2ui['credential_GroupGrid'].get(selected);
    if(record == null) {
        w2alert("디렉터 인증서 정보 설정 중 에러가 발생 했습니다.");
        return;
    }
    iaas = record.iaasType;
    $("input[name=credentialInfoId]").val(record.recid);
    $("input[name=credentialConfigName]").val(record.credentialConfigName);
    $("input[name=credentialKeyName]").val(record.credentialKeyName);
    $("input[name=directorPrivateIp]").val(record.directorPrivateIp);
    $("input[name=directorPublicIp]").val(record.directorPublicIp);
    $("select[name=iaasType]").val(record.iaasType);
    
    credentialConfigInfo = {
        networkConfigId : record.networkConfigName
    }
    settingNetworkConfigInfo(iaas);
    
}

/********************************************************
 * 설명 : 인증서 정보 목록 조회
 * 기능 : doSearch
 *********************************************************/
function doSearch() {
    credentialConfigInfo="";//네트워크 정보
    iaas = "";
    resetForm();
    w2ui['credential_GroupGrid'].clear();
    w2ui['credential_GroupGrid'].load('/deploy/hbBootstrap/credential/list');
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
 * 설명 : 인증서 정보 등록
 * 기능 : registBootstrapCredentialConfigInfo
 *********************************************************/
function registBootstrapCredentialConfigInfo(){
    credentialConfigInfo = {
            id                     : $("input[name=credentialInfoId]").val(),
            iaasType               : $("select[name=iaasType]").val(),
            networkConfigName      : $("select[name=networkConfigInfo]").val(),
            credentialConfigName   : $("input[name=credentialConfigName]").val(),
            directorPublicIp       : $("input[name=directorPublicIp]").val(),
            directorPrivateIp      : $("input[name=directorPrivateIp]").val()
    }
    $.ajax({
        type : "PUT",
        url : "/deploy/hbBootstrap/credential/save",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(credentialConfigInfo),
        success : function(data, status) {
            w2utils.unlock($("#layout_layout_panel_main"));
            w2alert("디렉터 인증서 파일 명은 <b><font color='red'>"+credentialConfigInfo.credentialConfigName+"-cred.yml</font></b> 입니다.");
            doSearch();
        },
        error : function( e, status ) {
            w2utils.unlock($("#layout_layout_panel_main"));
            var errorResult = JSON.parse(e.responseText);
            w2alert(errorResult.message, "디렉터 인증서 정보 저장");
        }
    });
}

/********************************************************
 * 설명 : 인증서 정보 삭제
 * 기능 : deleteBootstrapcredentialConfigInfo
 *********************************************************/
function deleteBootstrapcredentialConfigInfo(id, credentialConfigName){
    w2popup.lock("삭제 중입니다.", true);
    credentialInfo = {
        id : id,
        credentialConfigName: credentialConfigName
    }
    $.ajax({
        type : "DELETE",
        url : "/deploy/hbBootstrap/credential/delete",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(credentialInfo),
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
    $().w2destroy('credential_GroupGrid');
}
/********************************************************
 * 설명 : 인증서 정보 리셋
 * 기능 : resetForm
 *********************************************************/
function resetForm(status){
    credentialConfigInfo = "";
    $(".panel-body").find("p").remove();
    $(".panel-body").children().children().children().css("borderColor", "#bbb");
    $("input[name=credentialConfigName]").val("");
    $("input[name=directorPublicIp]").val("");
    $("input[name=directorPrivateIp]").val("");
    $("input[name=credentialInfoId]").val("");
    $("select[name=iaasType]").val("");
    $("select[name=networkConfigInfo]").attr("disabled", "disabled");
    $("select[name=networkConfigInfo]").html("<option value='' >네트워크 별칭을 선택하세요.</option>");
    if(status=="reset"){
        w2ui['credential_GroupGrid'].clear();
        doSearch();
    }
    document.getElementById("settingForm").reset();
}

</script>
<div id="main">
    <div class="page_site">이종 BOOTSTRAP 설치 > <strong>디렉터 인증서 관리</strong></div>
    <!-- 사용자 목록-->
    <div class="pdt20">
        <div class="title fl"> 디렉터 인증서 목록</div>
    </div>
    <div id="credential_GroupGrid" style="width:100%;  height:700px;"></div>

</div>


<div id="regPopupDiv" hidden="true" >
    <form id="settingForm" action="POST" >
    <input type="hidden" name="credentialInfoId" />
        <div class="w2ui-page page-0" style="">
           <div class="panel panel-default">
               <div class="panel-heading"><b>디렉터 인증서 정보</b><p style="color:red;">BOOTSTRAP 설치 시 Public IP를 사용할 경우만 Public IP 값을 입력하세요.</p></div>
               <div class="panel-body" style="height:560px; overflow-y:auto;">
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">디렉터 인증서 별칭</label>
                       <div>
                           <input class="form-control" name = "credentialConfigName" type="text"  maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="디렉터 인증서 별칭을 입력 하세요."/>
                       </div>
                   </div>
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">클라우드 인프라 환경</label>
                       <div>
                           <select class="form-control" onchange ="settingNetworkConfigInfo(this.value);" name="iaasType" style="width: 320px; margin-left: 20px;">
                               <option value="">인프라 환경을 선택하세요.</option>
                               <option value="aws">AWS</option>
                               <option value="openstack">Openstack</option>
                           </select>
                       </div>
                   </div>
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">네트워크 별칭</label>
                       <div>
                           <select disabled="disabled" class="form-control" onchange="getNetworkConfigInfo(this.value);" name="networkConfigInfo" style="width: 320px; margin-left: 20px;">
                               <option value="">네트워크 별칭을 선택하세요.</option>
                           </select>
                       </div>
                   </div>
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">디렉터 Public IP</label>
                       
                       <div>
                           <input class="form-control"  readonly name="directorPublicIp" type="text" maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="디렉터 Public IP를 입력하세요."/>
                       </div>
                   </div>
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">디렉터 Private IP</label>
                       <div>
                           <input class="form-control" readonly  name="directorPrivateIp" type="text" maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="디렉터 Private IP를 입력하세요."/>
                       </div>
                   </div>
               </div>
           </div>
        </div>
    </form>
    <div id="regPopupBtnDiv" style="text-align: center; margin-top: 5px;">
        <sec:authorize access="hasAuthority('DEPLOY_HBBOOTSTRAP_CREDENTIAL_ADD')">
            <span id="installBtn" onclick="$('#settingForm').submit();" class="btn btn-primary">등록</span>
        </sec:authorize>
        <span id="resetBtn" onclick="resetForm('reset');" class="btn btn-info">취소</span>
        <sec:authorize access="hasAuthority('DEPLOY_HBBOOTSTRAP_CREDENTIAL_DELETE')">
            <span id="deleteBtn" class="btn btn-danger">삭제</span>
        </sec:authorize>
    </div>
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
        //onfocusout: true,
        rules: {
            directorPrivateIp : {
                required : function(){
                    return checkEmpty( $("input[name='directorPrivateIp']").val() );
                },  ipv4 : function(){
                    return $("input[name='directorPrivateIp']").val();
                }
            },
            credentialConfigName : {
                required : function(){
                    return checkEmpty( $("input[name='credentialConfigName']").val() );
                }, sqlInjection : function(){
                    return $("input[name='credentialConfigName']").val();
                }
            },
            iaasType : {
                required : function(){
                    return checkEmpty( $("select[name='iaasType']").val() );
                }
            },
            networkConfigInfo : {
                required : function(){
                    return checkEmpty( $("select[name='networkConfigInfo']").val() );
                }
            },
        }, messages: {
            credentialConfigName: { required:  "디렉터 인증서 별칭" + text_required_msg },
            directorPrivateIp: {  required:  "디렉터 Private IP" + text_required_msg , ipv4: text_ip_msg},
            networkConfigInfo: {  required:  "네트워크 별칭" + text_required_msg },
            iaasType: {  required:  "클라우드 인프라 환경" + text_required_msg }
            
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
            w2utils.lock($("#layout_layout_panel_main"), save_lock_msg, true);
            registBootstrapCredentialConfigInfo();
        }
    });
});

function validateIpv4(params){
    return /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(params);
}

</script>