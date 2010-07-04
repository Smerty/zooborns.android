package org.smerty.zooborns.feed;

import java.io.DataInput;
import java.io.IOException;
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

import android.content.ComponentName;
import android.content.pm.PackageInfo;
import android.widget.Toast;

public class FeedFetcher {

	private Document rssDoc;
	
	public Document getDoc() {
		return rssDoc;
	}

	public boolean pull() {

		try {

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, "UTF-8");
			HttpProtocolParams.setUseExpectContinue(params, true);
			HttpProtocolParams.setHttpElementCharset(params, "UTF-8");

			String agent = "ZooBorns ";

			/*
			try {
				ComponentName compName = new ComponentName(this,FeedFetcher.class);
				PackageInfo pkgInfo = this.getPackageManager().getPackageInfo(compName.getPackageName(), 0);
				agent += "(v" + pkgInfo.versionName + "-" + pkgInfo.versionCode	+ ") ";
			} catch (android.content.pm.PackageManager.NameNotFoundException e) {
				agent += "(version unknown) ";
			}
			*/

			agent += "for android";

			agent = "MSIE 8.0";

			HttpProtocolParams.setUserAgent(params, agent);

			DefaultHttpClient client = new DefaultHttpClient(params);

			InputStream dataInput = null;

			try {
				HttpGet method = new HttpGet(
						"http://www.zooborns.com/zooborns/rss.xml");
				HttpResponse res = client.execute(method);
				dataInput = res.getEntity().getContent();
			} catch (IOException e) {
				e.printStackTrace();
				// Toast.makeText(getBaseContext(), "Network Failure...",
				// Toast.LENGTH_SHORT).show();
				return false;
			}

			rssDoc = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;

			try {
				db = dbf.newDocumentBuilder();
				rssDoc = db.parse(dataInput);
				dataInput.close();
				// finish();
			} catch (SAXParseException e) {
				e.printStackTrace();
				// Toast.makeText(getBaseContext(),
				// "SAXParseException, bad XML?",
				// Toast.LENGTH_SHORT).show();
				return false;
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				// Toast.makeText(getBaseContext(), "SAXException",
				// Toast.LENGTH_SHORT).show();
				e.printStackTrace();
				return false;
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				// Toast.makeText(getBaseContext(),
				// "ParserConfigurationException", Toast.LENGTH_SHORT)
				// .show();
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			rssDoc.getDocumentElement().normalize();
		}

		finally {
			
		}
		return true;
	}
}
