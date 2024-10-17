package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.erorr.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserService userServiceImpl;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemRequest create(long userId, ItemRequestDto dto) {
        log.info("Create ItemRequest {}, for user {}", dto, userId);
        User user = getRequestor(userId);
        ItemRequest itemRequest = itemRequestMapper.mapToItemRequest(dto);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequest resultItemRequest = itemRequestRepository.save(itemRequest);
        log.info("Created ItemRequest {}, for user {}", resultItemRequest, userId);
        return resultItemRequest;
    }

    @Override
    public ItemRequestDto getRequestByIdWithItems(long userId, long requestId) {
        log.info("Get ItemRequest with ID {}", requestId);
        getRequestor(userId);
        ItemRequestDto itemRequest = itemRequestMapper.mapToItemRequestDto(itemRequestRepository
                .findById(requestId).orElseThrow(() -> new EntityNotFoundException("Request not found")));
        itemRequest.setItems(itemRepository.findAllByRequestId(requestId)
                .stream().map(itemMapper::mapToItemDto).toList());
        return itemRequest;
    }

    @Override
    public List<ItemRequestDto> getUserRequests(long userId, Integer from, Integer size) {
        log.info("Get User requests with ID {}", userId);
        getRequestor(userId);
        Pageable pageable = createPageable(from, size, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequestDto> requests = getRequestsForUser(userId, pageable);
        List<Long> requestIds = extractRequestIds(requests);
        Map<Long, List<ItemDto>> itemDtoForRequest = toReceiveItemsForRequest(requestIds);

        setItemsForRequests(requests, itemDtoForRequest);

        return requests;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(long userId, Integer from, Integer size) {
        log.info("Get All Request");
        Pageable pageable = createPageable(from, size, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequestDto> requests = getRequestsFromOtherUsers(userId, pageable);
        List<Long> requestIds = extractRequestIds(requests);
        Map<Long, List<ItemDto>> itemDtoForRequest = toReceiveItemsForRequest(requestIds);

        setItemsForRequests(requests, itemDtoForRequest);
        return requests;
    }

    @Override
    public ItemRequest getRequestById(long userId, long requestId) {
        log.info("Get Request with ID {}", requestId);
        return itemRequestRepository
                .findById(requestId).orElseThrow(() -> new EntityNotFoundException("Request not found"));
    }

    private User getRequestor(long userId) {
        log.debug("Get User requestor with ID {}", userId);
        return userServiceImpl.getUser(userId);
    }

    private Map<Long, List<ItemDto>> toReceiveItemsForRequest(List<Long> requestId) {
        log.debug("Get Requests for requestListId {}", requestId);
        return itemRepository.findAllByRequestIds(requestId)
                .stream().map(itemMapper::mapToItemDto)
                .collect(Collectors.groupingBy(ItemDto::getRequestId));
    }

    private List<Long> extractRequestIds(List<ItemRequestDto> requests) {
        log.debug("Extract ID requests for requestListId {}", requests);
        return requests.stream()
                .map(ItemRequestDto::getId)
                .toList();
    }

    private void setItemsForRequests(List<ItemRequestDto> requests, Map<Long, List<ItemDto>> itemDtoForRequest) {
        log.debug("Set Items for requests {}", requests);
        requests.forEach(itemRequestDto -> {
            List<ItemDto> items = Optional.ofNullable(itemDtoForRequest.get(itemRequestDto.getId()))
                    .orElse(Collections.emptyList());
            itemRequestDto.setItems(items);
        });
    }

    private List<ItemRequestDto> getRequestsForUser(long userId, Pageable pageable) {
        log.debug("Get Requests for user with ID {}", userId);
        return itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId, pageable)
                .stream()
                .map(itemRequestMapper::mapToItemRequestDto)
                .toList();
    }

    private List<ItemRequestDto> getRequestsFromOtherUsers(long userId, Pageable pageable) {
        log.debug("Receive requests for other user");
        return itemRequestRepository
                .findAllByRequestorIdNotOrderByCreatedDesc(userId, pageable).stream()
                .map(itemRequestMapper::mapToItemRequestDto)
                .toList();
    }

    private Pageable createPageable(Integer from, Integer size, Sort sort) {
        log.debug("Create Pageable from {}, size {}", from, size);
        if (from != null && size != null) {
            return PageRequest.of(from / size, size, sort);
        }
        return Pageable.unpaged();
    }
}