package me.cubecrafter.xutils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.file.YamlConfiguration;

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

    public static void copy(InputStream inputStream, File target) {
        target.getParentFile().mkdirs();
        try {
            Files.copy(inputStream, target.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File create(File file) {
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

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

    public static void delete(File file) {
        if (!file.exists()) return;
        try (Stream<Path> walk = Files.walk(file.toPath())) {
            walk.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(File file, String content) {
        file.getParentFile().mkdirs();
        try {
            Files.write(file.toPath(), content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String read(File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JsonObject readJson(File file) {
        String content = read(file);
        if (content == null) return null;
        return JsonParser.parseString(content).getAsJsonObject();
    }

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

    public static YamlConfiguration loadConfig(String name, File file) {
        if (!file.exists()) {
            copy(XUtils.getPlugin().getResource(name), file);
        }
        return YamlConfiguration.loadConfiguration(file);
    }

}
