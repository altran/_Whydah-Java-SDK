package net.whydah.sso.commands;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import net.whydah.sso.application.ApplicationCredential;
import net.whydah.sso.user.UserTokenXpathHelper;
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

        Client tokenServiceClient = Client.create();
        WebResource logonResource = tokenServiceClient.resource(tokenServiceUri).path("logon");
        MultivaluedMap<String,String> formData = new MultivaluedMapImpl();


        formData.add("applicationcredential", appCredential.toXML());
        ClientResponse response;
        try {
            response = logonResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
        } catch (RuntimeException e) {
            logger.error("CommandLogonApplication - logonApplication - Problem connecting to {}", logonResource.toString());
            throw(e);
        }
        //todo håndtere feil i statuskode + feil ved app-pålogging (retry etc)
        if (response.getStatus() != 200) {
            logger.error("CommandLogonApplication - Application authentication failed with statuscode {}", response.getStatus());
            throw new RuntimeException("CommandLogonApplication - Application authentication failed");
        }
        String myAppTokenXml = response.getEntity(String.class);
        String myAppTokenId = UserTokenXpathHelper.getAppTokenIdFromAppToken(myAppTokenXml);
        logger.debug("CommandLogonApplication - Applogon ok: apptokenxml: {}", myAppTokenXml);
        logger.debug("CommandLogonApplication - myAppTokenId: {}", myAppTokenId);
        return myAppTokenId;
    }

    @Override
    protected String getFallback() {
        return "FallbackApplicationTokenID";
    }
}