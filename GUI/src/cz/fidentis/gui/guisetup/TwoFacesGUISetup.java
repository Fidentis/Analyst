/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.gui.guisetup;

import cz.fidentis.comparison.ComparisonMethod;
import cz.fidentis.comparison.ICPmetric;
import cz.fidentis.comparison.RegistrationMethod;
import cz.fidentis.controller.Comparison2Faces;
import cz.fidentis.visualisation.surfaceComparison.VisualizationType;
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
public class TwoFacesGUISetup {
    //default values
    
   //registration configuration
   private static final int SELECTED_REGISTRATION = 0; 
    
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
   private static final boolean FP_DATABASE = false;
   
   private static final Color PRIMARY_MODEL = new Color(51,153,255);
   private static final boolean SOLID_PRIMARY = false;
   private static final Color SECONDARY_MODEL = new Color(255,255,0);
   private static final boolean SOLDI_SECONDARY = false;
   private static final Color FOG_COLOR = new Color(255,102,204);
   private static final int OVERLAY_TRANSPARENCY = 100;
   private static final boolean INNER_SURFACE_SOLID = true;
   private static final boolean USE_GLYPHS = false;
   private static final boolean USE_COUNTOURS = true;
   private static final int FOG_VERSION = 0;
   
   //comparison results
   private static final int VISUALIZATION = 0;
   private static final int VALUES_TYPE = 0;
   private static final int MAX_THRESHOLD = 100;
   private static final int MIN_THRESHOLD = 0;
   private static final int COLOR_SCHEME = 0;
   
   private static final int VECTOR_DENSITY = 10;
   private static final int CYLINDER_LENGTH = 1;
   private static final int CYLINDER_RADIUS = 1;
   
   private static final int FP_DISTANCE = 0;
   
   //set data for registration from data model
   public static void setUpValuesRegistration(JComboBox selectedRegistration, JCheckBox fpScale, JSlider fpThreshold, JCheckBox showFpInfo, JPanel fpColor, JSlider fpSize,
           JComboBox icpMetric, JCheckBox icpScale, JCheckBox symModels, JSpinner icpError, JSpinner maxIteration, JComboBox icpUndersampling, 
           JSpinner undersamplingPercentage, JSpinner undersamplingNumber, JRadioButton numberUndersampling, JRadioButton percentageUndersampling,  JSlider undersamplingRadius,
           JCheckBox continueComparison, Comparison2Faces data){
       
       selectedRegistration.setSelectedIndex(data.getRegistrationMethod().ordinal());
       
       //FP
       fpScale.setSelected(data.isFpScaling());
       fpThreshold.setValue(data.getFpTreshold());
       showFpInfo.setSelected(data.isShowPointInfo());
       fpColor.setBackground(data.getPointColor());
       fpSize.setValue(data.getFpSize());
       
       //ICP
       icpMetric.setSelectedIndex(data.getIcpMetric().ordinal());
       icpScale.setSelected(data.getScaleEnabled());
       symModels.setSelected(data.isUseSymmetry());
       icpError.setValue(data.getICPerrorRate());
       maxIteration.setValue(data.getICPmaxIteration());
       icpUndersampling.setSelectedIndex(data.getMethod());
       undersamplingPercentage.setValue(data.getValue());
       undersamplingNumber.setValue(data.getValue());
       
       //SELECT CORRECT JRADIOBUTTON
       //selectedUndersampling.setSelected(true);
       undersamplingRadius.setValue(UNDERSAMPLING_RADIUS);
       
       continueComparison.setSelected(data.isContinueComparison());
       
   }
   
   //sets default registration values to data model
   public static void setUpDefaultRegistrationData(Comparison2Faces data){
       data.setRegistrationMethod(RegistrationMethod.values()[SELECTED_REGISTRATION]);
       
       //FP
       data.setFpScaling(FP_SCALE);
       data.setFpTreshold(FP_THRESHOLD);
       data.setShowPointInfo(SHOW_FP_INFO);
       data.setPointColor(FP_COLOR);
       data.setFpSize(FP_SIZE);
       
       //ICP
       data.setIcpMetric(ICPmetric.values()[ICP_METRIC]);
       data.setScaleEnabled(ICP_SCALE);
       data.setUseSymmetry(SYMMETRIC_MODELS);
       data.setICPerrorRate(ERROR_VALUE);
       data.setICPmaxIteration(MAX_ITERATION);
       data.setMethod(UNDERSAMPLING_INDEX);
       data.setType(UNDERSAMPLING_INDEX);
       data.setValue(PERCENTAGE_VALUE);
       
       data.setContinueComparison(CONTINUE_COMPARISON);
       data.setFirstCreated(false);       
   }
   
   //set up used data to data model
   public static void setUpUsedRegistrationData(JComboBox selectedRegistration, JCheckBox fpScale, JSlider fpThreshold, JCheckBox showFpInfo, JPanel fpColor, JSlider fpSize,
           JComboBox icpMetric, JCheckBox icpScale, JCheckBox symModels, JSpinner icpError, JSpinner maxIteration, JComboBox icpUndersampling, 
           JSpinner undersamplingPercentage, JSpinner undersamplingNumber, JRadioButton numberUndersampling, JRadioButton percentageUndersampling,  JSlider undersamplingRadius,
           JCheckBox continueComparison, Comparison2Faces data){
       
       data.setRegistrationMethod(RegistrationMethod.values()[selectedRegistration.getSelectedIndex()]);
       
       //FP
       data.setFpScaling(fpScale.isSelected());
       data.setFpTreshold(fpThreshold.getValue());
       data.setShowPointInfo(showFpInfo.isSelected());
       data.setPointColor(fpColor.getBackground());
       data.setFpSize(fpSize.getValue());
       
       //ICP
       data.setIcpMetric(ICPmetric.values()[icpMetric.getSelectedIndex()]);
       data.setScaleEnabled(icpScale.isSelected());
       data.setUseSymmetry(symModels.isSelected());
       data.setICPerrorRate((float) icpError.getValue());
       data.setICPmaxIteration((int) maxIteration.getValue());
       data.setMethod(icpUndersampling.getSelectedIndex());
       data.setType(percentageUndersampling.isSelected() ? 1 : 0);
       data.setValue((float) (percentageUndersampling.isSelected() ? undersamplingPercentage.getValue() : undersamplingNumber.getValue()));
       
       data.setContinueComparison(continueComparison.isSelected());
   }

   //sets up overlay values from data model
    private static void overlaySetup(JPanel primaryPanel, JCheckBox solidPrimary, JPanel secondaryPanel, JCheckBox secondarySolid, JPanel fogPanel, JSlider overlayTransparency, JCheckBox innerSurfaceSolid, JCheckBox useGlyphs, JCheckBox useContours
            , JRadioButton noneFogging, JRadioButton colorOverlayFogging, JRadioButton transparencyFogging, JRadioButton innerSurfaceFogging, Comparison2Faces data) {
        //overlay
        primaryPanel.setBackground(data.getPrimaryColor());
        solidPrimary.setSelected(data.isIsPrimarySolid());
        secondaryPanel.setBackground(data.getSecondaryColor());
        secondarySolid.setSelected(data.isIsSecondarySolid());
        fogPanel.setBackground(data.getFogColor());
        overlayTransparency.setValue((int) data.getOverlayTransparency());
        innerSurfaceSolid.setSelected(data.isInnerSurfaceSolid());
        useGlyphs.setSelected(data.isUseGlyphs());
        useContours.setSelected(data.isUseContours());
        
        switch(data.getFogVersion()){
            case 0:
                noneFogging.setSelected(true);
                break;
            case 1:
                colorOverlayFogging.setSelected(true);
                break;
            case 2:
                transparencyFogging.setSelected(true);
                break;
            case 3:
                innerSurfaceFogging.setSelected(true);
                break;
            default:
                noneFogging.setSelected(true);
        }
    }
    
    //sets up gui values from data model
    public static void setUpValuesCompConfiguration(JComboBox comparisonMethod, JCheckBox fpScaling, JCheckBox useDatabase, JSlider fpThreshold,
           JPanel primaryPanel, JCheckBox solidPrimary, JPanel secondaryPanel, JCheckBox secondarySolid, JPanel fogPanel, JSlider overlayTransparency, 
           JCheckBox innerSurfaceSolid, JCheckBox useGlyphs, JCheckBox useContours, JRadioButton noneFogging, JRadioButton colorOverlayFogging, JRadioButton transparencyFogging, JRadioButton innerSurfaceFogging,
           Comparison2Faces data){
        
       comparisonMethod.setSelectedItem(data.getComparisonMethod());
       
       //FP
       fpScaling.setSelected(data.isFpScaling());
       useDatabase.setSelected(data.getUseDatabase() != 0);
       fpThreshold.setValue(data.getFpTreshold());
       
       overlaySetup(primaryPanel, solidPrimary, secondaryPanel, secondarySolid, fogPanel, overlayTransparency, 
           innerSurfaceSolid, useGlyphs, useContours, noneFogging, colorOverlayFogging, transparencyFogging, innerSurfaceFogging, data);
    }
    
    //sets up default values to data model
    public static void setUpDefaultComparisonConfigurationData(Comparison2Faces data){
       data.setComparisonMethod(COMPARISON_METHOD);
       
       //FP
       data.setFpScaling(FP_SCALE);
       data.setUseDatabase(FP_DATABASE ? 1 : 0);
       data.setFpTreshold(FP_THRESHOLD);
       
       overlaySetupData(data);
    }
   
    //sets up default values to data model overlay
    private static void overlaySetupData(Comparison2Faces data) {
        //overlay
       data.setPrimaryColor(PRIMARY_MODEL);
       data.setIsPrimarySolid(SOLID_PRIMARY);
       data.setSecondaryColor(SECONDARY_MODEL);
       data.setIsSecondarySolid(SOLDI_SECONDARY);
       data.setFogColor(FOG_COLOR);
       data.setOverlayTransparency(OVERLAY_TRANSPARENCY);
       data.setInnerSurfaceSolid(INNER_SURFACE_SOLID);
       data.setUseGlyphs(USE_GLYPHS);
       data.setUseContours(USE_COUNTOURS);
       data.setFogVersion(FOG_VERSION);
    }
    
    //sets up GUI for comparison results based on data information
   public static void setUpComparisonResult(JComboBox visualization, JComboBox values, JSlider maxThresh, JSpinner maxThrehsSpinner,
           JSlider minThresh, JSpinner minThreshSpinner, JComboBox colorScheme, JSlider vectorRadius, JSlider vectorLength, JSlider cylinderRadius,
           JPanel primaryPanel, JCheckBox solidPrimary, JPanel secondaryPanel, JCheckBox secondarySolid, JPanel fogPanel, JSlider overlayTransparency, 
           JCheckBox innerSurfaceSolid, JCheckBox useGlyphs, JCheckBox useContours, JRadioButton selectedFogging, JSlider fpDistance, JSlider fpSize, 
           JRadioButton noneFogging, JRadioButton colorOverlayFogging, JRadioButton transparencyFogging, JRadioButton innerSurfaceFogging, Comparison2Faces data){
       
       visualization.setSelectedIndex(data.getVisualization());
       values.setSelectedItem(data.getValuesTypeIndex());
       
       maxThresh.setValue(data.getHausdorfMaxTreshold());
       maxThrehsSpinner.setValue(data.getHausdorfMaxTreshold());
       minThresh.setValue(data.getHausdorfMinTreshold());
       minThreshSpinner.setValue(data.getHausdorfMinTreshold());
       
       colorScheme.setSelectedIndex(data.getColorScheme());
       vectorRadius.setValue(data.getVectorDensity());
       vectorLength.setValue(data.getVectorLength());
       cylinderRadius.setValue(data.getCylinderRadius());
       
       fpDistance.setValue(data.getFpDistance());
       fpSize.setValue(data.getFpSize());
       
       overlaySetup(primaryPanel, solidPrimary, secondaryPanel, secondarySolid, fogPanel, overlayTransparency, innerSurfaceSolid, useGlyphs, useContours,
               noneFogging, colorOverlayFogging, transparencyFogging, innerSurfaceFogging, data);   
   }
   
   //sets up default comparison result data
   public static void setUpComparisonResultDefaultData(Comparison2Faces data){
       data.setVisualization(VISUALIZATION);
       data.setValuesTypeIndex(VALUES_TYPE);
       
       data.setHausdorfMaxTreshold(MAX_THRESHOLD);
       data.setHausdorfMinTreshold(MIN_THRESHOLD);
       
       data.setColorScheme(COLOR_SCHEME);
       data.setVectorDensity(VECTOR_DENSITY);
       data.setVectorLength(CYLINDER_LENGTH);
       data.setCylinderRadius(CYLINDER_RADIUS);
       
       data.setFpDistance(FP_DISTANCE);
       data.setFpSize(FP_SIZE);
       
       overlaySetupData(data);
   }
   
   
}
