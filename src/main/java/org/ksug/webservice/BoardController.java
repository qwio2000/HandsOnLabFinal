package org.ksug.webservice;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.validation.Valid;

import org.ksug.board.Board;
import org.ksug.board.Post;
import org.ksug.board.module.BoardService;
import org.ksug.board.module.PostForm;
import org.ksug.board.module.ResourceNotFoundException;
import org.ksug.webservice.support.MessageSourceAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/board")
public class BoardController {
	
	private BoardService boardService;
	private MessageSourceAccessor messageSource;
	
	@RequestMapping(value = "/{boardname}/info", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ResponseEntity<Board> info(@PathVariable String boardname) {
		return ResponseEntity.ok(boardService.findBoard(boardname));
	}
	
	@RequestMapping(value = "/{boardname}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ResponseEntity<List<Post>> listPosts(@PathVariable String boardname) {
		return ResponseEntity.ok(boardService.findPosts(boardname));
	}
	
	@RequestMapping(value = "/{boardname}", method = { RequestMethod.POST })
	public ResponseEntity<Post> createPost(@PathVariable String boardname, @Valid PostForm postForm) {
		return ResponseEntity.status(HttpStatus.CREATED).body(boardService.writePost(boardname, postForm));
	}
	
	@RequestMapping(value = "/{boardname}/{postId}", method = { RequestMethod.PUT })
	public ResponseEntity<Post> updatePost(@PathVariable Long postId, String author, String title, String content) {
		return ResponseEntity.ok(boardService.editPost(postId, author, title, content));
	}
	
	@RequestMapping(value = "/{boardname}/{postId}", method = { RequestMethod.DELETE })
	public ResponseEntity<Post> deletePost(@PathVariable Long postId) {
		return ResponseEntity.ok(boardService.erasePost(postId));
	}
	
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, Object>> resourceNotFoundException(ResourceNotFoundException exception, Locale locale) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", new Date());
		body.put("status", exception.getStatus().value());
		body.put("error", exception.getError());
		body.put("message", messageSource.getMessage(exception.getCode(), exception.getArgs(), locale).orElse("No message available"));		
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
	}
	
	@ExceptionHandler(BindException.class)
	public ResponseEntity<Map<String, Object>> bindException(BindException exception, Locale locale) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", new Date());
		body.put("status", HttpStatus.BAD_REQUEST.value());
		body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
		
		List<String> errors = new ArrayList<>();
		exception.getAllErrors().forEach(error -> {
			messageSource.getMessage(error, locale).ifPresent(errors::add);
		});
		if(!errors.isEmpty()) {
			body.put("errors", errors);
		}
		
		body.put("message", messageSource.getMessage("error.BindException." + exception.getObjectName(), locale).orElse("No message available"));
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}
	
	
	@Autowired
	public void setBoardService(BoardService boardService) {
		this.boardService = boardService;
	}

	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = new MessageSourceAccessor(messageSource);
	}
	
}
