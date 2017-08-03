package sk.stuba.fiit.learning_to_rank.downloading;

import sk.stuba.fiit.learning_to_rank.documents.Document;
import sk.stuba.fiit.learning_to_rank.parsing.Parser;


public interface IDownloadDocumentManager {
	
	public abstract void saveParsedDocument(Document d);
	public abstract String getUrlOfDocumentToDownload();
	public abstract String preProcessUrl(String url);
	public abstract Parser getParser();
	public abstract void done();
}
