package ru.practicum.shareit.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

@Mapper
public interface CommentMapper {

    Comment mapToComment(CommentDto commentDto);

    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "authorName", source = "author.name")
    CommentDto mapToCommentDto(Comment comment);
}
