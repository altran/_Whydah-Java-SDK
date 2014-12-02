package net.whydah.sso.commands;

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
    private static String applicationid;


    @BeforeClass
    public static void setup() throws Exception {
        tokenServiceUri = UriBuilder.fromUri("https://whydahdev.altrancloud.com/tokenservice/").build();
        applicationid = "15";

    }


    @Test
    public void testApplicationLoginCommandFallback() throws Exception {

        String applicationsecret = "false secret";

        String myApplicationTokenID = new CommandLogonApplication(tokenServiceUri, applicationid, applicationsecret).execute();
        // System.out.println("ApplicationTokenID=" + myApplicationTokenID);
        assertEquals("FallbackApplicationTokenID", myApplicationTokenID);

        Future<String> fAppTokenID = new CommandLogonApplication(tokenServiceUri, applicationid, applicationsecret).queue();
        assertEquals("FallbackApplicationTokenID", fAppTokenID.get());


        Observable<String> oAppTokenID = new CommandLogonApplication(tokenServiceUri, applicationid, applicationsecret).observe();
        // blocking
        assertEquals("FallbackApplicationTokenID", oAppTokenID.toBlocking().single());
    }

    @Test
    public void testApplicationLoginCommand() throws Exception {

        String applicationsecret = "33779936R6Jr47D4Hj5R6p9qT";
        String myApplicationTokenID = new CommandLogonApplication(tokenServiceUri, applicationid, applicationsecret).execute();
        // System.out.println("ApplicationTokenID=" + myApplicationTokenID);
        assertTrue(myApplicationTokenID.length() > 6);

        Future<String> fAppTokenID = new CommandLogonApplication(tokenServiceUri, applicationid, applicationsecret).queue();
        assertTrue(fAppTokenID.get().length() > 6);

        Observable<String> oAppTokenID = new CommandLogonApplication(tokenServiceUri, applicationid, applicationsecret).observe();
        // blocking
        assertTrue(oAppTokenID.toBlocking().single().length() > 6);
    }

}
