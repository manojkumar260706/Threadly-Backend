package com.threadly.controller;

import com.threadly.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagsController {

    @Autowired
    private TagService tagService;

    @GetMapping("/trending")
    public List<String> getTrendingTags(@RequestParam(defaultValue = "10") int limit) {
        return tagService.getTrendingTags(limit);
    }
}
