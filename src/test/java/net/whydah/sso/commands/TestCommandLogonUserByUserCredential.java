package net.whydah.sso.commands;

import net.whydah.sso.application.ApplicationCredential;
import net.whydah.sso.user.UserCredential;
import org.junit.BeforeClass;
import org.junit.Test;
import rx.Observable;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by totto on 12/2/14.
 */
public class TestCommandLogonUserByUserCredential {

    private static URI tokenServiceUri;
    private static ApplicationCredential appCredential;
    private static UserCredential userCredential;


        @BeforeClass
        public static void setup() throws Exception {
            tokenServiceUri = UriBuilder.fromUri("https://whydahdev.altrancloud.com/tokenservice/").build();
            appCredential = new ApplicationCredential();
            appCredential.setApplicationID("15");
            userCredential = new UserCredential("admin","admin");


        }



        @Test
        public void testApplicationLoginCommand() throws Exception {

            String applicationsecret = "33779936R6Jr47D4Hj5R6p9qT";
            appCredential.setApplicationSecret(applicationsecret);
            String myApplicationTokenID = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
            // System.out.println("ApplicationTokenID=" + myApplicationTokenID);
            assertTrue(myApplicationTokenID.length() > 6);


            String ticket = UUID.randomUUID().toString();
            String userToken = new CommandLogonUserByUserCredential(tokenServiceUri,myApplicationTokenID,  appCredential, userCredential,ticket ).execute();
            System.out.println("userToken:"+userToken);
        }

}