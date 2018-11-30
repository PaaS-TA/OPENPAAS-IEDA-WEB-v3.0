<%
/* =================================================================
 * 작성일 : 2017.07.07
 * 작성자 : 이동현
 * 상세설명 : AWS VPC 관리 화면
 * =================================================================
 */ 
%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix = "spring" uri = "http://www.springframework.org/tags" %>

<script>
var save_lock_msg = '<spring:message code="common.save.data.lock"/>';//등록 중 입니다.
var detail_Vpc_lock_msg='<spring:message code="common.search.detaildata.lock"/>';//상세 조회 중 입니다.
var delete_lock_msg='<spring:message code="common.delete.data.lock"/>';//삭제 중 입니다.
var text_required_msg='<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
var text_cidr_msg='<spring:message code="common.text.validate.cidr.message"/>';//CIDR 대역을 확인 하세요.
var delete_confirm_msg='<spring:message code="common.popup.delete.message"/>';//을(를) 삭제 하시겠습니까?
        
var accountId ="";
var bDefaultAccount = "";
var setAwsRegion = "";
var region = "";

$(function() {
    
    bDefaultAccount = setDefaultIaasAccountList("aws");
    
    $('#aws_vpcGrid').w2grid({
        name: 'aws_vpcGrid',
        method: 'GET',
        msgAJAXerror : 'AWS 계정을 확인해주세요.',
        header: '<b>Vpc 목록</b>',
        multiSelect: false,
        show: {    
                selectColumn: true,
                footer: true},
        style: 'text-align: center',
        columns    : [
                     {field: 'recid',     caption: 'recid', hidden: true}
                   , {field: 'accountId',     caption: 'accountId', hidden: true}
                   , {field: 'nameTag', caption: 'VPC Name', size: '50%', style: 'text-align:center', render : function(record){
                       if(record.nameTag == null || record.nameTag == ""){
                           return "-"
                       }else{
                           return record.nameTag;
                       }}
                   }
                   , {field: 'vpcId', caption: 'VPC Id', size: '50%', style: 'text-align:center'}
                   , {field: 'status', caption: 'VPC Status', size: '50%', style: 'text-align:center'}
                   , {field: 'ipv4CidrBlock', caption: 'IPv4 CIDR', size: '50%', style: 'text-align:center'}
                   , {field: 'ipv6CidrBlock', caption: 'IPv6 CIDR', size: '50%', style: 'text-align:center', render : function(record){
                       if(record.ipv6CidrBlock == null || record.ipv6CidrBlock == ""){
                           return "-"
                       }else{
                           return record.ipv6CidrBlock;
                       }}
                   }
                   , {field: 'dhcpOptionSet', caption: 'DHCP Options Set', size: '50%', style: 'text-align:center'}
                   , {field: 'tenancy', caption: 'Tenacy', size: '50%', style: 'text-align:center'}
                   , {field: 'defaultVpc', caption: 'Default VPC', size: '50%', style: 'text-align:center', render : function(record){
                       
                       if(record.defaultVpc == true){
                           return "yes"
                       }else{
                           return "no";
                       }}
                   }
                   ],
        onSelect: function(event) {
            event.onComplete = function() {
                $('#deleteBtn').attr('disabled', false);
                var accountId =  w2ui.aws_vpcGrid.get(event.recid).accountId;
                var vpcId = w2ui.aws_vpcGrid.get(event.recid).vpcId;
                var region = $("select[name='region']").val();
                doSearchVpcDetailInfo(accountId, vpcId, region);
            }
        },
        onUnselect: function(event) {
            event.onComplete = function() {
                $('#deleteBtn').attr('disabled', true);
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
     * 설명 : VPC 생성 버튼 클릭
    *********************************************************/
    $("#addBtn").click(function(){
        if($("#addBtn").attr('disabled') == "disabled") return;
       w2popup.open({
           title   : "<b>AWS VPC 생성</b>",
           width   : 580,
           height  : 370,
           modal   : true,
           body    : $("#registPopupDiv").html(),
           buttons : $("#registPopupBtnDiv").html(),
           onClose : function(event){
            accountId = $("select[name='accountId']").val();
            w2ui['aws_vpcGrid'].clear();
            doSearch();
            $("#vpcDetailTable td").html("");
           }
       });
    });
    
    /********************************************************
     * 설명 : AWS VPC 삭제 버튼 클릭
    *********************************************************/
   $("#deleteBtn").click(function(){
       if($("#deleteBtn").attr('disabled') == "disabled") return;
       var selected = w2ui['aws_vpcGrid'].getSelection();        
       if( selected.length == 0 ){
           w2alert("선택된 정보가 없습니다.", "VPC 삭제");
           return;
       }
       else {
           var result = "";
           result = "<table class= 'table table-fixed' style ='margin-top:15px; margin-bottom:-10px; text-align:left;'>"
           result +="<tr><td>Subnet</td><td>InternetGateWay</td></tr>"
           result +="<tr><td>Security Groups</td><td>Route Table</td></tr>"
           result +="<tr><td>Network ACLs</td><td>Network Interface</td></tr>"
           result +="<tr><td>VPN Attachments</td><td>VPC Peering Connections</td></tr>"
           result += "</table><br/>"
           var record = w2ui['aws_vpcGrid'].get(selected);
           w2confirm({
               title   : "<b>VPC 삭제</b>",
               msg     : "VPC (" + record.vpcId + ")"+ delete_confirm_msg +"<br/>"
                                      +"<strong><font color='red'>VPC와 연동 된</font></strong><br/>"
                                      + result
                                      +"<strong><font color='red'>등이 삭제 될 수 있습니다.</font></strong><br/>"
                                      +"<strong><font color='red'>그래도 삭제 하시 겠습니까?</strong><red>"   ,
               yes_text : "확인",
               no_text : "취소",
               height : 350,
               yes_callBack: function(event){
                   deleteAwsVpcInfo(record);
               },
               no_callBack    : function(){
                   w2ui['aws_vpcGrid'].clear();
                   accountId = record.accountId;
                   doSearch();
                   $("#vpcDetailTable td").html("");
               }
           });
       }
   });
});

/********************************************************
 * 설명 : VPC 목록 조회 Function 
 * 기능 : doSearch
 *********************************************************/
function doSearch() {
    region = $("select[name='region']").val();
    if(region == null) region = "us-west-2";
    w2ui['aws_vpcGrid'].load('/awsMgnt/vpc/list/'+accountId+'/'+region+'');
    doButtonStyle();
    accountId = "";
}

/********************************************************
 * 설명 : VPC 정보 상세 조회 Function 
 * 기능 : doSearchVpcDetail
 *********************************************************/
function doSearchVpcDetailInfo(accountId, vpcId, region){
    w2utils.lock($("#layout_layout_panel_main"), detail_Vpc_lock_msg, true);
    $.ajax({
        type : "GET",
        url : "/awsMgnt/vpc/save/detail/"+accountId+"/"+vpcId+"/"+region+"",
        contentType : "application/json",
        success : function(data, status) {
            w2utils.unlock($("#layout_layout_panel_main"));
            if(data != null){
                $(".vpcId").html(data.vpcId);
                $(".state").html(data.status);
                $(".ipv4Cidr").html(data.ipv4CidrBlock);
                if(data.ipv6CidrBlock != null){
                    $(".ipv6Cidr").html(data.ipv6CidrBlock);
                }else{
                    $(".ipv6Cidr").html("-");
                }
                $(".dhcpOptionsSet").html(data.dhcpOptionSet);
                $(".routeTable").html(data.routeTable);
                $(".networkAcl").html(data.networkAcle);
                $(".tenancy").html(data.tenancy);
                if(data.dnsHostNames == true){
                    $(".dnsHostnames").html("yes");
                }else{
                    $(".dnsHostnames").html("no");
                }
                if(data.dnsResolution == true){
                    $(".dnsResolution").html("yes");
                }else{
                    $(".dnsResolution").html(data.dnsResolution);
                }
                if(data.classicLinkDns == true){
                    $(".classicLinkDns").html("yes");
                }else{
                    $(".classicLinkDns").html("no");
                }
                if(data.nameTag != null){
                    $(".vpcTagName").html(data.nameTag);
                }else{
                    $(".vpcTagName").html("-");
                }
            }
        },
        error : function(request, status, error) {
            w2utils.unlock($("#layout_layout_panel_main"));
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "AWS VPC 상세 조회");
        }
    });
}

/********************************************************
 * 설명 : AWS VPC 생성
 * 기능 : saveAwsVpcInfo
 *********************************************************/
function saveAwsVpcInfo(){
     w2popup.lock(save_lock_msg, true);
    var vpcInfo = {
        region : $("select[name='region']").val(),	
        accountId : $("select[name='accountId']").val(),
        nameTag : $(".w2ui-msg-body input[name='nameTag']").val(),
        ipv4CirdBlock : $(".w2ui-msg-body input[name='ipv4CirdBlock']").val(),
        ipv6CirdBlock : $(".w2ui-msg-body :radio[name='ipv6CirdBlock']:checked").val(),
        tenancy : $(".w2ui-msg-body select[name='tenancy']").val(),
    }
    $.ajax({
        type : "POST",
        url : "/awsMgnt/vpc/save",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(vpcInfo),
        success : function(status) {
            w2popup.unlock();
            w2popup.close();
            accountId = vpcInfo.accountId;
            doSearch();
        }, error : function(request, status, error) {
            w2popup.unlock();
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message);
        }
    });
}

/********************************************************
 * 설명 : AWS VPC 삭제
 * 기능 : deleteAwsVpcInfo
 *********************************************************/
function deleteAwsVpcInfo(record){
     w2popup.lock(delete_lock_msg, true);
    var vpcInfo = {
            region : $("select[name='region']").val(),
            accountId : record.accountId,
            vpcId : record.vpcId
    }
    $.ajax({
        type : "DELETE",
        url : "/awsMgnt/vpc/delete",
        contentType : "application/json",
        async : true,
        data : JSON.stringify(vpcInfo),
        success : function(status) {
            w2popup.unlock();
            w2popup.close();
            accountId = vpcInfo.accountId;
            w2ui['aws_vpcGrid'].clear();
            $("#vpcDetailTable td").html("");
            doSearch();
        }, error : function(request, status, error) {
            w2popup.unlock();
            $("#vpcDetailTable td").html("");
            w2ui['aws_vpcGrid'].clear();
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
    $().w2destroy('aws_vpcGrid');
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
     <div class="page_site pdt20">인프라 관리 > AWS 관리 > <strong>AWS VPC 관리 </strong></div>
     <div id="awsMgnt" class="pdt20">
        <ul>
            <li>
                <label style="font-size: 14px">AWS 관리 화면</label> &nbsp;&nbsp;&nbsp; 
                <div class="dropdown" style="display:inline-block;">
                    <a href="#" class="dropdown-toggle iaas-dropdown" data-toggle="dropdown" aria-expanded="false">
                        &nbsp;&nbsp;VPC 관리<b class="caret"></b>
                    </a>
                    <ul class="dropdown-menu alert-dropdown">
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
                        <sec:authorize access="hasAuthority('AWS_ROUTE_TABLE_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/awsMgnt/routeTable"/>', 'AWS Route Table');">Route Table 관리</a></li>
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
        <div class="title fl">AWS VPC 목록</div>
        <div class="fr"> 
            <sec:authorize access="hasAuthority('AWS_VPC_CREATE')">
            <span id="addBtn" class="btn btn-primary" style="width:120px">생성</span>
            </sec:authorize>
            <sec:authorize access="hasAuthority('AWS_VPC_DELETE')">
            <span id="deleteBtn" class="btn btn-danger" style="width:120px">삭제</span>
            </sec:authorize>
        </div>
    </div>
    <div id="aws_vpcGrid" style="width:100%; height:405px"></div>

<!-- VPC 생성 팝업 -->
<div id="registPopupDiv" hidden="true">
    <form id="awsVpcForm" action="POST" style="padding:5px 0 5px 0;margin:0;">
        <div class="panel panel-info" style="height: 260px; margin-top: 7px;"> 
            <div class="panel-heading"><b>AWS VPC 정보</b></div>
            <div class="panel-body" style="padding:20px 10px; height:220px; overflow-y:auto;">
                <input type="hidden" name="accountId"/>
                <div class="w2ui-field">
                    <label style="width:36%;text-align: left; padding-left: 20px;">VPC Name tag</label>
                    <div>
                        <input name="nameTag" type="text"   maxlength="100" style="width: 300px; margin-top: 1px;" placeholder="VPC 태그 명을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:36%;text-align: left; padding-left: 20px;">IPv4 CIDR block</label>
                    <div>
                        <input name="ipv4CirdBlock" type="text"   maxlength="100" style="width: 300px" placeholder="VPC IPv4 CIDR block을 입력하세요."/>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:36%;text-align: left; padding-left: 20px;">IPv6 CIDR block</label>
                    <div>
                        <input type="radio" name="ipv6CirdBlock" value="false" checked="checked">&nbsp;&nbsp;&nbsp;No IPv6 CIDR Block
                        <br>
                        <input type="radio" name="ipv6CirdBlock" value="true">&nbsp;&nbsp;&nbsp;Amazon provided IPv6 CIDR block
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:36%;text-align: left; padding-left: 20px;">Tenancy</label>
                    <div>
                        <select name="tenancy">
                            <option value="Default">Default</option>
                            <option value="Dedicated">Dedicated</option>
                        </select>
                    </div>
                </div>
            </div>
        </div>
    </form> 
</div>
<div id="registPopupBtnDiv" hidden="true">
     <button class="btn" id="registBtn" onclick="$('#awsVpcForm').submit();">확인</button>
     <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
</div>
    <div class="pdt20" >
        <div class="title fl">AWS VPC 상세 목록</div>
    </div>
    <div id="aws_vpcDetailGrid" style="width:100%; height:128px; margin-top:50px; border-top: 2px solid #c5c5c5; ">
    <table id= "vpcDetailTable" class="table table-condensed table-hover">
           <tr>
               <th class= "trTitle">VPC ID</th>
               <td class= "vpcId"></td>
               <th class= "trTitle">State</th>
               <td class="state"></td>
               <th class= "trTitle">IPv4 CIDR</th>
               <td class= "ipv4Cidr"></td>
           </tr>
           <tr>
               <th class= "trTitle">IPv6 CIDR</th>
               <td class= "ipv6Cidr"></td>
               <th class= "trTitle">DHCP options set</th>
               <td class= "dhcpOptionsSet"></td>
               <th class= "trTitle">Route table</th>
               <td class= "routeTable"></td>
           </tr>
           <tr>
               <th class= "trTitle">Network ACL</th>
               <td class= "networkAcl"></td>
               <th class= "trTitle">Tenancy</th>
               <td class= "tenancy"></td>
               <th class= "trTitle">DNS hostnames</th>
               <td class= "dnsHostnames"></td>
           </tr>
           <tr style = "border-bottom: 1px solid #ddd;">
               <th class= "trTitle">DNS resolution</th>
               <td class= "dnsResolution"></td>
               <th class= "trTitle">ClassicLink DNS Support</th>
               <td class= "classicLinkDns"></td>
               <th class= "trTitle">VPC TagName</th>
               <td class= "vpcTagName"></td>
           </tr>
        </table>
    </div>
</div>

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
    $.validator.addMethod( "ipv4Range", function( value, element, params ) {
        return /^((\b|\.)(0|1|2(?!5(?=6|7|8|9)|6|7|8|9))?\d{1,2}){4}(-((\b|\.)(0|1|2(?!5(?=6|7|8|9)|6|7|8|9))?\d{1,2}){4}|\/((0|1|2|3(?=1|2))\d|\d))\b$/.test(params);
    }, text_cidr_msg );
    
    $("#awsVpcForm").validate({
        ignore : "",
        onfocusout: true,
        rules: {
            nameTag : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='nameTag']").val() );
                },
            }, ipv4CirdBlock: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='ipv4CirdBlock']").val() );
                }, ipv4Range : function(){
                    return $(".w2ui-msg-body input[name='ipv4CirdBlock']").val();
                }
            }
        }, messages: {
            nameTag: { 
                 required:  "VPC Name Tag" + text_required_msg
            }, ipv4CirdBlock: { 
                required:  "Ipv4 CIDR Block"+text_required_msg
                ,ipv4Range : text_cidr_msg
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
            saveAwsVpcInfo();
        }
    });
});

</script>
