package org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.common.dao.ManifestTemplateVO;
import org.openpaas.ieda.deploy.web.common.dto.ReplaceItemDTO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class HbCfDeploymentService {
    
    @Autowired private MessageSource message;
    @Autowired private HbCfDeploymentDAO hbCfDeploymentDAO;
    
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String DEPLOYMENT_FILE = LocalDirectoryConfiguration.getDeploymentDir() + SEPARATOR;
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CF Deployment 정보 목록 조회
     * @title : getHbBCfDeploymentList
     * @return : List<HbCfDeploymentVO>
    ***************************************************/
    public List<HbCfDeploymentVO> getHbCfDeploymentList(String installStatus) {
        List<HbCfDeploymentVO> CfDeploymentList = hbCfDeploymentDAO.selectCfDeploymentList(installStatus);
        return CfDeploymentList;
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CF Deployment 상세 정보 조회
     * @title : getHbCfDeploymentInfo
     * @return : HbCfDeploymentVO
    ***************************************************/
    public HbCfDeploymentVO getHbCfDeploymentInfo(int id){
        HbCfDeploymentVO vo = hbCfDeploymentDAO.selectCfDeploymentConfigInfo(id);
        if(vo == null){
            throw new CommonException(setMessageSourceValue("common.badRequest.exception.code"), 
                    setMessageSourceValue("common.badRequest.message"), HttpStatus.BAD_REQUEST);
        }
        return vo;
    }
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bosh Create Cloud Config Replace 공통
     * @title : setMessageSourceValue
     * @return : String
    *****************************************************************/
    public void commonCreateCloudConfig(HbCfDeploymentVO vo, ManifestTemplateVO result) {
        String content = "";
        String cloudConfigType = "";
        InputStream inputs  = null;
        if(result == null) {
            throw new CommonException(setMessageSourceValue("common.badRequest.exception.code"), 
                  "설치 가능한 CF Deployment 버전을 확인하세요.", HttpStatus.BAD_REQUEST);
        }
        
        if(vo.getHbCfDeploymentNetworkConfigVO() != null){
            if(vo.getHbCfDeploymentNetworkConfigVO().getSubnetId1() != null && !"".equals(vo.getHbCfDeploymentNetworkConfigVO().getSubnetId1())) cloudConfigType = "/cloud-config.yml";
            System.out.println(vo.getHbCfDeploymentNetworkConfigVO().getSubnetId2());
            if(vo.getHbCfDeploymentNetworkConfigVO().getSubnetId2() != null && !"".equals(vo.getHbCfDeploymentNetworkConfigVO().getSubnetId2())) cloudConfigType = "/cloud-config-network-2.yml";
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
            IOUtils.write(content, new FileOutputStream(DEPLOYMENT_FILE + SEPARATOR + vo.getCloudConfigFile()), "UTF-8");
        } catch (IOException e) {
            throw new CommonException(setMessageSourceValue("common.badRequest.exception.code"), 
                    setMessageSourceValue("common.badRequest.message"), HttpStatus.BAD_REQUEST);
        }
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 화면에 입력한 값을 통해 Cloud Config 생성
     * @title : makeReplaceItems
     * @return : List<ReplaceItemDTO>
    ***************************************************/
    public List<ReplaceItemDTO> makeReplaceItems(HbCfDeploymentVO vo) {
        List<ReplaceItemDTO> items = new ArrayList<ReplaceItemDTO>();
        if(vo.getHbCfDeploymentNetworkConfigVO() != null){
            items.add(new ReplaceItemDTO("[net_id]", vo.getHbCfDeploymentNetworkConfigVO().getSubnetId1())); 
            items.add(new ReplaceItemDTO("[security_group]", vo.getHbCfDeploymentNetworkConfigVO().getSecurityGroup1())); 
            items.add(new ReplaceItemDTO("[range]", vo.getHbCfDeploymentNetworkConfigVO().getSubnetRange1())); 
            items.add(new ReplaceItemDTO("[gateway]", vo.getHbCfDeploymentNetworkConfigVO().getSubnetGateway1())); 
            items.add(new ReplaceItemDTO("[reserved]", vo.getHbCfDeploymentNetworkConfigVO().getSubnetReservedFrom1() + " - " + vo.getHbCfDeploymentNetworkConfigVO().getSubnetReservedTo1() ));
            items.add(new ReplaceItemDTO("[static]", vo.getHbCfDeploymentNetworkConfigVO().getSubnetStaticFrom1() + " - " + vo.getHbCfDeploymentNetworkConfigVO().getSubnetStaticTo1() ));
            items.add(new ReplaceItemDTO("[dns]", vo.getHbCfDeploymentNetworkConfigVO().getSubnetDns1()));
            items.add(new ReplaceItemDTO("[availabilityzone]", vo.getHbCfDeploymentNetworkConfigVO().getAvailabilityZone1()));
            if(vo.getHbCfDeploymentNetworkConfigVO().getSubnetId2() != null && !"".equals(vo.getHbCfDeploymentNetworkConfigVO().getSubnetId2())){
                items.add(new ReplaceItemDTO("[net_id2]", vo.getHbCfDeploymentNetworkConfigVO().getSubnetId2())); 
                items.add(new ReplaceItemDTO("[security_group2]", vo.getHbCfDeploymentNetworkConfigVO().getSecurityGroup2())); 
                items.add(new ReplaceItemDTO("[range2]", vo.getHbCfDeploymentNetworkConfigVO().getSubnetRange2())); 
                items.add(new ReplaceItemDTO("[gateway2]", vo.getHbCfDeploymentNetworkConfigVO().getSubnetGateway2())); 
                items.add(new ReplaceItemDTO("[reserved2]", vo.getHbCfDeploymentNetworkConfigVO().getSubnetReservedFrom2() + " - " + vo.getHbCfDeploymentNetworkConfigVO().getSubnetReservedTo2() ));
                items.add(new ReplaceItemDTO("[static2]", vo.getHbCfDeploymentNetworkConfigVO().getSubnetStaticFrom2() + " - " + vo.getHbCfDeploymentNetworkConfigVO().getSubnetStaticTo2() ));
                items.add(new ReplaceItemDTO("[dns2]", vo.getHbCfDeploymentNetworkConfigVO().getSubnetDns2()));
                items.add(new ReplaceItemDTO("[availabilityzone2]", vo.getHbCfDeploymentNetworkConfigVO().getAvailabilityZone2()));
            }
        }
        items.add(new ReplaceItemDTO("[small_instance_type]", vo.getHbCfDeploymentResourceConfigVO().getInstanceTypeS()));
        items.add(new ReplaceItemDTO("[medium_instance_type]", vo.getHbCfDeploymentResourceConfigVO().getInstanceTypeM()));
        items.add(new ReplaceItemDTO("[large_instance_type]", vo.getHbCfDeploymentResourceConfigVO().getInstanceTypeL()));
        return items;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : message 값 가져오기
     * @title : setMessageSourceValue
     * @return : String
    *****************************************************************/
    public String setMessageSourceValue(String name){
        return message.getMessage(name, null, Locale.KOREA);
    }

}
