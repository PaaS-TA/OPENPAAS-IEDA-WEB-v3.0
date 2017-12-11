package org.openpaas.ieda.deploy.web.deploy.cfDiego.service;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Locale;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfVO;
import org.openpaas.ieda.deploy.web.deploy.cf.dto.CfParamDTO;
import org.openpaas.ieda.deploy.web.deploy.cf.service.CfDeleteDeployAsyncService;
import org.openpaas.ieda.deploy.web.deploy.cf.service.CfService;
import org.openpaas.ieda.deploy.web.deploy.cfDiego.dao.CfDiegoDAO;
import org.openpaas.ieda.deploy.web.deploy.cfDiego.dao.CfDiegoVO;
import org.openpaas.ieda.deploy.web.deploy.cfDiego.dto.CfDiegoParamDTO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkDAO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkVO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.resource.ResourceDAO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.resource.ResourceVO;
import org.openpaas.ieda.deploy.web.deploy.diego.dao.DiegoVO;
import org.openpaas.ieda.deploy.web.deploy.diego.dto.DiegoParamDTO;
import org.openpaas.ieda.deploy.web.deploy.diego.service.DiegoDeleteDeployAsyncService;
import org.openpaas.ieda.deploy.web.deploy.diego.service.DiegoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;

@Service
public class CfDiegoService {

    @Autowired private CfDiegoDAO cfDiegoDao;
    @Autowired private CfService cfService;
    @Autowired private DiegoService diegoService;
    @Autowired private NetworkDAO networkDao;
    @Autowired private ResourceDAO resourceDao;
    @Autowired private CfDeleteDeployAsyncService cfDeleteDeployAsyncService;
    @Autowired private DiegoDeleteDeployAsyncService diegoDeleteDeployAsyncService;
    @Autowired private MessageSource message;
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF & Dieg 목록 조회
     * @title : getCfDiegoList
     * @return : List<CfDiegoVO>
    *****************************************************************/
    public List<CfDiegoVO>  getCfDiegoList(String iaas){
        List<CfDiegoVO> list = cfDiegoDao.selectCfDiegoList(iaas);
        String cfDeployType = message.getMessage("common.deploy.type.cf.name", null, Locale.KOREA);
        String diegoDeployType = message.getMessage("common.deploy.type.diego.name", null, Locale.KOREA);
        //NETWORK
        if( !list.isEmpty() ){
            for( CfDiegoVO cfDiegoVo : list ){
                for( int i=0; i < 2; i++){
                    List<NetworkVO> netoworks = null;
                    if( i == 0 ){
                        netoworks = networkDao.selectNetworkList(cfDiegoVo.getCfVo().getId(), cfDeployType);
                    }else {
                        if( cfDiegoVo.getDiegoVo().getId() == null || cfDiegoVo.getDiegoVo().getId() == 0  ) {
                            break;
                        }
                        netoworks = networkDao.selectNetworkList(cfDiegoVo.getDiegoVo().getId(), diegoDeployType);
                    }
                    String br = "";
                    int cnt = 0;
                    String subnetRange , subnetGateway , subnetDns , subnetReservedIp;
                    subnetRange = subnetGateway = subnetDns = subnetReservedIp = "";
                    String subnetStaticIp ,subnetId , cloudSecurityGroups;
                    subnetStaticIp  = subnetId = cloudSecurityGroups=  "";
                    
                    if(netoworks  != null){
                        for(NetworkVO networkVO: netoworks){
                            if( "internal".equalsIgnoreCase(networkVO.getNet())){
                                cnt ++;
                                if( cnt > 2  && cnt < netoworks.size() ){
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
                            }
                        }
                        
                        //Resource
                        ResourceVO resource = null;
                        if( i == 0 ){
                            cfDiegoVo.getCfVo().getNetwork().setSubnetRange(subnetRange);
                            cfDiegoVo.getCfVo().getNetwork().setSubnetGateway(subnetGateway);
                            cfDiegoVo.getCfVo().getNetwork().setSubnetDns(subnetDns);
                            cfDiegoVo.getCfVo().getNetwork().setSubnetReservedFrom(subnetReservedIp);
                            cfDiegoVo.getCfVo().getNetwork().setSubnetStaticFrom(subnetStaticIp);
                            cfDiegoVo.getCfVo().getNetwork().setSubnetId(subnetId);
                            cfDiegoVo.getCfVo().getNetwork().setCloudSecurityGroups(cloudSecurityGroups);
                            //cf resource
                            resource = resourceDao.selectResourceInfo( cfDiegoVo.getCfVo().getId(), cfDeployType);
                            if( resource != null ){
                                cfDiegoVo.getCfVo().getResource().setStemcellName(resource.getStemcellName());
                                cfDiegoVo.getCfVo().getResource().setStemcellVersion(resource.getStemcellVersion());
                                cfDiegoVo.getCfVo().getResource().setBoshPassword(resource.getBoshPassword());
                            }
                        } else {
                            cfDiegoVo.getDiegoVo().getNetwork().setSubnetRange(subnetRange);
                            cfDiegoVo.getDiegoVo().getNetwork().setSubnetGateway(subnetGateway);
                            cfDiegoVo.getDiegoVo().getNetwork().setSubnetDns(subnetDns);
                            cfDiegoVo.getDiegoVo().getNetwork().setSubnetReservedFrom(subnetReservedIp);
                            cfDiegoVo.getDiegoVo().getNetwork().setSubnetStaticFrom(subnetStaticIp);
                            cfDiegoVo.getDiegoVo().getNetwork().setSubnetId(subnetId);
                            cfDiegoVo.getDiegoVo().getNetwork().setCloudSecurityGroups(cloudSecurityGroups);
                            //diego Resource
                            resource = resourceDao.selectResourceInfo( cfDiegoVo.getDiegoVo().getId(), diegoDeployType);
                            if( resource != null ){
                                cfDiegoVo.getDiegoVo().getResource().setStemcellName(resource.getStemcellName());
                                cfDiegoVo.getDiegoVo().getResource().setStemcellVersion(resource.getStemcellVersion());
                                cfDiegoVo.getDiegoVo().getResource().setBoshPassword(resource.getBoshPassword());
                            }
                        }
                    }
                }
            }
        }
        return list;
    }
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF & DIego 정보 조회
     * @title : getCfDiegoInfo
     * @return : CfDiegoVO
    *****************************************************************/
    public CfDiegoVO getCfDiegoInfo( int id ){
        CfDiegoVO result = cfDiegoDao.selectCfDiegoInfoById(id);
        CfDiegoVO cfDiegoVo = new CfDiegoVO();

        if( result != null ){
            if( result.getCfVo().getId() != 0 ){
                CfVO cfVo = cfService.getCfInfo( result.getCfVo().getId() );
                cfDiegoVo.setIaasType(cfVo.getIaasType());
                cfDiegoVo.setCfVo( cfVo );
                if( result.getDiegoVo().getId() != 0 ){
                    DiegoVO diegoVo =  diegoService.getDiegoDetailInfo( result.getDiegoVo().getId() );
                    cfDiegoVo.setDiegoVo( diegoVo );
                }
            } 
        }else{
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        return cfDiegoVo;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 및 Diego 배포 파일 생성
     * @title : createSettingFile
     * @return : void
    *****************************************************************/
    public void createSettingFile(CfDiegoParamDTO.Install dto ){
        if( "cf".equalsIgnoreCase(dto.getPlatform()) ){
            CfVO vo = cfService.getCfInfo( Integer.parseInt(dto.getId()) );
            cfService.createSettingFile(vo);
        }else{
            DiegoVO vo = diegoService.getDiegoDetailInfo( Integer.parseInt(dto.getId()) );
            diegoService.createSettingFile(vo);
        }
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 및 Diego 단순 레코드 삭제
     * @title : deleteCfDiegoInfoRecord
     * @return : void
    *****************************************************************/
    @Transactional
    public void deleteCfDiegoInfoRecord(CfDiegoParamDTO.Delete dto){
        ObjectMapper mapper = new ObjectMapper();
        CfDiegoVO vo = null;
        CfDiegoVO cfDiegoInfo  = null;
        try{
            if( dto.getPlatform().equalsIgnoreCase("diego") ){//delete diego
                vo = cfDiegoDao.selectCfDiegoInfoByPlaform(dto.getPlatform(), Integer.parseInt(dto.getId()) );
                if( vo != null && vo.getDiegoVo().getId() != 0 ){
                    String json =  new Gson().toJson(dto);
                    DiegoParamDTO.Delete diegoDto = mapper.readValue(json, DiegoParamDTO.Delete.class);
                    diegoService.deleteDiegoInfoRecord(diegoDto);
                }
            }
            if( dto.getPlatform().equalsIgnoreCase("cf") ){//delete cf
                cfDiegoInfo = cfDiegoDao.selectCfDiegoInfoByPlaform(dto.getPlatform(), Integer.parseInt(dto.getId()) );
                if(  cfDiegoInfo != null && cfDiegoInfo.getCfVo().getId() != 0  ){
                    dto.setId(String.valueOf(cfDiegoInfo.getCfVo().getId()));
                    String json =  new Gson().toJson(dto);
                    CfParamDTO.Delete  cfDto = mapper.readValue(json, CfParamDTO.Delete.class);
                    cfService.deleteCfInfoRecord(cfDto);
                    //delete cf & diego
                    cfDiegoDao.deleteCfDiegoInfo(cfDiegoInfo.getId());
                }
            }
        }catch(IOException e){
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF & Diego Asnc 삭제
     * @title : deleteCfDiego
     * @return : void
    *****************************************************************/
    public void deleteCfDiego(CfDiegoParamDTO.Delete dto, Principal principal){
        ObjectMapper mapper = new ObjectMapper();
        String json =  new Gson().toJson(dto);
        CfDiegoVO vo = null;
        try{
            //1.1 get cf & diego info/ id: cf_id
            vo = cfDiegoDao.selectCfDiegoInfoByPlaform(dto.getPlatform(), Integer.parseInt(dto.getId()) );
            if( "cf".equalsIgnoreCase(dto.getPlatform()) ){
                CfParamDTO.Delete cfDto = mapper.readValue(json, CfParamDTO.Delete.class);
                cfDeleteDeployAsyncService.deleteDeployAsync(cfDto, "cfDiego", principal);
                //1.3 delete cf & diego
                cfDiegoDao.deleteCfDiegoInfo(vo.getId());
            }else{
                DiegoParamDTO.Delete diegoDto = mapper.readValue(json, DiegoParamDTO.Delete.class);
                diegoDeleteDeployAsyncService.deleteDeployAsync(diegoDto, "cfDiego", principal);
            }
        }catch (IOException e){
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
    }
}
