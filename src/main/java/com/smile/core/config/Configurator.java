package com.smile.core.config;

import lombok.Data;

import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class Configurator {
    private static final String DEFAULT_REPORT_DIRECTOR = System.getProperty("user.dir") + "/report";
    private static final String DEFAULT_LOG_DIRECTOR = System.getProperty("user.dir") + "/log";

    private String backendUrl;
    private static Configurator instance = null;
    private Path reportPath;
    private Path logPath;
    private Map<String, String> params;

    private Configurator() {
    }

    public static synchronized Configurator getInstance() {
        if (instance == null) {
            instance = new Configurator();
        }
        return instance;
    }

    public void initParameters(Map<String, String> params) {
        this.params = normalizeParameters(params);
        setBackendUrl(getParameter(ConfigKeys.BACKEND_URL));
        this.reportPath = Path.of(getParameterOrDefault(ConfigKeys.REPORT_LOCATION, DEFAULT_REPORT_DIRECTOR));
        this.logPath = Path.of(getParameterOrDefault(ConfigKeys.LOG_LOCATION, DEFAULT_LOG_DIRECTOR));
    }

    public String getParameterOrDefault(ConfigKeys key, String defaultValue) {
        return this.params.getOrDefault(key.name(), defaultValue);
    }

    public String getParameter(ConfigKeys key) {
        return this.params.get(key.name());
    }

    private Map<String, String> normalizeParameters(Map<String, String> params) {
        return params.entrySet().stream().collect(
                Collectors.toMap(
                        entry -> entry.getKey().toUpperCase(),
                        Map.Entry::getValue
                )
        );
    }

    public void setBackendUrl(String backendUrl) {
        if (!backendUrl.startsWith("http")) {
            backendUrl = "http://" + backendUrl;
        }
        this.backendUrl = backendUrl;
    }
}
