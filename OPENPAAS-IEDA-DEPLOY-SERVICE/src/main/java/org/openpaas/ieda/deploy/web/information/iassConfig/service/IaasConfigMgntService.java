package org.openpaas.ieda.deploy.web.information.iassConfig.service;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.information.iassConfig.dao.IaasConfigMgntDAO;
import org.openpaas.ieda.deploy.web.information.iassConfig.dao.IaasConfigMgntVO;
import org.openpaas.ieda.deploy.web.information.iassConfig.dto.IaasConfigMgntDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class IaasConfigMgntService{

    @Autowired IaasConfigMgntDAO dao;
    @Autowired MessageSource message;

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 전체 환경 설정 목록 정보 조회
     * @title : getAllIaasConfigInfoList
     * @return : List<IaasConfigMgntVO>
    *****************************************************************/
    public List<IaasConfigMgntVO> getAllIaasConfigInfoList (Principal principal){
        return dao.selectAllIaasConfigInfoList(principal.getName());
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 별 환경 설정 정보 개수 조회
     * @title : getIaasConfigCount
     * @return : HashMap<String, Object>
    ***************************************************/
    public HashMap<String, Integer> getIaasConfigCount (Principal principal){
        return dao.selectIaasConfigCount(principal.getName());
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 해당 인프라 계정 정보 목록 조회
     * @title : getAllIaasConfigInfoList
     * @return : List<IaasConfigMgntVO>
    ***************************************************/
    public List<IaasConfigMgntVO> getIaasConfigInfoList(String iaasType, Principal principal){
        return dao.selectIaasConfigInfoList(principal.getName(), iaasType);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 환경 설정 정보 등록/수정
     * @title : saveIaasConfigInfo
     * @return : void
    ***************************************************/
    public void saveIaasConfigInfo(String iaasType, IaasConfigMgntDTO dto, Principal principal){
        IaasConfigMgntVO vo =null;
        int checkAccountNameCnt = dao.selectIaasConfigDuplicationByConfigName(dto);
         //등록
        if( StringUtils.isEmpty(dto.getId()) ){
            vo =  new IaasConfigMgntVO();
            vo.setIaasType(dto.getIaasType());
            vo.setCreateUserId(principal.getName());
            vo.setTestFlag(dto.getTestFlag());
            //환경 설정 중복체크
            int checkAccountCnt = dao.selectIaasConfigDuplicationByConfigInfo(vo);
            if(checkAccountCnt >  0){
                throw new CommonException(message.getMessage("iaas.configMgnt.conflict.code.exception", null, Locale.KOREA), 
                        message.getMessage("iaas.configMgnt.conflict.message.exception",null, Locale.KOREA), HttpStatus.CONFLICT);
            }
            if(checkAccountNameCnt >  0){
                throw new CommonException(message.getMessage("iaas.configMgnt.conflict.code.exception", null, Locale.KOREA), 
                        message.getMessage("iaas.configMgnt.configAlias.conflict.message.exception",null, Locale.KOREA), HttpStatus.CONFLICT);
            }
        }else{
            vo =  dao.selectIaasConfigInfo(principal.getName(), iaasType, Integer.parseInt(dto.getId()));
            if(!dto.getIaasConfigAlias().equals(vo.getIaasConfigAlias()) && checkAccountNameCnt >  0){
                throw new CommonException(message.getMessage("iaas.configMgnt.conflict.code.exception", null, Locale.KOREA), 
                        message.getMessage("iaas.configMgnt.configAlias.conflict.message.exception",null, Locale.KOREA), HttpStatus.CONFLICT);
            }
        }   
        vo.setIaasConfigAlias( dto.getIaasConfigAlias());
        vo.setAccountId(Integer.parseInt(dto.getAccountId()));
        vo.setCommonRegion( dto.getCommonRegion() );
        vo.setCommonKeypairName( dto.getCommonKeypairName());
        vo.setCommonKeypairPath(dto.getCommonKeypairPath());
        vo.setCommonSecurityGroup(dto.getCommonSecurityGroup());
        vo.setCommonAvailabilityZone(dto.getCommonAvailabilityZone());
        vo.setVsphereVcentDataCenterName(dto.getVsphereVcenterDataCenterName());
        vo.setVsphereVcenterVmFolder(dto.getVsphereVcenterVmFolder());
        vo.setVsphereVcenterTemplateFolder(dto.getVsphereVcenterTemplateFolder());
        vo.setVsphereVcenterDatastore(dto.getVsphereVcenterDatastore());
        vo.setVsphereVcenterPersistentDatastore(dto.getVsphereVcenterPersistentDatastore());
        vo.setVsphereVcenterDiskPath(dto.getVsphereVcenterDiskPath());
        vo.setVsphereVcenterCluster(dto.getVsphereVcenterCluster());
        vo.setAzureResourceGroup(dto.getAzureResourceGroup());
        vo.setAzureStorageAccountName(dto.getAzureStorageAccountName());
        vo.setAzureSshPublicKey(dto.getAzureSshPublicKey());
        vo.setAzurePrivateKey(dto.getAzurePrivateKey());
        vo.setGooglePublicKey(dto.getGooglePublicKey());
        vo.setUpdateUserId(principal.getName());
        if( StringUtils.isEmpty(dto.getId())){
            dao.insertIaasConfigInfo(vo);
        }else{
            dao.updateIaasConfigInfo(vo);
        }
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 환경 설정 정보 상세 조회
     * @title : getIaasConfigInfo
     * @return : IaasConfigMgntVO
    *****************************************************************/
    public IaasConfigMgntVO getIaasConfigInfo(String iaasType, int id, Principal principal) {
        IaasConfigMgntVO iaasConfigInfo = dao.selectIaasConfigInfo(principal.getName(), iaasType, id);
        if(iaasConfigInfo == null){
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), 
                    message.getMessage("common.badRequest.message",null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        return iaasConfigInfo;
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 환경 설정 정보 삭제
     * @title : deleteIaasConfigInfo
     * @return : void
    *****************************************************************/
    public void deleteIaasConfigInfo( IaasConfigMgntDTO dto, Principal principal  ){
        if( StringUtils.isEmpty(dto.getId())  ){
               throw new CommonException(
                       message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), 
                       message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
           }
        dao.deleteIaasConfigInfo(principal.getName(), Integer.parseInt(dto.getId()));
    }
}