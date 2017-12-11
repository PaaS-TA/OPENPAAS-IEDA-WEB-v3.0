/**
 * 
 */
$(function() {
    $("#codeForm").validate({
        ignore : "",
        onfocusout: true,
        rules: {
            subCodeName: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subCodeName']").val() ); }
            }, subCodeNameKR: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subCodeNameKR']").val() ); }
            }, subCodeValue: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subCodeValue']").val() ); }
            }, subCodeDescription: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body textarea[name='subCodeDescription']").val() ); }
            }
        }, messages: {
            subCodeName        : { required:  "코드명(영문)"+text_required_msg }
           ,subCodeNameKR      : {  required: "코드명(한글)"+text_required_msg }
           ,subCodeValue       : { required:  "코드값"+text_required_msg }
           ,subCodeDescription : {  required: "설명"+text_required_msg }
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
            saveCodeInfo();
        }
    });
});
    