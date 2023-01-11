package com.chaechae.realworldspringboot.article.service;

import com.chaechae.realworldspringboot.article.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<String> getTagNameList() {
        return tagRepository.getTagNameList();
    }
}
