package org.openpaas.ieda.controller.iaasMgnt.azureMgnt.web.publicIp;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.controller.iaasMgnt.azureMgnt.web.storageAccount.AzureStorageAccountMgntController;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.publicIp.dao.AzurePublicIpMgntVO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.publicIp.dto.AzurePublicIpMgntDTO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.publicIp.service.AzurePublicIpMgntService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class AzurePublicIpMgntController {
	@Autowired 
	private AzurePublicIpMgntService azureAzurePublicIpMgntService;
	
	@Autowired
    private MessageSource message;
    private final static Logger LOG = LoggerFactory.getLogger(AzureStorageAccountMgntController.class);
    /***************************************************
    * @project : AZURE 인프라 관리 대시보드
    * @description : AZURE Storage Account 관리 화면 이동
    * @title : goAzureStorageAccountMgnt
    * @return : String
    ***************************************************/
    @RequestMapping(value="/azureMgnt/publicIp", method=RequestMethod.GET)
    public String goAzurePublicIpMgnt(){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> AZURE Public IP 화면 이동");
        }
        return "iaas/azure/publicIp/azurePublicIpMgnt";
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE Public IP 목록 조회
     * @title : getAzurePublicIpInfoList
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/publicIp/list/{accountId}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getAzurePublicIpInfoList(Principal principal, @PathVariable("accountId") int accountId){
        List<AzurePublicIpMgntVO> list = azureAzurePublicIpMgntService.getAzurePublicIpInfoList(principal,accountId);
        HashMap<String, Object> map = new HashMap<String, Object>();
        if(list != null && list.size() != 0){
            map.put("total", list.size());
            map.put("records", list);
        }
        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE Public IP 할당
     * @title : saveAzurePublicIpInfo
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/publicIp/save", method=RequestMethod.POST)
    public ResponseEntity<?> saveAzurePublicIpInfo(@RequestBody AzurePublicIpMgntDTO dto, Principal principal){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> Azure Public IP 할당");
     }
     try {
    	 azureAzurePublicIpMgntService.createPublicIp(dto, principal);
    } catch (Exception e) {
        String detailMessage = e.getMessage();
        if(!detailMessage.equals("") && detailMessage != "null"){
            throw new CommonException(
              detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
        }else{
            throw new CommonException(
                    message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        
    }
     return new ResponseEntity<>(HttpStatus.CREATED);
 } 
    
}
