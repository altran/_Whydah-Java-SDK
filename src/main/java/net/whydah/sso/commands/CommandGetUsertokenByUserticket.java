package net.whydah.sso.commands;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import net.whydah.sso.user.UserCredential;
import net.whydah.sso.user.UserHelper;
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

        logger.trace("CommandValidateUsertokenId - myAppTokenId={}",myAppTokenId);

        Client tokenServiceClient = Client.create();

        WebResource userTokenResource = tokenServiceClient.resource(tokenServiceUri).path("user/" + myAppTokenId + "/get_usertoken_by_userticket");
        MultivaluedMap<String,String> formData = new MultivaluedMapImpl();
        logger.trace("CommandGetUsertokenByUserticket - getUserTokenByUserTicket - ticket: {} apptoken: {}",userticket,myAppTokenXml);
        formData.add("apptoken", myAppTokenXml);
        formData.add("userticket", userticket);
        ClientResponse response = userTokenResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
            logger.warn("CommandGetUsertokenByUserticket - getUserTokenByUserTicket failed");
            throw new IllegalArgumentException("CommandGetUsertokenByUserticket - getUserTokenByUserTicket failed.");
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
        return UserHelper.getDummyToken();
    }



}