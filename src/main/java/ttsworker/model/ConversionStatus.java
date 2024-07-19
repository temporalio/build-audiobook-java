// @@@SNIPSTART audiobook-project-java-Conversion-Status-data-type
package ttspackage;

import java.nio.file.Path;
import java.util.List;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = ConversionStatus.class)
public class ConversionStatus {
    public String inputPathString; // Provided by Workflow input
    public Path inputPath = null; // Source file path
    public Path tempOutputPath = null; // Work file Path
    public Path outputPath = null; // Results file Path
    public List<String> chunks = null; // Batched input text
    public int chunkCount = 1; // Number of text chunks
    public int count = 0; // Number of chunks processed
    public String message = "Text to speech request received"; // User-facing Query text

    public ConversionStatus() {} // Jackson

    public ConversionStatus(String inputPath) {
        this.inputPathString = inputPath;
    }

}
// @@@SNIPEND
