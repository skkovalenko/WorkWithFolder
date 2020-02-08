
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Scanner;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private static final Marker MARKER_CORRECT_INPUT_PATH = MarkerManager.getMarker("CORRECT_INPUT_PATH");
    private static final Marker MARKER_INCORRECT_INPUT_PATH = MarkerManager.getMarker("INCORRECT_INPUT_PATH");
    private static final Marker MARKER_EXCEP = MarkerManager.getMarker("EXCEPTION");


    public static void main(String[] args) {
        while (true){
            System.out.println("Please, select option: \n'C' - this option will copy the folder" +
                    "\n'S' - that option will show the size of the folder");

            String selectOption = new Scanner(System.in).nextLine().trim().toUpperCase();
            if(!selectOption.matches("^S|C$")){
                System.out.println("This option is not. Try again");
                continue;
            }
            if(selectOption.equals("S")){
                sizeFolder();
            }
            if(selectOption.equals("C")){
                try {
                    copyFolder();
                } catch (IOException e) {
                    LOGGER.error(MARKER_EXCEP, "Exception from copyFolder: ", e);
                    e.printStackTrace();
                }
            }
        }
    }

    private static void copyFolder() throws IOException{
        while (true){
            System.out.println("Please, enter the path for the folder you need to copy");
            Path pathFrom = Paths.get(new Scanner(System.in).nextLine().trim());
            if(Files.notExists(pathFrom)){
                LOGGER.warn(MARKER_INCORRECT_INPUT_PATH, "Incorrect: Path for copy: {}", pathFrom);
                System.out.println("Folder does not exist. Try again...");
                continue;
            }
            LOGGER.info(MARKER_CORRECT_INPUT_PATH, "Correct: path for copied: {}", pathFrom);
            System.out.println("Please, enter the path where to copy");
            Path pathTo = Paths.get(new Scanner(System.in).nextLine().trim());
            if(Files.exists(pathTo)){
                LOGGER.warn(MARKER_INCORRECT_INPUT_PATH, "Incorrect: Path where to copy exists: {}", pathTo);
                System.out.println("This path exists. Try again...");
                continue;
            }
            if(Files.notExists(pathTo.getParent())){
                    Files.createDirectories(pathTo);
            }
            System.out.println("Please, wait...");
                Files.walkFileTree(pathFrom, new MyFileCopyVisitor(pathFrom, pathTo));
            System.out.println("Copied");
            LOGGER.info(MARKER_CORRECT_INPUT_PATH, "Correct: Folder copied: {}", pathTo);
            break;
        }
    }

    private static void sizeFolder(){
        System.out.println("Please, enter the path for the folder to find out the size:");
        while (true) {
            String inputUserPath = new Scanner(System.in).nextLine().trim();
            Path path = Paths.get(inputUserPath);
            if (Files.isDirectory(path)) {
                long sizeFolder = 0;
                try {
                    sizeFolder = Files.walk(path)
                            .map(Path::toFile)
                            .filter(File::isFile)
                            .mapToLong(File::length)
                            .sum();
                } catch (Exception e) {
                    LOGGER.error(MARKER_EXCEP, "Exception for sizeFolder", e);
                    e.printStackTrace();
                }
                System.out.printf("%s %s%n", path.getFileName(), sizeConversion(sizeFolder));
                LOGGER.info(MARKER_CORRECT_INPUT_PATH, "Correct: Path for the folder to find out the size: {} size - {}", inputUserPath, sizeConversion(sizeFolder));
                break;
            } else {
                LOGGER.warn(MARKER_INCORRECT_INPUT_PATH, "Incorrect: Path for the folder to find out the size: {}", inputUserPath);
                System.out.println("Wrong path, try again");
            }
        }
    }

    private static String sizeConversion(long b){
        String prefix = "KMGTPE";
        if(b < 1024)  return b + "b";
        long resique;
        for (int count = 0; b > 1024; count++){
            resique = b % 1024;
            b = b / 1024;
            if(b < 1024){
                double size =  b + (double) resique / 1024;
                return String.format("size: %.2f %sb", size, prefix.charAt(count));
            }
        }
        return null;
    }
}
