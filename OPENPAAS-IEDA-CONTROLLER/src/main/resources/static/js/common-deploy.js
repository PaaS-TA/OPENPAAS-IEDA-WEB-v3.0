/*******************************************************************************
 * 설명 : 기본설치관리자 정보 조회
 ******************************************************************************/
function getDefaultDirector(url, type) {
    var isOk = true;
    var directorInfoDiv = '<div class="title">디렉터 정보</div>';
    directorInfoDiv += '<table class="tbl1" border="1" cellspacing="0">';
    directorInfoDiv += '<tr><th width="18%" class="th_fb">디렉터 이름</th><td class="td_fb"><b id="directorName"></b></td>';
    directorInfoDiv += '<th width="18%" class="th_fb">디렉터 계정</th><td class="td_fb"><b id="userId"></b></td></tr>';
    directorInfoDiv += '<tr><th width="18%" >디렉터 URL</th><td><b id="directorUrl"></b></td>';
    directorInfoDiv += '<th width="18%" >디렉터 UUID</th><td ><b id="directorUuid"></b></td></tr></table>';
    $.ajax({
        type : "GET",
        url : url,
        async : false,
        success : function(data) {
            if (!checkEmpty(data)) {
                if(type!="hybrid"){
                    $("#isDefaultDirector").html(directorInfoDiv);
                    setDefaultDirectorInfo(data);
                } else{
                    $("#isDefaultDirector").html("");
                }
                isOk = data.connect;
            } else {
                isOk = false;
                var message = "";
                if(type!="hybrid"){
                    var message = "기본 디렉터가 존재하지 않습니다. 플랫폼설치 -> BOOSTRAP설치 메뉴를 이용해서 BOOTSTRAP 설치 후 디렉터를 등록하세요.";
                } else{
                    var message = "디렉터가 존재하지 않습니다. 이기종 플랫폼설치 -> BOOSTRAP설치 메뉴를 이용해서 BOOTSTRAP 설치 후 디렉터를 등록하세요.";
                }
                
                var errorDirectorDiv = '<div class="alert alert-danger" style="font-size:15px;text-align:center;"><strong>'
                    + message + '</strong></div>';
                $("#isDefaultDirector").html(errorDirectorDiv);
            }
        },error : function(request, status, error) {
            if (error == "Forbidden") {
                location.href = "/abuse";
            }
        }
    });
    return isOk;
}

/********************************************************
 * 기능 : setDefaultIaasAccountList
 * 설명 : 인프라 인프라 계정 정보 조회 기능
 *********************************************************/
function setDefaultIaasAccountList(){
    var result = "";
    flag = true;
    $.ajax({
        type : "GET",
        url : "/common/deploy/accountList/aws",
        contentType : "application/json",
        dataType : "json",
        success : function(data, status) {
            if(data.length == 0){
                result = "<option value='1' >AWS 계정 정보가 존재 하지 않습니다.</option>";
                $('#doSearch').attr('disabled', true);
            }else{
                for(var i=0;i<data.length;i++){
                    if(data[0].defaultYn == "N"){
                         selectIaasAccountPopUp(data);
                    }else{
                        if(data[i].defaultYn == "Y" && flag == true){
                            result += "<option value='"+data[0].id+"' >";
                            result += data[0].accountName;
                            result += "</option>";
                            flag = false;
                        }else{
                            result += "<option value='"+data[i].id+"' >";
                            result += data[i].accountName;
                            result += "</option>";
                        }
                    }
                }
            }
            $('#setAwsAccountList').html(result);
        },
        error : function(request, status, error) {
            w2popup.unlock();
            var errorResult = JSON.parse(request.responseText);
            w2alert(errorResult.message);
        }
    });
}


/*******************************************************************************
 * 설명 : 기본설치관리자 설정 
 * Function : setDefaultDirectorInfo
 ******************************************************************************/
function setDefaultDirectorInfo(data) {
    $('#directorName').text(data.directorName + '(' + data.directorCpi + ')');
    $('#userId').text(data.userId);

    var diretorUrl = "https://" + data.directorUrl + ":" + data.directorPort;

    $('#directorUrl').text(diretorUrl);
    $('#directorUuid').text(data.directorUuid);
}

/*******************************************************************************
 * 설명 : Lock 파일 설정 
 * Function : commonLockFile
 ******************************************************************************/
function commonLockFile(url, message) {

    var lock = true;
    $.ajax({
        type : "get",
        url : url,
        async : false,
        error : function(request, status, error) {
            if (error == "Forbidden") {
                location.href = "/abuse";
            }
            lock = false;
        },
        success : function(data) {
            if (!data) {
                w2alert(message);
            }
            lock = data;
        }
    });
    return lock;
}

/*******************************************************************************
 * 설명 : Lock 파일 설정 
 * Function : commonLockFile
 ******************************************************************************/
function deleteLockFile(url) {
}

/*******************************************************************************
 * 설명 : 배포명 중복 체크
 * Function : checkDeploymentNameDuplicate
 ******************************************************************************/
function checkDeploymentNameDuplicate( platform, deploymentName, iaas ) {
    var check = true;
    $.ajax({
        type : "get",
        url : "/common/deploy/deployments/"+platform + "/" +iaas,
        async : false,
        success : function(data) {
            data.map(function(obj) {
                if( deploymentName == obj){
                    check = false;
                }
            });
        }
    });
    return check;
}

/*******************************************************************************
 * 설명 : 버전 비교
 * Function : compare
 ******************************************************************************/
function compare(a, b) {
    if (a === b) {
       return 0;
    }
    var a_components = a.split(".");
    var b_components = b.split(".");

    var len = Math.min(a_components.length, b_components.length);

    // loop while the components are equal
    for (var i = 0; i < len; i++) {
        // A bigger than B
        if (parseInt(a_components[i]) > parseInt(b_components[i])) {
            return 1;
        }

        // B bigger than A
        if (parseInt(a_components[i]) < parseInt(b_components[i])) {
            return -1;
        }
    }

    // If one's a prefix of the other, the longer one is greater.
    if (a_components.length > b_components.length) {
        return 1;
    }
    if (a_components.length < b_components.length) {
        return -1;
    }
    // Otherwise they are the same.
    return 0;
}

/******************************************************************
 * Function : setPrivateKeyPath
 * 설명 : 공통 Private key File List
 ***************************************************************** */
function setPrivateKeyPath(value){
    $(".w2ui-msg-body input[name=commonKeypairPath]").val(value);
}


/******************************************************************
 * Function : setPrivateKeyPathFileName
 * 설명: 공통 File upload Input
 ***************************************************************** */
function setPrivateKeyPathFileName(fileInput){
    var file = fileInput.files;
    $(".w2ui-msg-body input[name=commonKeypairPath]").val(file[0].name);
    $(".w2ui-msg-body #keyPathFileName").val(file[0].name);
    
}

/*******************************************************************************
 * Popup ValidationCheck
 ******************************************************************************/
//function popupValidation() {
//    var elements = $(".w2ui-box1 .w2ui-msg-body .w2ui-field input:visible, textarea:visible");
//    var checkValidation = true;
//    var emptyFieldLabels = null;
//    var emailFieldLabels = null;
//    if (elements.length > 0) {
//        emptyFieldLabels = new Array();
//        emailFieldLabels = new Array();
//        elements.each(function(obj) {
//                    var tagType = $(this).get(0).tagName;
//                    var inputType = $(this).attr('type');
//                    var elementName = $(this).attr('name');
//                    var elementValue = $(this).val().trim();
//                    var label = "";
//                    // 빈값일 경우
//                    if (elementName && !elementValue) {
//                        if (tagType.toLowerCase() == "input") {
//                            if (inputType == 'text') {
//                                if ($(this).attr('name') == "subnetStaticFrom" || $(this).attr('name') == "subnetStaticTo") {
//                                    label = "Static Ip";
//                                    $(this).css({"border-color" : "red"}).parent().parent().find(".isMessage").text(label + "를(을) 입력하세요").css({"color" : "red"})
//                                
//                                } else if ($(this).attr('name') == "subnetReservedFrom" || $(this).attr('name') == "subnetReservedTo") {
//                                    label = "Reserved Range";
//                                    $(this).css({ "border-color" : "red"}).parent().parent().find(".isMessage").text(label + "를(을) 입력하세요").css({"color" : "red"});
//                                
//                                } else if ($(this).attr('name') == "releasePathFile" || $(this).attr('name') == "releaseUrl") {
//                                    label = "release path";
//                                    $(this).css({ "border-color" : "red" }).parent().find(".isMessage").text( label + "를(을) 입력하세요").css({"color" : "red"});
//                                
//                                } else if ($(this).attr('name') == "deploymentName") {
//                                    checkValidation = true;
//                                
//                                } else if ($(this).attr('name') == "stemcellPathVersion" || $(this).attr('name') == "stemcellPathUrl" || $(this).attr('name') == "stemcellPathFileName"){
//                                    if($(".w2ui-msg-body input[name=stemcellPathFileName]").val() != "" ||  $(".w2ui-msg-body input[name='stemcellPathUrl']").val() != "" 
//                                            || $(".w2ui-msg-body input[name='stemcellPathVersion']").val() != ""){
//                                        checkValidation = true;
//                                    }else{
//                                        label = "시스템 다운 유형";
//                                        $(this).css({"border-color" : "red"});
//                                    }
//                                } else if($(this).attr('name') == "releasePathVersion" || $(this).attr('name') == "releasePathUrl" || $(this).attr('name') == "releaseFileName"){
//                                    if($(".w2ui-msg-body input[name=releaseFileName]").val() != "" ||  $(".w2ui-msg-body input[name='releasePathUrl']").val() != "" 
//                                        || $(".w2ui-msg-body input[name='releasePathVersion']").val() != ""){
//                                      checkValidation = true;
//                                    }else{
//                                      label = "릴리즈 다운 유형";
//                                      $(this).css({"border-color" : "red"});
//                                    }
//                                } else if( $(this).attr('name') == 'appSshFingerprint'){
//                                    checkValidation = true;
//                                } else if( $(this).attr('name') == 'cadvisorDriverIp' || $(this).attr('name') == 'cadvisorDriverPort' 
//                                    || $(this).attr('name') == 'ingestorIp' || $(this).attr('name') == 'ingestorPort') {
//                                    if( $("input[name=paastaMonitoring]:checkbox:checked").length == 0){
//                                        checkValidation = true;
//                                    }else{
//                                        label = $(this).parent().parent().find("label").text();
//                                        $(this).css({"border-color" : "red"}).parent().find(".isMessage").text(label + "를(을) 입력하세요").css({"color" : "red"});
//                                    }
//                                }else {
//                                    if ($(this).attr('name') != "deploymentName" ||  $(this).attr('name') != "stemcellPathVersion" || 
//                                            $(this).attr('name') != "stemcellPathFileName" || $(this).attr('name') != "stemcellPathUrl" ||
//                                            $(this).attr('name') != "releasePathVersion" || $(this).attr('name') != "releasePathUrl" || $(this).attr('name') != "releaseFileName") {
//                                        label = $(this).parent().parent().find("label").text();
//                                        $(this).css({"border-color" : "red"}).parent().find(".isMessage").text(label + "를(을) 입력하세요").css({"color" : "red"});
//                                    }
//                                    
//                                }
//                            }else if (inputType == 'password') {
//                                label = $(this).parent().parent().find("label").text();
//                                $(this).css({"border-color" : "red"}).parent().find(".isMessage").text(label + "를(을) 입력하세요").css({"color" : "red"});
//
//                            } else if (inputType == 'url') {
//                                label = $(this).parent().parent().find("label").text();
//                                $(this).css({"border-color" : "red"}).parent().find(".isMessage").text(label + "를(을) 입력하세요").css({"color" : "red"});
//
//                            } else if (inputType == 'list') {
//                                if ($(this).attr('name') == "keyPathList") {// 예외
//                                    label = "Private Key File";
//                                } else if($(this).attr('name') == "releaseIaasType"){
//                                    if($(".w2ui-msg-body input[name='releaseType']").val()!="BOSH_CPI"){
//                                        checkValidation = true;
//                                    }else{
//                                        label = "IaaS 유형";
//                                        $(this).css({"border-color" : "red"});
//                                    }
//                                } else {
//                                    label = $(this).parent().parent().find("label").text();
//                                }
//                                
//                            } else if (inputType == 'file') {
//                                if ($(this).attr('name') == "keyPathFile") {
//                                    label = "Private Key File";
//                                    $(this).css({"border-color" : "red"});
//                                } else if ($(this).attr('name') == "releasePathFile") {
//                                    label = "release path File";
//                                    $(this).css({"border-color" : "red"});
//                                }
//                            }
//                            if ($(this).attr('name') != "deploymentName" &&  $(this).attr('name') != "stemcellPathVersion" && 
//                                    $(this).attr('name') != "stemcellPathFileName" && $(this).attr('name') != "stemcellPathUrl"
//                                        && $(this).attr('name') != "releasePathVersion" && $(this).attr('name') != "releasePathUrl" && $(this).attr('name') != "releaseFileName") {
//                                $(this).css({"border-color" : "red"});
//                            }
//                        } else if (tagType.toLowerCase() == "textarea") {
//                            label = $(this).parent().parent().find('label').text();
//                            $(this).css({
//                                "border-color" : "red"
//                            });
//                        }
//                        if (label)
//                            emptyFieldLabels.push(label);
//                    }
//
//                    // 값이 있을 경우
//                    else if (elementName && elementValue) {
//                        if (tagType.toLowerCase() == "input") {
//                            if (inputType.toLowerCase() == "text") {
//                                if ($(this).attr('name') == 'email') {
//                                    if (emailValidation($(this).val())) {
//                                        $(this).css({"border" : "1px solid #bbb"}).parent().find(".isMessage").text("");
//                                    } else {
//                                        emailFieldLabels.push($(this));
//                                    }
//                                } else if ($(this).attr('name') == 'userId' || $(this).attr('name') == 'roleName'
//                                        || $(this).attr('name') == 'userName' || $(this).attr('name') == 'codeName'
//                                        || $(this).attr('name') == 'subCodeName' || $(this).attr('name') == 'subCodeNameKR') {
//                                    
//                                    if (!specialCharacterValidation($(this).val())) {
//                                        $(this).css({"border" : "1px solid #bbb"}).parent().find(".isMessage").text("");
//                                        if ($(this).attr('name') == 'roleName') {
//                                            if( textLengthValidation($(this).val()) ) {
//                                                $(this).css({"border" : "1px solid #bbb"}).parent().find(".isMessage").text("");
//                                            } else {
//                                                label = $(this).parent().parent().find("label").text();
//                                                $(this).css({"border-color" : "red"}).parent().find(".isMessage").text("2~15자 사이로 입력 해주세요.").css({"color" : "red"});
//                                                emptyFieldLabels.push(label);
//                                            }
//                                        }
//                                    } else {
//                                        label = $(this).parent().parent().find("label").text();
//                                        $(this).css({"border-color" : "red"}).parent().find(".isMessage").text(label + " 특수 문자 입력 불가").css({"color" : "red"});
//                                        emptyFieldLabels.push(label);
//                                    }
//                                } else if($(this).attr('name') == "stemcellPathFileName"){
//                                    if($(".w2ui-msg-body input[name=stemcellPathFileName]").val().indexOf($(".w2ui-msg-body input[name='iaasList']").val().toLowerCase())==-1){
//                                        label = "IaaS 유형";
//                                        emptyFieldLabels.push(label);
//                                    }
//                                    if($(".w2ui-msg-body input[name=stemcellPathFileName]").val().indexOf($(".w2ui-msg-body input[name='osList']").val().toLowerCase())==-1){
//                                        label = "OS 유형";
//                                        emptyFieldLabels.push(label);
//                                    }
//                                    var osVersion = "";
//                                    if($(".w2ui-msg-body input[name='osVersionList']").val() == "7.X"){
//                                        osVersion = "7";
//                                    }else{
//                                        osVersion = $(".w2ui-msg-body input[name='osVersionList']").val();
//                                    }
//                                    if($(".w2ui-msg-body input[name=stemcellPathFileName]").val().indexOf(osVersion.toLowerCase())==-1){
//                                        label = "OS 버전";
//                                        emptyFieldLabels.push(label);
//                                    }
//                                } else if($(this).attr('name') == "stemcellPathUrl"){
//                                    if($(".w2ui-msg-body input[name=stemcellPathUrl]").val().indexOf($(".w2ui-msg-body input[name='iaasList']").val().toLowerCase())==-1){
//                                        label = "IaaS 유형";
//                                        emptyFieldLabels.push(label);
//                                    }
//                                    if($(".w2ui-msg-body input[name=stemcellPathUrl]").val().indexOf($(".w2ui-msg-body input[name='osList']").val().toLowerCase())==-1){
//                                        label = "OS 유형";
//                                        emptyFieldLabels.push(label);
//                                    }
//                                    var osVersion = "";
//                                    if($(".w2ui-msg-body input[name='osVersionList']").val() == "7.X"){
//                                        osVersion = "7";
//                                    }else{
//                                        osVersion = $(".w2ui-msg-body input[name='osVersionList']").val();
//                                    }
//                                    if($(".w2ui-msg-body input[name=stemcellPathUrl]").val().indexOf(osVersion.toLowerCase())==-1){
//                                        label = "OS 버전";
//                                        emptyFieldLabels.push(label);
//                                    }
//                                } else if($(this).attr('name') == "cadvisorDriverPort" || $(this).attr('name') == 'ingestorPort'){
//                                    if(!onlyNumberPort(elementValue)){
//                                        label = "포트 번호";
//                                        $(this).css({"border-color" : "red"}).parent().find(".isMessage").text(label + "를(을) 확인하세요").css({"color" : "red"});
//                                        emptyFieldLabels.push(label);
//                                    }else{
//                                        $(this).css({"border" : "1px solid #bbb"}).parent().find(".isMessage").text("");
//                                    }
//                                } else if($(this).attr('name') == "cadvisorDriverIp" || $(this).attr('name') == 'ingestorIp'){
//                                    if(!validateIP(elementValue)){
//                                        label = "서버 IP"; 
//                                        $(this).css({"border-color" : "red"}).parent().find(".isMessage").text(label + "를(을) 확인하세요").css({"color" : "red"});
//                                        emptyFieldLabels.push(label);
//                                    }else {
//                                        $(this).css({"border" : "1px solid #bbb"}).parent().find(".isMessage").text("");
//                                    }
//                                }
//                                
//                            } else if (inputType.toLowerCase() == "list") {
//                                $(this).css({"border" : "1px solid #bbb"});// .parent().find(".isMessage").text("");
//                            } else if (inputType.toLowerCase() == "url") {
//                                if (validateIP(elementValue)) {
//                                    $(this).css({"border" : "1px solid #bbb"}).parent().find(".isMessage").text("");
//                                } else {
//                                    label = $(this).parent().parent().find("label").text();
//                                    $(this).css({"border-color" : "red"}).parent().find(".isMessage").text(label + "를(을) 입력하세요").css({"color" : "red"});
//                                    emptyFieldLabels.push(label);
//                                }
//                            } else if (inputType == 'file') {
//                                if ($(this).attr('name') == "keyPathFile") {
//                                    if ($.inArray($(this).val().split('.').pop().toLowerCase(), [ 'pem' ]) == -1) {
//                                        emptyFieldLabels.push("Empty Key File");
//                                    }
//                                }
//                            }
//                        } else if (tagType.toLowerCase() == "textarea") {
//                            $(this).css({"border" : "1px solid #bbb"});// .parent().find(".isMessage").text("");
//                        }
//                    }
//                });
//    }
//    if (emptyFieldLabels.length > 0) {
//        checkValidation = false;
//        if (emptyFieldLabels[0] == "Empty Key File") {
//            w2alert("KeyPath File은 .pem 파일만 등록 가능합니다.", $(".w2ui-msg-title b")
//                    .text());
//            return;
//        } else if (emptyFieldLabels[0] == "아이디") {
//            w2alert("아이디 특수문자 사용 불가", $(".w2ui-msg-title b").text());
//            return;
//        }
//        w2alert(emptyFieldLabels[0] + "을(를) 확인하세요.", $(".w2ui-msg-title b").text());
//
//    } else if (emailFieldLabels.length > 0) {
//        checkValidation = false;
//        if (emailFieldLabels[0].attr('name') == 'email') {
//            w2alert("이메일을(를) 확인하세요.", $(".w2ui-msg-title b").text());
//            emailFieldLabels[0].css({"border-color" : "red"}).parent().find(".isMessage").text("이메일을(를) 입력하세요").css({"color" : "red"});
//            return;
//        }
//    } else {
//        checkValidation = true;
//    }
//    return checkValidation;
//}

/*******************************************************************************
 * Popup NetworkValidation for vSphere
 ******************************************************************************/
function popupNetworkValidation() {
    var elements = $(".w2ui-box1 .w2ui-msg-body .w2ui-field input:visible, textarea:visible");
    var checkValidation = true;
    var emptyFieldLabels = null;
    var check = "";
    if (elements.length > 0) {
        emptyFieldLabels = new Array();
        
        elements.each(function(obj) {
                    var tagType = $(this).get(0).tagName;
                    var inputType = $(this).attr('type');
                    var elementName = $(this).attr('name');
                    var elementValue = $(this).val().trim();
                    var label = "";
                    if (elementName && !elementValue) {
                        if (tagType.toLowerCase() == "input") {
                            if (inputType == 'text') {
                                if ($(this).attr('name') == "publicStaticIp") {
                                    check = "true";
                                    return;
                                } else if (check == "true") {
                                    if ($(this).attr('name') == "publicSubnetId"
                                            || $(this).attr('name') == "publicSubnetRange"
                                            || $(this).attr('name') == "publicSubnetGateway"
                                            || $(this).attr('name') == "publicSubnetDns"
                                            || $(this).attr('name') == "publicNtp") {
                                        return true;
                                    }
                                } else {
                                    // 일반
                                    check = "";
                                    label = $(this).parent().parent().find("label").text();
                                    $(this).css({"border-color" : "red"});
                                    $(this).parent().parent()
                                            .find(".isMessage").text(label + "를(을) 입력하세요").css({"color" : "red"});
                                    $(this).on("change", function() {
                                        if (!checkEmpty($(this).val())) {
                                            $(this).css({"border-color" : "grey"}).parent().parent().find(".isMessage").text("OK").show().fadeOut();
                                        }
                                    });
                                }
                            }
                        }
                        if (label) emptyFieldLabels.push(label);
                    }

                    // 값이 있을 경우
                    else if (elementName && elementValue) {
                        if (tagType.toLowerCase() == "input") {
                            if (inputType.toLowerCase() == "text") {
                                if ($(this).attr('name') == "publicSubnetId"
                                        || $(this).attr('name') == "publicSubnetRange"
                                        || $(this).attr('name') == "publicSubnetGateway"
                                        || $(this).attr('name') == "publicSubnetDns"
                                        || $(this).attr('name') == "publicNtp") {

                                    if ($(".w2ui-msg-body input[name='publicStaticIp']").val().trim() == "" 
                                            || $(".w2ui-msg-body input[name='publicStaticIp']").val() == null) {
                                        
                                        // 디렉터공인 ip
                                        var label = "디렉터 공인 IP ";
                                        $(".w2ui-msg-body input[name='publicStaticIp']").css({"border-color" : "red"}).parent().find(".isMessage")
                                                .text(label + "를(을) 입력하세요").css({"color" : "red"});
                                        emptyFieldLabels.push(label);
                                    }
                                } else {
                                    $(this).css({"border-color" : "1px solid #bbb"}).parent().find(".isMessage").text("");
                                }
                            }
                        }
                    }
                });
    }
    if (emptyFieldLabels.length > 0) {
        checkValidation = false;
        w2alert(emptyFieldLabels[0] + "을(를) 확인하세요.", $(".w2ui-msg-title b").text());
    } else {
        checkValidation = true;
    }
    return checkValidation;
}


/********************************************************
 * 설명 : 디렉터 리스트 조회
 * 기능 : directorList
 *********************************************************/
function getDirectorList(){
    var directorArray = []
    $.ajax({
        type : "GET",
        url : "/common/use/hbDirector",
        async : true,
        success : function(data){
            var $object = jQuery("#directors");
            var directorList = "";
            if(data != null){
                data.map(function(obj){
                    directorArray.push(obj);
            });
                directorList = "<select name='select' id='directors' class='select' style='width:300px' onchange='doSearch(this.value);'>";
                directorList += "<option selected='selected' disabled='disabled' value='' style='color:gray'>디렉터를 선택하세요.</option>";
                for(var i=0; i<directorArray.length; i++){
                    directorList += "<option value='"+directorArray[i].iedaDirectorConfigSeq+"/"+directorArray[i].directorCpi+"'>"+directorArray[i].directorName+"("+directorArray[i].directorUrl+")"+"</option>\n";
                }
            }else{
                directorList = "<option selected='selected' disabled='disabled' value='' style='color:red'>디렉터가 존재하지 않습니다.</option>";
            }
            directorList += "</select>"
            $object.html(directorList);
        },error : function(xhr, status) {
               if(xhr.status==403){
                   location.href = "/abuse";
               }else{
                   var errorResult = JSON.parse(request.responseText);
                   w2alert(errorResult, "디렉터 조회");
               }
           }
    });
 }
