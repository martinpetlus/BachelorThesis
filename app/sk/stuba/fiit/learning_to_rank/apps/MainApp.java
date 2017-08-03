package sk.stuba.fiit.learning_to_rank.apps;

import java.util.ArrayList;
import java.util.HashMap;

import sk.stuba.fiit.learning_to_rank.db.AcmPapersManager;
import sk.stuba.fiit.learning_to_rank.db.AnnotaManager;
import sk.stuba.fiit.learning_to_rank.documents.AcmPaper;
import sk.stuba.fiit.learning_to_rank.documents.AnnotaDocument;
import sk.stuba.fiit.learning_to_rank.downloading.AcmPapersDownloadManager;
import sk.stuba.fiit.learning_to_rank.downloading.DownloadManager;
import sk.stuba.fiit.learning_to_rank.downloading.IDownloadDocumentManager;
import sk.stuba.fiit.learning_to_rank.features.AuthorsInstitutionsPublishers;
import sk.stuba.fiit.learning_to_rank.learning.LearnModelForUser;
import sk.stuba.fiit.learning_to_rank.learning.Model;
import sk.stuba.fiit.learning_to_rank.pairs.SearchLog;
import sk.stuba.fiit.learning_to_rank.util.AppProperties;
import sk.stuba.fiit.learning_to_rank.util.Timer;


public class MainApp {

	public static void main(String[] args) {
		AnnotaManager annota = new AnnotaManager();
		AcmPapersManager papersManager = new AcmPapersManager();
		Timer timer = new Timer();

		DownloadManager downloadManager = new DownloadManager(AppProperties.numberOfDownloaders);
		IDownloadDocumentManager m = new AcmPapersDownloadManager(annota.getAllAcmPapersUrlsToDownload());
		
		if (AppProperties.httpProxyHost != null && AppProperties.httpProxyPort != null) {
			System.setProperty("http.proxyHost", AppProperties.httpProxyHost);
			System.setProperty("http.proxyPort", AppProperties.httpProxyPort);
			System.out.println("Set http proxy host: " + AppProperties.httpProxyHost);
			System.out.println("Set http proxy port: " + AppProperties.httpProxyPort);
		}
		
		timer.start();
		System.out.println("Downloading and parsing documents... ");
		downloadManager.downloadDocuments(m);
		
		while (!downloadManager.isDownloadComplete()) {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
				e.printStackTrace(System.err);
				System.exit(1);
			}
		}
		
		m.done();
		timer.end();
		
		System.out.println("Elapsed time: " + timer.getDiffInSeconds() + " seconds\n");
		
		timer.start();
		System.out.print("Updating Annota data on Acm papers... ");
		ArrayList<AnnotaDocument> annotaAcmPapers = annota.getListOfAcmPapers();
		papersManager.updateAnnotaData(annotaAcmPapers);
		timer.end();
		System.out.println(timer.getDiffInSeconds() + " seconds");
		
		timer.start();
		System.out.print("Saving vector of authors, institutions and publishers... ");
		AuthorsInstitutionsPublishers.save(annota);
		timer.end();
		System.out.println(timer.getDiffInSeconds() + " seconds");
		
		HashMap<Integer, AcmPaper> acmPapersByAnnotaId = new HashMap<Integer, AcmPaper>();
		
		ArrayList<Integer> userIds = annota.getAllUserIdsFromSearchLogs();
		LearnModelForUser userModel = new LearnModelForUser(papersManager, acmPapersByAnnotaId);
		userModel.setNotClickedAcmPapersIds(annota.getAllNotClickedIdsOfAcmPapersFromSearchLogs());
		
		for (int userId : userIds) {
			System.out.println("Learning model for user with id: " + userId + "...");
			ArrayList<SearchLog> userSearches = annota.getSearchLogsOnlyAcmPapers(userId);
			Model model = userModel.learn(userSearches);
			if (model == null) {
				System.out.println("No model learned for user with id = " + userId 
						+ " beacause of lack of user clicks...");
			}
			else {
				if (!annota.saveUserModel(userId, model)) {
					System.err.println("Error: error saving model for user with id = " + userId + "...");
				}
				else {
					System.out.println("Learned model for user with id = " + userId);
				}
			}
		}
		
		papersManager.close();
		annota.close();
	}
}
