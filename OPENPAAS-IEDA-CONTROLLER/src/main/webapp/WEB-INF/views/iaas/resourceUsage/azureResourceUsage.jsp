<%
/* =================================================================
 * 작성일 : 2018.03.09
 * 작성자 : 
 * 상세설명 : Azure 리소스 사용량 조회 화면
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix = "spring" uri = "http://www.springframework.org/tags" %>
<script type="text/javascript">
var search_lock_msg = '<spring:message code="common.search.data.lock"/>';//데이터 조회 중 입니다.

$(function() {
    doSearch();
});

function doSearch(){
    getIAzureResourceUsageInfo();
}


/****************************************************
 * 기능 : getIAzureResourceUsageInfo
 * 설명 : Azure 리소스 조회 요청
*****************************************************/
function getIAzureResourceUsageInfo(){
     w2utils.lock($("#layout_layout_panel_main"), search_lock_msg, true);
    $.ajax({
        type : "GET",
        url : "/iaasMgnt/resourceUsage/azure/list",
        contentType : "application/json",
        success : function(data, status) {
            settingResourceUsageData(data);
        },
        error : function(request, status, error) {
            w2utils.unlock("#layout_layout_panel_main");
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message, "인프라 전체 리소스 사용 정보 조회");
            $(".panel-body .instanceUsage").html("<p>Azure 인스턴스 사용량 정보가 존재하지 않습니다.</p>");
            $(".panel-body .networkUsage").html("<p>Azure 네트워크 사용량 정보가 존재하지 않습니다.</p>");
            $(".panel-body .volumeUsage").html("<p>Azure 볼륨 사용량 정보가 존재하지 않습니다.</p>");
            
        }
    });
}

/****************************************************
 * 기능 : settingResourceUsageData
 * 설명 : Azure 리소스 조회 데이터 설정
*****************************************************/
function settingResourceUsageData(data){
    var instance = "";
    var network = "";
    var volume = "";
    var billing = "";
    var totalInstance =0;
    var totalNetwork=0;
    var totalVolume=0;
    var totalBilling=0;
    var percentage= 0;
    
    for( var i=0; i<data.length; i++  ){
        totalInstance += data[i].instance;
        totalNetwork += data[i].network;
        totalVolume += data[i].volume;
        totalBilling += data[i].billing;
    }
    //인스턴스 사용량 데이터 설정
    for( var i=0; i<data.length; i++ ){
        if( data[i].instance != 0 ) percentage = data[i].instance/totalInstance * 100;
        
        instance += "<div>";
        instance += "<span class='account-sp'>계정 "+data[i].accountName+"</span>";
        instance += "<div class='progress'>";
        instance += "<div class='progress-bar progress-bar-info' role='progressbar' aria-valuemin='0' aria-valuenow='"+percentage+"'aria-valuemax='100' style='width:"+percentage+"%;'>";
        instance += data[i].instance+"VM(s)";
        instance += "</div></div></div>";
        
        if( data[i].network != 0 ){
            percentage = data[i].network/totalNetwork * 100;
        }else{
            percentage = 0;
        }
        network += "<div>";
        network += "<span class='account-sp'>계정 "+data[i].accountName+"</span>";
        network += "<div class='progress'>";
        network += "<div class='progress-bar progress-bar-warning' role='progressbar' aria-valuemin='0' aria-valuenow='"+percentage+"'aria-valuemax='100' style='width:"+percentage+"%;'>";
        network += data[i].network+"개";
        network += "</div></div></div>";
        
        if( data[i].volume != 0 ){
            percentage = data[i].volume/totalVolume * 100;
        }else{
            percentage=0;
        }
        volume += "<div>";
        volume += "<span class='account-sp'>계정 "+data[i].accountName+"</span>";
        volume += "<div class='progress'>";
        volume += "<div class='progress-bar progress-bar-success' role='progressbar' aria-valuemin='0' aria-valuenow='"+percentage+"'aria-valuemax='100' style='width:"+percentage+"%;'>";
        volume += bytesToSize(data[i].volume);
        volume += "</div></div></div>";
        
        if( data[i].billing != 0 ){
            percentage = data[i].billing/totalBilling * 100;
        }else{
            percentage=0;
        }
        billing += "<div>";
        billing += "<span class='account-sp'>계정 "+data[i].accountName+"</span>";
        billing += "<div class='progress'>";
        billing += "<div class='progress-bar progress-bar-success' role='progressbar' aria-valuemin='0' aria-valuenow='"+percentage+"'aria-valuemax='100' style='width:"+percentage+"%;'>";
        billing += data[i].billing;
        billing += "</div></div></div>";
        
    }
    $("#instanceUsageWrap .instanceUsage").html(instance);
    $("#instanceUsageWrap .instanceSummary").html(totalInstance + "<span>VM(s)</span>");
    $("#networkUsageWrap .networkUsage").html(network);
    $("#networkUsageWrap .networkSummary").html(totalNetwork + "<span>개</span>");
    $("#volumeUsageWrap .volumeUsage").html(volume);
    $("#billingUsageWrap .billingUsage").html(billing);
    $("#billingUsageWrap .billingSummary").html(totalBilling + "<span>USD</span>");
    totalVolume = bytesToSize(totalVolume);
    var index = totalVolume.indexOf("B");
    var idx2 = 0;
    var bytes = "";
    if( index > 0 ){
        if( totalVolume.indexOf("Byte") > 0 ){
            idx2 = totalVolume.indexOf("Byte");
            bytes = totalVolume.substring(idx2);
            totalVolume = totalVolume.substring(0, idx2 );
        }else{
            idx2 = totalVolume.indexOf("B");
            bytes = totalVolume.substring(idx2-1);
            totalVolume = totalVolume.substring(0, idx2-2);
        }
    }else{ totalVolume = 0; }
    $("#volumeUsageWrap .volumeSummary").html(totalVolume + "<span>"+bytes+"</span>");
    w2utils.unlock($("#layout_layout_panel_main"));
}

//화면 리사이즈시 호출
$( window ).resize(function() {
    setLayoutContainerHeight();
});
</script>

<div id="main">
    <div class="pdt20">
        <div class="fl" >
            <div class="dropdown" style="display:inline-block;">
                <a href="#" class="dropdown-toggle iaas-dropdown" data-toggle="dropdown" aria-expanded="false">
                    <i class="fa fa-cloud"></i>&nbsp;Azure<b class="caret"></b>
                </a>
                <ul class="dropdown-menu alert-dropdown">
                 <sec:authorize access="hasAuthority('AWS_RESOURCE_USAGE_MENU')">
                        <li><a href="javascript:goPage('<c:url value="/iaasMgnt/resourceUsage/aws"/>', 'AWS 리소스 사용량');">AWS</a></li>
                    </sec:authorize>
                    <sec:authorize access="hasAuthority('OPENSTACK_RESOURCE_USAGE_MENU')">
                        <li><a href="javascript:goPage('<c:url value="/iaasMgnt/resourceUsage/openstack"/>', 'OPENSTACK 리소스 사용량');">Openstack</a></li>
                    </sec:authorize>
                </ul>
            </div>
        </div> 
    </div>
    <div class="pdt20" style="width:100%; float:left;">
        <div class="title fl">Azure 인스턴스 사용량 정보</div>
        <div id="instanceUsageWrap" style="float:left; width:100%">
             <div class="panel-group">
                <div class="panel panel-default">
                    <div class="panel-heading" style="position:relative"><b>Azure 인스턴스 사용량</b><img src='<c:url value="images/iaasMgnt/month-white-icon.png"/>' style="width:107px;position:absolute; right:20px;" alt="CLOCK" /></div>
                   <div class="panel-body">
                        <div class="instanceUsage"></div>
                        <div style="float:left; width:25%; padding:15px;">
                            <p class='summary-st'>Total</p>
                            <p class="instanceSummary"></p>
                        </div>
                   </div>
                </div>
             </div>
        </div>
    </div>
    <div class="pdt20">
        <div class="title fl">Azure 네트워크 사용량 정보</div>
        <div id="networkUsageWrap" style="float:left; width:100%">
             <div class="panel-group">
                <div class="panel panel-default" style="position:relative;">
                    <div class="panel-heading" style="position:relative"><b>Azure 네트워크 사용량</b>(기준: Virtual Networks) <img src='<c:url value="images/iaasMgnt/month-white-icon.png"/>' style="width:107px;position:absolute; right:20px;" alt="CLOCK" /></div>
                   <div class="panel-body">
                        <div class="networkUsage"></div>
                        <div style="float:left; width:25%; padding:15px;">
                            <p class='summary-st'>Total</p>
                            <p class="networkSummary"></p>
                        </div>
                   </div>
                </div>
             </div>
        </div>
    </div>
    <div class="pdt20">
        <div class="title fl">Azure 볼륨 사용량 정보</div>
        <div id="volumeUsageWrap" style="float:left; width:100%">
             <div class="panel-group">
                <div class="panel panel-default">
                    <div class="panel-heading" style="position:relative"><b>Azure 볼륨 사용량</b><img src='<c:url value="images/iaasMgnt/month-white-icon.png"/>' style="width:107px;position:absolute; right:20px;" alt="CLOCK" /></div>
                   <div class="panel-body">
                        <div class="volumeUsage"></div>
                        <div style="float:left; width:25%; padding:15px;">
                            <p class='summary-st'>Total</p>
                            <p class="volumeSummary"></p>
                        </div>
                   </div>
                </div>
             </div>
        </div>
    </div>
    <div class="pdt20">
        <div class="title fl">Azure 과금 사용량 정보</div>
        <div id="billingUsageWrap" style="float:left; width:100%">
             <div class="panel-group">
                <div class="panel panel-default">
                    <div class="panel-heading" style="position:relative"><b>Azure 과금 사용량</b><img src='<c:url value="images/iaasMgnt/month-white-icon.png"/>' style="width:107px;position:absolute; right:20px;" alt="CLOCK" /></div>
                   <div class="panel-body">
                        <div class="billingUsage"></div>
                        <div style="float:left; width:25%; padding:15px;">
                            <p class='summary-st'>Total</p>
                            <p class="billingSummary"></p>
                        </div>
                   </div>
                </div>
             </div>
        </div>
    </div>
</div>