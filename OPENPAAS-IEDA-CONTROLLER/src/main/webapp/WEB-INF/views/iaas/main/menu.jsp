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
 function goWindow(page, title) {
     window.open(page, title);
 }
 
</script>

<!-- 왼쪽 메뉴 -->
<div id="left">
    <div class="collapse navbar-collapse navbar-ex1-collapse">
        <ul class="nav navbar-nav side-nav">
           <li class="active"><a href="javascript:goPage('<c:url value="/iaasMgnt/main/dashboard"/>', 'Dashboard');" style="font-weight:600; font-size:20px;"><i class="fa fa-fw fa-home"></i>플랫폼 설치 자동화</a></li>
           <li>
              <a href="javascript:;" data-toggle="collapse" data-target="#adminMgnt" ><i class="fa fa-fw fa fa-user"></i> 플랫폼 관리자 관리 <i class="fa fa-fw fa-caret-down"></i></a>
              <ul id="adminMgnt" class="collapse">
                  <sec:authorize access="hasAuthority('ADMIN_CODE_MENU')">
                      <li><a href="javascript:goPage('<c:url value="/admin/code"/>', 'Code MANAGEMENT');">코드 관리</a></li>
                  </sec:authorize>
                  <sec:authorize access="hasAuthority('ADMIN_ROLE_MENU')">
                      <li><a href="javascript:goPage('<c:url value="/admin/role"/>', 'Openstack MANAGEMENT');">권한 관리</a></li>
                  </sec:authorize>
                  <sec:authorize access="hasAuthority('ADMIN_USER_MENU')">
                      <li><a href="javascript:goPage('<c:url value="/admin/user"/>', 'Openstack MANAGEMENT');">로그인 계정 관리</a></li>
                  </sec:authorize>
               </ul>
           </li>
           
           <sec:authorize access="hasAuthority('IAAS_MANAGEMENT_MENU')">
               <li>
                   <a href="javascript:;" data-toggle="collapse" data-target="#infraMgnt" ><i class="fa fa-fw fa-cloud"></i> 인프라 환경 관리 <i class="fa fa-fw fa-caret-down"></i></a>
                   <ul id="infraMgnt" class="collapse">
                       <sec:authorize access="hasAuthority('IAAS_ACCOUNT_MENU')">
                           <li><a href="javascript:goPage('<c:url value="/iaasMgnt/account"/>', 'ACCOUNT');">인프라 계정 관리</a></li>
                       </sec:authorize>
                       <li><a href="javascript:goPage('<c:url value="/iaasMgnt/aws"/>', 'AWS MANAGEMENT');">AWS 관리</a></li>
                       <li><a href="javascript:goPage('<c:url value="/iaasMgnt/openstack"/>', 'Openstack MANAGEMENT');">Openstack 관리</a></li>
                       <li><a href="javascript:goPage('<c:url value="/iaasMgnt/azure"/>', 'Azure MANAGEMENT');">Azure 관리</a></li>
                    </ul>
                </li>
            </sec:authorize>
              <li><a href="javascript:goWindow('<c:url value="/platform"/>', 'IEDA-WEB');"><i class="fa fa-fw fa-cog"></i>PaaS-TA 설치 자동화</a></li>
              <li><a href="javascript:goWindow('<c:url value="/hbFlatform"/>', 'IEDA-HYBRID-WEB');"><i class="fa fa-fw fa-cogs"></i>이종 PaaS-TA 설치 자동화</a></li>
              
<%--               <li>
              <a href="javascript:;" data-toggle="collapse" data-target="#hybird2" ><i class="fa fa-fw fa fa-cogs"></i> 이종 PaaS-TA 설치 자동화 <i class="fa fa-fw fa-caret-down"></i></a>
                  <ul id="hybird2" class="collapse">
                      <sec:authorize access="hasAuthority('ADMIN_CODE_MENU')">
                          <li><a href="javascript:goPage('<c:url value="/admin/code"/>', 'Code MANAGEMENT');">AWS-OPENSTACK</a></li>
                      </sec:authorize>
                      <sec:authorize access="hasAuthority('ADMIN_ROLE_MENU')">
                          <li><a href="javascript:goPage('<c:url value="/admin/role"/>', 'Openstack MANAGEMENT');">AZURE-OPENSTACK</a></li>
                      </sec:authorize>
                  </ul>
              </li> --%>
              
              
              
              <%-- <li><a href="javascript:goWindow('<c:url value="/hbFlatform"/>', 'HB-IEDA-WEB');"><i class="fa fa-fw fa-cogs"></i>Layout1</a></li>
              <li><a href="javascript:goWindow('<c:url value="/hbFlatform2"/>', 'HB-IEDA-WEB');"><i class="fa fa-fw fa-cogs"></i>Layout2</a></li> --%>
        </ul>
    </div>
</div>
<!-- //왼쪽 메뉴 끝-->