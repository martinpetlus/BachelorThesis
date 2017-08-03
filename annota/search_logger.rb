class SearchLogger
  def self.log_results(user_id, query, tags, results)
    continued_log = SearchLog.continued(user_id, query, tags)
    if continued_log
      continued_log.merge_results(results)
      continued_log.save
      continued_log
    else
      SearchLog.create!(:user_id => user_id, :query => query, :tags => tags, :ranked_results_ids => results.map(&:id))
    end
  end

  def self.log_combined_ranking(search_log_id, original_ranking, ltr_ranking)
    LtrRankings.create!(:search_log_id => search_log_id, :original_ranking => original_ranking,
                        :ltr_ranking => ltr_ranking) unless LtrRankings.exists?(:search_log_id => search_log_id)
  end

  def self.log_result_click(user_id, search_log_id, document)
    log = SearchLog.where(:id => search_log_id).first
    return unless log
    log.clicked_ids ||= []
    log.clicked_ids << document.id
    log.save
  end
end