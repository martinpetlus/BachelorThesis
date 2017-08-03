class CreateSearchLogs < ActiveRecord::Migration
      def change
        create_table :search_logs do |t|
          t.integer      :user_id, null: false
          t.string       :query
          t.string_array :ranked_results_ids, null: false
          t.string_array :clicked_ids
          t.timestamps
    end
    add_index :search_logs, :user_id
    add_index :search_logs, :query
    add_index :search_logs, :created_at
  end
end
