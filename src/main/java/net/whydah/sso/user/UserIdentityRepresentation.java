package net.whydah.sso.user;

import java.io.Serializable;

/**
 * @author totto
 */
public class UserIdentityRepresentation implements Serializable {
    private static final long serialVersionUID = 20;
    protected String username;
    protected String firstName;
    protected String lastName;
    protected String personRef;
    protected String email;
    protected String cellPhone;

    public UserIdentityRepresentation(String username, String firstName, String lastName, String personRef, String email, String cellPhone) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.personRef = personRef;
        this.email = email;
        this.cellPhone = cellPhone;
    }

    public String getPersonName() {
        return firstName + ' ' + lastName;
    }
    public String getPersonRef() {
        return personRef;
    }
    public String getUsername() {
        return username;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getEmail() {
        return email;
    }
    public String getCellPhone() {
        return cellPhone;
    }


    public void setPersonRef(String personRef) {
        this.personRef = personRef;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String toXML() {
        StringBuilder strb = new StringBuilder();
        String headAndIdentity = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<whydahuser>\n" +
                "    <identity>\n" +
                "        <username>" + getUsername() + "</username>\n" +
                "        <cellPhone>" + (getCellPhone() != null ? getCellPhone() : "") + "</cellPhone>\n" +
                "        <email>" + getEmail() + "</email>\n" +
                "        <firstname>" + getFirstName() + "</firstname>\n" +
                "        <lastname>" + getLastName() + "</lastname>\n" +
                "        <personRef>" + (getPersonRef() != null ? getPersonRef() : "") + "</personRef>\n" +
                "    </identity>\n";
        strb.append(headAndIdentity);

        strb.append(
                        "</whydahuser>"
        );
        return strb.toString();
    }
}
