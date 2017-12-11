/**
 * 
 */
$(function() {
    $('#openstackRouterForm').validate({
        ignore: "",
        onfocusout: true,
        rules: {
            routerName :{
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='routerName']").val());
                }
            }
        },
        messages: {
            routerName: {
                required: "Router Name" + text_required_msg
            }
        },
        unhighlight: function(element) {
            setSuccessStyle(element);
        },
        errorPlacement: function(event, element) {
            //do nothing
        },
        invalidHandler: function(event, validator){
            var errors = validator.numberOfInvalids();
            if (errors) {
                setInvalidHandlerStyle(errors, validator);
            }
        },
        submitHandler: function(form){
            w2popup.lock(save_lock_msg, true);
            saveOpenstackRouter();
        }
    });
     $('#settingInterfaceForm').validate({
        ignore: "",
        onfocusout: true,
        rules: {
            subnetInterfaceIpAddress: {
                ipv4: function(){
                    return $(".w2ui-msg-body input[name='subnetInterfaceIpAddress']").val();
                }
            }
        },
        messages: {
            
        },
        unhighlight: function(element) {
            setSuccessStyle(element);
        },
        errorPlacement: function(event, element) {
            //do nothing
        },
        invalidHandler: function(event, validator) {
            var errors = validator.numberOfInvalids();
            if (errors) {
                setInvalidHandlerStyle(errors, validator);
            }
        },
        submitHandler: function(form) {
            w2popup.lock(save_lock_msg, true);
            openstackAttachInterface();//연결함수.
        }
    });
     $('#openstackRouterGatewaySettingForm').validate({
         ignore: "",
         onfocusout: true,
         unhighlight: function(element) {
             setSuccessStyle(element);
         },
         errorPlacement: function(event, element) {
             //do nothing
         },
         invalidHandler: function(event, validator) {
             var errors = validator.numberOfInvalids();
             if (errors) {
                 setInvalidHandlerStyle(errors, validator);
             }
         },
         submitHandler: function(form) {
             openstackRouterGatewayAttatch();//연결함수.
         }
     });
});
    