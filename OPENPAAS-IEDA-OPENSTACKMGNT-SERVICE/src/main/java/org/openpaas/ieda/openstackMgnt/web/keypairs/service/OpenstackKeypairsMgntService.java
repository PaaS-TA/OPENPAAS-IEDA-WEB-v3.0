package org.openpaas.ieda.openstackMgnt.web.keypairs.service;
 
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.openpaas.ieda.openstackMgnt.api.keypairs.OpenstackKeypairsMgntApiService;
import org.openstack4j.model.compute.Keypair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
 
@Service
public class OpenstackKeypairsMgntService {
     
    @Autowired CommonIaasService commonIaasService;
    @Autowired MessageSource message;
    @Autowired OpenstackKeypairsMgntApiService openstackKeypairsMgntApiService; 
     
    public List<HashMap<String,Object>> getOpenstackKeypairsInfoList(Principal principal, int accountId){
        IaasAccountMgntVO vo =  getOpenstackAccountInfo(principal, accountId);
        List<? extends Keypair> keypairsList = openstackKeypairsMgntApiService.getOpenstackKeypairsInfoListApiFromOpenstack(vo);
        List<HashMap<String,Object>> maps = new ArrayList<HashMap<String,Object>>();
        for(int i=0; i<keypairsList.size(); i++){
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("keypairsName", keypairsList.get(i).getName());
            map.put("fingerprint", keypairsList.get(i).getFingerprint());
            maps.add(map);
        }
        return maps;
    }
     
    /***************************************************
     * @project : OPENSTACK 인프라 관리 대시보드
     * @description : OPENSTACK Keypairs 할당
     * @title : saveOpenstackKeypairsInfo
     * @return : IaasAccountMgntVO
     ***************************************************/
     public void saveOpenstackKeypairsInfo(String keyFileName, int accountId, Principal principal,
             HttpServletResponse response) {
          
         IaasAccountMgntVO vo =  getOpenstackAccountInfo(principal, accountId);
         try{
             String openstackKeyFile = openstackKeypairsMgntApiService.saveOpenstackKeypairsInfoApiFromOpenstack(vo, keyFileName);
             openstackKeyFile = openstackKeyFile.replace("\n", "\r\n");
             response.setContentType("application/octet-stream");
             response.setHeader("Content-Disposition", "attachment; filename=" + keyFileName+".pem");
             IOUtils.write(openstackKeyFile, response.getOutputStream(), "UTF-8");
         }catch (Exception e) {
             throw new CommonException(
                     message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
         }
     }
     
    /***************************************************
     * @project : OPENSTACK 인프라 관리 대시보드
     * @description : OPENSTACK 계정 정보가 실제 존재 하는지 확인 및 상세 조회
     * @title : getOpenstackAccountInfo 
     * @return : IaasAccountMgntVO
     ***************************************************/
     public IaasAccountMgntVO getOpenstackAccountInfo(Principal principal, int accountId){
         return commonIaasService.getIaaSAccountInfo(principal, accountId, "openstack");
     }
}