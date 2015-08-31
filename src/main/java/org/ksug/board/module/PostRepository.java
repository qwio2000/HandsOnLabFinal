package org.ksug.board.module;

import java.util.List;

import org.ksug.board.Board;
import org.ksug.board.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
	
	List<Post> findByBoard(Board board);

}
