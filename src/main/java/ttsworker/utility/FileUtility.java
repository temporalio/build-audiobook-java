package ttspackage;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.Optional;

import org.apache.commons.io.FilenameUtils;

public class FileUtility {

    /**
     * Validate the input file path as a readable text file.
     *
     * - Creates a canonical path, bypassing symbolic links and file system shortcut symbols.
     * - Checks for `.txt` file extension.
     * - Checks that the file exists and is readable.
     * - Checks that the file is not empty.
     *
     * @param inputPath A String pointing to a text input file.
     * @return An `Optional<Path>` with the validated path, otherwise empty
     */
    public static Optional<Path> validateInputFile(String inputPath) {
        Path filePath;

        if (inputPath == null || inputPath.isEmpty()) {
            return Optional.empty();
        }

        // Resolve ~ and symbolic links if used
        try {
            if (inputPath.startsWith("~")) {
                String home = System.getProperty("user.home");
                inputPath = home + inputPath.substring(1);
            }

            filePath = Paths.get(inputPath)
            .toAbsolutePath().normalize()
            .toRealPath(LinkOption.NOFOLLOW_LINKS);

        } catch (InvalidPathException | IOException e) {
            return Optional.empty();
        }

        // Ensure this is a 'txt' file, exists, and can be read
        if (!inputPath.endsWith(".txt") ||
            !Files.exists(filePath) ||
            !Files.isReadable(filePath)) {
            return Optional.empty();
        }

        // Don't process empty files
        try {
            if (Files.size(filePath) == 0) {
                return Optional.empty();
            }
        } catch (IOException e) {
            return Optional.empty();
        }

        return Optional.of(filePath);
    }

    /**
     * Fetch the content of a text file.
     *
     * - Reads and returns the file contents as a `String`.
     *
     * @param inputPath A `Path` pointing to a text input file.
     * @return An `Optional<String>` with the file contents, otherwise empty.
     */
    public static Optional<String> fetchFileContent(Path inputPath) {
        try {
            String content = Files.readString(inputPath);
            return Optional.of(content);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /**
     * Create a temporary file
     *
     * - Uses the System's default temporary-file directory.
     *
     * @return If successful, an `Optional<Path>`, otherwise an empty `Optional`.
     */
    public static Optional<Path> createTemporaryFile() {
        try {
            Path tempFile = Files.createTempFile(null, null);
            return Optional.of(tempFile);
        } catch (IOException e) {
            e.printStackTrace(); // Log the exception if needed
            return Optional.empty();
        }
    }

    /**
     * Replace a `Path` extension with a new extension
     *
     * - Assumes a pre-normalized path.
     *
     * @param inputPath The source file path.
     * @param newExtension The new extension to use.
     * @return An `Optional<Path>` pointing to the updated file, otherwise empty.
     */
    public static Optional<Path> replaceExtension(Path inputPath, String newExtension) {
        try {
            // Get the parent directory
            Path parentDir = inputPath.getParent();

            // Extract the file name without extension
            String baseName = FilenameUtils.getBaseName(inputPath.toString());

            // Create the new file name with the new extension
            String newFileName = baseName + newExtension;

            // Create the new path
            Path newPath = parentDir.resolve(newFileName);
            return Optional.of(newPath);
        } catch (InvalidPathException e) {
            return Optional.empty();
        }
    }

    /**
     * Returns a unique file name by appending a numeric suffix if the proposed path already exists.
     *
     * @param proposedPath The proposed file path as a Path object.
     * @param extension The file extension to use.
     * @return An Optional containing the unique Path if successful, otherwise an empty Optional.
     */
    public static Optional<Path> findUniqueName(Path proposedPath, String extension) {
        if (proposedPath == null || extension == null) {
            return Optional.empty();
        }

        try {
            int suffixCounter = 1;
            String baseName = FilenameUtils.getBaseName(proposedPath.toString());
            Path parentDir = proposedPath.getParent();
            Path newPath = parentDir.resolve(Paths.get(baseName + extension));

            while (Files.exists(newPath)) {
                String newFileName = baseName + "-" + suffixCounter + extension;
                newPath = parentDir.resolve(newFileName);
                suffixCounter += 1;
            }

            return Optional.of(newPath);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Move a file from the source path to the destination path.
     *
     * @param source the `Path` of the file to be moved.
     * @param destination the `Path` where the file should be moved to.
     * @throws IOException if an error occurs while moving the file.
     */
    public static void moveFile(Path source, Path destination) throws IOException {
        Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
    }

}
