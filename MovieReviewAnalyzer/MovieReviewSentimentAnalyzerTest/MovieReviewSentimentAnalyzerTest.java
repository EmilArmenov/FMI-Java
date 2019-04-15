import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

import bg.sofia.uni.fmi.mjt.sentiment.MovieReviewSentimentAnalyzer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MovieReviewSentimentAnalyzerTest {

    private static final double DELTA = 0.0001;

    private MovieReviewSentimentAnalyzer analyzer;

    private InputStream reviewsStream;
    private InputStream stopwordsStream;
    private OutputStream resultStream;

    @Before
    public void init() throws FileNotFoundException {
        stopwordsStream = new FileInputStream("resources/stopwords.txt");
        reviewsStream = new FileInputStream("resources/reviews.txt");
        resultStream = new FileOutputStream("resources/reviews.txt", true);
        analyzer = new MovieReviewSentimentAnalyzer(stopwordsStream, reviewsStream, resultStream);
    }

    @Test
    public void isRandomReviewSentimentedProperly() {
        final double expected = 2.0;
        Assert.assertEquals(expected,
                analyzer.getReviewSentiment("Test method with words impressed and ambition"), DELTA);
    }

    @Test
    public void testIsUnknownReviewSentimentedProperly() {
        final double expected = -1.0;
        assertEquals(expected, analyzer.getReviewSentiment("This review does not contain words in the file"), DELTA);
    }

    @Test
    public void testUnknownEmptyReview() {
        final double expected = -1.0;
        assertEquals(expected, analyzer.getReviewSentiment(""), DELTA);
    }

    @Test
    public void testScoreOfNullValueReview() {
        final double expected = -1.0;
        assertEquals(expected, analyzer.getReviewSentiment(null), DELTA);
    }

    @Test
    public void testSameWordMultipleTimesIsInTheSentimentRange() {
        final double expected = 2.0;
        assertEquals(expected, analyzer.getReviewSentiment("finish Finish, FiNish. FINISH"), DELTA);
    }

    @Test
    public void isRandomReviewSentimentedProperlyAsName() {
        final String expected = "somewhat negative";
        Assert.assertEquals(expected, analyzer.getReviewSentimentAsName("RUN FOR YOUR LIVES"));
    }

    @Test
    public void isRandomReviewRoundedProperlyToItsSentiment() {
        final String expected = "neutral";
        Assert.assertEquals(expected, analyzer.getReviewSentimentAsName("SmaRt Major, daRK"));
    }

    @Test
    public void isExistingWordScoredProperly() {
        final double expected = 2.333333;
        Assert.assertEquals(expected, analyzer.getWordSentiment("testword"), DELTA);
    }

    @Test
    public void getScoreOfEmptyWord() {
        final double expected = -1.0;
        Assert.assertEquals(expected, analyzer.getWordSentiment(""), DELTA);
    }

    @Test
    public void getScoreOfNullValueWord() {
        final double expected = -1.0;
        Assert.assertEquals(expected, analyzer.getWordSentiment(null), DELTA);
    }

    @Test
    public void isUnknownWordScoredProperly() {
        final double expected = -1.0;
        Assert.assertEquals(expected, analyzer.getWordSentiment("nonexistingWord"), DELTA);
    }

    @Test
    public void isReviewFoundByExistingSentiment() {
        final String expected = "But it has an ambition to say something about its subjects , but not a willingness .";
        final double sentiment = 1.0;
        Assert.assertEquals(expected, analyzer.getReview(sentiment));
    }

    @Test
    public void returnNullForNonExistingSentiment() {
        final double sentiment = 0.666;
        Assert.assertNull(analyzer.getReview(sentiment));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionWhenNegativeMostFrequentWordsInput() {
        final int negativeInput = -1;
        analyzer.getMostFrequentWords(negativeInput);
    }

    @Test
    public void testGetMostFrequentWords() {
        final int input = 2;
        final String expectedMostFrequent = "testword watching";
        List<String> list = new LinkedList<>(analyzer.getMostFrequentWords(input));

        Assert.assertEquals(expectedMostFrequent, ((LinkedList<String>) list).pollFirst()
                + " " + ((LinkedList<String>) list).pollFirst());
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionWhenNegativeMostPositiveWordsInput() {
        final int negativeInput = -1;
        analyzer.getMostPositiveWords(negativeInput);
    }

    @Test
    public void testGetMostPositiveWords() {
        final int input = 2;
        final String expectedMostPositive = "java positive";
        List<String> list = new LinkedList<>(analyzer.getMostPositiveWords(input));

        Assert.assertEquals(expectedMostPositive, ((LinkedList<String>) list).pollFirst()
                + " " + ((LinkedList<String>) list).pollFirst());
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionWhenNegativeMostNegativeWordsInput() {
        final int negativeInput = -1;
        analyzer.getMostNegativeWords(negativeInput);
    }

    @Test
    public void testGetMostNegativeWords() {
        final int input = 2;
        final String expectedMostNegative = "run negative";
        List<String> list = new LinkedList<>(analyzer.getMostNegativeWords(input));

        Assert.assertEquals(expectedMostNegative, ((LinkedList<String>) list).pollFirst()
                + " " + ((LinkedList<String>) list).pollFirst());
    }

    @Test
    public void testIsAppendingProperly() {
        final int expected = analyzer.getReviewsSize() + 1;
        final int sentimentValue = 4;
        analyzer.appendReview("Meme Review", sentimentValue);
        assertEquals(expected, analyzer.getReviewsSize());
    }

    @Test
    public void testIsStopWordNegativeFromDictionary() {
        String assertMessage = "A word should not be incorrectly identified as a stopword, " +
                "if it is not part of the stopwords list";
        assertFalse(assertMessage, analyzer.isStopWord("effects"));
    }

    @Test
    public void testIsStopWordPositive() {
        assertTrue(analyzer.isStopWord("a"));
    }
}