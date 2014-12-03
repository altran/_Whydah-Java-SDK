package net.whydah.sso.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

/**
 * Created by totto on 12/3/14.
 */
public class ApplicationXpathHelper {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationXpathHelper.class);


    public static  String getAppTokenIdFromAppToken(String appTokenXML) {
        //logger.trace("appTokenXML: {}", appTokenXML);
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(appTokenXML)));
            XPath xPath = XPathFactory.newInstance().newXPath();

            String expression = "/applicationtoken/params/applicationtokenID[1]";
            XPathExpression xPathExpression = xPath.compile(expression);
            String appId = xPathExpression.evaluate(doc);
            logger.debug("getAppTokenIdFromAppToken: applicationTokenId={}, appTokenXML={}", appId, appTokenXML);
            return appId;
        } catch (Exception e) {
            logger.error("getAppTokenIdFromAppToken - appTokenXML - Could not get applicationID from XML: " + appTokenXML, e);
        }
        return "";
    }

}
