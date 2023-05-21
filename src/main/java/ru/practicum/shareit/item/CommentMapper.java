package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;


public class CommentMapper {
    public static CommentDto CommentToCommentDto(Comment comment) {
        return CommentDto.builder()
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .id(comment.getId())
                .build();
    }

    public static Comment CommentDtoToComment(CommentDto commentDto, User user, Item item) {
        return Comment.builder()
                .text(commentDto.getText())
                .author(user)
                .created(commentDto.getCreated())
                .id(commentDto.getId())
                .item(item)
                .build();
    }

    public static List<CommentDto> ListCommentToListCommentDto(List<Comment> commentList) {
        return commentList.stream().map(CommentMapper::CommentToCommentDto).collect(Collectors.toList());
    }

}
