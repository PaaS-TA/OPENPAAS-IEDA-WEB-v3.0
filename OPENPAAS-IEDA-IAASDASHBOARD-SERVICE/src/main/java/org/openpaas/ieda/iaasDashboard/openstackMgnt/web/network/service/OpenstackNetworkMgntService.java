package org.openpaas.ieda.iaasDashboard.openstackMgnt.web.network.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.api.network.OpenstackNetworkMgntApiService;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.network.dao.OpenstackNetworkMgntVO;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.network.dto.OpenstackNetworkMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.network.IPVersionType;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Subnet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class OpenstackNetworkMgntService {
    
    @Autowired CommonIaasService commonIaasService;
    @Autowired MessageSource message;
    @Autowired OpenstackNetworkMgntApiService openstackNetworkMgntApiService;
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : Openstack 네트워크 목록 조회
    * @title : getOpenstackNetworkInfoList
    * @return : List<OpenstackNetworkMgntVO>
    ***************************************************/
    public List<OpenstackNetworkMgntVO> getOpenstackNetworkInfoList(Principal principal, int accountId) {
        IaasAccountMgntVO vo =  getOpenstackAccountInfo(principal, accountId);
        List<? extends Network> networkList = openstackNetworkMgntApiService.getOpenstackNetworkInfoListApiFromOpenstack(vo);
        List<OpenstackNetworkMgntVO> resultList = new ArrayList<OpenstackNetworkMgntVO>();
        int recid = 1;
        for(int i=0; i<networkList.size(); i++){
            OpenstackNetworkMgntVO networkVo = new OpenstackNetworkMgntVO();
            String subnetName = "";
            String ipv6CidrBlock = "";
            networkVo.setNetworkId(networkList.get(i).getId());
            networkVo.setNetworkName(networkList.get(i).getName());
            if(networkList.get(i).getStatus() != null) {
                networkVo.setStatus(networkList.get(i).getStatus().name());
            }
            networkVo.setTenantId(networkList.get(i).getTenantId());
            networkVo.setRouterExternal(networkList.get(i).isRouterExternal());
            networkVo.setShared(networkList.get(i).isShared());
            networkVo.setAdminStateUp(networkList.get(i).isAdminStateUp());
            if(networkList.get(i).getNeutronSubnets() != null && networkList.get(i).getNeutronSubnets().size() != 0){
                for(int j=0; j<networkList.get(i).getNeutronSubnets().size(); j++){
                    subnetName += networkList.get(i).getNeutronSubnets().get(j).getName();
                    if(j < networkList.get(i).getNeutronSubnets().size()- 1){
                        subnetName += "</br> ";
                    }
                }
                for(int j=0; j<networkList.get(i).getNeutronSubnets().size(); j++){
                    ipv6CidrBlock += networkList.get(i).getNeutronSubnets().get(j).getCidr();
                    if(j < networkList.get(i).getNeutronSubnets().size()- 1){
                        ipv6CidrBlock += "</br> ";
                    }
                }
            }
            networkVo.setSubnetName(subnetName);
            networkVo.setCidrIpv4(ipv6CidrBlock);
            networkVo.setRecid(recid);
            networkVo.setAccountId(accountId);
            recid ++;
            resultList.add(networkVo);
        }
        return resultList;
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 네트워크 상세 정보 조회
    * @title : getOpenstackNetworkDetailInfo
    * @return : OpenstackNetworkMgntVO
    ***************************************************/
    public OpenstackNetworkMgntVO getOpenstackNetworkDetailInfo(Principal principal, int accountId, String networkId) {
        IaasAccountMgntVO vo =  getOpenstackAccountInfo(principal, accountId);
        Network networkApiVo = null;
        try{
            networkApiVo = openstackNetworkMgntApiService.getOpenstackNetworkDetailInfoApiFromOpenstack(vo, networkId);
        }catch (Exception e) {
            String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != null){
                throw new CommonException(
                  detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
            }else{
                throw new CommonException(
                        message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
        }
        OpenstackNetworkMgntVO networkVo = new OpenstackNetworkMgntVO();
        networkVo.setNetworkId(networkApiVo.getId());
        networkVo.setAccountId(accountId);
        networkVo.setNetworkName(networkApiVo.getName());
        if(networkApiVo.getStatus() != null) {
            networkVo.setStatus(networkApiVo.getStatus().name());
        }
        if(networkApiVo.getNetworkType() != null) {
            networkVo.setNetworkType(networkApiVo.getNetworkType().name());
        }
        networkVo.setTenantId(networkApiVo.getTenantId());
        String subnetName = "";
        String ipv6CidrBlock = "";
        if(networkApiVo.getNeutronSubnets()!= null && networkApiVo.getNeutronSubnets().size() != 0){
            for(int j=0; j<networkApiVo.getNeutronSubnets().size(); j++){
                subnetName += networkApiVo.getNeutronSubnets().get(j).getName();
                if(j < networkApiVo.getNeutronSubnets().size()- 1){
                    subnetName += ", ";
                }
            }
            for(int j=0; j<networkApiVo.getNeutronSubnets().size(); j++){
                ipv6CidrBlock += networkApiVo.getNeutronSubnets().get(j).getCidr();
                if(j < networkApiVo.getNeutronSubnets().size()- 1){
                    ipv6CidrBlock += ", ";
                }
            }
        }
        networkVo.setSubnetName(subnetName);
        networkVo.setCidrIpv4(ipv6CidrBlock);
        networkVo.setProviderNetwork(networkApiVo.getProviderPhyNet());
        networkVo.setSegId(networkApiVo.getProviderSegID());
        networkVo.setAdminStateUp(networkApiVo.isAdminStateUp());
        networkVo.setRouterExternal(networkApiVo.isRouterExternal());
        networkVo.setShared(networkApiVo.isShared());
        return networkVo;
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 네트워크 생성
    * @title : saveOpenstackNetworkInfo
    * @return : void
    ***************************************************/
    public void saveOpenstackNetworkInfo(OpenstackNetworkMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo =  getOpenstackAccountInfo(principal, dto.getAccountId());
        IPVersionType ipVersion = null;
        if("IPv4".equalsIgnoreCase(dto.getIpVersion())){
            ipVersion = IPVersionType.V4;
        } else {
            ipVersion = IPVersionType.V6;
        } 
        try{
            openstackNetworkMgntApiService.saveOpenstackNetworkInfoApiFromOpenstack(vo, dto, ipVersion);
        }catch (Exception e) {
            String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != null){
                throw new CommonException(
                  detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
            }else{
                throw new CommonException(
                        message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 네트워크 삭제
    * @title : deleteOpenstackNetworkInfo
    * @return : void
    ***************************************************/
    public void deleteOpenstackNetworkInfo(OpenstackNetworkMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo =  getOpenstackAccountInfo(principal, dto.getAccountId());
        ActionResponse response = openstackNetworkMgntApiService.deleteOpenstackNetworkInfoApiFromOpenstack(vo, dto);
        if (!response.isSuccess()) {
            throw new CommonException( message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), 
                    response.getFault(), HttpStatus.BAD_REQUEST);
        }
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 서브넷 목록 조회
    * @title : getOpenstackSubnetInfoList
    * @return : List<OpenstackNetworkMgntVO>
    ***************************************************/
    public List<OpenstackNetworkMgntVO> getOpenstackSubnetInfoList(Principal principal, int accountId, String networkId) {
        IaasAccountMgntVO vo =  getOpenstackAccountInfo(principal, accountId);
        List<? extends Subnet> apiSubnetList = null;
        try {
            apiSubnetList = openstackNetworkMgntApiService.getOpenstackSubnetInfoListApiFromOpenstack(vo, networkId);
        } catch (Exception e) {
            String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != null){
                throw new CommonException(
                  detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
            }else{
                throw new CommonException(
                        message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
        }
        List<OpenstackNetworkMgntVO> resultList = new ArrayList<OpenstackNetworkMgntVO>();
        OpenstackNetworkMgntVO subnetVo = null;
        int recid = 1;
        if(apiSubnetList != null){
            for(int i = 0; i < apiSubnetList.size(); i++){
                subnetVo = new OpenstackNetworkMgntVO();
                String dnsName = "";
                String routeDestination = "";
                String allocationPoolStart = "";
                String allocationPoolEnd = "";
                String allocationPoolIp = "";
                subnetVo.setAccountId(accountId);
                subnetVo.setCidrIpv4(apiSubnetList.get(i).getCidr());
                subnetVo.setGatewayIp(apiSubnetList.get(i).getGateway());
                subnetVo.setSubnetName(apiSubnetList.get(i).getName());
                subnetVo.setSubnetId(apiSubnetList.get(i).getId());
                subnetVo.setNetworkId(apiSubnetList.get(i).getNetworkId());
                subnetVo.setTenantId(apiSubnetList.get(i).getTenantId());
                subnetVo.setIpVersion(apiSubnetList.get(i).getIpVersion().name());
                subnetVo.setDhcpEnabled(apiSubnetList.get(i).isDHCPEnabled());
                if(apiSubnetList.get(i).getAllocationPools()!=null && apiSubnetList.get(i).getAllocationPools().size() != 0){
                    for(int j=0; j<apiSubnetList.get(i).getAllocationPools().size(); j++){
                        allocationPoolStart += apiSubnetList.get(i).getAllocationPools().get(j).getStart();
                        allocationPoolEnd += apiSubnetList.get(i).getAllocationPools().get(j).getEnd();
                        allocationPoolIp += allocationPoolStart +" - "+ allocationPoolEnd;
                        if(j < apiSubnetList.get(i).getAllocationPools().size()-1){
                            allocationPoolIp += "<br>";
                        }
                    }
                }
                
                if(apiSubnetList.get(i).getDnsNames()!=null && apiSubnetList.get(i).getDnsNames().size() != 0){
                    for(int j=0; j<apiSubnetList.get(i).getDnsNames().size(); j++){
                        dnsName +=apiSubnetList.get(i).getDnsNames().get(j);
                        if(j < apiSubnetList.get(i).getDnsNames().size()-1){
                            dnsName += "<br>";
                        }
                    }
                }
                if(apiSubnetList.get(i).getHostRoutes() != null && apiSubnetList.get(i).getHostRoutes().size() != 0){
                    for(int j=0; j<apiSubnetList.get(i).getHostRoutes().size(); j++){
                        routeDestination += apiSubnetList.get(i).getHostRoutes().get(j).getDestination();
                        if(j < apiSubnetList.get(i).getHostRoutes().size() -1 ){
                            routeDestination += "<br>";
                        }
                    }
                }
                subnetVo.setRecid(recid);
                recid++;
                subnetVo.setAllocationPools(allocationPoolIp);
                subnetVo.setDnsName(dnsName);
                subnetVo.setRouteDestination(routeDestination);
                resultList.add(subnetVo);
            }
        }
        return resultList;
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 서브넷 생성
    * @title : saveOpenstackSubnetkInfo
    * @return : IaasAccountMgntVO
    ***************************************************/
    public void saveOpenstackSubnetkInfo(OpenstackNetworkMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo =  getOpenstackAccountInfo(principal, dto.getAccountId());
        IPVersionType ipVersion = null;
        if("IPv4".equalsIgnoreCase(dto.getIpVersion())){
            ipVersion = IPVersionType.V4;
        }else if("IPv6".equalsIgnoreCase(dto.getIpVersion())){
            ipVersion = IPVersionType.V6;
        }
        try{
            openstackNetworkMgntApiService.saveOpenstackSubnetkInfoApiFromOpenstack(vo, dto, ipVersion);
        }catch (Exception e) {
        	String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != null){
                throw new CommonException(
                  detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
            }else{
            throw new CommonException(
                    message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 서브넷 삭제
    * @title : deleteOpenstackSubnetInfo
    * @return : void
    ***************************************************/
    public void deleteOpenstackSubnetInfo(OpenstackNetworkMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo =  getOpenstackAccountInfo(principal, dto.getAccountId());
        ActionResponse response = openstackNetworkMgntApiService.deleteOpenstackSubnetInfoApiFromOpenstack(vo, dto);
        if( !response.isSuccess() ) {
            throw new CommonException(
                    message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), response.getFault(), HttpStatus.BAD_REQUEST);
            
        }
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
