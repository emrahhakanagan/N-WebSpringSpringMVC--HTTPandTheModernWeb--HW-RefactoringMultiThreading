package org.example;

import org.apache.commons.fileupload.RequestContext;
import java.io.InputStream;

public class InputStreamRequestContext implements RequestContext {
    private final InputStream inputStream;
    private final String contentType;
    private final int contentLength;

    public InputStreamRequestContext(InputStream inputStream, String contentType, int contentLength) {
        this.inputStream = inputStream;
        this.contentType = contentType;
        this.contentLength = contentLength;
    }

    @Override
    public String getCharacterEncoding() {
        // Возвращает кодировку запроса. В простейшем случае можно вернуть null или "UTF-8".
        return "UTF-8";
    }

    @Override
    public String getContentType() {
        // Возвращает тип контента запроса. Это значение должно быть получено из заголовка запроса "Content-Type".
        return contentType;
    }

    @Override
    public int getContentLength() {
        // Возвращает длину контента запроса. Это значение должно быть получено из заголовка запроса "Content-Length".
        return contentLength;
    }

    @Override
    public InputStream getInputStream() {
        // Возвращает InputStream, который может быть использован для чтения тела запроса.
        return inputStream;
    }
}

