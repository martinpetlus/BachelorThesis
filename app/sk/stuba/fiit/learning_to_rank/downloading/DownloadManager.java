package sk.stuba.fiit.learning_to_rank.downloading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DownloadManager {
	
	private ExecutorService executorService;
	private int numberOfDownloaders;
	
	public DownloadManager(int numberOfDownloaders) {	
		this.executorService = Executors.newFixedThreadPool(numberOfDownloaders);
		this.numberOfDownloaders = numberOfDownloaders;
	}
	
	public void downloadDocuments(IDownloadDocumentManager m) {	
		for (int i = 0; i < numberOfDownloaders; i++) {
			executorService.execute(new DocumentDownloader(m));
		}
		
		executorService.shutdown();
	}
	
	public boolean isDownloadComplete() {
		return executorService.isTerminated();
	}
}
