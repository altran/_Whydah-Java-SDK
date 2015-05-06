package net.whydah.sso.application;

/**
 * Created by totto on 12/2/14.
 */

public class ApplicationCredential {

    public ApplicationCredential(String applicationID, String applicationSecret) {
        this.applicationID = applicationID;
        this.applicationSecret=applicationSecret;
    }

    public ApplicationCredential(){}

    private String applicationID = "apphkjhkjhkjh";
    private String applicationSecret = "nmnmnm,n,";

    private final String templateToken = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> \n " +
            "<template>\n" +
            "    <applicationcredential>\n" +
            "        <params>\n" +
            "            <applicationID>" + getApplicationID() + "</applicationID>\n" +
            "            <applicationSecret>" + getApplicationSecret() + "</applicationSecret>\n" +
            "        </params> \n" +
            "    </applicationcredential>\n" +
            "</template>";


    public String toXML() {
        if (applicationID == null) {
            return templateToken;
        } else {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> \n " +
                    "<applicationcredential>\n" +
                    "    <params>\n" +
                    "        <applicationID>" + getApplicationID() + "</applicationID>\n" +
                    "        <applicationSecret>" + getApplicationSecret() + "</applicationSecret>\n" +
                    "    </params> \n" +
                    "</applicationcredential>\n";
        }
    }


    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    public void setApplicationSecret(String applicationSecret) {
        this.applicationSecret = applicationSecret;
    }

    public String getApplicationID() {
        return applicationID;
    }

    public String getApplicationSecret() {
        return applicationSecret;
    }

}
