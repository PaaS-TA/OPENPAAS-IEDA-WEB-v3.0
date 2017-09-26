/**
 * 
 */
$(function() {
	$("#KeyInfoForm").validate({
        ignore : "",
        rules:{
        	cfDomain : {
                required : function(){ return checkEmpty( $(".w2ui-msg-body input[name='cfDomain']").val() ); }
            },countryCode : {
                required : function(){ return checkEmpty( $(".w2ui-msg-body select[name='countryCode']").val() ); }
            },stateName : {
                required : function(){ return checkEmpty( $(".w2ui-msg-body input[name='stateName']").val() ); }
            },localityName : {
                required : function(){ return checkEmpty( $(".w2ui-msg-body input[name='localityName']").val() );}
            }, organizationName: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='organizationName']").val() ); }
            }, unitName: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='unitName']").val() ); }
            }, email: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='email']").val() ); }
            }
        }, messages: {
             cfDomain         : { required: "도메인"+text_required_msg }
            ,countryCode      : { required: "국가 코드"+select_required_msg }
            ,stateName        : { required: "시/도"+text_required_msg }
            ,localityName     : { required: "시/구/군"+text_required_msg }
            ,organizationName : { required: "회사명"+text_required_msg }
            , unitName        : { required: "부서명"+text_required_msg }
            , email           : { required: "Email" + text_required_msg }
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
        	if( keyInfo.status == "Y" ){
        		createKeyInfo();
        	}else{
        		saveKeyInfo('after');	
        	}
        }
    });
});
    