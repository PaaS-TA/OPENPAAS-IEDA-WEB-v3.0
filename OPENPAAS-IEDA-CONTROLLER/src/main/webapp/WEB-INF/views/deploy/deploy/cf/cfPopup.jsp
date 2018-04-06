<%
/* =================================================================
 * 상세설명 : CF 설치
 * =================================================================
 * 수정일    작성자             내용     
 * -----------------------------------------------------------------
 * 2016.10  지향은    화면 수정 및 vSphere 클라우드 기능 추가
 * 2016.12  지향은    CF 목록과 팝업 화면 .jsp 분리 및 설치 버그 수정 
 * 2017.09  지향은    CF for Google
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix = "spring" uri = "http://www.springframework.org/tags" %>
<style>
.popover{
    max-width:280px
}
.popover-content {
    width:275px;
     max-height: 300px;
     overflow-y: auto;
}
</style>
<script type="text/javascript">
var search_data_fail_msg = '<spring:message code="common.data.select.fail"/>'//목록을 가져오는데 실패하였습니다.
var save_lock_msg='<spring:message code="common.save.data.lock"/>';//등록 중 입니다.
var save_info_msg = '<spring:message code="common.save.data.info.lock"/>';//저장 중 입니다.
var text_required_msg='<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
var select_required_msg='<spring:message code="common.select.vaildate.required.message"/>';//을(를) 선택하세요.
var text_ip_msg = '<spring:message code="common.text.validate.ip.message"/>';//IP을(를) 확인 하세요.
var deploy_type = '<spring:message code="common.deploy.type.cf.name"/>';//CF DEPLOY TYPE VALUE
var country_parent_code = '<spring:message code="common.code.country.code.parent"/>';//ieda_common_code country 조회

    
//setting variable
var cfId = "";
var networkId = "";
var defaultInfo = "";
var networkInfo = [];
var keyInfo="";
var publicStaticIp = "";
var internalCnt=1;
var jobsInfo=[];
var resourceInfo = "";
var releases = "";
var stemcells = "";
var deploymentFile = "";
var installStatus ="";
var countryCodes = null;
var keyFile ="";
var config = "";

$(function() {
    $(document).delegate(".w2ui-popup","click",function(e){
     $('[data-toggle="popover"]').each(function () {
            if (!$(this).is(e.target) && $(this).has(e.target).length === 0 && $('.popover').has(e.target).length === 0) {
                $(this).popover('hide');
            }
        });
    });
    
});

/********************************************************
 * 설명 : cf 고급 설정 레이아웃 추가
 *********************************************************/
// var pstyle = 'width:615px;height:595;';
// var config = {
//       layout2: {
//           name: 'layout2',
//           padding: 4,
//           panels: [
//               { type: 'left', size: 200, hidden: true, content: 'left' },
//               {type: 'main', style:pstyle }
//           ]
//       }
//  };
 
/********************************************************
 * 설명 : cf 고급 설정 레이아웃 초기화
 *********************************************************/
$(function () {
    // initialization in memory
    setW2layout()
});
 
function setW2layout(){
    var pstyle = 'width:615px;height:595;';
    config = {
          layout2: {
              name: 'layout2',
              padding: 4,
              panels: [
                  { type: 'left', size: 200, hidden: true, content: 'left' },
                  {type: 'main', style:pstyle }
              ]
          }
     };
    $().w2layout(config.layout2);
}

/********************************************************
 * 설명 : CF 릴리즈 설치 지원 버전 목록 조회
 * 기능 : getReleaseVersionList
 *********************************************************/
function getReleaseVersionList(){
     var contents = "";
     $.ajax({
        type :"GET",
        url :"/common/deploy/list/releaseInfo/cf/"+iaas, 
        contentType :"application/json",
        success :function(data, status) {
            if ( !checkEmpty(data) ) {
                contents = "<table id='popoverTable'><tr><th>릴리즈 유형</th><th>릴리즈 버전</th></tr>";
                data.map(function(obj) {
                    contents += "<tr><td>" + obj.releaseType+ "</td><td>" +  obj.minReleaseVersion +"</td></tr>";
                });
                contents += "</table>";
                $('.cf-info').attr('data-content', contents);
            }
        },
        error :function(request, status, error) {
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "CF 릴리즈 설치 지원 버전");
        }
    });
}

/********************************************************
 * 설명 : CF 조회 시 데이터 조회
 * 기능 : getCfData
 *********************************************************/
function getCfData(record) {
    $.ajax({
        type : "GET",
        url : "/deploy/"+menu+"/install/detail/" + record.id,
        contentType : "application/json",
        success : function(data, status) {
            if (data != null && data != "") {
                iaas = data.content.iaasType.toUpperCase();
                diegoUse = record.diegoYn;
                setCfData(data.content);
                defaultInfoPopup();
            }
        },
        error : function(request, status, error) {
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "CF 수정");
        }
    });
}

/********************************************************
 * 설명 :  CF Data Setting
 * 기능 : setCfData
 *********************************************************/
function setCfData(contents) {
    if( menu == "cfDiego" ) {
        contents = contents.cfVo;
    }
    cfId = contents.id;
    iaas = contents.iaasType;
    diegoUse = contents.diegoYn;
    
     //External을 제외한 Internal 수
    if( contents.networks.length > 1){
        internalCnt = contents.networks.length-1;
    }
    //기본정보
    defaultInfo = {
        iaas                   : iaas,
        deploymentName         : contents.deploymentName,
        directorUuid           : contents.directorUuid,
        releaseName            : contents.releaseName,
        releaseVersion         : contents.releaseVersion,
        appSshFingerprint      : contents.appSshFingerprint,
        deaMemoryMB            : contents.deaMemoryMB,
        deaDiskMB              : contents.deaDiskMB,
        domain                 : contents.domain,
        description            : contents.description,
        domainOrganization     : contents.domainOrganization,        
        loginSecret            : contents.loginSecret,
        paastaMonitoringUse    : contents.paastaMonitoringUse,
        ingestorIp             : contents.ingestorIp,
    }
    //네트워크 정보 
    for(var i=0; i<contents.networks.length; i++){
         var arr = {
            id                      : contents.id,
            deployType               : contents.networks[i].deployType,
            seq                      : i,
            net                      : contents.networks[i].net,
            publicStaticIp           : contents.networks[i].publicStaticIp,
            subnetRange              : contents.networks[i].subnetRange,
            subnetGateway            : contents.networks[i].subnetGateway,
            subnetDns                : contents.networks[i].subnetDns,
            subnetReservedFrom       : contents.networks[i].subnetReservedFrom,
            subnetReservedTo         : contents.networks[i].subnetReservedTo,
            subnetStaticFrom         : contents.networks[i].subnetStaticFrom,
            subnetStaticTo           : contents.networks[i].subnetStaticTo,
            subnetId                 : contents.networks[i].subnetId,
            networkName              : contents.networks[i].networkName,
            cloudSecurityGroups      : contents.networks[i].cloudSecurityGroups,
            availabilityZone         : contents.networks[i].availabilityZone
        }
         networkInfo.push(arr);
    }
    
    keyFile = contents.keyFile;
    keyInfo = {
            countryCode       : contents.countryCode,
            stateName         : contents.stateName,
            localityName      : contents.localityName,
            organizationName  : contents.organizationName,
            unitName          : contents.unitName,
            email             : contents.email,
            status            : ""
    }
    
    for(var i=0; i<contents.jobs.length; i++){
        var job = {
                id           : contents.jobs[i].id,
                seq          : contents.jobs[i].seq,
                deploy_type  : contents.jobs[i].deploy_type,
                job_name     : contents.jobs[i].job_name,
                job_id       : contents.jobs[i].job_id,
                instances    : contents.jobs[i].instances,
                zone         : contents.jobs[i].zone
        }
        jobsInfo.push(job);
    }
    //resource
    if(contents.resource != null && contents.resource != undefined && contents.resource != ""){
        resourceInfo = {
                id               : contents.id,
                stemcellName     : contents.resource.stemcellName,
                stemcellVersion  : contents.resource.stemcellVersion,
                boshPassword     : contents.resource.boshPassword,
                smallFlavor      : contents.resource.smallFlavor,
                mediumFlavor     : contents.resource.mediumFlavor,
                largeFlavor      : contents.resource.largeFlavor,
                runnerFlavor     : contents.resource.runnerFlavor,
                smallRam         : contents.resource.smallRam,
                smallDisk        : contents.resource.smallDisk,
                smallCpu         : contents.resource.smallCpu,
                mediumRam        : contents.resource.mediumRam,
                mediumDisk       : contents.resource.mediumDisk,
                mediumCpu        : contents.resource.mediumCpu,
                largeRam         : contents.resource.largeRam,
                largeDisk        : contents.resource.largeDisk,
                largeCpu         : contents.resource.largeCpu,
                runnerRam        : contents.resource.runnerRam,
                runnerDisk       : contents.resource.runnerDisk,
                runnerCpu        : contents.resource.runnerCpu
        }
    }
}

/********************************************************
 * 설명 : 기본 정보 팝업
 * 기능 : defaultInfoPopup
 *********************************************************/
function defaultInfoPopup() {
    settingDiegoUse(diegoUse, $("#defaultInfoDiv ul"));
    w2popup.open({
        title : "<b>CF 설치</b>",
        width : 750,
        height :825,
        modal : true,
        body    : $("#defaultInfoDiv").html(),
        buttons : $("#DefaultInfoButtonDiv").html(),
        onOpen : function(event) {
            event.onComplete = function() {
                //릴리즈 정보 popup over
                 $('[data-toggle="popover"]').popover();
                 $(".paastaMonitoring-info").attr('data-content', "paasta-controller v3.0 이상에서 지원")
                
                 getReleaseVersionList();
                 //cf & diego 통합 설치일 경우 fingerprint readonly
                 if( menu.toLowerCase() =="cfdiego" ){
                     $(".w2ui-msg-body input[name='appSshFingerprint']").attr("readonly", true);
                 }

                 if ( !checkEmpty(defaultInfo )) {
                    //설치관리자 UUID
                    $(".w2ui-msg-body input[name='deploymentName']").val(defaultInfo.deploymentName);
                    $(".w2ui-msg-body input[name='directorUuid']").val(defaultInfo.directorUuid);
                    $(".w2ui-msg-body input[name='appSshFingerprint']").val(defaultInfo.appSshFingerprint);
                    $(".w2ui-msg-body input[name='domainOrganization']").val(defaultInfo.domainOrganization);
                    $(".w2ui-msg-body input[name='deaMemoryMB']").val(defaultInfo.deaMemoryMB);
                    $(".w2ui-msg-body input[name='deaDiskMB']").val(defaultInfo.deaDiskMB);
                    
                    //CF 정보
                    $(".w2ui-msg-body input[name='domain']").val(defaultInfo.domain);
                    $(".w2ui-msg-body input[name='description']").val(defaultInfo.description);
                    $(".w2ui-msg-body input[name='loginSecret']").val(defaultInfo.loginSecret);
                    
                    if( !checkEmpty(defaultInfo.ingestorIp) ){//PaaS-TA 모니터링 체크 
                        $(".w2ui-msg-body input:checkbox[name='paastaMonitoring']").attr("checked", true);
                        checkPaasTAMonitoringUseYn();
                        $(".w2ui-msg-body input[name='ingestorIp']").val(defaultInfo.ingestorIp);
                    }
                } else{
                    if( !checkEmpty($("#directorUuid").text()) ){
                        $(".w2ui-msg-body input[name='directorUuid']").val($("#directorUuid").text());
                    }
                }
                w2popup.lock("릴리즈를 조회 중입니다.", true);
                getCfRelease();
            }
        }, onClose : function(event) {
            event.onComplete = function() {
                initSetting();
            }
        }
    });
}

/********************************************************
 * 설명 : CF 릴리즈 조회
 * 기능 : getCfRelease
 *********************************************************/
function getCfRelease() {
    $.ajax({
        type : "GET",
        url : "/common/deploy/release/list/cf",
        contentType : "application/json",
        success : function(data, status) {
            releases = new Array();
            if( data.records != null){
                w2popup.unlock();
                var option = "<option value=''>CF 릴리즈를 선택하세요.</option>";
                data.records.map(function(obj) {
                    releases.push(obj.version);
                    if( defaultInfo.releaseName == obj.name && defaultInfo.releaseVersion == obj.version){
                        option += "<option value='"+obj.name+"/"+obj.version+"' selected>"+obj.name+"/"+obj.version+"</option>";
                    }else{
                        option += "<option value='"+obj.name+"/"+obj.version+"'>"+obj.name+"/"+obj.version+"</option>";    
                    }
                });
            }
            $(".w2ui-msg-body select[name='releases']").html(option);
            setInputDisplay(defaultInfo.releaseName+"/"+defaultInfo.releaseVersion);
            setDisabledMonitoring(defaultInfo.releaseName+"/"+defaultInfo.releaseVersion);
        },
        error : function(e, status) {
            w2popup.unlock();
            w2alert("Cf Release List 를 가져오는데 실패하였습니다.", "CF 설치");
        }
    });
}

/********************************************************
 * 설명 : Loggregator 릴리즈 조회
 * 기능 : getLoggregatorRelease
 *********************************************************/
function getLoggregatorRelease(){
    $.ajax({
        type : "GET",
        url : "/common/deploy/release/list/loggregator",
        contentType : "application/json",
        success : function(data, status) {
            w2popup.unlock();
            var option = "";
            if( data.records.length > 0){
                data.records.map(function(obj) {
                    option += "<option value='"+obj.name+"/"+obj.version+"'>"+obj.name+"/"+obj.version+"</option>";
                });
            }else{
                option ="<option value=''>loggregator 릴리즈가 필요합니다.</option>"
            }
            $(".w2ui-msg-body select[name='loggregatorReleases']").html(option);
        },
        error : function(e, status) {
            w2popup.unlock();
            w2alert("Cf Release List 를 가져오는데 실패하였습니다.", "CF 설치");
        }
    });
}


/********************************************************
 * 설명 : paasta-controller v2.0 이상에서 지원 
 * 기능 : setDisabledMonitoring
 *********************************************************/
function setDisabledMonitoring(val){
    if( !checkEmpty(val) || val != "undefined/undefined"){
        var cfReleaseName = val.split("/")[0];
        var cfReleaseVersion = val.split("/")[1];
        //paasta-controller v2.0.0 이상 PaaS-TA 모니터링 지원 checkbox
        if( cfReleaseName.indexOf("paasta-controller") > -1 && ( compare(cfReleaseVersion, "2.0") > -1 || compare(cfReleaseVersion, "3.0") > -1 || compare(cfReleaseVersion, "3.1") > -1) ){
            $('.w2ui-msg-body #paastaMonitoring').attr('disabled',false);
        }else{
            if( $(".w2ui-msg-body input:checkbox[name='paastaMonitoring']").is(":checked")){
                $(".w2ui-msg-body input:checkbox[name='paastaMonitoring']").prop('checked',false);
                checkPaasTAMonitoringUseYn();
            }
            $('.w2ui-msg-body #paastaMonitoring').attr('disabled',true);
        }
    }
    
}

/********************************************************
 * 설명 : 272 이상일 경우 화면 설정
 * 기능 : setInputDisplay
 *********************************************************/
function setInputDisplay(val){
    var name = val.split("/")[0];
    var version = val.split("/")[1];
    if( Number(version) >= 272 || (name.indexOf("paasta-controller") > -1 && compare(version, "3.0") > -1 ) || (name.indexOf("paasta-controller") > -1 && compare(version, "3.1") > -1 )){
        //핑거프린트 자동 입력
        $(".w2ui-msg-body #fingerprint").css("display", "none");
        $(".w2ui-msg-body #deaDiskmbDiv").css("display", "none");
        $(".w2ui-msg-body #deaMemorymbDiv").css("display", "none");
        $(".w2ui-msg-body #loggregator").css("display", "block");
        if( Number(version) == "3.1" || Number(version) == "287" ){
        	$(".w2ui-msg-body #loggregator").css("display", "none");
        	$(".w2ui-msg-body #loggregator").val("");
        }
        $(".w2ui-msg-body input[name='appSshFingerprint']").val("");
        $(".w2ui-msg-body input[name='deaMemoryMB']").val("");
        $(".w2ui-msg-body input[name='deaDiskMB']").val("");
    } 
    else{
        if( diegoUse == "true" ){
            $(".w2ui-msg-body #fingerprint").css("display", "block");
        }
        $(".w2ui-msg-body #loggregator").css("display", "none");
        $(".w2ui-msg-body #deaDiskmbDiv").css("display", "block");
        $(".w2ui-msg-body #deaMemorymbDiv").css("display", "block");
    }
    getLoggregatorRelease();
    
}

/********************************************************
 * 설명 : PaaS-TA 모니터링 사용 체크 검사
 * 기능 : checkPaasTAMonitoringUseYn
 *********************************************************/
function checkPaasTAMonitoringUseYn(value){
    var cnt = $("input[name=paastaMonitoring]:checkbox:checked").length;
    if(cnt > 0 ){
        $(".w2ui-msg-body input[name='ingestorIp']").attr("disabled", false);
    }else{
        $(".w2ui-msg-body input[name='ingestorIp']").css({"border-color" : "rgb(187, 187, 187)"}).parent().find(".isMessage").text("");
        //값 초기화
        $(".w2ui-msg-body input[name='ingestorIp']").val("");
        //Read-only
        $(".w2ui-msg-body input[name='ingestorIp']").attr("disabled", true);
    }
     
}

/********************************************************
 * 설명 : 기본정보 등록
 * 기능 : saveDefaultInfo
 *********************************************************/
function saveDefaultInfo() {
    var release = $(".w2ui-msg-body select[name='releases']").val();
    var loggregatorRelease = $(".w2ui-msg-body select[name='loggregatorReleases']").val();
    defaultInfo = {
                id                   : (cfId) ? cfId : "",
                iaas                 : iaas.toUpperCase(),
                diegoYn              : diegoUse,
                platform             : "cf",
                deploymentName       : $(".w2ui-msg-body input[name='deploymentName']").val(),
                directorUuid         : $(".w2ui-msg-body input[name='directorUuid']").val(),
                releaseName          : release.split("/")[0],
                releaseVersion       : release.split("/")[1],
                loggregatorReleaseName    : loggregatorRelease.split("/")[0],
                loggregatorReleaseVersion : loggregatorRelease.split("/")[1],
                appSshFingerprint    : $(".w2ui-msg-body input[name='appSshFingerprint']").val(),
                deaMemoryMB          : $(".w2ui-msg-body input[name='deaMemoryMB']").val(),
                deaDiskMB            : $(".w2ui-msg-body input[name='deaDiskMB']").val(),
                domain               : $(".w2ui-msg-body input[name='domain']").val(),
                description          : $(".w2ui-msg-body input[name='description']").val(),
                domainOrganization   : $(".w2ui-msg-body input[name='domainOrganization']").val(),
                loginSecret          : $(".w2ui-msg-body input[name='loginSecret']").val(),
                paastaMonitoringUse  : $(".w2ui-msg-body input:checkbox[name='paastaMonitoring']").is(":checked") == true ? "true" : "false",
                ingestorIp           : $(".w2ui-msg-body input[name='ingestorIp']").val(),
    }
    $.ajax({
        type : "PUT",
        url : "/deploy/"+menu+"/install/saveDefaultInfo",
        contentType : "application/json",
        data : JSON.stringify(defaultInfo),
        success : function(data, status) {
            w2popup.unlock();
            if( menu == 'cfDiego' ){
                cfId = data.content.cfVo.id;
            }else{
                cfId = data.content.id;
            }
            settingIaasPopup("network");
        },
        error: function(request, status, error) {
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "CF 설치");
        }
    });
 }
 
/********************************************************
 * 설명 : 네트워크 정보 팝업(openstack/aws/google)
 * 기능 : defaultNetworkPopup
 *********************************************************/
function defaultNetworkPopup(div, height){
    settingDiegoUse(diegoUse, $(div+" ul"));
    w2popup.open({
        title   : "<b>CF 설치</b>",
        width   : 750,
        height  : height,
        body    : $(div+"Div").html(),
        buttons : $(div+"Buttons").html(),
        modal   : true,
        showMax : false,
        onOpen  : function(event) {
            event.onComplete = function() {
                if ( networkInfo.length > 0 ) {
                    networkId = networkInfo[0].id;
                    setNetworkInfo(networkInfo);
                }
                if( iaas.toUpperCase() == "AWS" ){
                    $(".w2ui-msg-body #availabilityZoneDiv").show();
                    $(".w2ui-msg-body #availabilityZoneDiv").css("display", "block");
                }
            }
        },
        onClose : function(event) {
            event.onComplete = function() {
                initSetting();
            }
        }
    });
}
 
 /********************************************************
  * 설명 : 인프라 환경에 따른 네트워크 정보 설정
  * 기능 : setNetworkInfo
  *********************************************************/
 function setNetworkInfo(networkInfo){
     for(var i=0; i <networkInfo.length; i++){
         if( (networkInfo[i].net).toLowerCase() == "external" ){
             if(  iaas.toLowerCase() == "vsphere" ){
                 $(".w2ui-msg-body input[name='publicSubnetId']").val(networkInfo[i].subnetId);
                 $(".w2ui-msg-body input[name='publicSubnetRange']").val(networkInfo[i].subnetRange); 
                 $(".w2ui-msg-body input[name='publicSubnetGateway']").val(networkInfo[i].subnetGateway);
                 $(".w2ui-msg-body input[name='publicSubnetDns']").val(networkInfo[i].subnetDns);
                 $(".w2ui-msg-body input[name='publicStaticIp']").val(networkInfo[i].subnetStaticFrom);
                 $(".w2ui-msg-body input[name='publicStaticFrom']").val(networkInfo[i].subnetStaticFrom);
                 $(".w2ui-msg-body input[name='publicStaticTo']").val(networkInfo[i].subnetStaticTo);
             }else{
                 $(".w2ui-msg-body input[name='publicStaticIp']").val(networkInfo[i].subnetStaticFrom); 
             }
         }else{
             if(networkInfo[i].seq  > 1)  settingNetwork(networkInfo[i].seq );
             var seq = networkInfo[i].seq;
             //기본 네트워크 정보
             $(".w2ui-msg-body input[name='subnetRange_"+seq+"']").val(networkInfo[i].subnetRange); 
             $(".w2ui-msg-body input[name='subnetGateway_"+seq+"']").val(networkInfo[i].subnetGateway);
             $(".w2ui-msg-body input[name='subnetDns_"+seq+"']").val(networkInfo[i].subnetDns);
             $(".w2ui-msg-body input[name='subnetReservedFrom_"+seq+"']").val(networkInfo[i].subnetReservedFrom);
             $(".w2ui-msg-body input[name='subnetReservedTo_"+seq+"']").val(networkInfo[i].subnetReservedTo);
             $(".w2ui-msg-body input[name='subnetStaticFrom_"+seq+"']").val(networkInfo[i].subnetStaticFrom);
             $(".w2ui-msg-body input[name='subnetStaticTo_"+seq+"']").val(networkInfo[i].subnetStaticTo);
             $(".w2ui-msg-body input[name='networkName_"+seq+"']").val(networkInfo[i].networkName);
             $(".w2ui-msg-body input[name='subnetId_"+seq+"']").val(networkInfo[i].subnetId);
             $(".w2ui-msg-body input[name='cloudSecurityGroups_"+seq+"']").val(networkInfo[i].cloudSecurityGroups);
             $(".w2ui-msg-body input[name='availabilityZone_"+seq+"']").val(networkInfo[i].availabilityZone);
         }
     }
 }

 /********************************************************
  * 설명 : 네트워크 화면 설정
  * 기능 : settingNetwork
  *********************************************************/
  function settingNetwork( index ){
      if( iaas.toLowerCase() == 'aws' || iaas.toLowerCase() == "openstack" ){
          addInternalNetworkInputs('#defaultNetworkInfoDiv_1', "#defaultNetworkInfoForm" );
      }else if( iaas.toLowerCase() == 'google' ){
          addInternalNetworkInputs('#googleNetworkInfoDiv_1', "#googleNetworkInfoForm" );
      }else{
          addInternalNetworkInputs('#vSphereNetworkInfoDiv_1', "#vSphereNetworkInfoForm" );
      }
  }
 
/********************************************************
 * 설명 : 네트워크 입력 추가
 * 기능 : addInternalNetworkInputs
 *********************************************************/
 function addInternalNetworkInputs(preDiv, form){
    w2popup.lock("Internal 네트워크 추가 중", true);
    var index = Number(preDiv.split("_")[1])+1;
    var div= preDiv.split("_")[0] + "_"+ index;
    var body_div= "<div class='panel-body'>";
    var field_div_label="<div class='w2ui-field'>"+"<label style='text-align: left; width: 36%; font-size: 11px;'>";
    var text_style="type='text' style='display:inline-blcok; width:70%;'";
    
    var html= "<div class='panel panel-info' style='margin-top:2%;'>";
        html+= "<div  class='panel-heading' style='position:relative;'>";
        html+=    "<b>Internal 네트워크</b>";
        html+=    "<div style='position: absolute;right: 10px; top: 2px;'>";
        html+=        '<span class="btn btn-info btn-sm" onclick="delInternalNetwork(\''+preDiv+'\', '+index+');">삭제</span>';
        html+=    "</div>";
        html+= "</div>";
        html+= body_div;
        if( iaas.toLowerCase() == "google" ){
            html+= field_div_label + "네트워크 명" + "</label>";
            html+= "<div style=' width: 60%;'>"+"<input name='networkName_"+index+"'" + text_style +" placeholder='네트워크 명을 입력하세요.'/>"+"</div></div>";
            
            html+= field_div_label + "서브넷 명" + "</label>"; 
            html+= "<div style=' width: 60%;'>"+"<input name='subnetId_"+index+"'" + text_style +" placeholder='서브넷 명을 입력하세요.'/>"+"</div></div>";
            
            html+= field_div_label + "방화벽 규칙" + "</label>"; 
            html+= "<div style=' width: 60%;'>"+"<input name='cloudSecurityGroups_"+index+"'" + text_style +" placeholder='예) internet, cf-security'/>"+"</div></div>";
            
            html+= field_div_label + "영역" + "</label>"; 
            html+= "<div style=' width: 60%;'>"+"<input name='availabilityZone_"+index+"'" + text_style +" placeholder='예) asia-northeast1-a'/>"+"</div></div>";
            
        }else if(iaas.toLowerCase() == "vsphere"){
            html+= field_div_label + "포트 그룹명" + "</label>"; 
            html+= "<div style=' width: 60%;'>"+"<input name='subnetId_"+index+"'" + text_style +" placeholder='포트 그룹명을 입력하세요.'/>"+"</div></div>";
        }else{
            html+= field_div_label + "서브넷 아이디" + "</label>"; 
            html+="<div style=' width: 60%;'>"+"<input name='subnetId_"+index+"'" + text_style +" placeholder='서브넷 아이디를 입력하세요.'/>"+"</div></div>";
            
            html+= field_div_label + "보안 그룹" + "</label>"; 
            html+= "<div style=' width: 60%;'>"+"<input name='cloudSecurityGroups_"+index+"'" + text_style +" placeholder='예) bosh-security, cf-security'/>"+"</div></div>";
            
            
            if( iaas.toLowerCase() == "aws" ){
                html+= field_div_label + "가용 영역" + "</label>"; 
                html+= "<div style=' width: 60%;'>"+"<input name='availabilityZone_"+index+"'" + text_style +" placeholder='예) us-west-2'/>"+"</div></div>";
            }
        }
        html+= field_div_label + "서브넷 범위" + "</label>"; 
        html+= "<div style=' width: 60%;'>"+"<input name='subnetRange_"+index+"'" + text_style +" placeholder='예) 10.0.0.0/24'/>" + "</div></div>";
        
        html+= field_div_label + "게이트웨이" + "</label>"; 
        html+= "<div style=' width: 60%;'>"+ "<input name='subnetGateway_"+index+"'" + text_style +" placeholder='예) 10.0.0.1'/>" + "</div></div>";
        
        html+= field_div_label + "DNS" + "</label>"; 
        html+= "<div style=' width: 60%;'>"+ "<input name='subnetDns_"+index+"'" + text_style +" placeholder='예) 8.8.8.8'/>" + "</div></div>";
       
        html+= field_div_label + "IP할당 제외 대역" + "</label>"; 
        html+=     "<div style=' width: 60%;'>";
        html+=         "<input name='subnetReservedFrom_"+index+"' type='text' style='display:inline-block; width:32%;' placeholder='예) 10.0.0.10' />";
        html+=         "<span style='width: 4%; text-align: center;'>&nbsp;&ndash; &nbsp;</span>";
        html+=         "<input name='subnetReservedTo_"+index+"' type='text' style='display:inline-block; width:32%;' placeholder='예) 10.0.0.20' />";
        html+=     "</div></div>";
        
        html+= field_div_label + "IP할당 대역(최소 20개)" + "</label>"; 
        html+=     "<div style=' width: 60%;'>"+"<input name='subnetStaticFrom_"+index+"' type='text' style='display:inline-block; width:32%;' placeholder='예) 10.0.0.10' />";
        html+=         "<span style='width: 4%; text-align: center;'>&nbsp;&ndash; &nbsp;</span>";
        html+=         "<input name='subnetStaticTo_"+index+"' type='text' style='display:inline-block; width:32%;' placeholder='예) 10.0.0.20'/>";
        html+=     "</div>";
        html+= "</div></div></div>";
        
        $(".w2ui-msg-body "+ div).show();
        $(".w2ui-msg-body "+preDiv + " .addInternal").hide();
        $(form + " "+ div).html(html);
        createInternalNetworkValidate(index);
        
}

 /********************************************************
  * 설명 : 네트워크 유효성 추가
  * 기능 : createInternalNetworkValidate
  *********************************************************/
function createInternalNetworkValidate(index){
    var subnet_message="서브넷 아이디";
    var zone_message = "가용 영역";
    if( iaas.toLowerCase() == "google"){
        $("[name*='networkName_"+index+"']").rules("add", {
            required: function(){
                return checkEmpty($(".w2ui-msg-body input[name='networkName_"+index+"']").val());
            }, messages: {required: "네트워크 명 "+text_required_msg}
        });
        subnet_message = "서브넷 명";
        zone_message = "zone";
    }else if(  iaas.toLowerCase() == "vsphere"){ 
        subnet_message="포트 그룹명";
    }
    $("[name*='subnetId_"+index+"']").rules("add", {
        required: function(){
            return checkEmpty($(".w2ui-msg-body input[name='subnetId_"+index+"']").val());
        }, messages: {required: subnet_message+text_required_msg}
    });
    $("[name*='subnetRange_"+index+"']").rules("add", {
        required: function(){
            return checkEmpty($(".w2ui-msg-body input[name='subnetRange_"+index+"']").val());
        },ipv4Range : function(){
            return $(".w2ui-msg-body input[name='subnetRange_"+index+"']").val();  
        }, messages: {required: "서브넷 범위"+text_required_msg}
    });
    
    $("[name*='subnetGateway_"+index+"']").rules("add", {
        required: function(){
            return checkEmpty($(".w2ui-msg-body input[name='subnetGateway_"+index+"']").val());
        },ipv4: function(){
            return $(".w2ui-msg-body input[name='subnetGateway_"+index+"']").val();  
        }, messages: {required: "게이트웨이"+text_required_msg}
    });
    
    $("[name*='subnetDns_"+index+"']").rules("add", {
        required: function(){
            return checkEmpty($(".w2ui-msg-body input[name='subnetDns_"+index+"']").val());
        },ipv4: function(){
            if( $(".w2ui-msg-body input[name='subnetDns_"+index+"']").val().indexOf(",") > -1 ){
                var list = ($(".w2ui-msg-body input[name='subnetDns_"+index+"']").val()).split(",");
                var flag = true;
                for( var i=0; i<list.length; i++ ){
                    var val = validateIpv4(list[i].trim());
                    if( !val ) flag = false;
                }
                if( !flag ) return "";
                else return list[0].trim();
            }else{
                return $(".w2ui-msg-body input[name='subnetDns_"+index+"']").val();
            }
        }, messages: {required: "DNS"+text_required_msg}
    });
    
    $("[name*='subnetReservedFrom_"+index+"']").rules("add", {
        required: function(){
            return checkEmpty($(".w2ui-msg-body input[name='subnetReservedFrom_"+index+"']").val());
        },ipv4: function(){
            return $(".w2ui-msg-body input[name='subnetReservedFrom_"+index+"']").val();  
        }, messages: {required: "IP 할당 제외 대역"+text_required_msg}
    });
    
    $("[name*='subnetReservedTo_"+index+"']").rules("add", {
        required: function(){
            return checkEmpty($(".w2ui-msg-body input[name='subnetReservedTo_"+index+"']").val());
        },ipv4: function(){
            return $(".w2ui-msg-body input[name='subnetReservedTo_"+index+"']").val();  
        }, messages: {required: "IP 할당 제외 대역"+text_required_msg}
    });
    
    $("[name*='subnetStaticFrom_"+index+"']").rules("add", {
        required: function(){
            return checkEmpty($(".w2ui-msg-body input[name='subnetStaticFrom_"+index+"']").val());
        },ipv4: function(){
            return $(".w2ui-msg-body input[name='subnetStaticFrom_"+index+"']").val();  
        }, messages: {required: "IP 할당 대역"+text_required_msg}
    });
    
    $("[name*='subnetStaticTo_"+index+"']").rules("add", {
        required: function(){
            return checkEmpty($(".w2ui-msg-body input[name='subnetStaticTo_"+index+"']").val());
        },ipv4: function(){
            return $(".w2ui-msg-body input[name='subnetStaticTo_"+index+"']").val();  
        }, messages: {required: "IP 할당 대역"+text_required_msg}
    });
    
    if( iaas.toLowerCase() != "vsphere" ){
        $("[name*='cloudSecurityGroups_"+index+"']").rules("add", {
            required: function(){
                return checkEmpty($(".w2ui-msg-body input[name='cloudSecurityGroups_"+index+"']").val());
            }, messages: {required: "보안 그룹"+text_required_msg}
        });
    }
    
    if( iaas.toLowerCase() == "google" ){
        $("[name*='networkName_"+index+"']").rules("add", {
            required: function(){
                return checkEmpty($(".w2ui-msg-body input[name='networkName_"+index+"']").val());
            }, messages: {required: "네트워크 명"+text_required_msg}
        });
    }
    if( iaas.toLowerCase() == 'aws' || iaas.toLowerCase() == 'google' ){
        $("[name*='availabilityZone_"+index+"']").rules("add", {
            required: function(){
                return checkEmpty($(".w2ui-msg-body input[name='availabilityZone_"+index+"']").val());
            }, messages: {required: zone_message + text_required_msg}
        });
    }
    
    w2popup.unlock();
}

/********************************************************
 * 설명 : ipv4 유효성 체크
 * 기능 : validateIpv4
 *********************************************************/
function validateIpv4(params){
    return /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(params);
}

/********************************************************
 * 설명 : 네트워크 입력 삭제
 * 기능 : delNetwork
 *********************************************************/
function delInternalNetwork(preDiv, index){
     var div= preDiv.split("_")[0] + "_"+ index;
     var form = preDiv.split("Div")[0]+"Form";
     $(form + " "+ div).html("");
     $(".w2ui-msg-body "+preDiv+" .addInternal").css("display","block");
}

/********************************************************
 * 설명 : 네트워크 정보 등록
 * 기능 : saveNetworkInfo
 *********************************************************/
function saveNetworkInfo(type, form) {
     networkInfo = [];
     //External 
     var staticFrom = iaas.toLowerCase() != "vsphere" ? 
             $(".w2ui-msg-body input[name='publicStaticIp']").val() : $(".w2ui-msg-body input[name='publicStaticFrom']").val();
     var external = {
                cfId               : cfId,
                id                 : checkEmpty(networkId) ? "" : networkId,
                iaas               : iaas.toUpperCase(),
                deployType         : deploy_type,
                seq                : 0,
                net                : "External",
                subnetStaticFrom   : staticFrom,
                subnetStaticTo     : $(".w2ui-msg-body input[name='publicStaticTo']").val(),
                subnetRange        : $(".w2ui-msg-body input[name='publicSubnetRange']").val(),
                subnetGateway      : $(".w2ui-msg-body input[name='publicSubnetGateway']").val(),
                subnetDns          : $(".w2ui-msg-body input[name='publicSubnetDns']").val(),
                subnetId           : $(".w2ui-msg-body input[name='publicSubnetId']").val()
     }
     networkInfo.push(external);
     //Internal
     var cnt = 1
     if( $("#"+form).find(".panel-body").length > 1 ){
         cnt = $("#"+form).find(".panel-body").length;
     }
     for(var i=1; i < cnt; i++){
         var internal = {
             cfId                : cfId,
             id                  : checkEmpty(networkId) ? "" : networkId,
             iaas                : iaas.toUpperCase(),
             deployType          : deploy_type,
             net                 : "Internal",
             seq                 : i,
             subnetRange         : $(".w2ui-msg-body input[name='subnetRange_"+i+"']").val(),
             subnetGateway       : $(".w2ui-msg-body input[name='subnetGateway_"+i+"']").val(),
             subnetDns           : $(".w2ui-msg-body input[name='subnetDns_"+i+"']").val(),
             subnetReservedFrom  : $(".w2ui-msg-body input[name='subnetReservedFrom_"+i+"']").val(),
             subnetReservedTo    : $(".w2ui-msg-body input[name='subnetReservedTo_"+i+"']").val(),
             subnetStaticFrom    : $(".w2ui-msg-body input[name='subnetStaticFrom_"+i+"']").val(),
             subnetStaticTo      : $(".w2ui-msg-body input[name='subnetStaticTo_"+i+"']").val(),
             networkName         : $(".w2ui-msg-body input[name='networkName_"+i+"']").val(),
             subnetId            : $(".w2ui-msg-body input[name='subnetId_"+i+"']").val(),
             cloudSecurityGroups : $(".w2ui-msg-body input[name='cloudSecurityGroups_"+i+"']").val(),
             availabilityZone    : $(".w2ui-msg-body input[name='availabilityZone_"+i+"']").val()
         }
         networkInfo.push(internal);
     }
     
     if (type == 'after') {
         $.ajax({
             type : "PUT",
             url : "/deploy/"+menu+"/install/saveNetworkInfo",
             contentType : "application/json",
             data : JSON.stringify(networkInfo),
             success : function(data, status) {
                 w2popup.clear();
                 getCountryCodes();
                 keyInfoPopup();
             },error : function(e, status) {
                 networkInfo = [];
                 w2alert("Cf Network 등록에 실패 하였습니다.", "Cf 설치");
             }
         });
     } else if (type == 'before') {
         w2popup.clear();
         defaultInfoPopup();
     }
}

/********************************************************
 * 설명 : 국가 코드 목록 조회
 * 기능 : getCountryCodes
 *********************************************************/
function getCountryCodes() {
    $.ajax({
        type : "GET",
        url : "/common/deploy/codes/countryCode/"+country_parent_code,
        contentType : "application/json",
        success : function(data, status) {
            countryCodes = new Array();
            if( data != null){
                var options = "";
                data.map(function(obj) {
                    if( keyInfo.countryCode == obj.codeName ){
                        options += "<option value='"+obj.codeName+"' selected>"+obj.codeName+"</option>";
                    }else{
                        options += "<option value='"+obj.codeName+"'>"+obj.codeName+"</option>";
                    }
                });
                $(".w2ui-msg-body select[name='countryCode']").html(options);
            }
        },
        error : function(e, status) {
            w2popup.unlock();
            w2alert("국가 코드를 가져오는데 실패하였습니다.", "CF 설치");
        }
    });
}

/********************************************************
 * 설명 : Key 생성  팝업
 * 기능 : uaaInfoPopup
 *********************************************************/
function keyInfoPopup(){
    settingDiegoUse(diegoUse, $("#KeyInfoDiv ul"));
    w2popup.open({
        title   : "<b>CF 설치</b>",
        width   : 650,
        height  : 500,
        body    : $("#KeyInfoDiv").html(),
        buttons : $("#KeyInfoButtonsDiv").html(),
        modal : true,
        showMax : false,
        onOpen : function(event) {
            event.onComplete = function() {
                $(".w2ui-msg-body input[name='cfDomain']").val(defaultInfo.domain);//도메인
                if (keyInfo != "") {
                    getCountryCodes();
                    $(".w2ui-msg-body input[name='stateName']").val(keyInfo.stateName);//시/도
                    $(".w2ui-msg-body input[name='localityName']").val(keyInfo.localityName);//시/구/군
                    $(".w2ui-msg-body input[name='organizationName']").val(keyInfo.organizationName);//회사명
                    $(".w2ui-msg-body input[name='unitName']").val(keyInfo.unitName);//부서명
                    $(".w2ui-msg-body input[name='email']").val(keyInfo.email);//이메일
                }
            }
        },
        onClose : function(event) {
            event.onComplete = function() {
                initSetting();
            }
        }
    });
}

/********************************************************
 * 설명 : Key 생성 확인
 * 기능 : createKeyConfirm
 *********************************************************/
function createKeyConfirm(){
     var message = "";
     if( !checkEmpty(keyFile) ){//이미 key가 생성됐으면,
         message = "Key를 재 생성하시겠습니다.? \nDiego와 연동한 경우 Diego를 다시 설치해야 합니다.";
     }else{
        message ="Key를 생성하시겠습니까?"; 
     }
     w2confirm({
        width          : 350,
        height         : 180,
        title          : '<b>Key 생성 여부</b>',
        msg            : message,
        modal          : true,
        yes_text       : "확인",
        no_text        : "취소",
        yes_callBack   : function(){
            keyInfo = {
                    id                  : cfId,
                    iaas                : iaas.toLowerCase(),
                    platform            : menu, //cf -> 1, diego -> 2, cf&diego -> 3
                    domain              : $(".w2ui-msg-body input[name='cfDomain']").val(), //도메인
                    countryCode         : $(".w2ui-msg-body select[name='countryCode']").val(), //국가코드
                    stateName           : $(".w2ui-msg-body input[name='stateName']").val(), //시/도
                    localityName        : $(".w2ui-msg-body input[name='localityName']").val(), //시/구/군
                    organizationName    : $(".w2ui-msg-body input[name='organizationName']").val(), //회사명
                    unitName            : $(".w2ui-msg-body input[name='unitName']").val(), //부서명
                    email               : $(".w2ui-msg-body input[name='email']").val(), //email
                    version             : defaultInfo.releaseVersion,
                    status              : "Y"
            }
           $('#KeyInfoForm').submit();
        },
        no_callBack : function(event){
        }
    });
}

/********************************************************
 * 설명 : Key 정보 생성
 * 기능 : createKeyInfo
 *********************************************************/
function createKeyInfo(){
     w2popup.lock("Key 생성 중입니다.", true);
     
    $.ajax({
       type : "POST",
       url : "/common/deploy/key/createKey",
       contentType : "application/json",
       data : JSON.stringify(keyInfo),
       success : function(data, status) {
           w2popup.unlock();
           w2alert("Key 생성에 성공하였습니다.", "CF Key 생성");
           keyFile = data.keyFile;
           keyInfo.status = "";
       },
       error :function(request, status, error) {
           w2popup.unlock();
           var errorResult = JSON.parse(request.responseText);
           w2alert(errorResult.message, "CF Key 생성");
           keyInfo.status = "";
           keyFile = "";
       }
   });
}

/********************************************************
 * 설명 : Key 정보 등록
 * 기능 : saveUaaInfo
 *********************************************************/
function saveKeyInfo(type){
     if( type == "after"){
         if( checkEmpty(keyFile) ){
             w2alert("Key를 생성하지 않았습니다. 확인해주세요.", "CF 설치");
             return;
         }
         keyInfo = {
                 id                  : cfId,
                 iaas                : iaas.toLowerCase(),
                 platform            : menu, //cf -> 1, diego -> 2, cf&diego -> 3
                 domain              : $(".w2ui-msg-body input[name='cfDomain']").val(), //도메인
                 countryCode         : $(".w2ui-msg-body select[name='countryCode']").val(), //국가코드
                 stateName           : $(".w2ui-msg-body input[name='stateName']").val(), //시/도
                 localityName        : $(".w2ui-msg-body input[name='localityName']").val(), //시/구/군
                 organizationName    : $(".w2ui-msg-body input[name='organizationName']").val(), //회사명
                 unitName            : $(".w2ui-msg-body input[name='unitName']").val(), //부서명
                 email               : $(".w2ui-msg-body input[name='email']").val(), //email
                 status              : ""
         }
        $.ajax({
            type : "PUT",
            url : "/deploy/"+menu+"/install/saveKeyInfo",
            contentType : "application/json",
            data : JSON.stringify(keyInfo),
            success : function(data, status) {
                w2popup.clear();
                settingIaasPopup("resource");
            },
            error : function(e, status) {
                w2alert("Key 생성 정보 등록에 실패 하였습니다.", "CF 설치");
            }
        });
    } else{
        w2popup.clear();
        settingIaasPopup("network");
    }
}


/********************************************************
 * 설명 : 리소스 정보 팝업(openstack/aws/google/vsphere)
 * 기능 : resourceInfoPopup
 *********************************************************/
function resourceInfoPopup(div, height) {
    settingDiegoUse(diegoUse, $(div+" ul"));
    w2popup.open({
        title   : "<b>CF 설치</b>",
        width   : 750,
        height  : 690,
        body    : $(div+"Div").html(),
        buttons : $(div+"Buttons").html(),
        modal : true,
        showMax : false,
        onOpen : function(event) {
            event.onComplete = function() {
                if(defaultInfo.releaseVersion >= 272 || (defaultInfo.releaseName.indexOf("paasta-controller") > -1 && compare(defaultInfo.releaseVersion, "3.0") > -1)){
                    $(".w2ui-msg-body #runnerTypeDiv").css('display', 'none');
                }else {
                    $(".w2ui-msg-body #runnerTypeDiv").css('display', 'block');
                }
                if (resourceInfo != "") {
                    $(".w2ui-msg-body input[name='boshPassword']").val(resourceInfo.boshPassword);
                    $(".w2ui-msg-body input[name='smallFlavor']").val(resourceInfo.smallFlavor);
                    $(".w2ui-msg-body input[name='mediumFlavor']").val(resourceInfo.mediumFlavor);
                    $(".w2ui-msg-body input[name='largeFlavor']").val(resourceInfo.largeFlavor);
                    $(".w2ui-msg-body input[name='runnerFlavor']").val(resourceInfo.runnerFlavor);
                    
                    if( iaas.toLowerCase() == "vsphere" ){
                        $(".w2ui-msg-body input[name='smallFlavorRam']").val(resourceInfo.smallRam);
                        $(".w2ui-msg-body input[name='smallFlavorDisk']").val(resourceInfo.smallDisk);
                        $(".w2ui-msg-body input[name='smallFlavorCpu']").val(resourceInfo.smallCpu);
                        $(".w2ui-msg-body input[name='mediumFlavorRam']").val(resourceInfo.mediumRam);
                        $(".w2ui-msg-body input[name='mediumFlavorDisk']").val(resourceInfo.mediumDisk);
                        $(".w2ui-msg-body input[name='mediumFlavorCpu']").val(resourceInfo.mediumCpu);
                        $(".w2ui-msg-body input[name='largeFlavorRam']").val(resourceInfo.largeRam);
                        $(".w2ui-msg-body input[name='largeFlavorDisk']").val(resourceInfo.largeDisk);
                        $(".w2ui-msg-body input[name='largeFlavorCpu']").val(resourceInfo.largeCpu);
                        $(".w2ui-msg-body input[name='runnerFlavorRam']").val(resourceInfo.runnerRam);
                        $(".w2ui-msg-body input[name='runnerFlavorDisk']").val(resourceInfo.runnerDisk);
                        $(".w2ui-msg-body input[name='runnerFlavorCpu']").val(resourceInfo.runnerCpu);
                    }
                }
                w2popup.lock("스템셀을 조회 중입니다.", true);
                getStamcellList();
            }
        },
        onClose : function(event) {
            event.onComplete = function() {
                initSetting();
            }
        }
    });
}

/********************************************************
 * 설명 : 스템셀 조회
 * 기능 : getStamcellList
 *********************************************************/
function getStamcellList() {
    $.ajax({
        type : "GET",
        url : "/common/deploy/stemcell/list/cf/" + iaas,
        contentType : "application/json",
        success : function(data, status) {
            stemcells = new Array();
            if(data.records != null ){
                var options= "<option value=''>스템셀을 업로드하세요.</option>";
                data.records.map(function(obj) {
                    var resource_data = resourceInfo.stemcellName + "/"+ resourceInfo.stemcellVersion;
                    if( resource_data == (obj.stemcellFileName+"/"+obj.stemcellVersion) ){
                        options += "<option value='"+obj.stemcellFileName+"/"+obj.stemcellVersion+"' selected>"+obj.stemcellFileName+"/"+obj.stemcellVersion+"</option>";
                    }else{
                        options += "<option value='"+obj.stemcellFileName+"/"+obj.stemcellVersion+"'>"+obj.stemcellFileName+"/"+obj.stemcellVersion+"</option>";
                    }
                });
                $(".w2ui-msg-body select[name='stemcells']").html(options);
            }
            w2popup.unlock();
        },
        error : function(e, status) {
            w2popup.unlock();
            w2alert("Stemcell List 를 가져오는데 실패하였습니다.", "CF 설치");
        }
    });
}

/********************************************************
 * 설명 : CF 고급 기능
 * 기능 : resourceJobSettingsPop
 *********************************************************/
function resourceJobSettingsPop(){
    var flag = false;
    for( var i=0; i < releases.length; i++ ){
        if( releases[i] == defaultInfo.releaseVersion ) flag = true;
    }
    if( !flag ){
        w2alert("지원하지 않는 릴리즈 입니다. ",true);
        return;
    }
    w2popup.open({
        title   : '<b>CF 고급 설정</b>',
        width   : 625,
        height  : 685,
        showMax : true,
        body    : '<div id="cfDetailDiv" style="position: absolute; left: 5px; top: 5px; right: 5px; bottom: 5px;"></div>',
        buttons :$("#cfDetailButtons").html(),
        onOpen  : function (event) {
            event.onComplete = function () {
                if( checkEmpty(config.layout) ){
                    setW2layout();
                }
                $('#w2ui-popup #cfDetailDiv').w2render('layout2');
                w2ui['layout2'].content('main', $('#cfDetailPopDiv').html());
                settingCfJobs();
            };
        }, onClose : function(event){
            jobPopupComplete();
       }
    });
}

/********************************************************
 * 설명 : CF 고급 설정 정보 저장
 * 기능 : saveCfJobsInfo
 *********************************************************/
function saveCfJobsInfo(){
     w2popup.lock(save_info_msg, true);
     var i=0;
     var flag=true;
     
      $(".w2ui-msg-body #cfJobListDiv li > ul").each(function(){
         var input = $($(this).children()[0]).find("input");
         if ($(input).val != "" && $(input).val() >=0 && $(input).val() <=3) {
         }else{
             flag = false;
         }
     }); 
     
     jobsInfo = [];
     $(".w2ui-msg-body #cfJobListDiv li > ul").each(function(){
         var input = $($(this).children()[0]).find("input");
         var pwd_input = $($(this).children()[1]).find("input");
         i ++;
         var index = $(input).attr("name").split("_z").length-1;
         var z_index = $(input).attr("name").split("_").length-1;
         var job = {
                 id           : cfId,
                 seq          : i,
                 deploy_type  : deploy_type,
                 job_name     : $(input).attr("name").split("_z")[index-1],
                 job_id       : $(input).attr("id"),
                 instances    : $(input).val(),
                 zone         : $(input).attr("name").split("_")[z_index]
         }
         jobsInfo.push(job);
     });
     
     if( flag ){
         $.ajax({
                type : "PUT",
                url : "/deploy/"+menu+"/install/save/jobsInfo",
                contentType : "application/json",
                data : JSON.stringify(jobsInfo),
                success : function(status) {
                    w2popup.unlock();
                    jobPopupComplete();
                },
                error :function(request, status, error) {
                    w2popup.unlock();
                }
            });
     }else{
         w2popup.unlock();
         w2alert("인스턴스 수는 0부터 3까지만 가능합니다.");
     }
}

/********************************************************
 * 설명 : CF Jobs 팝업 화면 닫았을 경우
 * 기능 : jobPopupComplete
 *********************************************************/
function jobPopupComplete(){
    config = "";
    $().w2destroy('layout2');
    if( iaas.toUpperCase() == "VSPHERE" ){
        resourceInfoPopup("#vSphereResourceInfo", 695);
    }else{
        resourceInfoPopup("#resourceInfo",690 );
   }
}

/********************************************************
 * 설명 : CF Jobs 정보 설정
 * 기능 : settingCfJobs
 *********************************************************/
function settingCfJobs(){
	console.log("1");
    var release_version = defaultInfo.releaseVersion;
    release_version = settingReleaseVersion(release_version);
    $.ajax({
        type : "GET",
        url : "/deploy/cf/install/save/job/list/"+release_version+"/"+'DEPLOY_TYPE_CF',
        contentType : "application/json",
        success : function(data, status) {
        	console.log(data);
            if( !checkEmpty(data) ){
                var div = "";
                var html = "";
                html += '<div class="panel panel-info" style="height: 100%;overflow: auto;" >';
                html += '<div class="panel-heading"><b>CF 고급 설정</b></div>';
                html += '<div class="panel-body">';
                html += '<div id="cfJobListDiv">';
                html += '<p style="color:red;">- 고급 설정 값을 변경하지 않을 경우 아래에 입력 된 기본 값으로 자동 설정됩니다.</p>';
                html += '<p style="color:red;">- 해당 Job의 인스턴스 수는 0-3까지 입력하실 수 있습니다.</p>';
                for( var j=1; j<networkInfo.length; j++ ){
                    html += "<p style='color: #565656;font-size: 13px;font-weight:bolder;margin-top: 20px;'>[Internal 네트워크_"+ j+ "]</p>"
                    for( var i=0; i<data.length; i++ ){
                        if( j == 1 && data[i].zone_z1 == "true" ){
                            html += setJobSettingHtml(data[i], j );
                        }else if( j == 2 && data[i].zone_z2 == "true"  ){
                            html += setJobSettingHtml(data[i], j);
                        }
                    }
                }
                html +='</div></div></div>';
                $("#cfDetailForm").html(html);
                $(".w2ui-msg-body #cfDetailForm").html(html);
               
                if( jobsInfo.length > 0 ){
                    for( var i=0; i<jobsInfo.length; i++ ){
                        $(".w2ui-msg-body input[name='"+jobsInfo[i].job_name+"_"+jobsInfo[i].zone+"']").val(jobsInfo[i].instances);
                    }
                }
            }
        },
        error : function(e, status) {
            w2alert(JSON.parse(e.responseText).message, "CF 설치");
        }
    });
}

/********************************************************
 * 설명 : CF 고급 설정 paasta release version 설정
 * 기능 : settingReleaseVersion
 *********************************************************/
function settingReleaseVersion( version ){
    var releaseVersion = version;
     if( version == "3.0" ){
        releaseVersion = "272";
    }else if( version == "2.0" ){
        releaseVersion = "247"
    } else if(version == "3.1"){
    	releaseVersion = "287";
    }
    return releaseVersion;
}

/********************************************************
 * 설명 : CF 고급 설정 HTML 설정
 * 기능 : setJobSettingHtml
 *********************************************************/
function setJobSettingHtml(data, j){
    var html = "";
    if( (iaas.toLowerCase() != "vsphere" && data.job_name =="stats") || 
            (iaas.toLowerCase() == "openstack" && data.job_name == "api_worker") ||
            (j == 1 && (data.job_name == "api" || data.job_name == "uaa" || data.job_name == "api_worker")) ){
        return html;
    }else{
        if( !(diegoUse == "true" && ( data.job_name == "hm9000" || data.job_name == "stats" || data.job_name == "runner")) ){
            html += '<ul class="w2ui-field" style="border: 1px solid #c5e3f3;padding: 10px;">';
            html +=     '<li style="display:inline-block; width:35%;">';
            html +=         '<label style="text-align: left;font-size:11px;">'+data.job_name+'_z'+j+'</label>';
            html +=     '</li>';
            html +=     '<li style="display:inline-block; width:60%;vertical-align:middle; line-height:3; text-align:right;">';
            html +=         '<ul>';
            html +=             '<li>';
            html +=                 '<label style="display:inline-block;">인스턴스 수 : </label>&nbsp;&nbsp;&nbsp;';
            if( iaas.toLowerCase() == "vsphere" && networkInfo.length > 2 && j == 1 && ( data.job_name == "consul" || data.job_name == "etcd" ) ){
                //vsphere Internal 네트워크가 2개 이상일 경우 etcd_z1, consul_z1의 instance 2 
                html +=                 '<input class="form-control" style="width:60%; display:inline-block;" onblur="instanceControl(this);" onfocusin="instanceControl(this);" onfocusout="instanceControl(this);" maxlength="1" type="number" min="0" max="3" value="2" id="'+data.id+'" name="'+data.job_name+'_z'+j+'"/>';
            }else{
                html +=                 '<input class="form-control" style="width:60%; display:inline-block;" onblur="instanceControl(this);" onfocusin="instanceControl(this);" onfocusout="instanceControl(this);" maxlength="1" type="number" min="0" max="3" value="1" id="'+data.id+'" name="'+data.job_name+'_z'+j+'"/>';
            }
            html +=              '</li>';
            html +=         '</ul>';
            html +=     '</li>';
            html += '</ul>';
        }
    }
    return html;
}

/********************************************************
 * 설명 : CF Jobs 유효성 추가
 * 기능 : instanceControl
 *********************************************************/
function instanceControl(e){
     if ( e.value != "" && e.value >=0 && e.value<=3) {
         if( $(e).parent().find("p").length > 0 ){
             $(e).parent().find("p").remove();
         }
     }else{
         var name = $(e).attr("name");
         if( jobsInfo.length >0 ){
             for( var i=0; i<jobsInfo.length; i++ ){
                 if( $(e).attr("name") == jobsInfo[i].job_name+"_"+jobsInfo[i].zone ){
//                      e.value= jobsInfo[i].instances;
                 }
             }
         }else{
//              $(e).val("1");
         }
         if( $(e).parent().find("p").length == 0 ){
             $(e).parent().append("<p>0부터 3까지 숫자만 입력 가능 합니다.</p>");
         }
     }
}


/********************************************************
 * 설명 : 리소스 정보 등록
 * 기능 : saveResourceInfo
 *********************************************************/
function saveResourceInfo(type) {
    var stemcellInfos = $(".w2ui-msg-body select[name='stemcells']").val().split("/");
    resourceInfo = {
            id               : cfId,
            iaas             : iaas.toUpperCase(),
            platform         : "cf",
            stemcellName     : stemcellInfos[0],
            stemcellVersion  : stemcellInfos[1],
            boshPassword     : $(".w2ui-msg-body input[name='boshPassword']").val(),
            smallFlavor      : $(".w2ui-msg-body input[name='smallFlavor']").val(),
            smallCpu         : $(".w2ui-msg-body input[name='smallFlavorCpu']").val(),
            smallRam         : $(".w2ui-msg-body input[name='smallFlavorRam']").val(),
            smallDisk        : $(".w2ui-msg-body input[name='smallFlavorDisk']").val(),
            mediumFlavor     : $(".w2ui-msg-body input[name='mediumFlavor']").val(),
            mediumCpu        : $(".w2ui-msg-body input[name='mediumFlavorCpu']").val(),
            mediumRam        : $(".w2ui-msg-body input[name='mediumFlavorRam']").val(),
            mediumDisk       : $(".w2ui-msg-body input[name='mediumFlavorDisk']").val(),
            largeFlavor      : $(".w2ui-msg-body input[name='largeFlavor']").val(),
            largeCpu         : $(".w2ui-msg-body input[name='largeFlavorCpu']").val(),
            largeRam         : $(".w2ui-msg-body input[name='largeFlavorRam']").val(),
            largeDisk        : $(".w2ui-msg-body input[name='largeFlavorDisk']").val(),
            runnerFlavor     : $(".w2ui-msg-body input[name='runnerFlavor']").val(),
            runnerCpu        : $(".w2ui-msg-body input[name='runnerFlavorCpu']").val(),
            runnerRam        : $(".w2ui-msg-body input[name='runnerFlavorRam']").val(),
            runnerDisk       : $(".w2ui-msg-body input[name='runnerFlavorDisk']").val()
    }
    if (type == 'after') {
        //Server send Cf Info
        $.ajax({
            type : "PUT",
            url : "/deploy/"+menu+"/install/saveResourceInfo",
            contentType : "application/json",
            data : JSON.stringify(resourceInfo),
            success : function(data, status) {
                w2popup.clear();
                deploymentFile = data.deploymentFile;
                createSettingFile(data.id);
            },
            error : function(e, status) {
                w2alert("Cf Resource 등록에 실패 하였습니다.", "Cf 설치");
            }
        });
    } else if (type == 'before') {
        w2popup.clear();
        keyInfoPopup();
    }
}

/********************************************************
 * 설명 :  Manifest 파일 생성
 * 기능 : createSettingFile
 *********************************************************/
function createSettingFile(id){
    var settingFile = {
            iaas        : iaas,
            id          : id,
            platform    : "cf"
    }
    $.ajax({
        type : "POST",
        url : "/deploy/"+menu+"/install/createSettingFile",
        contentType : "application/json",
        data : JSON.stringify(settingFile),
        success : function(status) {
            deployPopup();
        },
        error :function(request, status, error) {
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "CF  배포 파일 생성");
            if(iaas.toLowerCase() == "vsphere"){
                resourceInfoPopup("#vSphereResourceInfo", 695);
            }else{
                resourceInfoPopup("#resourceInfo", 690);
            }
        }
    });
}

/********************************************************
 * 설명 :  배포정보 팝업
 * 기능 : deployPopup
 *********************************************************/
function deployPopup() {
    settingDiegoUse(diegoUse, $("#deployDiv ul"));
    w2popup.open({
        title   : "<b>CF 설치</b>",
        width   : 750,
        height  : 550,
        body    : $("#deployDiv").html(),
        buttons : $("#deployDivButtons").html(),
        modal : true,
        showMax : true,
        onClose : initSetting,
        onOpen : function(event) {
            event.onComplete = function() {
                getDeployInfo();
            }
        }
    });
}

/********************************************************
 * 설명 : 배포정보 조회
 * 기능 : getDeployInfo
 *********************************************************/
function getDeployInfo() {
    $.ajax({
        type : "GET",
        url : "/common/use/deployment/" + deploymentFile,
        contentType : "application/json",
        success : function(data, status) {
            if (status == "success") {
                $(".w2ui-msg-body #deployInfo").text(data);
            } else if (status == "204") {
                w2alert("배포파일이 존재하지 않습니다.", "CF 설치");
            }
        },
        error : function(e, status) {
            w2alert(JSON.parse(e.responseText).message, "CF 설치");
        }
    });
}

/********************************************************
 * 설명 : 배포 확인창
 * 기능 : cfDeploy
 *********************************************************/
function cfDeploy(type) {
     if( type == "before"  ){
         if(iaas.toLowerCase() == "vsphere"){
                resourceInfoPopup("#vSphereResourceInfo", 695);
            }else{
                resourceInfoPopup("#resourceInfo",690 );
            }
     }else{
        if ( menu =="cf" ){
            w2confirm({
                msg : "설치하시겠습니까?",
                title : "<b>"+w2utils.lang('CF 설치') + "</b>",
                yes_text : "예",
                no_text : "아니오",
                yes_callBack : installPopup
            });
        }else if( menu == "cfDiego" ){
            popupInit();
            setDiegoPopup('after'); //cfDiego.jsp
        }
     }
 }
    

/********************************************************
 * 설명 :  설치 팝업
 * 기능 : installPopup
 *********************************************************/
var installClient = "";
function installPopup(){
    var deploymentName =  defaultInfo.deploymentName;
    var message = "CF(배포명:" + deploymentName +  ") ";
    
    var requestParameter = {
            id       : cfId,
            iaas     : iaas,
            platform : "cf"
    };
    
    w2popup.open({
        title   : "<b>CF 설치</b>",
        width   : 750,
        height  : 600,
        body    : $("#installDiv").html(),
        buttons : $("#installButtons").html(),
        modal   : true,
        showMax : true,
        onOpen  : function(event){
            event.onComplete = function(){
                //deployFileName
                var socket = new SockJS('/deploy/cf/install/cfInstall');
                installClient = Stomp.over(socket); 
                installClient.connect({}, function(frame) {
                    installClient.subscribe('/user/deploy/cf/install/logs', function(data){
                        
                        var installLogs = $(".w2ui-msg-body #installLogs");
                        var response = JSON.parse(data.body);
                        
                        if ( response.messages != null ) {
                               for ( var i=0; i < response.messages.length; i++) {
                                installLogs.append(response.messages[i] + "\n").scrollTop( installLogs[0].scrollHeight );
                               }
                               
                               if ( response.state.toLowerCase() != "started" ) {
                                if ( response.state.toLowerCase() == "done" )    message = message + " 설치가 완료되었습니다."; 
                                if ( response.state.toLowerCase() == "error" ) message = message + " 설치 중 오류가 발생하였습니다.";
                                if ( response.state.toLowerCase() == "cancelled" ) message = message + " 설치 중 취소되었습니다.";
                                
                                installStatus = response.state.toLowerCase();
                                $('.w2ui-msg-buttons #deployPopupBtn').prop("disabled", false);
                                if(installClient!=""){
                                    installClient.disconnect();
                                    installClient = "";
                                }
                                w2alert(message, "CF 설치");
                               }
                        }
                    });
                    installClient.send('/send/deploy/cf/install/cfInstall', {}, JSON.stringify(requestParameter));
                });
            }
        }, onClose : function(event){
            if( installClient != "" ){
                installClient.disconnect();
            }
            initSetting();
        }
    });
}
    
/********************************************************
 * 설명 : diego 사용 여부에 따른 화면 display
 * 기능 : settingDiegoUse
 *********************************************************/
function settingDiegoUse(flag, thisDiv){
    if(flag=="false"){
        $("#fingerprint").css("display","none");
    }else{
        $("#fingerprint").css("display","block");
        //progress style
        if( menu == "cfDiego" ){
            thisDiv.removeClass("progressStep_6");
            thisDiv.addClass("progressStep_5");
            $(".progressStep_5 .install").hide();
        }
    }
    return;
}

/********************************************************
 * 설명 : CF 삭제
 * 기능 : deletePopup
 *********************************************************/
function deletePopup(record){
    var requestParameter = {
            iaas        : (record.iaas) ? record.iaas : record.cType, 
            id          : record.id,
            platform    : "cf"
    };
    if ( record.deployStatus == null || record.deployStatus == '' ) {
        // 단순 레코드 삭제
        $.ajax({
            type : "DELETE",
            url : "/deploy/cf/delete/data",
            data : JSON.stringify(requestParameter),
            contentType : "application/json",
            success : function(data, status) {
                doSearch();
            },
            error : function(request, status, error) {
                w2alert( JSON.parse(request.responseText).message, "CF 삭제");
            }
        });
    } else{
        var message = "";
        var body = '<textarea id="deleteLogs" style="width:95%;height:90%;overflow-y:visible;resize:none;background-color: #FFF; margin:2%" readonly="readonly"></textarea>';
        w2popup.open({
            width : 700,
            height : 500,
            title : "<b>CF 삭제</b>",
            body  : body,
            buttons : '<button class="btn" style="float: right; padding-right: 15%;" onclick="popupComplete();">닫기</button>',
            showMax : true,
            onOpen : function(event){
                event.onComplete = function(){
                    var socket = new SockJS('/deploy/cf/delete/instance');
                    deleteClient = Stomp.over(socket); 
                    deleteClient.connect({}, function(frame) {
                        deleteClient.subscribe('/user/deploy/cf/delete/logs', function(data){
                            
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
                                    if(deleteClient!=""){
                                        deleteClient.disconnect();
                                        deleteClient = "";
                                    }
                                    w2alert(message, "CF 삭제");
                                    if( menu == "cfDiego" ){
                                        $("#cfDiegoPopupDiv").load("/deploy/diego/install/diegoPopup",function(event){
                                            record = record.diegoVo;
                                            diegoDeletePopup(record);
                                        });
                                    }
                                   }
                            }
                        });
                        deleteClient.send('/send/deploy/cf/delete/instance', {}, JSON.stringify(requestParameter));
                    });
                }
            }, onClose : function (event){
                event.onComplete= function(){
                    $("textarea").text("");
                    w2ui['config_cfGrid'].reset();
                    if( deleteClient != "" ){
                        deleteClient.disconnect();
                    }
                    doSearch();
                }
            }
        });
    }        
}

/********************************************************
 * 설명 : 인프라 별 팝업 화면 설정
 * 기능 : settingIaasPopup
 *********************************************************/
function settingIaasPopup(type){
     if( type == "network" ){
        if( iaas.toUpperCase() == "VSPHERE" ){
            defaultNetworkPopup("#VsphereNetworkInfo", 695);
        }else if(iaas.toUpperCase() == "GOOGLE" ){
            defaultNetworkPopup("#googleNetworkInfo", 665);
        }else{
            defaultNetworkPopup("#defaultNetworkInfo", 665);
        }
     }else if( type == "resource" ){
         if( iaas.toUpperCase() == "VSPHERE" ){
             resourceInfoPopup("#vSphereResourceInfo", 695);
         }else{
             resourceInfoPopup("#resourceInfo",690 );
        }
     }
}

/********************************************************
 * 설명 : 전체전역변수 초기화
 * 기능 : initSetting
 *********************************************************/
function initSetting() {
    cfId = "";
    installClient ="";
    installStep = 0;
    internalCnt = 1;
    popupInit();
    gridReload();
}

/********************************************************
 * 설명 : 팝업전역변수 초기화
 * 기능 : popupInit
 *********************************************************/
function popupInit(){
    diegoUse = "";
    networkId="";
    defaultInfo = "";
    networkInfo = [];
    publicStaticIp = "";
    jobsInfo=[];
    resourceInfo = "";
    releases = "";
    stemcells = "";
    installStatus ="";
    deploymentFile = "";
    country_parent_code = "";
    countryCodes = null;
    keyFile ="";

}

/********************************************************
 * 설명 : Install/Delete 팝업 종료시 이벤트
 * 기능 : popupComplete
 *********************************************************/
function popupComplete(){
    var msg;
    if(installStatus == "done" || installStatus == "error"){
        msg = $(".w2ui-msg-title b").text() + " 화면을 닫으시겠습니까?";
    }else{
        msg = $(".w2ui-msg-title b").text() + " 화면을 닫으시겠습니까?<BR>(닫은 후에도 완료되지 않는 설치 또는 삭제 작업은 계속 진행됩니다.)";
    }
    w2confirm({
        title   : "<b>"+ $(".w2ui-msg-title b").text() +"</b>",
        msg     : msg,
        yes_text: "확인",
        yes_callBack : function(envent){
            w2popup.close();
            //params init
            initSetting();
        },
        no_text : "취소"
    });
}

/********************************************************
 * 설명 : 팝업창 닫을 경우
 * 기능 : popupClose
 *********************************************************/
function popupClose() {
     $().w2destroy('layout2');
    //params init
    initSetting();
    //grid Reload
    gridReload();
}

/********************************************************
 * 설명 : 그리드 재조회
 * 기능 : gridReload
 *********************************************************/
function gridReload() {
     if( menu == "cf" ){
         w2ui['config_cfGrid'].reset();
         doSearch();
     }else if( menu =="cfDiego" ){
         w2ui['config_cfDiegoGrid'].load("<c:url value='/deploy/cfDiego/list/"+iaas+"'/>",
                    function() { doButtonStyle(); });
     }else{
         w2ui['config_cfGrid'].reset();
         doSearch();
     }
}
</script>

<!-- Default 정보 DIV -->
<div id="defaultInfoDiv" style="width: 100%; height: 100%;" hidden="true">
    <form id="defaultInfoForm" >
        <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
            <ul class="progressStep_6">
                <li class="active">기본 정보</li>
                <li class="before">네트워크 정보</li>
                <li class="before">Key 생성</li>
                <li class="before">리소스 정보</li>
                <li class="before">배포파일 정보</li>
                <li class="before install">설치</li>
            </ul>
        </div>
        <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
            <div class="panel panel-info">    
                <div class="panel-heading"><b>기본정보</b></div>
                <div class="panel-body">
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 36%; font-size: 11px;">설치관리자 UUID</label>
                        <div style=" width: 60%;">
                            <input name="directorUuid" type="text" style="display:inline-block;width:80%;" readonly  placeholder="설치관리자 UUID를 입력하세요." />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 36%; font-size: 11px;">배포 명</label>
                        <div style=" width: 60%;">
                            <input name="deploymentName" type="text" style="display:inline-block;width:80%;" onkeydown="return fn_press_han(event, this);" onblur="return fn_press_han(event, this);"  style='ime-mode:inactive;' placeholder="배포 명을 입력하세요." />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 36%; font-size: 11px;">CF 릴리즈
                        <span class="glyphicon glyphicon glyphicon-question-sign cf-info" style="cursor:pointer;font-size: 14px;color: #157ad0;" data-toggle="popover"  data-trigger="click" data-html="true" title="<b>설치 지원 버전 목록</b>"></span>
                        </label>
                        <div style=" width: 60%;">
                            <select name="releases" onchange='setDisabledMonitoring(this.value); setInputDisplay(this.value);' style="display:inline-block; width: 80%;">
                                <option value="">CF 릴리즈를 선택하세요.</option>
                            </select>
                        </div>
                    </div>
                    <div class="w2ui-field" id="loggregator" style="display:none;">
                        <label style="text-align: left; width: 36%; font-size: 11px;">LOGGREGATOR 릴리즈</label>
                        <div style=" width: 60%;">
                            <select name="loggregatorReleases" style="display:inline-block; width: 80%;">
                                <option value="">LOGGREGATOR 릴리즈를 선택하세요.</option>
                            </select>
                        </div>
                    </div>
                    <div class="w2ui-field" id="fingerprint">
                        <label style="text-align: left; width: 36%; font-size: 11px;">SSH 핑거프린트
                        </label>
                        <div style=" width: 60%;">
                            <input name="appSshFingerprint" type="text" style="display:inline-block;width:80%;" placeholder="Diego 키 생성 후 SSH 핑거프린트를 입력하세요." />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 36%; font-size: 11px;">기본 조직명</label>
                        <div style=" width: 60%;">
                            <input name="domainOrganization" type="text" style="display:inline-block;width:80%;" placeholder="기본 조직명을 입력하세요." />
                        </div>
                    </div>
                    <div class="w2ui-field" id="deaDiskmbDiv">
                        <label style="text-align: left; width: 36%; font-size: 11px;">DEA DISK 사이즈</label>
                        <div style=" width: 60%;">
                            <input name="deaDiskMB" type="text" style="display:inline-block;width:80%;" onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;' placeholder="예) 32768" />
                        </div>
                    </div>
                    <div class="w2ui-field" id="deaMemorymbDiv">
                        <label style="text-align: left; width: 36%; font-size: 11px;">DEA MEMORY 사이즈</label>
                        <div style=" width: 60%;">
                            <input name="deaMemoryMB" type="text" style="display:inline-block;width:80%;" onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;' placeholder="예) 8192" />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align:left; width:36%; font-size:11px;">PaaS-TA 모니터링
                        <span class="glyphicon glyphicon glyphicon-question-sign paastaMonitoring-info" style="cursor:pointer;font-size: 14px;color: #157ad0;" data-toggle="popover"  data-trigger="click" data-html="true"></span>
                        </label>
                        <div style=" width: 60%;">
                            <input name="paastaMonitoring" type="checkbox" id="paastaMonitoring" onchange="checkPaasTAMonitoringUseYn(this);" disabled />사용
                        </div>
                    </div>
                </div>
            </div>
            <div class="panel panel-info" style="margin-top:2%;">
                <div class="panel-heading"><b>CF 정보</b></div>
                <div class="panel-body" >
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 36%; font-size: 11px;">도메인</label>
                        <div style=" width: 60%;">
                            <input name="domain" type="text" style="isplay:inline-blcok; width: 80%;" required placeholder="도메인을 입력하세요. 예)cfdoamin.com" />
                            <div class="isMessage"></div>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 36%; font-size: 11px;">도메인 설명</label>
                        <div style=" width: 60%;">
                            <input name="description" type="text" style="isplay:inline-blcok; width: 80%;" required placeholder="도메인에 대한 설명을 입력하세요." />
                            <div class="isMessage"></div>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 36%; font-size: 11px;">로그인 비밀번호</label>
                        <div style=" width: 60%;">
                            <input name="loginSecret" type="text" style="display:inline-blcok; width: 80%;" required placeholder="로그인 비밀번호룰 입력하세요." />
                            <div class="isMessage"></div>
                        </div>
                    </div>
                </div>
            </div>    
            <div class="panel panel-info" style="margin-top:2%;">
                <div class="panel-heading"><b>PaaS-TA 모니터링 정보</b></div>
                <div class="panel-body">
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 36%; font-size: 11px;">PaaS-TA 모니터링 Ingestor 서버 IP</label>
                        <div style=" width: 60%;">
                            <input name="ingestorIp" type="text" style="display:inline-blcok; width: 80%;" disabled placeholder="예)10.0.0.0" />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form>
    <div id="DefaultInfoButtonDiv" class="w2ui-buttons" hidden="true">
        <button class="btn" style="float: right; padding-right: 15%" onclick="$('#defaultInfoForm').submit();">다음>></button>
    </div>
</div>

<!-- aws/openstack Network 설정 DIV -->
<div id="defaultNetworkInfoDiv" style="width: 100%; height: 100%;" hidden="true">
    <form id="defaultNetworkInfoForm" action="POST">
        <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
            <ul class="progressStep_6">
                <li class="pass">기본 정보</li>
                <li class="active">네트워크 정보</li>
                <li class="before">Key 생성</li>
                <li class="before">리소스 정보</li>
                <li class="before">배포파일 정보</li>
                <li class="before install">설치</li>
            </ul>
        </div>
        <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
            <div class="panel panel-info" style="margin-bottom:10px;">    
                <div  class="panel-heading" style=""><b>External 네트워크</b></div>
                <div class="panel-body">
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">CF API TARGET IP</label> 
                        <div style=" width: 60%;">
                            <input name="publicStaticIp" type="text" style="display:inline-blcok; width:70%;" placeholder="예) 10.0.0.20"/>
                        </div>
                    </div>
                </div>
            </div>
            <div class="panel panel-info" id="defaultNetworkInfoDiv_1">
                <div  class="panel-heading" style="position:relative;">
                    <b>Internal 네트워크</b>
                    <div style="position: absolute;right: 0;top: 5px;">
                        <a class="btn btn-info btn-sm addInternal" onclick="addInternalNetworkInputs('#defaultNetworkInfoDiv_1', '#defaultNetworkInfoForm');">추가</a>
                    </div>
                </div>
                <div class="panel-body">
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">서브넷 아이디</label>
                        <div style=" width: 60%;">
                            <input name="subnetId_1" type="text"  style="display:inline-blcok; width:70%;" placeholder="서브넷 아이디를 입력하세요."/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">보안 그룹</label>
                        <div style=" width: 60%;">
                            <input name="cloudSecurityGroups_1" type="text"  style="display:inline-blcok; width:70%;" placeholder="예) bosh-security, cf-security"/>
                        </div>
                    </div>
                    <div class="w2ui-field" hidden="true" id="availabilityZoneDiv">
                        <label style="text-align: left;width:36%;font-size:11px;">가용 영역</label>
                        <div style=" width: 60%;">
                            <input name="availabilityZone_1" type="text"  style="display:inline-blcok; width:70%;" placeholder="예) us-west-2"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">서브넷 범위</label>
                        <div style=" width: 60%;">
                            <input name="subnetRange_1" type="text"  style="display:inline-blcok; width:70%;" placeholder="예) 10.0.0.0/24"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">게이트웨이</label>
                        <div style=" width: 60%;">
                            <input name="subnetGateway_1" type="text"  style="display:inline-blcok; width:70%;" placeholder="예) 10.0.0.1"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">DNS</label>
                        <div style=" width: 60%;">
                            <input name="subnetDns_1" type="text"  style="display:inline-blcok; width:70%;" placeholder="예) 8.8.8.8"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 36%; font-size: 11px;">IP할당 제외 대역</label>
                        <div style=" width: 60%;">
                            <input name="subnetReservedFrom_1" type="text" style="display:inline-block; width:32%;" placeholder="예) 10.0.0.100" />
                            <span style="width: 4%; text-align: center;">&nbsp;&ndash; &nbsp;</span>
                            <input name="subnetReservedTo_1"  type="text" style="display:inline-block; width:32%;" placeholder="예) 10.0.0.106" />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 36%; font-size: 11px;">IP할당 대역(최소 20개)</label>
                        <div style=" width: 60%;">
                            <input name="subnetStaticFrom_1"  type="text" style="display:inline-block; width:32%;" placeholder="예) 10.0.0.100" />
                            <span style="width: 4%; text-align: center;">&nbsp;&ndash; &nbsp;</span>
                            <input name="subnetStaticTo_1" type="text" style="display:iinline-block; width:32%;" placeholder="예) 10.0.0.106" />
                        </div>
                    </div>
                </div>
            </div>
            <!-- 추가 네트워크 div_1 -->
            <div  id="defaultNetworkInfoDiv_2" hidden="true"></div>
        </div>
    </form>
    <div class="w2ui-buttons" id="defaultNetworkInfoButtons" hidden="true">
        <button class="btn" style="float: left;" onclick="saveNetworkInfo('before', 'defaultNetworkInfoForm');" >이전</button>
        <button class="btn" style="float: right; padding-right: 15%" onclick="$('#defaultNetworkInfoForm').submit();" >다음>></button>
    </div>
</div>

<!-- google Network 설정 DIV -->
<div id="googleNetworkInfoDiv" style="width: 100%; height: 100%;" hidden="true">
    <form id="googleNetworkInfoForm">
        <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
            <ul class="progressStep_6">
                <li class="pass">기본 정보</li>
                <li class="active">네트워크 정보</li>
                <li class="before">Key 생성</li>
                <li class="before">리소스 정보</li>
                <li class="before">배포파일 정보</li>
                <li class="before install">설치</li>
            </ul>
        </div>
        <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
            <div class="panel panel-info" style="margin-bottom:10px;">    
                <div  class="panel-heading" style=""><b>External 네트워크</b></div>
                <div class="panel-body">
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">CF API TARGET IP</label> 
                        <div style=" width: 60%;">
                            <input name="publicStaticIp" type="text" style="display:inline-blcok; width:70%;" placeholder="예) 10.0.0.20"/>
                        </div>
                    </div>
                </div>
            </div>
            <div class="panel panel-info" id="googleNetworkInfoDiv_1">
                <div  class="panel-heading" style="position:relative;">
                    <b>Internal 네트워크</b>
                    <div style="position: absolute;right: 10px ;top: 2px; ">
                        <a class="btn btn-info btn-sm addInternal" onclick="addInternalNetworkInputs('#googleNetworkInfoDiv_1', '#googleNetworkInfoForm');">추가</a>
                    </div>
                </div>
                <div class="panel-body">
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">네트워크 명</label>
                        <div>
                            <input name="networkName_1" type="text" style="display:inline-blcok; width:70%;" placeholder="네트워크명을 입력하세요."/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">서브넷 명</label>
                        <div>
                            <input name="subnetId_1" type="text"  style="display:inline-blcok; width:70%;" placeholder="서브넷 아이디를 입력하세요."/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">방화벽 규칙</label>
                        <div>
                            <input name="cloudSecurityGroups_1" type="text"  style="display:inline-blcok; width:70%;" placeholder="예) internet, cf-security"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">영역</label>
                        <div>
                            <input name="availabilityZone_1" type="text"  style="display:inline-blcok; width:70%;" placeholder="예) asia-northeast1-a"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">서브넷 범위</label>
                        <div>
                            <input name="subnetRange_1" type="text"  style="display:inline-blcok; width:70%;" placeholder="예) 10.0.0.0/24"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">게이트웨이</label>
                        <div>
                            <input name="subnetGateway_1" type="text"  style="display:inline-blcok; width:70%;" placeholder="예) 10.0.0.1"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">DNS</label>
                        <div>
                            <input name="subnetDns_1" type="text"  style="display:inline-blcok; width:70%;" placeholder="예) 8.8.8.8"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 36%; font-size: 11px;">IP할당 제외 대역</label>
                        <div>
                            <input name="subnetReservedFrom_1" id="subnetReservedFrom_1" type="text" style="display:inline-block; width:32%;" placeholder="예) 10.0.0.100" />
                            <span style="width: 4%; text-align: center;">&nbsp;&ndash; &nbsp;</span>
                            <input name="subnetReservedTo_1" id="subnetReservedTo_1" type="text" style="display:inline-block; width:32%;" placeholder="예) 10.0.0.106" />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 36%; font-size: 11px;">IP할당 대역(최소 20개)</label>
                        <div>
                            <input name="subnetStaticFrom_1" id="subnetStaticFrom_1" type="text" style="display:inline-block; width:32%;" placeholder="예) 10.0.0.100" />
                            <span style="width: 4%; text-align: center;">&nbsp;&ndash; &nbsp;</span>
                            <input name="subnetStaticTo_1" id="subnetStaticTo_1" type="text" style="display:iinline-block; width:32%;" placeholder="예) 10.0.0.106" />
                        </div>
                    </div>
                </div>
            </div>
            <!-- 추가 네트워크 div_1 -->
            <div  id="googleNetworkInfoDiv_2" hidden="true"></div>
        </div>
    </form>
    <div class="w2ui-buttons" id="googleNetworkInfoButtons" hidden="true">
        <button class="btn" style="float: left;" onclick="saveNetworkInfo('before', 'googleNetworkInfoForm');" >이전</button>
        <button class="btn" style="float: right; padding-right: 15%" onclick="$('#googleNetworkInfoForm').submit();" >다음>></button>
    </div>
</div>

<!-- vSphere Network -->
<div id="VsphereNetworkInfoDiv" style="width:100%;height:100%;" hidden="true">
    <form id="vSphereNetworkInfoForm">
        <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
            <ul class="progressStep_6" >
                <li class="pass">기본 정보</li>
                <li class="active">네트워크 정보</li>
                <li class="before">Key 생성</li>
                <li class="before">리소스 정보</li>
                <li class="before">배포파일 정보</li>
                <li class="before install">설치</li>
            </ul>
        </div>
        <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
            <!-- External -->
            <div class="panel panel-info">
                <div  class="panel-heading"><b>External 네트워크</b></div>
                <div class="panel-body">
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">포트 그룹명</label>
                        <div style=" width: 60%;">
                            <input name="publicSubnetId" type="text"  style="diplay:inline-block;width:70%;" placeholder="포트 그룹명을 입력하세요."/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">서브넷 범위</label>
                        <div style=" width: 60%;">
                            <input name="publicSubnetRange" type="text"  style="diplay:inline-block;width:70%;" placeholder="예) 10.0.0.0/24"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">게이트웨이</label>
                        <div style=" width: 60%;">
                            <input name="publicSubnetGateway" type="text" style="diplay:inline-block;width:70%;" placeholder="예) 10.0.0.1"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">DNS</label>
                        <div style=" width: 60%;">
                            <input name="publicSubnetDns" type="text" style="diplay:inline-block;width:70%;" placeholder="예) 8.8.8.8"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">IP할당 대역</label> 
                        <div style=" width: 60%;">
                            <input name="publicStaticFrom" type="text" style="display:inline-block;width:32%;" placeholder="예) 10.0.0.100" />
                            <span style="width: 4%; text-align: center;">&nbsp;&ndash; &nbsp;</span>
                            <input name="publicStaticTo" type="text" style="display:inline-block;width:32%;" placeholder="예) 10.0.0.106" />
                        </div>
                    </div>
                </div>
            </div>
            <!-- Internal -->
            <div class="panel panel-info" style="margin-top:2%;" id="vSphereNetworkInfoDiv_1">
                <div  class="panel-heading" style="position:relative;">
                    <b>Internal 네트워크</b>
                    <div style="position: absolute;right: 10px ;top: 2px; ">
                        <a class="btn btn-info btn-sm addInternal" onclick="addInternalNetworkInputs('#vSphereNetworkInfoDiv_1', '#vSphereNetworkInfoForm');">추가</a>
                    </div>
                </div>
                <div class="panel-body">
                    <div class="w2ui-field">
                        <label class="subnetId" style="text-align: left;width:36%;font-size:11px;">포트 그룹명</label>
                        <div style=" width: 60%;">
                            <input name="subnetId_1" type="text" style="display:inline-block;width:70%;" placeholder="포트 그룹명을 입력하세요."/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">서브넷 범위</label>
                        <div style=" width: 60%;">
                            <input name="subnetRange_1" type="text" style="display:inline-block;width:70%;" placeholder="예) 10.0.0.0/24"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">게이트웨이</label>
                        <div style=" width: 60%;">
                            <input name="subnetGateway_1" type="text" style="display:inline-block;width:70%;" placeholder="예) 10.0.0.1"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:36%;font-size:11px;">DNS</label>
                        <div style=" width: 60%;">
                            <input name="subnetDns_1" type="text" style="display:inline-block;width:70%;" placeholder="예) 8.8.8.8"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 36%; font-size: 11px;">IP할당 제외 대역</label>
                        <div style=" width: 60%;">
                            <input name="subnetReservedFrom_1" type="text" style="display:inline-block;width:32%;" placeholder="예) 10.0.0.100" />
                            <span style="width: 4%; text-align: center;">&nbsp;&ndash; &nbsp;</span>
                            <input name="subnetReservedTo_1" type="text" style="display:inline-block;width:32%;" placeholder="예) 10.0.0.106" />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 36%; font-size: 11px;">IP할당 대역(최소 20개)</label>
                        <div style=" width: 60%;">
                            <input name="subnetStaticFrom_1" type="text" style="display:inline-block;width:32%;" placeholder="예) 10.0.0.100" />
                            <span style="width: 4%; text-align: center;">&nbsp;&ndash; &nbsp;</span>
                            <input name="subnetStaticTo_1" type="text" style="display:inline-block;width:32%;" placeholder="예) 10.0.0.106" />
                        </div>
                    </div>
                </div>
            </div>
            <!-- 추가 네트워크 div_1 -->
            <div  id="vSphereNetworkInfoDiv_2" hidden="true"></div>
        </div>
    </form>
    <div class="w2ui-buttons" id="VsphereNetworkInfoButtons" hidden="true">
        <button class="btn" style="float: left;" onclick="saveNetworkInfo('before', 'vSphereNetworkInfoForm');" >이전</button>
        <button class="btn" style="float: right; padding-right: 15%" onclick="$('#vSphereNetworkInfoForm').submit();" >다음>></button>
    </div>
</div>

<!--  Key 생성 Div -->
<div id="KeyInfoDiv" style="width: 100%; height: 100%;" hidden="true">
    <form id="KeyInfoForm">
        <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
            <ul class="progressStep_6">
                <li class="pass">기본 정보</li>
                <li class="pass">네트워크 정보</li>
                <li class="active">Key 생성</li>
                <li class="before">리소스 정보</li>
                <li class="before">배포파일 정보</li>
                <li class="before install">설치</li>
            </ul>
        </div>
        <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
            <div class="panel panel-info">    
                <div class="panel-heading"><b>Key 생성 정보</b></div>
                <div class="panel-body" style="padding:5px 5% 10px 5%;">
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 40%; font-size: 11px;">도메인</label>
                        <div style=" width: 60%;">
                            <input name="cfDomain" type="text" id="cfDomain" style="display:inline-block; width: 75%;" readonly placeholder="도메인을 입력하세요." />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 40%; font-size: 11px;">국가 코드</label>
                        <div style=" width: 60%;">
                            <select name="countryCode" id="countryCode" style="display:inline-block; width:75%;"></select>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 40%; font-size: 11px;">시/도</label>
                        <div style=" width: 60%;">
                            <input name="stateName" type="text" id="stateName" style="display:inline-block; width: 75%;" placeholder="시/도를 선택하세요." />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 40%; font-size: 11px;">시/구/군</label>
                        <div style=" width: 60%;">
                            <input name="localityName" type="text" id="localityName" style="display:inline-block; width: 75%;" placeholder="시/구/군을 입력하세요." />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 40%; font-size: 11px;">회사명</label>
                        <div style=" width: 60%;">
                            <input name="organizationName" type="text" id="organizationName" style="display:inline-block; width: 75%;" placeholder="회사명을 입력하세요." />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 40%; font-size: 11px;">부서명</label>
                        <div style=" width: 60%;">
                            <input name="unitName" type="text" id="unitName" style="display:inline-block; width: 75%;" placeholder="부서명을 입력하세요." />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 40%; font-size: 11px;">Email</label>
                        <div style=" width: 60%;">
                            <input name="email" type="text" id="email" style="float: left; width: 75%;"  placeholder="Email을 입력하세요." />
                        </div>
                    </div>
                </div>
                <span class="btn btn-info btn-sm" style="float: right; margin-top:10px;"  onclick="createKeyConfirm();">Key 생성</span>
            </div>
        </div>
        <div class="w2ui-buttons" id="KeyInfoButtonsDiv" hidden="true">
            <button class="btn" style="float: left;" onclick="saveKeyInfo('before');" >이전</button>
            <button class="btn" style="float: right; padding-right: 15%" onclick="$('#KeyInfoForm').submit();">다음>></button>
        </div>
    </form>
</div>

<!-- Resource  설정 DIV -->
<div id="resourceInfoDiv" style="width: 100%; height: 100%;" hidden="true">
    <form id="resourceInfoForm">
        <div style="margin-left:2%;display:inline-block;width:97%;padding-top:15px;">
            <ul class="progressStep_6">
                <li class="pass">기본 정보</li>
                <li class="pass">네트워크 정보</li>
                <li class="pass">Key 생성</li>
                <li class="active">리소스 정보</li>
                <li class="before">배포파일 정보</li>
                <li class="before install">설치</li>
            </ul>
        </div>
        <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
            <div class="panel panel-info">    
                <div class="panel-heading" style="position:relative"><b>리소스 정보</b>
                    <div style="position: absolute;right: 10px ;top: 2px; ">
                        <a class="btn btn-info btn-sm" onclick="resourceJobSettingsPop();">고급 기능</a>
                    </div>
                </div>
                
                <div class="panel-body" style="padding:5px 5% 10px 5%;">
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 30%; font-size: 11px;">Stemcell</label>
                        <div style=" width: 60%;">
                            <div>
                                <select name="stemcells" style="display:inline-block; width: 80%;"></select>
                            </div>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 30%; font-size: 11px;">VM 비밀번호</label>
                        <div style=" width: 60%;">
                            <input name="boshPassword"  type="text" style="display:inline-block; width: 80%;" placeholder="VM 비밀번호를 입력하세요." />
                        </div>
                    </div>
                </div>
            </div>
            <div class="panel panel-info" style="margin-top:10px;">
                <div class="panel-heading"><b>Small Resource Type</b></div>
                <div class="panel-body"  style="padding:5px 5% 10px 5%;">
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 30%; font-size: 11px;">Flavor</label>
                        <div style=" width: 60%;">
                            <input name="smallFlavor" type="text" style="display:inline-block; width: 80%;" placeholder="Small Flavor Type을 입력하세요."  />
                        </div>
                    </div>
                </div>
            </div>
            <div class="panel panel-info" style="margin-top:10px;">
                <div class="panel-heading"><b>Medium Resource Type</b></div>
                <div class="panel-body"  style="padding:5px 5% 10px 5%;" >
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 30%; font-size: 11px;">Flavor</label>
                        <div style=" width: 60%;">
                            <input name="mediumFlavor" type="text" style="display:inline-block; width: 80%;" placeholder="Medium Flavor Type을 입력하세요."  />
                        </div>
                    </div>
                </div>
            </div>
            <div class="panel panel-info" style="margin-top:10px;">
                <div class="panel-heading"><b>Large Resource Type</b></div>
                <div class="panel-body"  style="padding:5px 5% 10px 5%;">
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 30%; font-size: 11px;">Flavor</label>
                        <div style=" width: 60%;">
                            <input name="largeFlavor" type="text" style="display:inline-block; width: 80%;" placeholder="Large Flavor Type을 입력하세요."  />
                        </div>
                    </div>
                </div>
            </div>
            <div class="panel panel-info" style="margin-top:10px;" id="runnerTypeDiv">
                <div class="panel-heading"><b>Runner Resource Type</b></div>
                <div class="panel-body"  style="padding:5px 5% 10px 5%;">
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 30%; font-size: 11px;">Flavor</label>
                        <div style=" width: 60%;">
                            <input name="runnerFlavor" type="text" style="display:inline-block; width: 80%;" placeholder="Runner Flavor Type을 입력하세요."  />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form>
    <div class="w2ui-buttons" id="resourceInfoButtons" hidden="true">
        <button class="btn" style="float: left;" onclick="saveResourceInfo('before');">이전</button>
        <button class="btn" style="float: right; padding-right: 15%" onclick="$('#resourceInfoForm').submit();">다음>></button>
    </div>
</div>

<!--  vSphere Resource -->
<div id="vSphereResourceInfoDiv" style="width: 100%; height: 100%;" hidden="true">
    <form id="vSphereResourceInfoForm">
        <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
            <ul class="progressStep_6">
                <li class="pass">기본 정보</li>
                <li class="pass">네트워크 정보</li>
                <li class="pass">Key 생성</li>
                <li class="active">리소스 정보</li>
                <li class="before">배포파일 정보</li>
                <li class="before install">설치</li>
            </ul>
        </div>
        <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
            <div class="panel panel-info">    
                <div class="panel-heading" style="position:relative"><b>리소스 정보</b>
                    <div style="position: absolute;right: 10px ;top: 2px; ">
                        <a class="btn btn-info btn-sm" onclick="resourceJobSettingsPop();">고급 기능</a>
                    </div>
                </div>
                <div class="panel-body" style="padding:5px 5% 10px 5%;">
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 36%; font-size: 11px;">Stemcell</label>
                        <div style=" width: 60%;">
                            <div>
                                <select name="stemcells" style="width: 70%;display: inline-block;"></select>
                            </div>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 36%; font-size: 11px;">VM 비밀번호</label>
                        <div style=" width: 60%;">
                            <input name="boshPassword" type="text" style="display:inline-block; width: 80%;" placeholder="VM 비밀번호를 입력하세요." />
                        </div>
                    </div>
                </div>
            </div>
            <div class="panel panel-info" style="margin-top:20px;">
                <div class="panel-heading"><b>Small Resource Type</b></div>
                <div class="panel-body"  style="padding:5px 5% 10px 5%;">
                    <div class="w2ui-field">
                        <label style="text-align: left;  width: 36%;">Small Type Ram</label>
                        <div style=" width: 60%;">
                            <input name="smallFlavorRam" type="text" style="display:inline-block; width: 80%;" onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'   placeholder="Ram을 입력하세요. 예) 1024"  />
                        </div>
                        <label style="text-align: left;  width: 36%; ">Small Type Disk</label>
                        <div style=" width: 60%;">
                            <input name="smallFlavorDisk" type="text" style="display:inline-block; width: 80%;" onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  placeholder="Disk를 입력하세요. 예) 4096"  />
                        </div>
                        <label style="text-align: left;  width: 36%;">Small Type Cpu</label>
                        <div style=" width: 60%;">
                            <input name="smallFlavorCpu" type="text" style="display:inline-block; width: 80%;" onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;' placeholder="Cpu를 입력하세요. 예) 1"  />
                        </div>
                    </div>
                </div>
            </div>
            <div class="panel panel-info" style="margin-top:20px;">
                <div class="panel-heading"><b>Medium Resource Type</b></div>
                <div class="panel-body"  style="padding:5px 5% 10px 5%;" >
                    <div class="w2ui-field">
                        <label style="text-align: left;  width: 36%;">Medium Ram</label>
                        <div style=" width: 60%;">
                            <input name="mediumFlavorRam" type="text" style="display:inline-block; width: 80%;" onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;' placeholder="Ram을 입력하세요. 예) 1024"  />
                        </div>
                        <label style="text-align: left;  width: 36%;">Medium Disk</label>
                        <div style=" width: 60%;">
                            <input name="mediumFlavorDisk" type="text" style="display:inline-block; width: 80%;" onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;' placeholder="Disk를 입력하세요. 예) 4096"  />
                        </div>
                        <label style="text-align: left;  width: 36%;">Medium Cpu</label>
                        <div style=" width: 60%;">
                            <input name="mediumFlavorCpu" type="text" style="display:inline-block; width: 80%;" onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;' placeholder="Cpu를 입력하세요. 예) 1"  />
                        </div>
                    </div>
                </div>
            </div>
            <div class="panel panel-info" style="margin-top:20px;">
                <div class="panel-heading"><b>Large Resource Type</b></div>
                <div class="panel-body"  style="padding:5px 5% 10px 5%;">
                    <div class="w2ui-field">
                        <label style="text-align: left;  width: 36%;">Large Ram</label>
                        <div style=" width: 60%;">
                            <input name="largeFlavorRam" type="text" style="display:inline-block; width: 80%;" onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;' placeholder="Ram을 입력하세요. 예) 1024"  />
                        </div>
                        <label style="text-align: left;  width: 36%;">Large Disk</label>
                        <div style=" width: 60%;">
                            <input name="largeFlavorDisk" type="text" style="display:inline-block; width: 80%;" onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;' placeholder="Disk를 입력하세요. 예) 10240 "  />
                        </div>
                        <label style="text-align: left;  width: 36%;">Large Cpu</label>
                        <div style=" width: 60%;">
                            <input name="largeFlavorCpu" type="text" style="display:inline-block; width: 80%;" onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;' placeholder="Cpu를 입력하세요. 예) 1"  />
                        </div>
                    </div>
                </div>
            </div>
            <div class="panel panel-info" id="runnerTypeDiv" style="margin-top:20px;">
                <div class="panel-heading"><b>Runner Resource Type</b></div>
                <div class="panel-body"  style="padding:5px 5% 10px 5%;">
                    <div class="w2ui-field">
                        <label style="text-align: left;  width: 36%;">Runner Ram</label>
                        <div style=" width: 60%;">
                            <input name="runnerFlavorRam" type="text" style="display:inline-block; width: 80%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;' placeholder="Ram을 입력하세요. 예) 16384"  />
                        </div>
                        <label style="text-align: left;  width: 36%;">Runner Disk</label>
                        <div style=" width: 60%;">
                            <input name="runnerFlavorDisk" type="text" style="display:inline-block; width: 80%;" onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;' placeholder="Disk를 입력하세요. 예) 32768"  />
                        </div>
                        <label style="text-align: left;  width: 36%;">Runner Cpu</label>
                        <div style=" width: 60%;">
                            <input name="runnerFlavorCpu" type="text" style="display:inline-block; width: 80%;" onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;' placeholder="Cpu를 입력하세요. 예) 2"  />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form>
    <div class="w2ui-buttons" id="vSphereResourceInfoButtons" hidden="true">
        <button class="btn" style="float: left;" onclick="saveResourceInfo('before');">이전</button>
        <button class="btn" style="float: right; padding-right: 15%" onclick="$('#vSphereResourceInfoForm').submit();">다음>></button>
    </div>
</div>

<!-- CF 고급 설정 화면 -->
<div class="w2ui-buttons" id="cfDetailButtons" hidden="true">
    <button class="btn" id="" style="" onclick="saveCfJobsInfo();">저장</button>
    <button class="btn" style="" onclick="jobPopupComplete();">닫기</button>
</div>
    
<div id="cfDetailPopDiv" hidden="true" style="width:100%;">
    <form id="cfDetailForm" style="height:100%;">
    </form>
</div>

<!-- 배포파일 정보 -->
<div id="deployDiv" style="width: 100%; height: 100%;" hidden="true">
    <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
        <ul class="progressStep_6">
            <li class="pass">기본 정보</li>
            <li class="pass">네트워크 정보</li>
            <li class="pass">Key 생성</li>
            <li class="pass">리소스 정보</li>
            <li class="active">배포파일 정보</li>
            <li class="before install">설치</li>
        </ul>
    </div>
    <div style="width:95%;height:82%;float:left;display: inline-block;margin-top:1%;">
        <textarea id="deployInfo" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:3%;" readonly="readonly"></textarea>
    </div>
    <div class="w2ui-buttons" id="deployDivButtons" hidden="true">
        <button class="btn" style="float: left;" onclick="cfDeploy('before');">이전</button>
        <button class="btn" style="float: right; padding-right: 15%" onclick="cfDeploy('after');">다음>></button>
    </div>
</div>

<!-- 설치화면 -->
<div id="installDiv" style="width: 100%; height: 100%;" hidden="true">
    <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
        <ul class="progressStep_6">
            <li class="pass">기본 정보</li>
            <li class="pass">네트워크 정보</li>
            <li class="pass">Key 생성</li>
            <li class="pass">리소스 정보</li>
            <li class="pass">배포파일 정보</li>
            <li class="active install">설치</li>
        </ul>
    </div>
    <div style="width:95%;height:84%;float: left;display: inline-block;margin-top:1%;">
        <textarea id="installLogs" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:3%;" readonly="readonly"></textarea>
    </div>
    <div class="w2ui-buttons" id="installButtons" hidden="true">
        <button class="btn" id="deployPopupBtn" style="float: left;" onclick="deployPopup()" disabled>이전</button>
        <button class="btn" style="float: right; padding-right: 15%" onclick="popupComplete();">닫기</button>
    </div>
</div>
<!-- End Popup -->
<script type="text/javascript" src="<c:url value='/js/rules/cf/cf_default.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/rules/cf/cf_network_default.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/rules/cf/cf_network_vsphere.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/rules/cf/cf_network_google.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/rules/cf/cf_keyinfo.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/rules/cf/cf_resource.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/rules/cf/cf_vSphereResource.js'/>"></script>
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