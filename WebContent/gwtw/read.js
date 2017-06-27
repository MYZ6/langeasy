$(function() {
	initData();

	initEvent();
});

function initEvent() {
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
	$.ajax({
		type : "GET",
		// url : root + '/gwtw/0200161.txt',
		url : root + '/gwtw/novel-test.txt',
		async : false,
		error : function() {
			console.error("query failed");
		},
		success : function(data) {
			var parr = data.split('\r\n\r\n');
			$(parr).each(function(i, item) {
				$('#word-content').append('<p>' + item + "</p>");
			})
			// console.log(parr);
			// data = data.replace(/\r\n\r\n/g, '你好');
			// data = data.replace(/你好(.*)你好/g, '<p class="">$1</p>');
			// $('#word-content').append('<pre>' + data + "</pre>");
		}
	});
}

var currentWordId = null;
function showWord(word) {
	$.ajax({
		type : "GET",
		url : root + '/gwtw?t=sentence&word=' + word,
		error : function() {
			console.error("query failed");
		},
		success : function(data) {
			$('#word-title, #pron, #meaning, #audio-example').empty();

			$(data).each(
				function(i, item) {
					$sentence = $('<div class="aexample"><div class="audio-item" style="font-size: 20px;"><span>' + (i + 1)
						+ '</span>&nbsp;&nbsp;' + sentenceRender(item.sentence) + '<div></div>');
					$('#audio-example').append($sentence);
				});
			function sentenceRender(sentence) {
				var startIndex = sentence.toLowerCase().indexOf(word);
				var endIndex = startIndex + word.length;
				var result = sentence.substr(0, startIndex) + '<span style="color: red; font-weight: bold;">'
					+ sentence.substr(startIndex, word.length) + '</span>' + sentence.substr(endIndex);
				return result;
			}
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