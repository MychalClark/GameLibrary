package edu.ranken.mychal_clark.gamelibrary.ebay;

public enum AuthEnvironment {
    SANDBOX(
        "api.sandbox.ebay.com",
        "https://api.sandbox.ebay.com",
        "https:api.sandbox.ebay.com/identity/v1/ouath2/token"
    ),
    PRODUCTION(
        "api.ebay.com",
        "https://api.ebay.com",
        "https://api.ebay.com/identity/v1/ouath2/token"
    );
    public final String host;
    public final String baseUrl;
    public final String authUrl;

    AuthEnvironment(String host, String baseUrl, String authUrl){
        this.host = host;
        this.baseUrl = baseUrl;
        this.authUrl = authUrl;
    }
}
