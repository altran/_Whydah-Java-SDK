package net.whydah.sso.util;

import net.whydah.sso.application.ApplicationCredential;
import net.whydah.sso.application.ApplicationXpathHelper;
import net.whydah.sso.commands.CommandLogonApplication;
import net.whydah.sso.commands.CommandLogonUserByUserCredential;
import net.whydah.sso.user.UserCredential;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.UUID;

/**
 * Created by totto on 06.05.15.
 */
public class WhydahUtil {



    public static String logOnApplicationAndUser(String stsURI, String applicationID,String applicationSecret, String username,String password){
        URI tokenServiceUri = UriBuilder.fromUri(stsURI).build();
        ApplicationCredential appCredential = new ApplicationCredential(applicationID,applicationSecret);
        String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
        String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppToken(myAppTokenXml);
        UserCredential userCredential = new UserCredential(username, password);
        String userToken = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, UUID.randomUUID().toString()).execute();
        return userToken;

    }
}
