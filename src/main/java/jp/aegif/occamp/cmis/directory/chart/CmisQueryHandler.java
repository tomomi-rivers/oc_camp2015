package jp.aegif.occamp.cmis.directory.chart;

import java.math.BigInteger;

import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.log4j.Logger;

public class CmisQueryHandler {

    private static final Logger logger = Logger.getLogger(CmisQueryHandler.class);

    public Results getDirectChildren(Results results, Session session)
    {
        ItemIterable<QueryResult> folderQueryResults =
                session.query(
                        "SELECT * FROM cmis:folder WHERE IN_FOLDER('"
                                + results.getFolderObjectId()
                                + "')", false);

        for (QueryResult hit : folderQueryResults) {

            Results childFolderResults = new Results();
            for (PropertyData<?> property : hit.getProperties()) {

                String queryName = property.getQueryName();
                Object value = property.getFirstValue();

                if (queryName.equals("cmis:path")) {
                    childFolderResults.setPath((String) value);
                }
                if (queryName.equals("cmis:objectId")) {
                    childFolderResults.setFolderObjectId((String) value);
                }
            }
            results.addTochildFolders(childFolderResults.getPath(), childFolderResults);
            logger.debug("-----------------------------------------");
        }

        ItemIterable<QueryResult> documentQueryResults =
                session.query(
                        "SELECT * FROM cmis:document WHERE IN_FOLDER('"
                                + results.getFolderObjectId()
                                + "')", false);

        for (QueryResult hit : documentQueryResults) {
            for (PropertyData<?> property : hit.getProperties()) {

                String queryName = property.getQueryName();
                Object value = property.getFirstValue();

                if (queryName.equals("cmis:contentStreamLength") && value != null) {
                    results.addContentStreamLengths((BigInteger) value);
                }
            }
            logger.debug("-----------------------------------------");
        }

        //Now continue with the child folders
        for (Results childFolderResults : results.getChildFolders().values()) {
            getDirectChildren(childFolderResults, session);
            childFolderResults.debug();
        }

        return results;
    }

}
