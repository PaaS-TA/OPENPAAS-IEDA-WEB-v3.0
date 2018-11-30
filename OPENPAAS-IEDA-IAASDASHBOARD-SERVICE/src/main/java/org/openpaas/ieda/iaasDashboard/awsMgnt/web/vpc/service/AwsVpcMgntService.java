package org.openpaas.ieda.iaasDashboard.awsMgnt.web.vpc.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.iaasDashboard.awsMgnt.api.vpc.AwsVpcMgntApiService;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.vpc.dao.AwsVpcMgntVO;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.vpc.dto.AwsVpcMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.model.Vpc;

@Service
public class AwsVpcMgntService {
    
    @Autowired AwsVpcMgntApiService awsVpcMgntApiService;
    @Autowired CommonIaasService commonIaasService;
    @Autowired MessageSource message;
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS 목록 조회
    * @title : getAwsVpcInfoList
    * @return : List<AwsVpcMgntVO>
    ***************************************************/
    public List<AwsVpcMgntVO> getAwsVpcInfoList( int accountId, String regionName, Principal principal ) {
        IaasAccountMgntVO vo =  getAwsAccountInfo(principal, accountId);
        Region region = getAwsRegionInfo(regionName);
        List<Vpc> apiAwsVpcList = awsVpcMgntApiService.getAwsVpcInfoListApiFromAws(vo, region.getName());
        List<AwsVpcMgntVO> awsVpcList = new ArrayList<AwsVpcMgntVO>();
        int i = 1;
        for(Vpc vpc : apiAwsVpcList ){
            String nameTag = "";
            String ipv6CidrBlock = "";
            AwsVpcMgntVO awsVpcVo = new AwsVpcMgntVO();
            awsVpcVo.setDefaultVpc(vpc.isDefault());
            awsVpcVo.setIpv4CidrBlock(vpc.getCidrBlock());
            if(vpc.getIpv6CidrBlockAssociationSet().size() > 0 ){
                for(int j= 0; j< vpc.getIpv6CidrBlockAssociationSet().size(); j++){
                    ipv6CidrBlock += vpc.getIpv6CidrBlockAssociationSet().get(j).getIpv6CidrBlock();
                    if(j < vpc.getIpv6CidrBlockAssociationSet().size() -1){
                        ipv6CidrBlock += ", ";
                    }
                }
                awsVpcVo.setIpv6CidrBlock(ipv6CidrBlock);
            }
            if(vpc.getTags().size() > 0){
                for(int j = 0; j < vpc.getTags().size(); j++){
                    nameTag += vpc.getTags().get(j).getValue();
                    if(j < vpc.getTags().size() -1){
                        nameTag += ", ";
                    }
                }
            }
            awsVpcVo.setNameTag(nameTag);
            awsVpcVo.setStatus(vpc.getState());
            awsVpcVo.setDhcpOptionSet(vpc.getDhcpOptionsId());
            awsVpcVo.setTenancy(vpc.getInstanceTenancy());
            awsVpcVo.setVpcId(vpc.getVpcId());
            awsVpcVo.setRecid(i);
            awsVpcVo.setAccountId(accountId);
            i++;
            awsVpcList.add(awsVpcVo);
        }
        return awsVpcList;
    }
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS VPC 정보 상세 조회 정보 설정 
    * @title : getAwsVpcDetailInfo
    * @return : AwsVpcMgntVO
    ***************************************************/
    public AwsVpcMgntVO getAwsVpcDetailInfo(int accountId, String vpcId, Principal principal, String regionName) {
        IaasAccountMgntVO vo =  getAwsAccountInfo(principal, accountId);
        Region region = getAwsRegionInfo(regionName);
        AwsVpcMgntVO awsVpcVo = new AwsVpcMgntVO();
        List<Vpc> apiVpcInfo = null;
        HashMap<String, Boolean> dnsHostMap = null;
        boolean classicLinkDns = false;
        String routeTableId = "";
        String networkAcleId = "";
        try{
            apiVpcInfo = awsVpcMgntApiService.getAwsVpcDetailInfoFromAws(vo, vpcId, region.getName());
            dnsHostMap = awsVpcMgntApiService.getVpcDnsNameInfoFromAws(vo, vpcId, region.getName());
            routeTableId = awsVpcMgntApiService.getRouteTableInfoFromAws(vo, vpcId, region.getName());
            networkAcleId = awsVpcMgntApiService.getNetworkAcleInfoFromAws(vo, vpcId, region.getName());
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
        if(!apiVpcInfo.isEmpty()){
            for(int i = 0; i< apiVpcInfo.size(); i++){
                String tagName = "";
                String ipv6Cidr = "";
                awsVpcVo.setIpv4CidrBlock(apiVpcInfo.get(i).getCidrBlock());
                awsVpcVo.setDefaultVpc(apiVpcInfo.get(i).getIsDefault());
                awsVpcVo.setDhcpOptionSet(apiVpcInfo.get(i).getDhcpOptionsId());
                awsVpcVo.setStatus(apiVpcInfo.get(i).getState());
                awsVpcVo.setTenancy(apiVpcInfo.get(i).getInstanceTenancy());
                if(apiVpcInfo.get(i).getIpv6CidrBlockAssociationSet().size() > 0 ){
                    for(int j=0; j<apiVpcInfo.get(i).getIpv6CidrBlockAssociationSet().size();j++){
                        ipv6Cidr += apiVpcInfo.get(i).getIpv6CidrBlockAssociationSet().get(j).getIpv6CidrBlock();
                        if(j < apiVpcInfo.get(i).getIpv6CidrBlockAssociationSet().size() -1){
                            ipv6Cidr += ", ";
                        }
                    }
                    awsVpcVo.setIpv6CidrBlock(ipv6Cidr);
                }
                if(apiVpcInfo.get(i).getTags().size() > 0){
                    for(int j=0; j< apiVpcInfo.get(i).getTags().size(); j++){
                        tagName += apiVpcInfo.get(i).getTags().get(j).getValue();
                        if(j < apiVpcInfo.get(i).getTags().size() -1){
                            tagName += ", ";
                        }
                        awsVpcVo.setNameTag(tagName);
                    }
                    
                }
            }
        }
        awsVpcVo.setDnsHostNames(dnsHostMap.get("DNShostnames"));
        awsVpcVo.setDnsResolution(dnsHostMap.get("DNSresolution"));
        awsVpcVo.setVpcId(vpcId);
        awsVpcVo.setClassicLinkDns(classicLinkDns);
        awsVpcVo.setRouteTable(routeTableId);
        awsVpcVo.setNetworkAcle(networkAcleId);
        return awsVpcVo;
    }
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS VPC 생성
    * @title : saveAwsVpcInfo
    * @return : void
    ***************************************************/
    public void saveAwsVpcInfo(AwsVpcMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo =  getAwsAccountInfo(principal, dto.getAccountId());
        Region region = getAwsRegionInfo(dto.getRegion());
        try{
        awsVpcMgntApiService.saveAwsVpcInfoApiFromAws(vo, dto, region.getName());
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
    * @description : AWS VPC 삭제
    * @title : deleteAwsVpcInfo
    * @return : void
    ***************************************************/
    public void deleteAwsVpcInfo(AwsVpcMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo =  getAwsAccountInfo(principal, dto.getAccountId());
        Region region = getAwsRegionInfo(dto.getRegion());
        try{
        awsVpcMgntApiService.deleteAwsVpcInfoApiFromAws(vo, dto, region.getName());
        }catch (Exception e) {
            String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != null){
                throw new CommonException(
                  detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
            }else{
                e.printStackTrace();
                throw new CommonException(
                        message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
        }
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
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS 리전 명 조회
    * @title : getAwsRegionInfo
    * @return : Region
    ***************************************************/
    public Region getAwsRegionInfo(String regionName) {
        return commonIaasService.getAwsRegionInfo(regionName);
    }
}