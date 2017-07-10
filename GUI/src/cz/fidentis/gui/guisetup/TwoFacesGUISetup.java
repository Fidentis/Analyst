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
import cz.fidentis.visualisation.ColorScheme;
import cz.fidentis.visualisation.surfaceComparison.VisualizationType;
import java.awt.Color;
import javax.vecmath.Vector3f;

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
   private static final int TYPE_INDEX = 1;
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
    private static final int SELECTED_PLANE = 1;
   private static final Vector3f ARBITRARY_NORMAL = new Vector3f(1,0,0);
   private static final Vector3f PLANE_POSITION = new Vector3f();
   private static final int CROSSCUT_SIZE = 50;
   private static final int CROSSCUT_THICKNESS = 50;
   private static final Color CROSSCUT_COLOR = new Color(255,255,255);
   private static final boolean HIGHLIGHT_CUTS = true;
   private static final boolean CROSSCUT_VECTORS = true;
   private static final boolean ALL_CUTS = false;
   private static final boolean SAMPLINGR_RAYS = false;
   private static final boolean SHOW_PLANE = true;
   private static final boolean BOXPLOT = false;
   private static final boolean CONTINUOUS_BOXPLOT = false;
   private static final VisualizationType VISUALIZATION = VisualizationType.COLORMAP;
   private static final int VALUES_TYPE = 0;
   private static final int MAX_THRESHOLD = 100;
   private static final int MIN_THRESHOLD = 0;
   private static final ColorScheme COLOR_SCHEME = ColorScheme.GREEN_BLUE;
   
   private static final int VECTOR_DENSITY = 10;
   private static final int CYLINDER_LENGTH = 1;
   private static final int CYLINDER_RADIUS = 1;
   
   private static final int FP_DISTANCE = 0;
   
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
       data.setType(TYPE_INDEX);
       data.setValue(PERCENTAGE_VALUE);
       
       data.setContinueComparison(CONTINUE_COMPARISON);
       data.setFirstCreated(false);       
   }
   
    
    //sets up default values to data model
    public static void setUpDefaultComparisonConfigurationData(Comparison2Faces data){
       data.setComparisonMethod(COMPARISON_METHOD);
       
       //FP
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
       
        data.setCrossCutPlaneIndex(SELECTED_PLANE);
         data.setArbitraryPlanePos(ARBITRARY_NORMAL.x, ARBITRARY_NORMAL.y, ARBITRARY_NORMAL.z );
         data.setPlanePosition(PLANE_POSITION.x, PLANE_POSITION.y, PLANE_POSITION.z);
         data.setCrosscutSize(CROSSCUT_SIZE);
         data.setCrosscutThickness(CROSSCUT_THICKNESS);
         data.setCrosscutColor(CROSSCUT_COLOR);       
       
         data.setHighlightCuts(HIGHLIGHT_CUTS);
         data.setShowVectors(CROSSCUT_VECTORS);
         data.setAllCuts(ALL_CUTS);
         data.setSamplingRays(SAMPLINGR_RAYS);
         data.setShowPlane(SHOW_PLANE);
         data.setShowBoxplot(BOXPLOT);
         data.setShowBoxplotFunction(CONTINUOUS_BOXPLOT);
       
       overlaySetupData(data);
   }
   
   
}
