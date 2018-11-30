<%
/* =================================================================
 * 상세설명 : 이종 CF Deployment 설치
 * =================================================================
 */
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri = "http://www.springframework.org/tags" %>
<script type="text/javascript">

/******************************************************************
 * 설명 :    변수 설정
 ***************************************************************** */
var iaas ="";
var cfDeploymentInfo = [];
var installStatus ="";//설치 상태
var installClient = "";//설치 client
var deleteClient = "";//삭제 client
var cfDeploymentName = new Array();
var text_required_msg = '<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
var text_injection_msg='<spring:message code="common.text.validate.sqlInjection.message"/>';//입력하신 값은 입력하실 수 없습니다.
var select_required_msg='<spring:message code="common.select.vaildate.required.message"/>';//을(를) 선택하세요.
$(function() {
    /********************************************************
     * 설명 :  cfDeployment 목록 설정
     *********************************************************/
     $('#config_cfDeployment_grid').w2grid({
        name: 'config_cfDeployment_grid',
        header: '<b>CF Deployment 정보 목록</b>',
        method: 'GET',
         multiSelect: false,
        show: {    
                selectColumn: true,
                footer: true},
        style: 'text-align: center',
        columns:[
              {field: 'recid',     caption: 'recid', hidden: true}
            , {field: 'cfDeploymentConfigName', caption: 'CF Deployment 정보 별칭', size: '20%'}
            , {field: 'iaasType', caption: '인프라 환경 타입', size:'120px', style:'text-align:center;' ,render: function(record){ 
                if(record.iaasType.toLowerCase() == "aws"){
                    return "<img src='images/iaasMgnt/aws-icon.png' width='80' height='30' />";
                }else if (record.iaasType.toLowerCase() == "openstack"){
                    return "<img src='images/iaasMgnt/openstack-icon.png' width='90' height='35' />";
                }
            }}
            , {field: 'defaultConfigInfo', caption: '기본 정보 별칭', size: '20%'}
            , {field: 'networkConfigInfo', caption: '네트워크 정보 별칭', size: '20%'}
            , {field: 'credentialConfigInfo', caption: '인증서 정보 별칭 ', size: '20%'}
            , {field: 'resourceConfigInfo', caption: '리소스 정보 별칭 ', size: '20%'}
            , {field: 'instanceConfigInfo', caption: '인스턴스 정보 별칭 ', size: '20%'}
            , {field: 'cloudConfigFile', caption: 'Cloud Config 파일 명 ', size: '20%'}
            ],
        onSelect : function(event) {
            event.onComplete = function() {
                $('#modifyBtn').attr('disabled', false);
                $('#deleteBtn').attr('disabled', false);
                return;
            }
        },
        onDblClick: function (event) {
            var grid = this;
            // need timer for nicer visual effect that record was selected
            setTimeout(function () {
                w2ui['config_cfDeployment_grid2'].add( $.extend({}, grid.get(event.recid), { selected : false }) );
                grid.selectNone();
                grid.remove(event.recid);
            }, 150);
        }
        ,onUnselect : function(event) {
            event.onComplete = function() {
                $('#modifyBtn').attr('disabled', true);
                $('#deleteBtn').attr('disabled', true);
                return;
            }
        },onLoad:function(event){
            if(event.xhr.status == 403){
                location.href = "/abuse";
                event.preventDefault();
            }
        },onError : function(event) {
        }
    });
    
     $('#config_cfDeployment_grid2').w2grid({ 
         name: 'config_cfDeployment_grid2', 
         header: '<b>cfDeployment 목록</b>',
         style: 'text-align: center',
         columns:[
             {field: 'recid',     caption: 'recid', hidden: true}
             , {field: 'cfDeploymentConfigName', caption: 'CF Deployment 정보 별칭', size: '20%'}
             , {field: 'iaasType', caption: '인프라 환경 타입', size:'120px', style:'text-align:center;' ,render: function(record){ 
                 if(record.iaasType.toLowerCase() == "aws"){
                     return "<img src='images/iaasMgnt/aws-icon.png' width='80' height='30' />";
                 }else if (record.iaasType.toLowerCase() == "openstack"){
                     return "<img src='images/iaasMgnt/openstack-icon.png' width='90' height='35' />";
                 }
             }}
             , {field: 'defaultConfigInfo', caption: '기본 정보 별칭', size: '20%'}
             , {field: 'networkConfigInfo', caption: '네트워크 정보 별칭', size: '20%'}
             , {field: 'credentialConfigInfo', caption: '인증서 정보 별칭 ', size: '20%'}
             , {field: 'resourceConfigInfo', caption: '리소스 정보 별칭 ', size: '20%'}
             , {field: 'instanceConfigInfo', caption: '인스턴스 정보 별칭 ', size: '20%'}
             , {field: 'cloudConfigFile', caption: 'Cloud Config 파일 명 ', size: '20%'}
           ],
           onSelect : function(event) {
               event.onComplete = function() {
               }
           },onDblClick: function (event) {
             var record = w2ui['config_cfDeployment_grid2'].get(event.recid);
             var grid = this;
             var gridName = "";
             if(record.deployStatus != null){
                 gridName = "config_cfDeployment_grid3";
             } else {
                 gridName = "config_cfDeployment_grid";
             }
             setTimeout(function () {
                 w2ui[''+gridName+''].add( $.extend({}, grid.get(event.recid), { selected : false }) );
                 grid.selectNone();
                 grid.remove(event.recid);
             }, 150);
           },onUnselect : function(event) {
               event.onComplete = function() {
               }
           },onLoad:function(event){
               if(event.xhr.status == 403){
                   location.href = "/abuse";
                   event.preventDefault();
               }
           },onError : function(event) {
           }
     });
     
     $('#config_cfDeployment_grid3').w2grid({
         name: 'config_cfDeployment_grid3',
         header: '<b>cfDeployment 목록</b>',
         method: 'GET',
          multiSelect: false,
         show: {    
                 selectColumn: true,
                 footer: true},
         style: 'text-align: center',
         columns:[
             {field: 'recid',     caption: 'recid', hidden: true}
             , {field: 'cfDeploymentConfigName', caption: 'CF Deployment 정보 별칭', size: '200px'}
             , {field: 'iaasType', caption: '인프라 환경 타입', size:'120px', style:'text-align:center;' ,render: function(record){ 
                 if(record.iaasType.toLowerCase() == "aws"){
                     return "<img src='images/iaasMgnt/aws-icon.png' width='80' height='30' />";
                 }else if (record.iaasType.toLowerCase() == "openstack"){
                     return "<img src='images/iaasMgnt/openstack-icon.png' width='90' height='35' />";
                 }
             }}
             , {field: 'deployStatus', caption: '배포상태', size: '80px', 
                 render: function(record) {
                         if ( record.deployStatus == 'DEPLOY_STATUS_DONE' )
                             return '<span class="btn btn-primary" style="width:60px">성공</span>';
                         else    if ( record.deployStatus == 'DEPLOY_STATUS_FAILED' )
                             return '<span class="btn btn-danger" style="width:60px">오류</span>';
                         else    if ( record.deployStatus == 'DEPLOY_STATUS_CANCELLED' )
                             return '<span class="btn btn-primary" style="width:60px">취소</span>';
                         else    if ( record.deployStatus == 'DEPLOY_STATUS_PROCESSING' )
                             return '<span class="btn btn-primary" style="width:60px">배포중</span>';
                         else    if ( record.deployStatus == 'DEPLOY_STATUS_DELETING' )
                             return '<span class="btn btn-primary" style="width:60px">삭제중</span>';
                         else
                             return '&ndash;';
                    }
                 }
             , {field: 'defaultConfigInfo', caption: '배포 명 ', size: '150px'}
             , {field: 'hbCfDeploymentDefaultConfigVO.cfDeploymentVersion', caption: 'CF Deployment ', size: '150px'}
             , {field: 'hbCfDeploymentDefaultConfigVO.domain', caption: 'CF 도메인 ', size: '150px'}
             , {field: 'hbCfDeploymentNetworkConfigVO.publicStaticIp', caption: 'Public IP ', size: '150px'}
             , {field: 'hbCfDeploymentCredentialConfigVO.credentialConfigKeyFileName', caption: 'Credential File Name ', size: '200px',
                 render: function(record) {
                     if ( record.hbCfDeploymentCredentialConfigVO.credentialConfigKeyFileName != null ){
                         var deplymentParam = {
                                 service : "cf"
                                ,iaas    : record.iaasType
                                ,id      : record.id
                             } 
                         var fileName = record.hbCfDeploymentCredentialConfigVO.credentialConfigKeyFileName;
                         return '<a style="color:#333;" href="/common/deploy/download/credential/' + fileName +'" onclick="window.open(this.href); return false;">' + record.hbCfDeploymentCredentialConfigVO.credentialConfigKeyFileName + '</a>';
                   }else {
                        return '&ndash;';
                      }
                  }
             }
             , {field: 'stemcell', caption: 'Stemcell', size: '350px'
                 , render:function(record){
                       return record.hbCfDeploymentResourceConfigVO.stemcellName +"/"+ record.hbCfDeploymentResourceConfigVO.stemcellVersion;
                     }
                 }
             , {field: 'cloudConfigFile', caption: 'Cloud Config 파일 명 ', size: '300px',
                 render: function(record) {
                     if ( record.cloudConfigFile != null ){
                         var deplymentParam = {
                                 service : "cf"
                                ,iaas    : record.iaasType
                                ,id      : record.id
                             } 
                         var fileName = record.cloudConfigFile;
                         return '<a style="color:#333;" href="/common/deploy/download/manifest/' + fileName +'" onclick="window.open(this.href); return false;">' + record.cloudConfigFile + '</a>';
                   }else {
                        return '&ndash;';
                      }
                  }
             }
             ],
         onSelect : function(event) {
             event.onComplete = function() {
                 $('#deleteVmBtn').attr('disabled', false);
                 $('#modifyVmBtn').attr('disabled', false);
                 return;
             }
         },onUnselect : function(event) {
             event.onComplete = function() {
                 $('#deleteVmBtn').attr('disabled', true);
                 $('#modifyVmBtn').attr('disabled', true);
                 return;
             }
         },onLoad:function(event){
             if(event.xhr.status == 403){
                 location.href = "/abuse";
                 event.preventDefault();
             }
         },onError : function(event) {
         }, onDblClick: function (event) {
             var grid = this;
             // need timer for nicer visual effect that record was selected
             setTimeout(function () {
                 w2ui['config_cfDeployment_grid2'].add( $.extend({}, grid.get(event.recid), { selected : false }) );
                 grid.selectNone();
                 grid.remove(event.recid);
             }, 150);
         }
     });
    
    /******************************************************************
     * 설명 : cfDeployment 설치 버튼
     ***************************************************************** */
     $("#installBtn").click(function(){
         w2popup.open({
            width   : 900,
            height  : 500,
            title : '<b>이종 CF Deployment 정보 등록</b>',
            body : $("#cfDeploymentRegistInfoDiv").html(),
            buttons: $("#cfDeploymentRegistInfoBtnDiv").html(),
            modal : true,
            onOpen:function(event){
                event.onComplete = function(){
                    getCfDeploymentDefaultInfo();
                    getCfDeploymentResourceInfo();
                    getCfDeploymentNetworkInfo();
                    getCfDeploymentInstanceInfo();
                    getCfDeploymentCredentialInfo();
                }
            },onClose:function(event){
                w2ui['config_cfDeployment_grid'].clear();
                doSearch();
            }
        });
     });
     
     /******************************************************************
     * 설명 : cfDeployment 수정 버튼
     ***************************************************************** */
    $("#modifyBtn").click(function(){
        if($("#modifyBtn").attr('disabled') == "disabled") return;
        
        var selected = w2ui['config_cfDeployment_grid'].getSelection();
        if( selected.length == 0 ){
            w2alert("선택된 정보가 없습니다.", "CF Deployment 등록 정보 수정");
            return;
        }
        var record = w2ui['config_cfDeployment_grid'].get(selected);
        cfDeploymentInfo = record;
        w2popup.open({
            width   : 900,
            height  : 500,
            title : '<b>이종 CF Deployment 정보 등록</b>',
            body : $("#cfDeploymentRegistInfoDiv").html(),
            buttons: $("#cfDeploymentRegistInfoBtnDiv").html(),
            modal : true,
            onOpen:function(event){
                event.onComplete = function(){
                    $(".w2ui-msg-body input[name='cfDeploymentId']").val(record.id);
                    $(".w2ui-msg-body input[name='cfDeploymentConfigName']").val(record.cfDeploymentConfigName);
                    $(".w2ui-msg-body select[name='iaasType']").val(record.iaasType);
                    getCfDeploymentDefaultInfo();
                    getCfDeploymentResourceInfo();
                    getCfDeploymentNetworkInfo();
                    getCfDeploymentInstanceInfo();
                    getCfDeploymentCredentialInfo();
                }
            },onClose:function(event){
                w2ui['config_cfDeployment_grid'].clear();
                doSearch();
            }
        });
     });
     
     
    /******************************************************************
     * 설명 : cfDeployment 수정 버튼
     ***************************************************************** */
    $("#modifyVmBtn").click(function(){
        if($("#modifyVmBtn").attr('disabled') == "disabled") return;
        
        var selected = w2ui['config_cfDeployment_grid3'].getSelection();
        if( selected.length == 0 ){
            w2alert("선택된 정보가 없습니다.", "CF Deployment 등록 정보 수정");
            return;
        }
        var record = w2ui['config_cfDeployment_grid3'].get(selected);
        cfDeploymentInfo = record;
        w2popup.open({
            width   : 900,
            height  : 500,
            title : '<b>이종 CF Deployment 정보 등록</b>',
            body : $("#cfDeploymentRegistInfoDiv").html(),
            buttons: $("#cfDeploymentRegistInfoBtnDiv").html(),
            modal : true,
            onOpen:function(event){
                event.onComplete = function(){
                    $(".w2ui-msg-body input[name='cfDeploymentId']").val(record.id);
                    $(".w2ui-msg-body input[name='cfDeploymentConfigName']").val(record.cfDeploymentConfigName);
                    $(".w2ui-msg-body select[name='iaasType']").val(record.iaasType);
                    getCfDeploymentDefaultInfo();
                    getCfDeploymentResourceInfo();
                    getCfDeploymentNetworkInfo();
                    getCfDeploymentInstanceInfo();
                    getCfDeploymentCredentialInfo();
                }
            },onClose:function(event){
                w2ui['config_cfDeployment_grid3'].clear();
                doSearch();
            }
        });
     });
    
     
     
     /******************************************************************
     * 설명 : cfDeployment 삭제 버튼
     ***************************************************************** */
    $("#deleteBtn").click(function(){
        if($("#deleteBtn").attr('disabled') == "disabled") return;
        
        var selected = w2ui['config_cfDeployment_grid'].getSelection();
        var record = w2ui['config_cfDeployment_grid'].get(selected);
        var message = "";
        
        if ( record.cfDeploymentConfigName ){
            message = "CF Deployment 정보 " + record.cfDeploymentConfigName + ")를 삭제하시겠습니까?";
        }else message = "선택된 CF Deployment을 삭제하시겠습니까?";
        
        w2confirm({
            title        : "CF Deployment 삭제",
            msg          : message,
            yes_text     : "확인",
            yes_callBack : function(event){
                deleteCfDeploymentInfo(record);
            },
            no_text : "취소",
            no_callBack : function(event){
                w2ui['config_cfDeployment_grid'].clear();
                doSearch();
            }
        });
     });
    /******************************************************************
     * 설명 : cfDeployment 설치 버튼
     ***************************************************************** */
    $("#installVmBtn").click(function(){
        if($("#installVmBtn").attr('disabled') == "disabled") return;
        
        var selectAll = w2ui['config_cfDeployment_grid2'].selectAll();

        var selected = w2ui['config_cfDeployment_grid2'].getSelection();
        
        if(selected.length == 3) {
            w2alert("최대 2개의 CF Deployment이 설치 가능 합니다. ", "cfDeployment 설치");
            return;
        }
        
        var record = new Array();
        
        for(var i=0; i<selected.length; i++){
            record.push(w2ui['config_cfDeployment_grid2'].get(selected[i]));
        }
        if(record == ""){
            w2alert("배포할 CF Deployment이 존재하지 않습니다.");
        }else{
            firstInstallPopup(record);
        }
    });
    
    /******************************************************************
     * 설명 : cfDeployment 삭제 버튼
     ***************************************************************** */
    $("#deleteVmBtn").click(function(){
        if($("#deleteVmBtn").attr('disabled') == "disabled") return;
        
        var selected = w2ui['config_cfDeployment_grid3'].getSelection();
        var record = w2ui['config_cfDeployment_grid3'].get(selected);
        var message = "";
        
        if ( record.cfDeploymentConfigName ){
            message = "CF Deployment 정보 " + record.cfDeploymentConfigName + ")를 삭제하시겠습니까?";
        }else message = "선택된 CF Deployment을 삭제하시겠습니까?";
        
        w2confirm({
            title        : "CF Deployment 삭제",
            msg          : message,
            yes_text     : "확인",
            yes_callBack : function(event){
                deleteCfDeploymentVmInfo(record);
            },
            no_text : "취소",
            no_callBack : function(event){
                w2ui['config_cfDeployment_grid3'].clear();
                doSearch();
            }
        });
     });
    
    doSearch();
});


/******************************************************************
 * 기능 : privateInstallPopup
 * 설명 : Private Type Boostrap 설치
 ***************************************************************** */
var cfDeploymentInstallSocket = null;
function firstInstallPopup(cfDeploymentInfo){
    
    var firstDeploy = cfDeploymentInfo[0];
    
    if(!lockFileSet(firstDeploy.cfDeploymentConfigName)) return;
    var message = firstDeploy.iaasType + " CF Deployment ";
    var requestParameter = {
           id : firstDeploy.id,
           iaasType: firstDeploy.iaasType
    };
    w2popup.open({
        title   : "<b>"+firstDeploy.iaasType.toUpperCase()+" 클라우드 환경 CF Deployment 설치</b>",
        width   : 800,
        height  : 620,
        modal   : true,
        showMax : true,
        body    : $("#InstallDiv1").html(),
        buttons : $("#InstallDivButtons").html(),
        onOpen : function(event){
            event.onComplete = function(){
                if(cfDeploymentInstallSocket != null) cfDeploymentInstallSocket = null;
                if(installClient != null) installClient = null;
                cfDeploymentInstallSocket = new SockJS('/deploy/hbCfDeployment/install/cfDeploymentInstall');
                installClient = Stomp.over(cfDeploymentInstallSocket);
                installClient.connect({}, function(frame) {
                    installClient.subscribe('/user/deploy/hbCfDeployment/install/logs', function(data){
                        var installLogs = $(".w2ui-msg-body #installLogs");
                        var response = JSON.parse(data.body);
                        if ( response.messages != null ){
                            for ( var i=0; i < response.messages.length; i++) {
                                installLogs.append(response.messages[i] + "\n").scrollTop( installLogs[0].scrollHeight );
                                
                            }
                            if ( response.state.toLowerCase() != "started" ) {
                                if ( response.state.toLowerCase() == "done" )    message = message + " 설치가 완료되었습니다.";
                                if ( response.state.toLowerCase() == "error" ) message = message + " 설치 중 오류가 발생하였습니다.";
                                if ( response.state.toLowerCase() == "cancelled" ) message = message + " 설치 중 취소되었습니다.";
                                
                                installStatus = response.state.toLowerCase();
                                $('.w2ui-msg-buttons #deployPopupBtn').prop("disabled", false);
                                
                                if(cfDeploymentInfo.length == 2){
                                    installClient.disconnect(secondInstallPopup(cfDeploymentInfo[1]));
                                }else{
                                    installClient.disconnect();
                                }
                                w2alert(message, "CF Deployment 설치");
                            }
                        }
                    });
                    installClient.send('/send/deploy/hbCfDeployment/install/cfDeploymentInstall', {}, JSON.stringify(requestParameter));
                });
            }
        }, onClose : function(event){
               event.onComplete = function(){
                   w2ui['config_cfDeployment_grid2'].clear();
                   if( installClient != ""  ){
                       installClient.disconnect();
                   }
                   popupClose();
               }
           }
    });
}
/******************************************************************
 * 기능 : InstallPopup
 * 설명 : CF Deployment 설치
 ***************************************************************** */
function secondInstallPopup(cfDeploymentInfo){
    if(installStatus != "done") return;
    if(!lockFileSet(cfDeploymentInfo.deploymentFile)) return;
    
    var message = cfDeploymentInfo.iaasType+" CF Deployment ";
    var requestParameter = {
           id : cfDeploymentInfo.id,
           iaasType: cfDeploymentInfo.iaasType
    };
    w2popup.open({
        title   : "<b>"+cfDeploymentInfo.iaasType.toUpperCase()+" 클라우드 환경 CF Deployment 설치</b>",
        width   : 800,
        height  : 620,
        modal   : true,
        showMax : true,
        body    : $("#InstallDiv2").html(),
        buttons : $("#InstallDivButtons").html(),
        onOpen : function(event){
            event.onComplete = function(){
                if(cfDeploymentInstallSocket != null) cfDeploymentInstallSocket = null;
                if(installClient != null) installClient = null;
                cfDeploymentInstallSocket = new SockJS('/deploy/hbCfDeployment/install/cfDeploymentInstall');
                installClient = Stomp.over(cfDeploymentInstallSocket);
                installClient.connect({}, function(frame) {
                    installClient.subscribe('/user/deploy/hbCfDeployment/install/logs', function(data){
                        var installLogs = $(".w2ui-msg-body #installLogs");
                        var response = JSON.parse(data.body);
                        if ( response.messages != null ){
                            for ( var i=0; i < response.messages.length; i++) {
                                installLogs.append(response.messages[i] + "\n").scrollTop( installLogs[0].scrollHeight );
                            }
                            if ( response.state.toLowerCase() != "started" ) {
                                if ( response.state.toLowerCase() == "done" ) message = message + " 설치가 완료되었습니다."; 
                                if ( response.state.toLowerCase() == "error" ) message = message + " 설치 중 오류가 발생하였습니다.";
                                if ( response.state.toLowerCase() == "cancelled" ) message = message + " 설치 중 취소되었습니다.";
                                
                                installStatus = response.state.toLowerCase();
                                $('.w2ui-msg-buttons #deployPopupBtn').prop("disabled", false);
                                    
                                installClient.disconnect();
                                w2alert(message, "CF Deployment 설치");
                            }
                        }
                    });
                    installClient.send('/send/deploy/hbCfDeployment/install/cfDeploymentInstall', {}, JSON.stringify(requestParameter));
                });
            }
        }, onClose : function(event){
               event.onComplete = function(){
                   w2ui['config_cfDeployment_grid2'].clear();
                   if( installClient != ""  ){
                       installClient.disconnect();
                   }
                   popupClose();
               }
           }
    });
}

/******************************************************************
 * 기능 : lockFileSet
 * 설명 : Lock 파일 생성
 ***************************************************************** */
var lockFile = false;
function lockFileSet(deployFile){
    if(!checkEmpty(deployFile) ){
        var FileName = "hybird_cfDeployment";
        var message = "현재 다른 설치 관리자가 CF Deployment를 사용 중 입니다.";
        lockFile = commonLockFile("<c:url value='/common/deploy/lockFile/"+FileName+"'/>",message);
    }
    return lockFile;
}



/******************************************************************
 * 기능 : getCfDeploymentDefaultInfo
 * 설명 : cfDeployment Default 정보 조회
 ***************************************************************** */
function getCfDeploymentDefaultInfo(){
    $.ajax({
        type : "GET",
        url : "/deploy/hbCfDeployment/defaultConfig/list",
        contentType : "application/json",
        async : true,
        success : function(data, status) {
            if( data.length == 0 ){
                return;
            }
            var options = "<option value=''>CF Deployment 기본 정보를 선택하세요.</option>";
            for( var i=0; i<data.records.length; i++ ){
                if( data.records[i].defaultConfigName == cfDeploymentInfo.defaultConfigInfo ){
                    options += "<option value='"+data.records[i].defaultConfigName+"' selected>"+data.records[i].defaultConfigName+"</option>";
                }else options += "<option value='"+data.records[i].defaultConfigName+"'>"+data.records[i].defaultConfigName+"</option>";
            }
            $("select[name='defaultConfigInfo']").html(options);
        },
        error : function( e, status ) {
            w2alert("CF Deployment 기본 정보 "+search_data_fail_msg, "CF Deployment 설치");
        }
    });
}

/******************************************************************
 * 기능 : getCfDeploymentNetworkInfo
 * 설명 : cfDeployment 네트워크 정보 조회
 ***************************************************************** */
function getCfDeploymentNetworkInfo(){
    $.ajax({
        type : "GET",
        url : "/deploy/hbCfDeployment/networkConfig/list",
        contentType : "application/json",
        async : true,
        success : function(data, status) {
            if( data.length == 0 ){
                return;
            }
            var options = "<option value=''>CF Deployment 네트워크 정보를 선택하세요.</option>";
            for( var i=0; i<data.records.length; i++ ){
                if( data.records[i].networkName == cfDeploymentInfo.networkConfigInfo ){
                    options += "<option value='"+data.records[i].networkName+"' selected>"+data.records[i].networkName+"</option>";
                }else options += "<option value='"+data.records[i].networkName+"'>"+data.records[i].networkName+"</option>";
            }
            $("select[name='networkConfigInfo']").html(options);
        },
        error : function( e, status ) {
            w2alert("CF Deployment 네트워크 정보 "+search_data_fail_msg, "CF Deployment 설치");
        }
    });
}

/******************************************************************
 * 기능 : getCfDeploymentResourceInfo
 * 설명 : cfDeployment 리소스 정보 조회
 ***************************************************************** */
function getCfDeploymentResourceInfo(){
    $.ajax({
        type : "GET",
        url : "/deploy/hbCfDeployment/resourceConfig/list",
        contentType : "application/json",
        async : true,
        success : function(data, status) {
            if( data.length == 0 ){
                return;
            }
            var options = "<option value=''>CF Deployment 리소스 정보를 선택하세요.</option>";
            for( var i=0; i<data.records.length; i++ ){
                if( data.records[i].resourceConfigName == cfDeploymentInfo.resourceConfigInfo ){
                    options += "<option value='"+data.records[i].resourceConfigName+"' selected>"+data.records[i].resourceConfigName+"</option>";
                }else options += "<option value='"+data.records[i].resourceConfigName+"'>"+data.records[i].resourceConfigName+"</option>";
            }
            $("select[name='resourceConfigInfo']").html(options);
        },
        error : function( e, status ) {
            w2alert("CF Deployment 리소스 정보 "+search_data_fail_msg, "CF Deployment 설치");
        }
    });
}

/******************************************************************
 * 기능 : getCfDeploymentInstanceInfo
 * 설명 : CF Deployment 인스턴스 정보 조회
 ***************************************************************** */
function getCfDeploymentInstanceInfo(){
    $.ajax({
        type : "GET",
        url : "/deploy/hbCfDeployment/instanceConfig/list",
        contentType : "application/json",
        async : true,
        success : function(data, status) {
            if( data.length == 0 ){
                return;
            }
            var options = "<option value=''>CF Deployment 인스턴스 정보를 선택하세요.</option>";
            for( var i=0; i<data.records.length; i++ ){
                if( data.records[i].instanceConfigName == cfDeploymentInfo.instanceConfigInfo ){
                    options += "<option value='"+data.records[i].instanceConfigName+"' selected>"+data.records[i].instanceConfigName+"</option>";
                }else options += "<option value='"+data.records[i].instanceConfigName+"'>"+data.records[i].instanceConfigName+"</option>";
            }
            $("select[name='instanceConfigInfo']").html(options);
        },
        error : function( e, status ) {
            w2alert("CF Deployment 인스턴스 정보 "+search_data_fail_msg, "CF Deployment 설치");
        }
    });
}

/******************************************************************
 * 기능 : getCfDeploymentInstanceInfo
 * 설명 : CF Deployment 인증서 정보 조회
 ***************************************************************** */
function getCfDeploymentCredentialInfo(){
    $.ajax({
        type : "GET",
        url : "/deploy/hbCfDeployment/credentialConfig/list",
        contentType : "application/json",
        async : true,
        success : function(data, status) {
            if( data.length == 0 ){
                return;
            }
            var options = "<option value=''>CF Deployment 인증서 정보를 선택하세요.</option>";
            for( var i=0; i<data.records.length; i++ ){
                if( data.records[i].credentialConfigName == cfDeploymentInfo.credentialConfigInfo ){
                    options += "<option value='"+data.records[i].credentialConfigName+"' selected>"+data.records[i].credentialConfigName+"</option>";
                }else options += "<option value='"+data.records[i].credentialConfigName+"'>"+data.records[i].credentialConfigName+"</option>";
            }
            $("select[name='credentialConfigInfo']").html(options);
        },
        error : function( e, status ) {
            w2alert("CF Deployment 인증서 정보 "+search_data_fail_msg, "CF Deployment 설치");
        }
    });
}

/******************************************************************
 * 기능 : saveCfDeploymentInfo
 * 설명 : cf Deployment 정보 저장
 ***************************************************************** */
function saveCfDeploymentInfo(){
    w2popup.lock( save_lock_msg, true); 
    cfDeploymentInfo = {
        id                     : $(".w2ui-msg-body input[name='cfDeploymentId']").val(),
        cfDeploymentConfigName : $(".w2ui-msg-body input[name='cfDeploymentConfigName']").val(),
        iaasType               : $(".w2ui-msg-body select[name='iaasType']").val(),
        networkConfigInfo      : $(".w2ui-msg-body select[name='networkConfigInfo']").val(),
        defaultConfigInfo      : $(".w2ui-msg-body select[name='defaultConfigInfo']").val(),
        resourceConfigInfo     : $(".w2ui-msg-body select[name='resourceConfigInfo']").val(),
        instanceConfigInfo     : $(".w2ui-msg-body select[name='instanceConfigInfo']").val(),
        credentialConfigInfo     : $(".w2ui-msg-body select[name='credentialConfigInfo']").val()
    }
    $.ajax({
        type : "PUT",
        url : "/deploy/hbCfDeployment/install/save",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(cfDeploymentInfo),
        success : function(status) {
            // ajax가 성공할때 처리...
            w2popup.unlock();
            w2popup.close();
            w2ui['config_cfDeployment_grid'].clear();
            w2ui['config_cfDeployment_grid2'].clear();
            doSearch();
        },
        error : function(request, status, error) {
            // ajax가 실패할때 처리...
            w2popup.unlock();
            w2ui['config_cfDeployment_grid'].clear();
            w2ui['config_cfDeployment_grid2'].clear();
            doSearch();
            w2popup.close();
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message);
        }
    });
}

/******************************************************************
 * 기능 : deleteCfDeploymentInfo
 * 설명 : cf Deployment 정보 삭제
 ***************************************************************** */
function deleteCfDeploymentInfo(record){
    cfDeploymentInfo = {
            id                     : record.id,
            cfDeploymentConfigName    : record.cfDeploymentConfigName
    }
    $.ajax({
        type : "DELETE",
        url : "/deploy/hbCfDeployment/install/delete",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(cfDeploymentInfo),
        success : function(status) {
            // ajax가 성공할때 처리...
            w2popup.unlock();
            w2popup.close();
            w2ui['config_cfDeployment_grid'].clear();
            w2ui['config_cfDeployment_grid2'].clear();
            doSearch();
        },
        error : function(request, status, error) {
            // ajax가 실패할때 처리...
            w2popup.unlock();
            w2ui['config_cfDeployment_grid'].clear();
            doSearch();
            w2popup.close();
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message);
        }
    });
}

/******************************************************************
 * 기능 : deleteCfDeploymentVmInfo
 * 설명 : cf Deployment VM 삭제
 ***************************************************************** */
function deleteCfDeploymentVmInfo(record){
    var requestParameter = {
            id:record.id,
            iaasType:record.iaasType
    };
    if ( record.deployStatus == null || record.deployStatus == '' ) {
        // 단순 레코드 삭제
        var url = "/deploy/cfDeployment/delete/data";
        $.ajax({
            type : "DELETE",
            url : url,
            data : JSON.stringify(requestParameter),
            contentType : "application/json",
            success : function(data, status) {
                cfDeploymentDeploymentName = [];
                gridReload();
            },
            error : function(request, status, error) {
                var errorResult = JSON.parse(request.responseText);
                w2alert(errorResult.message, "CF Deployment 삭제");
            }
        });
    } else {
        if(!lockFileSet(record.cfDeploymentConfigName)) return;
        var message = "CF Deployment";
        var body = '<textarea id="deleteLogs" style="width:95%;height:90%;overflow-y:visible;resize:none;background-color: #FFF; margin:2%" readonly="readonly"></textarea>';
        w2popup.open({
            width   : 700,
            height  : 500,
            title   : "<b>CF Deployment 삭제</b>",
            body    : body,
            buttons : '<button class="btn" style="float: right; padding-right: 15%;" onclick="popupComplete();">닫기</button>',
            modal   : true,
            showMax : true,
            onOpen  : function(event){
                event.onComplete = function(){
                    var socket = new SockJS('/deploy/hbCfDeployment/delete/instance');
                    deleteClient = Stomp.over(socket);
                     deleteClient.connect({}, function(frame) {
                        deleteClient.subscribe('/user/deploy/hbCfDeployment/delete/logs', function(data){
                            
                            var deleteLogs = $(".w2ui-msg-body #deleteLogs");
                            var response = JSON.parse(data.body);
                            
                            if ( response.messages != null ) {
                                   for ( var i=0; i < response.messages.length; i++) {
                                       deleteLogs.append(response.messages[i] + "\n").scrollTop( deleteLogs[0].scrollHeight );
                                   }
                                   if ( response.state.toLowerCase() != "started" ) {
                                    if ( response.state.toLowerCase() == "done" )    message = message + " 삭제가 완료되었습니다."; 
                                    if ( response.state.toLowerCase() == "error" ) message = message + " 삭제 중 오류가 발생하였습니다.";
                                    if ( response.state.toLowerCase() == "cancelled" ) message = message + " 삭제 중 취소되었습니다.";
                                    
                                    installStatus = response.state.toLowerCase();
                                    deleteClient.disconnect();
                                    w2alert(message, "CF Deployment 삭제");
                                   }
                            }
                        });
                        deleteClient.send('/send/deploy/hbCfDeployment/delete/instance', {}, JSON.stringify(requestParameter));
                    });
                }
            }, onClose : function (event){
                event.onComplete= function(){
                    cfDeploymentDeploymentName = [];
                    w2ui['config_cfDeployment_grid3'].clear();
                    if( deleteClient != ""  ){
                        deleteClient.disconnect();
                    }
                    popupClose();
                }
            } 
        });
    }
}

/******************************************************************
 * 기능 : 팝업창 닫을 경우
 * 설명 : popupClose
 ***************************************************************** */
function popupClose() {
   //grid Reload
   doSearch();
   doButtonStyle();
}

/******************************************************************
 * 기능 : doSearch
 * 설명 : cfDeployment 목록 조회
 ***************************************************************** */
function doSearch() {
    //doButtonStyle();
    cfDeploymentInfo = "";
    w2ui['config_cfDeployment_grid'].load("<c:url value='/deploy/hbCfDeployment/list/installAble'/>",
            function (){ doButtonStyle(); });
    w2ui['config_cfDeployment_grid3'].load("<c:url value='/deploy/hbCfDeployment/list/installed'/>");
}
 /******************************************************************
  * 기능 : doButtonStyle
  * 설명 : Button 제어
  ***************************************************************** */
function doButtonStyle(){
    //Button Style init
    $('#modifyBtn').attr('disabled', true);
    $('#modifyVmBtn').attr('disabled', true);
    $('#deleteBtn').attr('disabled', true);
    $('#deleteVmBtn').attr('disabled', true);
}

/******************************************************************
 * 기능 : deployLogMsgPopup
 * 설명 : 배포 로그 팝업창
 ***************************************************************** */
function deployLogMsgPopup(msg){
    var body = '<textarea id="deployLogMsg" style="margin-left:2%;width:95%;height:93%;overflow-y:visible;resize:none;background-color: #FFF; margin:2%" readonly="readonly"></textarea>';
    
    w2popup.open({
        width   : 800,
        height  : 700,
        title   : "<b>CF Deployment 배포로그"+"</b>",
        body    : body,
        buttons : '<button class="btn" style="float: right; padding-right: 15%;" onclick="w2popup.close();">닫기</button>',
        showMax : true,
        onOpen  : function(event){
            event.onComplete = function(){
                $("#deployLogMsg").text(msg);
            }
        }
    });    
}


 /******************************************************************
  * 기능 : clearMainPage
  * 설명 : 다른페이지 이동시 cfDeployment Grid clear
  ***************************************************************** */
function clearMainPage() {
    $().w2destroy('config_cfDeployment_grid');
    $().w2destroy('config_cfDeployment_grid2');
    $().w2destroy('config_cfDeployment_grid3');
}

 /******************************************************************
  * 설명 : 화면 리사이즈시 호출 
  ***************************************************************** */
$( window ).resize(function() {
    setLayoutContainerHeight();
});

/******************************************************************
 * 기능 : popupComplete
 * 설명 : 설치 화면 닫기
 ***************************************************************** */
function popupComplete(){
    var msg;
    if(installStatus == "done" || installStatus == "error"){
        msg = $(".w2ui-msg-title b").text() + " 화면을 닫으시겠습니까?";
    }else{
        msg = $(".w2ui-msg-title b").text() + " 화면을 닫으시겠습니까?<BR>(닫은 후에도 완료되지 않는 설치 또는 삭제 작업은 계속 진행됩니다.)";
    }
    w2confirm({
        title   : $(".w2ui-msg-title b").text(),
        msg     : msg,
        yes_text: "확인",
        yes_callBack : function(envent){
            popupClose();
            w2popup.close();
        },
        no_text : "취소"
    });
}
</script>

<div id="main">
    <div class="page_site">이종 CF Deployment > <strong>이종 CF Deployment 설치</strong></div>
    <!-- cfDeployment 목록-->
    <div class="pdt20"> 
        <div class="title fl">배포 가능 한 Private/Public CF Deployment 목록 (더블 클릭) </div>
        <div class="fr"> 
            <sec:authorize access="hasAuthority('DEPLOY_HBCF_INSTALL')">
            <span id="installBtn" class="btn btn-primary"  style="width:120px">정보 등록</span>
            </sec:authorize>
            &nbsp;
            <sec:authorize access="hasAuthority('DEPLOY_HBCF_INSTALL')">
            <span id="modifyBtn" class="btn btn-info" style="width:120px">정보 수정</span>
            </sec:authorize>
            &nbsp;
            <sec:authorize access="hasAuthority('DEPLOY_HBCF_DELETE')">
            <span id="deleteBtn" class="btn btn-danger" style="width:120px">정보 삭제</span>
            </sec:authorize>
        </div>
    </div>
    <div id="config_cfDeployment_grid" style="width:100%; height:300px"></div>
    
    
    <div class="pdt20"> 
        <div class="title fl">배포 할 Private/Public CF Deployment 목록 (더블 클릭)</div>
        <div class="fr"> 
            <sec:authorize access="hasAuthority('DEPLOY_HBCF_INSTALL')">
            <span id="installVmBtn" class="btn btn-primary"  style="width:120px">VM 설치</span>
            </sec:authorize>
        </div>
    </div>
    <div id="config_cfDeployment_grid2" style="width:100%; height:300px"></div>
    
    <div class="pdt20"> 
        <div class="title fl">배포 한 Private/Public CF Deployment 목록 </div>
        <div class="fr"> 
            <sec:authorize access="hasAuthority('DEPLOY_HBCF_INSTALL')">
            <span id="modifyVmBtn" class="btn btn-info" style="width:120px">VM 수정</span>
            </sec:authorize>
            <sec:authorize access="hasAuthority('DEPLOY_HBCF_DELETE')">
            <span id="deleteVmBtn" class="btn btn-danger"  style="width:120px">VM 삭제</span>
            </sec:authorize>
        </div>
    </div>
    <div id="config_cfDeployment_grid3" style="width:100%; height:300px"></div>
</div>

<div id="cfDeploymentRegistInfoDiv" style="width:100%;height:100%;" hidden="true">
    <form id="settingForm" action="POST">
    <input class="form-control" name = "cfDeploymentId" type="hidden"/>
        <div class="w2ui-page page-0" style="margin-top:30px;padding:0 3%;">
            <div class="panel panel-info"> 
                <div class="panel-heading" style = "text-align: left; font-size:15px;"><b>이종 CF Deployment 설치 정보</b></div>
                <div class="panel-body" style="padding:5px 5% 7px 5%;">
                   <div class="w2ui-field">
                       <label style="width:40%; text-align: left;padding-left: 20px;">CF Deployment 정보 별칭</label>
                       <div>
                           <input class="form-control" name = "cfDeploymentConfigName" type="text"  maxlength="100" style="width: 350px; margin-left: 20px;" placeholder="CF Deployment 정보 별칭을 입력 하세요."/>
                       </div>
                   </div>
                  <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">클라우드 인프라 환경</label>
                       <div>
                           <select class="form-control" onchange="" name="iaasType" style="width: 350px; margin-left: 20px;">
                               <option value="">인프라 환경을 선택하세요.</option>
                               <option value="aws">AWS</option>
                               <option value="openstack">Openstack</option>
                           </select>
                       </div>
                   </div>
                   <div class="w2ui-field">
                      <label style="width:40%;text-align: left;padding-left: 20px;">CF Deployment 기본 정보 별칭</label>
                      <div style="width: 60%">
                          <select class="form-control" name="defaultConfigInfo" onchange="" style="width: 350px; margin-left: 20px;">
                              <option value="">CF Deployment 기본 정보를 선택하세요.</option>
                          </select>
                      </div>
                    </div>
                    <div class="w2ui-field">
                      <label style="width:40%;text-align: left;padding-left: 20px;">CF Deployment 네트워크 정보 별칭</label>
                      <div style="width: 60%">
                          <select class="form-control" name="networkConfigInfo" onchange="" style="width: 350px; margin-left: 20px;">
                              <option value="">CF Deployment 네트워크 정보를 선택하세요.</option>
                          </select>
                      </div>
                    </div>
                    <div class="w2ui-field">
                      <label style="width:40%;text-align: left;padding-left: 20px;">CF Deployment 인증서 정보 별칭</label>
                      <div style="width: 60%">
                          <select class="form-control" name="credentialConfigInfo" onchange="" style="width: 350px; margin-left: 20px;">
                              <option value="">CF Deployment 인증서 정보를 선택하세요.</option>
                          </select>
                      </div>
                    </div>
                    <div class="w2ui-field">
                      <label style="width:40%;text-align: left;padding-left: 20px;">CF Deployment 리소스 정보 별칭</label>
                      <div style="width: 60%">
                          <select class="form-control" name="resourceConfigInfo" onchange="" style="width: 350px; margin-left: 20px;">
                              <option value="">CF Deployment 리소스 정보를 선택하세요.</option>
                          </select>
                      </div>
                    </div>
                    <div class="w2ui-field">
                      <label style="width:40%;text-align: left;padding-left: 20px;">CF Deployment 인스턴스 정보 별칭</label>
                      <div style="width: 60%">
                          <select class="form-control" name="instanceConfigInfo" onchange="" style="width: 350px; margin-left: 20px;">
                              <option value="">CF Deployment 인스턴스 정보를 선택하세요.</option>
                          </select>
                      </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="w2ui-buttons" id="cfDeploymentRegistInfoBtnDiv" hidden="true">
            <button class="btn" id="registcfDeploymentInfoBtn" onclick="$('#settingForm').submit();">확인</button>
            <button class="btn" id="popClose" onclick="w2popup.close();">취소</button>
        </div>
    </form>
</div>

<!-- Deploy DIV -->
<div id="DeployDiv" style="width:100%;height:100%;" hidden="true">
    <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
        <ul class="progressStep_6" >
            <li class="active">배포 파일 정보</li>
            <li class="before">설치</li>
        </ul>
    </div>
    <div style="width:93%;height:84%;float: left;display: inline-block;margin:10px 0 0 1%;">
        <textarea id="deployInfo" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:3%;" readonly="readonly"></textarea>
    </div>
    <div class="w2ui-buttons" id="DeployBtnDiv" hidden="true">
        <button class="btn" style="float: right; padding-right: 15%" onclick="confirmDeploy('after');">다음>></button>
    </div>
</div>

<!-- Install DIV -->
<div id="InstallDiv1" style="width:100%;height:100%;" hidden="true">
    <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
        <ul class="progressStep_7" >
            <li style="font-size: 15px; width: 370px;" class="active">1 CF Deployment Install Log</li>
            <li style="font-size: 15px; width: 370px;" class="before">2 CF Deployment Install Log</li>
        </ul>
    </div>
    <div style="width:93%;height:84%;float: left;display: inline-block;margin:10px 0 0 1%;">
        <textarea id="installLogs" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:3%;" readonly="readonly"></textarea>
    </div>
    <div class="w2ui-buttons" id="InstallDivButtons" hidden="true">
            <button class="btn" style="float: right; padding-right: 15%" onclick="popupComplete();">닫기</button>
    </div>
</div>

<!-- Install DIV -->
<div id="InstallDiv2" style="width:100%;height:100%;" hidden="true">
    <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
        <ul class="progressStep_7" >
            <li style="font-size: 15px; width: 370px;" class="pass">1 CF Deployment Install Log</li>
            <li style="font-size: 15px; width: 370px;" class="active">2 CF Deployment Install Log</li>
        </ul>
    </div>
    <div style="width:93%;height:84%;float: left;display: inline-block;margin:10px 0 0 1%;">
        <textarea id="installLogs" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:3%;" readonly="readonly"></textarea>
    </div>
    <div class="w2ui-buttons" id="InstallDivButtons" hidden="true">
            <button class="btn" style="float: right; padding-right: 15%" onclick="popupComplete();">닫기</button>
    </div>
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
            cfDeploymentConfigName : {
                required : function(){
                  return checkEmpty( $(".w2ui-msg-body input[name='cfDeploymentConfigName']").val() );
                }, sqlInjection : function(){
                      return $(".w2ui-msg-body input[name='cfDeploymentConfigName']").val();
                }
            },
            iaasType : {
                required : function(){
                  return checkEmpty( $(".w2ui-msg-body select[name='iaasType']").val() );
                }
            },
            defaultConfigInfo : {
                 required : function(){
                     return checkEmpty( $(".w2ui-msg-body select[name='defaultConfigInfo']").val() );
                   }
            },
            networkConfigInfo : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body select[name='networkConfigInfo']").val() );
                  }
            },
            credentialConfigInfo : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body select[name='credentialConfigInfo']").val() );
                  }
            },
            resourceConfigInfo : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body select[name='resourceConfigInfo']").val() );
                  }
            },
            instanceConfigInfo : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body select[name='instanceConfigInfo']").val() );
                  }
            }
        }, messages: {
            cfDeploymentConfigName: { cfDeploymentConfigName:  "CF Deployment 정보 별칭" + text_required_msg },
            iaasType: { required:  "인프라 환경" + select_required_msg },
            defaultConfigInfo: { required:  "기본 정보" + select_required_msg },
            networkConfigInfo: { required:  "네트워크 정보" + select_required_msg },
            credentialConfigInfo: { required:  "인증서 정보" + select_required_msg },
            resourceConfigInfo: { required:  "리소스 정보" + select_required_msg },
            instanceConfigInfo: { required:  "인스턴스 정보" + select_required_msg }
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
            saveCfDeploymentInfo();
        }
    });
});
</script>