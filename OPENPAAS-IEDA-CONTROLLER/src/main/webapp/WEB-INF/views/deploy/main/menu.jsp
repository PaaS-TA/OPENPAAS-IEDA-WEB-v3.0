<%
/* =================================================================
 * 작성일 : 2018.03.26
 * 작성자 : 이동현
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
           <li class="active"><a href="javascript:goPage('<c:url value="/main/dashboard"/>', 'Dashboard');" style="font-weight:600; font-size:20px;"><i class="fa fa-fw fa-home"></i> PaaS-TA 설치 자동화</a></li>
           <li>
              <a href="javascript:;" data-toggle="collapse" data-target="#configMenu" ><i class="fa fa-fw fa fa-cog"></i> 환경설정 및 관리 <i class="fa fa-fw fa-caret-down"></i></a>
              <ul id="configMenu" class="collapse">
                  <sec:authorize access="hasAuthority('CONFIG_DIRECTOR_MENU')">
                      <li><a href="javascript:goPage('<c:url value="/config/director"/>', '디렉터 설정');">디렉터 설정</a></li>
                  </sec:authorize>
                  <sec:authorize access="hasAuthority('CONFIG_STEMCELL_MENU')">
                      <li><a href="javascript:goPage('<c:url value="/config/stemcell"/>', 'Public 스템셀 다운로드');">스템셀 관리</a></li>
                  </sec:authorize>
                  <sec:authorize access="hasAuthority('CONFIG_RELEASE_MENU')">
                      <li><a href="javascript:goPage('<c:url value="/config/systemRelease"/>', 'System 릴리즈 관리');">릴리즈 관리</a></li>
                  </sec:authorize>
                  <sec:authorize access="hasAuthority('INFO_IAASCONFIG_MENU')">
                        <li><a href="javascript:goPage('<c:url value="/info/iaasConfig"/>', '인프라 환경 설정 관리');">인프라 환경 설정 관리</a></li>
                  </sec:authorize>
                  <sec:authorize access="hasAuthority('INFO_IAASCONFIG_MENU')">
                        <li><a href="javascript:goPage('<c:url value="/config/credential"/>', '디렉터 인증서 관리');">디렉터 인증서 관리</a></li>
                   </sec:authorize>
               </ul>
           </li>
           <li>
               <a href="javascript:;" data-toggle="collapse" data-target="#deployMenu" ><i class="fa fa-fw fa-spinner"></i> 플랫폼 설치 <i class="fa fa-fw fa-caret-down"></i></a>
               <ul id="deployMenu" class="collapse">
                   <sec:authorize access="hasAuthority('DEPLOY_BOOTSTRAP_MENU')">
                       <li><a href="javascript:goPage('<c:url value="/deploy/bootstrap"/>', 'Micro BOSH 설치');"> BOOTSTRAP 설치</a></li>
                   </sec:authorize>
                   <sec:authorize access="hasAuthority('DEPLOY_CF_MENU')">
                       <li><a href="javascript:goPage('<c:url value="/deploy/cf"/>', 'CF DEPLOYMENT');"> CF-DEPLOYMENT 설치</a></li>
                   </sec:authorize>
                   <sec:authorize access="hasAuthority('DEPLOY_SERVICEPACK_MENU')">
                       <li><a href="javascript:goPage('<c:url value="/deploy/servicePack"/>', '서비스팩 설치');"> 서비스팩 설치</a></li>
                   </sec:authorize>
                </ul>
            </li>
            <li>
               <a href="javascript:;" data-toggle="collapse" data-target="#deploymentMenu" ><i class="fa fa-fw fa-search-plus"></i> 배포 정보 조회 및 관리 <i class="fa fa-fw fa-caret-down"></i></a>
               <ul id="deploymentMenu" class="collapse">
                    <sec:authorize access="hasAuthority('INFO_STEMCELL_MENU')">
                        <li><a href="javascript:goPage('<c:url value="/info/stemcell"/>', '스템셀 업로드');">스템셀 업로드</a></li>
                    </sec:authorize>
                    <sec:authorize access="hasAuthority('INFO_RELEASE_MENU')">
                        <li><a href="javascript:goPage('<c:url value="/info/release"/>', '릴리즈 업로드');">릴리즈 업로드</a></li>
                    </sec:authorize>
                    <sec:authorize access="hasAuthority('INFO_DEPLOYMENT_MENU')">
                        <li><a href="javascript:goPage('<c:url value="/info/deployment"/>', '배포목록');">배포 정보</a></li>
                    </sec:authorize>
                    <sec:authorize access="hasAuthority('INFO_TASK_MENU')">
                        <li><a href="javascript:goPage('<c:url value="/info/task"/>', 'Task정보');">Task 정보</a></li>
                    </sec:authorize>
                    <sec:authorize access="hasAuthority('INFO_VM_MENU')">
                        <li><a href="javascript:goPage('<c:url value="/info/vms"/>', 'VM 관리');">VM 관리</a></li>
                    </sec:authorize>
                    <sec:authorize access="hasAuthority('INFO_PROPERTY_MENU')">
                        <li><a href="javascript:goPage('<c:url value="/info/property"/>', 'Property 관리');">Property 관리</a></li>
                    </sec:authorize>
                    <sec:authorize access="hasAuthority('INFO_SNAPSHOT_MENU')">
                        <li><a href="javascript:goPage('<c:url value="/info/snapshot"/>', '스냅샷 관리');">스냅샷 관리</a></li>
                    </sec:authorize>
                    <sec:authorize access="hasAuthority('INFO_MANIFEST_MENU')">
                        <li><a href="javascript:goPage('<c:url value="/info/manifest"/>', 'Manifest 관리');">Manifest 관리</a></li>
                    </sec:authorize>
                </ul>
            </li>
        </ul>
    </div>
</div>
<!-- //왼쪽 메뉴 끝-->