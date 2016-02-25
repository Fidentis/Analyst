/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.merging.scene;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author matej
 */
public class FileLogDestiantion extends AbstarctLogDestination{

    private PrintWriter writer;
    private File writerFile;
    
    public FileLogDestiantion() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(Calendar.getInstance().getTime());
        sdf = new SimpleDateFormat("hh:mm:ss");
        String time = sdf.format(Calendar.getInstance().getTime());
        File base = new File("./Logs");
        if (!base.exists()) {
            base.mkdir();
        }
        File dotaysDir = new File(base,date);
        if (!dotaysDir.exists()) {
            dotaysDir.mkdir();
        }
        
        try {
            writerFile = new File(dotaysDir, time + ".log");
            writer = new PrintWriter(writerFile, "utf-8");
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(FileLogDestiantion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    @Override
    public void log(String message) {
        writer.println(calendar.getTime());
        writer.println(message);
        writer.flush();
    }

    @Override
    public void log(Exception exception) {
        writer.println(calendar.getTime());
        exception.printStackTrace(writer);
        writer.flush();
    }
    
    
}
