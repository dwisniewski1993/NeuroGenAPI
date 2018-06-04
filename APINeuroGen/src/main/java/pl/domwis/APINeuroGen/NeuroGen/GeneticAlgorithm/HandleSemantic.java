package pl.domwis.APINeuroGen.NeuroGen.GeneticAlgorithm;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class HandleSemantic {
    private String pathfile;
    private File inputFile;
    private DocumentBuilderFactory dbFactory;
    private DocumentBuilder dbBuilder;
    private Document doc;
    private NodeList unitList;
    private int learningPathLenght;

    public HandleSemantic(String pathfile) throws ParserConfigurationException, IOException, SAXException {
        this.pathfile = pathfile;
        this.inputFile = new File(this.pathfile);
        this.dbFactory = DocumentBuilderFactory.newInstance();
        this.dbBuilder = this.dbFactory.newDocumentBuilder();
        this.doc = this.dbBuilder.parse(this.inputFile);
        this.doc.getDocumentElement().normalize();
        this.unitList = this.doc.getElementsByTagName("UNIT");
        this.learningPathLenght = this.unitList.getLength();
    }

    public int getLearningPathLenght(){return this.learningPathLenght;}

    public int getUnitDifficultyLevel(int elementID){
        Node nUnit = this.unitList.item(elementID-1);
        if (nUnit.getNodeType()==Node.ELEMENT_NODE){
            Element eElement = (Element) nUnit;
            return Integer.parseInt(eElement.getElementsByTagName("DIFFICULTY_LEVEL").item(0).getTextContent());
        }
        else {return 0;}
    }

    public int getDistanceToElement(int elementID, int searchElementID){
        Node nUnit = this.unitList.item(elementID-1);
        if (nUnit.getNodeType()==Node.ELEMENT_NODE){
            Element eElement = (Element) nUnit;
            Element distEl = (Element) eElement.getElementsByTagName("DISTANCE").item(0);
            int flag = 0;
            for (int i=1; i<=this.getLearningPathLenght()-1;i++){
                Element id = (Element) distEl.getElementsByTagName("FROM").item(i-1);
                try{
                    if (searchElementID==Integer.parseInt(id.getAttribute("id"))){
                        flag = i-1;
                    }
                }
                catch (NumberFormatException e){
                    System.out.println(e);
                }
            }
            return Integer.parseInt(distEl.getElementsByTagName("FROM").item(flag).getTextContent());
        }
        else {return 0;}
    }

    public Set<Integer> getIDArrays(){
        ArrayList<Integer> idList = new ArrayList<>();
        for (int i=0; i<getLearningPathLenght();i++){
            Node nUnit = this.unitList.item(i);
            if (nUnit.getNodeType()==Node.ELEMENT_NODE){
                Element eElement = (Element) nUnit;
                for (int j=0;j<getLearningPathLenght();j++){
                    idList.add(Integer.parseInt(eElement.getAttribute("id")));
                }
            }
        }
        Set<Integer> ids = new HashSet<Integer>(idList);
        return ids;
    }
}
