import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.*;
import java.util.Map.*;

public class RCV {
    /**
     * This function determines whether RCV or STV should be used depending on the number of winners
     * @param ballots A 2D matrix of the ballots, with each individual ballot represented by an ArrayList
     * @param numWinners The number of desired winners for this election
     */
    public static void calculateRCV(ArrayList<ArrayList<String>> ballots, int numWinners) {
        if (numWinners > 1) {
            calculateSingleTransferableVote(ballots, numWinners);
        }
        else if (numWinners == 1) {
            calculateInstantRunoff(ballots);
        }
        else {
            System.out.println("Silly goose, you have to have at least 1 winner");
        }
    }

    /**
     * Calculates ranked choice election with a single winner, uses instant runoff algorithm
     * @param ballots A 2D matrix of the ballots, with each individual ballot represented by an ArrayList
     */
    private static void calculateInstantRunoff(ArrayList<ArrayList<String>> ballots) {
        int minSize = Integer.SIZE;
        do {
            HashMap<String, Integer> firstPlaces = new HashMap<>();

            for (ArrayList<String> ballot : ballots) {
                String first = ballot.get(0);
                if (firstPlaces.containsKey(first)) {
                    firstPlaces.put(first, firstPlaces.get(first) + 1);
                } else {
                    firstPlaces.putIfAbsent(first, 1);
                }
            }

            HashMap<String, Integer> sorted = firstPlaces.entrySet().stream()
                    .sorted(Entry.comparingByValue())
                    .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
                            (e1, e2) -> e1, LinkedHashMap::new));

            String temp = (String) sorted.keySet().toArray()[0];
            int min = sorted.get(temp);

            for(ArrayList<String> ballot : ballots) {
                ballot.removeIf(string -> !firstPlaces.containsKey(string) || firstPlaces.get(string) == min);
                if (ballot.size() < minSize) {
                    minSize = ballot.size();
                }
            }

            temp = (String) sorted.keySet().toArray()[sorted.size() - 1];
            System.out.println(sorted);
            if (sorted.get(temp) > ballots.size() / 2) {
                break;
            }
        } while (minSize > 0);
    }

    /**
     * TODO: implement an algorithm for STV, use fractional votes
     * @param ballots A 2D matrix of the ballots, with each individual ballot represented by an ArrayList
     * @param numWinners The number of desired winners for this election
     */
    private static void calculateSingleTransferableVote(ArrayList<ArrayList<String>> ballots, int numWinners) {
        //int numBallots = ballots.size();
        //int quota = (int)((double)numBallots / ((double)numWinners + 1)) + 1;
        //int numElected = 0;
    }

    /**
     * Driver function which creates a 2D ArrayList of the ballots from a tab delimited txt files
     * @param args To be left empty
     * @throws FileNotFoundException In the case the tab delimited txt file of ballots is not found
     */
    public static void main(String[] args) throws FileNotFoundException {
        ArrayList<ArrayList<String>> ballots = new ArrayList<>();

        //TODO: File path must be changed for each different user and file
        //This program only works with tab delimited txt files, which can be saved in excel sheets
        File excel = new File("D:\\Nikhaz\\Downloads\\2021electionsVP.txt");
        Scanner fileScanner = new Scanner(excel);

        while(fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            ballots.add(new ArrayList<>(Arrays.asList(line.split("\\t"))));
        }
        fileScanner.close();
        //TODO: Change this variable to represent the number of winners you have for this election
        int numWinners = 1;

        calculateRCV(ballots, numWinners);
    }
}
