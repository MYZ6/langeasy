$(function() {
	initData();

	initEvent();
});

var ctrlIsPressed = false;

function initEvent() {
	$(document).keydown(function(event) {
		if (event.which == "17")
			ctrlIsPressed = true;
	});

	$(document).keyup(function() {
		ctrlIsPressed = false;
	});

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

	$('#word-content, #novel-panel').click(function(e) {
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

	$('#btn-toggle').click(function(e) {
		$('#novel-panel').toggle();
	});
}

function translate(word, x, y) {
	$('#translate-panel').empty().html('<img src="loading.gif" />').show();
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
			$('#translate-panel').empty().html(data);
		}
	});
}

var partIndex = [], chapterIndex = [];
function initData(_reload) {
	if (type == '') {
		type = 'list';
	}
	$('#word-list').empty();
	$.ajax({
		type : "GET",
		url : root + '/gwtw?t=' + type,
		data : {
			"wtype" : 3
		},
		async : false,
		error : function() {
			console.error("query failed");
		},
		success : function(data) {
			// console.log(data);
			$(data.rows).each(
				function(i, word) {
					$word = $('<div class="word-item"><span>' + (i + 1)
						+ '/</span><span>' + word.wordid + '&nbsp;</span><t class="word">' + word.word + '</t></div>');
					$word.click(function() {
						showWord(word.word);
					});
					$('#word-list').append($word);
				});
			if (_reload == undefined) {
				$('.word-item').eq(0).trigger('click');
			}
		}
	});

	$.ajax({
		type : "GET",
		url : root + '/gwtw/0200161.txt',
		// url : root + '/gwtw/novel-test.txt',
		async : false,
		error : function() {
			console.error("query failed");
		},
		success : function(data) {
			var parr = data.split('\r\n\r\n');
			$(parr).each(function(i, item) {
				item = item.replace(/\r\n/g, ' ');
				if (item.indexOf('PART ') > -1) {
					var partNum = item.trim().substr(5);
					partIndex.push({"num" : partNum, "index" : i});
				}
				if (item.indexOf('CHAPTER') > -1) {
					var roman = item.trim().substr(8);
					var chapterNum = romanMap[roman];
					chapterIndex.push({"num" : chapterNum, "index" : i});
				}
				$('#novel-panel').append('<p id="pg' + i + '" pindex="' + i + '">' + i + '/' + item + "</p>");
			});
			console.log(partIndex);
		}
	});
}

var sentenceArr = null;
function showWord(word) {
	$.ajax({
		type : "GET",
		url : root + '/gwtw?t=sentence&word=' + word,
		error : function() {
			console.error("query failed");
		},
		success : function(data) {
			$('#word-title, #pron, #meaning, #audio-example').empty();
			sentenceArr = data;
			$(data).each(
				function(i, item) {
					$sentence = $('<div class="aexample"><div class="audio-item" style="font-size: 20px;"><span>' + (i + 1)
						+ '</span>&nbsp;&nbsp;' + sentenceRender(item.sentence) + '</div>' +
						'<a href="#" onclick="findSentence(' + i + ')">More</a></div>');
					$('#audio-example').append($sentence);
				});
			function sentenceRender(sentence) {
				sentence = sentence.replace(/\b(\w+)\b/g, '<t class="">$1</t>');
				var startIndex = sentence.toLowerCase().indexOf(word);
				var endIndex = startIndex + word.length;
				var result = sentence.substr(0, startIndex) + '<span style="color: red; font-weight: bold;">'
					+ sentence.substr(startIndex, word.length) + '</span>' + sentence.substr(endIndex);
				return result;
			}

			$('#audio-example t').hover(function() {
				if (ctrlIsPressed) {
					$(this).addClass("hover");
				}
			}, function() {
				$(this).removeClass("hover");
			}).click(function(e) {
				if (ctrlIsPressed) {
					var word = $(this).text();
					var scrollPos = $('#word-list').scrollTop();
					console.log(word, scrollPos);
					var $target = $('t.word:contains("' + word + '"):last');
					if ($target.length > 0) {
						if ($target.text() != word) {
							return;
						}
						var targetPos = $target.offset().top - 40;
						console.log(word, scrollPos, targetPos);
						if (targetPos == 0) {
							return;
						}
						$('#word-list').scrollTop(scrollPos + targetPos);
					}
				}
			});
		}
	});
}

var lastParaId = null;
function findSentence(index) {
	var obj = sentenceArr[index];
	var sentence = obj.sentence;
	var scrollPos = $('#novel-panel').scrollTop();
	var $target = $('#novel-panel p:contains("' + sentence.replace(/([ \'"!])/g, '\\$1') + '"):last');
	console.log($target, scrollPos, obj.sentence);
	if ($target.length > 0) {
		var targetPos = $target.offset().top - 240;
		console.log(word, scrollPos, targetPos);
		if (targetPos == 0) {
			return;
		}
		$('#novel-panel').scrollTop(scrollPos + targetPos);

		$('#' + lastParaId).find('h1').remove();
		$('#' + lastParaId).html($('#' + lastParaId).text());
		lastParaId = $target[0].id;
		var ptext = $target.text();
		var startIndex = ptext.indexOf(sentence);
		var endIndex = startIndex + sentence.length;
		var pindex = $target.attr('pindex');
		var highlightText = '<h1 style="color: green;">PART ' + getPartNum(pindex) +
			' CHAPTER ' + getChapterNum(pindex) + '</h1>'
			+ ptext.substr(0, startIndex) + '<span style="background: yellow; font-weight: bold;">'
			+ sentence + '</span>' + ptext.substr(endIndex);
		$target.html(highlightText);
	}
}

function getPartNum(paraIndex) {
	for (var i = 4; i >= 0; i--) {
		if (partIndex[i].index < paraIndex) {
			return partIndex[i].num;
		}
	}
}
function getChapterNum(paraIndex) {
	for (var i = 62; i >= 0; i--) {
		if (chapterIndex[i].index < paraIndex) {
			return chapterIndex[i].num + ' P' + (paraIndex - chapterIndex[i].index);
		}
	}
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

var romanMap = {
	"I" : 1,
	"II" : 2,
	"III" : 3,
	"IV" : 4,
	"V" : 5,
	"VI" : 6,
	"VII" : 7,
	"VIII" : 8,
	"IX" : 9,
	"X" : 10,
	"XI" : 11,
	"XII" : 12,
	"XIII" : 13,
	"XIV" : 14,
	"XV" : 15,
	"XVI" : 16,
	"XVII" : 17,
	"XVIII" : 18,
	"XIX" : 19,
	"XX" : 20,
	"XXI" : 21,
	"XXII" : 22,
	"XXIII" : 23,
	"XXIV" : 24,
	"XXV" : 25,
	"XXVI" : 26,
	"XXVII" : 27,
	"XXVIII" : 28,
	"XXIX" : 29,
	"XXX" : 30,
	"XXXI" : 31,
	"XXXII" : 32,
	"XXXIII" : 33,
	"XXXIV" : 34,
	"XXXV" : 35,
	"XXXVI" : 36,
	"XXXVII" : 37,
	"XXXVIII" : 38,
	"XXXIX" : 39,
	"XL" : 40,
	"XLI" : 41,
	"XLII" : 42,
	"XLIII" : 43,
	"XLIV" : 44,
	"XLV" : 45,
	"XLVI" : 46,
	"XLVII" : 47,
	"XLVIII" : 48,
	"XLIX" : 49,
	"L" : 50,
	"LI" : 51,
	"LII" : 52,
	"LIII" : 53,
	"LIV" : 54,
	"LV" : 55,
	"LVI" : 56,
	"LVII" : 57,
	"LVIII" : 58,
	"LIX" : 59,
	"LX" : 60,
	"LXI" : 61,
	"LXII" : 62,
	"LXIII" : 63,
	"LXIV" : 64,
	"LXV" : 65,
	"LXVI" : 66,
	"LXVII" : 67,
	"LXVIII" : 68,
	"LXIX" : 69,
	"LXX" : 70,
	"LXXI" : 71,
	"LXXII" : 72,
	"LXXIII" : 73,
	"LXXIV" : 74,
	"LXXV" : 75,
	"LXXVI" : 76,
	"LXXVII" : 77,
	"LXXVIII" : 78,
	"LXXIX" : 79,
	"LXXX" : 80,
	"LXXXI" : 81,
	"LXXXII" : 82,
	"LXXXIII" : 83,
	"LXXXIV" : 84,
	"LXXXV" : 85,
	"LXXXVI" : 86,
	"LXXXVII" : 87,
	"LXXXVIII" : 88,
	"LXXXIX" : 89,
	"XC" : 90,
	"XCI" : 91,
	"XCII" : 92,
	"XCIII" : 93,
	"XCIV" : 94,
	"XCV" : 95,
	"XCVI" : 96,
	"XCVII" : 97,
	"XCVIII" : 98,
	"XCIX" : 99,
	"C" : 100
};