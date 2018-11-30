<%
/* =================================================================
 * 작성일 : 2018.07
 * 작성자 : 이정윤
 * 상세설명 : 인증서 정보 관리 화면
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
var select_required_msg='<spring:message code="common.select.vaildate.required.message"/>';//을(를) 선택하세요.
var search_data_fail_msg ='클라우드 인프라 환경을 선택하세요.';
var country_parent_code = '<spring:message code="common.code.country.code.parent"/>';//ieda_common_code country 조회
var credentialConfigInfo = [];//인증서
var countryCodes = null;
var iaas = "";
var releaseInfo = [];
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
         *  설명 : 인증서 목록 Grid
        *********************************************************/
        grid: {
            name: 'cf_deployment_credential_Grid',
            header: '<b>인증서 정보</b>',
            method: 'GET',
                multiSelect: false,
            show: {
                    selectColumn: true,
                    footer: true },
            style: 'text-align: center',
            columns:[
                   { field: 'recid', hidden: true },
                   { field: 'id', hidden: true },
                   { field: 'credentialConfigName', caption: '인증서 명', size:'15%', style:'text-align:center;' },
                   { field: 'credentialConfigKeyFileName', caption: '인증서 파일 명', size:'20%', style:'text-align:center;'},
                   { field: 'releaseName', hidden: true },
                   { field: 'releaseVersion', hidden: true },
                   { field: 'iaasType', caption: '인프라 환경 타입', size:'15%', style:'text-align:center;' ,render: function(record){ 
                       if(record.iaasType.toLowerCase() == "aws"){
                           return "<img src='images/iaasMgnt/aws-icon.png' width='80' height='30' />";
                       }else if (record.iaasType.toLowerCase() == "openstack"){
                           return "<img src='images/iaasMgnt/openstack-icon.png' width='90' height='35' />";
                       }
                   }},
                   { field: 'countryCode', caption: '국가 코드', size:'10%', style:'text-align:center;'},
                   { field: 'domain', caption: '도메인', size:'15%', style:'text-align:center;'},
                   { field: 'company', caption: '회사명', size:'10%', style:'text-align:center;'},
                   { field: 'jobTitle', caption: '부서명', size:'10%', style:'text-align:center;'},
                   { field: 'emailAddress', caption: '이메일', size:'20%', style:'text-align:center;'}
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
                    getCountryCodes();
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
    }
}

$(function(){
    $('#cf_deployment_credential_Grid').w2layout(credentialLayout.layout2);
    w2ui.layout2.content('left', $().w2grid(credentialLayout.grid));
    w2ui['layout2'].content('main', $('#regPopupDiv').html());
    
    doSearch();
    $("#deleteBtn").click(function(){
        if($("#deleteBtn").attr('disabled') == "disabled") return;
        var selected = w2ui['cf_deployment_credential_Grid'].getSelection();
        if( selected.length == 0 ){
            w2alert("선택된 정보가 없습니다.", "인증서 정보 삭제");
            return;
        }
        else {
            var record = w2ui['cf_deployment_credential_Grid'].get(selected);
            w2confirm({
                title       : "인증서",
                msg         : "인증서 ("+record.credentialConfigName + ")을 삭제하시겠습니까?",
                yes_text    : "확인",
                no_text     : "취소",
                yes_callBack: function(event){
                    deleteHbCfDeploymentCredentialConfigInfo(record.recid, record.credentialConfigName);
                },
                no_callBack    : function(){
                    w2ui['cf_deployment_credential_Grid'].clear();
                    doSearch();
                }
            });
        }
    });
});

/********************************************************
 * 설명 : 인증서 수정 정보 설정
 * 기능 : settingCredentialInfo
 *********************************************************/
function settingCredentialInfo(){
    
    var selected = w2ui['cf_deployment_credential_Grid'].getSelection();
    var record = w2ui['cf_deployment_credential_Grid'].get(selected);
    
    if(record == null) {
        w2alert("인증서 설정 중 에러가 발생 했습니다.");
        return;
    }
    iaas = record.iaasType;
    $("input[name=credentialInfoId]").val(record.recid);
    $("input[name=credentialConfigName]").val(record.credentialConfigName);
    $("select[name=iaasType]").val(record.iaasType);
    $("select[name=countryCode]").html("<option value='"+record.countryCode+"' selected >"+record.countryCode+"</option>");
    releaseInfo = {
        releaseName            : record.releaseName,
        releaseVersion         : record.releaseVersion,
    }
    getCfDeployment(iaas);
    $("input[name=city]").val(record.city);
    $("input[name=domain]").val(record.domain);
    $("input[name=company]").val(record.company);
    $("input[name=jobTitle]").val(record.jobTitle);
    $("input[name=emailAddress]").val(record.emailAddress);
    
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
                    if( credentialConfigInfo.countryCode == obj.codeName ){
                        options += "<option value='"+obj.codeName+"' :selected>"+obj.codeName+"</option>";
                    }else{
                        options += "<option value='"+obj.codeName+"'>"+obj.codeName+"</option>";
                    }
                });
                $("select#countryCode").html(options);
            }
        },
        error : function(e, status) {
            w2popup.unlock();
            w2alert("국가 코드를 가져오는데 실패하였습니다.", "CF Deployment");
        }
    });
}

/********************************************************
 * 설명 : 인증서 목록 조회
 * 기능 : doSearch
 *********************************************************/
function doSearch() {
    credentialConfigInfo=[];//인증서
    releaseInfo = [];
    iaas = "";
    resetForm();
    getCountryCodes();
    w2ui['cf_deployment_credential_Grid'].clear();
    w2ui['cf_deployment_credential_Grid'].load('/deploy/hbCfDeployment/credentialConfig/list');
}

/********************************************************
 * 설명 : 초기 버튼 스타일
 * 기능 : doButtonStyle
 *********************************************************/
function doButtonStyle() {
    $('#deleteBtn').attr('disabled', true);
}

/********************************************************
 * 설명 : 인증서 등록
 * 기능 : registHbCfDeploymentCredentialConfigInfo
 *********************************************************/
function registHbCfDeploymentCredentialConfigInfo(){
    w2popup.lock("등록 중입니다.", true);
    var release = $("select[name='cfdeployment']").val();
    credentialConfigInfo = {
            id                     : $("input[name='credentialInfoId']").val(),
            iaasType               : $("select[name='iaasType'] :selected").val(),
            credentialConfigName   : $("input[name='credentialConfigName']").val(),
            countryCode            : $("select[name='countryCode'] :selected").val(),
            domain                 : $("input[name='domain']").val(),
            city                   : $("input[name='city']").val(),
            company                : $("input[name='company']").val(),
            jobTitle               : $("input[name='jobTitle']").val(),
            releaseName          : release.split("/")[0],
            releaseVersion       : release.split("/")[1],
            emailAddress           : $("input[name='emailAddress']").val()
    }
    $.ajax({
        type : "PUT",
        url : "/deploy/hbCfDeployment/credentialConfig/save",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(credentialConfigInfo),
        success : function(data, status) {
            w2utils.unlock($("#layout_layout_panel_main"));
            w2alert("Key 생성에 성공하였습니다.<br><font style='color:red'> 파일 명은 "+credentialConfigInfo.credentialConfigName+"-cred.yml<br>경로는 .bosh_plugin/hybrid_cf_credential 입니다.</font>", "CF-Deployment Key 생성");
            doSearch();
        },
        error : function( e, status ) {
            w2utils.unlock($("#layout_layout_panel_main"));
            var errorResult = JSON.parse(e.responseText);
            w2alert(errorResult.message, "인증서 저장");
        }
    });
}

/********************************************************
 * 설명 : 인증서 삭제
 * 기능 : deleteHbCfDeploymentCredentialConfigInfo
 *********************************************************/
function deleteHbCfDeploymentCredentialConfigInfo(id, credentialConfigName){
    w2popup.lock("삭제 중입니다.", true);
    credentialInfo = {
        id : id,
        credentialConfigName : credentialConfigName
    }
    $.ajax({
        type : "DELETE",
        url : "/deploy/hbCfDeployment/credentialConfig/delete",
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
    $().w2destroy('cf_deployment_credential_Grid');
}

/********************************************************
 * 설명 : 인증서 리셋
 * 기능 : resetForm
 *********************************************************/
function resetForm(status){
    $(".panel-body").find("p").remove();
    $(".panel-body").children().children().children().css("borderColor", "#bbb");
    $("select[name=iaasType]").val("");
    $("input[name=credentialConfigName]").val("");
    $("select[name=countryCode]").val("");
    $("input[name=domain]").val("");
    $("input[name=city]").val("");
    $("input[name=company]").val("");
    $("input[name=jobTitle]").val("");
    $("input[name=emailAddress]").val("");
    $("input[name=credentialInfoId]").val("");
    $("select[name=cfdeployment]").html("<option value=''>CF Deployment를 선택하세요.</option>");
    $("select[name=cfdeployment]").attr("disabled", "disabled");
    if(status=="reset"){
        w2ui['cf_deployment_credential_Grid'].clear();
        doSearch();
    }
    document.getElementById("settingForm").reset();
}

/********************************************************
 * 설명 : CF Deployment version 조회
 * 기능 : getCfDeployment
 *********************************************************/
function getCfDeployment(iaas) {
    var option = "<option value=''>CF Deployment를 선택하세요.</option>";
    if(iaas == ""){
        $("select[name=cfdeployment]").attr("disabled", "disabled");
        $("select[name='cfdeployment']").html(option);
    } else {
        if($("select[name=cfdeployment]").attr("disabled") == "disabled"){
            $("select[name=cfdeployment]").removeAttr("disabled");
        }
        $.ajax({
            type : "GET",
            url :"/common/deploy/list/releaseInfo/cfDeployment/"+iaas, 
            contentType : "application/json",
            success : function(data, status) {
                if( data != null){
                    data.map(function(obj) {
                        if( releaseInfo.releaseName == obj.releaseType && releaseInfo.releaseVersion == obj.minReleaseVersion){
                            option += "<option value='"+obj.releaseType+"/"+obj.minReleaseVersion+"' selected>"+obj.releaseType+"/"+obj.minReleaseVersion+"</option>";
                        }else{
                            option += "<option value='"+obj.releaseType+"/"+obj.minReleaseVersion+"'>"+obj.releaseType+"/"+obj.minReleaseVersion+"</option>";    
                        }
                    });

                }
                $("select[name='cfdeployment']").html(option);
            },
            error : function(e, status) {
                w2popup.unlock();
                w2alert("Cf Deployment List 를 가져오는데 실패하였습니다.", "CF Deployment");
            }
        });
    }
}

</script>
<div id="main">
    <div class="page_site"> 이종 CF Deployment > <strong>인증서 정보 관리</strong></div>
    <!-- 사용자 목록-->
    <div class="pdt20">
        <div class="title fl"> 인증서 목록</div>
    </div>
    <div id="cf_deployment_credential_Grid" style="width:100%;  height:700px;"></div>

</div>


<div id="regPopupDiv" hidden="true" >
    <form id="settingForm" action="POST" >
    <input type="hidden" name="credentialInfoId" />
        <div class="w2ui-page page-0" style="">
           <div class="panel panel-default">
               <div class="panel-heading"><b>인증서 정보 </b></div>
               <div class="panel-body" style="height:615px; overflow-y:auto;">
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;"> 인증서 별칭</label>
                       <div>
                           <input class="form-control" name = "credentialConfigName" type="text"  maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="인증서 별칭을 입력 하세요."/>
                       </div>
                   </div>
                   
                   <div class="w2ui-field">
                       <label style="width:40%; text-align: left;padding-left: 20px;"> 클라우드 인프라 환경</label>
                       <div>
                           <select class="form-control" onchange="getCfDeployment(this.value);" name="iaasType" style="width: 320px; margin-left: 20px;">
                               <option value=""> 인프라 환경을 선택하세요. </option>
                               <option value="aws"> AWS </option>
                               <option value="openstack"> Openstack </option>
                           </select>
                       </div>
                   </div>
                   <div class="w2ui-field">
                       <label style="width:40%; text-align: left;padding-left: 20px;"> CF Deployment 버전 명</label>
                       <div>
                            <select class="form-control" disabled="disabled" name="cfdeployment" style="display:inline-block; width: 320px; margin-left: 20px;">
                                <option value="">CF Deployment를 선택하세요.</option>
                            </select>
                      </div>
                  </div>
                  
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;">CF 도메인 </label>
                       <div>
                           <input class="form-control"  name="domain" type="text" maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="CF 도메인을 입력하세요."/>
                       </div>
                   </div>
                   
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;"> 국가 코드</label>
                       <div>
                           <select class="form-control" id="countryCode" name="countryCode"  style="width: 320px; margin-left: 20px;">
                              <!--  <option value=""> 국가 코드를 선택하세요. </option>
                               <option value="KR"> KR </option> -->
                               
                           </select>
                       </div>
                   </div>

                    <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;"> 도시  </label>
                       <div>
                           <input class="form-control"  name="city" type="text" maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="도시를 입력하세요."/>
                       </div>
                   </div>
                    <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;"> 회사 명 </label>
                       <div>
                           <input class="form-control" name="company" type="text" maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="회사 명을 입력하세요."/>
                       </div>
                   </div>
                   
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;"> 부서 명 </label>
                       <div>
                           <input class="form-control" name="jobTitle" type="text" maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="부서 명을 입력하세요."/>
                       </div>
                   </div>
                   
                   <div class="w2ui-field">
                       <label style="width:40%;text-align: left;padding-left: 20px;"> 이메일 </label>
                       <div>
                           <input class="form-control" name="emailAddress" type="text" maxlength="100" style="width: 320px; margin-left: 20px;" placeholder="이메일을 입력하세요."/>
                       </div>
                   </div>
                   
               </div>
           </div>
        </div>
    </form>
    <div id="regPopupBtnDiv" style="text-align: center; margin-top: 5px;">
    	<sec:authorize access="hasAuthority('DEPLOY_HBCF_CREDENTIAL_ADD')">
        	<span id="installBtn" onclick="$('#settingForm').submit();" class="btn btn-primary">등록</span>
        </sec:authorize>
        <span id="resetBtn" onclick="resetForm('reset');" class="btn btn-info">취소</span>
        <sec:authorize access="hasAuthority('DEPLOY_HBCF_CREDENTIAL_DELETE')">
        	<span id="deleteBtn" class="btn btn-danger">삭제</span>
        </sec:authorize>
    </div>
</div>
<script>
$(function() {
    $.validator.addMethod("validemail", 
           function(value, element) {
                return /^\w+([-+.']\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(value);
                              }, 
               "Sorry, I've enabled very strict email validation"
           );
    
    $("#settingForm").validate({
        ignore : [],
        //onfocusout: function(element) {$(element).valid()},
        rules: {
            credentialConfigName: { 
                required: function(){
                    return checkEmpty( $("input[name='credentialConfigName']").val() );
                }
            }, iaasType: { 
                required: function(){
                    return checkEmpty( $("select[name='iaasType']").val() );
                }
            }, cfdeployment: { 
                required: function(){
                    return checkEmpty( $("select[name='cfdeployment']").val() );
                }
            }, countryCode: { 
                required: function(){
                    return checkEmpty( $("select[name='countryCode']").val() );
                }
            }, domain: { 
                required: function(){
                    return checkEmpty( $("input[name='domain']").val() );
                }
            }, city: { 
                required: function(){
                    return checkEmpty( $("input[name='city']").val() );
                }
            }, company: { 
                required: function(){
                    return checkEmpty( $("input[name='company']").val() );
                }
            }, jobTitle: { 
                required: function(){
                    return checkEmpty( $("input[name='jobTitle']").val() );
                }
            }, emailAddress: { 
                required: function(){
                    return checkEmpty( $("input[name='emailAddress']").val() );
                }, validemail : function(){
                    return $(".w2ui-msg-body input[name='emailAddress']").val();
                }
            }
        }, messages: {
            credentialConfigName: { 
                required:  "인증서 별칭"+text_required_msg
            }, iaasType: { 
                required:  "클라우드 인프라 환경 타입"+select_required_msg,
            }, cfdeployment: { 
                required:  "cf Deployment 버전"+select_required_msg,
            }, countryCode: { 
                required:  "국가 코드 "+select_required_msg,
            }, domain: { 
                required:  "도메인 "+text_required_msg,
            }, city: { 
                required:  "도시 "+text_required_msg,
            }, company: { 
                required:  "회사 명 "+text_required_msg,                
            }, jobTitle: { 
                required:  "부서 명 "+text_required_msg,
            }, emailAddress: { 
                required:  "이메일 주소"+text_required_msg,
                validemail : "이메일 형식을 확인하세요.",
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
            w2utils.lock($("#layout_layout_panel_main"), save_lock_msg, true);
            registHbCfDeploymentCredentialConfigInfo();
        }
    });
});


</script>