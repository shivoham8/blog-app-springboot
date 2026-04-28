package com.blog.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleError(Exception e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "error";
    }
}