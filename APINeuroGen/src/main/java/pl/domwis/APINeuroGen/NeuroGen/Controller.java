package pl.domwis.APINeuroGen.NeuroGen;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;
import pl.domwis.APINeuroGen.NeuroGen.GeneticAlgorithm.GeneticAlgorithm;
import pl.domwis.APINeuroGen.NeuroGen.NeuralNetwork.NeuralNetwork;
import pl.domwis.APINeuroGen.NeuroGen.exceptions.DataNotFoundException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@RestController
public class Controller {

    @RequestMapping(value = "/importdatasets", method = RequestMethod.POST)
    public String importDatasets(@RequestParam(value = "courseName", defaultValue = "tabliczka mnozenia") String courseName,
                                 @RequestParam(value = "datasetFile") MultipartFile datasetFile,
                                 @RequestParam(value = "semanticFile", required = false) MultipartFile semanticFile) throws IOException {
        File newDataset = new File(courseName + " dataset.csv");
        newDataset.createNewFile();
        writeMultiPartFIleToFile(datasetFile, newDataset);

        if (semanticFile != null) {
            File newSemantic = new File(courseName + " semantic.xml");
            newSemantic.createNewFile();
            writeMultiPartFIleToFile(semanticFile, newSemantic);
        }
        return "DONE " + newDataset.getAbsolutePath();
    }

    private void writeMultiPartFIleToFile(@RequestParam(value = "datasetFile") MultipartFile datasetFile, File newDataset) throws IOException {
        FileOutputStream fos = new FileOutputStream(newDataset);
        fos.write(datasetFile.getBytes());
        fos.close();
    }

    @RequestMapping(value = "/neuralnetwork", method = RequestMethod.POST)
    public List neuralNetwork(@RequestParam(value = "courseName", defaultValue = "tabliczka mnozenia") String courseName,
                              @RequestParam(value = "nIN", defaultValue = "10") int nIn,
                              @RequestParam(value = "nOut", defaultValue = "10") int nOut,
                              @RequestParam(value = "sample") String sample) throws IOException,
            InterruptedException {

        boolean datasetExist = Files.exists(Paths.get(courseName + " dataset.csv"));

        int nEpochs = 100;
        int nHiddenNodes = 100;
        String dataset = courseName + " dataset.csv";

        if (!datasetExist){
            throw new DataNotFoundException("Brak pliku: " + dataset);
        }

        String[] strValues = sample.split(",");
        float[] values = new float[strValues.length];
        for (int i = 0; i < strValues.length; i++) {
            values[i] = Integer.parseInt(strValues[i]);
        }

        INDArray pred = Nd4j.create(values);

        System.out.println("NeuroGen: NN");
        NeuralNetwork nn = new NeuralNetwork(courseName, nEpochs, nIn, nOut, nHiddenNodes, dataset);
        List prediction = nn.getPredictionList(pred);

        return prediction;
    }

    @RequestMapping(value = "/geneticalgorithm", method = RequestMethod.POST)
    public List geneticAlgorithm(@RequestParam(value = "courseName", defaultValue = "tabliczka mnozenia") String courseName,
                                 @RequestParam(value = "prediction") Set<Integer> prediction) throws
            ParserConfigurationException, SAXException, IOException {

        boolean semanticExist = Files.exists(Paths.get(courseName + " semantic.xml"));

        int populationSize = 50;
        int maxPhenotypeAge = 20;
        double crossoverPropability = 0.6;
        double mutationPropability = 0.2;
        int numberOfGenerations = 100;
        List best;

        if (!semanticExist){
            throw new DataNotFoundException("Brak pliku: " + courseName + " semantic.xml");
        }

        System.out.println("NeuroGen: GA");
        GeneticAlgorithm genAlg = new GeneticAlgorithm(courseName, populationSize, maxPhenotypeAge, crossoverPropability,
                mutationPropability, numberOfGenerations, prediction);
        best = genAlg.getBestList();

        return best;
    }

    @RequestMapping(value = "/neurogen", method = RequestMethod.POST)
    public List neurogen(@RequestParam(value = "courseName", defaultValue = "tabliczka mnozenia") String courseName,
                         @RequestParam(value = "nIN", defaultValue = "10") int nIn,
                         @RequestParam(value = "nOut", defaultValue = "10") int nOut,
                         @RequestParam(value = "sample") String sample) throws IOException, InterruptedException,
            ParserConfigurationException, SAXException {

        boolean datasetExist = Files.exists(Paths.get(courseName + " dataset.csv"));
        boolean semanticExist = Files.exists(Paths.get(courseName + " semantic.xml"));

        int nEpochs = 100;
        int nHiddenNodes = 100;
        String dataset = courseName + " dataset.csv";

        int populationSize = 50;
        int maxPhenotypeAge = 20;
        double crossoverPropability = 0.6;
        double mutationPropability = 0.2;
        int numberOfGenerations = 100;
        List best;

        if (!datasetExist){
            throw new DataNotFoundException("Brak pliku: " + dataset);
        }

        if (!semanticExist){
            throw new DataNotFoundException("Brak pliku: " + courseName + " semantic.xml");
        }

        String[] strValues = sample.split(",");
        float[] values = new float[strValues.length];
        for (int i = 0; i < strValues.length; i++) {
            values[i] = Integer.parseInt(strValues[i]);
        }

        INDArray pred = Nd4j.create(values);

        System.out.println("NeuroGen: FULL");
        NeuralNetwork neuralnet = new NeuralNetwork(courseName, nEpochs, nIn, nOut, nHiddenNodes, dataset);
        Set<Integer> predicion = neuralnet.getPredictionSet(pred);

        GeneticAlgorithm geneticalg = new GeneticAlgorithm(courseName, populationSize, maxPhenotypeAge,
                crossoverPropability, mutationPropability, numberOfGenerations, predicion);

        best = geneticalg.getBestList();

        return best;
    }

    @RequestMapping(value = "/requesttraining", method = RequestMethod.POST)
    public void trainnet(@RequestParam(value = "courseName", defaultValue = "tabliczka mnozenia") String courseName,
                         @RequestParam(value = "nIN", defaultValue = "10") int nIn,
                         @RequestParam(value = "nOut", defaultValue = "10") int nOut) throws IOException, InterruptedException {

        int nEpochs = 100;
        int nHiddenNodes = 100;
        String dataset = courseName + " dataset.csv";

        System.out.println("NeuroGen: TRAIN NETWORK");
        NeuralNetwork nn = new NeuralNetwork(courseName, nEpochs, nIn, nOut, nHiddenNodes, dataset);
        nn.requestTraining();
    }

    @RequestMapping(value = "/availablemethods", method = RequestMethod.GET)
    public Map<String, Object> getavailablemethods(@RequestParam(value = "courseName") String courseName){

        boolean datasetExist = Files.exists(Paths.get(courseName + " dataset.csv"));
        boolean semanticExist = Files.exists(Paths.get(courseName + " semantic.xml"));
        Map<String, Object> mapedList = new HashMap<>();

        List<String> methods = new ArrayList<>();///neuralnetwork

        if (datasetExist){
            methods.add("neuralnetwork");
        }
        if (semanticExist){
            methods.add("geneticalgorithm");
        }
        if (datasetExist && semanticExist){
            methods.add("neurogen");
        }

        mapedList.put("methods", methods);

        return mapedList;
    }
}
