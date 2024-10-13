package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItemId(long itemId);

    @Query("""
            SELECT c FROM Comment c
            WHERE c.item.id IN :itemIds
            """)
    List<Comment> findAllByItemId(@Param("itemIds") List<Long> itemIds);
}
