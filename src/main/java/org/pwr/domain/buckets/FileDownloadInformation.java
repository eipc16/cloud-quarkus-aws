package org.pwr.domain.buckets;

public class FileDownloadInformation {

    private FileDetails fileDetails;
    private StreamingResponse streamingResponse;

    public FileDownloadInformation(FileDetails fileDetails, StreamingResponse streamingResponse) {
        this.fileDetails = fileDetails;
        this.streamingResponse = streamingResponse;
    }

    public FileDetails getFileDetails() {
        return fileDetails;
    }

    public StreamingResponse getStreamingResponse() {
        return streamingResponse;
    }
}
