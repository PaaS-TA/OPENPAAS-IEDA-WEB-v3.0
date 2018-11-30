<%
/* =================================================================
 * 작성일 : 2018.07
 * 작성자 : 이정윤
 * 상세설명 : CF Deploymnet 인스턴스 정보 관리 화면
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri = "http://www.springframework.org/tags" %>

<script type="text/javascript">
var text_required_msg = '<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
var select_required_msg = '<spring:message code="common.select.vaildate.required.message"/>';//을(를) 선택하세요.
var search_data_fail_msg = '클라우드 인프라 환경을 선택하세요.';
var instanceConfigInfo = [];//인스턴스 정보
var iaas = "";
var jobsInfo=[];
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
         *  설명 : 인스턴스 정보 목록 Grid
        *********************************************************/
        grid: {
            name: 'instance_grid',
            header: '<b>Instance 정보</b>',
            method: 'GET',
                multiSelect: false,
            show: {
                    selectColumn: true,
                    footer: true},
            style: 'text-align: center',
            columns:[
                   { field: 'recid', hidden: true },
                   { field: 'id', hidden: true },
                   { field: 'cfDeploymentName', hidden: true },
                   { field: 'cfDeploymentVersion', hidden: true },
                   { field: 'instanceConfigName', caption: '인스턴스 정보 별칭', size:'120px', style:'text-align:center;' },
                   { field: 'iaasType', caption: '인프라 환경 타입', size:'100px', style:'text-align:center;' ,render: function(record){ 
                       if(record.iaasType.toLowerCase() == "aws"){
                           return "<img src='images/iaasMgnt/aws-icon.png' width='80' height='30' />";
                       }else if (record.iaasType.toLowerCase() == "openstack"){
                           return "<img src='images/iaasMgnt/openstack-icon.png' width='90' height='35' />";
                       }
                   }},
                   { field: 'adapter', caption: 'Adapter', size:'90px', style:'text-align:center;'},
                   { field: 'api', caption: 'Api', size:'90px', style:'text-align:center;'},
                   { field: 'ccWorker', caption: 'CC-Worker', size:'90px', style:'text-align:center;'},
                   { field: 'consul', caption: 'Consul', size:'90px', style:'text-align:center;'},
                   { field: 'theDatabase', caption: 'Database', size:'90px', style:'text-align:center;'},
                   { field: 'diegoApi', caption: 'Diego-Api', size:'90px', style:'text-align:center;'},
                   { field: 'diegoCell', caption: 'Diego-Cell', size:'90px', style:'text-align:center;'},
                   { field: 'doppler', caption: 'Doppler', size:'90px', style:'text-align:center;'},
                   { field: 'haproxy', caption: 'Haproxy', size:'90px', style:'text-align:center;'},
                   { field: 'logApi', caption: 'Log-Api', size:'90px', style:'text-align:center;'},
                   { field: 'nats', caption: 'Nats', size:'90px', style:'text-align:center;'},
                   { field: 'scheduler', caption: 'Scheduler', size:'90px', style:'text-align:center;'},
                   { field: 'router', caption: 'Router', size:'90px', style:'text-align:center;'},
                   { field: 'singletonBlobstore', caption: 'Singleton-Blobstore', size:'130px', style:'text-align:center;'},
                   { field: 'tcpRouter', caption: 'Tcp-Router', size:'90px', style:'text-align:center;'},
                   { field: 'uaa', caption: 'Uaa', size:'90px', style:'text-align:center;'}
                  ],
            onSelect : function(event) {
                event.onComplete = function() {
                    $('#deleteBtn').attr('disabled', false);
                    settingInstanceInfo();
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
    $('#instance_grid').w2layout(resourceLayout.layout2);
    w2ui.layout2.content('left', $().w2grid(resourceLayout.grid));
    w2ui['layout2'].content('main', $('#regPopupDiv').html());
    doSearch();
    //delete Btn
    $("#deleteBtn").click(function(){
        if($("#deleteBtn").attr('disabled') == "disabled") return;
        var selected = w2ui['instance_grid'].getSelection();
        if( selected.length == 0 ){
            w2alert("선택된 정보가 없습니다.", "인스턴스 삭제");
            return;
        }
        else {
            var record = w2ui['instance_grid'].get(selected);
            w2confirm({
                title        : "인스턴스 정보",
                msg            : "인스턴스 정보 ("+record.instanceConfigName + ")을 삭제하시겠습니까?",
                yes_text    : "확인",
                no_text        : "취소",
                yes_callBack: function(event){
                    deleteHbCfDeploymnetInstanceConfigInfo(record.recid, record.instanceConfigName);
                },
                no_callBack    : function(){
                    w2ui['instance_grid'].clear();
                    doSearch();
                }
            });
        }
    });
});

/********************************************************
 * 설명 : CF Deployment version 명 조회
 * 기능 : getCfDeployment
 *********************************************************/
 function getCfDeploymentVersionList(iaasType) {
    if(iaasType!=""){
        var option ="";
        $.ajax({
            type : "GET",
            url :"/common/deploy/list/releaseInfo/cfDeployment/"+iaasType, 
            contentType : "application/json",
            success : function(data, status) {
                releases = new Array();
                if( data != null){
                    option = "<option value=''>CF Deployment를 선택하세요.</option>";
                    data.map(function(obj) {
                          if( instanceConfigInfo.cfDeploymentName == obj.releaseType && instanceConfigInfo.cfDeploymentVersion == obj.minReleaseVersion){
                           option += "<option value='"+obj.releaseType+"/"+obj.minReleaseVersion+"' selected>"+obj.releaseType+"/"+obj.minReleaseVersion+"</option>";
                        }else{
                        option += "<option value='"+obj.releaseType+"/"+obj.minReleaseVersion+"'>"+obj.releaseType+"/"+obj.minReleaseVersion+"</option>";    
                        } 
                    });
                }
                $("select#cfDeploymentVersion").html(option);
            },
            error : function(e, status) {
                w2popup.unlock();
                w2alert("Cf Deployment List 를 가져오는데 실패하였습니다.", "CF Deployment");
            }
        });
    } else {
        $("select[name=cfDeploymentVersion]").html("<option value='' >CF Deployment를 선택하세요.</option>");
    }
} 

/********************************************************
 * 설명 : 인스턴스 수정 정보 설정
 * 기능 : settingInstanceInfo
 *********************************************************/
function settingInstanceInfo(){
    var selected = w2ui['instance_grid'].getSelection();
    var record = w2ui['instance_grid'].get(selected);
    if(record == null) {
        w2alert("Instance 정보 설정 중 에러가 발생 했습니다.");
        return;
    }
    iaas = record.iaasType;
    instanceConfigInfo = {
        cfDeploymentName : record.cfDeploymentName,
        cfDeploymentVersion : record.cfDeploymentVersion
    };
    getCfDeploymentVersionList(iaas);
    settingCfJobs(record.cfDeploymentName+"/"+record.cfDeploymentVersion, "settingInfo", record);
    
    $("input[name=instanceInfoId]").val(record.recid);
    $("input[name=instanceConfigName]").val(record.instanceConfigName);
    $("select[name=iaasType]").val(record.iaasType);
 }
 
/********************************************************
 * 설명 : CF Jobs 정보 설정
 * 기능 : settingCfJobs
 *********************************************************/
function settingCfJobs(value, type, record){
    if( checkEmpty(value) || value == "undefined/undefined"){
        w2alert("CF Deployment 버전을 확인하세요.");
    } else {
        var release_version = value.split("/")[1];
        var deploy_type = "DEPLOY_TYPE_CF";
        $.ajax({
            type : "GET",
            url : "/deploy/hbCfDeployment/instanceConfig/job/list/"+release_version+"/"+ deploy_type,
            contentType : "application/json",
            success : function(data, status) {
                if( data != null ){
                    var div = "";
                    var html = "";
                    html += '<div class="panel-body">';
                    html += '<div id="cfJobListDiv">';
                    html += '<p style="color:red;">- 고급 설정 값을 변경하지 않을 경우 아래에 입력 된 기본 값으로 자동 설정됩니다.</p>';
                    html += '<p style="color:red;">- 해당 Job의 인스턴스 수는 0-100까지 입력하실 수 있습니다.</p>';
                    
                    for( var i=0; i<data.length; i++ ){
                        html += setJobSettingHtml(data[i]);
                    }
                    
                    html +='</div></div>';
                    $("#instanceSet").html(html);
                    if(type=="settingInfo"){
                        $("input[name=adapter]").val(record.adapter);
                        $("input[name=api]").val(record.api);
                        $("input[name=cc-worker]").val(record.ccWorker);
                        $("input[name=consul]").val(record.consul);
                        $("input[name=database]").val(record.theDatabase);
                        $("input[name=diego-api]").val(record.diegoApi);
                        $("input[name=diego-cell]").val(record.diegoCell);
                        $("input[name=doppler]").val(record.doppler);
                        $("input[name=haproxy]").val(record.haproxy);
                        $("input[name=log-api]").val(record.logApi);
                        $("input[name=scheduler]").val(record.scheduler);
                        $("input[name=nats]").val(record.nats);
                        $("input[name=router]").val(record.router);
                        $("input[name=singleton-blobstore]").val(record.singletonBlobstore);
                        $("input[name=tcp-router]").val(record.tcpRouter);
                        $("input[name=uaa]").val(record.uaa);
                    }
                }
            },
            error : function(e, status) {
                w2alert("CF Deployment 버전을 확인하세요.", "Instance ");
            }
        });
    }
}

/********************************************************
 * 설명 : CF 고급 설정 HTML 설정
 * 기능 : setJobSettingHtml
 *********************************************************/
function setJobSettingHtml(data){
    var html = "";
        html += '<ul class="w2ui-field" style="border: 1px solid #c5e3f3;padding: 10px;">';
        html +=     '<li style="display:inline-block; width:35%;">';
        html +=         '<label style="text-align: left;font-size:11px;">'+data.job_name+'</label>';
        html +=     '</li>';
        html +=     '<li style="display:inline-block; width:60%;vertical-align:middle; line-height:3; text-align:right;">';
        html +=         '<ul>';
        html +=             '<li>';
        html +=                 '<label style="display:inline-block;">인스턴스 수 : </label>&nbsp;';
        html +=                 '<input class="form-control" style="width:30%; display:inline-block;" onblur="instanceControl(this);" onfocusin="instanceControl(this);" onfocusout="instanceControl(this);" maxlength="100" type="number" min="0" max="100" value="1" id="'+data.id+'" name="'+data.job_name+'"/>';
        html +=              '</li>';
        html +=         '</ul>';
        html +=     '</li>';
        html += '</ul>';
    return html;
}

/********************************************************
 * 설명 : CF Jobs 유효성 추가
 * 기능 : instanceControl
 *********************************************************/
function instanceControl(e){
     if ( e.value != "" && e.value >=0 && e.value<=100) {
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
             $(e).parent().append("<p>100까지 숫자만 입력 가능 합니다.</p>"); 
         }
     }
}

/********************************************************
 * 설명 : 인스턴스 정보 목록 조회
 * 기능 : doSearch
 *********************************************************/
function doSearch() {
    instanceConfigInfo="";//인스턴스 정보
    iaas = "";
    resetForm();
    
    w2ui['instance_grid'].clear();
    w2ui['instance_grid'].load('/deploy/hbCfDeployment/instanceConfig/list');
    doButtonStyle(); 

}

/********************************************************
 * 설명 : 인스턴스 정보 등록
 * 기능 : saveResourceInfo
 *********************************************************/
function registHbCfDeploymnetInstanceConfigInfo() {
    var cfdeploymentInfo = $("select[name=cfDeploymentVersion]").val();
    var cfDeploymentName = cfdeploymentInfo.split("/")[0];
    var cfDeploymentVersion = cfdeploymentInfo.split("/")[1];
    
    instanceConfigInfo = {
            id                 : $("input[name=instanceInfoId]").val(),
            iaasType           : $("select[name=iaasType]").val(),
            cfDeploymentName   : cfDeploymentName,
            cfDeploymentVersion: cfDeploymentVersion,
            instanceConfigName : $("input[name=instanceConfigName]").val(),
            adapter            : $("input[name=adapter]").val(),
            api                : $("input[name=api]").val(),
            ccWorker           : $("input[name=cc-worker]").val(),
            consul             : $("input[name=consul]").val(),
            theDatabase        : $("input[name=database]").val(),
            scheduler          : $("input[name=scheduler]").val(),
            diegoApi           : $("input[name=diego-api]").val(),
            diegoCell          : $("input[name=diego-cell]").val(),
            doppler            : $("input[name=doppler]").val(),
            haproxy            : $("input[name=haproxy]").val(),
            logApi             : $("input[name=log-api]").val(),
            nats               : $("input[name=nats]").val(),
            router             : $("input[name=router]").val(),
            singletonBlobstore : $("input[name=singleton-blobstore]").val(),
            tcpRouter          : $("input[name=tcp-router]").val(),
            uaa                : $("input[name=uaa]").val()
    }
        //Server send Cf Info
        $.ajax({
            type : "PUT",
            url : "/deploy/hbCfDeployment/instanceConfig/save",
            contentType : "application/json",
            data : JSON.stringify(instanceConfigInfo),
            success : function(data, status) {
                doSearch();
            },
            error : function(e, status) {
                w2popup.unlock();
                var errorResult = JSON.parse(e.responseText);
                w2alert(errorResult.message, "기본 정보 저장");
            }
        }); 
}

/********************************************************
 * 설명 : 인스턴스 정보 삭제
 * 기능 : deleteHbCfDeploymnetInstanceConfigInfo
 *********************************************************/
function deleteHbCfDeploymnetInstanceConfigInfo(id, instanceConfigName){
     var instanceRequestInfo = {
            id: id,
            instanceConfigName: instanceConfigName
     };
     $.ajax({
        type: "DELETE",
        url: "/deploy/hbCfDeployment/instanceConfig/delete",
        data : JSON.stringify(instanceRequestInfo),
        contentType : "application/json",
        async : true,
        success : function(data, status) {
            doSearch();
        },
        error : function(request, status, error) {
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message);
        }
     });
 }

/********************************************************
 * 설명 : 초기 버튼 스타일
 * 기능 : doButtonStyle
 *********************************************************/
function doButtonStyle() {
    $('#deleteBtn').attr('disabled', true);
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
    $().w2destroy('instance_grid');
}
/********************************************************
 * 설명 : 인스턴스 정보 리셋
 * 기능 : resetForm
 *********************************************************/
function resetForm(status){
    $(".panel-body").find("p").remove();
    $(".panel-body").children().children().children().css("borderColor", "#bbb");
    $("input[name=instanceConfigName]").val("");
    $("input[name=instanceInfoId]").val("");
    $("select[name=iaasType]").val("");
    instanceConfigInfo = [];
    $("select[name=cfDeploymentVersion]").html("<option value='' >CF Deployment를 선택하세요.</option>");
    if(status=="reset"){
        w2ui['instance_grid'].clear();
        doSearch();
    }
    $("#instanceSet").html("");
    document.getElementById("cfDetailForm").reset();
}

</script>
<div id="main">
    <div class="page_site">이종 CF Deployment > <strong>Instance 정보 관리</strong></div>
    <!-- 사용자 목록-->
    <div class="pdt20">
        <div class="title fl"> 인스턴스 정보 목록</div>
    </div>
    <div id="instance_grid" style="width:100%;  height:700px;"></div>

</div>

<div id="regPopupDiv" hidden="" >
  <form id="cfDetailForm" action="POST" >
    <input type="hidden" name="instanceInfoId" />
        <div class="w2ui-page page-0" style="">
           <div class="panel panel-default">
               <div class="panel-heading"><b>인스턴스 정보</b></div>
               <div class="panel-body" style="height:270px; overflow-y:auto;">
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">인스턴스 정보 별칭</label>
                       <div>
                           <input class="form-control" name = "instanceConfigName" type="text"  maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="인스턴스 별칭을 입력 하세요."/>
                       </div>
                   </div>
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">클라우드 인프라 환경</label>
                       <div>
                           <select class="form-control" onchange="getCfDeploymentVersionList(this.value);" name="iaasType" style="width: 320px; margin-left: 20px;">
                               <option value="">인프라 환경을 선택하세요.</option>
                               <option value="aws">AWS</option>
                               <option value="openstack">Openstack</option>
                           </select>
                       </div>
                   </div>
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">CF Deployment 버전 명</label>
                       <div>
                           <select class="form-control" id="cfDeploymentVersion" onchange ="settingCfJobs(this.value)" name="cfDeploymentVersion"  style="width: 320px; margin-left: 20px;">
                               <option value=""> 클라우드 인 환경을  먼저 선택하세요.</option>
                           </select>
                       </div>
                   </div>
               </div>
               <div class="panel-heading" style="position:relative"><b>인스턴스 수 설정</b></div>
                   <div class="panel-body" id="instanceSet" style="padding:5px 5% 10px 5%;">
                   </div> 
           </div>
        </div>
    </form>
    
    <div id="regPopupBtnDiv" style="text-align: center; margin-top: 5px;">
        <sec:authorize access="hasAuthority('DEPLOY_HBCF_INSTANCE_ADD')">
            <span id="addBtn" onclick="$('#cfDetailForm').submit();" class="btn btn-primary">등록</span>
        </sec:authorize>
        <span id="resetBtn" onclick="resetForm('reset');" class="btn btn-info">취소</span>
        <sec:authorize access="hasAuthority('DEPLOY_HBCF_INSTANCE_DELETE')">
            <span id="deleteBtn" class="btn btn-danger">삭제</span>
        </sec:authorize>
    </div>

</div>
<script>
$(function() {
    
    
    $("#cfDetailForm").validate({
        ignore : [],
        //onfocusout: function(element) {$(element).valid()},
        rules: {
            instanceConfigName: { 
                required: function(){
                    return checkEmpty( $("input[name=instanceConfigName]").val() );
                }
            }, iaasType: { 
                required: function(){
                    return checkEmpty( $("select[name=iaasType]").val() );
                }
            }, adapter: {
                required: function(){
                    return checkEmpty( $("input[name=adapter]").val() );
                }
            }, ccWorker: {
                required: function(){
                    return checkEmpty( $("input[name=cc_worker]").val() );
                }
            }, consul: {
                required: function(){
                    return checkEmpty( $("input[name=consul]").val() );
                }
            }, theDatabase: {
                required: function(){
                    return checkEmpty( $("input[name=theDatabase]").val() );
                }
            }, diegoApi: {
                required: function(){
                    return checkEmpty( $("input[name=diego_api]").val() );
                }
            }, diegoCell: {
                required: function(){
                    return checkEmpty( $("input[name=diego_cell]").val() );
                }
            }, doppler: {
                required: function(){
                    return checkEmpty( $("input[name=doppler]").val() );
                }
            }, haproxy: {
                required: function(){
                    return checkEmpty( $("input[name=ha_proxy]").val() );
                }
            }, logApi: {
                required: function(){
                    return checkEmpty( $("input[name=log_api]").val() );
                }
            }, nats: {
                required: function(){
                    return checkEmpty( $("input[name=nats]").val() );
                }
            }, router: {
                required: function(){
                    return checkEmpty( $("input[name=router]").val() );
                }
            }, singletonBlobstore: {
                required: function(){
                    return checkEmpty( $("input[name=singleton_blobstore]").val() );
                }
            }, tcpRouter: {
                required: function(){
                    return checkEmpty( $("input[name=tcp_router]").val() );
                }
            }, uaa: {
                required: function(){
                    return checkEmpty( $("input[name=uaa]").val() );
                }
            }, cfDeploymentVersion: { 
                required: function(){
                    return checkEmpty( $("select[name='cfDeploymentVersion']").val() );
                }
            }
            
        }, messages: {
            instanceConfigName: { 
                required:  "인스턴스 별칭"+text_required_msg
            }, iaasType: { 
                required:  "클라우드 인프라 환경 타입"+text_required_msg,
            }, adapter: { 
                required:  "adapter"+text_required_msg,
            }, api: { 
                required:  "api"+text_required_msg,
            }, ccWorker: { 
                required:  "ccWorker"+text_required_msg,
            }, consul: { 
                required:  "consul"+text_required_msg,
            }, theDatabase: { 
                required:  "theDatabase"+text_required_msg,
            }, diegoApi: { 
                required:  "diegoApi"+text_required_msg,
            }, diegoCell: { 
                required:  "diegoCell"+text_required_msg,
            }, doppler: { 
                required:  "doppler"+text_required_msg,
            }, haproxy: { 
                required:  "haproxy"+text_required_msg,
            }, logApi: { 
                required:  "logApi"+text_required_msg,
            }, nats: { 
                required:  "nats"+text_required_msg,
            }, router: { 
                required:  "router"+text_required_msg,
            }, singletonBlobstore: { 
                required:  "singletonBlobstore"+text_required_msg,
            }, tcpRouter: { 
                required:  "tcpRouter"+text_required_msg,
            }, uaa: { 
                required:  "uaa"+text_required_msg,
            }, cfDeploymentVersion: { 
                required:  "CF Deployment 버전"+select_required_msg,
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
            registHbCfDeploymnetInstanceConfigInfo();
        }
    });
});


</script>