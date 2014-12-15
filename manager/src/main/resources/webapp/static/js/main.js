
function msg(level, message) {
	var msgElement = $('<div class="alert alert-' + level + '">' +
	'<a href="#" class="close" data-dismiss="alert">&times;</a>' + message +
	'</div>');

	msgElement.alert();
	$('#alerts').append(msgElement);
	msgElement.delay(1500).alert('close');
}

var module = angular.module('PlumberManager', []);
module.controller('Workers', function($scope, $http, $timeout) {
	function update() {
		$http.get('/api/worker').success(function (data) {
			$scope.items = data;
			$timeout(update, 1500);
		}).error(function() {
			$timeout(update, 1500);
		});
	}

	update();

	$timeout(update, 1500);

	$scope.setWorker = function(worker) {
		$scope.worker = worker;
	};
});

$(function() {



	$('#test-button').click(function() {
		var jobString = $('#test-job').val();
		var job = $.parseJSON(jobString);

		$.ajax({
			type: "POST",
			url: '/api/job',
			data: JSON.stringify(job),
			success: function(data) {
				msg('success', 'Created job ' + data.id);
			},
			contentType: 'application/json'
		});
	});





});



