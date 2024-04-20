package com.example.utilities;

import java.util.HashMap;

public class Contants {
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PREFERENCE_NAME = "ChatAppPreference";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_IMAGE = "image";

    public static final String KEY_FCM_TOKEN = "fcmToKen";
    public static final String KEY_USER = "user";
    public static final String KEY_COLLECTION_CHAT = "chat";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_RECEIVER_ID = "receverId";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_CONVERSATIONS = "Conversations";
    public static final String KEY_SENDER_NAME = "senversations";
    public static final String KEY_RECEIVER_NAME = "receiverName";
    public static final String KEY_SENDER_IMAGE = "senderImage";
    public static final String KEY_RECEIVER_IMAGE = "receiverImage";
    public static final String KEY_LAST_MESSAGE = "lastmessage";
    public static final String KEY_AVAILABILITY ="availability";
    public static final String REMOTE_MSG_AUTHORIZATION ="Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE ="Content-Type";

    public static HashMap<String ,String > remoteMsgHeaders =null;
    public static HashMap<String,String> getRemoteMsgHeaders(){
        if (remoteMsgHeaders ==null){
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(
                    REMOTE_MSG_AUTHORIZATION,"key=AAAAdLF8JCU:APA91bGwFT-NcCDIq1IgXZYfDkGnKBAJgBFqzKQfKj6fSBoFd9eiw9J-IhJ4xUX4gJJ7Pyd1NC35NWT45bcMW6ol0wE9eaKd0iOB7DmEVImPlQTQPggGSpRoZX-cLjNnB1hUpp-2lfa3"
            );
            remoteMsgHeaders.put(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );
        }
        return remoteMsgHeaders;
    }
}
