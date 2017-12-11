/**
 * 
 */
$(function(){
    $("#resourceInfoForm").validate({
        ignore : "",
        rules: {
            stemcell : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body select[name='stemcell']").val() );
                }
            }, boshPassword: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='boshPassword']").val() );
                }
            }, cloudInstanceType: { 
                required: function(){
                    if( $(".w2ui-msg-body .vsphereResourceDiv").css("display") == "none"  ){
                        return checkEmpty( $(".w2ui-msg-body input[name='cloudInstanceType']").val() );
                    } else return false;
                }
            }, resourcePoolCpu: { 
                required: function(){
                    if( $(".w2ui-msg-body .vsphereResourceDiv").css("display") != "none"  ){
                        return checkEmpty( $(".w2ui-msg-body input[name='resourcePoolCpu']").val() );
                    }else return false;
                }
            }, resourcePoolRam: { 
                required: function(){
                    if( $(".w2ui-msg-body .vsphereResourceDiv").css("display") != "none"  ){
                        return checkEmpty( $(".w2ui-msg-body input[name='resourcePoolRam']").val() );
                    }else return false;
                }
            }, resourcePoolDisk: { 
                required: function(){
                    if( $(".w2ui-msg-body .vsphereResourceDiv").css("display") != "none"  ){
                        return checkEmpty( $(".w2ui-msg-body input[name='resourcePoolDisk']").val() );
                    }else return false;
                }
            }
        }, messages: {
            stemcell: { 
                required:  "스템셀"+text_required_msg
            }, boshPassword: { 
                required:  "VM 비밀번호"+text_required_msg,
            }, cloudInstanceType: { 
                required:  "인스턴스 유형" + select_required_msg,
            }, resourcePoolCpu: { 
                required:  "리소스 풀 CPU"+select_required_msg,
            }, resourcePoolRam: { 
                required:  "리소스 풀 RAM"+text_required_msg
            }, resourcePoolDisk: { 
                required:  "리소스 풀 DISK"+text_required_msg
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
            saveResourceInfo('after');
        }
    });
});