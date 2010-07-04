package org.smerty.zooborns.data;

import java.io.StringWriter;
import java.util.ArrayList;

import org.smerty.zooborns.feed.FeedFetcher;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

public class ZooBornsGallery {
	
	public ArrayList<ZooBornsEntry> entries;
	//public Date fetchedAt;

	public ZooBornsGallery() {
		super();
		entries = new ArrayList<ZooBornsEntry>();
	}
	
	public boolean update() {
		
		FeedFetcher fFetcher = new FeedFetcher();
        
        fFetcher.pull();
        
        Document doc = fFetcher.getDoc();
        
		NodeList itemNodes = doc.getElementsByTagName("entry");

		for (int i = 0; i < itemNodes.getLength(); i++) {
			Node itemNode = itemNodes.item(i);
		    
		    if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
		  
		      Element fstElmnt = (Element) itemNode;
		      
		      Log.d("ZooBornsGallery", "Content : " + fstElmnt.toString());
		      
		      NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("title");
		      Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
		      NodeList fstNm = fstNmElmnt.getChildNodes();
		      
		      String title =  ((Node) fstNm.item(0)).getNodeValue();
		      
				if (title != null && title.length() > 0) {
					Log.d("ZooBornsGallery", "Entry Title : " + title);

					ZooBornsEntry zE = new ZooBornsEntry(null, title, null);
					
					NodeList imgNodes = fstElmnt.getElementsByTagName("img");

					Log.d("ZooBornsGallery", "img tag count : " + imgNodes.getLength());
					
					for (int n = 0; n < imgNodes.getLength(); n++) {
						Node imgNode = imgNodes.item(n);

						if (imgNode.getNodeType() == Node.ELEMENT_NODE) {

							Element imgElmnt = (Element) imgNode;
							imgNode.getAttributes();
							
							String test = imgElmnt.getAttribute("src");
							if (test != null && test.length() > 0) {
								Log.d("ZooBornsGallery", "Img Src : " + test);
								zE.addPhoto(new ZooBornsPhoto(test));
							}
						}
					}

					
					addEntry(zE);
		      }
		      
		    }
		}

		
		
		return true;
	}
	
	public boolean addEntry(ZooBornsEntry entry) {
		return entries.add(entry);
	}
	
	
}
