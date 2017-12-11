<%
/* =================================================================
 * 작성일 : 2017.07.31
 * 작성자 : 이동현
 * 상세설명 : OPENSTACK NETWORK 관리 화면
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
var save_lock_msg = '<spring:message code="common.save.data.lock"/>';//등록 중 입니다.
var text_injection_msg='<spring:message code="common.text.validate.sqlInjection.message"/>';//입력하신 값은 입력하실 수 없습니다.
var detail_network_lock_msg='<spring:message code="common.search.detaildata.lock"/>';//상세 조회 중 입니다.
var delete_lock_msg='<spring:message code="common.delete.data.lock"/>';//삭제 중 입니다.
var text_required_msg='<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
var text_cidr_msg='<spring:message code="common.text.validate.cidr.message"/>';//CIDR 대역을 확인 하세요
var text_ip_msg = '<spring:message code="common.text.validate.ip.message"/>';//IP을(를) 확인 하세요.
$(function() {
    
    bDefaultAccount = setDefaultIaasAccountList("openstack");
    $('#openstack_networkGrid').w2grid({
        name: 'openstack_networkGrid',
        method: 'GET',
        msgAJAXerror : 'OPENSTACK 계정을 확인해주세요.',
        header: '<b>Network 목록</b>',
        multiSelect: false,
        show: {    
                selectColumn: true,
                footer: true},
        style: 'text-align: center',
        columns    : [
                     {field: 'recid',     caption: 'Recid', hidden: true}
                   , {field: 'accountId',     caption: 'accountId', hidden: true}
                   , {field: 'networkId',     caption: 'networkId', hidden: true}
                   , {field: 'networkName', caption: 'Network Name', size: '50%', style: 'text-align:center'}
                   , {field: 'subnetName', caption: 'Subnet Name', size: '50%', style: 'text-align:center', render : function(record){
                       if(record.subnetName == ""){
                           return "-"
                       }else{
                           return record.subnetName;
                       }
                   }}
                   , {field: 'cidrIpv4', caption: 'Cidr Ipv4', size: '50%', style: 'text-align:center', render : function(record){
                       if(record.cidrIpv4 == ""){
                           return "-"
                       }else{
                           return record.cidrIpv4;
                       }
                   }}
                   , {field: 'status', caption: 'Status', size: '50%', style: 'text-align:center'}
                   , {field: 'adminStateUp', caption: 'Admin State Up', size: '50%', style: 'text-align:center', render : function(record){
                       if(record.adminStateUp == true){
                           return record.adminStateUp = "UP"
                       }else{
                           return record.adminStateUp = "DOWN"
                       }
                     }}
                   , {field: 'shared', caption: 'Shared', size: '50%', style: 'text-align:center', render : function(record){
                       if(record.shared == true){
                           return record.shared = "YES"
                       }else{
                           return record.shared = "NO"
                       }
                     }}
                   ],
        onSelect: function(event) {
            event.onComplete = function() {
                $('#deleteBtn').attr('disabled', false);
                $('#subnetBtn').attr('disabled', false);
                var accountId =  w2ui.openstack_networkGrid.get(event.recid).accountId;
                var networkId = w2ui.openstack_networkGrid.get(event.recid).networkId;
                doSearchNetworkDetailInfo(accountId, networkId);
            }
        },
        onUnselect: function(event) {
            event.onComplete = function() {
                $("#networkDetailTable td").html("");
                $('#deleteBtn').attr('disabled', true);
                $('#subnetBtn').attr('disabled', true);
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
     * 설명 : 네트워크 생성 버튼 클릭
    *********************************************************/
    $("#addBtn").click(function(){
        if($("#addBtn").attr('disabled') == "disabled") return;
       w2popup.open({
           title   : "<b>OPENSTACK 네트워크 생성</b>",
           width   : 700,
           height  : 485,
           modal   : true,
           body    : $("#registPopupDiv").html(),
           buttons : $("#registPopupBtnDiv").html(),
           onClose : function(event){
               accountId = $("select[name='accountId']").val();
               w2ui['openstack_networkGrid'].clear();
               doSearch();
               $("#networkDetailTable td").html("");
           }
       });
    });
    
    /********************************************************
     * 설명 : OPENSTACK Network 삭제 버튼
    *********************************************************/
    $("#deleteBtn").click(function(){
        if($("#deleteBtn").attr('disabled') == "disabled") return;
        var selected = w2ui['openstack_networkGrid'].getSelection();
        if( selected.length == 0 ){
            w2alert("선택된 정보가 없습니다.", "Network 삭제");
            return;
        }else {
            var record = w2ui['openstack_networkGrid'].get(selected);
            w2confirm({
                title    : "<b>OPENSTACK 네트워크 삭제</b>",
                msg      : "네트워크 (" + record.networkId + ") </br> 을 삭제하시겠습니까?",
                yes_text : "확인",
                no_text  : "취소",
                yes_callBack: function(event){
                    deleteOpenstackNetworkInfo(record);
                }, no_callBack    : function(){
                    w2ui['openstack_networkGrid'].clear();
                    accountId = record.accountId;
                    doSearch();
                    $("#networkDetailTable td").html("");
                }
            });
        }
    });
});
 /********************************************************
 * 설명 : Openstack 서브넷 추가 그리드 및 폼 값 초기화
 *********************************************************/
 var config = {
        layout2: {
            name: 'layout2',
            padding: 4,
            panels: [
                { type: 'left', size: '70%', minSize: 300 },
                { type: 'main', minSize: 300 }
            ]
        },
        grid: { 
            method: 'GET',
            msgAJAXerror : 'OPENSTACK 계정을 확인해주세요.',
            header: '<b>OPENSTACK Subnet 목록</b>',
            style: 'text-align: center',
            multiSelect: false,
            show: {    
                    selectColumn: true,
                    footer: true},
            name: 'openstackSubnet_grid',
            columns    : [
                {field: 'recid',     caption: 'Recid', hidden: true}
              , {field: 'accountId',     caption: 'accountId', hidden: true}
              , {field: 'networkId',     caption: 'networkId', hidden: true}
              , {field: 'subnetName', caption: 'subnet Name', size: '120px', style: 'text-align:center' }
              , {field: 'subnetId', caption: 'subnet Id', size: '250px', style: 'text-align:center'}
              , {field: 'networkId', caption: 'network Id', size: '250px', style: 'text-align:center'}
              , {field: 'ipVersion', caption: 'Ip Version', size: '120px', style: 'text-align:center'}
              , {field: 'allocationPools', caption: 'Allocation Pools', size: '250px', style: 'text-align:center'}
              , {field: 'dhcpEnabled', caption: 'Dhcp Enabled', size: '80px', style: 'text-align:center'}
              , {field: 'gatewayIp', caption: 'Gateway Ip', size: '200px', style: 'text-align:center'}
              , {field: 'dnsName', caption: 'Dns Server Name', size: '200px', style: 'text-align:center'}
              ], onLoad:function(event){
                  event.onComplete = function() {
                      $('#w2ui-popup #deleteSubnetBtn').attr('disabled', true);
                  }
              }, onSelect: function(event) {
                  event.onComplete = function() {
                      $('#w2ui-popup #deleteSubnetBtn').attr('disabled', false);
                  }
              }, onUnselect: function(event) {
                  event.onComplete = function() {
                      $('#w2ui-popup #deleteSubnetBtn').attr('disabled', true);
                  }
              }, onError:function(evnet){
              }
        }
 };
 /********************************************************
  * 설명 : 서브넷 추가 팝업 화면 
  * 기능 : subnetAdd
  *********************************************************/
 function openstackSubnetInfo(){
     if($("#subnetBtn").attr('disabled') == "disabled") return;
     var selected = w2ui['openstack_networkGrid'].getSelection();
     var record = w2ui['openstack_networkGrid'].get(selected);
     w2popup.open({
         title   : '<b>OPENSTACK 서브넷 설정</b>',
         width   : 1400,
         height  : 550,
         showMax : true,
         body    : '<div id="subnetMain" style="position: absolute; left: 5px; top: 5px; right: 5px; bottom: 5px;"></div>',
         onOpen  : function (event) {
             event.onComplete = function () {
                 $('#w2ui-popup #subnetMain').w2render('layout2');
                 w2ui.layout2.content('left', w2ui.openstackSubnet_grid);
                 w2ui['openstackSubnet_grid'].load('/openstackMgnt/subnet/list/'+record.accountId+'/'+record.networkId);
                 w2ui['layout2'].content('main', $('#regSubPopupDiv').html());
             };
         }, onClose : function(event){
             accountId = $("select[name='accountId']").val();
             w2ui['openstack_networkGrid'].clear();
             w2ui['openstackSubnet_grid'].clear();
             doSearch();
             $("#networkDetailTable td").html("");
        }
     });
 }
    
 /********************************************************
  * 설명 : 서브넷 생성 팝업 화면 grid 초기화
  *********************************************************/
 $(function () {
     // initialization in memory
     $().w2layout(config.layout2);
     $().w2grid(config.grid);
 });

 /********************************************************
  * 설명 : Openstack 네트워크 상세 조회 
  * 기능 : doSearchNetworkDetailInfo
  *********************************************************/
 function doSearchNetworkDetailInfo(accountId, networkId){
     w2utils.lock($("#layout_layout_panel_main"), detail_network_lock_msg, true);
     $.ajax({
         type : "GET",
         url : "/openstackMgnt/network/save/detail/"+accountId+"/"+networkId+"",
         contentType : "application/json",
         success : function(data, status) {
             w2utils.unlock($("#layout_layout_panel_main"));
             if(data != null){
                 $(".networkId").html(data.networkId);
                 $(".networkName").html(data.networkName);
                 $(".networkType").html(data.networkType);
                 $(".providerNetwork").html(data.providerNetwork);
                 if(data.routerExternal == true){
                     $(".routerExternal").html("YES");
                 }else{
                     $(".routerExternal").html("NO");
                 }
                 
                 $(".segId").html(data.segId);
                 $(".shared").html(data.shared);
                 $(".status").html(data.status);
                 if(data.subnetName != ""){
                     $(".subnetName").html(data.subnetName);
                 }else{
                     $(".subnetName").html("-");
                 }
                 
                 $(".tenantId").html(data.tenantId);
                 if(data.shared == true){
                     $(".shared").html("YES");
                 }else{
                     $(".shared").html("NO");
                 }
                 $(".status").html(data.status);
                 if(data.cidrIpv4 != ""){
                     $(".cidrIpv4").html(data.cidrIpv4);
                 }else{
                     $(".cidrIpv4").html("-");
                 }

                 if(data.adminStateUp == true){
                     $(".adminStateUp").html("UP");
                 }else{
                     $(".adminStateUp").html("DOWN");
                 }
                 if(data.providerNetwork != null){
                     $(".providerNetwork").html(data.providerNetwork);
                 }else{
                     $(".providerNetwork").html("-")
                 }
             }
         },
         error : function(request, status, error) {
             w2utils.unlock($("#layout_layout_panel_main"));
             var errorResult = JSON.parse(request.responseText);
             w2alert(errorResult.message, "OPENSTACK 네트워크 상세 조회");
         }
     });
 }
    
 /********************************************************
  * 설명 : Openstack 네트워크 정보 저장
  * 기능 : saveOpenstackNetworkInfo
  *********************************************************/
 function saveOpenstackNetworkInfo(){
     var networkInfo = {
         accountId      : $("select[name='accountId']").val(),
         subnetName     : $(".w2ui-msg-body input[name='subnetName']").val(),
         networkName    : $(".w2ui-msg-body input[name='networkName']").val(),
         networkAddress : $(".w2ui-msg-body input[name='networkAddress']").val(),
         gatewayIp      : $(".w2ui-msg-body input[name='gatewayIp']").val(),
         dnsNameServers : $(".w2ui-msg-body textarea[name='dnsNameServers']").val(),
         adminState     : $(".w2ui-msg-body :checkbox[name='adminState']").is(':checked'),
         enableDHCP     : $(".w2ui-msg-body :checkbox[name='enableDHCP']").is(':checked'),
         ipVersion      : $(".w2ui-msg-body select[name='ipVersion']").val()
     }
     
     $.ajax({
         type : "POST",
         url : "/openstackMgnt/network/save",
         contentType : "application/json",
         async : true,
         data : JSON.stringify(networkInfo),
         success : function(status) {
             w2popup.unlock();
             w2popup.close();
             accountId = networkInfo.accountId;
             doSearch();
         }, error : function(request, status, error) {
             w2popup.unlock();
             w2popup.close();
             w2utils.unlock($("#layout_layout_panel_main"));
             var errorResult = JSON.parse(request.responseText);
             w2alert(errorResult.message);
         }
     });
 }
 /********************************************************
  * 설명 : Openstack 네트워크 정보 삭제
  * 기능 : deleteOpenstackNetworkInfo
 *********************************************************/
 function deleteOpenstackNetworkInfo(record){
      w2popup.lock(delete_lock_msg, true);
      var networkInfo = {
              accountId : record.accountId,
              networkId : record.networkId
      }
      $.ajax({
          type : "DELETE",
          url : "/openstackMgnt/network/delete",
          contentType : "application/json",
          async : true,
          data : JSON.stringify(networkInfo),
          success : function(status) {
              w2popup.unlock();
              w2popup.close();
              accountId = networkInfo.accountId;
              w2ui['openstack_networkGrid'].clear();
              $("#networkDetailTable td").html("");
              doSearch();
          }, error : function(request, status, error) {
              w2popup.unlock();
              $("#networkDetailTable td").html("");
              w2ui['openstack_networkGrid'].clear();
              accountId = networkInfo.accountId;
              doSearch();
              var errorResult = JSON.parse(request.responseText);
              w2alert(errorResult.message, "네트워크 삭제");
          }
      });
 }

 /********************************************************
  * 설명 : Openstack 서브넷 정보 저장
  * 기능 : saveOpenstackSubnetInfo
 *********************************************************/
 function saveOpenstackSubnetInfo(){
     var selected = w2ui['openstack_networkGrid'].getSelection();
     var record = w2ui['openstack_networkGrid'].get(selected);
     var subnetInfo = {
             accountId      : $("select[name='accountId']").val(),
             networkId      : record.networkId,
             subnetName     : $(".w2ui-msg-body input[name='subnetName']").val(),
             networkAddress : $(".w2ui-msg-body input[name='networkAddress']").val(),
             gatewayIp      : $(".w2ui-msg-body input[name='gatewayIp']").val(),
             adminState     : $(".w2ui-msg-body :checkbox[name='adminState']").is(':checked'),
             dnsNameServers : $(".w2ui-msg-body textarea[name='dnsNameServers']").val(),
             enableDHCP     : $(".w2ui-msg-body :checkbox[name='enableDHCP']").is(':checked'),
             ipVersion      : $(".w2ui-msg-body select[name='ipVersion']").val()
     }
     $.ajax({
         type : "POST",
         url : "/openstackMgnt/subnet/save",
         contentType : "application/json",
         async : true,
         data : JSON.stringify(subnetInfo),
         success : function(status) {
             w2popup.unlock();
             accountId = subnetInfo.accountId;
             subNetInitSetting(record);
         }, error : function(request, status, error) {
             w2popup.unlock();
             var errorResult = JSON.parse(request.responseText);
             w2alert(errorResult.message);
         }
     });
 }

 /********************************************************
  * 설명 : Openstack 서브넷 정보 삭제
  * 기능 : deleteOpenstackSubnetInfo
 *********************************************************/
 function deleteOpenstackSubnetInfo(){
     if($("#w2ui-popup #deleteSubnetBtn").attr('disabled') == "disabled") return;
     var selected = w2ui['openstackSubnet_grid'].getSelection();
     var record = w2ui['openstackSubnet_grid'].get(selected);
     
     var subnetInfo = {
             accountId : record.accountId,
             networkId : record.networkId,
             subnetId : record.subnetId
     }
     $.ajax({
         type : "DELETE",
         url : "/openstackMgnt/subnet/delete",
         contentType : "application/json",
         async : true,
         data : JSON.stringify(subnetInfo),
         success : function(status) {
             w2popup.unlock();
             w2utils.unlock($("#layout_layout_panel_main"));
             accountId = subnetInfo.accountId;
             subNetInitSetting(record);
         }, error : function(request, status, error) {
             w2popup.unlock();
             w2utils.unlock($("#layout_layout_panel_main"));
             subNetInitSetting(record);
             var errorResult = JSON.parse(request.responseText);
             w2alert(errorResult.message);
         }
     });
 }

 /********************************************************
  * 설명 : Openstack 네트워크 목록 조회 
  * 기능 : doSearch
 *********************************************************/
 function doSearch() {
     w2ui['openstack_networkGrid'].load('/openstackMgnt/network/list/'+accountId+'');
     doButtonStyle();
     accountId = "";
 }

 /********************************************************
  * 설명 : Openstack 서브넷 목록 초기화 Function 
  * 기능 : subNetInitSetting
 *********************************************************/
 function subNetInitSetting(record){
     w2ui['openstackSubnet_grid'].clear();
     w2ui['openstackSubnet_grid'].load('/openstackMgnt/subnet/list/'+record.accountId+'/'+record.networkId);
     $(".w2ui-msg-body input[name='subnetName']").val("");
     $(".w2ui-msg-body input[name='networkAddress']").val("");
     $(".w2ui-msg-body input[name='gatewayIp']").val("");
     $(".w2ui-msg-body textarea[name='dnsNameServers']").val("");
 }

 /********************************************************
  * 설명 : 초기 버튼 스타일
  * 기능 : doButtonStyle
 *********************************************************/
 function doButtonStyle() {
     $('#deleteBtn').attr('disabled', true);
     $('#subnetBtn').attr('disabled', true);
     $('#deleteSubnetBtn').attr('disabled', true);
 }

 /****************************************************
  * 기능 : clearMainPage
  * 설명 : 다른페이지 이동시 호출
 *****************************************************/
 function clearMainPage() {
     $().w2destroy('openstack_networkGrid');
     $().w2destroy('openstackSubnet_grid');
     $().w2destroy('layout2');
     w2popup.unlock();
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
.trTitle { background-color: #f3f6fa; width: 180px; }
td { width: 280px; }
</style>
<div id="main">
    <div class="page_site pdt20">인프라 관리 > OPENSTACK 관리 > <strong>OPENSTACK Network 관리 </strong></div>
    <div id="openstackMgnt" class="pdt20">
        <ul>
            <li>
                <label style="font-size: 14px">OPENSTACK 관리 화면</label> &nbsp;&nbsp;&nbsp; 
                <div class="dropdown" style="display:inline-block;">
                    <a href="#" class="dropdown-toggle iaas-dropdown" data-toggle="dropdown" aria-expanded="false">
                        &nbsp;&nbsp;Network 관리<b class="caret"></b>
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
                <label  style="font-size:14px">OPENSTACK 계정 명</label>
                &nbsp;&nbsp;&nbsp;
                <select name="accountId" id="setAccountList" class="select" style="width: 300px; font-size: 15px; height: 32px;" onchange="setAccountInfo(this.value, 'openstack')">
                </select>
                <span id="doSearch" onclick="setDefaultIaasAccount('noPopup','openstack');" class="btn btn-info" style="width:80px" >선택</span>
            </li>
        </ul>
    </div>
    <div class="pdt20">
        <div class="title fl">Openstack Network 목록</div>
        <div class="fr"> 
            <sec:authorize access="hasAuthority('OPENSTACK_NETWORK_SAVE')">
            <span id="addBtn" class="btn btn-primary" style="width:120px">생성</span>
            </sec:authorize>
            <sec:authorize access="hasAuthority('OPENSTACK_NETWORK_DELETE')">
            <span id="deleteBtn" class="btn btn-danger" style="width:120px">삭제</span>
            </sec:authorize>
            <sec:authorize access="hasAuthority('OPENSTACK_SUBNET_SETTING')">
            <span id="subnetBtn" onclick="openstackSubnetInfo();" class="btn btn-info" style="width:120px">서브넷 설정</span>
            </sec:authorize>
        </div>
    </div>
    <div id="openstack_networkGrid" style="width:100%; height:475px"></div>
    
    
    <!-- 네트워크 생성 팝업 -->
<div id="registPopupDiv" hidden="true">
    <form id="openstackNetworkForm" action="POST">
        <div class="panel panel-info" style="margin-top:5px;"> 
            <div class="panel-heading"><b>Openstack Network 정보</b></div>
            <div class="panel-body" style="padding:10px 10px; height:350px; overflow-y:auto;">
                <input type="hidden" name="accountId"/>
                <div class="w2ui-field">
                    <label style="width:36%;text-align: left; padding-left: 20px;">Network Name</label>
                    <div>
                        <input name="networkName" type="text"   maxlength="100" style="width: 320px; margin-top: 1px;" placeholder="네트워크 명을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:36%;text-align: left; padding-left: 20px;">Admin State</label>
                    <div style="width: 50%">
                        <input  name="adminState" type="checkbox"  checked style="margin:0px;" />&nbsp;
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:36%;text-align: left; padding-left: 20px;">Subnet Name</label>
                    <div>
                        <input name="subnetName" type="text"   maxlength="100" style="width: 320px; margin-top: 1px;" placeholder="서브넷 명을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:36%;text-align: left; padding-left: 20px;">Network Address</label>
                    <div>
                        <input name="networkAddress" type="text"   maxlength="100" style="width: 320px; margin-top: 1px;" placeholder="네트워크 주소를 입력하세요."/>
                    </div>
                </div>
                
                <div class="w2ui-field">
                    <label style="width:36%;text-align: left; padding-left: 20px;">IP Version</label>
                    <div>
                        <select style="width: 320px;" name="ipVersion">
                            <option value="IPv4">IPv4</option>
                        </select>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:36%;text-align: left; padding-left: 20px;">Gateway IP</label>
                    <div>
                        <input name="gatewayIp" type="text"   maxlength="100" style="width: 320px; margin-top: 1px;" placeholder="게이트웨이를 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:36%;text-align: left; padding-left: 20px;">Enable DHCP</label>
                    <div style="width:50%;">
                         <input name="enableDHCP" type="checkbox" checked style="margin:0px;" />&nbsp;
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:36%;text-align: left; padding-left: 20px;">DNS Name Servers</label>
                    <div>
                        <textarea rows="5" style="width:320px;height:60px;" name="dnsNameServers" placeholder="DNS 주소가 2개 이상일 경우 enter를 사용해주세요.&#13;&#10;예시)8.8.8.8&#13;&#10;8.8.4.4"></textarea>
                    </div>
                </div>
            </div>
        </div>
    </form> 
</div>
<div id="registPopupBtnDiv" hidden="true">
     <button class="btn" id="registBtn" onclick="$('#openstackNetworkForm').submit();">확인</button>
     <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
</div>
     <div class="pdt20" >
        <div class="title fl">Openstack Network 상세 정보</div>
    </div>
    <div id="openstack_networkDetailGrid" style="width:100%; height:128px; margin-top:50px; border-top: 2px solid #c5c5c5; ">
    <table id= "networkDetailTable" class="table table-condensed table-hover">
           <tr>
               <th class= "trTitle">Network ID</th>
               <td class= "networkId"></td>
               <th class= "trTitle">Network Name</th>
               <td class="networkName"></td>
               <th class= "trTitle">Project ID</th>
               <td class= "tenantId"></td>
           </tr>
           <tr>
               <th class= "trTitle">Status</th>
               <td class= "status"></td>
               <th class= "trTitle">Admin Status</th>
               <td class= "adminStateUp"></td>
               <th class= "trTitle">Shared</th>
               <td class= "shared"></td>
           </tr>
           <tr>
               <th class= "trTitle">External Network</th>
               <td class= "routerExternal"></td>
               <th class= "trTitle">Segmentation ID</th>
               <td class= "segId"></td>
               <th class= "trTitle">Subnet Name</th>
               <td class= "subnetName"></td>
           </tr>
           <tr style = "border-bottom: 1px solid #ddd;">
               <th class= "trTitle">CidrIpv4</th>
               <td class= "cidrIpv4"></td>
               <th class= "trTitle">Network Type</th>
               <td class= "networkType"></td>
               <th class= "trTitle">Provider Network</th>
               <td class= "providerNetwork"></td>
           </tr>
        </table>
    </div>
    <div id="regSubPopupDiv" hidden="true">
        <form id="settingForm" action="POST" >
            <div class="panel panel-info" style="margin-top:5px;" >
                <div class="panel-heading"><b>OPENSTACK 서브넷 생성 정보</b></div>
                <div class="panel-body">
                <input type="hidden" name="accountId"/>
                    <div class="w2ui-field">
                        <label style="width:100%;text-align: left;padding-left: 5px;">Subnet Name</label>
                        <div >
                            <input name="subnetName" type="text" maxlength="100" style="width: 365px" required="required" placeholder="서브넷 명을 입력하세요." />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="width:100%;text-align: left;padding-left: 5px;">Network Address</label>
                        <div >
                            <input name="networkAddress" type="text" maxlength="100" style="width: 365px" required="required" placeholder="네트워크 주소를 입력하세요."  />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="width:100%;text-align: left;padding-left: 5px;">Gateway IP</label>
                        <div >
                            <input name="gatewayIp" type="text" maxlength="100" style="width: 365px" required="required" placeholder="게이트웨이 주소를 입력하세요." />
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="text-align: left; padding-left: 5px;">Enable DHCP</label>
                        <div style="width: 50%">
                            <input  name="enableDHCP" type="checkbox"  checked  style="margin:0px;" />&nbsp;
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="width:36%;text-align: left; padding-left: 5px;">IP Version</label>
                        <div>
                            <select style="width: 365px;" name="ipVersion">
                                <option value="IPv4">IPv4</option>
                            </select>
                        </div>
                    </div>
                    <div class="w2ui-field">
                        <label style="width:55%;text-align: left; padding-left: 5px;">DNS Name Servers</label>
                        <div>
                            <textarea rows="5" style="width:364px; height:60px;" name="dnsNameServers" placeholder="DNS 주소가 2개 이상일 경우 enter를 사용해주세요.&#13;&#10;예시)8.8.8.8&#13;&#10;8.8.4.4"></textarea>
                        </div>
                    </div>
                </div>
            </div>
        </form>
        <div style="text-align: center; padding-top:10px;">
	        <span id="addSubnetBtn" onclick="$('#settingForm').submit();" class="btn btn-primary" style="width:55px" >생성</span>
	        <span id="deleteSubnetBtn" onclick="deleteOpenstackSubnetInfo();" class="btn btn-danger" style="width:55px" >삭제</span>
	        <span id="cancle" onclick="w2popup.close();" class="btn btn-info" style="width:55px" >취소</span>
        </div>
    </div>
</div>
<div id="registAccountPopupDiv"  hidden="true">
    <input name="codeIdx" type="hidden"/>
    <div class="panel panel-info" style="margin-top:5px;" >    
        <div class="panel-heading"><b>openstack 계정 별칭 목록</b></div>
        <div class="panel-body">
            <div class="w2ui-field">
                <label style="width:30%;text-align: left;padding-left: 20px; margin-top: 20px;">계정 별칭</label>
                <div style="width: 70%;" class="accountList"></div>
            </div>
        </div>
    </div>
</div>

<div id="registAccountPopupBtnDiv" hidden="true">
    <button class="btn" id="registBtn" onclick="setDefaultIaasAccount('popup','openstack');">확인</button>
    <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
 </div>

<script type="text/javascript" src="<c:url value='/js/rules/openstackMgnt/openstack_network_rule.js'/>"></script>
<script>
$(function() {
    //서브넷 범위
    $.validator.addMethod( "ipv4Range", function( value, element, params ) {
         return /^((\b|\.)(0|1|2(?!5(?=6|7|8|9)|6|7|8|9))?\d{1,2}){4}(-((\b|\.)(0|1|2(?!5(?=6|7|8|9)|6|7|8|9))?\d{1,2}){4}|\/((0|1|2|3(?=1|2))\d|\d))\b$/.test(params);
    }, text_cidr_msg );
    //ipv4
    $.validator.addMethod( "ipv4", function( value, element, params ) {
         if( params ){
              return /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(params);     
         }else{
              return true;
         }
    }, text_ip_msg);
    //injection
    $.validator.addMethod("sqlInjection", function(value, element, params) {
        return checkInjectionBlacklist(params);
      },text_injection_msg);
});
</script>
