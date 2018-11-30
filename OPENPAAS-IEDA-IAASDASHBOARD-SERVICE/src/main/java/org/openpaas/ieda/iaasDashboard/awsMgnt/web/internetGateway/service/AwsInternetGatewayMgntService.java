package org.openpaas.ieda.iaasDashboard.awsMgnt.web.internetGateway.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.iaasDashboard.awsMgnt.api.internetgateway.AwsInternetGatewayMgntApiService;
import org.openpaas.ieda.iaasDashboard.awsMgnt.api.vpc.AwsVpcMgntApiService;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.internetGateway.dao.AwsInternetGatewayMgntVO;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.internetGateway.dto.AwsInternetGatewayMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.model.InternetGateway;
import com.amazonaws.services.ec2.model.Vpc;

@Service
public class AwsInternetGatewayMgntService {
    
    @Autowired CommonIaasService commonIaasService;
    @Autowired AwsInternetGatewayMgntApiService awsInternetGatewayMgntApiService;
    @Autowired AwsVpcMgntApiService awsVpcMgntApiService;
    @Autowired MessageSource message;
    
    /***************************************************
    * @param region 
     * @project : AWS 인프라 관리 대시보드
    * @description : AWS 인터넷 게이트웨이 목록 조화
    * @title : getAwsInternetGatewayInfoList
    * @return : List<AwsVpcMgntVO>
    ***************************************************/
    public List<AwsInternetGatewayMgntVO> getAwsInternetGatewayInfoList(Principal principal, int accountId, String regionName) {
        IaasAccountMgntVO vo =  getAwsAccountInfo(principal, accountId);
        Region region = getAwsRegionInfo(regionName);
        List<AwsInternetGatewayMgntVO> list = new ArrayList<AwsInternetGatewayMgntVO>();
        List<InternetGateway> apiList = awsInternetGatewayMgntApiService.getAwsInternetGatewayInfoListFromAws(vo, region);
        int i = 1;
        for(InternetGateway internetGateway : apiList ){
            String tagName = "";
            String statue = "";
            String vpcId = "";
            AwsInternetGatewayMgntVO internetGatewayVO = new AwsInternetGatewayMgntVO();
            if(internetGateway.getAttachments().size() != 0){
                for(int j = 0; j < internetGateway.getAttachments().size(); j ++){
                    statue += internetGateway.getAttachments().get(j).getState();
                    if(j < internetGateway.getAttachments().size() -1){
                        statue += ", ";
                    }
                }
                internetGatewayVO.setStatus(statue);
                for(int k = 0; k < internetGateway.getAttachments().size(); k++){
                    if(internetGateway.getAttachments().get(k).getVpcId()!=null){
                        vpcId += internetGateway.getAttachments().get(k).getVpcId();
                        if(k < internetGateway.getAttachments().size() -1){
                            vpcId += ", "; 
                        }
                    }
                }
                internetGatewayVO.setVpcId(vpcId);
            }
            if(internetGateway.getTags().size() != 0){
                for(int j=0; j < internetGateway.getTags().size(); j++){
                    tagName += internetGateway.getTags().get(j).getValue();
                    if(j < internetGateway.getTags().size() -1){
                        tagName += ", ";
                    }
                }
                internetGatewayVO.setInternetGatewayName(tagName);
            }
            internetGatewayVO.setInternetGatewayId(internetGateway.getInternetGatewayId());
            internetGatewayVO.setRecid(i);
            internetGatewayVO.setAccountId(accountId);
            i++;
            list.add(internetGatewayVO);
        }
        return list;
    }
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS 인터넷 게이트웨이 생성
    * @title : saveAwsInternetGatewayInfo
    * @return : void
    ***************************************************/
    public void saveAwsInternetGatewayInfo(AwsInternetGatewayMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo =  getAwsAccountInfo(principal, dto.getAccountId());
        Region region = getAwsRegionInfo(dto.getRegion());
        try{
            awsInternetGatewayMgntApiService.saveAwsInternetGatewayInfoFromAws(dto, vo, region);
        }catch (Exception e) {
            String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != null){
                throw new CommonException(
                  detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
            }else{
                throw new CommonException(
                        message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
        }
    }
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS 인터넷 게이트웨이 삭제
    * @title : deleteAwsInternetGatewayInfo
    * @return : void
    ***************************************************/
    public void deleteAwsInternetGatewayInfo(AwsInternetGatewayMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo =  getAwsAccountInfo(principal, dto.getAccountId());
        Region region = getAwsRegionInfo(dto.getRegion());
        try{
            awsInternetGatewayMgntApiService.deleteAwsInternetGatewayInfoFromAws(dto, vo, region);
        }catch (Exception e) {
            String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != null){
                throw new CommonException(
                  detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
            }else{
                throw new CommonException(
                        message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
        }
    }
    
    /***************************************************
     * @param region 
     * @project : AWS 인프라 관리 대시보드
    * @description : 인터넷 게이트에 연결 할 VPC 목록 조회
    * @title : getAwsVpcInfoList
    * @return : List<AwsInternetGatewayMgntVO>
    ***************************************************/
    public List<AwsInternetGatewayMgntVO> getAwsVpcInfoList(Principal principal, int accountId, String regionName) {
        IaasAccountMgntVO vo =  getAwsAccountInfo(principal, accountId);
        Region region = getAwsRegionInfo(regionName);
        List<Vpc> apiVpcList = awsVpcMgntApiService.getAwsVpcInfoListApiFromAws(vo, region.getName());
        List<InternetGateway> apiInternetList = awsInternetGatewayMgntApiService.getAwsInternetGatewayInfoListFromAws(vo, region);
        List<AwsInternetGatewayMgntVO> resultList = new ArrayList<AwsInternetGatewayMgntVO>();
        
        for(int i=0;i<apiVpcList.size();i++){
            String attachedVpcIds ="";
            for(int j=0;j<apiInternetList.size();j++){
                int attSize = apiInternetList.get(j).getAttachments().size();
                if(attSize!=0){
                    for(int k=0; k< attSize; k++){
                        if(apiInternetList.get(j).getAttachments().get(k).getVpcId()!= null ){
                          attachedVpcIds += apiInternetList.get(j).getAttachments().get(k).getVpcId();
                        }
                    }
                    
                }
            }
            AwsInternetGatewayMgntVO internetGatewayVO = new AwsInternetGatewayMgntVO();
            //VPC id가 연결을 가지고 있는 VPC id 목록에 해당 되지 않을 때에만 실행 한다.
            if(attachedVpcIds.contains(apiVpcList.get(i).getVpcId()) != true){
                internetGatewayVO.setVpcId(apiVpcList.get(i).getVpcId());
                //VPC에 nameTag가 있으면 VO로 nameTag 값을 넘겨준다.
                int tsize = apiVpcList.get(i).getTags().size();
                String tags = "";
                if(tsize !=0){
                  for (int m=0; m<tsize; m++ ){
                    tags += apiVpcList.get(i).getTags().get(m).getValue().toString();
                  }
                  internetGatewayVO.setVpcName(tags);
                }
                resultList.add(internetGatewayVO);
            }
        }
        return resultList;
    }
    
    /***************************************************
     * @project : AWS 인프라 관리 대시보드
    * @description : AWS VPC 연결
    * @title : internetGatewayAttachVpc
    * @return : void
    ***************************************************/
    public void internetGatewayAttachVpc(AwsInternetGatewayMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo =  getAwsAccountInfo(principal, dto.getAccountId());
        Region region = getAwsRegionInfo(dto.getRegion());
        try{
            awsInternetGatewayMgntApiService.internetGatewayAttachVpcFromAws(dto, vo, region);
        }catch (Exception e) {
            String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != null){
                throw new CommonException(
                  detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
            }else{
                throw new CommonException(
                        message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
        }
    }
    
    /***************************************************
     * @project : AWS 인프라 관리 대시보드
    * @description : AWS VPC 연결 해제
    * @title : internetGatewayAttachVpc
    * @return : void
    ***************************************************/
    public void internetGatewayDetachVpc(AwsInternetGatewayMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo =  getAwsAccountInfo(principal, dto.getAccountId());
        Region region = getAwsRegionInfo(dto.getRegion());
        try{
            awsInternetGatewayMgntApiService.internetGatewayDetachVpcFromAws(dto, vo, region);
        }catch (Exception e) {
            String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != null){
                throw new CommonException(
                  detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
            }else{
                throw new CommonException(
                        message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
        }
    }
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS 리전 명 조회
    * @title : getAwsRegionInfo
    * @return : Region
    ***************************************************/
    public Region getAwsRegionInfo(String regionName) {
        return commonIaasService.getAwsRegionInfo(regionName);
    }
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS 계정 정보가 실제 존재 하는지 확인 및 상세 조회
    * @title : getAwsAccountInfo
    * @return : IaasAccountMgntVO
    ***************************************************/
    public IaasAccountMgntVO getAwsAccountInfo(Principal principal, int accountId){
        return commonIaasService.getIaaSAccountInfo(principal, accountId, "aws");
    }
    
}