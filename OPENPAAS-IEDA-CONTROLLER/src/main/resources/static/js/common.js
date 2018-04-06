/*******************************************************************************
 * 설명 : 특수문자 유효성 
 * 기능 : Keycode
 ******************************************************************************/
function Keycode(e) {
    var code = (window.event) ? event.keyCode : e.which; // IE : FF - Chrome
    // both
    if (code > 32 && code < 48)
        nAllow(e);
    if (code > 57 && code < 65)
        nAllow(e);
    if (code > 90 && code < 97)
        nAllow(e);
    if (code > 122 && code < 127)
        nAllow(e);
}

/*******************************************************************************
 * 설명 : 특수문자 유효성 메시지 
 * 기능 : nAllow
 ******************************************************************************/
function nAllow(e) {
    setGuideMessage($(".w2ui-msg-body #codeValueSuccMsg"), "",
            $(".w2ui-msg-body #codeValueErrMsg"), "특수문자는 사용할 수 없습니다.");

    if (navigator.appName != "Netscape") { // for not returning keycode value
        event.returnValue = false; // IE , - Chrome both
    } else {
        e.preventDefault(); // FF , - Chrome both
    }
}

/*******************************************************************************
 * 설명 : 숫자만 입력
 *  기능 : onlyNumber
 ******************************************************************************/
function onlyNumber(event) {
    event = event || window.event;
    var keyID = (event.which) ? event.which : event.keyCode;
    if ((keyID >= 48 && keyID <= 57) || (keyID >= 96 && keyID <= 105)
            || keyID == 8 || keyID == 9 || keyID == 46 || keyID == 37
            || keyID == 39)
        return;
    else
        return false;
}

/*******************************************************************************
 * 설명 : 포트 숫자만 입력
 *  기능 : onlyNumberPort
 ******************************************************************************/
function onlyNumberPort(val){
    regNumber = /^[0-9]*$/;
    if (regNumber.test(val) == true) {
        return true;
    } else {
        return false;
    }
}

/*******************************************************************************
 * 설명 : _만 입력 
 * 기능 : onlyNumberSpecialChar
 ******************************************************************************/
function onlyNumberSpecialChar(event) {
    var special_pattern = /[_]/gi;
    if (special_pattern.test(event.value) == true) {
        return true;

    } else {
        return false;
    }
}

/*******************************************************************************
 * 설명 : 문자 remove 
 * 기능 : removeChar
 ******************************************************************************/
function removeChar(event) {
    event = event || window.event;
    var keyID = (event.which) ? event.which : event.keyCode;
    if (keyID == 8 || keyID == 46 || keyID == 37 || keyID == 39)
        return;
    else
        event.target.value = event.target.value.replace(/[^0-9_]/g, "");
}

/*******************************************************************************
 * 설명 : 한글만 입력 
 * 기능 : fn_press_han
 ******************************************************************************/
function fn_press_han(event, obj) {
    // 좌우 방향키, 백스페이스, 딜리트, 탭키에 대한 예외
    event = event || window.event;
    if (event.keyCode == 8 || event.keyCode == 9 || event.keyCode == 37
            || event.keyCode == 39 || event.keyCode == 46)
        return;

    obj.value = obj.value.replace(/[\ㄱ-ㅎㅏ-ㅣ가-힣]/g, '');
}

/*******************************************************************************
 * 설명 : 입력값 오류 경고 
 * 기능 : setGuideMessage
 ******************************************************************************/
function setGuideMessage(successObject, successMessage, errorObject,
        errorMessage) {
    if (successMessage == "")
        successObject.html(successMessage);
    else {
        errorObject.css("color", "grey");
        successObject.html(successMessage).show().fadeOut(300);
    }
    alert(errorMessage);
}

/*******************************************************************************
 * 설명 : 빈값 체크 
 * 기능 : checkEmpty
 ******************************************************************************/
function checkEmpty(value) {
    return (value == null || value == "") ? true : false;
}

/*******************************************************************************
 * 설명 : URL 확인 
 * 기능 : validateIP
 ******************************************************************************/
function validateIP(input) {
    if (/^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/
            .test(input))
        return true;
    else
        return false;
}

/*******************************************************************************
 * 설명 : 이메일 유효성 
 * 기능 : emailValidation
 ******************************************************************************/
function emailValidation(email) {
    var email = email;
    var regex = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/;
    if (regex.test(email) == false)
        return false;
    else
        return true;
}

/*******************************************************************************
 * 설명 : 특수 문자 유효성
 * 기능 : specialCharacterValidation
 ******************************************************************************/
function specialCharacterValidation(input) {
    var txt = input;
    var regex = /[\{\}\[\]\/?.,;:|\)*~`!^\-+<>@\#$%&\\\=\(\'\"]/i;
    if (regex.test(txt)) {
        return true;
    } else {
        return false;
    }
}

/*******************************************************************************
 * 설명 : : IP 유효성 체크
 * 기능 : injectionBlacklist
 ******************************************************************************/
function checkIP4Checker(value){
    return /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(value);
}

/*******************************************************************************
 * 설명 : 문자 길이 문자 유효성 
 * 기능 : textLengthValidation
 ******************************************************************************/
function textLengthValidation(input) {
    var txt = input;
    var pattern = /^[\w\Wㄱ-ㅎㅏ-ㅣ가-힣]{2,15}$/;
    if (pattern.test(txt)) {
        return true;
    } else {
        return false;
    }
}

/********************************************************
 * 기능 : 스페이스 입력 block
 * 설명 : textSpaceValid
 *********************************************************/
function textSpaceValid( evt ){
     evt .value =  evt .value.replace(/\s/gi,"");
      return true;
}


/*******************************************************************************
* 설명 : byte 계산
* 기능 : bytesToSize
******************************************************************************/
function bytesToSize(bytes) {
       var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
       if (bytes == 0) return '0Byte';
       var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
       return Math.round(bytes / Math.pow(1024, i), 2) + ' ' + sizes[i];
};

/*******************************************************************************
* 설명 : gb => byte 계산
* 기능 : gbConverter
******************************************************************************/
function gbConverter(value){
    return value * (1024 * 1024 * 1024);
}

/*******************************************************************************
 * 설명 : : db 인젝션 필터링 문자
 * 기능 : injectionBlacklist
 ******************************************************************************/
 function checkInjectionBlacklist( value ){
    var blackList = ["--", ";--", ";","/*", "*/", "@@", "char", "nchar", "varchar", "nvarchar", "alter", "begin", "cast"
                     ,"create", "cursor", "declare", "delete", "drop", "end", "exec", "execute", "fetch", "insert", "kill",
                     "select", "sys", "sysobjects", "syscolumns", "table", "update",'"',"'", "#", "$", "%", "!", "^"];
    if( !checkEmpty(value) ){
        for( var i=0; i < blackList.length; i++ ){
            if( value.indexOf(blackList[i]) > -1 ){
                return false;
            }
        }
    }
    return true;
 }
 
 /*******************************************************************************
  * 설명 : : db 인젝션 필터링 문자
  * 기능 : injectionBlacklist
  ******************************************************************************/
  function checkCidrBlacklist( value ){
     var blackList = [];
     if ("^([0-9]{1,3}\.){3}[0-9]{1,3}($|/(16|24))$".test(input))
         return true;
     else
         return false;
  }
 
 /*******************************************************************************
  * 설명 : 유효성 성공한 Element 스타일 설정
  * 기능 : setSuccessStyle
  ******************************************************************************/
 function setSuccessStyle(label){
     var name = $(label).attr("name");
     var tag = $(label).prop("tagName").toLowerCase();
     var $element = $(".w2ui-msg-body "+tag+"[name='"+name+"']");
     
     if( $element.parent().parent().find("p").length == 0 ){
    	 $element.css("borderColor", "#bbb");  
     }else{
    	 $element.parent().parent().find("p").hide('fast', function(){
    		 $element.parent().find("p").remove();
             $element.css("borderColor", "#bbb");  
         });
     }
 }
 
 /*******************************************************************************
  * 설명 : 유효성 체크 스타일 설정
  * 기능 : setInvalidHandlerStyle
  ******************************************************************************/
 function setInvalidHandlerStyle(errors, validator){
     $(".w2ui-msg-body input[name='"+validator.errorList[0].element.name+"'] " ).focus();
     for( var i=0; i < errors; i++ ){
         var name = validator.errorList[i].element.name;
         var tag = (validator.errorList[i].element.tagName).toLowerCase();
         var $element = $(".w2ui-msg-body "+tag+"[name='"+name+"'] " );
         $element.removeClass("error");
          if( $element.parent().find("p").length == 0  || 
        		  ( $element.parent().find("input:text").length ==2 && $element.parent().find("p").length == 1) ){
              if( tag == 'textarea' || tag == 'select' ){
                  $element.parent().append("<p style='color:red;'>"+validator.errorList[i].message+"</p>");
              }else{ //input
                  if( tag == "input" && $element.parent().find("p").length == 0){
                	  if( $element.parent().find("input:text").length == 2 ){
//                		  $element.parent().append("<p style='color:red; margin-left:100px;'>"+validator.errorList[i].message+"</p>");
                	  }else{
                		  $element.parent().append("<p style='color:red;'>"+validator.errorList[i].message+"</p>");
                	  }
                  }
              }
              $element.css("borderColor","red");
              
          }else{
              if( tag == 'textarea' || tag == 'select' ){
                  $element.parent().find("p").replaceWith("<p style='color:red;'>"+validator.errorList[i].message+"</p>");
              }else{
                  $element.parent().find("p").replaceWith("<p style='color:red;'>"+validator.errorList[i].message+"</p>");
              }
              
          }
     }
 }
 
 /********************************************************
  * 기능 : setDefaultIaasAccountList
  * 설명 : 인프라 인프라 계정 정보 조회 기능
  *********************************************************/
 function setDefaultIaasAccountList(iaas){
     var result = "";
     flag = true;
     $.ajax({
         type : "GET",
         url : '/common/deploy/accountList/'+iaas+'',
         contentType : "application/json",
         dataType : "json",
         success : function(data, status) {
             if(data.length == 0){
                 result = "<option value='noAccount' >"+iaas+" 계정 정보가 존재 하지 않습니다.</option>";
                 $('#doSearch').attr('disabled', true);
                 $('#addBtn').attr('disabled', true);
                 $('#deleteBtn').attr('disabled', true);
                 $('#attachBtn').attr('disabled', true);
                 $('#detachBtn').attr('disabled', true);
                 $('#subnetBtn').attr('disabled', true);
                 $('#regionList').attr('disabled', true);
                 $('#regionList').html("<option>지역을 선택하세요.</option>")
             }else{
                 for(var i=0;i<data.length;i++){
                     if(data[0].defaultYn != "Y"){
                          selectIaasAccountPopUp(data, iaas);
                     }else{
                         if(data[i].defaultYn == "Y" && flag == true){
                             result += "<option value='"+data[0].id+"' >";
                             result += data[0].accountName;
                             result += "</option>";
                             accountId = data[0].id;
                             flag = false;
                             doSearch();
                         }else{
                             result += "<option value='"+data[i].id+"' >";
                             result += data[i].accountName;
                             result += "</option>";
                         }
                     }
                 }
                 if(iaas == "aws"){
                 setDefaultAwsRegion();
                 }
             }
             $('#setAccountList').html(result);
         },
         error : function(request, status, error) {
             w2popup.unlock();
             var errorResult = JSON.parse(request.responseText);
             w2alert(errorResult.message);
         }
     });
 }
 
 /********************************************************
  * 기능 : selectIaasAccountPopUp
  * 설명 : 기본 인프라 계정 정보가 존재 하지 않을 경우 계정 별칭 선택 창 기능
  *********************************************************/
 function selectIaasAccountPopUp(data, iaas){
     w2popup.open({
         title   : "<b>"+iaas+" 계정 선택</b>",
         width   : 530,
         height  : 200,
         modal   : true,
         body    : $("#registAccountPopupDiv").html(),
         buttons : $("#registAccountPopupBtnDiv").html(),
         onOpen : function(event){
             event.onComplete = function(){
                 var result = "";
                 result = "<select name='accountId' style='width:80%; margin-top:13px;'>";
                 for(var i=0;i<data.length;i++){
                        result += "<option value='" + data[i].id + "' >";
                        result += data[i].accountName;
                        result += "</option>";
                 }
                 result += "</select>";
                 $(".accountList").html(result);
             }
         },onClose:function(event){
             var result = "";
             result = "<select name='accountId' style='width:80%; margin-top:13px;'>";
             result = "<option value='noSelect' >"+iaas+" 계정 정보를 확인해주세요.</option>";
             for(var i=0;i<data.length;i++){
                    result += "<option value='" + data[i].id + "' >";
                    result += data[i].accountName;
                    result += "</option>";
             }
             result += "</select>";
             $('#regionList').attr('disabled', true);
             $('#regionList').html("<option>지역을 선택하세요.</option>");
             $("#setAccountList").html(result);
             $('#doSearch').attr('disabled', true);
             $('#addBtn').attr('disabled', true);
             $('#deleteBtn').attr('disabled', true);
             $('#attachBtn').attr('disabled', true);
             $('#detachBtn').attr('disabled', true);
             $('#subnetBtn').attr('disabled', true);
         }
     });
 }
 /*******************************************************************************
  * 설명 : IaaS Account 선택 시 Onchange 이벤트
  * 기능 : setAccountInfo
  ******************************************************************************/
function setAccountInfo(val, iaas){
    if(val == "noSelect"){
        $('#doSearch').attr('disabled', true);
        return;
    }
    $('#doSearch').attr('disabled', false);
    if(iaas == "aws")
    	setDefaultAwsRegion();
    
    setDefaultIaasAccount("noPopup", iaas);
}
 

 /********************************************************
  * 기능 : setDefaultIaasAccount
  * 설명 : 기본  인프라 계정 정보 저장 기능
  *********************************************************/
 function setDefaultIaasAccount(flag, iaas){
     if($("#doSearch").attr('disabled') == "disabled") return;
     w2utils.lock($("#layout_layout_panel_main"), "기본 인프라 계정 설정 중", true);
     if(flag == "noPopup"){
         var accountInfo ={
              id :  $("select[name='accountId']").val(),
              accountName : $("select[name='accountId']").text(),
              iaasType :  iaas
         }
     }else{
         var accountInfo ={
              id :  $(".w2ui-msg-body select[name='accountId']").val(),
              accountName : $(".w2ui-msg-body select[name='accountId']").text(),
              iaasType :  iaas
         }
     }
     $.ajax({
         type : "POST",
         url : "/common/iaasMgnt/setDefaultIaasAccount",
         contentType : "application/json",
         data : JSON.stringify(accountInfo),
         success : function(data, status) {
             flag = true;
             w2utils.unlock($("#layout_layout_panel_main"));
             w2popup.unlock();
             w2popup.close();
             if($('#regionList').val() == "지역을 선택하세요."){
                 setDefaultAwsRegion();
                 $('#regionList').attr('disabled', false);
             }
             $('#addBtn').attr('disabled', false);
             $('#doSearch').attr('disabled', false);
             setDefaultIaasAccountList(iaas);
         },
         error : function(request, status, error) {
             w2popup.unlock();
             w2utils.unlock($("#layout_layout_panel_main"));
             var errorResult = JSON.parse(request.responseText);
             w2alert(errorResult.message);
             setDefaultIaasAccountList(iaas);
         }
     });
 }
 /********************************************************
  * 기능 : setDefaultAwsRegion
  * 설명 : 기본  AWS 리전 정보 목록 조회 기능
  *********************************************************/
 function setDefaultAwsRegion(){
     $.ajax({
         type : "GET",
         url : '/common/aws/region/list',
         contentType : "application/json",
         dataType : "json",
         success : function(data, status) {
             var result = "";
             for(var i=0; i<data.length; i++){
                 if( data[i].name != "us-gov-west-1" && data[i].name != "cn-north-1" ){
                     if(data[i].name == "us-west-2"){
                         result += "<option value='" + data[i].name + "'selected >";
                         result += data[i].name;
                         result += "</option>"; 
                     }else{
                         result += "<option value='" + data[i].name + "' >";
                         result += data[i].name;
                         result += "</option>"; 
                     }
                 }
             }
             $("#regionList").html(result);
             $('#regionList').attr('disabled', false);
         },
         error : function(request, status, error) {
             w2popup.unlock();
             var errorResult = JSON.parse(request.responseText);
             w2alert(errorResult.message);
         }
     });
 }
 /********************************************************
  * 기능 : awsRegionOnchange
  * 설명 : AWS Region Onchange 이벤트 기능
  *********************************************************/
 function awsRegionOnchange(){
     accountId = $("select[name='accountId']").val();
     doSearch();
 }
 