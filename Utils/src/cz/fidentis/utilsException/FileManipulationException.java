/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fidentis.utilsException;

/**
 * Exception throw while creating/deleting or manipulation with files is mishandled.
 * 
 * @author Zuzana Ferkova
 */
public class FileManipulationException extends Exception{
    
    public FileManipulationException(){
        super();
    }
        
  public FileManipulationException(String message){
      super(message);
  }
  
  
  public FileManipulationException(String message, Throwable cause){
      super(message, cause);
  }
  public FileManipulationException(Throwable cause){
      super(cause); 
  }
    
}
