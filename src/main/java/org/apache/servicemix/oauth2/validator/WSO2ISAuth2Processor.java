package org.apache.servicemix.oauth2.validator;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.servicemix.oauth2.validator.OAuthTokenValidationServiceClient;

import javax.servlet.http.HttpServletRequest;

/**
 *Camel Processor for validating OAuth2 token
 *
 */
public class WSO2ISAuth2Processor implements Processor {
    private String authorizationHeader;
    private String requestURI;
    private String accessToken;
    private String queryString;
    private OAuthTokenValidationServiceClient client;
    private String trustStorePath;
    private String trustStorePassword;
    private String identityURL;
    private String userName;
    private String password;

    public String getTrustStorePath() {
        return trustStorePath;
    }

    public void setTrustStorePath(String trustStorePath) {
        this.trustStorePath = trustStorePath;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    public String getIdentityURL() {
        return identityURL;
    }

    public void setIdentityURL(String identityURL) {
        this.identityURL = identityURL;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public void process(Exchange exchange) throws Exception {

        try {
            client = new OAuthTokenValidationServiceClient(trustStorePath, trustStorePassword, identityURL, userName, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        validateRequest(exchange);
        if (!client.validate(requestURI, accessToken)) {
            throw new Exception("OAuth 2.0 authentication failed");
        }
    }

    private void validateRequest(Exchange exchange) throws Exception {

        org.apache.cxf.message.Message cxfMessage = exchange.getIn().getHeader(CxfConstants.CAMEL_CXF_MESSAGE, org.apache.cxf.message.Message.class);
        HttpServletRequest request = (HttpServletRequest) cxfMessage.get("HTTP.REQUEST");

        //
        authorizationHeader = request.getHeader("Authorization");
        requestURI = request.getRequestURI();
        //
        if (authorizationHeader == null && requestURI == null && !"".equals(requestURI)) {
            throw new Exception("Not a valid OAuth Request");
        }
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            accessToken = authorizationHeader.substring(7).trim();
        } else if (queryString != null && queryString.contains("access_token")) {

            String[] params = queryString.substring(queryString.indexOf("?") + 1).split("&");
            for (String param : params) {
                if (param.contains("access_token")) {
                    accessToken = param.trim().substring(12).trim();
                }
            }
        }
    }
}
