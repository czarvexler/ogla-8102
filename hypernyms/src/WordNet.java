import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;

import java.util.*;
import java.util.stream.Collectors;

public class WordNet {

    private final Map<Integer, Synset> synsetMap;
    private final Digraph digraph;

    // constructor takes the name of the two input files
    public WordNet(final String synsets, final String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException();
        }
        try {
            final In synsetsIn = new In(synsets);
            final String[] synsetLines = synsetsIn.readAllLines();
            this.digraph = new Digraph(synsetLines.length);
            this.synsetMap = new HashMap<>(synsetLines.length);

            parseSynsets(synsetLines);
            parseHypernymsFile(hypernyms);
        }
        catch (final Exception e) {
            throw new IllegalArgumentException(e);
        }

    }

    private void parseSynsets(String[] synsetLines) {
        int vertexIdAllocator = 0;
        for (final String synsetLine : synsetLines) {
            final String[] parsedLine = synsetLine.split(",");
            final Integer id = Integer.parseInt(parsedLine[0]);
            final List<String> synonyms = Arrays.asList(parsedLine[1].split(" "));
            final Synset synset = new Synset(vertexIdAllocator, synonyms);
            this.synsetMap.put(id, synset);
            vertexIdAllocator++;
        }
    }

    private void parseHypernymsFile(String hypernyms) {
        final In hypernymsIn = new In(hypernyms);
        final String[] hypernymLines = hypernymsIn.readAllLines();
        Arrays.stream(hypernymLines).forEach(hypernymline -> {
            final String[] parsedLine = hypernymline.split(",");
            final int sourceSynsetId = Integer.parseInt(parsedLine[0]);
            final Synset sourceSynset = validateAndGetSynsetFromId(sourceSynsetId);
            for (int i =1; i < parsedLine.length; i++) {
                final int targetSynsetId = Integer.parseInt(parsedLine[i]);
                this.digraph.addEdge(sourceSynset.vertexId, targetSynsetId);
            }
        });
    }

    private Synset validateAndGetSynsetFromId(final int synsetId) {
        final Synset res = this.synsetMap.get(synsetId);
        if (res == null) {
            throw new IllegalArgumentException("Could not find a synset with id " + synsetId);
        }
        return res;
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return getNouns();
    }

    // is the word a WordNet noun?
    public boolean isNoun(final String word) {
        if (word == null) {
            throw new IllegalArgumentException("Null word not allowed");
        }
        return getNouns().contains(word);
    }

    private Collection<String> getNouns() {
        return this.synsetMap.values().stream().flatMap(synset -> synset.synonyms.stream()).collect(Collectors.toSet());
    }

    // distance between nounA and nounB (defined below)
    public int distance(final String nounA, final String nounB) {
        final Set<Synset> synsetA = findSynsetFromString(nounA);
        final Set<Synset> synsetB = findSynsetFromString(nounB);
        final BreadthFirstDirectedPaths breadthFirstDirectedPaths = new BreadthFirstDirectedPaths(this.digraph, synsetA.stream().map(synset -> synset.vertexId).collect(Collectors.toSet()));
        return synsetB.stream().map(synset -> breadthFirstDirectedPaths.distTo(synset.vertexId)).min(Integer::compareTo)
                .orElseThrow(() -> new IllegalStateException("No Path exsists between a synset containing " + nounA + " and a synset containing " + nounB));
    }

    private Set<Synset> findSynsetFromString(final String noun) {
        if (noun == null) {
            throw new IllegalArgumentException("Null word not allowed");
        }
        final Set<Synset> res = this.synsetMap.values().stream().filter(synset -> synset.synonyms.contains(noun)).collect(Collectors.toSet());
        if (res.isEmpty()) {
            throw new IllegalArgumentException("Found no Synsets containing " + noun);
        }
        return res;
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(final String nounA, final String nounB) {

    }

    private Synset vertexIdtoSynset(final int vertexId) {
        return this.synsetMap.values().stream().filter(synset -> synset.vertexId == vertexId).findAny().orElseThrow(() -> new IllegalArgumentException());
    }

    // do unit testing of this class
    public static void main(final String[] args) {
        //todo clear before submission?
        final WordNet test = new WordNet("file:///C:/Users/Kingsgambit/code/algorithms-2018/week1/week1-ogla-2018/hypernyms/wordnet/synsets.txt","file:///C:/Users/Kingsgambit/code/algorithms-2018/week1/week1-ogla-2018/hypernyms/wordnet/hypernyms.txt");
        final String testRes = test.digraph.toString();
        final String[] testResParsed = testRes.split(System.lineSeparator());
        final String expectedRes = new Digraph(new In("file:///C:/Users/Kingsgambit/code/algorithms-2018/week1/week1-ogla-2018/hypernyms/wordnet/digraph-wordnet.txt")).toString();
        final String[] expectedResParsed = expectedRes.split(System.lineSeparator());
        for (int i = 0 ; i < expectedResParsed.length; i++) {
            final String expectedString = expectedResParsed[i];
            final String actualString = testResParsed[i];
            final char[] expected = expectedString.toCharArray();
            Arrays.sort(expected);
            final char[] actual = actualString.toCharArray();
            Arrays.sort(actual);

            if (!Arrays.equals(expected, actual)) {
                System.out.println("Expected " + expectedString);
                System.out.println("Actual " + actualString);
                break;
            }
        }
        System.out.println("Testing for presence of bird produces " + test.isNoun("bird"));
//        (distance = 23) white_marlin, mileage
//                (distance = 33) Black_Plague, black_marlin
//                (distance = 27) American_water_spaniel, histology
//                (distance = 29) Brown_Swiss, barrel_roll
        System.out.println("Number of nounces expected 119,188 and is " + ((Set<String>) test.nouns()).size());

        System.out.println("Distance Test 1 " + test.distance("1530s", "decade") + " is 1");
        System.out.println("Distance Test 2 " + test.distance("white_marlin", "white_marlin") + " is 0");
        System.out.println("Distance Test 3 " + test.distance("white_marlin", "entity") + " is ???");
        System.out.println("Distance Test 3 " + test.distance("mileage", "entity") + " is ???");
        final SAP sap = new SAP(test.digraph);
        System.out.println("SAP Length 1 " + sap.length(Arrays.asList(80917),Arrays.asList(54384, 54385, 54386)) + " is 23");
        System.out.println("SAP Length 2 " + sap.length(2134, 24524) + " is 33");
        System.out.println("SAP Length 3 " + sap.length(709,46146) + " is 27");
        System.out.println("SAP Length 4 " + sap.length(2533,23170) + " is 29");
        System.out.println("SAP Length 5 " + sap.length(Arrays.asList(81679, 81680, 81681, 81682), Arrays.asList(24306, 24307, 25293, 33764, 70067)) + " is 5");
        //System.out.println("SAP Length 5 " + sap.length(test.helper("individual"), test.helper("edible_fruit")) + " is 7 or 10");


        System.out.println("SAP Ancestor 1 " + sap.ancestor(Arrays.asList(80917),Arrays.asList(54384, 54385, 54386)) + " is entity");
        System.out.println("SAP Ancestor 2 " + sap.ancestor(2134, 24524) + " is entity");
        System.out.println("SAP Ancestor 3 " + sap.ancestor(709,46146) + " is entity");
        System.out.println("SAP Ancestor 4 " + sap.ancestor(2533,23170) + " is entity");
        System.out.println("SAP Ancestor 5 " + sap.ancestor(Arrays.asList(81679, 81680, 81681, 81682), Arrays.asList(24306, 24307, 25293, 33764, 70067)) + " is 20743");
        //System.out.println("SAP Ancestor 5 " + sap.ancestor(test.helper("individual"), test.helper("edible_fruit")) + " is 60600");

    }

    private static class Synset {

        private final int vertexId;
        private final Collection<String> synonyms;

        Synset(final int vertexId, final Collection<String> synonyms) {
            this.vertexId = vertexId;
            this.synonyms = synonyms;
        }
    }
}