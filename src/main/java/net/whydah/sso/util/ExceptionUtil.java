package net.whydah.sso.util;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * Created by totto on 12/2/14.
 */
public class ExceptionUtil {


    public static  String printableUrlErrorMessage(String errorMessage, WebTarget request, Response response) {
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
