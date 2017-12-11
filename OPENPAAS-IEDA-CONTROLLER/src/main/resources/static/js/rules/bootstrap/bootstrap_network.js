/**
 * 
 */
function setNetworkValidate(iaas){
    $(iaas).validate({
        ignore : "",
        rules: {
            subnetId : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='subnetId']").val() );
                }
            }, networkName: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='networkName']").val() );
                }
            }, privateStaticIp: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='privateStaticIp']").val() );
                },ipv4 : function(){
                    return $(".w2ui-msg-body input[name='privateStaticIp']").val()
                }
            }, publicStaticIp: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='publicStaticIp']").val() );
                },ipv4 : function(){
                    return $(".w2ui-msg-body input[name='publicStaticIp']").val()
                }
            }, subnetRange: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='subnetRange']").val() );
                },ipv4Range : function(){
                    return $(".w2ui-msg-body input[name='subnetRange']").val()
                }
            }, subnetGateway: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='subnetGateway']").val() );
                },ipv4 : function(){
                    return $(".w2ui-msg-body input[name='subnetGateway']").val()
                }
            }, subnetDns: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='subnetDns']").val() );
                },ipv4  : function(){ 
                    if( $(".w2ui-msg-body input[name='subnetDns']").val().indexOf(",") > -1 ){
                        var list = ($(".w2ui-msg-body input[name='subnetDns']").val()).split(",");
                        var flag = true;
                        for( var i=0; i<list.length; i++ ){
                            var val = validateIpv4(list[i].trim());
                            if( !val ) flag = false;
                        }
                        if( !flag ) return "";
                        else return list[0].trim();
                    }else{
                        return $(".w2ui-msg-body input[name='subnetDns']").val();
                    }
                }
            }, publicSubnetId: { 
                required: function(){
                    if( $("input[name='iaasType']").val() == "vSphere" ){
                        return checkEmpty( $(".w2ui-msg-body input[name='publicSubnetId']").val() );
                    }else return false;
                }
            }, publicSubnetRange: { 
                required: function(){
                    if( $("input[name='iaasType']").val() == "vSphere" ){
                        return checkEmpty( $(".w2ui-msg-body input[name='publicSubnetRange']").val() );
                    }else return false;
                },ipv4Range : function(){
                    if( $("input[name='iaasType']").val() == "vSphere" ){
                        return $(".w2ui-msg-body input[name='publicSubnetRange']").val()
                    }else return false;
                }
            }, publicSubnetGateway: { 
                required: function(){
                    if( $("input[name='iaasType']").val() == "vSphere" ){
                        return checkEmpty( $(".w2ui-msg-body input[name='publicSubnetGateway']").val() );
                    }else return false;
                },ipv4 : function(){
                    if( $("input[name='iaasType']").val() == "vSphere" ){
                        return $(".w2ui-msg-body input[name='publicSubnetGateway']").val()
                    }else return false;
                }
            }, publicSubnetDns: { 
                required: function(){
                    if( $("input[name='iaasType']").val() == "vSphere" ){
                        return checkEmpty( $(".w2ui-msg-body input[name='publicSubnetDns']").val() );
                    }else return false;
                },ipv4 : function(){
                    if( $("input[name='iaasType']").val() == "vSphere" ){
                        return $(".w2ui-msg-body input[name='subnetDns']").val()
                    }else return false;
                }
            }
        }, messages: {
            subnetId: { 
                required: $(".w2ui-msg-body input[name='subnetId']").parent().parent().find("label").text()+text_required_msg
            }, networkName: { 
                required:  "네트워크 명"+text_required_msg
            }, privateStaticIp: { 
                required:  "설치관리자 내부망 IPs"+text_required_msg,
            }, publicStaticIp: { 
                required:  "설치관리자 IPs" + text_required_msg,
            }, subnetRange: { 
                required:  "서브넷 범위"+select_required_msg,
            }, subnetGateway: { 
                required:  "게이트웨이"+text_required_msg
            }, subnetDns: { 
                required:  "DNS"+text_required_msg
            }, publicSubnetId: { 
                required:  "포트 그룹명"+text_required_msg,
            }, publicSubnetRange: { 
                required:  "서브넷 범위"+text_required_msg
            }, publicSubnetGateway: { 
                required:  "게이트웨이"+text_required_msg
            }, publicSubnetDns: { 
                required:  "DNS"+text_required_msg
            }
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
            w2popup.lock(save_lock_msg, true);
            saveNetworkInfo('after');
        }
    });
    
    $(iaas).submit();
}

function validateIpv4(params){
    return /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(params);
}