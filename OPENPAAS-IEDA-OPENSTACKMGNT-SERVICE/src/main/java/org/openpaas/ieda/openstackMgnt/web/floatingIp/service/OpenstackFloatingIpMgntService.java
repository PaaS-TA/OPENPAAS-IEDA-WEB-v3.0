package org.openpaas.ieda.openstackMgnt.web.floatingIp.service;
 
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
 
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.openpaas.ieda.openstackMgnt.api.floatingIp.OpenstackFloatingIpMgntApiService;
import org.openpaas.ieda.openstackMgnt.web.floatingIp.dto.OpenstackFloatingIpMgntDTO;
import org.openstack4j.model.compute.FloatingIP;
import org.openstack4j.model.network.Network;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
 
@Service
public class OpenstackFloatingIpMgntService {
     
    @Autowired CommonIaasService commonIaasService;
    @Autowired MessageSource message;
    @Autowired OpenstackFloatingIpMgntApiService openstackFloatingIpMgntApiService; 
     
     
    /***************************************************
     * @project : OPENSTACK 관리 대시보드
     * @description : Openstack Floating IP 목록 조회
     * @title : getOpenstackFloatingIpInfoList
     * @return : List<HashMap<String,Object>>
    ***************************************************/
    public List<HashMap<String,Object>> getOpenstackFloatingIpInfoList(Principal principal, int accountId){
        IaasAccountMgntVO vo =  getOpenstackAccountInfo(principal, accountId);
        List<? extends FloatingIP> floatingIpList = openstackFloatingIpMgntApiService.getOpenstackFloatingIpInfoListApiFromOpenstack(vo);
        List<HashMap<String,Object>> maps = new ArrayList<HashMap<String,Object>>();
        for(int i=0; i<floatingIpList.size(); i++){
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ipAddress", floatingIpList.get(i).getFloatingIpAddress());
            String instanceId = floatingIpList.get(i).getInstanceId();
            String instanceName = openstackFloatingIpMgntApiService.getOpenstackInstanceName(vo, instanceId);
            map.put("instanceName", instanceName);
            map.put("pool", floatingIpList.get(i).getPool());
             
            maps.add(map);
        }
        return maps;
    }
     
    /***************************************************
     * @project : OPENSTACK 인프라 관리 대시보드
     * @description : Openstack Floating IP 할당
     * @title : saveFloatingIpInfo
     * @return : void
    ***************************************************/
    public void saveFloatingIpInfo(OpenstackFloatingIpMgntDTO dto, Principal principal){
        IaasAccountMgntVO vo =  getOpenstackAccountInfo(principal, dto.getAccountId());
        try{
            openstackFloatingIpMgntApiService.saveOpenstackFloatingIpInfoApiFromOpenstack(vo, dto);
        }catch (Exception e) {
            throw new CommonException(
                    message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
    }
     
    /***************************************************
     * @project : OPENSTACK 관리 대시보드
     * @description : Openstack Floating IP 목록 조회
     * @title : getOpenstackFloatingIpInfoList
     * @return : List<String> 
    ***************************************************/
    public List<String> getOpenstackPoolInfoList(Principal principal, int accountId){
        IaasAccountMgntVO vo =  getOpenstackAccountInfo(principal, accountId);
        List<? extends Network> poolList = openstackFloatingIpMgntApiService.getOpenstackPoolInfoListApiFromOpenstack(vo);
        List<String> pools = new ArrayList<String>();
        for(int i=0; i<poolList.size(); i++){
           if(poolList.get(i).isRouterExternal()){
               String poolName = poolList.get(i).getName();
               pools.add(poolName);
            }
        }
        return pools;
    }
     
    /***************************************************
     * @project : OPENSTACK 인프라 관리 대시보드
     * @description : OPENSTACK 계정 정보가 실제 존재 하는지 확인 및 상세 조회
     * @title : getOpenstackAccountInfo
     * @return : IaasAccountMgntVO
     ***************************************************/
     public IaasAccountMgntVO getOpenstackAccountInfo(Principal principal, int accountId){
         return commonIaasService.getIaaSAccountInfo(principal, accountId, "openstack");
     }
}