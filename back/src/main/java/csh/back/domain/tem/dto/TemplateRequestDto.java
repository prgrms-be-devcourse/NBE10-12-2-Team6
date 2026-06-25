package csh.back.domain.tem.dto;

public class TemplateRequestDto {
    public record CreateRequest(
            String title,
            String content) {

    }

    public record DeleteRequest(
            Long id
    ) {

    }

    public record UpdateRequest(
            Long id,
            String content
    ) {

    }
}
