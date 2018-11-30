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
           <li class="active"><a href="javascript:goPage('<c:url value="/main/hbDashboard"/>', 'Dashboard');" style="font-weight:600; font-size:20px;"><i class="fa fa-fw fa-home"></i> PaaS-TA 설치 자동화</a></li>
           <li>
              <a href="javascript:;" data-toggle="collapse" data-target="#configMenu" ><i class="fa fa-fw fa fa-cog"></i> 환경설정 및 관리 <i class="fa fa-fw fa-caret-down"></i></a>
              <ul id="configMenu" class="collapse">
                  <sec:authorize access="hasAuthority('CONFIG_HBSTEMCELL_MENU')">
                      <li><a href="javascript:goPage('<c:url value="/config/hbstemcell"/>', 'Public 스템셀 다운로드');">스템셀 관리</a></li>
                  </sec:authorize>
                  <sec:authorize access="hasAuthority('CONFIG_HBRELEASE_MENU')">
                      <li><a href="javascript:goPage('<c:url value="/config/hbRelease"/>', 'System 릴리즈 관리');">릴리즈 관리</a></li>
                  </sec:authorize>
                  <sec:authorize access="hasAuthority('INFO_HBIAASCONFIG_MENU')">
                        <li><a href="javascript:goPage('<c:url value="/info/hbIaasConfig"/>', '인프라 환경 설정 관리');">인프라 환경 설정 관리</a></li>
                  </sec:authorize>
               </ul>
           </li>
           <li>
               <a href="javascript:;" data-toggle="collapse" data-target="#deployMenu" ><i class="fa fa-fw fa-spinner"></i>이종 BOOTSTRAP<i class="fa fa-fw fa-caret-down"></i></a>
               <ul id="deployMenu" class="collapse">
                   <sec:authorize access="hasAuthority('DEPLOY_HBBOOTSTRAP_CPI_MENU')">
                       <li><a href="javascript:goPage('<c:url value="/deploy/hbBootstrap/cpiConfig"/>', 'CPI 정보 관리');">CPI 정보 관리</a></li>
                   </sec:authorize>
                   <sec:authorize access="hasAuthority('DEPLOY_HBBOOTSTRAP_NETWORK_MENU')">
                       <li><a href="javascript:goPage('<c:url value="/deploy/hbBootstrap/networkConfig"/>', 'NETWROK 정보 관리');">NETWROK 정보 관리</a></li>
                   </sec:authorize>
                   <sec:authorize access="hasAuthority('DEPLOY_HBBOOTSTRAP_CREDENTIAL_MENU')">
                        <li><a href="javascript:goPage('<c:url value="/deploy/hbBootstrap/credentialConfig"/>', '디렉터 인증서 관리');">디렉터 인증서 관리</a></li>
                   </sec:authorize>
                  <sec:authorize access="hasAuthority('DEPLOY_HBBOOTSTRAP_DEFAULT_MENU')">
                       <li><a href="javascript:goPage('<c:url value="/deploy/hbBootstrap/defaultConfig"/>', '기본 정보 관리');">기본 정보 관리</a></li>
                   </sec:authorize>
                   <sec:authorize access="hasAuthority('DEPLOY_HBBOOTSTRAP_RESOURCE_MENU')">
                       <li><a href="javascript:goPage('<c:url value="/deploy/hbBootstrap/resourceConfig"/>', '리소스 정보 관리');">리소스 정보 관리</a></li>
                   </sec:authorize>
                   <sec:authorize access="hasAuthority('DEPLOY_HBBOOTSTRAP_INSTALL_MENU')">
                       <li><a href="javascript:goPage('<c:url value="/deploy/hbBootstrap"/>', 'BOOTSTRAP 설치');">이종 BOOTSTRAP 설치</a></li>
                   </sec:authorize>
                   <sec:authorize access="hasAuthority('CONFIG_HBDIRECTOR_MENU')">
                      <li><a href="javascript:goPage('<c:url value="/config/hbDirector"/>', '디렉터 설정');">디렉터 설정</a></li>
                   </sec:authorize>
                </ul>
            </li>
            
            <li>
               <a href="javascript:;" data-toggle="collapse" data-target="#cfMenu" ><i class="fa fa-fw fa-spinner"></i>이종 CF-DEPLOYMENT<i class="fa fa-fw fa-caret-down"></i></a>
               <ul id="cfMenu" class="collapse">
                   <sec:authorize access="hasAuthority('DEPLOY_HBCF_DEFAULT_MENU')">
                       <li><a href="javascript:goPage('<c:url value="/deploy/hbCfDeployment/defaultConfig"/>', '기본 정보 관리');">기본 정보 관리</a></li>
                   </sec:authorize>
                   <sec:authorize access="hasAuthority('DEPLOY_HBCF_NETWORK_MENU')">
                       <li><a href="javascript:goPage('<c:url value="/deploy/hbCfDeployment/networkConfig"/>', 'NETWROK 정보 관리');">NETWROK 정보 관리</a></li>
                   </sec:authorize>
                   <sec:authorize access="hasAuthority('DEPLOY_HBCF_CREDENTIAL_MENU')">
                       <li><a href="javascript:goPage('<c:url value="/deploy/hbCfDeployment/credentialConfig"/>', '인증서 정보 관리');">인증서 정보 관리</a></li>
                   </sec:authorize>
                   <sec:authorize access="hasAuthority('DEPLOY_HBCF_RESOURCE_MENU')">
                       <li><a href="javascript:goPage('<c:url value="/deploy/hbCfDeployment/resourceConfig"/>', '리소스 정보 관리');">리소스 정보 관리</a></li>
                   </sec:authorize>
                   <sec:authorize access="hasAuthority('DEPLOY_HBCF_INSTANCE_MENU')">
                       <li><a href="javascript:goPage('<c:url value="/deploy/hbCfDeployment/instanceConfig"/>', '인스턴스 정보 관리');">인스턴스 정보 관리</a></li>
                   </sec:authorize>
                   <sec:authorize access="hasAuthority('DEPLOY_HBCF_MENU')">
                       <li><a href="javascript:goPage('<c:url value="/deploy/hbCfDeployment/hbCfDeployment"/>', '이종 CF 설치');">이종 CF-DEPLOYMENT 설치</a></li>
                   </sec:authorize>
                </ul>
            </li>
            
            <li>
               <a href="javascript:;" data-toggle="collapse" data-target="#deploymentMenu" ><i class="fa fa-fw fa-search-plus"></i> 배포 정보 조회 및 관리 <i class="fa fa-fw fa-caret-down"></i></a>
               <ul id="deploymentMenu" class="collapse">
                    <sec:authorize access="hasAuthority('INFO_HBSTEMCELL_MENU')">
                        <li><a href="javascript:goPage('<c:url value="/info/hbStemcell"/>', '스템셀 업로드');">스템셀 업로드</a></li>
                    </sec:authorize>
                    <sec:authorize access="hasAuthority('INFO_HBRELEASE_MENU')">
                        <li><a href="javascript:goPage('<c:url value="/info/hbRelease"/>', '릴리즈 업로드');">릴리즈 업로드</a></li>
                    </sec:authorize>
                    <sec:authorize access="hasAuthority('INFO_HBDEPLOYMENT_MENU')">
                        <li><a href="javascript:goPage('<c:url value="/info/hbDeployment"/>', '배포목록');">배포 정보</a></li>
                    </sec:authorize>
                </ul>
            </li>
        </ul>
    </div>
</div>
<!-- //왼쪽 메뉴 끝-->