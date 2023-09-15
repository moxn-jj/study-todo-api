package com.mun.todo.controller;

import com.mun.todo.entity.Todo;
import com.mun.todo.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/api/todos")
public class TodoController {

    @Autowired
    private TodoService todoService;

    /**
     * 목록 조회
     * @return 
     * @throws Exception
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getTodos() throws Exception {

        List<Todo> todos = todoService.getTodos(Sort.by(Sort.Direction.ASC, "id"));

        return ResponseEntity.ok(todos);
    }

    /**
     * 등록
     * @param todo 
     * @return
     * @throws Exception
     */
    @PostMapping
    public ResponseEntity<String> postTodo(@RequestBody Todo todo) throws Exception {

        todo.setIsComplete(false);
        todoService.postTodo(todo);

        return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
    }

    /**
     * 수정
     * @param id 
     * @return
     * @throws Exception
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> putTodo(@PathVariable("id") Long id) throws Exception {

        Todo todo = todoService.findTodoById(id);
        Boolean isComplete = todo.getIsComplete()? false : true;
        todo.setIsComplete(isComplete);
        todoService.postTodo(todo);

        return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
    }

    /**
     * 삭제
     * @param id 
     * @return
     * @throws Exception
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTodo(@PathVariable("id") Long id) throws Exception {

        todoService.deleteTodo(id);

        return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
    }
}
