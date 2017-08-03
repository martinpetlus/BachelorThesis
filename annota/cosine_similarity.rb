require 'matrix'

module Ltr
  class CosineSimilarity
    def self.calculate(terms, text)
      if terms && terms.present? && text
        text = text.strip

        if text.empty?
          return 0.0
        end

        words = text.split(/[\s\W]+/)

        union = terms | words

        one_occ_vec = []
        two_occ_vec = []

        union.each do |el|
          one_occ_vec << terms.count(el)
          two_occ_vec << words.count(el)
        end

        dot_prod = (Matrix.row_vector(one_occ_vec) * Matrix.column_vector(two_occ_vec)).element(0,0)

        one_mag = Vector.elements(one_occ_vec, :copy => false).magnitude
        two_mag = Vector.elements(two_occ_vec, :copy => false).magnitude

        return dot_prod / (one_mag * two_mag)
      else
        return 0.0
      end
    end
  end
end