package de.l3s.boilerpipe.sax;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
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
	 * Encodes <img > tags as ??img??<attributes>??/img??
	 */
	public void encodeImageTagsAsText()
	{
		String htmlDataString = new String(this.data);
		htmlDataString = HTMLDocument.encodeImageTagsAsText(htmlDataString, this.charset.name());
		final byte[] htmlData = htmlDataString.getBytes();
		this.data = htmlData;
	}
	
	/*
	 * Encodes <img > tags as ??img??<attributes>??/img??
	 */
	public static String encodeImageTagsAsText(String htmlDataString, String encoding)
	{
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
				String encodedImageTag = "??img??" + imageAttributes + "??/img??";
				System.out.println("encodedImageTag: " + encodedImageTag);
				htmlDataString = matcher.replaceFirst(encodedImageTag);
			}
		}
			
		return htmlDataString;
	}
	
	/*
	 * Decodes ??img??<attributes>??/img?? as <img > tags
	 */
	public void restoreTextEncodedImageTags()
	{
		String htmlDataString = new String(this.data);
		htmlDataString = HTMLDocument.restoreTextEncodedImageTags(htmlDataString, this.charset.name());
		final byte[] htmlData = htmlDataString.getBytes();
		this.data = htmlData;
	}
	
	/*
	 * Decodes ??img??<attributes>??/img?? as <img > tags
	 */
	public static String restoreTextEncodedImageTags(String htmlDataString, String encoding)
	{
		
		Pattern PAT_IMAGE_TAG = Pattern.compile("\\?\\?img\\?\\?(.*?)\\?\\?/img\\?\\?");
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
				String encodedImageTag = "<img " + imageAttributes + ">";
				System.out.println("encodedImageTag: " + encodedImageTag);
				htmlDataString = matcher.replaceFirst(encodedImageTag);
			}
		}
		
		return htmlDataString;
	}
}
