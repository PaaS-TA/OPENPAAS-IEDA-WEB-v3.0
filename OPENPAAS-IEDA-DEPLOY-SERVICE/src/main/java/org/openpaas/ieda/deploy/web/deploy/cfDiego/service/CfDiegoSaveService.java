package org.openpaas.ieda.deploy.web.deploy.cfDiego.service;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfDAO;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfVO;
import org.openpaas.ieda.deploy.web.deploy.cf.dto.CfParamDTO;
import org.openpaas.ieda.deploy.web.deploy.cf.service.CfSaveService;
import org.openpaas.ieda.deploy.web.deploy.cfDiego.dao.CfDiegoDAO;
import org.openpaas.ieda.deploy.web.deploy.cfDiego.dao.CfDiegoVO;
import org.openpaas.ieda.deploy.web.deploy.cfDiego.dto.CfDiegoParamDTO;
import org.openpaas.ieda.deploy.web.deploy.common.dto.network.NetworkDTO;
import org.openpaas.ieda.deploy.web.deploy.common.dto.resource.ResourceDTO;
import org.openpaas.ieda.deploy.web.deploy.diego.dao.DiegoDAO;
import org.openpaas.ieda.deploy.web.deploy.diego.dao.DiegoVO;
import org.openpaas.ieda.deploy.web.deploy.diego.dto.DiegoParamDTO;
import org.openpaas.ieda.deploy.web.deploy.diego.service.DiegoSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Service
public class CfDiegoSaveService {
    
    @Autowired CfDiegoDAO cfDiegoDao;
    @Autowired CfSaveService cfSaveService;
    @Autowired DiegoSaveService diegoSaveService;
    @Autowired CfDAO cfDao;
    @Autowired DiegoDAO diegoDao;
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 및 Diego 기본 정보 저장
     * @title : saveDefaultInfo
     * @return : CfDiegoVO
    *****************************************************************/
    @Transactional
    public CfDiegoVO saveDefaultInfo( CfDiegoParamDTO.Default dto, Principal principal) {
        CfDiegoVO vo = null;
        CfVO cfVo = null;
        DiegoVO diegoVo = null;
        CfParamDTO.Default cfDto = null;
        DiegoParamDTO.Default diegoDto = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            String cfJson = new Gson().toJson(dto); 
            if( "cf".equalsIgnoreCase(dto.getPlatform()) ){ //cf update/insert
                cfDto = mapper.readValue(cfJson, CfParamDTO.Default.class);
                cfVo = cfSaveService.saveDefaultInfo(cfDto, principal);
                vo = cfDiegoDao.selectCfDiegoInfoByPlaform( dto.getPlatform(), cfVo.getId());
            }else{ //diego update/insert
                diegoDto = mapper.readValue(cfJson, DiegoParamDTO.Default.class);
                diegoVo = diegoSaveService.saveDefaultInfo(diegoDto, principal);
                vo = cfDiegoDao.selectCfDiegoInfoByPlaform( "cf", diegoVo.getCfId());
                if ( vo != null ){
                    dto.setId( String.valueOf( diegoVo.getId()) );
                }
            }
            
            //cfDiego
            if( (dto.getId() == null || StringUtils.isEmpty(dto.getId())) && ( "cf".equalsIgnoreCase(dto.getPlatform())) ){ 
                if( cfVo != null || diegoVo != null ){
                    //insert cfDiego
                    vo = new CfDiegoVO();
                    vo.setCreateUserId(principal.getName());
                    vo.setUpdateUserId(principal.getName());
                    vo.setIaasType(dto.getIaas());
                    vo.getCfVo().setId(cfVo.getId() != null ? cfVo.getId() : 0);
                    vo.getDiegoVo().setId( diegoVo != null ? diegoVo.getId() : 0 );
                    cfDiegoDao.insertCfDiegoInfo(vo);
                }
            } else{
                //id != null => cfDiego update
                if( "diego".equalsIgnoreCase(dto.getPlatform()) &&  vo.getDiegoVo().getId() == null ){
                    vo.getDiegoVo().setId( Integer.parseInt(dto.getId()) );
                }
                vo.setUpdateUserId(principal.getName());
                cfDiegoDao.updateCfDiegoInfo(vo);
            }
        } catch (IOException e) {
            throw new CommonException("notfound.cfDiego.exception",
                    dto.getPlatform().toUpperCase() + " 정보를 읽어을 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NullPointerException e){
            throw new CommonException("notfound.cfDiego.exception",
                    "CF & Diego 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
        return vo;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 네트워크 정보 저장
     * @title : saveNetworkInfo
     * @return : void
    *****************************************************************/
    public void saveNetworkInfo(List<NetworkDTO> dto, Principal principal ){
        CfDiegoVO vo  = null;
        try{
            if( dto != null ) {
                if( dto.get(0).getCfId() != null ){ //save cf network info 
                    cfSaveService.saveNetworkInfo(dto, principal);
                    vo = cfDiegoDao.selectCfDiegoInfoByPlaform("cf", Integer.parseInt(dto.get(0).getCfId()));
                }else if( dto.get(0).getDiegoId() != null ){ //save diego network info
                    diegoSaveService.saveNetworkInfo(dto, principal);
                    vo = cfDiegoDao.selectCfDiegoInfoByPlaform("diego", Integer.parseInt(dto.get(0).getDiegoId()));
                }else{
                    throw new CommonException("notfound.cfDiego.exception",
                            "CF 또는 Diego 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);
                }
            }
            if( vo != null  ){
                vo.setUpdateUserId(principal.getName());
            }
            cfDiegoDao.updateCfDiegoInfo(vo);
        } catch(NullPointerException e){
            throw new CommonException("nullPoint.cfDiego.exception",
                    "네트워크 정보를 저장할 수 없습니다. ", HttpStatus.NOT_FOUND);
        } 
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 리소스 정보 저장 및 배포 파일명 설정 
     * @title : saveResourceInfo
     * @return : Map<String,Object>
    *****************************************************************/
    public Map<String, Object> saveResourceInfo(ResourceDTO dto, Principal principal){
        Map<String, Object> map  = null;
        //1.1 cf resource
        if( "cf".equalsIgnoreCase(dto.getPlatform()) ){
            map = cfSaveService.saveResourceInfo(dto, principal);
        } else {
            CfDiegoVO cfDiegoVo = cfDiegoDao.selectCfDiegoInfoByPlaform( "cf", Integer.parseInt(dto.getCfId()) );
            String keyFile = cfDiegoVo.getCfVo().getKeyFile();
            dto.setKeyFile(keyFile);
            map = diegoSaveService.saveResourceInfo(dto, principal);
        }
        CfDiegoVO result = cfDiegoDao.selectCfDiegoInfoByPlaform( dto.getPlatform(), Integer.parseInt(map.get("id").toString()) );
        result.setUpdateUserId(principal.getName());
        cfDiegoDao.updateCfDiegoInfo(result);
        
        return map;

    } 
}