package net.whydah.sso.application;

/**
 * Created by totto on 12/3/14.
 */
public class ApplicationHelper {
        public static String getDummyApplicationToken() {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> \n " +

                    "    <applicationtoken>\n" +
                    "        <params>\n" +
                    "            <applicationtokenID>47289347982137421</applicationtokenID>\n" +
                    "            <applicationid>45</applicationid>\n" +
                    "            <applicationname>dummyapp</applicationname>\n" +
                    "            <expires>3453453</expires>\n" +
                    "        </params> \n" +
                    "           <Url type=\"application/xml\"" +
                    "                template=\"http://example.com/user/47289347982137421/get_usertoken_by_usertokenid\"/>" +
                    "    </applicationtoken>\n";
        }
}
