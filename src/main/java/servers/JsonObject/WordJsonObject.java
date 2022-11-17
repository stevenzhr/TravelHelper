package servers.JsonObject;

public class WordJsonObject {
    private Boolean success;
    private String word;

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public WordJsonObject(Boolean success, String hotelId) {
        this.success = success;
        this.word = hotelId;
    }
}
