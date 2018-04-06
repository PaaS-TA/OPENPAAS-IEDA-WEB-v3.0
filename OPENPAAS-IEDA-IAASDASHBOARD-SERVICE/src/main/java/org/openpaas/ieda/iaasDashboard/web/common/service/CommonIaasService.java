package org.openpaas.ieda.iaasDashboard.web.common.service;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.iaasDashboard.api.account.IaasAccountMgntApiService;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntDAO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.account.dto.IaasAccountMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.common.dao.CommonCodeVO;
import org.openpaas.ieda.iaasDashboard.web.common.dao.CommonIaasDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
@Service
public class CommonIaasService {
    
    @Autowired MessageSource message;
    @Autowired CommonIaasDAO commonIaasDAO;
    @Autowired IaasAccountMgntApiService api;
    @Autowired IaasAccountMgntDAO iaasAccountDao;
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : RSA PublicKey 생성
     * @title : getPublicKey
     * @return : HashMap<String,Object>
    ***************************************************/
    public HashMap<String, Object> getPublicKey(HttpServletRequest request){
        HashMap<String, Object> map = new HashMap<String, Object>();
        KeyPairGenerator generator;
        try {
            generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            KeyPair keyPair = generator.genKeyPair();
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            HttpSession session = request.getSession();
            
            // 세션에 공개키의 문자열을 키로하여 개인키를 저장한다.
            session.setAttribute("__rsaPrivateKey__", privateKey);
            RSAPublicKeySpec publicSpec = (RSAPublicKeySpec) keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
            
            String publicKeyModulus = publicSpec.getModulus().toString(16);
            String publicKeyExponent = publicSpec.getPublicExponent().toString(16);
            map.put("publicKeyModulus", publicKeyModulus);
            map.put("publicKeyExponent", publicKeyExponent);
        } catch (NoSuchAlgorithmException e) {
            throw new CommonException(message.getMessage("common.rsa.privateKey.exception.code", null, Locale.KOREA), 
                    message.getMessage("common.rsa.publicKey.exception.message",null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        } catch (InvalidKeySpecException e) {
            throw new CommonException(message.getMessage("common.rsa.privateKey.exception.code", null, Locale.KOREA), 
                    message.getMessage("common.rsa.publicKey.exception.message",null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        return map;
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Rsa 복호화
     * @title : decryptRsa
     * @return : String
    ***************************************************/
    public String decryptRsa(PrivateKey privateKey, String securedValue){
        Cipher cipher = null;
        String decryptedValue =""; 
        try {
            cipher = Cipher.getInstance("RSA");
            byte[] encryptedBytes = hexToByteArray(securedValue);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = null;
            if( encryptedBytes.length > 0 ){
                decryptedBytes = cipher.doFinal(encryptedBytes);
            }else{
                decryptedBytes = cipher.doFinal(Base64.decodeBase64(securedValue));
            }
            decryptedValue = new String(decryptedBytes, "utf-8"); // 문자 인코딩 주의.
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new CommonException(message.getMessage("common.rsa.privateKey.exception.code", null, Locale.KOREA), 
                    message.getMessage("common.rsa.privateKey.exception.message",null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
            throw new CommonException(message.getMessage("common.rsa.privateKey.exception.code", null, Locale.KOREA), 
                    message.getMessage("common.rsa.privateKey.exception.message",null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
       
        return decryptedValue;
    }
    

    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 16진 문자열을 byte 배열로 변환한다.
     * @title : hexToByteArray
     * @return : byte[]
    ***************************************************/
    public static byte[] hexToByteArray(String hex) {
        if (hex == null || hex.length() % 2 != 0) {
            return new byte[]{};
        }
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            byte value = (byte)Integer.parseInt(hex.substring(i, i + 2), 16);
            bytes[(int) Math.floor(i / 2)] = value;
        }
        return bytes;
    }
    
    /***************************************************
    * @project : 인프라 관리 대시보드
    * @description : 기본 인프라 계정을 조회 하고 설정 한다.
    * @title : setDefaultIaasAccountInfo
    * @return : void
    ***************************************************/
    public void setDefaultIaasAccountInfo(IaasAccountMgntDTO dto, Principal principal) {
        commonInfraAccountExistCheck(dto, principal);
        HashMap<String, String> map = commonIaasDAO.selectdefaultIaasAccountInfo("Y", dto.getIaasType());
        IaasAccountMgntVO vo = null;
        if(map!=null){
            vo = new IaasAccountMgntVO();
            String id = String.valueOf(map.get("id"));
            if(!dto.getId().equalsIgnoreCase(id)){
                vo.setId(Integer.parseInt(id));
                vo.setDefaultYn("N");
                vo.setIaasType(dto.getIaasType());
                commonIaasDAO.updateDefaultIaasAccountInfo(vo);
            }
        }
        vo = new IaasAccountMgntVO();
        vo.setId(Integer.parseInt(dto.getId()));
        vo.setDefaultYn("Y");
        vo.setIaasType(dto.getIaasType());
        commonIaasDAO.updateDefaultIaasAccountInfo(vo);
    }
    
    /***************************************************
    * @project : 인프라 관리 대시보드
    * @description : 공통 실제 Infra 계정 존재 유무 확인
    * @title : commonInfraAccountExistCheck
    * @return : void
    ***************************************************/
    public IaasAccountMgntVO commonInfraAccountExistCheck(IaasAccountMgntDTO dto, Principal principal){
        
        IaasAccountMgntVO vo = iaasAccountDao.selectIaasAccountInfo(principal.getName(), dto.getIaasType(), Integer.parseInt(dto.getId()));
        
        String commonAccessUser = vo.getCommonAccessUser();
        String commonAccessSecret = vo.getCommonAccessSecret();
        
      //API를 통해 계정 존재 확인
        boolean flag = false;
        if( vo.getIaasType().trim().equalsIgnoreCase("AWS") ){
            flag = api.getAccountInfoFromAWS(commonAccessUser, commonAccessSecret );
        } else  if( vo.getIaasType().trim().equalsIgnoreCase("OPENSTACK") ){
            if( vo.getOpenstackKeystoneVersion().equalsIgnoreCase("v2") ){
                flag = api.getAccountInfoFromOpenstackV2(vo.getCommonAccessEndpoint(), vo.getCommonTenant(), commonAccessUser, commonAccessSecret);
            }else{
                flag = api.getAccountInfoFromOpenstackV3(vo.getCommonAccessEndpoint(), vo.getOpenstackDomain(), vo.getCommonProject(),
                        commonAccessUser, commonAccessSecret);
            }
        } else  if( vo.getIaasType().toUpperCase().trim().equals("AZURE") ){
            flag = api.getAccountInfoFromAzure(commonAccessUser, vo.getCommonTenant(), commonAccessSecret, vo.getAzureSubscriptionId());
        } else  if( vo.getIaasType().trim().equalsIgnoreCase("VSPHERE") ){
            flag = api.getAccountInfoFromVsphere(vo.getCommonAccessEndpoint(), commonAccessUser, commonAccessSecret);
        }
       
        if(! flag ){
            throw new CommonException(
                    message.getMessage("iaas.accountManagement.connect.code.exception", null, Locale.KOREA), 
                    message.getMessage(vo.getIaasType().toLowerCase()+".accountManagement.connect.message.exception", null, Locale.KOREA)+ "<br/>(계정 :"+commonAccessUser+")", HttpStatus.BAD_REQUEST);
        }else{
            return vo;
        }
    }
    
    /***************************************************
    * @project : 인프라 관리 대시보드
    * @description : 공통 인프라 계정 정보 조회
    * @title : getIaaSAccountInfo
    * @return : IaasAccountMgntVO
    ***************************************************/
    public IaasAccountMgntVO getIaaSAccountInfo(Principal principal, int accountId, String iaasType) {
        IaasAccountMgntDTO dto = new IaasAccountMgntDTO();
        dto.setId(String.valueOf(accountId));
        dto.setIaasType(iaasType);
        IaasAccountMgntVO vo = commonInfraAccountExistCheck(dto, principal);
        return vo;
    }
    
    /***************************************************
    * @project : 인프라 관리 대시보드
    * @description : AWS 공통 리전 명 정보 조회
    * @title : getAwsRegionInfo
    * @return : Region
    ***************************************************/
    public Region getAwsRegionInfo(String regionName) {
        return Region.getRegion(Regions.fromName(regionName));
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure 리전 명 정보 조회
     * @title : getAzureLocationInfo
     * @return : Location
     ***************************************************/
    public String getAzureLocationInfo(String location){
    	com.microsoft.azure.management.resources.fluentcore.arm.Region theRegion = 
    			com.microsoft.azure.management.resources.fluentcore.arm.Region.fromName(location);
    	String regionName = theRegion.name().toString();
    	 String rglocation = com.microsoft.azure.management.resources.fluentcore.arm.Region.findByLabelOrName(regionName).name();
    	return  rglocation;
    }
    
    
   
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 서브그룹 정보 목록을 조회 
     * @title : getSubGroupCodeList
     * @return : List<CommonIaasVO>
    ***************************************************/
    public List<CommonCodeVO> getSubGroupCodeList(String parentCode) {
        List<CommonCodeVO> list = commonIaasDAO.selectParentCodeAndSubGroupCode(parentCode);
        if ( list.size() == 0 ) {
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), 
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        return list;
    }
    
    
}
