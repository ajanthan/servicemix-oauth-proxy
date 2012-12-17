/*
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.servicemix.oauth2.validator;

import org.wso2.carbon.identity.oauth2.OAuth2TokenValidationService;
import org.wso2.carbon.identity.oauth2.OAuth2TokenValidationServicePortType;
import org.wso2.carbon.identity.oauth2.dto.xsd.OAuth2TokenValidationRequestDTO;
import org.wso2.carbon.identity.oauth2.dto.xsd.ObjectFactory;

import javax.xml.ws.BindingProvider;
import java.net.MalformedURLException;
import java.net.URL;

public class OAuthTokenValidationServiceClient {


    /*affected */
    private static String adminUserName;
    private static String adminPassword;

    private String wsdlLocation = null;

    private OAuth2TokenValidationService oAuth2TokenValidationService = null;
    private OAuth2TokenValidationServicePortType portType = null;

    public OAuthTokenValidationServiceClient(String trustStorePath, String trustStorePassword,
                                             String identityURL, String userName, String password)
            throws MalformedURLException {
        //set trust store properties required in SSL communication.
        System.setProperty("javax.net.ssl.trustStore", trustStorePath);
        System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);

        adminUserName = userName;
        adminPassword = password;

        //create service client with given url
        if (wsdlLocation != null) {
            URL wsdlURL = new URL(wsdlLocation);
            oAuth2TokenValidationService = new OAuth2TokenValidationService(wsdlURL);
        } else {
            //create client with defaults in wsdl
            oAuth2TokenValidationService = new OAuth2TokenValidationService();
        }

        portType = oAuth2TokenValidationService.getOAuth2TokenValidationServiceHttpsSoap11Endpoint();
        BindingProvider bp = (BindingProvider) portType;
        bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, adminUserName);
        bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, adminPassword);
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, identityURL + "/services/OAuth2TokenValidationService");

    }

    public boolean validate(String requestURI, String token) {
        OAuth2TokenValidationRequestDTO oAuth2TokenValidationRequestDTO = new OAuth2TokenValidationRequestDTO();
        ObjectFactory factory = new ObjectFactory();
        oAuth2TokenValidationRequestDTO.setContext(factory.createOAuth2TokenValidationRequestDTOContext(requestURI));
        oAuth2TokenValidationRequestDTO.setAccessToken(factory.createOAuth2TokenValidationRequestDTOAccessToken(token));
        oAuth2TokenValidationRequestDTO.setTokenType(factory.createOAuth2TokenValidationRequestDTOTokenType("bearer"));
        return portType.validate(oAuth2TokenValidationRequestDTO).isValid();

    }

}
