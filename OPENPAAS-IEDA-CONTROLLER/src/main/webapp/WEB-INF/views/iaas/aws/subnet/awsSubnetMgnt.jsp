
<%
   /* =================================================================
 * 작성일 : 2017.7.05 
 * 작성자 : 이정윤
 * 상세설명 : AWS 서브넷 관리 화면
 * =================================================================
 */
%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<script>
var save_lock_msg = '<spring:message code="common.save.data.lock"/>';//등록 중 입니다.
var search_lock_msg = '<spring:message code="common.search.data.lock"/>';//조회 중 입니다.
var detail_subnet_lock_msg='<spring:message code="common.search.detaildata.lock"/>';//상세 조회 중 입니다. 
var text_required_msg='<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
var text_cidr_msg='<spring:message code="common.text.validate.cidr.message"/>';//CIDR 대역을 확인 하세요.
var delete_lock_msg='<spring:message code="common.delete.data.lock"/>';//삭제 중 입니다.
var delete_confirm_msg='<spring:message code="common.popup.delete.message"/>';//을(를) 삭제 하시겠습니까?

var vpcsInfo="";
var accountId="";
var bDefaultAccount = "";
var region = "";
    $(function() {
        bDefaultAccount = setDefaultIaasAccountList("aws");
        
        $('#aws_subnetGrid').w2grid({
            name : 'aws_subnetGrid',
            method : 'GET',
            msgAJAXerror : 'AWS Subnet 목록 조회 실패',
            header : '<b>Property 목록</b>',
            multiSelect : false,
            show : {
                selectColumn : true,
                footer : true
            },
            style : 'text-align: center',
            columns : [ {field : 'recid', caption : 'recid', hidden : true}
                      , {field : 'nameTag', caption : 'Name', size : '20%', style : 'text-align:center'}
                      , {field : 'subnetId', caption : 'Subnet ID', size : '20%', style : 'text-align:center'}
                      , {field : 'state', caption : 'State', size : '20%', style : 'text-align:center'}
                      , {field : 'vpcId',caption : 'VPC',size : '20%',style : 'text-align:center'}
                      , {field : 'cidrBlock',caption : 'IPv4 CIDR',size : '20%',style : 'text-align:center'}
                      , {field : 'availabilityZone',caption : 'Availability Zone',size : '20%',style : 'text-align:center'}
                      ],
            onSelect : function(event) {
                event.onComplete = function() {
                    $('#deleteBtn').attr('disabled', false);
                    $('#addBtn').attr('disabled', false);
                    
                    var accountId =  w2ui.aws_subnetGrid.get(event.recid).accountId;
                    var subnetId = w2ui.aws_subnetGrid.get(event.recid).subnetId;
                    var region = $("select[name='region']").val();
                    doSearchSubnetDetail(accountId, subnetId, region);
                }
            },
            onUnselect : function(event) {
                event.onComplete = function() {
                    $('#deleteBtn').attr('disabled', true);
                    $('#addBtn').attr('disabled', false);
                    $("#subnetDetailTable td").html("");
                }
            },
            onLoad : function(event) {
                if (event.xhr.status == 403) {
                    location.href = "/abuse";
                    event.preventDefault();
                }
            },
            onError : function(event) {
            }
        });
        
        doSearch();
        /*************************** *****************************
         * 설명 :  AWS Subnet 생성 팝업 화면
         *********************************************************/
        $("#addBtn").click(function(){
            if($("#addBtn").attr('disabled') == "disabled") return;
            w2popup.open({
                title   : "<b>AWS Subnet 생성</b>",
                width   : 650,
                height  : 370,
                modal   : true,
                body    : $("#registPopupDiv").html(),
                buttons : $("#registPopupBtnDiv").html(),
                onOpen : function(event){
                    event.onComplete = function(){
                        getVpcIds();
                    }                   
                },onClose:function(event){
                    initsetting();
                    doSearch();
                    $("#subnetDetailTable td").html("");
                }
            });
        });
    });
    
    /********************************************************
     * 설명 : AWS Subnet 삭제 시 체크 박스에 따른 삭제 버튼 활성화
    *********************************************************/
    function checkCheckbox(record){    
        var result = "";
        result  = "</br>";
        result += "<div style='color:red'>경고: 만약 이 Default 서브넷을 삭제 하시면, 연관된 가용 지역에 서브넷을 생성하시고 인스턴스 런치할 때 서브넷을 지정하지 않는이상, ";
        result += "가용지역 ( "+record.availabilityZone+" ) 에 런치 하실 수 없습니다. </div>";
        
        w2confirm({
                title  : "<b>Subnet 삭제</b>",
                msg : "<span style='color:red'> Subnet (" + record.subnetId + ")을 삭제 후 복구할 수 없습니다.</span><br/>"
                       + result+"</br> <span style='color:red'>정말로 삭제 하시겠습니까?</span> ",
                yes_text : "삭제하겠습니다",
                no_text : "취소",
                height : 350,
                yes_callBack: function(event){
                    deleteAwsSubnetInfo(record);
                },
                no_callBack    : function(){
                    w2ui['aws_subnetGrid'].clear();
                    accountId = record.accountId;
                    doSearch();
                    $("#subnetDetailTable td").html("");
                }
            });
    };
    
    /********************************************************
     * 설명 : AWS Subnet 삭제 버튼 클릭
    *********************************************************/
   $("#deleteBtn").click(function(){
       if($("#deleteBtn").attr('disabled') == "disabled") return;
       var selected = w2ui['aws_subnetGrid'].getSelection();        
       if( selected.length == 0 ){
           w2alert("선택된 정보가 없습니다.", "Subnet 삭제");
           return;
       }
       else {
           var record = w2ui['aws_subnetGrid'].get(selected);
           var result = "";
           result = "</br><div>경고: 만약 이 Default 서브넷을 삭제 하시면, 연관된 가용 지역에 서브넷을 생성하시고 인스턴스 런치할 때 서브넷을 지정하지 않는이상, 가용지역 ( "
                     +record.availabilityZone+
                     " ) 에 런치 하실 수 없습니다. </div>";
           w2confirm({
               title : "Subnet 삭제",
               msg : "Subnet (" + record.subnetId + ")"+ delete_confirm_msg +"<br/>"+ result,
               yes_text : "삭제하겠습니다",
               no_text : "취소",
               height : 350,
               yes_callBack: function(event){
                   w2popup.lock($("#layout_layout_panel_main"),delete_lock_msg, true);
                   checkCheckbox(record);
                   doSearch();
                   $("#subnetDetailTable td").html("");
               },
               no_callBack    : function(){
                   w2ui['aws_subnetGrid'].clear();
                   accountId = record.accountId;
                   doSearch();
                   $("#subnetDetailTable td").html("");
               }
           });
       }
   });

/********************************************************
 * 설명 : AWS Subnet 생성 버튼 클릭
 * 기능 : saveAwsSubnetInfo
 *********************************************************/ 
function saveAwsSubnetInfo(){
     w2popup.lock(save_lock_msg, true);
     var subnetInfo ={
             accountId : $("select[name='accountId']").val()
            ,nameTag: $(".w2ui-msg-body input[name='nameTag']").val()
            ,vpcId: $(".w2ui-msg-body select[name='vpcId']").val()
            ,availabilityZone: $(".w2ui-msg-body select[name='availabilityZone']").val()
            ,region : $("select[name='region']").val()
            ,cidrBlock : $(".w2ui-msg-body input[name='cidrBlock']").val()
     }
     $.ajax({
         type: "POST",
         url : "/awsMgnt/subnet/save",
         contentType: "application/json",
         async : true,
         data: JSON.stringify(subnetInfo),
         success : function(status){
             w2popup.unlock();
             w2popup.close();
             accountId = subnetInfo.accountId;
             doSearch();
             $("#subnetDetailTable td").html("");
         }, error : function(request, status, error){
             w2popup.unlock();
             var errorResult = JSON.parse(request.responseText).message;
             var idx = errorResult.indexOf("(");
             var message = errorResult.substring(0, idx);
             w2alert(message);
         }
     });
 }

/****************************************************
 * 기능 : showVpcDetails
 * 설명 : 해당 Vpc 에 대한 detail 정보
*****************************************************/
function showVpcDetails(vpcId){
     var details="";
     var details2="";
     var vpcId = $(".w2ui-msg-body select[name='vpcId']").val();
     if( checkEmpty(vpcsInfo) || checkEmpty(vpcId) ){
         return;
     }
     
     for( var i=0; i<vpcsInfo.length; i++  ){
         if( vpcsInfo[i].vpcId == vpcId){
             //vpc 선택에  따라 IPv6 CIDR Block 정보 테이블이 있는 경우도 있다.
             if( !checkEmpty(vpcsInfo[i].ipv4CidrBlock) ){
                 details +="<tr><th>CIDR</th><td>" + vpcsInfo[i].ipv4CidrBlock + "</td><th>Status</th><td>" 
                 + vpcsInfo[i].status +"</td></tr>"; 
             }
             if( !checkEmpty(vpcsInfo[i].ipv6CidrBlock)){
                 details2 +="<tr><th>IPv6</th><td>" + vpcsInfo[i].ipv6CidrBlock + "</td><th>Status</th><td>" 
                 +  vpcsInfo[i].status +"</td></tr>"; 
             }
         }
     }
     $(".w2ui-msg-body #vpcDetailTable").html(details+details2);
     w2popup.unlock();
     /////vpc 선택에  따라 IPv6 CIDR Block 정보 input 박스가 생기기도 한다. (기능 추가 필요한지 추후에 고려)
}

/****************************************************
  * 기능 : getVpcIds
  * 설명 :Subnet 생성시 VpcIds 조회 요청
  *****************************************************/
 function getVpcIds() {
     w2popup.lock("VPC "+search_lock_msg, true);
     var accountId = $("select[name=accountId]").val();
     var region = $("select[name='region']").val();
     $.ajax({
         type : "GET",
         url : "/awsMgnt/subnet/save/vpcs/"+accountId+"/"+region,
         contentType : "application/json",
         success : function(data, status) {
             setupVpcIds(data);
             getAvailabilityZones(accountId);
         },
         error : function(request, status, error) {
             var errorResult = JSON.parse(request.responseText);
             w2alert(errorResult.message, "VPC 목록 조회");
         }
     });
 }

 /****************************************************
  * 기능 : setupVpcIds
  * 설명 : Subnet에 대한 Vpc Id설정 
 *****************************************************/
 function setupVpcIds(data){
      var options= "";
      if( data.length == 0 ){
          options +="<option value=''>존재하지 않습니다.</option>";
      }else{
          for (var i=0; i<data.length; i++){
              options +="<option value='"+data[i].vpcId+"'>" + data[i].vpcId+" | "+ data[i].nameTag + "</option>"; 
          } 
      }
      vpcsInfo=data;
      w2popup.unlock();
      $(".w2ui-msg-body select[name='vpcId']").html(options);
 }

 /****************************************************
  * 기능 : getAvailabilityZones
  * 설명 : AvailabilityZone 조회
 *****************************************************/
 function getAvailabilityZones(accountId){
     w2popup.lock("가용 영역 "+search_lock_msg, true);
     var region = $("select[name='region']").val();
     $.ajax({
         type : "GET",
         url : "/common/aws/avaliabilityzone/list/"+accountId+"/"+region+"",
         contentType : "application/json",
         success : function(data, status) {
             setupAvailabilityZones(data);
         },
         error : function(request, status, error) {
             var errorResult = JSON.parse(request.responseText);
             w2alert(errorResult.message, "VPC 목록 조회");
         }
     });
 }

 /****************************************************
  * 기능 : setupAvailabilityZones
  * 설명 : 해당 Subnet에 대한 AvailabilityZone설정
 *****************************************************/
 function setupAvailabilityZones(data){
      var options= "";
      if( data.length == 0 ){
          options +="<option>존재하지 않습니다.</option>";
      }else{
          for (var i=0; i<data.length; i++){
              options +="<option value='"+data[i]+"'>" + data[i] + "</option>"; 
          } 
      }
      w2popup.unlock();
      $(".w2ui-msg-body select[name='availabilityZone']").html(options);
      showVpcDetails();
 }

 /********************************************************
  * 설명 : AWS Subnet 목록 조회 Function 
  * 기능 : doSearch
  *********************************************************/
 function doSearch() {
     region = $("select[name='region']").val();
     if(region == null) region = "us-west-2";
     if(accountId != "")
     w2ui['aws_subnetGrid'].load('/awsMgnt/subnet/list/'+accountId+'/'+region+'');
     doButtonStyle();
     
 }

 /********************************************************
  * 설명 : AWS Subnet 정보 상세 조회 Function 
  * 기능 : doSearchSubnetDetail
  *********************************************************/
 function doSearchSubnetDetail(accountId, subnetId, region){
      w2utils.lock($("#layout_layout_panel_main"), detail_subnet_lock_msg, true);
      $.ajax({
          type : "GET",
          url : "/awsMgnt/subnet/save/detail/"+accountId+"/"+subnetId+"/"+region+"",
          contentType : "application/json",
          success : function(data, status) {
              w2utils.unlock($("#layout_layout_panel_main"));
              if(data != null){
                  $(".subnetId").html(data.subnetId+"");
                  $(".state").html(data.state+"");
                  $(".cidrBlock").html(data.cidrBlock+"");
                  //ipv6CidrBlock 값 없을때 "-" 적용 안됨 .. (UX)
                  if(data.ipv6CidrBlock == null){
                      $(".ipv6Cidr").html("-");
                  }else{
                      $(".ipv6Cidr").html(data.ipv6CidrBlock+"");
                  }
                  $(".availabilityZone").html(data.availabilityZone+"");
                  $(".routeTable").html(data.routeTable+"");
                  $(".networkAcl").html(data.networkAcl+"");
                  $(".vpcId").html(data.vpcId+"");
                  
                  if(data.defaultForAz == true){
                      $(".defaultSubnet").html("Yes");
                  }else{
                      $(".defaultSubnet").html("No");
                  }

                  if(data.autoAssignPublicIp == true){
                      $(".autoAssignPublicIp").html("Yes");
                  }else{
                      $(".autoAssignPublicIp").html("No");
                  }

                  if(data.autoAssignAddress == true){
                      $(".autoAssignAddress").html("Yes");
                  }else{
                      $(".autoAssignAddress").html("No");
                  }
                  $(".availableIps").html(data.availableIpAddressCount+"");
              } 
              return;
          }, error : function(request, status, error) {
              w2utils.unlock($("#layout_layout_panel_main"));
              var errorResult = JSON.parse(request.responseText);
              w2alert(errorResult.message, "Subnet 상세 정보");
          } 
      });
 }

 /********************************************************
  * 설명 : AWS Subnet 삭제
  * 기능 : deleteAwsSubnetInfo
  *********************************************************/
 function deleteAwsSubnetInfo(record){
      w2popup.lock( delete_lock_msg, true );
     var subnetInfo = {
             accountId : record.accountId,
             region : $("select[name='region']").val(),
             subnetId : record.subnetId
     }
     $.ajax({
         type : "DELETE",
         url : "/awsMgnt/subnet/delete",
         contentType : "application/json",
         async : true,
         data : JSON.stringify(subnetInfo),
         success : function(status) {
             w2popup.unlock();
             w2popup.close();
             accountId = subnetInfo.accountId;
             w2ui['aws_subnetGrid'].clear();
             doSearch();
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
 }

 /********************************************************
  * 기능 : initsetting
  * 설명 : 기본 설정값 초기화
 *********************************************************/
 function initsetting(){
      bDefaultAccount="";
      vpcsInfo="";
      w2ui['aws_subnetGrid'].clear();
      $("#subnetDetailTable td").html("");
      $('#addBtn').attr('disabled', false);
      doSearch();
 }

 /********************************************************
  * 기능 : clearMainPage
  * 설명 : 다른페이지 이동시 호출
 *********************************************************/
 function clearMainPage() {
     $().w2destroy('aws_subnetGrid');
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
.trTitle {  background-color: #f3f6fa; width: 180px; }
td {  width: 280px; }
</style>
<div id="main">
    <div class="page_site pdt20">인프라 관리 > AWS 관리 > <strong>AWS Subnet 관리 </strong></div>
    <div id="awsMgnt" class="pdt20">
        <ul>
            <li>
                <label style="font-size: 14px">AWS 관리 화면</label> &nbsp;&nbsp;&nbsp; 
                <div class="dropdown" style="display:inline-block;">
                    <a href="#" class="dropdown-toggle iaas-dropdown" data-toggle="dropdown" aria-expanded="false">
                        &nbsp;&nbsp;Subnet 관리<b class="caret"></b>
                    </a>
                    <ul class="dropdown-menu alert-dropdown">
                         <sec:authorize access="hasAuthority('AWS_VPC_MENU')">
                            <li><a href="javascript:goPage('<c:url value="/awsMgnt/vpc"/>', 'AWS VPC');">VPC 관리</a></li>
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
        <div class="title fl">AWS Subnet 목록</div>
        <div class="fr">
         <sec:authorize access="hasAuthority('AWS_SUBNET_CREATE')">
            <span id="addBtn" class="btn btn-primary" style="width: 120px">생성</span>
         </sec:authorize>
         <sec:authorize access="hasAuthority('AWS_SUBNET_DELETE')">
            <span id="deleteBtn" class="btn btn-danger" style="width: 120px">삭제</span>
         </sec:authorize>
        </div>
    </div>
    <div id="aws_subnetGrid" style="width: 100%; height: 405px;"></div>

    <div class="pdt20">
        <div class="title fl">AWS Subnet 상세 목록</div>
    </div>

    <div id="aws_subnetDetailGrid"
        style="width: 100%; height: 128px; margin-top: 50px; border-top: 2px solid #c5c5c5;">
        <table id="subnetDetailTable"
            class="table table-condensed table-hover">
            <tr>
                <th class="trTitle">Subnet ID</th>
                <td class="subnetId"></td>
                <th class="trTitle">State</th>
                <td class="state"></td>
                <th class="trTitle">IPv4 CIDR</th>
                <td class="cidrBlock"></td>
            </tr>
            <tr>
                <th class="trTitle">IPv6 CIDR</th>
                <td class="ipv6Cidr"></td>
                <th class="trTitle">Availability Zone</th>
                <td class="availabilityZone"></td>
                <th class="trTitle">Route table</th>
                <td class="routeTable"></td>
            </tr>
            <tr>
                <th class="trTitle">Network ACL</th>
                <td class="networkAcl"></td>
                <th class="trTitle">VPC</th>
                <td class="vpcId"></td>
                <th class="trTitle">Default subnet</tH>
                <td class="defaultSubnet"></td>
            </tr>
            <tr style="border-bottom: 1px solid #ddd;">
                <th class="trTitle">Auto-assign Public IP</th>
                <td class="autoAssignPublicIp"></td>
                <th class="trTitle">Auto Assign IPv6 address</th>
                <td class="autoAssignAddress"></td>
                <th class="trTitle">Available IPs</th>
                <td class="availableIps"></td>
            </tr>
        </table>
    </div>
</div>

<!-- AWS Subnet 등록  팝업 Div-->
<div id="registPopupDiv" hidden="true">
    <form id="awsSubnetForm" action="POST" style="padding:5px 0 5px 0;margin:0;">
        <div class="panel panel-info"> 
            <div class="panel-heading"><b>AWS Subnet 정보</b></div>
            <div class="panel-body" style="padding:10px 10px; height:230px; overflow-y:auto;">
                <input type="hidden" name="accountId" />
                
                <div class="w2ui-field">
                    <label style="width:36%;text-align: left; padding-left: 20px;">Name Tag</label>
                    <div>
                        <input name="nameTag" type="text"  maxlength="100" style="width: 300px" placeholder="Name Tag"/>
                    </div>
                </div>
                
                <div class="w2ui-field">
                    <label style="width: 36%; text-align: left; padding-left: 20px;">VPC</label>
                    <div>
                       <select name="vpcId" onchange="showVpcDetails();" style="width:300px">
                           <option value="">VPC를 선택하세요.</option>
                       </select>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:36%;text-align: left; padding-left: 20px;">VPC CIDRs</label>
                    <div style="width:250px; height:65px; ">
                    <table id="vpcDetailTable" class="table table-condensed table-hover">
                        <tr>
                            <th class="trTitle">CIDR</th>
                            <td class="cidrBlock"></td>
                            <th class="trTitle">Status</th>
                            <td class="state"></td>
                        </tr>
                     </table>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:36%;text-align: left; padding-left: 20px;">Availability Zone</label>
                    <div>
                        <select name="availabilityZone" style="width:300px">
                           <option>가용 지역를 선택하세요.</option>
                       </select>
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="width:36%;text-align: left; padding-left: 20px;">IPv4 CIDR block</label>
                    <div>
                        <input name="cidrBlock" type="text" maxlength="100" style="width: 300px" placeholder="IPv4 CIDR block을 입력하세요."/>
                    </div>
                </div>
                
            </div>
        </div>
    </form> 
</div>
<div id="registPopupBtnDiv" hidden="true">
     <button class="btn" id="registBtn" onclick="$('#awsSubnetForm').submit();">확인</button>
     <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
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
    
    $("#awsSubnetForm").validate({
        ignore : "",
        onfocusout: true,
        rules: {
            cidrBlock: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='cidrBlock']").val() );
                }, ipv4Range : function(){
                    return $(".w2ui-msg-body input[name='cidrBlock']").val();
                }
            }
        }, messages: {
             cidrBlock: { 
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
            saveAwsSubnetInfo();
        }
    });
});

</script>
