<%
/* =================================================================
 * 작성일 : 2017.05.02
 * 작성자 : Ji,Hyangeun
 * 상세설명 : Top 화면
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Navigation -->
<div id="top">
    <div class="navbar navbar-inverse navbar-fixed-top" >
    <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-ex1-collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a href="/iaasMgnt" class="navbar-brand" id="header"></a>
            <a class="navbar-brand" href="/platform"> PaaS-TA <span style="color: #ffffff;">설치 자동화</span></a>
        </div>
 
 <!-- Top Menu Items -->
 <ul class="nav navbar-right top-nav">
 	<li style="padding-top:17px; font-size:14px; color:#ffffff">Welcome to Platform Management Dashboard.</li>
     <li class="dropdown" style="display:none">
         <a href="#" class="dropdown-toggle" data-toggle="dropdown"><i class="fa fa-cloud"></i> AWS-c1 <b class="caret"></b></a>
         <ul class="dropdown-menu alert-dropdown">
             <li>
                 <a href="#">Alert Name <span class="label label-default">Alert Badge</span></a>
             </li>
             <li>
                 <a href="#">Alert Name <span class="label label-primary">Alert Badge</span></a>
             </li>
             <li>
                 <a href="#">Alert Name <span class="label label-success">Alert Badge</span></a>
             </li>
             <li>
                 <a href="#">Alert Name <span class="label label-info">Alert Badge</span></a>
             </li>
             <li>
                 <a href="#">Alert Name <span class="label label-warning">Alert Badge</span></a>
             </li>
             <li>
                 <a href="#">Alert Name <span class="label label-danger">Alert Badge</span></a>
             </li>
             <li class="divider"></li>
             <li>
                 <a href="#">View All</a>
             </li>
         </ul>
     </li>
     <li class="dropdown">
         <a href="#" class="dropdown-toggle" data-toggle="dropdown"><i class="fa fa-user"></i> ${userId} <b class="care t"></b></a>
         <ul class="dropdown-menu">
             <li>
                 <a href="/logout"><i class="fa fa-fw fa-power-off"></i> 로그아웃</a>
             </li>
         </ul>
     </li>
 </ul>
</div>
</div>
