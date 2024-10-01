package com.elixr.ChatApp_UserManagement.contants;

public class UserConstants {
    public static final String ALLOWED_HEADERS = "*";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String CLIENT_ERROR = "Client Error: ";
    public static final String EMPTY_STRING = "";
    public static final String NEW_NAME = "newName";
    public static final String OLD_NAME = "oldName";
    public static final String REGISTERED_CORS_PATTERN = "/**";
    public static final String SERVER_ERROR = "Server Error: ";
    public static final String USER_COLLECTION_NAME = "users";
    private static final String UI_BASEURL = "ui.baseurl";
    public static final String UI_URL_VALUE = "${"+UI_BASEURL+"}";
    private static final String AUTH_BASEURL = "auth.baseurl";
    public static final String AUTH_SERVICE_URL_VALUE = "${"+AUTH_BASEURL+"}";
    private static final String MESSAGE_BASEURL = "message.baseurl";
    public static final String MESSAGE_SERVICE_URL_VALUE = "${"+MESSAGE_BASEURL+"}";
}
