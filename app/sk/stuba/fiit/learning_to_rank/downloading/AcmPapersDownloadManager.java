package sk.stuba.fiit.learning_to_rank.downloading;

import java.util.ArrayList;

import sk.stuba.fiit.learning_to_rank.db.AcmPapersManager;
import sk.stuba.fiit.learning_to_rank.documents.AcmPaper;
import sk.stuba.fiit.learning_to_rank.documents.Document;
import sk.stuba.fiit.learning_to_rank.parsing.AcmPaperParser;
import sk.stuba.fiit.learning_to_rank.parsing.Parser;


public class AcmPapersDownloadManager extends AcmPapersManager implements IDownloadDocumentManager {

	private ArrayList<String> papersToDownload;
	private int index;
	
	public AcmPapersDownloadManager(ArrayList<String> papersToDownload) {
		this.papersToDownload = papersToDownload;
		this.index = 0;
	}
	
	@Override
	public synchronized String getUrlOfDocumentToDownload() {
		if (index < papersToDownload.size()) {
			return papersToDownload.get(index++);
		}
		return null;
	}

	@Override
	public synchronized void saveParsedDocument(Document doc) {
		super.saveAcmPaper((AcmPaper) doc);
	}
	
	@Override
	public synchronized void done() {
		super.close();
	}

	@Override
	public Parser getParser() {
		return new AcmPaperParser();
	}

	@Override
	public String preProcessUrl(String url) {
		if (url.indexOf('#') > 0) {
			url = url.substring(0, url.indexOf('#'));
		}
		
		if (!url.contains("preflayout=flat")) {
			url += "&preflayout=flat";
		}
		
		return url;
	}
}
