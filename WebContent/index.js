$(function() {
	initPlayerlist();

	initData();

	$('#passBtn').click(doPass);
});

function doPass(evt) {
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

function initData() {
	if (type == '') {
		type = 'list';
	}
	$.ajax({
		type : "GET",
		url : root + '/api?t=' + type,
		async : false,
		error : function() {
			console.error("query failed");
		},
		success : function(data) {
			console.log(data);
			$(data)
					.each(
							function(i, word) {
								$word = $('<div id="word' + word.wordid
										+ '" class="word-item">' + word.word
										+ '</div>');
								$word.click(function() {
									// var _url = root + '/word.jsp?id=' +
									// word.wordid;
									// window.open(_url, '_blank');
									showWord(word.wordid);
								});
								$('#word-list').append($word);
							});
			$('.word-item').eq(0).trigger('click');
		}
	});
}

var currentWordId = null;
function showWord(wordId) {
	currentWordId = wordId;
	$
			.ajax({
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
					var pronUrl = root + '/api?t=p&path=' + data.oggpath;
					$('#pron').append(
							'<audio controls src="' + pronUrl
									+ '">sdfd</audio>');
					$('#btn-listen').click(function() {
						window.open(data.oggpath);
					});

					$(data.meaning)
							.each(
									function(i, item) {
										$('#meaning')
												.append(
														'<div class="meaning-type">'
																+ item.type
																+ '</div><div class="meaning-content">'
																+ item.meaning
																+ '</div>');
										$(item.example)
												.each(
														function(i, item) {
															$('#meaning')
																	.append(
																			'<div class="example">'
																					+ item.sentence
																					+ '</div>');
														});
									});

					$(data.aexample)
							.each(
									function(i, item) {
										var _url = root + '/api?t=m&id='
												+ item.sentenceid + "&ts="
												+ new Date().getTime();
										$sentence = $('<div class="aexample audio-item" data-src="'
												+ _url
												+ '"><span>'
												+ item.sentenceid
												+ '</span>&nbsp;&nbsp;'
												+ item.sentence + '</div>');

										// $sentence.append('<audio controls
										// src="' + _url +
										// '">sdfd</audio>');
										// $sentence.click(function() {
										// window.open(_url);
										// });
										$('#audio-example').append($sentence);
									});

					resetPlayer();
				}
			});
}

var audioInstance = null;
function initPlayerlist() {
	// Setup the player to autoplay the next track
	audioInstance = audiojs.create(document.getElementById('audio-player'), {
		trackEnded : function() {
			var next = $('.audio-item.playing').next();
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
			var next = $('.audio-item.playing').next();
			if (!next.length)
				next = $('.audio-item').first();
			next.click();
			// back arrow
		} else if (unicode == 37) {
			var prev = $('.audio-item.playing').prev();
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
	$target.addClass('playing').siblings().removeClass('playing');
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

			loadAudio($(this));
		});
	}
}