// @@@SNIPSTART audiobook-project-java-tts-utility-class
package ttspackage;

import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;

/**
 * Utility class to convert text to speech using the OpenAI API.
 */
public class TTSUtility {
    private static final String TTS_API_URL = "https://api.openai.com/v1/audio/speech";

    /**
     * Converts the given text to speech and returns the audio as a byte array.
     *
     * @param text the text to convert to speech.
     * @return a byte array containing the audio data in MP3 format.
     * @throws IOException if an error occurs while making the API request or processing the response.
     * @throws IllegalArgumentException if the Bearer token is not set in the environment variables.
     */
    public static byte[] textToSpeech(String text) throws IOException {

        // Fetch and clean up Bearer token from environment
        String bearerToken = System.getenv("OPEN_AI_BEARER_TOKEN");
        if (bearerToken != null) {
            bearerToken = bearerToken.trim();
            bearerToken = bearerToken.replaceAll("[\\P{Print}]", "");
        } else {
            throw new IllegalArgumentException("Bearer token is not set");
        }

        OkHttpClient client = new OkHttpClient();

        // Create API request payload
        JSONObject json = new JSONObject();
        json.put("model", "tts-1");
        json.put("input", text);
        json.put("voice", "nova"); // see https://platform.openai.com/docs/guides/text-to-speech/voice-options
        json.put("response_format", "mp3");
        json.put("speed", 1);

        RequestBody body = RequestBody.create(
                json.toString(), MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(TTS_API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + bearerToken)
                .build();

        // Fetch and return response body
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            return response.body().bytes();
        }
    }
}
// @@@SNIPEND
