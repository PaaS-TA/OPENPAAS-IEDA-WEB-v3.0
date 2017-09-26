package org.openpaas.ieda.controller.iaasMgnt.dashboard.web.main;

import org.openpaas.ieda.common.web.security.SessionInfoDTO;
import org.openpaas.ieda.iaasDashboard.web.account.service.IaasAccountMgntService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IaasMainController {
    @Autowired IaasAccountMgntService service;
	
	/***************************************************
	 * @project          : 인프라 관리 대시보드
	 * @description   : 메인 화면을 호출하 이동
	 * @title               : goLayout
	 * @return            : String
	***************************************************/
	@RequestMapping(value="/iaasMgnt", method=RequestMethod.GET)
	public String goLayout(){ 
		return "/iaas/main/layout";
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 메인의 top 화면 호출
	 * @title               : goTop
	 * @return            : String
	***************************************************/
	@RequestMapping(value="/iaasMgnt/top", method=RequestMethod.GET)
	public String goTop(ModelMap model) {
	    SessionInfoDTO session = new SessionInfoDTO();
	    model.addAttribute("userId", session.getUserId());
	    
		return "/iaas/main/top";
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 메인의 menu 화면 호출 
	 * @title               : goMenu
	 * @return            : String
	***************************************************/
	@RequestMapping(value="/iaasMgnt/menu", method=RequestMethod.GET)
	public String goMenu() {
		return "/iaas/main/menu";
	}
}
