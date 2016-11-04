/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.amap.amapvox.io.tls.rsp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.vecmath.Matrix4d;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author Julien Heurtebize (julienhtbe@gmail.com)
 */
public class Rsp {
    
    
    private String projectName;
    private ArrayList<Scans> rxpList;
    private Matrix4d popMatrix;
    
    private Document document;
    private SAXBuilder sxb;
    private Element root;
    
    public Rsp(){
        popMatrix = new Matrix4d();
        popMatrix.setIdentity();
    }
    
    public RxpScan getRxpScanByName(String name){
        
        for(Scans scans : rxpList){
            for (Entry entry : scans.getScanList().entrySet()) {
                RxpScan scan = (RxpScan) entry.getValue();
                if(scan.getFileName().equals(name)){
                    return scan;
                }
            }
        }
        
        return null;
    }
    
    public Matrix4d getPopMatrix() {
        return popMatrix;
    }

    public String getProjectName() {
        return projectName;
    }

    public ArrayList<Scans> getRxpList() {
        return rxpList;
    }
    
    private Matrix4d extractMat4D(String matString){
        
        String[] matSplit = matString.split(" ");
        Matrix4d matrix = new Matrix4d();
        
        double[] mat = new double[16];
                
        int index = 0;
        for(int i=0;i<matSplit.length;i++){

            matSplit[i] = matSplit[i].trim();
            if(!matSplit[i].isEmpty() && index < 16){
                Double matxyx = Double.valueOf(matSplit[i]);
                mat[index] = matxyx;
                index++;
            }
        }
        
        matrix.set(mat);
        
        return matrix;
    }
    
    public void read(final File rspFile) throws JDOMException, IOException{
        
        sxb = new SAXBuilder();
        rxpList = new ArrayList<>();
        
        //avoid loading of dtd file
        sxb.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        
        try {
            document = sxb.build(new FileInputStream(rspFile));
            root = document.getRootElement();
            projectName = root.getAttributeValue("name");
            Element scanPositions = root.getChild("scanpositions");
            String folderScanPositions = scanPositions.getAttributeValue("fold");
            List<Element> childrens = scanPositions.getChildren("scanposition");
            popMatrix = extractMat4D(root.getChild("pop").getChildText("matrix"));
            
            //scan id
            int scanCount = 0;
            
            for(Element child:childrens){
                Scans rxp = new Scans();
                
                rxp.setName(child.getAttributeValue("name"));
                rxp.setFold(child.getAttributeValue("fold"));
                
                Element registeredElement = child.getChild("registered");
                
                if(registeredElement != null){
                    
                    if(Integer.valueOf(registeredElement.getText()) == 1){
                        
                        Element singlescans = child.getChild("singlescans");
                        String singlescansFold = singlescans.getAttributeValue("fold");
                        Map<Integer, RxpScan> scanList = new HashMap<>();

                        List<Element> scans = singlescans.getChildren("scan");

                        Element sop = child.getChild("sop");
                        Matrix4d sopMatrix = extractMat4D(sop.getChildText("matrix"));
                        rxp.setSopMatrix(sopMatrix);

                        int compteur = 0;
                        for(Element sc:scans){

                            RxpScan scan = new RxpScan();
                            scan.setName(sc.getAttributeValue("name"));
                            scan.setFileName(sc.getChildText("file"));
                            String rspFilePathOnly = rspFile.getAbsolutePath().substring(0,rspFile.getAbsolutePath().lastIndexOf(File.separator));

                            scan.setAbsolutePath(rspFilePathOnly+File.separator+folderScanPositions+File.separator+rxp.getFold()+File.separator+singlescansFold+File.separator+scan.getFileName());
                            scanList.put(scanCount, scan);

                            if(scan.getName().contains(".mon")){
                                rxp.setRxpLiteFile(new File(scan.getAbsolutePath()));
                                rxp.setScanLite(scan);
                            }else{
                                rxp.setScanFull(scan);
                            }
                            scan.setFile(new File(scan.getAbsolutePath()));
                            scan.setSopMatrix(sopMatrix);


                            compteur++;
                            scanCount++;
                        }

                        rxp.setScanList(scanList);

                        rxpList.add(rxp);
                    }else{
                        //logger.info("Scan "+ rxp.getName() +" skipped cause unregistered");
                    }
                }
                
            }
            
        } catch (JDOMException ex) {
            throw new JDOMException("error parsing or reading rsp: "+rspFile.getAbsolutePath(), ex);
        }catch(IOException ex){
            throw new IOException("error parsing or reading rsp: "+rspFile.getAbsolutePath(), ex);
        }
    }
}
