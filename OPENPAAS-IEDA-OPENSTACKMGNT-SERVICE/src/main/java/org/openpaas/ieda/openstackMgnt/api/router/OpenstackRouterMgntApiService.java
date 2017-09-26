package org.openpaas.ieda.openstackMgnt.api.router;

import java.util.ArrayList;
import java.util.List;

import org.openpaas.ieda.common.web.common.service.CommonApiService;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.openstackMgnt.web.router.dao.OpenstackRouterMgntVO;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.network.AttachInterfaceType;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Port;
import org.openstack4j.model.network.Router;
import org.openstack4j.model.network.RouterInterface;
import org.openstack4j.model.network.Subnet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;


@Service
public class OpenstackRouterMgntApiService {

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
    * @description : Openstack 라우터 목록 조회 실제 API 호출
    * @title : getOpenstackRouterInfoListApiFromOpenstack
    * @return : List<? extends Router>
    ***************************************************/
    public List<? extends Router> getOpenstackRouterInfoListApiFromOpenstack(IaasAccountMgntVO vo){
        OSClientV2 os = getOpenstackClientV2(vo);
        List<? extends Router> Routerlist = os.networking().router().list();
        return Routerlist;
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : Openstack 라우터 생성 실제 API 호출
    * @title : createOpenstackRouterCreateApiFromOpenstack
    * @return : boolean
    ***************************************************/
    public boolean createOpenstackRouterApiFromOpenstack(IaasAccountMgntVO vo, OpenstackRouterMgntVO rvo){
        boolean flag = false;
        OSClientV2 os = getOpenstackClientV2(vo);
        Router router = os.networking().router().create(Builders.router()
                .name(rvo.getRouterName())
                .adminStateUp(true)
                .build());
        if(router.getId() != null){
            flag = true;
        }
        return flag;
    }
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : Openstack 라우터 삭제 실제 API 호출
    * @title : deleteOpenstackRouterApiFromOpenstack
    * @return : boolean
    ***************************************************/
    public boolean deleteOpenstackRouterApiFromOpenstack(IaasAccountMgntVO vo, OpenstackRouterMgntVO rvo){
        boolean flag = false;
        OSClientV2 os = getOpenstackClientV2(vo);
        List<? extends Router> RouterList = getOpenstackRouterInfoListApiFromOpenstack(vo);
        for(int i=0;i<RouterList.size();i++){
            if(rvo.getRouteId().equals(RouterList.get(i).getId())){
                os.networking().router().delete(rvo.getRouteId());
                flag = true;
            }
        }
        return flag;
    }
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : Openstack 라우터 정보 API 호출
    * @title : getRouterApiFromOpenstack
    * @return : Router
    ***************************************************/
    public Router getRouterApiFromOpenstack(IaasAccountMgntVO vo, String routeId){
        OSClientV2 os = getOpenstackClientV2(vo);
        Router router = os.networking().router().get(routeId);
        return router;
    }
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : Openstack 네트워크 Port 정보 API 호출
    * @title : getOpenstackNetworkPortApiFromOpenstack
    * @return : List<? extends Port>
    ***************************************************/
    public List<? extends Port> getOpenstackNetworkPortApiFromOpenstack(IaasAccountMgntVO vo){
        OSClientV2 os = getOpenstackClientV2(vo);
        return os.networking().port().list();
    }
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : Openstack 라우터 인터페이스 연결(생성)
    * @title : attachRouterInterfaceApiFromOpenstack
    * @return : boolean
    ***************************************************/
    public boolean attachRouterInterfaceApiFromOpenstack(IaasAccountMgntVO vo, OpenstackRouterMgntVO rvo){
        boolean flag = false;
        OSClientV2 os = getOpenstackClientV2(vo);
        RouterInterface iface = os.networking().router()
                                    .attachInterface(rvo.getRouteId(), AttachInterfaceType.SUBNET, rvo.getSubnetId());
        if(iface!=null){
            flag = true;
        }
        return flag;
    }
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : Openstack 라우터 인터페이스 연결해제(삭제)
    * @title : detachRouterInterfaceApiFromOpenstack
    * @return : boolean
    ***************************************************/
    public boolean detachRouterInterfaceApiFromOpenstack(IaasAccountMgntVO vo, OpenstackRouterMgntVO rvo){
        boolean flag = false;
        OSClientV2 os = getOpenstackClientV2(vo);
        RouterInterface iface = os.networking().router()
                                    .detachInterface(rvo.getRouteId(), rvo.getSubnetId(), null);
        if(iface!=null){
            flag = true;
        }
        return flag;
    }
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : Openstack 네트워크 서브넷 정보리스트
    * @title : getNetworkSubnetInfoApiFromOpenstack
    * @return : List<? extends Subnet>
    ***************************************************/
    public List<? extends Subnet> getNetworkSubnetInfoApiFromOpenstack(IaasAccountMgntVO vo,String networkId){
        OSClientV2 os = getOpenstackClientV2(vo);
        return os.networking().network().get(networkId).getNeutronSubnets();
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : Openstack 라우터 External Network 유무판단
    * @title : getRouterExternalNetworkInfoApiFromOpenstack
    * @return : String
    ***************************************************/
    public String getRouterExternalNetworkInfoApiFromOpenstack(IaasAccountMgntVO vo, int count){
        String exnetInfo = "";
        OSClientV2 os = getOpenstackClientV2(vo);
        List<? extends Router> RouterList = getOpenstackRouterInfoListApiFromOpenstack(vo);
        if(RouterList.get(count).getExternalGatewayInfo() != null){
            exnetInfo = os.networking().network().get(RouterList.get(count).getExternalGatewayInfo().getNetworkId()).getName();
        }else{
            exnetInfo = "-";
        }
        return exnetInfo;
    }
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : Openstack 네트워크 정보 리스트 API 호출
    * @title : getNetworkInfoApiFromOpenstack
    * @return : List<String>
    ***************************************************/
    public List<String> getNetworkInfoApiFromOpenstack(IaasAccountMgntVO vo){
    	OSClientV2 os = getOpenstackClientV2(vo);
    	List<? extends Network> nList = os.networking().network().list();
    	List<String> resultId = new ArrayList<String>();
    	for(int i=0;i<nList.size();i++){
    		String exId = "";
    		if(nList.get(i).isShared()==true){
    			exId = nList.get(i).getId();
    			resultId.add(exId);
    		}
    	}
    	return resultId;
    }
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : Openstack 라우터 게이트웨이 연결 API 호출
    * @title : setRouterGatewayAttachApiFromOpenstack
    * @return : boolean
    ***************************************************/
    public boolean setRouterGatewayAttachApiFromOpenstack(IaasAccountMgntVO vo, OpenstackRouterMgntVO rvo){
    	boolean flag = false;
    	OSClientV2 os = getOpenstackClientV2(vo);
    	Router router = getRouterApiFromOpenstack(vo, rvo.getRouteId());
    	router = os.networking().router().update(router.toBuilder().externalGateway(rvo.getNetworkId()).build());
    	if(rvo.getNetworkId().equals(router.getExternalGatewayInfo().getNetworkId())){
    		flag = true;
    	}
    	return flag;
    }
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : Openstack 라우터 게이트웨이 해제 API 호출
    * @title : setRouterGatewayDetachApiFromOpenstack
    * @return : boolean
    ***************************************************/
    public boolean setRouterGatewayDetachApiFromOpenstack(IaasAccountMgntVO vo, OpenstackRouterMgntVO rvo){
    	boolean flag = false;
    	OSClientV2 os = getOpenstackClientV2(vo);
    	Router router = getRouterApiFromOpenstack(vo, rvo.getRouteId());
    	router = os.networking().router().update(router.toBuilder().externalGateway(rvo.getNetworkId()).clearExternalGateway().build());
    	flag = true;
    	return flag;
    }
}
