package ttspackage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * Utility class for chunking and appending data.
 */
public class DataUtility {
    /**
     * The maximum number of tokens allowed in a single text chunk for OpenAI text-to-speech processing.
     *
     * This constant defines the upper limit on the number of tokens that a single chunk of text can contain.
     * This relatively low value reduces the size of the returned audio data.
     */
    private static final int MAX_TOKENS = 512;

    /**
     * The average number of tokens per word used for estimating the size of text chunks.
     *
     * This constant provides an estimate of the average number of tokens per word in the text.
     * This approximates the number of tokens in a given text chunk, helping to split the text efficiently
     * without exceeding the token limit defined by {@code MAX_TOKENS}.
     */
    private static final float AVERAGE_TOKENS_PER_WORD = 1.33f;

    /**
     * Splits the given text into chunks, each chunk not exceeding the max token limit.
     *
     * - Uses an average token per word estimate to split text appropriately.
     * - Ensures that no chunk exceeds the specified max tokens limit.
     *
     * @param text The text to be split into chunks.
     * @return A list of text chunks.
     */
    public static List<String> splitText(String text) {
        List<String> chunks = new ArrayList<>();
        String[] words = text.split("\\s+");
        StringJoiner chunk = new StringJoiner(" ");

        for (String word : words) {
            if ((chunk.length() + word.length()) * AVERAGE_TOKENS_PER_WORD <= MAX_TOKENS) {
                chunk.add(word);
            } else {
                chunks.add(chunk.toString());
                chunk = new StringJoiner(" ");
                chunk.add(word);
            }
        }

        if (chunk.length() > 0) {
            chunks.add(chunk.toString());
        }

        return chunks;
    }

    /**
     * Appends the given data to the specified file.
     *
     * - Uses Path and Files classes for file handling.
     * - Ensures that data is appended to the file.
     *
     * @param data The data to append to the file.
     * @param filePath The path of the file to append to.
     * @throws IOException If an I/O error occurs.
     */
    public static void appendToFile(byte[] data, Path filePath) throws IOException {
        Files.write(filePath, data, java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
    }
}

