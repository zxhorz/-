require(['jquery',
         'global',
         'module/util',
         'module/datatables',
         'module/ajax',
         'bootstrap',
         'requirejs/domready!'],
function($, global, util, datatables){
	var productTable = null;

    var returnLink = function(data,type,full){
        var href = global.context+'/web/product/'+full.id;
        return "<a href='"+href+"'>"+full.id+"</a>";
    };

    var returnDelete = function(data,type,full){
        return '<a class="btn btn-danger btn-remove" data-id="'+full.id+'"><i class="fa fa-remove"></i>Delete</a>';
    };

	var _rendarTable = function(){
		var options = {};
		options.tableId = '#main-table';
		options.aaSorting = [[1,"asc"]];
		options.sAjaxSource = global.context+"/web/products";
        options.functionList = {"returnLink":returnLink,"returnDelete":returnDelete};
		productTable = datatables.init(options);
		productTable.setParams(util.getSearchData("#search-area"));
		return productTable.create();
	};

	var bindEvent = function(){
		$("#product-search-btn").on("click",function(){
			productTable.setParams(util.getSearchData('#search-area'));
			productTable.invoke("fnDraw");
		});
        $("body").on("click",".btn-remove",function(){
            var id = $(this).attr("data-id");
            $.ajax({
                url:global.context+"/web/product/"+id,
                type: 'DELETE',
                contentType: 'application/json;charset=utf-8',
                success:function(){
                    productTable.invoke("fnDraw");
                }
            });
        });

		$("#search-area").on("keydown",function(e){
			if(e.which == 13){
				$("#product-search-btn").trigger("click");
				return false;
			}
		});
	};
	
	var init = function(){
		_rendarTable();
		bindEvent();
	};
	
	init();
});