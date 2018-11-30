<%
/* =================================================================
 * 작성일 : 2018.03.20
 * 작성자 : 이정윤
 * 상세설명 : AWS 관리 화면
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix = "spring" uri = "http://www.springframework.org/tags" %>
 
<script>
var text_required_msg='<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
var detail_lock_msg='<spring:message code="common.search.detaildata.lock"/>';//상세 조회 중 입니다.
var text_cidr_msg='<spring:message code="common.text.validate.cidr.message"/>';//CIDR 대역을 확인 하세요.
var text_injection_msg='<spring:message code="common.text.validate.sqlInjection.message"/>';//입력하신 값은 입력하실 수 없습니다.
var save_lock_msg = '<spring:message code="common.save.data.lock"/>';//등록 중 입니다.
var delete_lock_msg='<spring:message code="common.delete.data.lock"/>';//삭제 중 입니다.
var select_required_msg='<spring:message code="common.select.vaildate.required.message"/>';//을(를) 선택하세요.
var search_lock_msg = '<spring:message code="common.update.data.lock"/>';//등록 중 입니다.
var accountId ="";
var bDefaultAccount = "";
var region = "";
var accountId =  "";
var routeTableId = "";
var vpcId = "";
var subnetInfoArray = [];

$(function() {
    
    bDefaultAccount = setDefaultIaasAccountList("aws");
    //화면 상단 라우트 테이블 정보 목록
    $('#aws_routeTableGrid').w2grid({
        name: 'aws_routeTableGrid',
        method: 'GET',
        msgAJAXerror : 'AWS Route Table 목록 조회 실패',
        header: '<b>AWS Route Table목록</b>',
        multiSelect: false,
        show: { 
                selectColumn: false,
                footer: true},
        style: 'text-align: center',
        columns    : [
                    {field: 'recid',     caption: 'recid', hidden: true}
                   ,{field: 'accountId',     caption: 'accountId', hidden: true}
                   ,{field: 'nameTag', caption: 'Name', size: '20%', style: 'text-align:center', info: true}
                   ,{field: 'routeTableId', caption: 'Route Table ID', size: '20%', style: 'text-align:center'}
                   ,{field: 'associationCnt', caption: 'Explicitly Associated With', size: '20%', style: 'text-align:center'}
                   ,{field: 'mainYN', caption: 'Main', size: '30%', style: 'text-align:center',
                        render: function(record) {
                            if(record.mainYN == false) {
                                return "N";
                            }else {
                                return "Y";
                            }
                        }
                   }
                   ,{field: 'vpcId', caption: 'VPC', size: '30%', style: 'text-align:center'}
                   ],
        onSelect: function(event) {
            event.onComplete = function() {
                $('#addRouteBtn').attr('disabled', false);
                $('#associateSubnetToBtn').attr('disabled', false);
                $('#deleteBtn').attr('disabled', false);
                
                region = $("select[name='region']").val();
                accountId =  $("select[name='accountId']").val();
                routeTableId = w2ui.aws_routeTableGrid.get(event.recid).routeTableId;
                vpcId = w2ui.aws_routeTableGrid.get(event.recid).vpcId;
                doSearchRouteDetail(accountId,routeTableId);
                doSearchAssociatedSubnets(accountId,routeTableId, vpcId);
            }
        },
        onUnselect: function(event) {
            event.onComplete = function() {
                $('#addRouteBtn').attr('disabled', true);
                $('#associateSubnetToBtn').attr('disabled', true);
                $('#deleteBtn').attr('disabled', true);
                w2ui['aws_routeGrid'].clear();
                w2ui['aws_associatedSubnetsGrid'].clear();
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
    
    //화면 하단 상세 목록 : 라우트 정보 목록
    $('#aws_routeGrid').w2grid({
        name: 'aws_routeGrid',
        method: 'GET',
        msgAJAXerror : 'AWS 계정을 확인해주세요.',
        header: '<b>Route 목록</b>',
        multiSelect: false,
        show: {    
                selectColumn: false,
                footer: true},
        style: 'text-align: center',
        columns    : [
                     {field: 'recid',     caption: 'recid', hidden: true}
                   , {field: 'accountId',     caption: 'accountId', hidden: true}
                   , {field: 'routeTableId',     caption: 'routeTableId', hidden: true}
                   , {field: 'destinationIpv4CidrBlock', caption: 'IPv4Destination', size: '50%', style: 'text-align:center'}
                   , {field: 'ipv6CidrBlock', caption: 'IPv6Destination', size: '50%', style: 'text-align:center'}
                   , {field: 'targetId', caption: 'Target', size: '50%', style: 'text-align:center'}
                   , {field: 'status', caption: 'Status', size: '50%', style: 'text-align:center'}
                   , {field: 'propagatedYN', caption: 'Propagated', size: '50%', style: 'text-align:center'}
                   ],
        onSelect: function(event) {
            event.onComplete = function() {
                $('#deleteRouteBtn').attr('disabled', false);
            }
        },
        onUnselect: function(event) {
            event.onComplete = function() {
                $('#deleteRouteBtn').attr('disabled', true);
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
    
    // 화면 하단 상세조회 목록 : associated subnets 목록
    $('#aws_associatedSubnetsGrid').w2grid({
        name: 'aws_associatedSubnetsGrid',
        method: 'GET',
        msgAJAXerror : 'AWS 계정을 확인해주세요.',
        header: '<b>Associated Subnets 목록</b>',
        multiSelect: false,
        show: {  
                selectColumn: false,
                footer: true},
        style: 'text-align: center',
        columns    : [
                     {field: 'recid',     caption: 'recid',  hidden: true}
                   , {field: 'accountId',     caption: 'accountId', hidden: true}
                   , {field: 'associationId',     caption: 'associationId', hidden: true}
                   , {field: 'subnetId', caption: 'subnetId', size: '25%', style: 'text-align:center'}
                   , {field: 'destinationIpv4CidrBlock', caption: 'IPv4 CIDR', size: '25%', style: 'text-align:center'}
                   , {field: 'ipv6CidrBlock', caption: 'IPv6', size: '25%', style: 'text-align:center'}
                   , {field: 'routeTableId',     caption: 'Current Route Table', size: '25%', style: 'text-align:center'}
        ],
        onSelect: function(event) {
            event.onComplete = function() {
                $('#disassociateSubnetFromBtn').attr('disabled', false);
            }
        },
        onUnselect: function(event) {
            event.onComplete = function() {
                $('#disassociateSubnetFromBtn').attr('disabled', true);
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
    
    /*************************** *****************************
     * 설명 :  AWS Route Table Create 팝업 화면
     *********************************************************/
    $("#addBtn").click(function(){
        if($("#addBtn").attr('disabled') == "disabled") return;
        w2popup.open({
            title   : "<b>AWS Route Table 생성 </b>",
            width   : 500,
            height  : 350,
            modal   : true,
            body    : $("#registPopupDiv").html(),
            buttons : $("#registPopupBtnDiv").html(),
            onOpen : function(event){
                event.onComplete = function(){
                    setAwsVpcIdList();
                }                   
            },onClose:function(event){
                initsetting();
                doSearch();
            }
        });
    }); 
    
    /*************************** *****************************
     * 설명 :  AWS Route Create 팝업 화면
     *********************************************************/
    $("#addRouteBtn").click(function(){
        if($("#addRouteBtn").attr('disabled') == "disabled") return;
        w2popup.open({
            title   : "<b>AWS Route Add </b>",
            width   : 500,
            height  : 350,
            modal   : true,
            body    : $("#registRoutePopupDiv").html(),
            buttons : $("#registRoutePopupBtnDiv").html(),
            onOpen : function(event){
                event.onComplete = function(){
                    setAwsTargetList();
                }                   
            },onClose:function(event){
                initsetting();
                doSearch();
            }
        });
    });
    
    
     /********************************************************
      * 설명 :subnet disassociate버튼 클릭
      *********************************************************/
      $("#disassociateSubnetFromBtn").click(function(){
          if($("#disassociateSubnetFromBtn").attr('disabled') == "disabled") return;
          w2popup.open({
              title: "<b>Disassociate Subnet  ( 연결 해제 ) </b>",
              width: 470,
              height: 225,
              modal: true,
              body: $("#subnetDisassociatePopupDiv").html(),
              buttons: $("#subnetDisassociatePopupBtnDiv").html(),
              onOpen: function(event){
                  accountId = $("select[name='accountId']").val();
              },
              onClose: function(event){
                  accountId = $("select[name='accountId']").val();
                  initsetting();
                  doSearch();
              }
          });
      });
    
      /********************************************************
       * 설명 :RouteTable delete버튼 클릭
       *********************************************************/
       $("#deleteBtn").click(function(){
           if($("#deleteBtn").attr('disabled') == "disabled") return;
           w2popup.open({
               title: "<b>라우트 테이블 삭제</b>",
               width: 470,
               height: 225,
               modal: true,
               body: $("#routeTableDeletePopupDiv").html(),
               buttons: $("#routeTableDeletePopupBtnDiv").html(),
               onOpen: function(event){
                   event.onComplete = function(){
                           accountId = $("select[name='accountId']").val();
                   }
               },
               onClose: function(event){
                   accountId = $("select[name='accountId']").val();
                   initsetting();
                   doSearch();
               }
           });
       });
       /********************************************************
        * 설명 :Subnet Associate 버튼 클릭
        *********************************************************/
        $("#associateSubnetToBtn").click(function(){
            if($("#associateSubnetToBtn").attr('disabled') == "disabled") return;
            w2popup.open({
                title: "<b>Subnet Associate</b>",
                width: 470,
                height: 430,
                modal: true,
                body: $("#subnetAssociatePopupDiv").html(),
                buttons: $("#subnetAssociatePopupBtnDiv").html(),
                onOpen: function(event){
                    event.onComplete = function(){
                    accountId = $("select[name='accountId']").val();
                    setAssociateSubnetId(accountId);
                    }
                },
                onClose: function(event){
                    accountId = $("select[name='accountId']").val();
                    initsetting();
                    doSearch();
                }
            });
        });
    
});

/********************************************************
 * 설명 : 서브넷 정보 조회 Function
 * 기능 : setAssociateSubnetId
 *********************************************************/
function setAssociateSubnetId(accountId){
    w2popup.lock("Subnet을 조회 중입니다.", true);
    $.ajax({
        type : "GET",
        url : "/awsMgnt/routeTable/list/avaliable/subnets/"+accountId+"/"+region+"/"+routeTableId+"/"+vpcId+"",
        contentType : "application/json",
        async : true,
        success : function(data, status) {
            console.log(data);
            subnetInfoArray = data;
            if( data!=null && data.length!=0 ){
                var subnetInfo = "";
                subnetInfo += "<select style='width:400px;' name='selectSubnetId' onchange='onchangesubnetInfo(this.value)';>";
                subnetInfo += "<option seleted value=''>서브넷을 선택하세요.</option>";
                for( var i=0; i<data.length; i++ ){
                    if(data[i].check != true && data[i].associationId == null){
                        subnetInfo += "<option value="+data[i].subnetId+">"+data[i].subnetId+"</option>"
                    }
                }
                subnetInfo += "</select>";
                $(".w2ui-msg-body #subnetId").html(subnetInfo);
                
            }else {
                subnetInfo += "<select style='width:400px;' name='selectSubnetId'>";
                subnetInfo += "<option value=''>subnet이 존재 하지 않습니다.</option>";
                subnetInfo += "<select>";
                $(".w2ui-msg-body #subnetId").html(subnetInfo);
            }
            w2popup.unlock();
        },
        error : function(request, status, error) {
            w2popup.unlock();
            initsetting();
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "");
        }
      });
}

/********************************************************
 * 설명 : 서브넷 정보 출력 onChange Event
 * 기능 : onchangesubnetInfo
 *********************************************************/
function onchangesubnetInfo(subnetId){
    if(subnetId == ""){
        $(".w2ui-msg-body input[name=ipv4Cidr]").val("");
        $(".w2ui-msg-body input[name=ipv6Cidr]").val("");
    }
    if( subnetInfoArray!=null && subnetInfoArray.length!=0 ){
        for( var i=0; i<subnetInfoArray.length; i++ ){
            if(subnetInfoArray[i].subnetId == subnetId){
                $(".w2ui-msg-body input[name=ipv4Cidr]").val(subnetInfoArray[i].destinationIpv4CidrBlock);
                $(".w2ui-msg-body input[name=ipv6Cidr]").val(subnetInfoArray[i].ipv6CidrBlock);
            }
        }
    }
 }

/********************************************************
 * 설명 : Route Table 목록 조회 Function
 * 기능 : doSearch
 *********************************************************/
function doSearch() {
    region = $("select[name='region']").val();
    if(region == null) region = "us-west-2";
    if(accountId != null)
    w2ui['aws_routeTableGrid'].load("<c:url value='/awsMgnt/routeTable/list/'/>"+accountId+"/"+region);
    doButtonStyle();
 }

/********************************************************
 * 설명 : Route Table 해당 Route List 조회 Function 
 * 기능 : doSearchRouteDetail
 *********************************************************/
function doSearchRouteDetail(accountId, routeTableId){
    w2utils.lock($("#layout_layout_panel_main"), detail_lock_msg, true);
    var region = $("select[name='region']").val();
    w2ui['aws_routeGrid'].load("<c:url value='/awsMgnt/routeTable/save/detail/route/'/>"+accountId+"/"+region+"/"+routeTableId);
    w2utils.unlock($("#layout_layout_panel_main"));
}

/********************************************************
 * 설명 : Route Table 해당 Subnet중 Associated Subnet List 조회 Function (화면 하단 Detail목록 ) 
 * 기능 : doSearchAssociatedSubnets
 *********************************************************/
function doSearchAssociatedSubnets(accountId, routeTableId, vpcId){
    var region = $("select[name='region']").val();
    w2ui['aws_associatedSubnetsGrid'].load("<c:url value='/awsMgnt/routeTable/list/detail/subnet/associated/'/>"+accountId+"/"+region+"/"+routeTableId+"/"+vpcId);
}
 
/********************************************************
 * 설명 : Route Table 생성
 * 기능 : awsRouteTableCreate
 *********************************************************/
function awsRouteTableCreate(){
    w2popup.lock( "생성중", true);
    var routeTableInfo = {
            accountId : $("select[name='accountId']").val(),
            region :  $("select[name='region']").val(),
            nameTag : $(".w2ui-msg-body input[name='nameTag']").val(),
            vpcId :$(".w2ui-msg-body select[name='vpcId']").val()
        }
    $.ajax({
        type : "POST",
        url : "/awsMgnt/routeTable/save",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(routeTableInfo),
        success : function(status) {
            w2popup.unlock();
            w2popup.close();    
            initsetting();
        },
        error : function(request, status, error) {
            w2popup.unlock();
            initsetting();
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "");
        }
      });
}

/********************************************************
 * 설명 : Route 생성
 * 기능 : awsRouteCreate
 *********************************************************/
function awsRouteCreate(){
    w2utils.lock($("#layout_layout_panel_main"), "", true);
    w2popup.lock( save_lock_msg , true);
    var selected = w2ui['aws_routeTableGrid'].getSelection();
    var record = w2ui['aws_routeTableGrid'].get(selected);
    routeInfo = { 
        accountId : $("select[name='accountId']").val(),
        region :  $("select[name='region']").val(), 
        routeTableId : record.routeTableId,
        destinationIpv4CidrBlock : $(".w2ui-msg-body input[name='destinationIpv4CidrBlock']").val(),
        targetId : $(".w2ui-msg-body select[name='targetId']").val()
    }
    $.ajax({
        type : "POST",
        url : "/awsMgnt/routeTable/route/add",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(routeInfo),
        success : function(status) {
            w2utils.unlock($("#layout_layout_panel_main"));
            w2popup.unlock();
            w2popup.close();    
        },
        error : function(request, status, error) {
            w2utils.unlock($("#layout_layout_panel_main"));
            w2popup.unlock();
            initsetting();
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "");
        }
    });
}

/********************************************************
 * 설명 : AWS Route 삭제 버튼 컨폼 
 * Function : awsRouteDeleteBtn()
 *********************************************************/
 function awsRouteDeleteBtn(){
     var selected = w2ui['aws_routeGrid'].getSelection();
     if( selected == null ){
         w2alert("선택된 정보가 없습니다.", "라우트 삭제");
         return;
     }else{
         var record = w2ui['aws_routeGrid'].get(selected);
         w2confirm({
             title: "AWS 라우트 삭제",
             msg: "라우트 테이블 (" + record.routeTableId + ") </br> 의 해당 라우트를 삭제하시겠습니까?",
             yes_text: "확인",
             no_text: "취소",
             yes_callBack: function(event){
                 awsRouteDelete(record);
             },
             no_callBack: function(event){
                 accountId = record.accountId;
                 initsetting();
                 doSearch();
             }
         });
     }
 }

/********************************************************
 * 설명 : Route 삭제 function
 * 기능 : awsRouteDelete
 *********************************************************/
function awsRouteDelete(record){
    w2utils.lock($("#layout_layout_panel_main"), "", true);
    w2popup.lock( delete_lock_msg , true);
    var selected = w2ui['aws_routeGrid'].getSelection();
    info = { 
            accountId : $("select[name='accountId']").val(),
            region :  $("select[name='region']").val(), 
            routeTableId : record.routeTableId,
            destinationIpv4CidrBlock : record.destinationIpv4CidrBlock,
            }
    $.ajax({
        type : "DELETE",
        url : "/awsMgnt/routeTable/route/delete",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(info),
        success : function(status) {
            w2utils.unlock($("#layout_layout_panel_main"));
            w2popup.unlock();
            w2popup.close();
            accountId = info.accountId;
            routeSetting();
            initsetting();
            doSearch();
            
        },
        error : function(request, status, error) {
            w2utils.unlock($("#layout_layout_panel_main"));
            w2popup.unlock();
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "");
        }
      });
}

/********************************************************
 * 기능 : setAwsVpcIdList
 * 설명 : 기본  Azure VPC 목록 조회 기능
 *********************************************************/
function setAwsVpcIdList(){
    w2popup.lock(detail_lock_msg, true);
    var accountId = $("select[name='accountId']").val();
    var region = $("select[name='region']").val();
    $.ajax({
           type : "GET",
           url : '/awsMgnt/routeTable/vpcIdList/'+accountId+'/'+region,
           contentType : "application/json",
           dataType : "json",
           success : function(data, status) {
               var result = "";
               if(data != null){
                   for(var i=0; i<data.length; i++){
                       result += "<option value='" + data[i].vpcId + "' >";
                       result += data[i].vpcId;
                       if(data[i].nameTag != null){
                       result += "  |  ";
                       result += data[i].nameTag;
                       }
                       result += "</option>"; 
                   }
               }
               $('#vpcInfoDiv #vpcInfo').html(result);
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
 * 기능 : setAwsTargetList
 * 설명 : 기본 AWS Target 목록 조회 기능
 *********************************************************/
function setAwsTargetList(){
    w2popup.lock(detail_lock_msg, true);
    var accountId = $("select[name='accountId']").val();
    var region = $("select[name='region']").val();
    var selected = w2ui['aws_routeTableGrid'].getSelection();
    var record = w2ui['aws_routeTableGrid'].get(selected);
    var vpcId = record.vpcId;
    $.ajax({
           type : "GET",
           url : '/awsMgnt/routeTable/route/list/targetList/'+accountId+'/'+region+'/'+vpcId,
           contentType : "application/json",
           dataType : "json",
           success : function(data, status) {
               var result = "";
               if(data != null && data.length != 0){
                   for(var i=0; i<data.length; i++){
                       
                       var splited =  data[i].split(" |");
                       result += "<option value='"+ splited[0] +"' >";
                       result += data[i];
                       result += "</option>"; 
                   }
               }
               $('#targetInfoDiv #targetInfo').html(result);
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
* 설명 : AWS Subnet Association(연결) function
* Function : associateSubnetToRouteTable
*********************************************************/
function associateSubnetToRouteTable(){
    w2popup.lock("연결 중", true);
    var selected = w2ui['aws_routeTableGrid'].getSelection();
    if( selected == null ){
        w2alert("선택된 정보가 없습니다.", "");
        return;
    }else{
        var record = w2ui['aws_routeTableGrid'].get(selected);
    }
    var info = {
          accountId : $("select[name='accountId']").val(),
          region :  $("select[name='region']").val(), 
          subnetId: $(".w2ui-msg-body select[name='selectSubnetId']").val(),
          routeTableId: record.routeTableId
    }
    $.ajax({
        type: "POST",
        url: "/awsMgnt/routeTable/list/subnet/associate",
        contentType: "application/json",
        async: true,
        data: JSON.stringify(info),
        success: function(status) {
            w2popup.unlock();
            w2popup.close();
            accountId = info.accountId;
            subnetsSetting();
            doSearch();
        },
        error: function(request, status, error) {
            w2popup.unlock();
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message);
        }
    });
}
   
/********************************************************
 * 설명 : AWS Subnet Disassociation (연결 해제) function
 * Function : disassociateSubnetFromRouteTable
*********************************************************/
function disassociateSubnetFromRouteTable(){
      w2popup.lock("연결 해제 중", true);
      var selected = w2ui['aws_associatedSubnetsGrid'].getSelection();
      if( selected == null ){
          w2alert("선택된 정보가 없습니다.", "");
          return;
      }else{
          var record = w2ui['aws_associatedSubnetsGrid'].get(selected);
      }
      var info = {
                accountId : $("select[name='accountId']").val(),
                region :  $("select[name='region']").val(), 
                associationId: record.associationId,
      }
      $.ajax({
          type: "DELETE",
          url: "/awsMgnt/routeTable/list/subnet/disassociate",
          contentType: "application/json",
          async: true,
          data: JSON.stringify(info),
          success: function(status) {
              w2popup.unlock();
              w2popup.close();
              accountId = info.accountId;
              subnetsSetting();
              doSearch();
          },
          error: function(request, status, error) {
              w2popup.unlock();
              var errorResult = JSON.parse(request.responseText);
              w2alert(errorResult.message);
          }
      });
}
   
/********************************************************
 * 설명 : AWS RouteTable 삭제 function
 * Function : routeTableDelete
*********************************************************/
function routeTableDelete(){
     w2popup.lock("삭제 중", true);
     var selected = w2ui['aws_routeTableGrid'].getSelection();
        if( selected == null ){
            w2alert("선택된 정보가 없습니다.", "");
            return;
        }else{
            var record = w2ui['aws_routeTableGrid'].get(selected);
        }
        var info = {
              accountId : $("select[name='accountId']").val(),
              region :  $("select[name='region']").val(), 
              routeTableId : record.routeTableId
        }
        $.ajax({
            type: "DELETE",
            url: "/awsMgnt/routeTable/delete",
            contentType: "application/json",
            async: true,
            data: JSON.stringify(info),
            success: function(status) {
                w2popup.unlock();
                w2popup.close();
                accountId = info.accountId;
                subnetsSetting();
                initsetting();
                doSearch();
                
            },
            error: function(request, status, error) {
                w2popup.unlock();
                var errorResult = JSON.parse(request.responseText);
                w2alert(errorResult.message);
            }
        });
}

/********************************************************
 * 기능 : initsetting
 * 설명 : 기본 설정값 초기화
 *********************************************************/
function initsetting(){
     bDefaultAccount="";
     region = "";
     routeTableId = "";
     vpcId = "";
     subnetInfoArray = [];
     w2ui['aws_routeTableGrid'].clear();
     w2ui['aws_routeGrid'].clear();
     w2ui['aws_associatedSubnetsGrid'].clear();
}

/********************************************************
 * 기능 : subnetsSetting
 * 설명 : 기본 설정값 초기화
 *********************************************************/
function subnetsSetting(){
    w2ui['aws_associatedSubnetsGrid'].clear();
    
}

/********************************************************
 * 기능 : routeSetting
 * 설명 : 기본 설정값 초기화
 *********************************************************/
function routeSetting(){
    w2ui['aws_routeGrid'].clear();
    w2ui['aws_associatedSubnetsGrid'].clear();
}

/********************************************************
 * 설명 : 초기 버튼 스타일
 * 기능 : doButtonStyle
 *********************************************************/
function doButtonStyle() {
    $('#addRouteBtn').attr('disabled', true);
    $('#deleteRouteBtn').attr('disabled', true);
    $('#deleteBtn').attr('disabled', true);
    $('#subnetAssociationBtn').attr('disabled', true);
    $('#associateSubnetToBtn').attr('disabled', true);
    $('#disassociateSubnetFromBtn').attr('disabled', true);
}

/********************************************************
 * 기능 : clearMainPage
 * 설명 : 다른페이지 이동시 호출
 *********************************************************/
function clearMainPage() {
    $().w2destroy('aws_routeTableGrid');
    $().w2destroy('aws_routeGrid');
    $().w2destroy('aws_associatedSubnetsGrid');
}

/********************************************************
 * 기능 : resize
 * 설명 : 화면 리사이즈시 호출
 *********************************************************/
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
     <div class="page_site pdt20">인프라 관리 > AWS 관리 > <strong>AWS Route Tables 관리 </strong></div>
     <div id="awsMgnt" class="pdt20">
        <ul>
            <li>
                <label style="font-size: 14px">AWS 관리 화면</label> &nbsp;&nbsp;&nbsp; 
                <div class="dropdown" style="display:inline-block;">
                    <a href="#" class="dropdown-toggle iaas-dropdown" data-toggle="dropdown" aria-expanded="false">
                        &nbsp;&nbsp;Route Table 관리<b class="caret"></b>
                    </a>
                    <ul class="dropdown-menu alert-dropdown">
                        <sec:authorize access="hasAuthority('AWS_VPC_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/awsMgnt/vpc"/>', 'AWS VPC');">VPC 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('AWS_SUBNET_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/awsMgnt/subnet"/>', 'AWS SUBNET');">Subnet 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('AWS_INTERNET_GATEWAY_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/awsMgnt/internetGateway"/>', 'AWS Internet GateWay');">Internet Gateway 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('AWS_SECURITY_GROUP_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/awsMgnt/securityGroup"/>', 'AWS SECURITY GROUP');">Security Group 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('AWS_ELASTIC_IP_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/awsMgnt/elasticIp"/>', 'AWS Elastic Ip');">Elastic Ip 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('AWS_KEYPAIR_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/awsMgnt/keypair"/>', 'AWS KEYPAIR');">KeyPair 관리</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('AWS_NAT_GATEWAY_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/awsMgnt/natGateway"/>', 'AWS NAT GateWay');">NAT Gateway 관리</a></li>
                        </sec:authorize>
                    </ul>
                </div>
            </li>
            <li>
                <label style="font-size: 14px">AWS Region</label>
                &nbsp;&nbsp;&nbsp;
                <select name="region" onchange="awsRegionOnchange();" id="regionList" class="select" style="width:300px; font-size: 15px; height: 32px;"></select>
            </li>
            <li>
                <label style="font-size: 14px">AWS 계정 명</label>
                &nbsp;&nbsp;&nbsp;
                <select name="accountId" id="setAccountList" class="select" style="width: 300px; font-size: 15px; height: 32px;" onchange="setAccountInfo(this.value, 'aws')">
                </select>
                <span id="doSearch" onclick="setDefaultIaasAccount('noPopup','aws');" class="btn btn-info" style="width:80px" >선택</span>
            </li>
        </ul>
    </div>

    <div class="pdt20">
        <div class="title fl">AWS Route Table 목록</div>
        <div class="fr"> 
         <%-- <sec:authorize access="hasAuthority('AWS_ROUTE_TABLE_CREATE')"> --%>
            <span id="addBtn" class="btn btn-primary" style="width:140px" > 라우트 테이블 생성 </span>
            <span id="deleteBtn" class="btn btn-danger"  onclick="" style="width:140px" > 라우트 테이블 삭제 </span>
            <!-- <span id="subnetAssociationBtn" class="btn  btn-warning" onclick="subnetAssociation()" style="left: 20px;" > Subnet Associations 수정 </span> -->
        </div>
    </div>
    <div id="aws_routeTableGrid" style="width:100%; height:305px"></div>
   
    <div class="pdt20">
    <div class="title fl">Routes 정보</div>
    <div class="fr"> 
    <span id="addRouteBtn" class="btn btn-primary"  onclick="" style="width:140px" > Route 추가 </span>
    <span id="deleteRouteBtn" class="btn btn-danger"  onclick="awsRouteDeleteBtn();" style="width:140px" > Route 삭제 </span>
    </div>
    <div id="aws_routeGrid" style="width:100%; height:150px"></div>
    </div>
    
    <div style="margin-top:20px;width:100%; float:left;">
    <div class="title fl">Associated Subnets 목록</div>
    <div class="fr"> 
    <span style="width:140px" class="btn btn-primary" id="associateSubnetToBtn" > Associate 하기 </span>
    <span style="width:140px" class="btn btn-danger" id="disassociateSubnetFromBtn" > Disassociate 하기 </span>
    </div>
    <div id="aws_associatedSubnetsGrid" style="width:100%; height:150px"></div>
    </div> 
    
    </div> 

<!-- AWS Route Table Create 팝업 Div-->
<div id="registPopupDiv" hidden="true">
<form id="awsRouteTableForm" action="POST" style="padding:5px 0 5px 0;margin:0;">
    <div id="awsRouteTableCreate" >
         <div class="panel panel-info" style="height: 250px; margin-top: 7px;"> 
            <div class="panel-heading"><b>AWS Route Table 생성</b></div>
            
           <div class="w2ui-field">
               <label style="width:100%; margin-top:20px; text-align: left; padding-left: 20px;"> Name Tag</label>
           </div>
            <div class="w2ui-field">
               <div id="nameTagDiv" style="width:420px;  padding-left: 20px;">
                   <input id="nameTag" style="width:400px;" name="nameTag" placeholder=""/>
               </div>
           </div>
           <div class="w2ui-field">
               <label style="width:100%; margin-top:20px; text-align: left; padding-left: 20px;"> VPC </label>
           </div>
           <div class="w2ui-field">    
               <div id="vpcInfoDiv" style="width:420px;  padding-left: 20px;">
                   <select id="vpcInfo" style="width:400px;" name="vpcId"><option></option></select>
               </div>
           </div>
        </div>   
    </div> 
</form>
</div>
<div id="registPopupBtnDiv" hidden="true">
     <button class="btn btn-primary" id="registBtn" onclick="$('#awsRouteTableForm').submit();">확인</button>
     <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
</div>


<!-- AWS Subnet Associate 팝업 Div-->
<div id="subnetAssociatePopupDiv" hidden="true">
<form id="awsSubnetAssociateForm" action="POST" style="padding:5px 0 5px 0;margin:0;">
    <div id="awsRouteTableCreate" >
         <div class="panel panel-info" style="height: 320px; margin-top: 7px;"> 
            <div class="panel-heading"><b>Subnet Associate</b></div>
            
           <div class="w2ui-field">
               <label style="width:100%; margin-top:20px; text-align: left; padding-left: 20px;"> Subnet Id</label>
           </div>
            <div class="w2ui-field">
               <div id="subnetId" style="width:420px;  padding-left: 20px;">
                   <select id="selectSubnetId" style="width:400px;" name="selectSubnetId">
                       <option value=''>서브넷을 선택하세요.</option>
                   </select>
               </div>
           </div>
           
           <div class="w2ui-field">
               <label style="width:100%; margin-top:20px; text-align: left; padding-left: 20px;">IPv4 CIDR</label>
           </div>
           <div class="w2ui-field">
               <div style="width:420px;  padding-left: 20px;">
                   <input name="ipv4Cidr" type="text" readonly style="width:400px;" placeholder="IPv4 CIDR를 입력하세요."/>
               </div>
           </div>
           <div class="w2ui-field">
               <label style="width:100%; margin-top:20px; text-align: left; padding-left: 20px;">IPv6 CIDR</label>
           </div>
           <div class="w2ui-field">
               <div style="width:420px;  padding-left: 20px;">
                   <input name="ipv6Cidr" type="text" readonly style="width:400px;" placeholder="IPv6 CIDR를 입력하세요."/>
               </div>
           </div>
           
        </div>   
    </div> 
</form>
</div>
<div id="subnetAssociatePopupBtnDiv" hidden="true">
     <button class="btn btn-primary" id="registBtn" onclick="$('#awsSubnetAssociateForm').submit();">확인</button>
     <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
</div>


<!-- AWS Route Add 팝업 Div-->
<div id="registRoutePopupDiv" hidden="true">
<form id="awsRouteForm" action="POST" style="padding:5px 0 5px 0;margin:0;">
    <div id="awsRouteCreate" >
         <div class="panel panel-info" style="height: 250px; margin-top: 7px;"> 
            <div class="panel-heading"><b>AWS Route 추가</b></div>
            
           <div class="w2ui-field">
               <label style="width:100%; margin-top:20px; text-align: left; padding-left: 20px;"> Destination Ip4 CIDR </label>
           </div>
            <div class="w2ui-field">
               <div id="destinationIdDiv" style="width:420px;  padding-left: 20px;">
                   <input id="destinationId" style="width:400px;" name="destinationIpv4CidrBlock" placeholder="0.0.0.0/0"/>
               </div>
           </div>
           <div class="w2ui-field">
               <label style="width:100%; margin-top:20px; text-align: left; padding-left: 20px;"> Target </label>
           </div>
           <div class="w2ui-field">    
               <div id="targetInfoDiv" style="width:420px;  padding-left: 20px;">
                   <select id="targetInfo" style="width:400px;" name="targetId"><option></option></select>
               </div>
           </div>
        </div>   
    </div> 
</form>
</div>
<div id="registRoutePopupBtnDiv" hidden="true">
     <button class="btn btn-primary" id="registBtn" onclick="$('#awsRouteForm').submit();">확인</button>
     <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
</div>


<div id="subnetAddPopupBtnDiv" hidden="true">
     <button class="btn btn-primary" id="" onclick="associateSubnetToRouteTable();">확인</button>
     <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
</div>

<!-- AWS  Disassociate Subnet 팝업 Div -->
<div id="subnetDisassociatePopupDiv" hidden="true">
    <div style="margin-top:50px; margin-left:10px;"> 이 Subnet을 이 라우트테이블에서 disassociate ( 연결 해제 ) 하시겠습니까?</div>
</div>
<div id="subnetDisassociatePopupBtnDiv" hidden="true">
     <button class="btn btn-primary" id="" onclick="disassociateSubnetFromRouteTable();">확인</button>
     <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
</div>

<!-- AWS  라우트 테이블 삭제 팝업 Div -->
<div id="routeTableDeletePopupDiv" hidden="true">
    <div style="margin-top:50px; margin-left:10px;"> 이 라우트 테이블을 삭제 하시겠습니까?</div>
</div>
<div id="routeTableDeletePopupBtnDiv" hidden="true">
     <button class="btn btn-primary" id="" onclick="routeTableDelete();">확인</button>
     <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
</div>


<!-- AWS 계정 선택 Div-->
<div id="registAccountPopupDiv"  hidden="true">
    <input name="codeIdx" type="hidden"/>
    <div class="panel panel-info" style="margin-top:5px;" >    
        <div class="panel-heading"><b>AWS 계정 별칭 목록</b></div>
        <div class="panel-body" style="padding:5px 5% 10px 5%;height:65px;">
            <div class="w2ui-field">
                <label style="width:30%;text-align: left;padding-left: 20px; margin-top: 20px;">AWS 계정 별칭</label>
                <div style="width: 70%;" class="accountList"></div>
            </div>
        </div>
    </div>
</div>

<div id="registAccountPopupBtnDiv" hidden="true">
    <button class="btn" id="registBtn" onclick="setDefaultIaasAccount('popup','aws');">확인</button>
    <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
</div>

<script>
$(function() {
    $.validator.addMethod("sqlInjection", function(value, element, params) {
        return checkInjectionBlacklist(params);
      },text_injection_msg);
    
    $("#awsRouteTableForm").validate({
        ignore : "",
        onfocusout: true,
        rules: {
            nameTag : {
                sqlInjection :   function(){
                    return $(".w2ui-msg-body input[name='nameTag']").val();
                }
            },
            vpcId : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body select[name='vpcId']").val() );
                },
            }
        }, messages: {
            nameTag: { 
                sqlInjection : text_injection_msg
           },
            vpcId: { 
                 required:  "VPC" + text_required_msg
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
            awsRouteTableCreate();
        }
    });
    
    $.validator.addMethod( "destinationIpv4CidrBlock", function( value, element, params ) {
        return /^((\b|\.)(0|1|2(?!5(?=6|7|8|9)|6|7|8|9))?\d{1,2}){4}(-((\b|\.)(0|1|2(?!5(?=6|7|8|9)|6|7|8|9))?\d{1,2}){4}|\/((0|1|2|3(?=1|2))\d|\d))\b$/.test(params);
    }, text_cidr_msg );
    
    $("#awsRouteForm").validate({
        ignore : "",
        onfocusout: true,
        rules: {
            destinationIpv4CidrBlock : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='destinationIpv4CidrBlock']").val() );
                }, destinationIpv4CidrBlock : function(){
                    return $(".w2ui-msg-body input[name='destinationIpv4CidrBlock']").val();
                }
            },
            targetId : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body select[name='targetId']").val() );
                }
            }
        }, messages: {
            destinationIpv4CidrBlock: { 
                required:  "Destination CIDR" + text_required_msg
           },
            targetId: { 
                 required:  "선택 가능한 Target이 없습니다. Internet Gateway 또는 NAT Gateway 가 필요합니다. "
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
            awsRouteCreate();
        }
    });
    
    $("#awsSubnetAssociateForm").validate({
        ignore : "",
        onfocusout: true,
        rules: {
            selectSubnetId : {
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body select[name='selectSubnetId']").val() );
                }
            }
        }, messages: {
            selectSubnetId: { 
                required:  "서브넷" + select_required_msg
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
            associateSubnetToRouteTable();
        }
    });
    
});



</script>
