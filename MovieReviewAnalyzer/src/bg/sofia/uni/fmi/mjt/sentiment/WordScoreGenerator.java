package bg.sofia.uni.fmi.mjt.sentiment;

public class WordScoreGenerator {

    private static final int START_WORD_FREQUENCY = 1;

    private int wordFrequency;
    private double wordScore;

    WordScoreGenerator(int wordScore) {
        this.wordFrequency = START_WORD_FREQUENCY;
        this.wordScore = wordScore;
    }

    public double getWordScore() {
        return wordScore / wordFrequency;
    }

    public void updateScore(int score) {
        wordScore += score;
        wordFrequency++;
    }

    public int getWordFrequency() {
        return wordFrequency;
    }
}