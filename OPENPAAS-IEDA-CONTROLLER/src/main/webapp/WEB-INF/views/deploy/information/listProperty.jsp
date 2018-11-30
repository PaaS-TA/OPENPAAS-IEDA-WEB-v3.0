<%
/* =================================================================
 * 작성일 : 2016-09
 * 작성자 : 이동현
 * 상세설명 : Property 관리
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2016.12       이동현        화면 개선 및 코드 버그 수정
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix = "spring" uri = "http://www.springframework.org/tags" %>
 <script>
 var search_msg='<spring:message code="common.search.data.lock"/>';//조회 중 입니다.
 var save_lock_msg='<spring:message code="common.save.data.lock"/>';//등록 중 입니다.
 var text_required_msg='<spring:message code="common.text.vaildate.required.message"/>';//을(를) 입력하세요.
 var hangeul_required_msg='<spring:message code="common.text.validate.hangeul.message"/>';//한글을 입력하실 수 없습니다.
 var deployment = ""; 
 var properties = [];
 var bDefaultDirector = "";
 var propertyInfo ="";
 $(function() {
   /************************************************
    * 설명: 기본 설치 관리자 정보 조회
   ************************************************/
   bDefaultDirector = getDefaultDirector("<c:url value='/common/use/director'/>");
   $('#us_PropertyGrid').w2grid({
       name: 'us_PropertyGrid',
       method: 'GET',
       msgAJAXerror : '프로퍼티 조회 실패',
       header: '<b>Property 목록</b>',
       multiSelect: false,
       show: { selectColumn: true, footer: true},
       style: 'text-align: center',
       columns    : [
           {field: 'recid', caption: 'recid', hidden: true}
           , {field: 'name', caption: 'Property 명', size: '50%', style: 'text-align:center', 
               render : function(record){
                   properties.push(record.name);
                   return record.name;
               }}
           , {field: 'value', caption: 'Property 값', size: '50%', style: 'text-align:center'}
           ],
       onSelect: function(event) {
           event.onComplete = function() {
               $('#detailProperty').attr('disabled', false);
               $('#modifyProperty').attr('disabled', false);
               $('#deleteProperty').attr('disabled', false);
           }
       },
       onUnselect: function(event) {
           event.onComplete = function() {
               $('#detailProperty').attr('disabled', true);
               $('#modifyProperty').attr('disabled', true);
               $('#deleteProperty').attr('disabled', true);
           }
       },
       onLoad:function(event){
           if(event.xhr.status == 403){
               location.href = "/abuse";
               event.preventDefault();
           }
           $('#createProperty').attr('disabled', false);
       }, onError:function(evnet){
       }
   });
   /************************************************
    * 설명: Property 조회
   ************************************************/
   $("#doSearch").click(function(){
           $('#detailProperty').attr('disabled', true);
            $('#modifyProperty').attr('disabled', true);
            $('#deleteProperty').attr('disabled', true);
       doSearch($("#deployments").val());
   });
     
   /****************************************
    * 설명:  Property 생성 팝업
   ****************************************/
   $("#createProperty").on("click",function(){
       if($("#createProperty").attr('disabled') == "disabled") return;
       w2popup.open({
           title   : "<b>Property 생성</b>",
           width   : 600,
           height  : 445,
           modal   : true,
           body    : $("#createPropertyDiv").html(),
           buttons : $("#createPropertyDivBtn").html(),
           onOpen :function(event) {
               event.onComplete = function() {
               }
           },
           onClose :function(event) {
               event.onComplete = function() {
                   buttonStyle();
                   doSearch($("#deployments").val());
               }
           }
       });
   });
     
   /********************************************************
    * 설명 : Property 수정 클릭 이벤트
   *********************************************************/    
   $("#modifyProperty").click(function(){
       if($("#modifyProperty").attr('disabled') == "disabled") return;
       var selected = w2ui['us_PropertyGrid'].getSelection();
       var recodes = w2ui['us_PropertyGrid'].get(selected);
       
       w2popup.open({
           title   : "<b>Property 수정 </b>",
           width   : 600,
           height  : 445,
           modal   : true,
           body : $("#createPropertyDiv").html(),
           buttons : $("#createPropertyDivBtn").html(),
           showMax :false,
           onOpen :function(event) {
               event.onComplete = function() {
                   getPropertyInfo();
               }
           },
           onClose :function(event) {
               event.onComplete = function() {
                   doSearch($("#deployments").val());
                   buttonStyle();
               }
           }
       });
   });

   /********************************************************
    * 설명 : Property 삭제 버튼 클릭
   *********************************************************/    
   $("#deleteProperty").on("click",function(){
       if($("#deleteProperty").attr('disabled') == "disabled") return;

       var selected = w2ui['us_PropertyGrid'].getSelection();
       var recodes = w2ui['us_PropertyGrid'].get(selected);
       w2confirm({
           title    : "Property 삭제",
           msg      : "Property (" + recodes.name + ")을 삭제하시겠습니까?",
           yes_text : "확인",
           no_text  : "취소",
           yes_callBack : function(event){
               deleteProperty(recodes);
           },
           no_callBack : function(){
               doSearch($("#deployments").val());
               buttonStyle();
           }
       });
   });

   /********************************************************
    * 설명 : Property 상세 보기 이벤트
   *********************************************************/    
   $("#detailProperty").on("click",function(){
       if($("#detailProperty").attr('disabled') == "disabled") return;

       var selected = w2ui['us_PropertyGrid'].getSelection();
       var records = w2ui['us_PropertyGrid'].get(selected);
      
       $.ajax({
           type : "GET",
           url : "/info/property/list/detailInfo/"+$("#deployments").val()+"/"+records.name,
           contentType : "application/json",
           async : true,
           success : function(data, status) {
               propertyDetailPopUp(data);
           },
           error : function(request, status, error) {
               w2popup.unlock();
               w2popup.close();
               var errorResult = JSON.parse(request.responseText);
               w2alert(errorResult.message);
           }
       });
   });
   
   initView(bDefaultDirector);
 });
 
 /************************************************
  * 설명: 조회기능
 ************************************************/
 function doSearch(deployment) {
     if( deployment != null ){
         w2ui['us_PropertyGrid'].load("<c:url value='/info/property/list/'/>"+deployment);
     }else{
         if(!bDefaultDirector){
            w2alert("기본 설치 관리자를 등록해 주세요. ","프로퍼티 조회");
        }else{
            w2alert("배포명을 선택해주세요. ","프로퍼티 조회");
        }
     }
     properties = [];
 }
 
 /********************************************************
  * 설명 : 그리드 재조회 및 버튼 초기화
  * 기능 : initView
 *********************************************************/
 function initView(bDefaultDirector) {
      if ( bDefaultDirector ) {
          getDeploymentList();
      }else{
          $("#deployments").html("<option selected='selected' disabled='disabled' value='all' style='color:red'>기본 설치자가 존재 하지 않습니다.</option>");
      }
      $('#createProperty').attr('disabled', true);
      $('#detailProperty').attr('disabled', true);
      $('#modifyProperty').attr('disabled', true);
      $('#deleteProperty').attr('disabled', true);
      propertyInfo = "";
  }
 
 /********************************************************
  * 설명 : 그리드 재조회 및 버튼 초기화
  * 기능 : initView
 *********************************************************/
 function buttonStyle(){
     w2ui['us_PropertyGrid'].clear();
    $('#createProperty').attr('disabled', false);
    $('#detailProperty').attr('disabled', true);
    $('#modifyProperty').attr('disabled', true);
    $('#deleteProperty').attr('disabled', true);
 }

 /********************************************************
  * 설명 : 배포 조회
  * 기능 : getDeploymentList
 *********************************************************/
 function getDeploymentList(){
     $.ajax({
         type : "GET",
         url : '/common/use/deployments',
         contentType : "application/json",
         async : true,
         success : function(data) {
             var $object = jQuery("#deployments");
             var optionString = "";
             if(data.contents != null){
                 optionString = "<option selected='selected' disabled='disabled' value='all' style='color:gray'>조회할 배포명을 선택하세요.(필수)</option>";
                 for (i = 0; i < data.contents.length; i++) {
                     optionString += "<option value='" + data.contents[i].name + "' >";
                     optionString += data.contents[i].name;
                     optionString += "</option>\n";
                 }
             }else{
                 optionString = optionString = "<option selected='selected' disabled='disabled' value='all' style='color:red'>조회할 배포명이 없습니다.</option>";
             }
             $object.html(optionString);
         }
     });
 }

 /****************************************
  * 설명 : Property 생성 및 수정
  * 기능 : saveProperty
 ****************************************/
 function saveProperty(){
      w2popup.lock(save_lock_msg, true);
      //생성일 경우
      var url ="/info/property/modify/createProperty";
      var type ="POST";
      if( checkEmpty(propertyInfo) ){
          for(var i=0;i<properties.length;i++){
              if($(".w2ui-msg-body input[name='propertyName']").val()==properties[i] &&
                      properties.name != $(".w2ui-msg-body input[name='propertyName']").val() ){
                  w2alert("중복 된 Property 명 입니다.", "Property 생성");
                  return;
              }
          }
      }else{
          //수정 일 경우
          url = "/info/property/modify/updateProperty";
          type = "PUT";
      }
      var propertyParam = {
              name : $(".w2ui-msg-body input[name='propertyName']").val(),
              value : $(".w2ui-msg-body textarea[name='propertyValue']").val(),
              deploymentName : $("#deployments").val()
      };
      $.ajax({
          type : type,
          url : url,
          contentType : "application/json",
          async : true,
          data : JSON.stringify(propertyParam),
          success : function(status) {
              w2popup.unlock();
              w2popup.close();
              doSearch($("#deployments").val());
              propertyInfo="";
          },
          error : function(request, status, error) {
              w2popup.unlock();
              w2popup.close();
              var errorResult = JSON.parse(request.responseText);
              w2alert(errorResult.message);
              doSearch($("#deployments").val());
          }
      });
  }

 /********************************************************
  * 설명 : Property 상세 조회
  * 기능 : getPropertyInfo
 *********************************************************/
 function getPropertyInfo(){
      w2popup.lock(search_msg, true);
      var selected = w2ui['us_PropertyGrid'].getSelection();
      var records = w2ui['us_PropertyGrid'].get(selected);

      $.ajax({
          type : "GET",
          url : "/info/property/list/detailInfo/"+$("#deployments").val()+"/"+records.name,
          contentType : "application/json",
          async : true,
          success : function(data, status) {
              $(".w2ui-msg-body input[name='propertyName']").prop('readonly','true');
              $(".w2ui-msg-body input[name='propertyName']").val(data.name);
              $(".w2ui-msg-body textarea[name='propertyValue']").val(data.value);
              propertyInfo = {
                      deployment : data.deployment,
                      name : data.name,
                      value : data.value
              }
              w2popup.unlock();
          },
          error : function(request, status, error) {
              w2popup.unlock();
              w2popup.close();
              var errorResult = JSON.parse(request.responseText);
              w2alert(errorResult.message);
          }
      });
 }
 
 /********************************************************
  * 설명 : Property 삭제
  * 기능 : deleteProperty
 *********************************************************/
 function deleteProperty(recodes){
      var propertyParam = {
              name : recodes.name,
              value : recodes.value,
              deploymentName : $("#deployments").val()
      };
      $.ajax({
          type : "DELETE",
          url : "/info/property/modify/deleteProperty",
          contentType : "application/json",
          async : true,
          data : JSON.stringify(propertyParam),
          success : function(status) {
              buttonStyle();
              doSearch($("#deployments").val());
          },
          error : function(request, status, error) {
              w2popup.unlock();
              w2popup.close();
              var errorResult = JSON.parse(request.responseText);
              w2alert(errorResult.message);
              buttonStyle();
              doSearch($("#deployments").val());
          }
      });
 }

 /********************************************************
  * 설명 : 상세 보기 팝업
  * 기능 : propertyDetailPopUp
 *********************************************************/
 function propertyDetailPopUp(data){
      $("#propertyInfoDiv").w2popup({
          title : "<b>Property 상세 보기</b>",
          width : 600,
          height :425,
          buttons : $("#detailPropertyDivBtn").html(),
          modal :true,
          showMax :true,
          onOpen :function(event) {
              event.onComplete = function() {
                  $(".w2ui-msg-body input[name='propertyName']").val(data.name);
                  $(".w2ui-msg-body textarea[name='propertyValue']").val(data.value);
              }
          },
          onClose : function(event){
              doSearch($("#deployments").val());
              buttonStyle();
          }
      });
 }
 
 /********************************************************
  * 설명 : 다른페이지 이동시 호출
  * 기능 : clearMainPage
  *********************************************************/
 function clearMainPage() {
     $().w2destroy('us_PropertyGrid');
 }

 /********************************************************
  * 설명 : 화면 리사이즈시 호출
  * 기능 : resize
  *********************************************************/
 $( window ).resize(function() {
     setLayoutContainerHeight();
 });
 </script>
 <div id="main">
    <div class="page_site">정보조회 > <strong>Property 관리</strong></div>
    <!-- 설치 관리자 -->
    <div id="isDefaultDirector"></div>
    
    <div class="pdt20"> 
        <div class="search_box" align="left" style="padding-left:10px; width:100%;">
            <label  style="font-size:11px; color:white;">배포명</label> &nbsp;&nbsp;&nbsp;
            <select name="select" id="deployments" class="select" style="width:300px"></select>&nbsp;&nbsp;&nbsp;
            <span id="doSearch" class="btn btn-info" style="width:50px" >조회</span>
        </div>
        
        <div class="title fl">Property 목록</div>
        <div class="fr"> 
            <!-- Btn -->
            <sec:authorize access="hasAuthority('INFO_PROPERTY_MODIFY')">
	            <span class="btn btn-primary" style="width:120px" id="createProperty">Property 생성</span>
	            <span class="btn btn-info" style="width:120px" id="modifyProperty">Property 수정</span>
	            <span class="btn btn-danger" style="width:120px" id="deleteProperty">Property 삭제</span>
	            <span class="btn btn-success" style="width:140px" id="detailProperty">Property 상세보기</span>
            </sec:authorize>
            <!-- //Btn -->
        </div>
        <div id="us_PropertyGrid" style="width:100%; height:533px"></div>
    </div>
</div>

<!-- Property 생성 팝업 -->
<div id="createPropertyDiv"  hidden="true">
    <form id="settingForm">
        <div class="panel panel-info" style="margin-top:5px;">
            <div class="panel-heading"><b>Property 정보</b></div>
            <div class="panel-body" style="height: 310px;overflow-y:auto;">
                <div class="w2ui-field">
                    <label style="text-align: left;">Property 명</label>
                    <div>
                        <input name="propertyName" type="text" maxlength="100" style="width: 365px" placeholder="프로퍼티 명을 입력하세요." />
                    </div>
                </div>
                <div class="w2ui-field">
                    <label style="text-align: left;">Property 값</label>
                    <div style="text-align:left;">
                        <textarea name="propertyValue" placeholder="프로퍼티 값을 입력하세요." style="display:inline-block; width: 365px; height: 212px; overflow-y: visible; resize: none;" ></textarea>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>
<div id="createPropertyDivBtn" hidden="true">
    <button class="btn" id="savePropertyBtn" onclick="$('#settingForm').submit();">저장</button>
    <button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
</div>

<div id="propertyInfoDiv" hidden="true">
    <div class="panel panel-info" style="margin-top:5px;">
        <div class="panel-heading"><b>Property 정보</b></div>
        <div class="panel-body" style="overflow-y:auto; height: 290px;">
            <div class="w2ui-field">
                <label style="text-align: left;">Property 명</label>
                <div>
                    <input name="propertyName" type="text" maxlength="100" style="width: 365px" readonly="readonly" />
                </div>
            </div>

            <div class="w2ui-field">
                <label style="text-align: left;">Property 값</label>
                <div>
                    <textarea readonly name="propertyValue" style=" color:#777; display:inline-block; width: 365px; height: 212px;overflow-y: visible; resize: none; background-color: #f1f1f1;"></textarea>
                </div>
            </div>
        </div>
    </div>
    <div id="detailPropertyDivBtn" hidden="true">
        <button class="btn" id="popClose"  onclick="w2popup.close();">닫기</button>
    </div>
</div>
<script type="text/javascript">
$(function() {
    $.validator.addMethod("hangeul", function(value, element, params) {
        var validation =  params.replace(/[\ㄱ-ㅎㅏ-ㅣ가-힣]/g, '');
        return params != validation ? false : true;
    }, hangeul_required_msg);
    
    $("#settingForm").validate({
        ignore : "",
        onfocusout: true,
        rules: {
            propertyName: { 
                required: function(){ 
                    return checkEmpty( $(".w2ui-msg-body input[name='propertyName']").val() ); 
                }, hangeul : function(){
                    return $(".w2ui-msg-body input[name='propertyName']").val();
                }
            }, propertyValue: { 
                required: function(){ 
                    return checkEmpty( $(".w2ui-msg-body textarea[name='propertyValue']").val() ); 
                }
            }
        }, messages: {
            propertyName  : { required:  "Property 명"+text_required_msg }
           ,propertyValue : {  required: "Property 값"+text_required_msg }
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
            saveProperty();
        }
    });
});
</script>