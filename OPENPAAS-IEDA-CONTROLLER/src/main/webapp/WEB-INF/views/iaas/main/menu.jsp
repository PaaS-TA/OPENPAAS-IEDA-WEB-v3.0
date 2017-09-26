<%
/* =================================================================
 * 작성일 : 2017.05.02
 * 작성자 : Ji,Hyangeun
 * 상세설명 : 메뉴 화면
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>  
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<script type="text/javascript">

 function goPage(page, title) {
 	if (typeof(window["clearMainPage"]) == "function") {
		clearMainPage();
	}
 	$.get(page, function (data, status, xhr) {
		w2ui['layout'].content('main', xhr.responseText);
		w2ui['layout'].resize();
		if (window.navigator.userAgent.indexOf('MSIE') != -1)
			setTimeout(function () { w2ui['layout'].resize(); }, 100);
		
    }).fail(function(xhr, status) {
    });
}	 

</script>

<!-- 왼쪽 메뉴 -->
<div id="left">
	<div class="collapse navbar-collapse navbar-ex1-collapse">
	    <ul class="nav navbar-nav side-nav">
	       <li class="active"><a href="javascript:goPage('<c:url value="/iaasMgnt/main/dashboard"/>', 'Dashboard');" style="font-weight:600; font-size:20px;"><i class="fa fa-fw fa-home"></i> Dashboard</a></li>
	       
	       <sec:authorize access="hasAuthority('IAAS_ACCOUNT_MENU')">
	           <li><a href="javascript:goPage('<c:url value="/iaasMgnt/account"/>', 'ACCOUNT');"><i class="fa fa-fw fa-user"></i> 계정 관리</a></li>
	       </sec:authorize>
	       
	        <sec:authorize access="hasAuthority('IAAS_MANAGEMENT_MENU')">
	           <li>
	               <a href="javascript:;" data-toggle="collapse" data-target="#demo" ><i class="fa fa-fw fa-cloud"></i> 인프라 관리 <i class="fa fa-fw fa-caret-down"></i></a>
		           <ul id="demo" class="collapse">
		               <li><a href="javascript:goPage('<c:url value="/iaasMgnt/aws"/>', 'AWS MANAGEMENT');">AWS 관리</a></li>
		               <li><a href="javascript:goPage('<c:url value="/iaasMgnt/openstack"/>', 'Openstack MANAGEMENT');">Openstack 관리</a></li>
		            </ul>
		        </li>
	        </sec:authorize>
	    </ul>
	</div>
</div>
<!-- //왼쪽 메뉴 끝-->