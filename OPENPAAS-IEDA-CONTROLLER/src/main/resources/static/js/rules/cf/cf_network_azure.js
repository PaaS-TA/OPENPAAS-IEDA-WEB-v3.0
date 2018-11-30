$(function() {
	$("#azureNetworkInfoForm").validate({
        ignore : "",
        rules:{
        	publicStaticIp : {
                ipv4     : function(){ return $(".w2ui-msg-body input[name='publicStaticIp']").val(); }
            },networkName_1 : {
	            required : function(){ return checkEmpty( $(".w2ui-msg-body input[name='networkName_1']").val() ); }
	        },subnetId_1 : {
	            required : function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetId_1']").val() ); }
	        },cloudSecurityGroups_1 : {
	            required : function(){ return checkEmpty( $(".w2ui-msg-body input[name='cloudSecurityGroups_1']").val() ); }
	        }, subnetRange_1: { 
	            required : function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetRange_1']").val() ); }
	           ,ipv4Range: function(){ return $(".w2ui-msg-body input[name='subnetRange_1']").val(); }
	        }, subnetGateway_1: { 
	            required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetGateway_1']").val() ); }
	           ,ipv4    : function(){ return $(".w2ui-msg-body input[name='subnetGateway_1']").val(); }
	        }, subnetDns_1: { 
	            required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetDns_1']").val() ); }
	           ,ipv4    : function(){
	               if( $(".w2ui-msg-body input[name='subnetDns_1']").val().indexOf(",") > -1 ){
                       var list = ($(".w2ui-msg-body input[name='subnetDns_1']").val()).split(",");
                       var flag = true;
                       for( var i=0; i<list.length; i++ ){
                           var val = validateIpv4(list[i].trim());
                           if( !val ) flag = false;
                       }
                       if( !flag ) return "false";
                       else return list[0].trim();
                   }else{
                       return $(".w2ui-msg-body input[name='subnetDns_1']").val();
                   }
	           }
	        }, subnetReservedFrom_1: { 
	            required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetReservedFrom_1']").val() ); }
	           ,ipv4    : function(){ return $(".w2ui-msg-body input[name='subnetReservedFrom_1']").val(); }
	        }, subnetReservedTo_1: { 
	            required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetReservedTo_1']").val() ); }
	           ,ipv4    : function(){ return $(".w2ui-msg-body input[name='subnetReservedTo_1']").val(); }
	        }, subnetStaticFrom_1: { 
	            required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetStaticFrom_1']").val() ); }
	           ,ipv4    : function(){ return $(".w2ui-msg-body input[name='subnetStaticFrom_1']").val(); }
	        }, subnetStaticTo_1: { 
	            required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetStaticTo_1']").val() ); } 
	           ,ipv4    : function(){ return $(".w2ui-msg-body input[name='subnetStaticTo_1']").val(); }
	        }
	     },messages: {
	    	  publicStaticIp       : { required: "CF API TARGET IP"+text_required_msg }
	    	 ,networkName_1        : { required: "네트워크 명"+text_required_msg } 
	         ,subnetId_1           : { required: "서브넷 명"+text_required_msg }
	         ,cloudSecurityGroups_1: { required: "보안 그룹"+text_required_msg }
	         ,subnetRange_1        : { required: "서브넷 범위"+text_required_msg }
	         ,subnetGateway_1      : { required: "게이트웨이"+text_required_msg }
	         ,subnetDns_1          : { required: "DNS" + text_required_msg }
	         ,subnetReservedFrom_1 : { required: "IP할당 제외 대역"+text_required_msg }
	         ,subnetReservedTo_1   : { required: "IP할당 제외 대역"+text_required_msg }
	         ,subnetStaticFrom_1   : { required: "IP할당 대역"+text_required_msg }
	         ,subnetStaticTo_1     : { required: "IP할당 대역"+text_required_msg }
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
            saveNetworkInfo('after', $(form).attr("id"));
         }
    });
});

function validateIpv4(params){
    return /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(params);
}