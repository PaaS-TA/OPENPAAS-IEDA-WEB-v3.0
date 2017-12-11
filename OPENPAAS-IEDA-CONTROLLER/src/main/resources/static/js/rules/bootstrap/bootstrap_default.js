/**
 * 
 */
$(function() {
    $("#defaultInfoForm").validate({
        ignore : [],
        onfocusout: true,
        rules: {
            deploymentName : {
                required : function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='deploymentName']").val() );
                }
            }, directorName: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='directorName']").val() );
                }
            }, ntp: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='ntp']").val() );
                }
            }, boshRelease: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body select[name='boshRelease']").val() );
                }
            }, boshCpiRelease: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body select[name='boshCpiRelease']").val() );
                }
            }, snapshotSchedule: { 
                required: function(){
                    if( $(".w2ui-msg-body input:radio[name=enableSnapshots]:checked").val() == "true"){
                        return checkEmpty( $(".w2ui-msg-body input[name='snapshotSchedule']").val() );
                    }else{ 
                        return false;
                    }
                }
            }, ingestorIp : {
                  required: function(){
                      if( $(".w2ui-msg-body #paastaMonitoring:checked").val() == "on"){
                           return checkEmpty( $(".w2ui-msg-body input[name='ingestorIp']").val() );
                      }else{
                           return false;
                      }
                 },ipv4 : function(){
                      if( $(".w2ui-msg-body #paastaMonitoring:checked").val() == "on"){
                           return $(".w2ui-msg-body input[name='ingestorIp']").val()
                      }else{
                           return "0.0.0.0";
                      }
                 }
            }, paastaMonitoringRelease : {
                  required: function(){
                      if( $(".w2ui-msg-body #paastaMonitoring:checked").val() == "on"){
                           return checkEmpty( $(".w2ui-msg-body select[name='paastaMonitoringRelease']").val() );
                      }else{
                           return false;
                      }
                  }
            }
        }, messages: {
            deploymentName: { 
                 required:  "배포명" + text_required_msg
            }, directorName: { 
                required:  "디렉터명"+text_required_msg
            }, ntp: { 
                required:  "npt"+text_required_msg
            }, boshRelease: { 
                required:  "BOSH 릴리즈" + select_required_msg
            }, boshCpiRelease: { 
                required:  "BOSH CPI 릴리즈"+select_required_msg
            }, snapshotSchedule: { 
                required:  "스냅샷 스케쥴"+text_required_msg
            }, ingestorIp: {
                required: "모니터링 Ip"+text_required_msg
            }, paastaMonitoringRelease: {
            	required: "모니터링 릴리즈"+select_required_msg
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
            saveDefaultInfo('after');
        }
    });
});
    