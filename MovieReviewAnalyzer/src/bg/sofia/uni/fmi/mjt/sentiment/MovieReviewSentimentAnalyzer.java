package bg.sofia.uni.fmi.mjt.sentiment;

import bg.sofia.uni.fmi.mjt.sentiment.enums.Sentiment;

import java.io.*;
import java.util.Collection;

public class MovieReviewSentimentAnalyzer implements SentimentAnalyzer, AutoCloseable {

    private ReviewAnalyzer analyzer;
    private BufferedWriter reviewsOutput;

    public MovieReviewSentimentAnalyzer(InputStream stopWordsInput,
                                        InputStream reviewsInput, OutputStream reviewsOutput) {

        analyzer = new ReviewAnalyzer(stopWordsInput, reviewsInput);
        this.reviewsOutput = new BufferedWriter(new OutputStreamWriter(reviewsOutput));
    }

    @Override
    public double getReviewSentiment(String review) {
        return analyzer.getScoreOfReview(review);
    }

    @Override
    public String getReviewSentimentAsName(String review) {
        int reviewScore = (int) Math.round(getReviewSentiment(review));

        final int negativeScore = 0;
        final int somewhatNegativeScore = 1;
        final int neutralScore = 2;
        final int somewhatPositiveScore = 3;
        final int positiveScore = 4;

        switch (reviewScore) {
            case negativeScore:
                return Sentiment.NEGATIVE.getSentiment();
            case somewhatNegativeScore:
                return Sentiment.SOMEWHAT_NEGATIVE.getSentiment();
            case neutralScore:
                return Sentiment.NEUTRAL.getSentiment();
            case somewhatPositiveScore:
                return Sentiment.SOMEWHAT_POSITIVE.getSentiment();
            case positiveScore:
                return Sentiment.POSITIVE.getSentiment();
            default:
                return "unknown";
        }
    }

    @Override
    public double getWordSentiment(String word) {
        return analyzer.getScoreOfWord(word);
    }

    @Override
    public String getReview(double sentimentValue) {
        return analyzer.getReview(sentimentValue);
    }

    @Override
    public Collection<String> getMostFrequentWords(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("The input can not be a negative number");
        } else {
            return analyzer.getMostFrequentWords(n);
        }
    }

    @Override
    public Collection<String> getMostPositiveWords(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("The input can not be a negative number");
        } else {
            return analyzer.getMostPositiveWords(n);
        }
    }

    @Override
    public Collection<String> getMostNegativeWords(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("The input can not be a negative number");
        } else {
            return analyzer.getMostNegativeWords(n);
        }
    }

    @Override
    public void appendReview(String review, int sentimentValue) {
        analyzer.analyzeReview(review, sentimentValue);

        try {
            reviewsOutput.write(sentimentValue + " " + review);
            reviewsOutput.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getSentimentDictionarySize() {
        return analyzer.getWordSentimentDictionarySize();
    }

    @Override
    public boolean isStopWord(String word) {
        return analyzer.isStopWord(word);
    }

    @Override
    public void close() {
        try {
            reviewsOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getReviewsSize() {
        return analyzer.getReviewsSize();
    }
}