package net.vaagen.fourinarow.geneticalgorithm;

import net.vaagen.fourinarow.NeuralPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Magnus on 2/1/2016.
 */
public class GeneticAlgorithm {

    // TODO : They are receiving genes out of nowhere? The should be no more genes than 37..

    public static final int WIN_PRIZE = 13;
    public static final int TIE_PRIZE = 6;
    public static final int LOSE_PRIZE = 0;

    private int population = 1000;
    private int amountOfKeepers = 32;
    // There should max be 108 genes, so this shouldn't need to be too high
    private int amountOfMatches = 1000;

    private int echos = 100;

    private Gene[] genes = new Gene[population];

    public static void main(String[] args) {
        new GeneticAlgorithm();
    }

    public GeneticAlgorithm() {
        initializeGenes();
/*
        Gene gene = new Gene(new NeuralPlayer());
        for (int i = 0; i < 1000000; i++)
            gene.playMatch();
        System.out.println("Fitness: " + gene.getFitness());
        gene = gene.clone();
        for (int i = 0; i < 1000000; i++)
            gene.playMatch();
        System.out.println("Fitness: " + gene.getFitness());*/



        System.out.println("Starting GeneticAlgorithm using Neural Networks to learn 'Four In A Row'!");
        System.out.println("Attributes set for evolution");
        System.out.println("   WIN/TIE/LOSE punishments set at [" + WIN_PRIZE + ", " + TIE_PRIZE + ", " + LOSE_PRIZE + "] respectively.");
        System.out.println("   Population: [" + population + "], AmountOfKeepers: [" + amountOfKeepers + "], AmountOfMatches: [" + amountOfMatches + "].");
        System.out.println("Echoing for " + echos + " turns.");
        System.out.println("============================================================");
        System.out.println("");
        System.out.println("");

        int[] keepers = new int[1];
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < echos; i++) {
            if (i != 0)
                System.out.println("Echo #" + i + ", Last Calculation: " + (System.currentTimeMillis() - startTime) / 1000 + "s");

            startTime = System.currentTimeMillis();

            playMatches(); // TODO : This takes time!
            keepers = removeScum();
            crossBreed(keepers);
            //mutate(keepers);
        }
    }

    private void initializeGenes() {
        for (int i = 0; i < genes.length; i++)
            genes[i] = new Gene(new NeuralPlayer());

        if (population < amountOfKeepers)
            System.out.println("WARNING! The population is less than the amount of keepers!");
    }

    private void playMatches() {
        for (int p = 0; p < genes.length; p++ ) {
            Gene gene = genes[p];
            for (int i = 0; i < amountOfMatches; i++)
                gene.playMatch();
            //System.out.println(gene.getFitness());
            if ((p+1) % 100 == 0 && false)
                System.out.println("100 more genes played their matches!");
        }
    }

    private int[] removeScum() {
        // Just keeping their positions
        int[] keepers = calculateKeepers(amountOfKeepers);

        for (int p = 0; p < genes.length; p++) {
            boolean shouldKeep = false;
            for (int k = 0; k < keepers.length; k++) {
                if (p == keepers[k]) {
                    shouldKeep = true;
                    Gene player = genes[keepers[k]];
                    System.out.println("Keeping player #" + p + ", because it had a fitness of " + player.getFitness());
                    break;
                }
            }

            if (!shouldKeep) {
                //System.out.println("Removing player #" + p);
                genes[p] = null;
            }
        }

        return keepers;
    }

    private void crossBreed(int[] keepers) {
        List<Gene> crossBredGenes = new ArrayList<>();

        int amountOfCrossBreeds = population - amountOfKeepers;
        int amountMade = 0;
        for (int k = 0; k < keepers.length; k++) {
            Gene gene = genes[keepers[k]];

            for (int k2 = 0; k2 < keepers.length; k2++) {
                if (amountMade < amountOfCrossBreeds) {
                    Gene crossBredGene = gene.crossBrede(genes[keepers[k2]]);
                    crossBredGenes.add(crossBredGene);
                    amountMade++;
                }
            }
        }

        crossBredGenes = mutate(crossBredGenes);

        for (int g = 0, i = 0; g < genes.length; g++) {
            if (genes[g] == null) {
                Gene gene;
                if (i < crossBredGenes.size())
                    gene = crossBredGenes.get(i);
                else
                    gene = mutate(crossBredGenes.get(new Random().nextInt(crossBredGenes.size()))); // TODO : Maybe power-mutate?

                genes[g] = gene;
                i++;
            } else
                this.genes[g] = this.genes[g].clone(); // This removes clones, and makes sure the victory ration is correct
        }
    }

    private List<Gene> mutate(List<Gene> genes) {
        Random random = new Random();
        for (Gene gene : genes) {
            gene.mutate();
        }

        return genes;
    }

    private Gene mutate(Gene gene) {
        return gene.mutate();
    }
/*
    private void mutate(int[] keepers) {
        for (int k = 0; k < keepers.length; k++) {
            Gene[] players = mutateGene(this.genes[keepers[k]], population / amountOfKeepers - 1);

            for (int p = 0, cc = 0; p < this.genes.length; p++) {
                if (this.genes[p] == null) {
                    if (cc < players.length) {
                        this.genes[p] = players[cc];
                        cc++;
                    }
                } else
                    this.genes[p] = this.genes[p].clone(); // This removes clones, and makes sure the victory ration is correct
            }
        }
    }

    private Gene[] mutateGene(Gene gene, int amountOfMutations) {
        Gene[] mutations = new Gene[amountOfMutations];
        for (int m = 0; m < mutations.length; m++) {
            mutations[m] = gene.clone().mutate();
            //System.out.println("Original player had " + player.genes.size() + " genes, the mutant has " + mutations[m].genes.size() + " genes.");
        }

        return mutations;
    }*/

    private int[] calculateKeepers(int amountOfKeepers) {
        int[] keepers = new int[amountOfKeepers];
        // MAKE SURE THE KEEPERS ARE NOT EQUAL AT THE BEGINNING
        for (int k = 0; k < keepers.length; k++)
            keepers[k] = k;

        for (int p = 0; p < genes.length; p++) {
            int score = genes[p].getFitness();

            boolean alreadyKeeper = false;
            int lowestKeeperPosition = 0;
            for (int k = 0; k < keepers.length; k++) {
                int keeperScore = genes[keepers[k]].getFitness();

                if (p == keepers[k]) {
                    alreadyKeeper = true;
                    continue;
                }

                if (genes[keepers[lowestKeeperPosition]].getFitness() > keeperScore) {
                    //System.out.println("Setting lowest keeping score " + k + " from " + lowestKeeperPosition);
                    lowestKeeperPosition = k;
                }
            }

            if (!alreadyKeeper && score > genes[keepers[lowestKeeperPosition]].getFitness()) {
                //System.out.println("Changing keeper from " + keepers[lowestKeeperPosition] + " to " + p);
                keepers[lowestKeeperPosition] = p;
            }
        }

        return keepers;
    }

}
