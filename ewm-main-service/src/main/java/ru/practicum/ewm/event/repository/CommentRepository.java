package ru.practicum.ewm.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
