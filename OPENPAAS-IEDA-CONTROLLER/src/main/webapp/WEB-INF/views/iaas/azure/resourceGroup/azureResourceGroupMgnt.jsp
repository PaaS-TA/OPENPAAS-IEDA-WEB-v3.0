<%
/* =================================================================
 * 작성일 : 2018.04.00
 * 작성자 : 이정윤 
 * 상세설명 : Azure Resource Group 관리 화면
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
var delete_lock_msg='<spring:message code="common.delete.data.lock"/>';//삭제 중 입니다.

var accountId ="";
var bDefaultAccount = "";


$(function() {
    
    bDefaultAccount = setDefaultIaasAccountList("azure");
    
    $('#azure_rgGrid').w2grid({
        name: 'azure_rgGrid',
        method: 'GET',
        msgAJAXerror : 'Azure 계정을 확인해주세요.',
        header: '<b>Resource Group 목록</b>',
        multiSelect: false,
        show: {    
                selectColumn: false,
                footer: true},
        style: 'text-align: center',
        columns    : [
                     {field: 'recid',     caption: 'recid', hidden: true}
                   , {field: 'accountId',     caption: 'accountId', hidden: true}
                   , {field: 'name', caption: 'Resource Group Name', size: '50%', style: 'text-align:center', render : function(record){
                       if(record.name == null || record.name == ""){
                           return "-"
                       }else{
                           return record.name;
                       }}
                   }
                   , {field: 'azureSubscriptionId', caption: 'Subscription ID', size: '50%', style: 'text-align:center'}
                   , {field: 'location', caption: 'Location', size: '50%', style: 'text-align:center'}
                   , {field: 'resourceGroupId', caption: 'Resource Group Id', size: '50%', style: 'text-align:center'}
                   , {field: 'status', caption: 'Status', size: '50%', style: 'text-align:center'}
                   ],
        onSelect: function(event) {
            event.onComplete = function() {
                $('#deleteBtn').attr('disabled', false);
                var accountId =  w2ui.azure_rgGrid.get(event.recid).accountId;
                var resourceGroupName = w2ui.azure_rgGrid.get(event.recid).name;
                var rglocation = w2ui.azure_rgGrid.get(event.recid).location;
                doSearchRgDetailInfo(accountId, resourceGroupName); 
                doSearchRgResourceInfo(accountId, resourceGroupName); 
            }
        },
        onUnselect: function(event) {
            event.onComplete = function() {
                $('#deleteBtn').attr('disabled', true);
                w2ui['azure_resourceGrid'].clear();
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
    
    $('#azure_resourceGrid').w2grid({
        name: 'azure_resourceGrid',
        method: 'GET',
        msgAJAXerror : 'Azure 계정을 확인해주세요.',
        header: '<b>Resource 목록</b>',
        multiSelect: false,
        show: {    
                selectColumn: false,
                footer: true},
        style: 'text-align: center',
        columns    : [
                     {field: 'recid',     caption: 'recid', hidden: true}
                   , {field: 'accountId',     caption: 'accountId', hidden: true}
                   , {field: 'resourceName', caption: 'Resource Name', size: '50%', style: 'text-align:center', info: true }
                   , {field: 'resourceType', caption: 'Resource Type', size: '50%', style: 'text-align:center'}
                   , {field: 'resourceLocation', caption: 'Location', size: '50%', style: 'text-align:center'}
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
     * 설명 : Resource Group 생성 버튼 클릭
    *********************************************************/
    $("#addBtn").click(function(){
       if($("#addBtn").attr('disabled') == "disabled") return;
       w2popup.open({
           title   : "<b>Azure Resource Group 생성</b>",
           width   : 580,
           height  : 370,
           modal   : true,
           body    : $("#registPopupDiv").html(),
           buttons : $("#registPopupBtnDiv").html(),
           onOpen  : function () {
               setAzureSubscription();
               setAzureRegion();
           },
           onClose : function(event){
            accountId = $("select[name='accountId']").val();
            w2ui['azure_rgGrid'].clear();
            w2ui['azure_resourceGrid'].clear();
            doSearch();
            $("#rgDetailTable td").html("");
           }
       });
    });
    
    /********************************************************
    * 설명 : Resource Group 삭제 버튼 클릭
   *********************************************************/
    $("#deleteBtn").click(function(){
        if($("#deleteBtn").attr('disabled') == "disabled") return;
        var selected = w2ui['azure_rgGrid'].getSelection();        
        if( selected.length == 0 ){
            w2alert("선택된 정보가 없습니다.", "Resource Group 삭제");
            return;
        }
        else {
            var record = w2ui['azure_rgGrid'].get(selected);
            w2confirm({
                title   : "<b>Resource Group 삭제</b>",
                msg     : "Resource Group (" + record.name + ") 안의 <br/> 모든 Resource 정보가 삭제됩니다</font></strong><br/>"
                                       +"<strong><font color='red'>그래도 삭제 하시 겠습니까?</strong><red>"   ,
                yes_text : "확인",
                no_text : "취소",
                height : 350,
                yes_callBack: function(event){
                    deleteAzureResourceGroupInfo(record);
                },
                no_callBack    : function(){
                    w2ui['azure_rgGrid'].clear();
                    w2ui['azure_resourceGrid'].clear();
                    accountId = record.accountId;
                    doSearch();
                    $("#rgDetailTable td").html("");
                }
            });
        }
    });
    
});

/********************************************************
 * 설명 : Resource Group 목록 조회 Function 
 * 기능 : doSearch
 *********************************************************/
function doSearch() {
    w2ui['azure_rgGrid'].load("<c:url value='/azureMgnt/resourceGroup/list/'/>"+accountId);
    doButtonStyle();
    accountId = "";
}

/********************************************************
 * 설명 : Resource Group 정보 상세 조회 Function 
 * 기능 : doSearchRgDetailInfo
 *********************************************************/
function doSearchRgDetailInfo(accountId, resourceGroupName){
    w2utils.lock($("#layout_layout_panel_main"), detail_rg_lock_msg, true);
    $.ajax({
        type : "GET",
        url : "/azureMgnt/resourceGroup/save/detail/"+accountId+"/"+resourceGroupName+"",
        contentType : "application/json",
        success : function(data, status) {
            w2utils.unlock($("#layout_layout_panel_main"));
        
            if(data != null){
                $(".resourceGroupName").html(data.name);
                $(".subscriptionName").html(data.subscriptionName);
                $(".deployments").html(data.deployments);
            }
            
        },
        error : function(request, status, error) {
            w2utils.unlock($("#layout_layout_panel_main"));
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "Azure Resource Group 상세 조회");
        }
    });
}
/********************************************************
 * 설명 : Resource Group 해당 Resource List 조회 Function 
 * 기능 : doSearchRgResourceInfo
 *********************************************************/
function doSearchRgResourceInfo(accountId, resourceGroupName){
    w2utils.lock($("#layout_layout_panel_main"), detail_rg_lock_msg, true);
    w2ui['azure_resourceGrid'].load("<c:url value='/azureMgnt/resourceGroup/save/detail/resource/'/>"+accountId+"/"+resourceGroupName);
    
}

/********************************************************
 * 설명 : Azure Resource Group 생성
 * 기능 : saveAzureRGInfo
 *********************************************************/
function saveAzureRGInfo(){
     w2popup.lock(save_lock_msg, true);
    var rgInfo = {
        accountId : $("select[name='accountId']").val(),
        rglocation : $(".w2ui-msg-body select[name='rglocation']").val(),    
        name : $(".w2ui-msg-body input[name='nameTag']").val(),
        azureSubscriptionId : $(".w2ui-msg-body input[name='azureSubscriptionId']").val(),
    }
    $.ajax({
        type : "POST",
        url : "/azureMgnt/resourceGroup/save",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(rgInfo),
        success : function(status) {
            w2popup.unlock();
            w2popup.close();
            accountId = rgInfo.accountId;
            doSearch();
        }, error : function(request, status, error) {
            w2popup.unlock();
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message);
        }
    });
}

/********************************************************
 * 기능 : setAzureRegion
 * 설명 : 기본  Azure 리전 정보 목록 조회 기능
 *********************************************************/
 /********************************************************
* 애저 전체 리전 정보 :
 centralus,eastasia,southeastasia,eastus,eastus2,westus,westus2,northcentralus,southcentralus,westcentralus,
 northeurope,westeurope,japaneast,japanwest,brazilsouth,australiasoutheast,australiaeast,westindia,
 southindia,centralindia,canadacentral,canadaeast,uksouth,ukwest,koreacentral,koreasouth 

 * 리소스 그룹 만들 수 없는 리전 :
 usgovtexas,usgoviowa,chinanorth,chinaeast,germanycentral,usdodcentral,usgovvirginia,
 usgovarizona,germanynortheast,usdodeast
 ********************************************************/
 function setAzureRegion(){
    $.ajax({
        type : "GET",
        url : '/azureMgnt/resourceGroup/save/azure/region/list',
        contentType : "application/json",
        dataType : "json",
        success : function(data, status) {
            var result = "";
            for(var i=0; i<data.length; i++){
                if(data[i] != "usgovtexas" && data[i] != "usgoviowa" && data[i] != "chinanorth" && data[i] != "chinaeast" && data[i] != "germanycentral" && data[i] != "usdodcentral" &&data[i] != "usgovvirginia" &&data[i] != "usgovarizona" && data[i] != "germanynortheast" && data[i] != "usdodeast" ){
                    if(data[i] == "centralus"){
                        result += "<option value='" + data[i] + "'selected >";
                        result += data[i];
                        result += "</option>"; 
                    }else{
                        result += "<option value='" + data[i] + "' >";
                        result += data[i];
                        result += "</option>"; 
                    }
                }
            }
            $("#locationListDiv #locationList").html(result);
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
  * 설명 : 기본  Azure 구독 정보 목록 조회 기능
  *********************************************************/
function setAzureSubscription(){
     accountId = $("select[name='accountId']").val();
     $.ajax({
            type : "GET",
            url : '/azureMgnt/resourceGroup/save/azure/subscription/list/'+accountId,
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
 * 설명 : Azure Region Onchange 이벤트 기능
 * 기능 : azureRegionOnchange
 *********************************************************/
 function azureRegionOnchange(){
    accountId = $("select[name='accountId']").val();
} 

 /********************************************************
  * 설명 : Azure Subscription Onchange 이벤트 기능
  * 기능 : azureSubscriptionOnchange
  *********************************************************/
  function azureSubscriptionOnchange(){
     accountId = $("select[name='accountId']").val();
 }
 
  /********************************************************
   * 설명 : Azure Resource Group 삭제
   * 기능 : deleteAzureResourceGroupInfo
   *********************************************************/
  function deleteAzureResourceGroupInfo(record){
      w2utils.lock($("#layout_layout_panel_main"), delete_lock_msg, true);
      var rgInfo = {
         accountId : record.accountId,
         name : record.name
      }
      $.ajax({
          type : "DELETE",
          url : "/azureMgnt/resouceGroup/delete",
          contentType : "application/json",
          async : true,
          data : JSON.stringify(rgInfo),
          success : function(status) {
              w2utils.unlock($("#layout_layout_panel_main"));
              w2popup.close();
              accountId = rgInfo.accountId;
              w2ui['azure_rgGrid'].clear();
              w2ui['azure_resourceGrid'].clear();
              $("#rgDetailTable td").html("");
              doSearch(); 
          }, error : function(request, status, error) {
              w2popup.unlock();
              $("#rgDetailTable td").html("");
              w2ui['azure_rgGrid'].clear();
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
    $().w2destroy('azure_rgGrid');
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
     <div class="page_site pdt20">인프라 관리 > azure 관리 > <strong>azure Resource Group 관리 </strong></div>
     <div id="azureMgnt" class="pdt20">
        <ul>
            <li>
                <label style="font-size: 14px;">Azure 관리 화면</label> &nbsp;&nbsp;&nbsp; 
                <div class="dropdown" style="display:inline-block;">
                    <a href="#" class="dropdown-toggle iaas-dropdown" data-toggle="dropdown" aria-expanded="false">
                        &nbsp;&nbsp;Resource Group 관리<b class="caret"></b>
                    </a>
                    <ul class="dropdown-menu alert-dropdown">
                        <sec:authorize access="hasAuthority('AZURE_NETWORK_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/azureMgnt/network"/>', 'Azure Network');">Virtual Network 관리</a></li>
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
                        <sec:authorize access="hasAuthority('AZURE_SECURITY_GROUP_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/azureMgnt/securityGroup"/>', 'Azure Security Group');">Security Group 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('AZURE_ROUTE_TABLE_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/azureMgnt/routeTable"/>', 'Azure Route Tablee');">Route Table 관리</a></li>
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
        <div class="title fl">Azure Resource Group 목록</div>
        <div class="fr"> 
        <%-- <sec:authorize access="hasAuthority('AZURE_RESOURCE_GROUP_CREATE')"> --%>
            <sec:authorize access="hasAuthority('AWS_VPC_CREATE')">
            <span id="addBtn" class="btn btn-primary" style="width:120px">생성</span>
            </sec:authorize>
            <%-- <sec:authorize access="hasAuthority('AZURE_RESOURCE_GROUP_DELETE')"> --%>
            <sec:authorize access="hasAuthority('AWS_VPC_DELETE')">
            <span id="deleteBtn" class="btn btn-danger" style="width:120px">삭제</span>
            </sec:authorize>
        </div>
    </div>
    <div id="azure_rgGrid" style="width:100%; height:305px"></div>

<!-- Resource Group 생성 팝업 -->
<div id="registPopupDiv" hidden="true">
    <form id="azureRGForm" action="POST" style="padding:5px 0 5px 0;margin:0;">
        <div class="panel panel-info" style="height: 260px; margin-top: 7px;"> 
            <div class="panel-heading"><b>azure Resource Group 정보</b></div>
            <div class="panel-body" style="padding:20px 10px; height:220px; overflow-y:auto;">
                <input type="hidden" name="accountId"/>
                <div class="w2ui-field">
                    <label style="width:36%;text-align: left; padding-left: 20px;">Resource Group Name</label>
                    <div>
                        <input name="nameTag" type="text"   maxlength="100" style="width: 300px; margin-top: 1px;" placeholder="Resource Group 태그 명을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:36%;text-align: left; padding-left: 20px;">RG Location</label>
                    <div id="locationListDiv">
                        <select name="rglocation" onClick = "azureRegionOnchange()" id="locationList" class="select" style="width:300px; font-size: 15px; height: 32px;"></select>
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
     <button class="btn" id="registBtn" onclick="$('#azureRGForm').submit();">확인</button>
     <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
</div>
    <div class="pdt20" >
        <div class="title fl">Azure Resource Group 상세 목록</div>
    </div>
    <div id="azure_rgDetailGrid" style="width:100%; height:128px; margin-top:50px; border-top: 2px solid #c5c5c5; ">
    <table id= "rgDetailTable" class="table table-condensed table-hover">
           <tr>
               <th class= "trTitle">Resource Group Name</th>
               <td class="resourceGroupName"></td>
               <th class= "trTitle">Subscription Name</th>
               <td class="subscriptionName"></td>
               <th class= "trTitle">Deployments</th>
               <td class= "deployments"></td>
           </tr>
        </table>
    </div>
    
        
        <div id="azure_resourceGrid" style="width:100%; min-height:200px; top:0px;"></div>
 
</div>

<div id="registAccountPopupDiv"  hidden="true">
    <input name="codeIdx" type="hidden"/>
    <div class="panel panel-info" style="margin-top:5px;" >    
        <div class="panel-heading"><b>azure 계정 별칭 목록</b></div>
        <div class="panel-body" style="padding:5px 5% 10px 5%;height:65px;">
            <div class="w2ui-field">
                <label style="width:30%;text-align: left;padding-left: 20px; margin-top: 20px;">azure 계정 별칭</label>
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
    $("#azureRGForm").validate({
        ignore : "",
        onfocusout: true,
        rules: {
            nameTag : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='nameTag']").val() );
                },
            }, 
            azureSubscriptionId: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='azureSubscriptionId']").val() );
                }
            }, 
            rglocation: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body select[name='rglocation']").val() );
                }
            }
        }, messages: {
            nameTag: { 
                 required:  "Name" + text_required_msg
            }, 
            azureSubscriptionId: { 
                required:  "Subscription "+text_required_msg
                
            }, 
            rglocation: { 
                required:  "Location "+text_required_msg
                
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
            saveAzureRGInfo();
        }
    });
});
</script>