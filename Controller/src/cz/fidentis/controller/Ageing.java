/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.controller;

import cz.fidentis.controller.ProjectTree.Node;
import cz.fidentis.featurepoints.FacialPoint;
import cz.fidentis.model.Model;
import java.util.List;

/**
 *
 * @author Katka
 */
public class Ageing {
    private String name = new String();
    private float originAge;
    private float targetAge;
    private Gender gender = Gender.MALE;
    private Model originModel;
    private List<FacialPoint> originFacialPoints;
    private Model targetModel;
    private List<FacialPoint> targetPoints;
    
    private Node node;
    private Node node_origin;
    private Node node_target;

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    public float getOriginAge() {
        return originAge;
    }

    public void setOriginAge(float originAge) {
        this.originAge = originAge;
    }

    public float getTargetAge() {
        return targetAge;
    }

    public void setTargetAge(float targetAge) {
        this.targetAge = targetAge;
    }
    
    public Gender getGender() {
        return gender;
    }
    
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Model getOriginModel() {
        return originModel;
    }

    public void setOriginModel(Model originModel) {
        this.originModel = originModel;
        if(node_origin != null) {
            node.removeChild(node.getChildren().indexOf(node_origin));
        }
        if(originModel != null) {
            node_origin = node.addChild(originModel);
        }
    }

    public List<FacialPoint> getOriginFacialPoints() {
        return originFacialPoints;
    }

    public void setOriginFacialPoints(List<FacialPoint> originFacialPoints) {
        this.originFacialPoints = originFacialPoints;
    }

    public Model getTargetModel() {
        return targetModel;
    }

    public void setTargetModel(Model targetModel) {
        this.targetModel = targetModel;
        if(node_target != null) {
            node.removeChild(node.getChildren().indexOf(node_target));
        }
        if(targetModel != null) {
            node_target = node.addChild(targetModel);
        }
    }

    public List<FacialPoint> getTargetPoints() {
        return targetPoints;
    }

    public void setTargetPoints(List<FacialPoint> targetPoints) {
        this.targetPoints = targetPoints;
    }
    
    public void setNode(Node n) {
        this.node = n;
    }
    
     @Override
    public String toString() {
        return name;
    }
}
