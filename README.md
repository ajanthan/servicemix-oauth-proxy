servicemix-oauth-proxy
======================

This is a sample on how to validate Oauth2 calls in apache servicemix using WSO2 Identity server.

How to run the sample:

----------------------
Setting up the sample in Service-Mix:

-------------------------------------

1. Download apache-servicemix-4.4.1.



2. Start service-mix by ./servicemix.



3. You will get the karaf console in which you can install osgi features.



4. Run "osgi:list" to see already installed features.



5. If following features not installed, install them with "features:install" command

	- camel-cxf

	- camel-cxf-transport

	- camel-http



6. Install the Hello Service which is already shipped with Service-Mix. This is our SOAP based backend service.

	- Run "features:install examples-cxf-osgi"

	- If the feature is installed properly, you will be able to access the service wsdl at http://localhost:8181/cxf/HelloWorld?wsdl



7. Edit the sample source code as follows to suite your environement:

	- change followings in ""

		- Identity Server Urls.

		- Trust store paths.

		- Trust store passsword

        - Admin username

        - Admin password



8. Build the sample:

	mvn clean install



9. Deploy the sample:

	- After building the sample, place "ervicemix-oauth-proxy-1.0.0.jar" found inside "cxf-camel-proxy/target" into "apache-servicemix-4.4.1/deploy".

	- If it is successfully deployed, you will be able to access the BE HelloService wsdl exposed through CXF proxy at:

		http://localhost:9181/cxf/HelloServiceProxy?wsdl



10. Now our Service-Mix setup is done.



Setting up Identity Server:

---------------------------



1. Start Identity Server.

2.Login and goto Home>Manage>OAuth

3.Register a New Application with OAuth 2.0, name and Callback Url(Put any dummy url,this is not used by the demonstration)

4.After successfull registration note down the Client Id and Client Secrete.



Running the sample:

-------------------



1.First get a token as follow and change the Client Id:Client Secrete

   curl --user _q6QHAUhFjcugjIcTtixAHOyh14a:ax_fsWGIgXjH49OBLIcVae3Z6uYa  -k -d "grant_type=password&username=admin&password=admin" -H "Content-Type:application/x-www-form-urlencoded" https://localhost:9443/oauth2endpoints/token





2.Call the web service and replace token with what you got from previous invocation.

   curl -v -X POST -H "Authorization: Bearer 7d9aaf815555d983446db1c29bd7426" -H "Content-Type: application/x-www-form-urlenco"  -d@sayHi.xml http://localhost:9181/cxf/HelloServiceProxy/sayHi





contents of sayHi.xml

    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cxf="http://cxf.examples.servicemix.apache.org/">
     <soapenv:Header/>
      <soapenv:Body>
       <cxf:sayHi>
        <!--Optional:-->
        <arg0>aja</arg0>
       </cxf:sayHi>
     </soapenv:Body>
    </soapenv:Envelope>
