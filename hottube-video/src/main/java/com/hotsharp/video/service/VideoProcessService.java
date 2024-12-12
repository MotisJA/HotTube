package com.hotsharp.video.service;

import java.io.File;

public interface VideoProcessService {
    void merge(int vid, String hash, Integer userId);

    void deleteFile(File file);
}
