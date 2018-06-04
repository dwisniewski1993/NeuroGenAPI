package pl.domwis.APINeuroGen.NeuroGen;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;
import pl.domwis.APINeuroGen.NeuroGen.GeneticAlgorithm.GeneticAlgorithm;
import pl.domwis.APINeuroGen.NeuroGen.NeuralNetwork.NeuralNetwork;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
public class Controller {

    @RequestMapping("/importDatasets")
    public String importDatasets(@RequestParam(value="courseName", defaultValue="tabliczka mnozenia") String courseName) {
        return "DONE";
    }

    @RequestMapping("/NeuralNetwork")
    public Set<Integer> neuralNetwork(@RequestParam(value = "courseName", defaultValue = "tabliczka mnozenia") String courseName,
                                      @RequestParam(value = "nIN", defaultValue = "10") int nIn,
                                      @RequestParam(value = "nOut", defaultValue = "10") int nOut,
                                      @RequestParam(value = "sample") String sample) throws IOException, InterruptedException {

        int nEpochs = 100;
        int nHiddenNodes = 100;
        String dataset = courseName+" dataset.csv";

        String[] strValues = sample.split(",");
        float[] values = new float[strValues.length];
        for (int i =0; i<strValues.length; i++){
            values[i] = Integer.parseInt(strValues[i]);
        }

        INDArray pred = Nd4j.create(values);

        System.out.println("NeuroGen: NN");
        NeuralNetwork nn = new NeuralNetwork(courseName, nEpochs, nIn, nOut, nHiddenNodes, dataset);
        Set<Integer> prediction = nn.getPrediction(pred);

        return prediction;
    }

    @RequestMapping("/GeneticAlgorithm")
    public List geneticAlgorithm(@RequestParam(value = "courseName", defaultValue = "tabliczka mnozenia") String courseName,
                                 @RequestParam(value = "prediction") Set<Integer> prediction) throws ParserConfigurationException, SAXException, IOException {

        int populationSize = 50;
        int maxPhenotypeAge = 20;
        double crossoverPropability = 0.6;
        double mutationPropability = 0.2;
        int numberOfGenerations = 100;

        System.out.println("NeuroGen: GA");
        GeneticAlgorithm genAlg = new GeneticAlgorithm(courseName, populationSize, maxPhenotypeAge, crossoverPropability, mutationPropability, numberOfGenerations, prediction);

        return genAlg.getBestList();
    }
}