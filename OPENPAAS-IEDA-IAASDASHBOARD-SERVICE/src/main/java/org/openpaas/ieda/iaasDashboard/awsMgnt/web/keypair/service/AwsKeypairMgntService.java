package org.openpaas.ieda.iaasDashboard.awsMgnt.web.keypair.service;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.model.KeyPairInfo;

@Service
public class AwsKeypairMgntService {
    @Autowired AwsKeypairMgntApiService awsKeypairMgntApiService;
    @Autowired CommonIaasService commonIaasService;
    @Autowired  MessageSource message;
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS KeyPair 목록 조회
    * @title : getAwsKeyPairInfoList
    * @return : List<KeyPairInfo>
    ***************************************************/
    
    public List<KeyPairInfo> getAwsKeyPairInfoList(Principal principal, int accountId, String regionName){
        IaasAccountMgntVO vo = getAwsAccountInfo(principal, accountId);
        Region region = getAwsRegionInfo(regionName);
        List<KeyPairInfo> keypairInfoList = awsKeypairMgntApiService.getAwsKeypairInfoListApiFromAws(vo, region.getName());
        return keypairInfoList;
    }
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS KeyPair 생성
    * @title : createAwsKeyPair
    * @return : void
    ***************************************************/
    public void createAwsKeyPair(Principal principal, String accountId, String keyPairName, String regionName, HttpServletResponse response){
        IaasAccountMgntVO vo = getAwsAccountInfo(principal, Integer.parseInt(accountId));
        Region region = getAwsRegionInfo(regionName);
        List<KeyPairInfo> list = getAwsKeyPairInfoList(principal, Integer.parseInt(accountId), regionName);
            for(int i=0;i<list.size();i++){
                if(keyPairName.equals(list.get(i).getKeyName())){
                    throw new CommonException(
                        message.getMessage("common.conflict.exception.code", null, Locale.KOREA), message.getMessage("common.conflict.message", null, Locale.KOREA), HttpStatus.CONFLICT);
                }
            }
            String content= awsKeypairMgntApiService.createAwsKeypairApiFromAws(vo, keyPairName, region.getName());
            if( StringUtils.isEmpty(content) ){
                throw new CommonException(
                    message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }    
            try {
                String fileName = keyPairName + ".pem";
                response.setContentType("application/octet-stream");
                //Content-Disposition : 브라우저에서 다운로드 창을 띄우는 역할
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
                //문자열 데이터를 파일에 쓴다.
                IOUtils.write(content, response.getOutputStream(), "UTF-8");
            } catch (IOException e) {
                 String detailMessage = e.getMessage();
                 if(!detailMessage.equals("") && detailMessage != null){
                     throw new CommonException(
                     detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
                 }else{
                throw new CommonException(
                    message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
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