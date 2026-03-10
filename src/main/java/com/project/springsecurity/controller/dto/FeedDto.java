package com.project.springsecurity.controller.dto;

import java.util.List;

public record FeedDto(List<FeedItemDto> feedItensDtos,
                      int page,
                      int pageSize,
                      int totalPages,
                      long totalElements) {
}
