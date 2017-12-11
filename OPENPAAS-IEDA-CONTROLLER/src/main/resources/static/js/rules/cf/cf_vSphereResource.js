/**
 * 
 */
$(function(){
	$("#vSphereResourceInfoForm").validate({
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
            },smallFlavorRam : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='smallFlavorRam']").val() );
                }
            },smallFlavorDisk : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='smallFlavorDisk']").val() );
                }
            },smallFlavorCpu : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='smallFlavorCpu']").val() );
                }
            },mediumFlavorRam : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='mediumFlavorRam']").val() );
                }
            },mediumFlavorDisk : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='mediumFlavorDisk']").val() );
                }
            },mediumFlavorCpu : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='mediumFlavorCpu']").val() );
                }
            }, largeFlavorRam: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='largeFlavorRam']").val() );
                }
            }, largeFlavorDisk: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='largeFlavorDisk']").val() );
                }
            }, largeFlavorCpu: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='largeFlavorCpu']").val() );
                }
            }, runnerFlavorRam: { 
                required: function(){
                    if( $(".w2ui-msg-body #runnerTypeDiv").css("display") == "none" ){
                        return false;
                    }else{
                        return checkEmpty( $(".w2ui-msg-body input[name='runnerFlavorRam']").val() );
                    }
                }
            }, runnerFlavorDisk: { 
                required: function(){
                    if( $(".w2ui-msg-body #runnerTypeDiv").css("display") == "none" ){
                        return false;
                    }else{
                        return checkEmpty( $(".w2ui-msg-body input[name='runnerFlavorDisk']").val() );
                    }
                }
            }, runnerFlavorCpu: { 
                required: function(){
                    if( $(".w2ui-msg-body #runnerTypeDiv").css("display") == "none" ){
                        return false;
                    }else{
                        return checkEmpty( $(".w2ui-msg-body input[name='runnerFlavorCpu']").val() );
                    }
                }
            }
        }, messages: {
        	stemcells:{
                required: "스템셀"+select_required_msg
            },boshPassword:{
                required: "VM 비밀번호"+select_required_msg
            },smallFlavorRam:{
                required: "smallFlavorRam"+text_required_msg
            },smallFlavorDisk:{
                required: "smallFlavorDisk"+text_required_msg
            },smallFlavorCpu:{
                required: "smallFlavorCpu"+text_required_msg
            }, mediumFlavorRam: {
                required: "mediumFlavorRam"+text_required_msg
            }, mediumFlavorDisk: {
                required: "mediumFlavorDisk"+text_required_msg
            }, mediumFlavorCpu: {
                required: "mediumFlavorCpu"+text_required_msg
            }, largeFlavorRam: {
                required: "largeFlavorRam"+text_required_msg
            }, largeFlavorDisk: {
                required: "largeFlavorDisk"+text_required_msg
            }, largeFlavorCpu: {
                required: "largeFlavorCpu"+text_required_msg
            }, runnerFlavorRam: {
                required: "runnerFlavor" + text_required_msg
            }, runnerFlavorDisk: {
                required: "runnerFlavorDisk" + text_required_msg
            }, runnerFlavorCpu: {
                required: "runnerFlavorCpu" + text_required_msg
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