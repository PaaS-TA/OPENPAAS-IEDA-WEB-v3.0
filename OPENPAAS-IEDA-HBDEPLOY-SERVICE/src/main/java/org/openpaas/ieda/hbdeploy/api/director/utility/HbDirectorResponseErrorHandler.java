package org.openpaas.ieda.hbdeploy.api.director.utility;

import java.io.IOException;

import org.openpaas.ieda.hbdeploy.api.director.utility.HbDirectorResponseErrorHandler;
import org.openpaas.ieda.deploy.api.director.utility.RestErrorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;


public class HbDirectorResponseErrorHandler implements ResponseErrorHandler {

private final static Logger LOG = LoggerFactory.getLogger(HbDirectorResponseErrorHandler.class);

@Override
public boolean hasError(ClientHttpResponse response) throws IOException {
///주어진 응답에 오류가 있는지 여부를 응답한다.
if(LOG.isDebugEnabled()){
LOG.debug("# DirectorResponseErrorHandler STATUS CODE : " + response.getStatusCode());
}
return RestErrorUtil.isError(response.getStatusCode());
}

@Override
public void handleError(ClientHttpResponse response) throws IOException {
//주어진 응답의 오류를 처리한다.
if(LOG.isDebugEnabled()){
LOG.debug("# Response error: {} {}"+ response.getStatusCode() + " : " + response.getStatusText());
}
}

}
