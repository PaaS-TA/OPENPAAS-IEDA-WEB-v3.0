$(function() {
    $("#defaultInfoForm").validate({
            ignore : [],
            onfocusout: true,
            rules: {
                directorUuid : {
                    required : function(){ return checkEmpty( $(".w2ui-msg-body input[name='directorUuid']").val() ); }
                }, deploymentName: { 
                    required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='deploymentName']").val() ); }
                }, diegoReleases: { 
                    required: function(){ return checkEmpty( $(".w2ui-msg-body select[name='diegoReleases']").val() ); }
                }, gardenReleaseName: { 
                    required: function(){ return checkEmpty( $(".w2ui-msg-body select[name='gardenReleaseName']").val() ); }
                }, cfInfo: { 
                    required: function(){ return checkEmpty( $(".w2ui-msg-body select[name='cfInfo']").val() ); }
                }, cadvisorDriverIp: { 
                    required: function(){
                        if( $(".w2ui-msg-body input:checkbox[name='paastaMonitoring']").is(":checked") ){
                            return checkEmpty( $(".w2ui-msg-body input[name='cadvisorDriverIp']").val() );
                        }else{
                            return false;
                        }
                    }
                }
            }, messages: {
                 directorUuid      : { required:  "설치관리자 UUID" + text_required_msg }
                ,deploymentName    : { required:  "배포 명"+text_required_msg }
                ,diegoReleases       : { required: "DIEGO 릴리즈" + select_required_msg }
                ,cflinuxfs2rootfsrelease        : { required:  "Cf-linuxfs2" + select_required_msg }
                ,gardenReleaseName      : { required:  "Garden 릴리즈"+select_required_msg }
                ,etcdReleases           : { required:  "ETCD 릴리즈"+select_required_msg }
                ,cfInfo      : { required:  "CF 배포 명 "+select_required_msg }
                ,cadvisorDriverIp       : { required:  "PaaS-TA 모니터링"+text_required_msg } 
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
                saveDefaultInfo('after');
            }
        });
});
    