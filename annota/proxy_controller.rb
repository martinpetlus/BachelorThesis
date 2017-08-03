require 'search_logger'

class ProxyController < ApplicationController
  def proxy
    document = Document.find(params[:id])
    SearchLogger.log_result_click(current_user.id, params[:sl_id], document)
    redirect_to document.url
  end
end
