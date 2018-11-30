package org.openpaas.ieda.iaasDashboard.azureMgnt.web.storageAccount.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Application;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.common.base.BaseAzureMgntControllerUnitTest;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.storageAccount.dao.AzureStorageAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.storageAccount.dto.AzureStorageAccountMgntDTO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.storageAccount.service.AzureStorageAccountMgntApiService;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.storageAccount.service.AzureStorageAccountMgntService;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.storage.AccessTier;
import com.microsoft.azure.management.storage.CustomDomain;
import com.microsoft.azure.management.storage.Encryption;
import com.microsoft.azure.management.storage.Kind;
import com.microsoft.azure.management.storage.ProvisioningState;
import com.microsoft.azure.management.storage.PublicEndpoints;
import com.microsoft.azure.management.storage.Sku;
import com.microsoft.azure.management.storage.StorageAccount;
import com.microsoft.azure.management.storage.StorageAccountEncryptionKeySource;
import com.microsoft.azure.management.storage.StorageAccountEncryptionStatus;
import com.microsoft.azure.management.storage.StorageAccountKey;
import com.microsoft.azure.management.storage.StorageService;
import com.microsoft.azure.management.storage.implementation.AccountStatuses;
import com.microsoft.azure.management.storage.implementation.StorageAccountInner;
import com.microsoft.azure.management.storage.implementation.StorageManager;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.rest.ServiceCallback;
import com.microsoft.rest.ServiceFuture;

import rx.Observable;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class AzureStorageAccountMgntServiceUnitTest extends BaseAzureMgntControllerUnitTest {
    
    private Principal principal = null;
    
    @InjectMocks AzureStorageAccountMgntService mockAzureStorageAccountMgntService;
    @Mock AzureStorageAccountMgntApiService mockAzureStorageAccountMgntApiService;
    @Mock CommonIaasService mockCommonIaasService;
    @Mock MessageSource mockMessageSource;
    /***************************************************
     * @project : Azure 관리 대시보드
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
     * @project : Azure 관리 대시보드
     * @description : Azure StorageAccount 목록 조회 TEST
     * @title : testGetAzureStorageAccountInfoList
     * @return : void
     ***************************************************/
    @Test
    public void testGetAzureStorageAccountInfoList(){
        IaasAccountMgntVO vo = getAzureAccountInfo();
        String subscriptionId = vo.getAzureSubscriptionId();
       // String subscriptionName = getAzureSubscriptionName();
        String subscriptionName = null;
        List<StorageAccount> storageAccountList = getResultStorageAccountListInfo();
        when(mockAzureStorageAccountMgntApiService.getAzureStorageAccountInfoListFromAzure(any())).thenReturn(storageAccountList);
        List<AzureStorageAccountMgntVO> resultList = mockAzureStorageAccountMgntService.getAzureStorageAccountInfoList(principal, 1);
        assertEquals(resultList.size(), 1);
        assertEquals(resultList.get(0).getStorageAccountName(), storageAccountList.get(0).name());
        assertEquals(resultList.get(0).getAzureSubscriptionId(), subscriptionId);
        assertEquals(resultList.get(0).getSubscriptionName(), subscriptionName);
        assertEquals(resultList.get(0).getLocation(), storageAccountList.get(0).regionName());
        assertEquals(resultList.get(0).getResourceGroupName(), storageAccountList.get(0).resourceGroupName());
    }
    
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : 해당 정보 목록 값 설정  
     * @title : getResultStorageAccountListInfo
     * @return : List<StorageAccount>
     ***************************************************/
    public List<StorageAccount> getResultStorageAccountListInfo(){
        List<StorageAccount> storageAccountList = new ArrayList<StorageAccount>();
        StorageAccount storageAccount = new StorageAccount(){

            @Override
            public String type() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String regionName() {
                // TODO Auto-generated method stub
                return "test-location";
            }

            @Override
            public Region region() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Map<String, String> tags() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String key() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String id() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String name() {
                // TODO Auto-generated method stub
                return "test-storageAccountName";
            }

            @Override
            public String resourceGroupName() {
                // TODO Auto-generated method stub
                return "test-resourceGroupName";
            }

            @Override
            public StorageManager manager() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public StorageAccountInner inner() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public StorageAccount refresh() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Observable<StorageAccount> refreshAsync() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Update update() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public AccessTier accessTier() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public AccountStatuses accountStatuses() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public DateTime creationTime() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public CustomDomain customDomain() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Encryption encryption() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public StorageAccountEncryptionKeySource encryptionKeySource() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Map<StorageService, StorageAccountEncryptionStatus> encryptionStatuses() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public PublicEndpoints endPoints() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public List<StorageAccountKey> getKeys() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Observable<List<StorageAccountKey>> getKeysAsync() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public ServiceFuture<List<StorageAccountKey>> getKeysAsync(ServiceCallback<List<StorageAccountKey>> arg0) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Kind kind() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public DateTime lastGeoFailoverTime() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public ProvisioningState provisioningState() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public List<StorageAccountKey> regenerateKey(String arg0) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Observable<List<StorageAccountKey>> regenerateKeyAsync(String arg0) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public ServiceFuture<List<StorageAccountKey>> regenerateKeyAsync(String arg0,
                    ServiceCallback<List<StorageAccountKey>> arg1) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Sku sku() {
                // TODO Auto-generated method stub
                return null;
            }
            
        };
        storageAccountList.add(storageAccount);
        return storageAccountList;
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure Storage Account 생성 TEST
     * @title : testSaveStorageAccountInfo
     * @return : void
     ***************************************************/
    @Test
    public void testSaveStorageAccountInfo(){
        getAzureAccountInfo();
        AzureStorageAccountMgntDTO dto = setAzureStorageAccountInfo();
        mockAzureStorageAccountMgntService.saveStorageAccountInfo(dto, principal);
    }
    
    /***************************************************
    * @project : 인프라 관리 대시보드
    * @description : Azure StorageAccount 정보 설정
    * @title : setAzureStorageAccountInfo
    * @return : AzureStorageAccountMgntDTO
    ***************************************************/
    public AzureStorageAccountMgntDTO setAzureStorageAccountInfo() {
    	AzureStorageAccountMgntDTO dto = new AzureStorageAccountMgntDTO();
        dto.setAccountId(1);
        dto.setStorageAccountName("test-storageAccountName");
        dto.setLocation("test-koreaSouth");
        dto.setResourceGroupName("test-resourceGroupName");
        return dto;
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure Storage Account 삭제 TEST
     * @title : testDeleteStorageAccountInfo
     * @return : void
     ***************************************************/
    @Test
    public void testDeleteStorageAccountInfo(){
        getAzureAccountInfo();
        AzureStorageAccountMgntDTO dto = setAzureStorageAccountInfo();
        mockAzureStorageAccountMgntService.deleteStorageAccountInfo(dto, principal);
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure Storage Account Key 조회 TEST
     * @title : testGetStorageAccountKey
     * @return : void
     ***************************************************/ 
/*    @Test 
    public void testGetStorageAccountKey(){
        getAzureAccountInfo();
        AzureStorageAccountMgntDTO dto = setAzureStorageAccountInfo();
        String storageAccountName = dto.getStorageAccountName();
        String result = mockAzureStorageAccountMgntService.getStorageAccountKey(principal, 1, storageAccountName);
        assertEquals(result, "/wPl3EPz9Z1HBouV1ndFo+LpH5HQ6WcI3vZaQjCc8Sjd0QOSJey5Qz4cIrbQOg42cCEzIr7eMNDN8QYuBi1x1w==");
    }*/
    
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : Azure StorageAccount Blob 목록 조회 TEST
     * @title : testGetAzureSBlobInfoList
     * @return : void
     ***************************************************/
 /*   @Test
    public void testGetAzureSBlobInfoList(){
        getAzureAccountInfo();
        AzureStorageAccountMgntDTO dto = setAzureStorageAccountInfo();
        String storageAccountName = dto.getStorageAccountName();
        List<StorageAccount> storageAccountList = getResultStorageAccountListInfo();
        List<CloudBlobContainer> blobList = getResultBlobListInfo();
        List<AzureStorageAccountMgntVO> resultList;
		try {
			resultList = mockAzureStorageAccountMgntService.getAzureBlobInfoList(principal, 1, storageAccountName);
			assertEquals(resultList.size(), 1);
			assertEquals(resultList.get(0).getStorageAccountName(), storageAccountList.get(0).name());
			assertEquals(resultList.get(0).getBlobName(), blobList.get(0).getName());
			assertEquals(resultList.get(0).getPublicAccessLevel().toString(), blobList.get(0).getProperties().getPublicAccess().toString());
			assertEquals(resultList.get(0).getLeaseState().toString(), blobList.get(0).getProperties().getLeaseState().toString());
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    */
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : 해당 정보 목록 값 설정  
     * @title : getResultBlobListInfo
     * @return : List<CloudBlobContainer>
     ***************************************************/
/*    public List<CloudBlobContainer> getResultBlobListInfo(){
		List<CloudBlobContainer> blobList = new ArrayList<CloudBlobContainer>();
        return blobList;    
    }*/
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure Storage Account Blob 생성 TEST
     * @title : testCreateAzureBlob
     * @return : void
     ***************************************************/
/*    @Test
    public void testCreateAzureBlob(){
        getAzureAccountInfo();
        AzureStorageAccountMgntDTO dto = setAzureBlobInfo();
        mockAzureStorageAccountMgntService.createAzureBlob(dto, principal);
    }*/
    
    /***************************************************
    * @project : 인프라 관리 대시보드
    * @description : Azure StorageAccount Blob 정보 설정
    * @title : setAzureBlobInfo
    * @return : AzureStorageAccountMgntDTO
    ***************************************************/
/*    public AzureStorageAccountMgntDTO setAzureBlobInfo() {
    	AzureStorageAccountMgntDTO dto = new AzureStorageAccountMgntDTO();
        dto.setAccountId(1);
        dto.setStorageAccountName("testaaabbb");
        dto.setBlobName("test-blobName");
        dto.setPublicAccessType("test-publicAccessType-private");
        return dto;
    }*/
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure Storage Account Blob 삭제 TEST
     * @title : testDeleteAzureBlob
     * @return : void
     ***************************************************/
/*    @Test
    public void testDeleteAzureBlob(){
        getAzureAccountInfo();
        AzureStorageAccountMgntDTO dto = setAzureStorageAccountInfo();
        mockAzureStorageAccountMgntService.deleteAzureBlob(dto, principal);
    }*/
    
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure Storage Account Test 생성 TEST
     * @title : testCreateAzureTable
     * @return : void
     * @throws StorageException 
     ***************************************************/
/*    @Test
    public void testCreateAzureTable() throws StorageException{
        getAzureAccountInfo();
        AzureStorageAccountMgntDTO dto = setAzureTableInfo();
        mockAzureStorageAccountMgntService.createAzureTable(dto, principal);
    }*/
    /***************************************************
    * @project : 인프라 관리 대시보드
    * @description : Azure StorageAccount Table 정보 설정
    * @title : setAzureTableInfo
    * @return : AzureStorageAccountMgntDTO
    ***************************************************/
/*    public AzureStorageAccountMgntDTO setAzureTableInfo() {
    	AzureStorageAccountMgntDTO dto = new AzureStorageAccountMgntDTO();
        dto.setAccountId(1);
        dto.setStorageAccountName("storageaccnametest");
        dto.setTableName("test-blobName");
        return dto;
    }*/
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure Storage Account Blob 삭제 TEST
     * @title : testDeleteAzureBlob
     * @return : void
     * @throws StorageException 
     ***************************************************/
    @Test
/*    public void testDeleteAzureTable() throws StorageException{
        getAzureAccountInfo();
        AzureStorageAccountMgntDTO dto = setAzureStorageAccountInfo();
			mockAzureStorageAccountMgntService.deleteAzureTable(dto, principal);
    }
    */
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure 구독 명 조회
     * @title : getAzureSubNameInfo
     * @return : String
     ***************************************************/
    public void getAzureSubscriptionName() {
        getAzureAccountInfo();
        String subscriptionName = "test-subscriptionName";
        when(mockCommonIaasService.getSubscriptionNameFromAzure(any(), anyString())).thenReturn(subscriptionName);
        //return subscriptionName;
    }
    
    /***************************************************
    * @project : 인프라 관리 대시보드
    * @description : Azure Account 조회 정보 결과 값 설정
    * @title : getAzureAccountInfo
    * @return : IaasAccountMgntVO
    ***************************************************/
    public IaasAccountMgntVO getAzureAccountInfo() {
        IaasAccountMgntVO vo = new IaasAccountMgntVO();
        vo.setAccountName("testAccountName");
        vo.setCreateUserId("admin");
        vo.setIaasType("azure");
        vo.setCommonTenant("commonUser");
        vo.setCommonAccessSecret("commonSecret");
        vo.setAzureSubscriptionId("azureSubscriptionId");
        when(mockCommonIaasService.getIaaSAccountInfo(any(), anyInt(), anyString())).thenReturn(vo);
        return vo;
    }
}
