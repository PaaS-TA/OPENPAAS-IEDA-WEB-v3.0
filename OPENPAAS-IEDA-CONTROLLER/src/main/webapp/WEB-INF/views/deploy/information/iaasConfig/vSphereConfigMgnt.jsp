<%
/* =================================================================
 * 작성일 : 2017.06.05
 * 작성자 : 이정윤
 * 상세설명 : 환경 설정 관리 화면( vSphere 인프라 환경 설정 조회)
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix = "spring" uri = "http://www.springframework.org/tags" %>
<style>
.error { display:block; color:red; font-weight:100;}
</style>

<script type="text/javascript">
var search_grid_fail_msg = '<spring:message code="common.grid.select.fail"/>';//목록 조회 중 오류가 발생했습니다.
var search_lock_msg = '<spring:message code="common.search.data.lock"/>';//데이터 조회 중 입니다.
var save_lock_msg='<spring:message code="common.save.data.lock"/>';//등록 중 입니다.
var delete_lock_msg='<spring:message code="common.delete.data.lock"/>';//삭제 중 입니다.
var select_fail_msg='<spring:message code="common.grid.selected.fail"/>'//선택된 레코드가 존재하지 않습니다.
var popup_delete_msg='<spring:message code="common.popup.delete.message"/>';//삭제 하시겠습니까?
var text_required_msg='<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
var text_injection_msg='<spring:message code="common.text.validate.sqlInjection.message"/>';//입력하신 값은 입력하실 수 없습니다.

$(function() {
    // vSphere 클라우드 인프라 환경 설정 정보 조회
    $('#vSphere_configGrid').w2grid({
        name: 'vSphere_configGrid',
        style   : 'text-align:center',
        method  : 'GET',
        multiSelect: false,
        show: {
            selectColumn: true,
            footer: true
            },
        columns : [
           {field: 'id',  caption: 'id', hidden:true}
           , {field: 'iaasConfigAlias', caption: '환경 설정 별칭', size: '100px', style: 'text-align:left'}
           , {field: 'deployStatus', caption: '플랫폼 배포 사용 여부', size: '130px', style: 'text-align:center'}
           , {field: 'accountName', caption: '인프라 계정 별칭', size: '100px', style: 'text-align:left'}
           , {field: 'vsphereVcentDataCenterName', caption: 'VcenterDataCenterName', size: '200px', style: 'text-align:left'}
           , {field: 'vsphereVcenterVmFolder', caption: 'VcenterVmFolder', size: '200px', style: 'text-align:left'}
           , {field: 'vsphereVcenterTemplateFolder', caption: 'VcenterTemplateFolder', size: '200px', style: 'text-align:left'}  
           , {field: 'vsphereVcenterDatastore', caption: 'VcenterDatastore', size: '200px', style: 'text-align:left'}
           , {field: 'vsphereVcenterPersistentDatastore', caption: 'VcenterPersistentDatastore', size: '200px', style: 'text-align:left'} 
           , {field: 'vsphereVcenterDiskPath', caption: 'VcenterDiskPath', size: '200px', style: 'text-align:left'}
           , {field: 'vsphereVcenterCluster', caption: 'VcenterCluster', size: '200px', style: 'text-align:left'} 
           , {field: 'createDate', caption: '생성 일자', size: '100px', style: 'text-align:center'}
           , {field: 'updateDate', caption: '수정 일자', size: '100px', style: 'text-align:center'}
         ],onError: function(event){
             //w2alert(search_grid_fail_msg, "vSphere 환경 설정 목록");
         },onLoad : function(event){
         },onSelect : function(event){
             event.onComplete = function(){
                 $("#updateConfigBtn").attr('disabled', false);
                $("#deleteConfigBtn").attr('disabled', false);
            }
         },onUnselect : function(event){
             event.onComplete = function(){
                 $("#updateConfigBtn").attr('disabled', true);
                 $("#deleteConfigBtn").attr('disabled', true);
            }
         }
    });
     doSearch(); 
});

/*************************** *****************************
 * 설명 :  vSphere 등록 팝업 화면
 *********************************************************/
$("#registConfigBtn").click(function(){
    w2popup.open({
        title   : "<b>vSphere 환경 설정 등록</b>",
        width   : 730,
        height  : 475,
        modal   : true,
        body    : $("#registPopupDiv").html(),
        buttons : $("#registPopupBtnDiv").html(),
        onOpen : function(event){
            event.onComplete = function(){
                 getVsphereAccountName();
            }
        },onClose:function(event){
            doSearch();
               $("#updateConfigBtn").attr('disabled', true);
            $("#deleteConfigBtn").attr('disabled', true);
        }
    });
});

/*************************** *****************************
 * 설명 :  vSphere 저장 관련 팝업 화면 호출 시 IaaS 계정 조회
 *********************************************************/
function getVsphereAccountName(accountId){
    w2popup.lock(search_lock_msg,true);
    $.ajax({
        type : "GET",
        url : "/common/deploy/accountList/vsphere",
        contentType : "application/json",
        success : function(data, status) {
            setupIaasAccountName(data, accountId);
            w2popup.unlock();
        },
        error : function(request, status, error) {
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "해당 계정 별칭 목록 조회");
        }
    });
}

/*************************** *****************************
 * 설명 :  조회 된 계정 결과를 화면에 출력
 *********************************************************/
function setupIaasAccountName(data, accountId){
     var iaasAccountName = "";
         iaasAccountName += "<select style='width: 300px' name='accountId'>";
         
     if( data.length ==0 ){
         iaasAccountName +="<option value=''>계정을 등록하세요.</option>";
     }else{
         for (var i=0; i<data.length; i++){
             if(accountId != "undefined" && data[i].id == accountId){
                 iaasAccountName +="<option selected value='"+data[i].id+"'>" + data[i].accountName + "</option>";
             }else{
                 iaasAccountName +="<option value='"+data[i].id+"'>" + data[i].accountName + "</option>";
             }
         } 
     }
     iaasAccountName+="</select>";
     $(".w2ui-msg-body #accountNameDiv").append(iaasAccountName);
}

/*************************** *****************************
 * 설명 :  Vsphere Config 정보 설정
 *********************************************************/
function vSphereConfigInfoRegist(){
    var vSphereConfigInfo = {
        id : $(".w2ui-msg-body input[name='id']").val(),
        iaasType : "VSPHERE",
        accountId : $(".w2ui-msg-body select[name='accountId']").val(),
        iaasConfigAlias : $(".w2ui-msg-body input[name='iaasConfigAlias']").val(),
        vsphereVcenterDataCenterName : $(".w2ui-msg-body input[name='vsphereVenterDataCenterName']").val(),
        vsphereVcenterVmFolder : $(".w2ui-msg-body input[name='vsphereVcenterVmFolder']").val(),
        vsphereVcenterTemplateFolder : $(".w2ui-msg-body input[name='vsphereVcenterTemplateFolder']").val(),
        vsphereVcenterDatastore : $(".w2ui-msg-body input[name='vsphereVcenterDatastore']").val(),
        vsphereVcenterPersistentDatastore : $(".w2ui-msg-body input[name='vsphereVcenterPersistentDatastore']").val(),
        vsphereVcenterDiskPath : $(".w2ui-msg-body input[name='vsphereVcenterDiskPath']").val(),
        vsphereVcenterCluster : $(".w2ui-msg-body input[name='vsphereVcenterCluster']").val()
    }
    saveRequestVsphereConfigInfo(vSphereConfigInfo);
}

/*************************** *****************************
 * 설명 :  Vsphere Config 정보 삽입 요청
 *********************************************************/
function saveRequestVsphereConfigInfo(vSphereConfigInfo){
    w2popup.lock(save_lock_msg, true);
     $.ajax({
        type : "PUT",
        url : "/info/iaasConfig/vsphere/save",
        contentType : "application/json",
        async : true,
        data: JSON.stringify(vSphereConfigInfo),
        success : function(data, status) {
            w2popup.unlock();
            w2popup.close();
            doSearch();
        },
        error : function(request, status, error) {
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "vSphere 환경 정보 저장 실패");
        }
     });
}

/*************************** *****************************
 * 설명 :  vSphere 수정 팝업 화면
 *********************************************************/
$("#updateConfigBtn").click(function(){
    if($("#updateConfigBtn").attr('disabled') == "disabled") return;
    w2popup.open({
        title   : "<b>vSphere 환경 설정 수정</b>",
        width   : 730,
        height  : 475,
        modal   : true,
        body    : $("#registPopupDiv").html(),
        buttons : $("#updatePopupBtnDiv").html(),
        onOpen : function(event){
            event.onComplete = function(){
                 var selected = w2ui['vSphere_configGrid'].getSelection();
                 if( selected.length == 0 ){
                     w2alert('<spring:message code="common.grid.selected.fail"/>', "vSphere 환경 설정 수정");
                     return;
                 }
                 var record = w2ui['vSphere_configGrid'].get(selected);
                 setVsphereUpdateInfoSet(record.id);
            }
        },onClose:function(event){
               $("#updateConfigBtn").attr('disabled', true);
            $("#deleteConfigBtn").attr('disabled', true);
            doSearch();
        }
    });
});

/*************************** *****************************
 * 설명 :  vSphere 수정 팝업 화면 호출 시 데이터 값 설정
 *********************************************************/
function setVsphereUpdateInfoSet(id){
     $.ajax({
            type : "GET",
            url : "/info/iaasConfig/vsphere/save/detail/"+id,
            contentType : "application/json",
            async : true,
            dataType : "json",
            success : function(data, status) {
                 w2popup.unlock();
                 if(data!=null){
                     getVsphereAccountName(data.accountId);
                     $(".w2ui-msg-body input[name='id']").val(data.id);
                     $(".w2ui-msg-body input[name='iaasConfigAlias']").val(data.iaasConfigAlias);
                     $(".w2ui-msg-body input[name='vsphereVenterDataCenterName']").val(data.vsphereVcentDataCenterName);
                     $(".w2ui-msg-body input[name='vsphereVcenterVmFolder']").val(data.vsphereVcenterVmFolder);
                     $(".w2ui-msg-body input[name='vsphereVcenterTemplateFolder']").val(data.vsphereVcenterTemplateFolder);
                     $(".w2ui-msg-body input[name='vsphereVcenterDatastore']").val(data.vsphereVcenterDatastore);
                     $(".w2ui-msg-body input[name='vsphereVcenterPersistentDatastore']").val(data.vsphereVcenterPersistentDatastore);
                     $(".w2ui-msg-body input[name='vsphereVcenterDiskPath']").val(data.vsphereVcenterDiskPath);
                     $(".w2ui-msg-body input[name='vsphereVcenterCluster']").val(data.vsphereVcenterCluster);
                 }
            },
            error : function(request, status, error) {
                var errorResult = JSON.parse(request.responseText);
                w2alert(errorResult.message, "vSphere 환경 정보 수정 실패");
            }
         });
}


/*************************** *****************************
 * 설명 :  vSphere 삭제 팝업 화면 호출
 *********************************************************/
$("#deleteConfigBtn").click(function(){
    if($("#deleteConfigBtn").attr('disabled') == "disabled") return;
     var selected = w2ui['vSphere_configGrid'].getSelection();
     if( selected.length == 0 ){
         w2alert('<spring:message code="common.grid.selected.fail"/>', "vSphere 환경 설정 삭제");
         return;
     }else{
         var record = w2ui['vSphere_configGrid'].get(selected);
         var msg = "환경 설정 정보(" + record.iaasConfigAlias + ")"+ popup_delete_msg;
         if( record.deployStatus == '사용중' ){
             msg = "<span style='color:red'>현재 vSphere 플랫폼 설치에서 <br/>해당 환경 설정 정보("+record.iaasConfigAlias+")를 사용하고 있습니다. </span><br/><span style='color:red; font-weight:bolder'>그래도 삭제 하시겠습니까?</span>";
         }
         
         w2confirm({
             title       : "<b>vSphere 환경 설정 삭제</b>",
             msg         : msg,
             yes_text    : "확인",
             no_text     : "취소",
             yes_callBack: function(event){
                 requestDeleteVsphereConfigInfo(record);
             },
             no_callBack    : function(){
                 w2ui['vSphere_configGrid'].clear();
                 doSearch();
             }
         });
     }
});

/********************************************************
 * 기능 : requestDeleteVsphereConfigInfo
 * 설명 : vsphere 환경 설정 정보 삭제 요청
 *********************************************************/
function requestDeleteVsphereConfigInfo(record){
    var configInfo ={
            id :  record.id,
            iaasType :  record.iaasType,
            accountId : record.accountId,
            iaasConfigAlias : record.iaasConfigAlias
     }
     
     w2popup.lock(delete_lock_msg, true);
     $.ajax({
             type : "DELETE",
             url : "/info/iaasConfig/aws/delete",
             contentType : "application/json",
             dataType: "json",
             async : true,
             data : JSON.stringify(configInfo),
             success : function(status) {
                 w2popup.unlock();
                 w2popup.close();    
                 initsetting();
                 doSearch(); 
             },
             error : function(request, status, error) {
                 w2popup.unlock();
                 var errorResult = JSON.parse(request.responseText);
                 w2alert(errorResult.message);
             }
         });
}

/********************************************************
 * 기능 : doSearch
 * 설명 : 조회기능
 *********************************************************/
function doSearch() {
    // 목록
    $("#deleteConfigBtn").attr('disabled', true);
    $("#updateConfigBtn").attr('disabled', true);
    w2ui['vSphere_configGrid'].clear();
    w2ui['vSphere_configGrid'].load("<c:url value='/info/iaasConfig/vSphere/list'/>","",function(event){});  
}


/********************************************************
 * 기능 : initsetting
 * 설명 : 기본 설정값 초기화
 *********************************************************/
function initsetting(){
    
}

/********************************************************
 * 기능 : clearMainPage
 * 설명 : 다른페이지 이동시 호출
 *********************************************************/
function clearMainPage() {
    $().w2destroy('vSphere_configGrid');
}

/********************************************************
 * 기능 : resize
 * 설명 : 화면 리사이즈시 호출
 *********************************************************/
$( window ).resize(function() {
  setLayoutContainerHeight();
});
</script>
<div id="main">
    <div class="page_site">정보조회 > 인프라 환경 설정 관리 > <strong>vSphere 환경 설정 관리 </strong></div>
     <div class="pdt20">
        <div class="fl" style="width:100%">
            <div class="dropdown" >
                <a href="#" class="dropdown-toggle iaas-dropdown" data-toggle="dropdown" aria-expanded="false">
                    <i class="fa fa-cloud"></i>&nbsp;vSphere<b class="caret"></b>
                </a>
                <ul class="dropdown-menu alert-dropdown">
                    <sec:authorize access="hasAuthority('INFO_IAASCONFIG_AWS_LIST')">
                        <li><a href="javascript:goPage('<c:url value="/info/iaasConfig/aws"/>', 'AWS 관리');">AWS</a></li>
                    </sec:authorize>
                    <sec:authorize access="hasAuthority('INFO_IAASCONFIG_OPENSTACK_LIST')">
                        <li><a href="javascript:goPage('<c:url value="/info/iaasConfig/openstack"/>', 'Openstack 관리');">Openstack</a></li>
                    </sec:authorize>
                    <sec:authorize access="hasAuthority('INFO_IAASCONFIG_GOOGLE_LIST')">
                        <li><a href="javascript:goPage('<c:url value="/info/iaasConfig/google"/>', 'Google 관리');">Google</a></li>
                    </sec:authorize>
                    <sec:authorize access="hasAuthority('INFO_IAASCONFIG_AZURE_LIST')">
                        <li><a href="javascript:goPage('<c:url value="/info/iaasConfig/azure"/>', 'Google 관리');">Azure</a></li>
                    </sec:authorize>
                    
                </ul>
            </div>
        </div> 
    </div>
    <div class="pdt20">
        <div class="title fl">vSphere 환경 설정 목록</div>
        <div class="fr"> 
            <!-- Button -->
            <sec:authorize access="hasAuthority('INFO_IAASCONFIG_VSPHERE_CREATE')">
               <span id="registConfigBtn" class="btn btn-primary" style="width:100px" >등록</span>
            </sec:authorize>
             <sec:authorize access="hasAuthority('INFO_IAASCONFIG_VSPHERE_UPDATE')">
               <span id="updateConfigBtn" class="btn btn-info" style="width:100px" >수정</span>
            </sec:authorize>
            <sec:authorize access="hasAuthority('INFO_IAASCONFIG_VSPHERE_DELETE')">
               <span id="deleteConfigBtn" class="btn btn-danger" style="width:100px" >삭제</span>
            </sec:authorize>
            <!-- //Btn -->
        </div>
        
        <div id="vSphere_configGrid" style="width: 100%; height: 610px;"></div>
   </div>
</div>


<!-- vSphere 등록 및 수정 팝업 Div-->
<div id="registPopupDiv" hidden="true">
    <input type="hidden" name="id" />
    <form id="vSphereConfigForm" action="POST" style="padding:5px 0 5px 0;margin:0;">
        <div class="panel panel-info"> 
            <div class="panel-heading"><b>vSphere 환경 설정 정보</b></div>
            <div class="panel-body" style="padding:20px 10px; height:340px; overflow-y:auto;">
                <div class="w2ui-field">
                    <label style="width:40%;text-align: left; padding-left: 20px;">vSphere 환경 설정 별칭</label>
                    <div>
                        <input name="iaasConfigAlias" type="text"  maxlength="100" style="width: 300px" placeholder="vSphere 환경 설정 별칭을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:40%;text-align: left; padding-left: 20px;">계정 별칭</label>
                    <div id="accountNameDiv"></div>
                </div>
                <div class="w2ui-field">
                    <label style="width:40%;text-align: left; padding-left: 20px;">vSphere Data Center</label>
                    <div>
                        <input name="vsphereVenterDataCenterName" type="text"  maxlength="100" style="width: 300px" placeholder="vSphere Data Center 명을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:40%;text-align: left; padding-left: 20px;">vSphere VM Folder</label>
                    <div>
                        <input name="vsphereVcenterVmFolder" type="text"  maxlength="100" style="width: 300px" placeholder="vSphere VM Folder를 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:40%;text-align: left; padding-left: 20px;">vSphere VM Template Folder</label>
                    <div>
                        <input name="vsphereVcenterTemplateFolder" type="text"  maxlength="100" style="width: 300px" placeholder="vSphere VM Template Folder를 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:40%;text-align: left; padding-left: 20px;">vSphere VM DataStore</label>
                    <div>
                        <input name="vsphereVcenterDatastore" type="text"  maxlength="100" style="width: 300px" placeholder="vSphere VM DataStore를 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:40%;text-align: left; padding-left: 20px;">vSphere Persistant Datastore</label>
                    <div>
                        <input name="vsphereVcenterPersistentDatastore" type="text"  maxlength="100" style="width: 300px" placeholder="vSphere Persistant Datastore를 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:40%;text-align: left; padding-left: 20px;">vSphere Disk Path</label>
                    <div>
                        <input name="vsphereVcenterDiskPath" type="text"  maxlength="100" style="width: 300px" placeholder="vSphere Disk Path를 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:40%;text-align: left; padding-left: 20px;">vSphere Cluster</label>
                    <div>
                        <input name="vsphereVcenterCluster" type="text"  maxlength="100" style="width: 300px" placeholder="vSphere Cluster를 입력하세요."/>
                    </div>
                </div>
            </div>
        </div>
    </form> 
</div>
<div id="registPopupBtnDiv" hidden="true">
     <button class="btn" id="registBtn" onclick="$('#vSphereConfigForm').submit();">확인</button>
     <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
</div>

<div id="updatePopupBtnDiv" hidden="true">
    <button class="btn" id="updateBtn" onclick="$('#vSphereConfigForm').submit();">확인</button>
    <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
</div>

<script>
$(function() {
    $.validator.addMethod("sqlInjection", function(value, element, params) {
        return checkInjectionBlacklist(params);
      },text_injection_msg);
    
    $("#vSphereConfigForm").validate({
        ignore : "",
        onfocusout: true,
        rules: {
            iaasConfigAlias : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='iaasConfigAlias']").val() );
                }, sqlInjection : function(){
                    return $(".w2ui-msg-body input[name='iaasConfigAlias']").val();
                }
            }, accountId: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body select[name='accountId']").val() );
                }, sqlInjection :   function(){
                    return $(".w2ui-msg-body select[name='accountId']").val();
                }
            }, vsphereVenterDataCenterName: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='vsphereVenterDataCenterName']").val() );
                }, sqlInjection :   function(){
                    return $(".w2ui-msg-body input[name='vsphereVenterDataCenterName']").val();
                }
             }, vsphereVcenterVmFolder: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='vsphereVcenterVmFolder']").val() );
                }, sqlInjection :   function(){
                    return $(".w2ui-msg-body input[name='vsphereVcenterVmFolder']").val();
                }    
            }, vsphereVcenterTemplateFolder: { 
                required: function(){
                return checkEmpty( $(".w2ui-msg-body input[name='vsphereVcenterTemplateFolder']").val() );
                }, sqlInjection :   function(){
                    return $(".w2ui-msg-body input[name='vsphereVcenterTemplateFolder']").val();
                }
            }, vsphereVcenterDatastore: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='vsphereVcenterDatastore']").val() );
                }, sqlInjection :   function(){
                    return $(".w2ui-msg-body input[name='vsphereVcenterDatastore']").val();
                }
            }, vsphereVcenterPersistentDatastore: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='vsphereVcenterPersistentDatastore']").val() );
                }, sqlInjection :   function(){
                    return $(".w2ui-msg-body input[name='vsphereVcenterPersistentDatastore']").val();
                }
            }, vsphereVcenterDiskPath: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='vsphereVcenterDiskPath']").val() );
                }, sqlInjection :   function(){
                    return $(".w2ui-msg-body input[name='vsphereVcenterDiskPath']").val();
                }
            }, vsphereVcenterCluster: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='vsphereVcenterCluster']").val() );
                }, sqlInjection :   function(){
                    return $(".w2ui-msg-body input[name='vsphereVcenterCluster']").val();
                }
            }
        }, messages: {
            iaasConfigAlias: { 
                required:  "환경 설정 별칭" + text_required_msg
                , sqlInjection : text_injection_msg
            }, accountId: { 
                required:  "인프라 계정 별칭"+text_required_msg
                , sqlInjection : text_injection_msg
            }, vsphereVenterDataCenterName: { 
                required:  "데이터 센터 명"+text_required_msg
                , sqlInjection : text_injection_msg
            }, vsphereVcenterVmFolder: { 
                required:  "VM Folder 명"+text_required_msg
                , sqlInjection : text_injection_msg
            }, vsphereVcenterTemplateFolder: { 
                required:  "Template Folder 명"+text_required_msg
                , sqlInjection : text_injection_msg
            }, vsphereVcenterDatastore: { 
                required:  "데이터 스토어"+text_required_msg
                , sqlInjection : text_injection_msg
            }, vsphereVcenterPersistentDatastore: { 
                required:  "영구 데이터 스토어"+text_required_msg
                , sqlInjection : text_injection_msg
            }, vsphereVcenterDiskPath: { 
                required:  "디스크 경로"+text_required_msg
                , sqlInjection : text_injection_msg
            }, vsphereVcenterCluster: { 
                required:  "Cluster 명"+text_required_msg
                , sqlInjection : text_injection_msg
            }
        }, unhighlight: function(element) {
            setSuccessStyle(element);
        },errorPlacement: function(error, element) {
            //do nothing
        },invalidHandler : function(event, validator) {
            var errors = validator.numberOfInvalids();
            if (errors) {
                setInvalidHandlerStyle(errors, validator);
            }
        },submitHandler: function (form) {
            var saveIdValue = $(".w2ui-msg-body input[name='id']").val();
            vSphereConfigInfoRegist();
        }
    });
});

</script>

