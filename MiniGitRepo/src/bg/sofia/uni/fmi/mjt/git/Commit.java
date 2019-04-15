package bg.sofia.uni.fmi.mjt.git;
#2E2C2C
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Commit {

    private String date;
    private String hash;
    private String message;

    public Commit(String message) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E MMM dd HH:mm yyyy");
        this.date = LocalDateTime.now().format(formatter);

        try {
            this.hash = hexDigest(date + message);
        } catch (NoSuchAlgorithmException e) {
            e.getMessage();
        }
        this.message = message;
    }

    private String hexDigest(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] bytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        return convertBytesToHex(bytes);
    }

    private String convertBytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte current : bytes) {
            hex.append(String.format("%02x", current));
        }

        return hex.toString();
    }

    public String getHash() {
        return hash;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Commit commit = (Commit) o;
        return Objects.equals(date, commit.date) &&
                Objects.equals(hash, commit.hash) &&
                Objects.equals(message, commit.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, hash, message);
    }

    @Override
    public String toString() {
        return "commit " + hash + "\nDate: " + date + "\n\n\t" + message;
    }
}
