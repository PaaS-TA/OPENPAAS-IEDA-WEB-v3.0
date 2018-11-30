<%
/* =================================================================
 * 작성일 : 2018.06
 * 작성자 : 이동현
 * 상세설명 : 기본 인증서 관리 화면
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
var boshInfo = ""; //기본 정보
var iaas = "";

var defaultLayout = {
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
            name: 'default_GroupGrid',
            header: '<b>기본 정보</b>',
            method: 'GET',
                multiSelect: false,
            show: {
                    selectColumn: true,
                    footer: true},
            style: 'text-align: center',
            columns:[
                   { field: 'recid', hidden: true },
                   { field: 'defaultConfigName', caption: '기본 정보 별칭', size:'150px', style:'text-align:center;' },
                   { field: 'iaasType', caption: '인프라 환경 타입', size:'120px', style:'text-align:center;' ,render: function(record){ 
                       if(record.iaasType.toLowerCase() == "aws"){
                           return "<img src='images/iaasMgnt/aws-icon.png' width='80' height='30' />";
                       }else if (record.iaasType.toLowerCase() == "openstack"){
                           return "<img src='images/iaasMgnt/openstack-icon.png' width='90' height='35' />";
                       }
                   }},
                   { field: 'deploymentName', caption: '배포 명', size:'140px', style:'text-align:center;'},
                   { field: 'directorName', caption: '디렉터 명', size:'140px', style:'text-align:center;'},
                   { field: 'credentialKeyName', caption: '디렉터 인증서', size:'180px', style:'text-align:center;'},
                   { field: 'boshRelease', caption: 'BOSH 릴리즈', size:'130px', style:'text-align:center;'},
                   { field: 'boshCpiRelease', caption: 'BOSH CPI 릴리즈', size:'180px', style:'text-align:center;'},
                   { field: 'boshBpmRelease', caption: 'BOSH BPM 릴리즈', size:'180px', style:'text-align:center;'},
                   { field: 'osConfRelease', caption: 'OS-CONF 릴리즈', size:'180px', style:'text-align:center;'},
                   { field: 'paastaMonitoringUse', caption: 'PaaS-TA 모니터링 사용', size:'180px', style:'text-align:center;'},
                   { field: 'paastaMonitoringRelease', caption: 'PaaS-TA 모니터링 릴리즈', size:'180px', style:'text-align:center;'},
                   { field: 'syslogRelease', caption: 'SYSLOG 릴리즈', size:'180px', style:'text-align:center;'},
/*                    { field: 'uaaRelease', caption: 'UAA 릴리즈', size:'180px', style:'text-align:center;'},
                   { field: 'credhubRelease', caption: 'CREDHUB 릴리즈', size:'180px', style:'text-align:center;'}, */
                   { field: 'ntp', caption: 'NTP 서버', size:'120px', style:'text-align:center;'}
                  ],
            onSelect : function(event) {
                event.onComplete = function() {
                    $('#deleteBtn').attr('disabled', false);
                    settingDefaultInfo();
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
        }
    }
}

$(function(){
    $('#default_GroupGrid').w2layout(defaultLayout.layout2);
    w2ui.layout2.content('left', $().w2grid(defaultLayout.grid));
    w2ui['layout2'].content('main', $('#regPopupDiv').html());
    doSearch();
    
    initView();
    
    $("#deleteBtn").click(function(){
        if($("#deleteBtn").attr('disabled') == "disabled") return;
        var selected = w2ui['default_GroupGrid'].getSelection();
        if( selected.length == 0 ){
            w2alert("선택된 정보가 없습니다.", "디렉터 인증서 삭제");
            return;
        }
        else {
            var record = w2ui['default_GroupGrid'].get(selected);
            w2confirm({
                title        : "기본 정보",
                msg            : "기본 정보 ("+record.defaultConfigName + ")을 삭제하시겠습니까?",
                yes_text    : "확인",
                no_text        : "취소",
                yes_callBack: function(event){
                    deleteBootstrapDefaultConfigInfo(record.recid, record.defaultConfigName);
                },
                no_callBack    : function(){
                    w2ui['default_GroupGrid'].clear();
                    doSearch();
                }
            });
        }
    });
});

/********************************************************
 * 설명 : 초기 화면 View
 * 기능 : initView
 *********************************************************/
function initView(){
     $("input[name='metricUrl']").attr("disabled", true);
     $("input[name='syslogAddress']").attr("disabled", true);
     $("input[name='syslogPort']").attr("disabled", true);
     $("input[name='syslogTransport']").attr("disabled", true);
     
     $('[data-toggle="popover"]').popover();
     $(".paastaMonitoring-info").attr('data-content', "paasta  v4.0 이상에서 지원");
     $('input:radio[name=enableSnapshots]:input[value=false]').attr("checked", true);
     enableSnapshotsFn("false");
     checkPaasTAMonitoringUseYn();
     getCredentialList();
}

/******************************************************************
 * 기능 : getLocalBoshList
 * 설명 : BOSH 릴리즈 정보
 ***************************************************************** */
function settingMonitoringView(value){
    if(value == "bosh-268.2.0.tgz"){
        $("#paastaMonitoring").attr("disabled", false);
    } else {
        $("#paastaMonitoring").attr("disabled", true);
        $('#paastaMonitoring').attr("checked", false);
        checkPaasTAMonitoringUseYn();
    }
}

/******************************************************************
 * 기능 : getLocalBoshList
 * 설명 : BOSH 릴리즈 정보
 ***************************************************************** */
function getLocalBoshList(type){
    $.ajax({
        type : "GET",
        url : "/common/deploy/systemRelease/list/"+type+"/''",
        contentType : "application/json",
        async : true,
        success : function(data, status) {
            if( data.length == 0 ){
                return;
            }
            if( type == "bosh" ){
                var options = "<option value=''>BOSH 릴리즈를 선택하세요.</option>";
                for( var i=0; i<data.length; i++ ){
                    if( data[i] == boshInfo.boshRelease ){
                        options += "<option value='"+data[i]+"' selected >"+data[i]+"</option>";
                    }else options += "<option value='"+data[i]+"'>"+data[i]+"</option>";
                    
                }
                $("select[name='boshRelease']").html(options);
            }else if(type == "bpm"){
                var options = "<option value=''>BPM 릴리즈를 선택하세요.</option>";
                for( var i=0; i<data.length; i++ ){
                    if( data[i] == boshInfo.boshBpmRelease ){
                        options += "<option value='"+data[i]+"' selected >"+data[i]+"</option>";
                    }else options += "<option value='"+data[i]+"'>"+data[i]+"</option>";
                    
                }
                $("select[name='boshBpmRelease']").html(options);
            } else if(type == "os-conf"){
                var options = "<option value=''>OS-CONF 릴리즈를 선택하세요.</option>";
                for( var i=0; i<data.length; i++ ){
                    if( data[i] == boshInfo.osConfRelease ){
                        options += "<option value='"+data[i]+"' selected >"+data[i]+"</option>";
                    }else options += "<option value='"+data[i]+"'>"+data[i]+"</option>";
                    
                }
                $("select[name='osConfRelease']").html(options);
            
            } else if(type == "syslog"){
                var options = "<option value=''>SYSLOG 릴리즈를 선택하세요.</option>";
                for( var i=0; i<data.length; i++ ){
                    if( data[i] == boshInfo.syslogRelease ){
                        options += "<option value='"+data[i]+"' selected >"+data[i]+"</option>";
                    }else options += "<option value='"+data[i]+"'>"+data[i]+"</option>";
                    
                }
                $("select[name='syslogRelease']").html(options);
                
            }
        },
        error : function( e, status ) {
            w2alert("Bosh 릴리즈 "+search_data_fail_msg, "BOOTSTRAP 설치");
        }
    });
}


/********************************************************
 * 설명 : Bosh 릴리즈 버전 목록 정보 조회
 * 기능 : getReleaseVersionList
 *********************************************************/
function getReleaseVersionList(){
    var contents = "";
    $.ajax({
        type :"GET",
        url :"/common/deploy/list/releaseInfo/bootstrap/"+iaas,
        contentType :"application/json",
        success :function(data, status) {
            if (data != null && data != "") {
                contents = "<table id='popoverTable'><tr><th>릴리즈 유형</th><th>릴리즈 버전</th></tr>";
                data.map(function(obj) {
                    contents += "<tr><td>" + obj.releaseType+ "</td><td>" +  obj.minReleaseVersion +"</td></tr>";
                });
                contents += "</table>";
                $('.boshRelase-info').attr('data-content', contents);
            }
        },
        error :function(request, status, error) {
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "bosh 릴리즈 정보 목록 조회");
        }
    });
}

/******************************************************************
 * 기능 : getLocalBoshCpiList
 * 설명 : BOSH CPI 릴리즈 정보
 ***************************************************************** */
function getLocalBoshCpiList(type, iaas){
   $.ajax({
       type : "GET",
       url : "/common/deploy/systemRelease/list/"+type+"/"+iaas,
       contentType : "application/json",
       async : true,
       success : function(data, status) {
           if( data.length == 0 ){
               return;
           }
           if( type == 'bosh_cpi' ){
               var options = "<option value=''>BOSH CPI 릴리즈를 선택하세요.</option>";
               for( var i=0; i<data.length; i++ ){
                   if( data[i] == boshInfo.boshCpiRelease ){
                       options += "<option value='"+data[i]+"' selected>"+data[i]+"</option>";
                   }else options += "<option value='"+data[i]+"'>"+data[i]+"</option>";
               }
               $("select[name='boshCpiRelease']").html(options);
           }
       },
       error : function( e, status ) {
           w2alert("Bosh Cpi "+search_data_fail_msg, "BOOTSTRAP 설치");
       }
   });
}


/********************************************************
 * 설명 : 디렉터 인증서 목록 조회
 * 기능 : getReleaseVersionList
 *********************************************************/
function getCredentialList(){
    $.ajax({
        type : "GET",
        url : "/common/deploy/hybridCreds/list",
        contentType : "application/json",
        async : true,
        success : function(data, status) {
            if( data.length == 0 ){
                return;
            }
            var options = "<option value=''>디렉터 인증서를 선택하세요.</option>";
            for( var i=0; i<data.length; i++ ){
                if( data[i] == boshInfo.credentialKeyName ){
                    options += "<option value='"+data[i]+"' selected >"+data[i]+"</option>";
                }else options += "<option value='"+data[i]+"'>"+data[i]+"</option>";
            }
            $("select[name='credentialKeyName']").html(options);
        },
        error : function( e, status ) {
            w2alert("디렉터 인증서 "+search_data_fail_msg, "BOOTSTRAP 설치");
        }
    });
}

/******************************************************************
 * 기능 : enableSnapshotsFn
 * 설명 : 스냅샷 가능 사용여부(사용일 경우)
 ***************************************************************** */
function enableSnapshotsFn(value){
   if(value == "true"){
       $(".snapshotScheduleDiv").show();
       $('input:radio[name=enableSnapshots]:input[value=false]').attr("checked", false);
       $('input:radio[name=enableSnapshots]:input[value=true]').attr("checked", true);
   }else if(value == "false"){
       $("input[name=snapshotSchedule]").val("");
       $(".snapshotScheduleDiv").hide();
       $('input:radio[name=enableSnapshots]:input[value=true]').attr("checked", false);
       $('input:radio[name=enableSnapshots]:input[value=false]').attr("checked", true);
   }
}

/******************************************************************
 * 기능 : checkPaasTAMonitoringUseYn
 * 설명 : PaaS-TA 모니터링 가능 사용여부(사용일 경우)
 ***************************************************************** */
function checkPaasTAMonitoringUseYn(type){
    var value = $("#paastaMonitoring:checked").val();
    if( value == "on"){
        $("input[name='metricUrl']").attr("disabled", false);
        $("input[name='syslogAddress']").attr("disabled", false);
        $("input[name='syslogPort']").attr("disabled", false);
        $("input[name='syslogTransport']").attr("disabled", false);
        $("select[name=paastaMonitoringRelease]").prop("disabled", false);
        $("select[name=syslogRelease]").prop("disabled", false);
        getLocalPaasTAMonitoringReleaseList('PAASTA-MONITORING');
        getLocalBoshList('syslog');
    }else{
        $("input[name='metricUrl']").attr("disabled", true);
        $("input[name='syslogAddress']").attr("disabled", true);
        $("input[name='syslogPort']").attr("disabled", true);
        $("input[name='syslogTransport']").attr("disabled", true);
        
        $("input[name='metricUrl']").val('');
        $("input[name='syslogAddress']").val('');
        $("input[name='syslogPort']").val('');
        $("input[name='syslogTransport']").val('');
        
        $("select[name=paastaMonitoringRelease]").attr("disabled", true);
        $("select[name=syslogRelease]").prop("disabled", true);
        $('input:checkbox[id="paastaMonitoring"]').removeAttr('checked');
    }
}

/******************************************************************
 * 기능 : getLocalPaasTAMonitoringReleaseList
 * 설명 : Paas-TA 모니터링 릴리즈 정보
 ***************************************************************** */
function getLocalPaasTAMonitoringReleaseList(type){
    $.ajax({
        type: "GET",
        url: "/common/deploy/systemRelease/list/"+type+"/''",
        contentType: "application/json",
        async: true,
        success: function(data, status){
            if( data.length == 0 ){
                return;
            }
            if(type == 'PAASTA-MONITORING'){
                var options = '<option value="">PaaS-TA 모니터링 릴리즈를 선택하세요.</option>';
                for( var i=0; i<data.length; i++ ){
                    if( data[i] == boshInfo.paastaMonitoringRelease){
                        options += "<option value='"+data[i]+"' selected >"+data[i]+"</option>";
                    }else options += "<option value='"+data[i]+"'>"+data[i]+"</option>";
                }
                $("select[name='paastaMonitoringRelease']").html(options);
            }
        },
        error: function(e, status){
            w2alert("Monitering 릴리즈 "+search_data_fail_msg, "BOOTSTRAP 설치");
        }
    });
 }

/********************************************************
 * 설명 : 인프라 별 Release 조회
 * 기능 : getInitBoshReleaseList
 *********************************************************/
function getInitBoshReleaseList(iaasType){
    if(iaasType==""){
        w2alert("클라우드 인프라 환경을 선택하세요.");
        $("select[name=boshRelease]").html("<option value='' >BOSH 릴리즈를 선택하세요.</option>");
        $("select[name=boshCpiRelease]").html("<option value='' >BOSH CPI 릴리즈를 선택하세요.</option>");
        $("select[name=osConfRelease]").html("<option value='' >OS-CONF 릴리즈를 선택하세요.</option>");
/*         $("select[name=uaaRelease]").html("<option value='' >UAA 릴리즈를 선택하세요.</option>");
        $("select[name=credHubRelease]").html("<option value='' >CREDHUB 릴리즈를 선택하세요.</option>"); */
        $("select[name=boshBpmRelease]").html("<option value='' >BPM 릴리즈를 선택하세요.</option>");
        $("select[name=boshBpmRelease]").attr("disabled", "disabled");
        $("select[name=credHubRelease]").attr("disabled", "disabled");
        $("select[name=uaaRelease]").attr("disabled", "disabled");
        $("select[name=boshRelease]").attr("disabled", "disabled");
        $("select[name=boshCpiRelease]").attr("disabled", "disabled");
        $("select[name=osConfRelease]").attr("disabled", "disabled");
        return;
    }
    $("select[name=boshRelease]").removeAttr("disabled");
    $("select[name=boshCpiRelease]").removeAttr("disabled");
    $("select[name=osConfRelease]").removeAttr("disabled");
    //$("select[name=credHubRelease]").removeAttr("disabled");
    $("select[name=boshBpmRelease]").removeAttr("disabled");
    //$("select[name=uaaRelease]").removeAttr("disabled");
    iaas = iaasType;
    //BOSH CPI 릴리즈 정보 가져오기
    getLocalBoshCpiList('bosh_cpi', iaas);
    
    $('[data-toggle="popover"]').popover();
    //BOSH 릴리즈 사용 가능한 버전 가져오기
    getReleaseVersionList();
    //PaaS-TA 모니터링 릴리즈 정보 가져오기PAASTA-MONITORING
    
    //BOSH 릴리즈 정보 가져오기
    getLocalBoshList('bosh');
    //BPM 릴리즈 정보 가져오기
    getLocalBoshList('bpm');
    //OS Conf 릴리즈 정보 가져오기
    getLocalBoshList('os-conf');
    //UAA 릴리즈 정보 가져오기
    //getLocalBoshList('uaa');
    //Credhub 릴리즈 정보 가져오기
    //getLocalBoshList('credhub');
    getLocalPaasTAMonitoringReleaseList('PAASTA-MONITORING');
    //syslog 릴리즈 정보 가져오기
    getLocalBoshList('syslog');
    
    
    

}

/********************************************************
 * 설명 : 인증서 목록 조회
 * 기능 : doSearch
 *********************************************************/
function doSearch() {
    boshInfo="";//인프라 환경 설정 정ㅈ보
    iaas = "";
    resetForm();
    w2ui['default_GroupGrid'].clear();
    //w2ui['regPopupDiv'].clear();
    w2ui['default_GroupGrid'].load('/deploy/hbBootstrap/default/list');
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
 * 설명 : 기본 정보 등록
 * 기능 : registBootstrapDefaultConfigInfo
 *********************************************************/
function registBootstrapDefaultConfigInfo(){
    if( $("#paastaMonitoring:checked").val() == "on"){
        var monitoringUse = "true";
        var metricUrl = $("input[name=metricUrl]").val();
        var syslogAddress = $("input[name='syslogAddress']").val();
        var syslogPort = $("input[name='syslogPort']").val();
        var syslogTransport = $("input[name='syslogTransport']").val();
        var monitoringRelease = $("select[name=paastaMonitoringRelease]").val();
        var syslogRelease = $("select[name=syslogRelease]").val();
    }else{
        var monitoringUse = "false";
        var metricUrl =  "";
        var syslogAddress = "";
        var syslogPort = "";
        var syslogTransport = "";
        var monitoringRelease = "";
        var syslogRelease = "";
    }
    boshInfo = {
            iaasType            : $("select[name=iaasType]").val(),
            defaultConfigName   : $("input[name=defaultConfigName]").val(),
            id                  : $("input[name=defaultId]").val(),
            deploymentName      : $("input[name=deploymentName]").val(),
            directorName        : $("input[name=directorName]").val(),
            credentialKeyName   : $("select[name=credentialKeyName]").val(),
            ntp                 : $("input[name=ntp]").val(),
            boshRelease         : $("select[name=boshRelease]").val(),
            credhubRelease      : $("select[name=credHubRelease]").val(),
            uaaRelease          : $("select[name=uaaRelease]").val(),
            osConfRelease       : $("select[name=osConfRelease]").val(),
            boshCpiRelease      : $("select[name=boshCpiRelease]").val(),
            boshBpmRelease      : $("select[name=boshBpmRelease]").val(),
            enableSnapshots     : $("input:radio[name=enableSnapshots]:checked").val(),
            snapshotSchedule    : $("input[name=snapshotSchedule]").val(),
            metricUrl : metricUrl,
            paastaMonitoringUse : monitoringUse,
            syslogAddress  : syslogAddress,
            syslogPort  : syslogPort,
            syslogTransport  : syslogTransport,
            paastaMonitoringRelease : monitoringRelease,
            syslogRelease : syslogRelease
    }
    $.ajax({
        type : "PUT",
        url : "/deploy/hbBootstrap/default/save",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(boshInfo),
        success : function(data, status) {
            w2utils.unlock($("#layout_layout_panel_main"));
            doSearch();
        },
        error : function( e, status ) {
            w2utils.unlock($("#layout_layout_panel_main"));
            var errorResult = JSON.parse(e.responseText);
            w2alert(errorResult.message, "기본 정보 저장");
        }
    });
}

/********************************************************
 * 설명 : 기본 정보 수정 데아터 설정
 * 기능 : settingDefaultInfo
 *********************************************************/
function settingDefaultInfo(){
    var selected = w2ui['default_GroupGrid'].getSelection();
    var record = w2ui['default_GroupGrid'].get(selected);
    if(record == null) {
        w2alert("기본 정보 설정 중 에러가 발생 했습니다.");
        return;
    }
    iaas = record.iaasType;
    boshInfo = record;
    $("input[name=defaultConfigName]").val(record.defaultConfigName);
    $("select[name=iaasType]").val(record.iaasType);
    $("input[name='defaultId']").val(record.recid);
    $("input[name='deploymentName']").val(record.deploymentName);
    $("input[name='directorName']").val(record.directorName);
    $("select[name='credentialKeyName']").val(record.credentialKeyName);
    $("input[name='ntp']").val(record.ntp);
    
    if( !checkEmpty(record.enableSnapshots) ){
        $("input[name='snapshotSchedule']").val(record.snapshotSchedule);
        enableSnapshotsFn(record.enableSnapshots);
    }else{
        $('input:radio[name=enableSnapshots]:input[value=false]').attr("checked", true);
        enableSnapshotsFn("false");
    }
    if( !checkEmpty(record.paastaMonitoringUse) ){
        if( record.paastaMonitoringUse == "true"){
            
            $("input[name='paastaMonitoring']").attr("checked", true);
            $("input[name='metricUrl']").removeAttr("disabled");
            $("input[name='metricUrl']").val(record.metricUrl);
            
            $("input[name='syslogAddress']").removeAttr("disabled");
            $("input[name='syslogAddress']").val(record.syslogAddress);
            
            $("input[name='syslogPort']").removeAttr("disabled");
            $("input[name='syslogPort']").val(record.syslogPort);
            
            $("input[name='syslogTransport']").removeAttr("disabled");
            $("input[name='syslogTransport']").val(record.syslogTransport);
            
            $("select[name='paastaMonitoringRelease']").removeAttr("disabled");
            $("select[name='paastaMonitoringRelease']").val(record.paastaMonitoringRelease);
            
            $("select[name='syslogRelease']").removeAttr("disabled");
            $("select[name='syslogRelease']").val(record.syslogRelease);
            
        }else{
            $("input[name='paastaMonitoring']").attr("checked", false);
        }
    }
    if( !checkEmpty(record.boshBpmRelease) ){
        $("#bpmConfDiv").show();
    }
    getInitBoshReleaseList(iaas);
    
}
/********************************************************
 * 설명 : 기본 정보 삭제
 * 기능 : deleteBootstrapDefaultConfigInfo
 *********************************************************/
function deleteBootstrapDefaultConfigInfo(id, defaultConfigName){
    w2popup.lock("삭제 중입니다.", true);
    boshInfo = {
        id : id,
        defaultConfigName : defaultConfigName
    }
    $.ajax({
        type : "DELETE",
        url : "/deploy/hbBootstrap/default/delete",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(boshInfo),
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
    $().w2destroy('default_GroupGrid');
}
/********************************************************
 * 설명 : 기본 정보 리셋
 * 기능 : resetForm
 *********************************************************/
function resetForm(status){
    boshInfo = "";
    $(".panel-body").find("p").remove();
    $(".panel-body").children().children().children().css("borderColor", "#bbb");
    
    $("input[name=deploymentName]").val("");
    $("input[name=directorName]").val("");
    $("input[name=snapshotSchedule]").val("");

    $("input[name=defaultId]").val("");
    
    $("select[name=iaasType]").val("");
    $("select[name=boshRelease]").html("<option value='' >BOSH 릴리즈를 선택하세요.</option>");
    $("select[name=boshRelease]").attr("disabled", "disabled");
    
    $("select[name=boshCpiRelease]").html("<option value='' >BOSH CPI 릴리즈를 선택하세요.</option>");
    $("select[name=boshCpiRelease]").attr("disabled", "disabled");
    $("select[name=boshBpmRelease]").html("<option value='' >BOSH BPM 릴리즈를 선택하세요.</option>");
    
    $("select[name=boshBpmRelease]").attr("disabled", "disabled");
    $("select[name=osConfRelease]").attr("disabled", "disabled");
    $("select[name=osConfRelease]").html("<option value='' >OS-CONF 릴리즈를 선택하세요.</option>");
    
    $("select[name=credHubRelease]").attr("disabled", "disabled");
    $("select[name=credHubRelease]").html("<option value='' >CREDHUB 릴리즈를 선택하세요.</option>");
    
    $("select[name=uaaRelease]").attr("disabled", "disabled");
    $("select[name=uaaRelease]").html("<option value='' >UAA 릴리즈를 선택하세요.</option>");
    
    $("input[name='paastaMonitoring']").attr("checked", false);
    $("select[name=paastaMonitoringRelease]").html("<option value='' >PaaS-TA 모니터링 릴리즈를 선택하세요.</option>");
    $("select[name=paastaMonitoringRelease]").attr("disabled", "disabled");
    
    $("select[name=syslogRelease]").html("<option value='' >SYSLOG 릴리즈를 선택하세요.</option>");
    $("select[name=syslogRelease]").attr("disabled", "disabled");
    
    $("input[name=metricUrl]").val("");
    $("input[name=metricUrl]").attr("disabled", "disabled");
    $("input[name=syslogAddress]").val("");
    $("input[name=syslogAddress]").attr("disabled", "disabled");
    $("input[name=syslogPort]").val("");
    $("input[name=syslogPort]").attr("disabled", "disabled");
    $("input[name=syslogTransport]").val("");
    $("input[name=syslogTransport]").attr("disabled", "disabled");
    
    $(".snapshotScheduleDiv").hide();
    
    if(status=="reset"){
        w2ui['default_GroupGrid'].clear();
        doSearch();
    }
    document.getElementById("settingForm").reset();
}

</script>
<div id="main">
    <div class="page_site">이종 BOOTSTRAP 설치 > <strong>기본 정보 관리</strong></div>
    <!-- 사용자 목록-->
    <div class="pdt20">
        <div class="title fl"> 기본 정보 목록</div>
    </div>
    <div id="default_GroupGrid" style="width:100%;  height:700px;"></div>

</div>


<div id="regPopupDiv" hidden="true" >
    <form id="settingForm" action="POST">
    <input type="hidden" name="defaultId" />
        <div class="w2ui-page page-0" style="">
           <div class="panel panel-default">
               <div class="panel-heading"><b>기본 정보</b></div>
               <div class="panel-body" style="height:615px; overflow-y:auto;">
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">기본 정보 별칭</label>
                       <div>
                           <input class="form-control" name = "defaultConfigName" type="text"  maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="기본 정보 별칭을 입력 하세요."/>
                       </div>
                   </div>
                  <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">클라우드 인프라 환경</label>
                       <div>
                           <select class="form-control" onchange="getInitBoshReleaseList(this.value);" name="iaasType" style="width: 320px; margin-left: 20px;">
                               <option value="">인프라 환경을 선택하세요.</option>
                               <option value="aws">AWS</option>
                               <option value="openstack">Openstack</option>
                           </select>
                       </div>
                   </div>
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">배포명</label>
                       <div>
                           <input class="form-control" name = "deploymentName" type="text"  maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="배포명을 입력 하세요."/>
                       </div>
                   </div>
                   
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">디렉터 명</label>
                       <div>
                           <input class="form-control" name = "directorName" type="text"  maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="디렉터 명을 입력하세요."/>
                       </div>
                   </div>
                   
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">디렉터 접속 인증서</label>
                       <div>
                           <select class="form-control"  name="credentialKeyName" style="width: 320px; margin-left: 20px;">
                               <option value="" >디렉터 인증서를 선택하세요.</option>
                           </select>
                       </div>
                   </div>
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">NTP 서버 시간</label>
                       <div>
                           <input class="form-control" name="ntp" type="text"  maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="NTP 서버 시간을 입력하세요."/>
                       </div>
                   </div>
                   
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">BOSH 릴리즈
                           <span class="glyphicon glyphicon glyphicon-question-sign boshRelase-info" style="cursor:pointer;font-size: 14px;color: #157ad0;" data-toggle="popover"  data-trigger="hover" data-html="true" title="설치 지원 버전 목록"></span>
                       </label>
                       <div>
                           <select class="form-control" onchange="settingMonitoringView(this.value);"  disabled name="boshRelease" style="width: 320px; margin-left: 20px;">
                               <option value="" >BOSH 릴리즈를 선택하세요.</option>
                           </select>
                       </div>
                   </div>
                   
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">BOSH CPI 릴리즈</label>
                       <div>
                           <select class="form-control" disabled  name="boshCpiRelease" style="width: 320px; margin-left: 20px;">
                               <option value="" >BOSH CPI 릴리즈를 선택하세요.</option>
                           </select>
                       </div>
                   </div>
                   
                   <div class="w2ui-field" id="bpmConfDiv">
                       <label style="width:40%;text-align: left;padding-left: 20px;">BOSH BPM 릴리즈</label>
                       <div>
                           <select class="form-control" name="boshBpmRelease" style="width: 320px; margin-left: 20px;">
                               <option value="" >BOSH BPM 릴리즈를 선택하세요.</option>
                           </select>
                       </div>
                   </div>
                   
                    <div class="w2ui-field" id="osConfDiv"> 
                        <label style="width:40%;text-align: left;padding-left: 20px;">OS-CONF 릴리즈</label>
                        <div>
                            <select name="osConfRelease" class="form-control" style="width: 320px; margin-left: 20px;">
                                <option value="">OS-CONF 릴리즈를 선택하세요.</option>
                            </select>
                        </div>
                    </div>
                    
<!--                     <div class="w2ui-field"> 
                        <label style="width:40%;text-align: left;padding-left: 20px;">UAA 릴리즈</label>
                        <div>
                            <select name="uaaRelease" class="form-control" style="width: 320px; margin-left: 20px;">
                                <option value="">UAA 릴리즈를 선택하세요.</option>
                            </select>
                        </div>
                    </div>
                    
                    <div class="w2ui-field"> 
                        <label style="width:40%;text-align: left;padding-left: 20px;">CREDHUB 릴리즈</label>
                        <div>
                            <select name="credHubRelease" class="form-control" style="width: 320px; margin-left: 20px;">
                                <option value="">CREDHUB 릴리즈를 선택하세요.</option>
                            </select>
                        </div>
                    </div> -->
                    
                    <div class="w2ui-field">
                        <label style="width:43%;text-align: left;padding-left: 20px;">스냅샷기능 사용여부</label>
                        <div>
                            <span onclick="enableSnapshotsFn('true');" style="width:30%;"><label><input type="radio" name="enableSnapshots" value="true" />&nbsp;사용</label></span>
                            &nbsp;&nbsp;
                            <span onclick="enableSnapshotsFn('false');" style="width:30%;"><label><input type="radio" name="enableSnapshots" value="false" />&nbsp;미사용</label></span>
                        </div>
                    </div>
                    
                    <div class="w2ui-field snapshotScheduleDiv" id="snapshotScheduleDiv">
                       <label style="width:40%;text-align: left;padding-left: 20px;">스냅샷 스케쥴</label>
                        <div>
                            <input class="form-control" name="snapshotSchedule" id="snapshotSchedule" type="text"  maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="예) 0 0 7 * * * UTC"/>
                        </div>
                    </div>
                    
                    <div class="w2ui-field">
                        <label style="width:40%;text-align: left;padding-left: 20px;">PaaS-TA 모니터링
                        <span class="glyphicon glyphicon glyphicon-question-sign paastaMonitoring-info" style="cursor:pointer;font-size: 14px;color: #157ad0;" data-toggle="popover"  data-trigger="click" data-html="true"></span>
                        </label>
                        <div style="width: 60%">
                            <input style="margin-left: 20px;" disabled name="paastaMonitoring" type="checkbox" id="paastaMonitoring" onclick="checkPaasTAMonitoringUseYn(this.value);"/>사용
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="width:40%;text-align: left;padding-left: 20px;">Metric URL</label>
                        <div>
                            <input class="form-control" name = "metricUrl" type="text"  maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="예)10.0.15.11:8059"/>
                        </div>
                    </div>
                    
                    <div class="w2ui-field">
                        <label style="width:40%;text-align: left;padding-left: 20px;">Syslog Address</label>
                        <div>
                            <input class="form-control" name = "syslogAddress" type="text"  maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="예)10.0.0.0"/>
                        </div>
                    </div>
                    
                    <div class="w2ui-field">
                        <label style="width:40%;text-align: left;padding-left: 20px;">Syslog Port</label>
                        <div>
                            <input class="form-control" name = "syslogPort" type="text"  maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="예)2514"/>
                        </div>
                    </div>
                    
                    <div class="w2ui-field">
                        <label style="width:40%;text-align: left;padding-left: 20px;">Syslog Transport</label>
                        <div>
                            <input class="form-control" name = "syslogTransport" type="text"  maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="예)relp"/>
                        </div>
                    </div>
                    
                    
                    <div class="w2ui-field"> 
                        <label style="width:40%;text-align: left;padding-left: 20px;">모니터링 릴리즈</label>
                        <div>
                            <select name="paastaMonitoringRelease" class="form-control" style="width: 320px; margin-left: 20px;">
                                <option value="">PaaS-TA 모니터링 릴리즈를 선택하세요.</option>
                            </select>
                        </div>
                    </div>
                    <div class="w2ui-field"> 
                        <label style="width:40%;text-align: left;padding-left: 20px;">SYSLOG 릴리즈</label>
                        <div>
                            <select name="syslogRelease" class="form-control" style="width: 320px; margin-left: 20px;">
                                <option value="">SYSLOG 릴리즈를 선택하세요.</option>
                            </select>
                        </div>
                    </div>
               </div>
           </div>
        </div>
    </form>
    <div id="regPopupBtnDiv" style="text-align: center; margin-top: 5px;">
        <sec:authorize access="hasAuthority('DEPLOY_HBBOOTSTRAP_DEFAULT_ADD')">
            <span id="installBtn" onclick="$('#settingForm').submit();" class="btn btn-primary">등록</span>
        </sec:authorize>
        <span id="resetBtn" onclick="resetForm('reset');" class="btn btn-info">취소</span>
        <sec:authorize access="hasAuthority('DEPLOY_HBBOOTSTRAP_DEFAULT_DELETE')">
            <span id="deleteBtn" class="btn btn-danger">삭제</span>
        </sec:authorize>
        
    </div>
</div>


<script>
$(function() {
    $("#settingForm").validate({
        ignore : [],
        rules: {
            deploymentName : {
                required : function(){
                    return checkEmpty( $("input[name='deploymentName']").val() );
                }
            }, directorName: { 
                required: function(){
                    return checkEmpty( $("input[name='directorName']").val() );
                }
            },  credentialKeyName: { 
                required: function(){
                    return checkEmpty( $("select[name='credentialKeyName']").val() );
                }
            }, ntp: { 
                required: function(){
                    return checkEmpty( $("input[name='ntp']").val() );
                }
            }, boshRelease: { 
                required: function(){
                    return checkEmpty( $("select[name='boshRelease']").val() );
                }
            }, boshCpiRelease: { 
                required: function(){
                    return checkEmpty( $("select[name='boshCpiRelease']").val() );
                }
            }, osConfigRelease: { 
                required: function(){
                    return checkEmpty( $("select[name='osConfRelease']").val() );
                }
            }, credHubRelease: { 
                required: function(){
                    return checkEmpty( $("select[name='credHubRelease']").val() );
                }
            }, uaaRelease: { 
                required: function(){
                    return checkEmpty( $("select[name='uaaRelease']").val() );
                }
            }, boshBpmRelease: { 
                required: function(){
                    return checkEmpty( $("select[name='boshBpmRelease']").val() );
                }
            }, snapshotSchedule: { 
                required: function(){
                    if( $("input:radio[name=enableSnapshots]:checked").val() == "true"){
                        return checkEmpty( $("input[name='snapshotSchedule']").val() );
                    }else{ 
                        return false;
                    }
                }
            }, syslogAddress : {
                  required: function(){
                      if( $("#paastaMonitoring:checked").val() == "on"){
                           return checkEmpty( $("input[name='syslogAddress']").val() );
                      }else{
                           return false;
                      }
                 },ipv4 : function(){
                      if( $(" #paastaMonitoring:checked").val() == "on"){
                           return $("input[name='syslogAddress']").val()
                      }else{
                           return "0.0.0.0";
                      }
                 }
            }, metricUrl : {
                required: function(){
                    if( $("#paastaMonitoring:checked").val() == "on"){
                         return checkEmpty( $("input[name='metricUrl']").val() );
                    }else{
                         return false;
                    }
               }
            }, syslogPort : {
                required: function(){
                    if( $("#paastaMonitoring:checked").val() == "on"){
                         return checkEmpty( $("input[name='syslogPort']").val() );
                    }else{
                         return false;
                    }
               }
            }, syslogTransport : {
                required: function(){
                    if( $("#paastaMonitoring:checked").val() == "on"){
                         return checkEmpty( $("input[name='syslogTransport']").val() );
                    }else{
                         return false;
                    }
               }
            }, paastaMonitoringRelease : {
                  required: function(){
                      if( $("#paastaMonitoring:checked").val() == "on"){
                           return checkEmpty( $("select[name='paastaMonitoringRelease']").val() );
                      }else{
                           return false;
                      }
                  }
          }, syslogRelease : {
              required: function(){
                  if( $("#paastaMonitoring:checked").val() == "on"){
                       return checkEmpty( $("select[name='syslogRelease']").val() );
                  }else{
                       return false;
                  }
              }
      },  iaasType: { 
                required: function(){
                    return checkEmpty( $("select[name='iaasType']").val() );
                }
          }, defaultConfigName: { 
                required: function(){
                    return checkEmpty( $("input[name='defaultConfigName']").val() );
                }
          }
        }, messages: {
            deploymentName: { 
                 required:  "배포명" + text_required_msg
            }, directorName: { 
                required:  "디렉터명"+text_required_msg
            }, credentialKeyName: { 
                required:  "디렉터 인증서"+select_required_msg
            }, ntp: { 
                required:  "NTP"+text_required_msg
            }, boshRelease: { 
                required:  "BOSH 릴리즈" + select_required_msg
            }, boshCpiRelease: { 
                required:  "BOSH CPI 릴리즈"+select_required_msg
            }, credHubRelease: { 
                required:  "CREDHUB 릴리즈"+select_required_msg
            }, uaaRelease: { 
                required:  "UAA 릴리즈"+select_required_msg
            }, osConfigRelease: { 
                required:  "OS-CONF 릴리즈"+select_required_msg
            }, boshBpmRelease: { 
                required:  "BOSH BPM 릴리즈"+select_required_msg
            }, snapshotSchedule: { 
                required:  "스냅샷 스케쥴"+text_required_msg
            }, metricUrl: {
                required: "metricUrl"+text_required_msg
            }, syslogAddress: {
                required: "syslogAddress"+text_required_msg
            }, syslogPort: {
                required: "syslogPort"+text_required_msg
            }, syslogTransport: {
                required: "syslogTransport"+text_required_msg
            }, syslogRelease: {
                required: "SYSLOG 릴리즈"+select_required_msg
            }, paastaMonitoringRelease: {
                required: "모니터링 릴리즈"+select_required_msg
            }, iaasType: {
                required: "클라우드 인프라 환경"+select_required_msg
            }, defaultConfigName: {
                required: "기본 정보 별칭"+text_required_msg
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
            registBootstrapDefaultConfigInfo();
        }
    });
});
</script>