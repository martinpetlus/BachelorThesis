class CreateLtrRankings < ActiveRecord::Migration
  def change
    create_table :ltr_rankings do |t|
      t.integer       :search_log_id, :null => false, :unique => true
      t.integer_array :original_ranking, :null => false
      t.integer_array :ltr_ranking, :null => false
      t.timestamps
    end
    add_index :ltr_rankings, :search_log_id
  end
end
