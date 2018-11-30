package org.openpaas.ieda.iaasDashboard.openstackMgnt.web.keypairs.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.api.keypairs.OpenstackKeypairsMgntApiService;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.keypairs.dto.OpenstackKeypairsMgntDTO;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.keypairs.service.OpenstackKeypairsMgntService;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.openstack4j.model.compute.Keypair;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class OpenstackKeypairsMgntServiceUnitTest {
    private Principal principal = null;
    
    @InjectMocks OpenstackKeypairsMgntService mockOpenstackKeypairsMgntService;
    @Mock CommonIaasService mockCommonIaasService;
    @Mock MessageSource mockMessageSource;
    @Mock OpenstackKeypairsMgntApiService mockOpenstackKeypairsMgntApiService;
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        principal = getLoggined();
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 시큐리티 토큰 생성
    * @title : getLoggined
    * @return : Principal
    ***************************************************/
    public Principal getLoggined() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken("admin", "admin");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(auth);
        securityContext.getAuthentication().getPrincipal();
        return auth;
    }
    
    /***************************************************
     * @project : OPENSTACK 관리 대시보드
     * @description : OPENSTACK Keypairs 목록 조회  TEST
     * @title : testGetOpenstackKeypairsInfoList
     * @return : void
     ***************************************************/
     @Test
     public void testGetOpenstackKeypairsInfoList(){
         getOpenstackAccountInfo();
         List<? extends Keypair> expectList = setResultOpenstackKeypairsList();
         doReturn(expectList).when(mockOpenstackKeypairsMgntApiService).getOpenstackKeypairsInfoListApiFromOpenstack(any());
         List<HashMap<String, Object>> resultList = mockOpenstackKeypairsMgntService.getOpenstackKeypairsInfoList(principal, 1);
         assertEquals(resultList.get(0).get("keypairsName"), expectList.get(0).getName());
         assertEquals(resultList.get(0).get("fingerprint"), expectList.get(0).getFingerprint());
     }
     
     
     /***************************************************
      * @project : OPENSTACK 관리 대시보드
      * @description : OPENSTACK Keypairs 생성 TEST
      * @title : testSaveOpenstackKeypairsInfo
      * @return : void
      ***************************************************/
      @Test
      public void testSaveOpenstackKeypairsInfo(){
          getOpenstackAccountInfo();
          HttpServletResponse req = new MockHttpServletResponse();
          when(mockOpenstackKeypairsMgntApiService.saveOpenstackKeypairsInfoApiFromOpenstack(any(), anyString())).thenReturn("paas-ta");
          mockOpenstackKeypairsMgntService.saveOpenstackKeypairsInfo("1", 1, principal, req);
      }

      /***************************************************
       * @project : OPENSTACK 관리 대시보드
       * @description : OPENSTACK Keypairs 목록 조회 결과 값 설정
       * @title : setResultOpenstackKeypairsList
       * @return : List<Keypair>
       ***************************************************/
      public List<Keypair> setResultOpenstackKeypairsList() {
          List<Keypair> keypairs = new ArrayList<Keypair>();
          Keypair keypair = new Keypair() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getPublicKey() {
                return null;
            }
            
            @Override
            public String getPrivateKey() {
                return null;
            }
            
            @Override
            public String getName() {
                return "keypairNameTest";
            }
            
            @Override
            public String getFingerprint() {
                return "fingerprintTest";
            }

            @Override
            public Date getCreatedAt() {
                return null;
            }

            @Override
            public Boolean getDeleted() {
                return null;
            }
            @Override
            public Date getDeletedAt() {
                return null;
            }

            @Override
            public Integer getId() {
                return null;
            }

            @Override
            public Date getUpdatedAt() {
                return null;
            }

            @Override
            public String getUserId() {
                return null;
            }
        };
        keypairs.add(keypair);
        return keypairs;
      }
      
      /***************************************************
       * @project : OPENSTACK 관리 대시보드
       * @description : OPENSTACK Keypairs 생성 시 입력 정보 설정 
       * @title : setOpenstackKeypairsInfo
       * @return : OpenstackKeypairsMgntDTO
       ***************************************************/
      public OpenstackKeypairsMgntDTO setOpenstackKeypairsInfo() {
          OpenstackKeypairsMgntDTO dto = new OpenstackKeypairsMgntDTO();
          dto.setAccountId(1);
          dto.setKeypairsName("keypairsName");
          return dto;
      }

    /***************************************************
      * @project : OPENSTACK 관리 대시보드
      * @description : OPENSTACK Account 조회 정보 결과 값 설정
      * @title : getAwsAccountInfo
      * @return : IaasAccountMgntVO
      ***************************************************/
      public IaasAccountMgntVO getOpenstackAccountInfo() {
          IaasAccountMgntVO vo = new IaasAccountMgntVO();
          vo.setAccountName("testAccountName");
          vo.setCreateUserId("admin");
          vo.setIaasType("openstack");
          vo.setCommonProject("bosh");
          vo.setCommonAccessSecret("commonSecret");
          vo.setCommonAccessUser("commonUser");
          when(mockCommonIaasService.getIaaSAccountInfo(any(), anyInt(), anyString())).thenReturn(vo);
          return vo;
      }
}