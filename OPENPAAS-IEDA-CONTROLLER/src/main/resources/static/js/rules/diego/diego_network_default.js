/**
 * 
 */
$(function() {
    $("#defaultNetworkInfoForm").validate({
        ignore : "",
        rules:{
           publicStaticIp_0 : {
               required : function(){ return checkEmpty( $(".w2ui-msg-body input[name='publicStaticIp_0']").val() ); } 
              ,ipv4     : function(){ return $(".w2ui-msg-body input[name='publicStaticIp_0']").val(); }
           },subnetId_0 : { 
               required : function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetId_0']").val() ); } 
           },cloudSecurityGroups_0 : {
               required : function(){ return checkEmpty( $(".w2ui-msg-body input[name='cloudSecurityGroups_0']").val() ); }
           },availabilityZone_0 : {
               required : function(){ 
                   if( iaas.toLowerCase() == "openstack" ) return false;
                   else return checkEmpty( $(".w2ui-msg-body input[name='availabilityZone_0']").val() ); 
               }
           }, subnetRange_0: { 
                required : function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetRange_0']").val() ); }
               ,ipv4Range: function(){  return $(".w2ui-msg-body input[name='subnetRange_0']").val(); }
           }, subnetGateway_0: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetGateway_0']").val() ); }
               ,ipv4    : function(){ return $(".w2ui-msg-body input[name='subnetGateway_0']").val(); }
           }, subnetDns_0: { 
               required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetDns_0']").val() ); } 
               , ipv4  : function(){ 
                   if( $(".w2ui-msg-body input[name='subnetDns_0']").val().indexOf(",") > -1 ){
                       var list = ($(".w2ui-msg-body input[name='subnetDns_0']").val()).split(",");
                       var flag = true;
                       for( var i=0; i<list.length; i++ ){
                           var val = validateIpv4(list[i].trim());
                           if( !val ) flag = false;
                       }
                       if( !flag ) return "";
                       else return list[0].trim();
                   }else{
                       return $(".w2ui-msg-body input[name='subnetDns_0']").val();
                   }
               }
           }, subnetReservedFrom_0: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetReservedFrom_0']").val() ); }
               ,ipv4    : function(){ return $(".w2ui-msg-body input[name='subnetReservedFrom_0']").val(); }
           }, subnetReservedTo_0: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetReservedTo_0']").val() ); }
               ,ipv4    : function(){ return $(".w2ui-msg-body input[name='subnetReservedTo_0']").val(); }
           }, subnetStaticFrom_0: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetStaticFrom_0']").val() ); }
               ,ipv4: function(){ return $(".w2ui-msg-body input[name='subnetStaticFrom_0']").val(); }
           }, subnetStaticTo_0: { 
                required: function(){ return checkEmpty( $(".w2ui-msg-body input[name='subnetStaticTo_0']").val() ); }
               ,ipv4: function(){ return $(".w2ui-msg-body input[name='subnetStaticTo_0']").val(); }
           }
        }, messages: {
             publicStaticIp        : { required: "CF API TARGET IP"+text_required_msg }
            ,subnetId_0            : { required: "서브넷 아이디"+text_required_msg }
            ,cloudSecurityGroups_0 : { required: "보안 그룹"+text_required_msg }
            ,availabilityZone_0    : { required: "가용 영역"+text_required_msg } 
            , subnetRange_0        : { required: "서브넷 범위"+text_required_msg } 
            , subnetGateway_0      : { required: "게이트웨이"+text_required_msg }
            , subnetDns_0          : { required: "DNS" + text_required_msg }
            , subnetReservedFrom_0 : { required:  "IP할당 제외 대역"+text_required_msg } 
            , subnetReservedTo_0   : { required: "IP할당 제외 대역"+text_required_msg }
            , subnetStaticFrom_0   : { required: "IP할당 대역"+text_required_msg }
            , subnetStaticTo_0     : { required: "IP할당 대역"+text_required_msg }
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

/********************************************************
 * 설명 : 네트워크 유효성 추가
 * 기능 : createInternalNetworkValidate
 *********************************************************/
function createInternalNetworkValidate(index){
   var subnet_message="서브넷 아이디";
   var zone_message = "가용 영역";
   if( iaas.toLowerCase() == "google"){
       $("[name*='networkName_"+index+"']").rules("add", {
           required: function(){
               return checkEmpty($(".w2ui-msg-body input[name='networkName_"+index+"']").val());
           }, messages: {required: "네트워크 명 "+text_required_msg}
       });
       subnet_message = "서브넷 명";
       zone_message = "zone";
   }else if(  iaas.toLowerCase() == "vsphere"){ 
       subnet_message="포트 그룹명";
   }
   $("[name*='subnetId_"+index+"']").rules("add", {
       required: function(){
           return checkEmpty($(".w2ui-msg-body input[name='subnetId_"+index+"']").val());
       }, messages: {required: subnet_message+text_required_msg}
   });
   $("[name*='subnetRange_"+index+"']").rules("add", {
       required: function(){
           return checkEmpty($(".w2ui-msg-body input[name='subnetRange_"+index+"']").val());
       },ipv4Range : function(){
           return $(".w2ui-msg-body input[name='subnetRange_"+index+"']").val();  
       }, messages: {required: "서브넷 범위"+text_required_msg}
   });
   
   $("[name*='subnetGateway_"+index+"']").rules("add", {
       required: function(){
           return checkEmpty($(".w2ui-msg-body input[name='subnetGateway_"+index+"']").val());
       },ipv4: function(){
           return $(".w2ui-msg-body input[name='subnetGateway_"+index+"']").val();  
       }, messages: {required: "게이트웨이"+text_required_msg}
   });
   
   $("[name*='subnetDns_"+index+"']").rules("add", {
       required: function(){
           return checkEmpty($(".w2ui-msg-body input[name='subnetDns_"+index+"']").val());
       },ipv4: function(){
           if( $(".w2ui-msg-body input[name='subnetDns_"+index+"']").val().indexOf(",") > -1 ){
               var list = ($(".w2ui-msg-body input[name='subnetDns_"+index+"']").val()).split(",");
               var flag = true;
               for( var i=0; i<list.length; i++ ){
                   var val = validateIpv4(list[i].trim());
                   if( !val ) flag = false;
               }
               if( !flag ) return "";
               else return list[0].trim();
           }else{
               return $(".w2ui-msg-body input[name='subnetDns_"+index+"']").val();
           }
       }, messages: {required: "DNS"+text_required_msg}
   });
   
   $("[name*='subnetReservedFrom_"+index+"']").rules("add", {
       required: function(){
           return checkEmpty($(".w2ui-msg-body input[name='subnetReservedFrom_"+index+"']").val());
       },ipv4: function(){
           return $(".w2ui-msg-body input[name='subnetReservedFrom_"+index+"']").val();  
       }, messages: {required: "IP 할당 제외 대역"+text_required_msg}
   });
   
   $("[name*='subnetReservedTo_"+index+"']").rules("add", {
       required: function(){
           return checkEmpty($(".w2ui-msg-body input[name='subnetReservedTo_"+index+"']").val());
       },ipv4: function(){
           return $(".w2ui-msg-body input[name='subnetReservedTo_"+index+"']").val();  
       }, messages: {required: "IP 할당 제외 대역"+text_required_msg}
   });
   
   $("[name*='subnetStaticFrom_"+index+"']").rules("add", {
       required: function(){
           return checkEmpty($(".w2ui-msg-body input[name='subnetStaticFrom_"+index+"']").val());
       },ipv4: function(){
           return $(".w2ui-msg-body input[name='subnetStaticFrom_"+index+"']").val();  
       }, messages: {required: "IP 할당 대역"+text_required_msg}
   });
   
   $("[name*='subnetStaticTo_"+index+"']").rules("add", {
       required: function(){
           return checkEmpty($(".w2ui-msg-body input[name='subnetStaticTo_"+index+"']").val());
       },ipv4: function(){
           return $(".w2ui-msg-body input[name='subnetStaticTo_"+index+"']").val();  
       }, messages: {required: "IP 할당 대역"+text_required_msg}
   });
   
   if( iaas.toLowerCase() != "vsphere" ){
       $("[name*='cloudSecurityGroups_"+index+"']").rules("add", {
           required: function(){
               return checkEmpty($(".w2ui-msg-body input[name='cloudSecurityGroups_"+index+"']").val());
           }, messages: {required: "보안 그룹"+text_required_msg}
       });
   }
   
   if( iaas.toLowerCase() == "google" ){
       $("[name*='networkName_"+index+"']").rules("add", {
           required: function(){
               return checkEmpty($(".w2ui-msg-body input[name='networkName_"+index+"']").val());
           }, messages: {required: "네트워크 명"+text_required_msg}
       });
   }
   if( iaas.toLowerCase() == 'aws' || iaas.toLowerCase() == 'google' ){
       $("[name*='availabilityZone_"+index+"']").rules("add", {
           required: function(){
               return checkEmpty($(".w2ui-msg-body input[name='availabilityZone_"+index+"']").val());
           }, messages: {required: zone_message + text_required_msg}
       });
   }
   
   w2popup.unlock();
}

function validateIpv4(params){
    return /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(params);
}