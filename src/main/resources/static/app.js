
function connectStompMessaging() {
	var socket = new SockJS('/gs-guide-websocket');
    window.stompClient = Stomp.over(socket);
    return new Promise(function(resolve, reject) {
    	window.stompClient.connect({}, resolve, reject);
	});
}

var app = new Vue({
	el: '#app',
	data: {
		messages: [],
		command: null
	},
	created: function () {
		connectStompMessaging()
			.then(this.subscribeToUserMessages)
	},
	methods: {
		subscribeToUserMessages: function() {
			console.log('subscribing to user endpoint');
			window.stompClient.subscribe('/user/messages', this.userMessageReceived);
		},
		userMessageReceived: function(message) {
			console.log('got user message message: ', message);
		},
		sendCommand: function() {
			var body = {
				message: this.command
			};
			console.log('sending: ', body);
			window.stompClient.send("/app/command", {}, JSON.stringify(body));
			this.command = null;
		}
	}
});
	