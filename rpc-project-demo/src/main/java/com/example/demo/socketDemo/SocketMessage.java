package com.example.demo.socketDemo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;


@Data
@AllArgsConstructor
public class SocketMessage implements Serializable {

    private String content;
}