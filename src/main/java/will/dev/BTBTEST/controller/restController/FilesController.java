package will.dev.BTBTEST.controller.restController;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import will.dev.BTBTEST.entity.FileParams;
import will.dev.BTBTEST.repository.FilesRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@RestController
public class FilesController {

    private final FilesRepository filesRepository;
    private final String basePath;
    public FilesController(
            @Value("${application.files.base-path}") final String basePath,
            FilesRepository filesRepository) {
        this.filesRepository = filesRepository;
        this.basePath = basePath;
    }

    @PostMapping("/files")
    public void create(@RequestBody FileParams file) throws IOException {
        final String temp = String.format("%s.%s",
                System.currentTimeMillis(),
                FilenameUtils.getExtension(file.getName())
        );
        file.setTemp(temp);
        //this.filesRepository.save(file);
        this.writeOnDisk(file);
    }

    private void writeOnDisk(FileParams file) throws IOException {

        final String fullPath = String.format("%s/%s", this.basePath, file.getTemp());
        final Path folder = Paths.get(fullPath).getParent();
        Files.createDirectories(folder);

        String fileAsString = String.valueOf(file.getContent());
        if (fileAsString.contains(",")) {
            fileAsString = fileAsString.split(",")[1];
        }
        final byte[] decodedFile = Base64.getDecoder().decode(fileAsString);
        final File fullPathAsFile = new File(fullPath);
        if (Files.exists(Paths.get(fullPath))) {
            FileUtils.delete(fullPathAsFile);
        }

        FileUtils.writeByteArrayToFile(fullPathAsFile, decodedFile);

    }
}


