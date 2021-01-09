package com.mua.json;

public class Application {

    public static void main(String[] args) {
        new JsonDecoder().decode(new JsonLoader().getJsonFromResources("1.json"));
    }

}
