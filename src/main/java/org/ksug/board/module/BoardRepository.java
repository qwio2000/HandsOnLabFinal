package org.ksug.board.module;

import org.ksug.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
	
	Board findByName(String name);
	
}
