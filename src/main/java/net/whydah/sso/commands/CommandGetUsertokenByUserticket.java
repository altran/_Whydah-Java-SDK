package net.whydah.sso.commands;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import net.whydah.sso.user.UserHelper;
import net.whydah.sso.util.ExceptionUtil;
import org.glassfish.jersey.client.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.net.URI;

import static javax.ws.rs.core.Response.Status.*;

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

        String responseXML = null;
        logger.trace("CommandValidateUsertokenId - myAppTokenId={}",myAppTokenId);

        Client tokenServiceClient = ClientBuilder.newClient();

        WebTarget userTokenResource = tokenServiceClient.target(tokenServiceUri).path("user/" + myAppTokenId + "/get_usertoken_by_userticket");
        logger.trace("CommandGetUsertokenByUserticket - getUserTokenByUserTicket - ticket: {} apptoken: {}",userticket,myAppTokenXml);
        Form formData = new Form();
        formData.param("apptoken", myAppTokenXml);
        formData.param("userticket", userticket);

        Response response = userTokenResource.request().post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);
        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
            logger.warn("CommandGetUsertokenByUserticket - getUserTokenByUserTicket failed");
            throw new IllegalArgumentException("CommandGetUsertokenByUserticket - getUserTokenByUserTicket failed.");
        }
        if (response.getStatus() == OK.getStatusCode()) {
            responseXML = response.readEntity(String.class);
            logger.debug("CommandGetUsertokenByUserticket - Response OK with XML: {}", responseXML);
        } else {
            //retry
            response =userTokenResource.request().post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);
            if (response.getStatus() == OK.getStatusCode()) {
                responseXML = response.readEntity(String.class);
                logger.debug("CommandGetUsertokenByUserticket - Response OK with XML: {}", responseXML);
            }
        }

        if (responseXML == null) {
            String authenticationFailedMessage = ExceptionUtil.printableUrlErrorMessage("User authentication failed", userTokenResource, response);
            logger.warn(authenticationFailedMessage);
            throw new RuntimeException(authenticationFailedMessage);
        }

        return responseXML;


    }

    @Override
    protected String getFallback() {
        return UserHelper.getDummyToken();
    }



}