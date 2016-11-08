/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.controller;

import cz.fidentis.composite.CompositeModel;
import cz.fidentis.composite.FacePartType;
import cz.fidentis.controller.ProjectTree.Node;
import cz.fidentis.model.Model;
import cz.fidentis.model.ModelLoader;
import java.io.File;
import java.util.ArrayList;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 * This class represents facial composite.
 *
 * @author Katarína Furmanová
 */
public class Composite {

    /**
     * Edit mode constatnt.
     */
    public static final int LEFT = 2;
    /**
     * Edit mode constatnt.
     */
    public static final int RIGHT = 1;
    /**
     * Edit mode constatnt.
     */
    public static final int BOTH = 0;
    private ArrayList<CompositeModel> faceParts = new ArrayList<CompositeModel>();
    private ArrayList<String> history = new ArrayList<String>();
    int historyIndex = 0;
    private ModelLoader loader = ModelLoader.instance();
    private FacePartType currentPart;
    private String name = new String();
    private Object selectedHead;
    private Object selectedForhead;
    private Object selectedEyes;
    private Object selectedEyebrows;
    private Object selectedNose;
    private Object selectedMouth;
    private Object selectedChin;
    private Object selectedEars;
    private Vector3f foreheadPosition;
    private Vector3f nosePosition;
    private Vector3f mouthPosition;
    private Vector3f chinPosition;
    private Vector3f leftearPosition;
    private Vector3f rightearPosition;
    private Vector3f lefteyePosition;
    private Vector3f righteyePosition;
    private Vector3f lefteyebrowPosition;
    private Vector3f righteyebrowPosition;
    private Node node;

    /**
     * Initializes basic positions of models.
     */
    public Composite() {
        initTranslations();
    }

    public void setNode(Node node) {
        this.node = node;
    }

    /**
     *
     * @return Returns the list of somatoscopic signs that the composite
     * consists of.
     */
    public ArrayList<CompositeModel> getFaceParts() {
        return faceParts;
    }

    /**
     * Returns list of possitions of individual parts of composite
     *
     * @return list of positions of individual parts
     */
    public ArrayList<Vector3f> positions() {
        ArrayList<Vector3f> pos = new ArrayList<>(10);

        /*pos.add(foreheadPosition);
         pos.add(nosePosition);
         pos.add(mouthPosition);
         pos.add(chinPosition);
         pos.add(leftearPosition);
         pos.add(rightearPosition);
         pos.add(lefteyePosition);
         pos.add(righteyePosition);
         pos.add(lefteyebrowPosition);
         pos.add(righteyebrowPosition);*/
        for (CompositeModel cm : faceParts) {
            if (cm.getPart() != FacePartType.HEAD) {
                pos.add(cm.getInitialShift());
            }
        }

        return pos;

    }

    /**
     * Returns position of point on the head for given FacePartType
     *
     * @param part FacePartType position on head to be returned
     * @param selection if selecting from pair models set 1 to select right
     * model, 2 to select left model
     * @return position of point on the head fot given FacePartType, or null if
     * no position was found (technically shouldn't happen)
     */
    public Vector3f getPart(FacePartType part, int selection) {
        Vector3f pickedPosition = null;

        switch (part) {
            case EYES:
                if (selection == RIGHT) {
                    pickedPosition = righteyePosition;
                } else {
                    pickedPosition = lefteyePosition;
                }
                break;
            case EARS:
                if (selection == RIGHT) {
                    pickedPosition = rightearPosition;
                } else {
                    pickedPosition = leftearPosition;
                }
                break;
            case EYEBROWS:
                if (selection == RIGHT) {
                    pickedPosition = righteyebrowPosition;
                } else {
                    pickedPosition = lefteyebrowPosition;
                }
                break;
            case FORHEAD:
                pickedPosition = foreheadPosition;
                break;
            case NOSE:
                pickedPosition = nosePosition;
                break;
            case MOUTH:
                pickedPosition = mouthPosition;
                break;
            case CHIN:
                pickedPosition = chinPosition;
                break;
        }

        return pickedPosition;
    }

    /**
     * Returns composite model for given FacePartType
     *
     * @param part FacePartType of the part to be returned
     * @param selection if selecting from pair models set 1 to select right
     * model, 2 to select left model
     * @return Returns the currently selected somatoscopic sign, null if the
     * composite doesn't contain the chosen part
     */
    public CompositeModel getPartCompositeModel(FacePartType part, int selection) {
        FacePartType type = part;
        switch (type) {
            case EYES:
                if (selection == RIGHT) {
                    type = FacePartType.RIGHT_EYE;
                } else {
                    type = FacePartType.LEFT_EYE;
                }
                break;
            case EARS:
                if (selection == RIGHT) {
                    type = FacePartType.RIGHT_EAR;
                } else {
                    type = FacePartType.LEFT_EAR;
                }
                break;
            case EYEBROWS:
                if (selection == RIGHT) {
                    type = FacePartType.RIGHT_EYEBROW;
                } else {
                    type = FacePartType.LEFT_EYEBROW;
                }
                break;
        }

        for (int i = 0; i < faceParts.size(); i++) {
            if (faceParts.get(i).getPart().equals(type)) {
                return faceParts.get(i);
            }
        }
        return null;
    }

    public CompositeModel getPartCompositeModel(FacePartType part) {

        for (int i = 0; i < faceParts.size(); i++) {
            if (faceParts.get(i).getPart().equals(part)) {
                return faceParts.get(i);
            }
        }
        return null;
    }

    /**
     * Reset given part to its initial position and scale (rotation whenever I
     * figure out how to change that Vector3f to Quaternion)
     *
     * @param part FacePartType of the part to be reseted
     * @param selection if selecting from pair models set 1 to select right
     * model, 2 to select left model
     */
    public void resetPartToInit(FacePartType part, int selection) {
        FacePartType type = part;
        CompositeModel cm;

        switch (type) {
            case EYES:
                if (selection == RIGHT) {
                    type = FacePartType.RIGHT_EYE;
                } else {
                    type = FacePartType.LEFT_EYE;
                }
                break;
            case EARS:
                if (selection == RIGHT) {
                    type = FacePartType.RIGHT_EAR;
                } else {
                    type = FacePartType.LEFT_EAR;
                }
                break;
            case EYEBROWS:
                if (selection == RIGHT) {
                    type = FacePartType.RIGHT_EYEBROW;
                } else {
                    type = FacePartType.LEFT_EYEBROW;
                }
                break;
        }
        for (int i = 0; i < faceParts.size(); i++) {
            if (faceParts.get(i).getPart() == type) {
                cm = faceParts.get(i);

                cm.setRotation(new Quat4f());
                cm.setScale(new Vector3f(1, 1, 1));
                cm.setTranslation(new Vector3f());
            }
        }
    }

    /**
     * Returns head in composite
     *
     * @return model of head in composite, or null if there's no head
     */
    public Model getHead() {
        for (int i = 0; i < faceParts.size(); i++) {
            if (faceParts.get(i).getPart() == FacePartType.HEAD) {
                return faceParts.get(i).getModel();
            }
        }

        return null;
    }

    /**
     *
     * @return Returns the list of models of somatoscopic signs that the
     * composite consists of.
     */
    public ArrayList<Model> getModels() {
        ArrayList<Model> models = new ArrayList<Model>();
        for (CompositeModel compositeModel : faceParts) {
            if (compositeModel.getVisible()) {
                if (compositeModel.getPart().equals(FacePartType.HEAD)) {
                    models.add(0, compositeModel.getModel());
                } else {
                    models.add(compositeModel.getModel());
                }
            }
        }
        return models;
    }

    public void setHeadAndClearParts(Model head) {
        for (CompositeModel facePart : faceParts) {
            if (!facePart.getVisible()) {
                continue;
            }
            if (facePart.getPart().equals(FacePartType.HEAD)) {
                facePart.setModel(head);
            } else {
                facePart.setVisible(Boolean.FALSE);
            }
        }
    }

    /**
     *
     * @return Returns index of a Head in list of somatoscopic signs of
     * composite, -1 if head is not in the list.
     */
    public int getHeadIndex() {
        for (int i = 0; i < faceParts.size(); i++) {
            if (faceParts.get(i).getPart().equals(FacePartType.HEAD)) {
                return i;
            }
        }
        return -1;
    }

    /**
     *
     * @return Name of the composite.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name of the composite.
     *
     * @param name name of the composite.
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Adds model to the list of somatoscopic signs and their models.
     *
     * @param file - file containing the model.
     * @param type - type of somatoscopic sign (NOSE, MOUTH, ...).
     * @param position - position of the model (as defined in .inf file of the
     * model).
     * @param shift - position of somatoscopic sign on a head (as defined in
     * .inf file of head currently used).
     */
    public void addModel(File file, FacePartType type, Vector3f position, Vector3f shift) {
        Model model = loader.loadModel(file, true, true);
        CompositeModel part = new CompositeModel();
        part.setModel(model);
        part.setPart(type);
        if (type != FacePartType.HEAD) {
            position.sub(model.getModelDims().getOriginalCenter());
            shift.sub(position);
            part.setInitialShift(shift);
            part.initRotation();
        } else {
            part.setInitialShift(new Vector3f());
        }

        Boolean replaced = false;
        for (int i = 0; i < faceParts.size(); i++) {
            if (faceParts.get(i).getPart().equals(type)) {
                part.setTranslation(faceParts.get(i).getTranslation());
                part.setRotQuat(faceParts.get(i).getRotQuat());
                part.setScale(faceParts.get(i).getScale());
                part.setEditMode(faceParts.get(i).getEditMode());
                faceParts.remove(i);
                replaced = true;

            }
        }
        if (!replaced) {
            part.setEditMode(0);
        }

        part.setVisible(true);
        faceParts.add(part);
    }

    /**
     * Removes somatoscopic sign from composite.
     *
     * @param part somatoscopic sign to be removed.
     */
    public void removeFacePart(CompositeModel part) {
        for (int i = 0; i < faceParts.size(); i++) {
            if (faceParts.get(i).equals(part)) {
                faceParts.get(i).setVisible(false);
                if (part.getPart().equals(FacePartType.HEAD)) {
                    initTranslations();
                }
            }
        }
    }

    /**
     *
     *
     * @return Return list of indexes of a faces of head model that shoud be
     * hidden.
     */
    /* public ArrayList<Integer> getFacesToDrop() {
     Model head = null;
     ArrayList<Integer> indexes = new ArrayList<Integer>();
     for (int i = 0; i < faceParts.size(); i++) {
     if (faceParts.get(i).getPart().equals(FacePartType.HEAD)) {
     head = faceParts.get(i).getModel();
     }
     }
     if (head != null) {
     for (int i = 0; i < faceParts.size(); i++) {
     if (!faceParts.get(i).getPart().equals(FacePartType.HEAD)) {
     indexes.addAll(findIntesectingFaces(head, faceParts.get(i).getModel()));

     }
     }
     }

     // System.out.println("Indexes"+ indexes);
     return indexes;

     }*/
    /**
     * @param head model of a head.
     * @param part model of a somatoscopic sign.
     *
     * @return Returns indexes of faces of head model intersecting with given
     * model.
     */
    /*
     public ArrayList<Integer> findIntesectingFaces(Model head, Model part) {
     ArrayList<Integer> indexes = new ArrayList<Integer>();
     ModelSelector m = new ModelSelector();
     for (int i = 0; i < part.getVerts().size() - 1; i++) {
     double P0[] = {0,0,0};
     double P1[] = {part.getVerts().get(i).x,part.getVerts().get(i).y,part.getVerts().get(i).z};
     m.setRay(P0, P1);
     for (int j = 0; j < head.getFaces().getNumFaces(); j++)//each face of model
     {
     int[] faceVertsIx = head.getFaces().getFaceVertIdxs(j);

     if (faceVertsIx[0] < head.getVerts().size()) {
     for (int k = 1; k <= (faceVertsIx.length - 2); k++) { //each vertex of face
     if (faceVertsIx[k] < head.getVerts().size() && faceVertsIx[k + 1] < head.getVerts().size()) {
     Vector3f p1 = head.getVerts().get(faceVertsIx[0] - 1);
     Vector3f p2 = head.getVerts().get(faceVertsIx[k] - 1);
     Vector3f p3 = head.getVerts().get(faceVertsIx[k + 1] - 1);

     Vector3f[] t = {p1, p2, p3};
     Vector3f intersectionPoint = m.calculateIntersection(t, false);
     if (intersectionPoint != null) {
     indexes.add(j);
     }
     }
     }
     }
     }
     }
     return indexes;
     }*/
    /**
     * Updates position rotation and size of selected model.
     *
     * @param position new position of a model
     * @param rotation new rotation of a model (in case of editing both models
     * from pair(both eyes, ears...) new rotation of right model)
     * @param rotation2 in case of editing both models from pair (both eyes,
     * ears...) new rotation of left model
     * @param size new size of a model
     */
    public void updateSelectedPart(Vector3f position, Quat4f rotation, Quat4f rotation2, Vector3f size) {
        if (currentPart != null && (getSelectedPart(1) != null)) {
            if (currentPart.equals(FacePartType.EYES) || currentPart.equals(FacePartType.EYEBROWS) || currentPart.equals(FacePartType.EARS)) {
                if (getSelectedPart(1).getEditMode() == BOTH) {
                    updatePositions(getSelectedPart(1), getSelectedPart(2), position, rotation, rotation2, size);
                } else if (getSelectedPart(1).getEditMode() == RIGHT) {
                    updatePositions(getSelectedPart(1), null, position, rotation, rotation2, size);
                } else {
                    updatePositions(getSelectedPart(2), null, position, rotation, rotation2, size);
                }
            } else {
                updatePositions(getSelectedPart(1), null, position, rotation, rotation2, size);
            }
        }
    }

    private void updatePositions(CompositeModel part, CompositeModel part2, Vector3f position, Quat4f rotation, Quat4f rotation2, Vector3f size) {
        if (part != null) {
            if (part2 != null) {
                Vector3f center = new Vector3f(
                        (part.getTranslation().getX() + part2.getTranslation().getX() + part.getInitialShift().getX() + part2.getInitialShift().getX()) / 2,
                        (part.getTranslation().getY() + part2.getTranslation().getY() + part.getInitialShift().getY() + part2.getInitialShift().getY()) / 2,
                        (part.getTranslation().getZ() + part2.getTranslation().getZ() + part.getInitialShift().getZ() + part2.getInitialShift().getZ()) / 2);
                part.setTranslation(new Vector3f(
                        part.getTranslation().getX() + position.getX() - center.getX(),
                        part.getTranslation().getY() + position.getY() - center.getY(),
                        part.getTranslation().getZ() + position.getZ() - center.getZ()));
                part2.setTranslation(new Vector3f(
                        part2.getTranslation().getX() + position.getX() - center.getX(),
                        part2.getTranslation().getY() + position.getY() - center.getY(),
                        part2.getTranslation().getZ() + position.getZ() - center.getZ()));

                part.setRotation(rotation);
                part2.setRotation(rotation2);

                /*  part.setRotation(rotation.getX(),
                 rotation.getY(),
                 rotation.getZ()));
                 part2.setRotation(new Vector3f(
                 rotation.getX(),
                 0 - (rotation.getY()),
                 0 - (rotation.getZ())));*/
                /*              center = new Vector3f(
                 (part.getInitialRotation().getX() + part2.getInitialRotation().getX()) / 2,
                 ( part.getInitialRotation().getY() - part2.getInitialRotation().getY()) / 2,
                 ( part.getInitialRotation().getZ() - part2.getInitialRotation().getZ()) / 2);

                 if(rotation.length() > 0){
                 part.setRotation(new Vector3f(
                 rotation.getX() - center.getX(),
                 rotation.getY() - center.getY(),
                 rotation.getZ() - center.getZ()));
                 part2.setRotation(new Vector3f(
                 rotation.getX() - center.getX(),
                 0 - (rotation.getY() - center.getY()),
                 0 - (rotation.getZ() - center.getZ())));
                 }
                 /*
                 center = new Vector3f(
                 (part.getRotation().getX() + part2.getRotation().getX() + part.getInitialRotation().getX() + part2.getInitialRotation().getX()) / 2,
                 (part.getRotation().getY() - part2.getRotation().getY() + part.getInitialRotation().getY() - part2.getInitialRotation().getY()) / 2,
                 (part.getRotation().getZ() - part2.getRotation().getZ() + part.getInitialRotation().getZ() - part2.getInitialRotation().getZ()) / 2);

                 part.setRotation(new Vector3f(
                 part.getRotation().getX() + rotation.getX() - center.getX(),
                 part.getRotation().getY() + rotation.getY() - center.getY(),
                 part.getRotation().getZ() + rotation.getZ() - center.getZ()));
                 part2.setRotation(new Vector3f(
                 part2.getRotation().getX() + rotation.getX() - center.getX(),
                 part2.getRotation().getY() - (rotation.getY() - center.getY()),
                 part2.getRotation().getZ() - (rotation.getZ() - center.getZ())));
                 */
                center = new Vector3f(
                        (part.getScale().getX() + part2.getScale().getX()) / 2,
                        (part.getScale().getY() + part2.getScale().getY()) / 2,
                        (part.getScale().getZ() + part2.getScale().getZ()) / 2);

                part.setScale(new Vector3f(
                        part.getScale().getX() + size.getX() - center.getX(),
                        part.getScale().getY() + size.getY() - center.getY(),
                        part.getScale().getZ() + size.getZ() - center.getZ()));

                part2.setScale(new Vector3f(
                        part2.getScale().getX() + size.getX() - center.getX(),
                        part2.getScale().getY() + size.getY() - center.getY(),
                        part2.getScale().getZ() + size.getZ() - center.getZ()));

            } else {
                part.setTranslation(new Vector3f(
                        position.getX() - part.getInitialShift().getX(),//-part.getTranslation().getX(),
                        position.getY() - part.getInitialShift().getY(),//-part.getTranslation().getY(),
                        position.getZ() - part.getInitialShift().getZ()//-part.getTranslation().getZ()
                ));

                part.setRotation(rotation);

                /*
                 if(rotation.length() > 0){
                 part.setRotation(new Vector3f(
                 rotation.getX(),//-part.getRotation().getX(),
                 rotation.getY(),//-part.getRotation().getY(),
                 rotation.getZ()//-part.getRotation().getZ()
                 ));
                 }*/
                part.setScale(new Vector3f(
                        size.getX(),
                        size.getY(),
                        size.getZ()));
            }
        }
    }

    //returns right eye,ear, eyebrow if selection = 1; left otherwise, in case of nose, mouth.. doesn't depend on selection
    /**
     *
     * @param selection if selecting from pair models set 1 to select right
     * model, 2 to select left model
     * @return Returns the currently selected somatoscopic sign
     */
    public CompositeModel getSelectedPart(int selection) {
        FacePartType type;
        type = currentPart;
        switch (currentPart) {
            case EYES:
                if (selection == RIGHT) {
                    type = FacePartType.RIGHT_EYE;
                } else {
                    type = FacePartType.LEFT_EYE;
                }
                break;
            case EARS:
                if (selection == RIGHT) {
                    type = FacePartType.RIGHT_EAR;
                } else {
                    type = FacePartType.LEFT_EAR;
                }

                break;
            case EYEBROWS:
                if (selection == RIGHT) {
                    type = FacePartType.RIGHT_EYEBROW;
                } else {
                    type = FacePartType.LEFT_EYEBROW;
                }
                break;

        }
        for (int i = 0; i < faceParts.size(); i++) {
            if (faceParts.get(i).getPart().equals(type)) {
                return faceParts.get(i);
            }
        }
        return null;
    }

    /**
     * Sets editing mode.
     *
     * @param i set 0 for BOTH, 1 for RIGHT, 2 for LEFT
     */
    public void setEditMode(int i) {
        if (getSelectedPart(1) != null) {
            getSelectedPart(1).setEditMode(i);
            if (currentPart.equals(FacePartType.EYES) || currentPart.equals(FacePartType.EYEBROWS) || currentPart.equals(FacePartType.EARS)) {
                getSelectedPart(RIGHT).setEditMode(i);
            }
        }
    }

    /**
     * Selects somatoscopisc sign.
     *
     * @param model model of somatoscopic sign to be selected.
     */
    public void selectFacePart(Model model) {
        CompositeModel part;
        for (int i = 0; i < faceParts.size(); i++) {
            if (faceParts.get(i).getModel().equals(model)) {
                part = faceParts.get(i);
                if (part.getPart().equals(FacePartType.LEFT_EYE) || part.getPart().equals(FacePartType.RIGHT_EYE)) {
                    currentPart = FacePartType.EYES;
                } else if (part.getPart().equals(FacePartType.LEFT_EAR) || part.getPart().equals(FacePartType.RIGHT_EAR)) {
                    currentPart = FacePartType.EARS;
                } else if (part.getPart().equals(FacePartType.LEFT_EYEBROW) || part.getPart().equals(FacePartType.RIGHT_EYEBROW)) {
                    currentPart = FacePartType.EYEBROWS;
                } else {
                    currentPart = part.getPart();
                }
                if (currentPart != null) {
                    getSelectedPart(1).setEditMode(0);
                }
            }
        }
    }

    /**
     *
     * @return Returns name of the somatoscopic sign currently selected.
     */
    public FacePartType getCurrentPart() {
        return currentPart;
    }

    /**
     * Selects the somatoscopic sign.
     *
     * @param currentPart name of the somatoscopic sign to be selected.
     */
    public void setCurrentPart(FacePartType currentPart) {
        if (!currentPart.equals(this.currentPart)) {
            this.currentPart = currentPart;
//         logAction("sel " + currentPart + " " + getSelectedPart(0).getEditMode());
        }
    }

    /**
     *
     * @return
     */
    public Object getSelectedHead() {
        return selectedHead;
    }

    /**
     *
     * @param selectedHead
     */
    public void setSelectedHead(Object selectedHead) {
        this.selectedHead = selectedHead;
    }

    /**
     *
     * @return
     */
    public Object getSelectedForhead() {
        return selectedForhead;
    }

    /**
     *
     * @param selectedForhead
     */
    public void setSelectedForhead(Object selectedForhead) {
        this.selectedForhead = selectedForhead;
    }

    /**
     *
     * @return
     */
    public Object getSelectedEyes() {
        return selectedEyes;
    }

    /**
     *
     * @param selectedEyes
     */
    public void setSelectedEyes(Object selectedEyes) {
        this.selectedEyes = selectedEyes;
    }

    /**
     *
     * @return
     */
    public Object getSelectedEyebrows() {
        return selectedEyebrows;
    }

    /**
     *
     * @param selectedEyebrows
     */
    public void setSelectedEyebrows(Object selectedEyebrows) {
        this.selectedEyebrows = selectedEyebrows;
    }

    /**
     *
     * @return
     */
    public Object getSelectedNose() {
        return selectedNose;
    }

    /**
     *
     * @param selectedNose
     */
    public void setSelectedNose(Object selectedNose) {
        this.selectedNose = selectedNose;
    }

    /**
     *
     * @return
     */
    public Object getSelectedMouth() {
        return selectedMouth;
    }

    /**
     *
     * @param selectedMouth
     */
    public void setSelectedMouth(Object selectedMouth) {
        this.selectedMouth = selectedMouth;
    }

    /**
     *
     * @return
     */
    public Object getSelectedChin() {
        return selectedChin;
    }

    /**
     *
     * @param selectedChin
     */
    public void setSelectedChin(Object selectedChin) {
        this.selectedChin = selectedChin;
    }

    /**
     *
     * @return
     */
    public Object getSelectedEars() {
        return selectedEars;
    }

    /**
     *
     * @param selectedEars
     */
    public void setSelectedEars(Object selectedEars) {
        this.selectedEars = selectedEars;
    }

    /**
     *
     * @return
     */
    public Vector3f getForeheadPosition() {
        return foreheadPosition;
    }

    /**
     *
     * @param foreheadPosition
     */
    public void setForeheadPosition(Vector3f foreheadPosition) {
        foreheadPosition.sub(getHeadOriginalCenetr());
        updateInitialShift(FacePartType.FORHEAD, this.foreheadPosition, foreheadPosition);
        this.foreheadPosition = foreheadPosition;
    }

    /**
     *
     * @return
     */
    public Vector3f getNosePosition() {
        return nosePosition;
    }

    /**
     *
     * @param nosePosition
     */
    public void setNosePosition(Vector3f nosePosition) {
        nosePosition.sub(getHeadOriginalCenetr());
        updateInitialShift(FacePartType.NOSE, this.nosePosition, nosePosition);
        this.nosePosition = nosePosition;
    }

    /**
     *
     * @return
     */
    public Vector3f getMouthPosition() {
        return mouthPosition;
    }

    /**
     *
     * @param mouthPosition
     */
    public void setMouthPosition(Vector3f mouthPosition) {
        mouthPosition.sub(getHeadOriginalCenetr());
        updateInitialShift(FacePartType.MOUTH, this.mouthPosition, mouthPosition);
        this.mouthPosition = mouthPosition;
    }

    /**
     *
     * @return
     */
    public Vector3f getChinPosition() {
        return chinPosition;
    }

    /**
     *
     * @param chinPosition
     */
    public void setChinPosition(Vector3f chinPosition) {
        chinPosition.sub(getHeadOriginalCenetr());
        updateInitialShift(FacePartType.CHIN, this.chinPosition, chinPosition);
        this.chinPosition = chinPosition;
    }

    /**
     *
     * @return
     */
    public Vector3f getLeftearPosition() {
        return leftearPosition;
    }

    /**
     *
     * @param leftearPosition
     */
    public void setLeftearPosition(Vector3f leftearPosition) {
        leftearPosition.sub(getHeadOriginalCenetr());
        updateInitialShift(FacePartType.LEFT_EAR, this.leftearPosition, leftearPosition);
        this.leftearPosition = leftearPosition;
    }

    /**
     *
     * @return
     */
    public Vector3f getRightearPosition() {
        return rightearPosition;
    }

    /**
     *
     * @param rightearPosition
     */
    public void setRightearPosition(Vector3f rightearPosition) {
        rightearPosition.sub(getHeadOriginalCenetr());
        updateInitialShift(FacePartType.RIGHT_EAR, this.rightearPosition, rightearPosition);
        this.rightearPosition = rightearPosition;
    }

    /**
     *
     * @return
     */
    public Vector3f getLefteyePosition() {
        return lefteyePosition;
    }

    /**
     *
     * @param lefteyePosition
     */
    public void setLefteyePosition(Vector3f lefteyePosition) {
        lefteyePosition.sub(getHeadOriginalCenetr());
        updateInitialShift(FacePartType.LEFT_EYE, this.lefteyePosition, lefteyePosition);
        this.lefteyePosition = lefteyePosition;
    }

    /**
     *
     * @return
     */
    public Vector3f getRighteyePosition() {
        return righteyePosition;
    }

    /**
     *
     * @param righteyePosition
     */
    public void setRighteyePosition(Vector3f righteyePosition) {
        righteyePosition.sub(getHeadOriginalCenetr());
        updateInitialShift(FacePartType.RIGHT_EYE, this.righteyePosition, righteyePosition);
        this.righteyePosition = righteyePosition;
    }

    /**
     *
     * @return
     */
    public Vector3f getRighteyebrowPosition() {
        return righteyebrowPosition;
    }

    /**
     *
     * @param righteyebrowPosition
     */
    public void setRighteyebrowPosition(Vector3f righteyebrowPosition) {
        righteyebrowPosition.sub(getHeadOriginalCenetr());
        updateInitialShift(FacePartType.RIGHT_EYEBROW, this.righteyebrowPosition, righteyebrowPosition);
        this.righteyebrowPosition = righteyebrowPosition;
    }

    /**
     *
     * @return
     */
    public Vector3f getLefteyebrowPosition() {
        return lefteyebrowPosition;
    }

    /**
     *
     * @param lefteyebrowPosition
     */
    public void setLefteyebrowPosition(Vector3f lefteyebrowPosition) {
        lefteyebrowPosition.sub(getHeadOriginalCenetr());
        updateInitialShift(FacePartType.LEFT_EYEBROW, this.lefteyebrowPosition, lefteyebrowPosition);
        this.lefteyebrowPosition = lefteyebrowPosition;
    }

    /**
     *
     * @return
     */
    public Vector3f getHeadOriginalCenetr() {
        for (int i = 0; i < faceParts.size(); i++) {
            if (faceParts.get(i).getPart().equals(FacePartType.HEAD) && faceParts.get(i).getVisible()) {
                return faceParts.get(i).getModel().getModelDims().getOriginalCenter();
            }
        }
        return new Vector3f();
    }

    /**
     *
     * @param part
     * @param original
     * @param updated
     */
    public void updateInitialShift(FacePartType part, Vector3f original, Vector3f updated) {
        for (int i = 0; i < faceParts.size(); i++) {
            if (faceParts.get(i).getPart().equals(part)) {
                Vector3f is = faceParts.get(i).getInitialShift();
                is.sub(original);
                is.add(updated);
                faceParts.get(i).setInitialShift(is);
            }
        }
    }

    private void initTranslations() {
        setChinPosition(new Vector3f(0, -75, 10));
        setForeheadPosition(new Vector3f(0, 85, 25));
        setMouthPosition(new Vector3f(0, -40, 12));
        setNosePosition(new Vector3f(0, -10, 40));
        setRightearPosition(new Vector3f(-80, 20, -80));
        setRighteyePosition(new Vector3f(-30, 28, 10));
        setLefteyePosition(new Vector3f(30, 28, 10));
        setLeftearPosition(new Vector3f(80, 20, -80));
        setLefteyebrowPosition(new Vector3f(-30, 45, 20));
        setRighteyebrowPosition(new Vector3f(30, 45, 20));

    }

    /**
     *
     */
    public void undo() {
    }

    /**
     *
     */
    public void redo() {
    }

    /**
     *
     * @param log
     */
    public void logAction(String log) {
        history.add(log);
        System.out.println(history);
    }
}
