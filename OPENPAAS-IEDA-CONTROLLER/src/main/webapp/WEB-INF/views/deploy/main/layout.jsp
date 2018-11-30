<%
/* =================================================================
 * 작성일 : 
 * 작성자 : 
 * 상세설명 : layout 화면(top/menu/main)
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
<meta http-equiv="Cache-Control" content="no-cache" /> 
<meta http-equiv="Expires" content="0" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" /> 
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>플랫폼 설치 자동화</title>

<!-- CSS  -->
<link rel="stylesheet" type="text/css" href="<c:url value='/webjars/bootstrap/3.3.5/css/bootstrap.min.css'/>"/>
<link rel="stylesheet" type="text/css" href="<c:url value='/webjars/w2ui/1.4.2/w2ui.min.css'/>"/>
<%-- <link rel="stylesheet" type="text/css" href="<c:url value='/webjars/jquery-ui/1.11.4/jquery-ui.css'/>"/> --%>

<!-- Custom Fonts -->
<link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css?ver=1'/>"/>
<link rel="stylesheet" type="text/css" href="<c:url value='/css/guide.css?ver=1'/>"/>
<link rel="stylesheet" type="text/css" href="<c:url value='/css/deploy-common.css?ver=11'/>"/>
<link rel="stylesheet" type="text/css" href="<c:url value='/css/deploy-content.css?ver=12'/>"/>
<link rel="stylesheet" type="text/css" href="<c:url value='/css/progress-step.css?ver=11'/>"/> <!-- progress-step css -->
<link rel="stylesheet" type="text/css" href="<c:url value='/css/infra-guide.css?ver=12'/>"/>
<link rel="stylesheet" type="text/css" href="<c:url value='/css/infra-init.css?ver=11'/>"/>
<!-- JAVA SCRIPT -->
<!-- JQuery -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>

<%-- <script type="text/javascript" src="<c:url value='/webjars/jquery-ui/1.11.4/jquery-ui.js'/>"></script> --%>
<%-- <script type="text/javascript" src="<c:url value='/webjars/jquery-form/3.51/jquery.form.js'/>"></script> --%>
<%-- <script type="text/javascript" src="<c:url value='/webjars/jquery/2.1.1/jquery.min.js'/>"></script> --%>

<!-- validation -->
<script src="//code.jquery.com/jquery-1.9.1.js"></script>
<script src="//ajax.aspnetcdn.com/ajax/jquery.validate/1.9/jquery.validate.min.js"> </script>
<script src="//ajax.aspnetcdn.com/ajax/jquery.validate/1.9/additional-methods.min.js"> </script>

<!-- bootstrap & W2UIscript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script type="text/javascript" src="<c:url value='/webjars/w2ui/1.4.2/w2ui.min.js'/>"></script>

<!-- ETC JS -->
<script type="text/javascript" src="<c:url value='/js/sockjs-0.3.4.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/stomp.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/yaml.js'/>"></script>
<!-- Common -->
<script type="text/javascript" src="<c:url value='/js/common.js?ver=7'/>"></script>
<script type="text/javascript" src="<c:url value='/js/common-deploy.js?ver=1'/>"></script>

<script type="text/javascript">
(function($) {
    $.ajaxSetup({
        error: function(xhr, status, err) {
            if (xhr.status == 403) {
                   location.href="/abuse";
            }
        }
    });
})(jQuery);

$(function() {
	var pstyle = 'background-color: #edf0ef; overflow-y: hidden;';
    $('#layout').w2layout({
        name: 'layout',
        panels: [
             { type: 'top', style: pstyle, size: 71}
             ,{ type: 'left', style: pstyle, size:256}
            ,{ type: 'main', style: pstyle, size:1599}
        ],  onError: function(event) {
        }        , onResize : function(event) {
            $(".w2ui-panel-content").css("overflow-y", "auto");
        }
    });
    
    setLayoutContainerHeight();
        w2ui['layout'].load('top', 'top');
        w2ui['layout'].load('left', 'menu');
        w2ui['layout'].load('main', 'main/dashboard');
});

function setLayoutContainerHeight(login){
	var layoutHeight = $(window).height()-60;
    var layoutWidth = $(window).width();
    
    $('#wrap1').height(layoutHeight);
    if(login=="login"){
        setLayout = true;
        w2ui['layout'].destroy();
        location.href="/login?code=abuse";
    }else{
        w2ui['layout'].resize();
    }
}
</script>
<style>
    .w2ui-popup .w2ui-popup-message{ z-index:1500; }
</style>
</head>
<body>

<div id="wrap1">
    <div id="layout" class="fullBox navbar navbar-inverse navbar-fixed-top"></div>
</div>
</body>
</html>