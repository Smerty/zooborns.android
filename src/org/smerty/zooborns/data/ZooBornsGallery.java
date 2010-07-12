package org.smerty.zooborns.data;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.smerty.zooborns.ZooBorns;
import org.smerty.zooborns.feed.FeedFetcher;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

public class ZooBornsGallery {

	public ArrayList<ZooBornsEntry> entries;
	private ZooBorns that;

	public ZooBornsGallery(ZooBorns that) {
		super();
		entries = new ArrayList<ZooBornsEntry>();
		this.that = that;
	}

	public boolean update() {

		FeedFetcher fFetcher = new FeedFetcher(that);

		fFetcher.pull();

		Document doc = fFetcher.getDoc();

		if (doc == null) {
			Log.d("update", "doc was null");
			return false;
		}

		NodeList itemNodes = doc.getElementsByTagName("entry");

		for (int i = 0; i < itemNodes.getLength(); i++) {
			Node itemNode = itemNodes.item(i);

			if (itemNode.getNodeType() == Node.ELEMENT_NODE) {

				Element fstElmnt = (Element) itemNode;

				NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("title");
				Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
				NodeList fstNm = fstNmElmnt.getChildNodes();

				String title = ((Node) fstNm.item(0)).getNodeValue();

				if (title != null && title.length() > 0) {
					Log.d("ZooBornsGallery", "Entry Title : " + title);

					ZooBornsEntry zE = new ZooBornsEntry(null, title, null);

					NodeList contentNodes = fstElmnt
							.getElementsByTagName("content");

					Log.d("ZooBornsGallery", "content tag count : "
							+ contentNodes.getLength());

					if (contentNodes.getLength() == 1) {
						String entryBody = contentNodes.item(0).getChildNodes()
								.item(0).getNodeValue();

						Pattern pat = null;
						Matcher match = null;
						String url = null;

						pat = Pattern.compile(".*<img[^>]*src=\"([^\"]*)",
								Pattern.CASE_INSENSITIVE);
						match = pat.matcher(entryBody);
						while (match.find()) {
							url = match.group(1);
							if (!url.contains("http://feeds.feedburner.com")) {
								zE.addPhoto(new ZooBornsPhoto(url));
							}
						}

					}

					if (zE.photos.size() > 0) {
						addEntry(zE);
					}
				}

			}
		}

		return true;
	}

	public boolean addEntry(ZooBornsEntry entry) {
		return entries.add(entry);
	}

}
