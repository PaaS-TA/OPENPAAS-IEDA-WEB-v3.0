/**
 * 
 */
$(function(){
    $("#azureResourceInfoForm").validate({
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
            }, windowsStemcells: {
                required: function(){
                    if( $(".w2ui-msg-body #windowsStemcellConfDiv").css("display") == "none"){
                        return false;
                    }else{
                        return checkEmpty( $(".w2ui-msg-body select[name='windowsStemcells']").val() );
                    }
                }
            }, windowsCellInstance: {
                required: function(){
                    if( $(".w2ui-msg-body #windowsStemcellConfDiv").css("display") == "none"){
                        return false;
                    }else{
                    	console.log(checkEmpty( $(".w2ui-msg-body input[name='windowsCellInstance']").val()));
                        if ( checkEmpty( $(".w2ui-msg-body input[name='windowsCellInstance']").val()) ){
                            return true;
                        }
                        var num = $(".w2ui-msg-body input[name='windowsCellInstance']").val();
                        num = Number(num);
                        if( num > 100){
                            console.log("num > 100");
                            return true;
                        }else{
                            console.log("num < 100");
                            return false;
                        }
                    }
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
            }, windowsStemcells : {
                required: "windowsStemcell" +text_required_msg
            }, windowsCellInstance : {
                required: "1 부터 100까지 숫자만 입력 가능 합니다."
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