package net.whydah.sso.commands;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import net.whydah.sso.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.net.URI;

import static com.sun.jersey.api.client.ClientResponse.Status.FORBIDDEN;
import static com.sun.jersey.api.client.ClientResponse.Status.OK;

/**
 * Created by totto on 12/2/14.
 */
public class CommandGetUsertokenByUserticket extends HystrixCommand<String> {

    private static final Logger logger = LoggerFactory.getLogger(CommandGetUsertokenByUserticket.class);

    private URI tokenServiceUri ;
    private String myAppTokenId ;
    private String userticket;
    private String myAppTokenXml;



    public CommandGetUsertokenByUserticket(URI tokenServiceUri,String myAppTokenId,String myAppTokenXml,String userticket) {
        super(HystrixCommandGroupKey.Factory.asKey("SSOAUserAuthGroup"));
        this.tokenServiceUri = tokenServiceUri;
        this.myAppTokenId=myAppTokenId;
        this.userticket=userticket;
        this.myAppTokenXml=myAppTokenXml;
    }

    @Override
    protected String run() {

        // logonApplication();

        Client tokenServiceClient = Client.create();

        WebResource userTokenResource = tokenServiceClient.resource(tokenServiceUri).path("user/" + myAppTokenId + "/get_usertoken_by_userticket");
        MultivaluedMap<String,String> formData = new MultivaluedMapImpl();
        logger.trace("CommandGetUsertokenByUserticket - getUserTokenByUserTicket - ticket: {} apptoken: {}",userticket,myAppTokenXml);
        formData.add("apptoken", myAppTokenXml);
        formData.add("userticket", userticket);
        ClientResponse response = userTokenResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
            logger.warn("getUserTokenByUserTicket failed");
            throw new IllegalArgumentException("getUserTokenByUserTicket failed.");
        }
        if (response.getStatus() == OK.getStatusCode()) {
            String responseXML = response.getEntity(String.class);
            logger.debug("CommandGetUsertokenByUserticket - Response OK with XML: {}", responseXML);
            return responseXML;
        }
        //retry
        response = userTokenResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
        if (response.getStatus() == OK.getStatusCode()) {
            String responseXML = response.getEntity(String.class);
            logger.debug("CommandGetUsertokenByUserticket - Response OK with XML: {}", responseXML);
            return responseXML;
        }
        String authenticationFailedMessage = ExceptionUtil.printableUrlErrorMessage("User authentication failed", userTokenResource, response);
        logger.warn(authenticationFailedMessage);
        throw new RuntimeException(authenticationFailedMessage);
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