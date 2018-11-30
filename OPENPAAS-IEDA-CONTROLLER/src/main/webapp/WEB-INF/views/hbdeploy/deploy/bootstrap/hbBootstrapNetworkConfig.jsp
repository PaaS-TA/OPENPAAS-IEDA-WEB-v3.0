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
var text_cidr_msg='<spring:message code="common.text.validate.cidr.message"/>';//CIDR 대역을 확인 하세요.
var text_ip_msg = '<spring:message code="common.text.validate.ip.message"/>';//IP을(를) 확인 하세요.
var networkConfigInfo = "";//네트워크 정보
var iaas = "";
var networkLayout = {
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
            name: 'network_GroupGrid',
            header: '<b>Network 정보</b>',
            method: 'GET',
                multiSelect: false,
            show: {
                    selectColumn: true,
                    footer: true},
            style: 'text-align: center',
            columns:[
                   { field: 'recid', hidden: true },
                   { field: 'networkConfigName', caption: '네트워크 정보 별칭', size:'50%', style:'text-align:center;' },
                   { field: 'iaasType', caption: '인프라 환경 타입', size:'50%', style:'text-align:center;' ,render: function(record){ 
                       if(record.iaasType.toLowerCase() == "aws"){
                           return "<img src='images/iaasMgnt/aws-icon.png' width='80' height='30' />";
                       }else if (record.iaasType.toLowerCase() == "openstack"){
                           return "<img src='images/iaasMgnt/openstack-icon.png' width='90' height='35' />";
                       }
                   }},
                   { field: 'publicStaticIp', caption: '디렉터 Public IP', size:'50%', style:'text-align:center;'},
                   { field: 'privateStaticIp', caption: '디렉터 Private IP', size:'60%', style:'text-align:center;'},
                   { field: 'subnetId', caption: '서브넷 아이디', size:'50%', style:'text-align:center;'},
                   { field: 'subnetRange', caption: '서브넷 범위', size:'50%', style:'text-align:center;'},
                   { field: 'subnetDns', caption: 'DNS 주소', size:'50%', style:'text-align:center;'}
                  ],
            onSelect : function(event) {
                event.onComplete = function() {
                    $('#deleteBtn').attr('disabled', false);
                    settingNetworkInfo();
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
    $('#network_GroupGrid').w2layout(networkLayout.layout2);
    w2ui.layout2.content('left', $().w2grid(networkLayout.grid));
    w2ui['layout2'].content('main', $('#regPopupDiv').html());
    doSearch();
    
    $("#deleteBtn").click(function(){
        if($("#deleteBtn").attr('disabled') == "disabled") return;
        var selected = w2ui['network_GroupGrid'].getSelection();
        if( selected.length == 0 ){
            w2alert("선택된 정보가 없습니다.", "네트워크 삭제");
            return;
        }
        else {
            var record = w2ui['network_GroupGrid'].get(selected);
            w2confirm({
                title        : "네트워크 정보",
                msg            : "네트워크 정보 ("+record.networkConfigName + ")을 삭제하시겠습니까?",
                yes_text    : "확인",
                no_text        : "취소",
                yes_callBack: function(event){
                    deleteBootstrapNetworkConfigInfo(record.recid, record.networkConfigName);
                },
                no_callBack    : function(){
                    w2ui['network_GroupGrid'].clear();
                    doSearch();
                }
            });
        }
    });
});

/********************************************************
 * 설명 : 네트워크 수정 정보 설정
 * 기능 : settingNetworkInfo
 *********************************************************/
function settingNetworkInfo(){
    var selected = w2ui['network_GroupGrid'].getSelection();
    var record = w2ui['network_GroupGrid'].get(selected);
    if(record == null) {
        w2alert("CPI 정보 설정 중 에러가 발생 했습니다.");
        return;
    }
    iaas = record.iaasType;
    $("input[name=networkInfoId]").val(record.recid);
    $("input[name=networkConfigName]").val(record.networkConfigName);
    $("input[name=subnetId]").val(record.subnetId);
    $("input[name=privateStaticIp]").val(record.privateStaticIp);
    $("input[name=subnetRange]").val(record.subnetRange);
    $("input[name=subnetGateway]").val(record.subnetGateway);
    $("input[name=subnetDns]").val(record.subnetDns);
    $("input[name=publicStaticIp]").val(record.publicStaticIp);
    $("select[name=iaasType]").val(record.iaasType);
    $("select[name=iaasConfigId]").val(record.iaasConfigAlias);
}

/********************************************************
 * 설명 : 네트워크 정보 목록 조회
 * 기능 : doSearch
 *********************************************************/
function doSearch() {
    networkConfigInfo="";//네트워크 정보
    iaas = "";
    resetForm();
    
    w2ui['network_GroupGrid'].clear();
    w2ui['network_GroupGrid'].load('/deploy/hbBootstrap/network/list');
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
 * 설명 : 네트워크 정보 등록
 * 기능 : registBootstrapNetworkConfigInfo
 *********************************************************/
function registBootstrapNetworkConfigInfo(){
    w2popup.lock("등록 중입니다.", true);
    networkConfigInfo = {
            id                     : $("input[name=networkInfoId]").val(),
            iaasType               : $("select[name=iaasType]").val(),
            publicStaticIp         : $("input[name=publicStaticIp]").val(),
            privateStaticIp        : $("input[name=privateStaticIp]").val(),
            subnetId               : $("input[name=subnetId]").val(),
            subnetRange            : $("input[name=subnetRange]").val(),
            subnetGateway          : $("input[name=subnetGateway]").val(),
            subnetDns              : $("input[name=subnetDns]").val(),
            networkConfigName      : $("input[name=networkConfigName]").val()
    }
    $.ajax({
        type : "PUT",
        url : "/deploy/hbBootstrap/network/save",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(networkConfigInfo),
        success : function(data, status) {
            doSearch();
        },
        error : function( e, status ) {
            w2popup.unlock();
            var errorResult = JSON.parse(e.responseText);
            w2alert(errorResult.message, "네트워크 정보 저장");
        }
    });
}

/********************************************************
 * 설명 : 네트워크 정보 삭제
 * 기능 : deleteBootstrapNetworkConfigInfo
 *********************************************************/
function deleteBootstrapNetworkConfigInfo(id, networkConfigName){
    w2popup.lock("삭제 중입니다.", true);
    networkInfo = {
        id : id,
        networkConfigName : networkConfigName
    }
    $.ajax({
        type : "DELETE",
        url : "/deploy/hbBootstrap/network/delete",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(networkInfo),
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
    $().w2destroy('network_GroupGrid');
}
/********************************************************
 * 설명 : 네트워크 정보 리셋
 * 기능 : resetForm
 *********************************************************/
function resetForm(status){
    $(".panel-body").find("p").remove();
    $(".panel-body").children().children().children().css("borderColor", "#bbb");
    $("input[name=networkConfigName]").val("");
    $("input[name=subnetId]").val("");
    $("input[name=privateStaticIp]").val("");
    $("input[name=subnetRange]").val("");
    $("input[name=subnetGateway]").val("");
    $("input[name=subnetDns]").val("");
    $("select[name=iaasType]").val("");
    $("input[name=networkInfoId]").val("");
    $("input[name=publicStaticIp]").val("");
    if(status=="reset"){
        w2ui['network_GroupGrid'].clear();
        doSearch();
    }
    document.getElementById("settingForm").reset();
}

</script>
<div id="main">
    <div class="page_site">이종 BOOTSTRAP 설치 > <strong>Network 정보 관리</strong></div>
    <!-- 사용자 목록-->
    <div class="pdt20">
        <div class="title fl"> 네트워크 정보 목록</div>
    </div>
    <div id="network_GroupGrid" style="width:100%;  height:750px;"></div>

</div>


<div id="regPopupDiv" hidden="true" >
    <form id="settingForm" action="POST" >
    <input type="hidden" name="networkInfoId" />
        <div class="w2ui-page page-0" style="">
           <div class="panel panel-default">
               <div class="panel-heading"><b>네트워크 정보</b><p style="color:red;">BOOTSTRAP 설치 시 Public IP를 사용할 경우만 Public IP 값을 입력하세요.</p></div>
               <div class="panel-body" style="height:630px; overflow-y:auto;">
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">네트워크 정보 별칭</label>
                       <div>
                           <input class="form-control" name = "networkConfigName" type="text"  maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="네트워크 별칭을 입력 하세요."/>
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
                       <label style="width:40%;text-align: left;padding-left: 20px;">디렉터 Public IP</label>
                       <div>
                           <input class="form-control" name="publicStaticIp" type="text"  maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="디렉터 Public IP를 입력하세요."/>
                       </div>
                   </div>
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">디렉터 Private IP</label>
                       <div>
                           <input class="form-control"  name="privateStaticIp" type="text" maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="디렉터 Private IP를 입력하세요."/>
                       </div>
                   </div>
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">서브넷 아이디</label>
                       <div>
                           <input class="form-control"  name="subnetId" type="text" maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="서브넷 아이디를 입력하세요."/>
                       </div>
                   </div>
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">서브넷 범위</label>
                       <div>
                           <input class="form-control" name="subnetRange" type="text" maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="서브넷 범위를 입력하세요."/>
                       </div>
                   </div>
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">서브넷 게이트웨이</label>
                       <div>
                           <input class="form-control" name="subnetGateway" type="text" maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="서브넷 게이트웨이를 입력하세요."/>
                       </div>
                   </div>
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">DNS 주소</label>
                       <div>
                           <input class="form-control" name="subnetDns" type="text" maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="DNS 주소를 입력하세요."/>
                       </div>
                   </div>
                   
               </div>
           </div>
        </div>
    </form>
    <div id="regPopupBtnDiv" style="text-align: center; margin-top: 5px;">
        <sec:authorize access="hasAuthority('DEPLOY_HBBOOTSTRAP_NETWORK_ADD')">
            <span id="installBtn" onclick="$('#settingForm').submit();" class="btn btn-primary">등록</span>
        </sec:authorize>
        <span id="resetBtn" onclick="resetForm('reset');" class="btn btn-info">취소</span>
        <sec:authorize access="hasAuthority('DEPLOY_HBBOOTSTRAP_NETWORK_DELETE')">
            <span id="deleteBtn" class="btn btn-danger">삭제</span>
        </sec:authorize>
    </div>
</div>
<script>
$(function() {
    $.validator.addMethod( "ipv4", function( value, element, params ) {
        return /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(params);
    }, text_ip_msg );
    
    $.validator.addMethod( "ipv4Range", function( value, element, params ) {
        return /^((\b|\.)(0|1|2(?!5(?=6|7|8|9)|6|7|8|9))?\d{1,2}){4}(-((\b|\.)(0|1|2(?!5(?=6|7|8|9)|6|7|8|9))?\d{1,2}){4}|\/((0|1|2|3(?=1|2))\d|\d))\b$/.test(params);
    }, text_ip_msg );
    
    
    $("#settingForm").validate({
        ignore : [],
        //onfocusout: function(element) {$(element).valid()},
        rules: {
            subnetId : {
                required : function(){
                    return checkEmpty( $("input[name='subnetId']").val() );
                }
            }, networkConfigName: { 
                required: function(){
                    return checkEmpty( $("input[name='networkName']").val() );
                }
            }, iaasType: { 
                required: function(){
                    return checkEmpty( $("select[name='iaasType']").val() );
                }
            }, privateStaticIp: { 
                required: function(){
                    return checkEmpty( $("input[name='privateStaticIp']").val() );
                },ipv4 : function(){
                    return $("input[name='privateStaticIp']").val()
                }
            }, subnetRange: { 
                required: function(){
                    return checkEmpty( $("input[name='subnetRange']").val() );
                },ipv4Range : function(){
                    return $("input[name='subnetRange']").val()
                }
            }, subnetGateway: { 
                required: function(){
                    return checkEmpty( $("input[name='subnetGateway']").val() );
                },ipv4 : function(){
                    return $("input[name='subnetGateway']").val()
                }
            }, subnetDns: { 
                required: function(){
                    return checkEmpty( $("input[name='subnetDns']").val() );
                },ipv4  : function(){ 
                    if( $("input[name='subnetDns']").val().indexOf(",") > -1 ){
                        var list = ($("input[name='subnetDns']").val()).split(",");
                        var flag = true;
                        for( var i=0; i<list.length; i++ ){
                            var val = validateIpv4(list[i].trim());
                            if( !val ) flag = false;
                        }
                        if( !flag ) return "";
                        else return list[0].trim();
                    }else{
                        return $("input[name='subnetDns']").val();
                    }
                }
            }
        }, messages: {
            subnetId: { 
                required:  "서브넷 아이디"+text_required_msg
            }, networkConfigName: { 
                required:  "네트워크 별칭"+text_required_msg
            }, privateStaticIp: { 
                required:  "디렉터 Private IP"+text_required_msg
                ,ipv4 : text_ip_msg
            }, subnetRange: { 
                required:  "서브넷 범위"+select_required_msg
                ,ipv4 : text_cidr_msg
            }, subnetGateway: { 
                required:  "서브넷 게이트웨이"+text_required_msg
                ,ipv4 : text_ip_msg
            }, subnetDns: { 
                required:  "DNS 주소"+text_required_msg
                ,ipv4 : text_ip_msg
            }, iaasType: { 
                required:  "클라우드 인프라 환경 타입"+select_required_msg,
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
            registBootstrapNetworkConfigInfo();
        }
    });
});

function validateIpv4(params){
    return /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(params);
}

</script>