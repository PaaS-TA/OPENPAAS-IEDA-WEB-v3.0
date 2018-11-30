package org.openpaas.ieda.iaasDashboard.openstackMgnt.api.network;

import java.util.List;

import org.openpaas.ieda.common.web.common.service.CommonApiService;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.network.dto.OpenstackNetworkMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.api.types.Facing;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.network.IPVersionType;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Subnet;
import org.openstack4j.model.network.builder.SubnetBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class OpenstackNetworkMgntApiService {
    
    @Autowired private CommonApiService commonApiService;
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
        if("v2".equalsIgnoreCase(vo.getOpenstackKeystoneVersion())){
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
        if("v2".equalsIgnoreCase(vo.getOpenstackKeystoneVersion())){
            OSClientV2 os= getOpenstackClientV2(vo);
            return os.networking().network().get(networkId);
        }else{
            OSClientV3 osV3= getOpenstackClientV3(vo);
            return osV3.networking().network().get(networkId);
        }
    }
    
    /***************************************************
     * @project : OPENSTACK 인프라 관리 대시보드
     * @description : OPENSTACK 네트워크 생성 실제 API 호출
     * @title : saveOpenstackNetworkInfoApiFromOpenstack
     * @return : void
    ***************************************************/
    public void saveOpenstackNetworkInfoApiFromOpenstack(IaasAccountMgntVO vo, OpenstackNetworkMgntDTO dto, IPVersionType ipVersion) {
        String networkId  = "";
        if("v2".equalsIgnoreCase(vo.getOpenstackKeystoneVersion())){
            OSClientV2 os= getOpenstackClientV2(vo);
            String projectId = os.perspective(Facing.ADMIN).identity().tenants().getByName(vo.getCommonTenant()).getId();
            networkId = os.networking().network().create(Builders.network().name(dto.getNetworkName())
                    .tenantId(projectId)
                    .adminStateUp(dto.isAdminState())
                    .build())
                    .getId();
        } else {
            OSClientV3 osV3= getOpenstackClientV3(vo);
            String projectId = osV3.perspective(Facing.ADMIN).identity().projects().getByName(vo.getCommonProject(), vo.getOpenstackDomain()).getId();
            networkId = osV3.networking().network().create(Builders.network().name(dto.getNetworkName())
                    .tenantId(projectId)
                    .adminStateUp(dto.isAdminState())
                    .build())
                    .getId();
        }
        //subnet 생성
        dto.setNetworkId(networkId);
        saveOpenstackSubnetkInfoApiFromOpenstack(vo, dto, ipVersion);
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 네트워크 삭제 실제 API 호출
    * @title : deleteOpenstackNetworkInfoApiFromOpenstack
    * @return : void
    ***************************************************/
    public ActionResponse deleteOpenstackNetworkInfoApiFromOpenstack(IaasAccountMgntVO vo, OpenstackNetworkMgntDTO dto) {
        ActionResponse response = null;
        if("v2".equalsIgnoreCase(vo.getOpenstackKeystoneVersion())){
            OSClientV2 os= getOpenstackClientV2(vo);
            response = os.networking().network().delete(dto.getNetworkId());
        }else{
            OSClientV3 osV3= getOpenstackClientV3(vo);
            response = osV3.networking().network().delete(dto.getNetworkId());
        }
        return response;
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 서브넷 조회 실제 API 호출
    * @title : getOpenstackSubnetInfoListApiFromOpenstack
    * @return : List<? extends Subnet>
    ***************************************************/
    public List<? extends Subnet> getOpenstackSubnetInfoListApiFromOpenstack(IaasAccountMgntVO vo, String networkId) {
        if("v2".equalsIgnoreCase(vo.getOpenstackKeystoneVersion())){
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
    public void saveOpenstackSubnetkInfoApiFromOpenstack(IaasAccountMgntVO vo, OpenstackNetworkMgntDTO dto, IPVersionType ipVersion) {
        String[] dnsServers = {};
        OSClientV2 os= null;
        OSClientV3 osV3= null;
        String projectId = "";
        if( dto.getDnsNameServers().indexOf("\n") > -1 ) {
            dnsServers = dto.getDnsNameServers().split("\n");
        }else if( dto.getDnsNameServers().length() > 0 ) {
            dnsServers = dto.getDnsNameServers().split("\n");
        }
        if("v2".equalsIgnoreCase(vo.getOpenstackKeystoneVersion())){
            os= getOpenstackClientV2(vo);
            projectId = os.perspective(Facing.ADMIN).identity().tenants().getByName(vo.getCommonTenant()).getId();
        }else{
            osV3= getOpenstackClientV3(vo);
            projectId = osV3.perspective(Facing.ADMIN).identity().projects().getByName(vo.getCommonProject(), vo.getOpenstackDomain()).getId();
        }
        SubnetBuilder builder = Builders.subnet()
                .name(dto.getSubnetName())
                .networkId(dto.getNetworkId())
                .tenantId(projectId)
                .ipVersion(ipVersion)
                .cidr(dto.getNetworkAddress())
                .gateway(dto.getGatewayIp())
                .enableDHCP(dto.isEnableDHCP());
        
        for( int i=0; i<dnsServers.length; i++ ){
            builder.addDNSNameServer(dnsServers[i]);
        }
        
        if("v2".equalsIgnoreCase(vo.getOpenstackKeystoneVersion())){
            os.networking().subnet().create(builder.build());
        }else{
            osV3.networking().subnet().create(builder.build());
        }
    }
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 서브넷 삭제 실제 API 호출
    * @title : deleteOpenstackSubnetInfoApiFromOpenstack
    * @return : void
    ***************************************************/
    public ActionResponse deleteOpenstackSubnetInfoApiFromOpenstack(IaasAccountMgntVO vo, OpenstackNetworkMgntDTO dto) {
        ActionResponse response = null;
        if("v2".equalsIgnoreCase(vo.getOpenstackKeystoneVersion())){
            OSClientV2 os= getOpenstackClientV2(vo);
            response = os.networking().subnet().delete(dto.getSubnetId());
        }else{
            OSClientV3 osV3= getOpenstackClientV3(vo);
            response = osV3.networking().subnet().delete(dto.getSubnetId());
        }
        return response;
    }
    
}
