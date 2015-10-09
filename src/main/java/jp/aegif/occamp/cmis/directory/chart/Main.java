package jp.aegif.occamp.cmis.directory.chart;

import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.log4j.Logger;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args)
    {
        Map<String, String> parameter = new HashMap<String, String>();

        // user credentials
        parameter.put(SessionParameter.USER, "admin");
        parameter.put(SessionParameter.PASSWORD, "admin");

        // session locale
        parameter.put(SessionParameter.LOCALE_ISO3166_COUNTRY, "");
        parameter.put(SessionParameter.LOCALE_ISO639_LANGUAGE, "");

        // repository url
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
        parameter.put(SessionParameter.ATOMPUB_URL,
                "http://localhost:8080/alfresco/api/-default-/public/cmis/versions/1.1/atom");
        parameter.put(SessionParameter.REPOSITORY_ID, "-default-");

        // create session
        SessionFactory f = SessionFactoryImpl.newInstance();
        Session session = f.createSession(parameter);
        OperationContext operationContext =
                session.createOperationContext(null, true, true, false, IncludeRelationships.BOTH,
                        null, false, null, true, 100);
        session.setDefaultContext(operationContext);

        ItemIterable<QueryResult> rootFolder =
                session.query("SELECT * FROM cmis:folder where cmis:name = 'Company Home'", false);
        String rootObjectId = "";
        String cmisPath = "";

        for (QueryResult hit : rootFolder) {
            for (PropertyData<?> property : hit.getProperties()) {

                String queryName = property.getQueryName();
                Object value = property.getFirstValue();

                System.out.println(queryName + ": " + value);

                if (queryName.equals("cmis:objectId")) {
                    rootObjectId = (String) value;
                }
                if (queryName.equals("cmis:path")) {
                    cmisPath = (String) value;
                }
            }
            logger.debug("-----------------------------------------");
        }

        Results rootResults = new Results(cmisPath, rootObjectId);
        CmisQueryHandler cmisQueryHandler = new CmisQueryHandler();

        rootResults = cmisQueryHandler.getDirectChildren(rootResults, session);
        rootResults.rootDebug();
        rootResults.calculate();
        String calcResult = rootResults.getCalcResult(new StringBuilder());
        logger.debug(calcResult);
    }
}
