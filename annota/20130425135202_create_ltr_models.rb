class CreateLtrModels < ActiveRecord::Migration
  def change
    create_table :ltr_models do |t|
      t.integer     :user_id, :unique => true, :null => false
      t.boolean     :ltr_enabled, :null => false, :default => false
      t.float_array :user_weight_vector, :null => false
      t.float_array :user_mean_vector
      t.float_array :user_sigma_vector
      t.timestamps
    end
    add_index :ltr_models, :user_id
  end
end
