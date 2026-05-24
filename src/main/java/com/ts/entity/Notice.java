package com.ts.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ts_notice")
public class Notice extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type = Type.NOTICE;

    @Column(nullable = false)
    private Boolean published = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    public enum Type { NOTICE, NEWS, ACTIVITY }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public Boolean getPublished() { return published; }
    public void setPublished(Boolean published) { this.published = published; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
}
