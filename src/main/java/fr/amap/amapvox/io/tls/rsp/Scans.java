/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.amap.amapvox.io.tls.rsp;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.vecmath.Matrix4d;

/**
 *
 * @author Julien Heurtebize (julienhtbe@gmail.com)
 */
public class Scans {
    
    private String name;
    private File rxpLiteFile;
    private File rxpFullFile;
    private String fold;
    private Matrix4d sopMatrix;
    private RxpScan scanLite;
    private RxpScan scanFull;
    
    
    public Scans(){
        sopMatrix = new Matrix4d();
        sopMatrix.setIdentity();
    }

    public RxpScan getScanLite() {
        return scanLite;
    }

    public void setScanLite(RxpScan scanLite) {
        this.scanLite = scanLite;
    }

    public RxpScan getScanFull() {
        return scanFull;
    }

    public void setScanFull(RxpScan scanFull) {
        this.scanFull = scanFull;
    }
    
    private Map<Integer, RxpScan> scanList;

    public void setFold(String fold) {
        this.fold = fold;
    }

    public void setRxpLiteFile(File rxpLiteFile) {
        this.rxpLiteFile = rxpLiteFile;
    }

    public void setRxpFullFile(File rxpFullFile) {
        this.rxpFullFile = rxpFullFile;
    }
    
    
    public void setSopMatrix(Matrix4d sopMatrix) {
        this.sopMatrix = sopMatrix;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setScanList(Map<Integer,RxpScan> scanList) {
        this.scanList = scanList;
    }

    public String getName() {
        return name;
    }

    public String getFold() {
        return fold;
    }

    public Matrix4d getSopMatrix() {
        return sopMatrix;
    }

    public Map<Integer, RxpScan> getScanList() {
        return scanList;
    }
    
    
    public Map<Integer, RxpScan> getScanListFiltered(boolean lite) {
        
        Map<Integer, RxpScan> result = new HashMap<>();
        
        for(Entry entry:scanList.entrySet()){
            
            if(scanList.get((int)entry.getKey()).getAbsolutePath().contains(".mon") && lite){
                
                result.put((int)entry.getKey(), scanList.get((int)entry.getKey()));
                
            }else if(!scanList.get((int)entry.getKey()).getAbsolutePath().contains(".mon") && !lite){
                result.put((int)entry.getKey(), scanList.get((int)entry.getKey()));
            }
        }
        
        return result;
    }
    
    
}
