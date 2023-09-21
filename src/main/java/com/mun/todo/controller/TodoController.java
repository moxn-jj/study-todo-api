package com.mun.todo.controller;

import com.mun.todo.entity.Todo;
import com.mun.todo.enums.CustomErrorCode;
import com.mun.todo.exception.CustomException;
import com.mun.todo.service.TodoService;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    /**
     * 목록 조회
     * @return 
     * @throws Exception
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getTodos(Authentication auth) {

        if(auth == null || StringUtils.isBlank(auth.getName())){
            throw new CustomException(CustomErrorCode.ERR_BAD_REQUEST);
        }
        List<Todo> todos = todoService.getTodos(Long.parseLong(auth.getName()), Sort.by(Sort.Direction.DESC, "id"));

        return ResponseEntity.ok(todos);
    }

    /**
     * 등록
     * @param todo 
     * @return
     * @throws Exception
     */
    @PostMapping
    public ResponseEntity<HttpStatus> postTodo(@RequestBody Todo todo, Authentication auth) {

        todo.setIsComplete(false);
        todo.setMemberId(Long.parseLong(auth.getName()));
        todoService.postTodo(todo);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 수정
     * @param id 
     * @return
     * @throws Exception
     */
    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> putTodo(@PathVariable("id") Long id) {

        Todo todo = todoService.findTodoById(id);
        Boolean isComplete = todo.getIsComplete()? false : true;
        todo.setIsComplete(isComplete);
        todoService.postTodo(todo);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 삭제
     * @param id 
     * @return
     * @throws Exception
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteTodo(@PathVariable("id") Long id) {

        todoService.deleteTodo(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
