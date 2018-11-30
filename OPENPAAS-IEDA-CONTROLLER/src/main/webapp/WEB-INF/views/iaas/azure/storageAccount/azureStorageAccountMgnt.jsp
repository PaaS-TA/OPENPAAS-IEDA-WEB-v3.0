<%
/* =================================================================
 * 작성일 : 2018.04.16
 * 작성자 : 이정윤 
 * 상세설명 : Azure  Storage Account 관리 화면
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
    $('#azureStorageGrid').w2grid({
        name: 'azureStorageGrid',
        method: 'GET',
        msgAJAXerror : 'Azure 계정을 확인해주세요.',
        header: '<b>Storage Account 목록</b>',
        multiSelect: false,
        show: {    
                selectColumn: true,
                footer: true},
        style: 'text-align: center',
        columns    : [
                     {field: 'recid',     caption: 'recid', hidden: true}
                   , {field: 'accountId',     caption: 'accountId', hidden: true}
                   , {field: 'storageAccountId',     caption: 'accountId', hidden: true}
                   , {field: 'storageAccountName', caption: 'Storage Account Name', size: '50%', style: 'text-align:center', render : function(record){
                       if(record.storageAccountName == null || record.storageAccountName == ""){
                           return "-"
                       }else{
                           return record.storageAccountName;
                       }}
                   }
                   , {field: 'subscriptionName', caption: 'Subscription', size: '50%', style: 'text-align:center'}
                   , {field: 'azureSubscriptionId', caption: 'Subscription ID', size: '50%', style: 'text-align:center'}
                   , {field: 'accountType', caption: 'Type', size: '50%', style: 'text-align:center'}
                   , {field: 'location', caption: 'Location', size: '50%', style: 'text-align:center'}
                   , {field: 'resourceGroupName', caption: 'Resource Group', size: '50%', style: 'text-align:center'}
                   ],
        onSelect: function(event) {
            event.onComplete = function() {
                $('#deleteBtn').attr('disabled', false);
                $('#addBlobBtn').attr('disabled', false);
                $('#addTableBtn').attr('disabled', false);
                var accountId =  w2ui.azureStorageGrid.get(event.recid).accountId;
                var storageAccountName = w2ui.azureStorageGrid.get(event.recid).storageAccountName;
                doSearchBlobsInfo(accountId, storageAccountName); 
                doSearchTablesInfo(accountId, storageAccountName); 
            }
        },
        onUnselect: function(event) {
            event.onComplete = function() {
                $('#deleteBtn').attr('disabled', true);
                $('#addBlobBtn').attr('disabled', true);
                $('#addTableBtn').attr('disabled', true);
                w2ui['azure_blobsGrid'].clear();
                w2ui['azure_tablesGrid'].clear();
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
    
    $('#azure_blobsGrid').w2grid({
        name: 'azure_blobsGrid',
        method: 'GET',
        msgAJAXerror : 'Azure 계정을 확인해주세요.',
        header: '<b>Storage Account의 Blobs 목록</b>',
        multiSelect: false,
        show: {    
                selectColumn: false,
                footer: true},
        style: 'text-align: center',
        columns    : [
                     {field: 'recid',     caption: 'recid', hidden: true}
                   , {field: 'storageAccountName',     caption: 'Storage Account Name',  size: '50%', style: 'text-align:center'}
                   , {field: 'resourceGroupName',     caption: 'resourceGroupName', hidden: true}
                   , {field: 'blobName', caption: 'Blob Name', size: '50%', style: 'text-align:center'}
                   , {field: 'publicAccessLevel', caption: 'Public Access Level', size: '50%', style: 'text-align:center'}
                   , {field: 'leaseState', caption: 'Lease State', size: '50%', style: 'text-align:center'}
                   , {field: 'etag', caption: 'Etag', size: '50%', style: 'text-align:center'}
                   ],
        onSelect: function(event) {
            event.onComplete = function() {
                $('#deleteBlobBtn').attr('disabled', false);
            }
        },
        onUnselect: function(event) {
            event.onComplete = function() {
                $('#deleteBlobBtn').attr('disabled', true);
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
    
    $('#azure_tablesGrid').w2grid({
        name: 'azure_tablesGrid',
        method: 'GET',
        msgAJAXerror : 'Azure 계정을 확인해주세요.',
        header: '<b>Storage Account의 Tables 목록</b>',
        multiSelect: false,
        show: {    
                selectColumn: false,
                footer: true},
        style: 'text-align: center',
        columns    : [
                     {field: 'recid',     caption: 'recid', hidden: true}
                   , {field: 'storageAccountName',     caption: 'Storage Account Name',  size: '50%', style: 'text-align:center'}
                   , {field: 'resourceGroupName',     caption: 'resourceGroupName', hidden: true}
                   , {field: 'tableName', caption: 'Table Name', size: '50%', style: 'text-align:center'}
                   , {field: 'tableUrl', caption: 'URL', size: '50%', style: 'text-align:center'}
                   ],
        onSelect: function(event) {
            event.onComplete = function() {
                $('#deleteTableBtn').attr('disabled', false);
            }
        },
        onUnselect: function(event) {
            event.onComplete = function() {
                $('#deleteTableBtn').attr('disabled', true);
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
     * 설명 : Azure Storage Account 생성 버튼 클릭
    *********************************************************/
    $("#addBtn").click(function(){
       if($("#addBtn").attr('disabled') == "disabled") return;
       w2popup.open({
           title   : "<b>Azure Storage Account  생성</b>",
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
            w2ui['azureStorageGrid'].clear();
            w2ui['azure_blobsGrid'].clear();
            w2ui['azure_tablesGrid'].clear();
            doSearch();
           }
       });
    });
    
    /********************************************************
    * 설명 : Azure Storage Account 삭제 버튼 클릭
   *********************************************************/
    $("#deleteBtn").click(function(){
        if($("#deleteBtn").attr('disabled') == "disabled") return;
        var selected = w2ui['azureStorageGrid'].getSelection();        
        if( selected.length == 0 ){
            w2alert("선택된 정보가 없습니다.", "Storage Account 삭제");
            return;
        }
        else {
            var record = w2ui['azureStorageGrid'].get(selected);
            w2confirm({
                title   : "<b>Storage Account 삭제</b>",
                msg     : "Storage Account (" + record.storageAccountName +") 를<br/>"
                                       +"<strong><font color='red'> 삭제 하시 겠습니까?</strong><red>"   ,
                yes_text : "확인",
                no_text : "취소",
                height : 250,
                yes_callBack: function(event){
                    w2utils.lock($("#layout_layout_panel_main"), delete_lock_msg, true);
                    deleteAzureStorageInfo(record);
                },
                no_callBack    : function(){
                    w2ui['azureStorageGrid'].clear();
                    w2ui['azure_blobsGrid'].clear();
                    w2ui['azure_tablesGrid'].clear();
                    accountId = record.accountId;
                    doSearch();
                }
            });
        }
    });
    
    /********************************************************
     * 설명 : Azure Blob 생성 버튼 클릭
    *********************************************************/
    $("#addBlobBtn").click(function(){
         if($("#addBlobBtn").attr('disabled') == "disabled") return;
         
        
       w2popup.open({
           title   : "<b>Network Blob 생성</b>",
           width   : 580,
           height  : 300,
           modal   : true,
           body    : $("#addBlobPopupDiv").html(),
           buttons : $("#addBlobPopupBtnDiv").html(),
           onOpen  : function () {
               var blobsInfo = $('#azure_blobsGrid .w2ui-grid-data').text();
               console.log(blobsInfo +"TEST TEST TEST 1");
               if(blobsInfo.trim().includes("GatewayBlob")){
                  console.log(blobsInfo.trim() +"TEST TEST TEST 2");
                     //존재 할 경우 선택 옵션을 없애준다.
                  //$(".w2ui-popup .w2ui-box1 .w2ui-msg-body #addBlobForm .panel-info .w2ui-field #switchType #types #typeB").hide();
               }
           },
           onClose : function(event){
            w2popup.unlock();
            $("#addBlobForm .blobInfo").html("<input name='blobName' value='bosh' type='text' readonly='readonly'  maxlength='100' style='width: 300px; margin-top: 1px;'/>");
            $("#addBlobForm .stemcellBlobInfo").html(''); 
            accountId = $("select[name='accountId']").val();
            w2ui['azureStorageGrid'].clear();
            w2ui['azure_blobsGrid'].clear();
            w2ui['azure_tablesGrid'].clear();
            doSearch();
           }
       });
    });
    
    /********************************************************
     * 설명 : Azure Blob 삭제 버튼 클릭
    *********************************************************/
     $("#deleteBlobBtn").click(function(){
         if($("#deleteBlobBtn").attr('disabled') == "disabled") return;
         var selected = w2ui['azure_blobsGrid'].getSelection();        
         if( selected.length == 0 ){
             w2alert("선택된 정보가 없습니다.", "Blob 삭제");
             return;
         }
         else {
             var record = w2ui['azure_blobsGrid'].get(selected);
             w2confirm({
                 title   : "<b>Blob 삭제</b>",
                 msg     : "Blob (" + record.blobName +") 을<br/>"
                                        +"<strong><font color='red'> 삭제 하시 겠습니까?</strong><red>"   ,
                 yes_text : "확인",
                 no_text : "취소",
                 height : 250,
                 yes_callBack: function(event){
                     w2utils.lock($("#layout_layout_panel_main"), delete_lock_msg, true);
                     deleteBlob(record);
                 },
                 no_callBack    : function(){
                     w2ui['azureStorageGrid'].clear();
                     w2ui['azure_blobsGrid'].clear();
                     w2ui['azure_tablesGrid'].clear();
                     accountId = record.accountId;
                     doSearch();
                 }
             });
         }
     });
    
     /********************************************************
      * 설명 : Azure Storage Table 생성 버튼 클릭
     *********************************************************/
     $("#addTableBtn").click(function(){
          if($("#addTableBtn").attr('disabled') == "disabled") return;
          
         
        w2popup.open({
            title   : "<b>Storage Table 생성</b>",
            width   : 580,
            height  : 300,
            modal   : true,
            body    : $("#addTablePopupDiv").html(),
            buttons : $("#addTablePopupBtnDiv").html(),
            onOpen  : function () {
            },
            onClose : function(event){
             w2popup.unlock();
             accountId = $("select[name='accountId']").val();
             w2ui['azureStorageGrid'].clear();
             w2ui['azure_blobsGrid'].clear();
             w2ui['azure_tablesGrid'].clear();
             doSearch();
            }
        });
     });
    
     /********************************************************
      * 설명 : Azure Table 삭제 버튼 클릭
     *********************************************************/
      $("#deleteTableBtn").click(function(){
          if($("#deleteTableBtn").attr('disabled') == "disabled") return;
          var selected = w2ui['azure_tablesGrid'].getSelection();        
          if( selected.length == 0 ){
              w2alert("선택된 정보가 없습니다.", "Blob 삭제");
              return;
          }
          else {
              var record = w2ui['azure_tablesGrid'].get(selected);
              w2confirm({
                  title   : "<b>Storage Table 삭제</b>",
                  msg     : "Storage Table (" + record.tableName +") 을<br/>"
                                         +"<strong><font color='red'> 삭제 하시 겠습니까?</strong><red>"   ,
                  yes_text : "확인",
                  no_text : "취소",
                  height : 250,
                  yes_callBack: function(event){
                      w2utils.lock($("#layout_layout_panel_main"), delete_lock_msg, true);
                      deleteTable(record);
                  },
                  no_callBack    : function(){
                      w2ui['azureStorageGrid'].clear();
                      w2ui['azure_blobsGrid'].clear();
                      w2ui['azure_tablesGrid'].clear();
                      accountId = record.accountId;
                      doSearch();
                  }
              });
          }
      });
    
});


/********************************************************
 * 설명 : Azure Storage Account 정보 목록 조회 Function 
 * 기능 : doSearch
 *********************************************************/
function doSearch() {
    w2ui['azureStorageGrid'].load("<c:url value='/azureMgnt/storageAccount/list/'/>"+accountId);
    doButtonStyle();
    accountId = "";
}

/********************************************************
 * 설명 : 해당 Azure Storage Account에 대한 Blobs List 조회 Function 
 * 기능 : doSearchBlobsInfo
 *********************************************************/
function doSearchBlobsInfo(accountId, storageAccountName){
	
    w2utils.lock($("#layout_layout_panel_main"), detail_rg_lock_msg, true);
    w2ui['azure_blobsGrid'].load("<c:url value='/azureMgnt/storageAccount/list/blobs/'/>"+accountId+"/"+storageAccountName);
    w2utils.unlock($("#layout_layout_panel_main"));
}



/********************************************************
 * 설명 : 해당 Azure Storage Account에 대한 Tables List 조회 Function 
 * 기능 : doSearchTablesInfo
 *********************************************************/
function doSearchTablesInfo(accountId, storageAccountName){
	
    w2utils.lock($("#layout_layout_panel_main"), detail_rg_lock_msg, true);
    w2ui['azure_tablesGrid'].load("<c:url value='/azureMgnt/storageAccount/list/tables/'/>"+accountId+"/"+storageAccountName);
    w2utils.unlock($("#layout_layout_panel_main"));
}

/********************************************************
 * 설명 : Azure Storage Account 생성
 * 기능 : saveAzureStorageInfo
 *********************************************************/
function saveAzureStorageInfo(){
    w2popup.lock(save_lock_msg, true);
    var rgInfo = {
        accountId : $("select[name='accountId']").val(),
        storageAccountName : $(".w2ui-msg-body input[name='storageAccountName']").val(),
        resourceGroupName : $(".w2ui-msg-body select[name='resourceGroupName'] :selected").text(),
        location : $(".w2ui-msg-body select[name='resourceGroupName'] :selected").val(),    
        azureSubscriptionId : $(".w2ui-msg-body input[name='azureSubscriptionId']").val(),
    }
    $.ajax({
        type : "POST",
        url : "/azureMgnt/storageAccount/save",
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
 * 설명 : Azure Blob 생성
 * 기능 : addNewBlob
 *********************************************************/
function addNewBlob(){
    w2popup.lock(save_lock_msg, true);
    var selected = w2ui['azureStorageGrid'].getSelection();
    var record = w2ui['azureStorageGrid'].get(selected);
    var rgInfo = {
        accountId : $("select[name='accountId']").val(),
        storageAccountName : record.storageAccountName,
        location : record.location,
        blobName : $(".w2ui-msg-body #addBlobForm input[name='blobName']").val(),
        publicAccessType : $(".w2ui-msg-body #addBlobForm input:radio[name='chk_type']:checked").val(),
    }
    $.ajax({
        type : "POST",
        url : "/azureMgnt/blob/save",
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
 * 설명 : Azure Storage Table 생성
 * 기능 : addNewTable
 *********************************************************/
function addNewTable(){
    w2popup.lock(save_lock_msg, true);
    var selected = w2ui['azureStorageGrid'].getSelection();
    var record = w2ui['azureStorageGrid'].get(selected);
    var tableInfo = {
        accountId : $("select[name='accountId']").val(),
        storageAccountName : record.storageAccountName,
        location : record.location,
        tableName : $(".w2ui-msg-body #addTableForm input[name='tableName']").val(),
    }
    $.ajax({
        type : "POST",
        url : "/azureMgnt/table/save",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(tableInfo),
        success : function(status) {
            w2popup.unlock();
            w2popup.close();
            accountId = tableInfo.accountId;
            doSearch();
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
 * 설명 : 해당 Storage Account 의 Azure Subscription 정보 조회 기능
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
  * 설명 : Azure Storage 삭제
  * 기능 :  deleteAzureStorageInfo
  *********************************************************/
 function  deleteAzureStorageInfo(record){
     w2popup.lock(delete_lock_msg, true);
     var rgInfo = {
             accountId : record.accountId,
             storageAccountId : record.storageAccountId
     }
     $.ajax({
         type : "DELETE",
         url : "/azureMgnt/storageAccount/delete",
         contentType : "application/json",
         async : true,
         data : JSON.stringify(rgInfo),
         success : function(status) {
             w2popup.unlock();
             w2popup.close();
             accountId = rgInfo.accountId;
             w2ui['azureStorageGrid'].clear();
             w2ui['azure_blobsGrid'].clear();
             w2ui['azure_tablesGrid'].clear();
             w2utils.unlock($("#layout_layout_panel_main"));
             doSearch();
         }, error : function(request, status, error) {
             w2popup.unlock();
             w2ui['azureStorageGrid'].clear();
             w2ui['azure_tablesGrid'].clear();
             var errorResult = JSON.parse(request.responseText);
             w2alert(errorResult.message);
         }
     });
 }
 
 /********************************************************
  * 설명 : Azure StorageAccount blob 삭제
  * 기능 : deleteBlob
  *********************************************************/
 function  deleteBlob(record){
     w2popup.lock(delete_lock_msg, true);
     var selectedStorage = w2ui['azureStorageGrid'].getSelection();
     var storageRecord = w2ui['azureStorageGrid'].get(selectedStorage);
     var rgInfo = {
             accountId : storageRecord.accountId,
             storageAccountName : storageRecord.storageAccountName,
             blobName : record.blobName,
     }
     $.ajax({
         type : "DELETE",
         url : "/azureMgnt/blob/delete",
         contentType : "application/json",
         async : true,
         data : JSON.stringify(rgInfo),
         success : function(status) {
             w2popup.unlock();
             w2utils.unlock($("#layout_layout_panel_main"));
             accountId = rgInfo.accountId;
             w2ui['azureStorageGrid'].clear();
             w2ui['azure_blobsGrid'].clear();
             w2ui['azure_tablesGrid'].clear();
             doSearch();
             w2popup.close();
         }, error : function(request, status, error) {
             w2popup.unlock();
             var errorResult = JSON.parse(request.responseText);
             w2alert(errorResult.message);
         }
     });
 }
 
 /********************************************************
  * 설명 : Azure StorageAccount Table삭제
  * 기능 : deleteTable
  *********************************************************/
 function  deleteTable(record){
     w2popup.lock(delete_lock_msg, true);
     var selectedStorage = w2ui['azureStorageGrid'].getSelection();
     var storageRecord = w2ui['azureStorageGrid'].get(selectedStorage);
     var tableInfo = {
             accountId : storageRecord.accountId,
             storageAccountName : storageRecord.storageAccountName,
             tableName : record.tableName,
     }
     $.ajax({
         type : "DELETE",
         url : "/azureMgnt/table/delete",
         contentType : "application/json",
         async : true,
         data : JSON.stringify(tableInfo),
         success : function(status) {
             w2popup.unlock();
             w2utils.unlock($("#layout_layout_panel_main"));
             accountId = tableInfo.accountId;
             w2ui['azureStorageGrid'].clear();
             w2ui['azure_blobsGrid'].clear();
             w2ui['azure_tablesGrid'].clear();
             doSearch();
             w2popup.close();
         }, error : function(request, status, error) {
             w2popup.unlock();
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
    $('#addBlobBtn').attr('disabled', true);
    $('#deleteBlobBtn').attr('disabled', true);
    $('#addTableBtn').attr('disabled', true);
    $('#deleteTableBtn').attr('disabled', true);
}

/****************************************************
 * 기능 : clearMainPage
 * 설명 : 다른페이지 이동시 호출
*****************************************************/
function clearMainPage() {
    $().w2destroy('azureStorageGrid');
    $().w2destroy('azure_blobsGrid');
    $().w2destroy('azure_tablesGrid');
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
     <div class="page_site pdt20">인프라 관리 > Azure 관리 > <strong>Azure Storage Account 관리 </strong></div>
     <div id="azureMgnt" class="pdt20">
        <ul>
            <li>
                <label style="font-size: 14px;">Azure 관리 화면</label> &nbsp;&nbsp;&nbsp; 
                <div class="dropdown" style="display:inline-block;">
                    <a href="#" class="dropdown-toggle iaas-dropdown" data-toggle="dropdown" aria-expanded="false">
                        &nbsp;&nbsp;Storage Account 관리<b class="caret"></b>
                    </a>
                    <ul class="dropdown-menu alert-dropdown">
                        <sec:authorize access="hasAuthority('AZURE_RESOURCE_GROUP_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/azureMgnt/resourceGroup"/>', 'Azure Resource Group');">Resource Group 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('AZURE_NETWORK_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/azureMgnt/network"/>', 'Azure Virtual Network');">Virtual Network 관리</a></li>
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
        <div class="title fl">Azure Storage Account 목록</div>
        <div class="fr"> 
        <sec:authorize access="hasAuthority('AZURE_STORAGE_ACCOUNT_CREATE')">
            <span id="addBtn" class="btn btn-primary" style="width:120px">생성</span>
        </sec:authorize>
        <sec:authorize access="hasAuthority('AZURE_STORAGE_ACCOUNT_DELETE')">
            <span id="deleteBtn" class="btn btn-danger" style="width:120px">삭제</span>
        </sec:authorize>
        </div>
    </div>
    
    <!-- Storage Account 정보 목록 그리드 -->
    <div id="azureStorageGrid" style="width:100%; height:305px"></div>

    <!-- Storage Account 생성 팝업 -->
    <div id="registPopupDiv" hidden="true">
        <form id="azureStorageForm" action="POST" style="padding:5px 0 5px 0;margin:0;">
            <div class="panel panel-info" style="height: 270px; margin-top: 7px;"> 
                <div class="panel-heading"><b>Azure Storage Account 생성 정보</b></div>
                <div class="panel-body" style="padding:20px 10px; height:250px; overflow-y:auto;">
                    <input type="hidden" name="accountId"/>
                    <div class="w2ui-field">
                        <label style="width:36%;text-align: left; padding-left: 20px;">Storage Account Name</label>
                        <div>
                            <input name="storageAccountName" type="text"   maxlength="100" style="width: 300px; margin-top: 1px;" placeholder="Storage Account명을 입력하세요."/>
                        </div>
                    </div>
                    
                    <div class="w2ui-field">
                        <label style="width:36%;text-align: left; padding-left: 20px;">Resource Group</label>
                         <div id="resourceGroupInfoDiv">
                            <select id="resourceGroupInfo" name="resourceGroupName" onClick = "azureResourceGroupOnchange(this.value, 'selected')" class="select" style="width:300px; font-size: 15px; height: 32px;"></select>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="width:36%;text-align: left; padding-left: 20px;">Location</label>
                         <div id="locationInfoDiv">
                         <div id="locationInfo" style="width:300px; font-size: 15px; height: 26px; border: 1px solid #ccc; border-radius:2px; padding-left:5px; line-height:26px; background-color: #eee; color:#777 !important;" >리소스 그룹의 리전 명</div>
                                <input id ="locationVal" name="location" hidden="true" readonly='readonly'  style="width:300px; font-size: 15px; height: 32px;"/> 
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
         <button class="btn" id="registBtn" onclick="$('#azureStorageForm').submit();">확인</button>
         <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
    </div>
    
    <div class="pdt20" >
        <div class="title fl">Azure Blobs 정보 목록</div>
    </div>
    <div class="fr"> 
        <sec:authorize access="hasAuthority('AZURE_STORAGE_CONTAINER_CREATE')">
            <span id="addBlobBtn" class="btn btn-primary" style="width:120px">Blob 추가</span>
            </sec:authorize>
        <sec:authorize access="hasAuthority('AZURE_STORAGE_CONTAINER_DELETE')">
            <span id="deleteBlobBtn" class="btn btn-danger" style="width:120px">Blob 삭제</span>
        </sec:authorize>
    </div>
    
    <div id="azure_blobsGrid" style="width:100%; min-height:200px; top:0px;"></div>
    
    <div class="pdt20" >
        <div class="title fl">Azure Tables 정보 목록</div>
    </div>
    <div class="fr">
        <sec:authorize access="hasAuthority('AZURE_STORAGE_ACCOUNT_TABLE_CREATE')">
          <span id="addTableBtn" class="btn btn-primary" style="width:120px">Table 추가</span> 
        </sec:authorize>
        <sec:authorize access="hasAuthority('AZURE_STORAGE_ACCOUNT_TABLE_DELETE')">
          <span id="deleteTableBtn" class="btn btn-danger" style="width:120px">Table 삭제</span>
        </sec:authorize>
    </div>
    
    <div id="azure_tablesGrid" style="width:100%; min-height:200px; top:0px;"></div>
    
    <!-- Storage Account blob 생성 팝업 -->
    <div id="addBlobPopupDiv" hidden="true">
        <form id="addBlobForm" action="POST" style="padding:5px 0 5px 0;margin:0;">
            <div class="panel panel-info" style="height: 200px; margin-top: 7px;"> 
                <div class="panel-heading"><b> Blob 생성</b></div>
                <div class="panel-body" style="padding:20px 10px; height:150px; overflow-y:auto;">
                    <input type="hidden" name="accountId"/>
                    <div class="w2ui-field">
                    <ul>
                    <li style="display: inline-block;">
                        <label style="width:150px; text-align: left; padding-left: 20px;">Public Access Level</label>
                    </li>
                    <li style="display: inline-block; padding-left: 20px;"> 
                        <div id="switchType"> 
                            <input type="radio" name="chk_type" value="private" checked="checked">Private &nbsp; &nbsp;
                            <div id="types" style="display:inline-block;">
                                <input id="typeB" type="radio" name="chk_type" value="blob"> <span class="typeB">Blob </span>
                            </div>
                        </div>
                     </li>
                     </ul>
                     <ul>
                     <li style="display: inline-block;"> 
                        <label style="width:150px; text-align: left; padding-left: 20px;">Blob Name</label>
                     </li>
                     <li style="display: inline-block; padding-left: 20px;">   
                        <div class="blobInfo">
                            <input name='blobName' value='bosh' type='text' readonly='readonly'  maxlength='100' style='width: 300px; margin-top: 1px;'/>
                        </div>
                        <div class="stemcellBlobInfo" style="margin-left:-5px;">
                        </div>
                        </li> 
                   </ul>      
                    </div>
                    
                </div>
            </div>
        </form> 
    </div>
    
    <div id="addBlobPopupBtnDiv" hidden="true">
         <button class="btn" id="registBtn" onclick="$('#addBlobForm').submit();">확인</button>
         <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
    </div>
    
    <!-- Storage Account Table 생성 팝업 -->
    <div id="addTablePopupDiv" hidden="true">
        <form id="addTableForm" action="POST" style="padding:5px 0 5px 0;margin:0;">
            <div class="panel panel-info" style="height: 200px; margin-top: 7px;"> 
                <div class="panel-heading"><b>Storage Table 생성</b></div>
                <div class="panel-body" style="padding:20px 10px; height:150px; overflow-y:auto;">
                    <input type="hidden" name="accountId"/>
                    <div class="w2ui-field">
                        <label style="width:36%;text-align: left; padding-left: 20px;">Storage Table Name</label>
                        <div>
                            <input name="tableName" type="text"   maxlength="100" style="width: 300px; margin-top: 1px;" readonly='readonly' value="stemcells"/>
                        </div>
                    </div>
                    
                </div>
            </div>
        </form> 
    </div>
    
    <div id="addTablePopupBtnDiv" hidden="true">
         <button class="btn" id="registBtn" onclick="$('#addTableForm').submit();">확인</button>
         <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
    </div>
    
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
    $("#azureStorageForm").validate({
        ignore : "",
        onfocusout: true,
        rules: {
            storageAccountName : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='storageAccountName']").val() );
                }
            }, 
            resourceGroupName: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body select[name='resourceGroupName']").val() );
                }
            } 
        }, messages: {
            storageAccountName: { 
                 required:  "Storage Account Name" + text_required_msg
            }, 
            resourceGroupName: { 
                required:  "Resource Group Name "+text_required_msg
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
        	saveAzureStorageInfo();
        }
    });
    
    $("#addBlobForm").validate({
        ignore : "",
        onfocusout: true,
        rules: {
            blobName: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body #addBlobForm input[name='blobName']").val() );
                }
            }, 
        }, messages: {
            blobName: { 
                required:  "Blob Name "+text_required_msg
                
            },
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
            addNewBlob();
        }
    });
   
    $(document).on("change","#switchType input[name='chk_type']:radio",function() {
        switch($(this).val()) {
            case 'blob' :
                $("#addBlobForm .stemcellBlobInfo").html("<input name='blobName' value='stemcell' type='text' readonly='readonly'  maxlength='100' style='width: 300px; margin-top: 1px;'/>");
                $("#addBlobForm .blobInfo").html('');
                break;
            case 'private' :
                $("#addBlobForm .blobInfo").html("<input name='blobName' value='bosh' type='text' readonly='readonly'  maxlength='100' style='width: 300px; margin-top: 1px;'/>");
                $("#addBlobForm .stemcellBlobInfo").html('');
                break;
    }            
    });
    
    $("#addTableForm").validate({
        ignore : "",
        onfocusout: true,
        rules: {
            tableName: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body #addTableForm input[name='tableName']").val() );
                }
            }, 
        }, messages: {
     	   tableName: { 
                required:  "Table Name "+text_required_msg
                
            },
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
            addNewTable();
        }
    });

});

</script>