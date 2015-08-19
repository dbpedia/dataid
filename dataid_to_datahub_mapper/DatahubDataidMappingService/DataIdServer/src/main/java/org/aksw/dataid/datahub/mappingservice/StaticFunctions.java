package org.aksw.dataid.datahub.mappingservice;

/**
 * Created by Chile on 8/17/2015.
 */
public class StaticFunctions {

    public static String getBasePath()
    {
        String f = StaticFunctions.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        return f.substring(1, f.indexOf("target"));
    }
}
