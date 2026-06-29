package csh.back.domain.member.service;

import csh.back.domain.member.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TemplateService {
    private final TemplateRepository templateRepository;
}
