require 'ltr/learning_to_rank'

class SearchController < ApplicationController
  load_and_authorize_resource :user_has_bookmark

  def index
    @search = Search.multi_search({:query => params[:query], :page => params[:page]})
  end

  def people
  end

  def groups
  end

  def documents
    @query = params[:query]
    tags = params[:tags] ? params[:tags].split(",") : []
    @search = Document.search(params)

    user_model = LtrModel.find_by_user_id(current_user.id)

    if user_model && user_model.ltr_enabled && @query.present?
      ltr_search = Document.search(params.merge(:display => 100, :page => 1))
      all_documents = ltr_search.results
      all_documents_ids = all_documents.map(&:id)

      acm_papers_ranking = Ltr::LearningToRank.only_acm_papers(all_documents)
      acm_papers_ranking_ids = acm_papers_ranking.map(&:id)

      ltr_ranking_ids = Ltr::LearningToRank.rank(user_model, @query, acm_papers_ranking)
      acm_papers_ranking_ids &= ltr_ranking_ids # vylucenie tych acm clankov, ktore nemame stiahnute
      result_ranking_ids = Ltr::LearningToRank.interleave(ltr_ranking_ids, acm_papers_ranking_ids)

      page = (params[:page] || 1).to_i
      display = (params[:display] || 10).to_i

      not_acm_papers = all_documents_ids - result_ranking_ids
      result_ranking_ids = result_ranking_ids.concat(not_acm_papers)
      result_ranking_ids = result_ranking_ids.slice((page == 0 || page == 1) ? 0 : (page-1) * display, display)
      result_ranking_ids ||= []

      @documents = result_ranking_ids.map { |document_id| Document.find_by_id(document_id) }
      @tags = ltr_search.facets["top_tags"]["terms"]
      @search_log = SearchLogger.log_results(current_user.id, params[:query], tags, @documents)
      SearchLogger.log_combined_ranking(@search_log.id, acm_papers_ranking_ids, ltr_ranking_ids)
    else
      @documents = @search.results
      @search_log = SearchLogger.log_results(current_user.id, params[:query], tags, @documents)
      @tags = @search.facets["top_tags"]["terms"]
    end

    bookmarks = UserHasBookmark.where(:document_id => @documents.map(&:id), :user_id => current_user.id)
    @bookmarks = {}
    bookmarks.each do |bookmark|
      @bookmarks[bookmark.document_id] = bookmark
    end

    @user_shares_documents = {}
    @tags ||= []
    @tags = @tags.select{|tag| !tags.include? tag[:term]}

    @tab = { id: :tab1, partial: 'search/documents_intab' }
  end

  def favorite_document
    @document = Document.find(params[:document_id])
    @bookmark = UserHasBookmark.where(:document_id => params[:document_id], :user_id => current_user.id).first
    if @bookmark.nil?
      @bookmark = UserHasBookmark.new(:favorite => params[:favorite], :document_id => @document.id)
      render 'search/bookmark_document'
    else
      @bookmark.favorite = params[:favorite]
      @bookmark.save
      render 'search/repaint_document'
    end
  end

  def bookmark_document
    @document = Document.find(params[:document_id])
    @bookmark = UserHasBookmark.where(:document_id => params[:document_id], :user_id => current_user.id).first
    if @bookmark
      if params[:read_later].present?
        @bookmark.read_later = params[:read_later]
        @bookmark.save
        render 'search/repaint_document'
      end
    else
      @bookmark = UserHasBookmark.new(:read_later => params[:read_later], :document_id => @document.id)
      render 'bookmark_document'
    end
  end

  def create_bookmark
    @document = Document.find(params[:document_id])
    @bookmark = UserHasBookmark.find_or_initialize_by_document_id_and_user_id(@document.id, current_user.id)
    authorize! :create, @bookmark

    @document.title ||= params[:title]
    @document.save!

    @bookmark.note = params[:note] if params[:note].present?
    @bookmark.shared_note = params[:shared_note] if params[:shared_note].present?
    @bookmark.read_later = params[:read_later] == "true"
    @bookmark.public = params[:public] == "true"
    @bookmark.favorite = params[:favorite] == "true"
    @bookmark.title = params[:title]

    @bookmark.update_last_visit
    @bookmark.save!

    @new_tags = (params[:tag_string] || "").split(",")
    @new_tags.each do |tag_name|
      tag = Tag.find_or_create_by_name(tag_name)
      TagRelation.find_or_create_by_tag_id_and_document_id_and_user_id(tag.id,@document.id,current_user.id)
    end
    render 'search/repaint_document'
  end

  def edit_share_document
    @document = Document.find(params[:document_id])
    present_groups = Group.with_user_and_document(current_user, @document)
    @present_groups = {}
    present_groups.each {|group| @present_groups[group.id] = true}
    @groups = current_user.groups
  end

  def share_document
    @document = Document.find(params[:document_id])
    @bookmark = UserHasBookmark.where(:user_id => current_user.id, :document_id => @document.id).first
    @new_shares = params[:shares] || {}
    @new_shares.keys.each do |group_id|
      @share = UserSharesDocument.create(:document_id => @bookmark.document_id, :user_id => current_user.id, :group_id => group_id, :title => @bookmark.title || @document.title)

      @article_detail = @bookmark.find_or_initialize_article_detail
      if @article_detail and !ArticleDetail.where(:document_id => @share.document_id, :group_id => @share.group_id).exists? # ak neexistuje detail clanku k zalozke, tak skopiruj zo zdielanej zalozky
        new_detail = @article_detail.dup
        new_detail.document_id = @share.document_id
        new_detail.group_id = @share.group_id
        new_detail.user_id = nil
        new_detail.save
      end
    end

    respond_to do |format|
      format.js
      format.html { redirect_to search_documents_path  }
    end
  end
end
