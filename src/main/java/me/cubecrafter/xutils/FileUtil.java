package me.cubecrafter.xutils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@UtilityClass
public class FileUtil {

    /**
     * Copy an input stream to a file.
     * @param inputStream the input stream
     * @param target the target file
     */
    public static void copy(InputStream inputStream, File target) {
        target.getParentFile().mkdirs();
        try {
            Files.copy(inputStream, target.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a file if it doesn't exist.
     * @param file the file
     * @return the created file
     */
    public static File create(File file) {
        if (file.exists()) return file;

        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * Copy a file or directory to another location.
     * @param source the source file or directory
     * @param target the target file or directory
     */
    public static void copy(File source, File target) {
        Path sourcePath = source.toPath();
        Path targetPath = target.toPath();

        try {
            Files.createDirectories(targetPath.getParent());
            Files.walkFileTree(source.toPath(), new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Files.createDirectories(targetPath.resolve(sourcePath.relativize(dir)));
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.copy(file, targetPath.resolve(sourcePath.relativize(file)));
                    return FileVisitResult.CONTINUE;
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete a file or directory.
     * @param file the file or directory
     */
    public static void delete(File file) {
        if (!file.exists()) return;
        try (Stream<Path> walk = Files.walk(file.toPath())) {
            walk.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write a string to a file.
     * @param file the file
     * @param content the content to write
     */
    public static void write(File file, String content) {
        file.getParentFile().mkdirs();
        try {
            Files.write(file.toPath(), content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read a file to a string.
     * @param file the file
     * @return the file content
     */
    public static String read(File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Read a file to a JsonObject.
     * @param file the file
     * @return the JsonObject
     */
    public JsonObject readJson(File file) {
        String content = read(file);
        if (content == null) return null;
        return JsonParser.parseString(content).getAsJsonObject();
    }

    /**
     * Create a zip archive from a file or directory.
     * @param source the source file or directory
     * @param target the target zip file
     * @param comment the zip comment
     */
    public static void zipFolder(File source, File target, String comment) {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(target.toPath()))) {
            zos.setLevel(Deflater.BEST_COMPRESSION);
            zos.setMethod(ZipOutputStream.DEFLATED);
            zos.setComment(comment);

            Path root = source.toPath();
            Files.walkFileTree(root, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    ZipEntry entry = new ZipEntry(root.relativize(file).toString());
                    entry.setLastModifiedTime(FileTime.fromMillis(0));
                    zos.putNextEntry(entry);
                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
