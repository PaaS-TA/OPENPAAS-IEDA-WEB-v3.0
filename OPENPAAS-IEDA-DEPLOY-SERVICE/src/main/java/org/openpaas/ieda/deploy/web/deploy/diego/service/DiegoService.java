package org.openpaas.ieda.deploy.web.deploy.diego.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.common.api.Sha512Crypt;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.common.dao.CommonDeployDAO;
import org.openpaas.ieda.deploy.web.common.dao.ManifestTemplateVO;
import org.openpaas.ieda.deploy.web.common.dto.ReplaceItemDTO;
import org.openpaas.ieda.deploy.web.common.service.CommonDeployUtils;
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkDAO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkVO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.resource.ResourceDAO;
import org.openpaas.ieda.deploy.web.deploy.diego.dao.DiegoDAO;
import org.openpaas.ieda.deploy.web.deploy.diego.dao.DiegoVO;
import org.openpaas.ieda.deploy.web.deploy.diego.dto.DiegoListDTO;
import org.openpaas.ieda.deploy.web.deploy.diego.dto.DiegoParamDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class DiegoService {

    @Autowired private DiegoDAO diegoDao; 
    @Autowired private NetworkDAO networkDao;
    @Autowired private ResourceDAO resourceDao;
    @Autowired private CommonDeployDAO commonDao;
    @Autowired MessageSource message;
    
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String CF_FILE  = LocalDirectoryConfiguration.getDeploymentDir()+ SEPARATOR;
    final private static String TEMP_FILE = LocalDirectoryConfiguration.getTempDir() + SEPARATOR;
    final private static String SHELLSCRIPT_FILE = LocalDirectoryConfiguration.getManifastTemplateDir() + SEPARATOR  + "diego" + SEPARATOR;
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 목록 정보를 조회
     * @title : getDiegoInfoList
     * @return : List<DiegoListDTO>
    *****************************************************************/
    public List<DiegoListDTO> getDiegoInfoList(String iaasType) {
        List<DiegoListDTO> diegoList = null;
        List<DiegoVO> resultList = diegoDao.selectDiegoListInfo(iaasType);
        String deployType = setMessageSourceValue("common.deploy.type.diego.name");
        
        if( resultList != null ){
            diegoList = new ArrayList<DiegoListDTO>();
            int recid = 0;

            for(DiegoVO vo:resultList){
                DiegoListDTO diegoInfo = new DiegoListDTO();
                diegoInfo.setRecid(recid++);
                diegoInfo.setId(vo.getId());
                diegoInfo.setIaas(vo.getIaasType());
                diegoInfo.setCreateDate(vo.getCreateDate());
                diegoInfo.setUpdateDate(vo.getUpdateDate());

                //1.1 기본정보    
                diegoInfo.setDeploymentName(vo.getDeploymentName());
                diegoInfo.setDirectorUuid(vo.getDirectorUuid());
                diegoInfo.setDiegoReleaseName(vo.getDiegoReleaseName());
                diegoInfo.setDiegoReleaseVersion(vo.getDiegoReleaseVersion());
                diegoInfo.setCflinuxfs2rootfsreleaseName(vo.getCflinuxfs2rootfsreleaseName());
                diegoInfo.setCflinuxfs2rootfsreleaseVersion(vo.getCflinuxfs2rootfsreleaseVersion());
                diegoInfo.setCfId(vo.getCfId());
                diegoInfo.setCfDeployment(vo.getCfDeployment());
                diegoInfo.setGardenReleaseName(vo.getGardenReleaseName());
                diegoInfo.setGardenReleaseVersion(vo.getGardenReleaseVersion());
                diegoInfo.setEtcdReleaseName(vo.getEtcdReleaseName());
                diegoInfo.setEtcdReleaseVersion(vo.getEtcdReleaseVersion());
                List<NetworkVO> netowrks = networkDao.selectNetworkList(vo.getId(), deployType);
                vo.setNetworks(netowrks);
                String br = "";
                int cnt = 0;
                String subnetRange , subnetGateway , subnetDns , subnetReservedIp;
                subnetRange = subnetGateway = subnetDns = subnetReservedIp = "";
                String subnetStaticIp, publicStaticIp ,subnetId , cloudSecurityGroups, availabilityZone;
                subnetStaticIp = publicStaticIp = subnetId = cloudSecurityGroups= availabilityZone = "";
                
                if(netowrks  != null){
                    for(NetworkVO networkVO: netowrks){
                        if( "internal".equalsIgnoreCase(networkVO.getNet().toLowerCase() )){
                            cnt ++;
                            if( cnt > 2  && cnt < netowrks.size() ){
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
                        }else {
                            publicStaticIp = networkVO.getSubnetStaticFrom();
                        }
                    }
                    diegoInfo.setSubnetRange(subnetRange);
                    diegoInfo.setSubnetGateway(subnetGateway);
                    diegoInfo.setSubnetDns(subnetDns);
                    diegoInfo.setSubnetReservedIp(subnetReservedIp);
                    diegoInfo.setSubnetStaticIp(subnetStaticIp);
                    diegoInfo.setSubnetId(subnetId);
                    diegoInfo.setCloudSecurityGroups(cloudSecurityGroups);
                    diegoInfo.setAvailabilityZone(availabilityZone);
                    diegoInfo.setPublicStaticIp(publicStaticIp);
                }
                vo.setResource(resourceDao.selectResourceInfo(vo.getId(), deployType));
                if(vo.getResource() != null){
                    //4 리소스 정보    
                    diegoInfo.setStemcellName(vo.getResource().getStemcellName());
                    diegoInfo.setStemcellVersion(vo.getResource().getStemcellVersion());
                    diegoInfo.setBoshPassword(vo.getResource().getBoshPassword());
                    diegoInfo.setDeployStatus(vo.getDeployStatus());
                    diegoInfo.setDeploymentFile(vo.getDeploymentFile());
                    if( !StringUtils.isEmpty(vo.getTaskId()) ) {
                        diegoInfo.setTaskId(vo.getTaskId());
                    }
                }
                diegoList.add(diegoInfo);
            }
        }
        return diegoList;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 정보 상세 조회  
     * @title : getDiegoDetailInfo
     * @return : DiegoVO
    *****************************************************************/
    public DiegoVO getDiegoDetailInfo(int id) {
        DiegoVO vo =  diegoDao.selectDiegoInfo(id);
        String deployType= setMessageSourceValue("common.deploy.type.diego.name");
        if( vo != null ){
            vo.setNetworks(networkDao.selectNetworkList(id, deployType));
            vo.setResource(resourceDao.selectResourceInfo(id, deployType));
            vo.setJobs(diegoDao.selectDiegoJobSettingInfoListBycfId( deployType, id));
        }
        return vo;
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 입력 정보를 바탕으로 manifest 파일 생성 및 배포 파일명 응답  
     * @title : createSettingFile
     * @return : void
    *****************************************************************/
    public void createSettingFile(DiegoVO vo) {
        String content = "";
        ManifestTemplateVO result = null;
        InputStream inputs  = null;
        
        try {
            result = commonDao.selectManifetTemplate(vo.getIaasType().toLowerCase(), vo.getDiegoReleaseVersion(), "DIEGO",vo.getDiegoReleaseName());
            ManifestTemplateVO manifestTemplate = null;
            if(result != null){
                inputs =  this.getClass().getClassLoader().getResourceAsStream("static/deploy_template/diego/"+result.getTemplateVersion()+ SEPARATOR + vo.getIaasType().toLowerCase() + SEPARATOR +result.getInputTemplate());
                content = IOUtils.toString(inputs, "UTF-8");
                manifestTemplate = new ManifestTemplateVO();
                manifestTemplate = setOptionManifestTemplateInfo(result, manifestTemplate, vo);
                manifestTemplate.setMinReleaseVersion(result.getTemplateVersion());
                manifestTemplate.setDeployType("DIEGO");
                manifestTemplate.setIaasType(vo.getIaasType());
            }else {
                throw new CommonException(setMessageSourceValue("common.badRequest.exception.code"),
                        setMessageSourceValue("common.notFound.template.message"), HttpStatus.BAD_REQUEST);
            }
            List<ReplaceItemDTO> replaceItems = setReplaceItems(vo);
            for (ReplaceItemDTO item : replaceItems) {
                content = content.replace(item.getTargetItem(), item.getSourceItem());
            }
            IOUtils.write(content, new FileOutputStream(TEMP_FILE+ vo.getDeploymentFile()), "UTF-8");
            CommonDeployUtils.setShellScript(vo.getDeploymentFile(),  manifestTemplate, vo);
        } catch (IOException e) {
            throw new CommonException(setMessageSourceValue("common.badRequest.exception.code"),
                    setMessageSourceValue("common.badRequest.message"), HttpStatus.BAD_REQUEST);
        } catch(NullPointerException e){
            throw new CommonException(setMessageSourceValue("common.badRequest.exception.code"),
                    setMessageSourceValue("common.badRequest.message"), HttpStatus.BAD_REQUEST);
        }
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : option Manifest 템플릿 정보 설정
     * @title : setOptionManifestTemplateInfo
     * @return : ManifestTemplateVO
    *****************************************************************/
    public ManifestTemplateVO setOptionManifestTemplateInfo(ManifestTemplateVO result, ManifestTemplateVO  manifestTemplate, DiegoVO vo){
        //Base Template File
        if( result.getCommonBaseTemplate() !=null && !(StringUtils.isEmpty( result.getCommonBaseTemplate())) ){
            manifestTemplate.setCommonBaseTemplate( result.getCommonBaseTemplate() );
        } else{
            manifestTemplate.setCommonBaseTemplate("");
        }
        //Job Template File
        if( result.getCommonJobTemplate()!=null && !(StringUtils.isEmpty( result.getCommonJobTemplate())) ){
            manifestTemplate.setCommonJobTemplate( result.getCommonJobTemplate());
        } else{
            manifestTemplate.setCommonJobTemplate("");
        }
        //common option Template File 
        if( "true".equals(vo.getPaastaMonitoringUse().toLowerCase()) && result.getCommonOptionTemplate() != null  && !(StringUtils.isEmpty( result.getCommonOptionTemplate())) ){
            manifestTemplate.setCommonOptionTemplate( result.getCommonOptionTemplate() );
        } else{
            manifestTemplate.setCommonOptionTemplate("");
        }
        //iaas Property Template File
        if( result.getIaasPropertyTemplate() != null && !(StringUtils.isEmpty( result.getIaasPropertyTemplate())) ){
            manifestTemplate.setIaasPropertyTemplate(vo.getIaasType().toLowerCase() +SEPARATOR+ result.getIaasPropertyTemplate() );
        } else{
            manifestTemplate.setIaasPropertyTemplate("");
        }
        //네트워크를 추가할 경우(2개 이상)
        if( vo.getNetworks().size() > 1 && result.getOptionNetworkTemplate() != null && !StringUtils.isEmpty( result.getOptionNetworkTemplate()) ){
            manifestTemplate.setOptionNetworkTemplate(vo.getIaasType().toLowerCase() +SEPARATOR+ result.getOptionNetworkTemplate() );
        } else{
            manifestTemplate.setOptionNetworkTemplate("");
        }
        //option resource Template File 
        if( result.getCommonOptionTemplate() != null  && !(StringUtils.isEmpty( result.getCommonOptionTemplate())) ){
            manifestTemplate.setOptionResourceTemplate( result.getOptionResourceTemplate() );
        } else{
            manifestTemplate.setOptionResourceTemplate("");    
        }
        //option etc Template File(Network 3개 일 경우)
        if( result.getOptionEtc() != null && vo.getNetworks().size() == 3 && !(StringUtils.isEmpty( result.getOptionEtc())) ){
            manifestTemplate.setOptionEtc( vo.getIaasType().toLowerCase() +SEPARATOR+ result.getOptionEtc() );
        } else{
            manifestTemplate.setOptionEtc("");    
        }
        //meta Template File
        if( result.getMetaTemplate() != null && !(StringUtils.isEmpty( result.getMetaTemplate())) ) {
            manifestTemplate.setMetaTemplate(vo.getIaasType().toLowerCase() +SEPARATOR+ result.getMetaTemplate());
        } else{
            manifestTemplate.setMetaTemplate("");    
        }
         //임시 CF 파일 경로 + 명
        manifestTemplate.setCfTempleate( CF_FILE +vo.getCfDeployment() );  
        //shell script File
        manifestTemplate.setShellScript(SHELLSCRIPT_FILE + "generate_diego_manifest");
        
        return manifestTemplate;
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 입력한 정보를 ReplaceItemDTO 목록에 넣고 setReplaceItems 메소드에 응답
     * @title : setReplaceItems
     * @return : List<ReplaceItemDTO>
    *****************************************************************/
    public List<ReplaceItemDTO> setReplaceItems(DiegoVO vo) {
        //1.1 기본정보
        List<ReplaceItemDTO> items = new ArrayList<ReplaceItemDTO>();    
        items.add(new ReplaceItemDTO("[diegoReleaseName]", vo.getDiegoReleaseName()));
        items.add(new ReplaceItemDTO("[diegoReleaseVersion]", "\"" + vo.getDiegoReleaseVersion() + "\""));
        items.add(new ReplaceItemDTO("[etcdReleaseName]", vo.getEtcdReleaseName()));
        items.add(new ReplaceItemDTO("[etcdReleaseVersion]", "\"" + vo.getEtcdReleaseVersion() + "\""));
        items.add(new ReplaceItemDTO("[gardenLinuxReleaseName]", vo.getGardenReleaseName()));
        items.add(new ReplaceItemDTO("[gardenLinuxReleaseVersion]", "\"" + vo.getGardenReleaseVersion() + "\""));
        if(vo.getCflinuxfs2rootfsreleaseName()!=null && !vo.getCflinuxfs2rootfsreleaseName().equalsIgnoreCase("") 
                && vo.getCflinuxfs2rootfsreleaseVersion() != null && !vo.getCflinuxfs2rootfsreleaseVersion().equalsIgnoreCase("")){
            items.add(new ReplaceItemDTO("[cflinuxfs2RootfsReleaseName]", vo.getCflinuxfs2rootfsreleaseName()));
            items.add(new ReplaceItemDTO("[cflinuxfs2RootfsReleaseVersion]", ("\"" + vo.getCflinuxfs2rootfsreleaseVersion()+"\"").trim()));
        }else{
            items.add(new ReplaceItemDTO("[cflinuxfs2RootfsReleaseName]", "\"" + "" + "\""));
            items.add(new ReplaceItemDTO("[cflinuxfs2RootfsReleaseVersion]", "\"" + "" + "\""));
        }
        items.add(new ReplaceItemDTO("[cadvisorDriverIp]", vo.getCadvisorDriverIp()));
        // 2. 네트워크 정보
        for( int i=0; i<vo.getNetworks().size(); i++ ){
            if( "internal".equalsIgnoreCase(vo.getNetworks().get(i).getNet())){
                if( !vo.getIaasType().equalsIgnoreCase("VSPHERE") ){
                    if(i  == 0 ){
                        items.add(new ReplaceItemDTO("[subnetRange]", vo.getNetworks().get(i).getSubnetRange()));
                        items.add(new ReplaceItemDTO("[subnetGateway]", vo.getNetworks().get(i).getSubnetGateway()));
                        items.add(new ReplaceItemDTO("[subnetDns]", vo.getNetworks().get(i).getSubnetDns()));
                        items.add(new ReplaceItemDTO("[subnetReserved]", vo.getNetworks().get(i).getSubnetReservedFrom() + " - " + vo.getNetworks().get(i).getSubnetReservedTo()));
                        items.add(new ReplaceItemDTO("[subnetStatic]", vo.getNetworks().get(i).getSubnetStaticFrom() + " - " + vo.getNetworks().get(i).getSubnetStaticTo()));
                        items.add(new ReplaceItemDTO("[subnetId]", vo.getNetworks().get(i).getSubnetId()));            
                        items.add(new ReplaceItemDTO("[cloudSecurityGroups]", vo.getNetworks().get(i).getCloudSecurityGroups()));
                        items.add(new ReplaceItemDTO("[availabilityZone]", vo.getNetworks().get(i).getAvailabilityZone()));
                        if( "google".equalsIgnoreCase(vo.getIaasType()) ){
                            items.add(new ReplaceItemDTO("[zone]", vo.getNetworks().get(i).getAvailabilityZone()));
                            items.add(new ReplaceItemDTO("[networkName]", vo.getNetworks().get(i).getNetworkName()));
                        }
                    }else if(i > 0){
                        items.add(new ReplaceItemDTO("[subnetRange"+i+"]", vo.getNetworks().get(i).getSubnetRange()));
                        items.add(new ReplaceItemDTO("[subnetGateway"+i+"]", vo.getNetworks().get(i).getSubnetGateway()));
                        items.add(new ReplaceItemDTO("[subnetDns"+i+"]", vo.getNetworks().get(i).getSubnetDns()));
                        items.add(new ReplaceItemDTO("[subnetReserved"+i+"]", vo.getNetworks().get(i).getSubnetReservedFrom() + " - " + vo.getNetworks().get(i).getSubnetReservedTo()));
                        items.add(new ReplaceItemDTO("[subnetStatic"+i+"]", vo.getNetworks().get(i).getSubnetStaticFrom() + " - " + vo.getNetworks().get(i).getSubnetStaticTo()));
                        items.add(new ReplaceItemDTO("[subnetId"+i+"]", vo.getNetworks().get(i).getSubnetId()));            
                        items.add(new ReplaceItemDTO("[cloudSecurityGroups"+i+"]", vo.getNetworks().get(i).getCloudSecurityGroups()));
                        items.add(new ReplaceItemDTO("[availabilityZone"+i+"]", vo.getNetworks().get(i).getAvailabilityZone()));
                        if( "google".equalsIgnoreCase(vo.getIaasType()) ){
                            items.add(new ReplaceItemDTO("[zone"+i+"]", vo.getNetworks().get(i).getAvailabilityZone()));
                            items.add(new ReplaceItemDTO("[networkName"+i+"]", vo.getNetworks().get(i).getNetworkName()));
                        }
                    }
                }else if(vo.getIaasType().equalsIgnoreCase("vsphere")){
                    if(i == 0){
                        items.add(new ReplaceItemDTO("[subnetRange]", vo.getNetworks().get(i).getSubnetRange()));
                        items.add(new ReplaceItemDTO("[subnetGateway]", vo.getNetworks().get(i).getSubnetGateway()));
                        items.add(new ReplaceItemDTO("[subnetDns]", vo.getNetworks().get(i).getSubnetDns()));
                        items.add(new ReplaceItemDTO("[subnetReserved]", vo.getNetworks().get(i).getSubnetReservedFrom() + " - " + vo.getNetworks().get(i).getSubnetReservedTo()));
                        items.add(new ReplaceItemDTO("[subnetStatic]", vo.getNetworks().get(i).getSubnetStaticFrom() + " - " + vo.getNetworks().get(i).getSubnetStaticTo()));
                        items.add(new ReplaceItemDTO("[subnetId]", vo.getNetworks().get(i).getSubnetId()));            
                    }else if(i > 0){
                        items.add(new ReplaceItemDTO("[subnetRange"+i+"]", vo.getNetworks().get(i).getSubnetRange()));
                        items.add(new ReplaceItemDTO("[subnetGateway"+i+"]", vo.getNetworks().get(i).getSubnetGateway()));
                        items.add(new ReplaceItemDTO("[subnetDns"+i+"]", vo.getNetworks().get(i).getSubnetDns()));
                        items.add(new ReplaceItemDTO("[subnetReserved"+i+"]", vo.getNetworks().get(i).getSubnetReservedFrom() + " - " + vo.getNetworks().get(i).getSubnetReservedTo()));
                        items.add(new ReplaceItemDTO("[subnetStatic"+i+"]", vo.getNetworks().get(i).getSubnetStaticFrom() + " - " + vo.getNetworks().get(i).getSubnetStaticTo()));
                        items.add(new ReplaceItemDTO("[subnetId"+i+"]", vo.getNetworks().get(i).getSubnetId()));            
                    }
                }
            }
        } 
        if( vo.getNetworks().size() == 1  ){
            //network1
            items.add(new ReplaceItemDTO("[subnetRange1]", ""));
            items.add(new ReplaceItemDTO("[subnetGateway1]", ""));
            items.add(new ReplaceItemDTO("[subnetDns1]", ""));
            items.add(new ReplaceItemDTO("[subnetReserved1]", ""));
            items.add(new ReplaceItemDTO("[subnetStatic1]", ""));
            items.add(new ReplaceItemDTO("[subnetId1]", ""));
            items.add(new ReplaceItemDTO("[networkName1]", ""));
            items.add(new ReplaceItemDTO("[zone1]", ""));
            //network2
            items.add(new ReplaceItemDTO("[subnetRange2]", ""));
            items.add(new ReplaceItemDTO("[subnetGateway2]", ""));
            items.add(new ReplaceItemDTO("[subnetDns2]", ""));
            items.add(new ReplaceItemDTO("[subnetReserved2]", ""));
            items.add(new ReplaceItemDTO("[subnetStatic2]", ""));
            items.add(new ReplaceItemDTO("[subnetId2]", ""));
            items.add(new ReplaceItemDTO("[networkName2]", ""));
            items.add(new ReplaceItemDTO("[zone2]", ""));
            
            items.add(new ReplaceItemDTO("[cloudSecurityGroups1]", ""));
            items.add(new ReplaceItemDTO("[cloudSecurityGroups2]", ""));
            
            items.add(new ReplaceItemDTO("[availabilityZone1]", ""));
            items.add(new ReplaceItemDTO("[availabilityZone2]", ""));
        }else if( vo.getNetworks().size() > 1 ){
            items.add(new ReplaceItemDTO("[subnetRange2]", ""));
            items.add(new ReplaceItemDTO("[subnetGateway2]", ""));
            items.add(new ReplaceItemDTO("[subnetDns2]", ""));
            items.add(new ReplaceItemDTO("[subnetReserved2]", ""));
            items.add(new ReplaceItemDTO("[subnetStatic2]", ""));
            items.add(new ReplaceItemDTO("[subnetId2]", ""));
            items.add(new ReplaceItemDTO("[cloudSecurityGroups2]", ""));
            items.add(new ReplaceItemDTO("[availabilityZone2]", ""));
            items.add(new ReplaceItemDTO("[networkName2]", ""));
            items.add(new ReplaceItemDTO("[zone2]", ""));
        }
                
        //3.리소스 정보
        items.add(new ReplaceItemDTO("[stemcellName]", vo.getResource().getStemcellName() ));
        items.add(new ReplaceItemDTO("[stemcellVersion]", "\"" + vo.getResource().getStemcellVersion() + "\"" ));    
        items.add(new ReplaceItemDTO("[boshPassword]", Sha512Crypt.Sha512_crypt(vo.getResource().getBoshPassword(), RandomStringUtils.randomAlphabetic(10), 0)));
        
        if("vsphere".equalsIgnoreCase(vo.getIaasType())){
            //small Flavor
            items.add(new ReplaceItemDTO("[sInsTypeCPU]",  String.valueOf(vo.getResource().getSmallCpu())));
            items.add(new ReplaceItemDTO("[sInsTypeRAM]", String.valueOf(vo.getResource().getSmallRam())));
            items.add(new ReplaceItemDTO("[sInsTypeDISK]", String.valueOf(vo.getResource().getSmallDisk())));
            //medium Flavor
            items.add(new ReplaceItemDTO("[mInsTypeCPU]",  String.valueOf(vo.getResource().getMediumCpu())));
            items.add(new ReplaceItemDTO("[mInsTypeRAM]", String.valueOf(vo.getResource().getMediumRam())));
            items.add(new ReplaceItemDTO("[mInsTypeDISK]", String.valueOf(vo.getResource().getMediumDisk())));
            //large Flavor
            items.add(new ReplaceItemDTO("[lInsTypeCPU]",  String.valueOf(vo.getResource().getLargeCpu())));
            items.add(new ReplaceItemDTO("[lInsTypeRAM]", String.valueOf(vo.getResource().getLargeRam())));
            items.add(new ReplaceItemDTO("[lInsTypeDISK]", String.valueOf(vo.getResource().getLargeDisk())));
            //runner Flavor
            items.add(new ReplaceItemDTO("[rInsTypeCPU]",  String.valueOf(vo.getResource().getRunnerCpu())));
            items.add(new ReplaceItemDTO("[rInsTypeRAM]", String.valueOf(vo.getResource().getRunnerRam())));
            items.add(new ReplaceItemDTO("[rInsTypeDISK]", String.valueOf(vo.getResource().getRunnerDisk())));
        }else{
            items.add(new ReplaceItemDTO("[smallInstanceType]", vo.getResource().getSmallFlavor()));
            items.add(new ReplaceItemDTO("[mediumInstanceType]", vo.getResource().getMediumFlavor()));
            items.add(new ReplaceItemDTO("[largeInstanceType]", vo.getResource().getLargeFlavor()));
            items.add(new ReplaceItemDTO("[cellInstanceType]", vo.getResource().getRunnerFlavor()));
        }
        boolean z1Flag = false;
        boolean z2Flag = false;
        boolean z3Flag = false;
        
         // 고급 기능을 사용 하지 않았을 경우 network 사이즈를 기준으로 Replace 초기화
        if( vo.getJobs().size() == 0 ){
            String releaseVersion = setDiegoReleaseVersionToJobInfo(vo.getDiegoReleaseVersion());
            List<HashMap<String, String>> map = getJobTemplateList("DEPLOY_TYPE_DIEGO", releaseVersion);
            for(int i=1; i < 4; i++ ) {//job 인스턴스 최대 개수가 3개이기 때문
                if(i <= vo.getNetworks().size() ){
                    for(int j=0; j < map.size(); j++){
                        items.add( new ReplaceItemDTO("["+map.get(j).get("job_name")+"Z"+i+"]", "1") );
                    }
                }else {
                    for(int j=0; j < map.size(); j++){
                        items.add( new ReplaceItemDTO("["+map.get(j).get("job_name")+"Z"+i+"]", "0") );
                    }
                }
            }
        }
        
        for( int j=0; j< vo.getJobs().size(); j++){
            HashMap<String, Object> map = vo.getJobs().get(j);
            if( vo.getNetworks().size() > 0) {
                items.add( new ReplaceItemDTO("["+map.get("job_name").toString()+map.get("zone").toString().toUpperCase()+"]"
                        , map.get("instances").toString()) );
            }
            if( map.get("zone").toString().equalsIgnoreCase("z1") ) {
                z1Flag = true;
            }
            if( map.get("zone").toString().equalsIgnoreCase("z2") ) {
                z2Flag = true;
            }
            if( map.get("zone").toString().equalsIgnoreCase("z3") ) {
                z3Flag = true;
            }
        }
        
        if( vo.getNetworks().size() == 1 && !z1Flag) {
            for( HashMap<String, Object> map: vo.getJobs()){
                items.add( new ReplaceItemDTO("["+map.get("job_name").toString()+"Z1]", "1") );
                items.add( new ReplaceItemDTO("["+map.get("job_name").toString()+"Z2]", "0") );
                items.add( new ReplaceItemDTO("["+map.get("job_name").toString()+"Z3]", "0") );
            }
        }
        if( ( vo.getNetworks().size() == 2 && !z2Flag ) || z2Flag ) {
            for( HashMap<String, Object> map: vo.getJobs()){
                items.add( new ReplaceItemDTO("["+map.get("job_name").toString()+"Z2]", "1") );
                items.add( new ReplaceItemDTO("["+map.get("job_name").toString()+"Z3]", "0") );
            }
        }
        if( vo.getNetworks().size() == 3 && !z3Flag ){
            for( HashMap<String, Object> map: vo.getJobs()){
                items.add( new ReplaceItemDTO("["+map.get("job_name").toString()+"Z2]", "1") );
                items.add( new ReplaceItemDTO("["+map.get("job_name").toString()+"Z3]", "1") );
            }
        }
        return items;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : job 릴리즈 버전 설정 
     * @title : setDiegoReleaseVersionToJobInfo
     * @return : String
    *****************************************************************/
    public String setDiegoReleaseVersionToJobInfo(String releaseVersion){
        String jobReleaseVersion = "";
        if( releaseVersion.equals("2.0")) {
            jobReleaseVersion = "1.1.0";
        }else if( releaseVersion.equals("3.0") ) {
            jobReleaseVersion = "1.25.3";
        }else if( releaseVersion.equals("3.1") )
            jobReleaseVersion = "1.34.0";
        else{
            jobReleaseVersion = releaseVersion;
        }
        
        return jobReleaseVersion;
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 단순 레코드 삭제
     * @title : deleteDiegoInfoRecord
     * @return : void
    *****************************************************************/
    @Transactional
    public void deleteDiegoInfoRecord(DiegoParamDTO.Delete dto){
        HashMap<String, String> map = new HashMap<String, String>();
        diegoDao.deleteDiegoInfoRecord(Integer.parseInt(dto.getId()));
        String deployType = setMessageSourceValue("common.deploy.type.diego.name");
        if( dto.getId() != null ){
            networkDao.deleteNetworkInfoRecord(Integer.parseInt(dto.getId()), deployType);
            resourceDao.deleteResourceInfo(Integer.parseInt(dto.getId()), deployType);
            map.put("id", dto.getId());
            map.put("deploy_type", deployType);
            diegoDao.deleteDiegoJobSettingInfo(map);
        }
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 및 릴리즈 버전 별 job 목록 조회
     * @title : getJobTemplateList
     * @return : List<HashMap<String,String>>
    *****************************************************************/
    public List<HashMap<String, String>> getJobTemplateList(String deployType, String releaseVersion) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("releaseVersion", releaseVersion);
        map.put("deployType",deployType);
        List<HashMap<String, String>> list = diegoDao.selectDiegoJobTemplatesByReleaseVersion(map);
        return list;
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

}
