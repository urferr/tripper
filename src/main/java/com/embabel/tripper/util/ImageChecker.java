package com.embabel.tripper.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;

import com.embabel.common.util.StringTransformer;

public class ImageChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageChecker.class);

    static final Pattern IMG_REX = Pattern.compile("<img[^>]*>");
    static final Pattern SRC_REX = Pattern.compile("src=[\"']([^\"']*)[\"']");

    public static StringTransformer removeInvalidImageLinks = theHtml -> {
	String aResultingHtml = theHtml;
	List<String> allImageTags = findImageTags(theHtml);

	for (var aImageTag : allImageTags) {
	    var aImageUrl = findImageUrl(aImageTag);

	    if (aImageUrl != null && !isImageUrlValid(aImageUrl)) {
		aResultingHtml = aResultingHtml.replace(aImageUrl, "");
	    }
	}

	return aResultingHtml;
    };

    private static List<String> findImageTags(String theHtml) {
	List<String> allImageTags = new ArrayList<>();
	Matcher aMatcher = IMG_REX.matcher(theHtml);

	while (aMatcher.find()) {
	    allImageTags.add(theHtml.substring(aMatcher.start(), aMatcher.end()));
	}

	return allImageTags;
    }

    private static String findImageUrl(String theImageTag) {
	Matcher aMatcher = SRC_REX.matcher(theImageTag);

	if (aMatcher.find() && aMatcher.groupCount() > 0) {
	    return aMatcher.group(1);
	}

	return null;
    }

    private static boolean isImageUrlValid(String theUrl) {
	var aRestClient = RestClient.builder().baseUrl(theUrl).build();

	try {
	    var aResponse = aRestClient.head().retrieve().toBodilessEntity();

	    return aResponse.getStatusCode() == HttpStatus.OK
		    && aResponse.getHeaders().getContentType().toString().startsWith("image/");
	}
	catch (Exception theException) {
	    return false;
	}
    }
}
