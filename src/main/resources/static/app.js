function connectStompMessaging() {
	if (!window.stompClientPromise) {
		var socket = new SockJS('/gs-guide-websocket');
		window.stompClient = Stomp.over(socket);
		window.stompClientPromise = new Promise(function(resolve, reject) {
			window.stompClient.connect({}, resolve, reject);
		});
	}
	return window.stompClientPromise;
}

var app = new Vue({
	el : '#app',
	methods : {
		performAttack : function() {
			var attack = {
				targetId : "Evil Cruiser"
			}
			window.stompClient.send("/app/perform-attack", {}, JSON
					.stringify(attack));
		}
	}
});