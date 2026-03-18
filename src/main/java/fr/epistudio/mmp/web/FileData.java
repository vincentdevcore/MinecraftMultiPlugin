package fr.epistudio.mmp.web;

public class FileData {

    private String previewFileName;
    private String newFileName;
    private boolean delete;
    private boolean newFile;

    public FileData() {
        this.previewFileName = "";
        this.newFileName = "";
        this.delete = false;
        this.newFile = false;
    }

    public FileData(String previewFileName) {
        this.previewFileName = previewFileName;
        this.newFileName = "";
        this.delete = false;
        this.newFile = false;
    }

    public String getPreviewFileName() {
        return previewFileName;
    }

    public String getNewFileName() {
        return newFileName;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setPreviewFileName(String previewFileName) {
        this.previewFileName = previewFileName;
    }

    public void setNewFileName(String newFileName) {
        this.newFileName = newFileName;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public void setNewFile(boolean b) {
        this.newFile = b;
    }

    public boolean isNewFile() {
        return newFile;
    }
}