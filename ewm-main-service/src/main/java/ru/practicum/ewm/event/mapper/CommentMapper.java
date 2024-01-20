package ru.practicum.ewm.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.event.dto.CommentDto;
import ru.practicum.ewm.event.dto.CommentRequest;
import ru.practicum.ewm.event.model.Comment;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {
    public Comment createComment(CommentRequest commentRequest, Event event, User author) {
        return Comment.builder()
                .text(commentRequest.getText())
                .event(event)
                .author(author)
                .created(LocalDateTime.now())
                .build();
    }

    public CommentDto createCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .eventId(comment.getEvent().getId())
                .author(UserMapper.createUserShortDto(comment.getAuthor()))
                .created(LocalDateTime.now())
                .build();
    }
}
