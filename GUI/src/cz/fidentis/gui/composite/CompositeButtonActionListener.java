/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.composite;

import cz.fidentis.composite.ModelInfo;
import cz.fidentis.composite.FacePartType;
import cz.fidentis.controller.Composite;
import cz.fidentis.gui.GUIController;
import cz.fidentis.randomFace.RandomFace;
import cz.fidentis.renderer.CompositeGLEventListener;
import cz.fidentis.utils.MeshUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.vecmath.Vector3f;

/**
 *
 * @author Katka
 */
public class CompositeButtonActionListener implements ActionListener {

    
    private CompositeGLEventListener listener;
    private File modelPath;
    private File modelPath2;
    private ModelInfo info;
    private ModelInfo info2;
    private FacePartType type;
    private Composite composite;
    private Object index;

    public void setListener(CompositeGLEventListener listener) {
        this.listener = listener;
    }


    public void setComposite(Composite composite) {
        this.composite = composite;
    }

    public void setModel(ModelInfo model) {
       // String path = "C:" + separatorChar + "HCI" + separatorChar + "Fidentis GUI" + separatorChar + "GUI";
      //  File directory = new File(".");
     //   try {
            if (modelPath == null) {
           //     System.out.println(directory.getCanonicalPath());
                modelPath = model.getFile() ; //path + separatorChar + "src" + separatorChar + "cz" + separatorChar + "fidentis" + separatorChar + "gui" + separatorChar + "models" + separatorChar + name;
                info = model;

            } else {
                modelPath2 = model.getFile(); //path + separatorChar + "src" + separatorChar + "cz" + separatorChar + "fidentis" + separatorChar + "gui" + separatorChar + "models" + separatorChar + name;
                info2 = model;
            }
       // } catch (IOException ex) {
      //      Exceptions.printStackTrace(ex);
      //  }




        // System.out.println(name);
        //System.out.println(modelPath);
    }

    public void setType(FacePartType type) {
        this.type = type;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Vector3f position = new Vector3f();
        Vector3f position2= new Vector3f();//needed only in case of pair models, e.g. eyebrows, eyes...
        Vector3f shift;
        Vector3f shift2= new Vector3f();//needed only in case of pair models, e.g. eyebrows, eyes...
        
        //get the position of point on selected model for automatic placement
        if(info.getPosition()!= null){
            shift = new Vector3f(MeshUtils.instance().computeCentroid(info.getPosition()));
          if(info2!= null && info2.getPosition()!= null){
            shift2 = new Vector3f(MeshUtils.instance().computeCentroid(info2.getPosition()));
          }
        }
        else{
            shift = new Vector3f();
            shift2 = new Vector3f();
        }
        GUIController.getConfigurationTopComponent().getCompositConfigurationPanel().setIsEditing(true);
        composite.logAction("Added " + type + " " + index);
        FacePartType type1 = type;
        FacePartType type2 = type;
        
        //get the custom adjustment of model position -  position of new model is calculated 
        //as 'automatic placement position' + 'custom adjustement made by user'
        //save the index of selected model
        switch (type) {
            case EYES:
                type1 = FacePartType.RIGHT_EYE;
                type2 = FacePartType.LEFT_EYE;
                position = new Vector3f(composite.getRighteyePosition());
                position2 = new Vector3f(composite.getLefteyePosition());
                composite.setSelectedEyes(index);
                break;
            case EYEBROWS:
                type1 = FacePartType.RIGHT_EYEBROW;
                type2 = FacePartType.LEFT_EYEBROW;
                position =new Vector3f(composite.getRighteyebrowPosition());
                position2 =new Vector3f(composite.getLefteyebrowPosition());
                composite.setSelectedEyebrows(index);
                break;
            case EARS:
                type1 = FacePartType.RIGHT_EAR;
                type2 = FacePartType.LEFT_EAR;
                position=new Vector3f(composite.getRightearPosition());
                position2=new Vector3f(composite.getLeftearPosition());
                composite.setSelectedEars(index);
                break;
            case HEAD:
                composite.setSelectedHead(index);
                break;
            case FORHEAD:
                composite.setSelectedForhead(index);
                position=new Vector3f(composite.getForeheadPosition());
                break;
            case NOSE:
                composite.setSelectedNose(index);
                position=new Vector3f(composite.getNosePosition());
                break;
            case MOUTH:
                composite.setSelectedMouth(index);
                position=new Vector3f(composite.getMouthPosition());                
                break;
            case CHIN:
                composite.setSelectedChin(index);
                position=new Vector3f(composite.getChinPosition());
                break;
        }
        
        //add or replace the model of given type with selected model (i.e. model bound to this button)
        composite.addModel(modelPath, type1, shift, position);
        if (modelPath2 != null) {
            composite.addModel(modelPath2, type2, shift2, position2);
        }

        //in case the model is of type HEAD, set the positions of points for automatic placement on head
        //(the points for automatic placement on head are paired with points on corresponding facial part models to calculate the placement) 
        if(info.getPart()!= null && info.getPart().equals("head")){
            composite.setChinPosition(new Vector3f(MeshUtils.instance().computeCentroid(info.getChinPosition())));
            composite.setForeheadPosition(new Vector3f(MeshUtils.instance().computeCentroid(info.getForeheadPosition())));
            composite.setMouthPosition(new Vector3f(MeshUtils.instance().computeCentroid(info.getMouthPosition())));
            composite.setNosePosition(new Vector3f(MeshUtils.instance().computeCentroid(info.getNosePosition())));
            composite.setRightearPosition(new Vector3f(MeshUtils.instance().computeCentroid(info.getRightearPosition())));
            composite.setRighteyePosition(new Vector3f(MeshUtils.instance().computeCentroid(info.getRighteyePosition())));
            composite.setLefteyePosition(new Vector3f(MeshUtils.instance().computeCentroid(info.getLefteyePosition())));
            composite.setLeftearPosition(new Vector3f(MeshUtils.instance().computeCentroid(info.getLeftearPosition())));
            composite.setLefteyebrowPosition(new Vector3f(MeshUtils.instance().computeCentroid(info.getLefteyebrowPosition())));
            composite.setRighteyebrowPosition(new Vector3f(MeshUtils.instance().computeCentroid(info.getRighteyebrowPosition())));
        }
        
        //add picked part to current composite in RandomFace class
        RandomFace.instance().addPart(type1, info);
        
        if(RandomFace.instance().isPair(type)){
            RandomFace.instance().addPart(type2, info2);
        }
        
        //update models in GLEvaneListener
        listener.updateModelList();

        //set the parameters in configuration window
        CompositeConfiguration compositeConfiguratin = GUIController.getConfigurationTopComponent().getCompositConfigurationPanel();
        compositeConfiguratin.setComposite(composite);
        compositeConfiguratin.setParameters();

        
        //update selected model and gizmo position in GLEventListener
        listener.updateSelectedModel();
        listener.getManipulator().shiftManipulators(GUIController.getConfigurationTopComponent().getCompositConfigurationPanel().getPosition());

        
    }

    public void setButtonIndex(Object i) {
        index = i;
    }
}
