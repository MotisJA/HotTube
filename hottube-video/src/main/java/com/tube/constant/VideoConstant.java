package com.tube.constant;

public class VideoConstant {
    public static final String VIDEO_SUFFIX = ".mp4";
    public static final String M3U8_REGEX = "([\\S]+\\.ts)";
    public static final String MINIO_VIDEO_PREFIX = "videos/";

    public static final int VIDEO_STATUS_CONVERTING = 0;
    public static final int VIDEO_STATUS_AUDIT = 1;
    public static final int VIDEO_STATUS_REVIEWED = 2;
    public static final int VIDEO_STATUS_FAILED = 3;
    public static final int VIDEO_STATUS_DELETED = 4;
}
