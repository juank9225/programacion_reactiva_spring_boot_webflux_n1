package com.example.demo.modelos;

import java.util.ArrayList;
import java.util.List;

public class Comentarios {

    private List<String> comentarios;

    public Comentarios() {//creamos un constructor que inicia comentarios vacio
        this.comentarios = new ArrayList<>();
    }

    public void addComentarios(String comentarios) {//metodo para agregr uno a uno los comentarios
        this.comentarios.add(comentarios);
    }

    @Override
    public String toString() {
        return "comentarios=" + comentarios;
    }
}
