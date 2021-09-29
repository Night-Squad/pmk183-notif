package com.bjbs.haji.business.apis.controllers.utility;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class CustomMultipartFile implements MultipartFile {

    private final byte[] fileContent;

    private String fileName;

    private String contentType;

    private File file;

    private String destPath = System.getProperty("java.io.tmpdir");

    private FileOutputStream fileOutputStream;

    public CustomMultipartFile(byte[] fileContent, String contentType, String fileName) throws IOException {
        this.fileContent = fileContent;
        this.fileName = fileName;
        this.contentType = contentType;
        file = new File(destPath, fileName);
        file.createNewFile();
    }

    @Override
    public String getName() {
        return this.fileName;
    }

    @Override
    public String getOriginalFilename() {
        return this.fileName;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return fileContent;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(fileContent);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        fileOutputStream = new FileOutputStream(dest);
        fileOutputStream.write(fileContent);
    }

    public File getFile() {
        return file;
    }
}
