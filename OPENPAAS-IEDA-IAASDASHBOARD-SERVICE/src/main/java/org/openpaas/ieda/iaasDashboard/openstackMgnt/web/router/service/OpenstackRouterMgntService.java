package org.openpaas.ieda.iaasDashboard.openstackMgnt.web.router.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.api.network.OpenstackNetworkMgntApiService;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.api.router.OpenstackRouterMgntApiService;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.network.dao.OpenstackNetworkMgntVO;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.router.dao.OpenstackRouterMgntVO;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.router.dto.OpenstackRouterMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Port;
import org.openstack4j.model.network.Router;
import org.openstack4j.model.network.Subnet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class OpenstackRouterMgntService {

    @Autowired CommonIaasService commonIaasService;
    @Autowired OpenstackRouterMgntApiService openstackRouterMgntApiService;
    @Autowired OpenstackNetworkMgntApiService openstackNetworkMgntApiService;
    @Autowired MessageSource message;
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : Openstack Router 목록 조회
    * @title : getOpenstackRouterInfoList
    * @return : List<OpenstackRouterkMgntVO>
    ***************************************************/
    
    public List<OpenstackRouterMgntVO> getOpenstackRouterInfoList(Principal principal, int accountId){
       IaasAccountMgntVO vo = getOpenstackAccountInfo(principal, accountId);
       List<? extends Router> routerList = openstackRouterMgntApiService.getOpenstackRouterInfoListApiFromOpenstack(vo);
       List<OpenstackRouterMgntVO> opvoList = new ArrayList<OpenstackRouterMgntVO>();
       for(int i=0;i<routerList.size();i++){
           OpenstackRouterMgntVO opvo = new OpenstackRouterMgntVO();
           opvo.setRecid(i+1);
           opvo.setAccountId(accountId);
           opvo.setRouterName(routerList.get(i).getName());
           opvo.setRouteId(routerList.get(i).getId());
           opvo.setStatus(routerList.get(i).getStatus());
           opvo.setExternalNetwork(openstackRouterMgntApiService.getRouterExternalNetworkInfoApiFromOpenstack(vo, i));
           opvoList.add(opvo);
       }
       return opvoList;
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : Openstack Router 생성
    * @title : createOpenstackRouter
    * @return : void
    ***************************************************/
    public void createOpenstackRouter(OpenstackRouterMgntVO rvo, Principal principal){
        IaasAccountMgntVO vo = getOpenstackAccountInfo(principal, rvo.getAccountId());
        List<? extends Router> rlist = openstackRouterMgntApiService.getOpenstackRouterInfoListApiFromOpenstack(vo);
            for(int i=0;i<rlist.size();i++){
                if(rvo.getRouterName().equalsIgnoreCase(rlist.get(i).getName())){
                    throw new CommonException(
                    message.getMessage("common.conflict.exception.code", null, Locale.KOREA), message.getMessage("common.conflict.message", null, Locale.KOREA), HttpStatus.CONFLICT);
                }
            }
        boolean flag = openstackRouterMgntApiService.createOpenstackRouterApiFromOpenstack(vo, rvo);
        if( !flag ){
            throw new CommonException(
                message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : Openstack Router 삭제
    * @title : deleteOpenstackRouter
    * @return : void
    ***************************************************/
    
    public void deleteOpenstackRouter(OpenstackRouterMgntVO rvo, Principal principal){
        IaasAccountMgntVO vo = getOpenstackAccountInfo(principal, rvo.getAccountId());
        boolean flag = openstackRouterMgntApiService.deleteOpenstackRouterApiFromOpenstack(vo, rvo);
        if( !flag ) {
            throw new CommonException(
                    message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : Openstack Router Interface 목록 
    * @title : getOpenstackRouterInterfaceInfoList
    * @return : List<OpenstackNetworkMgntVo>
    ***************************************************/
    public List<OpenstackRouterMgntDTO> getOpenstackRouterInterfaceInfoList(Principal principal, int accountId, String routeId){
        IaasAccountMgntVO vo = getOpenstackAccountInfo(principal, accountId);
        List<? extends Port> pList = openstackRouterMgntApiService.getOpenstackNetworkPortApiFromOpenstack(vo);
        Router router = openstackRouterMgntApiService.getRouterApiFromOpenstack(vo, routeId);
        List<OpenstackRouterMgntDTO> resultlist = new ArrayList<OpenstackRouterMgntDTO>();
        for(int i=0;i<pList.size();i++){
            OpenstackRouterMgntDTO dto = new OpenstackRouterMgntDTO();
            if(pList.get(i).getDeviceId().equalsIgnoreCase(router.getId())){
                Network network = openstackNetworkMgntApiService.getOpenstackNetworkDetailInfoApiFromOpenstack(vo, pList.get(i).getNetworkId());
                dto.setAccountId(accountId);
                dto.setRouteId(router.getId());
                dto.setSubnetId(pList.get(i).getFixedIps().iterator().next().getSubnetId());
                dto.setSubnetName(pList.get(i).getName());
                dto.setSubnetFixedIps(pList.get(i).getFixedIps().iterator().next().getIpAddress());
                dto.setSubnetStatus(pList.get(i).getState());
                dto.setSubnetType(network.isShared());
                dto.setSubnetAdminStateUp(pList.get(i).isAdminStateUp());
                dto.setRecid(i);
                resultlist.add(dto);
            }
        }
        return resultlist;
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : Openstack 라우터 인터페이스 연결(생성)
    * @title : attachOpenstackRouterInterface
    * @return : void
    ***************************************************/
    public void attachOpenstackRouterInterface(OpenstackRouterMgntVO rvo, Principal principal){
        IaasAccountMgntVO vo = getOpenstackAccountInfo(principal, rvo.getAccountId());
        List<OpenstackRouterMgntDTO> riface = getOpenstackRouterInterfaceInfoList(principal, rvo.getAccountId(), rvo.getRouteId());
        for(int i=0;i<riface.size();i++){
            if(rvo.getSubnetId().equalsIgnoreCase(riface.get(i).getSubnetId())){
                throw new CommonException(
                message.getMessage("common.conflict.exception.code", null, Locale.KOREA), message.getMessage("common.conflict.message", null, Locale.KOREA), HttpStatus.CONFLICT);
            }
        }
        boolean flag = openstackRouterMgntApiService.attachRouterInterfaceApiFromOpenstack(vo, rvo);
        if( !flag ){
            throw new CommonException(
                message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
    }
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : Openstack 라우터 인터페이스 연결해제(삭제)
    * @title : detachOpenstackRouterInterface
    * @return : void
    ***************************************************/
    public void detachOpenstackRouterInterface(OpenstackRouterMgntVO rvo, Principal principal){
        boolean flag = false;
        IaasAccountMgntVO vo = getOpenstackAccountInfo(principal, rvo.getAccountId());
        List<OpenstackRouterMgntDTO> riface = getOpenstackRouterInterfaceInfoList(principal, rvo.getAccountId(), rvo.getRouteId());
        for(int i=0;i<riface.size();i++){
            if(rvo.getSubnetId().equalsIgnoreCase(riface.get(i).getSubnetId())){
                flag = openstackRouterMgntApiService.detachRouterInterfaceApiFromOpenstack(vo, rvo);
            }
        }
        if( !flag ){
            throw new CommonException(
                    message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
    }
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : Openstack 네트워크 Port 리스트 
    * @title : getOpenstackNetworkPortInfoList
    * @return : List<? extends Port>
    ***************************************************/
    public List<? extends Port> getOpenstackNetworkPortInfoList(Principal principal, int accountId){
        IaasAccountMgntVO vo = getOpenstackAccountInfo(principal, accountId);
        List<? extends Port> pList = openstackRouterMgntApiService.getOpenstackNetworkPortApiFromOpenstack(vo);
        return pList;
    }
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : Openstack 네트워크 서브넷 정보 리스트
    * @title : getOpenstackNetworkSubnetInfo
    * @return : List<OpenstackNetworkMgntVO>
    ***************************************************/
    public List<OpenstackNetworkMgntVO> getOpenstackNetworkSubnetInfo(Principal principal, int accountId){
        IaasAccountMgntVO vo = getOpenstackAccountInfo(principal, accountId);
        List<? extends Network> nList = openstackNetworkMgntApiService.getOpenstackNetworkInfoListApiFromOpenstack(vo);
        List<OpenstackNetworkMgntVO> resultList = new ArrayList<OpenstackNetworkMgntVO>();
        for(int i=0;i<nList.size();i++){
            String networkId = nList.get(i).getId();
            List<? extends Subnet> subnetList = openstackRouterMgntApiService.getNetworkSubnetInfoApiFromOpenstack(vo, networkId);
            if(subnetList == null){
                continue;
            }
            for(int j=0;j<subnetList.size();j++){
                OpenstackNetworkMgntVO nvo = new OpenstackNetworkMgntVO();
                nvo.setSubnetId(subnetList.get(j).getId());
                nvo.setSubnetName(subnetList.get(j).getName());
                nvo.setCidrIpv4(subnetList.get(j).getCidr());
                resultList.add(nvo);
            }
        }
        return resultList;
    }
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : Openstack 네트워크 정보 리스트
    * @title : getOpenstackNetworkInfoList
    * @return : List<OpenstackNetworkMgntVO>
    ***************************************************/
    public List<HashMap<String, String>> getOpenstackNetworkInfoList(Principal principal, int accountId){
        IaasAccountMgntVO vo = getOpenstackAccountInfo(principal, accountId);
//        List<OpenstackNetworkMgntVO> resultlist = new ArrayList<OpenstackNetworkMgntVO>();
        List<HashMap<String, String>> list = openstackRouterMgntApiService.getNetworkInfoApiFromOpenstack(vo);
//        for(int i=0;i<list.size();i++){
//            OpenstackNetworkMgntVO nvo = new OpenstackNetworkMgntVO();
//            nvo.setNetworkId(list.get(i).get("id"));
//            resultlist.add(nvo);
//        }
        return list;
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
    
    /***************************************************
     * @project : OPENSTACK 인프라 관리 대시보드
     * @description : Openstack 라우터 게이트 웨이 연결
     * @title : setOpenstackRouterGatewayAttach
     * @return : void
     ***************************************************/
    public void setOpenstackRouterGatewayAttach(Principal principal, OpenstackRouterMgntVO rvo){
        boolean flag = false;
        IaasAccountMgntVO account = getOpenstackAccountInfo(principal, rvo.getAccountId());
        flag = openstackRouterMgntApiService.setRouterGatewayAttachApiFromOpenstack(account, rvo);
        if( !flag ){
            throw new CommonException(
                    message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
    }
    /***************************************************
     * @project : OPENSTACK 인프라 관리 대시보드
     * @description : Openstack 라우터 게이트 웨이 연결해제
     * @title : setOpenstackRouterGatewayDetach
     * @return : void
     ***************************************************/
    public void setOpenstackRouterGatewayDetach(Principal principal, OpenstackRouterMgntVO rvo){
        boolean flag = false;
        IaasAccountMgntVO vo = getOpenstackAccountInfo(principal, rvo.getAccountId());
        flag = openstackRouterMgntApiService.setRouterGatewayDetachApiFromOpenstack(vo, rvo);
        if( !flag ){
            throw new CommonException(
                    message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
    }
}
