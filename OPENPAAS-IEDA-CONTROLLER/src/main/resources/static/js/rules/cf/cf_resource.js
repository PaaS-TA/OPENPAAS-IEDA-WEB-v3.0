/**
 * 
 */
$(function(){
	$("#resourceInfoForm").validate({
        ignore : "",
        rules:{
        	stemcells : { 
        		required : function(){
        			return checkEmpty( $(".w2ui-msg-body select[name='stemcells']").val() );
        		}
            },boshPassword : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='boshPassword']").val() );
                }
            },smallFlavor : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='smallFlavor']").val() );
                }
            },mediumFlavor : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='mediumFlavor']").val() );
                }
            }, largeFlavor: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='largeFlavor']").val() );
                }
            }, runnerFlavor: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='runnerFlavor']").val() );
                }
            }
        }, messages: {
        	stemcells:{
                required: "스템셀"+select_required_msg
            },boshPassword:{
                required: "VM 비밀번호"+select_required_msg
            },smallFlavor:{
                required: "smallFlavor"+text_required_msg
            }, mediumFlavor: {
                required: "mediumFlavor"+text_required_msg
            }, largeFlavor: {
                required: "largeFlavor"+text_required_msg
            }, runnerFlavor: {
                required: "runnerFlavor" + text_required_msg
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
        	saveResourceInfo('after');
        }
    });
});