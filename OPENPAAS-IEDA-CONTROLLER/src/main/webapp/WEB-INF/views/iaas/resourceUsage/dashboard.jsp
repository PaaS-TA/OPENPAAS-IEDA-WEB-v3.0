<%
/* =================================================================
 * 작성일 : 2017.05.02
 * 작성자 : Ji,Hyangeun
 * 상세설명 : 대시보드 화면(리소스 사용량 정보)
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix = "spring" uri = "http://www.springframework.org/tags" %>
<style>
</style>
<script type="text/javascript">
var search_lock_msg = '<spring:message code="common.search.data.lock"/>';//데이터 조회 중 입니다.
$(function() {
    getInfraAllResourceUsageInfo();
});


/****************************************************
 * 기능 : getInfraAllResourceUsageInfo
 * 설명 : 인프라 전체 리소스 조회 요청
*****************************************************/
function getInfraAllResourceUsageInfo(){
     w2utils.lock($("#layout_layout_panel_main"), search_lock_msg, true);
    $.ajax({
        type : "GET",
        url : "/iaasMgnt/resourceUsage/all/list",
        contentType : "application/json",
        success : function(data, status) {
            settingResourceUsageData(data);
            w2utils.unlock($("#layout_layout_panel_main"));
        },
        error : function(request, status, error) {
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "인프라 전체 리소스 사용 정보 조회");
            w2utils.unlock($("#layout_layout_panel_main"));
        }
    });
    
}

function setHoverStyle(event){
    $(event).css("backgroundColor", "#b4b4b4").css("transparent", "0.8");
    $(event).children().css("display", "none");
    $(event).append("<p style='position:relative; top:50%; left:20px;color:white'>AWS 리소스 사용량 조회 화면으로 이동</p>")
}

function setMouseOutStyle(event){
    $(event).css("backgroundColor", "white").css("opacity", "100");
    $(event).children().css("display", "block");
    $(event).find("p").remove();
}

/****************************************************
 * 기능 : settingResourceUsageData
 * 설명 : 인프라 전체 리소스 조회 데이터 설정
*****************************************************/
function settingResourceUsageData(data){
    var awsInstance=0; var awsNetwork =0; var awsVolume =0; var awsBilling=0;
    var openstackInstance=0; var openstackNetwork=0; var openstackVolume=0;
    var azureInstance=0; var azureNetwork =0; var azureVolume =0; var azureBilling=0;
    for( var i=0; i < data.length; i++ ){
          if( (data[i].iaasType).toUpperCase() == 'AWS' ){
              awsInstance +=  data[i].instance;
              awsNetwork += data[i].network;
              awsVolume += data[i].volume;
              awsBilling += data[i].billing;
              $("."+data[i].iaasType.toLowerCase()+"-instance").html( awsInstance +" VM(s)" );
              $("."+data[i].iaasType.toLowerCase()+"-network").html( awsNetwork + " 개" );
              $("."+data[i].iaasType.toLowerCase()+"-volume").html( bytesToSize(awsVolume) ); 
              $("."+data[i].iaasType.toLowerCase()+"-billing").html( awsBilling + "USD" ); 
          }else if( (data[i].iaasType).toUpperCase() == 'OPENSTACK' ){
              openstackInstance +=  data[i].instance;
              openstackNetwork += data[i].network;
              openstackVolume += data[i].volume;
              $("."+data[i].iaasType.toLowerCase()+"-instance").html( openstackInstance +" VM(s)" );
              $("."+data[i].iaasType.toLowerCase()+"-network").html( openstackNetwork + " 개" );
              $("."+data[i].iaasType.toLowerCase()+"-volume").html( openstackVolume + "GB" );
          }else if( (data[i].iaasType).toUpperCase() == 'AZURE' ){
              azureInstance +=  data[i].instance;
              azureNetwork += data[i].network;
              azureVolume += data[i].volume;
              azureBilling += data[i].billing;
              $("."+data[i].iaasType.toLowerCase()+"-instance").html( azureInstance +" VM(s)" );
              $("."+data[i].iaasType.toLowerCase()+"-network").html( azureNetwork + " 개" );
              $("."+data[i].iaasType.toLowerCase()+"-volume").html( bytesToSize(azureVolume) );
              $("."+data[i].iaasType.toLowerCase()+"-billing").html( azureBilling + "USD" ); 
          }else{
              
          }
    }
    $(".totalInstance").html( awsInstance + openstackInstance + azureInstance);
    $(".totalNetwork").html( awsNetwork + openstackNetwork + azureNetwork);
    var totalVolume = bytesToSize(awsVolume + gbConverter(openstackVolume) + azureVolume);
    var idx2 = 0;
    var bytes = "";
       if( totalVolume.indexOf("Byte") > 0 ){
           idx2 = totalVolume.indexOf("Byte");
           bytes = totalVolume.substring(idx2);
           totalVolume = totalVolume.substring(0, idx2 );
       }else{
           idx2 = totalVolume.indexOf("B");
           bytes = totalVolume.substring(idx2-1);
           totalVolume = totalVolume.substring(0, idx2-2);
       }
    $(".totalVolume").html( totalVolume );
    $(".totalVolume_size").html( bytes );
}

function clearMainPage() {
    w2utils.unlock($("#layout_layout_panel_main"));
}

//화면 리사이즈시 호출
$( window ).resize(function() {
    setLayoutContainerHeight();
});
</script>

<div id="main">
    <div class="pdt20">
        <div class="title fl">인프라 전체 리소스 사용량</div>
        <div class="iaasTotalResourceUsageDiv" >
            <ul>
                <li>
                    <ul class="totalResourceUsage-ul-Div">
                        <li><img src='<c:url value="images/iaasMgnt/instance.png" />' class="instance-icon" alt="인스턴스"><span></span></li>
                        <li><span class="totalInstance">0</span><span style="font-size:22px;"> VM(s)</span></li>
                    </ul>
                    
                </li>
                <li>
                    <ul class="totalResourceUsage-ul-Div" >
                        <li><img src='<c:url value="images/iaasMgnt/network.png" />' class="instance-icon" alt="네트워크"><span></span></li>
                        <li><span class="totalNetwork">0</span><span style="font-size:22px;"> Network(s)</span></li>
                    </ul>
                </li>
                <li>
                    <ul class="totalResourceUsage-ul-Div">
                        <li><img src='<c:url value="images/iaasMgnt/volume.png" />' style="" class="instance-icon" alt="볼륨"><span></span></li>
                        <li><span class="totalVolume">0</span><span class="totalVolume_size" style="font-size:22px;"> Byte</span></li>
                    </ul>
                </li>
            </ul>
        </div>
    </div>
    
    <div class="pdt40" style="float:left">
        <div class="title fl">인프라 리소스 사용량</div>
        <div id="iaasResourceUsageWrap">
            <div class="iaasResourceUsageDiv" onclick="javascript:goPage('<c:url value="/iaasMgnt/resourceUsage/aws"/>', 'AWS 리소스 사용량 조회');">
                <ul>
                    <li>
                        <ul style="margin-top:59px">
                            <li style="margin-bottom:22px;"><img src='<c:url value="images/iaasMgnt/aws-icon.png"/>' style="width:254px;" class="aws-icon" alt="AWS"><span></span></li>
                            <li>>> AWS 상세 화면 이동</li>
                        </ul>
                    </li>
                    <li class="resource-li"><span class="resource-li-span1">인스턴스 : </span><span class="resource-li-span2 aws-instance">0 VM(s)</span></li>
                    <li class="resource-li network-li"><span class="resource-li-span1">네트워크 : </span><span class="resource-li-span2 aws-network">0 개</span><span class="resource-li-span3">(기준: US-WEST-2)</span></li>
                    <li class="resource-li"><span class="resource-li-span1">볼륨 : </span><span class="resource-li-span2 aws-volume">0 Byte</span></li>
                    <li class="resource-li"><span class="resource-li-span1">과금 : </span><span class="resource-li-span2 aws-billing">0 USD</span></li>
                </ul>
            </div>
            <div class="iaasResourceUsageDiv" style="margin-right:0;" onclick="javascript:goPage('<c:url value="/iaasMgnt/resourceUsage/openstack"/>', 'Openstack 리소스 사용량 조회');">
                <ul>
                    <li>
                        <ul style="margin-top:51px">
                            <li style="margin-bottom:18px;"><img src='<c:url value="images/iaasMgnt/openstack-icon.png"/>' class="aws-icon" alt="Openstack"><span></span></li>
                            <li>>> Openstack 상세 화면 이동</li>
                        </ul>
                    </li>
                    <li class="resource-li"><span class="resource-li-span1">인스턴스 : </span><span class="resource-li-span2 openstack-instance">0 VM(s)</span></li>
                    <li class="resource-li network-li"><span class="resource-li-span1">네트워크 : </span><span class="resource-li-span2 openstack-network">0 개</span><span class="resource-li-span3"></span></li>
                    <li class="resource-li"><span class="resource-li-span1">볼륨 : </span><span class="resource-li-span2 openstack-volume">0 Byte</span></li>
                </ul>
            </div>
        
            <div class="iaasResourceUsageDiv" onclick="javascript:goPage('<c:url value="/iaasMgnt/resourceUsage/azure"/>', 'Azure 리소스 사용량 조회');">
                <ul>
                    <li>
                        <ul style="margin-top:-30px">
                            <li style="margin-bottom:-25px;"><img src='<c:url value="images/iaasMgnt/azure-icon.png"/>' class="azure-icon" alt="Azure"><span></span></li>
                            <li>>> Azure 상세 화면 이동</li>
                        </ul>
                    </li>
                    <li class="resource-li"><span class="resource-li-span1">인스턴스 : </span><span class="resource-li-span2 azure-instance">0 VM(s)</span></li>
                    <li class="resource-li network-li"><span class="resource-li-span1">네트워크 : </span><span class="resource-li-span2 azure-network">0 개</span><span class="resource-li-span3"></span></li>
                    <li class="resource-li"><span class="resource-li-span1">볼륨 : </span><span class="resource-li-span2 azure-volume">0 Byte</span></li>
                    <li class="resource-li"><span class="resource-li-span1">과금 : </span><span class="resource-li-span2 azure-billing">0 USD</span></li>
                </ul>
            </div>
        
        </div>
    </div>
</div>