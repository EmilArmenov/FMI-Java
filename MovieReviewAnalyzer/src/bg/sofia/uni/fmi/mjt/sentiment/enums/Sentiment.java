package bg.sofia.uni.fmi.mjt.sentiment.enums;

public enum Sentiment {

    NEGATIVE("negative"),
    SOMEWHAT_NEGATIVE("somewhat negative"),
    NEUTRAL("neutral"),
    SOMEWHAT_POSITIVE("somewhat positive"),
    POSITIVE("positive");

    private String value;

    private Sentiment(String value) {
        this.value = value;
    }

    public String getSentiment() {
        return value;
    }
}
