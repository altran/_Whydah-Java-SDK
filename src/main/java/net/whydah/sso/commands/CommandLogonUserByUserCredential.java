package net.whydah.sso.commands;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import net.whydah.sso.application.ApplicationCredential;
import net.whydah.sso.user.UserCredential;
import net.whydah.sso.user.UserHelper;
import net.whydah.sso.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.net.URI;
import java.util.UUID;

import static com.sun.jersey.api.client.ClientResponse.Status.*;

/**
 * Created by totto on 12/2/14.
 */
public class CommandLogonUserByUserCredential  extends HystrixCommand<String> {

    private static final Logger logger = LoggerFactory.getLogger(CommandLogonUserByUserCredential.class);

    private URI tokenServiceUri;
    private String myAppTokenId;
    private String myAppTokenXml;
    private UserCredential userCredential;
    private String userticket;


    public CommandLogonUserByUserCredential(URI tokenServiceUri,String myAppTokenId,String myAppTokenXml ,UserCredential userCredential) {
        super(HystrixCommandGroupKey.Factory.asKey("SSOAUserAuthGroup"));
        this.tokenServiceUri = tokenServiceUri;
        this.myAppTokenId=myAppTokenId;
        this.myAppTokenXml=myAppTokenXml;
        this.userCredential=userCredential;
        this.userticket= UUID.randomUUID().toString();  // Create new UUID ticket if not provided
    }


    public CommandLogonUserByUserCredential(URI tokenServiceUri,String myAppTokenId,String myAppTokenXml ,UserCredential userCredential,String userticket) {
        super(HystrixCommandGroupKey.Factory.asKey("SSOAUserAuthGroup"));
        this.tokenServiceUri = tokenServiceUri;
        this.myAppTokenId=myAppTokenId;
        this.myAppTokenXml=myAppTokenXml;
        this.userCredential=userCredential;
        this.userticket=userticket;
    }

    @Override
    protected String run() {

        logger.trace("CommandLogonUserByUserCredential - myAppTokenId={}",myAppTokenId);

        Client tokenServiceClient = Client.create();

        WebResource getUserToken = tokenServiceClient.resource(tokenServiceUri).path("user/" + myAppTokenId + "/" + userticket + "/usertoken");
        MultivaluedMap<String,String> formData = new MultivaluedMapImpl();
        formData.add("apptoken", myAppTokenXml);
        formData.add("usercredential", userCredential.toXML());
        ClientResponse response = getUserToken.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
            logger.warn("CommandLogonUserByUserCredential - getUserToken - User authentication failed with status code " + response.getStatus());
            return null;
        }
        if (response.getStatus() == OK.getStatusCode()) {
            String responseXML = response.getEntity(String.class);
            logger.trace("CommandLogonUserByUserCredential - getUserToken - Log on OK with response {}", responseXML);
            return responseXML;
        }

        //retry once for other statuses
        logger.info("CommandLogonUserByUserCredential - getUserToken - retry once for other statuses");
        response = getUserToken.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
        if (response.getStatus() == OK.getStatusCode()) {
            String responseXML = response.getEntity(String.class);
            logger.trace("CommandLogonUserByUserCredential - getUserToken - Log on OK with response {}", responseXML);
            return responseXML;
        } else if (response.getStatus() == NOT_FOUND.getStatusCode()) {
            logger.error(ExceptionUtil.printableUrlErrorMessage("CommandLogonUserByUserCredential - getUserToken - Auth failed - Problems connecting with TokenService", getUserToken, response));
        } else {
            logger.info(ExceptionUtil.printableUrlErrorMessage("CommandLogonUserByUserCredential - getUserToken - User authentication failed", getUserToken, response));
        }
        return null;

    }

    @Override
    protected String getFallback() {
        return UserHelper.getDummyToken();
    }



}