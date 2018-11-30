package org.openpaas.ieda.iaasDashboard.awsMgnt.web.keypair.service;

import java.util.List;

import org.openpaas.ieda.common.web.common.service.CommonApiService;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.KeyPairInfo;

@Service
public class AwsKeypairMgntApiService {

    @Autowired
    CommonApiService commonApiService;
    
    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS keypair 목록 조회 실제 API 호출
     * @title : getAwsKeypairInfoListApiFromAws
     * @return : List<KeypairInfo>
     ***************************************************/
    public List<KeyPairInfo> getAwsKeypairInfoListApiFromAws(IaasAccountMgntVO vo, String region){
        AWSStaticCredentialsProvider provider = 
                commonApiService
                .getAwsStaticCredentialsProvider(vo.getCommonAccessUser(), vo.getCommonAccessSecret());
        AmazonEC2Client ec2 = 
                (AmazonEC2Client)AmazonEC2ClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(provider).build();
        List<KeyPairInfo> keyPair = ec2.describeKeyPairs().getKeyPairs();
        
        return keyPair;
    }
    
    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS keypair 생성 실제 API 호출
     * @title : createAwsKeypairApiFromAws
     * @return : void
     ***************************************************/
    public String createAwsKeypairApiFromAws(IaasAccountMgntVO vo, String keyPairName, String region){
        AWSStaticCredentialsProvider provider = 
                commonApiService.getAwsStaticCredentialsProvider(vo.getCommonAccessUser(), vo.getCommonAccessSecret());
        AmazonEC2Client ec2 =
                (AmazonEC2Client)AmazonEC2ClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(provider).build();
        
        CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest().withKeyName(keyPairName);
        CreateKeyPairResult cresult = ec2.createKeyPair(createKeyPairRequest);
        String content = "";
        //요청 결과 정보
        content = cresult.getKeyPair().getKeyMaterial().replace("\n", "\r\n");
        
        return content;
    }
}
