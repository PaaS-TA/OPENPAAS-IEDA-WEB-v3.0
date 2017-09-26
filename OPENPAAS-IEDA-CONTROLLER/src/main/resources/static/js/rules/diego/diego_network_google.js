/**
 * 
 */
$(function() {
	$("#googleNetworkInfoForm").validate({
        ignore : "",
        rules:{
        	networkName_0 : {
	            required : function(){ return checkEmpty( $(".w2ui-msg-body input[name='networkName_0']").val() ); }
	        },subnetId_0 : {
	            required : function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetId_0']").val() ); }
	        },cloudSecurityGroups_0 : {
	            required : function(){ return checkEmpty( $(".w2ui-msg-body input[name='cloudSecurityGroups_0']").val() ); }
	        },availabilityZone_0 : {
	            required : function(){ return checkEmpty( $(".w2ui-msg-body input[name='availabilityZone_0']").val() ); }
	        }, subnetRange_0: { 
	            required : function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetRange_0']").val() ); }
	           ,ipv4Range: function(){ return $(".w2ui-msg-body input[name='subnetRange_0']").val(); }
	        }, subnetGateway_0: { 
	            required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetGateway_0']").val() ); }
	           ,ipv4    : function(){ return $(".w2ui-msg-body input[name='subnetGateway_0']").val(); }
	        }, subnetDns_0: { 
	            required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetDns_0']").val() ); }
	           ,ipv4    : function(){
	               if( $(".w2ui-msg-body input[name='subnetDns_0']").val().indexOf(",") > -1 ){
                       var list = ($(".w2ui-msg-body input[name='subnetDns_1']").val()).split(",");
                       var flag = true;
                       for( var i=0; i<list.length; i++ ){
                           var val = validateIpv4(list[i].trim());
                           if( !val ) flag = false;
                       }
                       if( !flag ) return "";
                       else return list[0].trim();
                   }else{
                       return $(".w2ui-msg-body input[name='subnetDns_0']").val();
                   }
	           }
	        }, subnetReservedFrom_0: { 
	            required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetReservedFrom_0']").val() ); }
	           ,ipv4    : function(){ return $(".w2ui-msg-body input[name='subnetReservedFrom_0']").val(); }
	        }, subnetReservedTo_0: { 
	            required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetReservedTo_0']").val() ); }
	           ,ipv4    : function(){ return $(".w2ui-msg-body input[name='subnetReservedTo_0']").val(); }
	        }, subnetStaticFrom_0: { 
	            required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetStaticFrom_0']").val() ); }
	           ,ipv4    : function(){ return $(".w2ui-msg-body input[name='subnetStaticFrom_0']").val(); }
	        }, subnetStaticTo_0: { 
	            required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetStaticTo_0']").val() ); } 
	           ,ipv4    : function(){ return $(".w2ui-msg-body input[name='subnetStaticTo_0']").val(); }
	        }
	     },messages: {
	    	  networkName_0        : { required: "네트워크 명"+text_required_msg } 
	         ,subnetId_0           : { required: "서브넷 명"+text_required_msg }
	         ,cloudSecurityGroups_0: { required: "방화벽 규칙"+text_required_msg }
	         ,availabilityZone_0   : { required: "영역"+text_required_msg }
	         ,subnetRange_0        : { required: "서브넷 범위"+text_required_msg }
	         ,subnetGateway_0      : { required: "게이트웨이"+text_required_msg }
	         ,subnetDns_0          : { required: "DNS" + text_required_msg }
	         ,subnetReservedFrom_0 : { required: "IP할당 제외 대역"+text_required_msg }
	         ,subnetReservedTo_0   : { required: "IP할당 제외 대역"+text_required_msg }
	         ,subnetStaticFrom_0   : { required: "IP할당 대역"+text_required_msg }
	         ,subnetStaticTo_0     : { required: "IP할당 대역"+text_required_msg }
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