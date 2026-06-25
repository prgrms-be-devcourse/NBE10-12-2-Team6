package csh.back.domain.tem.service;

import csh.back.domain.tem.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TemplateService {
    private final TemplateRepository templateRepository;
}
