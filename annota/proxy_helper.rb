module ProxyHelper
  def link_to_proxied_result(document, search_log_id)
    link_to document.label, proxied_result_path(sl_id: search_log_id, id: document.id), :target => "_blank", :title => document.label
  end
end
