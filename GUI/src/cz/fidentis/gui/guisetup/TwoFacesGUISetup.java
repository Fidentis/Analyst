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
   
   private static final Color PRIMARY_MODEL = new Color(255,255,0);
   private static final boolean SOLID_PRIMARY = false;
   private static final Color SECONDARY_MODEL = new Color(51,153,255);
   private static final boolean SOLDI_SECONDARY = false;
   private static final Color FOG_COLOR = new Color(255,102,204);
   private static final int OVERLAY_TRANSPARENCY = 100;
   private static final boolean INNER_SURFACE_SOLID = true;
   private static final boolean USE_GLYPHS = false;
   private static final boolean USE_COUNTOURS = true;
   
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
   
   public static void defaultValueComparisonConfiguration(JComboBox comparisonMethod, JCheckBox fpScaling, JCheckBox useDatabase, JSlider fpThreshold,
           JPanel primaryPanel, JCheckBox solidPrimary, JPanel secondaryPanel, JCheckBox secondarySolid, JPanel fogPanel, JSlider overlayTransparency, 
           JCheckBox innerSurfaceSolid, JCheckBox useGlyphs, JCheckBox useContours, JRadioButton selectedFogging){
       
       comparisonMethod.setSelectedItem(COMPARISON_METHOD);
       
       //FP
       fpScaling.setSelected(FP_SCALE);
       useDatabase.setSelected(FP_DATABASE);
       fpThreshold.setValue(FP_THRESHOLD);
       
        overlaySetup(primaryPanel, solidPrimary, secondaryPanel, secondarySolid, fogPanel, overlayTransparency, innerSurfaceSolid, useGlyphs, useContours, selectedFogging);
       
   }

    private static void overlaySetup(JPanel primaryPanel, JCheckBox solidPrimary, JPanel secondaryPanel, JCheckBox secondarySolid, JPanel fogPanel, JSlider overlayTransparency, JCheckBox innerSurfaceSolid, JCheckBox useGlyphs, JCheckBox useContours, JRadioButton selectedFogging) {
        //overlay
        primaryPanel.setBackground(PRIMARY_MODEL);
        solidPrimary.setSelected(SOLID_PRIMARY);
        secondaryPanel.setBackground(SECONDARY_MODEL);
        secondarySolid.setSelected(SOLDI_SECONDARY);
        fogPanel.setBackground(FOG_COLOR);
        overlayTransparency.setValue(OVERLAY_TRANSPARENCY);
        innerSurfaceSolid.setSelected(INNER_SURFACE_SOLID);
        useGlyphs.setSelected(USE_GLYPHS);
        useContours.setSelected(USE_COUNTOURS);
        selectedFogging.setSelected(true);
    }
   
   public static void defaultValueComparisonResult(JComboBox visualization, JComboBox values, JSlider maxThresh, JSpinner maxThrehsSpinner,
           JSlider minThresh, JSpinner minThreshSpinner, JComboBox colorScheme, JSlider vectorRadius, JSlider vectorLength, JSlider cylinderRadius,
           JPanel primaryPanel, JCheckBox solidPrimary, JPanel secondaryPanel, JCheckBox secondarySolid, JPanel fogPanel, JSlider overlayTransparency, 
           JCheckBox innerSurfaceSolid, JCheckBox useGlyphs, JCheckBox useContours, JRadioButton selectedFogging, JSlider fpDistance, JSlider fpSize){
       
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
       
       overlaySetup(primaryPanel, solidPrimary, secondaryPanel, secondarySolid, fogPanel, overlayTransparency, innerSurfaceSolid, useGlyphs, useContours, selectedFogging);
       
   }
}
