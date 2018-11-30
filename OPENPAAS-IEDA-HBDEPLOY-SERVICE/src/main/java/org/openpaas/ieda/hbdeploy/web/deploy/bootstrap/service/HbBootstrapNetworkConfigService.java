package org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.service;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapNetworkConfigDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapNetworkConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootstrapNetworkConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class HbBootstrapNetworkConfigService {
    
    @Autowired private MessageSource message;
    @Autowired private HbBootstrapNetworkConfigDAO bootstrapNetworkDao;
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CPI 목록 정보 조회
     * @title : getNetworkConfigInfoList
     * @return : List<HbBootstrapNetworkConfigVO>
    *****************************************************************/
    public List<HbBootstrapNetworkConfigVO> getNetworkConfigInfoList() {
        List<HbBootstrapNetworkConfigVO> list = bootstrapNetworkDao.selectBootstrapNetworkConfigInfoList();
        return list;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Network 정보 등록/수정
     * @title : saveCpiInfo
     * @return : HbBootstrapCpiVO
    *****************************************************************/
    @Transactional
    public void saveNetworkConfigInfo(HbBootstrapNetworkConfigDTO dto, Principal principal) {
        HbBootstrapNetworkConfigVO vo = null;
        int count = bootstrapNetworkDao.selectBootstrapNetworkConfigByName(dto.getNetworkConfigName());
        if( StringUtils.isEmpty(dto.getId())){
            vo = new HbBootstrapNetworkConfigVO();
            vo.setCreateUserId(principal.getName());
            if(count > 0){
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                        message.getMessage("hybrid.configMgnt.alias.conflict.message.exception", null, Locale.KOREA), HttpStatus.CONFLICT);
            }
        }else{
            vo = bootstrapNetworkDao.selectBootstrapNetworkConfigInfo(Integer.parseInt(dto.getId()), dto.getIaasType().toLowerCase());
            if(!dto.getNetworkConfigName().equals(vo.getNetworkConfigName()) && count > 0){
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                        message.getMessage("hybrid.configMgnt.alias.conflict.message.exception", null, Locale.KOREA), HttpStatus.CONFLICT);
            }
        }
        if( vo != null ){
            vo.setNetworkConfigName(dto.getNetworkConfigName());
            vo.setIaasType(dto.getIaasType());
            vo.setSubnetId(dto.getSubnetId());
            vo.setPrivateStaticIp(dto.getPrivateStaticIp());
            vo.setPublicStaticIp(dto.getPublicStaticIp());
            vo.setSubnetRange(dto.getSubnetRange());
            vo.setSubnetGateway(dto.getSubnetGateway());
            vo.setSubnetDns(dto.getSubnetDns());
            vo.setUpdateUserId(principal.getName());
            vo.setUpdateUserId(principal.getName());
        }
        if( StringUtils.isEmpty(dto.getId()) ){
            bootstrapNetworkDao.insertBootStrapNetworkConfigInfo(vo);
        }else{
            bootstrapNetworkDao.updateBootStrapNetworkConfigInfo(vo);
        }
    }

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Network 정보 삭제
     * @title : deleteNetworkConfigInfo
     * @return : void
    *****************************************************************/
    public void deleteNetworkConfigInfo(HbBootstrapNetworkConfigDTO dto, Principal principal) {
        if(dto.getId() == null || dto.getId().isEmpty()){
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        bootstrapNetworkDao.deleteBootStrapNetworkConfigInfo(dto);
        
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Network 정보 상세 조회
     * @title : deleteNetworkConfigInfo
     * @return : void
    *****************************************************************/
    public HbBootstrapNetworkConfigVO getNetworkConfigInfo(String networkId, String iaasType) {
        HbBootstrapNetworkConfigVO vo = bootstrapNetworkDao.selectBootstrapNetworkConfigInfo(Integer.parseInt(networkId), iaasType.toUpperCase());
        return vo;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 인프라 환경 별 Network 목록 정보 조회
     * @title : getNetworkConfigInfoList
     * @return : void
    *****************************************************************/
    public List<HbBootstrapNetworkConfigVO> getNetworkConfigInfoList(String iaasType) {
        List<HbBootstrapNetworkConfigVO> list = bootstrapNetworkDao.selectBootstrapNetworkConfigInfoListByIaasType(iaasType.toUpperCase());
        return list;
    }
}
