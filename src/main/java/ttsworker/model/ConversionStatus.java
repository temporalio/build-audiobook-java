// @@@SNIPSTART audiobook-project-java-Conversion-Status-data-type
package ttspackage;

import java.nio.file.Path;
import java.util.List;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = ConversionStatus.class)
public class ConversionStatus {
    public String inputPathString; // Provided by Workflow input
    public Path inputPath; // Source file path
    public Path tempOutputPath; // Work file Path
    public Path outputPath; // Results file Path
    public List<String> chunks; // Batched input text
    public int chunkCount; // Number of text chunks
    public int count; // Number of chunks processed
    public String message; // User-facing Query text

    public ConversionStatus() {} // Jackson

    public ConversionStatus(String inputPath) {
        this.inputPathString = inputPath;
        this.tempOutputPath = null;
        this.inputPath = null;
        this.outputPath = null;
        this.chunks = null;
        this.chunkCount = 1;
        this.count = 0;
        this.message = "Text to speech request received";
    }

}
// @@@SNIPEND
