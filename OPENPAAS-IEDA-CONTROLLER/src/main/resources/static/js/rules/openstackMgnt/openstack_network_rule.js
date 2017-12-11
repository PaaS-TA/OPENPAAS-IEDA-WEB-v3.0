/**
 * 
 */
$(function() {
    //networkRules
    $("#openstackNetworkForm").validate({
        ignore : "",
        onfocusout: true,
        rules: {
            networkName : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='networkName']").val().trim() );
                },sqlInjection : function(){
                    return $(".w2ui-msg-body input[name='networkName']").val();
                }
            },
            subnetName : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='subnetName']").val().trim() );
                },sqlInjection : function(){
                    return $(".w2ui-msg-body input[name='subnetName']").val();
                }
            },
            networkAddress : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='networkAddress']").val().trim() );
                }, ipv4Range : function(){
                    if( $(".w2ui-msg-body select[name='ipVersion']").val() == "IPv4" ){
                        return $(".w2ui-msg-body input[name='networkAddress']").val().trim();
                    }else { 
                        return false; 
                    }
                }
            },
            gatewayIp : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='gatewayIp']").val().trim() );
                }, ipv4 : function(){
                    if( $(".w2ui-msg-body select[name='ipVersion']").val().trim() == "IPv4" ){
                        return $(".w2ui-msg-body input[name='gatewayIp']").val().trim();
                    }else { 
                        return false; 
                    }
                }
            },
            dnsNameServers : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body textarea[name='dnsNameServers']").val().trim() );
                }, ipv4 : function(){
                    if( $(".w2ui-msg-body textarea[name='dnsNameServers']").val().indexOf("\n") > -1 ){
                        var list = ($(".w2ui-msg-body textarea[name='dnsNameServers']").val()).split("\n");
                        var flag = true;
                        for( var i=0; i<list.length; i++ ){
                            var val = validateIpv4(list[i]);
                            if( !val ){
                                flag = false;
                            }
                        }
                        if( !flag ){
                            return "false";
                        }else{
                            return list[0];
                        }
                    }else{
                        return $(".w2ui-msg-body textarea[name='dnsNameServers']").val();
                    }
                }
            },
        }, messages: {
            networkName: { 
                 required:  "Network Name" + text_required_msg
           }, 
            subnetName: { 
                required:  "Subnet Name" + text_required_msg
           },
           networkAddress: { 
                required:  "Network Address" + text_required_msg
           }, 
           gatewayIp: { 
               required:  "Gateway Ip" + text_required_msg
           }, 
           dnsNameServers: { 
               required:  "Dns Name Servers" + text_required_msg
           }, 
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
            saveOpenstackNetworkInfo();
        }
    });
    
    //subnet rules
    $("#settingForm").validate({
        ignore : "",
        onfocusout: true,
        rules: {
            networkName : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='networkName']").val().trim() );
                },sqlInjection : function(){
                    return $(".w2ui-msg-body input[name='networkName']").val();
                }
            },
            subnetName : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='subnetName']").val().trim() );
                },sqlInjection : function(){
                    return $(".w2ui-msg-body input[name='subnetName']").val();
                }
            },
            networkAddress : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='networkAddress']").val().trim() );
                }, ipv4Range : function(){
                    return $(".w2ui-msg-body input[name='networkAddress']").val().trim();
                }
            },
            gatewayIp : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='gatewayIp']").val().trim() );
                }, ipv4 : function(){
                    return $(".w2ui-msg-body input[name='gatewayIp']").val().trim();
                }
            },
            dnsNameServers : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body textarea[name='dnsNameServers']").val().trim() );
                }, ipv4 : function(){
                    if( $(".w2ui-msg-body textarea[name='dnsNameServers']").val().indexOf("\n") > -1 ){
                        var list = ($(".w2ui-msg-body textarea[name='dnsNameServers']").val()).split("\n");
                        var flag = true;
                        for( var i=0; i<list.length; i++ ){
                            var val = validateIpv4(list[i]);
                            if( !val ){
                                flag = false;
                            }
                        }
                        if( !flag ){
                            return "false";
                        }else{
                            return list[0];
                        }
                    }else{
                        return $(".w2ui-msg-body textarea[name='dnsNameServers']").val();
                    }
                }
            },
        }, messages: {
            networkName: { 
                 required:  "Network Name" + text_required_msg
           }, 
            subnetName: { 
                required:  "Subnet Name" + text_required_msg
           },
           networkAddress: { 
                required:  "Network Address" + text_required_msg
           }, 
           gatewayIp: { 
               required:  "Gateway Ip" + text_required_msg
           }, 
           dnsNameServers: { 
               required:  "Dns Name Servers" + text_required_msg
           }, 
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
            saveOpenstackSubnetInfo();
        }
    });
});

function validateIpv4(params){
    return /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(params);
}