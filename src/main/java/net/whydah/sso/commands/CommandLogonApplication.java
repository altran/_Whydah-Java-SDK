package net.whydah.sso.commands;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import net.whydah.sso.application.ApplicationCredential;
import net.whydah.sso.application.ApplicationHelper;
import net.whydah.sso.application.ApplicationXpathHelper;
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

        Client tokenServiceClient = ClientBuilder.newClient();
        Form formData = new Form();
        formData.param("applicationcredential", appCredential.toXML());

        Response response;
        WebTarget logonResource = tokenServiceClient.target(tokenServiceUri).path("logon");
        try {
//            response = logonResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
            response = postForm(formData, logonResource);
        } catch (RuntimeException e) {
            logger.error("CommandLogonApplication - logonApplication - Problem connecting to {}", logonResource.toString());
            throw(e);
        }
        if (response.getStatus() != OK.getStatusCode()) {
            logger.warn("CommandLogonApplication - Application authentication failed with statuscode {} - retrying ", response.getStatus());
            try {
//                WebResource logonResource = tokenServiceClient.resource(tokenServiceUri).path("logon");
                response = postForm(formData,logonResource);
            } catch (RuntimeException e) {
                logger.error("CommandLogonApplication - logonApplication - Problem connecting to {}", logonResource.toString());
                throw(e);
            }
            if (response.getStatus() != 200) {
                logger.error("CommandLogonApplication - Application authentication failed with statuscode {}", response.getStatus());
                throw new RuntimeException("CommandLogonApplication - Application authentication failed");
            }
        }
        String myAppTokenXml = response.readEntity(String.class);
        logger.debug("CommandLogonApplication - Applogon ok: apptokenxml: {}", myAppTokenXml);
        String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppToken(myAppTokenXml);
        logger.trace("CommandLogonApplication - myAppTokenId: {}", myApplicationTokenID);
        return myAppTokenXml;
    }

    private Response postForm(Form formData, WebTarget logonResource) {
        return logonResource.request().post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);
    }

    @Override
    protected String getFallback() {
        return  ApplicationHelper.getDummyApplicationToken();
    }
}