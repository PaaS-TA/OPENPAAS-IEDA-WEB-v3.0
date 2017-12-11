<%
/* =================================================================
 * 작성일 : 2017.08.29
 * 작성자 : 배병욱
 * 상세설명 : OPENSTACK Router 관리화면
 * =================================================================
 */
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix = "spring" uri = "http://www.springframework.org/tags" %>
 
<script>
var accountId ="";
var bDefaultAccount = "";
var search_lock_msg = '<spring:message code="common.search.data.lock"/>';//조회 중 입니다.
var save_lock_msg = '<spring:message code="common.save.data.lock"/>';//등록 중 입니다.
var delete_lock_msg='<spring:message code="common.delete.data.lock"/>';//삭제 중 입니다.
var text_required_msg='<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
var text_ip_msg = '<spring:message code="common.text.validate.ip.message"/>';//IP을(를) 확인 하세요.
var delete_confirm_msg = '<spring:message code="common.popup.delete.message"/>';//을(를) 삭제하시겠습니까?
$(function(){
     
    bDefaultAccount = setDefaultIaasAccountList("openstack");
     
    $("#openstack_routerGrid").w2grid({
        name: 'openstack_routerGrid',
        method: 'GET',
        msgAJAXerror: 'OPENSTACK 계정을 확인해주세요.',
        header: '<b>Router 목록</b>',
        multiSelect: false,
        show: {
            selectColumn: true,
            footer: true
        },
        style: 'text-align: center',
        columns: [
            { field: 'recid', caption: 'Recid', hidden: true},
            { field: 'accountId', caption: 'accountId', hidden: true},
            { field: 'routerName', caption: 'Router Name', size: '50%', style: 'text-align: center'},
            { field: 'routeId', caption: 'Router Id', size: '50%', style: 'text-align: center'},
            { field: 'status', caption: 'Status', size: '50%', style: 'text-align: center'},
            { field: 'externalNetwork', caption: 'Extrenal Network', size: '50%', style: 'text-align: center'}
        ],
        onSelect: function(event){
            event.onComplete = function() {
                $('#addBtn').attr('disabled', false);
                $('#modifyBtn').attr('disabled', false);
                $('#deleteBtn').attr('disabled', false);
                $('#setgatewayBtn').attr('disabled',false);
                var accountId = w2ui.openstack_routerGrid.get(event.recid).accountId;
                var routeId = w2ui.openstack_routerGrid.get(event.recid).routeId;
            }
        },
        onUnselect: function(event){
            event.onComplete = function() {
                $('#addBtn').attr('disabled', false);
                $('#modifyBtn').attr('disabled', true);
                $('#deleteBtn').attr('disabled', true);
                $('#setgatewayBtn').attr('disabled', true);
            }
        },
        onLoad: function(event){
            if(event.xhr.status == 403){
                location.href = "/abuse";
                event.preventDefault();
            }
        },
        onError: function(event){
             
        }
    });
    /********************************************************
     * 설명 : 라우터 생성 버튼 클릭
     *********************************************************/
     $("#addBtn").click(function(){
         if($("#addBtn").attr('disabled') == "disabled") return;
         w2popup.open({
             title: "<b>Router 생성</b>",
             width: 700,
             height: 225,
             modal: true,
             body: $("#registPopupDiv").html(),
             buttons: $("#registPopupBtnDiv").html(),
             onClose: function(event){
                 accountId = $("select[name='accountId']").val();
                 w2ui['openstack_routerGrid'].clear();
                 doSearch();
             }
         });
     });
     /********************************************************
     * 설명 : 라우터 삭제 버튼 클릭
     *********************************************************/
     $("#deleteBtn").click(function(){
         if($("#deleteBtn").attr('disabled') == "disabled") return;
         var selected = w2ui['openstack_routerGrid'].getSelection();
         if( selected == 0 ){
             w2alert("선택된 정보가 없습니다.", "라우터 삭제");
             return;
         }else{
             var record = w2ui['openstack_routerGrid'].get(selected);
             w2confirm({
                 title   : "<b>Router 삭제</b>",
                 msg     : "Router (" + record.routerName +")" + delete_confirm_msg,
                 yes_text: "확인",
                 no_text : "취소",
                 yes_callBack: function(event){
                     deleteOpenstackRouter(record);
                 },
                 no_callBack: function(){
                     w2ui['openstack_routerGrid'].clear();
                     accountId = record.accountId;
                     doSearch();
                 }
             });
         }
     });
});


/********************************************************
 * 설명 : Openstack Interface 추가 그리드 및 폼 값 초기화
 *********************************************************/
var config = {
         layouti: {
             name: 'layouti',
             padding: 4,
             panels: [
                 { type: 'left', size: '70%'},
                 { type: 'main'}
             ]
         },
         grid: {
             method: 'GET',
             msgAJAXerror: 'OPENSTACK 계정을 확인해주세요.',
             header: '<b>라우터 인터페이스 목록</b>',
             style: 'text-align: center',
             show: {
                 selectColumn: true,
                 footer: true
             },
             name: 'openstack_routerInterfaceGrid',
             columns: [
                 { field: 'recid', caption: 'Recid', hidden: true},
                 { field: 'accountId', caption: 'accountId', hidden: true},
                 { field: 'routeId', caption: 'routerId', hidden: true},
                 { field: 'subnetId', caption: 'InterfaceId', size: '20%', style: 'text-align: center'},
                 { field: 'subnetName', caption: 'InterfaceName', size: '12%', style: 'text-align: center', render : function(record){
                     if(record.subnetName == ""){
                         return "-";
                     }else{
                         return record.subnetName;
                     }
                 }},
                 { field: 'subnetFixedIps', caption: 'FixedIPs', size: '10%', style: 'text-align: center'},
                 { field: 'subnetStatus', caption: 'Status', size: '10%', style: 'text-align: center'},
                 { field: 'subnetType', caption: 'Type', size: '10%', style: 'text-align: center', render: function(record){
                	 if(record.subnetType == true){
                		 return "External Gateway";
                	 }else{
                		 return "Internal Network";
                	 }
                 }},
                 { field: 'subnetAdminStateUp', caption: 'AdminStatus', size: '7%', style: 'text-align: center', render: function(record){
                     if(record.subnetAdminStateUp == true){
                         return record.subnetAdminStateUp = "UP";
                     }else{
                         return record.subnetAdminStateUp = "DOWN";
                     }
                 }}
             ],
             onLoad: function(event){
                 event.onComplete = function(){
                     $('#w2ui-popup #deleteInterfaceBtn').attr('disabled', true);
                 }
             },
             onSelect: function(event) {
                 event.onComplete = function() {
                     $('#w2ui-popup #deleteInterfaceBtn').attr('disabled', false);
                 }
             },
             onUnselect: function(event) {
                 event.onComplete = function(){
                     $('#w2ui-popup #deleteInterfaceBtn').attr('disabled', true);
                 }
             },
             onError: function(event){
                 // comple
             }
         }
 };
 
/********************************************************
* 설명 : Router 생성 확인 버튼 클릭
*********************************************************/
function saveOpenstackRouter(){
     var routerInfo = {
             accountId: $("select[name='accountId']").val(),
             routerName: $(".w2ui-msg-body input[name='routerName']").val()
     }
     $.ajax({
         type: "POST",
         url: "/openstackMgnt/router/create",
         contentType: "application/json",
         async: true,
         data: JSON.stringify(routerInfo),
         success: function(status) {
             w2popup.unlock();
             w2popup.close();
             accountId = routerInfo.accountId;
             doSearch();
         },
         error: function(request, status, error) {
             w2popup.unlock();
             accountId = routerInfo.accountId;
             doSearch();
             var errorResult = JSON.parse(request.responseText);
             w2alert(errorResult.message);
         }
     });
}
$(function () {
    // initialization in memory
    $().w2layout(config.layouti);
    $().w2grid(config.grid);
});

/********************************************************
 * 설명 : 라우터 목록 조회 Function 
 * 기능 : doSearch
 *********************************************************/
function doSearch() {
     w2ui['openstack_routerGrid'].load('/openstackMgnt/router/list/'+accountId+'');
     doButtonStyle();
     accountId = "";
}
/********************************************************
 * 기능 : initsetting
 * 설명 : 기본 설정값 초기화
 *********************************************************/
function initsetting(){
     bDefaultAccount="";
     w2ui['openstack_routerGrid'].clear();
}
 
/********************************************************
 * 설명 : 초기 버튼 스타일
 * 기능 : doButtonStyle
 *********************************************************/
function doButtonStyle() {
    $('#addBtn').attr('disabled',false);
    $('#modifyBtn').attr('disabled', true);
    $('#deleteBtn').attr('disabled', true);
    $('#setgatewayBtn').attr('disabled', true);
}
 
/****************************************************
 * 기능 : resize
 * 설명 : 화면 리사이즈시 호출
*****************************************************/
$( window ).resize(function() {
  setLayoutContainerHeight();
});
 
/****************************************************
 * 기능 : clearMainPage
 * 설명 : 다른페이지 이동시 호출
*****************************************************/
function clearMainPage(){
    $().w2destroy('openstack_routerGrid');
}
 
 /********************************************************
 * 설명 : Openstack 라우터 정보 삭제
 * 기능 : deleteOpenstackRouter
 *********************************************************/
 function deleteOpenstackRouter(record){
     var routerInfo = {
             accountId: record.accountId,
             routeId: record.routeId
     }
     $.ajax({
         type: "DELETE",
         url: "/openstackMgnt/router/delete",
         contentType: "application/json",
         async: true,
         data: JSON.stringify(routerInfo),
         success: function(status) {
             w2popup.unlock();
             w2popup.close();
             accountId = routerInfo.accountId;
             w2ui['openstack_routerGrid'].clear();
             doSearch();
         },
         error: function(request, status, error) {
             w2ui['openstack_routerGrid'].clear();
             var errorResult = JSON.parse(request.responseText);
             w2alert(errorResult.message);
         }
     });
 }
  
  /********************************************************
  * 설명 : Openstack 라우터 인터페이스 팝업
  * 기능 : openstackRouterInterfaceInfo()
  *********************************************************/
  function openstackRouterInterfaceInfo(){
      if($("#modifyBtn").attr('disabled') == "disabled") return;
      var selected = w2ui['openstack_routerGrid'].getSelection();
      var record = w2ui['openstack_routerGrid'].get(selected);
      w2popup.open({
          title: '<b>OPENSTACK 라우터 인터페이스 설정</b>',
          width: 1435,
          height: 495,
          showMax: true,
          body: "<div id='subInterfaceMain' style='position: absolute; width:99%; height:95%; margin:5px 0;'></div>",
         onOpen: function (event) {
             event.onComplete = function () {
                 $('.w2ui-popup #subInterfaceMain').w2render('layouti');
                 w2ui.layouti.content('left', w2ui.openstack_routerInterfaceGrid);
                 w2ui['openstack_routerInterfaceGrid'].load('/openstackMgnt/router/interface/list/'+record.accountId+'/'+record.routeId);
                 w2ui['layouti'].content('main', $('#registSubpopupDiv').html());
                 openstackRouterInterfaceSubnetInfo(record);
                 openstackRouterInsertInfo();
             };
         },onClose: function (event) {
              event.onComplete = function() {
                  accountId = $("select[name=accountId]").val();
                  w2ui['openstack_routerGrid'].clear();
                  w2ui['openstack_routerInterfaceGrid'].clear();
                  doSearch();
              };
          }
      });
  }
  
  /********************************************************
   * 설명 : Openstack 라우터 인터페이스 서브넷 조회 
   * 기능 : openstackRouterInterfaceSubnetInfo
  *********************************************************/
  function openstackRouterInterfaceSubnetInfo(record){
       w2popup.lock("서브넷 "+ search_lock_msg, true);
       $.ajax({
           type : "GET",
           url : "/openstackMgnt/router/interface/list/"+record.accountId,
           contentType : "application/json",
           dataType : "json",
           success : function(data, status) {
               var options = "";
               if(checkEmpty(data)){
                   options += "<option value=''>서브넷을 선택하세요.</option>"
               }else{
                   for(var i=0; i<data.length; i++){
                       options += "<option value='" + data[i].subnetId + "' >";
                       options += data[i].subnetName+"("+data[i].cidrIpv4+")";
                       options += "</option>";
                   }
               }
               $(".w2ui-msg-body select[name='subnetInterfaceId']").html(options);
               w2popup.unlock();
           },error : function(request, status, error) {
               w2popup.unlock();
               var errorResult = JSON.parse(request.responseText);
               w2alert(errorResult.message);
           }
       });
   }
  
   /********************************************************
    * 설명 : Openstack 라우터 정보 설정
    * 기능 : openstackRouterInsertInfo()
   *********************************************************/
   function openstackRouterInsertInfo(){
        var selected = w2ui['openstack_routerGrid'].getSelection();
        var record = w2ui['openstack_routerGrid'].get(selected);
        $("input[name='insertRouterName']").attr("value", record.routerName);
        $("input[name='insertRouterId']").attr("value", record.routeId);
    }
   
   /********************************************************
   * 설명 : Openstack 라우터 인터페이스 저장(연결)
   * 기능 : openstackAttachInterface();
   *********************************************************/
    function openstackAttachInterface(){
       var routeInterfaceInfo = {
               accountId: $("select[name='accountId']").val(),
               subnetId: $(".w2ui-msg-body select[name='subnetInterfaceId']").val(),
               subnetFixedIps: $(".w2ui-msg-body input[name='subnetInterfaceIpAddress']").val(),
               routerName: $(".w2ui-msg-body input[name='insertRouterName']").val(),
               routeId: $(".w2ui-msg-body input[name='insertRouterId']").val()
       }
       
       $.ajax({
           type: "POST",
           url: "/openstackMgnt/router/interface/attach",
           contentType: "application/json",
           async: true,
           data: JSON.stringify(routeInterfaceInfo),
           success: function(status) {
               w2popup.unlock();
               w2ui['openstack_routerInterfaceGrid'].clear();
               var selected = w2ui['openstack_routerGrid'].getSelection();
               var record = w2ui['openstack_routerGrid'].get(selected);
               $('.w2ui-popup #subInterfaceMain').w2render('layouti');
               w2ui.layouti.content('left', w2ui.openstack_routerInterfaceGrid);
               w2ui['openstack_routerInterfaceGrid'].load('/openstackMgnt/router/interface/list/'+record.accountId+'/'+record.routeId);
               w2ui['layouti'].content('main', $('#registSubpopupDiv').html());
           },
           error: function(request, status, error) {
               w2popup.unlock();
               var errorResult = JSON.parse(request.responseText);
               w2alert(errorResult.message);
           }
       });
   }
   /********************************************************
    * 설명 : Openstack 라우터 인터페이스 삭제(해제) 버튼
    * 기능 : deleteOpenstackIntefaceBtn()
   *********************************************************/
    function deleteOpenstackInterfaceBtn(){
        if($(".w2ui-popup #deleteInterfaceBtn").attr('disabled') == "disabled") return;
        
        var selected = w2ui['openstack_routerInterfaceGrid'].getSelection();
        if( selected == 0 ){
            w2alert("선택된 정보가 없습니다.", "인터페이스 삭제");
            return;
        }else{
            var record = w2ui['openstack_routerInterfaceGrid'].get(selected);
            w2confirm({
                title   : "<b>Openstack 라우터 인터페이스 연결 해제</b>",
                msg     : "인터페이스 (" + record.subnetId + ") </br> 와의 연결을 해제하시겠습니까?",
                yes_text: "확인",
                no_text : "취소",
                yes_callBack: function(event){
                    w2popup.lock(delete_lock_msg, true);
                    deleteOpenstackInterface(record);
                }, no_callBack: function(event){
                    w2ui['openstack_routerInterfaceGrid'].clear();
                     accountId = record.accountId;
                     routeId = record.routeId;
                     $('.w2ui-popup #subInterfaceMain').w2render('layouti');
                    w2ui.layouti.content('left', w2ui.openstack_routerInterfaceGrid);
                    w2ui['openstack_routerInterfaceGrid'].load('/openstackMgnt/router/interface/list/'+record.accountId+'/'+record.routeId);
                    w2ui['layouti'].content('main', $('#registSubpopupDiv').html());
                }
            });
        }
    }
    /********************************************************
     * 설명 : Openstack 라우터 인터페이스 삭제(해제)
     * 기능 : deleteOpenstackInterface(record)
     *********************************************************/
    function deleteOpenstackInterface(record){
        var routeInterfaceInfo = {
                accountId: $("select[name='accountId']").val(),
                subnetId: record.subnetId,
                routeId: record.routeId
        }
        $.ajax({
            type: "DELETE",
            url: "/openstackMgnt/router/interface/detach",
            contentType: "application/json",
            async: true,
            data: JSON.stringify(routeInterfaceInfo),
            success: function(status) {
                w2popup.unlock();
                var selected = w2ui['openstack_routerGrid'].getSelection();
                var record = w2ui['openstack_routerGrid'].get(selected);
                $('.w2ui-popup #subInterfaceMain').w2render('layouti');
                w2ui.layouti.content('left', w2ui.openstack_routerInterfaceGrid);
                w2ui['openstack_routerInterfaceGrid'].load('/openstackMgnt/router/interface/list/'+record.accountId+'/'+record.routeId);
                w2ui['layouti'].content('main', $('#registSubpopupDiv').html());
            },
            error: function(request, status, error) {
                w2popup.unlock();
                var errorResult = JSON.parse(request.responseText);
                w2alert(errorResult.message);
            }
        });
    }
     
     /********************************************************
      * 설명 : Openstack 라우터 게이트웨이 팝업 오픈
      * 기능 : openstackRouterGatewayPopupOpen()
      *********************************************************/
     function openstackRouterGatewayPopupOpen(){
         if($("#setgatewayBtn").attr('disabled') == "disabled") return;
         w2popup.open({
             title: '<b>OPENSTACK 게이트웨이 설정</b>',
             width: 700,
             height: 290,
             showMax: true,
             body: $('#registGatewaySettingPopupDiv').html(),
             buttons: $('#gatewayPopupBtn').html(),
             onOpen: function(event){
                 event.onComplete = function() {
                     var selected = w2ui['openstack_routerGrid'].getSelection();
                     if( selected == 0 ){
                         w2alert("선택된 정보가 없습니다.", "게이트웨이 설정");
                         return;
                     }else{
                         var record = w2ui['openstack_routerGrid'].get(selected);
                         if( record.externalNetwork != "-" ){
                             $(".w2ui-msg-buttons #gatewayAttachBtn").attr("disabled", true);
                             $(".w2ui-msg-buttons #gatewayDettachBtn").attr("disabled", false);
                             $(".w2ui-msg-body select[name='selectExNetName']").attr("disabled", true);
                         }else{
                             $(".w2ui-msg-buttons #gatewayAttachBtn").attr("disabled", false);
                             $(".w2ui-msg-buttons #gatewayDettachBtn").attr("disabled", true);
                         }
                     }
                     openstackRouterGatewayExnetworkSelect();
                     openstackRouterInsertInfo();
                 }
             },
             onClose: function (event) {
                 event.onComplete = function() {
                     accountId = $("select[name=accountId]").val();
                     w2ui['openstack_routerGrid'].clear();
                     w2ui['openstack_routerInterfaceGrid'].clear();
                     doSearch();
                 };
             }
         });
      }
     /********************************************************
      * 설명 : Openstack 라우터 게이트웨이 External Network 조회
      * 기능 : openstackRouterGatewayExnetworkSelect()
      *********************************************************/
      function openstackRouterGatewayExnetworkSelect(){
          w2popup.lock(search_lock_msg, true);
          var accountId = $("select[name=accountId]").val();
          $.ajax({
              type: 'GET',
              url: '/openstackMgnt/router/gateway/exnetlist/'+accountId,
              contentType: 'application/json',
              dataType: 'json',
              success: function(data, status){
                  var options = "";
                  if( checkEmpty(data) ){
                      optons = "<option value=''>External 네트워크를 선택하세요.</option>";
                  }else{
	                  for(var i=0;i<data.length; i++){
	                      options += "<option value='"+data[i].id+"'>";
	                      options += data[i].name;
	                      options += "</option>";
	                  }
                  }
                  $(".w2ui-msg-body select[name='selectExNetName']").html(options);
                  w2popup.unlock();
              },
              error : function(request, status, error) {
                  w2popup.unlock();
                  var errorResult = JSON.parse(request.responseText);
                  w2alert(errorResult.message);
              }
          });
      }
      /********************************************************
       * 설명 : Openstack 라우터 게이트웨이 연결
       * 기능 : openstackRouterGatewayAttatch()
       *********************************************************/
      function openstackRouterGatewayAttatch(){
           if($(".w2ui-msg-buttons #gatewayAttachBtn").attr('disabled') == "disabled") return;
           w2popup.lock(save_lock_msg, true);
           var routerGatewayInfo = {
               accountId: $("select[name='accountId']").val(),
               networkId: $(".w2ui-msg-body select[name='selectExNetName']").val(),
               routerName: $(".w2ui-msg-body input[name='insertRouterName']").val(),
               routeId: $(".w2ui-msg-body input[name='insertRouterId']").val()
           }
           $.ajax({
               type: 'POST',
               url: '/openstackMgnt/router/gateway/attach',
               contentType: 'application/json',
               async: true,
               data: JSON.stringify(routerGatewayInfo),
               success: function(status){
                   w2popup.unlock();
                   w2popup.close();
                   accountId = $("select[name=accountId]").val();
                   w2ui['openstack_routerGrid'].clear();
                   doSearch();
               },error: function(request, status, error) {
                   w2popup.unlock();
                   accountId = $("select[name=accountId]").val();
                   var errorResult = JSON.parse(request.responseText);
                   w2alert(errorResult.message);
               }
           });
       }
      /********************************************************
       * 설명 : Openstack 라우터 게이트웨이 연결해제
       * 기능 : openstackRouterGatewayDetach()
       *********************************************************/
       function openstackRouterGatewayDetach(){
           if($(".w2ui-msg-buttons #gatewayDettachBtn").attr('disabled') == "disabled") return;
           w2popup.lock(save_lock_msg, true);
           var routerGatewayInfo = {
               accountId: $("select[name='accountId']").val(),
               networkId: $("select[name='selectExNetName']").val(),
               routerName: $("input[name='insertRouterName']").val(),
               routeId: $("input[name='insertRouterId']").val()
           }
           $.ajax({
               type: 'DELETE',
               url: '/openstackMgnt/router/gateway/detach',
               contentType: 'application/json',
               async: true,
               data: JSON.stringify(routerGatewayInfo),
               success: function(status){
                   w2popup.unlock();
                   w2popup.close();
                   accountId = $("select[name=accountId]").val();
                   w2ui['openstack_routerGrid'].clear();
                   doSearch();
               },
               error: function(request, status, error) {
                   w2popup.unlock();
                   accountId = $("select[name=accountId]").val();
                   w2ui['openstack_routerGrid'].clear();
                   doSearch();
                   var errorResult = JSON.parse(request.responseText);
                   w2alert(errorResult.message);
               }
           });
       }
      
 /****************************************************
  * 기능 : clearMainPage
  * 설명 : 다른페이지 이동시 호출
 *****************************************************/
 function clearMainPage() {
     $().w2destroy('openstack_routerGrid');
     $().w2destroy('openstack_routerInterfaceGrid');
     $().w2destroy('layouti');
 }

/****************************************************
 * 기능 : resize
 * 설명 : 화면 리사이즈시 호출
*****************************************************/
$( window ).resize(function() {
  setLayoutContainerHeight();
});
</script>
<div id="main">
    <div class="page_site pdt20">인프라 관리 > OPENSTACK 관리 > <strong>OPENSTACK Route 관리 </strong></div>
    <div id="openstackMgnt" class="pdt20">
        <ul>
            <li>
                <label style="font-size: 14px">OpenStack 관리화면</label> &nbsp;&nbsp;&nbsp;
                <div class="dropdown" style="display: inline-block;">
                    <a href="#" class="dropdown-toggle iaas-dropdown" data-toggle="dropdown" aria-expanded="false">
                        &nbsp;&nbsp;Router 관리<b class="caret"></b>
                    </a>
                    <ul class="dropdown-menu alert-dropdown">
                        <sec:authorize access="hasAuthority('OPENSTACK_NETWORK_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/openstackMgnt/network"/>', 'Openstack Network');">Network 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('OPENSTACK_ROUTER_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/openstackMgnt/router"/>', 'OPENSTACK ROUTER');">Router 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('OPENSTACK_KEYPAIRS_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/openstackMgnt/keypairs"/>', 'OPENSTACK KEYPAIR');">KeyPair 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('OPENSTACK_INTERFACE_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/openstackMgnt/interface"/>', 'OPENSTACK INTERFACE');">인터페이스 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('OPENSTACK_FLOATING_IP_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/openstackMgnt/floatingIp"/>', 'OPENSTACK FLOATING IP');">Floating IP 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('OPENSTACK_SECURITY_GROUP_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/openstackMgnt/securityGroup"/>', 'OPENSTACK Security Group');">Security Group 관리</a></li>
                        </sec:authorize>
                    </ul>
                </div>
            </li>
            <li>
                <label style="font-size: 14px;">OPENSTACK 계정 명</label>
                &nbsp;&nbsp;&nbsp;
                <select name="accountId" id="setAccountList" class="select" style="width: 300px; font-size: 15px; height: 32px;" onchange="setAccountInfo(this.value, 'openstack')"></select>
                <span id="doSearch" onclick="setDefaultIaasAccount('noPopup','openstack');" class="btn btn-info" style="width:80px" >선택</span>
            </li>
        </ul>
    </div>
    <div class="pdt20">
        <div class="title fl">Openstack Router 목록</div>
        <div class="fr"> 
            <sec:authorize access="hasAuthority('OPENSTACK_ROUTER_CREATE')">
            <span id="addBtn" class="btn btn-primary" style="width:120px">생성</span>
            </sec:authorize>
            <sec:authorize access="hasAuthority('OPENSTACK_ROUTER_INTERFACESET')">
            <span id="modifyBtn" onclick="openstackRouterInterfaceInfo()" class="btn btn-info" style="width:120px">인터페이스</span>
            </sec:authorize>
            <sec:authorize access="hasAuthority('OPENSTACK_ROUTER_GATEWAYSET')">
            <span id="setgatewayBtn" onclick="openstackRouterGatewayPopupOpen()" class="btn btn-warning" style="width: 120px;">게이트웨이 </span>
            </sec:authorize>
            <sec:authorize access="hasAuthority('OPENSTACK_ROUTER_DELETE')">
            <span id="deleteBtn" class="btn btn-danger" style="width:120px">삭제</span>
            </sec:authorize>
        </div>
    </div>
    <div id="openstack_routerGrid" style="width: 100%; height: 400px;"></div>
     
     
    <!-- 라우터 생성 팝업 -->
<div id="registPopupDiv" hidden="true">
    <form id="openstackRouterForm" action="POST">
        <div class="panel panel-info" style="height: 125px; margin-top: 7px">
            <div class="panel-heading"><b>Router 정보</b></div>
            <div class="panel-body" style="padding: 10px 10px; height: 100px; overflow-y:auto;">
                <input type="hidden" name="accountId"/>
                <div class="w2ui-field">
                    <label style="width:35%; margin-right:20px; ">Router Name</label>
                    <input name="routerName" type="text" maxlength="100" style="width: 320px; margin-bottom: 3px;" placeholder="라우터 명을 입력하세요."/>
                </div>
            </div>
        </div>
    </form>
</div>
<div id="registPopupBtnDiv" hidden="true">
    <button class="btn" id="registBtn" onclick="$('#openstackRouterForm').submit();">확인</button>
    <button class="btn" id="popClose" onclick="w2popup.close();">닫기</button>
</div>
    <div id="registSubpopupDiv" hidden="true">
        <form id="settingInterfaceForm" action="POST">
            <div class="panel panel-info">
                <div class="panel-heading"><b>Openstack 인터페이스 설정 정보</b></div>
                <div class="panel-body" style="margin-bottom:55px;height:290px;">
                    <input type="hidden" name="accountId"/>
                    <div class="w2ui-field">
                       <label style="width: 100%; text-align: left; padding-left: 10px;">Subnet:*</label>
                       <div class="w2ui-field">
                          <select name="subnetInterfaceId" id="subnetInterfaceName" style="width: 300px">
                            <option value="">서브넷을 선택하세요.</option>
                          </select>
                       </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="width: 100%; text-align: left; padding-left: 10px;">IP Address (optional):</label>
                        <div>
                            <input name="subnetInterfaceIpAddress" type="text" maxlength="100" style="width: 300px" placeholder="IP주소를 입력하세요"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="width: 100%; text-align: left; padding-left: 10px;">Router Name:*</label>
                        <div>
                            <input name="insertRouterName" type="text" maxlength="100" style="width: 300px" readonly="readonly"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="width: 100%; text-align: left; padding-left: 10px;">Router ID:*</label>
                        <div>
                            <input name="insertRouterId" type="text" maxlength="100" style="width: 300px" readonly="readonly"/>
                        </div>
                    </div>
                </div>
            </div>
        </form>
        <div style="text-align: center; padding-top:10px;">
            <span id="addInterfaceBtn" onclick="$('#settingInterfaceForm').submit();" class="btn btn-primary" style="width:100px" >연결</span>
            <span id="deleteInterfaceBtn" onclick="deleteOpenstackInterfaceBtn();" class="btn btn-danger" style="width:100px" >해제</span>
            <span id="cancel" onclick="w2popup.close();" class="btn btn-info" style="width:100px" >닫기</span>
        </div>
    </div>
    <!-- 게이트웨이 세팅 팝업 -->
    <div id="registGatewaySettingPopupDiv" hidden="true">
        <form id="openstackRouterGatewaySettingForm" action="POST">
            <div class="panel panel-info" style="height: 190px; margin-top: 7px">
                <div class="panel-heading"><b>게이트웨이 설정</b></div>
                <div class="panel-body" style="padding: 10px 10px; height: 150px; overflow-y:auto;">
                    <input type="hidden" name="accountId"/>
                    <div class="w2ui-field">
                        <label style="width: 35%; text-align: left; padding-left: 55px;'">External Network</label>
                        <div class="w2ui-field">
                          <select name="selectExNetName" id="selectExNetworkName" style="width:300px;">
                            <option value="">External 네트워크를 선택하세요.</option>
                          </select>
                       </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="width: 35%; text-align: left; padding-left: 55px;">Router Name:*</label>
                        <div>
                            <input name="insertRouterName" type="text" maxlength="100" style="width: 300px" readonly="readonly"/>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="width: 35%; text-align: left; padding-left: 55px;">Router ID:*</label>
                        <div>
                            <input name="insertRouterId" type="text" maxlength="100" style="width: 300px" readonly="readonly"/>
                        </div>
                    </div>
                </div>
            </div>
        </form>
        <div style="text-align: center;" id="gatewayPopupBtn" class="w2ui-buttons" hidden="true">
            <span id="gatewayAttachBtn" onclick="$('#openstackRouterGatewaySettingForm').submit();" class="btn btn-primary" style="width:100px" >연결</span>
            <span id="gatewayDettachBtn" onclick="openstackRouterGatewayDetach()" class="btn btn-warning" style="width: 100px;">연결해제</span>
            <span id="cancel" onclick="w2popup.close();" class="btn btn-danger" style="width:100px" >닫기</span>
        </div>
    </div>
</div>
<script type="text/javascript" src="<c:url value='/js/rules/openstackMgnt/openstack_router_rule.js'/>"></script>
<script>
$(function(){
    $.validator.addMethod( "ipv4", function(value,element,params) {
        if(params){
            return /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(params);
        }else{
            return true;
        }
    }, text_ip_msg);
});
</script>