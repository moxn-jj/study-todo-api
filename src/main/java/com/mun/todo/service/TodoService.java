package com.mun.todo.service;

import com.mun.todo.entity.Todo;
import com.mun.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;


    public List<Todo> getTodos(Sort sort) {
        return todoRepository.findAll(sort);
    }

    public void postTodo(Todo todo) {
        todoRepository.save(todo);
    }

    public void deleteTodo(Long id) {
        todoRepository.deleteById(id);
    }

    public Todo findTodoById(Long id) {
        return todoRepository.findById(id).orElse(new Todo());
    }
}
