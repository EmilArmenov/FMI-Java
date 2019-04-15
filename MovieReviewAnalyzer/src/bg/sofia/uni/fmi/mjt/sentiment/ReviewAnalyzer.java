package bg.sofia.uni.fmi.mjt.sentiment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class ReviewAnalyzer {
    private Set<String> stopWords;
    private Set<String> reviews;
    private Map<String, WordScoreGenerator> wordsSentiment;

    public ReviewAnalyzer(InputStream stopWordsInput, InputStream reviewsInput) {
        stopWords = new HashSet<>();
        reviews = new HashSet<>();
        wordsSentiment = new HashMap<>();
        loadAnalyzer(stopWordsInput, reviewsInput);
    }

    public void loadAnalyzer(InputStream stopWordsInput, InputStream reviewsInput) {

        try (BufferedReader brStop = new BufferedReader(new InputStreamReader(stopWordsInput));
             BufferedReader brReview = new BufferedReader(new InputStreamReader(reviewsInput))) {
            //stopWords = brStop.lines().collect(Collectors.toSet());
            String line;
            while ((line = brStop.readLine()) != null) {
                if (line.equals("")) {
                    continue;
                }

                String[] splittedStopWords = line.split("[^a-zA-Z0-9]+");
                for (String word: splittedStopWords) {
                    stopWords.add(word.toLowerCase());
                }
            }

            while ((line = brReview.readLine()) != null) {
                if (line.equals("")) {
                    continue;
                }

                int reviewScore = line.charAt(0) - '0';
                int reviewStartPos = 1;
                String review = line.substring(reviewStartPos, line.length()).trim();
                analyzeReview(review, reviewScore);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not load the dataset", e);
        }
    }

    public void analyzeReview(String review, int sentimentValue) {
        int reviewStartPos = 1;
        String[] words = review.split("[^a-zA-Z0-9]+");

        for (String word : words) {
            if (word.equals("") || stopWords.contains(word.toLowerCase())) {
                continue;
            } else if (wordsSentiment.containsKey(word.toLowerCase())) {
                wordsSentiment.get(word.toLowerCase()).updateScore(sentimentValue);
            } else {
                wordsSentiment.put(word.toLowerCase(), new WordScoreGenerator(sentimentValue));
            }
        }

        reviews.add(review);
    }

    public double getScoreOfWord(String word) {
        if (word == null || !wordsSentiment.containsKey(word.toLowerCase())) {
            return -1.0;
        } else {
            return wordsSentiment.get(word.toLowerCase()).getWordScore();
        }
    }

    public double getScoreOfReview(String review) {
        double unknownResult = -1.0;

        if (review == null) {
            return unknownResult;
        }

        String[] words = review.split("[^a-zA-Z0-9]+");
        double result = 0.0;
        int numberOfSentimentWords = 0;

        for (String word : words) {
            if (wordsSentiment.containsKey(word.toLowerCase())) {
                result += wordsSentiment.get(word.toLowerCase()).getWordScore();
                numberOfSentimentWords++;
            }
        }

        return (numberOfSentimentWords == 0) ? unknownResult : (result / numberOfSentimentWords);
    }

    public String getReview(double sentimentValue) {
        for (String review : reviews) {
            if (Double.compare(sentimentValue, getScoreOfReview(review)) == 0) {
                return review;
            }
        }

        return null;
    }

    public LinkedList<String> getMostFrequentWords(int n) {
        Map<String, WordScoreGenerator> sorted = wordsSentiment.entrySet().stream()
                .sorted(Collections.reverseOrder(Comparator.comparingInt(e -> e.getValue().getWordFrequency())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

        LinkedList<String> mostFrequent = new LinkedList<>();
        Iterator<Map.Entry<String, WordScoreGenerator>> it = sorted.entrySet().iterator();

        for (int i = 0; it.hasNext() && i < n; i++) {
            mostFrequent.addLast(it.next().getKey());
        }

        return mostFrequent;
    }

    public LinkedList<String> getMostPositiveWords(int n) {
        Map<String, WordScoreGenerator> sorted = wordsSentiment.entrySet().stream()
                .sorted(Collections.reverseOrder(Comparator.comparingDouble(e -> e.getValue().getWordScore())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

        LinkedList<String> mostPositive = new LinkedList<>();
        Iterator<Map.Entry<String, WordScoreGenerator>> it = sorted.entrySet().iterator();

        for (int i = 0; it.hasNext() && i < n; i++) {
            mostPositive.addLast(it.next().getKey());
        }

        return mostPositive;
    }

    public LinkedList<String> getMostNegativeWords(int n) {
        Map<String, WordScoreGenerator> sorted = wordsSentiment.entrySet().stream()
                .sorted(Comparator.comparingDouble(e -> e.getValue().getWordScore()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

        LinkedList<String> mostNegative = new LinkedList<>();
        Iterator<Map.Entry<String, WordScoreGenerator>> it = sorted.entrySet().iterator();

        for (int i = 0; it.hasNext() && i < n; i++) {
            mostNegative.addLast(it.next().getKey());
        }

        return mostNegative;
    }

    public int getWordSentimentDictionarySize() {
        return wordsSentiment.size();
    }

    public boolean isStopWord(String word) {
        return stopWords.contains(word.toLowerCase());
    }

    public int getReviewsSize() {
        return reviews.size();
    }
}
