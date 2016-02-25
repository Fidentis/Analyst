/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.fidentis.controller;

import cz.fidentis.model.Model;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Katka
 */
public class ProjectTree {
    private Node root;

    public ProjectTree(Object rootData) {
        root = new Node();
        root.data = rootData;
        root.children = new ArrayList<Node>();
        if(rootData instanceof String){
                root.name = (String)rootData;
            }
    }
    
    public Node getRoot(){
        return root;
    }
    
    public static class Node {
        private String name;
        private Object data;
        private Node parent;
        private ArrayList<Node> children = new ArrayList<>() ;
        
        public Node addChild(Object childData){
            Node child = new Node();
            child.setData(childData);
            
            return addChild(child);
        }
        public String getName(){
            return name;
        }
        
        public Node addChild(Node child){
            children.add(child);
            child.parent = this;
            return child;
            
        }
        
        public void removeChild(int index) {
            if(index < 0 || index >= children.size()) return;
            children.remove(index);
        }
        
        public void setData(Object data){
            this.name = getNameFromData(data);
            this.data = data;
        }
        public Object getData(){
            return data;
        }
        
        public List<Node> getChildren(){
            return children;
        }
        
        public void removeChildren(){
            children.clear();
        }
        
        private String getNameFromData(Object childData) {
            String dataName = childData.toString();
            if(childData instanceof String){
                dataName = (String)childData;
            }
            if(childData instanceof File){
                dataName = ((File)childData).getName();
            }
            if(childData instanceof Composite){
                dataName = ((Composite)childData).getName();
            }
            if(childData instanceof Comparison2Faces){
                dataName = ((Comparison2Faces)childData).getName();
            }
            if(childData instanceof OneToManyComparison){
                dataName = ((OneToManyComparison)childData).getName();
            }
            if(childData instanceof BatchComparison){
                dataName = ((BatchComparison)childData).getName();
            }
            if(childData instanceof Model){
                dataName = ((Model)childData).getName();
            }
            return dataName;
        }
    }
 
}
