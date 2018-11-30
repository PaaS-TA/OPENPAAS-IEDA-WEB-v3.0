package org.openpaas.ieda.iaasDashboard.azureMgnt.web.keypair.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.keypair.dao.AzureKeypairMgntVO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.keypair.dto.AzureKeypairMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AzureKeypairMgntService {
    
    @Autowired
    private CommonIaasService commonIaasService;
    
    final private static String SSH_DIR = LocalDirectoryConfiguration.getSshDir();
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE 계정 정보가 실제 존재 하는지 확인 및 상세 조회
     * @title : getAzureAccountInfo
     * @return : IaasAccountMgntVO
     ***************************************************/
    public IaasAccountMgntVO getAzureAccountInfo(Principal principal, int accountId) {
        return commonIaasService.getIaaSAccountInfo(principal, accountId, "azure");
    }
    
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : Azure Keypair 목록 조회
     * @title : getAzureKeypairList
     * @return : List<AzurePublicIpMgntVO>
     ***************************************************/
    public List<AzureKeypairMgntVO> getAzureKeypairList(int accountId) {
        
        List<String> results = new ArrayList<String>();
        results = getKeypairFileList("azure");
        List<AzureKeypairMgntVO> list = new ArrayList<AzureKeypairMgntVO>();
        for (int i=0; i< results.size(); i++){
            String result = results.get(i);
            AzureKeypairMgntVO azureVo = new AzureKeypairMgntVO();
            azureVo.setKeypairName(result);
            azureVo.setAccountId(accountId);
            azureVo.setRecid(i);
            list.add(azureVo);
        }
        return list;
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure Public IP 할당
     * @title : createPublicIp
     * @return : void
     ***************************************************/
    public void createKeypair(AzureKeypairMgntDTO dto) {
        File publicKeyFile = new File(SSH_DIR+"/"+dto.getKeypairName()+".pub");
        if(publicKeyFile.exists()){
        	publicKeyFile.delete();
        }
        File privateKeyFile = new File(SSH_DIR+"/"+dto.getKeypairName());
        if(privateKeyFile.exists()){
        	privateKeyFile.delete();
        }
        try {
            List<String> cmd = new ArrayList<String>();
            cmd.add("ssh-keygen");
            cmd.add("-t");
            cmd.add("rsa");
            cmd.add("-b");
            cmd.add("2048");
            cmd.add("-f");
            cmd.add(SSH_DIR+"/"+dto.getKeypairName());
            cmd.add("-q");
            cmd.add("-P");
            cmd.add("");
            cmd.add("-C");
            cmd.add("");
            
            ProcessBuilder builder = new ProcessBuilder(cmd);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            InputStream inputStream = process.getInputStream();
            String info = null;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            while ((info = bufferedReader.readLine()) != null){
                System.out.println(info);
            }
            
        } catch (IOException e) {
            throw new CommonException("Ioexception", "Key Pair 생성 중 에러가 발생했습니다.", HttpStatus.BAD_REQUEST);
        }
    }
    
    /****************************************************************
     * @project :인프라 관리 대시보드
     * @description : 로컬에서 Public/Private Key 파일(.pem)  정보 목록 조회
     * @title : getKeypairFileList
     * @return : List<String>
    *****************************************************************/
    public List<String> getKeypairFileList(String iaasType){
        File keyPathFile = new File(SSH_DIR);
        if ( !keyPathFile.isDirectory() ) {
            return null;
        }
        List<String> localFiles = null;
        File[] listFiles = keyPathFile.listFiles();
        if(listFiles != null){
            for (File file : listFiles) {
                if ( localFiles == null ){
                    localFiles = new ArrayList<String>();
                }
                
                localFiles.add(file.getName());
            }
        }
        return localFiles;
    }
    
}
