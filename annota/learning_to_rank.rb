require 'score_acm_paper'

module Ltr
  class LearningToRank
    def self.only_acm_papers(documents)
      documents.select { |doc| doc.url =~ /https?:\/\/dl\.acm\.org\/citation\.cfm\?id=/ }
    end

    def self.rank(user_model, query, documents)
      @scorer = ScoreAcmPaper.new(query, user_model)
      acm_papers = {}
      LtrAcmPaper.find_all_by_annota_id(documents.collect(&:id)).each do |paper|
        acm_papers[paper.annota_id] = paper
      end

      scores = documents.map do |document|
        acm_paper = acm_papers[document.id]
        next if acm_paper.nil?
        [@scorer.score(acm_paper), document.id]
      end

      scores.reject! { |el| el == nil }
      scores.sort! { |a, b| b.first <=> a.first }
      scores.map! { |el| el.second }
    end

    def self.interleave(ranking_a, ranking_b)
      result_ranking = []
      if Random.rand() <= 0.5
        self.combine_rankings(ranking_a, ranking_b, -1, -1, result_ranking, [ranking_a.size, ranking_b.size].min)
      else
        self.combine_rankings(ranking_b, ranking_a, -1, -1, result_ranking, [ranking_a.size, ranking_b.size].min)
      end
      result_ranking
    end

    private

    def self.combine_rankings(ranking_a, ranking_b, k_a, k_b, result_ranking, size)
      if (k_a == k_b)
        if (k_a + 1 < size)
          if (!result_ranking.include?(ranking_a[k_a + 1]))
            result_ranking << ranking_a[k_a + 1]
          end
          self.combine_rankings(ranking_a, ranking_b, k_a + 1, k_b, result_ranking, size)
        end
      else
        if (k_b + 1 < size)
          if (!result_ranking.include?(ranking_b[k_b + 1]))
            result_ranking << ranking_b[k_b + 1]
          end
          self.combine_rankings(ranking_a, ranking_b, k_a, k_b + 1, result_ranking, size)
        end
      end
    end
  end
end