/**
 * 
 */
$(function() {
$("#defaultInfoForm").validate({
        ignore : [],
        onfocusout: true,
        rules: {
            directorUuid : {
                required : function(){ return checkEmpty( $(".w2ui-msg-body input[name='directorUuid']").val() ); }
            }, deploymentName: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='deploymentName']").val() ); }
            }, releases: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body select[name='releases']").val() ); }
            }, domainOrganization: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='domainOrganization']").val() ); }
            }, deaDiskMB: { 
                required: function(){
                    if( $(".w2ui-msg-body #deaDiskmbDiv").css("display") == "none"  ){
                        return false;
                    }else{
                        return checkEmpty( $(".w2ui-msg-body input[name='deaDiskMB']").val() ); 
                    }
                }
            }, deaMemoryMB: { 
                required: function(){
                    if( $(".w2ui-msg-body #deaMemorymbDiv").css("display") == "none"  ){
                        return false;
                    }else{
                        return checkEmpty( $(".w2ui-msg-body input[name='deaMemoryMB']").val() ); 
                    }
                }
            }, domain: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='domain']").val() ); }
            }, description: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='description']").val() ); }
            }, loginSecret: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='loginSecret']").val() ); }
            }, ingestorIp: { 
                required: function(){
                    if( $(".w2ui-msg-body input:checkbox[name='paastaMonitoring']").is(":checked") ){
                        return checkEmpty( $(".w2ui-msg-body input[name='ingestorIp']").val() );
                    }else{
                        return false;
                    }
                }
            }, ingestorPort: { 
                required: function(){
                    if( $(".w2ui-msg-body input:checkbox[name='paastaMonitoring']").is(":checked") ){
                        return checkEmpty( $(".w2ui-msg-body input[name='ingestorPort']").val() );
                    }else{
                        return false;
                    }
                }
            }, loggregatorReleases: { 
                required: function(){
                    var name = $(".w2ui-msg-body select[name='releases']").val().split("/")[0];
                    var version = $(".w2ui-msg-body select[name='releases']").val().split("/")[1];
                    if( (name.indexOf("cf") > -1 &&  Number(version) >= 272 ) || (name.indexOf("paasta-controller") > -1 && compare(version, "3.0") > -1) ){
                    	
                        if( (name.indexOf("paasta-controller") > -1 && compare(version, "3.1") > -1) || (name.indexOf("cf") > -1 &&  Number(version) >= 287 ) ){
                            return false;
                        }
                        return checkEmpty( $(".w2ui-msg-body select[name='loggregatorReleases']").val() );
                    }else{
                        return false;
                    }
                }
            }
        }, messages: {
             directorUuid        : { required: "설치관리자 UUID" + text_required_msg }
            ,deploymentName      : { required: "배포 명"+text_required_msg }
            ,domainOrganization  : { required: "기본 조직 명"+text_required_msg }
            ,releases            : { required: "CF 릴리즈" + select_required_msg }
            ,deaDiskMB           : { required: "DEA DISK 사이즈" + text_required_msg }
            ,deaMemoryMB         : { required: "DEA MEMORY 사이즈"+text_required_msg }
            ,domain              : { required: "도메인"+text_required_msg }
            ,description         : { required: "도메인 설명"+text_required_msg }
            ,loginSecret         : { required: "로그인 비밀번호"+text_required_msg }
            ,ingestorIp          : { required: "Ingestor 서버 IP"+text_required_msg } 
            ,ingestorPort        : { required: "Ingestor 서버 PORT"+text_required_msg }
            ,loggregatorReleases : { required: "Loggergator 릴리즈"+select_required_msg }
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
            saveDefaultInfo();
        }
    });
});
    