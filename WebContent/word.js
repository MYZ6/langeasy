$(function() {
	initData();
});

function initData() {
	$.ajax({
		type : "GET",
		url : root + '/api?t=word&id=' + wordId,
		async : false,
		error : function() {
			console.error("query failed");
		},
		success : function(data) {
			console.log(data);
			$('title').html("" + data.word);
			$('#word-title').html(data.word);
			$('#pron').html(data.pron);
			$('#pron').append('<audio controls src="' + data.oggpath + '">sdfd</audio>');
			$('#btn-listen').click(function() {
				window.open(data.oggpath);
			});

			$(data.meaning).each(function(i, item) {
				$('#meaning').append('<div>' + item.type + '</div><div>' + item.meaning + '</div>');
				$(item.example).each(function(i, item) {
					$('#meaning').append('<div>' + item.sentence + '</div>');
				});
			});

			$(data.aexample).each(function(i, item) {
				$sentence = $('<div>' + item.sentence + '</div>');
				var _url = root + '/api?t=m&id=' + item.sentenceid;
				$sentence.append('<audio controls src="' + _url + '">sdfd</audio>');
				// $sentence.click(function() {
				// window.open(_url);
				// });
				$('#audio-example').append($sentence);
			});

		}
	});
}