package net.whydah.sso.commands;

import net.whydah.sso.application.ApplicationCredential;
import net.whydah.sso.application.ApplicationHelper;
import org.junit.BeforeClass;
import org.junit.Test;
import rx.Observable;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Properties;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by totto on 12/2/14.
 */
public class TestCommandLogonApplication {

    private static URI tokenServiceUri;
    private static ApplicationCredential appCredential;


    @BeforeClass
    public static void setup() throws Exception {
        tokenServiceUri = UriBuilder.fromUri("https://whydahdev.altrancloud.com/tokenservice/").build();
        appCredential = new ApplicationCredential("15","33779936R6Jr47D4Hj5R6p9qT");
    }


    @Test
    public void testApplicationLoginCommandFallback() throws Exception {

        String applicationsecret = "false secret";
        appCredential.setApplicationSecret(applicationsecret);

        String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
        // System.out.println("ApplicationTokenID=" + myApplicationTokenID);
        assertEquals(ApplicationHelper.getDummyApplicationToken(), myAppTokenXml);

        Future<String> fmyAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).queue();
        assertEquals(ApplicationHelper.getDummyApplicationToken(), fmyAppTokenXml.get());


        Observable<String> omyAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).observe();
        // blocking
        assertEquals(ApplicationHelper.getDummyApplicationToken(), omyAppTokenXml.toBlocking().single());
    }

    @Test
    public void testApplicationLoginCommand() throws Exception {

        String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
        // System.out.println("ApplicationTokenID=" + myApplicationTokenID);
        assertTrue(myAppTokenXml.length() > 6);

        Future<String> fmyAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).queue();
        assertTrue(fmyAppTokenXml.get().length() > 6);

        Observable<String> omyAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).observe();
        // blocking
        assertTrue(omyAppTokenXml.toBlocking().single().length() > 6);
    }

}
