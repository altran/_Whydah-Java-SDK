package net.whydah.sso.commands;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import net.whydah.sso.application.ApplicationCredential;
import net.whydah.sso.application.ApplicationHelper;
import net.whydah.sso.application.ApplicationXpathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.net.URI;

/**
 * Created by totto on 12/1/14.
 *
 * Log on an application given supplied application credentials and return an applicationTokenID which is
 * an application session key.
 *
 */
public class CommandLogonApplication extends HystrixCommand<String> {

    private static final Logger logger = LoggerFactory.getLogger(CommandLogonApplication.class);
    private URI tokenServiceUri ;
    private ApplicationCredential appCredential ;

    public CommandLogonApplication(URI tokenServiceUri,ApplicationCredential appCredential) {
        super(HystrixCommandGroupKey.Factory.asKey("SSOApplicationAuthGroup"));
        this.tokenServiceUri = tokenServiceUri;
        this.appCredential=appCredential;
    }

    @Override
    protected String run() {
        logger.trace("CommandLogonApplication - appCredential={}",appCredential.toXML());

        Client tokenServiceClient = Client.create();

        MultivaluedMap<String,String> formData = new MultivaluedMapImpl();
        formData.add("applicationcredential", appCredential.toXML());

        ClientResponse response;
        try {
            WebResource logonResource = tokenServiceClient.resource(tokenServiceUri).path("logon");
            response = logonResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
        } catch (RuntimeException e) {
            logger.error("CommandLogonApplication - logonApplication - Problem connecting to {}", tokenServiceClient.resource(tokenServiceUri).path("logon").toString());
            throw(e);
        }
        if (response.getStatus() != 200) {
            logger.warn("CommandLogonApplication - Application authentication failed with statuscode {} - retrying ", response.getStatus());
            try {
                WebResource logonResource = tokenServiceClient.resource(tokenServiceUri).path("logon");
                response = logonResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
            } catch (RuntimeException e) {
                logger.error("CommandLogonApplication - logonApplication - Problem connecting to {}", tokenServiceClient.resource(tokenServiceUri).path("logon").toString());
                throw(e);
            }
            if (response.getStatus() != 200) {
                logger.error("CommandLogonApplication - Application authentication failed with statuscode {}", response.getStatus());
                throw new RuntimeException("CommandLogonApplication - Application authentication failed");
            }
        }
        String myAppTokenXml = response.getEntity(String.class);
        logger.debug("CommandLogonApplication - Applogon ok: apptokenxml: {}", myAppTokenXml);
        String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppToken(myAppTokenXml);
        logger.trace("CommandLogonApplication - myAppTokenId: {}", myApplicationTokenID);
        return myAppTokenXml;
    }

    @Override
    protected String getFallback() {
        return  ApplicationHelper.getDummyApplicationToken();
    }
}