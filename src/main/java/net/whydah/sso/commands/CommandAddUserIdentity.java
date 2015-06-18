package net.whydah.sso.commands;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import net.whydah.sso.user.UserCredential;
import net.whydah.sso.user.UserHelper;
import org.glassfish.jersey.client.ClientResponse;
import org.slf4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

import static javax.ws.rs.core.Response.Status.*;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by baardl on 15.06.15.
 */
public class CommandAddUserIdentity extends HystrixCommand<String> {
    private static final Logger log = getLogger(CommandAddUserIdentity.class);
    private URI userAdminServiceUri;
    private String myAppTokenId;
    private String adminUserTokenId;
    private UserCredential userCredential;
    private String userIdentityXml;



    public CommandAddUserIdentity(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String userIdentityXml) {
        super(HystrixCommandGroupKey.Factory.asKey("UASUserGroup"));
        this.userAdminServiceUri = userAdminServiceUri;
        this.myAppTokenId=myAppTokenId;
        this.adminUserTokenId=adminUserTokenId;
        this.userIdentityXml=userIdentityXml;
    }

    @Override
    protected String run() {

        log.trace("CommandLogonUserByUserCredential - myAppTokenId={}",myAppTokenId);

        Client tokenServiceClient = ClientBuilder.newClient();

        WebTarget addUser = tokenServiceClient.target(userAdminServiceUri).path(myAppTokenId + "/" + adminUserTokenId + "/user");
//        ClientResponse response = addUser.post(Entity.entity(userIdentityXml,MediaType.APPLICATION_XML_TYPE),ClientResponse.class);
        Response response = addUser.request().post(Entity.xml(userIdentityXml));
        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
            log.info("CommandLogonUserByUserCredential - addUser - User authentication failed with status code " + response.getStatus());
            return null;
            //throw new IllegalArgumentException("Log on failed. " + ClientResponse.Status.FORBIDDEN);
        }
        if (response.getStatus() == OK.getStatusCode()) {
            String responseXML = response.readEntity(String.class);
            log.debug("CommandLogonUserByUserCredential - addUser - Log on OK with response {}", responseXML);
            return responseXML;
        }

        /*
        //retry once for other statuses
        response = addUser.type(MediaType.APPLICATION_XML_TYPE).post(ClientResponse.class, formData);
        if (response.getStatus() == OK.getStatusCode()) {
            String responseXML = response.getEntity(String.class);
            log.debug("CommandLogonUserByUserCredential - addUser - Log on OK with response {}", responseXML);
            return responseXML;
        } else if (response.getStatus() == NOT_FOUND.getStatusCode()) {
            log.error(ExceptionUtil.printableUrlErrorMessage("CommandLogonUserByUserCredential - addUser - Auth failed - Problems connecting with TokenService", addUser, response));
        } else {
            log.info(ExceptionUtil.printableUrlErrorMessage("CommandLogonUserByUserCredential - addUser - User authentication failed", addUser, response));
        }
        */
        return null;
        //throw new RuntimeException("User authentication failed with status code " + response.getStatus());

    }

    @Override
    protected String getFallback() {
        return UserHelper.getDummyToken();
    }

}
