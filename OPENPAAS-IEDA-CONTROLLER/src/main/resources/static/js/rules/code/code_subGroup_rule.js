/**
 * 
 */
$(function() {
    $("#codeGroupForm").validate({
        ignore : "",
        onfocusout: true,
        rules: {
            codeName : {
                required : function(){ return checkEmpty( $(".w2ui-msg-body input[name='codeName']").val() ); }
            }, codeValue: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='codeValue']").val() ); }
            }, codeDescription: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body textarea[name='codeDescription']").val() ); }
            }
        }, messages: {
            codeName        : { required:  "코드 그룹명" + text_required_msg }
           ,codeValue       : { required:  "코드 그룹값"+text_required_msg }
           ,codeDescription : {  required: "설명"+text_required_msg }
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
            registCodeGroupInfo();
        }
    });
});
    