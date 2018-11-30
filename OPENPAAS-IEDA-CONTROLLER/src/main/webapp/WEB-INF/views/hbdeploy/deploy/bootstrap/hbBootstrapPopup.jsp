<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "spring" uri = "http://www.springframework.org/tags" %>

<script type="text/javascript">

var search_data_fail_msg = '<spring:message code="common.data.select.fail"/>'//목록을 가져오는데 실패하였습니다.
var save_lock_msg='<spring:message code="common.save.data.lock"/>';//등록 중 입니다.
var text_required_msg='<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
var select_required_msg='<spring:message code="common.select.vaildate.required.message"/>';//을(를) 선택하세요.
var text_ip_msg = '<spring:message code="common.text.validate.ip.message"/>';//IP을(를) 확인 하세요.

/******************************************************************
 * 설명 : 변수 설정
***************************************************************** */
var bootstrapId= "";
var iaasConfigInfo="";//인프라 환경 설정 정ㅈ보
var boshInfo = ""; //기본 정보
var networkInfo = "";//네트워크 정보
var resourceInfo = "";//리소스 정보
var deploymentInfo ="";//배포파일생성 정보
var installClient = "";//설치 client
var deleteClient = "";//삭제 client
var stemcells;//스템셀
var installStatus ="";//설치 상태
var boshCpiReleases;
var deployFileName;//배포파일명
var hybridBootstrapId="";
var publicBootStrapId="";
var privateBootstrapId="";
/******************************************************************
 * 기능 : getBootstrapData
 * 설명 : Bootstrap 상세 조회
 ***************************************************************** */
function getBootstrapData(iaas, bootstrapId){
    var url = "/deploy/hbBootstrap/install/detail/"+bootstrapId+"/"+iaas;
    $.ajax({
        type : "GET",
        url : url,
        contentType : "application/json",
        success : function(data, status) {
            initSetting();
            setBootstrapData(data);
        },
        error : function(request, status, error) {
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "BOOTSTRAP 수정");
        }
    });
}


/******************************************************************
 * 기능 : setBootstrapData
 * 설명 : Bootstrap 데이터 셋팅
 ***************************************************************** */
function setBootstrapData(contents){
    hybridBootstrapId = contents.hybridBootStrapId;
    bootstrapId =  contents.id;
    iaas = contents.iaasType;
    $("input[name=iaasType]").val(contents.iaasType);
    iaasConfigInfo = {
            id           : bootstrapId,
            iaasType     : iaas,
            iaasConfigId : contents.iaasConfigId,
            bootstrapType: contents.bootstrapType
    }
    boshInfo = {
            id               : bootstrapId,
            iaas             : contents.iaasType,
            deploymentName   : contents.deploymentName,
            directorName     : contents.directorName,
            credentialKeyName: contents.credentialKeyName,
            ntp              : contents.ntp,
            boshRelease      : contents.boshRelease,
            boshCpiRelease   : contents.boshCpiRelease,
            osConfRelease    : contents.osConfRelease,
            enableSnapshots  : contents.enableSnapshots,
            snapshotSchedule : contents.snapshotSchedule,
            paastaMonitoringUse : contents.paastaMonitoringUse,
            paastaMonitoringIp : contents.paastaMonitoringIp,
            paastaMonitoringRelease : contents.paastaMonitoringRelease,
            influxdbIp : contents.influxdbIp
    }
    networkInfo = {
            id                  : bootstrapId,
            subnetId            : contents.subnetId,
            networkName         : contents.networkName,
            privateStaticIp     : contents.privateStaticIp,
            publicStaticIp      : contents.publicStaticIp,
            subnetRange         : contents.subnetRange,
            subnetGateway       : contents.subnetGateway,
            subnetDns           : contents.subnetDns,
            publicSubnetId      : contents.publicSubnetId,
            publicSubnetRange   : contents.publicSubnetRange,
            publicSubnetGateway : contents.publicSubnetGateway,
            publicSubnetDns     : contents.publicSubnetDns,
    }
    resourceInfo = {
            id                : bootstrapId,
            stemcell          : contents.stemcell,
            boshPassword      : contents.boshPassword,
            cloudInstanceType : contents.cloudInstanceType,
            resourcePoolCpu   : contents.resourcePoolCpu,
            resourcePoolRam   : contents.resourcePoolRam,
            resourcePoolDisk  : contents.resourcePoolDisk
    }
    if( iaas == "AWS" ) awsPopup();
    else if( iaas == "Openstack" ) openstackPopup();
    else if( iaas == "vSphere" ) vSpherePopup();
    else if( iaas == "Google" ) googlePopup();
    else if( iaas == "Azure" ) azurePopup();
}


/******************************************************************
 * 기능 : awsPopup
 * 설명 : AWS 정보 입력 팝업 화면
 ***************************************************************** */
function awsPopup(deploymentFileName){
     w2popup.open({
        title  : "<b>Public 클라우드 환경 BOOTSTRAP 설치</b>",
        width  : 730,
        height : 520,
        onClose: popupClose,
        modal  : true,
        body   : $("#AWSInfoDiv").html(),
        buttons: $("#awsBtnDiv").html(),
        onOpen:function(event){
            event.onComplete = function(){
                $("input[name=iaasType]").val(iaas);
                $("input[name=privateDeploymentFileName]").val(deploymentFileName);
                getIaasConfigAliasList(iaas);
            }
        },onClose:function(event){
            gridReload();
        }
    });
}

/******************************************************************
 * 기능 : openstackPopup
 * 설명 : Openstack 정보 입력 팝업 화면
 ***************************************************************** */
function openstackPopup(){
     w2popup.open({
        title   : "<b>Private 클라우드 환경 BOOTSTRAP 설치</b>",
        width   : 730,
        height  : 580,
        onClose : popupClose,
        modal   : true,
        body    : $("#OpenstackInfoDiv").html(),
        buttons : $("#openstackInfoBtnDiv").html(),
        onOpen:function(event){
            event.onComplete = function(){
                $("input[name=iaasType]").val(iaas);
                getIaasConfigAliasList(iaas);
            }
        },onClose:function(event){
            gridReload();
        }
    }); 
}

/******************************************************************
 * 기능 : vSpherePopup
 * 설명 : vSphere 정보 입력 팝업 화면
 ***************************************************************** */
function vSpherePopup(){
     w2popup.open({
        title   : "<b>Private 클라우드 환경 BOOTSTRAP 설치</b>",
        width   : 730,
        height  : 600,
        onClose : popupClose,
        modal   : true,
        body    : $("#vSphereInfoDiv").html(),
        buttons : $("#vSphereInfoBtnDiv").html(),
        onOpen:function(event){
            event.onComplete = function(){
                $("input[name=iaasType]").val(iaas);
                getIaasConfigAliasList(iaas);
            }
        },onClose:function(event){
            gridReload()
        }
    }); 
}

/******************************************************************
 * 기능 : googlePopup
 * 설명 : Google 정보 입력 팝업 화면
 ***************************************************************** */
function googlePopup(deploymentFileName){
     w2popup.open({
        title   : "<b>Public 클라우드 환경 BOOTSTRAP 설치</b>",
        width   : 730,
        height  : 545,
        onClose : popupClose,
        modal   : true,
        body    : $("#GoogleInfoDiv").html(),
        buttons : $("#googleInfoBtnDiv").html(),
        onOpen:function(event){
            event.onComplete = function(){
                $(".w2ui-msg-body input[name=iaasType]").val(iaas);
                $("input[name=privateDeploymentFileName]").val(deploymentFileName);
                getIaasConfigAliasList(iaas);
            }
        },onClose:function(event){
            gridReload()
        }
    }); 
}

/******************************************************************
 * 기능 : azurePopup
 * 설명 : azure 정보 입력 팝업 화면
 ***************************************************************** */
function azurePopup(deploymentFileName){
     w2popup.open({
        title   : "<b>Public 클라우드 환경 BOOTSTRAP 설치</b>",
        width   : 730,
        height  : 650,
        onClose : popupClose,
        modal   : true,
        body    : $("#azureInfoDiv").html(),
        buttons : $("#azureInfoBtnDiv").html(),
        onOpen:function(event){
            event.onComplete = function(){
                $(".w2ui-msg-body input[name=iaasType]").val(iaas);
                $("input[name=privateDeploymentFileName]").val(deploymentFileName);
                getIaasConfigAliasList(iaas);
            }
        },onClose:function(event){
            gridReload();
        }
    }); 
}

/********************************************************
 * 설명 : 인프라 환경 설정 별칭 목록 조회
 * 기능 : getIaasConfigAliasList
 *********************************************************/
function getIaasConfigAliasList(iaas){
    $.ajax({
        type :"GET",
        url :"/common/deploy/list/iaasConfig/"+iaas, 
        contentType :"application/json",
        success :function(data, status) {
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
                $(".w2ui-msg-body select[name=iaasConfigId]").append(options);
                
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
                    if( data.openstackKeystoneVersion == "v2" ){
                        $(".w2ui-msg-body commonProject").css("display", "block");
                        $(".w2ui-msg-body div#openstackDomain").css("display", "none");
                        $(".w2ui-msg-body div#commonTenant").css("display", "none");
                        $(".w2ui-msg-body #region").css("display", "none");
                        $(".w2ui-msg-body input[name=commonRegion]").val("");
                        $(".w2ui-msg-body input[name=commonProject]").val("");
                    }else{
                        $(".w2ui-msg-body commonProject").css("display", "block");
                        $(".w2ui-msg-body div#openstackDomain").css("display", "block");
                        $(".w2ui-msg-body div#commonTenant").css("display", "none");
                        $(".w2ui-msg-body #region").css("display", "block");
                        $(".w2ui-msg-body input[name=commonTenant]").val("");
                    }
                    $(".w2ui-msg-body input[name=commonAccessEndpoint]").val(data.commonAccessEndpoint);
                    $(".w2ui-msg-body input[name=commonAccessUser]").val(data.commonAccessUser);
                    $(".w2ui-msg-body input[name=commonAccessSecret]").val(data.commonAccessSecret);
                    $(".w2ui-msg-body input[name=commonSecurityGroup]").val(data.commonSecurityGroup);
                    $(".w2ui-msg-body input[name=openstackKeystoneVersion]").val(data.openstackKeystoneVersion);
                    $(".w2ui-msg-body input[name=commonTenant]").val(data.commonTenant);
                    $(".w2ui-msg-body input[name=commonRegion]").val(data.commonRegion);
                    $(".w2ui-msg-body input[name=commonProject]").val(data.commonProject);
                    $(".w2ui-msg-body input[name=openstackDomain]").val(data.openstackDomain);
                    $(".w2ui-msg-body input[name=googleJsonKey]").val(data.googleJsonKey);
                    $(".w2ui-msg-body input[name=commonAvailabilityZone]").val(data.commonAvailabilityZone);
                    $(".w2ui-msg-body input[name=commonKeypairName]").val(data.commonKeypairName);
                    $(".w2ui-msg-body input[name=commonKeypairPath]").val(data.commonKeypairPath);
                    $(".w2ui-msg-body input[name=vsphereVcentDataCenterName]").val(data.vsphereVcentDataCenterName);
                    $(".w2ui-msg-body input[name=vsphereVcenterVmFolder]").val(data.vsphereVcenterVmFolder);
                    $(".w2ui-msg-body input[name=vsphereVcenterTemplateFolder]").val(data.vsphereVcenterTemplateFolder);
                    $(".w2ui-msg-body input[name=vsphereVcenterDatastore]").val(data.vsphereVcenterDatastore);
                    $(".w2ui-msg-body input[name=vsphereVcenterPersistentDatastore]").val(data.vsphereVcenterPersistentDatastore);
                    $(".w2ui-msg-body input[name=vsphereVcenterDiskPath]").val(data.vsphereVcenterDiskPath);
                    $(".w2ui-msg-body input[name=vsphereVcenterCluster]").val(data.vsphereVcenterCluster);
                    $(".w2ui-msg-body input[name=azureSubscriptionId]").val(data.azureSubscriptionId);
                    $(".w2ui-msg-body input[name=azureResourceGroupName]").val(data.azureResourceGroupName);
                    $(".w2ui-msg-body input[name=azureStorageAccountName]").val(data.azureStorageAccountName);
                    $(".w2ui-msg-body textarea[name=azureSshPublicKey]").val(data.azureSshPublicKey);
                    $(".w2ui-msg-body textarea[name='googleSshPublicKey']").val(data.googlePublicKey);
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


/******************************************************************
 * 기능 : saveIaasConfigInfo
 * 설명 : 인프라 환경 설정 정보 저장
 ***************************************************************** */
function saveIaasConfigInfo(){
     if( checkEmpty($(".w2ui-msg-body select[name=iaasConfigId]").val()) ){
         w2alert("인프라 환경 별칭"+select_required_msg,"BOOTSTRAP 설치");
         return;
     }
     var bootstrapDeployType = "";
     if(iaas=="Openstack" || iaas=="vSphere"){
         bootstrapDeployType = "private";
     }else {
         bootstrapDeployType = "public";
     }
     iaasConfigInfo = {
        id           : checkEmpty(bootstrapId) ? null:bootstrapId,
        iaasType     : iaas,
        privateDeploymentFileName : $("input[name=privateDeploymentFileName]").val(),
        iaasConfigId : $(".w2ui-msg-body select[name=iaasConfigId]").val(),
        bootstrapType: bootstrapDeployType
    }
    $.ajax({
        type : "PUT",
        url : "/deploy/hbBootstrap/install/setIaasConfigInfo",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(iaasConfigInfo),
        success : function(data, status) {
            w2popup.clear();
            iaasConfigInfo.id = data.id;
            hybridBootstrapId = data.hybridBootStrapId;
            privateBootstrapId = data.privateBootStrapId;
            publicBootStrapId = data.publicBootStrapId;
            bootstrapId = data.id;
            defaultInfoPop(iaas);
        },
        error : function( e, status ) {
            w2popup.unlock();
            w2alert("BOOTSTRAP "+iaas+" 정보 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
        }
    });
}

/******************************************************************
 * 기능 : defaultInfoPop
 * 설명 : Default Info popup
 ***************************************************************** */
function defaultInfoPop(iaas){
     settingPopupTab("progressStep_6", iaas, $("#DefaultInfoDiv ul"));
     var deployType = "";
     if(iaas == "Openstack" || iaas == "vSphere") deployType = "Pirvate";
     else deployType = "Public";
    
     w2popup.open({
        title   : "<b>"+deployType+" 클라우드 환경 BOOTSTRAP 설치</b>",
        width   : 730,
        height  : 760,
        onClose : popupClose,
        modal   : true,
        body    : $("#DefaultInfoDiv").html(),
        buttons : $("#DefaultInfoButtonDiv").html(),
        onOpen:function(event){
            event.onComplete = function(){
                if( iaas == "Google" ){
                    $(".w2ui-msg-body #osConfDiv").show();
                }
                
                $(".w2ui-msg-body input[name='ingestorIp']").attr("disabled", true);
                $(".w2ui-msg-body input[name='influxdbIp']").attr("disabled", true);
                $('[data-toggle="popover"]').popover();
                $(".paastaMonitoring-info").attr('data-content', "paasta-controller v3.0 이상에서 지원")
                if( !checkEmpty(boshInfo) && boshInfo != "" ){
                    $(".w2ui-msg-body input[name='deploymentName']").val(boshInfo.deploymentName);
                    $(".w2ui-msg-body input[name='directorName']").val(boshInfo.directorName);
                    $(".w2ui-msg-body select[name='credentialKeyName']").val(boshInfo.credentialKeyName);
                    $(".w2ui-msg-body input[name='ntp']").val(boshInfo.ntp);
                    $('.w2ui-msg-body input:radio[name=enableSnapshots]:input[value="' +boshInfo.enableSnapshots + '"]').attr("checked", true);    
                    if( !checkEmpty(boshInfo.enableSnapshots) ){
                        $(".w2ui-msg-body input[name='snapshotSchedule']").val(boshInfo.snapshotSchedule);
                        enableSnapshotsFn(boshInfo.enableSnapshots);
                    }else{
                        $('input:radio[name=enableSnapshots]:input[value=false]').attr("checked", true);
                        enableSnapshotsFn("false");
                    }
                    if( !checkEmpty(boshInfo.paastaMonitoringUse) ){
                        if( boshInfo.paastaMonitoringUse == "true"){
                            $(".w2ui-msg-body input[name='paastaMonitoring']").attr("checked", true);
                            $(".w2ui-msg-body input[name='ingestorIp']").removeAttr("disabled");
                            $(".w2ui-msg-body input[name='ingestorIp']").val(boshInfo.paastaMonitoringIp);
                            
                            $(".w2ui-msg-body input[name='influxdbIp']").removeAttr("disabled");
                            $(".w2ui-msg-body input[name='influxdbIp']").val(boshInfo.influxdbIp);
                            
                            $(".w2ui-msg-body select[name='paastaMonitoringRelease']").val(boshInfo.paastaMonitoringRelease);
                        }else{
                            $(".w2ui-msg-body input[name='paastaMonitoring']").attr("checked", false);
                            $(".w2ui-msg-body  select[name=paastaMonitoringRelease]").attr("disabled", true);
                        }
                    }
                }else{
                    $('input:radio[name=enableSnapshots]:input[value=false]').attr("checked", true);
                    enableSnapshotsFn("false");
                    checkPaasTAMonitoringUseYn();
                }
                
                getCredentialList();
                //ETC 릴리즈 정보 가져오기(PaaS-TA Monitoring 릴리즈)
                getLocalPaasTAMonitoringReleaseList('BOSH_MONITORING_AGENT');
                //BOSH 릴리즈 정보 가져오기
                getLocalBoshList('bosh');
                //BOSH CPI 릴리즈 정보 가져오기
                getLocalBoshCpiList('bosh_cpi', iaas);
                if( iaas == "Google" ){
                    getLocalBoshList('os-conf');
                }
                $('[data-toggle="popover"]').popover();
                getReleaseVersionList();
            }
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

/********************************************************
 * 설명 : 디렉터 인증서 목록 조회
 * 기능 : getReleaseVersionList
 *********************************************************/
function getCredentialList(){
    $.ajax({
        type : "GET",
        url : "/common/deploy/creds/list",
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
            $(".w2ui-msg-body select[name='credentialKeyName']").html(options);
        },
        error : function( e, status ) {
            w2alert("디렉터 인증서 "+search_data_fail_msg, "BOOTSTRAP 설치");
        }
    });
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
                $(".w2ui-msg-body select[name='boshRelease']").html(options);
            } else if(type= 'os_conf'){
                var options = "<option value=''>OS CONF 릴리즈를 선택하세요.</option>";
                for( var i=0; i<data.length; i++ ){
                    if( data[i] == boshInfo.osConfRelease ){
                        options += "<option value='"+data[i]+"' selected>"+data[i]+"</option>";
                    }else options += "<option value='"+data[i]+"'>"+data[i]+"</option>";
                }
                $(".w2ui-msg-body select[name='osConfRelease']").html(options)
            }
        },
        error : function( e, status ) {
            w2alert("Bosh 릴리즈 "+search_data_fail_msg, "BOOTSTRAP 설치");
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
                $(".w2ui-msg-body select[name='boshCpiRelease']").html(options);
            }
        },
        error : function( e, status ) {
            w2alert("Bosh Cpi "+search_data_fail_msg, "BOOTSTRAP 설치");
        }
    });
}


 /******************************************************************
  * 기능 : enableSnapshotsFn
  * 설명 : 스냅샷 가능 사용여부(사용일 경우)
  ***************************************************************** */
function enableSnapshotsFn(value){
    if(value == "true"){
        $(".w2ui-msg-body .snapshotScheduleDiv").show();
    }else if(value == "false"){
        $(".w2ui-msg-body  input[name=snapshotSchedule]").val("");
        $(".w2ui-msg-body .snapshotScheduleDiv").hide();
    }
}

/******************************************************************
 * 기능 : checkPaasTAMonitoringUseYn
 * 설명 : PaaS-TA 모니터링 가능 사용여부(사용일 경우)
 ***************************************************************** */
function checkPaasTAMonitoringUseYn(){
    var value = $("#paastaMonitoring:checked").val();
    if( value == "on"){
        $(".w2ui-msg-body  input[name=ingestorIp]").attr("disabled", false);
        $(".w2ui-msg-body  input[name=influxdbIp]").attr("disabled", false);
        $(".w2ui-msg-body  select[name=paastaMonitoringRelease]").attr("disabled", false);
        //ETC 릴리즈 정보 가져오기(PaaS-TA Monitoring 릴리즈)
        getLocalPaasTAMonitoringReleaseList('BOSH_MONITORING_AGENT');
    }else{
        $(".w2ui-msg-body  input[name=ingestorIp]").val("");
        $(".w2ui-msg-body  select[name=paastaMonitoringRelease]").val("");
        $(".w2ui-msg-body  input[name=ingestorIp]").attr("disabled", true);
        
        $(".w2ui-msg-body  input[name=influxdbIp]").attr("disabled", true);
        $(".w2ui-msg-body  input[name=influxdbIp]").val("");
        $(".w2ui-msg-body  select[name=paastaMonitoringRelease]").attr("disabled", true);
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
            if(type == 'BOSH_MONITORING_AGENT'){
                var options = '<option value="">PaaS-TA 모니터링 릴리즈를 선택하세요.</option>';
                for( var i=0; i<data.length; i++ ){
                    if( data[i] == boshInfo.paastaMonitoringRelease){
                        options += "<option value='"+data[i]+"' selected >"+data[i]+"</option>";
                    }else options += "<option value='"+data[i]+"'>"+data[i]+"</option>";
                    
                }
                $(".w2ui-msg-body select[name='paastaMonitoringRelease']").html(options);
            }
        },
        error: function(e, status){
            w2alert("Bosh 릴리즈 "+search_data_fail_msg, "BOOTSTRAP 설치");
        }
    });
 }
 
 

 
/******************************************************************
 * 기능 : saveDefaultInfo
 * 설명 : Default Info Save
 ***************************************************************** */
function saveDefaultInfo(type){
    if( $("#paastaMonitoring:checked").val() == "on"){
        var monitoringUse = "true";
        var ingrestorIp = $(".w2ui-msg-body input[name=ingestorIp]").val();
        var monitoringRelease = $(".w2ui-msg-body select[name=paastaMonitoringRelease]").val();
        var influxdbIp = $(".w2ui-msg-body input[name='influxdbIp']").val();
    }else{
        var monitoringUse = "false";
        var influxdbIp =  "";
        var ingrestorIp = "";
        var monitoringRelease = "";
    }
    
    var bootstrapDeployType = "";
    if(iaas=="Openstack" || iaas=="vSphere"){
        bootstrapDeployType = "private";
    }else {
        bootstrapDeployType = "public";
    }
    
    boshInfo = {
            id                  : iaasConfigInfo.id,
            iaasType            : iaas,
            deploymentName      : $(".w2ui-msg-body input[name=deploymentName]").val(),
            bootstrapType : bootstrapDeployType,
            directorName        : $(".w2ui-msg-body input[name=directorName]").val(),
            credentialKeyName   : $(".w2ui-msg-body select[name=credentialKeyName]").val(),
            ntp                 : $(".w2ui-msg-body input[name=ntp]").val(),
            boshRelease         : $(".w2ui-msg-body select[name=boshRelease]").val(),
            osConfRelease       : $(".w2ui-msg-body select[name=osConfRelease]").val(),
            boshCpiRelease      : $(".w2ui-msg-body select[name=boshCpiRelease]").val(),
            enableSnapshots     : $(".w2ui-msg-body input:radio[name=enableSnapshots]:checked").val(),
            snapshotSchedule    : $(".w2ui-msg-body input[name=snapshotSchedule]").val(),
            influxdbIp : influxdbIp,
            paastaMonitoringUse : monitoringUse,
            paastaMonitoringIp  : ingrestorIp,
            paastaMonitoringRelease : monitoringRelease
    }
    if(type == 'before'){
        w2popup.unlock();
        w2popup.clear();
        if(iaas.toUpperCase() == "OPENSTACK"){
            openstackPopup(); return;
        }else if(iaas.toUpperCase() == "AWS"){
            awsPopup(); return;
        }else if(iaas.toUpperCase() == "VSPHERE"){
            vSpherePopup(); return;
        }else if(iaas.toUpperCase() == "GOOGLE" ){
            googlePopup(); return;
        }else if(iaas.toUpperCase() == "AZURE" ){
            azurePopup(); return;
        }
    }else{
        $.ajax({
            type : "PUT",
            url : "/deploy/hbBootstrap/install/setDefaultInfo",
            contentType : "application/json",
            async : true,
            data : JSON.stringify(boshInfo),
            success : function(data, status) {
                w2popup.unlock();
                w2popup.clear();
                selectNetworkInfoPopup(iaas);
            },
            error : function( e, status ) {
                w2popup.unlock();
                w2popup.unlock();
                w2alert("기본정보 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
            }
        });
    }
}

/******************************************************************
 * 기능 : selectNetworkInfoPopup
 * 설명 : 인프라에 따른 네트워크 정보 팝업 화면 호출
 ***************************************************************** */
function selectNetworkInfoPopup(iaas){
    if( iaas.toUpperCase() == 'VSPHERE' ){
        networkInfoPopup("#VsphereNetworkInfoDiv", "#VsphereNetworkInfoBtnDiv", 680);
    }else if( iaas.toUpperCase() == "GOOGLE" ){
        networkInfoPopup("#GoogleNetworkInfoDiv", "#GoogleNetworkInfoBtnDiv", 570)   
    }else if( iaas.toUpperCase() == "AZURE" ){
        networkInfoPopup("#AzureNetworkInfoDiv", "#AzureNetworkInfoBtnDiv", 570)   
    }
    else{
        networkInfoPopup("#NetworkInfoDiv", "#NetworkInfoBtnDiv", 535);
    }
}
/******************************************************************
 * 기능 : networkInfoPopup
 * 설명 : Network Info Popup
 ***************************************************************** */
function networkInfoPopup(div, btn, height){
    settingPopupTab("progressStep_6", iaas, $(""+div+" ul"));
    var deployType = "";
    if(iaas == "Openstack" || iaas == "vSphere") deployType = "Pirvate";
    else deployType = "Public"
    w2popup.open({
        title   : "<b>"+deployType+" 클라우드 환경 BOOTSTRAP 설치</b>",
        width   : 730,
        height  : height,
        onClose : popupClose,
        modal   : true,
        body    : $(div).html(),
        buttons : $(btn).html(),
        onOpen:function(event){
            event.onComplete = function(){
                if(networkInfo != ""){
                    settingVSPhereNetworkInfo();
                }
            }
        }
    });
}

/******************************************************************
 * 기능 : settingVSPhereNetworkInfo
 * 설명 : vSphere 네트워크 정보 저장 
 ***************************************************************** */
function settingVSPhereNetworkInfo(){
    //Internal
    $(".w2ui-msg-body input[name='privateStaticIp']").val(networkInfo.privateStaticIp);
    $(".w2ui-msg-body input[name='subnetId']").val(networkInfo.subnetId);
    $(".w2ui-msg-body input[name='networkName']").val(networkInfo.networkName);
    $(".w2ui-msg-body input[name='subnetRange']").val(networkInfo.subnetRange);
    $(".w2ui-msg-body input[name='subnetGateway']").val(networkInfo.subnetGateway);
    $(".w2ui-msg-body input[name='subnetDns']").val(networkInfo.subnetDns);
    //External
    $(".w2ui-msg-body input[name='publicStaticIp']").val(networkInfo.publicStaticIp);
    $(".w2ui-msg-body input[name='publicSubnetId']").val(networkInfo.publicSubnetId);
    $(".w2ui-msg-body input[name='publicSubnetRange']").val(networkInfo.publicSubnetRange);
    $(".w2ui-msg-body input[name='publicSubnetGateway']").val(networkInfo.publicSubnetGateway);
    $(".w2ui-msg-body input[name='publicSubnetDns']").val(networkInfo.publicSubnetDns);
}

/******************************************************************
 * 기능 : saveNetworkInfo
 * 설명 : 네트워크 정보 저장 
 ***************************************************************** */
function saveNetworkInfo(type){
    var bootstrapDeployType = "";
    if(iaas=="Openstack" || iaas=="vSphere"){
        bootstrapDeployType = "private";
    }else {
        bootstrapDeployType = "public";
    }
    networkInfo = {
            id                  : bootstrapId,
            iaasType            : iaas,
            bootstrapType       : bootstrapDeployType,
            //private
            subnetId            : $(".w2ui-msg-body input[name='subnetId']").val(),
            networkName         : $(".w2ui-msg-body input[name='networkName']").val(),
            privateStaticIp     : $(".w2ui-msg-body input[name='privateStaticIp']").val(),
            publicStaticIp      : $(".w2ui-msg-body input[name='publicStaticIp']").val(),
            subnetRange         : $(".w2ui-msg-body input[name='subnetRange']").val(),
            subnetGateway       : $(".w2ui-msg-body input[name='subnetGateway']").val(),
            subnetDns           : $(".w2ui-msg-body input[name='subnetDns']").val(),
            
            //public
            publicStaticIp      : $(".w2ui-msg-body input[name='publicStaticIp']").val(),
            publicSubnetId      : $(".w2ui-msg-body input[name='publicSubnetId']").val(),
            publicSubnetRange   : $(".w2ui-msg-body input[name='publicSubnetRange']").val(),
            publicSubnetGateway : $(".w2ui-msg-body input[name='publicSubnetGateway']").val(),
            publicSubnetDns     : $(".w2ui-msg-body input[name='publicSubnetDns']").val(),
    }
    if( type == "before") {
        w2popup.unlock();
        defaultInfoPop(iaas);
        return;
    }else{
        var elements = $(".w2ui-box1 .w2ui-msg-body .w2ui-field input:visible");
        $.ajax({
            type : "PUT",
            url : "/deploy/hbBootstrap/install/setNetworkInfo",
            contentType : "application/json",
            async : true,
            data : JSON.stringify(networkInfo),
            success : function(data, status) {
                w2popup.unlock();
                w2popup.clear();
                if( iaas.toUpperCase() == "VSPHERE" ){
                    resourceInfoPopup(390);
                }else resourceInfoPopup(330);
            },
            error : function( e, status ) {
                w2popup.unlock();
                w2alert("Network 정보 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
            }
        });
    }
}

/******************************************************************
 * 기능 : resourceInfoPopup
 * 설명 : Resource Info Popup
 ***************************************************************** */
function resourceInfoPopup(height){
    settingPopupTab("ResourceInfoDiv", iaas, $("#ResourceInfoDiv ul"));
    var deployType = "";
    if(iaas == "Openstack" || iaas == "vSphere") deployType = "Pirvate";
    else deployType = "Public"
    w2popup.open({
        title   : "<b>"+deployType+" 클라우드 환경 BOOTSTRAP 설치</b>",
        width   : 730,
        height  : height,
        onClose : popupClose,
        modal   : true,
        body    : $("#ResourceInfoDiv").html(),
        buttons : $("#ResourceInfoBtnDiv").html(),
        onOpen:function(event){
            event.onComplete = function(){
                if(iaas.toUpperCase() == 'VSPHERE'){  
                    $(".w2ui-msg-body .cloudInstanceTypeDiv").hide(); 
                    $(".w2ui-msg-body .vsphereResourceDiv").show(); 
                }else{
                    $(".w2ui-msg-body .cloudInstanceTypeDiv").show();
                    $(".w2ui-msg-body .vsphereResourceDiv").hide(); 
                }
                getStemcellList(iaas);
            }
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
            stemcells = new Array();
            if(data.records != null){
                var option = "";
                data.records.map(function (obj){
                    option += "<option "+ obj.stemcellFileName +">"+obj.stemcellFileName+"</option>";
                });
            }else{
                option = "<option value=''>스템셀을 선택하세요.</option>"
            }
            $(".w2ui-msg-body select[name='stemcell']").html(option);
            setReourceData();
        },
        error : function( e, status ) {
            w2alert("스템셀 "+search_data_fail_msg, "BOOTSTRAP 설치");
        }
    });
}


/******************************************************************
 * 기능 : setReourceData
 * 설명 : Resource Info Setting
 ***************************************************************** */
function setReourceData(){
    if(resourceInfo != ""){
        $(".w2ui-msg-body #stemcell").val(resourceInfo.stemcell);
//         $(".w2ui-msg-body input[name='stemcell']").data('selected', {text:resourceInfo.stemcell});
        $(".w2ui-msg-body input[name='boshPassword']").val(resourceInfo.boshPassword);
        if(iaas.toUpperCase() != 'VSPHERE') { 
            $(".w2ui-msg-body input[name='cloudInstanceType']").val(resourceInfo.cloudInstanceType);
        }else{
            $(".w2ui-msg-body input[name='resourcePoolCpu']").val(resourceInfo.resourcePoolCpu);
            $(".w2ui-msg-body input[name='resourcePoolRam']").val(resourceInfo.resourcePoolRam);
            $(".w2ui-msg-body input[name='resourcePoolDisk']").val(resourceInfo.resourcePoolDisk);
        }
    }
}

/******************************************************************
 * 기능 : saveResourceInfo
 * 설명 : Openstack/AWS Resource Info Save
 ***************************************************************** */
function saveResourceInfo(type){
     
    var bootstrapDeployType = "";
    if(iaas=="Openstack" || iaas=="vSphere"){
        bootstrapDeployType = "private";
    }else {
        bootstrapDeployType = "public";
    }
     
    var cloudInstanceType = "";
     if(iaas != 'VSPHERE' ) { 
         cloudInstanceType =  $(".w2ui-msg-body input[name='cloudInstanceType']").val();  
    }
    resourceInfo = {
            id                : bootstrapId,
            stemcell          : $(".w2ui-msg-body select[name='stemcell']").val(),
            boshPassword      : $(".w2ui-msg-body input[name='boshPassword']").val(),
            bootstrapType       :bootstrapDeployType,
            iaasType            : iaas,
            cloudInstanceType : cloudInstanceType,
            resourcePoolCpu   : $(".w2ui-msg-body input[name='resourcePoolCpu']").val(),
            resourcePoolRam   : $(".w2ui-msg-body input[name='resourcePoolRam']").val(),
            resourcePoolDisk  : $(".w2ui-msg-body input[name='resourcePoolDisk']").val()
    }
    if( type == "before"){
        w2popup.unlock();
        w2popup.clear();
        selectNetworkInfoPopup(iaas);
    }else{
        $.ajax({
            type : "PUT",
            url : "/deploy/hbBootstrap/install/setResourceInfo",
            contentType : "application/json",
            async : true,
            data : JSON.stringify(resourceInfo),
            success : function(data, status) {
                w2popup.unlock();
                w2popup.clear();
                createSettingFile(data);
            },
            error :function(request, status, error) {
                w2popup.unlock();
                var errorResult = JSON.parse(request.responseText);
                w2alert(errorResult.message, "BOOTSTRAP 리소스 정보 저장");
                
            }
        });
    }
}


/******************************************************************
 * 기능 : createSettingFile
 * 설명 : 배포 파일 생성
 ***************************************************************** */
function createSettingFile(data){
    deploymentInfo = {
            iaasType       : data.iaasType,
            deploymentFile : data.deploymentFile
    }
    
    $.ajax({
        type : "POST",
        url : "/deploy/hbBootstrap/install/createSettingFile/"+ data.id+"/"+data.iaasType,
        contentType : "application/json",
        async : true,
        data : JSON.stringify(deploymentInfo),
        success : function(status) {
            var credentialFile = data.credentialKeyName;
            w2alert(""+iaas+" BOOTSTRAP 설치 성공 후 <br> 디렉터 설정 Credential 파일 명은 <br><strong><font color='red'> "+credentialFile+" </strong></font>입니다.");
            deployFileName = deploymentInfo.deploymentFile;
            deployPopup(deployFileName);
        },
        error :function(request, status, error) {
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "BOOTSTRAP 배포 파일 생성");
            if( iaas.toUpperCase() == "VSPHERE" ){
                resourceInfoPopup(390);
            }else resourceInfoPopup(330);
        }
    });
}


/******************************************************************
 * 기능 : deployPopup
 * 설명 : deploy File Info
 ***************************************************************** */
function deployPopup(deploymentFileName){
    
    settingPopupTab("DeployDiv", iaas, $("#DeployDiv ul"));
    var deployType = "";
    if(iaas == "Openstack" || iaas == "vSphere") deployType = "Pirvate";
    else deployType = "Public"
    w2popup.open({
        title   : "<b>"+deployType+" 클라우드 환경 BOOTSTRAP 설치</b>",
        width   : 730,
        height  : 615,
        modal   : true,
        showMax : true,
        body    : $("#DeployDiv").html(),
        buttons : $("#DeployBtnDiv").html(),
        onClose : popupClose,
        onOpen  : function(event){
            event.onComplete = function(){
                getDeployInfo();
                
            }
        }
    }); 
}

/******************************************************************
 * 기능 : getDeployInfo
 * 설명 : Manifest 파일 내용 출력
 ***************************************************************** */
function getDeployInfo(){
    $.ajax({
        type : "GET",
        url :"/common/use/deployment/"+deployFileName,
        contentType : "application/json",
        async : true,
        success : function(data, status) {
            if(status == "success"){
                $(".w2ui-msg-body #deployInfo").text(data);
            }
        },
        error : function( e, status ) {
            w2alert("Temp 파일을 가져오는 중 오류가 발생하였습니다. ", "BOOTSTRAP 설치");
        }
    });
}


/******************************************************************
 * 기능 : confirmDeploy
 * 설명 : bootstrap Install Confirm
 ***************************************************************** */
function deployment(type, div){
    var deployClassName = $("#DeployDiv ul").attr('class');
    if( (type == 'after' && deployClassName == 'progressStep_5') || (type == 'after' && div == 'firstPublicBootstrap') ){
        var selected = w2ui['config_hybrid_bootstrapGrid'].getSelection();
        var record = w2ui['config_hybrid_bootstrapGrid'].get(selected);
        if(selected.length == 0 || record.id == null){
            $("#bootstrapPopupDiv").load("/deploy/hbBootstrap/install/bootstrapPopup",function(event){
                if(publicIaas == "AWS"){
                    iaas = "AWS";
                    awsPopup(deployFileName);
                } else if (publicIaas == "Azure"){
                    iaas = "Azure";
                    azurePopup(deployFileName);
                } else if (publicIaas == "Google"){
                    iaas = "Google";
                    googlePopup(deployFileName);
                } else {
                    w2confirm({
                        width : 490, 
                        height : 200,
                        title : '<b>이기종 BOOTSTRAP 설치</b>',
                        msg : $(" #publicBootSelectBody").html(),
                        modal : true,
                        yes_text : "확인",
                        no_text : "취소",
                        yes_callBack : function(){
                            publicIaas = $("select[name='selectPublicIaas']").val();
                            deployment('after', "firstPublicBootstrap");
                        },no_callBack : function(event){
                          }
                    });
                }
            });
        } else {
            $("#bootstrapPopupDiv").load("/deploy/hbBootstrap/install/bootstrapPopup",function(event){
                iaas = record.hyPubIaas.toLowerCase();
                getBootstrapData(iaas, record.id, record.hybridBootStrapId);
            });
        }
    } else if(type == 'after' && deployClassName == 'progressStep_6'){
        var selected = w2ui['config_hybrid_bootstrapGrid'].getSelection();
        var record = w2ui['config_hybrid_bootstrapGrid'].get(selected);

        if( selected.length != 0 && record.hyPriId == null ){ 
            w2alert("Private Bootstrap이 존재 하지 않습니다. <br> <b>Public BootStrap 정보를 확인 후 삭제 해주세요.</b>");
        } else {
            w2confirm({
                msg          : "Public/Private BOOTSTRAP를 설치하시겠습니까?",
                title        : w2utils.lang('BOOTSTRAP 설치'),
                yes_text     : "예",
                no_text      : "아니오",
                yes_callBack : hybridInstallInfoSetting
            });
        }
    }else{
        w2popup.clear();
        if( iaas.toUpperCase() == "VSPHERE" ){
            resourceInfoPopup(390);
        }else resourceInfoPopup(330);
    }
}

/******************************************************************
 * 기능 : selectPublicIaas
 * 설명 : PublicIaaS 선택 Onchange Event
 ******************************************************************/
function selectPublicIaas(iaas) {
    $("select[name='selectPublicIaas']").val(iaas);
}

/******************************************************************
 * 기능 : selectPublicIaas
 * 설명 : Public/Private Bootstrap을 설치 하기 위해 정보 설정
 ******************************************************************/
function hybridInstallInfoSetting(){
    var url = "/deploy/hbBootstrap/install/hbDetail/"+privateBootstrapId+"/"+publicBootStrapId;
    $.ajax({
        type : "GET",
        url : url,
        contentType : "application/json",
        success : function(data, status) {
            privateInstallPopup(data);
        },
        error : function(request, status, error) {
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "BOOTSTRAP 수정");
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
        var FileName = "bootstrap";
        var message = "현재 다른 설치 관리자가 BOOTSTRAP를 사용 중 입니다.";
        lockFile = commonLockFile("<c:url value='/common/deploy/lockFile/"+FileName+"'/>",message);
    }
    return lockFile;
}

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



 /******************************************************************
  * 기능 : settingPopupTab
  * 설명 : 설치 팝업 화면에서 IaaS Tab명 설정 
  ***************************************************************** */
 function settingPopupTab(div, iaas, privateIaasDiv){
    $("ul."+div).find("li:eq(0)").each(function(i){
        $(this).html(iaas + " 정보");
    });
    if(iaas == "Openstack" || iaas == "vSphere"){
        privateIaasDiv.removeClass("progressStep_6");
        privateIaasDiv.addClass("progressStep_5");
        $(".progressStep_5 .private").hide();
    }
 }
 
 /******************************************************************
  * 기능 : gridReload
  * 설명 : 목록 재 조회
  ***************************************************************** */
 function gridReload(){
    w2ui['config_hybrid_bootstrapGrid'].clear();
    doSearch();
 }

 /******************************************************************
  * 기능 : doButtonStyle
  * 설명 : Button 제어
  ***************************************************************** */
function doButtonStyle(){
    //Button Style init
    $('#modifyBtn').attr('disabled', true);
    $('#deleteBtn').attr('disabled', true);
}

 /******************************************************************
  * 기능 : clearMainPage
  * 설명 : 다른페이지 이동시 Bootstrap Grid clear
  ***************************************************************** */
function clearMainPage() {
    $().w2destroy('config_hybrid_bootstrapGrid');
}

 /******************************************************************
  * 설명 : 화면 리사이즈시 호출 
  ***************************************************************** */
$( window ).resize(function() {
    setLayoutContainerHeight();
});


 /******************************************************************
  * 기능 : 전역변수 초기화
  * 설명 : initSetting
  ***************************************************************** */
function initSetting(){
    iaas = "";
    bootstrapId= "";
    boshInfo = "";
    networkInfo = "";
    resourceInfo = "";
    bootStrapDeploymentName = [];
    installClient = "";
    deleteClient = "";
    installStatus = "";
    lockFile ="";
    deployFileName="";
    publicIaas = "";
    privateIaas = "";
    publicBootStrapId="";
    privateBootstrapId="";
}

 /******************************************************************
  * 기능 : 팝업창 닫을 경우
  * 설명 : popupClose
  ***************************************************************** */
function popupClose() {
    //params init
    initSetting();
    //grid Reload
    gridReload();
    //button Control
    doButtonStyle();
}

</script>
<input type="hidden" name="bootstrapId" />
<input type="hidden" name="iaasType" />
<input type="hidden" name="privateDeploymentFileName"/>
<div id="AWSInfoDiv" style="width:100%;height:100%;" hidden="true">
    <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
        <ul class="progressStep_6" >
            <li class="active">AWS 정보</li>
            <li class="before">기본 정보</li>
            <li class="before">네트워크 정보</li>
            <li class="before">리소스 정보</li>
            <li class="before">배포 파일 정보</li>
            <li class="before">설치</li>
        </ul>
    </div>
    <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
    <div class="panel panel-info">
        <div class="panel-heading"><b>AWS 정보</b></div>
         <div class="panel-body" style="padding:5px 5% 10px 5%;">
             <div class="w2ui-field">
               <label style="text-align: left;width:40%;font-size:11px;">인프라 환경 별칭</label>
               <div style="width: 60%">
                   <select name="iaasConfigId" onchange="settingIaasConfigInfo(this.value);" style="width:70%;">
                       <option value="">인프라 환경 별칭을 선택하세요.</option>
                   </select>
               </div>
             </div>
             <div class="w2ui-field">
               <label style="text-align: left;width:40%;font-size:11px;">Access Key ID</label>
               <div style="width: 60%">
                   <input name=commonAccessUser type="text" style="float:left;width:70%;" readonly placeholder="AWS Access Key를 입력하세요."/>
               </div>
             </div>
             <div class="w2ui-field">
               <label style="text-align: left;width:40%;font-size:11px;">Secret Access Key</label>
               <div style="width: 60%">
                   <input name="commonAccessSecret" type="password" style="float:left;width:70%;" readonly placeholder="AWS Secret Access Key를 입력하세요."/>
               </div>
             </div>
             <div class="w2ui-field">
               <label style="text-align: left;width:40%;font-size:11px;">Security Group</label>
               <div style="width: 60%">
                   <input name="commonSecurityGroup" type="text" style="float:left;width:70%;" readonly placeholder="보안 그룹을 입력하세요."/>
               </div>
             </div>
             <div class="w2ui-field">
               <label style="text-align: left;width:40%;font-size:11px;">Region</label>
               <div style="width: 60%">
                   <input name="commonRegion" type="text" style="float:left;width:70%;" readonly placeholder="지역을 입력하세요.(예: us-east-1)"/>
               </div>
             </div>
             <div class="w2ui-field">
               <label style="text-align: left;width:40%;font-size:11px;">Availability Zone</label>
               <div style="width: 60%">
                   <input name="commonAvailabilityZone" type="text" style="float:left;width:70%;" readonly placeholder="가용 영역을 입력하세요."/>
               </div>
             </div>
             <div class="w2ui-field">
               <label style="text-align: left;width:40%;font-size:11px;">Private Key Name</label>
               <div style="width: 60%">
                 <input name="commonKeypairName" type="text" style="float:left;width:70%;" readonly placeholder="Key Pair 이름을 입력하세요."/>
               </div>
             </div>
             <div class="w2ui-field">
               <label style="text-align: left;width:40%;font-size:11px;">Private Key Path</label>
               <div style="width: 60%">
                   <input name="commonKeypairPath" type="text" style="float:left;width:70%;" readonly placeholder="Key path를 입력하세요."/>
                </div>
             </div>
          </div>
     </div>
    </div>
    <div class="w2ui-buttons" id="awsBtnDiv" hidden="true">
        <button class="btn" style="float: left; padding-right: 15%" onclick="settingBeforePrivateIaaSInfo();">이전</button>
        <button class="btn" style="float: right;padding-right:15%" onclick="saveIaasConfigInfo();" >다음>></button>
    </div>
</div>
<div id="OpenstackInfoDiv" style="width:100%;height:100%;" hidden="true">
    <div style="margin-left:2%;display:inline-block;width:97%; padding-top:20px;">
        <ul class="progressStep_5" >
            <li class="active">Openstack 정보</li>
            <li class="before">기본 정보</li>
            <li class="before">네트워크 정보</li>
            <li class="before">리소스 정보</li>
            <li class="before">배포 파일 정보</li>
        </ul>
    </div>
    <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
        <div class="panel panel-info"> 
            <div class="panel-heading"><b>오픈스택 정보</b></div>
            <div class="panel-body" style="padding:5px 5% 10px 5%;">
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">인프라 환경 별칭</label>
                    <div style="width:60%;">
                        <select name="iaasConfigId" onchange="settingIaasConfigInfo(this.value);" style="width:70%;">
                            <option value="">인프라 환경 별칭을 선택하세요.</option>
                        </select>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">Identify API Tokens URL</label>
                    <div style="width:60%;">
                        <input name="commonAccessEndpoint" type="text" readonly style="float:left;width:70%;" placeholder="Identify API Tokens URL을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field" id="openstackKeystoneVersion">
                    <label style="text-align: left;width:40%;font-size:11px;">Keystone Version</label>
                    <div style="width:60%;">
                        <input name="openstackKeystoneVersion" type="text" readonly style="float:left;width:70%;" placeholder="키스톤 버전을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field" id="region" style="display:none;">
                    <label style="text-align: left;width:40%;font-size:11px;">Region</label>
                    <div style="width:60%;">
                        <input name="commonRegion" type="text" style="float:left;width:70%;" readonly placeholder="Region을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field" id="commonTenant">
                    <label style="text-align: left;width:40%;font-size:11px;">Tenant</label>
                    <div style="width:60%;">
                        <input name="commonTenant" type="text" readonly style="float:left;width:70%;" placeholder="테넌트 명을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field" id="commonProject" style="display:none;">
                    <label style="text-align: left;width:40%;font-size:11px;">Project</label>
                    <div style="width:60%;">
                        <input name="commonProject" type="text" readonly style="float:left;width:70%;" placeholder="프로젝트 명을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field" id="openstackDomain" hidden="true">
                    <label style="text-align: left;width:40%;font-size:11px;">Domain</label>
                    <div style="width:60%;">
                        <input name="openstackDomain" type="text" readonly style="float:left;width:70%;" placeholder="도메인 명을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">Username</label>
                    <div style="width:60%;">
                        <input name="commonAccessUser" type="text" readonly style="float:left;width:70%;" placeholder="계정명을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">Password</label>
                    <div style="width:60%;">
                        <input name="commonAccessSecret" type="password" readonly style="float:left;width:70%;" placeholder="계정 비밀번호를 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">Security Group</label>
                    <div style="width:60%;">
                        <input name="commonSecurityGroup" type="text" readonly style="float:left;width:70%;" placeholder="시큐리티 그룹을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">Private Key Name</label>
                    <div style="width:60%;">
                        <input name="commonKeypairName" type="text" readonly style="loat:left;width:70%;"  placeholder="Key Pair 이름을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">Private Key Path</label>
                    <div style="width:60%;">
                        <input name="commonKeypairPath" type="text" readonly style="float:left;width:70%;"  placeholder="Key path를 입력하세요."/>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="w2ui-buttons" id="openstackInfoBtnDiv" hidden="true">
        <button class="btn" style="float: right; padding-right: 15%" onclick="saveIaasConfigInfo();">다음>></button>
    </div>
</div>
<div id="vSphereInfoDiv" style="width:100%;height:100%;" hidden="true">
    <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
        <ul class="progressStep_5" >
            <li class="active">vSphere 정보</li>
            <li class="before">기본 정보</li>
            <li class="before">네트워크 정보</li>
            <li class="before">리소스 정보</li>
            <li class="before">배포 파일 정보</li>
        </ul>
    </div>
    <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
        <div class="panel panel-info">
            <div class="panel-heading"><b>VSPHERE 정보</b></div>
            <div class="panel-body" style="padding:5px 5% 10px 5%;">
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">인프라 환경 별칭</label>
                    <div style="width: 60%">
                        <select name="iaasConfigId" onchange="settingIaasConfigInfo(this.value);" style="width:70%;">
                            <option value="">인프라 환경 별칭을 선택하세요.</option>
                        </select>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">vCenter IP</label>
                    <div style="width: 60%">
                        <input name="commonAccessEndpoint" type="text" readonly style="float:left;width:70%;" placeholder="vCenter IP를 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">vCenter ID</label>
                    <div style="width: 60%">
                        <input name="commonAccessUser" type="text" readonly style="float:left;width:70%;" placeholder="vCenter 로그인 ID를 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">vCenter Password</label>
                    <div style="width: 60%">
                        <input name="commonAccessSecret" type="password" readonly style="float:left;width:70%;" placeholder="vCenter 로그인 비밀번호를 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">vCenter DataCenter</label>
                    <div style="width: 60%">
                        <input name="vsphereVcentDataCenterName" type="text" readonly style="float:left;width:70%;" placeholder="vCenter DataCenter명을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">DataCenter VM Folder Name</label>
                    <div style="width: 60%">
                        <input name="vsphereVcenterVmFolder" type="text" readonly style="float:left;width:70%;" placeholder="DataCenter VM 폴더명을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">DataCenter VM Stemcell Folder Name</label>
                    <div style="width: 60%">
                        <input name="vsphereVcenterTemplateFolder" type="text" readonly style="float:left;width:70%;" placeholder="DataCenter VM 스템셀 폴더명을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">DataCenter DataStore</label>
                    <div style="width: 60%">
                        <input name="vsphereVcenterDatastore" type="text" readonly style="float:left;width:70%;" placeholder="DataCenter 데이터 스토어를 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">vCenter Persistent Datastore</label>
                    <div style="width: 60%">
                        <input name="vsphereVcenterPersistentDatastore" type="text" readonly style="float:left;width:70%;" placeholder="DataCenter 영구 데이터 스토어를 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">vCenter Disk Path</label>
                    <div style="width: 60%">
                        <input name="vsphereVcenterDiskPath" type="text" readonly style="float:left;width:70%;" placeholder="DataCenter 디스크 경로를 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">vCenter Cluster</label>
                    <div style="width: 60%">
                        <input name="vsphereVcenterCluster" type="text" readonly style="float:left;width:70%;" placeholder="DataCenter 클러스터명을 입력하세요."/>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="w2ui-buttons" id="vSphereInfoBtnDiv" hidden="true">
        <button class="btn" style="float: right; padding-right: 15%" onclick="saveIaasConfigInfo();">다음>></button>
    </div>
</div>
<div id="GoogleInfoDiv" style="width:100%;height:100%;" hidden="true">
    <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
        <ul class="progressStep_6" >
            <li class="active">Google 정보</li>
            <li class="before">기본 정보</li>
            <li class="before">네트워크 정보</li>
            <li class="before">리소스 정보</li>
            <li class="before">배포 파일 정보</li>
            <li class="before">설치</li>
        </ul>
    </div>
    <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
        <div class="panel panel-info"> 
            <div class="panel-heading"><b>Google 정보</b></div>
            <div class="panel-body" style="padding:5px 5% 10px 5%;">
                <div class="w2ui-field">
                  <label style="text-align: left;width:40%;font-size:11px;">인프라 환경 별칭</label>
                  <div style="width: 60%">
                      <select name="iaasConfigId" onchange="settingIaasConfigInfo(this.value);" style="width:80%;">
                          <option value="">인프라 환경 별칭을 선택하세요.</option>
                      </select>
                  </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">Project Id</label>
                    <div style="width: 60%">
                        <input name="commonProject" type="text" readonly style="float:left;width:80%;" placeholder="프로젝트 아이디를 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">서비스 계정 Json 파일</label>
                    <div style="width: 60%">
                        <input name="googleJsonKey" type="text" readonly style="float:left;width:80%;" placeholder="서비스 계정 Json 파일을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">Zone</label>
                    <div style="width: 60%">
                        <input name="commonAvailabilityZone" type="text" readonly style="float:left;width:80%;" placeholder="google zone을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">네트워크 태그 명</label>
                    <div style="width: 60%">
                        <input name="commonSecurityGroup" type="text" readonly style="float:left;width:80%;" placeholder="네트워크 태그 명을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">Public Key</label>
                    <div style="width: 60%">
                        <textarea name="googleSshPublicKey" readonly style="float:left;width:80%; height:85px;resize:none;"rows=10; placeholder="SSH 공개 키를 입력하세요."></textarea>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">Private Key File</label>
                    <div style="width: 60%">
                        <input name="commonKeypairPath" type="text" readonly style="float:left;width:80%;" placeholder="Private Key File을 입력하세요."/>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="w2ui-buttons" id="googleInfoBtnDiv" hidden="true">
        <button class="btn" style="float: left; padding-right: 15%" onclick="settingBeforePrivateIaaSInfo();">이전</button>
        <button class="btn" style="float: right; padding-right: 15%" onclick="saveIaasConfigInfo();">다음>></button>
    </div>
</div>

<div id="azureInfoDiv" style="width:100%;height:100%;" hidden="true">
    <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
        <ul class="progressStep_6" >
            <li class="active">Azure 정보</li>
            <li class="before">기본 정보</li>
            <li class="before">네트워크 정보</li>
            <li class="before">리소스 정보</li>
            <li class="before">배포 파일 정보</li>
            <li class="before">설치</li>
        </ul>
    </div>
    <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
        <div class="panel panel-info"> 
            <div class="panel-heading"><b>Azure 정보</b></div>
            <div class="panel-body" style="padding:5px 5% 10px 5%;">
                <div class="w2ui-field">
                  <label style="text-align: left;width:40%;font-size:11px;">인프라 환경 별칭</label>
                  <div style="width: 60%">
                      <select name="iaasConfigId" onchange="settingIaasConfigInfo(this.value);" style="width:80%;">
                          <option value="">인프라 환경 별칭을 선택하세요.</option>
                      </select>
                  </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">Subscription Id</label>
                    <div style="width: 60%">
                        <input name="azureSubscriptionId" type="text" readonly style="float:left;width:80%;" placeholder="구독 아이디를 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">Tenant</label>
                    <div style="width: 60%">
                        <input name="commonTenant" type="text" readonly style="float:left;width:80%;" placeholder="테넌트를 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">Application Id</label>
                    <div style="width: 60%">
                        <input name="commonAccessUser" type="text" readonly style="float:left;width:80%;" placeholder="어플리케이션 아이디(Client Id)를 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">Application Key</label>
                    <div style="width: 60%">
                        <input name="commonAccessSecret" type="password" readonly style="float:left;width:80%;" placeholder="어플리케이션 키(Client Key)를 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">Security Group</label>
                    <div style="width: 60%">
                        <input name="commonSecurityGroup" type="text" readonly style="float:left;width:80%;" placeholder="보안 그룹을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">Resource Group</label>
                    <div style="width: 60%">
                        <input name="azureResourceGroupName" type="text" readonly style="float:left;width:80%;" placeholder="리소스 그룹을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">Storage Account</label>
                    <div style="width: 60%">
                        <input name="azureStorageAccountName" type="text" readonly style="float:left;width:80%;" placeholder="저장소 계정 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">Private key File</label>
                    <div style="width: 60%">
                        <input name="commonKeypairPath" type="text" readonly style="float:left;width:80%;" placeholder="개인 키 파일을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;width:40%;font-size:11px;">SSH Public Key</label>
                    <div style="width: 60%">
                        <textarea name="azureSshPublicKey" readonly style="float:left;width:80%; height:85px;resize:none;"rows=10; placeholder="SSH 공개 키를 입력하세요."></textarea>
                        <br/>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="w2ui-buttons" id="azureInfoBtnDiv" hidden="true">
        <button class="btn" style="float: left; padding-right: 15%" onclick="settingBeforePrivateIaaSInfo();">이전</button>
        <button class="btn" style="float: right; padding-right: 15%" onclick="saveIaasConfigInfo();">다음>></button>
    </div>
</div>


<!-- 기본 설정 정보 -->
<div id="DefaultInfoDiv" style="width:100%;height:100%;" hidden="true">
    <form id="defaultInfoForm" >
        <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
            <ul class="progressStep_6" >
                <li class="pass"></li>
                <li class="active">기본 정보</li>
                <li class="before">네트워크 정보</li>
                <li class="before">리소스 정보</li>
                <li class="before">배포 파일 정보</li>
                <li class="before private">설치</li>
            </ul>
        </div>
        <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
            <div class="panel panel-info">    
                <div class="panel-heading"><b>기본 정보</b></div>
                <div class="panel-body" style="padding:5px 5% 10px 5%;">
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">배포명</label>
                        <div style="width: 60%">
                            <input name="deploymentName" type="text" style="display:inline-block;width:70%;" placeholder="배포명을 입력하세요."/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">디렉터 명</label>
                        <div style="width: 60%">
                            <input name="directorName" type="text" style="display:inline-block;width:70%;" placeholder="디렉터 명을 입력하세요."/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">디렉터 접속 인증서</label>
                        <div style="width: 60%">
                            <select name="credentialKeyName"  style="display:inline-block;width:70%;">
                                <option value="">디렉터 인증서를 선택하세요.</option>
                            </select>
                        </div>
                    </div>
                    
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">NTP</label>
                        <div style="width: 60%">
                            <input name="ntp" type="text" style="display:inline-block;width:70%;" placeholder="예) 10.0.0.2"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">BOSH 릴리즈
                            <span class="glyphicon glyphicon glyphicon-question-sign boshRelase-info" style="cursor:pointer;font-size: 14px;color: #157ad0;" data-toggle="popover"  data-trigger="hover" data-html="true" title="설치 지원 버전 목록"></span>
                        </label>
                        <div style="width: 60%">
                            <select name="boshRelease"  class="form-control select-control">
                                <option value="">BOSH 릴리즈를 선택하세요.</option>
                            </select>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align:left; width:36%; font-size:11px;">BOSH CPI 릴리즈</label>
                        <div style="width: 60%">
                            <select name="boshCpiRelease" class="form-control select-control">
                                <option value="">BOSH CPI 릴리즈를 선택하세요.</option>
                            </select>
                        </div>
                    </div>
 
                    <div class="w2ui-field" id="osConfDiv" hidden="true"> 
                        <label style="text-align:left; width:36%; font-size:11px;">OS-CONF 릴리즈</label>
                        <div style="width: 60%">
                            <select name="osConfRelease" class="form-control select-control">
                                <option value="">OS-CONF 릴리즈를 선택하세요.</option>
                            </select>
                        </div>
                    </div>
                    
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">스냅샷기능 사용여부</label>
                        <div style="width: 60%">
                              <span onclick="enableSnapshotsFn('true');" style="width:30%;"><label><input type="radio" name="enableSnapshots" value="true" />&nbsp;사용</label></span>
                            &nbsp;&nbsp;
                            <span onclick="enableSnapshotsFn('false');" style="width:30%;"><label><input type="radio" name="enableSnapshots" value="false" />&nbsp;미사용</label></span>
                        </div>
                    </div>
                    <div class="w2ui-field snapshotScheduleDiv" id="snapshotScheduleDiv">
                       <label style="text-align: left;width:36%;font-size:11px;">스냅샷 스케쥴</label>
                        <div style="width: 60%">
                            <input name="snapshotSchedule" id="snapshotSchedule" type="text"  style="display:inline-block;width:70%;" required placeholder="예) 0 0 7 * * * UTC"/>
                        </div>
                    </div>
                    
                    <div class="w2ui-field">
                        <label style="text-align:left; width:36%; font-size:11px;">PaaS-TA 모니터링
                        <span class="glyphicon glyphicon glyphicon-question-sign paastaMonitoring-info" style="cursor:pointer;font-size: 14px;color: #157ad0;" data-toggle="popover"  data-trigger="click" data-html="true"></span>
                        </label>
                        <div style="width: 60%">
                            <input name="paastaMonitoring" type="checkbox" id="paastaMonitoring" onclick="checkPaasTAMonitoringUseYn()"/>사용
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="panel panel-info" style="margin-top:2%;">
                <div class="panel-heading"><b>PaaS-TA 모니터링 정보</b></div>
                <div class="panel-body">
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 36%; font-size: 11px;">PaaS-TA 모니터링 Ingestor 서버 IP</label>
                        <div style="width: 60%">
                            <input name="ingestorIp" type="text" style="display:inline-block; width: 70%;" placeholder="예)10.0.0.0" />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 36%; font-size: 11px;">PaaS-TA 모니터링 Influxdb 서버 IP</label>
                        <div style="width: 60%">
                            <input name="influxdbIp" type="text" style="display:inline-block; width: 70%;" placeholder="예)10.0.0.0" />
                        </div>
                    </div>
                    
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 36%; font-size: 11px;">PaaS-TA 모니터링 릴리즈</label>
                        <div style="width: 60%">
                            <select name="paastaMonitoringRelease" class="form-control select-control">
                                <option value="">PaaS-TA 모니터링 릴리즈를 선택하세요.</option>
                            </select>
                        </div>
                    </div>
                </div>
             </div>
            
        </div>
    </form>
    <div class="w2ui-buttons" id="DefaultInfoButtonDiv"hidden="true">
        <button class="btn" style="float: left;" onclick="saveDefaultInfo('before');" >이전</button>
        <button class="btn" style="float: right;padding-right:15%" onclick="$('#defaultInfoForm').submit();" >다음>></button>
    </div>
</div>
<!-- 네트워크 div -->
<div id="NetworkInfoDiv" style="width:100%;height:100%;" hidden="true">
    <form id="networkInfoForm">
        <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
            <ul class="progressStep_6" >
                <li class="pass"></li>
                <li class="pass">기본 정보</li>
                <li class="active">네트워크 정보</li>
                <li class="before">리소스 정보</li>
                <li class="before">배포 파일 정보</li>
                <li class="before private">설치</li>
            </ul>
        </div>
        <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
             <div class="panel panel-info" style="margin-bottom:20px;">    
                 <div  class="panel-heading" style="padding:5px 5% 10px 5%;"><b>External 네트워크 정보</b></div>
                 <div class="panel-body">
                     <div class="w2ui-field">
                         <label style="text-align: left;width:36%;font-size:11px;">설치관리자 IPs</label> 
                         <div style="width: 60%">
                             <input name="publicStaticIp" type="text" style="display:inline-block;width:70%;" placeholder="예) 10.0.0.20"/>
                         </div>
                     </div>
                 </div>
             </div>
             <div class="panel panel-info" style="margin-bottom:20px;">    
                 <div  class="panel-heading" style="padding:5px 5% 10px 5%;"><b>Internal 네트워크 정보</b></div>
                 <div class="panel-body">
                     <div class="w2ui-field" >
                         <label style="text-align: left;width:36%;font-size:11px;">설치관리자 내부망 IPs</label> 
                         <div style="width: 60%">
                             <input name="privateStaticIp" type="text"  style="display:inline-block;width:70%;" placeholder="예) 10.0.0.20"/>
                         </div>
                     </div>
                     <div class="w2ui-field">
                         <label class="subnetId" style="text-align: left;width:36%;font-size:11px;">서브넷 아이디</label>
                         <div style="width: 60%">
                             <input name="subnetId" type="text"  style="display:inline-block;width:70%;" placeholder="서브넷 아이디를 입력하세요."/>
                         </div>
                     </div>
                     <div class="w2ui-field">
                         <label style="text-align: left;width:36%;font-size:11px;">서브넷 범위</label>
                         <div style="width: 60%">
                             <input name="subnetRange" type="text"  style="display:inline-block;width:70%;" placeholder="예) 10.0.0.0/24"/>
                         </div>
                     </div>
                     <div class="w2ui-field">
                         <label style="text-align: left;width:36%;font-size:11px;">게이트웨이</label>
                         <div style="width: 60%">
                             <input name="subnetGateway" type="text" style="display:inline-block;width:70%;" placeholder="예) 10.0.0.1"/>
                         </div>
                     </div>
                     <div class="w2ui-field">
                         <label style="text-align: left;width:36%;font-size:11px;">DNS</label>
                         <div style="width: 60%">
                             <input name="subnetDns" type="text" style="display:inline-block;width:70%;" placeholder="예) 8.8.8.8"/>
                         </div>
                     </div>
                 </div>
             </div>
         </div>
    </form>
    <div class="w2ui-buttons" id="NetworkInfoBtnDiv" hidden="true">
        <button class="btn" style="float: left;" onclick="saveNetworkInfo('before');" >이전</button>
        <button class="btn" style="float: right; padding-right: 15%" onclick="setNetworkValidate('#networkInfoForm');" >다음>></button>
    </div>
</div>

<!-- Google 네트워크 div -->
<div id="GoogleNetworkInfoDiv" style="width:100%;height:100%;" hidden="true">
    <form id="GoogleNetworkInfoForm">
        <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
            <ul class="progressStep_6" >
                <li class="pass">Google 정보</li>
                <li class="pass">기본 정보</li>
                <li class="active">네트워크 정보</li>
                <li class="before">리소스 정보</li>
                <li class="before">배포 파일 정보</li>
                <li class="before private">설치</li>
            </ul>
        </div>
        <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
             <div class="panel panel-info"  style="margin-bottom:20px;">
                 <div  class="panel-heading" style="padding:5px 5% 10px 5%;"><b>네트워크 External</b></div>
                 <div class="panel-body">
                     <div class="w2ui-field">
                         <label style="text-align: left;width:36%;font-size:11px;">설치관리자 IPs</label> 
                         <div style="width: 60%">
                             <input name="publicStaticIp" type="text"  style="display:inline-block;width:70%;" placeholder="예) 10.0.0.20"/>
                         </div>
                     </div>
                 </div>
             </div>
             <div class="panel panel-info" style="margin-bottom:20px;">    
                 <div  class="panel-heading" style="padding:5px 5% 10px 5%;"><b>네트워크 Internal</b></div>
                 <div class="panel-body">
                     <div class="w2ui-field" >
                         <label style="text-align: left;width:36%;font-size:11px;">설치관리자 내부망 IPs</label> 
                         <div style="width: 60%">
                             <input name="privateStaticIp" type="text"  style="display:inline-block;width:70%;" placeholder="예) 10.0.0.20"/>
                         </div>
                     </div>
                     <div class="w2ui-field">
                         <label style="text-align: left;width:36%;font-size:11px;">네트워크 명</label>
                         <div style="width: 60%">
                             <input name="networkName" type="text"  style="display:inline-block;width:70%;" placeholder="네트워크 명을 입력하세요."/>
                         </div>
                     </div>
                     <div class="w2ui-field">
                         <label class="subnetId" style="text-align: left;width:36%;font-size:11px;">서브넷 명</label>
                         <div style="width: 60%">
                             <input name="subnetId" type="text"  style="display:inline-block;width:70%;" placeholder="서브넷 명을 입력하세요."/>
                         </div>
                     </div>
                     <div class="w2ui-field">
                         <label style="text-align: left;width:36%;font-size:11px;">서브넷 범위</label>
                         <div style="width: 60%">
                             <input name="subnetRange" type="text"  style="display:inline-block;width:70%;"  placeholder="예) 10.0.0.0/24"/>
                         </div>
                     </div>
                     <div class="w2ui-field">
                         <label style="text-align: left;width:36%;font-size:11px;">게이트웨이</label>
                         <div style="width: 60%">
                             <input name="subnetGateway" type="text"  style="display:inline-block;width:70%;" placeholder="예) 10.0.0.1"/>
                         </div>
                     </div>
                     <div class="w2ui-field">
                         <label style="text-align: left;width:36%;font-size:11px;">DNS</label>
                         <div style="width: 60%">
                             <input name="subnetDns" type="text"  style="display:inline-block;width:70%;" placeholder="예) 8.8.8.8"/>
                         </div>
                     </div>
                 </div>
             </div>
         </div>
    </form>
    <div class="w2ui-buttons" id="GoogleNetworkInfoBtnDiv" hidden="true">
        <button class="btn" style="float: left;" onclick="saveNetworkInfo('before');" >이전</button>
        <button class="btn" style="float: right; padding-right: 15%" onclick="setNetworkValidate('#GoogleNetworkInfoForm');" >다음>></button>
    </div>
</div>

<!-- Azure 네트워크 div -->
<div id="AzureNetworkInfoDiv" style="width:100%;height:100%;" hidden="true">
    <form id="azureNetworkInfoForm">
        <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
            <ul class="progressStep_6" >
                <li class="pass">Azure 정보</li>
                <li class="pass">기본 정보</li>
                <li class="active">네트워크 정보</li>
                <li class="before">리소스 정보</li>
                <li class="before">배포 파일 정보</li>
                <li class="before private">설치</li>
            </ul>
        </div>
        <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
             <div class="panel panel-info"  style="margin-bottom:20px;">
                 <div  class="panel-heading" style="padding:5px 5% 10px 5%;"><b>네트워크 External</b></div>
                 <div class="panel-body">
                     <div class="w2ui-field">
                         <label style="text-align: left;width:36%;font-size:11px;">설치관리자 IPs</label> 
                         <div style="width: 60%">
                             <input name="publicStaticIp" type="text"  style="display:inline-block;width:70%;" placeholder="예) 10.0.0.20"/>
                         </div>
                     </div>
                 </div>
             </div>
             <div class="panel panel-info" style="margin-bottom:20px;">    
                 <div  class="panel-heading" style="padding:5px 5% 10px 5%;"><b>네트워크 Internal</b></div>
                 <div class="panel-body">
                     <div class="w2ui-field" >
                         <label style="text-align: left;width:36%;font-size:11px;">설치관리자 내부망 IPs</label> 
                         <div style="width: 60%">
                             <input name="privateStaticIp" type="text"  style="display:inline-block;width:70%;" placeholder="예) 10.0.0.20"/>
                         </div>
                     </div>
                     <div class="w2ui-field">
                         <label style="text-align: left;width:36%;font-size:11px;">네트워크 명</label>
                         <div style="width: 60%">
                             <input name="networkName" type="text"  style="display:inline-block;width:70%;" placeholder="네트워크 명을 입력하세요."/>
                         </div>
                     </div>
                     <div class="w2ui-field">
                         <label class="subnetId" style="text-align: left;width:36%;font-size:11px;">서브넷 명</label>
                         <div style="width: 60%">
                             <input name="subnetId" type="text"  style="display:inline-block;width:70%;" placeholder="서브넷 명을 입력하세요."/>
                         </div>
                     </div>
                     <div class="w2ui-field">
                         <label style="text-align: left;width:36%;font-size:11px;">서브넷 범위</label>
                         <div style="width: 60%">
                             <input name="subnetRange" type="text"  style="display:inline-block;width:70%;"  placeholder="예) 10.0.0.0/24"/>
                         </div>
                     </div>
                     <div class="w2ui-field">
                         <label style="text-align: left;width:36%;font-size:11px;">게이트웨이</label>
                         <div style="width: 60%">
                             <input name="subnetGateway" type="text"  style="display:inline-block;width:70%;" placeholder="예) 10.0.0.1"/>
                         </div>
                     </div>
                     <div class="w2ui-field">
                         <label style="text-align: left;width:36%;font-size:11px;">DNS</label>
                         <div style="width: 60%">
                             <input name="subnetDns" type="text"  style="display:inline-block;width:70%;" placeholder="예) 8.8.8.8"/>
                         </div>
                     </div>
                 </div>
             </div>
         </div>
    </form>
    <div class="w2ui-buttons" id="AzureNetworkInfoBtnDiv" hidden="true">
        <button class="btn" style="float: left;" onclick="saveNetworkInfo('before');" >이전</button>
        <button class="btn" style="float: right; padding-right: 15%" onclick="setNetworkValidate('#azureNetworkInfoForm');" >다음>></button>
    </div>
</div>


<!-- vSphere 네트워크 div -->
<div id="VsphereNetworkInfoDiv" style="width:100%;height:100%;" hidden="true">
    <form id="vSphereNetworkInfoForm">
        <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
            <ul class="progressStep_6" >
                <li class="pass"></li>
                <li class="pass">기본 정보</li>
                <li class="active">네트워크 정보</li>
                <li class="before">리소스 정보</li>
                <li class="before">배포 파일 정보</li>
                <li class="before private">설치</li>
            </ul>
        </div>
        <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
             <div class="panel panel-info" style="margin-bottom:20px;">
                 <div  class="panel-heading" style="padding:5px 5% 10px 5%;"><b>네트워크 External</b></div>
                 <div class="panel-body">
                     <div class="w2ui-field">
                         <label style="text-align: left;width:36%;font-size:11px;">설치관리자  IPs</label> 
                         <div style="width: 60%">
                             <input name="publicStaticIp" type="text"  style="display:inline-block;width:70%;" placeholder="예) 10.0.0.20"/>
                         </div>
                     </div>
                     <div class="w2ui-field">
                         <label class="publicSubnetId" style="text-align: left;width:36%;font-size:11px;">포트 그룹명</label>
                         <div style="width: 60%">
                             <input name="publicSubnetId" type="text"  style="display:inline-block;width:70%;" placeholder="포트 그룹명을 입력하세요."/>
                         </div>
                     </div>
                     <div class="w2ui-field">
                         <label style="text-align: left;width:36%;font-size:11px;">서브넷 범위</label>
                         <div style="width: 60%">
                             <input name="publicSubnetRange" type="text"  style="display:inline-block;width:70%;" placeholder="예) 10.0.0.0/24"/>
                         </div>
                     </div>
                     <div class="w2ui-field">
                         <label style="text-align: left;width:36%;font-size:11px;">게이트웨이</label>
                         <div style="width: 60%">
                             <input name="publicSubnetGateway" type="text" style="display:inline-block;width:70%;" placeholder="예) 10.0.0.1"/>
                         </div>
                     </div>
                     <div class="w2ui-field">
                         <label style="text-align: left;width:36%;font-size:11px;">DNS</label>
                         <div style="width: 60%">
                             <input name="publicSubnetDns" type="text"  style="display:inline-block;width:70%;" placeholder="예) 8.8.8.8"/>
                         </div>
                     </div>
                 </div>
             </div>
             <!-- Internal -->
             <div class="panel panel-info" style="margin-bottom:20px;">
                 <div  class="panel-heading" style="padding:5px 5% 10px 5%;"><b>네트워크 Internal</b></div>
                 <div class="panel-body">
                     <div class="w2ui-field" >
                         <label style="text-align: left;width:36%;font-size:11px;">설치관리자 내부망 IPs</label> 
                         <div style="width: 60%">
                             <input name="privateStaticIp" type="text"  style="display:inline-block;width:70%;" placeholder="예) 10.0.0.20"/>
                         </div>
                     </div>
                     <div class="w2ui-field">
                         <label class="subnetId" style="text-align: left;width:36%;font-size:11px;">포트그룹 명</label>
                         <div style="width: 60%">
                             <input name="subnetId" type="text" style="display:inline-block;width:70%;" placeholder="포트그룹 명을 입력하세요."/>
                         </div>
                     </div>
                     <div class="w2ui-field">
                         <label style="text-align: left;width:36%;font-size:11px;">서브넷 범위</label>
                         <div style="width: 60%">
                             <input name="subnetRange" type="text"  style="display:inline-block;width:70%;" placeholder="예) 10.0.0.0/24"/>
                         </div>
                     </div>
                     <div class="w2ui-field">
                         <label style="text-align: left;width:36%;font-size:11px;">게이트웨이</label>
                         <div style="width: 60%">
                             <input name="subnetGateway" type="text"  style="display:inline-block;width:70%;" placeholder="예) 10.0.0.1"/>
                         </div>
                     </div>
                     <div class="w2ui-field">
                         <label style="text-align: left;width:36%;font-size:11px;">DNS</label>
                         <div style="width: 60%">
                             <input name="subnetDns" type="text"  style="display:inline-block;width:70%;" placeholder="예) 8.8.8.8"/>
                             <div class="isMessage"></div>
                         </div>
                     </div>
                 </div>
             </div>
         </div>
    </form>
    <div class="w2ui-buttons" id="VsphereNetworkInfoBtnDiv" hidden="true">
        <button class="btn" style="float: left;" onclick="saveNetworkInfo('before');" >이전</button>
        <button class="btn" style="float: right; padding-right: 15%" onclick="setNetworkValidate('#vSphereNetworkInfoForm');" >다음>></button>
    </div>
</div>

<!-- 리소스 사용량 -->
<div id="ResourceInfoDiv" style="width:100%;height:100%;" hidden="true">
    <form id="resourceInfoForm">
        <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
            <ul class="progressStep_6" >
                <li class="pass"></li>
                <li class="pass">기본 정보</li>
                <li class="pass">네트워크 정보</li>
                <li class="active">리소스 정보</li>
                <li class="before">배포 파일 정보</li>
                <li class="before private">설치</li>
            </ul>
        </div>
        <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
            <div class="panel panel-info">    
                <div class="panel-heading"><b>리소스 정보</b></div>
                <div class="panel-body" style="padding:5px 5% 10px 5%;">
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">스템셀</label>
                        <div style="width: 60%">
                            <div>
                               <select name="stemcell" id="stemcell" style="width:70%;">
                                   <option value="">스템셀을 선택하세요.</option>
                               </select>
<!--                                 <input type="list" name="stemcell" style="display:inline-block; width:70%;margin-top:1.5px;" placeholder="스템셀을 선택하세요."/> -->
                            </div>
                        </div>
                    </div>
                    <div class="w2ui-field cloudInstanceTypeDiv">
                        <label style="text-align: left;width:36%;font-size:11px;">인스턴스 유형</label>
                        <div style="width: 60%">
                            <input name="cloudInstanceType" type="text"  style="display:inline-block;width:70%;" placeholder="인스턴스 유형을 입력하세요."/>
                        </div>
                    </div>
                    
                    <div class="w2ui-field vsphereResourceDiv">
                        <label style="text-align: left;width:36%;font-size:11px;">리소스 풀 CPU</label>
                        <div style="width: 60%">
                            <input name="resourcePoolCpu" type="text"  style="display:inline-block;width:70%;" placeholder="리소스 풀 CPU 예) 2" onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  />
                        </div>
                    </div>
                    <div class="w2ui-field vsphereResourceDiv">
                        <label style="text-align: left;width:36%;font-size:11px;">리소스 풀 RAM</label>
                        <div style="width: 60%">
                            <input name="resourcePoolRam" type="text"  style="display:inline-block;width:70%;" placeholder="리소스 풀 RAM 예) 4096" onkeydown="return onlyNumber(event);" onkeyup='removeChar(event)' style='ime-mode:disabled;'  />
                        </div>
                    </div>
                    <div class="w2ui-field vsphereResourceDiv">
                        <label style="text-align: left;width:36%;font-size:11px;">리소스 풀 DISK</label>
                        <div style="width: 60%">
                            <input name="resourcePoolDisk" type="text"  style="display:inline-block;width:70%;" placeholder="리소스 풀 DISK 예) 20000" onkeydown="return onlyNumber(event);"  onkeyup='removeChar(event)' style='ime-mode:disabled;'  />
                        </div>
                    </div>
                    
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">VM 비밀번호</label>
                        <div style="width: 60%">
                            <input name="boshPassword" type="text"  style="display:inline-block;width:70%;" placeholder="VM 비밀번호를 입력하세요."/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form>
    <div class="w2ui-buttons" id="ResourceInfoBtnDiv" hidden="true">
        <button class="btn" style="float: left;" onclick="saveResourceInfo('before');" >이전</button>
        <button class="btn" style="float: right; padding-right: 15%" onclick="$('#resourceInfoForm').submit();" >다음>></button>
    </div>
</div>

<!-- Deploy DIV -->
<div id="DeployDiv" style="width:100%;height:100%;" hidden="true">
    <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
        <ul class="progressStep_6" >
            <li class="pass"></li>
            <li class="pass">기본 정보</li>
            <li class="pass">네트워크 정보</li>
            <li class="pass">리소스 정보</li>
            <li class="active">배포 파일 정보</li>
            <li class="before private">설치</li>
        </ul>
    </div>
    <div style="width:93%;height:84%;float: left;display: inline-block;margin:10px 0 0 1%;">
        <textarea id="deployInfo" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:3%;" readonly="readonly"></textarea>
    </div>
    <div class="w2ui-buttons" id="DeployBtnDiv" hidden="true">
        <button class="btn" style="float: left;" onclick="deployment('before');">이전</button>
        <button class="btn" style="float: right; padding-right: 15%" onclick="deployment('after', 'DeployDiv');">다음>></button>
    </div>
</div>

<!-- Install DIV -->
<div id="privateInstallDiv" style="width:100%;height:100%;" hidden="true">
    <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
        <ul class="progressStep_7" >
            <li class="active">Private 설치</li>
            <li class="before">Public 설치</li>
        </ul>
    </div>
    <div style="width:93%;height:84%;float: left;display: inline-block;margin:10px 0 0 1%;">
        <textarea id="installLogs" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:3%;" readonly="readonly"></textarea>
    </div>
    <div class="w2ui-buttons" id="InstallDivButtons" hidden="true">
            <!-- 설치 실패 시 -->
            <button class="btn" id="deployPopupBtn" style="float: left;" onclick="deployPopup();" disabled>이전</button>
            <button class="btn" style="float: right; padding-right: 15%" onclick="popupComplete();">닫기</button>
    </div>
</div>

<!-- Install DIV -->
<div id="publicInstallDiv" style="width:100%;height:100%;" hidden="true">
    <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
        <ul class="progressStep_7" >
            <li class="before">Private 설치</li>
            <li class="active">Public 설치</li>
        </ul>
    </div>
    <div style="width:93%;height:84%;float: left;display: inline-block;margin:10px 0 0 1%;">
        <textarea id="installLogs" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:3%;" readonly="readonly"></textarea>
    </div>
    <div class="w2ui-buttons" id="InstallDivButtons" hidden="true">
            <!-- 설치 실패 시 -->
            <button class="btn" id="deployPopupBtn" style="float: left;" onclick="deployPopup();" disabled>이전</button>
            <button class="btn" style="float: right; padding-right: 15%" onclick="popupComplete();">닫기</button>
    </div>
</div>


<div id="publicBootSelectBody" style="width:100%; height: 80px;" hidden="true">
    <div class="w2ui-lefted" style="text-align: center;">
         수정 할 Public 환경의 BOOTSTRAP가 존재 하지 않습니다.<br>
        BOOTSTRAP를 설치할 <b>Public 클라우드 환경</b>을 선택하세요<br />
        <br />
    </div>
    <div id="selectPublicIaas" style="width:40%;margin: 0 auto;">
        <select class="form-control" name="selectPublicIaas" onchange="selectPublicIaas(this.value);">
            <option value="AWS">AWS</option>
            <option value="Google">Google</option>
            <option value="Azure">Azure</option>
        </select>
    </div>
</div>

<script type="text/javascript" src="<c:url value='/js/rules/bootstrap/bootstrap_default.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/rules/bootstrap/bootstrap_network.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/rules/bootstrap/bootstrap_resource.js'/>"></script>
<script>
$(function() {
    $.validator.addMethod( "ipv4", function( value, element, params ) {
        return /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(params);
    }, text_ip_msg );
    
    $.validator.addMethod( "ipv4Range", function( value, element, params ) {
        return /^((\b|\.)(0|1|2(?!5(?=6|7|8|9)|6|7|8|9))?\d{1,2}){4}(-((\b|\.)(0|1|2(?!5(?=6|7|8|9)|6|7|8|9))?\d{1,2}){4}|\/((0|1|2|3(?=1|2))\d|\d))\b$/.test(params);
    }, text_ip_msg );
});

</script>
