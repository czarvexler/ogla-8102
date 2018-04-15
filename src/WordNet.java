import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author aleksander.veksler
 * @since Apr-2018
 */
public class WordNet {

    private final Map<Integer, Synset> synsetMap;
    private final Digraph digraph;
    private final SAP sap;

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
            this.sap = new SAP(this.digraph);
        } catch (final Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void parseSynsets(final String[] synsetLines) {
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

    private void parseHypernymsFile(final String hypernyms) {
        final In hypernymsIn = new In(hypernyms);
        final String[] hypernymLines = hypernymsIn.readAllLines();
        Arrays.stream(hypernymLines).forEach(hypernymline -> {
            final String[] parsedLine = hypernymline.split(",");
            final int sourceSynsetId = Integer.parseInt(parsedLine[0]);
            final Synset sourceSynset = validateAndGetSynsetFromId(sourceSynsetId);
            for (int i = 1; i < parsedLine.length; i++) {
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
        final Set<Synset> synsetsA = findSynsetFromString(nounA);
        final Set<Integer> verticiesA = synsetsA.stream().map(synset -> synset.vertexId).collect(Collectors.toSet());

        final Set<Synset> synsetsB = findSynsetFromString(nounB);
        final Set<Integer> verticiesB = synsetsB.stream().map(synset -> synset.vertexId).collect(Collectors.toSet());

        return this.sap.length(verticiesA, verticiesB);
    }

    private Set<Synset> findSynsetFromString(final String noun) {
        if (noun == null) {
            throw new IllegalArgumentException("Null word not allowed");
        }
        final Set<Synset> res = this.synsetMap.values().stream()
                .filter(synset -> synset.synonyms.contains(noun)).collect(Collectors.toSet());
        if (res.isEmpty()) {
            throw new IllegalArgumentException("Found no Synsets containing " + noun);
        }
        return res;
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(final String nounA, final String nounB) {
        final Set<Synset> synsetsA = findSynsetFromString(nounA);
        final Set<Integer> verticiesA = synsetsA.stream().map(synset -> synset.vertexId).collect(Collectors.toSet());

        final Set<Synset> synsetsB = findSynsetFromString(nounB);
        final Set<Integer> verticiesB = synsetsB.stream().map(synset -> synset.vertexId).collect(Collectors.toSet());

        return vertexIdtoSynset(this.sap.ancestor(verticiesA, verticiesB))
                .synonyms.stream().collect(Collectors.joining(" "));
    }

    private Synset vertexIdtoSynset(final int vertexId) {
        return this.synsetMap.values().stream().filter(synset -> synset.vertexId == vertexId).findAny().orElseThrow(
                () -> new IllegalArgumentException("Could not find Synset matching specified vertex id " + vertexId));
    }

    // do unit testing of this class
    public static void main(final String[] args) {
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
