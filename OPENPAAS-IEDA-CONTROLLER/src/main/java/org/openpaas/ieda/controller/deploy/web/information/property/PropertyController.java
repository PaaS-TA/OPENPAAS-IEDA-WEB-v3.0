package org.openpaas.ieda.controller.deploy.web.information.property;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.openpaas.ieda.controller.common.BaseController;
import org.openpaas.ieda.deploy.web.information.property.PropertyVO;
import org.openpaas.ieda.deploy.web.information.property.dto.PropertyDTO;
import org.openpaas.ieda.deploy.web.information.property.service.PropertyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PropertyController extends BaseController{
    
    @Autowired PropertyService propertyService;
    final private static Logger LOGGER = LoggerFactory.getLogger(PropertyController.class);
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Property 화면 이동
     * @title : goProperty
     * @return : String
    ***************************************************/
    @RequestMapping(value="/info/property", method=RequestMethod.GET)
    public String goProperty(){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Property 화면 요청"); }
        return "/deploy/information/listProperty";
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Property 목록 정보 조회
     * @title : getPropertyList
     * @return : ResponseEntity<Map<String,Object>>
    ***************************************************/
    @RequestMapping(value="/info/property/list/{deployment}", method=RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getPropertyList(@PathVariable String deployment){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Property 조회 요청"); }
        List<PropertyDTO> propertyList = propertyService.getPropertyList(deployment);
        HashMap<String, Object> result = new HashMap<String, Object>();
        int size =0;
        if( propertyList != null ){
            size = propertyList.size();
        }
        result.put("records", propertyList);
        result.put("total", size);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Property 상세 정보 조회
     * @title : getPropertyDetailInfo
     * @return : ResponseEntity<PropertyVO>
    ***************************************************/
    @RequestMapping(value="/info/property/list/detailInfo/{deployment}/{name}", method=RequestMethod.GET)
    public ResponseEntity<PropertyVO>  getPropertyDetailInfo(@PathVariable String deployment, @PathVariable String name){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Property 상세 조회 요청"); }
        PropertyVO property = propertyService.getPropertyDetailInfo(deployment, name);
        return new ResponseEntity<PropertyVO>(property, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Property 정보 생성
     * @title : createPropertyInfo
     * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/info/property/modify/createProperty", method=RequestMethod.POST)
    public ResponseEntity<?> createPropertyInfo(@RequestBody @Valid PropertyDTO dto){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Property 생성 요청"); }
        propertyService.createProperyInfo(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Property 정보 수정
     * @title : updatePropertyInfo
     * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/info/property/modify/updateProperty", method=RequestMethod.PUT)
    public ResponseEntity<?> updatePropertyInfo(@RequestBody @Valid PropertyDTO dto){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Property 수정 요청"); }
        propertyService.updateProperyInfo(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Property 삭제
     * @title : deletePropertyInfo
     * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/info/property/modify/deleteProperty", method=RequestMethod.DELETE)
    public ResponseEntity<?> deletePropertyInfo(@RequestBody @Valid PropertyDTO dto){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Property 삭제 요청"); }
        propertyService.deleteProperyInfo(dto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
