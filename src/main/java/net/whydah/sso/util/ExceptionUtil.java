package net.whydah.sso.util;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Created by totto on 12/2/14.
 */
public class ExceptionUtil {


    public static  String printableUrlErrorMessage(String errorMessage, WebResource request, ClientResponse response) {
        StringBuilder sb = new StringBuilder();
        sb.append(errorMessage);
        sb.append(" Code: ");
        if(response != null) {
            sb.append(response.getStatus());
            sb.append(" URL: ");
        }
        if(request != null) {
            sb.append(request.toString());
        }
        return sb.toString();
    }

}
