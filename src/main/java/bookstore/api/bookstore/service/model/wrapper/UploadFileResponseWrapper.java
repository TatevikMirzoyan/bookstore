package bookstore.api.bookstore.service.model.wrapper;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Tatevik Mirzoyan
 * Created on 04-Apr-21
 */
@Getter
@Setter
public class UploadFileResponseWrapper {
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;

    public UploadFileResponseWrapper(String fileName, String fileDownloadUri, String fileType, long size) {
        this.fileName = fileName;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.size = size;
    }
}
