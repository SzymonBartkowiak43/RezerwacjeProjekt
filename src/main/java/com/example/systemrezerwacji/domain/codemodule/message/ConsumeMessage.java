package com.example.systemrezerwacji.domain.codemodule.message;


public record ConsumeMessage(String message, boolean isSuccess) {
    static final String SUCCESS_MESSAGE = "success";

    public static ConsumeMessage success() {
        return new ConsumeMessage(SUCCESS_MESSAGE, true);
    }

    public static ConsumeMessage failure(String message) {
        return new ConsumeMessage(message, false);
    }
}
