package com.chaechae.realworldspringboot.article.controller;

import com.chaechae.realworldspringboot.article.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @GetMapping("/tags")
    public ResponseEntity<List<String>> getTags() {
        List<String> tagNameList = tagService.getTagNameList();
        return ResponseEntity.status(200).body(tagNameList);
    }
}
