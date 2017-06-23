$(function() {
	initPlayerlist();

	initData();

	$('#passBtn').click(doPass);

	initEvent();
});

function initEvent() {
	$('.repeat-status').click(function(evt) {
		if ($(this).hasClass('checked')) {
			$(this).removeClass('checked');
			repeatStatus = false;
		} else {
			$(this).addClass('checked');
			repeatStatus = true;
		}
	});

	function getSelectionText(e) {
		var text = '';
		if (window.getSelection) {
			text = window.getSelection().toString();
		} else if (document.selection && document.selection.type != "Control") {
			text = document.selection.createRange().text;
		}
		text = text.trim();
		if (text != '') {
			translate(encodeURI(text), e.pageX - 180, e.pageY + 5 + $('#word-content').scrollTop());
		} else {
			$('#translate-panel').hide();
		}
	}

	function singleClick(e) {
		getSelectionText(e);
	}

	function doubleClick(e) {
		// do something, "this" will be the DOM element
		getSelectionText(e);
	}

	$('#word-content').click(function(e) {
		var that = this;
		setTimeout(function() {
			var dblclick = parseInt($(that).data('double'), 10);
			if (dblclick > 0) {
				$(that).data('double', dblclick - 1);
			} else {
				singleClick.call(that, e);
			}
		}, 300);
	}).dblclick(function(e) {
		$(this).data('double', 2);
		doubleClick.call(this, e);
	});
}

function doPass() {
	$.ajax({
		type : "GET",
		url : root + '/api?t=pass',
		data : {
			"wordid" : currentWordId
		},
		error : function() {
			console.error("query failed");
		},
		success : function(data) {
			console.log(data);
		}
	});
}

function favorite(_target, _sentenceId) {
	$.ajax({
		type : "GET",
		url : root + '/api?t=fav',
		data : {
			"sid" : _sentenceId
		},
		error : function() {
			console.error("operate failed");
		},
		success : function(data) {
			$(_target).parent().html('<span style="color: red; border: 1px solid yellow;">Favorited</span>');
			initData(true);
		}
	});
}

function translate(word, x, y) {
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
			$('#translate-panel').empty().html(data).show();
		}
	});
}

function initData(_reload) {
	if (type == '') {
		type = 'list';
	}
	$('#word-list').empty();
	$.ajax({
		type : "GET",
		url : root + '/api?t=' + type,
		async : false,
		error : function() {
			console.error("query failed");
		},
		success : function(data) {
			// console.log(data);
			$(data).each(
				function(i, word) {
					$word = $('<div id="word' + word.wordid + '" class="word-item"><span>' + (i + 1)
						+ '/</span><span>' + word.wordid + '&nbsp;</span>' + word.word + '</div>');
					if (word.favorite == true) {
						$word.append('<span style="color: red; border: 1px solid yellow;">Favorited</span>');
					}
					$word.click(function() {
						// var _url = root + '/word.jsp?id=' +
						// word.wordid;
						// window.open(_url, '_blank');
						showWord(word.wordid);
					});
					$('#word-list').append($word);
				});
			if (_reload == undefined) {
				$('.word-item').eq(0).trigger('click');
			}
		}
	});
}

var currentWordId = null;
function showWord(wordId) {
	currentWordId = wordId;
	$.ajax({
		type : "GET",
		url : root + '/api?t=word&id=' + wordId,
		async : false,
		error : function() {
			console.error("query failed");
		},
		success : function(data) {
			$('#word-title, #pron, #meaning, #audio-example').empty();

			console.log(data);
			$('title').html("" + data.word);
			$('#word-title').html(data.word);
			$('#pron').html(data.pron);
			var pronUrl = root + '/api?t=p&id=' + data.wordid;
			$('#pron').append('<audio controls src="' + pronUrl + '">sdfd</audio>');
			$('#btn-listen').click(function() {
				window.open(data.oggpath);
			});

			$(data.meaning).each(
				function(i, item) {
					$('#meaning').append(
						'<div class="meaning-type">' + item.type + '</div><div class="meaning-content">'
						+ item.meaning + '</div>');
					$(item.example).each(function(i, item) {
						$('#meaning').append('<div class="example">' + item.sentence + '</div>');
					});
				});

			$(data.aexample).each(
				function(i, item) {
					var _url = root + '/api?t=m&id=' + item.sentenceid + "&ts=" + new Date().getTime();
					$sentence = $('<div class="aexample"><div>' + item.booktype
						+ '</div><div class="book-name" bookid="' + item.bookid + '">' + item.bookname
						+ '</div><div style="color: blue;">' + item.coursename + favoriteRender(item)
						+ '</div><div class="audio-item" data-src="' + _url + '"><span>' + (i + 1)
						+ '</span>&nbsp;&nbsp;' + sentenceRender(item.sentence) + '<div>'
						+ item.chinese + '</div></div>');

					// $sentence.append('<audio controls
					// src="' + _url +
					// '">sdfd</audio>');
					// $sentence.click(function() {
					// window.open(_url);
					// });
					$('#audio-example').append($sentence);
				});
			function sentenceRender(sentence) {
				var word = data.word;
				var startIndex = sentence.toLowerCase().indexOf(word);
				var endIndex = startIndex + word.length;
				var result = sentence.substr(0, startIndex) + '<span style="color: red; font-weight: bold;">'
					+ sentence.substr(startIndex, word.length) + '</span>' + sentence.substr(endIndex);
				return result;
			}

			function favoriteRender(item) {
				var result = '<span style="font-size: 28px; margin-left: 100px;"><span>';
				if (item.favorite == true) {
					result += '<span style="color: red; border: 1px solid yellow;">Favorited</span>';
				} else {
					result += '<a href="javascript:;" onclick="favorite(this, ' + item.sentenceid + ');" ' +
						'style="text-decoration: underline;">favorite</a>';
				}
				result += '</span><b>[' + item.playCount + ']</b></span>';
				return result;
			}

			$('.book-name').click(function(evt) {
				var bookid = $(this).attr('bookid');
				var url = root + '/book.jsp?id=' + bookid;
				window.open(url);
			});
			resetPlayer();
		}
	});
}
{
	var word = 'abrupt';
	var sentence = "Mrs Merkel's abrupt abandonment of nuclear power has also brought the two parties' environmental policies closer.";
	var startIndex = sentence.indexOf(word);
	var endIndex = startIndex + word.length;
	var result = sentence.substr(0, startIndex) + '<span>' + sentence.substr(startIndex, word.length) + '</span>'
		+ sentence.substr(endIndex);
	console.log(startIndex, result);
	console.log(sentence);
}

var audioInstance = null;
var repeatStatus = false;
function initPlayerlist() {
	// Setup the player to autoplay the next track
	audioInstance = audiojs.create(document.getElementById('audio-player'), {
		trackEnded : function() {
			var next = $('.audio-item.playing');
			if (!repeatStatus) {
				next = $('.audio-item.playing').parent().next().find('.audio-item');
			}
			if (!next.length)
				next = $('.audio-item').first();
			loadAudio($(next));
		}
	});

	// Load in the first track
	resetPlayer();

	// Keyboard shortcuts
	$(document).keydown(function(e) {
		var unicode = e.charCode ? e.charCode : e.keyCode;
		// right arrow
		if (unicode == 39) {
			var next = $('.audio-item.playing').parent().next().find('.audio-item');
			if (!next.length)
				next = $('.audio-item').first();
			next.click();
			// back arrow
		} else if (unicode == 37) {
			var prev = $('.audio-item.playing').parent().prev().find('.audio-item');
			if (!prev.length)
				prev = $('.audio-item').last();
			prev.click();
			// spacebar
		} else if (unicode == 32) {
			e.preventDefault();

			audioInstance.playPause();
		}
	});
}

function loadAudio($target) {
	audioInstance.load($target.attr('data-src'));
	$('#audio-text').html($target.html());
	$target.addClass('playing');
	$('.audio-item').not($target).removeClass('playing');
	audioInstance.play();
}

function resetPlayer() {
	var first = $('.audio-item').attr('data-src');
	console.log(first);
	if (first != undefined) {
		loadAudio($('.audio-item').eq(0));

		// Load in a track on click
		$('.audio-item').click(function(e) {
			e.preventDefault();

			// loadAudio($(this));

			var that = this;
			setTimeout(function() {
				var dblclick = parseInt($(that).data('double'), 10);
				if (dblclick > 0) {
					$(that).data('double', dblclick - 1);
				} else {
					loadAudio($(that));
				}
			}, 300);
		}).dblclick(function(evt) {
			$(this).data('double', 2);
			console.log('sd')
		});
	}
}