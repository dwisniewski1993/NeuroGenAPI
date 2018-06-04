package pl.domwis.APINeuroGen.NeuroGen.GeneticAlgorithm;

import org.jenetics.*;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionStatistics;
import static org.jenetics.engine.EvolutionResult.toBestPhenotype;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GeneticAlgorithm {
    private int populationSize;
    private int maxPhenotypeAge;
    private double crossoverProp;
    private double mutationProp;
    private Set<Integer> idList;
    private int numberOfGenerations;
    private ISeq<Integer> allele;
    private Factory<Genotype<EnumGene<Integer>>> gtf;
    private Engine<EnumGene<Integer>, Integer> engine;
    private EvolutionStatistics<Integer, ?> stats;
    private Phenotype<EnumGene<Integer>, Integer> best;
    private static HandleSemantic ontology;

    public GeneticAlgorithm(String coursename, int popSize, int maxPhenAge, double crossProp, double mutProp, int numOfGen, Set<Integer> idList) throws IOException, SAXException, ParserConfigurationException {
        this.ontology = new HandleSemantic(coursename+" semantic.xml");
        this.populationSize = popSize;
        this.maxPhenotypeAge = maxPhenAge;
        this.crossoverProp = crossProp;
        this.mutationProp = mutProp;
        this.numberOfGenerations = numOfGen;
        this.idList = idList;
        this.allele = ISeq.of(this.idList);
        this.gtf = Genotype.of(PermutationChromosome.of(this.allele));
        this.engine = Engine.builder(GeneticAlgorithm::FF, this.gtf)
                .populationSize(this.populationSize)
                .maximalPhenotypeAge(this.maxPhenotypeAge)
                .survivorsSelector(new RouletteWheelSelector<>())
                .offspringSelector(new TournamentSelector<>())
                .alterers(new PartiallyMatchedCrossover<>(this.crossoverProp), new SwapMutator<>(this.mutationProp))
                .build();
        this.stats = EvolutionStatistics.ofNumber();
        this.best = this.engine.stream().limit(this.numberOfGenerations).peek(this.stats).collect(toBestPhenotype());

    }

    public Phenotype<EnumGene<Integer>, Integer> getBest(){
        return this.best;
    }

    public EvolutionStatistics<Integer, ?> getStats() {
        return this.stats;
    }

    public List getBestList(){
        int size = this.best.getGenotype().getChromosome().length();
        List chromosome = new ArrayList(size);

        for (int i=0; i<size; i++){
            chromosome.add(this.best.getGenotype().getChromosome().getGene(i));
        }

        return chromosome;
    }

    //Fitness Function
    private static int FF(final Genotype<EnumGene<Integer>> gt){
        //Fitness=suma(waga * odległość od(i do i-1) + (1-waga) * poziom trudności

        int fitness = 0;
        double wage = 0.8;
        String [] chromosome = new String [gt.getChromosome().length()];

        for (int i = 0; i < gt.getChromosome().length(); i++){
            chromosome [i] = gt.getChromosome().getGene(i).toString();
        }

        return getFitness(chromosome);
    }

    private static int getFitness(String [] chromosome){
        double fitness = 0;
        double wage = 0.8;

        for (int i=1; i < chromosome.length;i++){
            fitness = fitness + ((wage*ontology.getDistanceToElement(Integer.parseInt(chromosome[i]), Integer.parseInt(chromosome[i-1])))
                    +((1-wage)*ontology.getUnitDifficultyLevel(Integer.parseInt(chromosome[i]))));
        }

        return (int) fitness;
    }
}
