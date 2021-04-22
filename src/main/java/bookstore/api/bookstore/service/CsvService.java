package bookstore.api.bookstore.service;

import bookstore.api.bookstore.util.CsvParser;
import bookstore.api.bookstore.util.FileHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tatevik Mirzoyan
 * Created on 15-Apr-21
 */
@Service
public class CsvService<T> {

    private final int MB_PER_SPLIT_FILE = 10;

    public List<T> getEntitiesFromCsv(MultipartFile file, Class<T> clazz) throws IOException {
        FileHelper fileHelper = new FileHelper();
        CsvParser<T> csvParser = new CsvParser<>();
        List<T> list = new ArrayList<>();
         fileHelper.splitFile(file, MB_PER_SPLIT_FILE)
                .forEach((temp) -> list.addAll(csvParser.parse(temp, clazz)));
         return list;
    }
}
