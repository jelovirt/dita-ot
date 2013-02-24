/*
 * This file is part of the DITA Open Toolkit project hosted on
 * Sourceforge.net. See the accompanying license.txt file for
 * applicable licenses.
 */

/*
 * (c) Copyright IBM Corp. 2005 All Rights Reserved.
 */
package org.dita.dost.log;

import static org.dita.dost.log.MessageBean.*;

import java.util.ArrayList;
import java.util.Properties;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Echo;
import org.dita.dost.invoker.ExtensibleAntInvoker.Param;
import org.dita.dost.log.DITAOTLogger;
import org.dita.dost.log.DITAOTAntLogger;

/**
 * Ant echo task for custom error message.
 * 
 * @author Wu, Zhi Qiang
 */
public final class DITAOTEchoTask extends Echo {
    private String id = null;

    private final Properties prop = new Properties();
    /** Nested params. */
    private final ArrayList<Param> params = new ArrayList<Param>();
    private DITAOTLogger logger;
    
    /**
     * Default Construtor.
     *
     */
    public DITAOTEchoTask(){
    }
    /**
     * Setter function for id.
     * @param identifier The id to set.
     */
    public void setId(final String identifier) {
        id = identifier;
    }

    /**
     * Handle nested parameters. Add the key/value to the pipeline hash, unless
     * the "if" attribute is set and refers to a unset property.
     * @return parameter
     */
    public Param createParam() {
        final Param p = new Param();
        params.add(p);
        return p;
    }
    
    /**
     * Task execute point.
     * @throws BuildException exception
     * @see org.apache.tools.ant.taskdefs.Echo#execute()
     */
    @Override
    public void execute() throws BuildException {
        for (final Param p : params) {
            if (!p.isValid()) {
                throw new BuildException("Incomplete parameter");
            }
            final String ifProperty = p.getIf();
            final String unlessProperty = p.getUnless();
            if ((ifProperty == null || getProject().getProperties().containsKey(ifProperty))
                    && (unlessProperty == null || !getProject().getProperties().containsKey(unlessProperty))) {
                prop.put("%" + p.getName(), p.getValue());
            }
        }
        
        logger = new DITAOTAntLogger(getProject());
        final MessageBean msgBean = MessageUtils.getInstance().getMessage(id, prop);
        if (msgBean != null) {
            final String type = msgBean.getType();
            if(FATAL.equals(type)){
                logger.logFatal(msgBean.toString());
            } else if(ERROR.equals(type)){
                logger.logError(msgBean.toString());
            } else if(WARN.equals(type)){
                logger.logWarn(msgBean.toString());
            } else if(INFO.equals(type)){
                logger.logInfo(msgBean.toString());
            } else if(DEBUG.equals(type)){
                logger.logDebug(msgBean.toString());
            }
        }
    }

}
