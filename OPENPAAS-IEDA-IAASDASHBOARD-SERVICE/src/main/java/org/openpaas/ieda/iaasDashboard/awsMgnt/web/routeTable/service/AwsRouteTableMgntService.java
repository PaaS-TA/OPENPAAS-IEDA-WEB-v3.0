package org.openpaas.ieda.iaasDashboard.awsMgnt.web.routeTable.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.routeTable.dao.AwsRouteTableMgntVO;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.routeTable.dto.AwsRouteTableMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.model.InternetGateway;
import com.amazonaws.services.ec2.model.NatGateway;
import com.amazonaws.services.ec2.model.Route;
import com.amazonaws.services.ec2.model.RouteTable;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.ec2.model.Vpc;

@Service
public class AwsRouteTableMgntService {
    @Autowired
    AwsRouteTableMgntApiService awsRouteTableMgntApiService;
    @Autowired
    CommonIaasService commonIaasService;
    @Autowired
    MessageSource message;

    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Route Table 목록 조회
     * @title : getAwsRouteTableInfoList
     * @return : List<AwsrouteTableMgntVO>
     ***************************************************/
    public List<AwsRouteTableMgntVO> getAwsRouteTableInfoList(int accountId, String regionName, Principal principal) {
        IaasAccountMgntVO vo = getAwsAccountInfo(principal, accountId);
        Region region = getAwsRegionInfo(regionName);
        List<RouteTable> apiAwsRouteTableList = awsRouteTableMgntApiService.getAwsRouteTableListApiFromAws(vo,
                region.getName());
        List<AwsRouteTableMgntVO> awsrouteTableList = new ArrayList<AwsRouteTableMgntVO>();
        if (apiAwsRouteTableList.size() != 0) {
            for (int i = 0; i < apiAwsRouteTableList.size(); i++) {
                RouteTable routeTable = apiAwsRouteTableList.get(i);
                AwsRouteTableMgntVO awsrouteTableVO = new AwsRouteTableMgntVO();
                awsrouteTableVO.setRouteTableId(routeTable.getRouteTableId());
                if (routeTable.getTags().size() != 0) {
                    awsrouteTableVO.setNameTag(routeTable.getTags().get(0).getValue().toString());
                } else {
                    awsrouteTableVO.setNameTag(" - ");
                }
                if (routeTable.getAssociations().size() != 0) {
                    int k = 1;
                    for (int j = 0; j < routeTable.getAssociations().size(); j++) {
                        String subnetId = routeTable.getAssociations().get(j).getSubnetId();
                        List<String> sIds = new ArrayList<>();
                        sIds.add(subnetId);
                        if (routeTable.getAssociations().size() > sIds.size()) {
                            for (int p = 0; p < routeTable.getRoutes().size(); p++) {
                                String igw = routeTable.getRoutes().get(p).getGatewayId();
                                // explicitly associated subnet 보다 association
                                // 개수가 클 경우
                                if (subnetId == null && igw != null) {
                                    // association아래 subnetID없고 route target에
                                    // internetGateway ID있을 때는 main 테이블이다.
                                    awsrouteTableVO.setMainYN(true);
                                }
                            }
                        } else if (routeTable.getAssociations().size() == sIds.size()) {
                            // explicitly associated subnet 개수와 association 개수가
                            // 같을 경우 해당 association에 대한 main 여부 (boolean)정보를
                            // 가져온다.
                            awsrouteTableVO.setMainYN(routeTable.getAssociations().get(j).getMain());
                        } else {
                            // do nothing
                        }

                        if (subnetId != null) {
                            k++;
                            awsrouteTableVO.setAssociationCnt(k - 1);
                        } else {
                            awsrouteTableVO.setAssociationCnt(0);
                        }
                    }

                } else {
                    awsrouteTableVO.setAssociationCnt(0);
                }

                if (routeTable.getVpcId() != null) {
                    awsrouteTableVO.setVpcId(routeTable.getVpcId().toString());

                } else {
                    awsrouteTableVO.setVpcId(" - ");
                }
                awsrouteTableVO.setRecid(i);
                awsrouteTableVO.setAccountId(accountId);
                awsrouteTableList.add(awsrouteTableVO);
            }
        }
        return awsrouteTableList;
    }

    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS 선택 한 Route Table에 대한 Route 목록 조회
     * @title : getAwsRouteList
     * @return : List<AwsRouteTableMgntVO>
     ***************************************************/
    public List<AwsRouteTableMgntVO> getAwsRouteList(int accountId, String regionName, Principal principal,
            String routeTableId) {
        IaasAccountMgntVO vo = getAwsAccountInfo(principal, accountId);
        Region region = getAwsRegionInfo(regionName);
        List<RouteTable> apiAwsRouteTableList = awsRouteTableMgntApiService.getAwsRouteTableListApiFromAws(vo,
                region.getName());
        List<AwsRouteTableMgntVO> list = new ArrayList<AwsRouteTableMgntVO>();
        for (int i = 0; i < apiAwsRouteTableList.size(); i++) {
            String tableId = apiAwsRouteTableList.get(i).getRouteTableId().toString();
            if (tableId.equals(routeTableId)) {
                RouteTable routeTable = apiAwsRouteTableList.get(i);
                for (int j = 0; j < routeTable.getRoutes().size(); j++) {

                    AwsRouteTableMgntVO awsRTmgntVo = new AwsRouteTableMgntVO();
                    awsRTmgntVo.setRouteTableId(routeTableId);

                    // Propagated 여부
                    int size = routeTable.getPropagatingVgws().size();
                    String sizeString = Integer.toString(size);
                    Boolean exists = !sizeString.equals("0");
                    awsRTmgntVo.setPropagatedYN(exists);

                    Route route = routeTable.getRoutes().get(j);
                    awsRTmgntVo.setDestinationIpv4CidrBlock(route.getDestinationCidrBlock());
                    awsRTmgntVo.setTargetId(route.getGatewayId());
                    if (route.getNatGatewayId() != null) {
                        awsRTmgntVo.setTargetId(route.getNatGatewayId());
                    }
                    awsRTmgntVo.setStatus(route.getState());
                    awsRTmgntVo.setRecid(j);
                    awsRTmgntVo.setAccountId(vo.getId());

                    list.add(awsRTmgntVo);
                }
            }
        }
        return list;
    }

    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS 선택 한 Route Table에 대한 Explicitly Associated Subnets 목록
     *              조회 //routeTable > associations > getSubnetId && get해당
     *              subnet의 object info 목록
     * @title : getAwsAssociatedWithThisTableSubnetList
     * @return : List<AwsRouteTableMgntVO>
     ***************************************************/
    public List<AwsRouteTableMgntVO> getAwsAssociatedWithThisTableSubnetList(int accountId, String regionName,
            Principal principal, String routeTableId, String vpcId) {
        IaasAccountMgntVO vo = getAwsAccountInfo(principal, accountId);
        Region region = getAwsRegionInfo(regionName);
        List<Subnet> apiAwsSubnetList = awsRouteTableMgntApiService.getAwsSubnetInfoListApiFromAws(vo,
                region.getName());
        List<RouteTable> apiAwsRouteTableList = awsRouteTableMgntApiService.getAwsRouteTableListApiFromAws(vo,
                region.getName());
        List<AwsRouteTableMgntVO> list = new ArrayList<AwsRouteTableMgntVO>();
        for (int i = 0; i < apiAwsSubnetList.size(); i++) {
            String subnetVpcId = apiAwsSubnetList.get(i).getVpcId().toString();
            if (subnetVpcId.equals(vpcId)) {
                List<String> subnetVpcIds = new ArrayList<String>();
                subnetVpcIds.add(subnetVpcId);
                for (int j = 0; j < subnetVpcIds.size(); j++) {
                    for (int h = 0; h < apiAwsRouteTableList.size(); h++) {
                        RouteTable apiRouteTable = apiAwsRouteTableList.get(h);
                        // if(apiRouteTable.getRouteTableId().equals(routeTableId)
                        // &&
                        // apiRouteTable.getVpcId().equals(subnetVpcIds.get(j))){
                        if (apiRouteTable.getRouteTableId().equals(routeTableId)) {
                            String apiSubnetId = apiAwsSubnetList.get(i).getSubnetId();
                            if (apiRouteTable.getAssociations().size() != 0) {
                                AwsRouteTableMgntVO awsRTmgntVo = new AwsRouteTableMgntVO();
                                int y = apiRouteTable.getAssociations().size();
                                for (int x = 0; x < y; x++) {
                                    String tableSubnetId = apiRouteTable.getAssociations().get(x).getSubnetId();
                                    if (apiSubnetId.equals(tableSubnetId)) {
                                        awsRTmgntVo.setRouteTableId(
                                                apiRouteTable.getAssociations().get(x).getRouteTableId());
                                        awsRTmgntVo.setSubnetId(apiAwsSubnetList.get(i).getSubnetId());
                                        awsRTmgntVo.setDestinationIpv4CidrBlock(apiAwsSubnetList.get(i).getCidrBlock());
                                        awsRTmgntVo.setAssociationId(
                                                apiRouteTable.getAssociations().get(x).getRouteTableAssociationId());
                                        int vp6size = apiAwsSubnetList.get(i).getIpv6CidrBlockAssociationSet().size();
                                        if (vp6size != 0) {
                                            for (int k = 0; k < vp6size; k++) {
                                                String ipv6Block = "";
                                                ipv6Block += apiAwsSubnetList.get(i).getIpv6CidrBlockAssociationSet()
                                                        .get(k).getIpv6CidrBlock();
                                                awsRTmgntVo.setIpv6CidrBlock(ipv6Block);
                                            }
                                        } else if (vp6size == 0) {
                                            awsRTmgntVo.setIpv6CidrBlock("-");
                                        }

                                        awsRTmgntVo.setRecid(i);
                                        awsRTmgntVo.setAccountId(accountId);
                                        list.add(awsRTmgntVo);
                                    }
                                    // else{
                                    // different routeTable with same vpcId &&
                                    // not main
                                    // }
                                }
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS 선택한 Route Table에 대한 Association이 수정 가능한 Subnets 목록 조회
     *              (화면 하단)
     * @title : getAwsAvailableSubAssociationList
     * @return : List<AwsRouteTableMgntVO>
     ***************************************************/
    public List<AwsRouteTableMgntVO> getAwsAvailableSubnetList(int accountId, String regionName, Principal principal,
            String routeTableId, String vpcId) {
        IaasAccountMgntVO vo = getAwsAccountInfo(principal, accountId);
        Region region = getAwsRegionInfo(regionName);
        List<Subnet> apiAwsSubnetList = awsRouteTableMgntApiService.getAwsSubnetInfoListApiFromAws(vo,
                region.getName());
        List<RouteTable> apiAwsRouteTableList = awsRouteTableMgntApiService.getAwsRouteTableListApiFromAws(vo,
                region.getName());
        List<AwsRouteTableMgntVO> list = new ArrayList<AwsRouteTableMgntVO>();
        for (int i = 0; i < apiAwsSubnetList.size(); i++) {
            String subnetVpcId = apiAwsSubnetList.get(i).getVpcId().toString();
            if (subnetVpcId.equals(vpcId)) {
                List<String> subnetVpcIds = new ArrayList<String>();
                subnetVpcIds.add(subnetVpcId);
                for (int j = 0; j < subnetVpcIds.size(); j++) {
                    AwsRouteTableMgntVO awsRTmgntVo = new AwsRouteTableMgntVO();
                    for (int h = 0; h < apiAwsRouteTableList.size(); h++) {
                        RouteTable routeTable = apiAwsRouteTableList.get(h);
                        if (routeTable.getVpcId().equals(subnetVpcIds.get(j))) {
                            String apiSubnetId = apiAwsSubnetList.get(i).getSubnetId();
                            if (routeTable.getAssociations().size() != 0) {
                                int y = routeTable.getAssociations().size();
                                for (int x = 0; x < y; x++) {
                                    String tableSubnetId = routeTable.getAssociations().get(x).getSubnetId();
                                    String rTableId = routeTable.getAssociations().get(x).getRouteTableId();
                                    if (apiSubnetId.equals(tableSubnetId)) {
                                        if (rTableId.equals(routeTableId)) {
                                            awsRTmgntVo.setRouteTableId("associated to " + rTableId);
                                            awsRTmgntVo.setCheck(true);
                                            awsRTmgntVo.setAssociationId(
                                                    routeTable.getAssociations().get(x).getRouteTableAssociationId());
                                        } else {
                                            awsRTmgntVo.setRouteTableId(rTableId);
                                            awsRTmgntVo.setAssociationId(
                                                    routeTable.getAssociations().get(x).getRouteTableAssociationId());
                                        }
                                    }
                                }
                            }
                        }
                        /*
                         * else{ awsRTmgntVo.setRouteTableId(""); }
                         */
                    }
                    awsRTmgntVo.setSubnetId(apiAwsSubnetList.get(i).getSubnetId());
                    awsRTmgntVo.setDestinationIpv4CidrBlock(apiAwsSubnetList.get(i).getCidrBlock());
                    int vp6size = apiAwsSubnetList.get(i).getIpv6CidrBlockAssociationSet().size();
                    if (vp6size != 0) {
                        for (int k = 0; k < vp6size; k++) {
                            String ipv6Block = "";
                            ipv6Block += apiAwsSubnetList.get(i).getIpv6CidrBlockAssociationSet().get(k)
                                    .getIpv6CidrBlock();
                            awsRTmgntVo.setIpv6CidrBlock(ipv6Block);
                        }
                    } else if (vp6size == 0) {
                        awsRTmgntVo.setIpv6CidrBlock("-");
                    }
                    awsRTmgntVo.setRecid(i);
                    awsRTmgntVo.setAccountId(accountId);
                    list.add(awsRTmgntVo);
                }
            }
        }
        return list;
    }

    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS VPC ID 목록 조회
     * @title : getAwsVpcIdList
     * @return : List<AwsRouteTableMgntVO>
     ***************************************************/
    public List<AwsRouteTableMgntVO> getAwsVpcIdList(int accountId, String regionName, Principal principal) {
        IaasAccountMgntVO vo = getAwsAccountInfo(principal, accountId);
        Region region = getAwsRegionInfo(regionName);
        List<Vpc> apiVpcIdList = awsRouteTableMgntApiService.getAwsVpcIdListApiFromAws(vo, region.getName());
        List<AwsRouteTableMgntVO> awsVpcIdList = new ArrayList<AwsRouteTableMgntVO>();
        for (int i = 0; i < apiVpcIdList.size(); i++) {
            Vpc vpc = apiVpcIdList.get(i);

            AwsRouteTableMgntVO awsRouteTableVO = new AwsRouteTableMgntVO();
            awsRouteTableVO.setVpcId(vpc.getVpcId().toString());
            if (vpc.getTags().size() != 0) {
                String result = "";
                for (int j = 0; j < vpc.getTags().size(); j++) {
                    result += vpc.getTags().get(j).getValue().toString();
                    if (result != "null") {
                        awsRouteTableVO.setNameTag(result);
                    } else if (result == "null") {
                        awsRouteTableVO.setNameTag("");
                    }
                }
            }
            awsRouteTableVO.setRecid(i);
            awsRouteTableVO.setAccountId(accountId);
            awsVpcIdList.add(awsRouteTableVO);
        }

        return awsVpcIdList;
    }

    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Target 목록 조회
     * @title : getAwsTargetInfoList
     * @return : List<AwsRouteTableMgntVO>
     ***************************************************/
    public ArrayList<String> getAwsTargetInfoList(int accountId, String regionName, Principal principal, String vpcId) {
        IaasAccountMgntVO vo = getAwsAccountInfo(principal, accountId);
        Region region = getAwsRegionInfo(regionName);
        List<InternetGateway> apiAwsIGWList = awsRouteTableMgntApiService.getAwsIGWListApiFromAws(vo, region.getName());
        int cnt = 1;
        ArrayList<String> targets = new ArrayList<String>();
        if (apiAwsIGWList != null && apiAwsIGWList.size() != 0) {
            for (int i = 0; i < apiAwsIGWList.size(); i++) {
                InternetGateway igw = apiAwsIGWList.get(i);
                for (int m = 0; m < igw.getAttachments().size(); m++) {
                    if (igw.getAttachments().get(m).getVpcId().equals(vpcId)) {
                        String result = "";
                        if (igw.getTags().size() != 0) {
                            for (int j = 0; j < igw.getTags().size(); j++) {
                                result += igw.getTags().get(j).getValue().toString();
                            }
                            if (result != "" && result != "null") {
                                targets.add(cnt - 1, igw.getInternetGatewayId().toString() + " | " + result);
                            }
                        } else {
                            targets.add(cnt - 1, igw.getInternetGatewayId().toString());
                        }
                        cnt++;
                    }
                }
            }
        }
        List<NatGateway> apiAwsNatGWList = awsRouteTableMgntApiService.getAwsNatGatewayListApiFromAws(vo,
                region.getName());
        int cntt = 1;
        if (apiAwsNatGWList != null && apiAwsNatGWList.size() != 0) {
            for (int k = 0; k < apiAwsNatGWList.size(); k++) {
                NatGateway natgw = apiAwsNatGWList.get(k);
                if (natgw != null) {
                    if (natgw.getState().equals("available") && natgw.getVpcId().equals(vpcId)) {
                        if (!natgw.getClass().getSimpleName().isEmpty()) {
                            targets.add(cnt - 1 + cntt - 1,
                                    natgw.getNatGatewayId().toString() + " | " + natgw.getClass().getSimpleName());
                        }
                        targets.add(cnt - 1 + cntt - 1, natgw.getNatGatewayId().toString());
                        cntt++;
                    }
                }
            }
        }
        if (targets == null || targets.size() == 0) {
            // do nothing
        }
        return targets;
    }

    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Route Table 생성
     * @title : saveAwsRouteTableInfo
     * @return : void
     ***************************************************/
    public void saveAwsRouteTableInfo(AwsRouteTableMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo = getAwsAccountInfo(principal, dto.getAccountId());
        Region region = getAwsRegionInfo(dto.getRegion());
        try {
            awsRouteTableMgntApiService.createAwsRouteTableFromAws(vo, region.getName(), dto);
        } catch (Exception e) {
            String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != null){
                throw new CommonException(
                  detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
            }else{
                throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                        message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
        }
    }

    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Route 생성
     * @title : addAwsRouteInfo
     * @return : void
     ***************************************************/
    public void addAwsRouteInfo(AwsRouteTableMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo = getAwsAccountInfo(principal, dto.getAccountId());
        Region region = getAwsRegionInfo(dto.getRegion());
        try {
            awsRouteTableMgntApiService.createAwsRouteFromAws(vo, region.getName(), dto);
        } catch (Exception e) {
            String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != null){
                throw new CommonException(
                  detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
            }else{
                e.printStackTrace();
                throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                        message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
        }
    }

    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Route 삭제
     * @title : deleteAwsRouteInfo
     * @return : void
     ***************************************************/
    public void deleteAwsRouteInfo(AwsRouteTableMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo = getAwsAccountInfo(principal, dto.getAccountId());
        Region region = getAwsRegionInfo(dto.getRegion());
        try {
            awsRouteTableMgntApiService.deleteAwsRouteInRouteTableFromAws(vo, region.getName(), dto);
        } catch (Exception e) {
            String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != null){
                throw new CommonException(
                  detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
            }else{
                throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                        message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
        }
    }

    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Associate Subnet with Route Table
     * @title : associateAwsSubnetWithRouteTableFromAws
     * @return : void
     ***************************************************/
    public void associateAwsSubnetWithRouteTable(AwsRouteTableMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo = getAwsAccountInfo(principal, dto.getAccountId());
        Region region = getAwsRegionInfo(dto.getRegion());
        try {
            awsRouteTableMgntApiService.associateAwsSubnetWithRouteTableFromAws(vo, region.getName(), dto);
        } catch (Exception e) {
            String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != null){
                throw new CommonException(
                  detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
            }else{
                e.printStackTrace();
                throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                        message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
        }
    }

    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Disassociate Subnet From Route Table
     * @title : disassociateAwsSubnetFromRouteTable
     * @return : void
     ***************************************************/
    public void disassociateAwsSubnetFromRouteTable(AwsRouteTableMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo = getAwsAccountInfo(principal, dto.getAccountId());
        Region region = getAwsRegionInfo(dto.getRegion());
        try {
            awsRouteTableMgntApiService.disassociateAwsSubnetFromRouteTableFromAws(vo, region.getName(), dto);
        } catch (Exception e) {
            String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != null){
                throw new CommonException(
                  detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
            }else{
                throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                        message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
        }
    }

    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS delete Route Table
     * @title : deleteRouteTable
     * @return : void
     ***************************************************/
    public void deleteRouteTable(AwsRouteTableMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo = getAwsAccountInfo(principal, dto.getAccountId());
        Region region = getAwsRegionInfo(dto.getRegion());
        try {
            awsRouteTableMgntApiService.deleteAwsRouteTableFromAws(vo, region.getName(), dto);
        } catch (Exception e) {
            String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != null){
                throw new CommonException(
                  detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
            }else{
                throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                        message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
        }
    }

    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS 계정 정보가 실제 존재 하는지 확인 및 상세 조회
     * @title : getAwsAccountInfo
     * @return : IaasAccountMgntVO
     ***************************************************/
    public IaasAccountMgntVO getAwsAccountInfo(Principal principal, int accountId) {
        return commonIaasService.getIaaSAccountInfo(principal, accountId, "aws");
    }

    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS 리전 명 조회
     * @title : getAwsRegionInfo
     * @return : Region
     ***************************************************/
    public Region getAwsRegionInfo(String regionName) {
        return commonIaasService.getAwsRegionInfo(regionName);
    }

}
