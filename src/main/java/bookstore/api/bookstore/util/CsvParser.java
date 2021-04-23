package bookstore.api.bookstore.util;

import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * @author Tatevik Mirzoyan
 * Created on 15-Apr-21
 */
public class CsvParser<T> {

    private final Logger logger = LoggerFactory.getLogger(CsvParser.class);

    public List<T> parse(File file, Class<T> clazz) {

        try (Reader reader = new FileReader(file)) {
            return new CsvToBeanBuilder<T>(reader)
                    .withType(clazz)
                    .withIgnoreEmptyLine(true)
                    .withThrowExceptions(false)
                    .withIgnoreQuotations(true)
                    // for Users import comment this part
                    .withSeparator(';')
                    .build()
                    .parse();
        } catch (IOException e) {
            logger.warn(e.getMessage(), clazz.getName());
        } catch (IllegalStateException ex) {
            logger.warn(ex.getMessage());
        } catch (Exception exx) {
            System.out.println(exx.getMessage());
        }
        return List.of();
    }
}
