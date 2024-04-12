package org.example;

import org.apache.commons.lang3.tuple.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        List<Path> files;
        String input = "M:\\platformyTechnologiczne\\platformyTechnologiczne6_1\\src\\main\\pictures";
        String output = "C:\\Users\\zuzia\\IdeaProjects=\\platformyTechnologiczne6\\src\\main\\java\\org\\example\\processedPictures";

        Path source = Path.of(input);

        try (Stream<Path> stream = Files.list(source)) {
            files = stream.collect(Collectors.toList());
            for (int numThreads = 1; numThreads <= 20; numThreads++) {
                System.out.println(".> Number of Threads: " + numThreads);
                long startTime = System.currentTimeMillis();
                processImagesInParallel(files, output, numThreads);
                long endTime = System.currentTimeMillis();
                System.out.println("-- Execution Time: " + (endTime - startTime) + " ms \n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processImagesInParallel(List<Path> files, String output, int numThreads) {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        try {
            executor.invokeAll(files.stream().map(path -> (Callable<Pair<String, BufferedImage>>) () -> {
                try {
                    processImage(path, output);
                    return null;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }


    private static void processImage(Path path, String output) throws IOException {
        try {
            BufferedImage image = ImageIO.read(path.toFile());
            BufferedImage processedImage = transformImage(image);

            String fileName = path.getFileName().toString();
            Path outputPath = Paths.get(output, fileName);
            ImageIO.write(processedImage, "jpg", outputPath.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage transformImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage transformedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = image.getRGB(i, j);
                Color color = new Color(rgb);
                int red = color.getRed();
                int blue = color.getBlue();
                int green = color.getGreen();
                Color newColor = new Color(red, blue, green);
                transformedImage.setRGB(i, j, newColor.getRGB());
            }
        }

        return transformedImage;
    }
}
