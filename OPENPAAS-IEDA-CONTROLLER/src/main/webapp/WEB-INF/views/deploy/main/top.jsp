<%
/* =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2016.10      지향은         OpenPaaS 이미지 수정
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style type="text/css">
    .log-out-btn{
        width:100px;
        height:35px;
        font-size:13px;
        font-family: sans-serif;
        text-decoration: none;
        padding:7px 18px;
        border:1px solid #eee;
        color:#eee;
        top:-50px;
        right:30px;
        float: right;
        position: relative;
        background-color: rgb(44,50,72);
    }    
    .log-out-btn:hover{
        cursor: pointer;
        color: rgb(113,113,113);
        background-color:#e9e9e9;
        transition: 0.4s;    
    }
    #header { background-color:#2c3349; height:71px; }
    #header > a >label { padding:15px; font-size:23px; color:white;cursor:pointer }
    #header .logo { background:url(../images/logo3.png); background-repeat: no-repeat; width:42px; height:40px; background-size:38px;margin-left:15px; display:inline-block; vertical-align:middle; }
    #header a.logout { float:right; font-size:14px; padding:23px;}
    #header a.logout:hover{color:#fbfbfb;  }
</style>


<%-- <a href="<c:url value='/'/>"><div id="header"></div></a> --%>
<div id="header">
    <div class="logo"></div>
    <a href="<c:url value='/'/>" ><label>PaaS 플랫폼 설치 자동화</label></a>
    <a href="/logout" class="logout"><i class="fa fa-fw fa-power-off"></i> 로그아웃</a>
</div>
