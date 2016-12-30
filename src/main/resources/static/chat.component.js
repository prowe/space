Vue.component('chat-panel', {
  template: "#chat-template",
	data: function() {
		return {
			messages: [],
			command: null
		};
	},
	created: function () {
		connectStompMessaging().then(this.subscribeToChat)
	},
	methods: {
		subscribeToChat: function() {
			console.log('subscribing to user endpoint');
			window.stompClient.subscribe('/user/topic/chat', this.chatMessageReceived);
		},
		chatMessageReceived: function(response) {
			console.log('got user message message: ', response);
			var message = JSON.parse(response.body);
			this.messages.push(message);
		},
		sendCommand: function() {
			var body = {
				message: this.command
			};
			console.log('sending: ', body);
			window.stompClient.send("/app/chat-messages", {}, JSON.stringify(body));
			this.command = null;
		}
	}
});