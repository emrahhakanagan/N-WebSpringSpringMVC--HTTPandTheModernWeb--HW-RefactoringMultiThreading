package org.example;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Request {
    private final String method;
    private final String path;
    private final String pathWithoutQS;
    private final Map<String, String> headers;
    private final BufferedReader in;
    private final List<NameValuePair> params;
    private final Map<String, List<String>> postParams = new HashMap<>();
    private final List<FileItem> multipartItems = new ArrayList<>();

    public Request(String method, String path, Map<String, String> headers, BufferedReader in, InputStream inputStream) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.in = in;
        this.pathWithoutQS = getOnlyPathWithoutQS(path);
        this.params = parsQueryString(path);

        if (method.equalsIgnoreCase("POST") && headers.containsKey("Content-Type")) {
            parseBody(in, headers.get("Content-Type"));
        }

        // Обработка multipart/form-data
        String contentType = headers.get("Content-Type");
        if (contentType != null && contentType.startsWith("multipart/form-data")) {
            parseMultipartData(inputStream, contentType);
        }
    }

    private void parseMultipartData(InputStream inputStream, String contentType) {
        // Создаем фабрику для дискового базового репозитория файлов
        FileItemFactory factory = new DiskFileItemFactory();

        // Создаем новый обработчик загрузки файлов
        FileUpload upload = new FileUpload(factory);

        // Настраиваем обработчик загрузки файлов для обработки мультипарт-форматированных данных
        upload.setHeaderEncoding("UTF-8");

        try {

            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            InputStreamRequestContext requestContext = new InputStreamRequestContext(inputStream, contentType, contentLength);

            // Парсим запрос, чтобы получить айтемы файла
            List<FileItem> items = upload.parseRequest(requestContext);


        } catch (FileUploadException e) {
            throw new RuntimeException("Ошибка при разборе мультипарт данных", e);
        }
    }

    public FileItem getPart(String name) {
        return multipartItems.stream()
                .filter(item -> item.getFieldName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public List<FileItem> getMultipartItems() {
        return multipartItems;
    }



    private void parseBody(BufferedReader in, String contentType) {
        if ("application/x-www-form-urlencoded".equalsIgnoreCase(contentType)) {
            StringBuilder bodyBuilder = new StringBuilder();
            String line;
            while (true) {
                try {
                    if (!((line = in.readLine()) != null && (!line.isEmpty()))) break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                bodyBuilder.append(line);
            }

            String body = URLDecoder.decode(bodyBuilder.toString(), StandardCharsets.UTF_8);
            Arrays.stream(body.split("&"))
                    .map(param -> param.split("="))
                    .forEach(parts -> {
                        if (parts.length == 2) {
                            postParams.computeIfAbsent(parts[0], k -> new ArrayList<>()).add(parts[1]);
                        }
                    });
        }
    }

    public String getPostParam(String name) {
        List<String> values = postParams.get(name);

        if (values != null && !values.isEmpty()) {
            return values.get(0);
        }
        return null;
    }

    public Map<String, List<String>> getPostParams() {
        return Collections.unmodifiableMap(postParams);
    }

    private List<NameValuePair> parsQueryString(String path) {
        try {
            return URLEncodedUtils.parse(new URI(path), StandardCharsets.UTF_8);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public List<NameValuePair> getQueryParams() {
        return params;
    }

    private String getOnlyPathWithoutQS(String path) {
        String[] parts = path.split("\\?", 2);
        return parts[0];
    }

    public String getQueryParam(String name) {
        return params.stream()
                .filter(param -> name.equals(param.getName()))
                .findFirst()
                .map(NameValuePair::getValue)
                .orElse(null);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public BufferedReader getIn() {
        return in;
    }

    public String getPathWithoutQS() {
        return pathWithoutQS;
    }
}

