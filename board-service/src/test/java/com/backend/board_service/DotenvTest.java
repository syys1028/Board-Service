package com.backend.board_service;

import io.github.cdimascio.dotenv.Dotenv;

public class DotenvTest {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        System.out.println("DB_URL: " + dotenv.get("DB_URL"));
        System.out.println("DB_USER: " + dotenv.get("DB_USER"));
        System.out.println("DB_PASSWORD: " + dotenv.get("DB_PASSWORD"));
    }
}
