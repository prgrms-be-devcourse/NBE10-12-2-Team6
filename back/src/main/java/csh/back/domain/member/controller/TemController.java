package csh.back.domain.member.controller;

import csh.back.domain.member.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/")
@RestController
public class TemController {
    private final TemplateService templateService;
}
