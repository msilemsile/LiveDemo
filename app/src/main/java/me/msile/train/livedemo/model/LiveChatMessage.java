package me.msile.train.livedemo.model;

/**
 * 聊天消息
 */

public class LiveChatMessage {

    public String userName;
    public String message;

    public static LiveChatMessage obtainMessage(String userName, String message) {
        LiveChatMessage chatMessage = new LiveChatMessage();
        chatMessage.userName = userName;
        chatMessage.message = message;
        return chatMessage;
    }

}
