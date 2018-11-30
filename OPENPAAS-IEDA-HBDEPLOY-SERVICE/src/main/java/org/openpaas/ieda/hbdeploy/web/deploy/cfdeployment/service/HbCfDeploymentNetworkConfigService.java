package org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentNetworkConfigDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentNetworkConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto.HbCfDeploymentNetworkConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
public class HbCfDeploymentNetworkConfigService {
    
    @Autowired private MessageSource message;
    @Autowired private  HbCfDeploymentNetworkConfigDAO cfDeploymentNetworkDao;

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Network 정보 조회 
     * @title : getNetworkConfigInfoList selectCfDeploymentNetworkConfigInfoList
     * @return : List< HbCfDeploymentNetworkConfigVO>
    *****************************************************************/
    public List< HbCfDeploymentNetworkConfigVO> getNetworkConfigInfoList() {
        List< HbCfDeploymentNetworkConfigVO> resultList = new ArrayList<HbCfDeploymentNetworkConfigVO>();
        List< HbCfDeploymentNetworkConfigVO> list = cfDeploymentNetworkDao.selectHbCfDeploymentNetworkConfigInfoList();
        if(! list.isEmpty()){
            for( HbCfDeploymentNetworkConfigVO vo :list){
                HbCfDeploymentNetworkConfigVO networkInfo = new HbCfDeploymentNetworkConfigVO();
                networkInfo  = setNetworkInfoList(vo);
                resultList.add(networkInfo);
            }
        }
        return resultList;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Network 정보 등록/수정
     * @title : saveNetworkConfigInfo
     * @return : void
    *****************************************************************/
    @SuppressWarnings("rawtypes")
    @Transactional
    public void saveNetworkConfigInfo(HbCfDeploymentNetworkConfigDTO dto, Principal principal) {
        int count = cfDeploymentNetworkDao.selectHbCfDeploymentNetworkConfigByName(dto.getNetworkName());
        HbCfDeploymentNetworkConfigVO vo = null;
        
        if( StringUtils.isEmpty(dto.getId())){
            vo = new HbCfDeploymentNetworkConfigVO();
            vo.setCreateUserId(principal.getName());
            if(count > 0){
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                        message.getMessage("hybrid.configMgnt.alias.conflict.message.exception", null, Locale.KOREA), HttpStatus.CONFLICT);
            }
        } else {
            vo = cfDeploymentNetworkDao.selectHbCfDeploymentNetworkConfigInfo(dto.getId());
            if(!dto.getNetworkName().equals(vo.getNetworkName()) && count > 0){
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                        message.getMessage("hybrid.configMgnt.alias.conflict.message.exception", null, Locale.KOREA), HttpStatus.CONFLICT);
            }
        }
        
        if(vo != null){
            vo.setIaasType(dto.getIaasType());
            vo.setNetworkName(dto.getNetworkName());
            Iterator<?> itr = dto.getNetworkInfoList().iterator();
            
            while(itr.hasNext()){
                HashMap productitem = (HashMap)itr.next();
                if("External".equalsIgnoreCase(productitem.get("direction").toString())){
                    vo.setPublicStaticIp(productitem.get("publicStaticIp").toString());
                } else {
                    if(dto.getNetworkInfoList().size() == 2){
                        vo.setSubnetId1(productitem.get("subnetId1").toString());
                        vo.setSecurityGroup1(productitem.get("securityGroup1").toString());
                        if("aws".equalsIgnoreCase(vo.getIaasType())) vo.setAvailabilityZone1(productitem.get("availabilityZone1").toString());
                        vo.setSubnetDns1(productitem.get("subnetDns1").toString());
                        vo.setSubnetRange1(productitem.get("subnetRange1").toString());
                        vo.setSubnetReservedFrom1(productitem.get("subnetReservedFrom1").toString());
                        vo.setSubnetReservedTo1(productitem.get("subnetReservedTo1").toString());
                        vo.setSubnetStaticFrom1(productitem.get("subnetStaticFrom1").toString());
                        vo.setSubnetStaticTo1(productitem.get("subnetStaticTo1").toString());
                        vo.setSubnetGateway1(productitem.get("subnetGateway1").toString());
                        vo.setSubnetId2("");
                        vo.setSecurityGroup2("");
                        vo.setSubnetDns2("");
                        vo.setSubnetRange2("");
                        vo.setSubnetReservedFrom2("");
                        vo.setSubnetReservedTo2("");
                        vo.setSubnetStaticFrom2("");
                        vo.setSubnetStaticTo2("");
                        vo.setSubnetGateway2("");
                    } else {
                        vo.setSubnetId1(productitem.get("subnetId1").toString());
                        vo.setSecurityGroup1(productitem.get("securityGroup1").toString());
                        vo.setSubnetDns1(productitem.get("subnetDns1").toString());
                        vo.setSubnetRange1(productitem.get("subnetRange1").toString());
                        vo.setSubnetReservedFrom1(productitem.get("subnetReservedFrom1").toString());
                        vo.setAvailabilityZone1(productitem.get("availabilityZone1").toString());
                        vo.setSubnetReservedTo1(productitem.get("subnetReservedTo1").toString());
                        vo.setSubnetStaticFrom1(productitem.get("subnetStaticFrom1").toString());
                        vo.setSubnetStaticTo1(productitem.get("subnetStaticTo1").toString());
                        vo.setSubnetGateway1(productitem.get("subnetGateway1").toString());
                        productitem = (HashMap)itr.next();
                        vo.setSubnetId2(productitem.get("subnetId2").toString());
                        vo.setSecurityGroup2(productitem.get("securityGroup2").toString());
                        vo.setSubnetDns2(productitem.get("subnetDns2").toString());
                        vo.setSubnetRange2(productitem.get("subnetRange2").toString());
                        if("aws".equalsIgnoreCase(vo.getIaasType())) vo.setAvailabilityZone2(productitem.get("availabilityZone2").toString());
                        vo.setSubnetReservedFrom2(productitem.get("subnetReservedFrom2").toString());
                        vo.setSubnetReservedTo2(productitem.get("subnetReservedTo2").toString());
                        vo.setSubnetStaticFrom2(productitem.get("subnetStaticFrom2").toString());
                        vo.setSubnetStaticTo2(productitem.get("subnetStaticTo2").toString());
                        vo.setSubnetGateway2(productitem.get("subnetGateway2").toString());
                    }
                }
            }
        }
        if( StringUtils.isEmpty(dto.getId()) ){
            cfDeploymentNetworkDao.insertHbCfDeploymentNetworkConfigInfo(vo);
        }else{
            cfDeploymentNetworkDao.updateHbCfDeploymentNetworkConfigInfo(vo);
        }
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Network 정보 목록 설정
     * @title : setNetworkInfoList
     * @return : HbCfDeploymentNetworkConfigVO
    *****************************************************************/
    public HbCfDeploymentNetworkConfigVO setNetworkInfoList(HbCfDeploymentNetworkConfigVO vo){
        
        vo.setAvailabilityZone(vo.getAvailabilityZone1());
        vo.setSubnetId(vo.getSubnetId1());
        vo.setSecurityGroup(vo.getSecurityGroup1());
        vo.setSubnetRange(vo.getSubnetRange1());
        vo.setSubnetGateway(vo.getSubnetGateway1());
        vo.setSubnetDns(vo.getSubnetDns1());
        vo.setSubnetReservedIp(vo.getSubnetReservedFrom1() + "-" + vo.getSubnetReservedTo1());
        vo.setSubnetStaticIp(vo.getSubnetStaticFrom1() + "-" + vo.getSubnetStaticTo1());
        
        if(vo.getAvailabilityZone2() != null ) vo.setAvailabilityZone(vo.getAvailabilityZone1() + "</br>" + vo.getAvailabilityZone2());
        if(vo.getSubnetId2() != null ) vo.setSubnetId(vo.getSubnetId1() + "</br>" + vo.getSubnetId2());
        if(vo.getSecurityGroup2() != null ) vo.setSecurityGroup(vo.getSecurityGroup1() + "</br>" + vo.getSecurityGroup2());
        if(vo.getSubnetRange2() != null ) vo.setSubnetRange(vo.getSubnetRange1() + "</br>" + vo.getSubnetRange2());
        if(vo.getSubnetGateway2() != null ) vo.setSubnetGateway(vo.getSubnetGateway1() + "</br>" + vo.getSubnetGateway2());
        if(vo.getSubnetDns2() != null ) vo.setSubnetDns(vo.getSubnetDns1() + "</br>" + vo.getSubnetDns2());
        if(vo.getSubnetReservedFrom2() != null && vo.getSubnetReservedTo2() != null) vo.setSubnetReservedIp(vo.getSubnetReservedFrom1() + "-" + vo.getSubnetReservedTo1() + "</br>" + vo.getSubnetReservedFrom2() +"-"+ vo.getSubnetReservedTo2());
        if(vo.getSubnetStaticFrom2() != null && vo.getSubnetStaticTo2() != null) vo.setSubnetStaticIp(vo.getSubnetStaticFrom1() + "-" + vo.getSubnetStaticTo1() + "</br>" + vo.getSubnetStaticFrom2() +"-"+ vo.getSubnetStaticTo2());
        return vo;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Network 정보 삭제
     * @title : deleteNetworkConfigInfo
     * @return : void
    *****************************************************************/
    public void deleteNetworkConfigInfo(HbCfDeploymentNetworkConfigDTO dto, Principal principal) {
        if(dto.getId()  == null || dto.getId().toString().isEmpty()){
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        cfDeploymentNetworkDao.deleteHbCfDeploymentNetworkConfigInfo(dto);
    }

}
