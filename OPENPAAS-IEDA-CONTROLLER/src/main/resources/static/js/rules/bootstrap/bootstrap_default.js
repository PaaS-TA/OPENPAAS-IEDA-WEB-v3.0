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
            },  credentialKeyName: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body select[name='credentialKeyName']").val() );
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
            }, boshBpmRelease: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body select[name='boshBpmRelease']").val() );
                }
            }, osConfRelease: { 
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body select[name='osConfRelease']").val() );
                }
            }, snapshotSchedule: { 
                required: function(){
                    if( $(".w2ui-msg-body input:radio[name=enableSnapshots]:checked").val() == "true"){
                        return checkEmpty( $(".w2ui-msg-body input[name='snapshotSchedule']").val() );
                    }else{ 
                        return false;
                    }
                }
            }, syslogAddress : {
                  required: function(){
                      if( $(".w2ui-msg-body input:checkbox[name=paastaMonitoring]").is(":checked") ){
                           return checkEmpty( $(".w2ui-msg-body input[name='syslogAddress']").val() );
                      }else{
                           return false;
                      }
                 },ipv4 : function(){
                      if( $(".w2ui-msg-body input:checkbox[name=paastaMonitoring]").is(":checked") ){
                           return $(".w2ui-msg-body input[name='syslogAddress']").val();
                      }else{
                           return "0.0.0.0";
                      }
                 }
            }, paastaMonitoringAgentRelease : {
                  required: function(){
                      if( $(".w2ui-msg-body input:checkbox[name=paastaMonitoring]").is(":checked") ){
                           return checkEmpty( $(".w2ui-msg-body select[name='paastaMonitoringAgentRelease']").val() );
                      }else{
                           return false;
                      }
                  }
            }, paastaMonitoringSyslogRelease : {
                required: function(){
                    if( $(".w2ui-msg-body input:checkbox[name=paastaMonitoring]").is(":checked") ){
                         return checkEmpty( $(".w2ui-msg-body select[name='paastaMonitoringSyslogRelease']").val() );
                    }else{
                         return false;
                    }
                }
            }, metricUrl : {
                required: function(){
                    if( $(".w2ui-msg-body input:checkbox[name=paastaMonitoring]").is(":checked") ){
                         return checkEmpty( $(".w2ui-msg-body input[name='metricUrl']").val() );
                    }else{
                         return false;
                    }
                }
            }, syslogPort : {
                required: function(){
                	console.log($(".w2ui-msg-body input:checkbox[name=paastaMonitoring]").val());
                    if( $(".w2ui-msg-body input:checkbox[name=paastaMonitoring]").is(":checked") ){
                         return checkEmpty( $(".w2ui-msg-body input[name='syslogPort']").val() );
                    }else{
                         return false;
                    }
                }
            }, syslogTransport : {
                required: function(){
                    if( $(".w2ui-msg-body input:checkbox[name=paastaMonitoring]").is(":checked") ){
                         return checkEmpty( $(".w2ui-msg-body input[name='syslogTransport']").val() );
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
            }, credentialKeyName: { 
                required:  "디렉터 인증서"+select_required_msg
            }, ntp: { 
                required:  "npt"+text_required_msg
            }, boshRelease: { 
                required:  "BOSH 릴리즈" + select_required_msg
            }, boshCpiRelease: { 
                required:  "BOSH CPI 릴리즈"+select_required_msg
            }, boshBpmRelease: { 
                required:  "BOSH BPM 릴리즈"+select_required_msg
            }, osConfRelease: { 
                required:  "BOSH OS_CONF 릴리즈"+select_required_msg
            }, snapshotSchedule: { 
                required:  "스냅샷 스케쥴"+text_required_msg
            }, syslogAddress: {
                required: "Syslog Address"+text_required_msg
            }, paastaMonitoringAgentRelease: {
                required: "Monitoring Agent 릴리즈"+select_required_msg
            }, paastaMonitoringSyslogRelease: {
                required: "Monitoring Syslog 릴리즈"+select_required_msg
            }, metricUrl: {
                required: "Metric Url"+text_required_msg
            }, syslogPort: {
                required: "Syslog Port"+text_required_msg
            }, syslogTransport: {
                required: "Syslog Transport"+text_required_msg
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
    