package org.openpaas.ieda.deploy.web.deploy.cf.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.common.dao.CommonDeployDAO;
import org.openpaas.ieda.deploy.web.common.dao.ManifestTemplateVO;
import org.openpaas.ieda.deploy.web.common.dto.ReplaceItemDTO;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfDAO;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfVO;
import org.openpaas.ieda.deploy.web.deploy.cf.dto.CfListDTO;
import org.openpaas.ieda.deploy.web.deploy.cf.dto.CfParamDTO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkDAO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkVO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.resource.ResourceDAO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.resource.ResourceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class CfService {

    @Autowired private CfDAO cfDao;
    @Autowired private NetworkDAO networkDao;
    @Autowired private ResourceDAO resourceDao;
    @Autowired private MessageSource message;
    @Autowired private CommonDeployDAO commonDao;
    
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String DEPLOYMENT_FILE = LocalDirectoryConfiguration.getDeploymentDir() + SEPARATOR;
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :CF 정보 목록 전체 조회   
     * @title : getCfLIst
     * @return : List<CfListDTO>
    *****************************************************************/
    public List<CfListDTO> getCfLIst(String iaas, String platform) {
        List<CfListDTO> cfList = new ArrayList<CfListDTO>();
        String codeName= setMessageSourceValue("common.deploy.type.cf.name");
        List<CfVO> listCf  = cfDao.selectCfList(iaas, platform);
        if( !listCf.isEmpty()){
            int recid = 0;
            for( CfVO vo : listCf ){
                CfListDTO cfInfo = new CfListDTO();
                cfInfo.setRecid(recid++);
                cfInfo.setId(vo.getId());
                cfInfo.setIaas(vo.getIaasType());
                cfInfo.setCreateDate(vo.getCreateDate());
                cfInfo.setUpdateDate(vo.getUpdateDate());
                cfInfo.setDeploymentName(vo.getDeploymentName());
                cfInfo.setDirectorUuid(vo.getDirectorUuid());
                cfInfo.setReleaseName(vo.getReleaseName());
                cfInfo.setReleaseVersion(vo.getReleaseVersion());
                
                cfInfo.setDomain(vo.getDomain());
                cfInfo.setDescription(vo.getDescription());
                cfInfo.setDomainOrganization(vo.getDomainOrganization());
                cfInfo.setPaastaMonitoringUse(vo.getPaastaMonitoringUse());
                cfInfo.setKeyFile(vo.getKeyFile());
                cfInfo.setUserAddSsh(vo.getUserAddSsh());
                //NETWORK
                cfInfo = setNetworkInfoList(cfInfo, vo, codeName);
                
                //Resource
                cfInfo = setResourceListInfo(cfInfo, vo, codeName);
                
                cfInfo.setDeployStatus(vo.getDeployStatus());
                cfInfo.setDeploymentFile(vo.getDeploymentFile());
                if( !StringUtils.isEmpty(vo.getTaskId()) ) {
                    cfInfo.setTaskId(vo.getTaskId());
                }
                cfList.add(cfInfo);
            }
        }
        return cfList;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 별 해당 CF의 네트워크 정보 조회
     * @title : setNetworkInfoList
     * @return : CfListDTO
    *****************************************************************/
    public CfListDTO setNetworkInfoList(CfListDTO cfListInfo, CfVO vo, String codeName){
        List<NetworkVO> netowrks = networkDao.selectNetworkList(vo.getId(), codeName);
        String br = "";
        int cnt = 0;
        String subnetRange , subnetGateway , subnetDns , subnetReservedIp;
        subnetRange = subnetGateway = subnetDns = subnetReservedIp = "";
        String subnetStaticIp ,subnetId , cloudSecurityGroups, availabilityZone, publicStaticIp;
        subnetStaticIp  = subnetId = cloudSecurityGroups = availabilityZone = publicStaticIp = "";
        
        if(netowrks  != null){
            for(NetworkVO networkVO: netowrks){
                if( "internal".equalsIgnoreCase(networkVO.getNet() )){
                    cnt ++;
                    if( cnt > 1  && cnt < netowrks.size() ){
                        br = ""; 
                    }else {
                        br = "<br>";
                    }
                    subnetRange += networkVO.getSubnetRange()  + br;
                    subnetGateway += networkVO.getSubnetGateway() + br;
                    subnetDns += networkVO.getSubnetDns() + br;
                    subnetReservedIp += (networkVO.getSubnetReservedFrom() + " - " +  networkVO.getSubnetReservedTo() + br);
                    subnetStaticIp += networkVO.getSubnetStaticFrom() +" - " + networkVO.getSubnetStaticTo() + br;
                    subnetId += networkVO.getSubnetId() + br;
                    cloudSecurityGroups += networkVO.getCloudSecurityGroups() + br;
                    availabilityZone += networkVO.getAvailabilityZone() + br;
                } else {
                    publicStaticIp += networkVO.getPublicStaticIp();
                }
            }
            cfListInfo.setSubnetRange(subnetRange);
            cfListInfo.setSubnetGateway(subnetGateway);
            cfListInfo.setSubnetDns(subnetDns);
            cfListInfo.setSubnetReservedIp(subnetReservedIp);
            cfListInfo.setSubnetStaticIp(subnetStaticIp);
            cfListInfo.setProxyStaticIps(publicStaticIp);
            cfListInfo.setSubnetId(subnetId);
            cfListInfo.setCloudSecurityGroups(cloudSecurityGroups);
            cfListInfo.setAvailabilityZone(availabilityZone);
        }
        return cfListInfo;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 별 해당 CF 리소스 목록 정보 조회
     * @title : setResourceListInfo
     * @return : CfListDTO
    *****************************************************************/
    public CfListDTO setResourceListInfo(CfListDTO cfListInfo, CfVO vo, String codeName){
        ResourceVO resource = resourceDao.selectResourceInfo(vo.getId(), codeName);
        if( resource != null ){
            cfListInfo.setStemcellName(resource.getStemcellName());
            cfListInfo.setStemcellVersion(resource.getStemcellVersion());
            cfListInfo.setBoshPassword(resource.getBoshPassword());
            if("azure".equals(vo.getIaasType().toLowerCase())){
                cfListInfo.setWindowsStemcellName(resource.getWindowsStemcellName());
                cfListInfo.setWindwosStemcellVersion(resource.getWindowsStemcellVersion());
            }
        }
        return cfListInfo;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 정보 상세 조회
     * @title : getCfInfo
     * @return : CfVO
    *****************************************************************/
    public CfVO getCfInfo(int id) {
        String codeName= setMessageSourceValue("common.deploy.type.cf.name");
        CfVO vo = cfDao.selectCfInfoById(id);
        if( vo == null ){
            throw new CommonException(setMessageSourceValue("common.badRequest.exception.code"), 
                    setMessageSourceValue("common.badRequest.message"), HttpStatus.BAD_REQUEST);
        }
        vo.setNetworks(networkDao.selectNetworkList(id, codeName) );
        vo.setResource(resourceDao.selectResourceInfo(id, codeName));
        vo.setJobs(cfDao.selectCfJobSettingInfoListBycfId(codeName, id));
        return vo;
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 네트워크 정보 목록 조회
     * @title : getNetowrkListInfo
     * @return : List<NetworkVO>
    *****************************************************************/
    public List<NetworkVO> getNetowrkListInfo(int id, String deployType){
        List<NetworkVO> list = networkDao.selectNetworkList(id, deployType);
        return list;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 및 릴리즈 버전 별 job 목록 조회
     * @title : getJobTemplateList
     * @return : List<HashMap<String,String>>
    *****************************************************************/
    public List<HashMap<String, String>> getJobTemplateList(String deployType, String releaseVersion){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("releaseVersion", releaseVersion);
        map.put("deployType", deployType);
        List<HashMap<String, String>> list = cfDao.selectCfJobTemplatesByReleaseVersion(map);
        return list;
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 단순 레코드 삭제 
     * @title : deleteCfInfoRecord
     * @return : void
    *****************************************************************/
    @Transactional
    public void deleteCfInfoRecord(CfParamDTO.Delete dto) {
        String deployType= setMessageSourceValue("common.deploy.type.cf.name");
        cfDao.deleteCfInfoRecord(Integer.parseInt(dto.getId()));
        if( dto.getId() != null ){
            HashMap<String, String> map = new HashMap<String, String>();
            networkDao.deleteNetworkInfoRecord(Integer.parseInt( dto.getId()), deployType );
            resourceDao.deleteResourceInfo( Integer.parseInt(dto.getId()), deployType );
            map.put("id", dto.getId());
            map.put("deploy_type", deployType);
            cfDao.deleteCfJobSettingListById(map);
        }
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : message 값 가져오기
     * @title : setMessageSourceValue
     * @return : String
    *****************************************************************/
    public String setMessageSourceValue(String name){
        return message.getMessage(name, null, Locale.KOREA);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bosh Create Cloud Config Replace 공통
     * @title : setMessageSourceValue
     * @return : String
    *****************************************************************/
    public void commonCreateCloudConfig(CfVO vo) {
        ManifestTemplateVO result = commonDao.selectManifetTemplate(vo.getIaasType(), vo.getReleaseVersion(), "CFDEPLOYMENT", vo.getReleaseName());
        String content = "";
        String cloudConfigType = "";
        InputStream inputs  = null;
        if(result == null) {
            throw new CommonException(setMessageSourceValue("common.badRequest.exception.code"), 
                  "설치 가능한 CF Deployment 버전을 확인하세요.", HttpStatus.BAD_REQUEST);
        }
        
        if(vo.getNetworks() != null && vo.getNetworks().size() != 0){
            if(vo.getNetworks().size() == 2) cloudConfigType = "/cloud-config.yml";
            else if(vo.getNetworks().size() == 3) cloudConfigType = "/cloud-config-network-2.yml";
        }
        
        inputs =  this.getClass().getClassLoader().getResourceAsStream("static/deploy_template/cf-deployment/"+ vo.getIaasType().toLowerCase() + cloudConfigType);
        
        if(inputs == null) {
            throw new CommonException(setMessageSourceValue("common.badRequest.exception.code"), 
                  "배포 파일 정보가 존재 하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
        try {
            content = IOUtils.toString(inputs, "UTF-8");
            List<ReplaceItemDTO> replaceItems = makeReplaceItems(vo);
            for (ReplaceItemDTO item : replaceItems) {
                content = content.replace(item.getTargetItem(), item.getSourceItem() == null ? "":item.getSourceItem());
            }
            IOUtils.write(content, new FileOutputStream(DEPLOYMENT_FILE + SEPARATOR + vo.getDeploymentFile()), "UTF-8");
        } catch (IOException e) {
            throw new CommonException(setMessageSourceValue("common.badRequest.exception.code"), 
                    setMessageSourceValue("common.badRequest.message"), HttpStatus.BAD_REQUEST);
        }
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 화면에 입력한 값을 통해 Cloud Config 생성
     * @title : makeReplaceItems
     * @return : List<ReplaceItemDTO>
    ***************************************************/
    public List<ReplaceItemDTO> makeReplaceItems(CfVO vo) {
        List<ReplaceItemDTO> items = new ArrayList<ReplaceItemDTO>();
        int internalCnt = 0;
        if(!"vsphere".equals(vo.getIaasType().toLowerCase())){
            if(vo.getNetworks() != null){
                for(int i=0; i<vo.getNetworks().size(); i++){
                       // 반복문을 줄일 수 Replace Item을 들고 있다 한번에 치환하여 반복문을 줄일 수 없음.
                    if("INTERNAL".equalsIgnoreCase(vo.getNetworks().get(i).getNet())){
                        internalCnt ++;
                        if(internalCnt == 1){
                            items.add(new ReplaceItemDTO("[net_id]", vo.getNetworks().get(i).getSubnetId())); 
                            items.add(new ReplaceItemDTO("[security_group]", vo.getNetworks().get(i).getCloudSecurityGroups())); 
                            items.add(new ReplaceItemDTO("[range]", vo.getNetworks().get(i).getSubnetRange())); 
                            items.add(new ReplaceItemDTO("[gateway]", vo.getNetworks().get(i).getSubnetGateway())); 
                            items.add(new ReplaceItemDTO("[reserved]", vo.getNetworks().get(i).getSubnetReservedFrom() + " - " + vo.getNetworks().get(i).getSubnetReservedTo() ));
                            items.add(new ReplaceItemDTO("[static]", vo.getNetworks().get(i).getSubnetStaticFrom() + " - " + vo.getNetworks().get(i).getSubnetStaticTo() ));
                            items.add(new ReplaceItemDTO("[dns]", vo.getNetworks().get(i).getSubnetDns()));
                            items.add(new ReplaceItemDTO("[availabilityzone]", vo.getNetworks().get(i).getAvailabilityZone()));
                            if("azure".equals(vo.getIaasType().toLowerCase())){
                                items.add(new ReplaceItemDTO("[network_name]", vo.getNetworks().get(i).getNetworkName()));
                                items.add(new ReplaceItemDTO("[subnet_name]", vo.getNetworks().get(i).getSubnetId())); 
                            } else if("google".equals(vo.getIaasType().toLowerCase())){
                                items.add(new ReplaceItemDTO("[network_name]", vo.getNetworks().get(i).getNetworkName()));
                                items.add(new ReplaceItemDTO("[subnetwork_name]", vo.getNetworks().get(i).getSubnetId())); 
                            }
                        } else if(internalCnt == 2 ) {
                            items.add(new ReplaceItemDTO("[net_id2]", vo.getNetworks().get(i).getSubnetId())); 
                            items.add(new ReplaceItemDTO("[security_group2]", vo.getNetworks().get(i).getCloudSecurityGroups())); 
                            items.add(new ReplaceItemDTO("[range2]", vo.getNetworks().get(i).getSubnetRange())); 
                            items.add(new ReplaceItemDTO("[gateway2]", vo.getNetworks().get(i).getSubnetGateway())); 
                            items.add(new ReplaceItemDTO("[reserved2]", vo.getNetworks().get(i).getSubnetReservedFrom() + " - " + vo.getNetworks().get(i).getSubnetReservedTo() ));
                            items.add(new ReplaceItemDTO("[static2]", vo.getNetworks().get(i).getSubnetStaticFrom() + " - " + vo.getNetworks().get(i).getSubnetStaticTo() ));
                            items.add(new ReplaceItemDTO("[dns2]", vo.getNetworks().get(i).getSubnetDns()));
                            items.add(new ReplaceItemDTO("[availabilityzone2]", vo.getNetworks().get(i).getAvailabilityZone()));
                            if("azure".equals(vo.getIaasType().toLowerCase())){
                                items.add(new ReplaceItemDTO("[network_name2]", vo.getNetworks().get(i).getNetworkName()));
                                items.add(new ReplaceItemDTO("[subnet_name2]", vo.getNetworks().get(i).getSubnetId())); 
                            } else if("google".equals(vo.getIaasType().toLowerCase())){
                                items.add(new ReplaceItemDTO("[network_name2]", vo.getNetworks().get(i).getNetworkName()));
                                items.add(new ReplaceItemDTO("[subnetwork_name2]", vo.getNetworks().get(i).getSubnetId()));
                            }
                        }
                    }
                }
            }
            items.add(new ReplaceItemDTO("[small_instance_type]", vo.getResource().getSmallFlavor()));
            items.add(new ReplaceItemDTO("[medium_instance_type]", vo.getResource().getMediumFlavor()));
            items.add(new ReplaceItemDTO("[large_instance_type]", vo.getResource().getLargeFlavor()));

        } else {
            //vSphere 환경일 경우
            for(int i=0; i<vo.getNetworks().size(); i++){
                if(vo.getNetworks() != null){
                    if("EXTERNAL".equalsIgnoreCase(vo.getNetworks().get(i).getNet())){
                        items.add(new ReplaceItemDTO("[public_gateway]", vo.getNetworks().get(i).getSubnetGateway()));
                        items.add(new ReplaceItemDTO("[public_dns]", vo.getNetworks().get(i).getSubnetDns()));
                        items.add(new ReplaceItemDTO("[public_range]", vo.getNetworks().get(i).getSubnetRange()));
                        items.add(new ReplaceItemDTO("[public_reserved]", vo.getNetworks().get(i).getSubnetStaticTo() + " - " + vo.getNetworks().get(i).getSubnetStaticTo()));
                        items.add(new ReplaceItemDTO("[public_network_name]", vo.getNetworks().get(i).getNet()));
                    } else {
                        internalCnt ++;
                        if(internalCnt == 1){
                            items.add(new ReplaceItemDTO("[private_gateway]", vo.getNetworks().get(i).getSubnetGateway()));
                            items.add(new ReplaceItemDTO("[private_dns]", vo.getNetworks().get(i).getSubnetDns()));
                            items.add(new ReplaceItemDTO("[private_range]", vo.getNetworks().get(i).getSubnetRange()));
                            items.add(new ReplaceItemDTO("[private_reserved]", vo.getNetworks().get(i).getSubnetReservedFrom() + " - " + vo.getNetworks().get(i).getSubnetReservedTo()));
                            items.add(new ReplaceItemDTO("[private_static]", vo.getNetworks().get(i).getSubnetStaticFrom() + " - " + vo.getNetworks().get(i).getSubnetStaticTo() ));
                            items.add(new ReplaceItemDTO("[private_network_name]", vo.getNetworks().get(i).getNet()));
                        } else if(internalCnt == 2){
                            items.add(new ReplaceItemDTO("[private_gateway2]", vo.getNetworks().get(i).getSubnetGateway()));
                            items.add(new ReplaceItemDTO("[private_dns2]", vo.getNetworks().get(i).getSubnetDns()));
                            items.add(new ReplaceItemDTO("[private_range2]", vo.getNetworks().get(i).getSubnetRange()));
                            items.add(new ReplaceItemDTO("[private_reserved2]", vo.getNetworks().get(i).getSubnetReservedFrom() + " - " + vo.getNetworks().get(i).getSubnetReservedTo()));
                            items.add(new ReplaceItemDTO("[private_static2]", vo.getNetworks().get(i).getSubnetStaticFrom() + " - " + vo.getNetworks().get(i).getSubnetStaticTo() ));
                            items.add(new ReplaceItemDTO("[private_network_name2]", vo.getNetworks().get(i).getNet()));
                        }
                    }
                }
            }
            items.add(new ReplaceItemDTO("[small_cpu]", String.valueOf(vo.getResource().getSmallCpu())));
            items.add(new ReplaceItemDTO("[small_ram]", String.valueOf(vo.getResource().getSmallRam())));
            items.add(new ReplaceItemDTO("[small_disk]", String.valueOf(vo.getResource().getSmallDisk())));
            items.add(new ReplaceItemDTO("[medium_cpu]", String.valueOf(vo.getResource().getMediumCpu())));
            items.add(new ReplaceItemDTO("[medium_ram]", String.valueOf(vo.getResource().getMediumRam())));
            items.add(new ReplaceItemDTO("[medium_disk]", String.valueOf(vo.getResource().getMediumDisk())));
            items.add(new ReplaceItemDTO("[large_cpu]", String.valueOf(vo.getResource().getLargeCpu())));
            items.add(new ReplaceItemDTO("[large_ram]", String.valueOf(vo.getResource().getLargeRam())));
            items.add(new ReplaceItemDTO("[large_disk]", String.valueOf(vo.getResource().getLargeDisk())));
        }
        return items;
    }
}