package com.softwareproject.app.repo;

import java.io.IOException;
import java.util.List;

public interface FileReaderRepo {
    List<String> readLines(String fileName) throws IOException;
}