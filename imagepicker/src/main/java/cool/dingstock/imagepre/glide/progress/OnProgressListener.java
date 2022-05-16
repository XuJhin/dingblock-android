package cool.dingstock.imagepre.glide.progress;

public interface OnProgressListener {
    void onProgress(String url, boolean isComplete, int percentage, long bytesRead, long totalBytes);
}