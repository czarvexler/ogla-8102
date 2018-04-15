import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class Outcast {

    private final WordNet wordnet;

    public Outcast(WordNet wordnet)  {
        this.wordnet = wordnet;
    }
    public String outcast(String[] nouns) {
        int maxDistance = -1;
        String nounForMaxDistance = "";
        for (final String noun : nouns) {
            final int nounDistance = Arrays.stream(nouns).mapToInt(nounB -> wordnet.distance(noun, nounB)).sum();
            if (nounDistance > maxDistance) {
                maxDistance = nounDistance;
                nounForMaxDistance = noun;
            }
        }
        return nounForMaxDistance;
    }
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}