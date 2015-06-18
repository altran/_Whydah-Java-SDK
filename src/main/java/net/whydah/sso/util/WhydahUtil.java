package net.whydah.sso.util;

import net.whydah.sso.application.ApplicationCredential;
import net.whydah.sso.application.ApplicationXpathHelper;
import net.whydah.sso.commands.CommandAddUserIdentity;
import net.whydah.sso.commands.CommandLogonApplication;
import net.whydah.sso.commands.CommandLogonUserByUserCredential;
import net.whydah.sso.user.UserCredential;
import net.whydah.sso.user.UserIdentityRepresentation;
import org.slf4j.Logger;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by totto on 06.05.15.
 */
public class WhydahUtil {
    private static final Logger log = getLogger(WhydahUtil.class);



    public static String logOnApplicationAndUser(String stsURI, String applicationID,String applicationSecret, String username,String password){
        URI tokenServiceUri = UriBuilder.fromUri(stsURI).build();
        ApplicationCredential appCredential = new ApplicationCredential(applicationID,applicationSecret);
        String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
        String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppToken(myAppTokenXml);
        UserCredential userCredential = new UserCredential(username, password);
        String userToken = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, UUID.randomUUID().toString()).execute();
        return userToken;

    }

    public static String addUser(String stsURI, String applicationTokenId, String adminUserTokenId, UserIdentityRepresentation userIdentity) {
        URI tokenServiceUri = UriBuilder.fromUri(stsURI).build();
        String userId = new CommandAddUserIdentity(tokenServiceUri, adminUserTokenId, adminUserTokenId,userIdentity.toXML()).execute();
        log.debug("Received userId");
        return userId;
    }
}
