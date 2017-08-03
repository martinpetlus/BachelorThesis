package sk.stuba.fiit.learning_to_rank.downloading;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import sk.stuba.fiit.learning_to_rank.parsing.Parser;


public class DocumentDownloader implements Runnable {
	
	private final IDownloadDocumentManager m;
	private final Parser parser;
	
	public DocumentDownloader(IDownloadDocumentManager m) {
		this.parser = m.getParser();
		this.m = m;
	}

	@Override
	public void run() {
		String url;
		
		while ((url = m.getUrlOfDocumentToDownload()) != null) {
			try {
				Connection conn = Jsoup.connect(m.preProcessUrl(url));
				
				conn.timeout(20000);
				conn.userAgent("Mozilla");
				conn.followRedirects(true);
				
				// Download document
				Document doc = conn.get();
				
				sk.stuba.fiit.learning_to_rank.documents.Document parsedDocument = parser.parse(doc.html());
				parsedDocument.setUrl(url);
				
				m.saveParsedDocument(parsedDocument);
				
				System.out.println("Downloaded and parsed document: " + url);
			} 
			catch (IOException e) {
				System.err.println(url + " : " + e.getMessage());
			}
		}
	}
}
