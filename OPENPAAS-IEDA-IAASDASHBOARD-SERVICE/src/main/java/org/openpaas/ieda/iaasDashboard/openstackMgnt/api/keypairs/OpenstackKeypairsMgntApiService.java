package org.openpaas.ieda.iaasDashboard.openstackMgnt.api.keypairs;
 
import java.util.List;

import org.openpaas.ieda.common.web.common.service.CommonApiService;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.compute.Keypair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
 
@Service
public class OpenstackKeypairsMgntApiService {
 
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
      * @description : Openstack Keypairs 목록 정보 조회 실제 API 호출
      * @title : getOpenstackKeypairsInfoListApiFromOpenstack
      * @return :List<? extends Keypair> 
      ***************************************************/
     public List<? extends Keypair> getOpenstackKeypairsInfoListApiFromOpenstack(IaasAccountMgntVO vo){
         List<? extends Keypair> list = null;
         String version = vo.getOpenstackKeystoneVersion();
         
         if(version.equalsIgnoreCase("v2")){
             OSClientV2 os= getOpenstackClientV2(vo);
             list = os.compute().keypairs().list();
         }else if(version.equalsIgnoreCase("v3")){
             OSClientV3 os= getOpenstackClientV3(vo);
             list = os.compute().keypairs().list();
         }
        return list;
     }
      
     /***************************************************
      * @project : OPENSTACK 인프라 관리 대시보드
      * @description : Openstack Keypairs 할당 실제 API 호출
      * @title : saveOpenstackKeypairsInfoApiFromOpenstack
      * @return : String
      ***************************************************/
     public String saveOpenstackKeypairsInfoApiFromOpenstack(IaasAccountMgntVO vo, String keyFileName){
         String openstackPrivatekey = "";
         String version = vo.getOpenstackKeystoneVersion();
         
         if(version.equalsIgnoreCase("v2")){
             OSClientV2 os= getOpenstackClientV2(vo);
             openstackPrivatekey = os.compute().keypairs().create(keyFileName, null).getPrivateKey();
         }else if(version.equalsIgnoreCase("v3")){
             OSClientV3 os= getOpenstackClientV3(vo);
             openstackPrivatekey = os.compute().keypairs().create(keyFileName, null).getPrivateKey();
         }
         return openstackPrivatekey;
     }
}