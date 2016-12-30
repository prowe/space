(function () {
	var chatMessageComponent = {
		template: '<strong>{{message.sourceId}}: </strong>{{message.message}} {{message}}',
		props: ['message'],
	};
	
	Vue.component('chat-panel', {
		template: "#chat-template",
		data: function() {
			return {
				messages: [],
				command: null
			};
		},
		directives: {
			'scroll-bottom': {
				componentUpdated: function(el) {
					console.log('scrolling', el);
			    	el.scrollTop = el.scrollHeight;
				}
			}
		},
		created: function () {
			connectStompMessaging().then(this.subscribeToChat)
		},
		methods: {
			subscribeToChat: function() {
				console.log('subscribing to user endpoint');
				window.stompClient.subscribe('/user/topic/chat', this.chatMessageReceived);
				window.stompClient.subscribe('/user/topic/actions', this.areaActionReceived);
			},
			chatMessageReceived: function(response) {
				console.log('got user message message: ', response);
				var message = JSON.parse(response.body);
				this.messages.push(message);
			},
			areaActionReceived: function(response) {
				console.log('got user action: ', response);
				var action = JSON.parse(response.body);
				this.messages.push(action);
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
})();