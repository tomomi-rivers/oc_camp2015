package jp.aegif.occamp.cmis.directory.chart;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public class Results {

    private static final Logger logger = Logger.getLogger(Results.class);

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public Map<String, Results> getChildFolders()
    {
        return childFolders;
    }

    public void setChildFolders(Map<String, Results> childFolders)
    {
        this.childFolders = childFolders;
    }

    public List<BigInteger> getContentStreamLengths()
    {
        return contentStreamLengths;
    }

    public void setContentStreamLengths(List<BigInteger> contentStreamLengths)
    {
        this.contentStreamLengths = contentStreamLengths;
    }

    public String getFolderObjectId()
    {
        return folderObjectId;
    }

    public void setFolderObjectId(String folderObjectId)
    {
        this.folderObjectId = folderObjectId;
    }

    private String path;
    private String folderObjectId;
    private Map<String, Results> childFolders = new HashMap<String, Results>();
    private List<BigInteger> contentStreamLengths = new ArrayList<BigInteger>();
    private BigInteger contentStreamSum = BigInteger.valueOf(0);

    public Results(String path)
    {
        this.path = path;
    }

    public Results(String path, String folderObjectId)
    {
        this.path = path;
        this.folderObjectId = folderObjectId;
    }

    public Results()
    {
    }

    public void addContentStreamLengths(BigInteger contentStreamLength)
    {
        this.contentStreamLengths.add(contentStreamLength);
    }

    public void addTochildFolders(String path, Results results)
    {
        this.childFolders.put(path, results);
    }

    public void debug()
    {
        logger.debug("***size of childFolders of" + path + ":" + this.childFolders.size());
        logger.debug("***length of contentStreamLength"
                + path
                + ":"
                + this.contentStreamLengths.size());

    }

    public void rootDebug()
    {
        logger.debug("cmis:path=" + path);
        for (Results folder : childFolders.values()) {
            folder.debug();
            folder.rootDebug();
        }
    }

    public void calculate()
    {
        for (BigInteger contentStreamLength : contentStreamLengths) {
            this.contentStreamSum = this.contentStreamSum.add(contentStreamLength);
        }
        for (Results folder : childFolders.values()) {
            folder.calculate();
        }
        logger.debug(path + ", sum of folder size(byte):" + this.contentStreamSum);
    }

    public String getCalcResult(StringBuilder sb)
    {
        sb.append("CMISPATH:" + path + ", size of direct child document(byte):" + this.contentStreamSum + "TEMPSEPARATOR");
        for (Results folder : childFolders.values()) {
            folder.getCalcResult(sb);
        }
        return sb.toString();
    }
}
