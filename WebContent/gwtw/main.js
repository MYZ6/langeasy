$(function() {
	reloadData();

	initEvent();
});

function initEvent() {
	$('#btn-pass').click(doPass);
	$('#btn-unknown').click(unknown);
	$('#btn-refresh').click(function() {
		reloadData();
	});
}

function doPass(evt) {
	var idArr = [];
	$(':checked').each(function(i, ele) {
		// console.log($(this).val());
		idArr.push($(this).val());
	});
	$.ajax({
		type : "GET",
		url : root + '/gwtw?t=pass',
		data : {
			"wordids" : idArr.join()
		},
		error : function() {
			console.error("query failed");
		},
		success : function(data) {
			reloadData();
		}
	});
}

function unknown(evt) {
	var idArr = [];
	$(':checked').each(function(i, ele) {
		idArr.push($(this).val());
	});
	$.ajax({
		type : "GET",
		url : root + '/gwtw?t=unknown',
		data : {
			"wordids" : idArr.join()
		},
		error : function() {
			console.error("query failed");
		},
		success : function(data) {
			reloadData();
		}
	});
}

function reloadData() {
	$('.word-list').empty();
	if (type == '') {
		type = 'list';
	}
	$.ajax({
		type : "GET",
		url : root + '/gwtw?t=' + type,
		data : {},
		async : false,
		error : function() {
			console.error("query failed");
		},
		success : function(data) {
			$('.total').html(data.total);
			$(data.rows).each(function(i, word) {
				$word = $(wordRender(word, i));
				$word.click(function() {
					var $checkBox = $("input[type=checkbox]", this);
					var checked = $checkBox.prop("checked");
					if (!checked) {
						$(this).addClass('checked');
						translate(word.word);

						var $clone = $(wordCopyRender(word, i));
						$clone.click(function(evt) {
							$clone.remove();
							$orig = $('.orig.word' + word.wordid);
							// console.log($orig);
							$('input', $orig).prop("checked", false);
							$orig.removeClass('checked');
						});
						$('#checked-list').append($clone);
					} else {
						$(this).removeClass('checked');
						$('.copy.word' + word.wordid).remove();
					}
					$checkBox.prop("checked", !checked);

				});
				$('#word-list').append($word);
			});
		}
	});
	function wordRender(word, i) {
		return '<div class="word-item orig word' + word.wordid + '"><input type="checkbox" value="' + word.wordid
				+ '" /><span>' + (i + 1) + '/</span><span>' + word.wordid + '&nbsp;</span>' + word.word + '</div>';
	}
	function wordCopyRender(word, i) {
		return '<div class="word-item copy checked word' + word.wordid + '"><span>' + (i + 1) + '/</span><span>'
				+ word.wordid + '&nbsp;</span>' + word.word + '</div>';
	}
}

function translate(word) {
	$.ajax({
		type : "GET",
		url : root + '/gwtw?t=translate',
		data : {
			"word" : word
		},
		error : function() {
			console.error("query failed");
		},
		success : function(data) {
			console.log($('#translate'));
			$('#translate').empty().html(data);
		}
	});
}
