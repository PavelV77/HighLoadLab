package com.example.KursNews.mappers;

import com.example.KursNews.dto.NewsDto;
import com.example.KursNews.entities.News;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NewsMapper {
    NewsDto toDto(News news);
    News toEntity(NewsDto dto);
}
