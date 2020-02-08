import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class MyFileCopyVisitor extends SimpleFileVisitor<Path> {
    private Path pathFrom;
    private Path pathTo;


    public MyFileCopyVisitor(Path f, Path t){
        pathFrom = f;
        pathTo = t;
    }

    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
        Path pToNew = pathTo.resolve(pathFrom.relativize(path));
        Files.copy(path, pToNew, StandardCopyOption.REPLACE_EXISTING);
        return FileVisitResult.CONTINUE;
    }
    public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) throws IOException {
        Path pToNew = pathTo.resolve(pathFrom.relativize(path));
        if(pathFrom.resolve(pathFrom.relativize(path)).equals(pathFrom.resolve(path.relativize(pathTo)))){
            return FileVisitResult.SKIP_SUBTREE;
        }
        Files.copy(path, pToNew, StandardCopyOption.REPLACE_EXISTING);
        return FileVisitResult.CONTINUE;
    }


}
