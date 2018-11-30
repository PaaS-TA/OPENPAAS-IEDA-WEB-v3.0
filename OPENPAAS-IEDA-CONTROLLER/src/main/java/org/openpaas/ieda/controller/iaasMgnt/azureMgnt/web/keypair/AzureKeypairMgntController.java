package org.openpaas.ieda.controller.iaasMgnt.azureMgnt.web.keypair;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.keypair.dao.AzureKeypairMgntVO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.keypair.dto.AzureKeypairMgntDTO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.keypair.service.AzureKeypairMgntService;
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
public class AzureKeypairMgntController {
    @Autowired 
    private AzureKeypairMgntService azureAzureKeypairMgntService;
    @Autowired
    private MessageSource message;
    private final static Logger LOG = LoggerFactory.getLogger(AzureKeypairMgntController.class);
    /***************************************************
    * @project : AZURE 인프라 관리 대시보드
    * @description : AZURE Keypair 관리 화면 이동
    * @title : goAzureKeypairMgnt
    * @return : String
    ***************************************************/
    @RequestMapping(value="/azureMgnt/storageAccessKey", method=RequestMethod.GET)
    public String goAzureKeypairMgnt(){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> AZURE Keypair 관리 화면 이동");
        }
        return "iaas/azure/keypair/azureKeypairMgnt";
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE Keypair 목록 조회
     * @title : getAzureKeypairInfoList
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/storageAccessKey/list/{accountId}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getAzureKeypairInfoList(@PathVariable("accountId") int accountId){
        List<AzureKeypairMgntVO> list = azureAzureKeypairMgntService.getAzureKeypairList(accountId);
        HashMap<String, Object> map = new HashMap<String, Object>();
        if(list != null && list.size() != 0){
            map.put("total", list.size());
            map.put("records", list);
        }
        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE Keypair 생성 
     * @title : createAzureKeypair
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/storageAccessKey/save", method=RequestMethod.POST)
    public ResponseEntity<?> createAzureKeypair(@RequestBody AzureKeypairMgntDTO dto){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> Azure Keypair 생성");
     }
     try {
         azureAzureKeypairMgntService.createKeypair(dto);
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
