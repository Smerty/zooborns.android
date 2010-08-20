package org.smerty.zooborns.feed;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class FeedFetcher {

	private Document rssDoc;
	
	public FeedFetcher() {
		super();
	}

	public Document getDoc() {
		return rssDoc;
	}

	public boolean pull() throws Exception {


		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		HttpProtocolParams.setUseExpectContinue(params, true);
		HttpProtocolParams.setHttpElementCharset(params, "UTF-8");

		String agent = "ZooBorns ";
		

		agent += "for android";

		HttpProtocolParams.setUserAgent(params, agent);

		DefaultHttpClient client = new DefaultHttpClient(params);

		InputStream dataInput = null;

		HttpGet method = new HttpGet(
				"http://feeds.feedburner.com/Zooborns");
		HttpResponse res = client.execute(method);
		dataInput = res.getEntity().getContent();

		rssDoc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;

		try {
			db = dbf.newDocumentBuilder();
			rssDoc = db.parse(dataInput);
			dataInput.close();
			
			rssDoc.getDocumentElement().normalize();
		} catch (SAXParseException e) {
			e.printStackTrace();
			throw new FeedParseException();
		} catch (SAXException e) {
			e.printStackTrace();
			throw new FeedParseException();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new FeedParseException();
		}
		return true;
	}
}

