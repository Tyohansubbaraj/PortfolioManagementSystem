package com.marketminds.portfoliomanagementsystem.config;

/**
 * Configuration class for managing API keys securely
 * This class stores API credentials in one centralized location
 * WARNING: In production, use environment variables or secret management tools
 */
public class ApiKeyConfig {

    // ========== HARDCODED API KEY SECTION ==========
    // WARNING: DO NOT commit this to version control in production
    // For development purposes only
    // In production, use:
    // 1. Environment variables
    // 2. Spring Cloud Config
    // 3. Vault, AWS Secrets Manager, or similar tools

    //private static final String GEMINI_API_KEY = "AIzaSyA-ihBv5cxaH7eECqkdkTfq34S51eJm7wo";
    private static final String GEMINI_API_KEY = "AIzaSyAsQ08dZjqa7VQLgO2MYrENGCPwZlvZZOs";
    // =============================================

    /**
     * Get the Gemini API key
     * @return The API key for Gemini service
     */
    public static String getGeminiApiKey() {
        // First, try to get from environment variable (preferred in production)
        String envApiKey = System.getenv("GEMINI_API_KEY");
        if (envApiKey != null && !envApiKey.isEmpty()) {
            return envApiKey;
        }

        // Fallback to system property if set
        String propApiKey = System.getProperty("GEMINI_API_KEY");
        if (propApiKey != null && !propApiKey.isEmpty()) {
            return propApiKey;
        }

        // Finally, use the hardcoded value
        return GEMINI_API_KEY;
    }

    /**
     * Set the API key programmatically (useful for testing)
     * @param apiKey The API key to set
     */
    public static void setGeminiApiKey(String apiKey) {
        System.setProperty("GEMINI_API_KEY", apiKey);
    }

    /**
     * Check if API key is configured
     * @return true if API key is available
     */
    public static boolean isApiKeyConfigured() {
        return !getGeminiApiKey().isEmpty();
    }
}
