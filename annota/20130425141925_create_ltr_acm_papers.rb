class CreateLtrAcmPapers < ActiveRecord::Migration
  def change
    create_table :ltr_acm_papers do |t|
      t.text          :url, :null => false
      t.text          :title
      t.text          :abstract
      t.string_array  :keywords
      t.integer       :downloads_count, :null => false, :default => 0
      t.integer       :citations_count, :null => false, :default => 0
      t.integer       :acceptance_rate, :null => false, :default => 100
      t.integer       :number_of_pages, :null => false, :default => 0
      t.integer       :year, :null => false, :default => 0
      t.string_array  :authors
      t.string_array  :institutions
      t.string        :publisher
      t.integer       :annota_id
      t.string_array  :annota_top_tags
      t.integer       :annota_bookmark_count, :null => false, :default => 0
      t.timestamps
    end
    add_index :ltr_acm_papers, :url
    add_index :ltr_acm_papers, :annota_id
  end
end
