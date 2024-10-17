package ru.practicum.shareit.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Mapper
public interface ItemRequestMapper {

    ItemRequest mapToItemRequest(ItemRequestDto itemRequest);

    ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest);
}