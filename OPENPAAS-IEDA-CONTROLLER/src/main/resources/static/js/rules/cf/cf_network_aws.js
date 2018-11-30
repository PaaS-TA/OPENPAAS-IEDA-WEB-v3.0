/**
 * 
 */
$(function() {
    $("#awsNetworkInfoForm").validate({
        ignore : "",
        rules:{
           subnetId_1 : {
               required : function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetId_1']").val() ); } 
           },cloudSecurityGroups_1 : {
               required : function(){ return checkEmpty( $(".w2ui-msg-body input[name='cloudSecurityGroups_1']").val() ); }
           },availabilityZone_1 : {
               required : function(){ 
                   return checkEmpty( $(".w2ui-msg-body input[name='availabilityZone_1']").val() ); 
               }
           }, subnetRange_1: { 
                required : function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetRange_1']").val() ); }
               ,ipv4Range: function(){  return $(".w2ui-msg-body input[name='subnetRange_1']").val(); }
           }, subnetGateway_1: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetGateway_1']").val() ); }
               ,ipv4    : function(){ return $(".w2ui-msg-body input[name='subnetGateway_1']").val(); }
           }, subnetDns_1: { 
               required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetDns_1']").val() ); } 
               , ipv4  : function(){ 
                   if( $(".w2ui-msg-body input[name='subnetDns_1']").val().indexOf(",") > -1 ){
                       var list = ($(".w2ui-msg-body input[name='subnetDns_1']").val()).split(",");
                       var flag = true;
                       for( var i=0; i<list.length; i++ ){
                           var val = validateIpv4(list[i].trim());
                           if( !val ) flag = false;
                       }
                       if( !flag ) return "false";
                       else return list[0].trim();
                   }else{
                       return $(".w2ui-msg-body input[name='subnetDns_1']").val();
                   }
               }
           }, subnetReservedFrom_1: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetReservedFrom_1']").val() ); }
               ,ipv4    : function(){ return $(".w2ui-msg-body input[name='subnetReservedFrom_1']").val(); }
           }, subnetReservedTo_1: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetReservedTo_1']").val() ); }
               ,ipv4    : function(){ return $(".w2ui-msg-body input[name='subnetReservedTo_1']").val(); }
           },
           
           subnetId_2 : {
               required : function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetId_2']").val() ); } 
           },cloudSecurityGroups_2 : {
               required : function(){ return checkEmpty( $(".w2ui-msg-body input[name='cloudSecurityGroups_2']").val() ); }
           },availabilityZone_2 : {
               required : function(){ 
                   return checkEmpty( $(".w2ui-msg-body input[name='availabilityZone_2']").val() ); 
               }
           }, subnetRange_2: { 
                required : function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetRange_2']").val() ); }
               ,ipv4Range: function(){  return $(".w2ui-msg-body input[name='subnetRange_2']").val(); }
           }, subnetGateway_2: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetGateway_2']").val() ); }
               ,ipv4    : function(){ return $(".w2ui-msg-body input[name='subnetGateway_2']").val(); }
           }, subnetDns_2: { 
               required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetDns_2']").val() ); } 
               , ipv4  : function(){ 
                   if( $(".w2ui-msg-body input[name='subnetDns_2']").val().indexOf(",") > -1 ){
                       var list = ($(".w2ui-msg-body input[name='subnetDns_2']").val()).split(",");
                       var flag = true;
                       for( var i=0; i<list.length; i++ ){
                           var val = validateIpv4(list[i].trim());
                           if( !val ) flag = false;
                       }
                       if( !flag ) return "false";
                       else return list[0].trim();
                   }else{
                       return $(".w2ui-msg-body input[name='subnetDns_2']").val();
                   }
               }
           }, subnetReservedFrom_2: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetReservedFrom_2']").val() ); }
               ,ipv4    : function(){ return $(".w2ui-msg-body input[name='subnetReservedFrom_1']").val(); }
           }, subnetReservedTo_2: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetReservedTo_2']").val() ); }
               ,ipv4    : function(){ return $(".w2ui-msg-body input[name='subnetReservedTo_1']").val(); }
           }
        }, messages: {
            subnetId_1            : { required: "서브넷 아이디"+text_required_msg }
            ,cloudSecurityGroups_1 : { required: "보안 그룹"+text_required_msg }
            ,availabilityZone_1    : { required: "가용 영역"+text_required_msg } 
            , subnetRange_1        : { required: "서브넷 범위"+text_required_msg } 
            , subnetGateway_1      : { required: "게이트웨이"+text_required_msg }
            , subnetDns_1          : { required: "DNS" + text_required_msg }
            , subnetReservedFrom_1 : { required:  "IP할당 제외 대역"+text_required_msg } 
            , subnetReservedTo_1   : { required: "IP할당 제외 대역"+text_required_msg }
            
            , subnetId_2            : { required: "서브넷 아이디"+text_required_msg }
            ,cloudSecurityGroups_2 : { required: "보안 그룹"+text_required_msg }
            ,availabilityZone_2    : { required: "가용 영역"+text_required_msg } 
            , subnetRange_2        : { required: "서브넷 범위"+text_required_msg } 
            , subnetGateway_2      : { required: "게이트웨이"+text_required_msg }
            , subnetDns_2          : { required: "DNS" + text_required_msg }
            , subnetReservedFrom_2 : { required:  "IP할당 제외 대역"+text_required_msg } 
            , subnetReservedTo_2   : { required: "IP할당 제외 대역"+text_required_msg }
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
            saveNetworkInfo('after', $(form).attr("id"));
        }
    });
});

function validateIpv4(params){
    return /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(params);
}