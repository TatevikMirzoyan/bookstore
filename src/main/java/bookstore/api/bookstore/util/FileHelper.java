package bookstore.api.bookstore.util;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tatevik Mirzoyan
 * Created on 15-Apr-21
 */
public class FileHelper {

    private static final String PATH = "C:/Tatev Mirzoyan/1.My_Files/Projects/bookstore/bookstore/src/main/resources/";

    public List<File> splitFile(MultipartFile file, int mbPerSplit) throws IOException {
        List<File> files = new ArrayList<>();
        long maxSizeFile = 1024L * 1024L * mbPerSplit;
        long sizeOfRows = 0;
        int fileNumber = 1;
        try (InputStreamReader stream = new InputStreamReader(file.getInputStream());
             BufferedReader reader = new BufferedReader(stream)) {
            final String headers = reader.readLine();
            String strLine = reader.readLine();
            while (strLine != null) {
                File newFile = new File(PATH + FilenameUtils.getBaseName(file.getOriginalFilename()) + "_" + fileNumber + "." + FilenameUtils.getExtension(file.getOriginalFilename()));
                try(Writer writer = new OutputStreamWriter(new FileOutputStream(newFile))) {
                    strLine = headers.concat("\n").concat(strLine);
                    while (strLine != null && sizeOfRows < maxSizeFile) {
                        sizeOfRows += strLine.getBytes(StandardCharsets.UTF_8).length;
                        writer.write(strLine);
                        writer.append('\n');
                        strLine = reader.readLine();
                    }
                    sizeOfRows = 0;
                    fileNumber++;
                    files.add(newFile);
                }
            }
        }
        return files;
    }
}
