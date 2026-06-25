package csh.back.domain.tem.dto;

public class TemplateResponseDto {
    public record CreateResponse(
            Long id,
            String tem
    ) {

    }

    public record DeleteResponse(
            Long id,
            String tem
    ) {

    }

    public record UpdateResponse(
            Long id,
            String tem
    ) {

    }
}
