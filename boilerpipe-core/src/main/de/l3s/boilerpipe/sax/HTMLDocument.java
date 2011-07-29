package de.l3s.boilerpipe.sax;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.InputSource;

/**
 * An {@link InputSourceable} for {@link HTMLFetcher}.
 * 
 * @author Christian Kohlschütter
 */
public class HTMLDocument implements InputSourceable {
	private Charset charset;
	private byte[] data;

	public HTMLDocument(final byte[] data, final Charset charset) {
		this.data = data;
		this.charset = charset;
	}
	
	public HTMLDocument(final String data) {
		Charset cs = Charset.forName("utf-8");
		this.data = data.getBytes(cs);
		this.charset = cs;
	}
	
	public Charset getCharset() {
		return charset;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public InputSource toInputSource() {
		final InputSource is = new InputSource(new ByteArrayInputStream(data));
		is.setEncoding(charset.name());

		return is;
	}
	
	/*
	 * Encodes <img > tags as #img#<attributes>#/img#
	 */
	public void encodeImageTagsAsText()
	{
		String htmlDataString = new String(this.data);
		htmlDataString = HTMLDocument.encodeImageTagsAsText(htmlDataString, this.charset.name());
		final byte[] htmlData = htmlDataString.getBytes();
		this.data = htmlData;
	}
	
	/*
	 * Encodes <img > tags as #img#<attributes>#/img#
	 */
	public static String encodeImageTagsAsText(String htmlDataString, String encoding)
	{
		ArrayList<String> images = new ArrayList<String>();
		
		Pattern PAT_IMAGE_TAG = Pattern.compile("<img (.*?)[/]?>");
		boolean repeat = true;
		while(repeat) {
			repeat = false;
			Matcher matcher = PAT_IMAGE_TAG.matcher(htmlDataString);
			if(matcher.find()) {
				repeat = true;
				String imageAttributes = matcher.group(1);
				try {
					imageAttributes = URLEncoder.encode(imageAttributes, encoding);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					imageAttributes = URLEncoder.encode(imageAttributes);
				}
				String encodedImageTag = "#img#" + imageAttributes + "#/img#";
				// Ignore duplicate images
				if(!images.contains(encodedImageTag))
				{
					images.add(encodedImageTag);
//					System.out.println("encodedImageTag: " + encodedImageTag);
					htmlDataString = matcher.replaceFirst(encodedImageTag);
				}
				else
				{
//					System.out.println("skipping duplicate encodedImageTag: " + encodedImageTag);
					htmlDataString = matcher.replaceFirst("");
				}
			}
		}

		return htmlDataString;
	}
	
	/*
	 * Decodes #img#<attributes>#/img# as <img > tags
	 */
	public void restoreTextEncodedImageTags()
	{
		String htmlDataString = new String(this.data);
		htmlDataString = HTMLDocument.restoreTextEncodedImageTags(htmlDataString, this.charset.name());
		final byte[] htmlData = htmlDataString.getBytes();
		this.data = htmlData;
	}
	
	/*
	 * Decodes #img#<attributes>#/img# as <img > tags
	 */
	public static String restoreTextEncodedImageTags(String htmlDataString, String encoding)
	{
		
		Pattern PAT_IMAGE_TAG = Pattern.compile("#img#(.*?)#/img#");
		boolean repeat = true;
		while(repeat) {
			repeat = false;
			Matcher matcher = PAT_IMAGE_TAG.matcher(htmlDataString);
			if(matcher.find()) {
				repeat = true;
				String imageAttributes = matcher.group(1);
				try {
					imageAttributes = URLDecoder.decode(imageAttributes, encoding);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					imageAttributes = URLDecoder.decode(imageAttributes);
				}
				String decodedImageTag = "<img " + imageAttributes + ">";
//				System.out.println("decodedImageTag: " + decodedImageTag);
				htmlDataString = matcher.replaceFirst(decodedImageTag);
			}
		}

		return htmlDataString;
	}
	
	/*
	 * Encodes &#xxxx; escaped chars as #esc#xxx#/esc#
	 */
	public void encodeEscapedCharsAsText()
	{
		String htmlDataString = new String(this.data);
		htmlDataString = HTMLDocument.encodeEscapedCharsAsText(htmlDataString, this.charset.name());
		final byte[] htmlData = htmlDataString.getBytes();
		this.data = htmlData;
	}
	
	/*
	 * Encodes &#xxxx; escaped chars as #esc#xxx#/esc#
	 */
	public static String encodeEscapedCharsAsText(String htmlDataString, String encoding)
	{
		// Wrap any escaped chars in cdata
		Pattern PAT_ESC_CHAR = Pattern.compile("&#(.*?);");
		boolean repeat = true;
		while(repeat) {
			repeat = false;
			Matcher matcher = PAT_ESC_CHAR.matcher(htmlDataString);
			if(matcher.find()) {
				repeat = true;
				String escChar = matcher.group(1);
				try {
					escChar = URLEncoder.encode(escChar, encoding);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					escChar = URLEncoder.encode(escChar);
				}
				String encodedEscChar = "#esc#" + escChar + "#/esc#";
//				System.out.println("encodedEscChar: " + encodedEscChar);
				htmlDataString = matcher.replaceFirst(encodedEscChar);
			}
		}
			
		return htmlDataString;
	}
	
	/*
	 * Decodes #esc#xxx#/esc# as &#xxxx; escaped chars
	 */
	public void restoreTextEncodedEscapedChars()
	{
		String htmlDataString = new String(this.data);
		htmlDataString = HTMLDocument.restoreTextEncodedEscapedChars(htmlDataString, this.charset.name());
		final byte[] htmlData = htmlDataString.getBytes();
		this.data = htmlData;
	}
	
	/*
	 * Decodes #esc#xxx#/esc# as &#xxxx; escaped chars
	 */
	public static String restoreTextEncodedEscapedChars(String htmlDataString, String encoding)
	{
		
		// Wrap any escaped chars in cdata
		Pattern PAT_ESC_CHAR = Pattern.compile("#esc#(.*?)#/esc#");
		boolean repeat = true;
		while(repeat) {
			repeat = false;
			Matcher matcher = PAT_ESC_CHAR.matcher(htmlDataString);
			if(matcher.find()) {
				repeat = true;
				String escChar = matcher.group(1);
				try {
					escChar = URLDecoder.decode(escChar, encoding);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					escChar = URLDecoder.decode(escChar);
				}
				String decodedEscChar = "&#" + escChar + ";";
//				System.out.println("decodedEscChar: " + decodedEscChar);
				htmlDataString = matcher.replaceFirst(decodedEscChar);
			}
		}
		
		return htmlDataString;
	}
	
}
