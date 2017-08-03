class SearchLog < ActiveRecord::Base
  # attr_accessible :title, :body

  def contains_results? results
    ranked_results_ids.include?(results.first.try(:id))
  end

  def self.continued user_id, query, tags
    log = where(:user_id => user_id).last
    if log && log.query == query && Set.new(log.tags) == Set.new(tags)
      log
    else
      nil
    end
  end

  def merge_results results
    unless contains_results?(results)
      ranked_results_ids << results.map(&:id)
      ranked_results_ids.flatten!
    end
  end
end
