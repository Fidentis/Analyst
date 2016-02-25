package cz.fidentis.merging.scene;

import java.util.HashSet;

/**
 *
 * @author matej
 */
public class Log {
    
    private static final HashSet<AbstarctLogDestination> destinations 
            = new HashSet<>();
    
    static {        
        destinations.add(new FileLogDestiantion());
    }
    
    public static void addDestination(AbstarctLogDestination dest){
        if (dest != null) {
            destinations.add(dest);
        }        
    }
    
    public static void log(String message) {
        for (AbstarctLogDestination destination : destinations) {
            destination.log(message);
        }        
    }
    
    public static void log(Exception ex) {
        for (AbstarctLogDestination destination : destinations) {
            destination.log(ex);
        }        
    }
}
