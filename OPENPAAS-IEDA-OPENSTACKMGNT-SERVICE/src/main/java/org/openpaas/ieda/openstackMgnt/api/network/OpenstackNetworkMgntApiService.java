package org.openpaas.ieda.openstackMgnt.api.network;

import java.util.List;

import org.openpaas.ieda.common.web.common.service.CommonApiService;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.openstackMgnt.web.network.dto.OpenstackNetworkMgntDTO;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.api.types.Facing;
import org.openstack4j.model.network.IPVersionType;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Subnet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OpenstackNetworkMgntApiService {
    
    @Autowired
    CommonApiService commonApiService;
    
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
        OSClientV3 osV3= commonApiService.getOSClientFromOpenstackV3(vo.getCommonAccessEndpoint(), vo.getOpenstackDomain(), vo.getCommonProject(), vo.getCommonAccessUser(), vo.getCommonAccessSecret());
        return osV3;
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : Openstack 네트워크 목록 정보 조회 실제 API 호출
    * @title : getOpenstackNetworkInfoListApiFromOpenstack
    * @return : List<? extends Network>
    ***************************************************/
    public List<? extends Network> getOpenstackNetworkInfoListApiFromOpenstack(IaasAccountMgntVO vo) {
        if("v2".equals(vo.getOpenstackKeystoneVersion())){
            OSClientV2 os= getOpenstackClientV2(vo);
            return os.networking().network().list();
        }else{
            OSClientV3 osV3= getOpenstackClientV3(vo);
            return osV3.networking().network().list();
        }
    }

    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 네트워크 상세 정보 조회 실제 API 호출
    * @title : getOpenstackNetworkDetailInfoApiFromOpenstack
    * @return : OpenstackNetworkMgntVO
    ***************************************************/
    public Network getOpenstackNetworkDetailInfoApiFromOpenstack(IaasAccountMgntVO vo, String networkId) {
        if("v2".equals(vo.getOpenstackKeystoneVersion())){
            OSClientV2 os= getOpenstackClientV2(vo);
            return os.networking().network().get(networkId);
        }else{
            OSClientV3 osV3= getOpenstackClientV3(vo);
            return osV3.networking().network().get(networkId);
        }
    }
    
    /***************************************************
    * @param ipVersion 
     * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 네트워크 생성 실제 API 호출
    * @title : saveOpenstackNetworkInfoApiFromOpenstack
    * @return : void
    ***************************************************/
    public void saveOpenstackNetworkInfoApiFromOpenstack(IaasAccountMgntVO vo, OpenstackNetworkMgntDTO dto, IPVersionType ipVersion) {
        if("v2".equals(vo.getOpenstackKeystoneVersion())){
            OSClientV2 os= getOpenstackClientV2(vo);
            String projectId = os.perspective(Facing.ADMIN).identity().tenants().getByName(vo.getCommonTenant()).getId();
            String networkId = os.networking().network()
            .create(Builders.network().name(dto.getNetworkName()).
                    tenantId(projectId)
                    .adminStateUp(dto.isAdminState())
                    .build())
                    .getId();
            Network networkInfo = os.networking().network().get(networkId);
            os.networking().subnet().create(Builders.subnet()
                    .name(dto.getSubnetName())
                    .networkId(networkId)
                    .tenantId(networkInfo.getTenantId())
                    .ipVersion(ipVersion)
                    .cidr(dto.getNetworkAddress())
                    .addDNSNameServer(dto.getDnsNameServers())
                    .gateway(dto.getGatewayIp())
                    .enableDHCP(dto.isEnableDHCP())
                    .build());
        }else{
            OSClientV3 osV3= getOpenstackClientV3(vo);
            String projectId = osV3.perspective(Facing.ADMIN).identity().projects().getByName(vo.getCommonProject(), vo.getCommonTenant()).getId();
            String networkId = osV3.networking().network()
            .create(Builders.network().name(dto.getNetworkName()).
                    tenantId(projectId)
                    .adminStateUp(dto.isAdminState())
                    .build())
                    .getId();
            Network networkInfo = osV3.networking().network().get(networkId);
            osV3.networking().subnet().create(Builders.subnet()
                    .name(dto.getSubnetName())
                    .networkId(networkId)
                    .tenantId(networkInfo.getTenantId())
                    .ipVersion(ipVersion)
                    .cidr(dto.getNetworkAddress())
                    .addDNSNameServer(dto.getDnsNameServers())
                    .gateway(dto.getGatewayIp())
                    .enableDHCP(dto.isEnableDHCP())
                    .build());
        }
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 네트워크 삭제 실제 API 호출
    * @title : deleteOpenstackNetworkInfoApiFromOpenstack
    * @return : void
    ***************************************************/
    public void deleteOpenstackNetworkInfoApiFromOpenstack(IaasAccountMgntVO vo, OpenstackNetworkMgntDTO dto) {
        if("v2".equals(vo.getOpenstackKeystoneVersion())){
            OSClientV2 os= getOpenstackClientV2(vo);
            os.networking().network().delete(dto.getNetworkId());
        }else{
            OSClientV3 osV3= getOpenstackClientV3(vo);
            osV3.networking().network().delete(dto.getNetworkId());
        }
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 서브넷 조회 실제 API 호출
    * @title : getOpenstackSubnetInfoListApiFromOpenstack
    * @return : List<? extends Subnet>
    ***************************************************/
    public List<? extends Subnet> getOpenstackSubnetInfoListApiFromOpenstack(IaasAccountMgntVO vo, String networkId) {
        if("v2".equals(vo.getOpenstackKeystoneVersion())){
            OSClientV2 os= getOpenstackClientV2(vo);
            return os.networking().network().get(networkId).getNeutronSubnets();
        }else{
            OSClientV3 osV3= getOpenstackClientV3(vo);
            return osV3.networking().network().get(networkId).getNeutronSubnets();
        }
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 서브넷 생성 실제 API 호출
    * @title : saveOpenstackSubnetkInfoApiFromOpenstack
    * @return : void
    ***************************************************/
    public void saveOpenstackSubnetkInfoApiFromOpenstack(IaasAccountMgntVO vo, OpenstackNetworkMgntDTO dto,
            IPVersionType ipVersion) {
        if("v2".equals(vo.getOpenstackKeystoneVersion())){
            OSClientV2 os= getOpenstackClientV2(vo);
            String projectId = os.perspective(Facing.ADMIN).identity().tenants().getByName(vo.getCommonTenant()).getId();
            os.networking().subnet().create(Builders.subnet()
                    .name(dto.getSubnetName())
                    .networkId(dto.getNetworkId())
                    .tenantId(projectId)
                    .ipVersion(ipVersion)
                    .cidr(dto.getNetworkAddress())
                    .addDNSNameServer(dto.getDnsNameServers())
                    .gateway(dto.getGatewayIp())
                    .enableDHCP(dto.isEnableDHCP())
                    .build());
        }else{
            OSClientV3 osV3= getOpenstackClientV3(vo);
            String projectId = osV3.perspective(Facing.ADMIN).identity().projects().getByName(vo.getCommonProject(), vo.getCommonTenant()).getId();
            osV3.networking().subnet().create(Builders.subnet()
                    .name(dto.getSubnetName())
                    .networkId(dto.getNetworkId())
                    .tenantId(projectId)
                    .ipVersion(ipVersion)
                    .cidr(dto.getNetworkAddress())
                    .addDNSNameServer(dto.getDnsNameServers())
                    .gateway(dto.getGatewayIp())
                    .enableDHCP(dto.isEnableDHCP())
                    .build());
        }
    }
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 서브넷 삭제 실제 API 호출
    * @title : deleteOpenstackSubnetInfoApiFromOpenstack
    * @return : void
    ***************************************************/
    public void deleteOpenstackSubnetInfoApiFromOpenstack(IaasAccountMgntVO vo, OpenstackNetworkMgntDTO dto) {
        if("v2".equals(vo.getOpenstackKeystoneVersion())){
            OSClientV2 os= getOpenstackClientV2(vo);
            os.networking().subnet().delete(dto.getSubnetId());
        }else{
            OSClientV3 osV3= getOpenstackClientV3(vo);
            osV3.networking().subnet().delete(dto.getSubnetId());
        }
    }
    
}
