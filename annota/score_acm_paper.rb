require 'cosine_similarity'
require 'fast-stemmer'
require 'matrix'

module Ltr
  class ScoreAcmPaper
    def initialize(query, user_model)
      @query_terms = query.strip.downcase.split(/[\s\W]+/).reject { |term| term.length < 4 }
      @query_terms.map! { |term| term.stem }
      @user_model = user_model
      @feature_cache = {}
      LtrFeature.all.each do |feature|
        @feature_cache[feature.author_or_publisher_or_institution] = feature.index
      end
    end

    def score(acm_paper)
      features = []

      features << CosineSimilarity.calculate(@query_terms, acm_paper.title)
      features << CosineSimilarity.calculate(@query_terms, acm_paper.abstract)

      if !acm_paper.keywords.nil?
        features << CosineSimilarity.calculate(@query_terms, acm_paper.keywords.join(" "))
      else
        features << 0.0
      end

      if !acm_paper.annota_top_tags.nil?
        features << CosineSimilarity.calculate(@query_terms, acm_paper.annota_top_tags.join(" "))
      else
        features << 0.0
      end

      features << Math.log(acm_paper.downloads_count + 1)
      features << Math.log(acm_paper.citations_count + 1)
      features << Math.log(acm_paper.number_of_pages + 1)
      features << Math.log(acm_paper.annota_bookmark_count + 1)
      features << Math.log(100.0 / acm_paper.acceptance_rate)

      features << (acm_paper.year >= 2011 ? 1.0 : 0.0)
      features << (acm_paper.year >= 2004 ? 1.0 : 0.0)
      features << (acm_paper.year >= 1999 ? 1.0 : 0.0)
      features << (acm_paper.year >= 1980 ? 1.0 : 0.0)

      ary = Array.new(@feature_cache.size) { |idx| 0.0 }

      acm_paper.authors.each do |author|
        ary[@feature_cache[author]] = 1.0 if @feature_cache[author]
      end unless acm_paper.authors.nil?

      acm_paper.institutions.each do |institution|
        ary[@feature_cache[institution]] = 1.0 if @feature_cache[institution]
      end unless acm_paper.institutions.nil?

      feature_index = @feature_cache[acm_paper.publisher] unless acm_paper.publisher.nil?
      ary[feature_index] = 1.0 if feature_index

      features.concat(ary)

      weights = @user_model.user_weight_vector

      if @user_model.user_mean_vector && @user_model.user_sigma_vector
        mean = @user_model.user_mean_vector
        sigma = @user_model.user_sigma_vector

        features = features.each_with_index.map { |el, idx|
          (el - mean[idx]) / sigma[idx]
        }
      end

      # If bias term is added
      features.unshift(1.0) if weights.size == features.size + 1

      (Matrix.row_vector(weights) * Matrix.column_vector(features)).element(0,0)
    end
  end
end