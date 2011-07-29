package com.gbayer.boilerpipe.demo;

import java.io.PrintWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.demo.Oneliner;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.HTMLHighlighter;

/**
 * Demonstrates how to use this slightly modified version of Boilerpipe to get the main content as HTML, with images included.
 * 
 * @author Greg Bayer <greg@gbayer.com>
 * Based on HTMLHighlightDemo by Christian Kohlschütter
 * @see Oneliner if you only need the plain text.
 */
public class ExtractHTMLWithImagesDemo {
	public static void main(String[] args) throws Exception {
		URL url = new URL(
//				"http://research.microsoft.com/en-us/um/people/ryenw/hcir2010/challenge.html"
//				"http://boilerpipe-web.appspot.com/"
//				"http://mashable.com/2011/07/19/bill-gates-reinvent-toilet/"
//				"http://techcrunch.com/2011/07/21/do-authors-dream-of-electric-book-signings-kindlegraph-hopes-so/"
//				"file:///Users/gbayer/Desktop/livecount.html"
				"file:///Users/gbayer/Desktop/techcrunch_story.html"
		        );
		
		// choose from a set of useful BoilerpipeExtractors...
		final BoilerpipeExtractor extractor = CommonExtractors.ARTICLE_EXTRACTOR;
//		final BoilerpipeExtractor extractor = CommonExtractors.DEFAULT_EXTRACTOR;
//		final BoilerpipeExtractor extractor = CommonExtractors.CANOLA_EXTRACTOR;
//		final BoilerpipeExtractor extractor = CommonExtractors.LARGEST_CONTENT_EXTRACTOR;

		// choose the operation mode (i.e., highlighting or extraction)
//		final HTMLHighlighter hh = HTMLHighlighter.newHighlightingInstance();
		
		// This new flag is all that is required to include images in extracted HTML
		final boolean includeImages = true;
		final boolean bodyOnly = true;
		final HTMLHighlighter hh = HTMLHighlighter.newExtractingInstance(includeImages, bodyOnly);
		
		PrintWriter out = new PrintWriter("/tmp/highlighted.html", "UTF-8");
		out.println("<base href=\"" + url + "\" >");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text-html; charset=utf-8\" />");
		
		String extractedHtml = hh.process(url, extractor);
		out.println(extractedHtml);

		out.close();
		
		System.out.println("Now open file:///tmp/highlighted.html in your web browser");
	}
}
