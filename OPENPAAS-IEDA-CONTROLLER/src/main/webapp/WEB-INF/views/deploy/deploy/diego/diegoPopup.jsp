<%
/* =================================================================
 * 상세설명 : Diego 설치
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2016.10       이동현           화면 수정 및 vSphere 클라우드 기능 추가
 * 2016.12       이동현           Diego 목록과 팝업 화면 .jsp 분리 및 설치 버그 수정 
 * 2017.09       이동현           화면 수정 및 Gcp 클라우드 기능 추가
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix = "spring" uri = "http://www.springframework.org/tags" %>
<style>
    .popover-content {
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
var deploy_type = '<spring:message code="common.deploy.type.diego.name"/>';//CF DEPLOY TYPE VALUE
var country_parent_code = '<spring:message code="common.code.country.code.parent"/>';//ieda_common_code country 조회

//setting variable
var diegoId = "";
var networkId = "";
var defaultInfo = "";    
var cfInfo = "";
var diegoInfo = "";
var etcdInfo = "";
var peerInfo = "";
var jobsInfo=[];
var networkInfo = new Array();
var publicStaticIp = "";
var internalCnt=1;
var resourceInfo = "";
var diegoReleases = new Array();
var cfReleases = new Array();
var gardenReleaseName = new Array();
var etcdReleases = new Array();
var cflinuxfs2rootfsrelease = new Array();
var cfInfo = new Array();
var stemcells = new Array();
var deploymentFile = "";
var installStatus = "";
var installClient ="";
var modifyNetWork = "";
var cfInfoYn = false;
var diegoKeyFile = "";

$(function() {
     $(document).delegate(".w2ui-popup","click",function(e){
     $('[data-toggle="popover"]').each(function () {
            //the 'is' for buttons that trigger popups
            //the 'has' for icons within a button that triggers a popup
            if (!$(this).is(e.target) && $(this).has(e.target).length === 0 && $('.popover').has(e.target).length === 0) {
                $(this).popover('hide');
           }
        });
    });
});

/********************************************************
 * 설명 : Diego 고급 설정 레이아웃 추가
 *********************************************************/
var pstyle = 'width:615px;height:595;';
var config = {
      layout2: {
          name: 'layout2',
          padding: 4,
          panels: [
              { type: 'left', size: 200, hidden: true, content: 'left' },
              {type: 'main', style:pstyle }
          ]
      }
 };
 
/********************************************************
 * 설명 : Diego 고급 설정 레이아웃 초기화
 *********************************************************/
$(function () {
    // initialization in memory
    $().w2layout(config.layout2);
});

/********************************************************
 * 설명 :  Diego 릴리즈 설치 지원 버전 목록 조회
 * 기능 : getReleaseVersionList
 *********************************************************/
function getReleaseVersionList(){
    var contents = "";
    $.ajax({
        type :"GET",
        url :"/common/deploy/list/releaseInfo/diego/"+iaas, 
        contentType :"application/json",
        success :function(data, status) {
            if (data != null && data != "") {
                contents = "<table id='popoverTable'><tr><th>릴리즈 유형</th><th>릴리즈 버전</th></tr>";
                data.map(function(obj) {
                    contents += "<tr><td>" + obj.releaseType+ "</td><td>" +  obj.minReleaseVersion +"</td></tr>";
                });
                contents += "</table>";
                $('.diego-info').attr('data-content', contents);
            }
        },
        error :function(request, status, error) {
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "DIEGO 릴리즈 정보 목록 조회");
        }
    });
}

/********************************************************
 * 설명 :  Diego 수정 - 데이터 조회
 * 기능 : getDiegoData
 *********************************************************/
function getDiegoData(record) {
    var url = "/deploy/"+menu+"/install/detail/" + record.id;
    $.ajax({
        type :"GET",
        url :url,
        contentType :"application/json",
        success :function(data, status) {
            if (data != null && data != "") {
                setDiegoData(data.content);
                defaultPopup();
            }
        },
        error :function(request, status, error) {
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "DIEGO 수정");
        }
    });
}


/********************************************************
 * 설명 :  Diego Data Setting
 * 기능 : setDiegoData
 *********************************************************/
function setDiegoData(contents) {
    if( menu == "cfDiego" ) {
        contents = contents.diegoVo;
    }
    diegoId = contents.id;
    iaas = contents.iaasType;
    if( contents.networks.length > 1){
        internalCnt = contents.networks.length-1;
        if( menu == "cfDiego" ) internalCnt = contents.networks.length;
    }
    defaultInfo = {
            iaas                            : contents.iaasType,
            cfId                            : cfId,
            deploymentName                  : contents.deploymentName,
            directorUuid                    : contents.directorUuid,
            diegoReleaseName                : contents.diegoReleaseName,
            diegoReleaseVersion             : contents.diegoReleaseVersion,
            gardenReleaseName               : contents.gardenReleaseName,
            gardenReleaseVersion            : contents.gardenReleaseVersion,
            etcdReleaseName                 : contents.etcdReleaseName,
            etcdReleaseVersion              : contents.etcdReleaseVersion,
            cfDeploymentName                : contents.cfName,
            cflinuxfs2rootfsreleaseName     : contents.cflinuxfs2rootfsreleaseName,
            cflinuxfs2rootfsreleaseVersion  : contents.cflinuxfs2rootfsreleaseVersion,
            paastaMonitoringUse             : contents.paastaMonitoringUse,
            cadvisorDriverIp                : contents.cadvisorDriverIp,
        }
        //네트워크 정보 설정
        for(var i=0; i<contents.networks.length; i++){
             var arr = {
                id                          : contents.id,
                deployType                  : contents.networks[i].deployType,
                seq                         : i,
                net                         : contents.networks[i].net,
                networkName                 : contents.networks[i].networkName,
                publicStaticIp              : contents.networks[i].publicStaticIp,
                subnetRange                 : contents.networks[i].subnetRange,
                subnetGateway               : contents.networks[i].subnetGateway,
                subnetDns                   : contents.networks[i].subnetDns,
                subnetReservedFrom          : contents.networks[i].subnetReservedFrom,
                subnetReservedTo            : contents.networks[i].subnetReservedTo,
                subnetStaticFrom            : contents.networks[i].subnetStaticFrom,
                subnetStaticTo              : contents.networks[i].subnetStaticTo,
                subnetId                    : contents.networks[i].subnetId,
                cloudSecurityGroups         : contents.networks[i].cloudSecurityGroups,
                availabilityZone            : contents.networks[i].availabilityZone
            }
             networkInfo.push(arr);
        }
        internalCnt = networkInfo.length;
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
        
        //리소스 정보 설정
        if(contents.resource != null && contents.resource != ""){
            //기본 정보 설정
            diegoKeyFile = contents.keyFile
            resourceInfo = {
                    id                     : contents.id,
                    stemcellName           : contents.resource.stemcellName,
                    stemcellVersion        : contents.resource.stemcellVersion,
                    boshPassword           : contents.resource.boshPassword,
                    smallFlavor            : contents.resource.smallFlavor,
                    mediumFlavor           : contents.resource.mediumFlavor,
                    largeFlavor            : contents.resource.largeFlavor,
                    runnerFlavor           : contents.resource.runnerFlavor,
                    smallRam               : contents.resource.smallRam,
                    smallDisk              : contents.resource.smallDisk,
                    smallCpu               : contents.resource.smallCpu,
                    mediumRam              : contents.resource.mediumRam,
                    mediumDisk             : contents.resource.mediumDisk,
                    mediumCpu              : contents.resource.mediumCpu,
                    largeRam               : contents.resource.largeRam,
                    largeDisk              : contents.resource.largeDisk,
                    largeCpu               : contents.resource.largeCpu,
                    runnerRam              : contents.resource.runnerRam,
                    runnerDisk             : contents.resource.runnerDisk,
                    runnerCpu              : contents.resource.runnerCpu
                }
        }
}

/********************************************************
 * 설명 :  기본정보 팝업
 * 기능 : defaultPopup
 *********************************************************/
function defaultPopup() {
    $("#defaultInfoDiv").w2popup({
        title : "<b>DIEGO 설치</b>",
        width : 750,
        height :560,
        modal :true,
        body    : $("#defaultInfoDiv").html(),
        buttons : $("#defaultInfoButtonDiv").html(),
        showMax :false,
        onOpen :function(event) {
            event.onComplete = function() {
                //릴리즈 정보 popup over
                 $('[data-toggle="popover"]').popover();
                 //Diego Release Info
                 getReleaseVersionList();
                 
                 $(".gardenRelease-info").attr('data-content', "https://github.com/cloudfoundry/diego-cf-compatibility");
                 $(".cflinux-info").attr('data-content', "https://github.com/cloudfoundry/diego-cf-compatibility");
                 $(".etcd-info").attr('data-content', "https://github.com/cloudfoundry/diego-cf-compatibility");
                 $(".paastaMonitoring-info").attr('data-content', "paasta-container v3.0 이상에서 지원")
                 
                if( menu == "cfDiego") {
                    $('.w2ui-msg-buttons #defaultPopupBtn').show();
                }
                
                if (defaultInfo != "") {
                    $(".w2ui-msg-body input[name='deploymentName']").val(defaultInfo.deploymentName);
                    $(".w2ui-msg-body input[name='directorUuid']").val(defaultInfo.directorUuid);
                    $(".w2ui-msg-body input[name='cfId']").val(cfId);
                    if( !checkEmpty(defaultInfo.cadvisorDriverIp) ){//PaaS-TA 모니터링 체크
                        $(".w2ui-msg-body input:checkbox[name='paastaMonitoring']").attr("checked", true);
                        checkPaasTAMonitoringUseYn();
                        $(".w2ui-msg-body input[name='cadvisorDriverIp']").val(defaultInfo.cadvisorDriverIp);
                    }
                    if(compare( defaultInfo.diegoReleaseVersion, "1.2.0" ) > 0){
                        $('.w2ui-msg-body #etcd').css('display','none');
                        $(".w2ui-msg-body input[name='etcdReleases']").val("");
                    } else {
                        $('.w2ui-msg-body #etcd').css('display','block');
                    }
                }else{
                    if( !checkEmpty($("#directorUuid").text()) ){
                        $(".w2ui-msg-body input[name='directorUuid']").val($("#directorUuid").text());
                    }
                }
                getReleases();//릴리즈 조회    
            }
        },
        onClose :function(event) {
            event.onComplete = function() {
                initSetting();
            }
        }
    });
}

/********************************************************
 * 설명 : Diego 설치 릴리즈 목록 조회 
 * 기능 : getReleases
 *********************************************************/
function getReleases(){
    cfInfo = new Array(); //CF  릴리즈
    etcdReleases = new Array(); //ETCD 릴리즈
    gardenReleaseName = new Array(); //Garden-Linux 릴리즈
    diegoReleases = new Array(); //DIEGO 릴리즈
    stemcells = new Array(); //STEMCELL
    //화면 LOCK
    w2popup.lock("릴리즈를 조회 중입니다.", true);
    getCfRelease(); //Diego 릴리즈 조회
}

/********************************************************
 * 설명 : Diego 릴리즈 조회
 * 기능 : getDiegoRelease
 *********************************************************/
function getDiegoRelease() {
    $.ajax({
        type :"GET",
        url :"/common/deploy/release/list/diego",
        contentType :"application/json",
        async :true,
        success :function(data, status) {
            diegoReleases = new Array();
            if( data.records != null){
                var option = "<option value=''>Diego 릴리즈를 선택하세요.</option>";
                data.records.map(function(obj) {
                    if( defaultInfo.diegoReleaseName == obj.name && defaultInfo.diegoReleaseVersion == obj.version){
                        option += "<option value='"+obj.name+"/"+obj.version+"' selected>"+obj.name+"/"+obj.version+"</option>";
                    }else{
                        option += "<option value='"+obj.name+"/"+obj.version+"'>"+obj.name+"/"+obj.version+"</option>";
                    }
                });
            }
            $(".w2ui-msg-body select[name='diegoReleases']").html(option);
            getcflinuxfs2RootfsRelease();
        },
        error :function(e, status) {
            w2alert("Diego Release List 를 가져오는데 실패하였습니다.", "DIEGO 설치");
        }
    });
}


/********************************************************
 * 설명 : cflinuxfs2Rootfs 릴리즈 조회 
 * 기능 : getcflinuxfs2RootfsRelease
 *********************************************************/
function getcflinuxfs2RootfsRelease(){
    $.ajax({
        type :"GET",
        url :"/common/deploy/release/list/cflinuxfs2-rootfs",
        contentType :"application/json",
        async :true,
        success :function(data, status) {
            cflinuxfs2rootfsrelease = new Array();
            if( data.records != null){
                var option = "<option value=''>Cf-linuxfs2 릴리즈를 선택하세요.</option>";
                data.records.map(function(obj) {
                    if( defaultInfo.cflinuxfs2rootfsreleaseName == obj.name && defaultInfo.cflinuxfs2rootfsreleaseVersion == obj.version){
                        option += "<option value='"+obj.name+"/"+obj.version+"' selected>"+obj.name+"/"+obj.version+"</option>";
                        $('.w2ui-msg-body #cflinux').css('display','block');
                    }else{
                        option += "<option value='"+obj.name+"/"+obj.version+"'>"+obj.name+"/"+obj.version+"</option>";    
                    }
                });
            }
            $(".w2ui-msg-body select[name='cflinuxfs2rootfsrelease']").html(option);
            getgardenRelease();
        },
        error :function(e, status) {
            w2popup.unlock();
            w2alert("getcflinuxfs2RootfsRelease List 를 가져오는데 실패하였습니다.", "DIEGO 설치");
        }
    });
}


/********************************************************
 * 설명 : CF 정보 조회
 * 기능 : getCfRelease
 *********************************************************/
var arrayCFInfoJSON = [];
function getCfRelease() {
    $.ajax({
        type :"GET",
        url :"/deploy/"+menu+"/list/cf/"+iaas+"",
        contentType :"application/json",
        async :true,
        success :function(data, status) {
            if(data.records.length==0){
                var option = "<option value=''>Diego와 연동할 CF가 존재 하지 않습니다..</option>";
                $(".w2ui-msg-body select[name='cfInfo']").html(option);
                cfInfoYn = true;
            }
            cfInfo = new Array();
            if(data.records != null){
                var option = "";
                option += '<option value="">Diego와 연동하여 배포 할 CF정보를 선택 하세요.</option>';
                data.records.map(function(obj) {
                    var getCfInfo =  $(".w2ui-msg-body #getCfInfo");
                    if( menu == "cfDiego"  ){
                        if(  cfId == obj.id) {
                            option += '<option selected value="'+obj.deploymentName+'">'+obj.deploymentName+'</option>';
                            $(".w2ui-msg-body select[name='cfInfo']").html(option);
                            $(".w2ui-msg-body input[name='cfId']").val(obj.id);
                            $(".w2ui-msg-body input[name='cfDeploymentFile']").val(obj.deploymentFile);
                            $(".w2ui-msg-body input[name='deploymentName']").val(obj.deploymentName+"-diego");
                            $(".w2ui-msg-body input[name='cfReleaseVersion']").val(obj.releaseVersion);
                            if(obj.releaseVersion > 271){
                                $(".w2ui-msg-body input[name='cfKeyFile']").val(obj.keyFile);
                            }
                        }
                    } else{
                        if(obj.diegoYn=="true") {
                            cfInfo.push(obj.deploymentName);
                            if(obj.deploymentName == defaultInfo.cfDeploymentName){
                                option += '<option selected value="'+obj.deploymentName+'">'+obj.deploymentName+'</option>';
                            }else{
                                option += '<option value="'+obj.deploymentName+'">'+obj.deploymentName+'</option>';
                            }
                            $(".w2ui-msg-body input[name='cfReleaseVersion']").val(obj.releaseVersion);
                            $(".w2ui-msg-body select[name='cfInfo']").html(option);
                            if(obj.releaseVersion > 271){
                                $(".w2ui-msg-body input[name='cfKeyFile']").val(obj.keyFile);
                            }
                            arrayCFInfoJSON=data;
                        }
                    }
                });
            }
            getDiegoRelease();
            if( defaultInfo != "" ){
                setCfDeployFile(defaultInfo.cfDeploymentName);
            }
        },
        error :function(e, status) {
            w2alert("CF 정보를 가져오는데 실패하였습니다.", "DIEGO 설치");
        }
    });
}


/********************************************************
 * 설명 : CF 배포 파일 설정
 * 기능 : setCfDeployFile
 *********************************************************/
function setCfDeployFile(value){
    var cf_id;
    var cfDeploymentFile;
    var cfReleaseVersion;
    var cfKey;
    for(var i=0;i<arrayCFInfoJSON.records.length;i++){
        if(value==arrayCFInfoJSON.records[i].deploymentName){
            cf_id = arrayCFInfoJSON.records[i].id;
            cfDeploymentFile = arrayCFInfoJSON.records[i].deploymentFile;
            cfReleaseVersion = arrayCFInfoJSON.records[i].releaseVersion;
            if(cfReleaseVersion > 271){
                cfKey = arrayCFInfoJSON.records[i].keyFile;
            }
            break;
        }
    }
    $(".w2ui-msg-body input[name='cfReleaseVersion']").val(cfReleaseVersion);
    $(".w2ui-msg-body input[name='deploymentName']").val(value+"-diego");
    $(".w2ui-msg-body input[name='cfId']").val(cf_id);
    $(".w2ui-msg-body input[name='cfDeploymentFile']").val(cfDeploymentFile);
    $(".w2ui-msg-body input[name='cfKeyFile']").val(cfKey);
}

/********************************************************
 * 설명 : garden-Linux 릴리즈 조회
 * 기능 : getgardenRelease
 *********************************************************/
function getgardenRelease() {
    $.ajax({
        type :"GET",
        url :"/common/deploy/release/list/garden-linux",
        contentType :"application/json",
        async :true,
        success :function(data, status) {
            gardenReleaseName = new Array();
            if( data.records != null){
                var option = "<option value=''>Garden 릴리즈를 선택하세요.</option>";
                data.records.map(function(obj) {
                    if( defaultInfo.gardenReleaseName == obj.name && defaultInfo.gardenReleaseVersion == obj.version){
                        option += "<option value='"+obj.name+"/"+obj.version+"' selected>"+obj.name+"/"+obj.version+"</option>";
                    }else{
                        option += "<option value='"+obj.name+"/"+obj.version+"'>"+obj.name+"/"+obj.version+"</option>";
                    }
                });
            }
            $(".w2ui-msg-body select[name='gardenReleaseName']").html(option);
            getEtcdRelease();
        },
        error :function(e, status) {
            w2alert("Garden Linux Release List 를 가져오는데 실패하였습니다.", "DIEGO 설치");
        }
    });
}

/********************************************************
 * 설명 : ETCD 릴리즈 조회
 * 기능 : getEtcdRelease
 *********************************************************/
function getEtcdRelease() {
    $.ajax({
        type :"GET",
        url :"/common/deploy/release/list/etcd",
        contentType :"application/json",
        async :true,
        success :function(data, status) {
            etcdReleases = new Array();
            if( data.records != null){
                var option = "<option value=''>Etcd 릴리즈를 선택하세요.</option>";
                data.records.map(function(obj) {
                    if( defaultInfo.etcdReleaseName == obj.name && defaultInfo.etcdReleaseVersion == obj.version){
                        option += "<option value='"+obj.name+"/"+obj.version+"' selected>"+obj.name+"/"+obj.version+"</option>";
                    }else{
                        option += "<option value='"+obj.name+"/"+obj.version+"'>"+obj.name+"/"+obj.version+"</option>";    
                    }
                });
            }
            $(".w2ui-msg-body select[name='etcdReleases']").html(option);
            w2popup.unlock();
            setReleaseList();
        },
        error :function(e, status) {
            w2popup.unlock();
            w2alert("ETCD Release List 를 가져오는데 실패하였습니다.", "DIEGO 설치");
        }
    });
}

/********************************************************
 * 설명 : Release List W2Field 적용
 * 기능 : setReleaseList
 *********************************************************/
function setReleaseList(){
     setDisabledMonitoring(defaultInfo.diegoReleaseName + "/"+ defaultInfo.diegoReleaseVersion);
}

/********************************************************
 * 설명 : 릴리즈 버전에 따른 cflinuxfs2rootfsrelease 화면 설정
 * 기능 : setcflinuxDisplay
 *********************************************************/
function setcflinuxDisplay(val){
    var diegoReleaseName = val.split("/")[0];
    var diegoReleaseVersion = val.split("/")[1];
    if(diegoReleaseVersion != undefined){
        if( compare( diegoReleaseVersion, "0.1463.0" ) > 0 ){
            $('.w2ui-msg-body #cflinux').css('display','block');
        } else{
            $('.w2ui-msg-body #cflinux').css('display','none');
            $(".w2ui-msg-body input[name='cflinuxfs2rootfsrelease']").val("");
        }
        if(compare( diegoReleaseVersion, "1.2.0" ) > 0){
            $('.w2ui-msg-body #etcd').css('display','none');
            $(".w2ui-msg-body input[name='etcdReleases']").val("");
        } else {
            $('.w2ui-msg-body #etcd').css('display','block');
        }
        setDisabledMonitoring(val);
    } else {
        $('.w2ui-msg-body #paastaMonitoring').attr('disabled',true);
        $(".w2ui-msg-body input:checkbox[name='paastaMonitoring']").prop('checked',false);
        $(".w2ui-msg-body input[name='cadvisorDriverIp']").val("");
        //Read-only
        $(".w2ui-msg-body input[name='cadvisorDriverIp']").attr("disabled", true);
    }
}

/********************************************************
 * 설명 : paasta-container v2.0 이상에서 지원
 * 기능 : setDisabledMonitoring
 *********************************************************/
function setDisabledMonitoring(val){
    if( !checkEmpty(val)){
        var diegoReleaseName = val.split("/")[0];
        var diegoReleaseVersion = val.split("/")[1];
        //paasta-container v2.0 이상 PaaS-TA 모니터링 지원 checkbox
        if( diegoReleaseName.indexOf("paasta-container") > -1 && compare(diegoReleaseVersion, "2.0") > -1){
            $('.w2ui-msg-body #paastaMonitoring').attr('disabled',false);
        }else{
            if( $(".w2ui-msg-body input:checkbox[name='paastaMonitoring']").is(":checked")){
                $(".w2ui-msg-body input:checkbox[name='paastaMonitoring']").prop('checked',false);
                checkPaasTAMonitoringUseYn(diegoReleaseName+"/"+diegoReleaseVersion);
            }
        }
    }
    
}

/********************************************************
 * 설명 : PaaS-TA 모니터링 사용 체크 검사
 * 기능 : checkPaasTAMonitoringUseYn
 *********************************************************/
function checkPaasTAMonitoringUseYn(value){
    var cnt = $("input[name=paastaMonitoring]:checkbox:checked").length;
    if(cnt > 0 ){
        $(".w2ui-msg-body input[name='cadvisorDriverIp']").attr("disabled", false);
    }else{
        $(".w2ui-msg-body input[name='cadvisorDriverIp']").css({"border-color" : "rgb(187, 187, 187)"}).parent().find(".isMessage").text("");
        //값 초기화
        $(".w2ui-msg-body input[name='cadvisorDriverIp']").val("");
        //Read-only
        $(".w2ui-msg-body input[name='cadvisorDriverIp']").attr("disabled", true);
    }
     
}


/********************************************************
 * 설명 : Key 생성 확인
 * 기능 : createKeyConfirm
 *********************************************************/
function createKeyConfirm(){
     var message = "";
     if( !checkEmpty(diegoKeyFile) ){//이미 key가 생성됐으면,
         message = "Key를 재 생성하시겠습니다.?";
     }else{
        message ="Key를 생성하시겠습니까?"; 
     }
     
     w2confirm({
        width        : 350,
        height       : 180,
        title        : '<b>Key 생성 여부</b>',
        msg          : message,
        modal        : true,
        yes_text     : "확인",
        no_text      : "취소",
        yes_callBack : function(){
            createKey();
        },
        no_callBack : function(event){
        }
    });
}

/********************************************************
 * 설명 :  diego 키 생성
 * 기능 : createKey
 *********************************************************/
function createKey(){
    w2popup.lock("Key 생성 중입니다.", true);
    keyInfo = {
            id : diegoId,
            iaas : iaas.toLowerCase(),
            platform : "diego", //cf -> 1, diego -> 2, cf&diego -> 3
            version : defaultInfo.diegoReleaseVersion
    }
    $.ajax({
        type : "POST",
        url : "/common/deploy/key/createKey",
        contentType : "application/json",
        data : JSON.stringify(keyInfo),
        async : true,
        success : function(data, status) {
            w2popup.unlock();
            w2popup.message({
                width  : 500,
                height : 120,
                html   : '<div>' + 
                         '<div style="padding: 10px; font-size:11px; text-align:center">'+ 
                         "Key 생성에 성공하였습니다. <br/> 아래 SSH 핑거프린트를 복사하여 CF SSH 핑거프린트 항목에 입력 후 CF를 재 설치해주세요. <br/><br/>" +
                         "ssh-key-fingerprint: <b>" +data.fingerprint + '</b>'+ 
                         '</div>'+
                         '<div rel="buttons" style="text-align:center;"><button class="btn" onclick="w2popup.message()">닫기</button><div>'+
                         '</div>'
            });
            diegoKeyFile = data.keyFile;
            
        },
        error :function(request, status, error) {
            w2popup.unlock();
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "Diego Key 생성");
        }
    });
}


/********************************************************
 * 설명 :  기본정보 저장
 * 기능 : saveDefaultInfo
 *********************************************************/
function saveDefaultInfo(type) {
    //CF 정보
    if(cfInfoYn==true){
        w2alert("Diego와 연동 할 CF를 설치해 주세요.", "DIEGO 설치");
        return;
    }
    var diegoRelease = $(".w2ui-msg-body select[name='diegoReleases']").val();
    
    if(compare( diegoRelease.split("/")[1], "1.2.0" ) > 0){
        $(".w2ui-msg-body select[name='etcdReleases']").val("");
    }
    
    var gardenRelease = $(".w2ui-msg-body select[name='gardenReleaseName']").val();
    var etcdRelease = $(".w2ui-msg-body select[name='etcdReleases']").val();
    var cflinuxfs2rootfsrelease = $(".w2ui-msg-body select[name='cflinuxfs2rootfsrelease']").val();
    var cfName = $(".w2ui-msg-body select[name='cfInfo']").val();
    var monitoringUse = "";
    if( $(".w2ui-msg-body input:checkbox[name='paastaMonitoring']").is(":checked")){
        monitoringUse = "true";
    }else{
        monitoringUse = "false";
    }
    defaultInfo = {
                id                              : (diegoId) ? diegoId :"",
                iaas                            : iaas.toUpperCase(),
                platform                        : "diego",
                deploymentName                  : $(".w2ui-msg-body input[name='deploymentName']").val(),
                directorUuid                    : $(".w2ui-msg-body input[name='directorUuid']").val(),
                diegoReleaseName                : diegoRelease.split("/")[0],
                diegoReleaseVersion             : diegoRelease.split("/")[1],
                cfDeploymentName                : cfName,
                cfId                            : $(".w2ui-msg-body input[name='cfId']").val(),
                cfDeploymentFile                : $(".w2ui-msg-body input[name='cfDeploymentFile']").val(),
                cfReleaseVersion                : $(".w2ui-msg-body input[name='cfReleaseVersion']").val(),
                cfKeyFile                       : $(".w2ui-msg-body input[name='cfKeyFile']").val(),
                gardenReleaseName               : gardenRelease.split("/")[0],
                gardenReleaseVersion            : gardenRelease.split("/")[1],
                etcdReleaseName                 : etcdRelease.split("/")[0],
                etcdReleaseVersion              : etcdRelease.split("/")[1],
                cflinuxfs2rootfsreleaseName     : cflinuxfs2rootfsrelease.split("/")[0],
                cflinuxfs2rootfsreleaseVersion  : cflinuxfs2rootfsrelease.split("/")[1],
                paastaMonitoringUse             : monitoringUse,
                cadvisorDriverIp                : $(".w2ui-msg-body input[name='cadvisorDriverIp']").val(),
    }
    if( type == 'after'){
            $.ajax({
                type :"PUT",
                url :"/deploy/"+menu+"/install/saveDefaultInfo",
                contentType :"application/json",
                data :JSON.stringify(defaultInfo),
                success :function(data, status) {
                    if( menu == "cfDiego" ){
                        diegoId = data.content.diegoVo.id;
                    }else{
                        diegoId = data.content.id;
                    }
                    settingIaasPopup("network");
                },
                error :function(request, status, error) {
                    var errorResult = JSON.parse(request.responseText);
                    w2alert(errorResult.message, "diego  기본정보 등록");
                    return;
                }
            });
    }else{
        w2popup.clear();
        setModifyPopup();
    }
}

/********************************************************
 * 설명 : 네트워크 정보 팝업(openstack/aws)
 * 기능 : networkPopup
 *********************************************************/
function networkPopup(div, height){
    if(iaas.toLowerCase() == "aws"){
        $('#availabilityZone').css('display','block');
    }else{
        $('#availabilityZone').css('display','none');
        $(".w2ui-msg-body input[name='availabilityZone']").val("");
    }
    
    $("#networkInfoDiv").w2popup({
        title   : "<b>DIEGO 설치</b>",
        width   : 750,
        height  : height,
        body    : $(div+"Div").html(),
        buttons : $(div+"Buttons").html(),
        modal   : true,
        showMax : false,
        onOpen  :function(event) {
            event.onComplete = function() {
                if (networkInfo.length > 0) {
                    networkId = networkInfo[0].id;
                    for(var i=0; i <networkInfo.length; i++){
                        if(networkInfo[i].seq  > 0)  {
                            settingNetwork(networkInfo[i].seq );
                        }
                        var seq = networkInfo[i].seq;
                        $(".w2ui-msg-body input[name='subnetRange_"+seq+"']").val(networkInfo[i].subnetRange); 
                        $(".w2ui-msg-body input[name='subnetGateway_"+seq+"']").val(networkInfo[i].subnetGateway);
                        $(".w2ui-msg-body input[name='networkName_"+seq+"']").val(networkInfo[i].networkName);
                        $(".w2ui-msg-body input[name='subnetDns_"+seq+"']").val(networkInfo[i].subnetDns);
                        $(".w2ui-msg-body input[name='subnetReservedFrom_"+seq+"']").val(networkInfo[i].subnetReservedFrom);
                        $(".w2ui-msg-body input[name='subnetReservedTo_"+seq+"']").val(networkInfo[i].subnetReservedTo);
                        $(".w2ui-msg-body input[name='subnetStaticFrom_"+seq+"']").val(networkInfo[i].subnetStaticFrom);
                        $(".w2ui-msg-body input[name='subnetStaticTo_"+seq+"']").val(networkInfo[i].subnetStaticTo);
                        $(".w2ui-msg-body input[name='subnetId_"+seq+"']").val(networkInfo[i].subnetId);
                        $(".w2ui-msg-body input[name='cloudSecurityGroups_"+seq+"']").val(networkInfo[i].cloudSecurityGroups);
                        $(".w2ui-msg-body input[name='availabilityZone_"+seq+"']").val(networkInfo[i].availabilityZone);
                    }
                }
            }
        },
        onClose :function(event) {
            event.onComplete = function() {
                initSetting();
            }
        }
    });
}

  
/********************************************************
 * 설명 : 네트워크 화면 설정
 * 기능 : settingNetwork
 *********************************************************/
 function settingNetwork( index ){
     if( iaas.toLowerCase() == 'aws' || iaas.toLowerCase() == "openstack" ){
         addInternalNetworkInputs('#defaultNetworkInfoDiv_'+index+'', "#defaultNetworkInfoForm" );
     }else if( iaas.toLowerCase() == 'google' ){
         addInternalNetworkInputs('#googleNetworkInfoDiv_'+index+'', "#googleNetworkInfoForm" );
     }else{
         addInternalNetworkInputs('#vSphereNetworkInfoDiv_'+index+'', "#vSphereNetworkInfoForm" );
     }
 }

/********************************************************
 * 설명 : 네트워크 입력 추가
 * 기능 : addInternalNetworkInputs
*********************************************************/
function addInternalNetworkInputs( preDiv, form ){
    var index = Number(preDiv.split("_")[1])+1;
    var divDisplay = preDiv.split("_")[0];
    var div= preDiv.split("_")[0] + "_"+ index;
    var body_div= "<div class='panel-body'>";
    var field_div_label="<div class='w2ui-field'>"+"<label style='text-align: left; width: 40%; font-size: 11px;'>";
    var text_style="type='text' style='display:inline-blcok; width:70%;'";
    var inputIndex = index -1;
    
    var html= "<div class='panel panel-info' style='margin-top:2%;'>";
        html+= "<div  class='panel-heading' style='position:relative;'>";
        html+=    "<b>Internal 네트워크</b>";
        html+=    "<div style='position: absolute;right: 10px; top: 2px;'>";
        if( index == 2 ){
            if( $(".w2ui-msg-body "+divDisplay+"_3").css("display") == "none" ){
                html+= '<span class="btn btn-info btn-sm addInternal" onclick="addInternalNetworkInputs(\''+div+'\', \''+form+'\');">추가</span>';
            }else{
                html+= '<span class="btn btn-info btn-sm addInternal" style="display:none;" onclick="addInternalNetworkInputs(\''+div+'\', \''+form+'\');">추가</span>';
            }
        }
        html+=        '&nbsp;&nbsp;<span class="btn btn-info btn-sm" onclick="delInternalNetwork(\''+preDiv+'\', '+index+');">삭제</span>';
        html+=    "</div>";
        html+= "</div>";
        html+= body_div;
        if( iaas.toLowerCase() == "google" ){
            html+= field_div_label + "네트워크 명" + "</label>";
            html+= "<div style='width: 60%'>"+"<input name='networkName_"+inputIndex+"'" + text_style +" placeholder='네트워크 명을 입력하세요.'/>"+"</div></div>";
            
            html+= field_div_label + "서브넷 명" + "</label>"; 
            html+= "<div style='width: 60%'>"+"<input name='subnetId_"+inputIndex+"'" + text_style +" placeholder='서브넷 명을 입력하세요.'/>"+"</div></div>";
            
            html+= field_div_label + "방화벽 규칙" + "</label>"; 
            html+= "<div style='width: 60%'>"+"<input name='cloudSecurityGroups_"+inputIndex+"'" + text_style +" placeholder='예) internet, cf-security'/>"+"</div></div>";
            
            html+= field_div_label + "영역" + "</label>"; 
            html+= "<div style='width: 60%'>"+"<input name='availabilityZone_"+inputIndex+"'" + text_style +" placeholder='예) asia-northeast1-a'/>"+"</div></div>";
            
        }else if(iaas.toLowerCase() == "vsphere"){
            html+= field_div_label + "포트 그룹명" + "</label>"; 
            html+= "<div style='width: 60%'>"+"<input name='subnetId_"+inputIndex+"'" + text_style +" placeholder='포트 그룹명을 입력하세요.'/>"+"</div></div>";
        }else{
            html+= field_div_label + "시큐리티 그룹" + "</label>"; 
            html+= "<div style='width: 60%'>"+"<input name='cloudSecurityGroups_"+inputIndex+"'" + text_style +" placeholder='예) bosh-security, cf-security'/>"+"</div></div>";
            
            html+= field_div_label + "서브넷 아이디" + "</label>"; 
            html+="<div style='width: 60%'>"+"<input name='subnetId_"+inputIndex+"'" + text_style +" placeholder='서브넷 아이디를 입력하세요.'/>"+"</div></div>";
            
            if( iaas.toLowerCase() == "aws" ){
                html+= field_div_label + "가용 영역" + "</label>"; 
                html+= "<div style='width: 60%'>"+"<input name='availabilityZone_"+inputIndex+"'" + text_style +" placeholder='예) us-west-2'/>"+"</div></div>";
            }
        }
        html+= field_div_label + "서브넷 범위" + "</label>"; 
        html+= "<div style='width: 60%'>"+"<input name='subnetRange_"+inputIndex+"'" + text_style +" placeholder='예) 10.0.0.0/24'/>" + "</div></div>";
        
        html+= field_div_label + "게이트웨이" + "</label>"; 
        html+= "<div style='width: 60%'>"+ "<input name='subnetGateway_"+inputIndex+"'" + text_style +" placeholder='예) 10.0.0.1'/>" + "</div></div>";
        
        html+= field_div_label + "DNS" + "</label>"; 
        html+= "<div style='width: 60%'>"+ "<input name='subnetDns_"+inputIndex+"'" + text_style +" placeholder='예) 8.8.8.8'/>" + "</div></div>";
       
        html+= field_div_label + "IP할당 제외 대역" + "</label>"; 
        html+=     "<div style='width: 60%'>";
        html+=         "<input name='subnetReservedFrom_"+inputIndex+"' type='text' style='display:inline-block; width:32%;' placeholder='예) 10.0.0.10' />";
        html+=         "<span style='width: 4%; text-align: center;'>&nbsp;&ndash; &nbsp;</span>";
        html+=         "<input name='subnetReservedTo_"+inputIndex+"' type='text' style='display:inline-block; width:32%;' placeholder='예) 10.0.0.20' />";
        html+=     "</div></div>";
        
        html+= field_div_label + "IP할당 대역(최소 20개)" + "</label>"; 
        html+=     "<div style='width: 60%'>"+"<input name='subnetStaticFrom_"+inputIndex+"' type='text' style='display:inline-block; width:32%;' placeholder='예) 10.0.0.10' />";
        html+=         "<span style='width: 4%; text-align: center;'>&nbsp;&ndash; &nbsp;</span>";
        html+=         "<input name='subnetStaticTo_"+inputIndex+"' type='text' style='display:inline-block; width:32%;' placeholder='예) 10.0.0.20'/>";
        html+=     "</div>";
        html+= "</div></div></div>";
        
        //추가 버튼 hidden
        $(".w2ui-msg-body "+ div).show();
        $(".w2ui-msg-body "+preDiv + " .addInternal").hide();
        $(form + " "+ div).html(html);
        $(form + " " +div).css("display", "block");
        createInternalNetworkValidate(inputIndex);
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
 * 설명 : 네트워크 입력 삭제
 * 기능 : delNetwork
 *********************************************************/
function delInternalNetwork(preDiv, index){
     var div= preDiv.split("_")[0] + "_"+ index;
     var form = preDiv.split("Div")[0]+"Form";
     $(form + " "+ div).html("");
     $(form + " "+ div).css("display", "none");
     $(".w2ui-msg-body "+preDiv+" .addInternal").show();
}

/********************************************************
 * 설명 :  Network 정보 저장
 * 기능 : saveNetworkInfo
 *********************************************************/
function saveNetworkInfo(type, form) {
    networkInfo = [];
    var subnetStaticFrom = "";
    if( iaas.toUpperCase() == "VSPHERE" ){
        subnetStaticFrom = $(".w2ui-msg-body input[name='publicStaticFrom']").val();
    }else{
        subnetStaticFrom = $(".w2ui-msg-body input[name='publicStaticIp']").val();
    }
    publicStaticIp = subnetStaticFrom;
    var form = "#defaultNetworkInfoForm";
    if(iaas.toUpperCase()=="VSPHERE") form = "#vSphereNetworkInfoForm";
    else if(iaas.toUpperCase()=="GOOGLE") form = "#googleNetworkInfoForm";
    $(".w2ui-msg-body "+form+" .panel-body").each(function(){
        var div = $($(this).parent().parent()).attr("id");
        var count = div.split("_")[1];
        if($(".w2ui-msg-body input[name='subnetRange_1']").val()==""&&$(".w2ui-msg-body input[name='subnetRange_2']").val()!="") count = 2;
        var inputIndex = count - 1;
        var  InternalArr = {
                diegoId                   : diegoId,
                    id                    : networkId,
                    iaas                  : iaas.toUpperCase(),
                    deployType            : 1400,
                    net                   : "Internal",
                    seq                   : count-1,
                    networkName         : $(".w2ui-msg-body input[name='networkName_"+inputIndex+"']").val(),
                    subnetRange           : $(".w2ui-msg-body input[name='subnetRange_"+inputIndex+"']").val(),
                    subnetGateway         : $(".w2ui-msg-body input[name='subnetGateway_"+inputIndex+"']").val(),
                    subnetDns             : $(".w2ui-msg-body input[name='subnetDns_"+inputIndex+"']").val(),
                    subnetReservedFrom    : $(".w2ui-msg-body input[name='subnetReservedFrom_"+inputIndex+"']").val(),
                    subnetReservedTo      : $(".w2ui-msg-body input[name='subnetReservedTo_"+inputIndex+"']").val(),
                    subnetStaticFrom      : $(".w2ui-msg-body input[name='subnetStaticFrom_"+inputIndex+"']").val(),
                    subnetStaticTo        : $(".w2ui-msg-body input[name='subnetStaticTo_"+inputIndex+"']").val(),
                    subnetId              : $(".w2ui-msg-body input[name='subnetId_"+inputIndex+"']").val(),
                    cloudSecurityGroups   : $(".w2ui-msg-body input[name='cloudSecurityGroups_"+inputIndex+"']").val(),
                    availabilityZone      : $(".w2ui-msg-body input[name='availabilityZone_"+inputIndex+"']").val()
         }
         networkInfo.push(InternalArr);
    });
    if (type == 'after') {
        //Server send Diego Info
        $.ajax({
            type :"PUT",
            url :"/deploy/"+menu+"/install/saveNetworkInfo",
            contentType :"application/json",
            async :true,
            data : JSON.stringify(networkInfo),
            success :function(data, status) {
                w2popup.clear();
                jobPopupComplete();
            },
            error :function(e, status) {
                w2alert("Diego (OPENSTACK) Network 등록에 실패 하였습니다.", "Diego 설치");
            }
        });
    } else if (type == 'before') {
        w2popup.clear();
        defaultPopup();
    }
}

/********************************************************
 * 설명 :  Resource 정보 팝업
 * 기능 : Resource
 *********************************************************/
function resourcePopup(div, height) {
     w2popup.open({
        title   : "<b>Diego 설치</b>",
        width   : 750,
        height  : 800,
        body    : $(div+"Div").html(),
        buttons : $(div+"Buttons").html(),
        modal   :true,
        showMax :false,
        onOpen  :function(event) {
            event.onComplete = function() {
            	console.log(defaultInfo);
                if( menu == "cfDiego" || defaultInfo.cfReleaseVersion > 271 || defaultInfo.cfReleaseVersion == "3.0" || defaultInfo.cfReleaseVersion == "3.1" ){
                    $('.w2ui-msg-body #keyBtn').css("display","none");
                    diegoKeyFile = defaultInfo.cfKeyFile;
                    console.log(diegoKeyFile);
                    
                } else {
                    $('.w2ui-msg-body #keyBtn').css("display","block");
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
        onClose :function(event) {
            event.onComplete = function() {
                initSetting();
            }
        }
    });
}

/********************************************************
 * 설명 : 리소스 정보 팝업(vSphere)
 * 기능 : vSphereResourceInfoPopup
 *********************************************************/
function vSphereResourceInfoPopup() {
     w2popup.open({
        title   : "<b>Diego 설치</b>",
        width   : 750,
        height  : 820,
        body    : $("vSphereResourceInfoDiv").html(),
        buttons : $("vSphereResourceInfoButtons").html(),
        modal : true,
        showMax : false,
        onOpen : function(event) {
            event.onComplete = function() {
                if( menu == "cfDiego" || defaultInfo.cfReleaseVersion > 271 || defaultInfo.cfReleaseVersion == "3.0" || defaultInfo.cfReleaseVersion == "3.1" ){
                    $('.w2ui-msg-body #keyBtn').css("display","none");
                    diegoKeyFile = defaultInfo.cfKeyFile;
                } else {
                    $('.w2ui-msg-body #keyBtn').css("display","block");
                }
                
                if (resourceInfo != "") {
                    $(".w2ui-msg-body input[name='boshPassword']").val(resourceInfo.boshPassword);
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
  * 설명 : Diego 설치 스템셀 조회 
  * 기능 : getStamcellList
  *********************************************************/
 function getStamcellList() {
    $.ajax({
        type : "GET",
        url : "/common/deploy/stemcell/list/diego/openstack",
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
  * 설명 : DIEGO 고급 기능
  * 기능 : resourceAdvancedSettingsPop
  *********************************************************/
 function resourceAdvancedSettingsPop(){
     w2popup.open({
         title   : 'Diego 고급 설정',
         width   : 625,
         height  : 685,
         showMax : true,
         body    : '<div id="diegoDetailDiv" style="position: absolute; left: 5px; top: 5px; right: 5px; bottom: 5px;"></div>',
         buttons :$("#diegoDetailButtons").html(),
         onOpen  : function (event) {
             event.onComplete = function () {
                 $('#w2ui-popup #diegoDetailDiv').w2render('layout2');
                 w2ui['layout2'].content('main', $('#diegoDetailPopDiv').html());
                 settingDiegoJobs();
             };
         }, onClose : function(event){
             jobPopupComplete();
        }
     });
 }
 
 /********************************************************
  * 설명 : DIEGO 고급 설정 정보 저장
  * 기능 : saveCfJobsInfo
  *********************************************************/
 function saveDiegoJobsInfo(){
     w2popup.lock(save_info_msg, true);
     var i=0;
     var flag=true;
      $(".w2ui-msg-body #diegoJobListDiv li > ul").each(function(){
         var input = $($(this).children()[0]).find("input");
         if ($(input).val != "" && $(input).val() >=0 && $(input).val() <= 3) {
         } else flag = false;
     }); 
     jobsInfo = [];
     $(".w2ui-msg-body #diegoJobListDiv li > ul").each(function(){
         var input = $($(this).children()[0]).find("input");
         var pwd_input = $($(this).children()[1]).find("input");
         i ++;
         var index = $(input).attr("name").split("_z").length-1;
         var z_index = $(input).attr("name").split("_").length-1;
         var job = {
                 id           : diegoId,
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
  * 설명 : DIEGO Jobs 팝업 화면 닫았을 경우
  * 기능 : jobPopupComplete
  *********************************************************/
 function jobPopupComplete(){
     if( iaas.toUpperCase() == "VSPHERE" ){
         resourcePopup("#vSphereResourceInfo", 695);
     }else{
         resourcePopup("#resourceInfo",690 );
    }
 }

 /********************************************************
  * 설명 : DIEGO Jobs 정보 설정
  * 기능 : settingDiegoJobs
  *********************************************************/
 function settingDiegoJobs(){
     var release_version = defaultInfo.diegoReleaseVersion;
     release_version = settingReleaseVersion(release_version);
     $.ajax({
         type : "GET",
         url : "/deploy/diego/install/save/job/list/"+release_version+"/"+'DEPLOY_TYPE_DIEGO',
         contentType : "application/json",
         success : function(data, status) {
             if( !checkEmpty(data) ){
                 var div = "";
                 var html = "";
                 html += '<div class="panel panel-info" style="height: 100%;overflow: auto;" >';
                 html += '<div class="panel-heading"><b>Diego 고급 설정</b></div>';
                 html += '<div class="panel-body">';
                 html += '<div id="diegoJobListDiv">';
                 html += '<p style="color:red;">- 고급 설정 값을 변경하지 않을 경우 아래에 입력 된 기본 값으로 자동 설정됩니다.</p>';
                 html += '<p style="color:red;">- 해당 Job의 인스턴스 수는 0-3까지 입력하실 수 있습니다.</p>';
                 for(var j=1; j<networkInfo.length+1; j++){
                     for( var i=0; i<data.length; i++ ){
                         html += "<p style='color: #565656;font-size: 13px;font-weight:bolder;margin-top: 20px;'>[Internal 네트워크_"+ j+ "]</p>"
                         for( var i=0; i<data.length; i++ ){
                             if( j == 1 && data[i].zone_z1 == "true" ){
                                 html += setJobSettingHtml(data[i], j );
                             }else if( j == 2 && data[i].zone_z2 == "true"  ){
                                 html += setJobSettingHtml(data[i], j);
                             } else if( j == 3 && data[i].zone_z3 == "true" ){
                                 html += setJobSettingHtml(data[i], j);
                             }
                         }
                     }
                 }
                 html +='</div></div></div>';
                 $("#diegoDetailForm").html(html);
                 $(".w2ui-msg-body #diegoDetailForm").html(html);
                 if( jobsInfo.length > 0 ){
                     for( var i=0; i<jobsInfo.length; i++ ){
                         $(".w2ui-msg-body input[name='"+jobsInfo[i].job_name+"_"+jobsInfo[i].zone+"']").val(jobsInfo[i].instances);
                     }
                 }
             }else{
                 w2alert("해당 Diego 버전은 고급 기능을 제공하지 않습니다.");
                 jobPopupComplete();
             }
         },
         error : function(e, status) {
             w2alert(JSON.parse(e.responseText).message, "CF 설치");
         }
     });
 }
 
 /********************************************************
  * 설명 : diego 고급 설정 paasta release version 설정
  * 기능 : settingReleaseVersion
  *********************************************************/
 function settingReleaseVersion( version ){
     var releaseVersion = version;
      if( version == "3.0" ){
         releaseVersion = "1.25.3";
     }else if( version == "2.0" ){
         releaseVersion = "1.1.0";
     }else if(releaseVersion == "3.1"){
    	 releaseVersion = "1.34.0";
     }
     return releaseVersion;
 }
 
 /********************************************************
  * 설명 : CF 고급 설정 HTML 설정
  * 기능 : setJobSettingHtml
  *********************************************************/
 function setJobSettingHtml(data, j){
     var html = "";
         html += '<ul class="w2ui-field" style="border: 1px solid #c5e3f3;padding: 10px;">';
         html +=     '<li style="display:inline-block; width:35%;">';
         html +=         '<label style="text-align: left;font-size:11px;">'+data.job_name+'_z'+j+'</label>';
         html +=     '</li>';
         html +=     '<li style="display:inline-block; width:60%;vertical-align:middle; line-height:3; text-align:right;">';
         html +=         '<ul>';
         html +=             '<li>';
         html +=                 '<label style="display:inline-block;">인스턴스 수 : </label>&nbsp;&nbsp;&nbsp;';
         html +=                 '<input class="form-control" style="width:60%; display:inline-block;" onblur="instanceControl(this);" onfocusin="instanceControl(this);" onfocusout="instanceControl(this);" maxlength="1" type="number" min="0" max="3" value="1" id="'+data.id+'" name="'+data.job_name+'_z'+j+'"/>';
         html +=              '</li>';
         html +=         '</ul>';
         html +=     '</li>';
         html += '</ul>';
     return html;
 }
 
 /********************************************************
  * 설명 : DIEGO Jobs 유효성 추가
  * 기능 : addCfDetailValidate
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
//                       e.value= jobsInfo[i].instances;
                  }
              }
          }else{
//               $(e).val("1");
          }
          if( $(e).parent().find("p").length == 0 ){
              $(e).parent().append("<p>0부터 3까지 숫자만 입력 가능 합니다.</p>");
          }
      }
 }
 
/********************************************************
 * 설명 :  리소스 정보 저장
 * 기능 : saveResourceInfo
 *********************************************************/
function saveResourceInfo(type) {
    var stemcellInfos = $(".w2ui-msg-body select[name='stemcells']").val().split("/");
    resourceInfo = {
            id                      : diegoId,
            cfId                    : cfId,
            iaas                    : iaas.toUpperCase(),
            platform                : "diego",
            stemcellName            : stemcellInfos[0],
            stemcellVersion         : stemcellInfos[1],
            boshPassword            : $(".w2ui-msg-body input[name='boshPassword']").val(),
            smallFlavor             : $(".w2ui-msg-body input[name='smallFlavor']").val(),
            smallCpu                : $(".w2ui-msg-body input[name='smallFlavorCpu']").val(),
            smallRam                : $(".w2ui-msg-body input[name='smallFlavorRam']").val(),
            smallDisk               : $(".w2ui-msg-body input[name='smallFlavorDisk']").val(),
            mediumFlavor            : $(".w2ui-msg-body input[name='mediumFlavor']").val(),
            mediumCpu               : $(".w2ui-msg-body input[name='mediumFlavorCpu']").val(),
            mediumRam               : $(".w2ui-msg-body input[name='mediumFlavorRam']").val(),
            mediumDisk              : $(".w2ui-msg-body input[name='mediumFlavorDisk']").val(),
            largeFlavor             : $(".w2ui-msg-body input[name='largeFlavor']").val(),
            largeCpu                : $(".w2ui-msg-body input[name='largeFlavorCpu']").val(),
            largeRam                : $(".w2ui-msg-body input[name='largeFlavorRam']").val(),
            largeDisk               : $(".w2ui-msg-body input[name='largeFlavorDisk']").val(),
            runnerFlavor            : $(".w2ui-msg-body input[name='runnerFlavor']").val(),
            runnerCpu               : $(".w2ui-msg-body input[name='runnerFlavorCpu']").val(),
            runnerRam               : $(".w2ui-msg-body input[name='runnerFlavorRam']").val(),
            runnerDisk              : $(".w2ui-msg-body input[name='runnerFlavorDisk']").val(),
            keyFile                 : diegoKeyFile
    }

    if (type == 'after') {
        //key 생성하지 하지 않았을 경우
        if($(".w2ui-msg-body #keyBtn").css('display') != "none" ){
            if( checkEmpty(diegoKeyFile) && menu =='diego' ){
              w2alert("Diego Key를 먼저 생성해주세요.", "DIEGO 설치");
              return;
          }
        }
        var url = "/deploy/"+ menu +"/install/saveResourceInfo";
        //Server send Diego Info
        $.ajax({
            type        :"PUT",
            url         :url,
            contentType :"application/json",
            async       :true,
            data        :JSON.stringify(resourceInfo),
            success     :function(data, status) {
                w2popup.clear();
                deploymentFile = data.deploymentFile;
                createSettingFile(data.id, deploymentFile);
            },
            error :function(e, status) {
                w2alert("Diego ("+iaas.toUpperCase()+") Resource 등록에 실패 하였습니다.", "Diego 설치");
            }
        });
    } else if (type == 'before') {
        settingIaasPopup("network");
    }
}

/********************************************************
 * 설명 : 인프라 별 팝업 화면 설정
 * 기능 : settingIaasPopup
 *********************************************************/
function settingIaasPopup(type){
     if( type == "network" ){
        if( iaas.toUpperCase() == "VSPHERE" ){
            networkPopup("#VsphereNetworkInfo", 505);
        }else if(iaas.toUpperCase() == "GOOGLE" ){
            networkPopup("#googleNetworkInfo", 545);
        }else{
            networkPopup("#defaultNetworkInfo", 515);
        }
     }
}

/********************************************************
 * 설명 : Manifest 파일 생성
 * 기능 : deployPopup
 *********************************************************/
function createSettingFile(id, deploymentFile){
    var settingFile = {
            id        : id,
            platform  : "diego"
    }
    $.ajax({
        type : "POST",
        url : "/deploy/"+menu+"/install/createSettingFile",
        contentType : "application/json",
        data : JSON.stringify(settingFile),
        async : true,
        success : function(status) {
            deployPopup();
        },
        error :function(request, status, error) {
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "diego  배포 파일 생성");
            jobPopupComplete();
        }
    });
}

/********************************************************
 * 설명 : 배포 정보 팝업
 * 기능 : deployPopup
 *********************************************************/
function deployPopup() {
     w2popup.open({
         title   : "<b>DIEGO 설치</b>",
         width   : 750,
         height  : 520,
         modal   :true,
         showMax :true,
         body    : $("#deployDiv").html(),
         buttons : $("#deployDivButtons").html(),
         onClose :initSetting,
         onOpen  :function(event) {
             event.onComplete = function() {
                 getDeployInfo();
             }
         }
    });
}

/********************************************************
 * 설명 :  배포 정보 조회
 * 기능 : deployPopup
 *********************************************************/
function getDeployInfo() {
    var url = "/common/use/deployment/"+deploymentFile+"";
    $.ajax({
        type :"GET",
        url :url,
        contentType :"application/json",
        async :true,
        success :function(data, status) {
            if (status == "success") {
                $(".w2ui-msg-body #deployInfo").text(data);
            } else if (status == "204") {
                w2alert("배포파일이 존재하지 않습니다.", "DIEGO 설치");
                jobPopupComplete();
            }
        },
        error :function(e, status) {
            w2alert("Temp 파일을 가져오는 중 오류가 발생하였습니다. ", "DIEGO 설치");
            jobPopupComplete();
        }
    });
}

/********************************************************
 * 설명 :  배포 확인창 뒤로가기
 * 기능 : resourcePopupSel
 *********************************************************/
function resourcePopupSel(){
    jobPopupComplete();
}

/********************************************************
 * 설명 :  Diego 설치 확인 팝업
 * 기능 : diegoDeploy
 *********************************************************/
function diegoDeploy(type) {
    w2confirm({
        msg :"설치하시겠습니까?",
        title :w2utils.lang( menu.toUpperCase() +' 설치'),
        yes_text :"예",
        no_text :"아니오",
        yes_callBack :function(event) {
            if( menu != "cfDiego" ){
                installPopup();
            }else{
                var selected = w2ui['config_cfDiegoGrid'].getSelection();
                var record = w2ui['config_cfDiegoGrid'].get(selected);
                //cf & diego 수정일 경우
                if( !checkEmpty(record) ) {
                     if( record.cfVo.deployStatus == 'CF 성공' ||  record.diegoVo.deployStatus == 'DIEGO 취소' ||  record.diegoVo.deployStatus == 'DIEGO 오류' ){
                         diegoInstallPopup(defaultInfo.deploymentName);
                     }else { 
                         cfInstallPopup(defaultInfo.cfDeploymentName, defaultInfo.deploymentName); 
                     }
                }else{
                    cfInstallPopup(defaultInfo.cfDeploymentName);
                }
            }
        }
    });
}

/********************************************************
 * 설명 :  Diego Install Popup 
 * 기능 : installPopup
 *********************************************************/
function installPopup(){
    var deploymentName = defaultInfo.deploymentName;
    var message = "DIEGO & Container";
    
    var requestParameter = {
            id          : diegoId,
            iaas        : iaas,
            platform    : "diego"
    };
    
    w2popup.open({
        title   : "<b>DIEGO 설치</b>",
        width   : 750,
        height  : 600,
        body    : $("#installDiv").html(),
        buttons : $("#installButtons").html(),
        modal   :true,
        showMax :true,
        onOpen :function(event){
            event.onComplete = function(){
                //deployFileName
                var socket = new SockJS('/deploy/diego/install/diegoInstall');
                installClient = Stomp.over(socket); 
                installClient.connect({}, function(frame) {
                    installClient.subscribe('/user/deploy/diego/install/logs', function(data){
                        
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
                                w2alert(message, "DIEGO 설치");
                               }
                        }

                    });
                    installClient.send('/send/deploy/diego/install/diegoInstall', {}, JSON.stringify(requestParameter));
                });
            }
        }, onClose : function (event){
            event.onComplete= function(){
                $("textarea").text("");
                if( installClient != ""){
                    installClient.disconnect();
                }
                initSetting();
            }
        }
    });
}

/********************************************************
 * 설명 :  Diego 삭제
 * 기능 : deletePopup
 *********************************************************/
function diegoDeletePopup(record){
    var requestParameter = {
            iaas            : (record.iaas) ? record.iaas : record.iaasType, 
            id                : record.id,
            platform        : "diego"
    };
    
    if ( record.deployStatus == null || record.deployStatus == '' ) {
        if( menu != "cfDiego" ){
            // 단순 레코드 삭제
            var url = "/deploy/diego/delete/data";
            $.ajax({
                type :"DELETE",
                url :url,
                data :JSON.stringify(requestParameter),
                contentType :"application/json",
                success :function(data, status) {
                    doSearch();
                },
                error :function(request, status, error) {
                    var errorResult = JSON.parse(request.responseText);
                    w2alert(errorResult.message, "DIEGO 삭제");
                }
            });
        }else{
            doSearch();
        }
    } else {
        var message = "";
        var body = '<textarea id="deleteLogs" style="width:95%;height:90%;overflow-y:visible;resize:none;background-color:#FFF; margin:2%" readonly="readonly"></textarea>';
        
        w2popup.open({
            width :700,
            height :500,
            title :"<b>DIEGO 삭제</b>",
            body  :body,
            buttons :'<button class="btn" style="float:right; padding-right:15%;" onclick="popupComplete();">닫기</button>',
            showMax :true,
            onOpen :function(event){
                event.onComplete = function(){
                    var socket = new SockJS('/deploy/diego/delete/instance');
                    deleteClient = Stomp.over(socket); 
                    deleteClient.connect({}, function(frame) {
                        deleteClient.subscribe('/user/deploy/diego/delete/logs', function(data){
                            
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
                                    if(deleteClient != ""){
                                        deleteClient.disconnect();
                                        deleteClient = "";
                                    }
                                    
                                    w2alert(message, "DIEGO 삭제");
                                   }
                            }
                            
                        });
                        deleteClient.send('/send/deploy/diego/delete/instance', {}, JSON.stringify(requestParameter));
                    });
                }
            },
            onClose :function (event){
                event.onComplete= function(){
                    $("textarea").text("");
                    if( deleteClient != ""){
                        deleteClient.disconnect();
                    }
                    initSetting();
                }
            }
        });
    }        
}

 /********************************************************
  * 설명 : 전역변수 초기화 
  * 기능 : initSetting
  *********************************************************/
function initSetting() {
    //private var
    diegoId = "";
    defaultInfo = "";    
    cfInfo = "";
    diegoInfo = "";
    etcdInfo = "";
    peerInfo = "";
    networkInfo = [];
    publicStaticIp = "";
    internalCnt=0;
    resourceInfo = "";
    internalCnt =1;
    diegoReleases = "";
    cfReleases = "";
    jobsInfo=[];
    gardenReleaseName = "";
    etcdReleases = "";
    cflinuxfs2rootfsrelease = "";
    stemcells = "";
    deploymentFile = "";
    defaultDirector = "";
    installClient = "";
    installStatus = "";
    modifyNetWork = "";
    cfInfoYn= false;
    diegoKeyFile ="";
    //grid Reload
    gridReload();
}

 /********************************************************
  * 설명 : Install/Delete 팝업 종료시 Event 
  * 기능 : popupComplete
  *********************************************************/
function popupComplete(){
    var msg;
    if(installStatus == "done" || installStatus == 'error'){
        msg = $(".w2ui-msg-title b").text() + " 화면을 닫으시겠습니까?";
    }else{
        msg = $(".w2ui-msg-title b").text() + " 화면을 닫으시겠습니까?<BR>(닫은 후에도 완료되지 않는 설치 또는 삭제 작업은 계속 진행됩니다.)";
    }
    w2confirm({
        title     : $(".w2ui-msg-title b").text(),
        msg        : msg,
        yes_text:"확인",
        yes_callBack :function(envent){
            w2popup.close();
            //params init
            initSetting();
        },
        no_text :"취소"
    });
}
 
 
/********************************************************
 * 설명 : 팝업창 닫을 경우
 * 기능 : popupClose
 *********************************************************/
function popupClose() {
     $().w2destroy("layout2");
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
     if( menu == "diego" ){
         w2ui['config_diegoGrid'].reset();
         doSearch();
     }else if( menu =="cfDiego" ){
         w2ui['config_cfDiegoGrid'].load("<c:url value='/deploy/cfDiego/list/"+iaas+"'/>",
                    function() { doButtonStyle(); });
     }
}
</script>

<!-- 기본 정보 설정 DIV -->
<div id="defaultInfoDiv" style="width:100%; height:100%;" hidden="true">
    <form id="defaultInfoForm" >
        <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
            <ul class="progressStep_5">
                <li class="active">기본 정보</li>
                <li class="before">네트워크 정보</li>
                <li class="before">리소스 정보</li>
                <li class="before">배포파일 정보</li>
                <li class="before">설치</li>
            </ul>
        </div>
        <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
            <div class="panel panel-info">    
                <div class="panel-heading"><b>기본 정보</b></div>
                <div class="panel-body" style="padding:5px 5% 10px 5%;">
                    <div class="w2ui-field" >
                        <label style="text-align:left; width:40%; font-size:11px;">설치관리자 UUID</label>
                        <div style=" width: 60%;">
                            <input name="directorUuid" type="text" style="float:left; width:80%;" required placeholder="설치관리자 UUID를 입력하세요." readonly="readonly"/>
                        </div>
                    </div>
                    <div class="w2ui-field" >
                        <label style="text-align:left; width:40%; font-size:11px;">배포 명</label>
                        <div style=" width: 60%;">
                            <input name="deploymentName" type="text" style="display:inline-block; width:80%;" required placeholder="" readonly="readonly"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align:left; width:40%; font-size:11px;">DIEGO 릴리즈
                        <span class="glyphicon glyphicon glyphicon-question-sign diego-info" style="cursor:pointer;font-size: 14px;color: #157ad0;" data-toggle="popover"  data-trigger="click" data-html="true" title="설치 지원 버전 목록"></span>
                        </label>
                        <div style=" width: 60%;">
                            <select name="diegoReleases" onchange='setcflinuxDisplay(this.value);' style="display:inline-block; width: 80%;">
                                <option value="">Diego 릴리즈를 선택하세요.</option>
                            </select>
                        </div>
                    </div>
                    <div class="w2ui-field" id="cflinux" style="display:none">
                        <label style="text-align:left; width:40%; font-size:11px;">Cflinuxfs2-Rootfs 릴리즈
                        <span class="glyphicon glyphicon glyphicon-question-sign cflinux-info" style="cursor:pointer;font-size: 14px;color: #157ad0;" data-toggle="popover"  data-trigger="click" data-html="true" title="Cflinuxfs2-Rootfs릴리즈 호환성 참조 사이트"></span>
                        </label>
                        <div style=" width: 60%;">
                            <select name="cflinuxfs2rootfsrelease"  style="display:inline-block; width: 80%;">
                                <option value="">Cf-linuxfs2 릴리즈를 선택하세요.</option>
                            </select>
                        </div>
                    </div>
                    <div class="w2ui-field" >
                        <label style="text-align:left; width:40%; font-size:11px;">Garden-Linux 릴리즈
                        <span class="glyphicon glyphicon glyphicon-question-sign gardenRelease-info" style="cursor:pointer;font-size: 14px;color: #157ad0;" data-toggle="popover"  data-trigger="click" data-html="true" title="Garden-Linux 릴리즈 호환성 참조 사이트"></span>
                        </label>
                        <div style=" width: 60%;">
                            <select name="gardenReleaseName"  style="display:inline-block; width: 80%;">
                                <option value="">Garden 릴리즈를 선택하세요.</option>
                            </select>
                        </div>
                    </div>
                    <div class="w2ui-field" id="etcd" style="display:none">
                        <label style="text-align:left; width:40%; font-size:11px;">ETCD 릴리즈
                        <span class="glyphicon glyphicon glyphicon-question-sign etcd-info" style="cursor:pointer;font-size: 14px;color: #157ad0;" data-toggle="popover"  data-trigger="click" data-html="true" title="etcd 릴리즈 호환성 참조 사이트"></span></label>
                        <div style=" width: 60%;">
                            <select name="etcdReleases" style="display:inline-block; width: 80%;">
                                <option value="">Etcd 릴리즈를 선택하세요.</option>
                            </select>
                        </div>
                    </div>
                    <div class="w2ui-field" >
                        <label style="text-align:left; width:40%; font-size:11px;">DIEGO와 연동할 CF 배포명</label>
                        <div id="getCfInfo" style=" width: 60%;">
                            <select name="cfInfo" onchange="setCfDeployFile(this.value);" style="display:inline-block; width: 80%;">
                                <option value="">Diego와 연동 할 CF를 선택 하세요.</option>
                            </select></div>
                        <input name="cfId" type="hidden"/>
                        <input name="cfDeploymentFile" type="hidden"/>
                        <input name="cfReleaseVersion" type="hidden"/>
                        <input name="cfKeyFile" type="hidden"/>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align:left; width:40%; font-size:11px;">PaaS-TA 모니터링
                        <span class="glyphicon glyphicon glyphicon-question-sign paastaMonitoring-info" style="cursor:pointer;font-size: 14px;color: #157ad0;" data-toggle="popover"  data-trigger="click" data-html="true" title="PaaS-TA 모니터링"></span>
                        </label>
                        <div style=" width: 60%;">
                            <input name="paastaMonitoring" type="checkbox" id="paastaMonitoring" onchange="checkPaasTAMonitoringUseYn(this);" disabled />사용
                            <input name="cfPaastaMonitoring" type="hidden" id="cfPaastaMonitoring" />
                        </div>
                    </div>
                </div>
            </div>
            <div class="panel panel-info">    
                <div class="panel-heading"><b>PaaS-TA 모니터링 정보</b></div>
                <div class="panel-body" style="padding:5px 5% 10px 5%;">
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 40%; font-size: 11px;">PaaS-TA 모니터링 DB 서버 IP</label>
                        <div style=" width: 60%;">
                            <input name="cadvisorDriverIp" type="text" style="display:inline-blcok; width: 80%;" disabled placeholder="예)10.0.0.0" />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form>
    <div class="w2ui-buttons" id="defaultInfoButtonDiv" hidden="true">
        <button class="btn" id="defaultPopupBtn" style="float:left; display:none" onclick="saveDefaultInfo('before');">이전</button>
        <button class="btn" style="float:right; padding-right:15%" onclick="$('#defaultInfoForm').submit();">다음>></button>
    </div>
</div>

<!-- Aws/Openstack Network 정보 -->
<div id="networkInfoDiv" style="width:100%; height:100%;" hidden="true">
    <form id="defaultNetworkInfoForm">
        <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
            <ul class="progressStep_5">
                <li class="pass">기본 정보</li>
                <li class="active">네트워크 정보</li>
                <li class="before">리소스 정보</li>
                <li class="before">배포파일 정보</li>
                <li class="before">설치</li>
            </ul>
        </div>
        <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;" id="defaultNetworkInfoDiv_1">
            <div class="panel panel-info" style="position:relative;">
                <div class="panel-heading">
                    <b>Internal 네트워크 정보</b>
                    <div  style="position: absolute;right:5px ;top: 2px;">
                        <a class="btn btn-info btn-sm addInternal" onclick="addInternalNetworkInputs('#defaultNetworkInfoDiv_1', '#defaultNetworkInfoForm');">추가</a>
                    </div>
                </div>
                <div class="panel-body">
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 40%; font-size: 11px;">시큐리티 그룹</label>
                        <div style="width: 60%">
                            <input name="cloudSecurityGroups_0" type="text" style="display:inline-blcok; width:70%;" placeholder="예) diego-security" />
                        </div>
                    </div>
                    <div class="w2ui-field" id ="availabilityZone" style="display:none">
                        <label style="text-align: left; width: 40%; font-size: 11px;">Availability Zone</label>
                        <div style="width: 60%">
                            <input name="availabilityZone_0" type="text" style="display:inline-blcok; width:70%;" placeholder="예) cf-AvaliailityZone" />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:40%;font-size:11px;">서브넷 아이디</label>
                        <div style="width: 60%">
                            <input name="subnetId_0" type="text"  style="display:inline-blcok; width:70%;" placeholder="서브넷 아이디를 입력하세요."/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:40%;font-size:11px;">서브넷 범위</label>
                        <div style="width: 60%">
                            <input name="subnetRange_0" type="text"  style="display:inline-blcok; width:70%;" placeholder="예) 10.0.0.0/24"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:40%;font-size:11px;">게이트웨이</label>
                        <div style="width: 60%">
                            <input name="subnetGateway_0" type="text"  style="display:inline-blcok; width:70%;" placeholder="예) 10.0.0.1"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:40%;font-size:11px;">DNS</label>
                        <div style="width: 60%">
                            <input name="subnetDns_0" type="text"  style="display:inline-blcok; width:70%;" placeholder="예) 8.8.8.8"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 40%; font-size: 11px;">IP할당 제외 대역</label>
                        <div style="width: 60%">
                            <input name="subnetReservedFrom_0" type="text" style="display:inline-block; width:32%;" placeholder="예) 10.0.0.100" />
                            <span style="width: 4%; text-align: center;">&nbsp;&ndash; &nbsp;</span>
                            <input name="subnetReservedTo_0" type="text" style="display:inline-block; width:32%;" placeholder="예) 10.0.0.106" />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 40%; font-size: 11px;">IP할당 대역(최소 15개)</label>
                        <div style="width: 60%">
                            <input name="subnetStaticFrom_0" type="text" style="display:inline-block; width:32%;" placeholder="예) 10.0.0.100" />
                            <span style="width: 4%; text-align: center;">&nbsp;&ndash; &nbsp;</span>
                            <input name="subnetStaticTo_0" type="text" style="display:inline-block; width:32%;" placeholder="예) 10.0.0.106" />
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- 추가 네트워크 div_1 -->
        <div id="defaultNetworkInfoDiv_2" style="margin:15px 0; padding:0 3%;" hidden="true"></div>
        <!-- 추가 네트워크 div_2 -->
        <div id="defaultNetworkInfoDiv_3" style="margin:15px 0; padding:0 3%;" hidden="true"></div>
    </form>
    <div class="w2ui-buttons" id="defaultNetworkInfoButtons" hidden="true">
        <button class="btn" style="float:left;" onclick="saveNetworkInfo('before');">이전</button>
        <button class="btn" style="float:right; padding-right:15%" onclick="$('#defaultNetworkInfoForm').submit();">다음>></button>
    </div>
</div>

<!-- vSphere Network -->
<div id="VsphereNetworkInfoDiv" style="width:100%;height:100%;" hidden="true">
    <form id="vSphereNetworkInfoForm">
        <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
            <ul class="progressStep_5">
                <li class="pass">기본 정보</li>
                <li class="active">네트워크 정보</li>
                <li class="before">리소스 정보</li>
                <li class="before">배포파일 정보</li>
                <li class="before">설치</li>
            </ul>
        </div>
        <div>
        <div style="margin: 15px 0px; padding: 0px 3%; display: block;" id="vSphereNetworkInfoDiv_1">
            <div class="panel panel-info" style="position:relative;">
                <div  class="panel-heading">
                    <b>Internal 네트워크</b>
                    <div style="position: absolute;right: 5px;top: 2px;">
                        <a class="btn btn-info btn-sm addInternal" onclick="addInternalNetworkInputs('#vSphereNetworkInfoDiv_1', '#vSphereNetworkInfoForm');">추가</a>
                    </div>
                </div>
                <div class="panel-body">
                    <div class="w2ui-field">
                        <label class="subnetId" style="text-align: left;width:40%;font-size:11px;">포트 그룹명</label>
                        <div>
                            <input name="subnetId_0" type="text"  style="display:inline-block;width:60%;" required placeholder="포트 그룹명을 입력하세요."/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:40%;font-size:11px;">서브넷 범위</label>
                        <div>
                            <input name="subnetRange_0" type="text"  style="display:inline-block;width:60%;"  required placeholder="예) 10.0.0.0/24"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:40%;font-size:11px;">게이트웨이</label>
                        <div>
                            <input name="subnetGateway_0" type="text"  style="display:inline-block;width:60%;"  required placeholder="예) 10.0.0.1"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:40%;font-size:11px;">DNS</label>
                        <div>
                            <input name="subnetDns_0" type="text"  style="display:inline-block;width:60%;" required placeholder="예) 8.8.8.8"/>
                            <div class="isMessage"></div>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 40%; font-size: 11px;">IP할당 제외 대역</label>
                        <div>
                            <input name="subnetReservedFrom_0" id="subnetStaticFrom" type="url" style="float:left;width:27%;" placeholder="예) 10.0.0.100" />
                            <span style="display:inline-block; width: 6%; text-align: center;">&nbsp;&ndash; &nbsp;</span>
                            <input name="subnetReservedTo_0" id="subnetStaticTo" type="url" style="display:inline-block;width:27%;" placeholder="예) 10.0.0.106" />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 40%; font-size: 11px;">IP할당 대역(최소 15개)</label>
                        <div>
                            <input name="subnetStaticFrom_0" type="url" style="float:left;width:27%;" placeholder="예) 10.0.0.100" />
                            <span style="display:inline-block; width: 6%; text-align: center;">&nbsp;&ndash; &nbsp;</span>
                            <input name="subnetStaticTo_0" type="url" style="display:inline-block; width:27%;" placeholder="예) 10.0.0.106" />
                        </div>
                    </div>
                </div>
            </div>
            </div>
            <!-- 추가 네트워크 div_1 -->
            <div  id="vSphereNetworkInfoDiv_2" style="margin:15px 0; padding:0 3%;" hidden="true"></div>
            <div  id="vSphereNetworkInfoDiv_3" style="margin:15px 0; padding:0 3%;" hidden="true"></div>
        </div><br/>
    <div class="w2ui-buttons" id="VsphereNetworkInfoButtons" hidden="true">
        <button class="btn" style="float: left;" onclick="saveNetworkInfo('before', '#vSphereNetworkInfoForm');" >이전</button>
        <button class="btn" style="float: right; padding-right: 15%" onclick="$('#vSphereNetworkInfoForm').submit();" >다음>></button>
    </div>
    </form>
</div>

<!-- google Network 설정 DIV -->
<div id="googleNetworkInfoDiv" style="width: 100%; height: 100%;" hidden="true">
    <form id="googleNetworkInfoForm">
        <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
            <ul class="progressStep_5">
                <li class="pass">기본 정보</li>
                <li class="active">네트워크 정보</li>
                <li class="before">리소스 정보</li>
                <li class="before">배포파일 정보</li>
                <li class="before install">설치</li>
            </ul>
        </div>
        <div class="w2ui-page page-0" id="googleNetworkInfoDiv_1" style="margin-top:15px;padding:0 3%;">
            <div class="panel panel-info">
                <div  class="panel-heading" style="position:relative;">
                    <b>Internal 네트워크</b>
                    <div style="position: absolute;right: 10px ;top: 2px; ">
                        <a class="btn btn-info btn-sm addInternal" onclick="addInternalNetworkInputs('#googleNetworkInfoDiv_1', '#googleNetworkInfoForm');">추가</a>
                    </div>
                </div>
                <div class="panel-body">
                    <div class="w2ui-field">
                        <label style="text-align: left;width:40%;font-size:11px;">네트워크 명</label>
                        <div style="width: 60%">
                            <input name="networkName_0" type="text" style="display:inline-blcok; width:70%;" placeholder="네트워크명을 입력하세요."/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:40%;font-size:11px;">서브넷 명</label>
                        <div style="width: 60%">
                            <input name="subnetId_0" type="text"  style="display:inline-blcok; width:70%;" placeholder="서브넷 아이디를 입력하세요."/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:40%;font-size:11px;">방화벽 규칙</label>
                        <div style="width: 60%">
                            <input name="cloudSecurityGroups_0" type="text"  style="display:inline-blcok; width:70%;" placeholder="예) internet, cf-security"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:40%;font-size:11px;">영역</label>
                        <div style="width: 60%">
                            <input name="availabilityZone_0" type="text"  style="display:inline-blcok; width:70%;" placeholder="예) asia-northeast1-a"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:40%;font-size:11px;">서브넷 범위</label>
                        <div style="width: 60%">
                            <input name="subnetRange_0" type="text"  style="display:inline-blcok; width:70%;" placeholder="예) 10.0.0.0/24"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:40%;font-size:11px;">게이트웨이</label>
                        <div style="width: 60%">
                            <input name="subnetGateway_0" type="text"  style="display:inline-blcok; width:70%;" placeholder="예) 10.0.0.1"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left;width:40%;font-size:11px;">DNS</label>
                        <div style="width: 60%">
                            <input name="subnetDns_0" type="text"  style="display:inline-blcok; width:70%;" placeholder="예) 8.8.8.8"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 40%; font-size: 11px;">IP할당 제외 대역</label>
                        <div style="width: 60%">
                            <input name="subnetReservedFrom_0" id="subnetReservedFrom_0" type="text" style="display:inline-block; width:32%;" placeholder="예) 10.0.0.100" />
                            <span style="width: 4%; text-align: center;">&nbsp;&ndash; &nbsp;</span>
                            <input name="subnetReservedTo_0" id="subnetReservedTo_0" type="text" style="display:inline-block; width:32%;" placeholder="예) 10.0.0.106" />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 40%; font-size: 11px;">IP할당 대역(최소 20개)</label>
                        <div style="width: 60%">
                            <input name="subnetStaticFrom_0" id="subnetStaticFrom_0" type="text" style="display:inline-block; width:32%;" placeholder="예) 10.0.0.100" />
                            <span style="width: 4%; text-align: center;">&nbsp;&ndash; &nbsp;</span>
                            <input name="subnetStaticTo_0" id="subnetStaticTo_0" type="text" style="display:iinline-block; width:32%;" placeholder="예) 10.0.0.106" />
                        </div>
                    </div>
                </div>
            </div>
            <!-- 추가 네트워크 div_1 -->
            <div  id="googleNetworkInfoDiv_2" hidden="true"></div>
            <div  id="googleNetworkInfoDiv_3" hidden="true"></div>
        </div>
    </form>
    <div class="w2ui-buttons" id="googleNetworkInfoButtons" hidden="true">
        <button class="btn" style="float: left;" onclick="saveNetworkInfo('before', '#googleNetworkInfoForm');" >이전</button>
        <button class="btn" style="float: right; padding-right: 15%" onclick="$('#googleNetworkInfoForm').submit();" >다음>></button>
    </div>
</div>


<!-- Resource  설정 DIV -->
<div id="resourceInfoDiv" style="width:100%; height:100%;" hidden="true">
    <form id="resourceInfoForm">
        <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
            <ul class="progressStep_5">
                <li class="pass">기본 정보</li>
                <li class="pass">네트워크 정보</li>
                <li class="active">리소스 정보</li>
                <li class="before">배포파일 정보</li>
                <li class="before">설치</li>
            </ul>
        </div>
        <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
            <div class="panel panel-info">    
                <div class="panel-heading" style="position:relative"><b>리소스 정보</b>
                    <div style="position: absolute;right: 10px ;top: 2px;  ">
                        <a class="btn btn-info btn-sm" onclick="resourceAdvancedSettingsPop();">고급 기능</a>
                    </div>
                </div>    
                <div class="panel-body" style="padding:5px 5% 10px 5%;">
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 40%; font-size: 11px;">Stemcell</label>
                        <div style="width: 60%">
                            <div>
                                <select name="stemcells" style="width: 80%;display: inline-block;"></select>
                            </div>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 40%; font-size: 11px;">VM 비밀번호</label>
                        <div style="width: 60%">
                            <input name="boshPassword" type="text" style="display:inline-block; width: 80%;" required placeholder="VM 비밀번호를 입력하세요." />
                        </div>
                    </div>
                </div>
            </div>
            <div class="panel panel-info"  style="margin-top:20px;">    
                <div class="panel-heading"><b>Small Instance Type</b></div>
                <div class="panel-body"  style="padding:5px 5% 10px 5%;">
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 40%; font-size: 11px;">Flavor</label>
                        <div style="width: 60%">
                            <input name="smallFlavor" type="text" style="display:inline-block; width: 80%;" required placeholder="Small Instance Type을 입력하세요."  />
                        </div>
                    </div>
                </div>
            </div>
            <div class="panel panel-info"  style="margin-top:20px;">    
                <div class="panel-heading"><b>Medium Instance Type</b></div>
                <div class="panel-body"  style="padding:5px 5% 10px 5%;" >
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 40%; font-size: 11px;">Flavor</label>
                        <div style="width: 60%">
                            <input name="mediumFlavor" type="text" style="display:inline-block; width: 80%;" required placeholder="Medium Instance Type을 입력하세요."  />
                        </div>
                    </div>
                </div>
            </div>
            <div class="panel panel-info"  style="margin-top:20px;">    
                <div class="panel-heading"><b>Large Instance Type</b></div>
                <div class="panel-body"  style="padding:5px 5% 10px 5%;">
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 40%; font-size: 11px;">Flavor</label>
                        <div style="width: 60%">
                            <input name="largeFlavor" type="text" style="display:inline-block; width: 80%;" required placeholder="Large Instance Type을 입력하세요."  />
                        </div>
                    </div>
                </div>
            </div>
            <div class="panel panel-info"  style="margin-top:20px;">    
                <div class="panel-heading"><b>Cell Instance Type</b></div>
                <div class="panel-body"  style="padding:5px 5% 10px 5%;">
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 40%; font-size: 11px;">Flavor</label>
                        <div style="width: 60%">
                            <input name="runnerFlavor" type="text" style="display:inline-block; width: 80%;" required placeholder="Cell Instance Type을 입력하세요."  />
                        </div>
                    </div>
                </div>
            </div>
            <div class="w2ui-buttons">
                <button class="btn" style="display:none; float: right; margin-top:10px;" id="keyBtn" onclick="createKeyConfirm();" >Key 생성</button>
            </div>
        </div>
        <div class="w2ui-buttons" id="resourceInfoButtons" hidden="true">
            <button class="btn" style="float:left;" onclick="saveResourceInfo('before');">이전</button>
            <button class="btn" style="float: right; padding-right: 15%" onclick="$('#resourceInfoForm').submit();">다음>></button>
        </div>
    </form>
</div>

<!--  vSphere Resource -->
<div id="vSphereResourceInfoDiv" style="width: 100%; height: 100%;" hidden="true">
    <form id="vSphereResourceInfoForm">
        <div style="margin-left: 2%;display:inline-block;width: 98%;padding-top:20px;">
            <ul class="progressStep_5">
                <li class="pass">기본 정보</li>
                <li class="pass">네트워크 정보</li>
                <li class="active">리소스 정보</li>
                <li class="before">배포파일 정보</li>
                <li class="before">설치</li>
            </ul>
        </div>
        <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
            <div class="panel panel-info">    
                <div class="panel-heading" style="position:relative"><b>리소스 정보</b>
                    <div style="position: absolute;right: 10px ;top: 2px;  ">
                        <a class="btn btn-info btn-sm" onclick="resourceAdvancedSettingsPop();">고급 기능</a>
                    </div>
                </div>    
                <div class="panel-body" style="padding:5px 5% 10px 5%;">
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 40%; font-size: 11px;">Stemcell</label>
                        <div style="width: 80%">
                            <div>
                                <select name="stemcells" style="width: 60%;display: inline-block;"></select>
                            </div>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; width: 40%; font-size: 11px;">VM 비밀번호</label>
                        <div style="width: 60%">
                            <input name="boshPassword" type="text" style="display:inline-block; width: 80%;"  required placeholder="VM 비밀번호를 입력하세요." />
                        </div>
                    </div>
                </div>
            </div>
            <div class="panel panel-info">    
                <div class="panel-heading"><b>Small Resource Type</b></div>
                <div class="panel-body"  style="padding:5px 5% 10px 5%;">
                    <div class="w2ui-field">
                        <label style="text-align: left;  width: 40%;">Small Type Ram</label>
                        <div style="width: 60%">
                            <input name="smallFlavorRam" type="text" style="display:inline-block; width: 80%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Ram을 입력하세요. 예) 1024"  />
                        </div>
                    </div>
                    <div class="w2ui-field">    
                        <label style="text-align: left;  width: 40%; ">Small Type Disk</label>
                        <div style="width: 60%">
                            <input name="smallFlavorDisk" type="text" style="display:inline-block; width: 80%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Disk를 입력하세요. 예) 4096"  />
                        </div>
                    </div>
                    <div class="w2ui-field">    
                        <label style="text-align: left;  width: 40%;">Small Type Cpu</label>
                        <div style="width: 60%">
                            <input name="smallFlavorCpu" type="text" style="display:inline-block; width: 80%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Cpu를 입력하세요. 예) 1"  />
                        </div>
                    </div>
                </div>
            </div>
            <div class="panel panel-info">    
                <div class="panel-heading"><b>Medium Resource Type</b></div>
                <div class="panel-body"  style="padding:5px 5% 10px 5%;" >
                    <div class="w2ui-field">
                        <label style="text-align: left;  width: 40%;">Medium Type Ram</label>
                        <div style="width: 60%">
                            <input name="mediumFlavorRam" type="text" style="display:inline-block; width: 80%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Ram을 입력하세요. 예) 1024"  />
                        </div>
                    </div>
                    <div class="w2ui-field">    
                        <label style="text-align: left;  width: 40%;">Medium Type Disk</label>
                        <div style="width: 60%">
                            <input name="mediumFlavorDisk" type="text" style="display:inline-block; width: 80%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Disk를 입력하세요. 예) 4096"  />
                        </div>
                    </div>
                    <div class="w2ui-field">    
                        <label style="text-align: left;  width: 40%;">Medium Type Cpu</label>
                        <div style="width: 60%">
                            <input name="mediumFlavorCpu" type="text" style="display:inline-block; width: 80%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Cpu를 입력하세요. 예) 1"  />
                        </div>
                    </div>
                </div>
            </div>
            <div class="panel panel-info">    
                <div class="panel-heading"><b>Large Resource Type</b></div>
                <div class="panel-body"  style="padding:5px 5% 10px 5%;">
                    <div class="w2ui-field">
                        <label style="text-align: left;  width: 40%;">Large Type Ram</label>
                        <div style="width: 60%">
                            <input name="largeFlavorRam" type="text" style="display:inline-block; width: 80%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Ram을 입력하세요. 예) 1024"  />
                        </div>
                    </div>
                    <div class="w2ui-field">    
                        <label style="text-align: left;  width: 40%;">Large Type Disk</label>
                        <div style="width: 60%">
                            <input name="largeFlavorDisk" type="text" style="display:inline-block; width: 80%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Disk를 입력하세요. 예) 10240 "  />
                        </div>
                    </div>
                    <div class="w2ui-field">    
                        <label style="text-align: left;  width: 40%;">Large Type Cpu</label>
                        <div style="width: 60%">
                            <input name="largeFlavorCpu" type="text" style="display:inline-block; width: 80%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Cpu를 입력하세요. 예) 1"  />
                        </div>
                    </div>
                    </div>
                </div>
            <div class="panel panel-info">    
                <div class="panel-heading"><b>Cell Resource Type</b></div>
                <div class="panel-body"  style="padding:5px 5% 10px 5%;">
                    <div class="w2ui-field">
                        <label style="text-align: left;  width: 40%;">Cell Type Ram</label>
                        <div style="width: 60%">
                            <input name="runnerFlavorRam" type="text" style="display:inline-block; width: 80%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Ram을 입력하세요. 예) 16384"  />
                        </div>
                    </div>
                    <div class="w2ui-field">    
                        <label style="text-align: left;  width: 40%;">Cell Type Disk</label>
                        <div style="width: 60%">
                            <input name="runnerFlavorDisk" type="text" style="display:inline-block; width: 80%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Disk를 입력하세요. 예) 32768"  />
                        </div>
                    </div>
                    <div class="w2ui-field">    
                        <label style="text-align: left;  width: 40%;">Cell Type Cpu</label>
                        <div style="width: 60%">
                            <input name="runnerFlavorCpu" type="text" style="display:inline-block; width: 80%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Cpu를 입력하세요. 예) 2"  />
                        </div>
                    </div>
                </div>
            </div>
            <div class="w2ui-buttons">
                <button class="btn" style="display:none; float: right; margin-bottom:10px;" id="keyBtn" onclick="createKeyConfirm();" >Key 생성</button>
            </div>
        </div>
        <div class="w2ui-buttons" id="vSphereResourceInfoButtons" hidden="true">
            <button class="btn" style="float:left;" onclick="saveResourceInfo('before');">이전</button>
            <button class="btn" style="float: right; padding-right: 15%" onclick="$('#vSphereResourceInfoForm').submit();">다음>></button>
        </div>
    </form>
</div>

<!-- DIEGO 고급 설정 화면 -->
<div class="w2ui-buttons" id="diegoDetailButtons" hidden="true">
    <button class="btn" id="" style="" onclick="saveDiegoJobsInfo();">저장</button>
    <button class="btn" style="" onclick="jobPopupComplete();">닫기</button>
</div>
<div id="diegoDetailPopDiv" hidden="true" style="width:100%;">
    <form id="diegoDetailForm" style="height:100%;">
    </form>
</div>

<!-- 배포파일 정보 -->
<div id="deployDiv" style="width: 100%; height: 100%;" hidden="true">
    <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
        <ul class="progressStep_5">
            <li class="pass">기본 정보</li>
            <li class="pass">네트워크 정보</li>
            <li class="pass">리소스 정보</li>
            <li class="active">배포파일 정보</li>
            <li class="before">설치</li>
        </ul>
    </div>
    <div style="width:95%;height:82%;float:left;display: inline-block;margin-top:1%;">
        <textarea id="deployInfo" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:3%;" readonly="readonly"></textarea>
    </div>
    <div class="w2ui-buttons" id="deployDivButtons" hidden="true">
        <button class="btn" style="float: left;"onclick="resourcePopupSel();">이전</button>
        <button class="btn" style="float: right; padding-right: 15%" onclick="diegoDeploy('after');">다음>></button>
    </div>
</div>

<!-- diego 설치화면 -->
<div id="installDiv" style="width:100%; height:100%;" hidden="true">
    <div style="margin-left:2%;display:inline-block;width:97%;padding-top:20px;">
        <ul class="progressStep_5">
            <li class="pass">기본 정보</li>
            <li class="pass">네트워크 정보</li>
            <li class="pass">리소스 정보</li>
            <li class="pass">배포파일 정보</li>
            <li class="active">설치</li>
        </ul>
    </div>
    <div style="width:95%;height:84%;float:left;display:inline-block;margin-top: 10px;">
        <textarea id="installLogs" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color:#FFF;margin-left:1%" readonly="readonly"></textarea>
    </div>
    <div class="w2ui-buttons" id="installButtons" hidden="true">
        <button class="btn" id="deployPopupBtn" style="float:left;" onclick="deployPopup()" disabled>이전</button>
        <button class="btn" style="float:right; padding-right:15%" onclick="popupComplete();">닫기</button>
    </div>
</div>
<!-- End Popup -->
<script type="text/javascript" src="<c:url value='/js/rules/diego/diego_default.js?v=1'/>"></script>
<script type="text/javascript" src="<c:url value='/js/rules/diego/diego_network_default.js?v=2'/>"></script>
<script type="text/javascript" src="<c:url value='/js/rules/diego/diego_network_vsphere.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/rules/diego/diego_network_google.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/rules/diego/diego_resource.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/rules/diego/diego_vSphereResource.js'/>"></script>
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
