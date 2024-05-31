package org.example;

import me.tongfei.progressbar.ProgressBar;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static {
        System.setProperty("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.NoOpLog");
    }

    public static List<Path> listFiles(Path directory) throws IOException {
        List<Path> fileList = new ArrayList<>();
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                fileList.add(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                System.err.println("Failed to visit file: " + file);
                return FileVisitResult.CONTINUE;
            }
        });
        return fileList;
    }

    public static void main(String[] args) throws IOException {
        var toFind = "Artur";

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment toFindSegment = arena.allocateFrom(toFind);
            org.example.scan.lib_h.extract_text_from_pdf(toFindSegment, toFindSegment);
            var root = "D:\\docs\\rpg\\One Ring";
            Path directory = Paths.get(root);
            List<Path> fileList = listFiles(directory);
            int count = 0;
            try (ProgressBar pb = new ProgressBar("Scanning", fileList.size())) { // name, initial max
                for (var file : fileList) {
                    var fileName = file.toFile().getName();
                    MemorySegment fileSegment = arena.allocateFrom(file.toString());
                    if (org.example.scan.lib_h.extract_text_from_pdf(fileSegment, toFindSegment)) {
                        count++;
                    }
                    fileName = fileName.substring(0, Math.min(fileName.length(), 20));
                    fileName = String.format("%-" + 20 + "s", fileName);
                    pb.setExtraMessage(fileName); // Set extra message to display at the end of the bar

//                    try (PDDocument document = PDDocument.load(file.toFile())) {
//                        PDFTextStripper pdfStripper = new PDFTextStripper();
//                        String text = pdfStripper.getText(document);
//                        if (text.contains(toFind)) {
//                            count++;
//                        }
//                    } catch (IOException e) {
////                    e.printStackTrace();
//                    }
                    pb.step(); // step by 1
                }
            }
            System.out.println(STR."Found \{count} files");
        }

    }
}