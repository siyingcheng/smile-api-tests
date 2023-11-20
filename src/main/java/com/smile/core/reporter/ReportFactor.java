package com.smile.core.reporter;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.google.gson.Gson;
import com.smile.core.apidriver.ApiResponseDto;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@UtilityClass
@Slf4j
public class ReportFactor {
    private final Reporter reporter = Reporter.getInstance();

    public static void reportResponseBody(Response response, boolean isDeepReporting) {
        if (!isDeepReporting) return;
        String body = response.body().asString().isEmpty()
                ? "No Content"
                : MarkupHelper.createJsonCodeBlock(response.getBody().as(ApiResponseDto.class)).getMarkup();
        String details = "Response Body " +
                " <a href=\"#\" onclick=\"$(this).next('div').toggle()\"> Expand / Collapse </a>" +
                "<div style=\"display: none;\">" +
                body +
                "</div>";
        reporter.log(Status.INFO, details);
    }

    public static void reportRequestBody(String payload, boolean isDeepReporting) {
        if (!isDeepReporting) return;
        String body = StringUtils.isEmpty(payload)
                ? "No Content"
                : MarkupHelper.createJsonCodeBlock(new Gson().fromJson(payload, Map.class)).getMarkup();
        String details = "Request Body " +
                " <a href=\"#\" onclick=\"$(this).next('div').toggle()\"> Expand / Collapse </a>" +
                "<div style=\"display: none;\">" +
                body +
                "</div>";
        reporter.log(Status.INFO, details);
    }

    public static void reportResponseHeaders(Response response, boolean isDeepReporting) {
        reportHeaders(response.getHeaders(), isDeepReporting, "Response Headers");
    }

    public static void reportRequestUrl(String method, String url) {
        String requestUrl = String.format("[%s]: <a>%s</a>", method, url);
        reporter.log(Status.INFO, requestUrl);
        log.info(requestUrl);
    }

    public static void reportRequestHeader(RequestSpecification request, boolean isDeepReporting) {
        Headers headers = ((FilterableRequestSpecification) request).getHeaders();
        reportHeaders(headers, isDeepReporting, "Request Headers");
    }

    private static void reportHeaders(Headers headers, boolean isDeepReporting, String headerText) {
        StringBuilder builder = new StringBuilder();
        builder.append(headerText)
                .append(" <a href=\"#\" onclick=\"$(this).next('div').toggle()\"> Expand / Collapse </a>")
                .append("<div style=\"display: none;\">");
        List<String> headerItems = new ArrayList<>();
        headers.forEach(header -> headerItems.add(header.getName() + " : " + header.getValue()));
        builder.append(MarkupHelper.createUnorderedList(headerItems).getMarkup())
                .append("</div>");
        if (isDeepReporting) {
            reporter.log(Status.INFO, builder.toString());
        }
    }
}
