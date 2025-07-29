package ru.job4j.thread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.net.URL;

public class Wget implements Runnable {
    private final String url;
    private final int speed;

    public Wget(String url, int speed) {
        this.url = url;
        this.speed = speed;
    }

    @Override
    public void run() {
        var startAt = System.currentTimeMillis();
        String fileName = "C:\\projects\\job4j_threads\\src\\main\\resources\\" + url.substring(url.lastIndexOf('/') + 1, url.length());
        var file = new File(fileName);
        try (var input = new URL(url).openStream();
             var output = new FileOutputStream(file)) {
            System.out.println("Open connection: " + (System.currentTimeMillis() - startAt) + " ms");
            var dataBuffer = new byte[512];
            int bytesRead;
            int bytesSum = 0;
            long startTime = System.nanoTime();
            long currentSpeed;
            long downloadTime;
            long currentDownloadTime;
            long downloadSpeed;
            while ((bytesRead = input.read(dataBuffer, 0, dataBuffer.length)) != -1) {
                bytesSum += bytesRead;
                long downloadAt = System.nanoTime();
                downloadTime = downloadAt - startTime;
                downloadSpeed = (bytesSum * 1000000) / downloadTime;
                if (downloadSpeed > speed) {
                    long sleepTime = downloadSpeed / speed;
                    Thread.sleep(sleepTime);
                }
                output.write(dataBuffer, 0, bytesRead);
                currentDownloadTime = System.nanoTime() - startTime;
                currentSpeed = (bytesSum * 1000000) / currentDownloadTime;
                System.out.println("Read " + bytesSum + " bytes : speed " + currentSpeed + " bytes/ms.");
            }
            System.out.println(Files.size(file.toPath()) + " bytes");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static boolean isValidURL(String url) throws MalformedURLException, URISyntaxException {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException e) {
            return false;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public static void main(String[] args) throws InterruptedException, MalformedURLException, URISyntaxException {
        String url = args[0];
        if (isValidURL(url)) {
            int speed = Integer.parseInt(args[1]);
            Thread wget = new Thread(new Wget(url, speed));
            wget.start();
            wget.join();
        } else {
            System.out.println("Invalid URL");
        }
    }
}
