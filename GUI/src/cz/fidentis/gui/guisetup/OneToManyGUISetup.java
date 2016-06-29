/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.guisetup;

import cz.fidentis.comparison.ComparisonMethod;
import cz.fidentis.comparison.RegistrationMethod;
import java.awt.Color;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;

/**
 *
 * @author xferkova
 */
public class OneToManyGUISetup {
    
   //registration configuration
   private static final RegistrationMethod SELECTED_REGISTRATION = RegistrationMethod.PROCRUSTES; 
    
   private static final boolean FP_SCALE = false;
   private static final int FP_THRESHOLD = 30;
   private static final boolean SHOW_FP_INFO = true;
   private static final Color FP_COLOR = new Color(255,19,27);
   private static final int FP_SIZE = 20;
   
   private static final int ICP_METRIC = 0;
   private static final boolean ICP_SCALE = false;
   private static final boolean SYMMETRIC_MODELS = false;
   private static final float ERROR_VALUE = 0.05f;
   private static final int MAX_ITERATION = 15;
   private static final int UNDERSAMPLING_INDEX = 0;
   private static final int PERCENTAGE_VALUE = 50;
   private static final int NUMBER_SPINNER = 0;
   private static final int UNDERSAMPLING_RADIUS = 50;
   private static final boolean CONTINUE_COMPARISON = false;
    
   //comparison configuration
   private static final ComparisonMethod COMPARISON_METHOD = ComparisonMethod.HAUSDORFF_DIST;
   private static final boolean CREATE_AVG_FACE = false;
   
   //comparison results
   private static final int SELECTED_METRIC = 0;
   private static final float X_PLANE_POSITION = 0f;
   private static final float Y_PLANE_POSITION = 0f;
   private static final float Z_PLANE_POSITION = 0f;
   private static final int CROSSCUT_SIZE = 50;
   private static final int CROSSCUT_THICKNESS = 50;
   private static final Color CROSSCUT_COLOR = new Color(255,255,255);
   private static final boolean HIGHLIGHT_CUTS = true;
   private static final boolean CROSSCUT_VECTORS = true;
   private static final boolean ALL_CUTS = false;
   private static final boolean SAMPLINGR_RAYS = false;
   
   private static final int VISUALIZATION = 0;
   private static final int VALUES_TYPE = 0;
   private static final int MAX_THRESHOLD = 100;
   private static final int MIN_THRESHOLD = 0;
   private static final int COLOR_SCHEME = 0;
   
   private static final int VECTOR_DENSITY = 10;
   private static final int CYLINDER_LENGTH = 1;
   private static final int CYLINDER_RADIUS = 1;
   
   private static final int FP_DISTANCE = 0;

    
    //is same as in 1:1, except registration is not enum, need to change that
    public static void defaultValuesRegistration(JComboBox selectedRegistration, JCheckBox fpScale, JSlider fpThreshold, JCheckBox showFpInfo, JPanel fpColor, JSlider fpSize,
           JComboBox icpMetric, JCheckBox icpScale, JCheckBox symModels, JSpinner icpError, JSpinner maxIteration, JComboBox icpUndersampling, 
           JSpinner undersamplingPercentage, JSpinner undersamplingNumber, JRadioButton selectedUndersampling, JSlider undersamplingRadius,
           JCheckBox continueComparison){
         
       selectedRegistration.setSelectedItem(SELECTED_REGISTRATION);
       
       //FP
       fpScale.setSelected(FP_SCALE);
       fpThreshold.setValue(FP_THRESHOLD);
       showFpInfo.setSelected(SHOW_FP_INFO);
       fpColor.setBackground(FP_COLOR);
       fpSize.setValue(FP_SIZE);
       
       //ICP
       icpMetric.setSelectedIndex(ICP_METRIC);
       icpScale.setSelected(ICP_SCALE);
       symModels.setSelected(SYMMETRIC_MODELS);
       icpError.setValue(ERROR_VALUE);
       maxIteration.setValue(MAX_ITERATION);
       icpUndersampling.setSelectedIndex(UNDERSAMPLING_INDEX);
       undersamplingPercentage.setValue(PERCENTAGE_VALUE);
       undersamplingNumber.setValue(NUMBER_SPINNER);
       selectedUndersampling.setSelected(true);
       undersamplingRadius.setValue(UNDERSAMPLING_RADIUS);
       
       continueComparison.setSelected(CONTINUE_COMPARISON);
         
     }
     
     public static void defaultValuesComparisonConfiguration(JComboBox comparisonMethod, JCheckBox createAvgFace, JCheckBox fpScaling, JSlider fpThrehsold){
         
         comparisonMethod.setSelectedItem(COMPARISON_METHOD);
         createAvgFace.setSelected(CREATE_AVG_FACE);
         fpScaling.setSelected(FP_SCALE);
         fpThrehsold.setValue(FP_THRESHOLD);
     }
     
     public static void defaultValuesComparisonResult(JComboBox comparisonMetric, JComboBox visualization, JComboBox values, 
           JRadioButton selectedPlane, JSpinner planeXposition, JSpinner planeYposition, JSpinner planeZposition, JSlider cutSize, JSlider cutThickness, 
           JPanel vectorColor, JCheckBox highlightCuts, JCheckBox showVectors, JCheckBox allCuts, JCheckBox samplingRays,
           JSlider maxThresh, JSpinner maxThrehsSpinner,
           JSlider minThresh, JSpinner minThreshSpinner, JComboBox colorScheme, JSlider vectorRadius, JSlider vectorLength, JSlider cylinderRadius,
           JSlider fpDistance, JSlider fpSize){
         
       visualization.setSelectedIndex(VISUALIZATION);
       values.setSelectedItem(VALUES_TYPE);
       
       maxThresh.setValue(MAX_THRESHOLD);
       maxThrehsSpinner.setValue(MAX_THRESHOLD);
       minThresh.setValue(MIN_THRESHOLD);
       minThreshSpinner.setValue(MIN_THRESHOLD);
       
       colorScheme.setSelectedIndex(COLOR_SCHEME);
       vectorRadius.setValue(VECTOR_DENSITY);
       vectorLength.setValue(CYLINDER_LENGTH);
       cylinderRadius.setValue(CYLINDER_RADIUS);
       
       fpDistance.setValue(FP_DISTANCE);
       fpSize.setValue(FP_SIZE);
       
       comparisonMetric.setSelectedIndex(SELECTED_METRIC);
       selectedPlane.setSelected(true);
       planeXposition.setValue(X_PLANE_POSITION);
       planeYposition.setValue(Y_PLANE_POSITION);
       planeZposition.setValue(Z_PLANE_POSITION);
       cutSize.setValue(CROSSCUT_SIZE);
       cutThickness.setValue(CROSSCUT_THICKNESS);
       vectorColor.setBackground(CROSSCUT_COLOR);
       highlightCuts.setSelected(HIGHLIGHT_CUTS);
       showVectors.setSelected(CROSSCUT_VECTORS);
       allCuts.setSelected(ALL_CUTS);
       samplingRays.setSelected(SAMPLINGR_RAYS);
         
     }
}
