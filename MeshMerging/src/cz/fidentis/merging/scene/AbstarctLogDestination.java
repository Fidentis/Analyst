package cz.fidentis.merging.scene;

import java.util.Calendar;

/**
 *
 * @author matej
 */
public abstract class AbstarctLogDestination {
    protected Calendar calendar = Calendar.getInstance();
    
    public abstract void log(String message);    
    
    public abstract void log(Exception exception);
}
