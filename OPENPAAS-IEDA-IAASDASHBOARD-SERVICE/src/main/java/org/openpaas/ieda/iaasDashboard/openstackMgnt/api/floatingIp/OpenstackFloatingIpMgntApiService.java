package org.openpaas.ieda.iaasDashboard.openstackMgnt.api.floatingIp;

import java.util.List;
import java.util.Locale;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.common.web.common.service.CommonApiService;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.floatingIp.dto.OpenstackFloatingIpMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.compute.FloatingIP;
import org.openstack4j.model.network.Network;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class OpenstackFloatingIpMgntApiService {

    @Autowired CommonApiService commonApiService;
    @Autowired MessageSource message;
    
    /***************************************************
     * @project : OPENSTACK 인프라 관리 대시보드
     * @description : 받아온 Openstack 정보를 통해 OSClientV2 객체 생성
     * @title : getOpenstackClientV2
     * @return : OSClientV2
     ***************************************************/
     public OSClientV2 getOpenstackClientV2(IaasAccountMgntVO vo){
         OSClientV2 os= commonApiService.getOSClientFromOpenstackV2(vo.getCommonAccessEndpoint(), vo.getCommonTenant(), vo.getCommonAccessUser(), vo.getCommonAccessSecret());
         return os;
     }
     
     /***************************************************
      * @project : OPENSTACK 인프라 관리 대시보드
      * @description : 받아온 Openstack 정보를 통해 OSClientV3 객체 생성
      * @title : getOpenstackClientV3
      * @return : OSClientV3
      ***************************************************/
      public OSClientV3 getOpenstackClientV3(IaasAccountMgntVO vo){
          OSClientV3 os= commonApiService.getOSClientFromOpenstackV3(vo.getCommonAccessEndpoint(), vo.getOpenstackDomain(), vo.getCommonProject(), vo.getCommonAccessUser(), vo.getCommonAccessSecret());
          return os;
      }
     
     /***************************************************
      * @project : OPENSTACK 인프라 관리 대시보드
      * @description : Openstack Floating IP 목록 정보 조회 실제 API 호출
      * @title : getOpenstackFloatingIPInfoListApiFromOpenstack
      * @return : List<? extends FloatingIP>
      ***************************************************/
     public List<? extends FloatingIP> getOpenstackFloatingIpInfoListApiFromOpenstack(IaasAccountMgntVO vo){
            
         List<? extends FloatingIP> list = null;
         String version = vo.getOpenstackKeystoneVersion();
         
         if(version.equalsIgnoreCase("v2")){
             OSClientV2 os= getOpenstackClientV2(vo);
             list = os.compute().floatingIps().list();
         }else if(version.equalsIgnoreCase("v3")){
             OSClientV3 os= getOpenstackClientV3(vo);
             list = os.compute().floatingIps().list();
         }
         return list;
     }
     
     /***************************************************
     * @project : OPENSTACK 인프라 관리 대시보드
     * @description : Openstack Floating IP 목록 정보 중 인스턴스 이름 조회 실제 API 호출
     * @title : getOpenstackInstanceName
     * @return : String
    ***************************************************/
    public String getOpenstackInstanceName(IaasAccountMgntVO vo, String instanceId){
          
           String instanceName = "";
           String version = vo.getOpenstackKeystoneVersion();
            try{
                if(version.equalsIgnoreCase("v2")){
                    OSClientV2 os= getOpenstackClientV2(vo);
                    instanceName = os.compute().servers().get(instanceId).getName();
                }else if(version.equalsIgnoreCase("v3")){
                    OSClientV3 os= getOpenstackClientV3(vo);
                    instanceName = os.compute().servers().get(instanceId).getName();
                    }
            }catch(NullPointerException e){
                instanceName = "-";
            }catch(Exception e){
                throw new CommonException(
                        message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
            return instanceName;
     }
     
     /***************************************************
      * @project : OPENSTACK 인프라 관리 대시보드
      * @description : Openstack Floating IP 할당 실제 API 호출
      * @title : saveOpenstackFloatingIpInfoApiFromOpenstack
      * @return :void
      ***************************************************/
     public void saveOpenstackFloatingIpInfoApiFromOpenstack(IaasAccountMgntVO vo, OpenstackFloatingIpMgntDTO dto){
         
         String version = vo.getOpenstackKeystoneVersion();
         
         if(version.equalsIgnoreCase("v2")){
             OSClientV2 os= getOpenstackClientV2(vo);
             String pool = dto.getPool();
             os.compute().floatingIps().allocateIP(pool);
         }else if(version.equalsIgnoreCase("v3")){
             OSClientV3 os= getOpenstackClientV3(vo);
             String pool = dto.getPool();
             os.compute().floatingIps().allocateIP(pool);
         }
     }
     
     /***************************************************
      * @project : OPENSTACK 인프라 관리 대시보드
      * @description : Openstack Pool 목록 정보 조회 실제 API 호출
      * @title : getOpenstackPoolInfoListApiFromOpenstack
      * @return : List<? extends Network>
      ***************************************************/
     public List<? extends Network> getOpenstackPoolInfoListApiFromOpenstack(IaasAccountMgntVO vo){
         
         List<? extends Network> list = null;
         String version = vo.getOpenstackKeystoneVersion();   
         
         if(version.equalsIgnoreCase("v2")){
             OSClientV2 os= getOpenstackClientV2(vo);
             list = os.networking().network().list();
         }else if(version.equalsIgnoreCase("v3")){
             OSClientV3 os= getOpenstackClientV3(vo);
             list = os.networking().network().list();
         }
        return list;
     }
}