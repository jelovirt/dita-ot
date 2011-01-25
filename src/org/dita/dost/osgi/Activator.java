package org.dita.dost.osgi;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
//import org.osgi.util.tracker.ServiceTracker;
//import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
//import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
//import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Plugin;


public class Activator extends Plugin {
//public class Activator implements BundleActivator {
    
    private static final String XSL_STRINGS_ID = "dita.xsl.strings";
    private static final String TEMPLATE_ID= "dita.template";
    private static final String XSL_MESSAGES_ID = "dita.xsl.messages";
    private static final String XSL_PARAM_ID = "dita.xsl.param";
    private static final String ANT_CONDUCTOR_ID = "dita.ant.conductor";
    private static final String ANT_DEPENDENCY_ID = "dita.ant.dependency";
    private static final String XSL_IMPORT_ID = "dita.xsl.import";
    private static final String CATALOG_ID = "dita.catalog";
    private static final String TRANSTYPE_ID = "dita.transtype";
    
    private static final String ID_ATTR = "id";
    private static final String FILE_ATTR = "file";
    
    private static BundleContext context;
//    private ServiceTracker serviceTracker;
    private File baseDir;
    
    static BundleContext getContext() {
        return context;
    }
    
    @Override
    public void start(BundleContext bundleContext) throws Exception {
//        System.out.println("Starting "  + getClass().getName() + " bundles xxx");
        Activator.context = bundleContext;
//        MyQuoteServiceTrackerCustomizer customer = new MyQuoteServiceTrackerCustomizer(context);  
//        serviceTracker = new ServiceTracker(context, IQuoteService.class.getName(), customer);  
//        serviceTracker.open();  
        getTranstypes();
        getCatalogs();
        getConductor();
//        System.out.println("ConfigurationLocation: " + Platform.getConfigurationLocation());
//        System.out.println("UserLocation: " + Platform.getUserLocation());
//        System.out.println("bundle location: " + context.getBundle().getLocation());
        baseDir = Platform.getStateLocation(bundleContext.getBundle()).toFile();
        System.out.println("StateLocation: " + baseDir);
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
//        System.out.println("Stopping "  + getClass().getName() + " bundles xxx");        
        Activator.context = null;
//        serviceTracker.close();
    }

    private void getTranstypes() {
        final IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(TRANSTYPE_ID);
        for (final IConfigurationElement e : config) {
            final String id = e.getAttribute(ID_ATTR);
            System.out.println("Transtype " + id);
        }
    }
    
    private void getCatalogs() {
        final IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(CATALOG_ID);
        for (final IConfigurationElement e : config) {
            final String file = e.getAttribute(FILE_ATTR);
            final File f = context.getDataFile(file);
            System.out.println("Catalog " + f.getAbsolutePath());
        }
    }
    
    private void getConductor() {
        final List<File> conductors = new ArrayList<File>();
        final IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(ANT_CONDUCTOR_ID);
        for (final IConfigurationElement e : config) {
            final String file = e.getAttribute(FILE_ATTR);
            System.out.println("Conductor file: " + file);
            
            final Bundle b = Platform.getBundle(e.getContributor().getName());
            URL bundleRoot = b.getEntry("/" + file);  
            try {
                URI fileURL = FileLocator.toFileURL(bundleRoot).toURI();
                System.out.println("Contributor via URI: " + fileURL);
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (URISyntaxException e2) {
                e2.printStackTrace();
            }
            //System.out.println("Contributor: " + b.getResource("/" + file));
            
            final File f = context.getDataFile(file);
            System.out.println("Conductor " + f.getAbsolutePath());
        }
        //final File build = new File(baseDir, "build.xml");
    }

}
