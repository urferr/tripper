package com.embabel.tripper.util;

import static com.embabel.tripper.util.ImageChecker.IMG_REX;
import static com.embabel.tripper.util.ImageChecker.SRC_REX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.regex.Matcher;

import org.junit.jupiter.api.Test;

import com.embabel.common.util.StringTransformer;

public class ImageCheckerTest {
    private static final String htmlWithImage = """
    	<header>
    	    <img src="https://plus.unsplash.com/premium_photo-1681487870238-4a2dfddc6bcb?q=80&w=880&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D" alt="A todo list"/>
    	    <div>
    	        <h1>EasyTask</h1>
    	        <p>Enterprise-level task management without friction</p>
    	    	<img src="assets/task-management-logo2.png" alt="A todo list"/>
    	    </div>
    	</header>
    				""";

    @Test
    public void imageTagExists() {
	Matcher aMatcher = IMG_REX.matcher(htmlWithImage);

	int matches = 0;
	while (aMatcher.find()) {
	    matches++;
	}

	assertEquals(matches, 2);
    }

    @Test
    public void srcAttributeExists() {
	Matcher aMatcher = SRC_REX.matcher(
		"<img src=\"https://plus.unsplash.com/premium_photo-1681487870238-4a2dfddc6bcb?q=80&w=880&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D\" alt=\"A todo list\"/>");

	int matches = 0;
	String aImageUrl = null;
	while (aMatcher.find()) {
	    aImageUrl = aMatcher.group(1);
	    matches++;
	}

	assertEquals(1, matches);
	assertEquals(
		"https://plus.unsplash.com/premium_photo-1681487870238-4a2dfddc6bcb?q=80&w=880&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
		aImageUrl);
    }

    @Test
    public void testStringTransformer() {
	StringTransformer aRemoveInvalidImageLinks = ImageChecker.removeInvalidImageLinks;

	String aTransformedHtml = aRemoveInvalidImageLinks.transform(htmlWithImage);

	assertNotEquals(aTransformedHtml, htmlWithImage);
    }
}
