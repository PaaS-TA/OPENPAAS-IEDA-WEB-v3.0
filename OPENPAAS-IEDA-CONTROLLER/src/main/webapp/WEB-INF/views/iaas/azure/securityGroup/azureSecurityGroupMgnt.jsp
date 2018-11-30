<%
/* =================================================================
 * 작성일 : 2018.05.21
 * 작성자 : 이정윤 
 * 상세설명 : Azure Security Group 관리 화면
 * =================================================================
 */ 
%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix = "spring" uri = "http://www.springframework.org/tags" %>

<script>
var save_lock_msg = '<spring:message code="common.save.data.lock"/>';//등록 중 입니다.
var detail_rg_lock_msg='<spring:message code="common.search.detaildata.lock"/>';//상세 조회 중 입니다.
var text_required_msg='<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
var delete_confirm_msg ='<spring:message code="common.popup.delete.message"/>';//삭제 하시겠습니까?
var delete_lock_msg= '<spring:message code="common.delete.data.lock"/>';//삭제 중 입니다.
var accountId ="";
var bDefaultAccount = "";

$(function() {
    bDefaultAccount = setDefaultIaasAccountList("azure");
    $('#azure_securityGrid').w2grid({
        name: 'azure_securityGrid',
        method: 'GET',
        msgAJAXerror : 'Azure 계정을 확인해주세요.',
        header: '<b>Security Group 목록</b>',
        multiSelect: false,
        show: {    
                selectColumn: true,
                footer: true},
        style: 'text-align: center',
        columns    : [
                     {field: 'recid',     caption: 'recid', hidden: true}
                   , {field: 'accountId',     caption: 'accountId', hidden: true}
                   , {field: 'securityGroupId',     caption: 'accountId', hidden: true}
                   , {field: 'securityGroupName', caption: 'Security Group Name', size: '50%', style: 'text-align:center', render : function(record){
                       if(record.securityGroupName == null || record.securityGroupName == ""){
                           return "-"
                       }else{
                           return record.securityGroupName;
                       }}
                   }
                   , {field: 'subscriptionName', caption: 'Subscription', size: '50%', style: 'text-align:center'}
                   , {field: 'azureSubscriptionId', caption: 'Subscription ID', size: '50%', style: 'text-align:center'}
                  // , {field: 'accountType', caption: 'Type', size: '50%', style: 'text-align:center'}
                   , {field: 'location', caption: 'Location', size: '50%', style: 'text-align:center'}
                   , {field: 'resourceGroupName', caption: 'Resource Group', size: '50%', style: 'text-align:center'}
                   ],
        onSelect: function(event) {
            event.onComplete = function() {
                $('#deleteBtn').attr('disabled', false);
                var accountId =  w2ui.azure_securityGrid.get(event.recid).accountId;
                var securityGroupName = w2ui.azure_securityGrid.get(event.recid).securityGroupName;
                console.log("test test abc"+securityGroupName+"test test abc"+securityGroupName+"test test abc");
                doSearchInboundsInfo(accountId, securityGroupName); 
                doSearchOutboundsInfo(accountId, securityGroupName); 
            }
        },
        onUnselect: function(event) {
            event.onComplete = function() {
                $('#deleteBtn').attr('disabled', true);
                w2ui['azure_inboundGrid'].clear();
                w2ui['azure_outboundGrid'].clear();
            }
        },
           onLoad:function(event){
            if(event.xhr.status == 403){
                location.href = "/abuse";
                event.preventDefault();
            }
        }, onError:function(evnet){
        }
    });
    
    $('#azure_inboundGrid').w2grid({
        name: 'azure_inboundGrid',
        method: 'GET',
        msgAJAXerror : 'Azure 계정을 확인해주세요.',
        header: '<b>Inbound Security Rules 목록</b>',
        multiSelect: false,
        show: {    
                selectColumn: false,
                footer: true},
        style: 'text-align: center',
        columns    : [
                     {field: 'recid',     caption: 'recid', hidden: true}
                   , {field: 'direction',     caption: 'direction', hidden: true}
                   , {field: 'securityGroupName',     caption: 'Security Group Name',  size: '50%', style: 'text-align:center'}
                   , {field: 'priority', caption: 'Priority', size: '50%', style: 'text-align:center'}
                   , {field: 'inboundName', caption: 'Inbound Name', size: '50%', style: 'text-align:center'}
                   , {field: 'port', caption: 'Port', size: '50%', style: 'text-align:center'}
                   , {field: 'protocol', caption: 'Protocol', size: '50%', style: 'text-align:center'}
                   , {field: 'source', caption: 'Source', size: '50%', style: 'text-align:center'}
                   , {field: 'destination', caption: 'Destination', size: '50%', style: 'text-align:center'}
                   , {field: 'action', caption: 'Action', size: '50%', style: 'text-align:center'}
                   ],
        onSelect: function(event) {
            event.onComplete = function() {
            }
        },
        onUnselect: function(event) {
            event.onComplete = function() {
            }
        },
           onLoad:function(event){
            if(event.xhr.status == 403){
                location.href = "/abuse";
                event.preventDefault();
            }
        }, onError:function(evnet){
        }
    });
    
    $('#azure_outboundGrid').w2grid({
        name: 'azure_outboundGrid',
        method: 'GET',
        msgAJAXerror : 'Azure 계정을 확인해주세요.',
        header: '<b>Outbound Security Rules 목록</b>',
        multiSelect: false,
        show: {    
                selectColumn: false,
                footer: true},
        style: 'text-align: center',
        columns    : [
                     {field: 'recid',     caption: 'recid', hidden: true}
                   , {field: 'direction',     caption: 'direction', hidden: true}
                   , {field: 'securityGroupName',     caption: 'Security Group Name',  size: '50%', style: 'text-align:center'}
                   , {field: 'priority', caption: 'Priority', size: '50%', style: 'text-align:center'}
                   , {field: 'outboundName', caption: 'Outbound Name', size: '50%', style: 'text-align:center'}
                   , {field: 'port', caption: 'Port', size: '50%', style: 'text-align:center'}
                   , {field: 'protocol', caption: 'Protocol', size: '50%', style: 'text-align:center'}
                   , {field: 'source', caption: 'Source', size: '50%', style: 'text-align:center'}
                   , {field: 'destination', caption: 'Destination', size: '50%', style: 'text-align:center'}
                   , {field: 'action', caption: 'Action', size: '50%', style: 'text-align:center'}
                   
                   ],
        onSelect: function(event) {
            event.onComplete = function() {
            }
        },
        onUnselect: function(event) {
            event.onComplete = function() {
            }
        },
           onLoad:function(event){
            if(event.xhr.status == 403){
                location.href = "/abuse";
                event.preventDefault();
            }
        }, onError:function(evnet){
        }
    });
    
    /********************************************************
     * 설명 : Azure Security Group 생성 버튼 클릭
    *********************************************************/
    $("#addBtn").click(function(){
       if($("#addBtn").attr('disabled') == "disabled") return;
       w2popup.open({
           title   : "<b>Azure Security Group  생성</b>",
           width   : 580,
           height  : 380,
           modal   : true,
           body    : $("#registPopupDiv").html(),
           buttons : $("#registPopupBtnDiv").html(),
           onOpen  : function () {
               setAzureSubscription();
               setAzureResourceGroupList();
           },
           onClose : function(event){
            w2popup.unlock();
            accountId = $("select[name='accountId']").val();
            w2ui['azure_securityGrid'].clear();
            w2ui['azure_inboundGrid'].clear();
            w2ui['azure_outboundGrid'].clear();
            doSearch();
           }
       });
    });
    
    /********************************************************
    * 설명 : Azure Security Group 삭제 버튼 클릭
   *********************************************************/
    $("#deleteBtn").click(function(){
        if($("#deleteBtn").attr('disabled') == "disabled") return;
        var selected = w2ui['azure_securityGrid'].getSelection();        
        if( selected.length == 0 ){
            w2alert("선택된 정보가 없습니다.", "Security Group 삭제");
            return;
        }
        else {
            var record = w2ui['azure_securityGrid'].get(selected);
            w2confirm({
                title   : "<b>Security Group 삭제</b>",
                msg     : "Security Group (" + record.securityGroupName +") 를<br/>"
                                       +"<strong><font color='red'> 삭제 하시 겠습니까?</strong><red>"   ,
                yes_text : "확인",
                no_text : "취소",
                height : 250,
                yes_callBack: function(event){
                    w2utils.lock($("#layout_layout_panel_main"), delete_lock_msg, true);
                    deleteAzureSecurityGroupInfo(record);
                },
                no_callBack    : function(){
                    w2ui['azure_securityGrid'].clear();
                    w2ui['azure_inboundGrid'].clear();
                    w2ui['azure_outboundGrid'].clear();
                    accountId = record.accountId;
                    doSearch();
                }
            });
        }
    });
    
    
    
});


/********************************************************
 * 설명 : Azure Security Group 정보 목록 조회 Function 
 * 기능 : doSearch
 *********************************************************/
function doSearch() {
    w2ui['azure_securityGrid'].load("<c:url value='/azureMgnt/securityGroup/list/'/>"+accountId);
    doButtonStyle();
    accountId = "";
}

/********************************************************
 * 설명 : 해당 Azure Security Group에 대한 inbounds List 조회 Function 
 * 기능 : doSearchInboundsInfo
 *********************************************************/
function doSearchInboundsInfo(accountId, securityGroupName){
    
    w2utils.lock($("#layout_layout_panel_main"), detail_rg_lock_msg, true);
    w2ui['azure_inboundGrid'].load("<c:url value='/azureMgnt/securityGroup/list/inbound/'/>"+accountId+"/"+securityGroupName);
    w2utils.unlock($("#layout_layout_panel_main"));
}



/********************************************************
 * 설명 : 해당 Azure Security Group에 대한 outbounds List 조회 Function 
 * 기능 : doSearchOutboundsInfo
 *********************************************************/
function doSearchOutboundsInfo(accountId, securityGroupName){
    
    w2utils.lock($("#layout_layout_panel_main"), detail_rg_lock_msg, true);
    w2ui['azure_outboundGrid'].load("<c:url value='/azureMgnt/securityGroup/list/outbound/'/>"+accountId+"/"+securityGroupName);
    w2utils.unlock($("#layout_layout_panel_main"));
}

/********************************************************
 * 설명 : Azure Security Group 생성
 * 기능 : saveAzureSecurityGroup
 *********************************************************/
function saveAzureSecurityGroup(){
    w2popup.lock(save_lock_msg, true);
    var sgInfo = {
        accountId : $("select[name='accountId']").val(),
        securityGroupName : $(".w2ui-msg-body input[name='securityGroupName']").val(),
        resourceGroupName : $(".w2ui-msg-body select[name='resourceGroupName'] :selected").text(),
        location : $(".w2ui-msg-body select[name='resourceGroupName'] :selected").val(),    
        azureSubscriptionId : $(".w2ui-msg-body input[name='azureSubscriptionId']").val(),
    }
    $.ajax({
        type : "POST",
        url : "/azureMgnt/securityGroup/save",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(sgInfo),
        success : function(status) {
            saveAzureInboundRules(sgInfo.resourceGroupName, sgInfo.securityGroupName);
            w2popup.unlock();
            w2popup.close();
            accountId = sgInfo.accountId;
            doSearch();
        }, error : function(request, status, error) {
            w2popup.unlock();
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message);
        }
    });
}

/********************************************************
 * 설명 : Azure Security Group 생성
 * 기능 : saveAzureSecurityGroup
 *********************************************************/
function saveAzureInboundRules(rgName, sgName){
    w2popup.lock(save_lock_msg, true);
    var accountId = $("select[name='accountId']").val();
    var info = { 
    	accountId : $("select[name='accountId']").val(),
    	resourceGroupName: rgName,
    	securityGroupName: sgName,
                    }
    $.ajax({
        type : "POST",
        url : "/azureMgnt/inbound/save",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(info),
        success : function(status) {
            w2popup.unlock();

        }, error : function(request, status, error) {
            w2popup.unlock();
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message);
        }
    });
}

/********************************************************
 * 기능 : setAzureResourceGroupList
 * 설명 : 해당 Supscription 에 대한  Azure 리소스 그룹 목록 조회 기능
 *********************************************************/
 function setAzureResourceGroupList(){
     accountId = $("select[name='accountId']").val();
     $.ajax({
            type : "GET",
            url : '/azureMgnt/resourceGroup/list/groupInfo/'+accountId,
            contentType : "application/json",
            dataType : "json",
            success : function(data, status) {
                var result = "";
                if(data.total != 0){
                            result = "<option value=''>리소스 그룹을 선택하세요.</option>";
                  for(var i=0; i<data.total; i++){
                    if(data.records != null){
                            result += "<option value='" +data.records[i].location + "' >";
                            result += data.records[i].resourceGroupName;
                            result += "</option>";
                    }
                  }
                }else{
                    result = "<option value=''>리소스 그룹이 존재 하지 않습니다.</option>"
                }
                $("#resourceGroupInfoDiv #resourceGroupInfo").html(result);
                
            },
            error : function(request, status, error) {
                w2popup.unlock();
                var errorResult = JSON.parse(request.responseText);
                w2alert(errorResult.message);
            }
        });
 }

/********************************************************
 * 기능 : setAzureSubscription
 * 설명 : 해당 Security Group 의 Azure Subscription 정보 조회 기능
 *********************************************************/
function setAzureSubscription(){
    accountId = $("select[name='accountId']").val();
    $.ajax({
           type : "GET",
           url : '/azureMgnt/network/list/subscriptionInfo/'+accountId, //common  으로 변경
           contentType : "application/json",
           dataType : "json",
           success : function(data, status) {
               var result = "";
               if(data != null){
                           result  += "<input name='azureSubscriptionId' style='display: none;' value='"+data.azureSubscriptionId+"' />";
                           result  += "<input name='' style='width: 300px;' value='"+data.subscriptionName+"' disabled/>";
               }
               $('#subscriptionInfoDiv #subscriptionInfo').html(result);
           },
           error : function(request, status, error) {
               w2popup.unlock();
               var errorResult = JSON.parse(request.responseText);
               w2alert(errorResult.message);
           }
       });
}

/********************************************************
 * 설명 : Azure ResourceGroup 의 location Onchange 이벤트 기능
 * 기능 : azureResourceGroupOnchange
 *********************************************************/
function azureResourceGroupOnchange(slectedvalue){
    accountId = $("select[name='accountId']").val();
    $('#locationInfoDiv #locationInfo').html(slectedvalue);
    $('#locationInfoDiv #locationVal').html(slectedvalue);
}

/********************************************************
 * 설명 : Azure Security Group 삭제
 * 기능 :  deleteAzureSecurityGroupInfo
 *********************************************************/
function  deleteAzureSecurityGroupInfo(record){
    w2popup.lock(delete_lock_msg, true);
    var rgInfo = {
            accountId : record.accountId,
            securityGroupId : record.securityGroupId,
    }
    $.ajax({
        type : "DELETE",
        url : "/azureMgnt/securityGroup/delete",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(rgInfo),
        success : function(status) {
            w2popup.unlock();
            w2popup.close();
            accountId = rgInfo.accountId;
            w2ui['azure_securityGrid'].clear();
            w2ui['azure_inboundGrid'].clear();
            w2ui['azure_outboundGrid'].clear();
            w2utils.unlock($("#layout_layout_panel_main"));
            doSearch();
        }, error : function(request, status, error) {
            w2popup.unlock();
            w2ui['azure_securityGrid'].clear();
            w2ui['azure_outboundGrid'].clear();
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

/****************************************************
 * 기능 : clearMainPage
 * 설명 : 다른페이지 이동시 호출
*****************************************************/
function clearMainPage() {
    $().w2destroy('azure_securityGrid');
    $().w2destroy('azure_inboundGrid');
    $().w2destroy('azure_outboundGrid');
}

/****************************************************
 * 기능 : resize
 * 설명 : 화면 리사이즈시 호출
*****************************************************/
$( window ).resize(function() {
  setLayoutContainerHeight();
});

</script>
<style>
.trTitle {
     background-color: #f3f6fa;
     width: 180px;
 }
td {
    width: 280px;
}
 
</style>
<div id="main">
     <div class="page_site pdt20">인프라 관리 > Azure 관리 > <strong>Azure Security Group 관리 </strong></div>
     <div id="azureMgnt" class="pdt20">
        <ul>
            <li>
                <label style="font-size: 14px;">Azure 관리 화면</label> &nbsp;&nbsp;&nbsp; 
                <div class="dropdown" style="display:inline-block;">
                    <a href="#" class="dropdown-toggle iaas-dropdown" data-toggle="dropdown" aria-expanded="false">
                        &nbsp;&nbsp;Security Group 관리<b class="caret"></b>
                    </a>
                    <ul class="dropdown-menu alert-dropdown">
                        <sec:authorize access="hasAuthority('AZURE_RESOURCE_GROUP_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/azureMgnt/resourceGroup"/>', 'Azure Resource Group');">Resource Group 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('AZURE_NETWORK_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/azureMgnt/network"/>', 'Azure Virtual Network');">Virtual Network 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('AZURE_STORAGE_ACCOUNT_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/azureMgnt/storageAccount"/>', 'Azure Storage Account');"> Storage Account 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('AZURE_PUBLIC_IP_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/azureMgnt/publicIp"/>', 'Azure Public IP');">Public IP 관리</a></li>
                        </sec:authorize>
                         <sec:authorize access="hasAuthority('AZURE_STORAGE_ACCESS_KEY_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/azureMgnt/storageAccessKey"/>', 'Azure Key Pair');">Key Pair 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('AZURE_ROUTE_TABLE_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/azureMgnt/routeTable"/>', 'Azure Route Table');">Route Table 관리</a></li>
                        </sec:authorize>
                    </ul>
                </div>
            </li>
            
            <li>
                <label style="font-size: 14px">Azure 계정 명</label>
                &nbsp;&nbsp;&nbsp;
                <select name="accountId" id="setAccountList" class="select" style="width: 300px; font-size: 15px; height: 32px;" onchange="setAccountInfo(this.value, 'azure')">
                </select>
                <span id="doSearch" onclick="setDefaultIaasAccount('noPopup','azure');" class="btn btn-info" style="width:80px" >선택</span>
            </li>
        </ul>
    </div>
    
    <div class="pdt20">
        <div class="title fl">Azure Security Group 목록</div>
        <div class="fr"> 
        <sec:authorize access="hasAuthority('AZURE_SECURITY_GROUP_CREATE')">
            <span id="addBtn" class="btn btn-primary" style="width:120px">생성</span>
        </sec:authorize>
        <sec:authorize access="hasAuthority('AZURE_SECURITY_GROUP_DELETE')">
            <span id="deleteBtn" class="btn btn-danger" style="width:120px">삭제</span>
        </sec:authorize>
        </div>
    </div>
    
    <!-- Security Group 정보 목록 그리드 -->
    <div id="azure_securityGrid" style="width:100%; height:305px"></div>

    <!-- Security Group 생성 팝업 -->
    <div id="registPopupDiv" hidden="true">
        <form id="azureSecurityGroupForm" action="POST" style="padding:5px 0 5px 0;margin:0;">
            <div class="panel panel-info" style="height: 270px; margin-top: 7px;"> 
                <div class="panel-heading"><b>Azure Security Group 생성 정보</b></div>
                <div class="panel-body" style="padding:20px 10px; height:250px; overflow-y:auto;">
                    <input type="hidden" name="accountId"/>
                    <div class="w2ui-field">
                        <label style="width:36%;text-align: left; padding-left: 20px;">Security Group Name</label>
                        <div>
                            <input name="securityGroupName" type="text"   maxlength="100" style="width: 300px; margin-top: 1px;" placeholder="Security Group명을 입력하세요."/>
                        </div>
                    </div>
                    
                    <div class="w2ui-field">
                        <label style="width:36%;text-align: left; padding-left: 20px;">Resource Group</label>
                         <div id="resourceGroupInfoDiv">
                            <select id="resourceGroupInfo" name="resourceGroupName" onClick = "azureResourceGroupOnchange(this.value, 'selected')" class="select" style="width:300px; "></select>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="width:36%;text-align: left; padding-left: 20px;">Location</label>
                         <div id="locationInfoDiv">
                         <div id="locationInfo" style="width:300px; border: 1px solid #ccc; border-radius:2px; padding-left:5px; line-height:26px; background-color: #eee; color:#777 !important;" >리소스 그룹의 리전 명</div>
                                <input id ="locationVal" name="location" hidden="true" readonly='readonly'  style="width:300px; "/> 
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="width:36%;text-align: left; padding-left: 20px;">Performance</label>
                        <div>
                            <input name="" type="text" readonly="readonly"  maxlength="100" style="width: 300px; margin-top: 1px;" placeholder="Standard"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="width:36%;text-align: left; padding-left: 20px;">Subscription</label>
                        <div id="subscriptionInfoDiv">
                            <div id="subscriptionInfo"><input style="width:300px;" placeholder="Loading..."/></div>
                        </div>
                    </div>
                </div>
            </div>
        </form> 
    </div>
    
    <div id="registPopupBtnDiv" hidden="true">
         <button class="btn" id="registBtn" onclick="$('#azureSecurityGroupForm').submit();">확인</button>
         <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
    </div>
    
    <div class="pdt20" >
        <div class="title fl">Azure Inbounds 정보 목록</div>
    </div>
    <div id="azure_inboundGrid" style="width:100%; min-height:200px; top:0px;"></div>
    
    <div class="pdt20" >
        <div class="title fl">Azure Outbounds 정보 목록</div>
    </div>
    <div id="azure_outboundGrid" style="width:100%; min-height:200px; top:0px;"></div>
    
    
</div>

<div id="registAccountPopupDiv"  hidden="true">
    <input name="codeIdx" type="hidden"/>
    <div class="panel panel-info" style="margin-top:5px;" >    
        <div class="panel-heading"><b>Azure 계정 별칭 목록</b></div>
        <div class="panel-body" style="padding:5px 5% 10px 5%;height:65px;">
            <div class="w2ui-field">
                <label style="width:30%;text-align: left;padding-left: 20px; margin-top: 20px;">Azure 계정 별칭</label>
                <div style="width: 70%;" class="accountList"></div>
            </div>
        </div>
    </div>
</div>

<div id="registAccountPopupBtnDiv" hidden="true">
    <button class="btn" id="registBtn" onclick="setDefaultIaasAccount('popup','azure');">확인</button>
    <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
</div>

<script>
$(function() {
    $("#azureSecurityGroupForm").validate({
        ignore : "",
        onfocusout: true,
        rules: {
            securityGroupName : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='securityGroupName']").val() );
                }
            } 
           
        }, messages: {
            securityGroupName: { 
                 required:  "Security Group Name" + text_required_msg
            } 
           
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
            saveAzureSecurityGroup();
        }
    });
});

</script>