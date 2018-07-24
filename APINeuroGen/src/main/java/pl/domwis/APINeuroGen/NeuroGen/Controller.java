package pl.domwis.APINeuroGen.NeuroGen;

import org.json.JSONArray;
import org.json.JSONObject;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;
import pl.domwis.APINeuroGen.NeuroGen.GeneticAlgorithm.GeneticAlgorithm;
import pl.domwis.APINeuroGen.NeuroGen.NeuralNetwork.NeuralNetwork;
import pl.domwis.APINeuroGen.NeuroGen.exceptions.DataNotFoundException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@RestController
public class Controller {

    @RequestMapping(value = "/importdatasets", method = RequestMethod.POST)
    public String importDatasets(@RequestParam(value = "jsonRaport") String jsonRaport,
                                 @RequestParam(value = "semanticFile", required = false) MultipartFile semanticFile) throws IOException {

        String courseName;

        JSONObject raports = new JSONObject(jsonRaport);

        JSONObject studentRaport = raports.getJSONObject("studentRaport"); // raport studenta
        JSONObject teacherRecommendation = raports.getJSONObject("teacherRecommendation"); // raport rekomendacji nauczyciela
        JSONObject allStudentsRaport = raports.getJSONObject("allStudentsRaport"); // raport studentów

        //JSONObject allStudentsRaportStr = raports.getAllStudentsRaport();
        //JSONObject teacherRecommendationStr = raports.getTeacherRecommendation();

        //JSONObject jsonObject = new JSONObject(allStudentsRaportStr);
        //System.out.println(allStudentsRaportStr);
        //JSONObject resultval = allStudentsRaportStr.getJSONObject("resultValue");
        //courseName = resultval.getString("course");

        //System.out.println(courseName);

        //parseRaportITS22(courseName, allStudentsRaportStr);
        //deleteEmptyLine(courseName);
        //rekomendacje
        //if (semanticFile != null) {
        //    File newSemantic = new File(courseName + " semantic.xml");
        //    newSemantic.createNewFile();
        //    writeMultiPartFIleToFile(semanticFile, newSemantic);
        //}
        return "{\"status\":\"ok\"}";
    }

    private void writeMultiPartFIleToFile(@RequestParam(value = "datasetFile") MultipartFile file, File newFile) throws IOException {
        FileOutputStream fos = new FileOutputStream(newFile);
        fos.write(file.getBytes());
        fos.close();
    }



    @RequestMapping(value = "/neuralnetwork", method = RequestMethod.POST)
    public List neuralNetwork(@RequestParam(value = "courseName", defaultValue = "tabliczka mnozenia") String courseName,
                              @RequestParam(value = "studentid") int studentid,
                              @RequestParam(value = "jsonRaport") String jsonRaport) throws IOException,
            InterruptedException {

        boolean datasetExist = Files.exists(Paths.get(courseName + " features.csv"));
        boolean datasetExist2 = Files.exists(Paths.get(courseName + " labels.csv"));

        int nEpochs = 100;
        int nHiddenNodes = 100;
        String features_dataset = courseName + " features.csv";
        String labels_dataset = courseName + " labels.csv";

        if (!datasetExist) {
            throw new DataNotFoundException("Brak pliku: " + features_dataset);
        }

        if (!datasetExist2) {
            throw new DataNotFoundException("Brak pliku: " + labels_dataset);
        }

        File ffile = new File(features_dataset);
        Scanner scan1 = new Scanner(ffile);
        int numIn = scan1.nextLine().split(",").length;

        File lfile = new File(labels_dataset);
        Scanner scan2 = new Scanner(lfile);
        int numOut = scan2.nextLine().split(",").length;

        String sample = takeSample(studentid, jsonRaport);

        String[] strValues = sample.split(",");
        float[] values = new float[strValues.length];
        for (int i = 0; i < strValues.length; i++) {
            values[i] = Integer.parseInt(strValues[i]);
        }

        INDArray pred = Nd4j.create(values);

        System.out.println("NeuroGen: NN");
        NeuralNetwork nn = new NeuralNetwork(courseName, nEpochs, numIn, numOut, nHiddenNodes, features_dataset, labels_dataset);
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

        if (!semanticExist) {
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
                         @RequestParam(value = "studentid") int studentid,
                         @RequestParam(value = "jsonRaport") String jsonRaport) throws IOException, InterruptedException,
            ParserConfigurationException, SAXException {

        boolean datasetExist = Files.exists(Paths.get(courseName + " features.csv"));
        boolean datasetExist2 = Files.exists(Paths.get(courseName + " labels.csv"));
        boolean semanticExist = Files.exists(Paths.get(courseName + " semantic.xml"));

        int nEpochs = 100;
        int nHiddenNodes = 100;
        String features_dataset = courseName + " features.csv";
        String labels_dataset = courseName + " labels.csv";

        int populationSize = 50;
        int maxPhenotypeAge = 20;
        double crossoverPropability = 0.6;
        double mutationPropability = 0.2;
        int numberOfGenerations = 100;
        List best;

        if (!datasetExist) {
            throw new DataNotFoundException("Brak pliku: " + features_dataset);
        }

        if (!datasetExist2) {
            throw new DataNotFoundException("Brak pliku: " + labels_dataset);
        }

        if (!semanticExist) {
            throw new DataNotFoundException("Brak pliku: " + courseName + " semantic.xml");
        }

        File ffile = new File(features_dataset);
        Scanner scan1 = new Scanner(ffile);
        int numIn = scan1.nextLine().split(",").length;

        File lfile = new File(labels_dataset);
        Scanner scan2 = new Scanner(lfile);
        int numOut = scan2.nextLine().split(",").length;

        String sample = takeSample(studentid, jsonRaport);

        String[] strValues = sample.split(",");
        float[] values = new float[strValues.length];
        for (int i = 0; i < strValues.length; i++) {
            values[i] = Integer.parseInt(strValues[i]);
        }

        INDArray pred = Nd4j.create(values);

        System.out.println("NeuroGen: FULL");
        NeuralNetwork neuralnet = new NeuralNetwork(courseName, nEpochs, numIn, numOut, nHiddenNodes, features_dataset, labels_dataset);
        Set<Integer> predicion = neuralnet.getPredictionSet(pred);

        GeneticAlgorithm geneticalg = new GeneticAlgorithm(courseName, populationSize, maxPhenotypeAge,
                crossoverPropability, mutationPropability, numberOfGenerations, predicion);

        best = geneticalg.getBestList();

        return best;
    }

    @RequestMapping(value = "/requesttraining", method = RequestMethod.POST)
    public void trainnet(@RequestParam(value = "courseName", defaultValue = "tabliczka mnozenia") String courseName) throws IOException, InterruptedException {

        int nEpochs = 100;
        int nHiddenNodes = 100;
        String features_dataset = courseName + " features.csv";
        String labels_dataset = courseName + " labels.csv";

        boolean datasetExist = Files.exists(Paths.get(courseName + " features.csv"));
        boolean datasetExist2 = Files.exists(Paths.get(courseName + " labels.csv"));

        File ffile = new File(features_dataset);
        Scanner scan1 = new Scanner(ffile);
        int numIn = scan1.nextLine().split(",").length;

        File lfile = new File(labels_dataset);
        Scanner scan2 = new Scanner(lfile);
        int numOut = scan2.nextLine().split(",").length;

        System.out.println("NeuroGen: TRAIN NETWORK");
        NeuralNetwork nn = new NeuralNetwork(courseName, nEpochs, numIn, numOut, nHiddenNodes, features_dataset, labels_dataset);
        nn.requestTraining();
    }

    @RequestMapping(value = "/availablemethods", method = RequestMethod.GET)
    public Map<String, Object> getavailablemethods(@RequestParam(value = "courseName") String courseName) {

        boolean datasetExist = Files.exists(Paths.get(courseName + " features.csv"));
        boolean datasetExist2 = Files.exists(Paths.get(courseName + " labels.csv"));
        boolean semanticExist = Files.exists(Paths.get(courseName + " semantic.xml"));
        Map<String, Object> mapedList = new HashMap<>();

        List<String> methods = new ArrayList<>();

        if (datasetExist && datasetExist2) {
            methods.add("neuralnetwork");
        }
        if (semanticExist) {
            methods.add("geneticalgorithm");
        }
        if (datasetExist && datasetExist2 && semanticExist) {
            methods.add("neurogen");
        }

        mapedList.put("methods", methods);

        return mapedList;
    }

    private void deleteEmptyLine(@RequestParam(value = "couseName") String courseName) throws IOException {
        File file = new File(courseName + " features.csv");
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        byte b;
        long length = randomAccessFile.length();
        if (length != 0) {
            do {
                length -= 1;
                randomAccessFile.seek(length);
                b = randomAccessFile.readByte();
            } while (b != 10 && length > 0);
            randomAccessFile.setLength(length);
            randomAccessFile.close();
        }
    }

    private void parseRaportITS22(@RequestParam(value = "courseName") String courseName,
                                  @RequestParam(value = "jsonRaport") JSONObject jsonRaport
                                  ) throws IOException {
        JSONObject jsonObject = new JSONObject(jsonRaport);

        JSONObject resultval = jsonObject.getJSONObject("resultValue");
        JSONArray students = resultval.getJSONArray("students");

        String featuresFile = courseName + " features.csv";
        FileWriter writer = new FileWriter(featuresFile);

        for (Object studentobj : students) {
            JSONObject student = (JSONObject) studentobj;
            ArrayList<Integer> interactionList = new ArrayList<>();
            JSONArray scos = ((JSONObject) studentobj).getJSONArray("scos");
            for (Object scoobj : scos) {
                JSONObject sco = (JSONObject) scoobj;
                JSONArray interactions = sco.getJSONArray("interactions");
                for (Object interobj : interactions) {
                    JSONObject interaction = (JSONObject) interobj;
                    try {
                        String result = interaction.getString("cmi.interactions.n.result");

                        if (result.equalsIgnoreCase("real")) {
                            String correct_response = interaction.getString("cmi.interactions.n.correct_responses");
                            String learner_response = interaction.getString("cmi.interactions.n.learner_response");
                            if (correct_response.equals(learner_response)) {//Do sprawdzenia
                                interactionList.add(1);
                            } else {
                                interactionList.add(0);
                            }
                        } else {
                            System.out.println(result);
                            if (result.equalsIgnoreCase("incorrect")) {
                                interactionList.add(0);
                            }
                            if (result.equalsIgnoreCase("correct")) {
                                interactionList.add(1);
                            }
                        }

                    } catch (Exception e) {
                    }
                }
            }
            for (int i = 0; i < interactionList.size(); i++) {
                Object[] answer = interactionList.toArray();
                writer.append(answer[i].toString());
                if (i != interactionList.size() - 1) {
                    writer.append(",");
                }
                else {writer.append("\n");}
            }
        }

        writer.flush();
        writer.close();
    }

    private String takeSample(int studentid, String jsonRaport){
        String samp = "";

        ArrayList<Integer> interactionList = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(jsonRaport);

        JSONObject resultval = jsonObject.getJSONObject("resultValue");
        JSONArray students = resultval.getJSONArray("students");

        for (Object studentobj : students){
            JSONObject student = (JSONObject) studentobj;
            int stunid = student.getInt("studentId");
            if (stunid==studentid){
                JSONArray scos = ((JSONObject) studentobj).getJSONArray("scos");
                for (Object scoobj : scos) {
                    JSONObject sco = (JSONObject) scoobj;
                    JSONArray interactions = sco.getJSONArray("interactions");
                    for (Object interobj : interactions) {
                        JSONObject interaction = (JSONObject) interobj;
                        if (interaction.getString("cmi.interactions.n.objectives").equalsIgnoreCase("")) {
                            interactionList.add(0);
                        }
                        try {
                            String result = interaction.getString("cmi.interactions.n.result");

                            if (result.equalsIgnoreCase("real")) {
                                String correct_response = interaction.getString("cmi.interactions.n.correct_responses");
                                String learner_response = interaction.getString("cmi.interactions.n.learner_response");
                                if (correct_response == learner_response) {
                                    interactionList.add(1);
                                } else {
                                    interactionList.add(0);
                                }
                            } else {
                                System.out.println(result);
                                if (result.equalsIgnoreCase("incorrect")) {
                                    interactionList.add(0);
                                }
                                if (result.equalsIgnoreCase("correct")) {
                                    interactionList.add(1);
                                }
                            }

                        } catch (Exception e) {
                        }

                    }
                }
            }
        }
        for (int i=0; i<interactionList.size();i++){
            if (i==0){
                samp = samp + Integer.toString(interactionList.get(i));
            }
            else {samp = samp + ","+Integer.toString(interactionList.get(i));}
        }

        return samp;
    }
}
