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
                required: function(){ return checkEmpty( $(".w2ui-msg-body select[name='cfdeployment']").val() ); }
            }, domainOrganization: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='domainOrganization']").val() ); }
            }, domain: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='domain']").val() ); }
            }, cfDbType: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body select[name='cfDbType']").val() ); }
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
            }, userAddSsh: {
                required: function(){
                    if( $(".w2ui-msg-body #userAddSsh").css("display") == "none"  ){
                        return false;
                    }else{
                        return checkEmpty( $(".w2ui-msg-body textarea[name='userAddSsh']").val() ); 
                    }
                }
            }, osConfReleases: {
                required: function(){
                    if( $(".w2ui-msg-body #osConfRelease").css("display") == "none"  ){
                        return false;
                    }else{
                        return checkEmpty( $(".w2ui-msg-body select[name='osConfReleases']").val() ); 
                    }
                }
            }, inceptionOsUserName: {
                required: function(){
                    if( $(".w2ui-msg-body #inceptionOsUserNameConfDiv").css("display") == "none"  ){
                        return false;
                    }else{
                        return checkEmpty( $(".w2ui-msg-body input[name='inceptionOsUserName']").val() ); 
                    }
                }
            }, cfAdminPassword: {
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='cfAdminPassword']").val() ); 
                }
            }, cfdeployment: {
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body select[name='cfdeployment']").val() ); 
                }
            }, paastaPortalDomain: {
                required: function(){
                    return checkEmpty( $(".w2ui-msg-body input[name='paastaPortalDomain']").val() ); 
                }
            }
        }, messages: {
             directorUuid        : { required: "설치관리자 UUID" + text_required_msg }
            ,deploymentName      : { required: "배포 명"+text_required_msg }
            ,domainOrganization  : { required: "기본 조직 명"+text_required_msg }
            ,releases            : { required: "CF Deployment" + select_required_msg }
            ,cfDbType            : { required: "CF 데이터베이스 유형" + select_required_msg }
            ,domain              : { required: "도메인"+text_required_msg }
            ,ingestorIp          : { required: "Ingestor 서버 IP"+text_required_msg } 
            ,ingestorPort        : { required: "Ingestor 서버 PORT"+text_required_msg }
            ,loggregatorReleases : { required: "Loggergator 릴리즈"+select_required_msg }
            ,userAddSsh          : { required: "Public SSH KEY" +text_required_msg}
            ,osConfReleases      : { required: "OS-CONF"+select_required_msg}
            ,inceptionOsUserName : { required: "Inception User Name"+text_required_msg}
            ,cfAdminPassword     : { required: "CF Admin Password"+text_required_msg}
            ,cfdeployment        : { required: "CF Deployment Version" + text_required_msg}
            ,paastaPortalDomain  : { required: "PaaS-TA 포털 버전" + text_required_msg}
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
    