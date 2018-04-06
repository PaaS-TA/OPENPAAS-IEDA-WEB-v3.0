package org.openpaas.ieda.controller.iaasMgnt.dashboard.web.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class CommonIaasMgntController {
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Aws 관리 화면 이동
    * @title : goAwsMgnt
    * @return : String
    ***************************************************/
    @RequestMapping(value="/iaasMgnt/aws", method=RequestMethod.GET)
    public String goAwsMgnt(){
        return "iaas/aws/awsManagement";
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Openstack 관리 화면 이동
    * @title : goOpenstackMgnt
    ***************************************************/
    @RequestMapping(value="/iaasMgnt/openstack", method=RequestMethod.GET)
    public String goOpenstackMgnt(){
        return "iaas/openstack/openstackManagement";
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Azure 관리 화면 이동
     * @title : goAzureMgnt
     ***************************************************/
     @RequestMapping(value="/iaasMgnt/azure", method=RequestMethod.GET)
     public String goAzureMgnt(){
         return "iaas/azure/azureManagement";
     }
    
}
