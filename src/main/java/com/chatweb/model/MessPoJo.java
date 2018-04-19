/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chatweb.model;

/**
 *
 * @author TAND.M
 */
public class MessPoJo<T> {
    private String event;
    private T value;

    public MessPoJo(String event, T value) {
        this.event = event;
        this.value = value;
    }

    public MessPoJo(){
        
    }
    public String getEvent() {
        return event;
    }

    public T getValue() {
        return value;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public void setValue(T value) {
        this.value = value;
    }
    
    
}
