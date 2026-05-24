package com.ts.dto;

import jakarta.validation.constraints.NotBlank;

public class NoticeRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    private String type;
    private Boolean published;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Boolean getPublished() { return published; }
    public void setPublished(Boolean published) { this.published = published; }
}
