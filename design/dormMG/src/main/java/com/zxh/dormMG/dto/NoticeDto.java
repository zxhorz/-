package com.zxh.dormMG.dto;

public class NoticeDto {
    private String title;
    private String content;

    public NoticeDto() {
    }

    public NoticeDto(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
