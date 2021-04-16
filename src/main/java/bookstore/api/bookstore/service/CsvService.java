package bookstore.api.bookstore.service;

import bookstore.api.bookstore.util.CsvParser;
import bookstore.api.bookstore.util.FileHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tatevik Mirzoyan
 * Created on 15-Apr-21
 */
@Service
public class CsvService<T> {

    private final int MB_PER_SPLIT_FILE = 10;

    public List<List<T>> getEntitiesFromCsv(MultipartFile file, Class<T> clazz) throws IOException {
        FileHelper fileHelper = new FileHelper();
        CsvParser<T> csvParser = new CsvParser<>();
        return fileHelper.splitFile(file, MB_PER_SPLIT_FILE)
                .stream()
                .map((temp) -> csvParser.parse(temp, clazz))
                .collect(Collectors.toList());
    }
}
