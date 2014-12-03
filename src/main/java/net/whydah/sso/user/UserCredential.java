package net.whydah.sso.user;

/**
 * Created by totto on 12/2/14.
 */
public class UserCredential {
    private String userName;
    private String password;

    String templateToken = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> \n " +
            "    <usercredential>\n" +
            "        <params>\n" +
            "            <username>" + getUserName() + "</username>\n" +
            "            <password>" + getPassword() + "</password>\n" +
            "        </params> \n" +
            "    </usercredential>\n";

    public UserCredential(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String toXML() {
        if (userName == null) {
            return templateToken;
        } else {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> \n " +
                    "<usercredential>\n" +
                    "    <params>\n" +
                    "        <username>" + getUserName() + "</username>\n" +
                    "        <password>" + getPassword() + "</password>\n" +
                    "    </params> \n" +
                    "</usercredential>\n";
        }
    }


    public String toString() {
        return "UserNameAndPasswordCredential{" + "userName='" + userName + '}';
    }


}
