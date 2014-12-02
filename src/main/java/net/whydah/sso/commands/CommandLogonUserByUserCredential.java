package net.whydah.sso.commands;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import net.whydah.sso.application.ApplicationCredential;
import net.whydah.sso.user.UserCredential;
import net.whydah.sso.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.net.URI;

import static com.sun.jersey.api.client.ClientResponse.Status.*;

/**
 * Created by totto on 12/2/14.
 */
public class CommandLogonUserByUserCredential  extends HystrixCommand<String> {

    private static final Logger logger = LoggerFactory.getLogger(CommandLogonUserByUserCredential.class);

    private URI tokenServiceUri;
    private ApplicationCredential appCredential ;
    private UserCredential userCredential;
    private String userticket;



    public CommandLogonUserByUserCredential(URI tokenServiceUri,ApplicationCredential appCredential ,UserCredential userCredential,String userticket) {
        super(HystrixCommandGroupKey.Factory.asKey("SSOAUserAuthGroup"));
        this.tokenServiceUri = tokenServiceUri;
        this.appCredential=appCredential;
        this.userticket=userticket;
    }

    @Override
    protected String run() {


        Client tokenServiceClient = Client.create();
        //logonApplication();
        logger.debug("getUserToken - Application logon OK. applicationTokenId={}. Log on with user credentials {}.", appCredential.getApplicationID(), userCredential.toString());

        WebResource getUserToken = tokenServiceClient.resource(tokenServiceUri).path("user/" + appCredential.getApplicationID() + "/" + userticket + "/usertoken");
        MultivaluedMap<String,String> formData = new MultivaluedMapImpl();
        formData.add("apptoken", appCredential.toXML());
        formData.add("usercredential", userCredential.toXML());
        ClientResponse response = getUserToken.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
            logger.info("getUserToken - User authentication failed with status code " + response.getStatus());
            return null;
            //throw new IllegalArgumentException("Log on failed. " + ClientResponse.Status.FORBIDDEN);
        }
        if (response.getStatus() == OK.getStatusCode()) {
            String responseXML = response.getEntity(String.class);
            logger.debug("getUserToken - Log on OK with response {}", responseXML);
            return responseXML;
        }

        //retry once for other statuses
        response = getUserToken.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
        if (response.getStatus() == OK.getStatusCode()) {
            String responseXML = response.getEntity(String.class);
            logger.debug("getUserToken - Log on OK with response {}", responseXML);
            return responseXML;
        } else if (response.getStatus() == NOT_FOUND.getStatusCode()) {
            logger.error(ExceptionUtil.printableUrlErrorMessage("getUserToken - Auth failed - Problems connecting with TokenService", getUserToken, response));
        } else {
            logger.info(ExceptionUtil.printableUrlErrorMessage("getUserToken - User authentication failed", getUserToken, response));
        }
        return null;
        //throw new RuntimeException("User authentication failed with status code " + response.getStatus());

    }

    @Override
    protected String getFallback() {
        return getDummyToken();
    }


    public static  String getDummyToken(){
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<usertoken xmlns:ns2=\"http://www.w3.org/1999/xhtml\" id=\"759799fe-2e2f-4c8e-b096-d5796733d4d2\">\n" +
                "    <uid>7583278592730985723</uid>\n" +
                "    <securitylevel>0</securitylevel>\n" +
                "    <personRef></personRef>\n" +
                "    <firstname>Olav</firstname>\n" +
                "    <lastname>Nordmann</lastname>\n" +
                "    <email></email>\n" +
                "    <timestamp>7982374982374</timestamp>\n" +
                "    <lifespan>3600000</lifespan>\n" +
                "    <issuer>/iam/issuer/tokenverifier</issuer>\n" +
                "    <application ID=\"2349785543\">\n" +
                "        <applicationName>MyApp</applicationName>\n" +
                "        <organization ID=\"2349785543\">\n" +
                "            <organizationName>myCompany</organizationName>\n" +
                "            <role name=\"janitor\" value=\"Employed\"/>\n" +
                "            <role name=\"board\" value=\"President\"/>\n" +
                "        </organization>\n" +
                "        <organization ID=\"0078\">\n" +
                "            <organizationName>myDayJobCompany</organizationName>\n" +
                "            <role name=\"board\" value=\"\"/>\n" +
                "        </organization>\n" +
                "    </application>\n" +
                "    <application ID=\"appa\">\n" +
                "        <applicationName>App A</applicationName>\n" +
                "        <organization ID=\"1078\">\n" +
                "            <organizationName>myFotballClub</organizationName>\n" +
                "            <role name=\"janitor\" value=\"Employed\"/>\n" +
                "        </organization>\n" +
                "    </application>\n" +
                "\n" +
                "    <ns2:link type=\"application/xml\" href=\"/\" rel=\"self\"/>\n" +
                "    <hash type=\"MD5\">7671ec2d5bac82d1e70b33c59b5c96a3</hash>\n" +
                "</usertoken>";

    }

}