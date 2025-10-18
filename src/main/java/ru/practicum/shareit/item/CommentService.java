package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemBookingInfoService itemBookingInfoService;

    @Autowired
    public CommentService(CommentRepository commentRepository, UserService userService,
                          ItemService itemService, ItemBookingInfoService itemBookingInfoService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.itemService = itemService;
        this.itemBookingInfoService = itemBookingInfoService;
    }

    @Transactional
    public Comment createComment(CommentCreateDto commentDto, Long itemId, Long userId) {
        User author = userService.getUserById(userId);
        Item item = itemService.getItemById(itemId);

        if (!itemBookingInfoService.hasUserBookedItem(userId, itemId)) {
            throw new ValidationException("User can only comment on items they have booked");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByItemId(Long itemId) {
        return commentRepository.findByItemIdOrderByCreatedDesc(itemId);
    }
}