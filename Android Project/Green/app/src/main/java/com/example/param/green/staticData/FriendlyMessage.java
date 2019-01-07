package com.example.param.green.staticData;

/**
 * Created by Param on 07-10-2017.
 */

public class FriendlyMessage {
    String name;


   public FriendlyMessage(){}

   public FriendlyMessage(String name, String message){
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    String message;

}
