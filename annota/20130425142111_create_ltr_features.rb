class CreateLtrFeatures < ActiveRecord::Migration
  def change
    create_table :ltr_features do |t|
      t.string    :author_or_publisher_or_institution, :unique => true, :null => false
      t.integer   :index, :unique => true, :null => false
      t.timestamps
    end
    add_index :ltr_features, :author_or_publisher_or_institution
  end
end
